package me.grovre.trackplaytime.listeners;

import me.grovre.trackplaytime.PlayerTimeData;
import me.grovre.trackplaytime.TrackPlaytime;
import me.grovre.trackplaytime.dbUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class OnPlayerLeave implements Listener {

    @EventHandler
    public void OnPlayerLeaveServer(PlayerQuitEvent event) throws IOException {
        Player player = event.getPlayer();
        TrackPlaytime plugin = TrackPlaytime.getPlugin();
        dbUtil db = new dbUtil(new File(plugin.getDataFolder().getAbsolutePath() + "/PlayerTimeData.json"));
        int playerIndex = db.containsUser(player.getUniqueId());
        PlayerTimeData ptd;
        if(playerIndex == -1) {
            ptd = new PlayerTimeData(player);
        } else {
            ptd = db.getPlayerTimeDataAtIndex(playerIndex);
        }
        long sessionTime = Math.subtractExact(System.currentTimeMillis(), TrackPlaytime.sessionTimeHMap.get(player.getUniqueId()));
        ptd.addPlaytime(sessionTime);
        db.addToDatabase(ptd);
        TrackPlaytime.sessionTimeHMap.remove(player.getUniqueId());
    }
}
