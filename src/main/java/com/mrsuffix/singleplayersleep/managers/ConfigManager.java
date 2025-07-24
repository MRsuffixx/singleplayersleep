package com.mrsuffix.singleplayersleep.managers;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Manages plugin configuration
 */
public class ConfigManager {

    private final SinglePlayerSleep plugin;
    private FileConfiguration config;

    public ConfigManager(SinglePlayerSleep plugin) {
        this.plugin = plugin;
    }

    /**
     * Load configuration from file
     */
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        plugin.debugLog("Configuration loaded");
    }

    /**
     * Get sleep delay in ticks
     * @return Delay in ticks (default: 65)
     */
    public long getSleepDelay() {
        return config.getLong("sleep-delay-ticks", 65L);
    }

    /**
     * Get auto-save delay in ticks
     * @return Delay in ticks (default: 10)
     */
    public long getAutoSaveDelay() {
        return config.getLong("auto-save-delay-ticks", 10L);
    }

    /**
     * Check if auto-save is enabled
     * @return true if enabled
     */
    public boolean isAutoSaveEnabled() {
        return config.getBoolean("auto-save.enabled", true);
    }

    /**
     * Get cooldown duration in seconds
     * @return Cooldown in seconds
     */
    public long getCooldownDuration() {
        return config.getLong("cooldown-seconds", 30L);
    }

    /**
     * Check if weather should be cleared
     * @return true if weather should be cleared
     */
    public boolean shouldClearWeather() {
        return config.getBoolean("clear-weather", true);
    }

    /**
     * Check if debug mode is enabled
     * @return true if debug mode is on
     */
    public boolean isDebugMode() {
        return config.getBoolean("debug-mode", false);
    }

    /**
     * Set debug mode
     * @param enabled New debug mode state
     */
    public void setDebugMode(boolean enabled) {
        config.set("debug-mode", enabled);
        plugin.saveConfig();
    }

    /**
     * Check if world is enabled for sleep functionality
     * @param worldName World name to check
     * @return true if enabled
     */
    public boolean isWorldEnabled(String worldName) {
        List<String> enabledWorlds = config.getStringList("enabled-worlds");
        return enabledWorlds.isEmpty() || enabledWorlds.contains(worldName);
    }

    /**
     * Get a message from config
     * @param key Message key
     * @return Formatted message
     */
    public String getMessage(String key) {
        return config.getString("messages." + key, "&cMessage not found: " + key);
    }

    /**
     * Check if particles are enabled
     * @return true if particles should be shown
     */
    public boolean areParticlesEnabled() {
        return config.getBoolean("effects.particles.enabled", true);
    }

    /**
     * Check if sounds are enabled
     * @return true if sounds should be played
     */
    public boolean areSoundsEnabled() {
        return config.getBoolean("effects.sounds.enabled", true);
    }
}
