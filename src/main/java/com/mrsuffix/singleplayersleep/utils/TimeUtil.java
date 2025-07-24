package com.mrsuffix.singleplayersleep.utils;

import org.bukkit.World;

/**
 * Utility class for time-related operations
 */
public class TimeUtil {

    // Minecraft time constants
    public static final long DAY_START = 0L;
    public static final long NOON = 6000L;
    public static final long SUNSET_START = 12000L;
    public static final long NIGHT_START = 13000L;
    public static final long MIDNIGHT = 18000L;
    public static final long SUNRISE_START = 23000L;
    public static final long DAY_END = 24000L;

    // Morning time to set when skipping night
    public static final long MORNING_TIME = 1000L;

    /**
     * Check if it's currently night time in a world
     * @param world World to check
     * @return true if it's night time
     */
    public static boolean isNight(World world) {
        long time = world.getTime();
        return time >= NIGHT_START && time < SUNRISE_START;
    }

    /**
     * Check if it's currently day time in a world
     * @param world World to check
     * @return true if it's day time
     */
    public static boolean isDay(World world) {
        return !isNight(world);
    }

    /**
     * Get a human-readable time description
     * @param world World to check
     * @return Time description
     */
    public static String getTimeDescription(World world) {
        long time = world.getTime();

        if (time >= DAY_START && time < NOON) {
            return "Morning";
        } else if (time >= NOON && time < SUNSET_START) {
            return "Afternoon";
        } else if (time >= SUNSET_START && time < NIGHT_START) {
            return "Evening";
        } else if (time >= NIGHT_START && time < MIDNIGHT) {
            return "Night";
        } else if (time >= MIDNIGHT && time < SUNRISE_START) {
            return "Late Night";
        } else {
            return "Dawn";
        }
    }

    /**
     * Get the percentage of night that has passed
     * @param world World to check
     * @return Percentage (0.0 to 1.0), or -1 if not night
     */
    public static double getNightProgress(World world) {
        if (!isNight(world)) {
            return -1.0;
        }

        long time = world.getTime();
        long nightDuration = SUNRISE_START - NIGHT_START;
        long nightElapsed = time - NIGHT_START;

        return (double) nightElapsed / nightDuration;
    }

    /**
     * Calculate ticks until sunrise
     * @param world World to check
     * @return Ticks until sunrise, or 0 if not night
     */
    public static long getTicksUntilSunrise(World world) {
        if (!isNight(world)) {
            return 0L;
        }

        long time = world.getTime();
        return SUNRISE_START - time;
    }

    /**
     * Format ticks into a readable time format
     * @param ticks Number of ticks
     * @return Formatted time string
     */
    public static String formatTime(long ticks) {
        long seconds = ticks / 20; // 20 ticks per second
        long minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
}
