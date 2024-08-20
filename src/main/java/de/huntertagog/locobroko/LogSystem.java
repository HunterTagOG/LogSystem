package de.huntertagog.locobroko;

import de.huntertagog.locobroko.api.LogManagerAPI;
import de.huntertagog.locobroko.database.Database;
import de.huntertagog.locobroko.manager.LogManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogSystem extends SimplePlugin {

    private static final Logger logger = LoggerFactory.getLogger(LogSystem.class);
    @Getter
    public static Database database;

    public static LogManager logManager;

    @Override
    protected void onPluginStart() {
        // Save default configuration
        this.saveDefaultConfig();

        database = new Database(
                getConfig().getString("database.url"),
                getConfig().getString("database.user"),
                getConfig().getString("database.password")
        );

        logManager = new LogManager(database);

        logger.info("@ LogSystem has been enabled!");
    }

    @Override
    protected void onPluginLoad() {
        // Load logic if needed
        logger.info("@ LogSystem is loading!");
    }

    @Override
    protected void onPluginReload() {
        logManager.saveAllLogsToDatabase();
        // Close the database connection
        database.close();
        // Unregister all listeners for safe reload
        HandlerList.unregisterAll((JavaPlugin) this);
        logger.info("@ LogSystem has been reloaded!");
    }

    @Override
    protected void onPluginStop() {
        logManager.saveAllLogsToDatabase();
        // Close the database connection
        database.close();
        // Unregister all listeners for safe reload
        HandlerList.unregisterAll((JavaPlugin) this);
        logger.info("@ LogSystem has been disabled!");
    }

    /**
     * Gets the instance of the LBSBlock plugin.
     *
     * @return the instance of LBSBlock
     */
    public static LogSystem getInstance() {
        return (LogSystem) SimplePlugin.getInstance();
    }

    // Methode, um die API für andere Plugins zugänglich zu machen
    public LogManagerAPI getLogManagerAPI() {
        return logManager;
    }

}
