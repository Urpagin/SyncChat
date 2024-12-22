package net.urpagin.syncchat.discord.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.urpagin.syncchat.ReadConfig;

import java.util.HashMap;

import static org.bukkit.Bukkit.getServer;

// Handles any new message on the channel.
public class DiscordMessageListener extends ListenerAdapter {

    // Discord ChannelID to which messages will be sent.
    public static long channelId;

    // Max length of a Minecraft chat message.
    private static final int MINECRAFT_CHAT_MAX_MSG_LENGTH = 256;

    // Emoji for error reactions
    private static final String DISCORD_ERROR_REPLY_REACTION_EMOJI = "❌"; // Red cross emoji (X)

    public DiscordMessageListener(long channelId) {
        DiscordMessageListener.channelId = channelId;
    }

    private void discordErrorReply(MessageReceivedEvent event, String errorMessage) {
        Emoji crossEmoji = Emoji.fromUnicode(DISCORD_ERROR_REPLY_REACTION_EMOJI);
        event.getMessage().addReaction(crossEmoji).queue();
        event.getMessage().reply(errorMessage).queue();
    }

    // GUILD_NICKNAME is the nickname someone has on a guild.
    // GLOBAL_NICKNAME is the name that you choose everyone will see on Discord.
    // GLOBAL_USERNAME is your Discord handle: e.g: @urpagin.
    public enum UsernameType {
        GUILD_NICKNAME, GLOBAL_NICKNAME, GLOBAL_USERNAME
    }

    private String getEventAuthorName(MessageReceivedEvent event, UsernameType usernameType) {
        if (usernameType == UsernameType.GUILD_NICKNAME) {
            Member member = event.getMember();
            if (member != null)
                return member.getEffectiveName(); // The Nickname of this Member or the Username if no Nickname is present.
        }

        if (usernameType == UsernameType.GLOBAL_NICKNAME) {
            return event.getAuthor().getGlobalName();
        }

        return event.getAuthor().getName(); // If not from guild, return Username.

    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != channelId) return;
        if (event.getAuthor().isBot()) return; // Should not send messages sent by ourselves
        if (event.getAuthor().isSystem()) return; // For good measure
        if (event.getMessage().getType() != MessageType.DEFAULT) return; // Rejects pin notifications and non text.
        if (event.getMessage().getContentRaw().isBlank()) {
            discordErrorReply(event, "The message content is empty! Cannot send to MC Chat.");
            return;
        }

        String authorName = getEventAuthorName(event, ReadConfig.getDiscordUsernameType());
        String authorMessage = event.getMessage().getContentRaw();

        // To use the .format() method then.
        HashMap<String, String> values = new HashMap<>();
        values.put(ReadConfig.NAME_KEY, authorName);
        values.put(ReadConfig.MESSAGE_KEY, authorMessage);

        try {
            String message = ReadConfig.format(ReadConfig.getMinecraftMessageFormatting(), values);
            if (message.length() > MINECRAFT_CHAT_MAX_MSG_LENGTH) {
                discordErrorReply(event, "The message length is greater than " + MINECRAFT_CHAT_MAX_MSG_LENGTH + " characters! Cannot send to MC Chat.");
                return;
            }
            getServer().broadcastMessage(message);


        } catch (Exception e) {
            // Do nothing
        }
    }
}
