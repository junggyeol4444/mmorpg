package com.multiverse. trade.gui;

import com.multiverse.trade.TradeCore;
import com. multiverse.trade. models.Trade;
import com. multiverse.trade. models.TradeStatus;
import com. multiverse.trade. utils.ItemUtil;
import com.multiverse.trade.utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity.Player;
import org.bukkit. inventory.Inventory;
import org.bukkit.inventory. InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org. bukkit.inventory. meta.ItemMeta;

import java.util. ArrayList;
import java. util.Arrays;
import java. util.List;
import java.util. UUID;

public class TradeGUI implements InventoryHolder {

    private final TradeCore plugin;
    private final Trade trade;
    private final Player player;
    private final Inventory inventory;

    private static final int[] PLAYER1_SLOTS = {0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30};
    private static final int[] PLAYER2_SLOTS = {5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35};
    private static final int[] DIVIDER_SLOTS = {4, 13, 22, 31};
    
    private static final int READY_BUTTON_SLOT = 36;
    private static final int MONEY_BUTTON_SLOT = 40;
    private static final int CANCEL_BUTTON_SLOT = 44;
    private static final int STATUS_SLOT = 49;

    public TradeGUI(TradeCore plugin, Trade trade, Player player) {
        this. plugin = plugin;
        this.trade = trade;
        this.player = player;
        
        String title = MessageUtil.color(plugin.getConfig().getString("gui.trade.title", "&8안전 거래"));
        this.inventory = Bukkit.createInventory(this, 54, title);
        
        initialize();
    }

    private void initialize() {
        ItemStack divider = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", new ArrayList<>());
        for (int slot : DIVIDER_SLOTS) {
            inventory.setItem(slot, divider);
        }

        for (int i = 36; i < 54; i++) {
            if (i != READY_BUTTON_SLOT && i != MONEY_BUTTON_SLOT && 
                i != CANCEL_BUTTON_SLOT && i != STATUS_SLOT) {
                inventory. setItem(i, createItem(Material. GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
            }
        }

        updateButtons();
        updateItems();
    }

    public void updateButtons() {
        boolean isPlayer1 = trade.getPlayer1().equals(player.getUniqueId());
        boolean isReady = isPlayer1 ? trade.isPlayer1Ready() : trade.isPlayer2Ready();

        ItemStack readyButton;
        if (isReady) {
            readyButton = createItem(Material. LIME_WOOL, "&a확인 완료", 
                Arrays.asList("&7클릭하여 확인 취소"));
        } else {
            readyButton = createItem(Material.RED_WOOL, "&c확인하기",
                Arrays.asList("&7클릭하여 거래 확인"));
        }
        inventory.setItem(READY_BUTTON_SLOT, readyButton);

        double myMoney = isPlayer1 ? trade.getPlayer1Money() : trade.getPlayer2Money();
        ItemStack moneyButton = createItem(Material. GOLD_INGOT, "&6금액 설정",
            Arrays.asList(
                "&7현재 설정: &a" + NumberUtil.format(myMoney),
                "",
                "&e클릭하여 금액 변경"
            ));
        inventory. setItem(MONEY_BUTTON_SLOT, moneyButton);

        ItemStack cancelButton = createItem(Material.BARRIER, "&c거래 취소",
            Arrays.asList("&7클릭하여 거래 취소"));
        inventory.setItem(CANCEL_BUTTON_SLOT, cancelButton);

        updateStatus();
    }

    private void updateStatus() {
        String otherName = Bukkit.getOfflinePlayer(trade.getOtherPlayerUUID(player.getUniqueId())).getName();
        boolean isPlayer1 = trade.getPlayer1().equals(player.getUniqueId());
        
        boolean myReady = isPlayer1 ? trade. isPlayer1Ready() : trade.isPlayer2Ready();
        boolean otherReady = isPlayer1 ? trade.isPlayer2Ready() : trade.isPlayer1Ready();

        String myStatus = myReady ? "&a✔" : "&c✖";
        String otherStatus = otherReady ? "&a✔" : "&c✖";

        ItemStack statusItem = createItem(Material. PAPER, "&e거래 상태",
            Arrays. asList(
                "&7내 상태: " + myStatus,
                "&7" + otherName + ": " + otherStatus,
                "",
                "&7상태:  &f" + trade.getStatus().getDisplayName()
            ));
        inventory.setItem(STATUS_SLOT, statusItem);
    }

    public void updateItems() {
        List<ItemStack> player1Items = trade.getPlayer1Items();
        List<ItemStack> player2Items = trade.getPlayer2Items();

        for (int i = 0; i < PLAYER1_SLOTS.length; i++) {
            if (i < player1Items.size()) {
                inventory.setItem(PLAYER1_SLOTS[i], player1Items.get(i));
            } else {
                inventory.setItem(PLAYER1_SLOTS[i], null);
            }
        }

        for (int i = 0; i < PLAYER2_SLOTS.length; i++) {
            if (i < player2Items.size()) {
                inventory.setItem(PLAYER2_SLOTS[i], player2Items.get(i));
            } else {
                inventory.setItem(PLAYER2_SLOTS[i], null);
            }
        }

        updateMoneDisplay();
    }

    private void updateMoneDisplay() {
        double player1Money = trade. getPlayer1Money();
        double player2Money = trade.getPlayer2Money();

        boolean isPlayer1 = trade.getPlayer1().equals(player.getUniqueId());

        if (player1Money > 0) {
            ItemStack moneyDisplay = createItem(Material.GOLD_NUGGET, 
                "&6" + (isPlayer1 ? "내 금액" :  "상대 금액"),
                Arrays.asList("&a" + NumberUtil. format(player1Money)));
            inventory. setItem(37, moneyDisplay);
        } else {
            inventory.setItem(37, createItem(Material. GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
        }

        if (player2Money > 0) {
            ItemStack moneyDisplay = createItem(Material. GOLD_NUGGET,
                "&6" + (isPlayer1 ? "상대 금액" : "내 금액"),
                Arrays.asList("&a" + NumberUtil.format(player2Money)));
            inventory.setItem(43, moneyDisplay);
        } else {
            inventory.setItem(43, createItem(Material. GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
        }
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName(MessageUtil.color(name));
            
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(MessageUtil.color(line));
            }
            meta. setLore(coloredLore);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    public Trade getTrade() {
        return trade;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isPlayerSlot(int slot) {
        boolean isPlayer1 = trade.getPlayer1().equals(player.getUniqueId());
        int[] mySlots = isPlayer1 ?  PLAYER1_SLOTS : PLAYER2_SLOTS;
        
        for (int mySlot : mySlots) {
            if (mySlot == slot) {
                return true;
            }
        }
        return false;
    }

    public int getItemSlotIndex(int inventorySlot) {
        boolean isPlayer1 = trade. getPlayer1().equals(player.getUniqueId());
        int[] mySlots = isPlayer1 ? PLAYER1_SLOTS :  PLAYER2_SLOTS;
        
        for (int i = 0; i < mySlots.length; i++) {
            if (mySlots[i] == inventorySlot) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}