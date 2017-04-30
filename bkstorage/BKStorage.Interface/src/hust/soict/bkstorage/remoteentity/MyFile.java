/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.remoteentity;

import java.io.Serializable;

/**
 *
 * @author toant_000
 */
public class MyFile implements Serializable {

    private int id;
    private String path;
    private boolean derectory;
    private long timeModified;
    private int parentID;
    private int userID;

    private byte [] data;

    public MyFile(int id, String path, boolean derectory, long timeModified,
            int patentID, int userID) {
        this.id = id;
        this.path = path;
        this.derectory = derectory;
        this.timeModified = timeModified;
        this.parentID = patentID;
        this.userID = userID;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public boolean isDerectory() {
        return derectory;
    }

    public long getTimeModified() {
        return timeModified;
    }

    public int getParentID() {
        return parentID;
    }

    public int getUserID() {
        return userID;
    }

   
    public byte [] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

  


}
