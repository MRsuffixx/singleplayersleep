package com.mrsuffix.singleplayersleep.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Utility class for message handling
 */
public class MessageUtil {

    /**
     * Send a colored message to a command sender
     * @param sender Command sender
     * @param message Message to send
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    /**
     * Send a colored message to a player
     * @param player Player to send message to
     * @param message Message to send
     */
    public static void sendMessage(Player player, String message) {
        player.sendMessage(colorize(message));
    }

    /**
     * Broadcast a colored message to all online players
     * @param message Message to broadcast
     */
    public static void broadcastMessage(String message) {
        Bukkit.broadcastMessage(colorize(message));
    }

    /**
     * Broadcast a colored message to all players in a specific world
     * @param worldName World name
     * @param message Message to broadcast
     */
    public static void broadcastToWorld(String worldName, String message) {
        Bukkit.getWorld(worldName).getPlayers().forEach(player ->
                sendMessage(player, message));
    }

    /**
     * Convert color codes in a message
     * @param message Message with color codes
     * @return Colored message
     */
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Strip color codes from a message
     * @param message Message with color codes
     * @return Plain text message
     */
    public static String stripColors(String message) {
        return ChatColor.stripColor(colorize(message));
    }
}
