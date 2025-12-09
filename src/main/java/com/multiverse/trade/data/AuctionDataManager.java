package com.multiverse. trade.data;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.models. Auction;
import com.multiverse.trade.models.AuctionStatus;
import com.multiverse.trade.models. Bid;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit. configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit. inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java. util.*;
import java.util.concurrent. ConcurrentHashMap;
import java. util.logging.Level;

public class AuctionDataManager {

    private final TradeCore plugin;
    private final File auctionsFolder;
    private final File activeFile;
    private final File expiredFile;
    private final Map<UUID, Auction> cachedAuctions = new ConcurrentHashMap<>();

    public AuctionDataManager(TradeCore plugin) {
        this.plugin = plugin;
        this.auctionsFolder = new File(plugin.getDataFolder(), "auctions");
        this.activeFile = new File(auctionsFolder, "active.yml");
        this.expiredFile = new File(auctionsFolder, "expired.yml");
        
        if (!auctionsFolder.exists()) {
            auctionsFolder.mkdirs();
        }
    }

    public void loadAll() {
        cachedAuctions.clear();
        
        loadAuctionsFromFile(activeFile);
        loadAuctionsFromFile(expiredFile);

        plugin.getLogger().info("경매 " + cachedAuctions.size() + "개 로드됨");
    }

    private void loadAuctionsFromFile(File file) {
        if (!file.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<?, ?>> auctionsList = config.getMapList("auctions");

        for (Map<?, ?> auctionMap : auctionsList) {
            try {
                Auction auction = deserializeAuction(auctionMap);
                if (auction != null) {
                    cachedAuctions. put(auction.getAuctionId(), auction);
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "경매 로드 실패", e);
            }
        }
    }

    private Auction deserializeAuction(Map<?, ?> map) {
        Auction auction = new Auction();
        
        auction.setAuctionId(UUID.fromString((String) map.get("auction-id")));
        auction.setSeller(UUID.fromString((String) map.get("seller")));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> itemData = (Map<String, Object>) map.get("item");
        if (itemData != null) {
            auction.setItem(ItemStack.deserialize(itemData));
        }

        auction.setStartingBid(((Number) map.get("starting-bid")).doubleValue());
        auction.setCurrentBid(((Number) map.get("current-bid")).doubleValue());
        auction.setBuyoutPrice(((Number) map.getOrDefault("buyout-price", 0.0)).doubleValue());
        
        String bidderStr = (String) map.get("current-bidder");
        if (bidderStr != null && !bidderStr. isEmpty()) {
            auction.setCurrentBidder(UUID.fromString(bidderStr));
        }

        auction.setStartTime(((Number) map.get("start-time")).longValue());
        auction.setEndTime(((Number) map.get("end-time")).longValue());
        auction.setDuration(((Number) map.getOrDefault("duration", 24)).intValue());
        auction.setStatus(AuctionStatus.valueOf((String) map.get("status")));
        auction.setListingFee(((Number) map.getOrDefault("listing-fee", 0.0)).doubleValue());
        auction.setSellerFee(((Number) map.getOrDefault("seller-fee", 5.0)).doubleValue());

        List<Bid> bidHistory = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<?, ?>> bidsList = (List<Map<?, ?>>) map.get("bid-history");
        if (bidsList != null) {
            for (Map<?, ? > bidMap : bidsList) {
                Bid bid = new Bid();
                bid.setBidder(UUID.fromString((String) bidMap.get("bidder")));
                bid.setAmount(((Number) bidMap.get("amount")).doubleValue());
                bid.setTimestamp(((Number) bidMap.get("timestamp")).longValue());
                bid.setAutoBid((Boolean) bidMap.getOrDefault("is-auto-bid", false));
                bidHistory.add(bid);
            }
        }
        auction.setBidHistory(bidHistory);

        return auction;
    }

    public void saveAuction(Auction auction) {
        if (auction == null) {
            return;
        }

        cachedAuctions. put(auction.getAuctionId(), auction);
        saveAll();
    }

    public void saveAll() {
        List<Map<String, Object>> activeList = new ArrayList<>();
        List<Map<String, Object>> expiredList = new ArrayList<>();

        for (Auction auction : cachedAuctions. values()) {
            Map<String, Object> auctionMap = serializeAuction(auction);
            
            if (auction.getStatus() == AuctionStatus. ACTIVE) {
                activeList.add(auctionMap);
            } else {
                expiredList.add(auctionMap);
            }
        }

        saveToFile(activeFile, activeList);
        saveToFile(expiredFile, expiredList);
    }

    private Map<String, Object> serializeAuction(Auction auction) {
        Map<String, Object> map = new LinkedHashMap<>();
        
        map.put("auction-id", auction.getAuctionId().toString());
        map.put("seller", auction. getSeller().toString());
        
        if (auction.getItem() != null) {
            map.put("item", auction.getItem().serialize());
        }

        map.put("starting-bid", auction.getStartingBid());
        map.put("current-bid", auction.getCurrentBid());
        map.put("buyout-price", auction.getBuyoutPrice());
        map.put("current-bidder", auction.getCurrentBidder() != null ? auction. getCurrentBidder().toString() : "");
        map.put("start-time", auction.getStartTime());
        map.put("end-time", auction.getEndTime());
        map.put("duration", auction.getDuration());
        map.put("status", auction.getStatus().name());
        map.put("listing-fee", auction.getListingFee());
        map.put("seller-fee", auction.getSellerFee());

        List<Map<String, Object>> bidsList = new ArrayList<>();
        for (Bid bid :  auction.getBidHistory()) {
            Map<String, Object> bidMap = new LinkedHashMap<>();
            bidMap.put("bidder", bid. getBidder().toString());
            bidMap.put("amount", bid.getAmount());
            bidMap. put("timestamp", bid.getTimestamp());
            bidMap. put("is-auto-bid", bid. isAutoBid());
            bidsList.add(bidMap);
        }
        map.put("bid-history", bidsList);

        return map;
    }

    private void saveToFile(File file, List<Map<String, Object>> list) {
        FileConfiguration config = new YamlConfiguration();
        config.set("auctions", list);

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "경매 저장 실패: " + file. getName(), e);
        }
    }

    public Auction getAuction(UUID auctionId) {
        return cachedAuctions.get(auctionId);
    }

    public Map<UUID, Auction> getAllAuctions() {
        return new HashMap<>(cachedAuctions);
    }

    public void deleteAuction(UUID auctionId) {
        cachedAuctions.remove(auctionId);
        saveAll();
    }
}