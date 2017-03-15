/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.gui;

import hust.soict.bkstorage.bll.MainBll;
import hust.soict.bkstorage.remotecontroller.Main;
import hust.soict.bkstorage.remoteentity.MyFile;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author toant_000
 */
public class MainGui implements Main {

    /**
     * Lấy tất cả file con của thư muc pid
     *
     * @param uid
     * @param pid
     * @return
     * @throws RemoteException
     */
    @Override
    public ArrayList<MyFile> getAllFileByParent(int uid, int pid) throws RemoteException {

        MainBll mainBll = new MainBll();
        ArrayList<MyFile> list = null;
        try {
            list = mainBll.getAllFileByParent(uid, pid);
        } catch (SQLException ex) {
        }
        return list;
    }

    /**
     * Lấy file dựa vào path
     *
     * @param path
     * @param uid
     * @return
     */
    @Override
    public MyFile getFileByPath(String path, int uid) {

        MainBll mainBll = new MainBll();
        MyFile result = null;
        try {
            result = mainBll.getFileByPath(path, uid);
        } catch (SQLException ex) {
        }

        return result;

    }

    /**
     * Lưu file mà client gửi lên
     * @param f 
     */
    @Override
    public void put(MyFile f) {
        
        MainBll mainBll = new MainBll();
        try {
            mainBll.storageFile(f);
        } catch (IOException | SQLException ex) {
            System.out.println(ex.toString());
        }
        
    }

    /**
     * Lấy dữ liệu cho file tương ứng
     *
     * @param f
     * @return
     * @throws RemoteException
     */
    @Override
    public MyFile getData(MyFile f) throws RemoteException {

        MainBll mainBll = new MainBll();
        try {
            mainBll.makeData(f);
        } catch (IOException ex) {
            return null;
        }

        return f;
    }

    /**
     * Xóa 1 file trên server
     *
     * @param f
     */
    public void delete(MyFile f) {

        try {
            MainBll mainBll = new MainBll();
            mainBll.delete(f);
        } catch (SQLException | IOException ex) {
        }

    }

}
