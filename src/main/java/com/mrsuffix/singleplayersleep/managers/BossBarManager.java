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
                .replace("{sleeping}", String.valueOf(sleepingPlayers))
                .replace("{needed}", String.valueOf(totalPlayers));

        BossBar.Color color = BossBar.Color.valueOf(plugin.getConfigManager().getBossBarColor().toUpperCase());
        BossBar.Overlay style = BossBar.Overlay.valueOf(plugin.getConfigManager().getBossBarStyle().toUpperCase());

        BossBar bossBar = BossBar.bossBar(
                Component.text(MessageUtil.colorize(title)),
                (float) sleepingPlayers / totalPlayers,
                color,
                style);

        // If player already has a bossbar, remove it first (or update it)
        removeBossBar(player);

        player.showBossBar(bossBar);
        playerBossBars.put(player.getUniqueId(), bossBar);
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
