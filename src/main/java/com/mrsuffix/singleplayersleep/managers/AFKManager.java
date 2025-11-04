package com.mrsuffix.singleplayersleep.managers;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages AFK (Away From Keyboard) player detection
 */
public class AFKManager {

    private final SinglePlayerSleep plugin;
    private final Map<UUID, Long> lastActivity = new HashMap<>();
    private final Map<UUID, Location> lastLocation = new HashMap<>();

    public AFKManager(SinglePlayerSleep plugin) {
        this.plugin = plugin;
        startAFKChecker();
    }

    /**
     * Start the AFK checker task
     */
    private void startAFKChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getConfigManager().isAFKDetectionEnabled()) {
                    return;
                }

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    checkPlayerMovement(player);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Check every second
    }

    /**
     * Check if player has moved and update their activity
     * @param player Player to check
     */
    private void checkPlayerMovement(Player player) {
        UUID uuid = player.getUniqueId();
        Location currentLocation = player.getLocation();

        Location lastLoc = lastLocation.get(uuid);
        if (lastLoc != null) {
            // Check if player moved significantly
            if (hasMoved(lastLoc, currentLocation)) {
                updateActivity(player);
            }
        } else {
            updateActivity(player);
        }

        lastLocation.put(uuid, currentLocation.clone());
    }

    /**
     * Check if player has moved significantly
     * @param from Previous location
     * @param to Current location
     * @return true if player moved
     */
    private boolean hasMoved(Location from, Location to) {
        if (!from.getWorld().equals(to.getWorld())) {
            return true;
        }

        double distance = from.distanceSquared(to);
        return distance > 0.1; // Moved more than 0.1 blocks
    }

    /**
     * Update player's last activity time
     * @param player Player to update
     */
    public void updateActivity(Player player) {
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
        plugin.debugLog("Updated activity for " + player.getName());
    }

    /**
     * Check if a player is AFK
     * @param player Player to check
     * @return true if player is AFK
     */
    public boolean isAFK(Player player) {
        if (!plugin.getConfigManager().isAFKDetectionEnabled()) {
            return false;
        }

        Long lastActive = lastActivity.get(player.getUniqueId());
        if (lastActive == null) {
            return false;
        }

        long afkTimeout = plugin.getConfigManager().getAFKTimeout() * 1000L;
        long timeSinceActive = System.currentTimeMillis() - lastActive;

        return timeSinceActive >= afkTimeout;
    }

    /**
     * Get the number of non-AFK players in a world
     * @param worldName World name
     * @return Number of active players
     */
    public int getActivePlayerCount(String worldName) {
        return (int) plugin.getServer().getWorld(worldName).getPlayers().stream()
                .filter(player -> !isAFK(player))
                .count();
    }

    /**
     * Remove player from tracking when they leave
     * @param player Player to remove
     */
    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        lastActivity.remove(uuid);
        lastLocation.remove(uuid);
        plugin.debugLog("Removed " + player.getName() + " from AFK tracking");
    }

    /**
     * Get time in seconds since player was last active
     * @param player Player to check
     * @return Seconds since last activity, or 0 if unknown
     */
    public long getTimeSinceActive(Player player) {
        Long lastActive = lastActivity.get(player.getUniqueId());
        if (lastActive == null) {
            return 0;
        }

        return (System.currentTimeMillis() - lastActive) / 1000L;
    }
}
