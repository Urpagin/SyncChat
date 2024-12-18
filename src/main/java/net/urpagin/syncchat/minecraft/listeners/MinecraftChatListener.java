package net.urpagin.syncchat.minecraft.listeners;

import net.urpagin.syncchat.SyncChat;
import net.urpagin.syncchat.ReadConfig;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class MinecraftChatListener implements Listener {

    // the prefixes used to make an MC Chat message send to Discord.
    private static final List<String> MINECRAFT_CHAT_PREFIXES = ReadConfig.getMinecraftChatPrefixes();

    // Formatting set to messages sent to Discord from the MC Chat.
    private static final String DISCORD_BOUND_FORMATTING = "§l"; // bold

    // Formatting set to messages that could not be sent to Discord.
    private static final String DISCORD_BOUND_FORMATTING_ERROR = "§c"; // red

    private boolean hasValidPrefix(String message) {
        if (message == null || message.isBlank()) return false;
        char firstChar = message.charAt(0);

        for (String prefix : MINECRAFT_CHAT_PREFIXES) {
            if (!prefix.isEmpty() && prefix.charAt(0) == firstChar) return true;
        }

        return false;
    }

    private String getPlayerHealthString(Player player) {
        AttributeInstance playerMaxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (playerMaxHealthAttr == null) return "";

        double playerHealth = player.getHealth();
        double playerMaxHealth = playerMaxHealthAttr.getValue();

        return String.format("%.1f/%.1fHP", playerHealth, playerMaxHealth);
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();

        // If no prefixes skip the hasValidPrefix() function.
        if (!MINECRAFT_CHAT_PREFIXES.isEmpty()) if (!hasValidPrefix(message)) return;

        // Removes the prefix from the message.
        message = message.substring(1).strip();

        if (message.isBlank()) return;

        // ---- now process chat message ----

        String playerHealthString = getPlayerHealthString(event.getPlayer());
        String discordMessage;

        if (playerHealthString.isEmpty()) {
            discordMessage = String.format("**<%s>** %s", playerName, message);
        } else {
            discordMessage = String.format("**<%s>** %s  |  *%s*", playerName, message, playerHealthString);
        }


        boolean isMessageSentSuccessfully = SyncChat.discord.sendMessageToChannel(discordMessage);

        if (isMessageSentSuccessfully) event.setMessage(DISCORD_BOUND_FORMATTING + message);
        else event.setMessage(DISCORD_BOUND_FORMATTING_ERROR + message);
    }
}
