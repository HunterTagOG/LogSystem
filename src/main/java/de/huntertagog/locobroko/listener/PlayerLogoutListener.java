package de.huntertagog.locobroko.listener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mineacademy.fo.annotation.AutoRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static de.huntertagog.locobroko.LogSystem.logManager;

@Getter
@AutoRegister
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerLogoutListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(PlayerLogoutListener.class);

    private static final Listener instance = new PlayerLogoutListener();

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        logManager.saveLogsToDatabase(playerUUID);
    }
}
