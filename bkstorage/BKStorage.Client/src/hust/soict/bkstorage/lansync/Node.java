/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.lansync;

import hust.soict.bkstorage.client.peerinterface.ElectionData;
import hust.soict.bkstorage.client.peerinterface.PeerNode;
import hust.soict.bkstorage.dal.ConnectionManager;
import hust.soict.bkstorage.entity.DerbyLanSyncSnapshotMapper;
import hust.soict.bkstorage.entity.FileMetaData;
import hust.soict.bkstorage.entity.LanSyncFileMetaData;
import hust.soict.bkstorage.entity.Snapshot;
import hust.soict.bkstorage.exception.SnapshotMappingException;
import hust.soict.bkstorage.remoteentity.MyFile;
import hust.soict.bkstorage.security.SecurityCommon;
import hust.soict.bkstorage.utils.FileUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author thinhnt
 */
public class Node implements PeerNode {

    private String id;
    private String ip;
    private String bcastIP;
    private PeerNode predecessor;
    private PeerNode successor;
    private Map<String, ElectionData> fileLeaders;
    private File syncFolder;
    private Snapshot snapshot;

    public Node(String ip, String bcastIP) {
        try {
            this.ip = ip;
            this.bcastIP = bcastIP;
            this.id = SecurityCommon.md5Hash(ip);
            snapshot = new Snapshot();
            snapshot.setSnapshotMapper(new DerbyLanSyncSnapshotMapper(
                    ConnectionManager.DerbyConnection.createConnection()));
            snapshot.load();
        } catch (SnapshotMappingException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public String getIP() {
        return ip;
    }

    public String getBcastAddress() {
        return bcastIP;
    }

    public Map<String, ElectionData> getFileLeaders() {
        return fileLeaders;
    }

    public void setFileLeaders(Map<String, ElectionData> fileLeader) {
        this.fileLeaders = fileLeader;
    }

    @Override
    public PeerNode join(String joinID) throws RemoteException {
        // Trường hợp trong ring mới có 1 nút duy nhất
        if (predecessor == null || predecessor == null) {
            return this;
        }

        if (joinID.compareTo(id) <= 0
                && (predecessor.getID().compareTo(joinID) < 0)
                || predecessor.getID().compareTo(id) > 0) {
            return this;
        }

        return successor;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public void setPredecessor(PeerNode node) throws RemoteException {
        this.predecessor = node;
    }

    @Override
    public PeerNode getPredecessor() throws RemoteException {
        return predecessor;
    }

    @Override
    public void setSuccessor(PeerNode node) throws RemoteException {
        this.successor = node;
    }

    @Override
    public PeerNode getSuccessor() throws RemoteException {
        return successor;
    }

    @Override
    public void election(Map<String, ElectionData> predLeaders) throws RemoteException {
        System.out.println("[" + ip + "]Election");
        if (fileLeaders == null) {
            fileLeaders = new HashMap<>();
            for (File l : syncFolder.listFiles()) {
                mapLocalFilesToLeader(l, fileLeaders);
            }

            for (FileMetaData snapshotFile : snapshot) {
                LanSyncFileMetaData lanSyncFileMetaData = (LanSyncFileMetaData) snapshotFile;
                String path = lanSyncFileMetaData.getFilePatch();
                ElectionData electionData = fileLeaders.get(path);
                if (electionData == null) { // Tồn tại trong snapshot nhưng ko có trong local --> File bị xóa
                    electionData = new ElectionDataImpl(true);
                    electionData.setElectionCompleted(true);
                    electionData.setLeaderNode(this);
                    fileLeaders.put(path, electionData);
                } else if (lanSyncFileMetaData.isDeleted()) {// Có trong local nhưng deleted = true trong snapshot --> File được thêm
                    try {
                        snapshot.delete(snapshotFile.getFilePatch());
                    } catch (SnapshotMappingException ex) {
                        Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        int count = 0;
        // So sánh, trộn preLeader và leader
        for (String path : predLeaders.keySet()) {
            ElectionData predData = predLeaders.get(path);
            ElectionData thisData = fileLeaders.get(path);
            if (thisData == null) { // Chuyển thông tin file vào nút hiện tại (nếu chưa có)
                fileLeaders.put(path, predData);
            } else if (!predData.isElectionCompleted()) {   // Nếu chưa tìm được leader cho file
                String thisID = thisData.getLeaderNode().getID();
                String predID = predData.getLeaderNode().getID();
                if (thisData.getLeaderNode().getID().
                        equals(predData.getLeaderNode().getID())) { // Leader là nút hiện tại
                    thisData.setElectionCompleted(true);
                    count++;
                } else {    // Nếu cả 2 node đều có --> So sánh
                    thisData.setDelete(predData.isDelete() || thisData.isDelete());
                    if (thisData.getLastModified() < predData.getLastModified()) {
                        thisData.setLeaderNode(predData.getLeaderNode());
                        thisData.setLastModified(predData.getLastModified());
                    } else if (thisData.getLastModified() == predData.getLastModified()) {
                        // Nếu bằng nhau, lấy leader của pred
                        thisData.setLeaderNode(predData.getLeaderNode());
                    }
                }
            } else {    // Nếu đã tìm được leader cho file
                thisData.setElectionCompleted(true);    // Ph
                thisData.setDelete(predData.isDelete());
                count++;
            }
        }

        if (count != 0 && count == fileLeaders.size()) {   // Tất cả các file đã tìm được node lãnh đạo
            // Broadcast
            broadcastElectionCompleted();
        } else {
            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    if (successor == null) {
                        fileLeaders = null;
                        return null;
                    }
                    successor.election(fileLeaders);
                    return null;
                }

            }.execute();
        }
    }

    public void setSyncFolder(File f) {
        this.syncFolder = f;
    }

    private void mapLocalFilesToLeader(File file, Map<String, ElectionData> result) {
        String path = FileUtil.getCommonPath(file);
        long lastModified = file.lastModified();
        ElectionData electionData = new ElectionDataImpl(lastModified, this);
        result.put(path, electionData);

        if (file.isDirectory()) {
            electionData.setDirectory(true);
            for (File child : file.listFiles()) {
                mapLocalFilesToLeader(child, result);
            }
        }
    }

    private void broadcastElectionCompleted() {
        DatagramSocket c = null;
        try {
            c = new DatagramSocket();
            c.setBroadcast(true);
            InetAddress broadcast = InetAddress.getByName(bcastIP);
            byte[] sendData = (DiscoveryThread.ELECTION_COMPLETED + ":"
                    + broadcast.getHostAddress()).getBytes();
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData,
                        sendData.length, broadcast, 8889);
                c.send(sendPacket);
            } catch (Exception e) {
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }

    @Override
    public byte[] download(String path) throws RemoteException {
        boolean finish = false;
        byte[] data = null;
        MyFile myFile = new MyFile(0, path, false, 0, 0, 0);
        File f = FileUtil.convert2ClientFile(myFile);
        while (!finish) {
            try {
                RandomAccessFile io = new RandomAccessFile(f, "rw");
                FileChannel channel = io.getChannel();
                FileLock look = channel.tryLock();
                if (look != null) {
                    // Nếu file đang tự do (không bị tiến trình khác chiếm)
                    try {
                        data = new byte[(int) io.length()];
                        io.readFully(data);
                        finish = true;
                    } finally {
                        look.release();
                        io.close();
                    }
                } else {
                    // File đã bị tiến trình khác khóa
                    io.close();
                    if (!f.exists()) {
                        finish = true;
                    }
                }
            } catch (FileNotFoundException ex) {
                finish = true;
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                finish = true;
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return data;
    }

}
