/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import hust.soict.bkstorage.utils.FileUtil;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Kết nối thử tới server
 * Đọc và ghi giá trị của tên server và số cổng xuống file
 *
 * @author toant_000
 */
public class ConfigDal extends Dal{

    /**
     *
     * @param serverName
     * @param port
     */
    public ConfigDal(String serverName, int port){
        super(serverName, port);
    }

    /**
     * Ghi tên server và số cổng xuống file
     * @throws IOException 
     */
    public void write() throws IOException {
        BufferedWriter io = new BufferedWriter(new FileWriter(FileUtil.makeConfigFile()));
        io.write("<servername>");
        io.write(serverName);
        io.write("</servername>");
        io.write("<port>");
        io.write(String.valueOf(port));
        io.write("</port>");
        io.flush();
        io.close();
    }


}
