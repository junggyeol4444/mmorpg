package com.multiverse.party. data. storage;

import com.multiverse.party.PartyCore;
import com. multiverse.party. data.YAMLDataManager;
import com.multiverse.party.models.Party;

import java.util.*;

public class PartyStorage {

    private final PartyCore plugin;
    private final YAMLDataManager dataManager;

    public PartyStorage(PartyCore plugin) {
        this. plugin = plugin;
        this.dataManager = (YAMLDataManager) plugin.getDataManager();
    }

    public void save(Party party) {
        if (party == null) return;
        dataManager.saveParty(party);
    }

    public Party load(UUID partyId) {
        return dataManager.loadParty(partyId);
    }

    public void delete(UUID partyId) {
        dataManager.deleteParty(partyId);
    }

    public void saveAll() {
        dataManager.saveAllParties();
    }

    public Map<UUID, Party> loadAll() {
        return dataManager.loadAllParties();
    }

    public boolean exists(UUID partyId) {
        return dataManager.partyExists(partyId);
    }

    public List<Party> getAllParties() {
        return new ArrayList<>(dataManager.getPartyCache().values());
    }

    public Party getPartyByName(String name) {
        if (name == null) return null;
        
        for (Party party : dataManager.getPartyCache().values()) {
            if (party.getPartyName() != null && 
                party.getPartyName().equalsIgnoreCase(name)) {
                return party;
            }
        }
        return null;
    }

    public Party getPartyByMember(UUID playerUUID) {
        for (Party party : dataManager.getPartyCache().values()) {
            if (party.getMembers().contains(playerUUID)) {
                return party;
            }
        }
        return null;
    }

    public List<Party> getPublicParties() {
        List<Party> publicParties = new ArrayList<>();
        
        for (Party party : dataManager.getPartyCache().values()) {
            if (party.isPublic()) {
                publicParties.add(party);
            }
        }
        
        return publicParties;
    }

    public List<Party> searchParties(String query) {
        List<Party> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (Party party : dataManager. getPartyCache().values()) {
            if (party.getPartyName() != null && 
                party.getPartyName().toLowerCase().contains(lowerQuery)) {
                results. add(party);
            }
        }
        
        return results;
    }

    public int getPartyCount() {
        return dataManager.getPartyCache().size();
    }

    public void updateCache(Party party) {
        dataManager.updatePartyCache(party);
    }

    public void removeFromCache(UUID partyId) {
        dataManager.removeFromPartyCache(partyId);
    }

    public List<Party> getPartiesByLevel(int minLevel, int maxLevel) {
        List<Party> results = new ArrayList<>();
        
        for (Party party : dataManager.getPartyCache().values()) {
            int level = party.getPartyLevel() != null ? party.getPartyLevel().getLevel() : 1;
            if (level >= minLevel && level <= maxLevel) {
                results. add(party);
            }
        }
        
        return results;
    }

    public List<Party> getPartiesWithSpace() {
        List<Party> results = new ArrayList<>();
        
        for (Party party : dataManager.getPartyCache().values()) {
            if (party.getMembers().size() < party.getMaxMembers()) {
                results.add(party);
            }
        }
        
        return results;
    }

    public List<Party> getSortedPartiesByLevel(boolean descending) {
        List<Party> parties = new ArrayList<>(dataManager.getPartyCache().values());
        
        parties.sort((p1, p2) -> {
            int level1 = p1.getPartyLevel() != null ? p1.getPartyLevel().getLevel() : 1;
            int level2 = p2.getPartyLevel() != null ? p2.getPartyLevel().getLevel() : 1;
            return descending ? Integer.compare(level2, level1) : Integer.compare(level1, level2);
        });
        
        return parties;
    }

    public List<Party> getSortedPartiesByMemberCount(boolean descending) {
        List<Party> parties = new ArrayList<>(dataManager.getPartyCache().values());
        
        parties. sort((p1, p2) -> {
            int count1 = p1.getMembers().size();
            int count2 = p2.getMembers().size();
            return descending ? Integer. compare(count2, count1) : Integer.compare(count1, count2);
        });
        
        return parties;
    }

    public List<Party> getSortedPartiesByCreatedTime(boolean descending) {
        List<Party> parties = new ArrayList<>(dataManager.getPartyCache().values());
        
        parties.sort((p1, p2) -> {
            long time1 = p1.getCreatedTime();
            long time2 = p2.getCreatedTime();
            return descending ? Long.compare(time2, time1) : Long.compare(time1, time2);
        });
        
        return parties;
    }
}