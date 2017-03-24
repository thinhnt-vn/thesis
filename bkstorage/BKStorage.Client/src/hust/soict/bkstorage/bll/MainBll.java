/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.bll;

import hust.soict.bkstorage.dal.Dal;
import hust.soict.bkstorage.dal.MainDal;
import hust.soict.bkstorage.exception.DownloadExeption;
import hust.soict.bkstorage.exception.OptionsMappingException;
import hust.soict.bkstorage.exception.SnapshotMappingException;
import hust.soict.bkstorage.remoteentity.MyFile;
import hust.soict.bkstorage.utils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author toant_000
 */
public class MainBll extends Bll {

    public MainBll() {
    }

    /**
     * Người dùng tắt chương trình
     *
     * @throws OptionsMappingException
     * @throws IOException
     */
    public void shutoff() throws OptionsMappingException, IOException {
        Dal.storeConfig();
        Dal.storeOptions();
    }

    /**
     * Kiểm tra trạng thái autosync
     *
     * @return
     */
    public boolean isAutoSync() {
        return new MainDal().isAutoSync();
    }

    /**
     * Tắt chế độ đồng bộ tự động
     */
    public void disableAutoSync() {
        new MainDal().setAutoSync(false);
    }

    /**
     * Bật chế độ đồng bộ tự động
     */
    public void enableAutoSync() {
        new MainDal().setAutoSync(true);
    }

    /**
     * Thực hiện khi người dùng đăng xuất
     *
     * @throws hust.soict.bkstorage.exception.SnapshotMappingException
     * @throws hust.soict.bkstorage.exception.OptionsMappingException
     */
    public void logout() throws SnapshotMappingException, OptionsMappingException {
        MainDal mainDal = new MainDal();
        mainDal.clearSnapshot();
        mainDal.clearUserOptions();
    }

    /**
     * Tải tất cả các file của người dùng từ server về Hàm này được gọi khi
     * người dùng đăng nhập lần đầu
     *
     * @throws hust.soict.bkstorage.exception.DownloadExeption
     */
    public void downloadAll() throws DownloadExeption {
        MainDal mainDal = new MainDal();
        try {
            // Lấy danh sách tất cả các file
            ArrayList<MyFile> list = mainDal.getAllFileByParent(id, -1);

            if (list == null) {
                throw new DownloadExeption("SQL.Không tải được dữ liệu!");
            }

            // Lấy nội dung của file và ghi xuống đĩa
            for (MyFile f : list) {
                download(f);
            }
        } catch (IOException | SnapshotMappingException ex) {
            try {
                mainDal.clearSnapshot();
            } catch (SnapshotMappingException ex1) {
                Logger.getLogger(MainBll.class.getName()).log(Level.SEVERE, null, ex1);
            }
            throw new DownloadExeption("Không download được dữ liệu. Error: " + ex.getMessage());
        }
    }

    /**
     * Tải dữ liệu của file (thư mục) tương ứng về máy
     *
     * @param f
     */
    private void download(MyFile f) throws RemoteException, IOException, SnapshotMappingException {

        MainDal mainDal = new MainDal();
        if (!f.isDerectory()) {
            f = mainDal.getData(f);
            if (f != null) {
                mainDal.write(f);
            }
        } else {
            FileUtil.mkdir(f);
            ArrayList<MyFile> list = mainDal.getAllFileByParent(id, f.getId());
            for (MyFile fi : list) {
                download(fi);
            }
        }

        if (!mainDal.isExistInSnapshot(f)) {
            mainDal.insertIntoSnapshot(f);
        }
    }

    /**
     * Upload 1 file hoặc thư mục
     *
     * @param clientFile
     */
    private void upload(File clientFile) throws RemoteException, IOException,
            SnapshotMappingException {
        MainDal mainDal = new MainDal();
        String path = FileUtil.getCommonPath(clientFile);
        boolean isDirectory = clientFile.isDirectory();
        long timeModified = clientFile.lastModified();
        int parentID;
        if (clientFile.getParentFile().equals(FileUtil.getUserDirectory())) {
            parentID = -1;
        } else {
            File parentFile = clientFile.getParentFile();
            String parentCommonPath = FileUtil.getCommonPath(parentFile);
            MyFile serverFile = mainDal.getFileByPath(parentCommonPath, id);
            parentID = serverFile.getId();
            if (serverFile == null) {   // Nếu file đã bị xóa
                return;
            }
        }
        MyFile f = new MyFile(-1, path, isDirectory, timeModified, parentID, id);
        if (!isDirectory) {
            mainDal.makeData(f);
            mainDal.put(f);
        } else {
            mainDal.put(f);
            String[] childFileNames = clientFile.list();
            for (String childFileName : childFileNames) {
                File childFile = new File(clientFile, childFileName);
                upload(childFile);
            }
        }
        if (!mainDal.isExistInSnapshot(f)) {
            mainDal.insertIntoSnapshot(f);
        }
    }

