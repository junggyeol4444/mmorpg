package com. multiverse.trade. gui;

import com.multiverse.trade.TradeCore;
import com. multiverse.trade. models.MarketOrder;
import com.multiverse.trade.models.OrderType;
import com.multiverse.trade.utils.ItemUtil;
import com.multiverse.trade.utils.MessageUtil;
import com. multiverse.trade. utils.NumberUtil;
import com.multiverse.trade.utils.TimeUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity.Player;
import org.bukkit. inventory.Inventory;
import org.bukkit.inventory. InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util. ArrayList;
import java. util.Arrays;
import java. util.List;

public class MarketOrderGUI implements InventoryHolder {

    private final TradeCore plugin;
    private final MarketOrder order;
    private final Inventory inventory;
    private int selectedAmount;

    public MarketOrderGUI(TradeCore plugin, MarketOrder order) {
        this. plugin = plugin;
        this.order = order;
        this.selectedAmount = 1;
        
        String title = MessageUtil.color("&8주문 상세");
        this.inventory = Bukkit.createInventory(this, 54, title);
        
        initialize();
    }

    private void initialize() {
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, createItem(Material. GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
        }

        updateDisplay();
    }

    private void updateDisplay() {
        ItemStack display = order.getItem().clone();
        display.setAmount(Math.min(selectedAmount, 64));
        inventory.setItem(13, display);

        String playerName = Bukkit.getOfflinePlayer(order. getPlayer()).getName();
        String typeColor = order.getType() == OrderType. SELL ? "&c" : "&a";
        String typeName = order.getType().getDisplayName();

        inventory.setItem(22, createItem(Material. PAPER, "&e주문 정보",
            Arrays.asList(
                typeColor + "[" + typeName + "]",
                "&7등록자: &f" + playerName,
                "&7단가: &a" + NumberUtil. format(order.getPricePerUnit()),
                "&7남은 수량: &e" + order.getRemainingAmount() + "개",
                "&7남은 시간: &c" + TimeUtil. formatDuration(order.getTimeRemaining())
            )));

        inventory.setItem(29, createItem(Material.RED_STAINED_GLASS_PANE, "&c-10",
            Arrays.asList("&7클릭하여 10개 감소")));
        inventory.setItem(30, createItem(Material. ORANGE_STAINED_GLASS_PANE, "&6-1",
            Arrays. asList("&7클릭하여 1개 감소")));

        double totalPrice = order.getPricePerUnit() * selectedAmount;
        inventory.setItem(31, createItem(Material.GOLD_INGOT, "&e선택 수량:  &f" + selectedAmount,
            Arrays.asList(
                "&7총 금액: &a" + NumberUtil.format(totalPrice),
                "",
                "&7최대:  " + order.getRemainingAmount() + "개"
            )));

        inventory.setItem(32, createItem(Material.LIME_STAINED_GLASS_PANE, "&a+1",
            Arrays.asList("&7클릭하여 1개 증가")));
        inventory.setItem(33, createItem(Material.GREEN_STAINED_GLASS_PANE, "&2+10",
            Arrays. asList("&7클릭하여 10개 증가")));

        inventory.setItem(38, createItem(Material.HOPPER, "&e최소",
            Arrays.asList("&7클릭하여 1개로 설정")));
        inventory.setItem(42, createItem(Material.CHEST, "&e최대",
            Arrays.asList("&7클릭하여 최대로 설정")));

        String actionText = order.getType() == OrderType.SELL ? "&a구매하기" : "&a판매하기";
        inventory.setItem(49, createItem(Material.EMERALD_BLOCK, actionText,
            Arrays.asList(
                "&7수량: &f" + selectedAmount + "개",
                "&7총 금액: &a" + NumberUtil.format(totalPrice),
                "",
                "&e클릭하여 거래"
            )));
    }

    public void handleClick(Player player, int slot) {
        switch (slot) {
            case 29:
                adjustAmount(-10);
                break;
            case 30:
                adjustAmount(-1);
                break;
            case 32:
                adjustAmount(1);
                break;
            case 33:
                adjustAmount(10);
                break;
            case 38:
                selectedAmount = 1;
                updateDisplay();
                break;
            case 42:
                selectedAmount = order.getRemainingAmount();
                updateDisplay();
                break;
            case 49:
                executeTrade(player);
                break;
        }
    }

    private void adjustAmount(int delta) {
        selectedAmount = Math.max(1, Math.min(order.getRemainingAmount(), selectedAmount + delta));
        updateDisplay();
    }

    private void executeTrade(Player player) {
        if (order. getPlayer().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtil.color("&c자신의 주문과는 거래할 수 없습니다."));
            player.closeInventory();
            return;
        }

        double totalPrice = order.getPricePerUnit() * selectedAmount;

        if (order. getType() == OrderType.SELL) {
            if (! plugin.getEconomy().has(player, totalPrice)) {
                MessageUtil.send(player, "shop.not-enough-money");
                player.closeInventory();
                return;
            }

            double spent = plugin.getMarketManager().instantBuy(player, order.getItem(), selectedAmount);
            if (spent > 0) {
                MessageUtil.send(player, "market.instant-buy",
                    "amount", String.valueOf(selectedAmount),
                    "price", NumberUtil.format(spent));
            }
        } else {
            ItemStack needed = order. getItem().clone();
            needed.setAmount(selectedAmount);

            if (!player. getInventory().containsAtLeast(needed, selectedAmount)) {
                player. sendMessage(MessageUtil.color("&c아이템이 부족합니다."));
                player.closeInventory();
                return;
            }

            double earned = plugin.getMarketManager().instantSell(player, order.getItem(), selectedAmount);
            if (earned > 0) {
                MessageUtil.send(player, "market.instant-sell",
                    "amount", String.valueOf(selectedAmount),
                    "price", NumberUtil. format(earned));
            }
        }

        player.closeInventory();
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

    public MarketOrder getOrder() {
        return order;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}