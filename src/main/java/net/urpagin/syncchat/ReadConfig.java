package net.urpagin.syncchat;

import net.urpagin.syncchat.discord.listeners.DiscordMessageListener;
import net.urpagin.syncchat.exceptions.InvalidConfigException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class ReadConfig {
    private static final String DEFAULT_TOKEN = "your_discord_bot_token_here";
    private static final long DEFAULT_CHANNEL_ID = -1;

    private static final String DEFAULT_DISCORD_PLAYING_SLASH_COMMAND_HEADER_FORMATTING = "## :woman_wearing_turban_tone5: Online Player List :person_wearing_turban_tone3:";
    private static final String DEFAULT_DISCORD_PLAYING_SLASH_COMMAND_LINE_FORMATTING = "- **{name}** (connected for {connectedElapsedTime})";

    private static final String DEFAULT_DISCORD_MESSAGE_FORMATTING = "**<{name}>** {message}";
    private static final String DEFAULT_MINECRAFT_MESSAGE_FORMATTING = "<{name}> §9{message}";
    private static final String DEFAULT_MINECRAFT_SENT_MESSAGE_FORMATTING = "§l{wholeMessage}";
    private static final String DEFAULT_DISCORD_DEATHS_SLASH_COMMAND_HEADER_FORMATTING = "## :skull: Deaths Leaderboard :skull:";
    private static final String DEFAULT_DISCORD_DEATHS_SLASH_COMMAND_LINE_FORMATTING = "- {name} died **{deathCount}** times ({playtimeHours}h)";
    private static final int DEFAULT_DISCORD_USERNAME_TYPE = 1;
    private static final String DEFAULT_DISCORD_PLAYING_SLASH_COMMAND_NO_PLAYERS = ":empty_nest::empty_nest: **No one is connected!** :empty_nest::empty_nest:";
    private static final String DEFAULT_DISCORD_DEATHS_SLASH_COMMAND_NO_DEATHS = ":smile: No one died! Yet... :smiling_imp:";

    // Constant variables for formatting
    public static final String NAME_KEY = "name";
    public static final String MESSAGE_KEY = "message";
    public static final String HEALTH_KEY = "health";
    public static final String MINECRAFT_CHAT_WHOLE_MESSAGE = "wholeMessage";
    public static final String CONNECTED_ELAPSED_TIME = "connectedElapsedTime";
    public static final String DEATH_COUNT = "deathCount";
    public static final String PLURAL = "plural";
    public static final String PLAYTIME_HOURS = "playtimeHours";


    private final JavaPlugin plugin;

    private static String botToken;
    private static long channelId;
    private static List<String> minecraftChatPrefixes;

    private static String discordMessageFormatting;
    private static String minecraftMessageFormatting;
    private static String discordPlayingSlashCommandHeaderFormatting;
    private static String discordPlayingSlashCommandLineFormatting;
    private static String minecraftSentMessageFormatting;
    private static String discordDeathsSlashCommandHeaderFormatting;
    private static String discordDeathsSlashCommandLineFormatting;
    private static DiscordMessageListener.UsernameType discordUsernameType;
    private static String discordPlayingSlashCommandNoPlayers;
    private static String discordDeathsSlashCommandNoDeaths;


    public ReadConfig(JavaPlugin plugin) throws InvalidConfigException {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadConfig();
    }

    private void loadConfig() throws InvalidConfigException {
        botToken = plugin.getConfig().getString("discord_bot_token", DEFAULT_TOKEN);
        channelId = plugin.getConfig().getLong("discord_channel_id", DEFAULT_CHANNEL_ID);
        minecraftChatPrefixes = plugin.getConfig().getStringList("minecraft_chat_prefixes");

        discordMessageFormatting = plugin.getConfig().getString(
                "discord-formatting.message", DEFAULT_DISCORD_MESSAGE_FORMATTING
        );
        discordPlayingSlashCommandHeaderFormatting = plugin.getConfig().getString(
                "discord-formatting.playing-slash-command-header", DEFAULT_DISCORD_PLAYING_SLASH_COMMAND_HEADER_FORMATTING
        );
        discordPlayingSlashCommandLineFormatting = plugin.getConfig().getString(
                "discord-formatting.playing-slash-command-line", DEFAULT_DISCORD_PLAYING_SLASH_COMMAND_LINE_FORMATTING
        );
        discordDeathsSlashCommandHeaderFormatting = plugin.getConfig().getString(
                "discord-formatting.deaths-slash-command-header",
                DEFAULT_DISCORD_DEATHS_SLASH_COMMAND_HEADER_FORMATTING
        );
        discordDeathsSlashCommandLineFormatting = plugin.getConfig().getString(
                "discord-formatting.deaths-slash-command-line",
                DEFAULT_DISCORD_DEATHS_SLASH_COMMAND_LINE_FORMATTING
        );

        int usernameTypeValue = plugin.getConfig().getInt("minecraft-formatting.discord-username-type", DEFAULT_DISCORD_USERNAME_TYPE);
        switch (usernameTypeValue) {
            case 1 -> discordUsernameType = DiscordMessageListener.UsernameType.GUILD_NICKNAME;
            case 2 -> discordUsernameType = DiscordMessageListener.UsernameType.GLOBAL_NICKNAME;
            case 3 -> discordUsernameType = DiscordMessageListener.UsernameType.GLOBAL_USERNAME;
            default -> throw new InvalidConfigException(
                    "Invalid value for 'minecraft-formatting.discord-username-type'. Must be 1, 2, or 3."
            );
        }

        discordPlayingSlashCommandNoPlayers = plugin.getConfig().getString(
                "discord-formatting.playing-slash-command-no-players",
                DEFAULT_DISCORD_PLAYING_SLASH_COMMAND_NO_PLAYERS
        );

        discordDeathsSlashCommandNoDeaths = plugin.getConfig().getString(
                "discord-formatting.deaths-slash-command-no-deaths",
                DEFAULT_DISCORD_DEATHS_SLASH_COMMAND_NO_DEATHS
        );


        minecraftMessageFormatting = plugin.getConfig().getString("minecraft-formatting.message", DEFAULT_MINECRAFT_MESSAGE_FORMATTING);
        minecraftSentMessageFormatting = plugin.getConfig().getString("minecraft-formatting.sent-message", DEFAULT_MINECRAFT_SENT_MESSAGE_FORMATTING);


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

    public static String getDiscordMessageFormatting() {
        return discordMessageFormatting;
    }

    public static String getMinecraftMessageFormatting() {
        return minecraftMessageFormatting;
    }

    public static String getDiscordPlayingSlashCommandHeaderFormatting() {
        return discordPlayingSlashCommandHeaderFormatting;
    }

    public static String getDiscordPlayingSlashCommandLineFormatting() {
        return discordPlayingSlashCommandLineFormatting;
    }

    public static String getMinecraftSentMessageFormatting() {
        return minecraftSentMessageFormatting;
    }

    public static String getDiscordDeathsSlashCommandHeaderFormatting() {
        return discordDeathsSlashCommandHeaderFormatting;
    }

    public static String getDiscordDeathsSlashCommandLineFormatting() {
        return discordDeathsSlashCommandLineFormatting;
    }

    public static DiscordMessageListener.UsernameType getDiscordUsernameType() {
        return discordUsernameType;
    }

    public static String getDiscordPlayingSlashCommandNoPlayers() {
        return discordPlayingSlashCommandNoPlayers;
    }

    public static String getDiscordDeathsSlashCommandNoDeaths() {
        return discordDeathsSlashCommandNoDeaths;
    }


    /**
     * Formats a string by replacing placeholder variables with corresponding values.
     *
     * <p>This method first validates that the string does not contain any nested or unmatched
     * braces. Then it splits the string by opening braces and replaces each placeholder of
     * the form {@code {key}} with the matching value from {@code content}.
     * (Docstring written with AI, function by Urpagin)
     *
     * @param text    the string containing placeholders enclosed in curly braces
     * @param content a map of placeholder keys to their replacement values
     * @return the formatted string with placeholders replaced
     * @throws Exception if the string has invalid braces or references a key not present in {@code content}
     */
    public static String format(String text, HashMap<String, String> content) throws Exception {
        StringBuilder builder = new StringBuilder();

        // Validates strings to avoid nested or unmatched braces.
        boolean isBraceOpen = false;

        for (char c : text.toCharArray()) {
            if (c == '{') {
                if (isBraceOpen) throw new Exception("Tried to format an invalid string: Nested braces found");
                isBraceOpen = true;
            } else if (c == '}') {
                if (!isBraceOpen) throw new Exception("Tried to format an invalid string: Unmatched closing brace");
                isBraceOpen = false;
            }
        }

        // Check if there is an unmatched opening brace.
        if (isBraceOpen) throw new Exception("Tried to format an invalid string: Unmatched opening brace");


        String[] tokens = text.split("\\{");
        // Append string before the first '{'.
        builder.append(tokens[0]);

        for (int i = 1; i < tokens.length; i++) {
            String[] subTokens = tokens[i].split("}");

            if (subTokens.length == 0) {
                throw new Exception("Tried to format a string with an empty variable");
            }

            // The key is what's before the closing brace
            String key = subTokens[0];
            if (!content.containsKey(key)) {
                throw new Exception("Tried to format a string with an unknown key");
            }
            builder.append(content.get(key));

            if (subTokens.length > 1) {
                // Append what's after the closing brace
                builder.append(subTokens[1]);
            }
        }

        return builder.toString();
    }
}
