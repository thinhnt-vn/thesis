/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift.internal;

/**
 *
 * @author thinhnt
 */
public class StorageException extends Exception {

    public final static int ERROR_CODE_UNABLE_CREATE_TOKEN = 1;
    private int errorCode;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
