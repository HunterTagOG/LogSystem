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
        return playerLogs.get(playerUUID); // Gibt die PlayerData für den Spieler zurück
    }

    @Override
    public int getEventCount(UUID playerUUID, String eventType, String eventData) {
        PlayerData playerData = playerLogs.get(playerUUID);
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
        if (playerData != null) {
            return playerData.getLogs().getOrDefault(eventType, new HashMap<>());
        }
        return new HashMap<>();
    }

    @Override
    public String getPlayerName(UUID playerUUID) {
        PlayerData playerData = playerLogs.get(playerUUID);
        return playerData != null ? playerData.getPlayerName() : null;
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
