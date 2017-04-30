/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.remoteentity;

import java.io.Serializable;

/**
 *
 * @author toant_000
 * @param <T>
 */
public class Package <T> implements Serializable{
    private final int id;        // id của người đăng nhập. trả lại -1 nếu đăng nhập thất bại
    private final T data;           // Nội dung

    public Package(int id, T data) {
        this.id = id;
        this.data = data;
    }

    public int getID() {
        return id;
    }

    public T getData() {
        return data;
    }
}
