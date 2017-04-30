/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.lansync;

import hust.soict.bkstorage.client.peerinterface.ElectionData;
import hust.soict.bkstorage.client.peerinterface.PeerFactory;
import hust.soict.bkstorage.client.peerinterface.PeerNode;
import hust.soict.bkstorage.entity.FileMetaData;
import hust.soict.bkstorage.entity.LanSyncFileMetaData;
import hust.soict.bkstorage.entity.Snapshot;
import hust.soict.bkstorage.exception.SnapshotMappingException;
import hust.soict.bkstorage.remoteentity.MyFile;
import hust.soict.bkstorage.utils.FileUtil;
import hust.soict.bkstorage.utils.NetUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author thinhnt
 */
public class LanSync implements ElectionCompleteListener {

    private List<PeerFactory> factories;
    private Registry registry;
    private int port;
    private String userName;
    private Thread discoveryThread;
    private File syncFolder;
    private WatchDir watchDirThread;

    public LanSync(File syncFolder) {
        factories = new ArrayList<>();
        this.syncFolder = syncFolder;
//        String portStr = Dal.options.getLanPort();
//        port = Integer.parseInt(portStr);
        DiscoveryThread.getInstance().addElectionListener(this);
    }

    public void startWatch() throws IOException {
        if (watchDirThread == null || !watchDirThread.isAlive()) {
            watchDirThread = new WatchDir(Paths.get(syncFolder.getPath()), true, this);
            watchDirThread.start();
        }
    }

