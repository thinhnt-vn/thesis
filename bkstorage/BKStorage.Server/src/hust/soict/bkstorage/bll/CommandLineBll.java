/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.bll;

import hust.soict.bkstorage.constants.Options;
import hust.soict.bkstorage.dal.CommandLineDal;
import hust.soict.bkstorage.dal.Dal;
import hust.soict.bkstorage.exceptions.CreateUserException;
import hust.soict.bkstorage.exceptions.ErrorCommandException;
import hust.soict.bkstorage.exceptions.StartupExeption;
import hust.soict.bkstorage.gui.FactoryGui;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

/**
 *
 * @author toant_000
 */
public class CommandLineBll {

    public static final String DEFAULT_PASSWORD = "123456";     // Mật khẩu mặc định cho người dùng mới    
    public static final String DEFAULT_CAPACITY = "15";     // Dung lượng mặc định cho người dùng    

    public CommandLineBll() {
    }

    /**
     * Kết nối tới SQL Server
     *
     * @param serverName
     * @param port
     * @param userName
     * @param password
     * @param dBName
     * @throws NumberFormatException
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws ErrorCommandException
     */
    public void connect2DB(String serverName, String port, String userName,
            String password, String dBName) throws NumberFormatException,
            ClassNotFoundException, SQLException, ErrorCommandException {

        if (serverName == null) {
            serverName = "localhost";
        }

        if (port == null) {
            port = "3306";
        }

        if (userName == null) {
            throw new ErrorCommandException("Lỗi! Bạn chưa nhập tên đăng nhập vào"
                    + " CSDL (thuộc tính \"-u\").");
        }

        if (password == null) {
            throw new ErrorCommandException("Lỗi! Bạn chưa nhập mật khẩu nhập vào "
                    + "CSDL (thuộc tính \"-p\").");
        }

        if (dBName == null) {
            throw new ErrorCommandException("Lỗi! Bạn chưa nhập tên cơ sở dữ liệu "
                    + "(thuộc tính \"-database\").");
        }
        int portValue = Integer.parseInt(port);
        new Dal().connect2SQLServer(serverName, portValue, userName, password, dBName);
    }

    /**
     * Mở dịch vụ của server
     *
     * @param port
     * @throws RemoteException
     * @throws StartupExeption
     */
    public void startup(String port) throws RemoteException, StartupExeption {

        if (Dal.getConnection() == null) {
            throw new StartupExeption("Lỗi! Không thể chạy dịch vụ khi chưa kết "
                    + "nối tới SQL Server");
        }

        if (port == null) {
            port = "8888";
        }

        int portValue;
        try {
            portValue = Integer.parseInt(port);
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Lỗi! Cổng phải là số.");
        }

        FactoryGui factoryGui = new FactoryGui();
        System.setProperty("java.rmi.server.hostname", Options.BIND_IP_VALUE);
        factoryGui.exportEntity(portValue);
        Registry registry = LocateRegistry.createRegistry(portValue);
        Remote stub = UnicastRemoteObject.exportObject(factoryGui, portValue);
        registry.rebind("factory", stub);
    }

    /**
     * Tạo người dùng mới
     *
     * @param name
     * @param userName
     * @param password
     * @param capacity
     * @throws ErrorCommandException
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     * @throws hust.soict.bkstorage.exceptions.CreateUserException
     */
    public void createUser(String name, String userName, String password, String capacity)
            throws ErrorCommandException, SQLException, IOException, CreateUserException {

        if (Dal.getConnection() == null) {
            throw new CreateUserException("Lỗi! Không thể thêm người dùng khi chưa kết "
                    + "nối tới SQL Server");
        }

        if (name == null) {
            throw new ErrorCommandException("Lỗi! Bạn chưa nhập tên của người dùng mới.");
        }

        if (userName == null) {
            throw new ErrorCommandException("Lỗi! Bạn chưa nhập tên đăng nhập "
                    + "cho người dùng mới.");
        }

        if (password == null) {
            password = DEFAULT_PASSWORD;
        }

        name = name.trim();
        userName = userName.trim();
        password = password.trim();

        if (name.isEmpty()) {
            throw new ErrorCommandException("Lỗi! Bạn chưa nhập tên của mình.");
        }

        if (userName.isEmpty()) {
            throw new ErrorCommandException("Lỗi! Tên đăng nhập phải chứa chữ cái.");
        }

        if (password.isEmpty()) {
            throw new ErrorCommandException("Lỗi! Mật khẩu phải chứa chữ cái.");
        }

        if (userName.contains(" ")) {
            throw new ErrorCommandException("Lỗi! Tên đăng nhập không được chứa khoảng trắng.");
        }

        if (capacity == null) {
            capacity = DEFAULT_CAPACITY;
        }

        int capacityInt;

        try {
            capacityInt = Integer.parseInt(capacity);
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Lỗi! Dung lượng phải là số.");
        }

        CommandLineDal commandLineDal = new CommandLineDal();

        // Kiểm tra xem người dùng đã tồn tại chưa
        if (commandLineDal.isExist(userName)) {
            throw new ErrorCommandException("Lỗi! Tên đăng nhập đã tồn tại.");
        }

        // Thêm
        commandLineDal.insertUser(name, userName, password, capacityInt);

        //Tạo container cho người dùng
        int userID = commandLineDal.getID(userName);

        commandLineDal.createContainerForUser(userID);
    }

}
