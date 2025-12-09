package com.multiverse.trade.managers;

import com. multiverse.trade. TradeCore;
import com. multiverse.trade. events.AuctionBidEvent;
import com.multiverse. trade.events.AuctionEndEvent;
import com.multiverse.trade.models. Auction;
import com.multiverse.trade.models.AuctionStatus;
import com. multiverse.trade. models. Bid;
import com.multiverse.trade.utils.ItemUtil;
import com.multiverse.trade.utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import org. bukkit.Bukkit;
import org.bukkit. OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util. stream.Collectors;

public class AuctionManager {

    private final TradeCore plugin;
    private final Map<UUID, Auction> auctions = new ConcurrentHashMap<>();
    private final Map<UUID, Double> autoBids = new ConcurrentHashMap<>();

    public AuctionManager(TradeCore plugin) {
        this.plugin = plugin;
        loadAuctions();
    }

    private void loadAuctions() {
        Map<UUID, Auction> loadedAuctions = plugin.getAuctionDataManager().getAllAuctions();
        auctions.putAll(loadedAuctions);
    }

    public Auction createAuction(Player seller, ItemStack item, double startingBid, double buyoutPrice, int durationHours) {
        UUID auctionId = UUID.randomUUID();

        Auction auction = new Auction();
        auction.setAuctionId(auctionId);
        auction.setSeller(seller. getUniqueId());
        auction.setItem(item. clone());
        auction.setStartingBid(startingBid);
        auction.setCurrentBid(startingBid);
        auction.setBuyoutPrice(buyoutPrice);
        auction.setCurrentBidder(null);
        auction.setBidHistory(new ArrayList<>());
        auction.setStartTime(System.currentTimeMillis());
        auction.setEndTime(System.currentTimeMillis() + (durationHours * 60L * 60L * 1000L));
        auction.setDuration(durationHours);
        auction.setStatus(AuctionStatus.ACTIVE);
        auction.setListingFee(plugin.getConfig().getDouble("auction. fees.listing-fee", 100.0));
        auction.setSellerFee(plugin.getConfig().getDouble("auction.fees.seller-fee", 5.0));

        auctions.put(auctionId, auction);
        plugin.getAuctionDataManager().saveAuction(auction);

        return auction;
    }

    public Auction getAuction(UUID auctionId) {
        return auctions.get(auctionId);
    }

    public List<Auction> getActiveAuctions() {
        return auctions.values().stream()
                .filter(a -> a.getStatus() == AuctionStatus.ACTIVE)
                .filter(a -> a. getEndTime() > System.currentTimeMillis())
                .sorted(Comparator. comparingLong(Auction::getEndTime))
                .collect(Collectors. toList());
    }

    public List<Auction> getAllAuctions() {
        return new ArrayList<>(auctions.values());
    }

    public List<Auction> getPlayerAuctions(Player player) {
        return auctions.values().stream()
                .filter(a -> a.getSeller().equals(player.getUniqueId()))
                .sorted(Comparator.comparingLong(Auction::getStartTime).reversed())
                .collect(Collectors. toList());
    }

    public void placeBid(Player bidder, UUID auctionId, double amount) {
        Auction auction = auctions.get(auctionId);
        if (auction == null || auction.getStatus() != AuctionStatus.ACTIVE) {
            return;
        }

        if (auction.getSeller().equals(bidder.getUniqueId())) {
            MessageUtil.send(bidder, "auction.own-auction");
            return;
        }

        double minBid = auction.getCurrentBid() + plugin.getConfig().getDouble("auction.bidding. min-increment", 100.0);
        if (amount < minBid) {
            MessageUtil.send(bidder, "auction.bid-too-low", "min", NumberUtil.format(minBid));
            return;
        }

        if (! plugin.getEconomy().has(bidder, amount)) {
            MessageUtil.send(bidder, "shop.not-enough-money");
            return;
        }

        AuctionBidEvent event = new AuctionBidEvent(auction, bidder, amount);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        UUID previousBidder = auction.getCurrentBidder();
        double previousBid = auction.getCurrentBid();

        if (previousBidder != null && ! previousBidder. equals(bidder. getUniqueId())) {
            OfflinePlayer prevPlayer = Bukkit. getOfflinePlayer(previousBidder);
            plugin.getEconomy().depositPlayer(prevPlayer, previousBid);

            Player prevOnline = Bukkit. getPlayer(previousBidder);
            if (prevOnline != null && prevOnline.isOnline()) {
                String itemName = ItemUtil. getItemName(auction.getItem());
                MessageUtil.send(prevOnline, "auction.outbid", "item", itemName);
            }
        }

        plugin.getEconomy().withdrawPlayer(bidder, amount);

        auction.setCurrentBid(amount);
        auction.setCurrentBidder(bidder.getUniqueId());

        Bid bid = new Bid();
        bid.setBidder(bidder.getUniqueId());
        bid.setAmount(amount);
        bid.setTimestamp(System.currentTimeMillis());
        bid.setAutoBid(false);
        auction.getBidHistory().add(bid);

        checkAutoBids(auction, bidder. getUniqueId());

        plugin.getAuctionDataManager().saveAuction(auction);

        MessageUtil.send(bidder, "auction.bid-placed", "amount", NumberUtil.format(amount));
    }

