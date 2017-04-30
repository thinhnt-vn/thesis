/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.entity;

import hust.soict.bkstorage.exception.SnapshotMappingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thinhnt
 */
public class DerbyLanSyncSnapshotMapper implements Snapshot.SnapshotMapper {

    private Connection connection;
    private final static String table = "lansyncfiles";

    public DerbyLanSyncSnapshotMapper(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void loadAll(Snapshot snapshot) throws SnapshotMappingException {
        try {
            String query = "SELECT * FROM " + table;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String filePath = rs.getString(2);
                boolean deleted = rs.getBoolean(3);
                LanSyncFileMetaData fileMetaData = new LanSyncFileMetaData(
                        filePath, deleted);
                snapshot.add(fileMetaData);
            }
        } catch (SQLException ex) {
            throw new SnapshotMappingException(ex.getMessage());
        }
    }

    @Override
    public void store(FileMetaData metaData) throws SnapshotMappingException {
        LanSyncFileMetaData lanSyncFileMetaData = (LanSyncFileMetaData) metaData;
        try {
            String v1 = lanSyncFileMetaData.getFilePatch();
            boolean v2 = lanSyncFileMetaData.isDeleted();
            String query = "INSERT INTO " + table + "(path, deleted) VALUES ('"
                    + v1 + "', "
                    + v2 + ")";
            System.out.println("Query: " + query);
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            throw new SnapshotMappingException(ex.getMessage());
        }
    }

    public void update(LanSyncFileMetaData metaData) throws SnapshotMappingException {
        try {
            String query = "UPDATE " + table + " SET deleted = " + metaData.isDeleted()
                    + " WHERE path = '" + metaData.getFilePatch() + "'";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex1) {
            Logger.getLogger(DerbyLanSyncSnapshotMapper.class.getName()).log(Level.SEVERE, null, ex1);
            throw new SnapshotMappingException(ex1.getMessage());
        }
    }

    @Override
    public void delete(FileMetaData metaData) throws SnapshotMappingException {
        try {
            String query = "DELETE FROM " + table + " WHERE path = '" + metaData.getFilePatch() + "'";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            throw new SnapshotMappingException(ex.getMessage());
        }
    }

    @Override
    public void deleteAll() throws SnapshotMappingException {
        try {
            String query = "DELETE FROM " + table;
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            throw new SnapshotMappingException(ex.getMessage());
        }
    }

}
