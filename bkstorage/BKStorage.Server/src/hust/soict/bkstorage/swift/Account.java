/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift;

/**
 *
 * @author thinhnt
 */
public interface Account {

    /**
     * Tạo mới container
     * @param container tên container
     */
    public void putContainer(String container);
    
    /**
     * Lấy ra thông tin container
     * @param container
     * @return 
     */
    public Container getContainer(String container);
    
    /**
     * Xóa container
     * @param container 
     */
    public void deleteContainer(String container);
}
