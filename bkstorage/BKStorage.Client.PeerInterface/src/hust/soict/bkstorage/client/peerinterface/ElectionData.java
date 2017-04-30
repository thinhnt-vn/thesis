/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.client.peerinterface;

import java.io.Serializable;

/**
 *
 * @author thinhnt
 */
public interface ElectionData extends Serializable {

    long getLastModified();

    void setLastModified(long lastModified);

    PeerNode getLeaderNode();

    void setLeaderNode(PeerNode node);
    
    void setElectionCompleted(boolean completed);
    
    boolean isElectionCompleted();
    
    void setDelete(boolean delete);
    
    boolean isDelete();
    
    void setDirectory(boolean isDirectory);
    
    boolean isDirectory();
}
