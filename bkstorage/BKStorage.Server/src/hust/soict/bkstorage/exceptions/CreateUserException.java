/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.exceptions;

/**
 * Ngoại lệ xảy ra tạo người dùng mà chưa kết nối đến SQL Server
 * @author toant_000
 */
public class CreateUserException extends Exception{
    
    public CreateUserException(String message){
        super(message);
    }
    
}