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
public class FileMetaData {
    private String filePatch;

    public FileMetaData() {
    }

    public FileMetaData(String filePath) {
        this.filePatch = filePath;
    }

    public String getFilePatch() {
        return filePatch;
    }

    public void setFilePatch(String filePatch) {
        this.filePatch = filePatch;
    }
}
