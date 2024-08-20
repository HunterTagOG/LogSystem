package de.huntertagog.locobroko.listener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.mineacademy.fo.annotation.AutoRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

import static de.huntertagog.locobroko.LogSystem.logManager;

@Getter
@AutoRegister
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerDeathListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(PlayerDeathListener.class);

    private static final Listener instance = new PlayerDeathListener();


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        UUID playerUUID = event.getEntity().getUniqueId();

        try {
            logManager.logEvent(playerUUID, event.getEntity().getName(), "deaths", "death");

            Entity killer = event.getEntity().getKiller();
            if (killer != null && killer.getType() == EntityType.PLAYER) {
                logManager.logEvent(playerUUID, event.getEntity().getName(), "killed_by_player", killer.getName());
            } else {
                EntityDamageEvent.DamageCause cause = Objects.requireNonNull(event.getEntity().getLastDamageCause()).getCause();
                switch (cause) {
                    case FALL:
                        logManager.logEvent(playerUUID, event.getEntity().getName(), "killed_by_falldamage", "fall");
                        break;
                    case LAVA:
                        logManager.logEvent(playerUUID, event.getEntity().getName(), "killed_by_lava", "lava");
                        break;
                    case DROWNING:
                        logManager.logEvent(playerUUID, event.getEntity().getName(), "killed_by_drowned", "drowned");
                        break;
                    case ENTITY_ATTACK:
                        if (killer != null && killer.getType() != EntityType.PLAYER) {
                            logManager.logEvent(playerUUID, event.getEntity().getName(), "killed_by_mob", killer.getType().toString());
                        }
                        break;
                    default:
                        logger.warn("Unhandled death cause: {}", cause);
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to log player death event for player {}: {}", event.getEntity().getName(), e.getMessage());
        }
    }
}
