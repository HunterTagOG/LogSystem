package de.huntertagog.locobroko.registry;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PlayerData {

    private final String playerName;
    private final Map<String, Map<String, Integer>> logs;

    public PlayerData(String playerName) {
        this.playerName = playerName;
        this.logs = new HashMap<>();
    }

}
