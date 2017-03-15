/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import hust.soict.bkstorage.utils.FileUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Đọc thông số từ file cấu hình
 *
 * @author toant_000
 */
public class WizardDal {

    public WizardDal() {
    }

    /**
     * Lấy tên server
     *
     * @return
     * @throws IOException
     */
    public String readServerName() throws IOException  {
        
        RandomAccessFile io = new RandomAccessFile(FileUtil.makeConfigFile(), "r");
        String text = io.readLine();
        io.close();
        if (text == null) {
            return null;
        }
        return text.substring(text.indexOf("<servername>") + 12,
                text.indexOf("</servername>"));
        
    }

    /**
     * Lấy số cổng
     *
     * @return
     * @throws IOException
     */
    public String readPort() throws IOException {
        
        RandomAccessFile io = new RandomAccessFile(FileUtil.makeConfigFile(), "r");
        String text = io.readLine();
        io.close();
        if (text == null) {
            return null;
        }
        return text.substring(text.indexOf("<port>") + 6,
                text.indexOf("</port>"));
        
    }

    /**
     * Lấy tên người dùng đã lưu
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String readUserName() throws IOException {
        
        RandomAccessFile io = new RandomAccessFile(FileUtil.makeLoginFile(), "r");
        String text = io.readLine();
        io.close();
        if (text == null) {
            return null;
        }
        return text.substring(text.indexOf("<username>") + 10,
                text.indexOf("</username>"));
        
    }

    /**
     * Lấy mật khẩu đã lưu
     * @return 
     */
    public String readPassword() throws IOException {
        
        RandomAccessFile io = new RandomAccessFile(FileUtil.makeLoginFile(), "r");
        String text = io.readLine();
        io.close();
        if (text == null) {
            return null;
        }
        return text.substring(text.indexOf("<password>") + 10,
                text.indexOf("</password>"));

    }

}
