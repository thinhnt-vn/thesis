/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift.internal;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author thinhnt
 */
public class JSON {

    public static String createPasswordUnscopedAuthBody(String domainName,
            String userName, String password) {
        JSONObject body = new JSONObject();
        JSONObject auth = new JSONObject();
        JSONObject identity = new JSONObject();
        JSONArray methods = new JSONArray();
        methods.add("password");
        identity.put("methods", methods);
        JSONObject passwordObj = new JSONObject();
        JSONObject user = new JSONObject();
        user.put("name", userName);
        JSONObject domain = new JSONObject();
        domain.put("name", domainName);
        user.put("domain", domain);
        user.put("password", password);
        passwordObj.put("user", user);
        identity.put("password", passwordObj);
        auth.put("identity", identity);
        body.put("auth", auth);
        return body.toString();
    }

    public static String createTokenProjectScopedAuthBody(String projectID,
            String unscopedToken) {
        JSONObject body = new JSONObject();
        JSONObject auth = new JSONObject();
        JSONObject identity = new JSONObject();
        JSONArray methods = new JSONArray();
        methods.add("token");
        identity.put("methods", methods);
        JSONObject token = new JSONObject();
        token.put("id", unscopedToken);
        identity.put("token", token);
        auth.put("identity", identity);
        JSONObject scope = new JSONObject();
        JSONObject project = new JSONObject();
        project.put("id", projectID);
        scope.put("project", project);
        auth.put("scope", scope);
        body.put("auth", auth);
        return body.toString();
    }

}
