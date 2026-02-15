package com.mrsuffix.singleplayersleep;

import com.mrsuffix.singleplayersleep.commands.SleepCommand;
import com.mrsuffix.singleplayersleep.listeners.AFKListener;
import com.mrsuffix.singleplayersleep.listeners.SleepListener;
import com.mrsuffix.singleplayersleep.managers.AFKManager;
import com.mrsuffix.singleplayersleep.managers.BossBarManager;
import com.mrsuffix.singleplayersleep.managers.ConfigManager;
import com.mrsuffix.singleplayersleep.managers.CooldownManager;
import com.mrsuffix.singleplayersleep.managers.StatisticsManager;
import com.mrsuffix.singleplayersleep.managers.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * SinglePlayerSleep - A modern Minecraft plugin that allows night to pass when
 * only one player sleeps
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
    private AFKManager afkManager;
    private BossBarManager bossBarManager;
    private UpdateChecker updateChecker;
    private boolean debugMode;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.statisticsManager = new StatisticsManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.afkManager = new AFKManager(this);
        this.bossBarManager = new BossBarManager(this);
        this.updateChecker = new UpdateChecker(this);

        // Load configuration
        configManager.loadConfig();
        statisticsManager.loadStatistics();

        this.debugMode = configManager.isDebugMode();

        // Register listeners
        getServer().getPluginManager().registerEvents(new SleepListener(this), this);
        getServer().getPluginManager().registerEvents(new AFKListener(this), this);

        // Register commands
        getCommand("sleep").setExecutor(new SleepCommand(this));

        // Check for updates
        updateChecker.checkForUpdates();

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
     * 
     * @return Plugin instance
     */
    public static SinglePlayerSleep getInstance() {
        return instance;
    }

    /**
     * Get the configuration manager
     * 
     * @return ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Get the statistics manager
     * 
     * @return StatisticsManager instance
     */
    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    /**
     * Get the cooldown manager
     * 
     * @return CooldownManager instance
     */
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    /**
     * Get the AFK manager
     * 
     * @return AFKManager instance
     */
    public AFKManager getAFKManager() {
        return afkManager;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    /**
     * Get the update checker
     * 
     * @return UpdateChecker instance
     */
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    /**
     * Check if debug mode is enabled
     * 
     * @return true if debug mode is on
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Log debug message if debug mode is enabled
     * 
     * @param message Message to log
     */
    public void debugLog(String message) {
        if (debugMode) {
            getLogger().log(Level.INFO, "[DEBUG] " + message);
        }
    }
}
