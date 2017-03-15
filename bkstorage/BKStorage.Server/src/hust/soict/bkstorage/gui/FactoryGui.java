/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.gui;

import hust.soict.bkstorage.factory.RemoteFactory;
import hust.soict.bkstorage.remotecontroller.Login;
import hust.soict.bkstorage.remotecontroller.Main;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author toant_000
 */
public class FactoryGui implements RemoteFactory{

    private LoginGui loginGui;
    private MainGui mainGui;
    
    public FactoryGui(){
        loginGui = new LoginGui();
        mainGui = new MainGui();
    }
    
    public void exportEntity(int port) throws RemoteException{
        UnicastRemoteObject.exportObject(loginGui, port);
        UnicastRemoteObject.exportObject(mainGui, port);
    }
    
    @Override
    public boolean getConnectState() throws RemoteException {
        return true;
    }

    @Override
    public Login createLogin() throws RemoteException {
        return loginGui;
    }

    @Override
    public Main createMain() throws RemoteException {
        return mainGui;
    }
    
}
