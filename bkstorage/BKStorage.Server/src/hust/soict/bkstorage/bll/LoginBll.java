/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.bll;

import hust.soict.bkstorage.dal.LoginDal;
import hust.soict.bkstorage.exceptions.IncorrectPasswordException;
import hust.soict.bkstorage.exceptions.NonExistentUserExcepiton;
import hust.soict.bkstorage.utils.FileUtil;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author toant_000
 */
public class LoginBll {

    public LoginBll() {
    }

    /**
     * Thực hiện đăng nhập, trả lại id của người dùng
     *
     * @param userName
     * @param password
     * @return 
     * @throws SQLException - Kết nối bị lỗi
     * @throws hust.soict.k57.it3650.exception.NonExistentUserExcepiton - Mật
     * khẩu không đúng
     * @throws hust.soict.k57.it3650.exception.IncorrectPasswordException - Mật
     * khẩu không đúng
     * @throws java.lang.ClassNotFoundException - Kho
     */
    public int login(String userName, String password) throws SQLException,
            NonExistentUserExcepiton, IncorrectPasswordException, ClassNotFoundException {
        
        LoginDal loginDal = new LoginDal();
        
        // Lấy mật khẩu từ csdl ứng với uername tương ứng
        String truthPassword = loginDal.getPassword(userName);
        if (truthPassword == null) {
            throw new NonExistentUserExcepiton("Không tồn tại người dùng này!");
        }

        if (!truthPassword.equals(password)) {
            throw new IncorrectPasswordException("Mật khẩu không đúng!");
        }

        return loginDal.getID(userName);
        
    }
    
    public long getTotalSize(int uid) throws IOException{
        
        long result = FileUtil.getFileSize(FileUtil.getUserDirectory(uid));
        return result;
        
    }

}
