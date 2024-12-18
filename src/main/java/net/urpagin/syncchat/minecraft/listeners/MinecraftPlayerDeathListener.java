package net.urpagin.syncchat.minecraft.listeners;

import net.urpagin.syncchat.SyncChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static org.bukkit.Bukkit.getLogger;

public class MinecraftPlayerDeathListener implements Listener {

    private static final String DISCORD_DEATH_MESSAGE_EMOJI = "☠\uFE0F";

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String deathMessage = event.getDeathMessage();
        if (deathMessage == null) {
            getLogger().warning("Cannot get death message for " + event.getEntity().getName());
            return;
        }

        String discordMessage = String.format("%s **%s** %s", DISCORD_DEATH_MESSAGE_EMOJI, deathMessage, DISCORD_DEATH_MESSAGE_EMOJI);

        boolean isMessageSentSuccessfully = SyncChat.discord.sendMessageToChannel(discordMessage);

        if (!isMessageSentSuccessfully) getLogger().warning("Could not send death message to Discord.");

    }
}
