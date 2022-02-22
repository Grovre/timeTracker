package me.grovre.trackplaytime.commands;

import me.grovre.trackplaytime.PlayerTimeData;
import me.grovre.trackplaytime.TrackPlaytime;
import me.grovre.trackplaytime.dbUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TimetopCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = sender instanceof Player ? (Player) sender : null;
        if(player == null) {
            System.out.println("You must execute this as a player!");
            return true;
        }

        ArrayList<PlayerTimeData> ptd = new ArrayList<>(10);
        dbUtil db = null;
        try {
            db = new dbUtil(new File(TrackPlaytime.getPlugin().getDataFolder().getAbsolutePath() + "/PlayerTimeData.json"));
            db.refreshPtd();
            ptd = (ArrayList<PlayerTimeData>) db.getAllEntriesFromDatabase()
                    .stream()
                    .sorted(Comparator.comparingLong(PlayerTimeData::getTotalPlaytime).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        player.sendMessage(ChatColor.YELLOW + " ----- " + ChatColor.LIGHT_PURPLE + "Timetop" + ChatColor.YELLOW + " ----- ");
        try {
            player.sendMessage(ChatColor.RED + "Total server playtime: " + db.getFormattedTotalPlaytime());
        } catch (IOException ignored) {}
        for (int i = 0; i < ptd.size(); i++) {
            PlayerTimeData p = ptd.get(i);
            long playtime = p.getTotalPlaytime();
            String time = String.format("%02dH:%02dM:%02dS",
                    TimeUnit.MILLISECONDS.toHours(playtime),
                    TimeUnit.MILLISECONDS.toMinutes(playtime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(playtime)),
                    TimeUnit.MILLISECONDS.toSeconds(playtime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(playtime)));

            player.sendMessage(ChatColor.RED + "" + (i + 1) + ". " + ChatColor.YELLOW + p.getPlayerName() + ": " + time);
        }


        return true;
    }
}
