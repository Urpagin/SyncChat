package net.urpagin.syncchat.discord.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.urpagin.syncchat.DiscordInterface;
import net.urpagin.syncchat.ReadConfig;
import net.urpagin.syncchat.minecraft.listeners.MinecraftPlayerConnectionTracker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;

public class DiscordCommandListener extends ListenerAdapter {

    final static String SKULL_EMOJI = "\uD83D\uDC80";
    final static String CROSSBONES_EMOJI = "☠\uFE0F";
    final static String MAN_SPEAKING_EMOJI = "\uD83D\uDDE3\uFE0F";
    final static String FIRE_EMOJI = "\uD83D\uDD25";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "playing":
                handlePlaying(event);
                break;
            case "deaths":
                handleDeaths(event);
                break;
            case "become_a_god":
                handleBecomeAGod(event);
                break;
        }
    }

    private void replyToEvent(SlashCommandInteractionEvent event, String response) {
        if (response.isBlank()) {
            event.reply("Cannot respond: tried to send an empty message!").mention().queue();
            return;
        }

        if (response.length() > DiscordInterface.DISCORD_MAX_MESSAGE_LENGTH) {
            event.reply("Cannot respond: tried to send a message exceeding Discord's limit of " + DiscordInterface.DISCORD_MAX_MESSAGE_LENGTH + " characters!").queue();
            return;
        }

        event.reply(response).queue();
    }

    private void handlePlaying(SlashCommandInteractionEvent event) {
        String response = getPlayingString();
        replyToEvent(event, response);
    }

    private String getPlayingString() {
        List<Player> onlinePlayerList = new ArrayList<>(getServer().getOnlinePlayers());

        if (onlinePlayerList.isEmpty()) {
            return ReadConfig.getDiscordPlayingSlashCommandNoPlayers();
        }

        // The main string.
        StringBuilder responseBuilder = new StringBuilder(ReadConfig.getDiscordPlayingSlashCommandHeaderFormatting() + "\n");

        Map<Player, String> playersElapsedTime = getPlayersElapsedTime();
        for (Map.Entry<Player, String> entry : playersElapsedTime.entrySet()) {
            String playerName = entry.getKey().getName();
            String elapsedTime = entry.getValue();

            // To use in the ReadConfig.format() method.
            HashMap<String, String> values = new HashMap<>();
            values.put(ReadConfig.NAME_KEY, playerName);
            values.put(ReadConfig.CONNECTED_ELAPSED_TIME, elapsedTime);

            try {
                String line = ReadConfig.format(ReadConfig.getDiscordPlayingSlashCommandLineFormatting(), values) + "\n";
                responseBuilder.append(line);
            } catch (Exception e) {
                // Do nothing
            }
        }

        String plural1 = (onlinePlayerList.size() > 1) ? "are" : "is";
        String plural2 = (onlinePlayerList.size() > 1) ? "players" : "player";
        responseBuilder.append(String.format("There %s **%s/%s** %s connected!", plural1, onlinePlayerList.size(), Bukkit.getServer().getMaxPlayers(), plural2));

        return responseBuilder.toString();
    }

    private Map<Player, String> getPlayersElapsedTime() {
        // Directly sorting the entries by time in ascending order
        return MinecraftPlayerConnectionTracker.playerJoinTimes.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> formatDuration(Duration.between(entry.getValue(), LocalDateTime.now())),
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }


    private static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    private void handleDeaths(SlashCommandInteractionEvent event) {
        String response = getDeathsString();
        replyToEvent(event, response);
    }

    private String getDeathsString() {
        Map<String, UUID> allPlayerNames = new HashMap<>();

        // Add offline players name & UUID to the set!
        for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
            if (!p.hasPlayedBefore() || p.getName() == null)
                continue;
            allPlayerNames.put(p.getName(), p.getUniqueId());
        }
        // Add online players name & UUID to the set!
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            allPlayerNames.put(p.getName(), p.getUniqueId());
        }

        if (allPlayerNames.isEmpty()) {
            return ReadConfig.getDiscordDeathsSlashCommandNoDeaths();
        }

        // Map of username to death count
        Map<UUID, Integer> playersDeathCount = new HashMap<>();
        for (Map.Entry<String, UUID> entry : allPlayerNames.entrySet()) {
            UUID playerId = entry.getValue();
            OfflinePlayer p = Bukkit.getOfflinePlayer(playerId);
            playersDeathCount.put(playerId, p.getStatistic(Statistic.DEATHS));
        }

        // Sorting death count by descending order
        Map<UUID, Integer> sortedByDeathCount = playersDeathCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));


        StringBuilder response = new StringBuilder(ReadConfig.getDiscordDeathsSlashCommandHeaderFormatting() + "\n");
        String firstPlaceEmoji = String.format(" %s%s%s", CROSSBONES_EMOJI, CROSSBONES_EMOJI, CROSSBONES_EMOJI);
        for (Map.Entry<UUID, Integer> entry : sortedByDeathCount.entrySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
            String playerName = offlinePlayer.getName();
            int deathCount = entry.getValue();
            String plural = (deathCount > 1) ? "s" : "";
            String totalHoursPlayed = String.valueOf(getHoursFromTicks(offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE)));

            // Values for the .format() method.
            HashMap<String, String> values = new HashMap<>();
            values.put(ReadConfig.NAME_KEY, playerName);
            values.put(ReadConfig.DEATH_COUNT, String.valueOf(deathCount));
            values.put(ReadConfig.PLURAL, plural);
            values.put(ReadConfig.PLAYTIME_HOURS, totalHoursPlayed);

            try {
                String line = ReadConfig.format(ReadConfig.getDiscordDeathsSlashCommandLineFormatting(), values) + firstPlaceEmoji + "\n";
                if (response.length() + line.length() < DiscordInterface.DISCORD_MAX_MESSAGE_LENGTH) {
                    response.append(line);
                }
            } catch (Exception e) {
                // Do nothing
            }

            firstPlaceEmoji = "";
        }

        return response.toString();
    }

    private long getHoursFromTicks(int ticks) {
        return ticks / 20 / 60 / 60; // Because 20 ticks is 1 second.
    }

    private void handleBecomeAGod(SlashCommandInteractionEvent event) {
        String response = String.format("%s%s%s%s blud be trippin frfr %s%s%s", MAN_SPEAKING_EMOJI, MAN_SPEAKING_EMOJI, FIRE_EMOJI, FIRE_EMOJI, SKULL_EMOJI, SKULL_EMOJI, SKULL_EMOJI);
        replyToEvent(event, response);
    }
}
