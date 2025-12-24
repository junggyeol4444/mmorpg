package com.multiverse.pvp.gui;

import com. multiverse.pvp.PvPCore;
import com.multiverse.pvp.data.KillStreak;
import com.multiverse.pvp.data.PvPRanking;
import com. multiverse.pvp.data.PvPStatistics;
import com.multiverse.pvp.enums.PvPTier;
import com. multiverse.pvp.utils.MessageUtil;
import org. bukkit.Bukkit;
import org.bukkit.Material;
import org. bukkit.entity. Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org. bukkit.event. inventory.InventoryClickEvent;
import org.bukkit. event.inventory. InventoryCloseEvent;
import org. bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org. bukkit.inventory. meta.ItemMeta;
import org.bukkit.inventory.meta. SkullMeta;

import java.util.*;

public class StatisticsGUI implements Listener {

    private final PvPCore plugin;
    private final Map<UUID, Inventory> openInventories;

    private static final String TITLE = "§6§l통계";

    public StatisticsGUI(PvPCore plugin) {
        this.plugin = plugin;
        this.openInventories = new HashMap<>();

        Bukkit. getPluginManager().registerEvents(this, plugin);
    }

    /**
     * GUI 열기
     */
    public void open(Player viewer, Player target) {
        String title = TITLE + " - " + target.getName();
        Inventory inventory = Bukkit.createInventory(null, 54, title);

        PvPStatistics stats = plugin. getStatisticsManager().getStatistics(target);
        PvPRanking ranking = plugin.getRankingManager().getRanking(target);
        KillStreak streak = plugin.getKillStreakManager().getKillStreak(target);

        // 플레이어 정보 (중앙 상단)
        inventory.setItem(4, createPlayerInfoItem(target, ranking));

        // 전투 통계 (왼쪽)
        inventory.setItem(19, createCombatStatsItem(stats));

        // 랭킹 정보 (중앙)
        inventory.setItem(22, createRankingItem(ranking));

        // 킬 스트릭 정보 (오른쪽)
        inventory.setItem(25, createStreakItem(streak));

        // 시간별 통계
        inventory. setItem(29, createTimeStatsItem(stats, "일일", stats.getTodayKills()));
        inventory.setItem(31, createTimeStatsItem(stats, "주간", stats. getThisWeekKills()));
        inventory. setItem(33, createTimeStatsItem(stats, "월간", stats.getThisMonthKills()));

        // 듀얼 통계
        inventory. setItem(37, createDuelStatsItem(stats));

        // 아레나 통계
        inventory.setItem(40, createArenaStatsItem(stats));

        // 기타 통계
        inventory.setItem(43, createMiscStatsItem(stats));

        // 닫기 버튼
        inventory.setItem(49, createCloseButton());

        openInventories.put(viewer.getUniqueId(), inventory);
        viewer.openInventory(inventory);
    }

