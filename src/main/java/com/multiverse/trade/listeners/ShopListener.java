package com.multiverse.trade.listeners;

import com.multiverse.trade.TradeCore;
import com.multiverse. trade.events.ShopPurchaseEvent;
import com.multiverse.trade.managers.PlayerShopManager;
import com.multiverse.trade.models.PlayerShop;
import com.multiverse.trade.models.ShopItem;
import com.multiverse.trade.utils.ItemUtil;
import com.multiverse.trade.utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import org.bukkit. Bukkit;
import org.bukkit. Location;
import org. bukkit.block.Block;
import org.bukkit. block.Sign;
import org. bukkit.entity. Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. block.Action;
import org. bukkit.event. block.BlockBreakEvent;
import org.bukkit. event.block.SignChangeEvent;
import org.bukkit. event.player.PlayerInteractEvent;

public class ShopListener implements Listener {

    private final TradeCore plugin;
    private final PlayerShopManager shopManager;

    public ShopListener(TradeCore plugin) {
        this.plugin = plugin;
        this.shopManager = plugin.getPlayerShopManager();
    }

    @EventHandler(priority = EventPriority. MONITOR)
    public void onShopPurchase(ShopPurchaseEvent event) {
        if (event.isCancelled()) {
            return;
        }

        PlayerShop shop = event.getShop();
        Player buyer = event.getBuyer();
        ShopItem item = event.getItem();
        int amount = event.getAmount();
        double totalPrice = event.getTotalPrice();

        String itemName = ItemUtil.getItemName(item. getItem());

        plugin.getLogger().info(String.format(
            "상점 구매:  %s가 %s의 상점에서 %s x%d를 %. 2f에 구매",
            buyer.getName(),
            Bukkit.getOfflinePlayer(shop.getOwner()).getName(),
            itemName,
            amount,
            totalPrice
        ));
    }

    @EventHandler(priority = EventPriority. HIGH)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        String[] lines = event.getLines();

        if (lines[0] == null) {
            return;
        }

        String firstLine = lines[0]. toLowerCase();
        if (! firstLine.equals("[shop]") && !firstLine. equals("[상점]")) {
            return;
        }

        if (! player.hasPermission("trade.shop")) {
            MessageUtil.send(player, "general.no-permission");
            event.setCancelled(true);
            return;
        }

        int maxShops = plugin.getConfig().getInt("player-shops.limits.max-shops-per-player", 3);
        if (shopManager.getPlayerShops(player).size() >= maxShops && 
            !player. hasPermission("trade.admin. bypass")) {
            MessageUtil.send(player, "shop.max-shops", "max", String.valueOf(maxShops));
            event.setCancelled(true);
            return;
        }

        double creationCost = plugin.getConfig().getDouble("player-shops.fees.creation-cost", 10000.0);
        if (creationCost > 0 && !plugin.getEconomy().has(player, creationCost)) {
            MessageUtil.send(player, "shop.not-enough-money");
            event.setCancelled(true);
            return;
        }

        String shopName = lines[1] != null && ! lines[1].isEmpty() ? lines[1] : player.getName() + "의 상점";
        Location location = event.getBlock().getLocation();

        PlayerShop shop = shopManager.createShop(player, location, shopName);

        if (shop != null) {
            if (creationCost > 0) {
                plugin.getEconomy().withdrawPlayer(player, creationCost);
                MessageUtil.send(player, "shop.creation-cost", "cost", NumberUtil.format(creationCost));
            }

            event.setLine(0, MessageUtil.color("&1[상점]"));
            event.setLine(1, MessageUtil.color("&0" + shopName));
            event.setLine(2, MessageUtil.color("&2" + player.getName()));
            event.setLine(3, MessageUtil. color("&4우클릭으로 열기"));

            MessageUtil.send(player, "shop.created", "name", shopName);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        if (!(block. getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) block.getState();
        String firstLine = sign.getLine(0);

        if (! firstLine.contains("[상점]") && !firstLine. toLowerCase().contains("[shop]")) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        Location location = block.getLocation();

        PlayerShop shop = shopManager.getShopAtLocation(location);
        if (shop == null) {
            player.sendMessage(MessageUtil.color("&c이 상점은 더 이상 존재하지 않습니다."));
            return;
        }

        if (shop.getOwner().equals(player.getUniqueId())) {
            plugin.getGuiManager().openShopManageGUI(player, shop);
        } else {
            if (!shop.isOpen()) {
                MessageUtil.send(player, "shop.shop-closed");
                return;
            }
            plugin.getGuiManager().openShopGUI(player, shop);
        }
    }

    @EventHandler(priority = EventPriority. HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!(block.getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) block.getState();
        String firstLine = sign.getLine(0);

        if (!firstLine.contains("[상점]") && !firstLine. toLowerCase().contains("[shop]")) {
            return;
        }

        Player player = event.getPlayer();
        Location location = block.getLocation();

        PlayerShop shop = shopManager.getShopAtLocation(location);
        if (shop == null) {
            return;
        }

        if (! shop.getOwner().equals(player.getUniqueId()) && !player.hasPermission("trade.admin")) {
            MessageUtil.send(player, "shop.not-owner");
            event.setCancelled(true);
            return;
        }

        if (! shop.getItems().isEmpty()) {
            player.sendMessage(MessageUtil.color("&c상점에 등록된 상품이 있습니다.  먼저 상품을 제거하세요."));
            event.setCancelled(true);
            return;
        }

        shopManager.deleteShop(shop. getShopId());
        MessageUtil.send(player, "shop.deleted");
    }
}