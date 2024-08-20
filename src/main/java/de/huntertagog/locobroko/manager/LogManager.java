package de.huntertagog.locobroko.manager;

import de.huntertagog.locobroko.api.LogManagerAPI;
import de.huntertagog.locobroko.database.Database;
import de.huntertagog.locobroko.registry.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LogManager implements LogManagerAPI {

    private final Map<UUID, PlayerData> playerLogs = new HashMap<>();
    private final Database database;

    public LogManager(Database database) {
        this.database = database;
    }

    @Override
    public PlayerData getPlayerData(UUID playerUUID) {
        PlayerData playerData = playerLogs.get(playerUUID);
        if (playerData == null) {
            playerData = loadPlayerDataFromDatabase(playerUUID);
        }
        return playerData;
    }

    @Override
    public int getEventCount(UUID playerUUID, String eventType, String eventData) {
        PlayerData playerData = playerLogs.get(playerUUID);
        if (playerData == null) {
            playerData = loadPlayerDataFromDatabase(playerUUID);
        }
        if (playerData != null) {
            Map<String, Integer> eventLogs = playerData.getLogs().get(eventType);
            if (eventLogs != null) {
                return eventLogs.getOrDefault(eventData, 0);
            }
        }
        return 0;
    }

    @Override
    public Map<String, Integer> getEventLogs(UUID playerUUID, String eventType) {
        PlayerData playerData = playerLogs.get(playerUUID);
        if (playerData == null) {
            playerData = loadPlayerDataFromDatabase(playerUUID);
        }
        if (playerData != null) {
            return playerData.getLogs().getOrDefault(eventType, new HashMap<>());
        }
        return new HashMap<>();
    }

    @Override
    public String getPlayerName(UUID playerUUID) {
        PlayerData playerData = playerLogs.get(playerUUID);
        if (playerData == null) {
            playerData = loadPlayerDataFromDatabase(playerUUID);
        }
        return playerData != null ? playerData.getPlayerName() : null;
    }

    // Helper method to load player data from the database and cache it
    private PlayerData loadPlayerDataFromDatabase(UUID playerUUID) {
        // Diese Methode sollte die Logik enthalten, um die PlayerData aus der Datenbank zu laden.
        Map<String, Map<String, Integer>> logsFromDb = database.loadPlayerLogs(playerUUID);
        String playerName = database.getPlayerName(playerUUID); // Annahme: Eine Methode zum Abrufen des Spielernamens existiert in der Datenbankklasse
        if (logsFromDb != null && playerName != null) {
            PlayerData playerData = new PlayerData(playerName);
            playerData.getLogs().putAll(logsFromDb);
            playerLogs.put(playerUUID, playerData); // Caching the loaded data
            return playerData;
        }
        return null;
    }

    public void logEvent(UUID playerUUID, String playerName, String eventType, String eventData) {
        PlayerData playerData = playerLogs.computeIfAbsent(playerUUID, uuid -> new PlayerData(playerName));
        playerData.getLogs()
                .computeIfAbsent(eventType, type -> new HashMap<>())
                .merge(eventData, 1, Integer::sum);
    }

    public void saveAllLogsToDatabase() {
        for (Map.Entry<UUID, PlayerData> playerEntry : playerLogs.entrySet()) {
            UUID playerUUID = playerEntry.getKey();
            PlayerData playerData = playerEntry.getValue();
            String playerName = playerData.getPlayerName();

            for (Map.Entry<String, Map<String, Integer>> eventEntry : playerData.getLogs().entrySet()) {
                String eventType = eventEntry.getKey();
                database.saveLogsToDatabase(playerUUID, playerName, eventType, eventEntry.getValue());
            }
        }
    }

    public void saveLogsToDatabase(UUID playerUUID) {
        PlayerData playerData = playerLogs.get(playerUUID);
        if (playerData != null) {
            String playerName = playerData.getPlayerName();

            for (Map.Entry<String, Map<String, Integer>> eventEntry : playerData.getLogs().entrySet()) {
                String eventType = eventEntry.getKey();
                database.saveLogsToDatabase(playerUUID, playerName, eventType, eventEntry.getValue());
            }
        }
    }

    public void loadPlayerLogs(UUID playerUUID, String playerName) {
        Map<String, Map<String, Integer>> loadedLogs = database.loadPlayerLogs(playerUUID);
        if (loadedLogs != null && !loadedLogs.isEmpty()) {
            playerLogs.put(playerUUID, new PlayerData(playerName));
            playerLogs.get(playerUUID).getLogs().putAll(loadedLogs);
        } else {
            playerLogs.put(playerUUID, new PlayerData(playerName));
        }
    }
}
