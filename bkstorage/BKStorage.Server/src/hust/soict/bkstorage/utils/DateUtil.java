/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author thinhnt
 */
public class DateUtil {

    public static String convert2String(long lastModified) {
        SimpleDateFormat formater = new SimpleDateFormat("dd-MMM-yy  hh:mm aa");
        return formater.format(new Date(lastModified));
    }

}
