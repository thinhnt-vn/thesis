/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.utils;

import hust.soict.bkstorage.constants.FileConstant;
import hust.soict.bkstorage.remoteentity.MyFile;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author toant_000
 */
public class FileUtil {

    /**
     * Tạo thư mục của người dùng
     *
     * @param uid
     * @return
     * @throws java.io.IOException
     */
    public static File makeUserDirectory(int uid) throws IOException {
        // Tạo thư mục chứa các file nếu chưa được tạo
        File parent = new File(FileConstant.DATA_DIR);
        parent.mkdir();

        // Tạo và trả lại file cấu hình trong thư mục
        File result = new File(parent, String.valueOf(uid));
        result.mkdir();
        return result;
    }

    /**
     * Lấy thư mục của người dùng
     *
     * @param userID
     * @return
     * @throws java.io.IOException
     */
    public static File getUserDirectory(int userID) throws IOException {

        // Tạo thư mục chứa các file nếu chưa được tạo
        File parent = new File(FileConstant.DATA_DIR);

        // Tạo và trả lại file cấu hình trong thư mục
        File result = new File(parent, String.valueOf(userID));
        return result;

    }

    /**
     * Lấy ra đường dẫn (ở dạng chung) của file
     *
     * @param f
     * @param uid
     * @return
     */
    public static String getCommonPath(File f, int uid) {
        if (f == null) {
            return null;
        }

        String serverPath = null;
        try {
            serverPath = f.getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        String idStr = String.valueOf(uid);
        int start = serverPath.indexOf(idStr)
                + idStr.length() + 1;
        if (start > serverPath.length()) {
            return "";
        }
        String result;
        result = serverPath.substring(start);
        return result;

    }

    /**
     * Chuyển đổi MyFile thành file của Server
     *
     * @param file
     * @return
     * @throws java.io.IOException
     */
    public static File convert2ServerFile(MyFile file) throws IOException {

        File userDirectory = getUserDirectory(file.getUserID());
        return new File(userDirectory, file.getPath());

    }

    /**
     * Trả lại true nếu thư mục f1 chứa f2
     *
     * @param f1
     * @param f2
     * @return
     */
    public static boolean contain(File f1, File f2) {

        if (!f1.isDirectory()) {
            return false;
        }
        
        File [] childFiles = f1.listFiles();
        for (File childFile : childFiles) {
            if (childFile.equals(f2)){
                return true;
            }
            if (contain(childFile, f2)){
                return true;
            }
        }
        
        return false;
    }

    /**
     * Lấy ra kích thước thư mục hay tập tin
     *
     * @param f
     * @return
     */
    public static long getFileSize(File f) {

        if (f == null) {
            return 0;
        }

        if (!f.isDirectory()) {
            return f.length();
        } else {
            long result = 0;
            File[] files = f.listFiles();
            for (File file : files) {
                result += getFileSize(file);
            }
            return result;
        }
    }

}
