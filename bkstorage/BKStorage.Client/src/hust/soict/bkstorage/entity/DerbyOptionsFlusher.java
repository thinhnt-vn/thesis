/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.entity;

import hust.soict.bkstorage.exception.OptionsMappingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author thinhnt
 */
public class DerbyOptionsFlusher implements Options.OptionsFlusher {

    private Connection conn;

    public DerbyOptionsFlusher(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void flush(HashMap<String, String> input) throws OptionsMappingException {
        try {
            PreparedStatement insertStat = conn.prepareStatement("INSERT INTO options VALUES "
                    + "(?, ?)");
            PreparedStatement updateStat = conn.prepareStatement("UPDATE options SET "
                    + "option_value = ? WHERE option_key = ?");

            for (String optionKey : input.keySet()) {
                String optionValue = input.get(optionKey);
                insertStat.setString(1, optionKey);
                insertStat.setString(2, optionValue);
                try {
                    insertStat.executeUpdate();
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 30000
                            && "23505".equals(ex.getSQLState())) {
                        updateStat.setString(1, optionValue);
                        updateStat.setString(2, optionKey);
                        updateStat.executeUpdate();
                    }
                }
            }
        } catch (SQLException ex) {
            throw new OptionsMappingException(ex.getMessage() + "\n Error code: "
                    + ex.getErrorCode() + "\n SQLState: " + ex.getSQLState());
        }
    }
}
