package com.  multiverse.trade.  gui;

import com.multiverse.trade.TradeCore;
import com.multiverse.  trade.managers.PlayerShopManager;
import com. multiverse.trade. models.  PlayerShop;
import com.multiverse.trade.models. ShopItem;
import com.multiverse.trade.utils. ItemUtil;
import com.multiverse. trade.utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import org. bukkit.  Bukkit;
import org.bukkit. Material;
import org.bukkit. entity.Player;
import org.bukkit.inventory. Inventory;
import org.bukkit.  inventory.InventoryHolder;
import org. bukkit.inventory.ItemStack;
import org. bukkit.  inventory.meta. ItemMeta;

import java.util.  ArrayList;
import java.util.  Arrays;
import java.util.  List;

public class ShopManageGUI implements InventoryHolder {

    private final TradeCore plugin;
    private final PlayerShop shop;
    private final Inventory inventory;
    private int currentPage;
    private final int itemsPerPage = 27;
    private boolean hasChanges;

    public ShopManageGUI(TradeCore plugin, PlayerShop shop) {
        this.  plugin = plugin;
        this.shop = shop;
        this.  currentPage = 1;
        this.  hasChanges = false;
        
        String title = MessageUtil.color("&8상점 관리:  " + shop.getShopName());
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

        List<ShopItem> items = shop.getItems();
        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, items.  size());

        for (int i = start; i < end; i++) {
            ShopItem shopItem = items.get(i);
            ItemStack display = createManageDisplayItem(shopItem);
            inventory.setItem(shopItem.getSlot() % itemsPerPage, display);
        }
    }

    private ItemStack createManageDisplayItem(ShopItem shopItem) {
        ItemStack display = shopItem.getItem().clone();
        ItemMeta meta = display.getItemMeta();
        
        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add("");
            lore.add(MessageUtil.color("&7가격:  &a" + NumberUtil.format(shopItem.getPrice())));
            lore.add(MessageUtil.color("&7재고: &e" + shopItem. getStock() + "/" + shopItem.getMaxStock()));
            lore.add(MessageUtil.color("&7판매량:   &b" + shopItem. getTotalSold() + "개"));
            lore.add(MessageUtil.color("&7수익: &6" + NumberUtil.format(shopItem.getTotalRevenue())));
            lore.add("");
            lore. add(MessageUtil. color("&e좌클릭:  &f가격 변경"));
            lore.add(MessageUtil.color("&e우클릭: &f재고 추가"));
            lore.add(MessageUtil.color("&e쉬프트+클릭: &f상품 제거"));
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }

    private void updateNavigation() {
        for (int i = 27; i < 54; i++) {
            inventory.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
        }

        List<ShopItem> items = shop.getItems();
        int totalPages = (int) Math.ceil((double) items.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        if (currentPage > 1) {
            inventory.setItem(45, createItem(Material. ARROW, "&a이전 페이지",
                Arrays.  asList("&7클릭하여 이전 페이지")));
        }

        if (currentPage < totalPages) {
            inventory.setItem(53, createItem(Material.  ARROW, "&a다음 페이지",
                Arrays. asList("&7클릭하여 다음 페이지")));
        }

        Material statusMaterial = shop.isOpen() ? Material.LIME_DYE : Material. GRAY_DYE;
        String statusText = shop.isOpen() ? "&a영업중" : "&c휴업중";
        inventory.setItem(49, createItem(statusMaterial, statusText,
            Arrays.asList(
                "&7클릭하여 상태 변경",
                "",
                "&7총 상품: &f" + items. size() + "개",
                "&7총 판매액:   &6" + NumberUtil. format(shop.getTotalSales()),
                "&7총 주문수: &f" + shop.getTotalOrders() + "건"
            )));

        inventory.setItem(47, createItem(Material.NAME_TAG, "&e상점 이름 변경",
            Arrays.asList("&7클릭하여 이름 변경")));

        inventory. setItem(51, createItem(Material.WRITABLE_BOOK, "&e상점 설명 변경",
            Arrays.asList("&7클릭하여 설명 변경")));
    }

    public void handleAddItem(Player player, int slot, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return;
        }

        int maxItems = plugin.getConfig().getInt("player-shops.limits.max-items-per-shop", 27);
        if (shop.getItems().size() >= maxItems) {
            player. sendMessage(MessageUtil.color("&c상점에 더 이상 상품을 추가할 수 없습니다."));
            return;
        }

        player.sendMessage(MessageUtil.color("&e상품 가격을 채팅창에 입력하세요.  (취소: cancel)"));
        plugin.getGuiManager().startPriceInput(player, shop, slot, item);
        player.  closeInventory();
        hasChanges = true;
    }

    public void handleRemoveItem(Player player, int slot) {
        ShopItem shopItem = null;
        for (ShopItem item :  shop.getItems()) {
            if (item.getSlot() == slot) {
                shopItem = item;
                break;
            }
        }

        if (shopItem != null) {
            ItemStack returnItem = shopItem. getItem().clone();
            returnItem.setAmount(shopItem.getStock());
            player.getInventory().addItem(returnItem);
            
            plugin.getPlayerShopManager().removeItem(shop, slot);
            updateItems();
            hasChanges = true;
            
            player.sendMessage(MessageUtil. color("&a상품을 제거했습니다."));
        }
    }

    public void toggleShopStatus() {
        shop.setOpen(!shop.isOpen());
        updateNavigation();
        hasChanges = true;
    }

    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateItems();
            updateNavigation();
        }
    }

    public void nextPage() {
        int totalPages = (int) Math.ceil((double) shop.getItems().size() / itemsPerPage);
        
        if (currentPage < totalPages) {
            currentPage++;
            updateItems();
            updateNavigation();
        }
    }

    public void saveChanges() {
        if (hasChanges) {
            plugin.getShopDataManager().saveShop(shop);
            hasChanges = false;
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

    public PlayerShop getShop() {
        return shop;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}