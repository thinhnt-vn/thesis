/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift;

import hust.soict.bkstorage.swift.internal.StorageObjectImpl;
import java.util.Map;

/**
 *
 * @author thinhnt
 */
public class ObjectBuilder {

    private String objectName;
    private byte[] content;
    private Map<String, String> metadata;

    public ObjectBuilder() {
    }

    public static ObjectBuilder newBuilder() {
        return new ObjectBuilder();
    }

    public ObjectBuilder name(String name) {
        this.objectName = name;
        return this;
    }

    public ObjectBuilder content(byte[] content) {
        this.content = content;
        return this;
    }

    public ObjectBuilder metadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    public StorageObject build() {
        return new StorageObjectImpl(objectName, content, metadata);
    }

}
