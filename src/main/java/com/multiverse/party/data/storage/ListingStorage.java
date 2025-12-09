package com.multiverse.party.data. storage;

import com.multiverse.party.PartyCore;
import com. multiverse.party. data.YAMLDataManager;
import com.multiverse.party.models.PartyListing;
import com.multiverse.party.models.enums.PartyPurpose;

import java.util.*;
import java. util.stream.Collectors;

public class ListingStorage {

    private final PartyCore plugin;
    private final YAMLDataManager dataManager;

    public ListingStorage(PartyCore plugin) {
        this. plugin = plugin;
        this.dataManager = (YAMLDataManager) plugin.getDataManager();
    }

    public void save(PartyListing listing) {
        if (listing == null) return;
        dataManager.saveListing(listing);
    }

    public PartyListing load(UUID partyId) {
        return dataManager.loadListing(partyId);
    }

    public void delete(UUID partyId) {
        dataManager.deleteListing(partyId);
    }

    public void saveAll() {
        dataManager.saveAllListings();
    }

    public List<PartyListing> loadAll() {
        return dataManager.loadAllListings();
    }

    public List<PartyListing> getAllListings() {
        return new ArrayList<>(dataManager.getListingCache().values());
    }

    public List<PartyListing> getActiveListings() {
        long currentTime = System.currentTimeMillis();
        List<PartyListing> activeListings = new ArrayList<>();
        
        for (PartyListing listing : dataManager.getListingCache().values()) {
            if (listing. getExpireTime() <= 0 || listing.getExpireTime() > currentTime) {
                activeListings. add(listing);
            }
        }
        
        return activeListings;
    }

    public List<PartyListing> getExpiredListings() {
        long currentTime = System.currentTimeMillis();
        List<PartyListing> expiredListings = new ArrayList<>();
        
        for (PartyListing listing : dataManager. getListingCache().values()) {
            if (listing.getExpireTime() > 0 && listing. getExpireTime() <= currentTime) {
                expiredListings.add(listing);
            }
        }
        
        return expiredListings;
    }

    public void cleanupExpiredListings() {
        List<PartyListing> expired = getExpiredListings();
        
        for (PartyListing listing : expired) {
            delete(listing.getPartyId());
        }
        
        if (! expired.isEmpty()) {
            plugin.getLogger().info("만료된 모집 공고 " + expired.size() + "개 정리 완료");
        }
    }

    public PartyListing createListing(UUID partyId, String title, String description,
                                       int minLevel, int maxLevel, List<String> requiredRoles,
                                       PartyPurpose purpose) {
        PartyListing listing = new PartyListing();
        listing.setPartyId(partyId);
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setMinLevel(minLevel);
        listing.setMaxLevel(maxLevel);
        listing.setRequiredRoles(requiredRoles != null ? requiredRoles : new ArrayList<>());
        listing.setPurpose(purpose != null ? purpose : PartyPurpose. GENERAL);
        listing.setCreatedTime(System.currentTimeMillis());
        
        long expireTime = plugin.getConfig().getLong("party-finder.listings.expire-time", 3600) * 1000;
        listing.setExpireTime(System.currentTimeMillis() + expireTime);
        
        save(listing);
        return listing;
    }

    public boolean hasListing(UUID partyId) {
        return dataManager.getListingCache().containsKey(partyId);
    }

    public List<PartyListing> searchListings(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getActiveListings();
        }
        
        String lowerQuery = query. toLowerCase();
        List<PartyListing> results = new ArrayList<>();
        
        for (PartyListing listing : getActiveListings()) {
            boolean matches = false;
            
            if (listing.getTitle() != null && 
                listing.getTitle().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }
            
            if (! matches && listing.getDescription() != null && 
                listing. getDescription().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }
            
            if (matches) {
                results.add(listing);
            }
        }
        
