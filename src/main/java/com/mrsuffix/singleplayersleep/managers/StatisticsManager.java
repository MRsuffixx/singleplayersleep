package com.mrsuffix.singleplayersleep.managers;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages plugin statistics
 */
public class StatisticsManager {

    private final SinglePlayerSleep plugin;
    private File statisticsFile;
    private FileConfiguration statisticsConfig;

    private long totalSleepCount = 0;
    private long manualSkips = 0;
    private final Map<String, Integer> playerSleepCounts = new HashMap<>();

    public StatisticsManager(SinglePlayerSleep plugin) {
        this.plugin = plugin;
        this.statisticsFile = new File(plugin.getDataFolder(), "statistics.yml");
    }

    /**
     * Load statistics from file
     */
    public void loadStatistics() {
        if (!statisticsFile.exists()) {
            plugin.saveResource("statistics.yml", false);
        }

        statisticsConfig = YamlConfiguration.loadConfiguration(statisticsFile);

        totalSleepCount = statisticsConfig.getLong("total-sleep-count", 0);
        manualSkips = statisticsConfig.getLong("manual-skips", 0);

        if (statisticsConfig.contains("player-sleep-counts")) {
            var section = statisticsConfig.getConfigurationSection("player-sleep-counts");
            if (section != null) {
                for (String player : section.getKeys(false)) {
                    playerSleepCounts.put(player, statisticsConfig.getInt("player-sleep-counts." + player));
                }
            }
        }

        plugin.debugLog("Statistics loaded - Total sleeps: " + totalSleepCount + ", Manual skips: " + manualSkips);
    }

    /**
     * Save statistics to file
     */
    public void saveStatistics() {
        statisticsConfig.set("total-sleep-count", totalSleepCount);
        statisticsConfig.set("manual-skips", manualSkips);
        statisticsConfig.set("last-updated", System.currentTimeMillis());

        // Clear existing player data
        statisticsConfig.set("player-sleep-counts", null);

        // Save player sleep counts
        for (Map.Entry<String, Integer> entry : playerSleepCounts.entrySet()) {
            statisticsConfig.set("player-sleep-counts." + entry.getKey(), entry.getValue());
        }

        try {
            statisticsConfig.save(statisticsFile);
            plugin.debugLog("Statistics saved");
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save statistics file: " + e.getMessage());
        }
    }

    /**
     * Increment total sleep count
     */
    public void incrementSleepCount() {
        totalSleepCount++;
        plugin.debugLog("Sleep count incremented to: " + totalSleepCount);
    }

    /**
     * Increment manual skip count
     */
    public void incrementManualSkips() {
        manualSkips++;
        plugin.debugLog("Manual skip count incremented to: " + manualSkips);
    }

    /**
     * Add a sleep event for a player
     * @param playerName Player name
     */
    public void addPlayerSleep(String playerName) {
        playerSleepCounts.merge(playerName, 1, Integer::sum);
        plugin.debugLog("Player " + playerName + " sleep count: " + playerSleepCounts.get(playerName));
    }

    /**
     * Get total sleep count
     * @return Total sleep events
     */
    public long getTotalSleepCount() {
        return totalSleepCount;
    }

    /**
     * Get manual skip count
     * @return Total manual skips
     */
    public long getManualSkips() {
        return manualSkips;
    }

    /**
     * Get sleep count for a specific player
     * @param playerName Player name
     * @return Sleep count for player
     */
    public int getPlayerSleepCount(String playerName) {
        return playerSleepCounts.getOrDefault(playerName, 0);
    }

    /**
     * Get top sleepers
     * @param limit Number of top players to return
     * @return Map of player names to sleep counts
     */
    public LinkedHashMap<String, Integer> getTopSleepers(int limit) {
        return playerSleepCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
