package com.mrsuffix.singleplayersleep.utils;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Utility class for visual and sound effects
 */
public class EffectUtil {

    /**
     * Play sleep-related effects for a player
     * @param player Player to show effects to
     */
    public static void playSleepEffects(Player player) {
        SinglePlayerSleep plugin = SinglePlayerSleep.getInstance();

        if (!plugin.getConfigManager().areParticlesEnabled() && !plugin.getConfigManager().areSoundsEnabled()) {
            return;
        }

        Location location = player.getLocation();
        World world = player.getWorld();
        
        // Calculate particle count based on optimization settings
        int particleCount = calculateParticleCount(world);

        // Play sleep sound
        if (plugin.getConfigManager().areSoundsEnabled()) {
            world.playSound(location, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
            plugin.debugLog("Played sleep sound for " + player.getName());
        }

        // Show sleep particles
        if (plugin.getConfigManager().areParticlesEnabled()) {
            new BukkitRunnable() {
                int count = 0;

                @Override
                public void run() {
                    if (count >= 20) { // Show particles for 1 second (20 ticks)
                        cancel();
                        return;
                    }

                    // Create dreamy particle effect around the player
                    Location particleLocation = location.clone().add(
                            (Math.random() - 0.5) * 3,
                            Math.random() * 2 + 1,
                            (Math.random() - 0.5) * 3
                    );

                    world.spawnParticle(Particle.CLOUD, particleLocation, Math.max(1, particleCount / 2), 0.1, 0.1, 0.1, 0.01);
                    world.spawnParticle(Particle.ENCHANTMENT_TABLE, particleLocation, particleCount, 0.2, 0.2, 0.2, 0.1);

                    count++;
                }
            }.runTaskTimer(plugin, 0L, 1L);

            plugin.debugLog("Started sleep particle effects for " + player.getName());
        }
    }

    /**
     * Play morning-related effects for a player
     * @param player Player to show effects to
     */
    public static void playMorningEffects(Player player) {
        SinglePlayerSleep plugin = SinglePlayerSleep.getInstance();

        if (!plugin.getConfigManager().areParticlesEnabled() && !plugin.getConfigManager().areSoundsEnabled()) {
            return;
        }

        Location location = player.getLocation();
        World world = player.getWorld();
        
        // Calculate particle count based on optimization settings
        int particleCount = calculateParticleCount(world);

        // Play morning sound (rooster crow simulation)
        if (plugin.getConfigManager().areSoundsEnabled()) {
            world.playSound(location, Sound.ENTITY_CHICKEN_AMBIENT, 1.0f, 0.8f);

            // Play a bell sound for "good morning"
            new BukkitRunnable() {
                @Override
                public void run() {
                    world.playSound(location, Sound.BLOCK_NOTE_BLOCK_BELL, 0.7f, 1.2f);
                }
            }.runTaskLater(plugin, 10L);

            plugin.debugLog("Played morning sounds for " + player.getName());
        }

        // Show morning particles
        if (plugin.getConfigManager().areParticlesEnabled()) {
            new BukkitRunnable() {
                int count = 0;

                @Override
                public void run() {
                    if (count >= 30) { // Show particles for 1.5 seconds
                        cancel();
                        return;
                    }

                    // Create sunrise effect with golden particles
                    Location particleLocation = location.clone().add(
                            (Math.random() - 0.5) * 4,
                            Math.random() * 3 + 1,
                            (Math.random() - 0.5) * 4
                    );

                    world.spawnParticle(Particle.FLAME, particleLocation, Math.max(1, particleCount / 2), 0.1, 0.1, 0.1, 0.01);
                    world.spawnParticle(Particle.VILLAGER_HAPPY, particleLocation, particleCount, 0.3, 0.3, 0.3, 0.1);

                    // Occasional firework-like effect
                    if (count % 10 == 0) {
                        world.spawnParticle(Particle.FIREWORKS_SPARK,
                                location.clone().add(0, 3, 0), 5, 1, 1, 1, 0.1);
                    }

                    count++;
                }
            }.runTaskTimer(plugin, 0L, 1L);

            plugin.debugLog("Started morning particle effects for " + player.getName());
        }
    }

    /**
     * Create a visual countdown effect
     * @param player Player to show countdown to
     * @param seconds Number of seconds to countdown
     */
    public static void showCountdown(Player player, int seconds) {
        SinglePlayerSleep plugin = SinglePlayerSleep.getInstance();

        new BukkitRunnable() {
            int remaining = seconds;

            @Override
            public void run() {
                if (remaining <= 0) {
                    cancel();
                    return;
                }

                // Show countdown in action bar
                player.sendActionBar(ChatColor.YELLOW + "Night skip in: " + ChatColor.GOLD + remaining + "s");

                // Play tick sound
                if (plugin.getConfigManager().areSoundsEnabled()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.0f + (remaining * 0.1f));
                }

                remaining--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    /**
     * Calculate optimal particle count based on player count and optimization settings
     * @param world World to check
     * @return Optimized particle count
     */
    private static int calculateParticleCount(World world) {
        SinglePlayerSleep plugin = SinglePlayerSleep.getInstance();
        
        if (!plugin.getConfigManager().isParticleOptimizeEnabled()) {
            return 2; // Default particle count
        }
        
        int playerCount = world.getPlayers().size();
        int maxPerPlayer = plugin.getConfigManager().getMaxParticlesPerPlayer();
        
        // Reduce particles as player count increases
        if (playerCount <= 1) {
            return maxPerPlayer;
        } else if (playerCount <= 5) {
            return Math.max(2, maxPerPlayer / 2);
        } else if (playerCount <= 10) {
            return Math.max(1, maxPerPlayer / 3);
        } else {
            return 1; // Minimum particles for large servers
        }
    }
}
