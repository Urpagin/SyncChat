package net.urpagin.syncchat;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ReadConfig {
    private static final String DEFAULT_TOKEN = "your_discord_bot_token_here";
    private static final long DEFAULT_CHANNEL_ID = -1;

    private final JavaPlugin plugin;

    private static String botToken;
    private static long channelId;
    private static List<String> minecraftChatPrefixes;

    public ReadConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadConfig();
    }

    private void loadConfig() {
        botToken = plugin.getConfig().getString("discord_bot_token", DEFAULT_TOKEN);
        channelId = plugin.getConfig().getLong("discord_channel_id", DEFAULT_CHANNEL_ID);
        minecraftChatPrefixes = plugin.getConfig().getStringList("minecraft_chat_prefixes");

        boolean configInvalid = false;

        if (DEFAULT_TOKEN.equals(botToken)) {
            plugin.getLogger().severe("Please set 'discord_bot_token' in the plugin's 'config.yml'");
            configInvalid = true;
        }

        if (channelId == DEFAULT_CHANNEL_ID) {
            plugin.getLogger().severe("Please set 'discord_channel_id' in the plugin's 'config.yml'");
            configInvalid = true;
        }

        if (configInvalid) disablePlugin();

    }

    private void disablePlugin() {
        plugin.getLogger().severe("Error in the plugin's config. Disabling plugin.");
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }

    public static String getBotToken() {
        return botToken;
    }

    public static long getDiscordChannelId() {
        return channelId;
    }

    public static List<String> getMinecraftChatPrefixes() {
        return minecraftChatPrefixes;
    }
}
