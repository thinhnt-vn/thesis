/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.remotecontroller;

import hust.soict.bkstorage.remoteentity.Package;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author toant_000
 */
public interface Login extends Remote {

    public Package<String> login(String userName, String password) throws RemoteException;

    public long getTotalSize(int uid) throws RemoteException;

}
