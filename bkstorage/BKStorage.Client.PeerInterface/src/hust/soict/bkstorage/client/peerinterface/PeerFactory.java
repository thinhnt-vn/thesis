/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.client.peerinterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author thinhnt
 */
public interface PeerFactory extends Remote {

    Discoverable getInstaceDiscovery() throws RemoteException;

    PeerNode getNode() throws RemoteException;
    
}
