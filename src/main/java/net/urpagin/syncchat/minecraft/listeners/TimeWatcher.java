package net.urpagin.syncchat.minecraft.listeners;

import net.urpagin.syncchat.DiscordInterface;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeWatcher extends BukkitRunnable {
    private static World world;
    private boolean wasDay;

    public TimeWatcher(World world) {
        TimeWatcher.world = world;
        this.wasDay = isDaytime(world.getTime());
    }

    private static boolean isDaytime(long time) {
        return time >= 0 && time < 12300;
    }

    public static boolean isDay() {
        return isDaytime(world.getTime());
    }

    @Override
    public void run() {
        boolean isDay = isDaytime(world.getTime());

        if (isDay == wasDay)
            return;
        wasDay = isDay;

        DiscordInterface.updateActivityPlaying();
    }
}