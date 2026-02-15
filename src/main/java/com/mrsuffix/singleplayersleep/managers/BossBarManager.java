package com.mrsuffix.singleplayersleep.managers;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import net.kyori.adventure.bossbar.BossBar;
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

        // Use cached Enums from ConfigManager
        BossBar.Color color = plugin.getConfigManager().getBossBarColor();
        BossBar.Overlay style = plugin.getConfigManager().getBossBarStyle();

        // Check if player already has a bossbar
        BossBar bossBar = playerBossBars.get(player.getUniqueId());

        if (bossBar == null) {
            // Create new if not exists
            bossBar = BossBar.bossBar(
                    net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand()
                            .deserialize(title),
                    (float) percentage / 100.0f,
                    color,
                    style);

            player.showBossBar(bossBar);
            playerBossBars.put(player.getUniqueId(), bossBar);
        } else {
            // Update existing
            bossBar.name(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(title));
            bossBar.progress(Math.max(0.0f, Math.min(1.0f, (float) percentage / 100.0f)));
            bossBar.color(color);
            bossBar.overlay(style);
        }
    }

    /**
     * Update progress of existing BossBars without recreating them
     * 
     * @param progress    Progress from 0.0 to 1.0
     * @param customTitle Optional custom title, null to keep existing
     */
    public void updateAllProgress(float progress, String customTitle) {
        if (!plugin.getConfigManager().isBossBarEnabled())
            return;

        net.kyori.adventure.text.Component titleComp = null;
        if (customTitle != null) {
            titleComp = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(customTitle);
        }

        for (BossBar bar : playerBossBars.values()) {
            bar.progress(Math.max(0.0f, Math.min(1.0f, progress)));
            if (titleComp != null) {
                bar.name(titleComp);
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
