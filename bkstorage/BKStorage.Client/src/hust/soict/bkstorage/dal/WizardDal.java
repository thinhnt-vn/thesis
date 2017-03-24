/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import java.io.IOException;

/**
 * Đọc thông số từ file cấu hình
 *
 * @author toant_000
 */
public class WizardDal extends Dal {

    public WizardDal() {
    }

    /**
     * Lấy tên server
     *
     * @return
     */
    public String readServerName() {
        return configProperties.getProperty(SERVER_IP_KEY);
    }

    /**
     * Lấy số cổng
     *
     * @return
     * @throws IOException
     */
    public String readPort() throws IOException {
        return configProperties.getProperty(SERVER_PORT_KEY);
    }

    /**
     * Lấy tên người dùng đã lưu
     *
     * @return
     */
    public String readUserName() {
        String rs = options.getUserName();
        if (rs == null || "null".equals(rs)) {
            return null;
        }

        return rs;
    }

    /**
     * Lấy mật khẩu đã lưu
     *
     * @return
     */
    public String readPassword() throws IOException {
        String rs = options.getPassword();
        if (rs == null || "null".equals(rs)) {
            return null;
        }

        return rs;
    }

}
