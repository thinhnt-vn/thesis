/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.entity;

import hust.soict.bkstorage.exception.OptionsMappingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 *
 * @author thinhnt
 */
public class DerbyOptionsLoader implements Options.OptionsLoader {

    private Connection conn;

    public DerbyOptionsLoader(Connection connection) {
        this.conn = connection;
    }

    @Override
    public void load(HashMap<String, String> result) throws OptionsMappingException {
        try {
            Statement stat = conn.createStatement();
            String query = "SELECT * FROM options";
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {                
                String optionKey = rs.getString(1);
                String optionValue = rs.getString(2);
                result.put(optionKey, optionValue);
            }
        } catch (SQLException ex) {
            throw new OptionsMappingException(ex.getMessage());
        }
    }

}
