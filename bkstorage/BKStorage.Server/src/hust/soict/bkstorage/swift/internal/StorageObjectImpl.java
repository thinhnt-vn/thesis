/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift.internal;

import hust.soict.bkstorage.swift.StorageObject;
import java.util.Map;

/**
 *
 * @author thinhnt
 */
public class StorageObjectImpl implements StorageObject {

    private String name;
    private byte[] content;
    private Map<String, String> metadata;

    public StorageObjectImpl(String name, byte[] content,
            Map<String, String> metadata) {
        this.name = name;
        this.content = content;
        this.metadata = metadata;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

}
