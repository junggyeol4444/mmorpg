package com. multiverse.trade. events;

import com.multiverse.trade.models.PlayerShop;
import com.multiverse. trade.models.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit. event. Cancellable;
import org.bukkit. event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util. UUID;

public class ShopPurchaseEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final PlayerShop shop;
    private final Player buyer;
    private final ShopItem item;
    private final int amount;
    private double totalPrice;
    private boolean cancelled;

    public ShopPurchaseEvent(PlayerShop shop, Player buyer, ShopItem item, int amount, double totalPrice) {
        this.shop = shop;
        this.buyer = buyer;
        this. item = item;
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.cancelled = false;
    }

    public PlayerShop getShop() {
        return shop;
    }

    public UUID getShopOwner() {
        return shop.getOwner();
    }

    public String getShopName() {
        return shop.getShopName();
    }

    public Player getBuyer() {
        return buyer;
    }

    public ShopItem getItem() {
        return item;
    }

    public ItemStack getItemStack() {
        return item.getItem();
    }

    public int getAmount() {
        return amount;
    }

    public double getPricePerUnit() {
        return item.getPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getRemainingStock() {
        return item.getStock() - amount;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}