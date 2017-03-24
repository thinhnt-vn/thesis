/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.bll;

import hust.soict.bkstorage.dal.ConfigDal;
import hust.soict.bkstorage.exception.TextFieldEmptyException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 *
 * @author toant_000
 */
public class ConfigBll{

    private final String serverName;
    private final String port;

    public ConfigBll(String serverName, String port) {
        this.serverName = serverName;
        this.port = port;
    }

    /**
     * Thực hiện kết nối thử đến Server
     *
     * @throws TextFieldEmptyException
     * @throws java.rmi.RemoteException
     * @throws java.rmi.NotBoundException - Không tìm thấy đối tượng
     */
    public void connect() throws TextFieldEmptyException, RemoteException, NotBoundException {

        /*
         Kiểm tra xem có trường nào bị trống không
         */
        if (serverName.isEmpty()) {
            throw new TextFieldEmptyException("Bạn chưa nhập tên máy chủ!");
        }

        if (port.isEmpty()) {
            throw new TextFieldEmptyException("Bạn chưa nhập số cổng!");
        }

        int portValue;
        try {
            portValue = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Nhập sai cổng! Cổng phải là số.");
        }

        // Kết nối tới Server
        ConfigDal configDal = new ConfigDal(serverName, portValue);
        configDal.connect();
    }

    /**
     * Lưu thông số xuống file
     * @throws IOException 
     */
    public void save() throws IOException{
        new ConfigDal(serverName, Integer.parseInt(port)).write();
    }
    

}
