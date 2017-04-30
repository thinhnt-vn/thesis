/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.utils;

import java.util.regex.Pattern;

/**
 *
 * @author thinhnt
 */
public class NetUtil {

    public static boolean isIPv4(String ip) {
        if (ip == null) {
            return false;
        }
        Pattern PATTERN = Pattern.compile(
                "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        return PATTERN.matcher(ip).matches();
    }

    public static String getSubnetIP(String ip) {
        if (ip == null) {
            return null;
        }
        int dotPos = ip.lastIndexOf(".");
        return getSubnetPrefix(ip) + "0";
    }
    
    public static String getSubnetPrefix(String ip){
        if (ip == null){
            return null;
        }
        int dotPos = ip.lastIndexOf(".");
        return ip.substring(0, dotPos + 1);
    }

}
