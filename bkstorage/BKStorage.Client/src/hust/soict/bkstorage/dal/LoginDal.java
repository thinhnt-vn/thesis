/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import hust.soict.bkstorage.remoteentity.Package;
import hust.soict.bkstorage.remotecontroller.Login;
import hust.soict.bkstorage.utils.FileUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.io.RandomAccessFile;

/**
 *
 * @author toant_000
 */
public class LoginDal extends Dal {

    public LoginDal() {

    }

    /**
     *
     * @param userName
     * @param password
     * @return
     * @throws java.rmi.RemoteException
     */
    public Package<String> login(String userName, String password) throws RemoteException {
        Login loginServerGui = factory.createLogin();
        return loginServerGui.login(userName, password);
    }

    /**
     * Trả lại kích thước dữ liệu của người dùng lưu trên Server
     * @param uid 
     * @return  
     */
    public long getTotalSize(int uid) throws RemoteException {

        Login loginServerGui = factory.createLogin();
        return loginServerGui.getTotalSize(uid);
        
    }

    /**
     * Ghi tên đăng nhập và mật khẩu
     *
     * @param userName
     * @param password
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void write(String userName, String password) throws FileNotFoundException, IOException {
        RandomAccessFile io = new RandomAccessFile(FileUtil.makeLoginFile(), "rw");
        io.writeBytes("<username>");
        io.writeBytes(userName);
        io.writeBytes("</username>");
        io.writeBytes("<password>");
        io.writeBytes(password);
        io.writeBytes("</password>");
        io.close();
    }

}
