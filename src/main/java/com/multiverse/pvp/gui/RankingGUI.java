package com.multiverse.pvp.gui;

import com. multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPRanking;
import com.multiverse. pvp.enums.LeaderboardType;
import com.multiverse.pvp.enums.PvPTier;
import com.multiverse.pvp.utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity. Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org. bukkit.event. inventory.InventoryClickEvent;
import org.bukkit. event.inventory. InventoryCloseEvent;
import org. bukkit.inventory. Inventory;
import org.bukkit. inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta. SkullMeta;

import java.util.*;

public class RankingGUI implements Listener {

    private final PvPCore plugin;
    private final Map<UUID, Inventory> openInventories;
    private final Map<UUID, LeaderboardType> selectedType;

    private static final String TITLE = "§6§l랭킹";

    public RankingGUI(PvPCore plugin) {
        this.plugin = plugin;
        this. openInventories = new HashMap<>();
        this.selectedType = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * GUI 열기
     */
    public void open(Player player) {
        open(player, LeaderboardType. RATING);
    }

    /**
     * 특정 리더보드 타입으로 열기
     */
    public void open(Player player, LeaderboardType type) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);

        selectedType.put(player.getUniqueId(), type);

        // 리더보드 타입 선택 버튼 (상단)
        int filterSlot = 0;
        for (LeaderboardType lbType : LeaderboardType.values()) {
            if (filterSlot > 7) break;
            inventory.setItem(filterSlot, createTypeButton(lbType, lbType == type));
            filterSlot++;
        }

        // 랭킹 목록 가져오기
        List<PvPRanking> rankings = plugin.getLeaderboardManager().getLeaderboard(type, 28);

        // 랭킹 아이템 배치
        int slot = 10;
        int rank = 1;
        for (PvPRanking ranking : rankings) {
            inventory.setItem(slot, createRankingItem(ranking, rank, type));
            rank++;

            slot++;
            if ((slot + 1) % 9 == 0) {
                slot += 2;
            }
            if (slot > 43) break;
        }

        // 내 순위 표시
        inventory.setItem(49, createMyRankItem(player, type));

        // 시즌 정보
        inventory.setItem(45, createSeasonInfoItem());

        // 닫기 버튼
        inventory. setItem(53, createCloseButton());

