/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.client.peerinterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 *
 * @author thinhnt
 */
public interface PeerNode extends Remote {

    PeerNode join(String joinID) throws RemoteException;
    
    void setPredecessor(PeerNode node) throws RemoteException;
    
    PeerNode getPredecessor() throws RemoteException;

    void setSuccessor(PeerNode node) throws RemoteException;
    
    PeerNode getSuccessor() throws RemoteException;
    
    String getID() throws RemoteException;
    
    void election(Map<String, ElectionData> fileLeaders) throws RemoteException;
    
    byte [] download(String path) throws RemoteException;
    
}
