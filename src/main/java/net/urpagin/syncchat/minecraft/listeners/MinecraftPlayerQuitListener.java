package net.urpagin.syncchat.minecraft.listeners;

import net.urpagin.syncchat.DiscordInterface;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MinecraftPlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DiscordInterface.updateActivityPlaying();
    }
}
