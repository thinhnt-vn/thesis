/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift.internal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.sasl.AuthenticationException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author thinhnt
 */
public class KeystoneAuthenticate implements Authenticate {

    private final static String TOKEN_PATH = "/auth/tokens";
    private final static String PROJECT_PATH = "/auth/projects";
    private String auth_url;
    private String storageURL;
    private String domain;
    private String project;
    private String userName;
    private String password;
    private String token;

    public KeystoneAuthenticate(String endpoint) {
        this.auth_url = endpoint;
    }

    @Override
    public void auth(String certificate1, String certificate2) {
        if (domain == null || userName == null || project == null
                || password == null) {
            String[] pro_use = certificate1.split(":");
            domain = pro_use[0];
            project = pro_use[1];
            userName = pro_use[2];
            password = certificate2;
        }

        try {
            String unscopedToken = createUnscopedToken();
            String projectID = getProjectIDByName(project, unscopedToken);
            if (projectID == null) {
                throw new AuthenticationException("No such element error");
            }
            token = createProjectToken(projectID, unscopedToken);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(KeystoneAuthenticate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String createUnscopedToken() throws UnsupportedEncodingException,
            IOException {
        String endpoint = auth_url + TOKEN_PATH;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(endpoint);
            httppost.setHeader(SwiftConstants.HEADER_CONTENT_TYPE,
                    SwiftConstants.HEADER_VALUE_APP_JSON);
            httppost.setEntity(new StringEntity(JSON.createPasswordUnscopedAuthBody(
                    domain, userName, password)));
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 201) {
                    String rs = response.getFirstHeader(
                            SwiftConstants.HEADER_X_SUBJECT_TOKEN).getValue();
                    return rs;
                } else {
                    throw new AuthenticationException("Unable to create token. "
                            + "HTTP Status: " + statusCode);
                }
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

    private String getProjectIDByName(String projectName, String unscopedToken)
            throws IOException {
        String endpoint = auth_url + PROJECT_PATH;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(endpoint);
        httpGet.setHeader(SwiftConstants.HEADER_CONTENT_TYPE,
                SwiftConstants.HEADER_VALUE_APP_JSON);
        httpGet.setHeader(SwiftConstants.HEADER_X_AUTH_TOKEN, unscopedToken);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String responseStr = EntityUtils.toString(response.getEntity());
                JSONParser parser = new JSONParser();
                try {
                    JSONObject obj = (JSONObject) parser.parse(responseStr);
                    JSONArray projects = (JSONArray) obj.get("projects");
                    if (projects.size() == 0) {
                        return null;
                    }

                    for (Object project1 : projects) {
                        JSONObject prj = (JSONObject) project1;
                        if (project.equals(prj.get("name"))) {
                            return prj.get("id").toString();
                        }
                    }
                } catch (ParseException ex) {
                    return null;
                }
            }
            return null;
        } finally {
            response.close();
        }
    }

    private String createProjectToken(String projectID, String unscopedToken)
            throws UnsupportedEncodingException, IOException, ParseException {
        String auth_endpoint = auth_url + TOKEN_PATH;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(auth_endpoint);
            httppost.setHeader(SwiftConstants.HEADER_CONTENT_TYPE,
                    SwiftConstants.HEADER_VALUE_APP_JSON);
            httppost.setEntity(new StringEntity(JSON.createTokenProjectScopedAuthBody(
                    projectID, unscopedToken)));
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 201) {
                    String responseStr = EntityUtils.toString(response.getEntity());
                    JSONParser parser = new JSONParser();
                    JSONObject obj;
                    obj = (JSONObject) parser.parse(responseStr);
                    JSONObject token = (JSONObject) obj.get("token");
                    JSONArray catalog = (JSONArray) token.get("catalog");
                    outer:
                    for (Object item : catalog) {
                        JSONObject i = (JSONObject) item;
                        if ("object-store".equals(i.get("type"))) {
                            JSONArray endpoints = (JSONArray) i.get("endpoints");
                            for (Object endpoint : endpoints) {
                                JSONObject e = (JSONObject) endpoint;
                                if ("public".equals(e.get("interface"))) {
                                    storageURL = e.get("url").toString();
                                    break outer;
                                }
                            }
                        }
                    }
                    String rs = response.getFirstHeader(
                            SwiftConstants.HEADER_X_SUBJECT_TOKEN).getValue();
                    return rs;
                } else {
                    throw new AuthenticationException("Unable to create token. "
                            + "HTTP Status: " + statusCode);
                }
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getStorageURL() {
        return storageURL;
    }

    @Override
    public void retry() {
        auth(null, null);
    }

}
