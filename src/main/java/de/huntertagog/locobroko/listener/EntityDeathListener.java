package de.huntertagog.locobroko.listener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.mineacademy.fo.annotation.AutoRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static de.huntertagog.locobroko.LogSystem.logManager;

@Getter
@AutoRegister
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityDeathListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(EntityDeathListener.class);

    private static final Listener instance = new EntityDeathListener();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            UUID killerUUID = event.getEntity().getKiller().getUniqueId();
            EntityType entityType = event.getEntity().getType();

            try {
                if (entityType == EntityType.PLAYER) {
                    logManager.logEvent(killerUUID, event.getEntity().getKiller().getName(), "player_killed", "player");
                } else {
                    logManager.logEvent(killerUUID, event.getEntity().getKiller().getName(), "mob_killed", entityType.toString());
                }
            } catch (Exception e) {
                logger.error("Failed to log entity death event for player {}: {}", event.getEntity().getKiller().getName(), e.getMessage());
            }
        }
    }
}
