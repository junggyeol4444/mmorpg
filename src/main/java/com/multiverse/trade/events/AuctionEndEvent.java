package com. multiverse.trade. events;

import com.multiverse.trade.models.Auction;
import com.multiverse.trade.models.AuctionStatus;
import org. bukkit.event.Event;
import org.bukkit. event.HandlerList;
import org. bukkit.inventory.ItemStack;

import java.util. UUID;

public class AuctionEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Auction auction;
    private final boolean sold;

    public AuctionEndEvent(Auction auction, boolean sold) {
        this. auction = auction;
        this.sold = sold;
    }

    public Auction getAuction() {
        return auction;
    }

    public UUID getAuctionId() {
        return auction.getAuctionId();
    }

    public UUID getSellerId() {
        return auction. getSeller();
    }

    public ItemStack getItem() {
        return auction. getItem();
    }

    public boolean isSold() {
        return sold;
    }

    public boolean isExpired() {
        return ! sold;
    }

    public UUID getWinnerId() {
        return sold ? auction.getCurrentBidder() : null;
    }

    public double getFinalPrice() {
        return sold ? auction.getCurrentBid() : 0;
    }

    public double getStartingBid() {
        return auction.getStartingBid();
    }

    public int getTotalBids() {
        return auction. getBidHistory().size();
    }

    public long getDuration() {
        return auction.getEndTime() - auction.getStartTime();
    }

    public AuctionStatus getStatus() {
        return auction.getStatus();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}