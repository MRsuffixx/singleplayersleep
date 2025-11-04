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
    
    // Cache for frequently accessed config values
    private long sleepDelay;
    private long autoSaveDelay;
    private boolean autoSaveEnabled;
    private long cooldownDuration;
    private boolean clearWeather;
    private boolean debugMode;
    private boolean particlesEnabled;
    private boolean soundsEnabled;
    private boolean percentageMode;
    private int sleepPercentage;
    private boolean afkDetectionEnabled;
    private long afkTimeout;
    private boolean ignoreAFKPlayers;
    private boolean particleOptimize;
    private int maxParticlesPerPlayer;
    private boolean updateCheckerEnabled;
    private String githubRepo;

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
        
        // Cache all config values
        cacheConfigValues();

        plugin.debugLog("Configuration loaded and cached");
    }
    
    /**
     * Cache all config values for better performance
     */
    private void cacheConfigValues() {
        sleepDelay = config.getLong("sleep-delay-ticks", 65L);
        autoSaveDelay = config.getLong("auto-save.delay-ticks", 10L);
        autoSaveEnabled = config.getBoolean("auto-save.enabled", true);
        cooldownDuration = config.getLong("cooldown-seconds", 30L);
        clearWeather = config.getBoolean("clear-weather", true);
        debugMode = config.getBoolean("debug-mode", false);
        particlesEnabled = config.getBoolean("effects.particles.enabled", true);
        soundsEnabled = config.getBoolean("effects.sounds.enabled", true);
        percentageMode = config.getBoolean("percentage-mode", false);
        sleepPercentage = config.getInt("sleep-percentage", 50);
        afkDetectionEnabled = config.getBoolean("afk-detection.enabled", true);
        afkTimeout = config.getLong("afk-detection.timeout-seconds", 300L);
        ignoreAFKPlayers = config.getBoolean("afk-detection.ignore-afk-players", true);
        particleOptimize = config.getBoolean("effects.particles.optimize", true);
        maxParticlesPerPlayer = config.getInt("effects.particles.max-per-player", 10);
        updateCheckerEnabled = config.getBoolean("update-checker.enabled", true);
        githubRepo = config.getString("update-checker.github-repo", "MRsuffixx/SinglePlayerSleep");
    }

    /**
     * Get sleep delay in ticks
     * @return Delay in ticks (default: 65)
     */
    public long getSleepDelay() {
        return sleepDelay;
    }

    /**
     * Get auto-save delay in ticks
     * @return Delay in ticks (default: 10)
     */
    public long getAutoSaveDelay() {
        return autoSaveDelay;
    }

    /**
     * Check if auto-save is enabled
     * @return true if enabled
     */
    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }

    /**
     * Get cooldown duration in seconds
     * @return Cooldown in seconds
     */
    public long getCooldownDuration() {
        return cooldownDuration;
    }

    /**
     * Check if weather should be cleared
     * @return true if weather should be cleared
     */
    public boolean shouldClearWeather() {
        return clearWeather;
    }

    /**
     * Check if debug mode is enabled
     * @return true if debug mode is on
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Set debug mode
     * @param enabled New debug mode state
     */
    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
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
        return particlesEnabled;
    }

    /**
     * Check if sounds are enabled
     * @return true if sounds should be played
     */
    public boolean areSoundsEnabled() {
        return soundsEnabled;
    }
    
    /**
     * Check if percentage mode is enabled
     * @return true if percentage mode is on
     */
    public boolean isPercentageMode() {
        return percentageMode;
    }
    
    /**
     * Get required sleep percentage
     * @return Percentage of players required to sleep
     */
    public int getSleepPercentage() {
        return sleepPercentage;
    }
    
    /**
     * Check if AFK detection is enabled
     * @return true if AFK detection is on
     */
    public boolean isAFKDetectionEnabled() {
        return afkDetectionEnabled;
    }
    
    /**
     * Get AFK timeout in seconds
     * @return AFK timeout
     */
    public long getAFKTimeout() {
        return afkTimeout;
    }
    
    /**
     * Check if AFK players should be ignored
     * @return true if AFK players are ignored
     */
    public boolean shouldIgnoreAFKPlayers() {
        return ignoreAFKPlayers;
    }
    
    /**
     * Check if particle optimization is enabled
     * @return true if particles should be optimized
     */
    public boolean isParticleOptimizeEnabled() {
        return particleOptimize;
    }
    
    /**
     * Get maximum particles per player
     * @return Max particles per player
     */
    public int getMaxParticlesPerPlayer() {
        return maxParticlesPerPlayer;
    }
    
    /**
     * Check if update checker is enabled
     * @return true if update checker is on
     */
    public boolean isUpdateCheckerEnabled() {
        return updateCheckerEnabled;
    }
    
    /**
     * Get GitHub repository for update checking
     * @return GitHub repo in format "owner/repo"
     */
    public String getGitHubRepo() {
        return githubRepo;
    }
}
