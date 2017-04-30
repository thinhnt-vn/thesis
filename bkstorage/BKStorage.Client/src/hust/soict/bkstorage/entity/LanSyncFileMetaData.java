/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.entity;

/**
 *
 * @author thinhnt
 */
public class LanSyncFileMetaData extends FileMetaData{
    
    private boolean deleted;

    public LanSyncFileMetaData(String path, boolean deleted) {
        super(path);
        this.deleted = deleted;
    }
    
    public boolean isDeleted(){
        return deleted;
    }
    
    public void setDelete(boolean deleted){
        this.deleted = deleted;
    }
    
}
