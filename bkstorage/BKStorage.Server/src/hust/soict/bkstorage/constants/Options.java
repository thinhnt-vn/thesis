/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.constants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thinhnt
 */
public class Options {

    public static String BIND_IP_VALUE;
    private final static String BIND_IP_KEY = "bind_ip";
    public static String BIND_PORT_VALUE;
    private static String BIND_PORT_KEY = "bind_port";
    public static String MYSQL_HOST_VALUE;
    private final static String MYSQL_HOST_KEY = "mysql_host";
    public static String MYSQL_PORT_VALUE;
    private final static String MYSQL_PORT_KEY = "mysql_port";
    public static String MYSQL_USER_NAME_VALUE;
    private final static String MYSQL_USER_NAME_KEY = "mysql_user_name";
    public static String MYSQL_PASSWORD_VALUE;
    private final static String MYSQL_PASSWORD_KEY = "mysql_pasword";
    public static String MYSQL_DB_NAME_VALUE;
    private final static String MYSQL_DB_NAME_KEY = "mysql_db_name";
    public static String CONFIG_FILE = "bkstorage.conf";
    private static Properties appProperties;

    public static void load(String configFile) throws FileNotFoundException,
            IOException {
        appProperties = new Properties();
        try (FileInputStream in = new FileInputStream(configFile)) {
            appProperties.load(in);
        }
        BIND_IP_VALUE = appProperties.getProperty(BIND_IP_KEY);
        BIND_PORT_VALUE = appProperties.getProperty(BIND_PORT_KEY);
        MYSQL_HOST_VALUE = appProperties.getProperty(MYSQL_HOST_KEY);
        MYSQL_PORT_VALUE = appProperties.getProperty(MYSQL_PORT_KEY);
        MYSQL_USER_NAME_VALUE = appProperties.getProperty(MYSQL_USER_NAME_KEY);
        MYSQL_PASSWORD_VALUE = appProperties.getProperty(MYSQL_PASSWORD_KEY);
        MYSQL_DB_NAME_VALUE = appProperties.getProperty(MYSQL_DB_NAME_KEY);
    }

    public static void store(String configFile) throws FileNotFoundException,
            IOException {
        appProperties.setProperty(BIND_IP_KEY, BIND_IP_VALUE);
        appProperties.setProperty(BIND_PORT_KEY, BIND_PORT_VALUE);
        appProperties.setProperty(MYSQL_HOST_KEY, MYSQL_HOST_VALUE);
        appProperties.setProperty(MYSQL_PORT_KEY, MYSQL_PORT_VALUE);
        appProperties.setProperty(MYSQL_USER_NAME_KEY, MYSQL_USER_NAME_VALUE);
        appProperties.setProperty(MYSQL_PASSWORD_KEY, MYSQL_PASSWORD_VALUE);
        appProperties.setProperty(MYSQL_DB_NAME_KEY, MYSQL_DB_NAME_VALUE);
        try (OutputStream out = new FileOutputStream(configFile)) {
            appProperties.store(out, null);
        }
    }
    
    public static void main(String[] args) {
        try {
            Options.load(Options.CONFIG_FILE);
            System.out.println(Options.BIND_IP_VALUE);
            System.out.println(Options.BIND_PORT_VALUE);
            System.out.println(Options.MYSQL_HOST_VALUE);
            System.out.println(Options.MYSQL_USER_NAME_VALUE);
            System.out.println(Options.MYSQL_PASSWORD_VALUE);
            System.out.println(Options.MYSQL_DB_NAME_VALUE);
        } catch (IOException ex) {
            Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
