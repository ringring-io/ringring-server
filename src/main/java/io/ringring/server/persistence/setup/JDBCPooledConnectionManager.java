package io.ringring.server.persistence.setup;

import java.sql.Connection;
import java.sql.SQLException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import io.ringring.server.config.ConfigManager;
import io.ringring.server.persistence.ConnectionManager;

/**
 * Created by kosztope on 23/01/14.
 */

@Singleton
public class JDBCPooledConnectionManager implements ConnectionManager {
    private volatile BoneCP connectionPool;
    private final ConfigManager configManager;

    @Inject
    JDBCPooledConnectionManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public Connection getConnection() {

        try {
            if (connectionPool == null)
                setUpConnectionPool();

            return connectionPool.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create new connection", e);
        }
    }

    private void setUpConnectionPool() {
        try {
            Class.forName(configManager.getDatabaseDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load JDBC driver" , e);
        }

        BoneCPConfig config = new BoneCPConfig();

        config.setJdbcUrl(configManager.getDatabaseUrl());
        config.setUsername(configManager.getDatabaseUser());
        config.setPassword(configManager.getDatabasePassword());
        config.setMaxConnectionsPerPartition(20);

        try {
            connectionPool = new BoneCP(config);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to connect to datasource", e);
        }
    }
}