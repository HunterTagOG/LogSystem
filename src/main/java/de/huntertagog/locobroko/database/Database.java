package de.huntertagog.locobroko.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Database extends AbstractDatabase {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private final HikariDataSource dataSource;

    public Database(String url, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(30000);

        this.dataSource = new HikariDataSource(config);
        createTablesIfNotExists();
    }

    @Override
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void createTablesIfNotExists() {
        String SQL_1 = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36) UNIQUE,
                    player_name VARCHAR(255)
                );""";
        String SQL_2 = """
                CREATE TABLE IF NOT EXISTS block_break (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36),
                    data VARCHAR(255),
                    count INT,
                    FOREIGN KEY (player_uuid) REFERENCES users(player_uuid) ON DELETE CASCADE
                );""";
        String SQL_3 = """
                CREATE TABLE IF NOT EXISTS block_place (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36),
                    data VARCHAR(255),
                    count INT,
                    FOREIGN KEY (player_uuid) REFERENCES users(player_uuid) ON DELETE CASCADE
                );""";
        String SQL_4 = """
                CREATE TABLE IF NOT EXISTS items_crafted (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36),
                    data VARCHAR(255),
                    count INT,
                    FOREIGN KEY (player_uuid) REFERENCES users(player_uuid) ON DELETE CASCADE
                );""";
        String SQL_5 = """
                CREATE TABLE IF NOT EXISTS player_killed (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36),
                    data VARCHAR(255),
                    count INT,
                    FOREIGN KEY (player_uuid) REFERENCES users(player_uuid) ON DELETE CASCADE
                );""";
        String SQL_6 = """
                CREATE TABLE IF NOT EXISTS mob_killed (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36),
                    data VARCHAR(255),
                    count INT,
                    FOREIGN KEY (player_uuid) REFERENCES users(player_uuid) ON DELETE CASCADE
                );""";
        String SQL_7 = """
                CREATE TABLE IF NOT EXISTS chest_opened (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36),
                    data VARCHAR(255),
                    count INT,
                    FOREIGN KEY (player_uuid) REFERENCES users(player_uuid) ON DELETE CASCADE
                );""";
        String SQL_8 = """
                CREATE TABLE IF NOT EXISTS seeds_planted (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36),
                    data VARCHAR(255),
                    count INT,
                    FOREIGN KEY (player_uuid) REFERENCES users(player_uuid) ON DELETE CASCADE
                );""";
        String SQL_9 = """
                CREATE TABLE IF NOT EXISTS deaths (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36),
                    data VARCHAR(255),
                    count INT,
                    FOREIGN KEY (player_uuid) REFERENCES users(player_uuid) ON DELETE CASCADE
                );""";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(SQL_1);
            stmt.execute(SQL_2);
            stmt.execute(SQL_3);
            stmt.execute(SQL_4);
            stmt.execute(SQL_5);
            stmt.execute(SQL_6);
            stmt.execute(SQL_7);
            stmt.execute(SQL_8);
            stmt.execute(SQL_9);
            logger.info("Database tables created or verified successfully.");

        } catch (SQLException e) {
            logger.error("An error occurred while creating database tables: {}", e.getMessage());
        }
    }

    public void saveLogsToDatabase(UUID playerUUID, String playerName, String eventType, Map<String, Integer> log) {
        ensurePlayerExists(playerUUID, playerName);

        String sql = "INSERT INTO " + eventType + " (player_uuid, data, count) " +
                "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE count = count + VALUES(count)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Map.Entry<String, Integer> entry : log.entrySet()) {
                stmt.setString(1, playerUUID.toString());
                stmt.setString(2, entry.getKey());
                stmt.setInt(3, entry.getValue());
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (SQLException e) {
            logger.error("An error occurred while saving logs to database: {}", e.getMessage());
        }
    }

    private void ensurePlayerExists(UUID playerUUID, String playerName) {
        String checkSql = "SELECT player_name FROM users WHERE player_uuid = ?";
        String insertSql = "INSERT INTO users (player_uuid, player_name) VALUES (?, ?)";
        String updateSql = "UPDATE users SET player_name = ? WHERE player_uuid = ?";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, playerUUID.toString());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    String existingName = rs.getString("player_name");
                    if (!existingName.equals(playerName)) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, playerName);
                            updateStmt.setString(2, playerUUID.toString());
                            updateStmt.executeUpdate();
                            logger.info("Player name updated in the database: {} -> {}", existingName, playerName);
                        }
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, playerUUID.toString());
                        insertStmt.setString(2, playerName);
                        insertStmt.executeUpdate();
                        logger.info("New player added to the database: {}", playerName);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred while ensuring player exists in the database: {}", e.getMessage());
        }
    }

    public Map<String, Map<String, Integer>> loadPlayerLogs(UUID playerUUID) {
        Map<String, Map<String, Integer>> playerLog = new HashMap<>();
        String[] tables = {"block_break", "block_place", "items_crafted", "player_killed", "mob_killed",
                "chest_opened", "seeds_planted", "deaths"};

        for (String table : tables) {
            String sql = "SELECT data, count FROM " + table + " WHERE player_uuid = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerUUID.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    Map<String, Integer> eventMap = new HashMap<>();
                    while (rs.next()) {
                        String data = rs.getString("data");
                        int count = rs.getInt("count");

                        eventMap.put(data, count);
                    }
                    if (!eventMap.isEmpty()) {
                        playerLog.put(table, eventMap);
                    }
                } catch (SQLException e) {
                    logger.error("An error occurred while loading player logs from table {}: {}", table, e.getMessage());
                }
            } catch (SQLException e) {
                logger.error("An error occurred while connecting to the database for table {}: {}", table, e.getMessage());
            }
        }

        return playerLog;
    }
}
