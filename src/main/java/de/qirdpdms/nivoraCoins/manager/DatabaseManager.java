package de.qirdpdms.nivoraCoins.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.qirdpdms.nivoraCoins.file.ConfigManager;
import de.qirdpdms.nivoraCoins.file.MessageManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

public class DatabaseManager {

    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");

    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private HikariDataSource dataSource;

    public DatabaseManager(ConfigManager configManager, MessageManager messageManager) {
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    public boolean connect() {
        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setPoolName(configManager.getPoolName());
            hikariConfig.setJdbcUrl(buildJdbcUrl());
            hikariConfig.setUsername(configManager.getDatabaseUsername());
            hikariConfig.setPassword(configManager.getDatabasePassword());
            hikariConfig.setMaximumPoolSize(configManager.getPoolSize());
            hikariConfig.setMinimumIdle(configManager.getMinimumIdle());
            hikariConfig.setConnectionTimeout(configManager.getConnectionTimeout());
            hikariConfig.setMaxLifetime(configManager.getMaxLifetime());
            hikariConfig.setKeepaliveTime(configManager.getKeepaliveTime());
            hikariConfig.setLeakDetectionThreshold(configManager.getLeakDetectionThreshold());
            hikariConfig.addDataSourceProperty("cachePrepStmts", true);
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
            hikariConfig.addDataSourceProperty("rewriteBatchedStatements", true);
            hikariConfig.addDataSourceProperty("useSSL", false);
            hikariConfig.addDataSourceProperty("serverTimezone", "UTC");
            this.dataSource = new HikariDataSource(hikariConfig);

            try (Connection ignored = dataSource.getConnection()) {
                messageManager.log("system.database-connected");
                return true;
            }
        } catch (Exception exception) {
            messageManager.logError("system.database-connection-failed", exception);
            close();
            return false;
        }
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS `" + getTableName() + "` (uuid VARCHAR(36) NOT NULL PRIMARY KEY, coins BIGINT NOT NULL DEFAULT 0)";
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            messageManager.log("system.database-table-ready");
        } catch (SQLException exception) {
            messageManager.logError("system.database-load-failed", exception);
            throw new IllegalStateException(exception);
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException();
        }

        return dataSource.getConnection();
    }

    public String getTableName() {
        String configuredName = configManager.getTableName();
        if (!TABLE_NAME_PATTERN.matcher(configuredName).matches()) {
            throw new IllegalStateException();
        }

        return configuredName;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private String buildJdbcUrl() {
        return "jdbc:mysql://" + configManager.getDatabaseHost() + ":" + configManager.getDatabasePort() + "/" + configManager.getDatabaseName() + "?useUnicode=true&characterEncoding=UTF-8";
    }
}


