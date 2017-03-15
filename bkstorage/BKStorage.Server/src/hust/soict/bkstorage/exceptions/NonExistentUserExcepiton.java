/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.exceptions;

/**
 * Ngoại lệ không tồn tại người dùng trong hệ thống
 * @author toant_000
 */
public class NonExistentUserExcepiton extends Exception{
    
    public NonExistentUserExcepiton(String message){
        super(message);
    }
    
}
