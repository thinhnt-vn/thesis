/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import hust.soict.bkstorage.factory.RemoteFactory;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author toant_000
 */
public class Dal {

    protected String serverName;
    protected int port;
    protected static RemoteFactory factory;

    public Dal() {
    }

    public Dal(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public boolean connect() throws RemoteException, NotBoundException {
        Registry reg = LocateRegistry.getRegistry(serverName, port);
        factory = (RemoteFactory) reg.lookup("factory");
        return factory.getConnectState();
    }

}
