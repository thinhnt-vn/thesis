/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.gui;

import hust.soict.bkstorage.bll.ConfigBll;
import hust.soict.bkstorage.bll.LoginBll;
import hust.soict.bkstorage.bll.MainBll;
import hust.soict.bkstorage.bll.WizardBll;
import hust.soict.bkstorage.exception.DownloadExeption;
import hust.soict.bkstorage.exception.FileEmptyException;
import hust.soict.bkstorage.exception.LoginFailException;
import hust.soict.bkstorage.exception.TextFieldEmptyException;
import hust.soict.bkstorage.utils.FileUtil;
import java.io.IOException;
import java.rmi.NotBoundException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author toant_000
 */
public class Wizard {

    public Wizard() {
    }

    public void init() {

        // Thử kết nối tới server
        WizardBll wizardBll = new WizardBll();
        try {
            String serverName = wizardBll.readServerName();
            String port = wizardBll.readPort();
            new ConfigBll(serverName, port).connect();
        } catch (IOException | FileEmptyException | TextFieldEmptyException | NotBoundException ex) {
            new ConfigGui().setVisible(true);
            return;
        }

        // Nếu kết nối thành công -> thử đăng nhập
        try {
            String userName = wizardBll.readUserName();
            String password = wizardBll.readPassword();
            new LoginBll(userName, password).login();
        } catch (IOException | FileEmptyException | LoginFailException ex) {
            new LoginGui().setVisible(true);
            return;
        }

        // Tạo thư mục của người dùng và tải dữ liệu nếu chưa tồn tại
        if (FileUtil.makeUserDirectory()) {
            try {
                new MainBll().downloadAll();
            } catch (DownloadExeption ex) {
                // Xóa thư mục

                JOptionPane.showMessageDialog(null, ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                FileUtil.delete(FileUtil.getUserDirectory());
                new LoginGui().setVisible(true);
                return;
            }
        }
//        try {
//            new MainBll().downloadAll();
//        } catch (DownloadExeption ex) {
//            JOptionPane.showMessageDialog(null, "Không tải được dữ liệu!",
//                    "Lỗi", JOptionPane.ERROR_MESSAGE);
//            FileUtil.delete(FileUtil.getUserDirectory());
//            new LoginGui().setVisible(true);
//            return;
//        }

        // Đăng nhập thành công -> hiện form chính
        new MainGui().setVisible(true);

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception weTried) {
        }
        new Wizard().init();
    }

}
