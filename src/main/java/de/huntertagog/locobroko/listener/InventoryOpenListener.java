package de.huntertagog.locobroko.listener;

import de.huntertagog.locobroko.LogSystem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.mineacademy.fo.annotation.AutoRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.huntertagog.locobroko.LogSystem.logManager;

@Getter
@AutoRegister
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InventoryOpenListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(InventoryOpenListener.class);

    private static final Listener instance = new InventoryOpenListener();

    // Speichert den Weltname aus der Konfiguration
    private final String warzoneWorldName = LogSystem.getInstance().getConfig().getString("warzone_world");

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.CHEST &&
                event.getPlayer() instanceof Player player) {

            if (player.getWorld().getName().equalsIgnoreCase(warzoneWorldName)) {
                try {
                    logManager.logEvent(player.getUniqueId(), player.getName(), "chest_opened", "chest");
                } catch (Exception e) {
                    logger.error("Failed to log chest opened event for player {}: {}", player.getName(), e.getMessage());
                }
            }
        }
    }
}
