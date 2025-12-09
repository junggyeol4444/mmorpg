package com. multiverse.trade. gui;

import com.multiverse.trade.TradeCore;
import com. multiverse.trade. managers.AuctionManager;
import com. multiverse.trade. models. Auction;
import com.multiverse.trade.models.AuctionStatus;
import com. multiverse.trade. utils.ItemUtil;
import com.multiverse.trade.utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import com.multiverse.trade.utils.TimeUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity.Player;
import org. bukkit.inventory. Inventory;
import org.bukkit. inventory. InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org. bukkit.inventory. meta.ItemMeta;

import java.util.*;

public class AuctionGUI implements InventoryHolder {

    private final TradeCore plugin;
    private List<Auction> auctions;
    private final Inventory inventory;
    private int currentPage;
    private final int auctionsPerPage = 45;
    private SortType sortType = SortType.TIME_LEFT;
    private boolean ascending = true;

    public AuctionGUI(TradeCore plugin, int page) {
        this.plugin = plugin;
        this.currentPage = page;
        
        String title = MessageUtil.color(plugin.getConfig().getString("gui.auction.title", "&8경매장"));
        this.inventory = Bukkit.createInventory(this, 54, title);
        
        refreshAuctions();
        initialize();
    }

    private void refreshAuctions() {
        auctions = new ArrayList<>(plugin. getAuctionManager().getActiveAuctions());
        sortAuctions();
    }

    private void sortAuctions() {
        Comparator<Auction> comparator;
        
        switch (sortType) {
            case PRICE:
                comparator = Comparator.comparingDouble(Auction::getCurrentBid);
                break;
            case BID_COUNT:
                comparator = Comparator. comparingInt(Auction::getBidCount);
                break;
            case TIME_LEFT:
            default:
                comparator = Comparator.comparingLong(Auction:: getEndTime);
                break;
        }
        
        if (! ascending) {
            comparator = comparator. reversed();
        }
        
        auctions.sort(comparator);
    }

    private void initialize() {
        updateAuctions();
        updateNavigation();
    }

    public void updateAuctions() {
        for (int i = 0; i < auctionsPerPage; i++) {
            inventory.setItem(i, null);
        }

        int start = (currentPage - 1) * auctionsPerPage;
        int end = Math.min(start + auctionsPerPage, auctions.size());

        for (int i = start; i < end; i++) {
            Auction auction = auctions.get(i);
            ItemStack display = createAuctionDisplayItem(auction);
            inventory.setItem(i - start, display);
        }
    }

    private ItemStack createAuctionDisplayItem(Auction auction) {
        ItemStack display = auction.getItem().clone();
        ItemMeta meta = display.getItemMeta();
        
        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta. getLore()) : new ArrayList<>();
            lore.add("");
            
            String sellerName = Bukkit.getOfflinePlayer(auction. getSeller()).getName();
            lore.add(MessageUtil.color("&7판매자:  &f" + sellerName));
            lore.add(MessageUtil.color("&7현재가: &a" + NumberUtil.format(auction.getCurrentBid())));
            
            if (auction.getBuyoutPrice() > 0) {
                lore.add(MessageUtil.color("&7즉시구매:  &6" + NumberUtil. format(auction.getBuyoutPrice())));
            }
            
            if (auction.getCurrentBidder() != null) {
                String bidderName = Bukkit.getOfflinePlayer(auction. getCurrentBidder()).getName();
                lore.add(MessageUtil.color("&7현재 입찰자: &e" + bidderName));
            }
            
            lore.add(MessageUtil.color("&7입찰 횟수: &f" + auction.getBidCount() + "회"));
            lore.add(MessageUtil.color("&7남은 시간: &c" + TimeUtil. formatDuration(auction.getTimeRemaining())));
            lore.add("");
            lore.add(MessageUtil.color("&e좌클릭:  &f입찰"));
            if (auction.getBuyoutPrice() > 0) {
                lore.add(MessageUtil.color("&e우클릭: &f즉시 구매"));
            }
            lore.add(MessageUtil.color("&e쉬프트+클릭: &f상세 정보"));
            
