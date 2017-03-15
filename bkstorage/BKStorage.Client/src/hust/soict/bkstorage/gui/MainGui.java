/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.gui;

import hust.soict.bkstorage.bll.MainBll;
import hust.soict.bkstorage.constants.FileConstant;
import hust.soict.bkstorage.entity.AutoSyncThread;
import hust.soict.bkstorage.entity.FileBrowser;
import hust.soict.bkstorage.utils.FileUtil;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import static java.awt.Frame.NORMAL;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author toant_000
 */
public class MainGui extends javax.swing.JFrame {

    private AutoSyncThread syncThread;
    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private boolean autoSync;

    /**
     * Creates new form MainForm
     */
    public MainGui() {
        initComponents();
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(FileConstant.ICON_DIR + "\\"
                + FileConstant.TRAY_ICON_FILE_NAME).getImage());
        FileBrowser fileBrowser = new FileBrowser(FileUtil.getUserDirectory());
        setContentPane(fileBrowser);
        fileBrowser.showRootFile();

        autoSync = FileUtil.isAutoSyncFile();
        addWindowStateListener(new WindowStateListener() {

            @Override
            public void windowStateChanged(WindowEvent e) {

                if (e.getNewState() == ICONIFIED) {
                    setVisible(false);
                }

            }
        });

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }

        });

        if (SystemTray.isSupported()) {
            addToSystemTray();
        }

        if (autoSync) {
            syncThread = new AutoSyncThread(this);
            syncThread.start();
        }
    }

    /**
     * Thêm biểu tượng của chương trình xuống System Tray
     */
    private void addToSystemTray() {

        MenuItem openFileBrowserItem = new MenuItem("Open File Browser");
        CheckboxMenuItem autoSyncItem = new CheckboxMenuItem("Auto Sync");
        autoSyncItem.setState(FileUtil.isAutoSyncFile());
        MenuItem syncItem = new MenuItem("Sync");
        syncItem.setEnabled(!autoSync);
        MenuItem logoutItem = new MenuItem("Logout");
        MenuItem exitItem = new MenuItem("Exit");

        openFileBrowserItem.addActionListener(new ActionListener() {
            // Mở trình duyệt file
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(true);
                setExtendedState(NORMAL);
            }

        });
        autoSyncItem.addItemListener(new AutoSyncFileListener(syncItem));   // Chọn chế độ đồng bộ
        syncItem.addActionListener(new ActionListener() {
            // Chủ động đồng bộ
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new MainBll().sync();
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Kết nối bị gián đoạn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    dispose();
                    removeTrayIcon();
                    new ConfigGui().setVisible(true);
                }catch (IOException | ClassNotFoundException io){
                }
            }
        });
        logoutItem.addActionListener(new ActionListener() {
//                Đăng xuất
            @Override
            public void actionPerformed(ActionEvent e) {
                if (syncThread != null && syncThread.isAlive()) {
                    syncThread.kill();
                    try {
                        syncThread.join();
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                try {
                    FileUtil.deleteAllConfigFile();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Không xóa được các "
                            + "file cấu hình", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                FileUtil.delete(FileUtil.getUserDirectory());
                dispose();
                removeTrayIcon();
                new LoginGui().setVisible(true);
            }

        });

        exitItem.addActionListener(new ActionListener() {
//                Thoát khỏi chương trình
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

        PopupMenu popupMenu = new PopupMenu();
        popupMenu.add(openFileBrowserItem);
        popupMenu.addSeparator();
        popupMenu.add(autoSyncItem);
        popupMenu.add(syncItem);
        popupMenu.addSeparator();
        popupMenu.add(logoutItem);
        popupMenu.add(exitItem);

        trayIcon = new TrayIcon(new ImageIcon(FileConstant.ICON_DIR + "\\"
                + FileConstant.TRAY_ICON_FILE_NAME).getImage(), "MyStorage", popupMenu);
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int count = e.getClickCount();
                if (count == 2) {
                    setVisible(true);
                    setExtendedState(NORMAL);
                }
            }
        });

        systemTray = SystemTray.getSystemTray();
        try {
            systemTray.add(trayIcon);
        } catch (AWTException ex) {
            JOptionPane.showMessageDialog(null, "Không thể thêm vào System Tray",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private MainGui getOuterClass() {
        return this;
    }

    public void removeTrayIcon() {
        systemTray.remove(trayIcon);
    }

    class AutoSyncFileListener implements ItemListener {

        private MenuItem syncItem;

        public AutoSyncFileListener(MenuItem syncItem) {
            this.syncItem = syncItem;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                autoSync = true;
                // Bật chế độ tự động đồng bộ
                syncThread = new AutoSyncThread(getOuterClass());
                syncThread.start();
                FileUtil.makeAutoSyncFile();
                syncItem.setEnabled(false);
            } else {
                autoSync = false;
                syncThread.kill();
                try {
                    syncThread.join();
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
                FileUtil.deleteAutoSyncFile();
                syncItem.setEnabled(true);
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton8 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jToolBar1.setRollover(true);

        jToggleButton1.setText("Tự động đồng bộ");
        jToggleButton1.setToolTipText("Tự động đồng bộ");
        jToggleButton1.setFocusable(false);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jToggleButton1ItemStateChanged(evt);
            }
        });
        jToolBar1.add(jToggleButton1);

        jButton1.setText("Đồng bộ");
        jButton1.setToolTipText("Đồng bộ");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton1);
        jToolBar1.add(jSeparator1);

        jButton4.setText("Di chuyển");
        jButton4.setToolTipText("Cut");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton4);

        jButton5.setText("Sao chép");
        jButton5.setToolTipText("Copy");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton5);

        jButton2.setText("Dán");
        jButton2.setToolTipText("Paste");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton2);

        jButton3.setText("Xóa");
        jButton3.setToolTipText("Delete");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton3);

        jButton7.setText("Đổi tên");
        jButton7.setToolTipText("Rename");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton7);
        jToolBar1.add(jSeparator2);

        jButton8.setText("Đăng xuất");
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton8);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(489, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jToggleButton1ItemStateChanged

    }//GEN-LAST:event_jToggleButton1ItemStateChanged

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
//            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new MainForm().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
