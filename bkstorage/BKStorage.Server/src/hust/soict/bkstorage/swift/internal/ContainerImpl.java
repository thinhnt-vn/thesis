/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift.internal;

import hust.soict.bkstorage.swift.Container;
import hust.soict.bkstorage.swift.StorageObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thinhnt
 */
public class ContainerImpl implements Container {

    private StorageAPI api;
    private String containerName;

    public ContainerImpl(StorageAPI api) {
        this.api = api;
    }

    public ContainerImpl(StorageAPI api, String containerName) {
        this(api);
        this.containerName = containerName;
    }

    @Override
    public StorageObject getObject(String object) {
        try {
            return api.getObject(containerName, object);
        } catch (StorageException ex) {
            Logger.getLogger(ContainerImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void putObject(StorageObject object) {
        try {
            api.putObject(containerName, object.getName(), object.getContent(), object.getMetadata());
        } catch (StorageException ex) {
            Logger.getLogger(ContainerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void deleteObject(String object) {
        try {
            api.deleteObject(containerName, object);
        } catch (StorageException ex) {
            Logger.getLogger(ContainerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