    /**
     * Đồng bộ tất cả dữ liệu
     *
     * @throws java.rmi.RemoteException
     * @throws java.lang.ClassNotFoundException
     * @throws hust.soict.bkstorage.exception.SnapshotMappingException
     */
    public void sync() throws RemoteException, IOException, ClassNotFoundException,
            SnapshotMappingException {
        try {
            MainDal mainDal = new MainDal();

            // Lấy tất cả các file có parent id = -1
            ArrayList<MyFile> list = mainDal.getAllFileByParent(id, -1);

            // Duyệt từng file con của thư mục MyStorage và đồng bộ chúng
            File userDir = FileUtil.getUserDirectory();
            String[] childFileNames = userDir.list();
            for (String childFileName : childFileNames) {
                File childFile = new File(userDir, childFileName);
                sync(childFile);
                // Setdirty = 0
                MyFile f = FileUtil.getFileFromList(list, FileUtil.getCommonPath(childFile));
                if (f != null) {
                    list.remove(f);
                }
            }

            // Còn lại là những file (hoặc thư mục) bị xóa bởi máy mình  hoặc được thêm bởi máy khác
            for (MyFile f : list) {
                if (mainDal.isExistInSnapshot(f)) {
                    // Nếu tồn tại trong snapshot -> File bị xóa bởi máy mình
                    // -> Xóa file trên server,
                    mainDal.deleteServerFile(f);
                    // -> Xóa trong snapshot
                    mainDal.deleteFromSnapshot(f);
                } else {
                    // File được thêm bởi máy khác -> Tải file về máy
                    download(f);
                }
            }
        } catch (RemoteException e) {
            throw new RemoteException("Lỗi!Kết nối bị gián đoạn.");
        }

    }

    /**
     * Đồng bộ file (gồm cả thư mục)
     *
     * @param clientFile /
     * @throws java.rmi.RemoteException
     * @throws java.lang.ClassNotFoundException
     */
    public void sync(File clientFile) throws RemoteException, IOException,
            ClassNotFoundException, SnapshotMappingException {
        MainDal mainDal = new MainDal();
        // Tìm file trên server tương ứng với f
        MyFile f = mainDal.getFileByPath(FileUtil.getCommonPath(clientFile), id);

        if (!clientFile.isDirectory()) {         //Nếu f là 1 file (không phải là thư mục)
            if (f != null) {      // Nếu tìm thấy
                if (clientFile.lastModified() > f.getTimeModified()) {
                    upload(clientFile);
                } else if (clientFile.lastModified() < f.getTimeModified()) {
                    download(f);
                }
            } else {
                f = FileUtil.convert2CommonFile(clientFile);
                /*
                 Nếu không tìm thấy chứng tỏ file được thêm bởi thiết bị hiện tại
                 hoặc file bị xóa bởi thiết bị khác
                 */
                if (mainDal.isExistInSnapshot(f)) {
                    /*
                     Nếu tìm thấy trong snapshot -> file bị xóa bởi thiết bị khác
                     */
                    FileUtil.delete(f);
                    mainDal.deleteFromSnapshot(f);
                } else {
                    /*
                     Nếu không thấy trong snapshot ->  file  được thêm bởi thiết bị hiện tại
                     */
                    upload(clientFile);
                }
            }
        } else {

            if (f != null) {        // Nếu tìm thấy thư mục tương ứng
                // Lấy tất cả các file con của f trên server
                ArrayList<MyFile> list = mainDal.getAllFileByParent(id, f.getId());

                // Duyệt từng file con của thư mục f (ở client) và đồng bộ chúng
                String[] childFileNames = clientFile.list();
                for (String childFileName : childFileNames) {
                    File childFile = new File(clientFile, childFileName);
                    sync(childFile);
                    // Setdirty = 0
                    MyFile overlookFile = FileUtil.getFileFromList(list, FileUtil.getCommonPath(childFile));
                    if (overlookFile != null) {
                        list.remove(overlookFile);
                    }
                }

                // Còn lại là những file (hoặc thư mục) bị xóa bởi máy mình  hoặc được thêm bởi máy khác
                for (MyFile fi : list) {
                    if (mainDal.isExistInSnapshot(fi)) {
                        // Nếu tồn tại trong snapshot -> File bị xóa bởi máy mình
                        // -> Xóa file trên server,
                        mainDal.deleteServerFile(fi);
                        // -> Xóa trong snapshot
                        mainDal.deleteFromSnapshot(fi);
                    } else {
                        // File được thêm bởi máy khác -> Tải file về máy
                        download(fi);
                    }
                }

            } else {
                /*
                 Nếu không tìm thấy chứng tỏ thư mục được thêm bởi thiết bị hiện tại
                 hoặc bị xóa bởi thiết bị khác
                 */
                f = FileUtil.convert2CommonFile(clientFile);

                if (mainDal.isExistInSnapshot(f)) {
                    /*
                     Nếu tìm thấy trong snapshot -> thư mục bị xóa bởi thiết bị khác
                     */
                    FileUtil.delete(f);
                    mainDal.deleteFromSnapshot(f);
                } else {
                    /*
                     Nếu không thấy trong snapshot ->  thư mục  được thêm bởi thiết bị hiện tại
                     */
                    upload(clientFile);
                }
            }
        }
    }
}
