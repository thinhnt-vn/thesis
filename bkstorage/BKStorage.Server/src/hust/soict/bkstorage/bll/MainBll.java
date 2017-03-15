/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.bll;

import hust.soict.bkstorage.dal.MainDal;
import hust.soict.bkstorage.remoteentity.MyFile;
import hust.soict.bkstorage.utils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author toant_000
 */
public class MainBll {

    public MainBll() {
    }

    /**
     * Lấy danh sách file con của thư mục pid
     *
     * @param uid
     * @param pid
     * @return
     * @throws java.sql.SQLException
     */
    public ArrayList<MyFile> getAllFileByParent(int uid, int pid) throws SQLException {

        MainDal mainDal = new MainDal();
        ResultSet rs = mainDal.getFileList(uid, pid);
        ArrayList<MyFile> list = new ArrayList<MyFile>();

        while (rs.next()) {
            int fid = rs.getInt("id");
            String path = rs.getString("path");
            int directory = rs.getInt("directory");
            boolean dir = directory == 1;
            long timeModified = rs.getLong("timemodified");

            list.add(new MyFile(fid, path, dir, timeModified, pid, uid));
        }

        return list;

    }

    /**
     * Lấy file ứng với path
     *
     * @param path
     * @param uid
     * @return
     * @throws java.sql.SQLException
     */
    public MyFile getFileByPath(String path, int uid) throws SQLException {

        MainDal mainDal = new MainDal();
        ResultSet rs = mainDal.getFileList(path, uid);
        MyFile myFile = null;
        while (rs.next()) {
            int fid = rs.getInt("id");
            int directory = rs.getInt("directory");
            boolean dir = directory == 1;
            long timeModified = rs.getLong("timemodified");
            int parentID = rs.getInt("parentid");
            myFile = new MyFile(fid, path, dir, timeModified, parentID, uid);
        }

        return myFile;
    }

    /**
     * Tạo dữ liệu cho file
     *
     * @param f
     * @throws IOException
     */
    public void makeData(MyFile f) throws IOException {

        File serverFile = FileUtil.convert2ServerFile(f);
        byte[] data = new MainDal().readDataFromFile(serverFile);
        f.setData(data);

    }

    /**
     * Lưu trữ file mà client gửi lên
     *
     * @param f
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public void storageFile(MyFile f) throws IOException, SQLException {
        MainDal mainDal = new MainDal();
        byte[] data = f.getData();
        File serverFile = FileUtil.convert2ServerFile(f);
        if (!f.isDerectory()) {
            mainDal.writeDataIntoFile(data, serverFile);
        } else {
            serverFile.mkdir();
        }

        serverFile.setLastModified(f.getTimeModified());

        if (!mainDal.isExistInDB(f.getPath(), f.getUserID())) {
            mainDal.insertFileRecord(f.getPath(), f.isDerectory(), f.getTimeModified(),
                    f.getParentID(), f.getUserID());
        } else {
            mainDal.updateFileRecord(f.getPath(), f.getTimeModified(), f.getUserID());
        }
    }

    /**
     * Xóa file trên server
     *
     * @param f
     * @throws SQLException
     * @throws java.io.IOException
     */
    public void delete(MyFile f) throws SQLException, IOException {

        MainDal mainDal = new MainDal();
        // Xóa bản ghi trong DB
        int id = f.getId();
        mainDal.deleteFileRecord(id);
        // Xóa file trên đĩa cứng của server
        mainDal.delete(f);

    }

}
