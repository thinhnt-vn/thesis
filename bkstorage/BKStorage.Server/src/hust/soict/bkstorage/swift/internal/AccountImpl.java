/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift.internal;

import hust.soict.bkstorage.swift.Account;
import hust.soict.bkstorage.swift.Container;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thinhnt
 */
public class AccountImpl implements Account {

    private String accountName;
    private StorageAPI api;

    public AccountImpl(String accountName, StorageAPI swiftAPI) {
        this.accountName = accountName;
        this.api = swiftAPI;
    }

    @Override
    public void putContainer(String container) {
        try {
            api.putContainer(container);
        } catch (StorageException ex) {
            Logger.getLogger(AccountImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Container getContainer(String container) {
        try {
            return api.getContainer(container);
        } catch (StorageException ex) {
            Logger.getLogger(AccountImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void deleteContainer(String container) {
        try {
            api.deleteContainer(container);
        } catch (StorageException ex) {
            Logger.getLogger(AccountImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
