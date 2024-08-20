package de.huntertagog.locobroko.listener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.mineacademy.fo.annotation.AutoRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.huntertagog.locobroko.LogSystem.logManager;

@Getter
@AutoRegister
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlockBreakListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(BlockBreakListener.class);

    private static final Listener instance = new BlockBreakListener();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String blockType = event.getBlock().getType().toString();
        try {
            logManager.logEvent(event.getPlayer().getUniqueId(), event.getPlayer().getName(), "block_break", blockType);
        } catch (Exception e) {
            logger.error("Failed to log block break event for player {}: {}", event.getPlayer().getName(), e.getMessage());
        }
    }
}
