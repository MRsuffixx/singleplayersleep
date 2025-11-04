package com.mrsuffix.singleplayersleep.listeners;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for player activity to track AFK status
 */
public class AFKListener implements Listener {

    private final SinglePlayerSleep plugin;

    public AFKListener(SinglePlayerSleep plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Update activity when player moves
        plugin.getAFKManager().updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Update activity when player interacts
        plugin.getAFKManager().updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove player from AFK tracking when they leave
        plugin.getAFKManager().removePlayer(event.getPlayer());
    }
}
