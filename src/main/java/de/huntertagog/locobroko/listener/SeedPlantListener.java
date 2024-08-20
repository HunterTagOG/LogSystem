package de.huntertagog.locobroko.listener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.mineacademy.fo.annotation.AutoRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Set;

import static de.huntertagog.locobroko.LogSystem.logManager;

@Getter
@AutoRegister
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SeedPlantListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(SeedPlantListener.class);

    private static final Listener instance = new SeedPlantListener();

    // Definiert ein Set der relevanten Saatgut-Materialien
    private static final Set<Material> SEEDS = EnumSet.of(
            Material.WHEAT_SEEDS,
            Material.POTATO,
            Material.CARROT,
            Material.BEETROOT_SEEDS,
            Material.MELON_SEEDS,
            Material.PUMPKIN_SEEDS,
            Material.TORCHFLOWER_SEEDS
    );

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Material blockType = event.getBlock().getType();

        if (SEEDS.contains(blockType)) {
            try {
                logManager.logEvent(event.getPlayer().getUniqueId(), event.getPlayer().getName(), "seeds_planted", blockType.toString());
            } catch (Exception e) {
                logger.error("Failed to log seed planting event for player {}: {}", event.getPlayer().getName(), e.getMessage());
            }
        }
    }
}
