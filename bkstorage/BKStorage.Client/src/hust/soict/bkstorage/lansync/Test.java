/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.lansync;

import java.io.File;

/**
 *
 * @author thinhnt
 */
public class Test {
    public static void main(String[] args) {
        File f = new File("testFol/a/b/c");
        System.out.println(f.isDirectory());
        System.out.println(f.getParentFile().getName());
        String v1 = "a";
        boolean v2 = true;
        String table = "lansyncfiles";
        String query = "INSERT INTO " + table + "(path, deleted) VALUES ('"
                    + v1 + "', "
                    + v2 + ")";
        System.out.println(query);
    }
}
