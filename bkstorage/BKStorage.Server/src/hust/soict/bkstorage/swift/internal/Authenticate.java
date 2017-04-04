/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift.internal;

import org.apache.http.auth.AuthenticationException;

/**
 *
 * @author thinhnt
 */
public interface Authenticate {

    public String getToken();

    public String getStorageURL();

    /**
     * Xác thực bằng tên đăng nhập, mật khẩu
     *
     * @param certificate1
     * @param certificate2
     */
    public void auth(String certificate1, String certificate2)
            throws AuthenticationException;

    /**
     * Tạo lại token khi hết hiệu lực
     */
    public void retry();

}
