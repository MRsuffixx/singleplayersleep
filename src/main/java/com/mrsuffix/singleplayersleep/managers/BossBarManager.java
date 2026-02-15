package com.mrsuffix.singleplayersleep.managers;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import com.mrsuffix.singleplayersleep.utils.MessageUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the BossBar display for sleeping players
 */
public class BossBarManager {

    private final SinglePlayerSleep plugin;
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();

    public BossBarManager(SinglePlayerSleep plugin) {
        this.plugin = plugin;
    }

    /**
     * Create or update BossBar for a player
     * 
     * @param player          Player to show BossBar to
     * @param percentage      Sleep percentage (0-100)
     * @param totalPlayers    Total players count
     * @param sleepingPlayers Sleeping players count
     */
    public void showBossBar(Player player, int percentage, int totalPlayers, int sleepingPlayers) {
        if (!plugin.getConfigManager().isBossBarEnabled()) {
            return;
        }

        String title = plugin.getConfigManager().getBossBarTitle()
                .replace("{player}", player.getName())
                .replace("{percentage}", String.valueOf(percentage))
                .replace("{current}", String.valueOf(sleepingPlayers))
                .replace("{required}", String.valueOf(totalPlayers));

        BossBar.Color color = parseColor(plugin.getConfigManager().getBossBarColor());
        BossBar.Overlay style = parseOverlay(plugin.getConfigManager().getBossBarStyle());

        // Create a new BossBar or reuse (ideally we should reuse, but for now let's
        // create fresh to ensure style updates apply)
        // Note: In a production environment, updating the existing instance is better
        // for performance.
        // However, to keep it simple and bug-free for this fix, we create a new one.
        BossBar bossBar = BossBar.bossBar(
                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(title),
                (float) sleepingPlayers / totalPlayers,
                color,
                style);

        // If player already has a bossbar, remove it first
        removeBossBar(player);

        player.showBossBar(bossBar);
        playerBossBars.put(player.getUniqueId(), bossBar);
    }

    private BossBar.Color parseColor(String colorName) {
        if (colorName == null)
            return BossBar.Color.BLUE;
        try {
            return BossBar.Color.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid BossBar color: " + colorName + ". Defaulting to BLUE.");
            return BossBar.Color.BLUE;
        }
    }

    private BossBar.Overlay parseOverlay(String styleName) {
        if (styleName == null)
            return BossBar.Overlay.PROGRESS;
        String normalized = styleName.toUpperCase();

        // Map Bukkit/Common names to Adventure names
        switch (normalized) {
            case "SOLID":
                return BossBar.Overlay.PROGRESS;
            case "SEGMENTED_6":
                return BossBar.Overlay.NOTCHED_6;
            case "SEGMENTED_10":
                return BossBar.Overlay.NOTCHED_10;
            case "SEGMENTED_12":
                return BossBar.Overlay.NOTCHED_12;
            case "SEGMENTED_20":
                return BossBar.Overlay.NOTCHED_20;
            default:
                try {
                    return BossBar.Overlay.valueOf(normalized);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger()
                            .warning("Invalid BossBar style: " + styleName + ". Defaulting to SOLID/PROGRESS.");
                    return BossBar.Overlay.PROGRESS;
                }
        }
    }

    /**
     * Remove BossBar from a player
     * 
     * @param player Player to remove BossBar from
     */
    public void removeBossBar(Player player) {
        BossBar bossBar = playerBossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            player.hideBossBar(bossBar);
        }
    }

    /**
     * Remove BossBars from all players
     */
    public void removeAllBossBars() {
        for (UUID uuid : playerBossBars.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.hideBossBar(playerBossBars.get(uuid));
            }
        }
        playerBossBars.clear();
    }
}
