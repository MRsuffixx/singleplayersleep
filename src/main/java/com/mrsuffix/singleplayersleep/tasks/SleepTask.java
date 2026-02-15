package com.mrsuffix.singleplayersleep.tasks;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import com.mrsuffix.singleplayersleep.utils.TimeUtil;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task to handle smooth night skipping (time acceleration)
 */
public class SleepTask extends BukkitRunnable {

    private final SinglePlayerSleep plugin;
    private final World world;
    private final Runnable onComplete;

    public SleepTask(SinglePlayerSleep plugin, World world, Runnable onComplete) {
        this.plugin = plugin;
        this.world = world;
        this.onComplete = onComplete;
    }

    @Override
    public void run() {
        if (!TimeUtil.isNight(world)) {
            // Already day
            complete();
            return;
        }

        long speed = plugin.getConfigManager().getSmoothSleepSpeed();
        long currentTime = world.getTime();

        // Accelerate time
        long newTime = currentTime + speed;

        // Check if we reached morning (wrap around check handled by Bukkit usually, but
        // logic here assumes < 24000 loop)
        // If we crossed into day (0 - 1000 roughly)
        if (newTime >= 24000) {
            newTime -= 24000;
        }

        world.setTime(newTime);

        // Check if it's morning now (approx check)
        if (TimeUtil.isDay(world)) {
            complete();
        }
    }

    private void complete() {
        this.cancel();
        if (onComplete != null) {
            onComplete.run();
        }
    }
}
