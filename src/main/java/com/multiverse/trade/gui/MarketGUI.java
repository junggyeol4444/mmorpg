package com. multiverse.trade. gui;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.models.MarketOrder;
import com. multiverse.trade. models.OrderType;
import com.multiverse.trade.utils.ItemUtil;
import com.multiverse.trade.utils.MessageUtil;
import com. multiverse.trade. utils.NumberUtil;
import com.multiverse.trade.utils.TimeUtil;
import org. bukkit.Bukkit;
import org.bukkit.Material;
import org. bukkit.entity. Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit. inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util. stream.Collectors;

public class MarketGUI implements InventoryHolder {

    private final TradeCore plugin;
    private List<MarketOrder> orders;
    private final Inventory inventory;
    private int currentPage;
    private final int ordersPerPage = 45;
    private OrderType filterType = null;

    public MarketGUI(TradeCore plugin, int page) {
        this.plugin = plugin;
        this. currentPage = page;
        
        String title = MessageUtil.color(plugin.getConfig().getString("gui.market.title", "&8거래소"));
        this.inventory = Bukkit.createInventory(this, 54, title);
        
        refreshOrders();
        initialize();
    }

    private void refreshOrders() {
        Map<UUID, MarketOrder> allOrders = plugin.getMarketDataManager().getAllOrders();
        
        orders = allOrders.values().stream()
                .filter(o -> o.isActive())
                .filter(o -> filterType == null || o.getType() == filterType)
                .sorted(Comparator. comparingDouble(MarketOrder::getPricePerUnit))
                .collect(Collectors. toList());
    }

    private void initialize() {
        updateOrders();
        updateNavigation();
    }

    public void updateOrders() {
        for (int i = 0; i < ordersPerPage; i++) {
            inventory.setItem(i, null);
        }

        int start = (currentPage - 1) * ordersPerPage;
        int end = Math.min(start + ordersPerPage, orders.size());

        for (int i = start; i < end; i++) {
            MarketOrder order = orders.get(i);
            ItemStack display = createOrderDisplayItem(order);
            inventory.setItem(i - start, display);
        }
    }

    private ItemStack createOrderDisplayItem(MarketOrder order) {
        ItemStack display = order.getItem().clone();
        ItemMeta meta = display. getItemMeta();
        
        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta. getLore()) : new ArrayList<>();
            lore.add("");
            
            String typeColor = order.getType() == OrderType. SELL ? "&c" : "&a";
            String typeName = order.getType().getDisplayName();
            lore.add(MessageUtil.color(typeColor + "[" + typeName + "]"));
            
            String playerName = Bukkit.getOfflinePlayer(order.getPlayer()).getName();
            lore.add(MessageUtil.color("&7등록자: &f" + playerName));
            lore.add(MessageUtil.color("&7단가: &a" + NumberUtil. format(order.getPricePerUnit())));
            lore.add(MessageUtil.color("&7수량: &e" + order.getRemainingAmount() + "/" + order.getAmount()));
            lore.add(MessageUtil.color("&7총액: &6" + NumberUtil. format(order.getRemainingValue())));
            lore.add(MessageUtil.color("&7남은시간: &c" + TimeUtil.formatDuration(order. getTimeRemaining())));
            lore.add("");
            
