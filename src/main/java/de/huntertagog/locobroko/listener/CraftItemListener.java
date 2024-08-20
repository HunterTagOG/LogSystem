package de.huntertagog.locobroko.listener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.mineacademy.fo.annotation.AutoRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.huntertagog.locobroko.LogSystem.logManager;

@Getter
@AutoRegister
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CraftItemListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(CraftItemListener.class);

    private static final Listener instance = new CraftItemListener();

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        String itemType = event.getRecipe().getResult().getType().toString();
        try {
            logManager.logEvent(event.getWhoClicked().getUniqueId(), event.getWhoClicked().getName(), "items_crafted", itemType);
        } catch (Exception e) {
            logger.error("Failed to log item craft event for player {}: {}", event.getWhoClicked().getName(), e.getMessage());
        }
    }
}
