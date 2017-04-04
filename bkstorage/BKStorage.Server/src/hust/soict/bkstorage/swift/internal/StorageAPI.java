/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift.internal;

import hust.soict.bkstorage.swift.Container;
import hust.soict.bkstorage.swift.StorageObject;
import java.util.Map;

/**
 *
 * @author thinhnt
 */
public interface StorageAPI {

    public void putContainer(String containerName) throws StorageException;

    public Container getContainer(String containerName) throws StorageException;

    public void deleteContainer(String containerName) throws StorageException;

    public StorageObject getObject(String container, String objectName)
            throws StorageException;

    public void putObject(String container, String objectNane, byte[] content,
            Map<String, String> metadata) throws StorageException;

    public void putObjectMetadata(String container, String objectNane,
            Map<String, String> metadata) throws StorageException;

    public void deleteObject(String container, String objectNane)
            throws StorageException;

}