            if (order.getType() == OrderType.SELL) {
                lore.add(MessageUtil.color("&e클릭:  &f구매"));
            } else {
                lore.add(MessageUtil.color("&e클릭: &f판매"));
            }
            lore.add(MessageUtil.color("&e쉬프트+클릭: &f수량 지정"));
            
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }

    private void updateNavigation() {
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
        }

        int totalPages = (int) Math.ceil((double) orders.size() / ordersPerPage);
        if (totalPages == 0) totalPages = 1;

        if (currentPage > 1) {
            inventory.setItem(45, createItem(Material.ARROW, "&a이전 페이지",
                Arrays.asList("&7클릭하여 이전 페이지")));
        }

        Material sellMaterial = filterType == OrderType. SELL ? Material. RED_WOOL : Material.RED_STAINED_GLASS;
        inventory.setItem(47, createItem(sellMaterial, "&c판매 주문",
            Arrays.asList(
                "&7" + (filterType == OrderType. SELL ? "&a✔ 선택됨" :  "클릭하여 필터")
            )));

        Material buyMaterial = filterType == OrderType.BUY ? Material.LIME_WOOL : Material.LIME_STAINED_GLASS;
        inventory.setItem(49, createItem(buyMaterial, "&a구매 주문",
            Arrays. asList(
                "&7" + (filterType == OrderType.BUY ?  "&a✔ 선택됨" : "클릭하여 필터")
            )));

        inventory.setItem(51, createItem(Material.COMPASS, "&e검색",
            Arrays.asList("&7클릭하여 검색")));

        if (currentPage < totalPages) {
            inventory.setItem(53, createItem(Material.ARROW, "&a다음 페이지",
                Arrays.asList("&7클릭하여 다음 페이지")));
        }
    }

    public void handleOrderClick(Player player, int slot, boolean shiftClick) {
        int index = (currentPage - 1) * ordersPerPage + slot;
        
        if (index >= orders.size()) {
            return;
        }

        MarketOrder order = orders.get(index);

        if (order. getPlayer().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtil.color("&c자신의 주문과는 거래할 수 없습니다."));
            return;
        }

        if (shiftClick) {
            player.sendMessage(MessageUtil.color("&e거래할 수량을 채팅창에 입력하세요.  (취소: cancel)"));
            plugin.getGuiManager().startMarketAmountInput(player, order);
            player.closeInventory();
        } else {
            executeInstantTrade(player, order, 1);
        }
    }

    private void executeInstantTrade(Player player, MarketOrder order, int amount) {
        amount = Math.min(amount, order.getRemainingAmount());
        
        if (order.getType() == OrderType. SELL) {
            double totalCost = order.getPricePerUnit() * amount;
            if (! plugin.getEconomy().has(player, totalCost)) {
                MessageUtil.send(player, "shop.not-enough-money");
                return;
            }

            ItemStack needed = order.getItem().clone();
            needed.setAmount(amount);
            
            plugin.getMarketManager().instantBuy(player, order.getItem(), amount);
        } else {
            ItemStack needed = order. getItem().clone();
            needed.setAmount(amount);
            
            if (!player.getInventory().containsAtLeast(needed, amount)) {
                player.sendMessage(MessageUtil.color("&c아이템이 부족합니다."));
                return;
            }

            plugin.getMarketManager().instantSell(player, order.getItem(), amount);
        }

        refreshOrders();
        updateOrders();
    }

    public void showSellOrders() {
        if (filterType == OrderType.SELL) {
            filterType = null;
        } else {
            filterType = OrderType.SELL;
        }
        currentPage = 1;
        refreshOrders();
        updateOrders();
        updateNavigation();
    }

    public void showBuyOrders() {
        if (filterType == OrderType.BUY) {
            filterType = null;
        } else {
            filterType = OrderType.BUY;
        }
        currentPage = 1;
        refreshOrders();
        updateOrders();
        updateNavigation();
    }

    public void openSearch(Player player) {
        player. sendMessage(MessageUtil.color("&e검색어를 채팅창에 입력하세요. (취소: cancel)"));
        plugin.getGuiManager().startMarketSearch(player);
        player.closeInventory();
    }

    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateOrders();
            updateNavigation();
        }
    }

    public void nextPage() {
        int totalPages = (int) Math.ceil((double) orders.size() / ordersPerPage);
        
        if (currentPage < totalPages) {
            currentPage++;
            updateOrders();
            updateNavigation();
        }
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item. getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName(MessageUtil.color(name));
            
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore. add(MessageUtil. color(line));
            }
            meta.setLore(coloredLore);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}