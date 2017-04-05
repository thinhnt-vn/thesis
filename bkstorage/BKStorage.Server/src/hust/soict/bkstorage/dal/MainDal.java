/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import hust.soict.bkstorage.swift.Account;
import hust.soict.bkstorage.swift.Container;
import hust.soict.bkstorage.swift.ObjectBuilder;
import hust.soict.bkstorage.swift.StorageObject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author toant_000
 */
public class MainDal extends Dal {

    public MainDal() {
    }

    /**
     * Lấy tất cả các file con của thư mục pid
     *
     * @param uid
     * @param pid
     * @return
     * @throws SQLException
     */
    public ResultSet getFileList(int uid, int pid) throws SQLException {
        String query = "SELECT * FROM file WHERE userid = " + uid + " AND parentid = " + pid;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        return rs;
    }

    /**
     * Lấy file từ CSDL
     *
     * @param path
     * @param uid
     * @return
     * @throws java.sql.SQLException
     */
    public ResultSet getFileList(String path, int uid) throws SQLException {
        String query = "SELECT * FROM file WHERE path ='" + path
                + "' AND userid = " + uid;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    /**
     * Kiểm tra xem 1 file đã tồn tại trong DB hay chưa
     *
     * @param path
     * @param uid
     * @return
     * @throws SQLException
     */
    public boolean isExistInDB(String path, int uid) throws SQLException {
        String query = "SELECT 1 FROM file WHERE userid = " + uid
                + " AND path = '" + path + "'";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        return rs.first();
    }

    /**
     * Thêm 1 bản ghi file ứng với file f
     *
     * @param path
     * @param isDirectory
     * @param timeModified
     * @param pid
     * @param uid
     * @throws SQLException
     */
    public void insertFileRecord(String path, boolean isDirectory, long timeModified,
            int pid, int uid) throws SQLException {
        int dir = isDirectory ? 1 : 0;
        String query = "INSERT INTO file (path, directory, timemodified, parentid,"
                + " userid) VALUES ('" + path + "'," + dir + "," + timeModified
                + "," + pid + "," + uid + ")";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    /**
     * Cập nhật lại thời gian chỉnh sửa vào db
     *
     * @param path
     * @param timeModified
     * @param uid
     * @throws java.sql.SQLException
     */
    public void updateFileRecord(String path, long timeModified, int uid)
            throws SQLException {
        String query = "UPDATE file SET timemodified = " + timeModified + " WHERE "
                + "userid = " + uid + " AND path = '" + path + "'";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    /**
     * Xóa đối tượng và các bản ghi tương ứng trong DB
     *
     * @param id
     * @throws java.sql.SQLException
     */
    public void deleteFile(int id) throws SQLException {
        // Lấy ra các bản ghi con
        String query = "SELECT id FROM file WHERE parentid = " + id;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            int childID = rs.getInt(1);
            deleteFile(childID);
        }
        query = "SELECT userid, path, directory FROM file WHERE id = " + id;
        rs = statement.executeQuery(query);
        while (rs.next()) {
            String container = rs.getString(1);
            String path = rs.getString(2);
            int dir = rs.getInt(3);
            if (dir == 1) {
                path += "/";
            }
            getAccount().getContainer(container).deleteObject(path);
        }

        query = "DELETE FROM file WHERE id = " + id;
        statement.executeUpdate(query);
    }

    /**
     * Trả lại mảng dữ liệu của file f
     *
     * @param container
     * @param objName
     * @return
     * @throws java.io.FileNotFoundException
     */
    public byte[] readObjectData(String container, String objName) throws
            FileNotFoundException, IOException {
        StorageObject object = getAccount().getContainer(container)
                .getObject(objName);
        return object.getContent();
    }

    /**
     * Ghi dữ liệu của 1 file xuống ổ đĩa
     *
     * @param container
     * @param objName
     * @param data
     */
    public void writeDataIntoObject(String container, String objName,
            byte[] data) {
        StorageObject obj = ObjectBuilder.newBuilder()
                .name(objName)
                .content(data)
                .build();
        Account acc = getAccount();
        Container con = acc.getContainer(container);
        con.putObject(obj);
    }

    public void createFolderObject(String container, String objName) {
        StorageObject obj = ObjectBuilder.newBuilder()
                .name(objName + "/")
                .build();
        getAccount().getContainer(container).putObject(obj);
    }

}
