/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.lansync;

import hust.soict.bkstorage.client.peerinterface.Discoverable;

/**
 *
 * @author thinhnt
 */
public class InstanceDiscovery implements Discoverable {

    private String sslKey;

    public InstanceDiscovery(String sslKey) {
        this.sslKey = sslKey;
    }

    @Override
    public String getSSLKey() {
        return sslKey;
    }

    public void setSSLKey(String sslKey) {
        this.sslKey = sslKey;
    }
    
}
