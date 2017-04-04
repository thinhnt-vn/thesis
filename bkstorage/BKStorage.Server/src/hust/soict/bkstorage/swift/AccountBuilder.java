/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift;

import hust.soict.bkstorage.swift.internal.AccountImpl;
import hust.soict.bkstorage.swift.internal.Authenticate;
import hust.soict.bkstorage.swift.internal.KeystoneAuthenticate;
import hust.soict.bkstorage.swift.internal.StorageAPI;
import hust.soict.bkstorage.swift.internal.SwiftApi;
import org.apache.http.auth.AuthenticationException;

/**
 *
 * @author thinhnt
 */
public class AccountBuilder {

    private String domainName;
    private String projectName;
    private String creticate1;
    private String creticate2;
    private String auth_url;

    public AccountBuilder() {
    }

    public static AccountBuilder newBuilder() {
        return new AccountBuilder();
    }

    public AccountBuilder domain(String domain) {
        this.domainName = domain;
        return this;
    }

    public AccountBuilder project(String project) {
        this.projectName = project;
        return this;
    }

    public AccountBuilder creticate(String cret1, String cret2) {
        this.creticate1 = cret1;
        this.creticate2 = cret2;
        return this;
    }

    public AccountBuilder authUrl(String url) {
        this.auth_url = url;
        return this;
    }

    public Account build() throws AuthenticationException {
        Authenticate auth = new KeystoneAuthenticate(auth_url);
        auth.auth(domainName + ":" + projectName + ":" + creticate1, creticate2);
        StorageAPI api = new SwiftApi(auth);
        Account acc = new AccountImpl(projectName, api);
        return acc;
    }

}
