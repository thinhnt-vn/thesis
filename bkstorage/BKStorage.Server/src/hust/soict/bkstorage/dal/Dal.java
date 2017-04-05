/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import hust.soict.bkstorage.constants.Options;
import hust.soict.bkstorage.swift.Account;
import hust.soict.bkstorage.swift.AccountBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.auth.AuthenticationException;

/**
 *
 * @author toant_000
 */
public class Dal {

    protected static Connection connection;
    private static Account ossAccount;

    public Dal() {
        getAccount();
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

    protected static Account getAccount() {
        if (ossAccount == null) {
            try {
                ossAccount = AccountBuilder.newBuilder()
                        .authUrl(Options.SWIFT_AUTH_URL_VALUE)
                        .domain(Options.SWIFT_DOMAIN_NAME_VALUE)
                        .project(Options.SWIFT_PROJECT_NAME_VALUE)
                        .creticate(Options.SWIFT_USERNAME_VALUE,
                                Options.SWIFT_PASSWORD_VALUE).build();
            } catch (AuthenticationException ex) {
                Logger.getLogger(Dal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ossAccount;
    }

}
