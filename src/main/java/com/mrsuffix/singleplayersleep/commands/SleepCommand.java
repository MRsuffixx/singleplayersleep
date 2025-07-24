package com.mrsuffix.singleplayersleep.commands;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import com.mrsuffix.singleplayersleep.utils.MessageUtil;
import com.mrsuffix.singleplayersleep.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles sleep-related commands
 */
public class SleepCommand implements CommandExecutor, TabCompleter {

    private final SinglePlayerSleep plugin;

    public SleepCommand(SinglePlayerSleep plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "skip":
                return handleSkip(sender, args);
            case "stats":
                return handleStats(sender);
            case "reload":
                return handleReload(sender);
            case "debug":
                return handleDebug(sender, args);
            case "cooldown":
                return handleCooldown(sender, args);
            case "help":
            default:
                showHelp(sender);
                return true;
        }
    }

    /**
     * Handle manual night skip command
     */
    private boolean handleSkip(CommandSender sender, String[] args) {
        if (!sender.hasPermission("singleplayersleep.skip")) {
            MessageUtil.sendMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        World world;
        if (args.length > 1) {
            world = Bukkit.getWorld(args[1]);
            if (world == null) {
                MessageUtil.sendMessage(sender, "&cWorld '" + args[1] + "' not found!");
                return true;
            }
        } else if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            MessageUtil.sendMessage(sender, "&cYou must specify a world name when using from console!");
            return true;
        }

        if (!TimeUtil.isNight(world)) {
            MessageUtil.sendMessage(sender, "&cIt's not night time in world " + world.getName() + "!");
            return true;
        }

        // Skip night
        world.setTime(TimeUtil.MORNING_TIME);
        if (plugin.getConfigManager().shouldClearWeather()) {
            world.setStorm(false);
            world.setThundering(false);
        }

        MessageUtil.broadcastMessage("&6" + sender.getName() + " manually skipped the night!");
        plugin.getStatisticsManager().incrementManualSkips();

        return true;
    }

    /**
     * Handle statistics command
     */
    private boolean handleStats(CommandSender sender) {
        if (!sender.hasPermission("singleplayersleep.stats")) {
            MessageUtil.sendMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        MessageUtil.sendMessage(sender, "&6=== Sleep Statistics ===");
        MessageUtil.sendMessage(sender, "&eSleep Events: &f" + plugin.getStatisticsManager().getTotalSleepCount());
        MessageUtil.sendMessage(sender, "&eManual Skips: &f" + plugin.getStatisticsManager().getManualSkips());
        MessageUtil.sendMessage(sender, "&eTop Sleepers:");

        plugin.getStatisticsManager().getTopSleepers(5).forEach((player, count) ->
                MessageUtil.sendMessage(sender, "&7- &f" + player + "&7: &e" + count + " times"));

        return true;
    }

    /**
     * Handle config reload command
     */
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("singleplayersleep.reload")) {
            MessageUtil.sendMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        plugin.getConfigManager().loadConfig();
        MessageUtil.sendMessage(sender, "&aConfiguration reloaded successfully!");
        return true;
    }

    /**
     * Handle debug toggle command
     */
    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("singleplayersleep.debug")) {
            MessageUtil.sendMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        boolean newState = !plugin.isDebugMode();
        plugin.getConfigManager().setDebugMode(newState);

        MessageUtil.sendMessage(sender, "&aDebug mode " + (newState ? "enabled" : "disabled") + "!");
        return true;
    }

    /**
     * Handle cooldown management
     */
    private boolean handleCooldown(CommandSender sender, String[] args) {
        if (!sender.hasPermission("singleplayersleep.cooldown")) {
            MessageUtil.sendMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 2) {
            MessageUtil.sendMessage(sender, "&cUsage: /sleep cooldown <clear|check> [world]");
            return true;
        }

        String action = args[1].toLowerCase();
        String worldName = args.length > 2 ? args[2] : (sender instanceof Player ? ((Player) sender).getWorld().getName() : null);

        if (worldName == null) {
            MessageUtil.sendMessage(sender, "&cYou must specify a world name when using from console!");
            return true;
        }

        switch (action) {
            case "clear":
                plugin.getCooldownManager().clearCooldown(worldName);
                MessageUtil.sendMessage(sender, "&aCooldown cleared for world " + worldName + "!");
                break;
            case "check":
                if (plugin.getCooldownManager().isOnCooldown(worldName)) {
                    long remaining = plugin.getCooldownManager().getRemainingCooldown(worldName);
                    MessageUtil.sendMessage(sender, "&eWorld " + worldName + " is on cooldown for " + (remaining / 1000) + " more seconds.");
                } else {
                    MessageUtil.sendMessage(sender, "&aWorld " + worldName + " is not on cooldown.");
                }
                break;
            default:
                MessageUtil.sendMessage(sender, "&cInvalid action. Use 'clear' or 'check'.");
        }

        return true;
    }

    /**
     * Show command help
     */
    private void showHelp(CommandSender sender) {
        MessageUtil.sendMessage(sender, "&6=== SinglePlayerSleep Commands ===");
        MessageUtil.sendMessage(sender, "&e/sleep skip [world] &7- Manually skip night");
        MessageUtil.sendMessage(sender, "&e/sleep stats &7- View sleep statistics");
        MessageUtil.sendMessage(sender, "&e/sleep reload &7- Reload configuration");
        MessageUtil.sendMessage(sender, "&e/sleep debug &7- Toggle debug mode");
        MessageUtil.sendMessage(sender, "&e/sleep cooldown <clear|check> [world] &7- Manage cooldowns");
        MessageUtil.sendMessage(sender, "&e/sleep help &7- Show this help");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subcommands = Arrays.asList("skip", "stats", "reload", "debug", "cooldown", "help");
            for (String sub : subcommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("skip")) {
                // Add world names
                Bukkit.getWorlds().forEach(world -> completions.add(world.getName()));
            } else if (args[0].equalsIgnoreCase("cooldown")) {
                completions.addAll(Arrays.asList("clear", "check"));
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("cooldown")) {
            // Add world names for cooldown command
            Bukkit.getWorlds().forEach(world -> completions.add(world.getName()));
        }

        return completions;
    }
}
