package com. multiverse.trade. gui;

import com.multiverse.  trade.TradeCore;
import com.multiverse. trade.models. PlayerShop;
import com.multiverse.trade.utils. MessageUtil;
import com.multiverse. trade.utils.NumberUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org.bukkit.  entity.Player;
import org.bukkit.inventory.Inventory;
import org. bukkit.inventory.  InventoryHolder;
import org.bukkit.  inventory.ItemStack;
import org.bukkit.inventory.meta. ItemMeta;
import org.  bukkit.inventory.  meta. SkullMeta;

import java.util. ArrayList;
import java.util.Arrays;
import java. util.List;

public class ShopListGUI implements InventoryHolder {

    private final TradeCore plugin;
    private final List<PlayerShop> shops;
    private final Inventory inventory;
    private int currentPage;
    private final int shopsPerPage = 45;

    public ShopListGUI(TradeCore plugin, List<PlayerShop> shops, int page) {
        this. plugin = plugin;
        this.shops = shops;
        this.currentPage = page;
        
        String title = MessageUtil.color("&8상점 목록");
        this.inventory = Bukkit. createInventory(this, 54, title);
        
        initialize();
    }

    private void initialize() {
        updateShops();
        updateNavigation();
    }

    public void updateShops() {
        for (int i = 0; i < shopsPerPage; i++) {
            inventory. setItem(i, null);
        }

        int start = (currentPage - 1) * shopsPerPage;
        int end = Math.  min(start + shopsPerPage, shops. size());

        for (int i = start; i < end; i++) {
            PlayerShop shop = shops.get(i);
            ItemStack display = createShopDisplayItem(shop);
            inventory.setItem(i - start, display);
        }
    }

    private ItemStack createShopDisplayItem(PlayerShop shop) {
        ItemStack head = new ItemStack(Material. PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        if (meta != null) {
            meta.setOwningPlayer(Bukkit.  getOfflinePlayer(shop.getOwner()));
            
            String ownerName = Bukkit.getOfflinePlayer(shop.getOwner()).getName();
            meta.setDisplayName(MessageUtil.  color("&e" + shop.  getShopName()));
            
            List<String> lore = new ArrayList<>();
            lore.add(MessageUtil.color("&7주인: &f" + ownerName));
            lore. add(MessageUtil. color("&7상태: " + (shop.isOpen() ? "&a영업중" :  "&c휴업중")));
            lore.add(MessageUtil.  color("&7상품 수: &f" + shop. getItemCount() + "개"));
            lore.add("");
            
            if (shop.getDescription() != null && ! shop.getDescription().isEmpty()) {
                lore.add(MessageUtil.color("&7" + shop.getDescription()));
                lore.add("");
            }
            
            lore.add(MessageUtil.color("&e클릭하여 방문"));
            meta.setLore(lore);
            
            head.setItemMeta(meta);
        }
        
        return head;
    }

    private void updateNavigation() {
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, createItem(Material.  GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
        }

        int totalPages = (int) Math.ceil((double) shops.size() / shopsPerPage);
        if (totalPages == 0) totalPages = 1;

        if (currentPage > 1) {
            inventory.setItem(45, createItem(Material.ARROW, "&a이전 페이지",
                Arrays. asList("&7클릭하여 이전 페이지")));
        }

        if (currentPage < totalPages) {
            inventory.setItem(53, createItem(Material. ARROW, "&a다음 페이지",
                Arrays. asList("&7클릭하여 다음 페이지")));
        }

        inventory.setItem(49, createItem(Material.BOOK, "&e상점 목록",
            Arrays.asList(
                "&7총 상점: &f" + shops.size() + "개",
                "&7페이지:   &f" + currentPage + "/" + totalPages
            )));
    }

    public void handleShopClick(Player player, int slot) {
        int index = (currentPage - 1) * shopsPerPage + slot;
        
        if (index >= shops.size()) {
            return;
        }

        PlayerShop shop = shops.get(index);

        if (!  shop.isOpen() && ! shop.getOwner().equals(player.getUniqueId())) {
            MessageUtil.send(player, "shop. shop-closed");
            return;
        }

        if (shop. getOwner().equals(player.getUniqueId())) {
            plugin.getGuiManager().openShopManageGUI(player, shop);
        } else {
            plugin.getGuiManager().openShopGUI(player, shop);
        }
    }

    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateShops();
            updateNavigation();
        }
    }

    public void nextPage() {
        int totalPages = (int) Math.ceil((double) shops.size() / shopsPerPage);
        
        if (currentPage < totalPages) {
            currentPage++;
            updateShops();
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