        openInventories.put(player.getUniqueId(), inventory);
        player.openInventory(inventory);
    }

    /**
     * 리더보드 타입 버튼 생성
     */
    private ItemStack createTypeButton(LeaderboardType type, boolean selected) {
        ItemStack item = new ItemStack(type.getIcon());
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        if (selected) {
            meta.setDisplayName(MessageUtil. colorize("&a▶ " + type.getFormattedName() + " &a◀"));
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(org. bukkit.inventory. ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.setDisplayName(MessageUtil.colorize(type.getFormattedName()));
        }

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageUtil.colorize("&7" + type.getDescription()));
        lore.add("");
        lore.add(MessageUtil.colorize("&e클릭하여 보기"));
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * 랭킹 아이템 생성
     */
    private ItemStack createRankingItem(PvPRanking ranking, int rank, LeaderboardType type) {
        ItemStack item;

        // 상위 3명은 플레이어 머리
        if (rank <= 3) {
            item = new ItemStack(Material. PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            if (skullMeta != null) {
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(ranking.getPlayerId()));
                item.setItemMeta(skullMeta);
            }
        } else {
            item = new ItemStack(ranking.getTier().getIcon());
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String playerName = Bukkit.getOfflinePlayer(ranking. getPlayerId()).getName();
        if (playerName == null) playerName = "알 수 없음";

        // 순위별 색상
        String rankColor;
        switch (rank) {
            case 1: rankColor = "&6&l"; break;
            case 2: rankColor = "&7&l"; break;
            case 3: rankColor = "&c&l"; break;
            default: rankColor = "&e"; break;
        }

        meta.setDisplayName(MessageUtil.colorize(rankColor + "#" + rank + " &f" + playerName));

        // 설명
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageUtil.colorize("&7티어: " + ranking.getTier().getFormattedName()));
        lore. add(MessageUtil. colorize("&7레이팅: &f" + ranking. getRating()));
        lore.add("");

        // 타입별 값 표시
        String value = type.formatValue(getValueByType(ranking, type));
        lore.add(MessageUtil.colorize("&e" + type.getDisplayName() + ": &f" + value));

        lore.add("");
        lore.add(MessageUtil.colorize("&7전적:  &a" + ranking.getWins() + "승 &c" + ranking.getLosses() + "패"));
        lore.add(MessageUtil.colorize("&7승률: &f" + String.format("%.1f", ranking.getWinRate()) + "%"));
        lore.add(MessageUtil.colorize("&7KDA: &f" + String.format("%.2f", ranking.getKDA())));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 타입별 값 가져오기
     */
    private double getValueByType(PvPRanking ranking, LeaderboardType type) {
        switch (type) {
            case KILLS:  return ranking.getKills();
            case STREAK: return ranking.getMaxWinStreak();
            case WINS: return ranking.getWins();
            case RATING: return ranking.getRating();
            case KDA: return ranking. getKDA();
            case WIN_RATE: return ranking.getWinRate();
            default: return ranking.getRating();
        }
    }

    /**
     * 내 순위 아이템 생성
     */
    private ItemStack createMyRankItem(Player player, LeaderboardType type) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            item.setItemMeta(skullMeta);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        PvPRanking myRanking = plugin.getRankingManager().getRanking(player);
        int myRank = plugin.getRankingManager().getPlayerRank(player);

        meta.setDisplayName(MessageUtil.colorize("&a&l내 순위"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        if (myRank > 0) {
            lore.add(MessageUtil.colorize("&e순위:  &f#" + myRank));
        } else {
            lore.add(MessageUtil.colorize("&e순위:  &7-"));
        }
        lore.add(MessageUtil.colorize("&e티어: " + myRanking.getTier().getFormattedName()));
        lore.add(MessageUtil.colorize("&e레이팅: &f" + myRanking. getRating()));
        lore.add("");

        String value = type.formatValue(getValueByType(myRanking, type));
        lore.add(MessageUtil.colorize("&b" + type.getDisplayName() + ": &f" + value));

        lore.add("");
        lore.add(MessageUtil.colorize("&7전적: &a" + myRanking. getWins() + "승 &c" + myRanking.getLosses() + "패"));
        lore. add(MessageUtil. colorize("&7KDA: &f" + String.format("%.2f", myRanking.getKDA())));

        // 다음 티어까지 정보
        PvPTier nextTier = myRanking.getTier().getNextTier();
        if (nextTier != null) {
            int needed = nextTier. getMinRating() - myRanking.getRating();
            lore.add("");
            lore. add(MessageUtil. colorize("&7다음 티어까지:  &f" + needed + " 레이팅"));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 시즌 정보 아이템 생성
     */
    private ItemStack createSeasonInfoItem() {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        int season = plugin.getSeasonManager().getCurrentSeason();
        int remainingDays = plugin.getSeasonManager().getRemainingDays();

        meta.setDisplayName(MessageUtil.colorize("&6&l시즌 " + season));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageUtil.colorize("&7남은 기간: &f" + remainingDays + "일"));
        lore.add(MessageUtil.colorize("&7진행률: &f" + String.format("%. 1f", plugin.getSeasonManager().getSeasonProgress() * 100) + "%"));
        lore.add("");
        lore.add(MessageUtil.colorize("&e시즌 종료 시 티어별 보상 지급! "));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 닫기 버튼 생성
     */
    private ItemStack createCloseButton() {
        ItemStack item = new ItemStack(Material. BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        meta.setDisplayName(MessageUtil.colorize("&c닫기"));

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = openInventories. get(player.getUniqueId());

        if (inventory == null || !event.getInventory().equals(inventory)) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event. getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        int slot = event.getRawSlot();

        // 리더보드 타입 선택 (0-7)
        if (slot >= 0 && slot <= 7) {
            LeaderboardType[] types = LeaderboardType.values();
            if (slot < types.length) {
                open(player, types[slot]);
            }
            return;
        }

        // 닫기 버튼
        if (slot == 53) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        openInventories.remove(player.getUniqueId());
        selectedType.remove(player.getUniqueId());
    }
}