/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author thinhnt
 */
public class FileBrowserDal extends Dal {

    public FileBrowserDal() {
        super();
    }

    /**
     * Lấy tên người dùng
     *
     * @param id
     * @return
     * @throws java.sql.SQLException
     */
    public ResultSet getUserName(int id) throws SQLException {

        String query = "SELECT username FROM user WHERE id = " + id;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        return rs;
    }


}
