/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.entity;

import hust.soict.bkstorage.exception.OptionsMappingException;
import java.util.HashMap;

/**
 *
 * @author thinhnt
 */
public class Options extends HashMap<String, String> {

    public static String AUTO_SYNC_KEY = "auto_sync";
    public static String USER_NAME_KEY = "user_name";
    public static String PASSWORD_KEY = "password";

    private OptionsLoader optionsLoader;
    private OptionsFlusher optionsFlusher;

    public Options() {
    }

    public Options setLoader(OptionsLoader loader) {
        this.optionsLoader = loader;
        return this;
    }

    public Options setFllusher(OptionsFlusher flusher) {
        this.optionsFlusher = flusher;
        return this;
    }

    public void load() throws OptionsMappingException {
        optionsLoader.load(this);
    }

    public void flush() throws OptionsMappingException {
        optionsFlusher.flush(this);
    }

    public boolean isAutoSync() {
        String isAutoStr = get(AUTO_SYNC_KEY);
        return Boolean.parseBoolean(isAutoStr);
    }

    public void setAutoSync(boolean autoSync) {
        put(AUTO_SYNC_KEY, String.valueOf(autoSync));
    }

    public String getUserName() {
        return get(USER_NAME_KEY);
    }

    public void setUserName(String userName) {
        put(USER_NAME_KEY, userName);
    }

    public String getPassword() {
        return get(PASSWORD_KEY);
    }

    public void setPassword(String password) {
        put(PASSWORD_KEY, password);
    }

    public static interface OptionsLoader {

        void load(HashMap<String, String> result) throws OptionsMappingException;
    }

    public static interface OptionsFlusher {

        void flush(HashMap<String, String> input) throws OptionsMappingException;
    }
//
//    public static void main(String[] args) {
//        
//        try {
//            Connection conn = ConnectionManager.createDerbyConnection();
//            Options options = new Options();
//            options.setFllusher(new DerbyOptionsFlusher(conn)).
//                    setLoader(new DerbyOptionsLoader(conn));
//            options.load();
//            print(options);
//            options.setUserName("Canada");
//            options.setPassword("Iraq");
//            options.put("b", null);
//            print(options);
//            options.flush();
//        } catch (ClassNotFoundException | SQLException | OptionsMappingException ex) {
//            Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    
//    public static void print(Options options){
//        System.out.println("---------");
//        options.keySet().stream().forEach((key) -> {
//            System.out.println("K: " + key + "; V: " + options.get(key));
//        });
//    }
}
