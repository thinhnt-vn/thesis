/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author thinhnt
 */
public class ConnectionManager {

    public static class DerbyConnection {
        private static String DRIVER_URL = "org.apache.derby.jdbc.EmbeddedDriver";
        private static String CONNECTION_URL = "jdbc:derby:db;create=true";
        private static Connection connection;

        /**
         * Tạo csdl của client
         * @throws java.lang.ClassNotFoundException
         * @throws java.sql.SQLException
         */
        public static void createDerbyDB() throws ClassNotFoundException,
                SQLException {
            Class.forName(DRIVER_URL);
            connection = DriverManager.getConnection(CONNECTION_URL);
        }

        /**
         * Tạo kết nối đến Derby Engine
         *
         * @return
         * @throws ClassNotFoundException
         * @throws SQLException
         */
        public static Connection createConnection() throws ClassNotFoundException,
                SQLException {
            if (connection == null) {
                Class.forName(DRIVER_URL);
                connection = DriverManager.getConnection(CONNECTION_URL);
            }
            return connection;
        }
    }

}
