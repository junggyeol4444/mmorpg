package com.multiverse.item.   events;

import org.bukkit.event.Event;
import org.bukkit.  event.HandlerList;
import org.bukkit.entity.Player;
import com.multiverse.item.  data.CustomItem;
import com.multiverse.item. data.ItemTrade;

public class ItemTradeEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private Player buyer;
    private Player seller;
    private CustomItem item;
    private ItemTrade trade;
    private int price;
    private int taxCost;
    private boolean success;
    private boolean cancelled;
    
    /**
     * 기본 생성자
     */
    public ItemTradeEvent(Player buyer, Player seller, CustomItem item, ItemTrade trade, 
                         int price, int taxCost) {
        this.  buyer = buyer;
        this.  seller = seller;
        this.  item = item;
        this.  trade = trade;
        this.  price = price;
        this.   taxCost = taxCost;
        this. success = false;
        this.  cancelled = false;
    }
    
    // Getters and Setters
    public Player getBuyer() {
        return buyer;
    }
    
    public void setBuyer(Player buyer) {
        this. buyer = buyer;
    }
    
    public Player getSeller() {
        return seller;
    }
    
    public void setSeller(Player seller) {
        this. seller = seller;
    }
    
    public CustomItem getItem() {
        return item;
    }
    
    public void setItem(CustomItem item) {
        this.  item = item;
    }
    
    public ItemTrade getTrade() {
        return trade;
    }
    
    public void setTrade(ItemTrade trade) {
        this. trade = trade;
    }
    
    public int getPrice() {
        return price;
    }
    
    public void setPrice(int price) {
        this. price = Math.max(0, price);
    }
    
    public int getTaxCost() {
        return taxCost;
    }
    
    public void setTaxCost(int taxCost) {
        this.taxCost = Math. max(0, taxCost);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    /**
     * 판매자가 받을 금액
     */
    public int getSellerProceeds() {
        return price - taxCost;
    }
    
    /**
     * 세금
     */
    public double getTaxRate() {
        if (price == 0) {
            return 0;
        }
        return (taxCost / (double) price) * 100.  0;
    }
    
    /**
     * 아이템이 바인딩 가능한지 확인
     */
    public boolean canBind() {
        return item != null && item.  isSoulbound();
    }
    
    /**
     * 거래 가능 여부 확인
     */
    public boolean isTradeValid() {
        return buyer != null && seller != null && item != null && price >= 0;
    }
    
    /**
     * 이벤트 취소
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * 이벤트 취소 설정
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.  cancelled = cancelled;
    }
    
    /**
     * 이벤트 핸들러 리스트 반환
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    /**
     * 핸들러 리스트 반환 (정적)
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    /**
     * 이벤트 정보 출력
     */
    @Override
    public String toString() {
        return "ItemTradeEvent{" +
                "buyer=" + (buyer != null ? buyer.getName() : "null") +
                ", seller=" + (seller != null ?  seller.getName() : "null") +
                ", item=" + (item != null ? item.  getName() : "null") +
                ", price=" + price +
                ", taxCost=" + taxCost +
                ", sellerProceeds=" + getSellerProceeds() +
                ", taxRate=" + String.format("%.1f%%", getTaxRate()) +
                ", success=" + success +
                "}";
    }
}