package me.grovre.trackplaytime;

import com.google.gson.Gson;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class dbUtil {

    private ArrayList<PlayerTimeData> playerTimeData;
    private Gson gson;
    private final File f;

    public dbUtil(File f) throws IOException {
        gson = new Gson();
        this.f = f;
        if(f.getParentFile().mkdir()) System.out.println("Creating TrackPlaytime folder");
        if(f.createNewFile()) System.out.println("Creating playerTimeData file");
    }

    public void addToDatabase(PlayerTimeData ptd) throws IOException {
        refreshPtd();
        if(playerTimeData == null) playerTimeData = new ArrayList<>();
        int containsUser = getIndexOfUUID(ptd.getUuid());
        if(containsUser == -1) {
            playerTimeData.add(ptd);
        } else {
            playerTimeData.remove(containsUser);
            playerTimeData.add(containsUser, ptd);
        }
        saveJson();
    }

    public ArrayList<PlayerTimeData> getAllEntriesFromDatabase() throws IOException {
        refreshPtd();
        return playerTimeData;
    }

    public void sortPtdByPlaytime() {
        playerTimeData = (ArrayList<PlayerTimeData>) playerTimeData.stream()
                .sorted(Comparator.comparingLong(PlayerTimeData::getTotalPlaytime))
                .collect(Collectors.toList());
    }

    public PlayerTimeData getPlayerTimeDataAtIndex(int i) throws IOException {
        refreshPtd();
        return playerTimeData.get(i);
    }

    public void refreshPtd() throws IOException {
        Reader fr = new FileReader(f);
        PlayerTimeData[] ptd = new PlayerTimeData[0];
        ptd = gson.fromJson(fr, ptd.getClass());
        playerTimeData = new ArrayList<>(Bukkit.getOfflinePlayers().length);
        if(ptd == null || ptd.length < 1) {
            System.out.println("PlayerTimeData file is empty");
        } else {
            Collections.addAll(playerTimeData, ptd);
        }
        fr.close();
    }

    public long getSummedPlaytime() throws IOException {
        refreshPtd();
        return playerTimeData.stream().map(PlayerTimeData::getTotalPlaytime).mapToLong(Long::longValue).sum();
    }

    public String getFormattedTotalPlaytime() throws IOException {
        long totalPlaytime = getSummedPlaytime();
        return String.format("%02dH:%02dM:%02dS",
                TimeUnit.MILLISECONDS.toHours(totalPlaytime),
                TimeUnit.MILLISECONDS.toMinutes(totalPlaytime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalPlaytime)),
                TimeUnit.MILLISECONDS.toSeconds(totalPlaytime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalPlaytime)));
    }

    public void saveJson() throws IOException {
        Writer fw = new FileWriter(f, false);
        PlayerTimeData[] ptd = new PlayerTimeData[playerTimeData.size()];
        for (int i = 0, playerTimeDataSize = playerTimeData.size(); i < playerTimeDataSize; i++) {
            PlayerTimeData pt = playerTimeData.get(i);
            ptd[i] = pt;
        }
        gson.toJson(ptd, fw);
        fw.close();
    }

    public int getIndexOfUUID(UUID uuid) throws IOException {
        refreshPtd();
        if(playerTimeData == null) return -1;
        for (int i = 0; i < playerTimeData.size(); i++) {
            PlayerTimeData pt = playerTimeData.get(i);
            if (pt.getUuid().equals(uuid)) {
                return i;
            }
        }
        return -1;
    }
}
