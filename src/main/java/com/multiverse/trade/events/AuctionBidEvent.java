package com. multiverse.trade. events;

import com.multiverse.trade.models. Auction;
import org.bukkit.entity.Player;
import org.bukkit. event. Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit. inventory.ItemStack;

import java.util.UUID;

public class AuctionBidEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Auction auction;
    private final Player bidder;
    private double bidAmount;
    private boolean cancelled;

    public AuctionBidEvent(Auction auction, Player bidder, double bidAmount) {
        this. auction = auction;
        this.bidder = bidder;
        this.bidAmount = bidAmount;
        this.cancelled = false;
    }

    public Auction getAuction() {
        return auction;
    }

    public UUID getAuctionId() {
        return auction.getAuctionId();
    }

    public UUID getSellerId() {
        return auction.getSeller();
    }

    public ItemStack getItem() {
        return auction.getItem();
    }

    public Player getBidder() {
        return bidder;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }

    public double getPreviousBid() {
        return auction.getCurrentBid();
    }

    public UUID getPreviousBidder() {
        return auction.getCurrentBidder();
    }

    public double getBuyoutPrice() {
        return auction.getBuyoutPrice();
    }

    public boolean isBuyout() {
        return auction.getBuyoutPrice() > 0 && bidAmount >= auction.getBuyoutPrice();
    }

    public long getTimeRemaining() {
        return auction.getEndTime() - System.currentTimeMillis();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this. cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}