/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import hust.soict.bkstorage.remoteentity.MyFile;
import static hust.soict.bkstorage.utils.FileUtil.convert2ServerFile;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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
        path = path.replace("\\", "\\\\");
        String query = "SELECT * FROM file WHERE path ='" + path + "' AND userid = " + uid;
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

        path = path.replace("\\", "\\\\");
        String query = "SELECT 1 FROM file WHERE userid = " + uid + " AND path = '" + path + "'";
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

        path = path.replace("\\", "\\\\");
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

        path = path.replace("\\", "\\\\");
        String query = "UPDATE file SET timemodified = " + timeModified + " WHERE "
                + "userid = " + uid + " AND path = '" + path + "'";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);

    }

    /**
     * Xóa bản ghi của file (gồm cả bản ghi của các file con) trong db
     *
     * @param id
     * @throws java.sql.SQLException
     */
    public void deleteFileRecord(int id) throws SQLException {

        // Lấy ra các bản ghi con
        String query = "SELECT id FROM file WHERE parentid = " + id;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            int childID = rs.getInt(1);
            deleteFileRecord(childID);
        }
        query = "DELETE FROM file WHERE id = " + id;
        statement.executeUpdate(query);

    }

    /**
     * Trả lại mảng dữ liệu của file f
     *
     * @param f
     * @return
     * @throws java.io.FileNotFoundException
     */
    public byte[] readDataFromFile(File f) throws FileNotFoundException, IOException {

        boolean finish = false;
        byte[] data = null;
        while (!finish) {
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
        }
        return data;

    }

    /**
     * Ghi dữ liệu của 1 file xuống ổ đĩa
     *
     * @param data
     * @param f
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void writeDataIntoFile(byte[] data, File f) throws FileNotFoundException, IOException {

        FileOutputStream outputStream = new FileOutputStream(f);
        BufferedOutputStream out = new BufferedOutputStream(outputStream);
        boolean finish = false;
        while (!finish) {
            FileChannel channel = outputStream.getChannel();
            FileLock look = channel.tryLock();
            if (look != null) {
                // Nếu file đang tự do (không bị tiến trình khác chiếm)
                try {
                    out.write(data);
                    out.flush();
                    finish = true;
                } finally {
                    look.release();
                    out.close();
                }
            } else {
                // File đã bị tiến trình khác khóa
                out.close();
                if (!f.exists()) {
                    finish = true;
                }
            }
        }

    }

    /**
     * Xóa file hoặc thư mục (gồm tất cả các tập tin con)
     *
     * @param f
     * @return
     */
    private boolean delete(File f) throws FileNotFoundException, IOException {

        if (f.isDirectory()) {
            String[] names = f.list();
            for (String name : names) {
                delete(new File(f, name));
            }
        }
        boolean result = false;
        while (f.exists()) {       
            f.delete();
        }
//        FileOutputStream out = new FileOutputStream(f);
//        boolean finish = false;
//        while (!finish) {
//            FileChannel channel = out.getChannel();
//            FileLock look = channel.tryLock();
//            if (look != null) {
//                // Nếu file đang tự do (không bị tiến trình khác chiếm)
//                try {
//                    out.close();
//                    result = f.delete();
//                    finish = true;
//                } finally {
//
//                }
//            } else {
//                // File đã bị tiến trình khác khóa
//                out.close();
//                if (!f.exists()) {
//                    finish = true;
//                }
//            }
//        }

        return result;
    }

    /**
     * Xóa file hoặc thư mục - file ở dạng chung
     *
     * @param f
     * @return
     * @throws java.io.IOException
     */
    public boolean delete(MyFile f) throws IOException {
        return delete(convert2ServerFile(f));
    }

}