        return results;
    }

    public List<PartyListing> getListingsByPurpose(PartyPurpose purpose) {
        if (purpose == null) {
            return getActiveListings();
        }
        
        List<PartyListing> results = new ArrayList<>();
        
        for (PartyListing listing : getActiveListings()) {
            if (listing. getPurpose() == purpose) {
                results.add(listing);
            }
        }
        
        return results;
    }

    public List<PartyListing> getListingsByLevelRange(int playerLevel) {
        List<PartyListing> results = new ArrayList<>();
        
        for (PartyListing listing : getActiveListings()) {
            if (playerLevel >= listing.getMinLevel() && playerLevel <= listing.getMaxLevel()) {
                results.add(listing);
            }
        }
        
        return results;
    }

    public List<PartyListing> getListingsWithRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return getActiveListings();
        }
        
        String lowerRole = role.toLowerCase();
        List<PartyListing> results = new ArrayList<>();
        
        for (PartyListing listing : getActiveListings()) {
            List<String> requiredRoles = listing.getRequiredRoles();
            if (requiredRoles != null) {
                for (String requiredRole : requiredRoles) {
                    if (requiredRole.toLowerCase().contains(lowerRole)) {
                        results. add(listing);
                        break;
                    }
                }
            }
        }
        
        return results;
    }

    public List<PartyListing> getSortedListingsByCreatedTime(boolean descending) {
        List<PartyListing> listings = getActiveListings();
        
        listings.sort((l1, l2) -> {
            long time1 = l1.getCreatedTime();
            long time2 = l2.getCreatedTime();
            return descending ? Long.compare(time2, time1) : Long.compare(time1, time2);
        });
        
        return listings;
    }

    public List<PartyListing> getFilteredListings(PartyPurpose purpose, int playerLevel,
                                                   String role, String searchQuery) {
        List<PartyListing> results = getActiveListings();
        
        if (purpose != null) {
            results = results. stream()
                    .filter(l -> l.getPurpose() == purpose)
                    .collect(Collectors.toList());
        }
        
        if (playerLevel > 0) {
            final int level = playerLevel;
            results = results. stream()
                    .filter(l -> level >= l.getMinLevel() && level <= l.getMaxLevel())
                    . collect(Collectors. toList());
        }
        
        if (role != null && !role.trim().isEmpty()) {
            String lowerRole = role.toLowerCase();
            results = results.stream()
                    . filter(l -> {
                        if (l.getRequiredRoles() == null) return false;
                        return l.getRequiredRoles().stream()
                                .anyMatch(r -> r.toLowerCase().contains(lowerRole));
                    })
                    .collect(Collectors.toList());
        }
        
        if (searchQuery != null && ! searchQuery.trim().isEmpty()) {
            String lowerQuery = searchQuery.toLowerCase();
            results = results. stream()
                    .filter(l -> {
                        if (l.getTitle() != null && l.getTitle().toLowerCase().contains(lowerQuery)) {
                            return true;
                        }
                        return l.getDescription() != null && 
                               l.getDescription().toLowerCase().contains(lowerQuery);
                    })
                    .collect(Collectors.toList());
        }
        
        return results;
    }

    public void updateListing(UUID partyId, String title, String description,
                              int minLevel, int maxLevel, List<String> requiredRoles,
                              PartyPurpose purpose) {
        PartyListing listing = load(partyId);
        if (listing == null) return;
        
        if (title != null) listing.setTitle(title);
        if (description != null) listing.setDescription(description);
        if (minLevel >= 0) listing.setMinLevel(minLevel);
        if (maxLevel >= 0) listing.setMaxLevel(maxLevel);
        if (requiredRoles != null) listing.setRequiredRoles(requiredRoles);
        if (purpose != null) listing.setPurpose(purpose);
        
        save(listing);
    }

    public void extendListing(UUID partyId, long additionalTime) {
        PartyListing listing = load(partyId);
        if (listing == null) return;
        
        long newExpireTime = listing.getExpireTime() + additionalTime;
        listing. setExpireTime(newExpireTime);
        
        save(listing);
    }

    public int getListingCount() {
        return dataManager.getListingCache().size();
    }

    public int getActiveListingCount() {
        return getActiveListings().size();
    }

    public Map<PartyPurpose, Integer> getListingCountByPurpose() {
        Map<PartyPurpose, Integer> counts = new HashMap<>();
        
        for (PartyPurpose purpose : PartyPurpose. values()) {
            counts.put(purpose, 0);
        }
        
        for (PartyListing listing : getActiveListings()) {
            PartyPurpose purpose = listing.getPurpose();
            counts.put(purpose, counts.getOrDefault(purpose, 0) + 1);
        }
        
        return counts;
    }

    public List<PartyListing> getRecentListings(int limit) {
        List<PartyListing> listings = getSortedListingsByCreatedTime(true);
        
        if (listings.size() > limit) {
            return listings.subList(0, limit);
        }
        
        return listings;
    }

    public void deleteListingsForParty(UUID partyId) {
        delete(partyId);
    }

    public void deleteAllExpired() {
        cleanupExpiredListings();
    }
}