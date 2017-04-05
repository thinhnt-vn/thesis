/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import static hust.soict.bkstorage.dal.Dal.connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author toant_000
 */
public class LoginDal extends Dal {

    public LoginDal() {
    }

    /**
     * Lấy mật khẩu ứng với tên người dùng
     *
     * @param userName
     * @return
     * @throws java.sql.SQLException - Kết nối bị lỗi
     */
    public String getPassword(String userName) throws SQLException {
        String query = "SELECT password FROM user WHERE username = '" + userName + "'";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        if (!rs.first()) {      // Không tìm thấy người dùng
            return null;
        }
        return rs.getString(1);
    }

    public long getUserByteUsed(int uid) {
        return getAccount().getContainer("" + uid).getBytesUsed();
    }

}
