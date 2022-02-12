package me.grovre.trackplaytime;

import com.google.gson.Gson;
import me.grovre.trackplaytime.PlayerTimeData;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

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
        int containsUser = containsUser(ptd.getUuid());
        if(containsUser == -1) {
            playerTimeData.add(ptd);
        } else {
            playerTimeData.remove(containsUser);
            playerTimeData.add(containsUser, ptd);
        }
        saveJson();
    }

    public PlayerTimeData getPlayerTimeDataAtIndex(int i) throws IOException {
        refreshPtd();
        return playerTimeData.get(i);
    }

    public void refreshPtd() throws IOException {
        Reader fr = new FileReader(f);
        PlayerTimeData[] ptd = new PlayerTimeData[0];
        ptd = gson.fromJson(fr, ptd.getClass());
        playerTimeData = new ArrayList<>();
        if(ptd == null || ptd.length < 1) {
            System.out.println("PlayerTimeData file is empty");
        } else {
            Collections.addAll(playerTimeData, ptd);
        }
        fr.close();
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

    public int containsUser(UUID uuid) throws IOException {
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
