/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.dal;

import hust.soict.bkstorage.entity.DerbyOptionsFlusher;
import hust.soict.bkstorage.entity.DerbyOptionsLoader;
import hust.soict.bkstorage.entity.DerbySnapshotMapper;
import hust.soict.bkstorage.entity.Options;
import hust.soict.bkstorage.entity.Snapshot;
import hust.soict.bkstorage.exception.OptionsMappingException;
import hust.soict.bkstorage.exception.SnapshotMappingException;
import hust.soict.bkstorage.factory.RemoteFactory;
import hust.soict.bkstorage.utils.FileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author toant_000
 */
public class Dal {

    protected String serverName;
    protected int port;
    protected static RemoteFactory factory;

    protected static Properties configProperties;
    protected static final String SERVER_IP_KEY = "bkstorage_ip";
    protected static final String SERVER_PORT_KEY = "bkstorage_port";

    protected static Options options;

    protected static Snapshot snapshot;

    public Dal() {
    }

    public Dal(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public void init() throws ClassNotFoundException, SQLException,
            OptionsMappingException, IOException, SnapshotMappingException {
        loadOptions();
        loadConfig();
        loadSnapshot();
    }

    /**
     * Nạp các tùy chọn đã lưu (tự động đồng bộ, tên người dùng, mật khẩu,
     * token)
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws OptionsMappingException
     */
    public static void loadOptions() throws ClassNotFoundException, SQLException,
            OptionsMappingException {
        if (options == null) {
            Connection conn = ConnectionManager.DerbyConnection.createConnection();
            options = new Options();
            options.setLoader(new DerbyOptionsLoader(conn))
                    .setFllusher(new DerbyOptionsFlusher(conn));
            options.load();
        }
    }

    /**
     * Lưu các tùy chọn đã lưu xuống file
     *
     * @throws OptionsMappingException
     */
    public static void storeOptions() throws OptionsMappingException {
        options.flush();
    }

    /**
     * Nạp các thông số cấu hình (ip máy chủ, cổng)
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void loadConfig() throws FileNotFoundException, IOException {
        if (configProperties == null) {
            configProperties = new Properties();
            File configFile = FileUtil.makeConfigFile();
            try (FileInputStream in = new FileInputStream(configFile)) {
                configProperties.load(in);
            }
        }
    }

    public static void loadSnapshot() throws ClassNotFoundException, SQLException,
            SnapshotMappingException {
        if (snapshot == null) {
            snapshot = new Snapshot();
            snapshot.setSnapshotMapper(new DerbySnapshotMapper(
                    ConnectionManager.DerbyConnection.createConnection()));
            snapshot.load();
        }
    }

    public static void storeConfig() throws FileNotFoundException, IOException {
        try (OutputStream out = new FileOutputStream(FileUtil.makeConfigFile())) {
            configProperties.store(out, null);
        }
    }

    /**
     * Kết nối đến máy chủ
     *
     * @return
     * @throws RemoteException
     * @throws NotBoundException
     */
    public boolean connect() throws RemoteException, NotBoundException {
        Registry reg = LocateRegistry.getRegistry(serverName, port);
        factory = (RemoteFactory) reg.lookup("factory");
        return factory.getConnectState();
    }

}
