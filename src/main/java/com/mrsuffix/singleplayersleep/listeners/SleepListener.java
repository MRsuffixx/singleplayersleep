package com.mrsuffix.singleplayersleep.listeners;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import com.mrsuffix.singleplayersleep.utils.EffectUtil;
import com.mrsuffix.singleplayersleep.utils.MessageUtil;
import com.mrsuffix.singleplayersleep.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles player sleep events and manages night skipping logic
 */
public class SleepListener implements Listener {

    private final SinglePlayerSleep plugin;
    private final Set<String> processingSleep = new HashSet<>();

    public SleepListener(SinglePlayerSleep plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        plugin.debugLog("Player " + player.getName() + " attempted to sleep in world " + world.getName());

        // Check if event was cancelled
        if (event.isCancelled()) {
            plugin.debugLog("Sleep event was cancelled for " + player.getName());
            return;
        }

        // Check if sleep functionality is enabled for this world
        if (!plugin.getConfigManager().isWorldEnabled(world.getName())) {
            plugin.debugLog("Sleep functionality disabled for world: " + world.getName());
            return;
        }

        // Check if it's actually night time
        if (!TimeUtil.isNight(world)) {
            plugin.debugLog("Not night time in world " + world.getName() + " (time: " + world.getTime() + ")");
            return;
        }

        // Check player game mode
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            plugin.debugLog("Player " + player.getName() + " is in " + player.getGameMode() + " mode, ignoring sleep");
            return;
        }

        // Check cooldown
        if (plugin.getCooldownManager().isOnCooldown(world.getName())) {
            long remainingTime = plugin.getCooldownManager().getRemainingCooldown(world.getName());
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("cooldown-active")
                    .replace("{time}", String.valueOf(remainingTime / 1000)));
            plugin.debugLog("World " + world.getName() + " is on cooldown for " + remainingTime + "ms");
            return;
        }

        // Anti-spam protection
        String worldKey = world.getName();
        if (processingSleep.contains(worldKey)) {
            plugin.debugLog("Sleep already being processed for world " + world.getName());
            return;
        }

        // Check if percentage mode is enabled
        if (plugin.getConfigManager().isPercentageMode()) {
            handlePercentageMode(player, world);
            return;
        }

        // Start sleep process (single player mode)
        processingSleep.add(worldKey);
        plugin.debugLog("Starting sleep process for " + player.getName() + " in world " + world.getName());

        // Play sleep sound and effects
        EffectUtil.playSleepEffects(player);

        // Broadcast sleep message
        String sleepMessage = plugin.getConfigManager().getMessage("player-sleeping")
                .replace("{player}", player.getName());
        MessageUtil.broadcastMessage(sleepMessage);

        // Update statistics
        plugin.getStatisticsManager().incrementSleepCount();
        plugin.getStatisticsManager().addPlayerSleep(player.getName());

        // Schedule night skip after delay
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    skipNight(world, player);
                } finally {
                    processingSleep.remove(worldKey);
                }
            }
        }.runTaskLater(plugin, plugin.getConfigManager().getSleepDelay());
    }

    /**
     * Skip the night and handle all related effects
     * @param world The world to skip night in
     * @param sleeper The player who triggered the sleep
     */
    private void skipNight(World world, Player sleeper) {
        plugin.debugLog("Skipping night in world " + world.getName());

        // Set time to morning
        world.setTime(TimeUtil.MORNING_TIME);

        // Clear weather if enabled
        if (plugin.getConfigManager().shouldClearWeather()) {
            world.setStorm(false);
            world.setThundering(false);
            plugin.debugLog("Cleared weather in world " + world.getName());
        }

        // Play morning effects for all players in the world
        for (Player player : world.getPlayers()) {
            EffectUtil.playMorningEffects(player);
        }

        // Broadcast good morning message
        String morningMessage = plugin.getConfigManager().getMessage("good-morning");
        MessageUtil.broadcastMessage(morningMessage);

        // Set cooldown
        plugin.getCooldownManager().setCooldown(world.getName());

        // Schedule auto-save
        new BukkitRunnable() {
            @Override
            public void run() {
                performAutoSave();
            }
        }.runTaskLater(plugin, plugin.getConfigManager().getAutoSaveDelay());

        plugin.debugLog("Night skipped successfully in world " + world.getName());
    }

    /**
     * Handle percentage-based sleep mode
     * @param player Player who entered bed
     * @param world World to check
     */
    private void handlePercentageMode(Player player, World world) {
        // Get all non-AFK players in the world
        long totalPlayers = world.getPlayers().stream()
                .filter(p -> p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)
                .filter(p -> !plugin.getConfigManager().shouldIgnoreAFKPlayers() || !plugin.getAFKManager().isAFK(p))
                .count();

        // Count sleeping players
        long sleepingPlayers = world.getPlayers().stream()
                .filter(Player::isSleeping)
                .filter(p -> p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)
                .filter(p -> !plugin.getConfigManager().shouldIgnoreAFKPlayers() || !plugin.getAFKManager().isAFK(p))
                .count();

        int requiredPercentage = plugin.getConfigManager().getSleepPercentage();
        int requiredPlayers = (int) Math.ceil((totalPlayers * requiredPercentage) / 100.0);

        plugin.debugLog("Percentage mode: " + sleepingPlayers + "/" + requiredPlayers + " players sleeping (" + 
                        (sleepingPlayers * 100 / Math.max(1, totalPlayers)) + "%)");

        // Check if enough players are sleeping
        if (sleepingPlayers >= requiredPlayers) {
            String worldKey = world.getName();
            if (processingSleep.contains(worldKey)) {
                return;
            }

            processingSleep.add(worldKey);
            plugin.debugLog("Percentage threshold reached, skipping night");

            // Play effects for all sleeping players
            world.getPlayers().stream()
                    .filter(Player::isSleeping)
                    .forEach(EffectUtil::playSleepEffects);

            // Broadcast sleep message
            String sleepMessage = plugin.getConfigManager().getMessage("player-sleeping")
                    .replace("{player}", sleepingPlayers + " players");
            MessageUtil.broadcastMessage(sleepMessage);

            // Update statistics
            plugin.getStatisticsManager().incrementSleepCount();
            world.getPlayers().stream()
                    .filter(Player::isSleeping)
                    .forEach(p -> plugin.getStatisticsManager().addPlayerSleep(p.getName()));

            // Schedule night skip
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        skipNight(world, player);
                    } finally {
                        processingSleep.remove(worldKey);
                    }
                }
            }.runTaskLater(plugin, plugin.getConfigManager().getSleepDelay());
        } else {
            // Not enough players sleeping, show progress
            int currentPercentage = (int) ((sleepingPlayers * 100) / Math.max(1, totalPlayers));
            String message = plugin.getConfigManager().getMessage("not-enough-sleepers")
                    .replace("{sleeping}", String.valueOf(sleepingPlayers))
                    .replace("{required}", String.valueOf(requiredPlayers))
                    .replace("{percentage}", String.valueOf(currentPercentage));
            
            world.getPlayers().forEach(p -> MessageUtil.sendMessage(p, message));
        }
    }

    /**
     * Perform automatic world save
     */
    private void performAutoSave() {
        if (!plugin.getConfigManager().isAutoSaveEnabled()) {
            return;
        }

        plugin.debugLog("Performing auto-save");

        // Broadcast save message
        String saveMessage = plugin.getConfigManager().getMessage("auto-save");
        MessageUtil.broadcastMessage(saveMessage);

        // Execute save command
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
            plugin.debugLog("Auto-save completed");
        });
    }
}
