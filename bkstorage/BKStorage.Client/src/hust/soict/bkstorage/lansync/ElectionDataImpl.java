/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.lansync;

import hust.soict.bkstorage.client.peerinterface.ElectionData;
import hust.soict.bkstorage.client.peerinterface.PeerNode;

/**
 *
 * @author thinhnt
 */
public class ElectionDataImpl implements ElectionData{
    
    private long lastModified;
    private PeerNode leaderNode;
    private boolean isDelete;
    private boolean electionCompleted;
    private boolean isDir;

    public ElectionDataImpl(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public ElectionDataImpl(long lastModified, PeerNode leaderNode) {
        this.lastModified = lastModified;
        this.leaderNode = leaderNode;
    }
    
    @Override
    public long getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public PeerNode getLeaderNode() {
        return leaderNode;
    }

    @Override
    public void setLeaderNode(PeerNode node) {
        this.leaderNode = node;
    }
    

    @Override
    public boolean isDelete() {
        return isDelete;
    }

    @Override
    public void setDelete(boolean delete) {
        this.isDelete = delete;
    }

    @Override
    public void setElectionCompleted(boolean completed) {
        this.electionCompleted = completed;
    }

    @Override
    public boolean isElectionCompleted() {
        return electionCompleted;
    }

    @Override
    public void setDirectory(boolean isDirectory) {
        this.isDir = isDirectory;
    }

    @Override
    public boolean isDirectory() {
        return isDir;
    }
    
}
