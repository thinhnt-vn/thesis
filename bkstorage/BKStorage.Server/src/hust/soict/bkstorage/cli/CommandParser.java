/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.cli;

import hust.soict.bkstorage.bll.CommandLineBll;
import hust.soict.bkstorage.bll.FileBrowserBll;
import hust.soict.bkstorage.constants.CommandNameConstant;
import hust.soict.bkstorage.constants.CommandOptionConstant;
import hust.soict.bkstorage.constants.FileConstant;
import hust.soict.bkstorage.exceptions.CreateUserException;
import hust.soict.bkstorage.exceptions.EmptyCommandException;
import hust.soict.bkstorage.exceptions.ErrorCommandException;
import hust.soict.bkstorage.exceptions.StartupExeption;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * Phân tích dòng lệnh do người dùng nhập.
 *
 * @author toant_000
 */
public class CommandParser {

    protected String command;

    public CommandParser() {
    }

    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Phân tích lệnh và thực hiện các yêu cầu tương ứng
     *
     * @throws hust.soict.k57.it3650.exception.EmptyCommandException - Người
     * dùng chỉ nhập khoảng trắng
     * @throws hust.soict.k57.it3650.exception.ErrorCommandException
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws java.rmi.RemoteException
     * @throws hust.soict.k57.it3650.exception.StartupExeption
     * @throws hust.soict.k57.it3650.exception.CreateUserException
     *
     */
    public void parse() throws EmptyCommandException, ErrorCommandException,
            NumberFormatException, ClassNotFoundException, SQLException, RemoteException, StartupExeption, IOException, CreateUserException {

        prepare();
        String commandName = getCommandName();

        if (commandName.equalsIgnoreCase(CommandNameConstant.CONNECT_DB_CMD_NAME)) {
            parseDBConnection();
            newLine("Kết nối thành công tới SQL Server!");
        } else if (commandName.equalsIgnoreCase(CommandNameConstant.STARTUP_SERVER_CMD_NAME)) {
            parseStartup();
            newLine("Đang chạy dịch vụ...");
        } else if (commandName.equalsIgnoreCase(CommandNameConstant.CREATE_USER_CMD_NAME)) {
            parseCreateUser();
            newLine("Thêm thành công người dùng!");
        } else if (commandName.equalsIgnoreCase(CommandNameConstant.FILE_BROWSER_CMD_NAME)) {
            parseFileBrowser();
        } else if (commandName.equalsIgnoreCase(CommandNameConstant.HELP_CMD_NAME)) {
            parseHelp();
        } else if (commandName.equalsIgnoreCase(CommandNameConstant.EXIT_CMD_NAME)) {
            newLine("Tạm biệt!");
            System.exit(0);
        } else if (commandName.isEmpty()) {
            throw new EmptyCommandException();
        } else if (commandName.equalsIgnoreCase("test")) {  //Backdoor
            CommandLineBll commandLineBll = new CommandLineBll();
            commandLineBll.connect2DB("localhost", "3306", "root", "526792", "mystorage");
            commandLineBll.startup("8888");
            newLine("Đang chạy dịch vụ...");
        } else {
            throw new ErrorCommandException("Lỗi! Không có lệnh " + commandName);
        }
    }

    /**
     * Phân tích lệnh kết nối tới CSDL
     *
     * @param command
     * @throws ErrorCommandException
     */
    private void parseDBConnection() throws ErrorCommandException,
            NumberFormatException, ClassNotFoundException, SQLException {

        String serverName = null;
        String port = null;
        String dBName = null;
        String userName = null;
        String password = null;

        // Lấy vị trí của thuộc tính đầu tiên
        int index = command.indexOf("-", 0);
        if (index < 0) {
            throw new ErrorCommandException("Lỗi! Bạn chưa thiết lập các thuộc tính để "
                    + "kết nối tới CSDL!");
        }

        // Duyệt tưng thuộc tính trong câu lệnh
        while (index >= 0) {

            // Lấy tên của thuộc tính
            int spaceIndex = command.indexOf(' ', index);
            if (spaceIndex < 0) {
                throw new ErrorCommandException("Lỗi! Bạn chưa thiết lập giá trị "
                        + "cho thuộc tính \"" + command.substring(index, command.length()) + "\"");
            }
            String opt = command.substring(index, spaceIndex);

            // So sánh tên
            if (opt.equals(CommandOptionConstant.SERVER_OPT)) {
                serverName = getValue(spaceIndex);
            } else if (opt.equals(CommandOptionConstant.PORT_OPT)) {
                port = getValue(spaceIndex);
            } else if (opt.equals(CommandOptionConstant.DATABASE_OPT)) {
                dBName = getValue(spaceIndex);
            } else if (opt.equals(CommandOptionConstant.USERNAME_OPT)) {
                userName = getValue(spaceIndex);
            } else if (opt.equals(CommandOptionConstant.PASSWORD_OPT)) {
                password = getValue(spaceIndex);
            } else {
                throw new ErrorCommandException("Lỗi! Lệnh \"mysql\" không hỗ trợ thuộc tính: \"" + opt + "\"");
            }

            // Lấy vị trí của thuộc tính tiếp theo
            index = command.indexOf("-", index + 1);

        }

        new CommandLineBll().connect2DB(serverName, port, userName, password, dBName);

    }

