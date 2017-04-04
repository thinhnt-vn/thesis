/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift;

import java.util.Map;

/**
 *
 * @author thinhnt
 */
public interface StorageObject {

    public String getName();

    public byte[] getContent();

    public Map<String, String> getMetadata();

}