    private void checkAutoBids(Auction auction, UUID excludeBidder) {
        if (! plugin.getConfig().getBoolean("auction.bidding.auto-bid", true)) {
            return;
        }

        double minIncrement = plugin.getConfig().getDouble("auction.bidding. min-increment", 100.0);

        for (Map.Entry<UUID, Double> entry : autoBids.entrySet()) {
            UUID bidderId = entry.getKey();
            double maxAmount = entry.getValue();

            if (bidderId.equals(excludeBidder) || bidderId. equals(auction.getSeller())) {
                continue;
            }

            if (bidderId.equals(auction.getCurrentBidder())) {
                continue;
            }

            double requiredBid = auction.getCurrentBid() + minIncrement;
            if (requiredBid <= maxAmount) {
                OfflinePlayer autoBidder = Bukkit. getOfflinePlayer(bidderId);
                if (plugin.getEconomy().has(autoBidder, requiredBid)) {
                    UUID previousBidder = auction.getCurrentBidder();
                    double previousBid = auction.getCurrentBid();

                    if (previousBidder != null) {
                        plugin.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(previousBidder), previousBid);
                    }

                    plugin.getEconomy().withdrawPlayer(autoBidder, requiredBid);

                    auction.setCurrentBid(requiredBid);
                    auction.setCurrentBidder(bidderId);

                    Bid bid = new Bid();
                    bid. setBidder(bidderId);
                    bid.setAmount(requiredBid);
                    bid.setTimestamp(System.currentTimeMillis());
                    bid.setAutoBid(true);
                    auction.getBidHistory().add(bid);

                    Player autoBidderOnline = Bukkit.getPlayer(bidderId);
                    if (autoBidderOnline != null) {
                        MessageUtil.send(autoBidderOnline, "auction.bid-placed", "amount", NumberUtil.format(requiredBid));
                    }

                    break;
                }
            }
        }
    }

    public void setAutoBid(Player player, UUID auctionId, double maxAmount) {
        Auction auction = auctions.get(auctionId);
        if (auction == null || auction.getStatus() != AuctionStatus.ACTIVE) {
            return;
        }

        String key = player.getUniqueId() + ":" + auctionId;
        autoBids.put(player.getUniqueId(), maxAmount);

        player.sendMessage(MessageUtil.color("&a자동 입찰이 설정되었습니다. 최대 금액: " + NumberUtil.format(maxAmount)));
    }

    public void buyout(Player buyer, UUID auctionId) {
        Auction auction = auctions. get(auctionId);
        if (auction == null || auction.getStatus() != AuctionStatus.ACTIVE) {
            return;
        }

        if (auction.getBuyoutPrice() <= 0) {
            MessageUtil.send(buyer, "auction.no-buyout");
            return;
        }

        if (auction.getSeller().equals(buyer.getUniqueId())) {
            MessageUtil.send(buyer, "auction.own-auction");
            return;
        }

        double buyoutPrice = auction.getBuyoutPrice();

        if (!plugin. getEconomy().has(buyer, buyoutPrice)) {
            MessageUtil.send(buyer, "shop.not-enough-money");
            return;
        }

        UUID previousBidder = auction.getCurrentBidder();
        double previousBid = auction.getCurrentBid();

        if (previousBidder != null && !previousBidder.equals(buyer.getUniqueId())) {
            plugin.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(previousBidder), previousBid);
        }

        plugin.getEconomy().withdrawPlayer(buyer, buyoutPrice);

        auction.setCurrentBid(buyoutPrice);
        auction.setCurrentBidder(buyer.getUniqueId());
        auction.setStatus(AuctionStatus.SOLD);
        auction.setEndTime(System.currentTimeMillis());

        Bid bid = new Bid();
        bid.setBidder(buyer.getUniqueId());
        bid.setAmount(buyoutPrice);
        bid.setTimestamp(System.currentTimeMillis());
        bid.setAutoBid(false);
        auction.getBidHistory().add(bid);

        completeAuction(auction);

        MessageUtil.send(buyer, "auction.buyout-success");
    }

    public void endAuction(Auction auction) {
        if (auction == null || auction.getStatus() != AuctionStatus. ACTIVE) {
            return;
        }

        if (auction.getCurrentBidder() != null) {
            auction.setStatus(AuctionStatus. SOLD);
            completeAuction(auction);
        } else {
            auction.setStatus(AuctionStatus. EXPIRED);
            expireAuction(auction);
        }
    }

    private void completeAuction(Auction auction) {
        UUID winnerId = auction.getCurrentBidder();
        UUID sellerId = auction.getSeller();
        double finalPrice = auction.getCurrentBid();

        double feeRate = auction.getSellerFee() / 100.0;
        double fee = finalPrice * feeRate;
        double sellerAmount = finalPrice - fee;

        plugin.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(sellerId), sellerAmount);

        if (fee > 0) {
            plugin.getTransactionFeeManager().distributeFee(fee);
        }

        Player winner = Bukkit.getPlayer(winnerId);
        if (winner != null && winner.isOnline()) {
            winner.getInventory().addItem(auction.getItem());
            String itemName = ItemUtil.getItemName(auction.getItem());
            MessageUtil. send(winner, "auction.won", "item", itemName);
        } else {
            plugin.getMailManager().sendSystemMail(
                winnerId,
                "경매 낙찰",
                "축하합니다! 경매에서 낙찰되었습니다.",
                Collections.singletonList(auction.getItem()),
                0, false, 0
            );
        }

        Player seller = Bukkit.getPlayer(sellerId);
        if (seller != null && seller.isOnline()) {
            String itemName = ItemUtil.getItemName(auction.getItem());
            MessageUtil. send(seller, "auction.sold", 
                "item", itemName,
                "price", NumberUtil.format(sellerAmount));
        }

        plugin.getPriceTracker().recordTransaction(auction.getItem(), finalPrice / auction.getItem().getAmount(), auction.getItem().getAmount());

        AuctionEndEvent event = new AuctionEndEvent(auction, true);
        Bukkit.getPluginManager().callEvent(event);

        plugin.getAuctionDataManager().saveAuction(auction);
    }

    private void expireAuction(Auction auction) {
        UUID sellerId = auction.getSeller();

        Player seller = Bukkit.getPlayer(sellerId);
        if (seller != null && seller.isOnline()) {
            seller.getInventory().addItem(auction. getItem());
            String itemName = ItemUtil.getItemName(auction.getItem());
            MessageUtil.send(seller, "auction.expired", "item", itemName);
        } else {
            plugin.getMailManager().sendSystemMail(
                sellerId,
                "경매 유찰",
                "경매가 유찰되어 아이템이 반환됩니다.",
                Collections.singletonList(auction. getItem()),
                0, false, 0
            );
        }

        AuctionEndEvent event = new AuctionEndEvent(auction, false);
        Bukkit.getPluginManager().callEvent(event);

        plugin.getAuctionDataManager().saveAuction(auction);
    }

    public void cancelAuction(UUID auctionId) {
        Auction auction = auctions. get(auctionId);
        if (auction == null) {
            return;
        }

        if (auction.getCurrentBidder() != null) {
            plugin.getEconomy().depositPlayer(
                Bukkit. getOfflinePlayer(auction.getCurrentBidder()),
                auction.getCurrentBid()
            );
        }

        Player seller = Bukkit.getPlayer(auction.getSeller());
        if (seller != null && seller.isOnline()) {
            seller.getInventory().addItem(auction. getItem());
        } else {
            plugin.getMailManager().sendSystemMail(
                auction.getSeller(),
                "경매 취소",
                "경매가 취소되어 아이템이 반환됩니다.",
                Collections.singletonList(auction.getItem()),
                0, false, 0
            );
        }

        auction.setStatus(AuctionStatus.CANCELLED);
        plugin.getAuctionDataManager().saveAuction(auction);
    }

    public List<Auction> searchAuctions(String query) {
        String lowerQuery = query.toLowerCase();
        return auctions.values().stream()
                .filter(a -> a.getStatus() == AuctionStatus.ACTIVE)
                .filter(a -> {
                    String itemName = ItemUtil. getItemName(a.getItem());
                    return itemName.toLowerCase().contains(lowerQuery);
                })
                .sorted(Comparator.comparingLong(Auction::getEndTime))
                .collect(Collectors. toList());
    }

    public List<Auction> sortByPrice(boolean ascending) {
        Comparator<Auction> comparator = Comparator.comparingDouble(Auction:: getCurrentBid);
        if (! ascending) {
            comparator = comparator.reversed();
        }
        return getActiveAuctions().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<Auction> sortByTimeLeft() {
        return getActiveAuctions().stream()
                .sorted(Comparator.comparingLong(Auction:: getEndTime))
                .collect(Collectors. toList());
    }

    public void checkExpiredAuctions() {
        long now = System.currentTimeMillis();
        for (Auction auction :  auctions.values()) {
            if (auction.getStatus() == AuctionStatus.ACTIVE && auction.getEndTime() <= now) {
                endAuction(auction);
            }
        }
    }

    public void saveAllAuctions() {
        for (Auction auction : auctions.values()) {
            plugin.getAuctionDataManager().saveAuction(auction);
        }
    }
}