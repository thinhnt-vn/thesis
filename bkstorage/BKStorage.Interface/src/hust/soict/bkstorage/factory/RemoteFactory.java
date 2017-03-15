/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.factory;

import hust.soict.bkstorage.remotecontroller.Login;
import hust.soict.bkstorage.remotecontroller.Main;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author toant_000
 */
public interface RemoteFactory extends Remote{
    
    public boolean getConnectState() throws RemoteException;
    
    public Login createLogin() throws RemoteException;
    
    public Main createMain() throws RemoteException;
    
}
