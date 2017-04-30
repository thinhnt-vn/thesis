/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 *
 * @author thinhnt
 */
public class NewClass {
    public static PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
        byte[] clear = Base64.getDecoder().decode(key64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    public static PublicKey loadPublicKey(String stored) throws GeneralSecurityException {
        byte[] data = Base64.getDecoder().decode(stored);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
    }

    public static String savePrivateKey(PrivateKey priv) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = fact.getKeySpec(priv,
                PKCS8EncodedKeySpec.class);
        byte[] packed = spec.getEncoded();
        String key64 = Base64.getEncoder().encodeToString(packed);

        Arrays.fill(packed, (byte) 0);
        PrintWriter w = null;
        try {
            w = new PrintWriter(new FileWriter(new File("private")));
            w.print(key64);
            w.flush();
        } catch (IOException ex) {
            
        } finally {
            if (w != null) {
                w.close();
            }
        }

        return key64;
    }

    public static String savePublicKey(PublicKey publ) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = fact.getKeySpec(publ,
                X509EncodedKeySpec.class);
        String rs = Base64.getEncoder().encodeToString(spec.getEncoded());
        PrintWriter w = null;
        try {
            w = new PrintWriter(new FileWriter(new File("public")));
            w.print(rs);
            w.flush();
        } catch (IOException ex) {
            
        } finally {
            if (w != null) {
                w.close();
            }
        }
        return rs;
    }

    public static void main(String[] args) throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        KeyPair pair = gen.generateKeyPair();
        
        savePublicKey(pair.getPublic());
        String pubKey = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("public"))) {
            pubKey = reader.readLine();
        }
        PublicKey pubSaved = loadPublicKey(pubKey);
        System.out.println(pair.getPublic() + "\n" + pubSaved);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubSaved);
        byte [] ciperText = cipher.doFinal("Nguyen Toan Thinh".getBytes());
        System.out.println("CiperText: \n" + new String(ciperText));

        savePrivateKey(pair.getPrivate());
        String privKey = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("private"))) {
            privKey = reader.readLine();
        }
        PrivateKey privSaved = loadPrivateKey(privKey);
        System.out.println(pair.getPrivate() + "\n" + privSaved);
        cipher.init(Cipher.DECRYPT_MODE, privSaved);
        byte [] plainText = cipher.doFinal(ciperText);
        System.out.println("Plaint: \n" + new String(plainText));
        
    }
}
