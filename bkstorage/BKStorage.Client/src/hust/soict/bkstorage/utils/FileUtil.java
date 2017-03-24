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
import java.util.ArrayList;

/**
 *
 * @author toant_000
 */
public class FileUtil {

    /**
     * Hàm này tạo ra file config và trả về file tạo được. Nếu file đã được tạo
     * thì trả luôn về file đã tạo.
     *
     * @return
     * @throws java.io.IOException
     */
    public static File makeConfigFile() throws IOException {

        // Tạo thư mục chứa các file nếu chưa được tạo
        File parent = new File(FileConstant.FILE_DIR);
        parent.mkdir();

        // Tạo và trả lại file cấu hình trong thư mục
        File result = new File(parent, FileConstant.CONFIG_FILE_NAME);
        result.createNewFile();
        return result;
    }

    /**
     * Tạo và trả về file để lưu tên đăng nhập và mật khẩu
     *
     * @return
     * @throws IOException
     */
    public static File makeLoginFile() throws IOException {
        // Tạo thư mục chứa các file nếu chưa được tạo
        File parent = new File(FileConstant.FILE_DIR);
        parent.mkdir();

        // Tạo và trả lại file cấu hình trong thư mục
        File result = new File(parent, FileConstant.LOGIN_FILE_NAME);
        result.createNewFile();
        return result;
    }

    /**
     * Tạo thư mục chứa dữ liệu của người dùng
     *
     * @return
     */
    public static boolean makeUserDirectory() {

        String homeDirectoryPath = System.getProperty("user.home");
        File homeDirectory = new File(homeDirectoryPath);
        File result = new File(homeDirectory, FileConstant.USER_DIR);
        return result.mkdir();

    }

    /**
     * Lấy thư mục chứa dữ liệu người dùng
     *
     * @return
     */
    public static File getUserDirectory() {
        String homeDirectoryPath = System.getProperty("user.home");
        File homeDirectory = new File(homeDirectoryPath);
        File result = new File(homeDirectory, FileConstant.USER_DIR);
        return result;
    }

//    /**
//     * Tạo ra file snapshot
//     *
//     * @return
//     * @throws IOException
//     */
//    public static File makeSnapshot() throws IOException {
//
//        // Tạo thư mục chứa các file nếu chưa được tạo
//        File parent = new File(FileConstant.FILE_DIR);
//        parent.mkdir();
//
//        // Tạo và trả lại file cấu hình trong thư mục
//        File result = new File(parent, FileConstant.SNAPSHOT_FILE_NAME);
//        result.createNewFile();
//        return result;
//
//    }

    /**
     * AutoSyncFile là 1 cờ trạng thái đồng bộ của người dùng Nều file này tồn
     * tại thì đang ở chế độ tự động đồng bộ
     *
     * @return
     */
    public static File makeaaAutoSyncFile() {

        // Tạo thư mục chứa các file nếu chưa được tạo
        File parent = new File(FileConstant.FILE_DIR);
        parent.mkdir();

        // Tạo và trả lại file cấu hình trong thư mục
        File result = new File(parent, FileConstant.AUTOSYNC_FILE_NAME);
        try {
            result.createNewFile();
        } catch (IOException ex) {
            return null;
        }
        return result;
    }

    /**
     * Tạo thư mục
     *
     * @param f
     * @return
     */
    public static boolean mkdir(MyFile f) {

        File clientFile = FileUtil.convert2ClientFile(f);
        boolean result = clientFile.mkdir();
        clientFile.setLastModified(f.getTimeModified());
        return result;

    }

    /**
     * Xóa file AutoSync
     */
//    public static void deleteaAutoSyncFile() {
//
//        File f = makeAutoSyncFile();
//        if (f != null) {
//            f.delete();
//        }
//
//    }

    /**
     * Kiểm tra xem đang có ở trạng thái tự động đồng bộ không
     *
     * @return
     */
//    public static boolean isaAutoSyncFile() {
//
//        // Tạo thư mục chứa các file nếu chưa được tạo
//        File parent = new File(FileConstant.FILE_DIR);
//        parent.mkdir();
//
//        File f = new File(parent, FileConstant.AUTOSYNC_FILE_NAME);
//        return f.exists();
//
//    }

    /**
     * Chuyển đổi file chung thành file của client
     *
     * @param f
     * @return
     */
    public static File convert2ClientFile(MyFile f) {
        String commonPath = f.getPath();
        File userDirectory = getUserDirectory();
        return new File(userDirectory, commonPath);
    }

    /**
     *
     * @param f
     * @return
     */
    public static MyFile convert2CommonFile(File f) {
        String commonPath = getCommonPath(f);
        return new MyFile(-1, commonPath, f.isDirectory(), f.lastModified(), -1, -1);
    }

    /**
     * Lấy ra đường dẫn (ở dạng chung) của file
     *
     * @param f
     * @return
     */
    public static String getCommonPath(File f) {
        if (f == null) {
            return null;
        }

        String clientPath = f.getPath();
        int start = clientPath.indexOf(FileConstant.USER_DIR)
                + FileConstant.USER_DIR.length() + 1;
        if (start > clientPath.length()) {
            return "";
        }
        return clientPath.substring(start).replace("\\", "/");
    }

    /**
     * Lấy ra file (từ danh sách) ứng với đường dẫn
     *
     * @param list
     * @param path
     * @return
     */
    public static MyFile getFileFromList(ArrayList<MyFile> list, String path) {

        if (list == null || path == null) {
            return null;
        }

        for (MyFile li : list) {
            if (li.getPath().equals(path)) {
                return li;
            }
        }

        return null;

    }

    /**
     * Xóa file hoặc thư mục (gồm tất cả các tập tin con)
     *
     * @param f
     * @return
     */
    public static boolean delete(File f) {

        if (f.isDirectory()) {
            String[] names = f.list();
            for (String name : names) {
                delete(new File(f, name));
            }
        }
        return f.delete();

    }

    /**
     * Xóa file hoặc thư mục - file ở dạng chung
     *
     * @param f
     * @return
     */
    public static boolean delete(MyFile f) {

        return delete(convert2ClientFile(f));

    }

//    /**
//     * Xóa tất cả file lưu trạng thái của người dùng. Được goi khi đăng xuất
//     * khỏi hệ thống
//     * @throws java.io.IOException
//     */
//    public static void deleteAllConfigFile() throws IOException {
//        makeLoginFile().delete();
//        makeSnapshot().delete();
//    }

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
