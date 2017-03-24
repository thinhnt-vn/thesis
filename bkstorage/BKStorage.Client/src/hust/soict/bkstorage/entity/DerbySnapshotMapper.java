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

/**
 *
 * @author thinhnt
 */
public class DerbySnapshotMapper implements Snapshot.SnapshotMapper {

    private Connection connection;

    public DerbySnapshotMapper(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void loadAll(Snapshot snapshot) throws SnapshotMappingException {
        try {
            String query = "SELECT * FROM files";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                FileMetaData fileMetaData = new FileMetaData();
                String filePath = rs.getString(2);
                fileMetaData.setFilePatch(filePath);
                snapshot.add(fileMetaData);
            }
        } catch (SQLException ex) {
            throw new SnapshotMappingException(ex.getMessage());
        }
    }

    @Override
    public void store(FileMetaData metaData) throws SnapshotMappingException {
        try {
            String query = "INSERT INTO files(path) VALUES ('" + metaData.getFilePatch() + "')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            throw new SnapshotMappingException(ex.getMessage());
        }
    }

    @Override
    public void delete(FileMetaData metaData) throws SnapshotMappingException {
        try {
            String query = "DELETE FROM files WHERE path = '" + metaData.getFilePatch() + "'";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            throw new SnapshotMappingException(ex.getMessage());
        }
    }
    
    public void deleteAll() throws SnapshotMappingException{
        try {
            String query = "DELETE FROM files";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            throw new SnapshotMappingException(ex.getMessage());
        }
    }
}
