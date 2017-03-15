/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.exceptions;

/**
 * Ngoại lệ người dùng nhập sai mật khẩu
 * @author toant_000
 */
public class IncorrectPasswordException extends Exception{
    
    public IncorrectPasswordException(String message){
        super(message);
    }
    
}
