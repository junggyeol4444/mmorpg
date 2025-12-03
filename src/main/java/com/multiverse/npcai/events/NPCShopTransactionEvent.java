package com.multiverse.npcai.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;

/**
 * NPC 상점 거래 이벤트 (구매/판매)
 */
public class NPCShopTransactionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final int npcId;
    private final String shopId;
    private final String itemId;
    private final int amount;
    private final double price;
    private final String transactionType; // BUY, SELL 등

    public NPCShopTransactionEvent(Player player, int npcId, String shopId, String itemId, int amount, double price, String transactionType) {
        this.player = player;
        this.npcId = npcId;
        this.shopId = shopId;
        this.itemId = itemId;
        this.amount = amount;
        this.price = price;
        this.transactionType = transactionType;
    }

    public Player getPlayer() { return player; }
    public int getNpcId() { return npcId; }
    public String getShopId() { return shopId; }
    public String getItemId() { return itemId; }
    public int getAmount() { return amount; }
    public double getPrice() { return price; }
    public String getTransactionType() { return transactionType; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}