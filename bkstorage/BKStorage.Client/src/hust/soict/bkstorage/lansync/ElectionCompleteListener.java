/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.lansync;

/**
 *
 * @author thinhnt
 */
public interface ElectionCompleteListener{
    
    void onElectionCompleted(String broadcastAddress);
    
}
