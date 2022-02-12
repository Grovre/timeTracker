package me.grovre.trackplaytime;

import com.google.gson.Gson;
import org.bukkit.entity.Player;

import java.io.FileReader;
import java.io.Reader;
import java.util.UUID;

public class PlayerTimeData {

    private String playerName;
    private final UUID uuid;
    private long totalPlaytime;

    public PlayerTimeData(Player player) {
        this.playerName = player.getName();
        this.uuid = player.getUniqueId();
        this.totalPlaytime = 0;
    }

    public void addPlaytime(long time) {
        totalPlaytime = Math.addExact(totalPlaytime, time);
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getTotalPlaytime() {
        return totalPlaytime;
    }

    public void setTotalPlaytime(long totalPlaytime) {
        this.totalPlaytime = totalPlaytime;
    }
}
