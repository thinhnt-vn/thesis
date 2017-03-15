/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.exceptions;

/**
 * Lỗi cú pháp của lệnh
 * @author toant_000
 */
public class ErrorCommandException extends Exception{
    
    public ErrorCommandException(String message){
        super(message);
    }
    
}
