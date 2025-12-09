package com. multiverse.trade. gui;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.utils.MessageUtil;
import com.multiverse.trade.utils. NumberUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org.bukkit.entity.Player;
import org.bukkit. inventory.Inventory;
import org.bukkit.inventory. InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org. bukkit.inventory. meta.ItemMeta;

import java. util. ArrayList;
import java. util.Arrays;
import java. util.List;

public class AuctionCreateGUI implements InventoryHolder {

    private final TradeCore plugin;
    private final Player player;
    private final Inventory inventory;
    
    private ItemStack auctionItem;
    private double startingPrice;
    private double buyoutPrice;
    private int duration;

    private static final int ITEM_SLOT = 13;
    private static final int STARTING_PRICE_SLOT = 29;
    private static final int BUYOUT_PRICE_SLOT = 31;
    private static final int DURATION_SLOT = 33;
    private static final int CONFIRM_SLOT = 49;

    public AuctionCreateGUI(TradeCore plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.startingPrice = 100;
        this.buyoutPrice = 0;
        this. duration = 24;
        
        String title = MessageUtil. color("&8경매 등록");
        this.inventory = Bukkit. createInventory(this, 54, title);
        
        initialize();
    }

    private void initialize() {
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, createItem(Material. GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
        }

        inventory.setItem(ITEM_SLOT, null);

        updatePriceDisplays();

        inventory.setItem(CONFIRM_SLOT, createItem(Material. EMERALD_BLOCK, "&a경매 등록",
            Arrays.asList(
                "&7클릭하여 경매 등록",
                "",
                "&c아이템을 슬롯에 넣어주세요!"
            )));

        inventory.setItem(4, createItem(Material.PAPER, "&e경매 등록 안내",
            Arrays.asList(
                "&7중앙 슬롯에 경매할 아이템을 넣으세요.",
                "",
                "&7시작가, 즉시구매가, 기간을 설정한 후",
                "&7등록 버튼을 클릭하세요."
            )));
    }

    private void updatePriceDisplays() {
        inventory.setItem(STARTING_PRICE_SLOT, createItem(Material.GOLD_INGOT, "&6시작가 설정",
            Arrays.asList(
                "&7현재:  &a" + NumberUtil. format(startingPrice),
                "",
                "&e클릭하여 변경"
            )));

        inventory. setItem(BUYOUT_PRICE_SLOT, createItem(Material.DIAMOND, "&b즉시구매가 설정",
            Arrays.asList(
                "&7현재: &a" + (buyoutPrice > 0 ? NumberUtil.format(buyoutPrice) : "없음"),
                "",
                "&e클릭하여 변경",
                "&7(0 입력 시 즉시구매 비활성화)"
            )));

        List<Integer> availableDurations = plugin.getConfig().getIntegerList("auction.durations. available");
        List<String> durationLore = new ArrayList<>();
        durationLore.add("&7현재:  &a" + duration + "시간");
        durationLore.add("");
        durationLore.add("&7사용 가능:");
        for (int d : availableDurations) {
            String marker = (d == duration) ? "&a✔ " : "&7  ";
            durationLore.add(marker + d + "시간");
        }
        durationLore.add("");
        durationLore.add("&e클릭하여 변경");

        inventory.setItem(DURATION_SLOT, createItem(Material.CLOCK, "&e기간 설정", durationLore));

        updateConfirmButton();
    }

    private void updateConfirmButton() {
        List<String> lore = new ArrayList<>();
        
        if (auctionItem != null) {
            lore.add("&7아이템: &f" + auctionItem. getType().name());
            lore.add("&7시작가: &a" + NumberUtil. format(startingPrice));
            if (buyoutPrice > 0) {
                lore.add("&7즉시구매가: &a" + NumberUtil. format(buyoutPrice));
            }
            lore.add("&7기간: &e" + duration + "시간");
            lore.add("");
            
            double listingFee = plugin.getConfig().getDouble("auction.fees.listing-fee", 100. 0);
            lore.add("&7등록비: &c" + NumberUtil. format(listingFee));
            lore.add("");
            lore.add("&a클릭하여 등록!");
        } else {
            lore.add("&c아이템을 슬롯에 넣어주세요!");
        }

        inventory.setItem(CONFIRM_SLOT, createItem(Material.EMERALD_BLOCK, "&a경매 등록", lore));
    }

    public void setStartingPrice(Player player) {
        player.sendMessage(MessageUtil.color("&e시작가를 채팅창에 입력하세요. (취소:  cancel)"));
        plugin.getGuiManager().startAuctionPriceInput(player, this, PriceInputType. STARTING);
        player.closeInventory();
    }

    public void setBuyoutPrice(Player player) {
        player.sendMessage(MessageUtil.color("&e즉시구매가를 채팅창에 입력하세요. (0 = 없음, 취소: cancel)"));
        plugin.getGuiManager().startAuctionPriceInput(player, this, PriceInputType.BUYOUT);
        player.closeInventory();
    }

    public void setDuration(Player player) {
        List<Integer> available = plugin.getConfig().getIntegerList("auction.durations.available");
        int currentIndex = available.indexOf(duration);
        int nextIndex = (currentIndex + 1) % available.size();
        duration = available.get(nextIndex);
        updatePriceDisplays();
    }

    public void confirmCreate(Player player) {
        ItemStack itemInSlot = inventory. getItem(ITEM_SLOT);
        
        if (itemInSlot == null || itemInSlot. getType().isAir()) {
            player.sendMessage(MessageUtil.color("&c경매할 아이템을 슬롯에 넣어주세요."));
            return;
        }

        auctionItem = itemInSlot;

        if (startingPrice <= 0) {
            player.sendMessage(MessageUtil.color("&c시작가는 0보다 커야 합니다."));
            return;
        }

        if (buyoutPrice > 0 && buyoutPrice <= startingPrice) {
            player.sendMessage(MessageUtil. color("&c즉시구매가는 시작가보다 커야 합니다."));
            return;
        }

        double listingFee = plugin.getConfig().getDouble("auction. fees.listing-fee", 100.0);
        if (! plugin.getEconomy().has(player, listingFee)) {
            MessageUtil.send(player, "shop. not-enough-money");
            return;
        }

        plugin.getEconomy().withdrawPlayer(player, listingFee);
        MessageUtil.send(player, "auction.listing-fee", "fee", NumberUtil.format(listingFee));

        inventory.setItem(ITEM_SLOT, null);

        plugin.getAuctionManager().createAuction(player, auctionItem, startingPrice, buyoutPrice, duration);
        
        player. closeInventory();
        MessageUtil.send(player, "auction.listed", "id", "NEW");
    }

    public void returnItem(Player player) {
        ItemStack itemInSlot = inventory.getItem(ITEM_SLOT);
        if (itemInSlot != null && ! itemInSlot. getType().isAir()) {
            player.getInventory().addItem(itemInSlot);
        }
    }

    public void setStartingPriceValue(double price) {
        this.startingPrice = price;
        updatePriceDisplays();
    }

    public void setBuyoutPriceValue(double price) {
        this.buyoutPrice = price;
        updatePriceDisplays();
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

    public enum PriceInputType {
        STARTING,
        BUYOUT
    }
}