    public void stopWatch() throws IOException {
        if (watchDirThread != null && watchDirThread.isAlive()) {
            watchDirThread.close();
            watchDirThread = null;
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Bật chức năng lansync
     *
     * @throws RemoteException
     * @throws SocketException
     */
    public void enable() throws RemoteException, SocketException, IOException {
        if (factories.isEmpty()) {
            scanNetworkInterfaces();
        }
        registry = LocateRegistry.createRegistry(port);
        for (PeerFactory factory : factories) {
            PeerFactoryImpl factoryImpl = (PeerFactoryImpl) factory;
            String ip = ((Node) factoryImpl.getNode()).getIP();
            factoryImpl.exportDiscovery(port);
            Remote stub = UnicastRemoteObject.exportObject(factory, port);
            registry.rebind("factory" + ip, stub);
        }
        if (discoveryThread == null) {
            discoveryThread = new Thread(DiscoveryThread.getInstance());
            discoveryThread.start();
        }
    }

    /**
     * Tắt chức năng lansync
     *
     * @throws NotBoundException
     * @throws RemoteException
     */
    public void disable() throws NotBoundException, RemoteException {
        if (discoveryThread != null) {
            discoveryThread.interrupt();
            DiscoveryThread.getInstance().closeSocket();
        }

        if (watchDirThread != null && watchDirThread.isAlive()) {
            try {
                watchDirThread.interrupt();
                watchDirThread.close();
            } catch (IOException ex) {
                Logger.getLogger(LanSync.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (PeerFactory factory : factories) {
            PeerFactoryImpl factoryImpl = (PeerFactoryImpl) factory;
            String ip = ((Node) factoryImpl.getNode()).getIP();
            registry.unbind("factory" + ip);
            factoryImpl.unexportObjects();
            UnicastRemoteObject.unexportObject(factory, false);
        }
    }

    private void scanNetworkInterfaces() throws SocketException {
        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
            if (ni.isLoopback() || !ni.isUp()) {
                continue;
            }
            List<InterfaceAddress> addresses = ni.getInterfaceAddresses();
//            Enumeration<InetAddress> e = ni.getInetAddresses();
            for (InterfaceAddress address : addresses) {
                if (address.getBroadcast() == null) {
                    continue;
                }
                String ip = address.getAddress().getHostAddress();
                if (NetUtil.isIPv4(ip) && !"127.0.0.1".equals(ip)) {
                    System.out.println(ip);
                    Node node = new Node(ip, address.getBroadcast().getHostAddress());
                    node.setSyncFolder(syncFolder);
                    InstanceDiscovery discovery = new InstanceDiscovery(userName);
                    PeerFactoryImpl factoryImpl = new PeerFactoryImpl(node, discovery);
                    factories.add(factoryImpl);
                }
            }
        }
    }

    public void sync() throws RemoteException {
        // Quét instance trong mỗi subnet
        System.out.println("Init Sync");
        for (PeerFactory factory : factories) {
            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    PeerFactoryImpl factoryImpl = (PeerFactoryImpl) factory;
                    String joinID = ((Node) factoryImpl.getNode()).getID();
                    String ip = ((Node) factoryImpl.getNode()).getIP();
                    String bcast = ((Node) factoryImpl.getNode())
                            .getBcastAddress();
                    PeerNode catchedNode = scanInstance(bcast, ip);
                    Node joinNode = (Node) factoryImpl.getNode();
                    if (catchedNode != null) {
                        // Tìm được node trong ring: Thực hiện join
                        PeerNode forwardNode = catchedNode.join(joinID);

                        while (!forwardNode.getID().equals(catchedNode.getID())) {
                            catchedNode = forwardNode;
                            forwardNode = catchedNode.join(joinID);
                        }

                        // bind Node
                        // Nếu node chưa bao giờ join vào ring
                        if (!joinNode.getID().equals(forwardNode.getID())) {
                            try {
                                factoryImpl.exportNode(port);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            joinNode.setSuccessor(catchedNode);
                            PeerNode pre = catchedNode.getPredecessor();
                            if (pre == null) {  // Trường hợp trong ring có 1 node
                                joinNode.setPredecessor(catchedNode);
                                catchedNode.setSuccessor(joinNode);
                                catchedNode.setPredecessor(joinNode);
                            } else {
                                joinNode.setPredecessor(pre);
                                pre.setSuccessor(joinNode);
                                catchedNode.setPredecessor(joinNode);
                            }
                        }
                        // yêu cầu đồng bộ
                        joinNode.election(new HashMap<>());
                    } else {
                        // Quét hết mà ko tìm được node nào --> Đây là node đầu tiên tham gia 
                        // --> Tạo khóa cho ring
                        ((InstanceDiscovery) factoryImpl.getInstaceDiscovery())
                                .setSSLKey(userName);
                        try {
                            factoryImpl.exportNode(port);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    return null;
                }

            }.execute();
        }
    }

    /**
     *
     * @param bcastAddress
     * @param sourceIP to detect response come from localhost
     * @return
     */
    private PeerNode scanInstance(String bcastAddress, String sourceIP) {
        DatagramSocket c = null;
        try {
            c = new DatagramSocket();
            c.setBroadcast(true);
            c.setSoTimeout(1000);
            byte[] sendData = DiscoveryThread.DISCOVERY_REQUEST.getBytes();

            InetAddress broadcast = InetAddress.getByName(bcastAddress);
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData,
                        sendData.length, broadcast, 8889);
                System.out.println("[LanClient]Send " + DiscoveryThread.DISCOVERY_REQUEST
                        + " request to: "
                        + broadcast.getHostAddress());
                c.send(sendPacket);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            while (true) {
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                String message = null;
                try {
                    c.receive(receivePacket);
                    message = new String(receivePacket.getData()).trim();
                    if (receivePacket.getAddress().getHostAddress().equals(sourceIP)) {
                        System.out.println("[LanClient]Receive " + message
                                + " response from local: "
                                + receivePacket.getAddress().getHostAddress()
                        );
                        continue;
                    }
                    System.out.println("[LanClient]Receive " + message + " response from instance: "
                            + receivePacket.getAddress().getHostAddress());
                } catch (SocketTimeoutException e) {
                    System.out.println("[LanClient]Timeout reached!!! " + e);
                    c.close();
                    return null;
                }
                if (message.equals(DiscoveryThread.DISCOVERY_RESPONSE)) {
                    String instanceIP = receivePacket.getAddress().getHostAddress();
                    Registry reg = LocateRegistry.getRegistry(instanceIP, port);
                    try {
                        PeerFactory factory = (PeerFactory) reg.lookup("factory" + instanceIP);
                        String sslKey = factory.getInstaceDiscovery().getSSLKey();
                        if (userName.equals(sslKey)) {
                            return factory.getNode();
                        }
                    } catch (RemoteException | NotBoundException ex) {
                        Logger.getLogger(LanSync.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
        return null;
    }

    @Override
    public void onElectionCompleted(String broadcastAddress) {
        Thread worker = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("[Listener] Election Completed: " + broadcastAddress);
                try {
                    synchronized (LanSync.this) {
                        stopWatch();
                        for (PeerFactory factory : factories) {
                            PeerFactoryImpl factoryImpl = (PeerFactoryImpl) factory;
                            Node node = (Node) factoryImpl.getNode();
                            String bcastNodeAddr = node.getBcastAddress();
                            if (broadcastAddress.equals(bcastNodeAddr)) {
                                fetch(node);
                                node.setFileLeaders(null);
                                node.setPredecessor(null);
                                node.setSuccessor(null);
                            }
                        }
                        startWatch();
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(LanSync.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(LanSync.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        worker.start();
    }

    private void fetch(Node thisNode) throws RemoteException {
        try {
            Map<String, ElectionData> fileLeaders = thisNode.getFileLeaders();
            for (String path : fileLeaders.keySet()) {
                ElectionData electionData = fileLeaders.get(path);
                File f = FileUtil.convert2ClientFile(new MyFile(-1, path, true, -1, -1, -1));
                if (electionData.isDelete()) {
                    if (f.isDirectory()) {
                        org.apache.derby.iapi.services.io.FileUtil.removeDirectory(f);
                    } else {
                        f.delete();
                    }
                    // Cập nhật trong snapshot
                    try {
                        if (!isExistInSnapshot(path, thisNode.getSnapshot())) {
                            thisNode.getSnapshot().insert(new LanSyncFileMetaData(path, true));
                        } else {
                            thisNode.getSnapshot().update(path, true);
                        }
                    } catch (SnapshotMappingException ex) {
                        Logger.getLogger(LanSync.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    // Download
                    PeerNode leaderNode = electionData.getLeaderNode();
                    if (!leaderNode.getID().equals(thisNode.getID())) {
                        if (electionData.isDirectory()) {
                            f.mkdir();
                        } else {
                            prepareParents(f);
                            FileOutputStream out = null;
                            try {
                                byte[] data = leaderNode.download(path);
                                out = new FileOutputStream(f);
                                out.write(data);
                            } catch (IOException ex) {
                                Logger.getLogger(LanSync.class.getName()).log(Level.SEVERE, null, ex);
                            } finally {
                                if (out != null) {
                                    try {
                                        out.close();
                                        out = null;
                                    } catch (IOException ex) {
                                        Logger.getLogger(LanSync.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                        f.setLastModified(electionData.getLastModified());
                    }

                    // Thêm vào snapshot
                    try {
                        if (!isExistInSnapshot(path, thisNode.getSnapshot())) {
                            thisNode.getSnapshot().insert(new LanSyncFileMetaData(path, false));
                        } else {
                            thisNode.getSnapshot().update(path, false);
                        }
                    } catch (SnapshotMappingException ex) {
                        Logger.getLogger(LanSync.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(LanSync.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void prepareParents(File f) {
        while (!f.getParentFile().exists()) {
            f = f.getParentFile();
            f.mkdir();
        }
    }

    private boolean isExistInSnapshot(String path, Snapshot snapshot) {
        for (Iterator<FileMetaData> iterator = snapshot.iterator(); iterator.hasNext();) {
            FileMetaData next = iterator.next();
            String nextPath = next.getFilePatch();
            if (path.equals(nextPath)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws SocketException,
            RemoteException, NotBoundException, IOException {
        LanSync l = new LanSync(FileUtil.getUserDirectory());
        l.setPort(8889);
        l.setUserName("thinhnt");
        l.enable();
        l.sync();
    }

}
