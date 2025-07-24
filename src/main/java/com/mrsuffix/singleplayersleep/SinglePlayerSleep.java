package com.mrsuffix.singleplayersleep;

import com.mrsuffix.singleplayersleep.commands.SleepCommand;
import com.mrsuffix.singleplayersleep.listeners.SleepListener;
import com.mrsuffix.singleplayersleep.managers.ConfigManager;
import com.mrsuffix.singleplayersleep.managers.CooldownManager;
import com.mrsuffix.singleplayersleep.managers.StatisticsManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * SinglePlayerSleep - A modern Minecraft plugin that allows night to pass when only one player sleeps
 * Compatible with Paper/Spigot 1.20+
 *
 * @author YourName
 * @version 1.0.0
 */
public final class SinglePlayerSleep extends JavaPlugin {

    private static SinglePlayerSleep instance;
    private ConfigManager configManager;
    private StatisticsManager statisticsManager;
    private CooldownManager cooldownManager;
    private boolean debugMode;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.statisticsManager = new StatisticsManager(this);
        this.cooldownManager = new CooldownManager(this);

        // Load configuration
        configManager.loadConfig();
        statisticsManager.loadStatistics();

        this.debugMode = configManager.isDebugMode();

        // Register listeners
        getServer().getPluginManager().registerEvents(new SleepListener(this), this);

        // Register commands
        getCommand("sleep").setExecutor(new SleepCommand(this));

        // Log startup
        getLogger().info("SinglePlayerSleep v" + getDescription().getVersion() + " has been enabled!");
        if (debugMode) {
            getLogger().info("Debug mode is enabled.");
        }
    }

    @Override
    public void onDisable() {
        // Save statistics before shutdown
        if (statisticsManager != null) {
            statisticsManager.saveStatistics();
        }

        getLogger().info("SinglePlayerSleep has been disabled!");
    }

    /**
     * Get the plugin instance
     * @return Plugin instance
     */
    public static SinglePlayerSleep getInstance() {
        return instance;
    }

    /**
     * Get the configuration manager
     * @return ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Get the statistics manager
     * @return StatisticsManager instance
     */
    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    /**
     * Get the cooldown manager
     * @return CooldownManager instance
     */
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    /**
     * Check if debug mode is enabled
     * @return true if debug mode is on
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Log debug message if debug mode is enabled
     * @param message Message to log
     */
    public void debugLog(String message) {
        if (debugMode) {
            getLogger().log(Level.INFO, "[DEBUG] " + message);
        }
    }
}
