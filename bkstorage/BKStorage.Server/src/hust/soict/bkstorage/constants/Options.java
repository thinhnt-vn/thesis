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

    public static String MY_IP_VALUE;
    private final static String MY_IP_KEY = "my_ip";
    public static String CONFIG_FILE = "bkstorage.conf";
    private static Properties appProperties;

    public static void load(String configFile) throws FileNotFoundException,
            IOException {
        appProperties = new Properties();
        FileInputStream in = new FileInputStream(configFile);
        appProperties.load(in);
        in.close();
        MY_IP_VALUE = appProperties.getProperty(MY_IP_KEY);
    }

    public static void store(String configFile) throws FileNotFoundException, 
            IOException {
        appProperties.setProperty(MY_IP_KEY, MY_IP_VALUE);
        OutputStream out = new FileOutputStream(configFile);
        appProperties.store(out, null);
        out.close();
    }
//    
//    public static void main(String[] args) {
//        try {
//            Options.load(Options.CONFIG_FILE);
//            System.out.println(Options.MY_IP_VALUE);
//            Options.MY_IP_VALUE = "sadf";
//            Options.store(Options.CONFIG_FILE);
//        } catch (IOException ex) {
//            Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//    }
}
