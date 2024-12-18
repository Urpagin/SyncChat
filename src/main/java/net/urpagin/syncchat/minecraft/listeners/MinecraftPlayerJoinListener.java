package net.urpagin.syncchat.minecraft.listeners;

import net.urpagin.syncchat.DiscordInterface;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MinecraftPlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DiscordInterface.updateActivityPlaying();
    }
}
