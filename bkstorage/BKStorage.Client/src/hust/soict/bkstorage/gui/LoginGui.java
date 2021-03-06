/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.gui;

import hust.soict.bkstorage.bll.LoginBll;
import hust.soict.bkstorage.bll.MainBll;
import hust.soict.bkstorage.constants.FileConstant;
import hust.soict.bkstorage.exception.DownloadExeption;
import hust.soict.bkstorage.exception.LoginFailException;
import hust.soict.bkstorage.utils.FileUtil;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author toant_000
 */
public class LoginGui extends javax.swing.JFrame {

    
    private DownloadProgressGui progressGui;
    /**
     * Creates new form LoginGui
     */
    public LoginGui() {
        initComponents();
        setIconImage(new ImageIcon(FileConstant.ICON_DIR + "\\"
                + FileConstant.LOGIN_ICON_FILE_NAME).getImage());
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        passwordTextField = new javax.swing.JPasswordField();
        userNameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        userNameTextField = new javax.swing.JTextField();
        cancelButton = new javax.swing.JButton();
        loginButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Đăng nhập");

        passwordTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordTextFieldActionPerformed(evt);
            }
        });

        userNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userNameLabel.setText("Tên đăng nhập:");

        passwordLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        passwordLabel.setText("Mật khẩu:");

        cancelButton.setText("Hủy bỏ");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        loginButton.setText("Đăng nhập");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(loginButton)
                        .addGap(10, 10, 10)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(userNameLabel)
                            .addComponent(passwordLabel))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(userNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, loginButton});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {passwordTextField, userNameTextField});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userNameLabel)
                    .addComponent(userNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordTextField))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(loginButton))
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void passwordTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordTextFieldActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed

        String userName = userNameTextField.getText().trim();
        char[] passwordChars = passwordTextField.getPassword();
        String password = String.valueOf(passwordChars).trim();

        if (userName.isEmpty() || password.isEmpty()) {
            return;
        }

        if (userName.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Tên tài khoản không được chứa "
                    + "khoảng trắng", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        LoginBll loginBll = new LoginBll(userName, password);
        try {       // Đăng nhập
            loginBll.login();
        } catch (RemoteException ex) {      // Lỗi kết nối
            JOptionPane.showMessageDialog(this, "Kết nối gián đoạn", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            new ConfigGui().setVisible(true);
            return;
        } catch (LoginFailException ex) {       // ĐĂng nhập thất bại
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Đăng nhập thành công -> tài khoản và mật khẩu xuống file
        try {
            loginBll.save();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Không lưu được tên tài khoản và "
                    + "mật khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo thư mục của người dùng và tải dữ liệu nếu chưa tồn tại
        if (FileUtil.makeUserDirectory()) {
            Thread progressThread = null;
            progressGui = null;
            long totalSize;
            try {
                totalSize = loginBll.getTotalSize();
            } catch (RemoteException ex) {
                new ConfigGui().setVisible(true);
                return;
            }
            dispose();
            progressGui = new DownloadProgressGui(totalSize);
            progressThread = new Thread(progressGui);
            progressThread.start();
            
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        new MainBll().downloadAll();
                        if (progressGui.isActive()){
                            progressGui.dispose();
                        }
                        new MainGui().setVisible(true);
                    } catch (DownloadExeption ex) {
                        JOptionPane.showMessageDialog(null, "Không tải được dữ liệu!",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        FileUtil.delete(FileUtil.getUserDirectory());
                        new LoginGui().setVisible(true);
                        return;
                    }
                }
            }).start();
            return;
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
        new MainGui().setVisible(true);
        dispose();
    }//GEN-LAST:event_loginButtonActionPerformed
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(LoginGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(LoginGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(LoginGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(LoginGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new LoginGui().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton loginButton;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPasswordField passwordTextField;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JTextField userNameTextField;
    // End of variables declaration//GEN-END:variables
}
