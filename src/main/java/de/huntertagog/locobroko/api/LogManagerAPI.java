package de.huntertagog.locobroko.api;

import de.huntertagog.locobroko.registry.PlayerData;

import java.util.Map;
import java.util.UUID;

public interface LogManagerAPI {

    // Methode zum Abrufen der PlayerData eines Spielers, die alle Logs und den Spielernamen enthält
    PlayerData getPlayerData(UUID playerUUID);

    // Methode zum Abrufen der Anzahl eines bestimmten Ereignistyps eines Spielers
    int getEventCount(UUID playerUUID, String eventType, String eventData);

    // Methode zum Abrufen aller Ereignisse eines bestimmten Typs eines Spielers
    Map<String, Integer> getEventLogs(UUID playerUUID, String eventType);

    // Methode zum Abrufen des Namen eines Spielers
    String getPlayerName(UUID playerUUID);
}
