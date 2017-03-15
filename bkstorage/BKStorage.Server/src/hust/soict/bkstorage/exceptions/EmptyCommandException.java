/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.exceptions;

/**
 * Khi người dùng nhập lệnh gồn toàn khoảng trắng
 * @author toant_000
 */
public class EmptyCommandException extends Exception{
    
    public EmptyCommandException(){
        super();
    }
    
}
