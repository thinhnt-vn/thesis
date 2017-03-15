/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.remotecontroller;

import hust.soict.bkstorage.remoteentity.MyFile;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author toant_000
 */
public interface Main extends Remote{
    
    public ArrayList<MyFile> getAllFileByParent(int uid, int pid) throws RemoteException;
    
    public MyFile getFileByPath(String path, int uid) throws RemoteException;
    
    public MyFile getData(MyFile f) throws RemoteException;
        
    public void put(MyFile f) throws RemoteException;
    
    public void delete(MyFile f) throws RemoteException;
    
}
