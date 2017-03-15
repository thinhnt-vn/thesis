/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import hust.soict.bkstorage.constants.FileConstant;
import hust.soict.bkstorage.remotecontroller.Main;
import hust.soict.bkstorage.remoteentity.MyFile;
import hust.soict.bkstorage.utils.FileUtil;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author toant_000
 */
public class MainDal extends Dal {

    public MainDal() {

    }

    /**
     * Lấy danh sách các file của người dùng có mã là id
     *
     * @param uid
     * @param pid
     * @return null nếu không lấy được
     * @throws java.rmi.RemoteException
     */
    public ArrayList<MyFile> getAllFileByParent(int uid, int pid) throws RemoteException {

        Main main = factory.createMain();
        return main.getAllFileByParent(uid, pid);

    }

    /**
     * Lấy 1 file từ server dựa vào path
     *
     * @param path
     * @param uid
     * @return
     * @throws java.rmi.RemoteException
     */
    public MyFile getFileByPath(String path, int uid) throws RemoteException {

        Main main = factory.createMain();
        return main.getFileByPath(path, uid);

    }

    /**
     * Đẩy file lên server
     *
     * @param f
     * @throws java.rmi.RemoteException
     */
    public void put(MyFile f) throws RemoteException {

        Main main = factory.createMain();
        main.put(f);
    }

    /**
     * Xóa file trên server
     *
     * @param f
     * @throws java.rmi.RemoteException
     */
    public void deleteServerFile(MyFile f) throws RemoteException  {

        Main main = factory.createMain();
        main.delete(f);

    }

    /**
     * Lấy dữ liệu (từ Server) cho file tương ứng
     *
     * @param file
     * @return
     * @throws java.rmi.RemoteException
     */
    public MyFile getData(MyFile file) throws RemoteException {

        Main main = factory.createMain();
        return main.getData(file);

    }

    /**
     * Lấy dữ liệu (từ ổ đĩa) lên file f
     *
     * @param f
     * @throws java.io.FileNotFoundException
     */
    public void makeData(MyFile f) throws FileNotFoundException, IOException {
        File clientFile = FileUtil.convert2ClientFile(f);
        RandomAccessFile in = new RandomAccessFile(clientFile, "r");
        byte[] data = new byte[(int) in.length()];
        in.readFully(data);
        f.setData(data);
        in.close();
    }

    /**
     * Ghi dữ liệu của file xuống đĩa cứng (ghi cả datemod)
     *
     * @param f
     * @throws java.io.IOException
     */
    public void write(MyFile f) throws IOException {

        File clientFile = FileUtil.convert2ClientFile(f);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(clientFile));
        byte[] data = f.getData();
        if (data != null) {
            out.write(data);
            out.flush();
        }
        out.close();
        clientFile.setLastModified(f.getTimeModified());

    }

    /**
     * Kiểm tra xem file f có tồn tại trong snapshot không
     *
     * @param f
     * @return
     * @throws java.io.FileNotFoundException
     */
    public boolean isExistInSnapshot(MyFile f) throws FileNotFoundException, IOException {

        String path = f.getPath();
        BufferedReader in = new BufferedReader(new FileReader(FileUtil.makeSnapshot()));
        String p;
        while ((p = in.readLine()) != null) {
            p = p.trim();
            if (path.equals(p)) {
                in.close();
                return true;
            }
        }
        in.close();
        return false;

    }

    /**
     * Thêm 1 file vào snapshot
     *
     * @param f
     * @throws java.io.IOException
     */
    public void insertIntoSnapshot(MyFile f) throws IOException {

        PrintWriter out = new PrintWriter(new FileWriter(FileUtil.makeSnapshot(), true));
        out.println(f.getPath());
        out.close();

    }

    /**
     * Xóa một file (cả các file con) lưu trong snapshot
     *
     * @param f
     * @throws java.io.IOException
     */
    public void deleteFromSnapshot(MyFile f) throws IOException {

        String path = f.getPath();
        File snapshotFile = FileUtil.makeSnapshot();
        File tempFile = File.createTempFile("stmp",
                ".tmp", new File(FileConstant.FILE_DIR));
        BufferedReader in = new BufferedReader(new FileReader(snapshotFile));
        PrintWriter out = new PrintWriter(new FileWriter(tempFile));
        String str;
        while ((str = in.readLine()) != null) {
            str = str.trim();
            if (str.startsWith(path)) {
                continue;
            }
            out.println(str);
        }
        in.close();
        out.close();
        snapshotFile.delete();
        tempFile.renameTo(snapshotFile);

    }
}
