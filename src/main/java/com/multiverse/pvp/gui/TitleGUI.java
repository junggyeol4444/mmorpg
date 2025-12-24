package com.multiverse.pvp. gui;

import com.multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPRanking;
import com.multiverse. pvp.data. PvPStatistics;
import com.multiverse.pvp.data.PvPTitle;
import com. multiverse.pvp.enums.TitleCategory;
import com. multiverse.pvp.utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity.Player;
import org.bukkit. event.EventHandler;
import org.bukkit.event. Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TitleGUI implements Listener {

    private final PvPCore plugin;
    private final Map<UUID, Inventory> openInventories;
    private final Map<UUID, TitleCategory> selectedCategory;
    private final Map<UUID, Integer> currentPage;

    private static final String TITLE = "§6§l칭호 관리";
    private static final int ITEMS_PER_PAGE = 21;

    public TitleGUI(PvPCore plugin) {
        this. plugin = plugin;
        this.openInventories = new HashMap<>();
        this.selectedCategory = new HashMap<>();
        this.currentPage = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * GUI 열기
     */
    public void open(Player player) {
        open(player, null, 0);
    }

    /**
     * 카테고리와 페이지 지정하여 열기
     */
    public void open(Player player, TitleCategory category, int page) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);

        selectedCategory.put(player.getUniqueId(), category);
        currentPage.put(player.getUniqueId(), page);

        // 칭호 목록 가져오기
        List<PvPTitle> titles;
        if (category != null) {
            titles = plugin.getTitleManager().getTitlesByCategory(category);
        } else {
            titles = plugin. getTitleManager().getAllTitles();
        }

        // 정렬 (해금된 것 먼저, 희귀도 순)
        List<PvPTitle> unlockedTitles = new ArrayList<>();
        List<PvPTitle> lockedTitles = new ArrayList<>();

        for (PvPTitle title : titles) {
            if (plugin.getTitleManager().hasTitle(player, title. getTitleId())) {
                unlockedTitles.add(title);
            } else {
                lockedTitles.add(title);
            }
        }

        unlockedTitles.sort((a, b) -> b.getRarity().ordinal() - a.getRarity().ordinal());
        lockedTitles.sort((a, b) -> b.getRarity().ordinal() - a.getRarity().ordinal());

        List<PvPTitle> sortedTitles = new ArrayList<>();
        sortedTitles.addAll(unlockedTitles);
        sortedTitles.addAll(lockedTitles);

        // 페이지 계산
        int totalPages = (int) Math.ceil((double) sortedTitles.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, sortedTitles. size());

        // 카테고리 필터 버튼 (상단)
        inventory.setItem(0, createCategoryButton(null, category == null));
        int filterSlot = 1;
        for (TitleCategory cat : TitleCategory.values()) {
            if (filterSlot > 7) break;
            inventory.setItem(filterSlot, createCategoryButton(cat, cat == category));
            filterSlot++;
        }

        // 칭호 아이템 배치
        int slot = 10;
        for (int i = startIndex; i < endIndex; i++) {
            PvPTitle title = sortedTitles.get(i);
            boolean unlocked = plugin.getTitleManager().hasTitle(player, title.getTitleId());
            boolean active = title.getTitleId().equals(plugin.getTitleManager().getActiveTitle(player));

            inventory.setItem(slot, createTitleItem(player, title, unlocked, active));

            slot++;
            if ((slot + 1) % 9 == 0) {
                slot += 2;
            }
        }

        // 현재 장착 칭호 표시
        inventory.setItem(49, createCurrentTitleItem(player));

        // 칭호 해제 버튼
        inventory.setItem(47, createUnequipButton());

        // 네비게이션
        if (page > 0) {
            inventory.setItem(45, createNavigationItem("이전 페이지", page - 1));
        }

        inventory. setItem(48, createPageInfoItem(page + 1, totalPages));

        if (page < totalPages - 1) {
            inventory.setItem(53, createNavigationItem("다음 페이지", page + 1));
        }

        // 닫기 버튼
        inventory. setItem(51, createCloseButton());

        openInventories.put(player.getUniqueId(), inventory);
        player.openInventory(inventory);
    }

    /**
     * 카테고리 버튼 생성
     */
    private ItemStack createCategoryButton(TitleCategory category, boolean selected) {
        Material material;
        String name;

        if (category == null) {
            material = Material. COMPASS;
            name = "&f전체";
        } else {
            material = category.getIcon();
            name = category.getFormattedName();
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        if (selected) {
            meta.setDisplayName(MessageUtil. colorize("&a▶ " + name + " &a◀"));
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } else {
            meta. setDisplayName(MessageUtil.colorize(name));
        }

        List<String> lore = new ArrayList<>();
        if (category != null) {
            lore.add("");
            lore. add(MessageUtil. colorize("&7" + category.getDescription()));
        }
        lore.add("");
        lore. add(MessageUtil. colorize("&e클릭하여 필터"));
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * 칭호 아이템 생성
     */
    private ItemStack createTitleItem(Player player, PvPTitle title, boolean unlocked, boolean active) {
        Material material;
        
        if (active) {
            material = Material.ENCHANTED_BOOK;
        } else if (unlocked) {
            material = Material.BOOK;
        } else {
            material = Material.GRAY_DYE;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        // 이름
        String displayName;
        if (active) {
            displayName = "&a&l[장착중] " + title.getFormattedName();
        } else if (unlocked) {
            displayName = title.getFormattedName();
        } else {
            displayName = "&8[잠김] &7" + title.getDisplayName();
        }
        meta.setDisplayName(MessageUtil. colorize(displayName));

        // 설명
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageUtil.colorize("&7희귀도: " + title.getRarity().getFormattedName()));
        lore.add(MessageUtil.colorize("&7카테고리: " + title.getCategory().getFormattedName()));
        lore.add("");

        if (! title.getDescription().isEmpty()) {
            lore.add(MessageUtil.colorize("&f" + title.getDescription()));
            lore.add("");
        }

        // 미리보기
        if (! title.getPrefix().isEmpty()) {
            lore.add(MessageUtil.colorize("&7미리보기:  " + title.formatPlayerName(player.getName())));
            lore.add("");
        }

        // 보너스
        if (! title.getBonuses().isEmpty()) {
            lore.add(MessageUtil.colorize("&6보너스: "));
            for (Map.Entry<String, Double> bonus : title. getBonuses().entrySet()) {
                String bonusName = formatBonusName(bonus.getKey());
                String bonusValue = String. format("+%. 1f%%", bonus.getValue() * 100);
                lore.add(MessageUtil.colorize("  &e" + bonusName + ": &a" + bonusValue));
            }
            lore.add("");
        }

        if (unlocked) {
            if (active) {
                lore.add(MessageUtil.colorize("&a현재 장착중"));
            } else {
                lore.add(MessageUtil.colorize("&a클릭하여 장착"));
            }
        } else {
            // 해금 조건 표시
            lore.add(MessageUtil.colorize("&c해금 조건: "));
            lore.add(MessageUtil.colorize("&7" + title.getUnlockDescription()));
            lore.add("");

            // 진행률
            PvPStatistics stats = plugin.getStatisticsManager().getStatistics(player);
            PvPRanking ranking = plugin.getRankingManager().getRanking(player);

            double progress = title.getProgress(
                    stats.getTotalKills(),
                    stats. getTotalWins(),
                    stats.getLongestKillStreak()
            );

            lore.add(MessageUtil.colorize("&7진행률: " + createProgressBar(progress) + " &f" + String.format("%.0f%%", progress * 100)));
        }

        meta. setLore(lore);

        if (active) {
            meta.addEnchant(org.bukkit.enchantments. Enchantment. DURABILITY, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }

        item. setItemMeta(meta);
        return item;
    }

    /**
     * 보너스 이름 포맷
     */
    private String formatBonusName(String key) {
        switch (key) {
            case "kill_exp":  return "킬 경험치";
            case "kill_money": return "킬 보상";
            case "win_exp": return "승리 경험치";
            case "win_money": return "승리 보상";
            case "streak_bonus": return "스트릭 보너스";
            case "all_exp": return "모든 경험치";
            case "all_money": return "모든 보상";
            default:  return key;
        }
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

        return sb.toString();
    }

    /**
     * 현재 칭호 아이템 생성
     */
    private ItemStack createCurrentTitleItem(Player player) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        String activeTitleId = plugin.getTitleManager().getActiveTitle(player);
        PvPTitle activeTitle = activeTitleId != null ? plugin.getTitleManager().getTitle(activeTitleId) : null;

        meta.setDisplayName(MessageUtil.colorize("&e&l현재 칭호"));

        List<String> lore = new ArrayList<>();
        lore.add("");

        if (activeTitle != null) {
            lore.add(MessageUtil.colorize("&7장착중: " + activeTitle.getFormattedName()));
            lore.add("");
            lore.add(MessageUtil.colorize("&7표시:  " + activeTitle.formatPlayerName(player. getName())));

            if (!activeTitle.getBonuses().isEmpty()) {
                lore.add("");
                lore. add(MessageUtil. colorize("&6활성 보너스:"));
                for (Map.Entry<String, Double> bonus : activeTitle.getBonuses().entrySet()) {
                    String bonusName = formatBonusName(bonus.getKey());
                    String bonusValue = String.format("+%.1f%%", bonus.getValue() * 100);
                    lore.add(MessageUtil.colorize("  &e" + bonusName + ": &a" + bonusValue));
                }
            }
        } else {
            lore.add(MessageUtil.colorize("&7장착된 칭호가 없습니다. "));
        }

        lore.add("");
        int unlockedCount = plugin.getTitleManager().getUnlockedTitles(player).size();
        int totalCount = plugin.getTitleManager().getAllTitles().size();
        lore.add(MessageUtil.colorize("&7해금:  &f" + unlockedCount + "/" + totalCount));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 칭호 해제 버튼 생성
     */
    private ItemStack createUnequipButton() {
        ItemStack item = new ItemStack(Material. BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        meta.setDisplayName(MessageUtil.colorize("&c칭호 해제"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore. add(MessageUtil. colorize("&7현재 장착된 칭호를 해제합니다."));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 네비게이션 아이템 생성
     */
    private ItemStack createNavigationItem(String name, int targetPage) {
        ItemStack item = new ItemStack(Material. ARROW);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        meta. setDisplayName(MessageUtil.colorize("&e" + name));

        item.setItemMeta(meta);
        return item;
    }

    /**
     * 페이지 정보 아이템 생성
     */
    private ItemStack createPageInfoItem(int current, int total) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        meta. setDisplayName(MessageUtil.colorize("&e페이지 " + current + "/" + total));

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
        Inventory inventory = openInventories.get(player.getUniqueId());

        if (inventory == null || !event. getInventory().equals(inventory)) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event. getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        int slot = event.getRawSlot();

        // 카테고리 필터 (0-7)
        if (slot >= 0 && slot <= 7) {
            TitleCategory[] categories = TitleCategory.values();
            if (slot == 0) {
                open(player, null, 0);
            } else if (slot - 1 < categories.length) {
                open(player, categories[slot - 1], 0);
            }
            return;
        }

        // 이전 페이지
        if (slot == 45 && clicked. getType() == Material.ARROW) {
            int page = currentPage. getOrDefault(player.getUniqueId(), 0);
            if (page > 0) {
                open(player, selectedCategory. get(player.getUniqueId()), page - 1);
            }
            return;
        }

        // 다음 페이지
        if (slot == 53 && clicked.getType() == Material.ARROW) {
            int page = currentPage.getOrDefault(player.getUniqueId(), 0);
            open(player, selectedCategory.get(player. getUniqueId()), page + 1);
            return;
        }

        // 칭호 해제
        if (slot == 47) {
            plugin.getTitleManager().setActiveTitle(player, null);
            open(player, selectedCategory.get(player.getUniqueId()), currentPage.getOrDefault(player.getUniqueId(), 0));
            return;
        }

        // 닫기 버튼
        if (slot == 51) {
            player.closeInventory();
            return;
        }

        // 칭호 클릭
        if (slot >= 10 && slot <= 43 && clicked.getType() != Material. GRAY_DYE) {
            String itemName = clicked.getItemMeta() != null ?  clicked.getItemMeta().getDisplayName() : "";
            
            // 칭호 ID 찾기
            for (PvPTitle title :  plugin.getTitleManager().getAllTitles()) {
                if (itemName.contains(title.getDisplayName())) {
                    if (plugin.getTitleManager().hasTitle(player, title.getTitleId())) {
                        plugin.getTitleManager().setActiveTitle(player, title.getTitleId());
                        open(player, selectedCategory.get(player.getUniqueId()), currentPage.getOrDefault(player.getUniqueId(), 0));
                    }
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        openInventories.remove(player.getUniqueId());
        selectedCategory.remove(player.getUniqueId());
        currentPage.remove(player.getUniqueId());
    }
}