/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.entity;

import hust.soict.bkstorage.bll.MainBll;
import hust.soict.bkstorage.exception.SnapshotMappingException;
import hust.soict.bkstorage.gui.ConfigGui;
import hust.soict.bkstorage.gui.MainGui;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author thinhnt
 */
public class AutoSyncThread extends Thread {

    private boolean finished;
    private MainGui mainGui;
    private static final int SYNC_CYCLE = 1000;

    public AutoSyncThread(MainGui mainGui) {
        this.mainGui = mainGui;
    }
    
    @Override
    public void run() {
        MainBll mainBll = new MainBll();
        while (!finished) {
            try {
                mainBll.sync();
                Thread.sleep(SYNC_CYCLE);
            } catch (RemoteException  ex) {
                JOptionPane.showMessageDialog(null, "Kết nối bị gián đoạn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                mainGui.dispose();
                mainGui.removeTrayIcon();
                new ConfigGui().setVisible(true);
                kill();
            } catch (InterruptedException | IOException | ClassNotFoundException e){
            } catch (SnapshotMappingException ex) {
                Logger.getLogger(AutoSyncThread.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        
    }

    public void kill() {
        finished = true;
    }

}
