/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import java.io.IOException;

/**
 * Kết nối thử tới server Đọc và ghi giá trị của tên server và số cổng xuống
 * file
 *
 * @author toant_000
 */
public class ConfigDal extends Dal {

    /**
     *
     * @param serverName
     * @param port
     */
    public ConfigDal(String serverName, int port) {
        super(serverName, port);
    }

    /**
     * Ghi tên server và số cổng xuống file
     */
    public void write() throws IOException {
        configProperties.setProperty(SERVER_IP_KEY, serverName);
        configProperties.setProperty(SERVER_PORT_KEY, String.valueOf(port));
    }
}
