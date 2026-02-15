package com.mrsuffix.singleplayersleep.listeners;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import com.mrsuffix.singleplayersleep.tasks.SleepTask;
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
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles player sleep events and manages night skipping logic
 */
public class SleepListener implements Listener {

    private final SinglePlayerSleep plugin;
    // Track active sleep tasks (waiting for night skip)
    private final Map<String, BukkitTask> pendingTasks = new HashMap<>();

    // Track active smooth sleep animations
    private final Map<String, BukkitTask> activeAnimations = new HashMap<>();

    public SleepListener(SinglePlayerSleep plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        if (event.isCancelled() || event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Player player = event.getPlayer();
        World world = player.getWorld();

        if (!plugin.getConfigManager().isWorldEnabled(world.getName())) {
            return;
        }

        // Delay processing slightly to ensure player is actually in bed
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isSleeping())
                    return;
                processSleep(player, world);
            }
        }.runTaskLater(plugin, 1L);
    }

    private void processSleep(Player player, World world) {
        String worldName = world.getName();

        // Initial BossBar Update
        updateBossBars(world);

        if (plugin.getConfigManager().isPercentageMode()) {
            checkPercentageRequirement(world);
        } else {
            // Single player mode
            if (pendingTasks.containsKey(worldName) || activeAnimations.containsKey(worldName)) {
                return; // Already processing
            }

            // Only log if enabled
            if (plugin.getConfigManager().isLogSleepEventsEnabled()) {
                plugin.getLogger().info("Starting sleep process for " + player.getName());
            }

            // Broadcast sleep message
            String sleepMessage = plugin.getConfigManager().getMessage("player-sleeping")
                    .replace("{player}", player.getName());

            // Send to online players directly to avoid console logging
            for (Player p : Bukkit.getOnlinePlayers()) {
                MessageUtil.sendMessage(p, sleepMessage);
            }

            EffectUtil.playSleepEffects(player);

            // Start BossBar Countdown
            long delay = plugin.getConfigManager().getSleepDelay();
            final long startTime = System.currentTimeMillis();

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    long elapsed = System.currentTimeMillis() - startTime;
                    float progress = (float) elapsed / (delay * 50.0f); // 50ms per tick

                    if (progress >= 1.0f) {
                        skipNight(world);
                        pendingTasks.remove(worldName);
                        this.cancel();
                    } else {
                        // Update BossBar with timer percentage
                        plugin.getBossBarManager().updateAllProgress(progress, null);
                    }
                }
            }.runTaskTimer(plugin, 1L, 1L);

            pendingTasks.put(worldName, task);
        }
    }

    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent event) {
        handleSleepCancel(event.getPlayer(), event.getPlayer().getWorld());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        handleSleepCancel(event.getPlayer(), event.getPlayer().getWorld());
    }

    private void handleSleepCancel(Player player, World world) {
        String worldName = world.getName();

        new BukkitRunnable() {
            @Override
            public void run() {
                updateBossBars(world);

                // If single player mode and task is pending, check if anyone else is sleeping
                if (!plugin.getConfigManager().isPercentageMode()) {
                    boolean anyoneSleeping = world.getPlayers().stream().anyMatch(Player::isSleeping);

                    if (!anyoneSleeping && pendingTasks.containsKey(worldName)) {
                        // Cancel the task!
                        pendingTasks.get(worldName).cancel();
                        pendingTasks.remove(worldName);

                        String msg = plugin.getConfigManager().getMessage("player-woke-up")
                                .replace("{player}", player.getName());
                        // Send to online players
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            MessageUtil.sendMessage(p, msg);
                        }

                        plugin.getBossBarManager().removeAllBossBars();
                    }
                }
            }
        }.runTaskLater(plugin, 2L);
    }

    private void checkPercentageRequirement(World world) {
        long totalPlayers = world.getPlayers().stream()
                .filter(p -> p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)
                .filter(p -> !plugin.getConfigManager().shouldIgnoreAFKPlayers() || !plugin.getAFKManager().isAFK(p))
                .count();

        long sleepingPlayers = world.getPlayers().stream()
                .filter(Player::isSleeping)
                .count();

        int requiredPercentage = plugin.getConfigManager().getSleepPercentage();
        int requiredPlayers = (int) Math.ceil((totalPlayers * requiredPercentage) / 100.0);

        if (sleepingPlayers >= requiredPlayers) {
            String worldName = world.getName();
            if (pendingTasks.containsKey(worldName) || activeAnimations.containsKey(worldName))
                return;

            // Broadcast & Skip
            String sleepMessage = plugin.getConfigManager().getMessage("player-sleeping")
                    .replace("{player}", sleepingPlayers + " players");

            for (Player p : Bukkit.getOnlinePlayers()) {
                MessageUtil.sendMessage(p, sleepMessage);
            }

            // For percentage mode, we can also use countdown, or keep it simple.
            // Let's use countdown for consistency if delay > 0
            long delay = plugin.getConfigManager().getSleepDelay();
            final long startTime = System.currentTimeMillis();

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    long elapsed = System.currentTimeMillis() - startTime;
                    float progress = (float) elapsed / (delay * 50.0f);

                    if (progress >= 1.0f) {
                        skipNight(world);
                        pendingTasks.remove(worldName);
                        this.cancel();
                    } else {
                        plugin.getBossBarManager().updateAllProgress(progress, null);
                    }
                }
            }.runTaskTimer(plugin, 1L, 1L);

            pendingTasks.put(worldName, task);
        }
    }

    private void skipNight(World world) {
        if (plugin.getConfigManager().isSmoothSleepEnabled()) {
            // Smooth Sleep
            if (activeAnimations.containsKey(world.getName())) {
                activeAnimations.get(world.getName()).cancel();
            }

            SleepTask animation = new SleepTask(plugin, world, () -> {
                finishNightSkip(world);
                activeAnimations.remove(world.getName());
            });

            BukkitTask task = animation.runTaskTimer(plugin, 1L, 1L);
            activeAnimations.put(world.getName(), task);

        } else {
            // Instant Skip
            world.setTime(TimeUtil.MORNING_TIME);
            finishNightSkip(world);
        }
    }

    private void finishNightSkip(World world) {
        if (plugin.getConfigManager().shouldClearWeather()) {
            world.setStorm(false);
            world.setThundering(false);
        }

        String goodMorning = plugin.getConfigManager().getMessage("good-morning");
        for (Player p : Bukkit.getOnlinePlayers()) {
            MessageUtil.sendMessage(p, goodMorning);
        }

        plugin.getBossBarManager().removeAllBossBars();

        plugin.getCooldownManager().setCooldown(world.getName());

        if (plugin.getConfigManager().isAutoSaveEnabled()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
                }
            }.runTaskLater(plugin, plugin.getConfigManager().getAutoSaveDelay());
        }
    }

    private void updateBossBars(World world) {
        if (!plugin.getConfigManager().isBossBarEnabled())
            return;

        if (pendingTasks.containsKey(world.getName())) {
            return;
        }

        long totalPlayers = world.getPlayers().stream()
                .filter(p -> p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)
                .filter(p -> !plugin.getConfigManager().shouldIgnoreAFKPlayers() || !plugin.getAFKManager().isAFK(p))
                .count();

        int sleeping = (int) world.getPlayers().stream().filter(Player::isSleeping).count();
        int percentage = (int) ((sleeping * 100.0) / Math.max(1, totalPlayers));

        for (Player p : world.getPlayers()) {
            plugin.getBossBarManager().showBossBar(p, percentage, (int) totalPlayers, sleeping);
        }

        if (sleeping == 0 && !activeAnimations.containsKey(world.getName())) {
            plugin.getBossBarManager().removeAllBossBars();
        }
    }
}
