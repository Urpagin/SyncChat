package net.urpagin.syncchat.discord.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.urpagin.syncchat.DiscordInterface;
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
    final static String TURBAN_A_EMOJI = "\uD83D\uDC73\uD83C\uDFFF\u200D♀\uFE0F";
    final static String TURBAN_B_EMOJI = "\uD83D\uDC73\uD83C\uDFFD";
    final static String EMPTY_NEST_EMOJI = "\uD83E\uDEB9";
    final static String SMILE_EMOJI = "\uD83D\uDE04";
    final static String WICKED_IMP_EMOJI = "\uD83D\uDE08";
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
        StringBuilder responseBuilder = new StringBuilder(String.format("## %s Online Player List %s\n", TURBAN_A_EMOJI, TURBAN_B_EMOJI));

        if (onlinePlayerList.isEmpty()) {
            responseBuilder.append(String.format("%s%s **No one is connected!** %s%s", EMPTY_NEST_EMOJI, EMPTY_NEST_EMOJI, EMPTY_NEST_EMOJI, EMPTY_NEST_EMOJI));
            return responseBuilder.toString();
        }

        Map<Player, String> playersElapsedTime = getPlayersElapsedTime();
        for (Map.Entry<Player, String> entry : playersElapsedTime.entrySet()) {
            String playerName = entry.getKey().getName();
            String elapsedTime = entry.getValue();
            responseBuilder.append("- **").append(playerName).append("** (connected for ").append(elapsedTime).append(")\n");
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
            return String.format("%s No one died! Yet... %s", SMILE_EMOJI, WICKED_IMP_EMOJI);
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


        StringBuilder response = new StringBuilder();
        response.append(String.format("## %s Deaths Leaderboard %s\n", SKULL_EMOJI, SKULL_EMOJI));
        String firstPlaceEmoji = String.format(" %s%s%s", CROSSBONES_EMOJI, CROSSBONES_EMOJI, CROSSBONES_EMOJI);
        for (Map.Entry<UUID, Integer> entry : sortedByDeathCount.entrySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
            String playerName = offlinePlayer.getName();
            int deathCount = entry.getValue();
            String plural = (deathCount > 1) ? "times" : "time";
            String totalHoursPlayed = getHoursFromTicks(offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE)) + "h";

            StringBuilder line = new StringBuilder();
            line.append("- ")
                    .append(playerName)
                    .append(" died **")
                    .append(deathCount)
                    .append("** ")
                    .append(plural)
                    .append(firstPlaceEmoji)
                    .append(" (")
                    .append(totalHoursPlayed)
                    .append(")\n");

            if (response.length() + line.length() < DiscordInterface.DISCORD_MAX_MESSAGE_LENGTH) {
                response.append(line);
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
