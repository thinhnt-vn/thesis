/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.cli;

import hust.soict.bkstorage.constants.CommandNameConstant;
import hust.soict.bkstorage.constants.FileConstant;
import hust.soict.bkstorage.exceptions.EmptyCommandException;
import hust.soict.bkstorage.exceptions.ErrorCommandException;
import hust.soict.bkstorage.utils.DateUtil;
import hust.soict.bkstorage.utils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cũng là bộ phân tích lệnh nhưng chỉ phân tích lệnh liên quan đến trình duyệt
 * file
 *
 * @author thinhnt
 */
public class FileBrowserParser extends CommandParser {

    private File currentFile;
    private boolean finished = false;
    private int uid;
    private String userName;

    public FileBrowserParser() {
        super();
    }

    public FileBrowserParser(int uid, String userName) {
        this.uid = uid;
        this.userName = userName;
        try {
            currentFile = FileUtil.getUserDirectory(uid);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void start() {

        Scanner scanner = new Scanner(System.in);
        while (!finished) {
            newCommandLine();
            command = scanner.nextLine();
            try {
                parse();
            } catch (EmptyCommandException ex) {
            } catch (ErrorCommandException ex) {
                newLine(ex.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(FileBrowserParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void parse() throws EmptyCommandException, ErrorCommandException, IOException {

        prepare();
        String commandName = getCommandName();

        if (commandName.equalsIgnoreCase(CommandNameConstant.CD_CMD_NAME)) {
            parseCD();
        } else if (commandName.equalsIgnoreCase(CommandNameConstant.DIR_CMD_NAME)) {
            parseDIR();
        } else if (commandName.equalsIgnoreCase(CommandNameConstant.HELP_CMD_NAME)) {
            parseHelp();
        } else if (commandName.equalsIgnoreCase(CommandNameConstant.EXIT_CMD_NAME)) {
            finished = true;
        } else if (commandName.isEmpty()) {
            throw new EmptyCommandException();
        } else {
            throw new ErrorCommandException("Lỗi! Không có lệnh " + commandName);
        }

    }

    /**
     * Xử lý lệnh cd
     *
     * @throws ErrorCommandException
     */
    private void parseCD() throws ErrorCommandException, IOException {

        int spaceIndex = command.indexOf(' ');
        if (spaceIndex < 0) {
            return;
        }

        String path = command.substring(spaceIndex + 1);
        File newCurrentFile = new File(new File(currentFile, path).getCanonicalPath());

        if (!newCurrentFile.exists()) {
            throw new ErrorCommandException("Lỗi! Không tìm thấy đường dẫn xác định.");
        }
        if (!newCurrentFile.isDirectory()) {
            throw new ErrorCommandException("Lỗi! Tên thư mục không chính xác.");
        }

        if (FileUtil.contain(newCurrentFile, new File(FileUtil.getUserDirectory(uid).getCanonicalPath()))) {
            throw new ErrorCommandException("Lỗi! Không được truy cập tới thư mục này");
        }

        currentFile = newCurrentFile;
    }

    /**
     * Xư lý lệnh DIR
     */
    private void parseDIR() throws ErrorCommandException, IOException {

        int spaceIndex = command.indexOf(' ');
        if (spaceIndex < 0) {
            show(currentFile);
            return;
        }

        String path = command.substring(spaceIndex + 1);
        File newFile = new File(currentFile, path);

        if (!newFile.exists()) {
            throw new ErrorCommandException("Lỗi! Không tìm thấy đường dẫn xác định.");
        }
        if (FileUtil.contain(newFile, FileUtil.getUserDirectory(uid))) {
            throw new ErrorCommandException("Lỗi! Không được truy cập tới thư mục này");
        }

        show(newFile);
    }

    protected void parseHelp() throws IOException, ErrorCommandException {
        
        int spaceIndex = command.indexOf(' ');
        if (spaceIndex < 0) {
            showFileContent(new File(FileConstant.HELP_DIR, FileConstant.BROWSERHELP_HELP_FILE));
            return;
        }

        String cmd = command.substring(spaceIndex + 1);
        switch (cmd) {
            case CommandNameConstant.CD_CMD_NAME:
                showFileContent(new File(FileConstant.HELP_DIR, FileConstant.CD_HELP_FILE));
                return;
            case CommandNameConstant.DIR_CMD_NAME:
                showFileContent(new File(FileConstant.HELP_DIR, FileConstant.DIR_HELP_FILE));
                return;
            case CommandNameConstant.EXIT_CMD_NAME:
                System.out.println("exit - Thoát khở File Broser");
                return;
            default:
                throw new ErrorCommandException("Lỗi! Không tìm thấy lệnh " + cmd);
        }

    }
    

    /**
     * Hiển thị các tập tin của thư mục f ra 1 bảng. Nểu f là tệp tin thì chỉ
     * hiện thông tin của f
     *
     * @param f
     */
    private void show(File f) {

        System.out.println("");
        showHeader();
        if (!f.isDirectory()) {
            newLine(f.getName(), f.length(), false, f.lastModified(), true);
        } else {
            newLine(".", f.length(), true, f.lastModified(), false);
            try {
                if (!f.equals(FileUtil.getUserDirectory(uid))) {
                    newLine("..", f.length(), true, f.lastModified(), false);
                }
            } catch (IOException ex) {
                Logger.getLogger(FileBrowserParser.class.getName()).log(Level.SEVERE, null, ex);
            }
            File[] childFiles = f.listFiles();
            for (int i = 0; i < childFiles.length; i++) {
                File childFile = childFiles[i];
                newLine(childFile.getName(), childFile.length(), childFile.isDirectory(),
                        childFile.lastModified(), i == childFiles.length - 1);
            }
        }

    }

    /**
     * Hiển thị tiêu đề cho bảng
     */
    private void showHeader() {

        System.out.printf("\t+------------------------------+-------------+-----+-------------------+\n");
        System.out.printf("\t|             NAME             | SIZE(BYTES) | DIR |   DATE MODIFIED   |\n");
        System.out.printf("\t+------------------------------+-------------+-----+-------------------+\n");

    }

    /**
     * Thêm một dòng mới vào bảng
     *
     * @param fileName
     * @param fileSize
     * @param isDirectory
     * @param lastModified
     * @param isLastLine
     */
    private void newLine(String fileName, long fileSize, boolean isDirectory, long lastModified, boolean isLastLine) {
        String size = String.valueOf(fileSize);
        String dir = isDirectory ? "Y" : "N";
        String date = DateUtil.convert2String(lastModified);
        System.out.printf("\t|%30s|%13s|%5s|%19s|\n", fileName, size, dir, date);
        if (isLastLine) {
            System.out.printf("\t+------------------------------+-------------+-----+-------------------+\n");
        }
    }

    protected void newCommandLine() {
        String path;
        path = FileUtil.getCommonPath(currentFile, uid);
        System.out.print("\n\t->MyStorage>FileBrowser>" + userName + "(" + uid + ")" + "$" + path + ">");
    }

}
