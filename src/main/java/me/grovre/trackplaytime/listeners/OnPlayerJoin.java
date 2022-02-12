package me.grovre.trackplaytime.listeners;

import me.grovre.trackplaytime.TrackPlaytime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void OnPlayerJoinServer(PlayerJoinEvent event) {
        Long joinTime = System.currentTimeMillis();
        TrackPlaytime.sessionTimeHMap.put(event.getPlayer().getUniqueId(), joinTime);
    }
}
