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
 * @author toant_000
 */
public class CommandLineDal extends Dal {

    public CommandLineDal() {
        super();
    }

    /**
     * Trả lại true nếu username đã tồn tại trong DB
     *
     * @param userName
     * @return
     * @throws java.sql.SQLException
     */
    public boolean isExist(String userName) throws SQLException {
        String query = "SELECT 1 FROM user WHERE username = \'" + userName + "\'";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        return rs.first();
    }

    /**
     * Thêm một người dùng vào CSDL
     *
     * @param name
     * @param userName
     * @param password
     * @param capacity
     * @throws SQLException
     */
    public void insertUser(String name, String userName, String password, int capacity)
            throws SQLException {
        String query = "INSERT INTO user(name, username, password, capacity) "
                + "VALUES(\'" + name + "\',\'" + userName + "\',\'" + password + "\'," + capacity + ")";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    /**
     * Xóa người dùng với id tương ứng
     *
     * @param id
     * @throws java.sql.SQLException
     */
    public void removeUser(int id) throws SQLException {
        String query = "DELETE FROM user WHERE id = " + id;
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);

        query = "DELETE FROM file WHERE userid = " + id;
        statement.executeUpdate(query);
    }

    public void createContainerForUser(int id) {
        getAccount().putContainer("" + id);
    }

}
