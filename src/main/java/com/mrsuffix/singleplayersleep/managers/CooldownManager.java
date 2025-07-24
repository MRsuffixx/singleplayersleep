package com.mrsuffix.singleplayersleep.managers;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages cooldowns for sleep functionality
 */
public class CooldownManager {

    private final SinglePlayerSleep plugin;
    private final Map<String, Long> worldCooldowns = new HashMap<>();

    public CooldownManager(SinglePlayerSleep plugin) {
        this.plugin = plugin;
    }

    /**
     * Set cooldown for a world
     * @param worldName World name
     */
    public void setCooldown(String worldName) {
        long cooldownDuration = plugin.getConfigManager().getCooldownDuration() * 1000; // Convert to milliseconds
        long cooldownEnd = System.currentTimeMillis() + cooldownDuration;
        worldCooldowns.put(worldName, cooldownEnd);

        plugin.debugLog("Cooldown set for world " + worldName + " until " + cooldownEnd);
    }

    /**
     * Check if a world is on cooldown
     * @param worldName World name
     * @return true if on cooldown
     */
    public boolean isOnCooldown(String worldName) {
        Long cooldownEnd = worldCooldowns.get(worldName);
        if (cooldownEnd == null) {
            return false;
        }

        if (System.currentTimeMillis() >= cooldownEnd) {
            worldCooldowns.remove(worldName);
            plugin.debugLog("Cooldown expired for world " + worldName);
            return false;
        }

        return true;
    }

    /**
     * Get remaining cooldown time in milliseconds
     * @param worldName World name
     * @return Remaining time in milliseconds, 0 if not on cooldown
     */
    public long getRemainingCooldown(String worldName) {
        Long cooldownEnd = worldCooldowns.get(worldName);
        if (cooldownEnd == null) {
            return 0;
        }

        long remaining = cooldownEnd - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * Clear cooldown for a world
     * @param worldName World name
     */
    public void clearCooldown(String worldName) {
        worldCooldowns.remove(worldName);
        plugin.debugLog("Cooldown cleared for world " + worldName);
    }

    /**
     * Clear all cooldowns
     */
    public void clearAllCooldowns() {
        worldCooldowns.clear();
        plugin.debugLog("All cooldowns cleared");
    }

    /**
     * Get all worlds currently on cooldown
     * @return Map of world names to cooldown end times
     */
    public Map<String, Long> getAllCooldowns() {
        // Remove expired cooldowns before returning
        worldCooldowns.entrySet().removeIf(entry ->
                System.currentTimeMillis() >= entry.getValue());

        return new HashMap<>(worldCooldowns);
    }
}

