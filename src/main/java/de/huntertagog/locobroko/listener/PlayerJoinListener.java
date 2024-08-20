package de.huntertagog.locobroko.listener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mineacademy.fo.annotation.AutoRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static de.huntertagog.locobroko.LogSystem.logManager;

@Getter
@AutoRegister
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerJoinListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(PlayerJoinListener.class);

    private static final Listener instance = new PlayerJoinListener();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        String playerName = event.getPlayer().getName();
        logManager.loadPlayerLogs(playerUUID, playerName);
    }
}
