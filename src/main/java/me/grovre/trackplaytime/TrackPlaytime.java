package me.grovre.trackplaytime;

import me.grovre.trackplaytime.commands.TimetopCommand;
import me.grovre.trackplaytime.listeners.OnPlayerJoin;
import me.grovre.trackplaytime.listeners.OnPlayerLeave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class TrackPlaytime extends JavaPlugin {

    private static TrackPlaytime plugin;
    public static Map<UUID, Long> sessionTimeHMap = new HashMap<>();

    public static TrackPlaytime getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        getServer().getPluginManager().registerEvents(new OnPlayerLeave(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);

        Objects.requireNonNull(getServer().getPluginCommand("timetop")).setExecutor(new TimetopCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for(Player p : Bukkit.getOnlinePlayers()) {
            dbUtil db = null;
            try {
                db = new dbUtil(new File(getDataFolder().getAbsolutePath() + "/PlayerTimeData.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int playerIndex = 0;
            try {
                playerIndex = db.getIndexOfUUID(p.getUniqueId());
            } catch (IOException e) {
                e.printStackTrace();
            }
            PlayerTimeData ptd = null;
            if(playerIndex == -1) {
                ptd = new PlayerTimeData(p);
            } else {
                try {
                    ptd = db.getPlayerTimeDataAtIndex(playerIndex);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            long timeJoined = sessionTimeHMap.get(p.getUniqueId());
            long sessionTime = System.currentTimeMillis() - timeJoined;
            ptd.addPlaytime(sessionTime);
            try {
                db.addToDatabase(ptd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sessionTimeHMap.remove(p.getUniqueId());
        }
    }
}
