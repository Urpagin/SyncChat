package net.urpagin.syncchat;

import net.urpagin.syncchat.exceptions.InvalidConfigException;
import net.urpagin.syncchat.minecraft.listeners.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SyncChat extends JavaPlugin implements Listener {

    public static DiscordInterface discord;

    private static String get_delta_rounded(long start, long end) {
        // Calculate duration in seconds
        double deltaTimeSeconds = (end - start) / 1_000_000_000.0;  // Convert nanoseconds to seconds
        String format = "%." + 1 + "f";
        return String.format(format, deltaTimeSeconds);
    }

    @Override
    public void onEnable() {
        long startTime = System.nanoTime();

        checkVersion();

        // Try to read in-memory the config.yml and check for values.
        // If there is a problem, we quit the plugin.
        try {
            new ReadConfig(this); // Initialize the class for static use.
        } catch (InvalidConfigException e) {
            this.getLogger().severe(e.getMessage());
            this.getLogger().severe("Config error. Plugin stopped.");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        // Register event listeners
        getServer().getPluginManager().registerEvents(new MinecraftChatListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftPlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftPlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftPlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftPlayerConnectionTracker(), this);
        new TimeWatcher(this.getServer().getWorlds().getFirst()).runTaskTimer(this, 0L, 80L); // Every 4 seconds

        discord = new DiscordInterface(this, ReadConfig.getBotToken(), ReadConfig.getDiscordChannelId());

        long endTime = System.nanoTime();

        getLogger().info("Plugin successfully loaded in " + get_delta_rounded(startTime, endTime) + "s!");
    }


    private void checkVersion() {
        String currentVersion = getDescription().getVersion();
        VersionCheck version = new VersionCheck(currentVersion);

        try {
            if (!version.isUpToDate()) {
                String latestVersion = version.getLatestVersion();
                this.getLogger().warning("Newer version available! Current is v" + currentVersion + ", latest is " + latestVersion);
            }
        } catch (Exception e) {
            this.getLogger().warning("Failed to fetch the latest version");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
