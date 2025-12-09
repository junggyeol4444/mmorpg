package com.multiverse.trade.gui;

import com.multiverse.trade.TradeCore;
import com.multiverse. trade.models.*;
import com.multiverse.trade.utils.MessageUtil;
import com.multiverse.trade.utils. NumberUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit. event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit. event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util. concurrent.ConcurrentHashMap;

public class GUIManager implements Listener {

    private final TradeCore plugin;
    private final Map<UUID, Object> playerGUIs = new ConcurrentHashMap<>();
    private final Map<UUID, InputSession> inputSessions = new ConcurrentHashMap<>();

    public GUIManager(TradeCore plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openTradeGUI(Player player, Trade trade) {
        TradeGUI gui = new TradeGUI(plugin, trade, player);
        playerGUIs.put(player.getUniqueId(), gui);
        player.openInventory(gui. getInventory());
    }

    public void updateTradeGUI(Player player, Trade trade) {
        Object gui = playerGUIs.get(player.getUniqueId());
        if (gui instanceof TradeGUI) {
            TradeGUI tradeGUI = (TradeGUI) gui;
            tradeGUI.updateItems();
            tradeGUI.updateButtons();
        }
    }

    public void openShopGUI(Player player, PlayerShop shop) {
        ShopGUI gui = new ShopGUI(plugin, shop);
        playerGUIs.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }

    public void openShopManageGUI(Player player, PlayerShop shop) {
        ShopManageGUI gui = new ShopManageGUI(plugin, shop);
        playerGUIs.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }

    public void openShopListGUI(Player player, List<PlayerShop> shops, int page) {
        ShopListGUI gui = new ShopListGUI(plugin, shops, page);
        playerGUIs.put(player.getUniqueId(), gui);
        player.openInventory(gui. getInventory());
    }

    public void openShopSelectGUI(Player player, List<PlayerShop> shops) {
        openShopListGUI(player, shops, 1);
    }

    public void openAuctionGUI(Player player, int page) {
        AuctionGUI gui = new AuctionGUI(plugin, page);
        playerGUIs.put(player.getUniqueId(), gui);
        player.openInventory(gui. getInventory());
    }

    public void openAuctionCreateGUI(Player player) {
        AuctionCreateGUI gui = new AuctionCreateGUI(plugin, player);
        playerGUIs.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }

    public void openAuctionSearchGUI(Player player, List<Auction> results, int page) {
        AuctionGUI gui = new AuctionGUI(plugin, page);
        playerGUIs.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }

    public void openMarketGUI(Player player, int page) {
        MarketGUI gui = new MarketGUI(plugin, page);
        playerGUIs.put(player.getUniqueId(), gui);
        player.openInventory(gui. getInventory());
    }

    public void openMarketOrderGUI(Player player, MarketOrder order) {
        MarketOrderGUI gui = new MarketOrderGUI(plugin, order);
        playerGUIs.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }

    public void openMailGUI(Player player, int page) {
        MailGUI gui = new MailGUI(plugin, player, page);
        playerGUIs. put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }

    public void openMailComposeGUI(Player player) {
        MailComposeGUI gui = new MailComposeGUI(plugin, player);
        playerGUIs. put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }

    public void removePlayerGUI(Player player) {
        playerGUIs.remove(player.getUniqueId());
    }

    public void startMoneyInput(Player player, Trade trade) {
        InputSession session = new InputSession();
        session.type = InputType. TRADE_MONEY;
        session.data = trade;
        inputSessions.put(player.getUniqueId(), session);
    }

    public void startAmountInput(Player player, PlayerShop shop, ShopItem item) {
        InputSession session = new InputSession();
        session.type = InputType. SHOP_AMOUNT;
        session.data = new Object[]{shop, item};
        inputSessions. put(player.getUniqueId(), session);
    }

    public void startPriceInput(Player player, PlayerShop shop, int slot, ItemStack item) {
        InputSession session = new InputSession();
        session.type = InputType.SHOP_PRICE;
        session.data = new Object[]{shop, slot, item};
        inputSessions.put(player. getUniqueId(), session);
    }

    public void startBidInput(Player player, Auction auction) {
        InputSession session = new InputSession();
        session.type = InputType.AUCTION_BID;
        session.data = auction;
        inputSessions.put(player.getUniqueId(), session);
    }

    public void startAuctionSearch(Player player) {
        InputSession session = new InputSession();
        session.type = InputType. AUCTION_SEARCH;
        inputSessions.put(player.getUniqueId(), session);
    }

    public void startAuctionPriceInput(Player player, AuctionCreateGUI gui, AuctionCreateGUI.PriceInputType priceType) {
        InputSession session = new InputSession();
        session.type = priceType == AuctionCreateGUI.PriceInputType.STARTING ? 
            InputType. AUCTION_STARTING_PRICE : InputType.AUCTION_BUYOUT_PRICE;
        session.data = gui;
        inputSessions.put(player.getUniqueId(), session);
    }

    public void startMarketAmountInput(Player player, MarketOrder order) {
        InputSession session = new InputSession();
        session.type = InputType. MARKET_AMOUNT;
        session. data = order;
        inputSessions. put(player.getUniqueId(), session);
    }

    public void startMarketSearch(Player player) {
        InputSession session = new InputSession();
        session.type = InputType. MARKET_SEARCH;
        inputSessions.put(player. getUniqueId(), session);
    }

    public void startMailRecipientInput(Player player, MailComposeGUI gui) {
        InputSession session = new InputSession();
        session.type = InputType. MAIL_RECIPIENT;
        session. data = gui;
        inputSessions. put(player.getUniqueId(), session);
    }

    public void startMailSubjectInput(Player player, MailComposeGUI gui) {
        InputSession session = new InputSession();
        session.type = InputType.MAIL_SUBJECT;
        session.data = gui;
        inputSessions.put(player. getUniqueId(), session);
    }

    public void startMailMoneyInput(Player player, MailComposeGUI gui) {
        InputSession session = new InputSession();
        session.type = InputType. MAIL_MONEY;
        session.data = gui;
        inputSessions.put(player.getUniqueId(), session);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        InputSession session = inputSessions.remove(player.getUniqueId());
        
        if (session == null) {
            return;
        }

        event.setCancelled(true);
        String message = event.getMessage();

        if (message. equalsIgnoreCase("cancel") || message.equalsIgnoreCase("취소")) {
            player.sendMessage(MessageUtil. color("&c입력이 취소되었습니다."));
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> handleInput(player, session, message));
    }

    private void handleInput(Player player, InputSession session, String message) {
        switch (session.type) {
            case TRADE_MONEY:
                handleTradeMoneyInput(player, (Trade) session.data, message);
                break;
            case SHOP_AMOUNT: 
                handleShopAmountInput(player, (Object[]) session.data, message);
                break;
            case SHOP_PRICE:
                handleShopPriceInput(player, (Object[]) session.data, message);
                break;
            case AUCTION_BID:
                handleAuctionBidInput(player, (Auction) session.data, message);
                break;
            case AUCTION_SEARCH:
                handleAuctionSearchInput(player, message);
                break;
            case AUCTION_STARTING_PRICE:
                handleAuctionStartingPriceInput(player, (AuctionCreateGUI) session.data, message);
                break;
            case AUCTION_BUYOUT_PRICE:
                handleAuctionBuyoutPriceInput(player, (AuctionCreateGUI) session.data, message);
                break;
            case MARKET_AMOUNT:
                handleMarketAmountInput(player, (MarketOrder) session.data, message);
                break;
            case MARKET_SEARCH:
                handleMarketSearchInput(player, message);
                break;
            case MAIL_RECIPIENT:
                handleMailRecipientInput(player, (MailComposeGUI) session.data, message);
                break;
            case MAIL_SUBJECT: 
                handleMailSubjectInput(player, (MailComposeGUI) session.data, message);
                break;
            case MAIL_MONEY:
                handleMailMoneyInput(player, (MailComposeGUI) session.data, message);
                break;
        }
    }

    private void handleTradeMoneyInput(Player player, Trade trade, String message) {
        double amount = NumberUtil. parseDouble(message, -1);
        if (amount < 0) {
            player.sendMessage(MessageUtil.color("&c올바른 금액을 입력하세요."));
            return;
        }

        plugin.getTradeManager().setMoney(trade, player, "default", amount);
        openTradeGUI(player, trade);
    }

    private void handleShopAmountInput(Player player, Object[] data, String message) {
        PlayerShop shop = (PlayerShop) data[0];
        ShopItem item = (ShopItem) data[1];
        
        int amount = NumberUtil.parseInt(message, -1);
        if (amount <= 0) {
            player.sendMessage(MessageUtil.color("&c올바른 수량을 입력하세요."));
            openShopGUI(player, shop);
            return;
        }

        plugin. getPlayerShopManager().buyItem(player, shop, item. getSlot(), amount);
        openShopGUI(player, shop);
    }

    private void handleShopPriceInput(Player player, Object[] data, String message) {
        PlayerShop shop = (PlayerShop) data[0];
        int slot = (int) data[1];
        ItemStack item = (ItemStack) data[2];
        
        double price = NumberUtil. parseDouble(message, -1);
        if (price <= 0) {
            player.sendMessage(MessageUtil.color("&c올바른 가격을 입력하세요."));
            player.getInventory().addItem(item);
            openShopManageGUI(player, shop);
            return;
        }

        plugin.getPlayerShopManager().addItem(shop, slot, item, price, item.getAmount());
        openShopManageGUI(player, shop);
    }

    private void handleAuctionBidInput(Player player, Auction auction, String message) {
        double amount = NumberUtil. parseDouble(message, -1);
        if (amount <= 0) {
            player.sendMessage(MessageUtil.color("&c올바른 금액을 입력하세요."));
            openAuctionGUI(player, 1);
            return;
        }

        plugin.getAuctionManager().placeBid(player, auction. getAuctionId(), amount);
        openAuctionGUI(player, 1);
    }

    private void handleAuctionSearchInput(Player player, String query) {
        List<Auction> results = plugin.getAuctionManager().searchAuctions(query);
        openAuctionSearchGUI(player, results, 1);
    }

    private void handleAuctionStartingPriceInput(Player player, AuctionCreateGUI gui, String message) {
        double price = NumberUtil.parseDouble(message, -1);
        if (price <= 0) {
            player.sendMessage(MessageUtil.color("&c올바른 가격을 입력하세요."));
        } else {
            gui.setStartingPriceValue(price);
        }
        player.openInventory(gui. getInventory());
    }

    private void handleAuctionBuyoutPriceInput(Player player, AuctionCreateGUI gui, String message) {
        double price = NumberUtil.parseDouble(message, 0);
        gui.setBuyoutPriceValue(price);
        player.openInventory(gui.getInventory());
    }

    private void handleMarketAmountInput(Player player, MarketOrder order, String message) {
        int amount = NumberUtil. parseInt(message, -1);
        if (amount <= 0) {
            player.sendMessage(MessageUtil.color("&c올바른 수량을 입력하세요. "));
            openMarketGUI(player, 1);
            return;
        }

        openMarketOrderGUI(player, order);
    }

    private void handleMarketSearchInput(Player player, String query) {
        openMarketGUI(player, 1);
    }

    private void handleMailRecipientInput(Player player, MailComposeGUI gui, String message) {
        gui.setRecipientValue(message);
        gui.refresh();
        player.openInventory(gui.getInventory());
    }

    private void handleMailSubjectInput(Player player, MailComposeGUI gui, String message) {
        gui.setSubjectValue(message);
        gui.refresh();
        player.openInventory(gui.getInventory());
    }

    private void handleMailMoneyInput(Player player, MailComposeGUI gui, String message) {
        double amount = NumberUtil. parseDouble(message, 0);
        if (amount < 0) {
            player.sendMessage(MessageUtil.color("&c올바른 금액을 입력하세요."));
        } else {
            gui.setMoneyValue(amount);
        }
        gui.refresh();
        player.openInventory(gui.getInventory());
    }

    private static class InputSession {
        InputType type;
        Object data;
    }

    private enum InputType {
        TRADE_MONEY,
        SHOP_AMOUNT,
        SHOP_PRICE,
        AUCTION_BID,
        AUCTION_SEARCH,
        AUCTION_STARTING_PRICE,
        AUCTION_BUYOUT_PRICE,
        MARKET_AMOUNT,
        MARKET_SEARCH,
        MAIL_RECIPIENT,
        MAIL_SUBJECT,
        MAIL_MONEY
    }
}