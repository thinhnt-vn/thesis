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
public interface Container {
    
    /**
     * Lấy ra đối tượng tương ứng với tên
     * @param object
     * @return 
     */
    public StorageObject getObject(String object);

    /**
     * Tạo mới hoặc cập nhật đối tượng
     * @param object 
     */
    public void putObject(StorageObject object);
    
    /**
     * Xóa đối tượng
     * @param object 
     */
    public void deleteObject(String object);
    
    /**
     * 
     * @return Dung lượng (bytes) đã sử dụng
     */
    public long getBytesUsed();
}
