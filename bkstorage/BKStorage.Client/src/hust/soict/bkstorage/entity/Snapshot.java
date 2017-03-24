/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.entity;

import hust.soict.bkstorage.exception.SnapshotMappingException;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author thinhnt
 */
public class Snapshot extends HashSet<FileMetaData> {

    private SnapshotMapper mapper;

    public static interface SnapshotMapper {

        public void loadAll(Snapshot snapshot) throws SnapshotMappingException;

        public void store(FileMetaData metaData) throws SnapshotMappingException;

        public void delete(FileMetaData metaData) throws SnapshotMappingException;

        public void deleteAll() throws SnapshotMappingException;
    }

    public void setSnapshotMapper(SnapshotMapper mapper) {
        this.mapper = mapper;
    }

    public void load() throws SnapshotMappingException {
        mapper.loadAll(this);
    }

    public void insert(FileMetaData metaData) throws SnapshotMappingException {
        add(metaData);
        mapper.store(metaData);
    }

    public void delete(String path) throws SnapshotMappingException {
        for (Iterator<FileMetaData> iterator = this.iterator(); iterator.hasNext();) {
            FileMetaData next = iterator.next();
            if (next.getFilePatch().equals(path)) {
                iterator.remove();
            }
            mapper.delete(next);
        }
    }

    public void deleteAll() throws SnapshotMappingException {
        super.clear();
        mapper.deleteAll();
    }
}