    /**
     * 플레이어 정보 아이템 생성
     */
    private ItemStack createPlayerInfoItem(Player target, PvPRanking ranking) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        if (skullMeta != null) {
            skullMeta. setOwningPlayer(target);
            item.setItemMeta(skullMeta);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String titlePrefix = "";
        String activeTitleId = plugin.getTitleManager().getActiveTitle(target);
        if (activeTitleId != null) {
            var title = plugin.getTitleManager().getTitle(activeTitleId);
            if (title != null && ! title.getPrefix().isEmpty()) {
                titlePrefix = title.getPrefix() + " ";
            }
        }

        meta.setDisplayName(MessageUtil. colorize(titlePrefix + "&f" + target.getName()));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore. add(MessageUtil. colorize("&7티어: " + ranking.getTier().getFormattedName()));
        lore.add(MessageUtil.colorize("&7레이팅: &f" + ranking.getRating()));
        
        int playerRank = plugin.getRankingManager().getPlayerRank(target);
        if (playerRank > 0) {
            lore.add(MessageUtil.colorize("&7순위: &e#" + playerRank));
        }
        
        lore.add("");
        lore.add(MessageUtil.colorize("&7PvP 포인트: &b" + ranking.getPvpPoints()));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 전투 통계 아이템 생성
     */
    private ItemStack createCombatStatsItem(PvPStatistics stats) {
        ItemStack item = new ItemStack(Material. DIAMOND_SWORD);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        meta. setDisplayName(MessageUtil.colorize("&c&l전투 통계"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageUtil.colorize("&7총 킬:  &a" + stats.getTotalKills()));
        lore.add(MessageUtil.colorize("&7총 데스:  &c" + stats.getTotalDeaths()));
        lore.add(MessageUtil.colorize("&7어시스트:  &e" + stats.getTotalAssists()));
        lore.add("");
        lore.add(MessageUtil.colorize("&7KDA: &f" + String.format("%.2f", stats.getKda())));
        lore.add("");
        lore. add(MessageUtil. colorize("&7총 데미지: &f" + String.format("%,d", stats.getTotalDamageDealt())));
        lore.add(MessageUtil.colorize("&7받은 데미지: &f" + String.format("%,d", stats.getTotalDamageReceived())));
        lore.add(MessageUtil.colorize("&7총 힐:  &f" + String.format("%,d", stats. getTotalHealing())));

        meta.setLore(lore);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 랭킹 정보 아이템 생성
     */
    private ItemStack createRankingItem(PvPRanking ranking) {
        ItemStack item = new ItemStack(ranking.getTier().getIcon());
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        meta.setDisplayName(MessageUtil.colorize("&6&l랭킹 정보"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageUtil.colorize("&7현재 티어: " + ranking.getTier().getFormattedName()));
        lore.add(MessageUtil.colorize("&7현재 레이팅: &f" + ranking.getRating()));
        lore.add(MessageUtil.colorize("&7최고 레이팅: &f" + ranking.getPeakRating()));
        lore.add("");
        lore. add(MessageUtil. colorize("&7시즌 전적: "));
        lore.add(MessageUtil.colorize("  &a" + ranking.getSeasonWins() + "승 &c" + ranking.getSeasonLosses() + "패"));
        lore.add(MessageUtil.colorize("  &7승률: &f" + String.format("%.1f", ranking.getSeasonWinRate()) + "%"));
        lore.add("");

        // 다음 티어까지
        PvPTier nextTier = ranking. getTier().getNextTier();
        if (nextTier != null) {
            int needed = ranking.getRatingToNextTier();
            lore.add(MessageUtil.colorize("&7다음 티어까지:  &e" + needed + " 레이팅"));
            
            // 진행률 바
            double progress = ranking.getTier().getProgress(ranking.getRating());
            lore. add(MessageUtil. colorize("&7진행률: " + createProgressBar(progress)));
        } else {
            lore.add(MessageUtil.colorize("&d최고 티어 달성! "));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 킬 스트릭 아이템 생성
     */
    private ItemStack createStreakItem(KillStreak streak) {
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        meta. setDisplayName(MessageUtil.colorize("&e&l킬 스트릭"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageUtil.colorize("&7현재 스트릭: &f" + streak.getCurrentStreak()));
        lore.add(MessageUtil.colorize("&7최고 스트릭: &6" + streak.getBestStreak()));
        lore.add("");

        if (streak.getCurrentLevel() != null) {
            lore.add(MessageUtil.colorize("&7현재 레벨: " + streak.getCurrentLevel().getAnnouncement()));
            lore.add(MessageUtil.colorize("&7보상 배율: &ax" + String.format("%.1f", streak.getCurrentMultiplier())));
        }

        lore.add("");
        lore.add(MessageUtil.colorize("&7스트릭 달성 횟수: "));
        for (var level : com.multiverse.pvp. enums. StreakLevel.values()) {
            int count = streak.getAchievementCount(level);
            if (count > 0) {
                lore. add(MessageUtil. colorize("  &7" + level.getDisplayName() + ": &f" + count + "회"));
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 시간별 통계 아이템 생성
     */
    private ItemStack createTimeStatsItem(PvPStatistics stats, String period, int kills) {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        meta. setDisplayName(MessageUtil.colorize("&b&l" + period + " 통계"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageUtil.colorize("&7킬:  &f" + kills));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 듀얼 통계 아이템 생성
     */
    private ItemStack createDuelStatsItem(PvPStatistics stats) {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        meta.setDisplayName(MessageUtil. colorize("&9&l듀얼 통계"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageUtil.colorize("&7승: &a" + stats.getDuelWins()));
        lore.add(MessageUtil.colorize("&7패:  &c" + stats.getDuelLosses()));
        lore.add(MessageUtil.colorize("&7항복: &e" + stats.getDuelSurrenders()));
        lore.add("");
        lore.add(MessageUtil.colorize("&7승률: &f" + String.format("%.1f", stats.getDuelWinRate()) + "%"));

        meta.setLore(lore);
        meta.addItemFlags(org.bukkit. inventory.ItemFlag. HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 아레나 통계 아이템 생성
     */
    private ItemStack createArenaStatsItem(PvPStatistics stats) {
        ItemStack item = new ItemStack(Material. GOLDEN_SWORD);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        meta. setDisplayName(MessageUtil.colorize("&6&l아레나 통계"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageUtil.colorize("&7총 승: &a" + stats.getTotalWins()));
        lore.add(MessageUtil.colorize("&7총 패: &c" + stats.getTotalLosses()));
        lore.add(MessageUtil.colorize("&7승률: &f" + String.format("%.1f", stats.getWinRate()) + "%"));
        lore.add("");
        lore.add(MessageUtil.colorize("&7아레나별 승률:"));

        for (Map.Entry<String, Integer> entry : stats.getArenaWins().entrySet()) {
            String arenaType = entry.getKey();
            int wins = entry.getValue();
            int losses = stats.getArenaLosses().getOrDefault(arenaType, 0);
            double winRate = (wins + losses) > 0 ? (wins * 100.0 / (wins + losses)) : 0;

            lore.add(MessageUtil.colorize("  &7" + arenaType + ": &a" + wins + "W &c" + losses + "L &f(" + String.format("%.0f", winRate) + "%)"));
        }

        meta.setLore(lore);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag. HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 기타 통계 아이템 생성
     */
    private ItemStack createMiscStatsItem(PvPStatistics stats) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        meta. setDisplayName(MessageUtil.colorize("&d&l기타 통계"));

        List<String> lore = new ArrayList<>();
        lore. add("");
        lore.add(MessageUtil.colorize("&7퍼스트 블러드: &f" + stats.getFirstBloods() + "회"));
        lore.add(MessageUtil.colorize("&7셧다운:  &f" + stats.getShutdowns() + "회"));
        lore.add(MessageUtil.colorize("&7리벤지: &f" + stats.getRevenges() + "회"));
        lore. add("");
        lore. add(MessageUtil. colorize("&7더블 킬: &f" + stats.getDoubleKills() + "회"));
        lore.add(MessageUtil.colorize("&7트리플 킬: &f" + stats. getTripleKills() + "회"));
        lore. add(MessageUtil. colorize("&7멀티 킬:  &f" + stats.getMultiKills() + "회"));
        lore.add("");
        lore.add(MessageUtil.colorize("&7최고 데미지: &f" + String.format("%.1f", stats.getHighestDamageDealt())));
        lore.add(MessageUtil.colorize("&7최다 킬 (경기): &f" + stats.getMostKillsInMatch()));
        lore. add("");
        lore. add(MessageUtil. colorize("&7플레이 시간: &f" + stats.getPlayTimeString()));

        // 가장 많이 사용한 무기
        Material mostUsedWeapon = stats. getMostUsedWeapon();
        if (mostUsedWeapon != null) {
            lore.add("");
            lore. add(MessageUtil. colorize("&7최다 사용 무기:  &f" + formatWeaponName(mostUsedWeapon)));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 무기 이름 포맷
     */
    private String formatWeaponName(Material material) {
        return material.name().replace("_", " ").toLowerCase();
    }

    /**
     * 진행률 바 생성
     */
    private String createProgressBar(double progress) {
        int bars = 10;
        int filled = (int) (progress * bars);

        StringBuilder sb = new StringBuilder();
        sb.append("&a");
        for (int i = 0; i < filled; i++) {
            sb.append("█");
        }
        sb.append("&7");
        for (int i = filled; i < bars; i++) {
            sb.append("█");
        }
        sb.append(" &f").append(String.format("%.0f%%", progress * 100));

        return sb.toString();
    }

    /**
     * 닫기 버튼 생성
     */
    private ItemStack createCloseButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        meta. setDisplayName(MessageUtil.colorize("&c닫기"));

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event. getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = openInventories.get(player.getUniqueId());

        if (inventory == null || !event.getInventory().equals(inventory)) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();

        // 닫기 버튼
        if (slot == 49) {
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
    }
}