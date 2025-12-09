package com. multiverse.trade. api;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.managers.*;
import com.multiverse.trade.models.*;
import org.bukkit.entity.Player;
import org.bukkit. inventory.ItemStack;

import java.util. List;
import java.util.UUID;

public class TradeAPI {

    private static TradeCore plugin;

    public static void init(TradeCore tradePlugin) {
        plugin = tradePlugin;
    }

    public static Trade startTrade(Player player1, Player player2) {
        return plugin.getTradeManager().startTrade(player1, player2);
    }

    public static void cancelTrade(Trade trade) {
        plugin.getTradeManager().cancelTrade(trade);
    }

    public static Trade getPlayerTrade(Player player) {
        return plugin.getTradeManager().getTrade(player);
    }

    public static boolean isInTrade(Player player) {
        return plugin.getTradeManager().isInTrade(player);
    }

    public static PlayerShop createShop(Player player, String name) {
        return plugin.getPlayerShopManager().createShop(player, null, name);
    }

    public static void deleteShop(UUID shopId) {
        plugin.getPlayerShopManager().deleteShop(shopId);
    }

    public static PlayerShop getShop(UUID shopId) {
        return plugin.getPlayerShopManager().getShop(shopId);
    }

    public static List<PlayerShop> getPlayerShops(Player player) {
        return plugin.getPlayerShopManager().getPlayerShops(player);
    }

    public static List<PlayerShop> getAllShops() {
        return plugin.getPlayerShopManager().getAllShops();
    }

    public static List<PlayerShop> searchShops(String query) {
        return plugin.getPlayerShopManager().searchShops(query);
    }

    public static boolean buyFromShop(Player buyer, PlayerShop shop, int slot, int amount) {
        return plugin.getPlayerShopManager().buyItem(buyer, shop, slot, amount);
    }

    public static Auction createAuction(Player seller, ItemStack item, double startingBid, 
                                        double buyoutPrice, int durationHours) {
        return plugin.getAuctionManager().createAuction(seller, item, startingBid, buyoutPrice, durationHours);
    }

    public static void placeBid(Player bidder, UUID auctionId, double amount) {
        plugin.getAuctionManager().placeBid(bidder, auctionId, amount);
    }

    public static void buyout(Player buyer, UUID auctionId) {
        plugin. getAuctionManager().buyout(buyer, auctionId);
    }

    public static Auction getAuction(UUID auctionId) {
        return plugin.getAuctionManager().getAuction(auctionId);
    }

    public static List<Auction> getActiveAuctions() {
        return plugin. getAuctionManager().getActiveAuctions();
    }

    public static List<Auction> searchAuctions(String query) {
        return plugin.getAuctionManager().searchAuctions(query);
    }

    public static MarketOrder createSellOrder(Player player, ItemStack item, int amount, double pricePerUnit) {
        return plugin. getMarketManager().createSellOrder(player, item, amount, pricePerUnit);
    }

    public static MarketOrder createBuyOrder(Player player, ItemStack item, int amount, double pricePerUnit) {
        return plugin.getMarketManager().createBuyOrder(player, item, amount, pricePerUnit);
    }

    public static void cancelOrder(UUID orderId) {
        plugin.getMarketManager().cancelOrder(orderId);
    }

    public static double instantSell(Player player, ItemStack item, int amount) {
        return plugin.getMarketManager().instantSell(player, item, amount);
    }

    public static double instantBuy(Player player, ItemStack item, int amount) {
        return plugin.getMarketManager().instantBuy(player, item, amount);
    }

    public static List<MarketOrder> getPlayerOrders(Player player) {
        return plugin.getMarketManager().getPlayerOrders(player);
    }

    public static void sendMail(UUID sender, UUID receiver, String subject, String message,
                                List<ItemStack> attachments, double money) {
        plugin.getMailManager().sendMail(sender, receiver, subject, message, attachments, money, false, 0);
    }

    public static void sendSystemMail(UUID receiver, String subject, String message) {
        plugin.getMailManager().sendSystemMail(receiver, subject, message);
    }

    public static void sendSystemMail(UUID receiver, String subject, String message,
                                      List<ItemStack> attachments, double money) {
        plugin.getMailManager().sendSystemMail(receiver, subject, message, attachments, money, false, 0);
    }

    public static List<Mail> getInbox(Player player) {
        return plugin.getMailManager().getInbox(player);
    }

    public static int getUnreadMailCount(Player player) {
        return plugin.getMailManager().getUnreadCount(player);
    }

    public static void claimMailAttachments(Player player, UUID mailId) {
        plugin.getMailManager().claimAttachments(player, mailId);
    }

    public static double getCurrentPrice(ItemStack item) {
        return plugin.getPriceTracker().getCurrentPrice(item);
    }

    public static double getAveragePrice(ItemStack item, int days) {
        return plugin.getPriceTracker().getAveragePrice(item, days);
    }

    public static MarketPrice getMarketPrice(ItemStack item) {
        return plugin. getPriceTracker().getMarketPrice(item);
    }

    public static PriceTrend getPriceTrend(ItemStack item) {
        return plugin.getPriceTracker().getTrend(item);
    }

    public static void addToBlacklist(UUID player, UUID target) {
        plugin.getTradeSecurityManager().addToBlacklist(player, target);
    }

    public static void removeFromBlacklist(UUID player, UUID target) {
        plugin.getTradeSecurityManager().removeFromBlacklist(player, target);
    }

    public static boolean isBlacklisted(UUID player, UUID target) {
        return plugin.getTradeSecurityManager().isBlacklisted(player, target);
    }

    public static PlayerTradeData getPlayerTradeData(UUID playerId) {
        return plugin.getPlayerTradeDataManager().getPlayerData(playerId);
    }

    public static TradeCore getPlugin() {
        return plugin;
    }
}