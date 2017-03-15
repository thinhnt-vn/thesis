/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.bll;

import hust.soict.bkstorage.dal.FileBrowserDal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author thinhnt
 */
public class FileBrowserBll {

    public FileBrowserBll() {

    }

    /**
     * Lấy tên người dùng theo id
     *
     * @param id
     * @return
     * @throws java.sql.SQLException
     */
    public String getUserNameByID(int id) throws SQLException {

        String result = null;
        ResultSet rs = new FileBrowserDal().getUserName(id);
        while (rs.next()) {
            result = rs.getString(1);
        }
        return result;

    }

    /**
     * Lấy id theo tên người dùng
     *
     * @param userName
     * @return
     * @throws SQLException
     */
    public int getIDByUserName(String userName) throws SQLException {

        return new FileBrowserDal().getID(userName);

    }

    public boolean isConnected2DB() {
        FileBrowserDal browserDal = new FileBrowserDal();
        return FileBrowserDal.getConnection() != null;
    }

}