    /**
     * Phân tích lệnh mở dịch vụ của Server
     *
     * @param command
     */
    private void parseStartup() throws ErrorCommandException, RemoteException, StartupExeption {

        String port = null;

        // Lấy vị trí của thuộc tính đầu tiên
        int index = command.indexOf("-", 0);
        if (index < 0) {
            port = "8888";
        }

        // Duyệt tưng thuộc tính trong câu lệnh
        while (index >= 0) {

            // Lấy tên của thuộc tính
            int spaceIndex = command.indexOf(' ', index);
            if (spaceIndex < 0) {
                throw new ErrorCommandException("Lỗi! Bạn chưa thiết lập giá trị "
                        + "cho thuộc tính \"" + command.substring(index,
                                command.length()) + "\"");
            }
            String opt = command.substring(index, spaceIndex);

            // So sánh tên
            if (opt.equals(CommandOptionConstant.PORT_OPT)) {
                port = getValue(spaceIndex);
            } else {
                throw new ErrorCommandException("Lỗi! Lệnh \"startup\" không hỗ "
                        + "trợ thuộc tính: \"" + opt + "\"");
            }

            // Lấy vị trí của thuộc tính tiếp theo
            index = command.indexOf("-", index + 1);

        }
        new CommandLineBll().startup(port);
    }

    /**
     * Phân tích lệnh tạo người dùng
     *
     * @param command
     */
    private void parseCreateUser() throws ErrorCommandException,
            SQLException, IOException, CreateUserException {

        String name = null;
        String userName = null;
        String password = null;
        String capacity = null;

        // Lấy vị trí của thuộc tính đầu tiên
        int index = command.indexOf("-", 0);
        if (index < 0) {
            throw new ErrorCommandException("Lỗi! Bạn chưa thiết lập các thuộc "
                    + "tính: tên người dùng (-name), tên đăng nhập (-u)!");
        }

        // Duyệt tưng thuộc tính trong câu lệnh
        while (index >= 0) {

            // Lấy tên của thuộc tính
            int spaceIndex = command.indexOf(' ', index);
            if (spaceIndex < 0) {
                throw new ErrorCommandException("Lỗi! Bạn chưa thiết lập giá trị "
                        + "cho thuộc tính \"" + command.substring(index, command.length()) + "\"");
            }
            String opt = command.substring(index, spaceIndex);

            // So sánh tên
            if (opt.equals(CommandOptionConstant.NAME_OPT)) {
                name = getValue(spaceIndex);
            } else if (opt.equals(CommandOptionConstant.USERNAME_OPT)) {
                userName = getValue(spaceIndex);
            } else if (opt.equals(CommandOptionConstant.PASSWORD_OPT)) {
                password = getValue(spaceIndex);
            } else if (opt.equals(CommandOptionConstant.CAPACITY_OPT)) {
                capacity = getValue(spaceIndex);
            } else {
                throw new ErrorCommandException("Lỗi! Lệnh \"mysql\" không hỗ trợ thuộc tính: \"" + opt + "\"");
            }

            // Lấy vị trí của thuộc tính tiếp theo
            index = command.indexOf("-", index + 1);

        }

        new CommandLineBll().createUser(name, userName, password, capacity);
    }

    /**
     * Phân tích lệnh mở trình duyệt file
     */
    private void parseFileBrowser() throws ErrorCommandException {

        FileBrowserBll browserBll = new FileBrowserBll();
        if (!browserBll.isConnected2DB()) {
            throw new ErrorCommandException("Lỗi! Không thể trình duyệt file khi chưa kết nối tới CSDL");
        }

        String id = null;
        String userName = null;

        // Lấy vị trí của thuộc tính đầu tiên
        int index = command.indexOf("-", 0);
        if (index < 0) {
            throw new ErrorCommandException("Lỗi! Bạn chưa thiết lập thuộc tính "
                    + "\"-u\" (tên người dùng muốn trình duyệt) hoặc \"-id\" (ID của người dùng muốn trình duyệt)!");
        }

        // Lấy tên của thuộc tính
        int spaceIndex = command.indexOf(' ', index);
        if (spaceIndex < 0) {
            throw new ErrorCommandException("Lỗi! Bạn chưa thiết lập giá trị "
                    + "cho thuộc tính \"" + command.substring(index, command.length()) + "\"");
        }
        String opt = command.substring(index, spaceIndex);

        // So sánh tên
        if (opt.equals(CommandOptionConstant.ID_OPT)) {
            id = getValue(spaceIndex);
        } else if (opt.equals(CommandOptionConstant.USERNAME_OPT)) {
            userName = getValue(spaceIndex);
        } else {
            throw new ErrorCommandException("Lỗi! Lệnh \"browser\" không hỗ trợ thuộc tính: \"" + opt + "\"");
        }

        if (id != null) {
            int idValue;
            try {
                idValue = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                throw new ErrorCommandException("Lỗi! Giá trị của thuộc tính -id phải là số");
            }
            try {
                userName = browserBll.getUserNameByID(idValue);
            } catch (SQLException ex) {
                throw new ErrorCommandException("Lỗi! Kết nối đến DB bị lỗi");
            }

            if (userName == null) {
                throw new ErrorCommandException("Lỗi! Không tồn tại id này");
            }

            new FileBrowserParser(idValue, userName).start();
            return;
        }

        if (userName != null) {
            int idValue;
            try {
                idValue = browserBll.getIDByUserName(userName);
            } catch (SQLException ex) {
                throw new ErrorCommandException("Lỗi! Kết nối đến DB bị lỗi");
            }

            if (idValue < 0) {
                throw new ErrorCommandException("Lỗi! Không tồn tại người dùng này");
            }

            new FileBrowserParser(idValue, userName).start();
        }

    }

