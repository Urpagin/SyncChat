package net.urpagin.syncchat;

import net.urpagin.syncchat.exceptions.InvalidConfigException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ReadConfig {
    private static final String DEFAULT_TOKEN = "your_discord_bot_token_here";
    private static final long DEFAULT_CHANNEL_ID = -1;

    private final JavaPlugin plugin;

    private static String botToken;
    private static long channelId;
    private static List<String> minecraftChatPrefixes;

    public ReadConfig(JavaPlugin plugin) throws InvalidConfigException {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadConfig();
    }

    private void loadConfig() throws InvalidConfigException {
        botToken = plugin.getConfig().getString("discord_bot_token", DEFAULT_TOKEN);
        channelId = plugin.getConfig().getLong("discord_channel_id", DEFAULT_CHANNEL_ID);
        minecraftChatPrefixes = plugin.getConfig().getStringList("minecraft_chat_prefixes");


        if (DEFAULT_TOKEN.equals(botToken)) {
            throw new InvalidConfigException("Please set 'discord_bot_token' in the plugin's 'config.yml'");
        }

        if (channelId == DEFAULT_CHANNEL_ID) {
            throw new InvalidConfigException("Please set 'discord_channel_id' in the plugin's 'config.yml'");
        }
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
