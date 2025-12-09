package com.multiverse.trade.gui;

import com. multiverse.trade. TradeCore;
import com. multiverse.trade. managers.PlayerShopManager;
import com.multiverse.  trade.models.PlayerShop;
import com.multiverse.  trade.models.ShopItem;
import com.multiverse. trade.utils.ItemUtil;
import com.multiverse.trade.utils.MessageUtil;
import com. multiverse.trade. utils.NumberUtil;
import org.bukkit.  Bukkit;
import org.bukkit.  Material;
import org. bukkit.  entity.Player;
import org.bukkit. inventory.Inventory;
import org.bukkit.  inventory. InventoryHolder;
import org.  bukkit.inventory.ItemStack;
import org.  bukkit.inventory.meta.  ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java. util.List;

public class ShopGUI implements InventoryHolder {

    private final TradeCore plugin;
    private final PlayerShop shop;
    private final Inventory inventory;
    private int currentPage;
    private final int itemsPerPage = 27;

    public ShopGUI(TradeCore plugin, PlayerShop shop) {
        this.plugin = plugin;
        this. shop = shop;
        this.currentPage = 1;
        
        String title = MessageUtil.color(shop.getShopName());
        this.inventory = Bukkit.createInventory(this, 54, title);
        
        initialize();
    }

    private void initialize() {
        updateItems();
        updateNavigation();
    }

    public void updateItems() {
        for (int i = 0; i < itemsPerPage; i++) {
            inventory.setItem(i, null);
        }

        List<ShopItem> items = shop.getItemsWithStock();
        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, items.size());

        for (int i = start; i < end; i++) {
            ShopItem shopItem = items.get(i);
            ItemStack display = createDisplayItem(shopItem);
            inventory.setItem(i - start, display);
        }
    }

    private ItemStack createDisplayItem(ShopItem shopItem) {
        ItemStack display = shopItem.getItem(). clone();
        ItemMeta meta = display. getItemMeta();
        
        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta. getLore()) : new ArrayList<>();
            lore.add("");
            lore.add(MessageUtil.color("&7가격: &a" + NumberUtil. format(shopItem. getPrice())));
            lore.add(MessageUtil.color("&7재고: &e" + shopItem.getStock() + "개"));
            lore.add("");
            lore.add(MessageUtil.  color("&e좌클릭:  &f1개 구매"));
            lore. add(MessageUtil. color("&e쉬프트+클릭: &f수량 지정 구매"));
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }

    private void updateNavigation() {
        for (int i = 27; i < 54; i++) {
            inventory.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
        }

        List<ShopItem> items = shop.getItemsWithStock();
        int totalPages = (int) Math.ceil((double) items.size() / itemsPerPage);

        if (currentPage > 1) {
            inventory.setItem(45, createItem(Material.ARROW, "&a이전 페이지",
                Arrays.asList("&7클릭하여 이전 페이지")));
        }

        if (currentPage < totalPages) {
            inventory.setItem(53, createItem(Material.ARROW, "&a다음 페이지",
                Arrays.asList("&7클릭하여 다음 페이지")));
        }

        String ownerName = Bukkit.getOfflinePlayer(shop. getOwner()).getName();
        inventory.setItem(49, createItem(Material.OAK_SIGN, "&e상점 정보",
            Arrays.asList(
                "&7주인:  &f" + ownerName,
                "&7상태: " + (shop.isOpen() ? "&a영업중" :   "&c휴업중"),
                "&7총 상품: &f" + items.size() + "개",
                "",
                "&7페이지: &f" + currentPage + "/" + Math.max(1, totalPages)
            )));
    }

    public void handleItemClick(Player player, int slot, boolean shiftClick) {
        List<ShopItem> items = shop.getItemsWithStock();
        int index = (currentPage - 1) * itemsPerPage + slot;
        
        if (index >= items.size()) {
            return;
        }

        ShopItem shopItem = items.get(index);

        if (shiftClick) {
            player.sendMessage(MessageUtil.color("&e구매할 수량을 채팅창에 입력하세요.  (취소:  cancel)"));
            plugin.getGuiManager().startAmountInput(player, shop, shopItem);
            player.closeInventory();
        } else {
            plugin.getPlayerShopManager().buyItem(player, shop, shopItem. getSlot(), 1);
            updateItems();
        }
    }

    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateItems();
            updateNavigation();
        }
    }

    public void nextPage() {
        List<ShopItem> items = shop.getItemsWithStock();
        int totalPages = (int) Math.ceil((double) items.size() / itemsPerPage);
        
        if (currentPage < totalPages) {
            currentPage++;
            updateItems();
            updateNavigation();
        }
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
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

    public PlayerShop getShop() {
        return shop;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}