    /**
     * Xư lý lệnh help
     * @throws hust.soict.k57.it3650.exception.ErrorCommandException
     * @throws java.io.IOException
     */
    protected void parseHelp() throws ErrorCommandException, IOException {

        int spaceIndex = command.indexOf(' ');
        if (spaceIndex < 0) {
            showFileContent(new File(FileConstant.HELP_DIR, FileConstant.MYSTORAGE_HELP_FILE));
            return;
        }

        String cmd = command.substring(spaceIndex + 1);
        switch (cmd) {
            case CommandNameConstant.CONNECT_DB_CMD_NAME:
                showFileContent(new File(FileConstant.HELP_DIR, FileConstant.MYSQL_HELP_FILE));
                return;
            case CommandNameConstant.STARTUP_SERVER_CMD_NAME:
                showFileContent(new File(FileConstant.HELP_DIR, FileConstant.STARTUP_HELP_FILE));
                return;
            case CommandNameConstant.CREATE_USER_CMD_NAME:
                showFileContent(new File(FileConstant.HELP_DIR, FileConstant.CREATEUSER_HELP_FILE));
                return;
            case CommandNameConstant.FILE_BROWSER_CMD_NAME:
                showFileContent(new File(FileConstant.HELP_DIR, FileConstant.BROWSER_HELP_FILE));
                return;
            case CommandNameConstant.EXIT_CMD_NAME:
                showFileContent(new File(FileConstant.HELP_DIR, FileConstant.EXIT_HELP_FILE));
                return;
            default:
                throw new ErrorCommandException("Lỗi! Không tìm thấy lệnh " + cmd);
        }

    }

    /**
     * Loại bỏ những khoảng trắng không cần thiết trong lệnh nguyên thủy
     *
     */
    protected void prepare() {

        //Xóa bỏ khoảng trắng ở 2 đầu
        command = command.trim();

        // Xóa bỏ khoảng trắng liền nhau
        while (command.indexOf("  ") > 0) {
            command = command.replaceAll("  ", " ");
        }

    }

    /**
     * Lấy ra tên của lệnh
     *
     * @return
     */
    protected String getCommandName() {

        int index = command.indexOf(' ');
        if (index < 0) {
            return command;
        }
        return command.substring(0, index);
    }

    /**
     * Lấy giá trị (bắt đầu từ vị trí spaceIndex) của thuộc tính
     *
     * @param spaceIndex
     * @return null nếu không lấy được giá trị
     * @throws hust.soict.k57.it3650.exception.ErrorCommandException
     */
    protected String getValue(int spaceIndex) throws ErrorCommandException {

        String result;
        boolean isString = false;
        if (command.charAt(spaceIndex + 1) == '\'') {   // Trường hượp gián trị là chuỗi
            spaceIndex++;
            isString = true;
        }
        int nextSpaceIndex = command.indexOf(command.charAt(spaceIndex), spaceIndex + 1);
        if (nextSpaceIndex < 0) {
            result = command.substring(spaceIndex + 1, command.length());
        } else {
            result = command.substring(spaceIndex + 1, nextSpaceIndex);
        }

        if (result.charAt(0) == '-') {
            return null;
        }

        if (isString && result.indexOf('-') > 0) {
            throw new ErrorCommandException("Lỗi! Giá trị của thuộc tính không "
                    + "được chứa ký tự \'-\'");
        }

        return result;

    }

    /**
     * Hiển thị nội dung 1 file
     *
     * @param f
     * @throws java.io.FileNotFoundException
     */
    protected void showFileContent(File f) throws FileNotFoundException, IOException {
        
        BufferedReader in = new BufferedReader(new FileReader(f));
        String line;
        while ((line = in.readLine()) != null) {            
            System.out.println("\t" + line);
        }
        
    }

    protected void newCommandLine() {
        System.out.print("\n\t->MyStorage$");
    }

    protected void newLine(String meassage) {
        System.out.print("\t->" + meassage);
    }
}
