package net.urpagin.syncchat.minecraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.util.HashMap;

public class MinecraftPlayerConnectionTracker implements Listener {
    public final static HashMap<Player, LocalDateTime> playerJoinTimes = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerJoinTimes.put(event.getPlayer(), LocalDateTime.now());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerJoinTimes.remove(event.getPlayer());
    }
}
