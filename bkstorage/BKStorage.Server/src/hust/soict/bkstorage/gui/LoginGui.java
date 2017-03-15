/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.gui;

import hust.soict.bkstorage.bll.LoginBll;
import hust.soict.bkstorage.exceptions.IncorrectPasswordException;
import hust.soict.bkstorage.exceptions.NonExistentUserExcepiton;
import hust.soict.bkstorage.remotecontroller.Login;
import hust.soict.bkstorage.remoteentity.Package;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 *
 * @author toant_000
 */
public class LoginGui implements Login {

    public LoginGui() {
    }

    @Override
    public Package<String> login(String userName, String password) throws RemoteException {

        try {
            int id = new LoginBll().login(userName, password);

            //Đăng nhập thành công
            return new Package<>(id, "Đăng nhập thành công!");
        } catch (SQLException | ClassNotFoundException ex) {
            return new Package<>(-1, "Không kết nối được tới dữ liệu của máy chủ, "
                    + "Hãy thông báo cho quản trị viên!");
        } catch (NonExistentUserExcepiton | IncorrectPasswordException ex) {
            return new Package<String>(-1, ex.getMessage());
        }
    }

    @Override
    public long getTotalSize(int uid) throws RemoteException {
        
        try {
            LoginBll loginBll = new LoginBll();
            return loginBll.getTotalSize(uid);
        } catch (IOException ex) {
            return 0;
        }
        
    }
    

}
