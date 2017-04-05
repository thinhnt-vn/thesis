/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.swift.internal;

import hust.soict.bkstorage.swift.Container;
import hust.soict.bkstorage.swift.StorageObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author thinhnt
 */
public class SwiftApi implements StorageAPI {

    private Authenticate auth;

    public SwiftApi(Authenticate auth) {
        this.auth = auth;
    }

    @Override
    public void putContainer(String containerName) throws StorageException {
        try {
            String endpoint = auth.getStorageURL()
                    + SwiftConstants.URL_SEPARATOR
                    + URLEncoder.encode(containerName, "UTF-8");

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPut httpPut = new HttpPut(endpoint);
            try {
                httpPut.setHeader(SwiftConstants.HEADER_X_AUTH_TOKEN,
                        auth.getToken());
                CloseableHttpResponse response = httpClient.execute(httpPut);
                try {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 401) {
                        auth.retry();
                        putContainer(containerName);
                    } else if (statusCode < 200 || statusCode >= 300) {
                        throw new StorageException("Error occurs"
                                + " when put container");
                    }
                } finally {
                    response.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(SwiftApi.class.getName())
                        .log(Level.SEVERE, null, ex);
            } finally {
                try {
                    httpClient.close();
                } catch (IOException ex) {
                    Logger.getLogger(SwiftApi.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SwiftApi.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Container getContainer(String containerName) throws StorageException {
        try {
            String endpoint = auth.getStorageURL() + SwiftConstants.URL_SEPARATOR
                    + URLEncoder.encode(containerName, "UTF-8");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(endpoint);
            try {
                httpGet.setHeader(SwiftConstants.HEADER_X_AUTH_TOKEN, auth.getToken());
                CloseableHttpResponse response = httpClient.execute(httpGet);
                try {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 401) {
                        auth.retry();
                        return getContainer(containerName);
                    } else if (statusCode != 204 && statusCode != 200) {
                        return null;
                    }
                    Map<String, String> metadata = new HashMap<>();
                    Header[] headers = response.getAllHeaders();
                    for (Header header : headers) {
                        metadata.put(header.getName(), header.getValue());
                    }
                    return new ContainerImpl(this, containerName, metadata);
                } finally {
                    try {
                        response.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SwiftApi.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                throw new StorageException(ex.getMessage());
            } finally {
                try {
                    httpClient.close();
                } catch (IOException ex) {
                    Logger.getLogger(SwiftApi.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SwiftApi.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void deleteContainer(String containerName) throws StorageException {
        try {
            String endpoint = auth.getStorageURL()
                    + SwiftConstants.URL_SEPARATOR
                    + URLEncoder.encode(containerName, "UTF-8");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpDelete httpDelete = new HttpDelete(endpoint);
            try {
                httpDelete.setHeader(SwiftConstants.HEADER_X_AUTH_TOKEN,
                        auth.getToken());
                CloseableHttpResponse response = httpClient.execute(httpDelete);
                try {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 401) {
                        auth.retry();
                        deleteContainer(containerName);
                    } else if (statusCode == 404) {
                        throw new StorageException("The container " + containerName
                                + " doesn't exist. HTTP Status Code: " + statusCode);
                    } else if (statusCode == 409) {
                        throw new StorageException("The container " + containerName
                                + " isn't empty. HTTP Status Code: " + statusCode);
                    } else if (statusCode != 204) {
                        throw new StorageException("Unknow Exception. "
                                + "HTTP Status Code: " + statusCode);
                    }
                } finally {
                    try {
                        response.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SwiftApi.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(SwiftApi.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    httpClient.close();
                } catch (IOException ex) {
                    Logger.getLogger(SwiftApi.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SwiftApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public StorageObject getObject(String container, String objectName)
            throws StorageException {
        try {
            String endpoint = auth.getStorageURL()
                    + SwiftConstants.URL_SEPARATOR
                    + URLEncoder.encode(container, "UTF-8")
                    + SwiftConstants.URL_SEPARATOR
                    + URLEncoder.encode(objectName, "UTF-8");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(endpoint);
            try {
                httpGet.setHeader(SwiftConstants.HEADER_X_AUTH_TOKEN, auth.getToken());
                CloseableHttpResponse response = httpClient.execute(httpGet);
                try {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 401) {
                        auth.retry();
                        return getObject(container, objectName);
                    } else if (statusCode == 416) {
                        throw new StorageException("Range Not Satisfiable. HTTP "
                                + "Status Code: " + statusCode);
                    } else if (statusCode == 404) {
                        throw new StorageException("The " + objectName + " doesn't "
                                + "exist. HTTP Status Code: " + statusCode);
                    } else if (statusCode != 200) {
                        throw new StorageException("Unknow exception. HTTP Status"
                                + " Code: " + statusCode);
                    }
                    byte[] content = EntityUtils.toByteArray(response.getEntity());
                    return new StorageObjectImpl(objectName, content, null);
                } finally {
                    try {
                        response.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SwiftApi.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                throw new StorageException(ex.getMessage());
            } finally {
                try {
                    httpClient.close();
                } catch (IOException ex) {
                    Logger.getLogger(SwiftApi.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SwiftApi.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void putObject(String container, String objectNane, byte[] content,
            Map<String, String> metadata) throws StorageException {
        try {
            String endpoint = auth.getStorageURL()
                    + SwiftConstants.URL_SEPARATOR
                    + URLEncoder.encode(container, "UTF-8") + SwiftConstants.URL_SEPARATOR
                    + URLEncoder.encode(objectNane, "UTF-8");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPut httpPut = new HttpPut(endpoint);
            try {
                httpPut.setHeader(SwiftConstants.HEADER_X_AUTH_TOKEN,
                        auth.getToken());
                if (content != null) {
                    httpPut.setEntity(new ByteArrayEntity(content));
                }
                CloseableHttpResponse response = httpClient.execute(httpPut);
                try {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 401) {
                        auth.retry();
                        putObject(container, objectNane, content, metadata);
                    } else if (statusCode == 408) {
                        throw new StorageException("Request time out. Http Status "
                                + "code: " + statusCode);
                    } else if (statusCode == 411) {
                        throw new StorageException("Missing Transfer-Encoding or "
                                + "Content-Length request header. Http Status "
                                + "code: " + statusCode);
                    } else if (statusCode == 422) {
                        throw new StorageException(" MD5 checksum of the data that is"
                                + " written to the object store does not match the "
                                + "optional ETag value. Http Status "
                                + "code: " + statusCode);
                    } else if (statusCode != 201) {
                        throw new StorageException("Unknow exception. Http Status "
                                + "code: " + statusCode);
                    }
                } finally {
                    response.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(SwiftApi.class.getName())
                        .log(Level.SEVERE, null, ex);
            } finally {
                try {
                    httpClient.close();
                } catch (IOException ex) {
                    Logger.getLogger(SwiftApi.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SwiftApi.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void putObjectMetadata(String container, String objectNane,
            Map<String, String> metadata) throws StorageException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteObject(String container, String objectNane)
            throws StorageException {
        try {
            String endpoint = auth.getStorageURL()
                    + SwiftConstants.URL_SEPARATOR
                    + URLEncoder.encode(container, "UTF-8") + SwiftConstants.URL_SEPARATOR
                    + URLEncoder.encode(objectNane, "UTF-8");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpDelete httpDelete = new HttpDelete(endpoint);
            try {
                httpDelete.setHeader(SwiftConstants.HEADER_X_AUTH_TOKEN,
                        auth.getToken());
                CloseableHttpResponse response = httpClient.execute(httpDelete);
                try {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 401) {
                        auth.retry();
                        deleteObject(container, objectNane);
                    } else if (statusCode == 404) {
                        throw new StorageException("The container " + objectNane
                                + " doesn't exist. HTTP Status Code: " + statusCode);
                    } else if (statusCode != 204) {
                        throw new StorageException("Unknow Exception. "
                                + "HTTP Status Code: " + statusCode);
                    }
                } finally {
                    try {
                        response.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SwiftApi.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(SwiftApi.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    httpClient.close();
                } catch (IOException ex) {
                    Logger.getLogger(SwiftApi.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SwiftApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
