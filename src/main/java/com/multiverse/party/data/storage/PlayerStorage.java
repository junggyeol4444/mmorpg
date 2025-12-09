package com. multiverse.party. data.storage;

import com.multiverse.party.PartyCore;
import com.multiverse.party.data.YAMLDataManager;
import com.multiverse.party.models. PlayerPartyData;

import java.util.*;

public class PlayerStorage {

    private final PartyCore plugin;
    private final YAMLDataManager dataManager;

    public PlayerStorage(PartyCore plugin) {
        this.plugin = plugin;
        this.dataManager = (YAMLDataManager) plugin.getDataManager();
    }

    public void save(UUID playerUUID, PlayerPartyData data) {
        if (playerUUID == null || data == null) return;
        dataManager.savePlayerData(playerUUID, data);
    }

    public PlayerPartyData load(UUID playerUUID) {
        return dataManager.loadPlayerData(playerUUID);
    }

    public void delete(UUID playerUUID) {
        dataManager. deletePlayerData(playerUUID);
    }

    public void saveAll() {
        dataManager.saveAllPlayerData();
    }

    public Map<UUID, PlayerPartyData> loadAll() {
        return dataManager.loadAllPlayerData();
    }

    public PlayerPartyData getOrCreate(UUID playerUUID, String playerName) {
        PlayerPartyData data = load(playerUUID);
        
        if (data == null) {
            data = new PlayerPartyData();
            data.setPlayerUUID(playerUUID);
            data.setPlayerName(playerName);
            data.setCurrentParty(null);
            data.setTotalParties(0);
            data.setPartiesCreated(0);
            data.setPartiesJoined(0);
            data.setAutoDecline(false);
            data.setFriendsOnly(false);
            data.setLastPartyTime(0);
            
            save(playerUUID, data);
        } else if (data.getPlayerName() == null || ! data.getPlayerName().equals(playerName)) {
            data.setPlayerName(playerName);
            save(playerUUID, data);
        }
        
        return data;
    }

    public UUID getCurrentParty(UUID playerUUID) {
        PlayerPartyData data = load(playerUUID);
        return data != null ? data.getCurrentParty() : null;
    }

    public void setCurrentParty(UUID playerUUID, UUID partyId) {
        PlayerPartyData data = load(playerUUID);
        if (data != null) {
            data.setCurrentParty(partyId);
            data.setLastPartyTime(System.currentTimeMillis());
            save(playerUUID, data);
        }
    }

    public void clearCurrentParty(UUID playerUUID) {
        setCurrentParty(playerUUID, null);
    }

    public void incrementPartiesCreated(UUID playerUUID) {
        PlayerPartyData data = load(playerUUID);
        if (data != null) {
            data.setPartiesCreated(data.getPartiesCreated() + 1);
            data.setTotalParties(data.getTotalParties() + 1);
            save(playerUUID, data);
        }
    }

    public void incrementPartiesJoined(UUID playerUUID) {
        PlayerPartyData data = load(playerUUID);
        if (data != null) {
            data. setPartiesJoined(data.getPartiesJoined() + 1);
            data.setTotalParties(data.getTotalParties() + 1);
            save(playerUUID, data);
        }
    }

    public boolean isAutoDecline(UUID playerUUID) {
        PlayerPartyData data = load(playerUUID);
        return data != null && data.isAutoDecline();
    }

    public void setAutoDecline(UUID playerUUID, boolean autoDecline) {
        PlayerPartyData data = load(playerUUID);
        if (data != null) {
            data.setAutoDecline(autoDecline);
            save(playerUUID, data);
        }
    }

    public boolean isFriendsOnly(UUID playerUUID) {
        PlayerPartyData data = load(playerUUID);
        return data != null && data.isFriendsOnly();
    }

    public void setFriendsOnly(UUID playerUUID, boolean friendsOnly) {
        PlayerPartyData data = load(playerUUID);
        if (data != null) {
            data.setFriendsOnly(friendsOnly);
            save(playerUUID, data);
        }
    }

    public int getPlayerCount() {
        return dataManager.getPlayerCache().size();
    }

    public List<PlayerPartyData> getAllPlayerData() {
        return new ArrayList<>(dataManager.getPlayerCache().values());
    }

    public UUID getPlayerUUID(String playerName) {
        return dataManager.getPlayerUUID(playerName);
    }

    public String getPlayerName(UUID playerUUID) {
        return dataManager.getPlayerName(playerUUID);
    }

    public void updateCache(UUID playerUUID, PlayerPartyData data) {
        dataManager.updatePlayerCache(playerUUID, data);
    }

    public void removeFromCache(UUID playerUUID) {
        dataManager. removeFromPlayerCache(playerUUID);
    }

    public List<PlayerPartyData> getPlayersWithMostParties(int limit) {
        List<PlayerPartyData> allData = new ArrayList<>(dataManager.getPlayerCache().values());
        
        allData. sort((d1, d2) -> Integer.compare(d2.getTotalParties(), d1.getTotalParties()));
        
        if (allData.size() > limit) {
            return allData.subList(0, limit);
        }
        
        return allData;
    }

    public List<PlayerPartyData> getRecentlyActivePlayers(long sinceTime) {
        List<PlayerPartyData> results = new ArrayList<>();
        
        for (PlayerPartyData data : dataManager.getPlayerCache().values()) {
            if (data.getLastPartyTime() >= sinceTime) {
                results.add(data);
            }
        }
        
        results.sort((d1, d2) -> Long.compare(d2.getLastPartyTime(), d1.getLastPartyTime()));
        
        return results;
    }

    public List<PlayerPartyData> getPlayersInParties() {
        List<PlayerPartyData> results = new ArrayList<>();
        
        for (PlayerPartyData data : dataManager. getPlayerCache().values()) {
            if (data.getCurrentParty() != null) {
                results. add(data);
            }
        }
        
        return results;
    }

    public int getActivePlayerCount() {
        int count = 0;
        
        for (PlayerPartyData data : dataManager.getPlayerCache().values()) {
            if (data. getCurrentParty() != null) {
                count++;
            }
        }
        
        return count;
    }

    public Map<UUID, Integer> getPartyCreationStats() {
        Map<UUID, Integer> stats = new HashMap<>();
        
        for (Map.Entry<UUID, PlayerPartyData> entry : dataManager. getPlayerCache().entrySet()) {
            stats.put(entry.getKey(), entry.getValue().getPartiesCreated());
        }
        
        return stats;
    }

    public void resetPlayerStats(UUID playerUUID) {
        PlayerPartyData data = load(playerUUID);
        if (data != null) {
            data.setTotalParties(0);
            data.setPartiesCreated(0);
            data.setPartiesJoined(0);
            save(playerUUID, data);
        }
    }
}