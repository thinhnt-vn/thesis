/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.bll;

import hust.soict.bkstorage.dal.LoginDal;
import hust.soict.bkstorage.exception.LoginFailException;
import hust.soict.bkstorage.remoteentity.Package;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * Xử lý đăng nhập
 *
 * @author toant_000
 */
public class LoginBll extends Bll{

    private final String userName;
    private final String password;

    public LoginBll(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Đăng nhập tài khoản người dùng, ghi nhớ tài khoản và mật khẩu xuống file
     *
     * @throws java.rmi.RemoteException
     * @throws hust.soict.k57.it3650.exception.LoginFailException
     */
    public void login() throws RemoteException, LoginFailException {

        LoginDal loginDal = new LoginDal();
        Package<String> p = loginDal.login(userName, password);
        int lID = p.getID();
        if (p.getID() == -1) {
            throw new LoginFailException(p.getData());
        }
        LoginBll.id = lID;
    }
    
    /**
     * Trả lại kích thước dữ liệu của người dùng lưu trên Server
     * @return 
     * @throws java.rmi.RemoteException 
     */
    public long getTotalSize() throws RemoteException{
        
        long result = new LoginDal().getTotalSize(id);
        return result;
        
    }

    /**
     * Lưu tên người dùng và mật khẩu xuống file
     *
     */
    public void save()  {
        new LoginDal().write(userName, password);
    }
}
