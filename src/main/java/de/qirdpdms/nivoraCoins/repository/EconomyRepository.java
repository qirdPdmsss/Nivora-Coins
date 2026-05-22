package de.qirdpdms.nivoraCoins.repository;

import de.qirdpdms.nivoraCoins.manager.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class EconomyRepository {

    private final DatabaseManager databaseManager;

    public EconomyRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public long loadCoins(UUID uuid) throws SQLException {
        String selectSql = "SELECT coins FROM `" + databaseManager.getTableName() + "` WHERE uuid = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectSql)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("coins");
                }
            }
        }

        createAccount(uuid);
        return 0L;
    }

    public void createAccount(UUID uuid) throws SQLException {
        String insertSql = "INSERT IGNORE INTO `" + databaseManager.getTableName() + "` (uuid, coins) VALUES (?, 0)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
        }
    }

    public void saveCoins(UUID uuid, long coins) throws SQLException {
        String upsertSql = "INSERT INTO `" + databaseManager.getTableName() + "` (uuid, coins) VALUES (?, ?) ON DUPLICATE KEY UPDATE coins = VALUES(coins)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(upsertSql)) {
            statement.setString(1, uuid.toString());
            statement.setLong(2, coins);
            statement.executeUpdate();
        }
    }
}

