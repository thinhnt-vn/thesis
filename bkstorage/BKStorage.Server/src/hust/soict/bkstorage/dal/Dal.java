/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author toant_000
 */
public class Dal {

    protected static Connection connection;

    public Dal() {

    }

    /**
     *
     * @param serverName
     * @param port
     * @param userName
     * @param password
     * @param dBName
     * @throws ClassNotFoundException - Không tìm thấy driver
     * @throws SQLException - Không kết nối được đến SQL server
     */
    public void connect2SQLServer(String serverName, int port, String userName,
            String password, String dBName) throws ClassNotFoundException, SQLException {
        if (connection == null) {
            String driverClass = "com.mysql.jdbc.Driver";
            Class.forName(driverClass);
            String url = "jdbc:mysql://" + serverName + ":" + port + "/" + dBName;
            try {
                connection = DriverManager.getConnection(url, userName, password);
            } catch (SQLException ex) {
                throw new SQLException("Lỗi! Không kết nối được tới SQL Server");
            }
        }
    }

    /**
     * Lấy id của người dùng ứng với tên đăng nhập tương ứng
     *
     * @param userName
     * @return
     * @throws java.sql.SQLException
     */
    public int getID(String userName) throws SQLException {

        String query = "SELECT id FROM user WHERE username = \'" + userName + "\'";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        int result = -1;
        while (rs.next()) {            
            result = rs.getInt(1);
        }
        return result;

    }

    public static Connection getConnection() {
        return connection;
    }

}
