package com. multiverse.trade. models;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java. util.List;
import java.util. UUID;

public class Auction {

    private UUID auctionId;
    private UUID seller;
    private ItemStack item;
    
    private double startingBid;
    private double currentBid;
    private double buyoutPrice;
    
    private UUID currentBidder;
    private List<Bid> bidHistory;
    
    private long startTime;
    private long endTime;
    private int duration;
    
    private AuctionStatus status;
    
    private double listingFee;
    private double sellerFee;

    public Auction() {
        this. bidHistory = new ArrayList<>();
        this.startingBid = 0;
        this. currentBid = 0;
        this.buyoutPrice = 0;
        this.currentBidder = null;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.duration = 24;
        this. status = AuctionStatus.ACTIVE;
        this. listingFee = 0;
        this.sellerFee = 5.0;
    }

    public UUID getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(UUID auctionId) {
        this.auctionId = auctionId;
    }

    public UUID getSeller() {
        return seller;
    }

    public void setSeller(UUID seller) {
        this.seller = seller;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public double getStartingBid() {
        return startingBid;
    }

    public void setStartingBid(double startingBid) {
        this.startingBid = startingBid;
        if (this.currentBid == 0) {
            this.currentBid = startingBid;
        }
    }

    public double getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(double currentBid) {
        this.currentBid = currentBid;
    }

    public double getBuyoutPrice() {
        return buyoutPrice;
    }

    public void setBuyoutPrice(double buyoutPrice) {
        this.buyoutPrice = buyoutPrice;
    }

    public UUID getCurrentBidder() {
        return currentBidder;
    }

    public void setCurrentBidder(UUID currentBidder) {
        this.currentBidder = currentBidder;
    }

    public List<Bid> getBidHistory() {
        return bidHistory;
    }

    public void setBidHistory(List<Bid> bidHistory) {
        this. bidHistory = bidHistory;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public double getListingFee() {
        return listingFee;
    }

    public void setListingFee(double listingFee) {
        this.listingFee = listingFee;
    }

    public double getSellerFee() {
        return sellerFee;
    }

    public void setSellerFee(double sellerFee) {
        this.sellerFee = sellerFee;
    }

    public void addBid(Bid bid) {
        bidHistory.add(bid);
        this.currentBid = bid.getAmount();
        this.currentBidder = bid.getBidder();
    }

    public int getBidCount() {
        return bidHistory.size();
    }

    public Bid getLastBid() {
        if (bidHistory.isEmpty()) {
            return null;
        }
        return bidHistory.get(bidHistory.size() - 1);
    }

    public boolean hasBids() {
        return ! bidHistory.isEmpty();
    }

    public boolean hasBuyout() {
        return buyoutPrice > 0;
    }

    public boolean isActive() {
        return status == AuctionStatus.ACTIVE && System.currentTimeMillis() < endTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= endTime;
    }

    public long getTimeRemaining() {
        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    public double getMinNextBid(double minIncrement) {
        return currentBid + minIncrement;
    }

    public double calculateSellerProceeds() {
        double feeAmount = currentBid * (sellerFee / 100.0);
        return currentBid - feeAmount;
    }

    public String getShortId() {
        return auctionId. toString().substring(0, 8);
    }
}