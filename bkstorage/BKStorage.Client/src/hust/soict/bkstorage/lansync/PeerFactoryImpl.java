/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.lansync;

import hust.soict.bkstorage.client.peerinterface.Discoverable;
import hust.soict.bkstorage.client.peerinterface.PeerFactory;
import hust.soict.bkstorage.client.peerinterface.PeerNode;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author thinhnt
 */
public class PeerFactoryImpl implements PeerFactory {

    private Node node;
    private InstanceDiscovery instanceDiscovery;

    public PeerFactoryImpl(Node node, InstanceDiscovery discovery) {
        this.node = node;
        this.instanceDiscovery = discovery;
    }
//    public PeerFactoryImpl(String ip, NetworkInterface netInterface) {
//        this.ip = ip;
//        this.netInterface = netInterface;
//        node = new Node(SecurityCommon.md5Hash(ip));
//        instanceDiscovery = new InstanceDiscovery(sslKey);
//    }
    
    public void exportDiscovery(int port) throws RemoteException {
        UnicastRemoteObject.exportObject(instanceDiscovery, port);
    }
    
    public void exportNode(int port) throws RemoteException {
        UnicastRemoteObject.exportObject(node, port);
    }
    
    public void unexportObjects() throws NoSuchObjectException {
        UnicastRemoteObject.unexportObject(node, false);
        UnicastRemoteObject.unexportObject(instanceDiscovery, false);
    }
    
    @Override
    public Discoverable getInstaceDiscovery() throws RemoteException{
        return instanceDiscovery;
    }

    @Override
    public PeerNode getNode() throws RemoteException{
        return node;
    }

}