            meta. setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }

    private void updateNavigation() {
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
        }

        int totalPages = (int) Math.ceil((double) auctions.size() / auctionsPerPage);
        if (totalPages == 0) totalPages = 1;

        if (currentPage > 1) {
            inventory. setItem(45, createItem(Material. ARROW, "&a이전 페이지",
                Arrays. asList("&7클릭하여 이전 페이지")));
        }

        inventory.setItem(47, createItem(Material.GOLD_INGOT, "&e가격순 정렬",
            Arrays.asList(
                "&7현재:  " + (sortType == SortType.PRICE ? "&a선택됨" : "&7미선택"),
                "&7클릭하여 정렬"
            )));

        inventory.setItem(49, createItem(Material.CLOCK, "&e시간순 정렬",
            Arrays.asList(
                "&7현재: " + (sortType == SortType.TIME_LEFT ?  "&a선택됨" : "&7미선택"),
                "&7클릭하여 정렬"
            )));

        inventory.setItem(51, createItem(Material.COMPASS, "&e검색",
            Arrays.asList("&7클릭하여 검색")));

        if (currentPage < totalPages) {
            inventory.setItem(53, createItem(Material.ARROW, "&a다음 페이지",
                Arrays.asList("&7클릭하여 다음 페이지")));
        }
    }

    public void handleAuctionClick(Player player, int slot, boolean shiftClick, boolean rightClick) {
        int index = (currentPage - 1) * auctionsPerPage + slot;
        
        if (index >= auctions. size()) {
            return;
        }

        Auction auction = auctions.get(index);

        if (auction.getSeller().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtil.color("&c자신의 경매에는 입찰할 수 없습니다."));
            return;
        }

        if (shiftClick) {
            showAuctionDetails(player, auction);
        } else if (rightClick && auction.getBuyoutPrice() > 0) {
            plugin.getAuctionManager().buyout(player, auction. getAuctionId());
            refreshAuctions();
            updateAuctions();
        } else {
            player.sendMessage(MessageUtil.color("&e입찰 금액을 채팅창에 입력하세요.  (취소:  cancel)"));
            plugin.getGuiManager().startBidInput(player, auction);
            player.closeInventory();
        }
    }

    private void showAuctionDetails(Player player, Auction auction) {
        String itemName = ItemUtil.getItemName(auction.getItem());
        String sellerName = Bukkit.getOfflinePlayer(auction.getSeller()).getName();
        
        player. sendMessage(MessageUtil.color("&6===== 경매 상세 정보 ====="));
        player.sendMessage(MessageUtil.color("&7아이템:  &f" + itemName));
        player.sendMessage(MessageUtil.color("&7판매자: &f" + sellerName));
        player.sendMessage(MessageUtil.color("&7시작가: &a" + NumberUtil.format(auction.getStartingBid())));
        player.sendMessage(MessageUtil.color("&7현재가: &a" + NumberUtil. format(auction.getCurrentBid())));
        
        if (auction. getBuyoutPrice() > 0) {
            player.sendMessage(MessageUtil.color("&7즉시구매가: &6" + NumberUtil.format(auction.getBuyoutPrice())));
        }
        
        player.sendMessage(MessageUtil.color("&7입찰 횟수:  &f" + auction.getBidCount() + "회"));
        player.sendMessage(MessageUtil.color("&7남은 시간: &c" + TimeUtil.formatDuration(auction.getTimeRemaining())));
    }

    public void sortByPrice() {
        if (sortType == SortType.PRICE) {
            ascending = !ascending;
        } else {
            sortType = SortType.PRICE;
            ascending = true;
        }
        refreshAuctions();
        updateAuctions();
        updateNavigation();
    }

    public void sortByTime() {
        if (sortType == SortType.TIME_LEFT) {
            ascending = !ascending;
        } else {
            sortType = SortType.TIME_LEFT;
            ascending = true;
        }
        refreshAuctions();
        updateAuctions();
        updateNavigation();
    }

    public void openSearch(Player player) {
        player.sendMessage(MessageUtil.color("&e검색어를 채팅창에 입력하세요.  (취소: cancel)"));
        plugin.getGuiManager().startAuctionSearch(player);
        player.closeInventory();
    }

    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateAuctions();
            updateNavigation();
        }
    }

    public void nextPage() {
        int totalPages = (int) Math.ceil((double) auctions.size() / auctionsPerPage);
        
        if (currentPage < totalPages) {
            currentPage++;
            updateAuctions();
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

    private enum SortType {
        TIME_LEFT,
        PRICE,
        BID_COUNT
    }
}