package com.multiverse.pvp. gui;

import com.multiverse.pvp.PvPCore;
import com.multiverse.pvp.data.PvPArena;
import com.multiverse.pvp.enums. ArenaStatus;
import com. multiverse.pvp.enums.ArenaType;
import com.multiverse. pvp.utils. MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity.Player;
import org.bukkit. event.EventHandler;
import org.bukkit.event. Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory. InventoryCloseEvent;
import org.bukkit. inventory. Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ArenaGUI implements Listener {

    private final PvPCore plugin;
    private final Map<UUID, Inventory> openInventories;
    private final Map<UUID, Integer> currentPage;
    private final Map<UUID, ArenaType> selectedType;

    private static final String TITLE = "§6§l아레나 목록";
    private static final int ITEMS_PER_PAGE = 28;

    public ArenaGUI(PvPCore plugin) {
        this. plugin = plugin;
        this.openInventories = new HashMap<>();
        this.currentPage = new HashMap<>();
        this.selectedType = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * GUI 열기
     */
    public void open(Player player) {
        open(player, null, 0);
    }

    /**
     * 타입 필터와 페이지 지정하여 열기
     */
    public void open(Player player, ArenaType typeFilter, int page) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);

        selectedType.put(player.getUniqueId(), typeFilter);
        currentPage. put(player.getUniqueId(), page);

        // 아레나 목록 가져오기
        List<PvPArena> arenas;
        if (typeFilter != null) {
            arenas = plugin. getArenaManager().getArenasByType(typeFilter);
        } else {
            arenas = plugin.getArenaManager().getAllArenas();
        }

        // 페이지 계산
        int totalPages = (int) Math.ceil((double) arenas.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, arenas.size());

        // 아레나 아이템 배치
        int slot = 10;
        for (int i = startIndex; i < endIndex; i++) {
            PvPArena arena = arenas.get(i);
            inventory.setItem(slot, createArenaItem(arena));

            slot++;
            if ((slot + 1) % 9 == 0) {
                slot += 2;
            }
        }

        // 타입 필터 버튼 (상단)
        inventory.setItem(0, createFilterItem(null, typeFilter == null));
        int filterSlot = 1;
        for (ArenaType type : ArenaType.values()) {
            inventory.setItem(filterSlot, createFilterItem(type, type == typeFilter));
            filterSlot++;
            if (filterSlot > 5) break;
        }

        // 네비게이션 버튼 (하단)
        if (page > 0) {
            inventory.setItem(45, createNavigationItem("이전 페이지", Material.ARROW, page - 1));
        }

        inventory.setItem(49, createPageInfoItem(page + 1, totalPages));

        if (page < totalPages - 1) {
            inventory.setItem(53, createNavigationItem("다음 페이지", Material.ARROW, page + 1));
        }

        // 매칭 큐 버튼
        inventory.setItem(47, createQueueButton());

        // 닫기 버튼
        inventory.setItem(51, createCloseButton());

        openInventories.put(player.getUniqueId(), inventory);
        player.openInventory(inventory);
    }

    /**
     * 아레나 아이템 생성
     */
    private ItemStack createArenaItem(PvPArena arena) {
        Material material = arena.getType().getIcon();
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        // 이름
        String statusColor = arena.getStatus().getColor();
        meta.setDisplayName(MessageUtil.colorize("&f" + arena.getArenaName() + " " + statusColor + "[" + arena.getStatus().getDisplayName() + "]"));

        // 설명
        List<String> lore = new ArrayList<>();
        lore. add("");
        lore. add(MessageUtil.colorize("&7타입: " + arena.getType().getFormattedName()));
        lore.add(MessageUtil.colorize("&7인원: &f" + arena.getPlayers().size() + "/" + arena.getMaxPlayers()));
        lore.add(MessageUtil.colorize("&7최소 인원: &f" + arena.getMinPlayers()));
        lore.add(MessageUtil.colorize("&7경기 시간: &f" + arena. getMatchDuration() + "초"));
        lore.add("");

        if (arena.getStatus().canJoin() && ! arena.isFull()) {
            lore.add(MessageUtil.colorize("&a클릭하여 참가"));
        } else if (arena.getStatus().canSpectate()) {
            lore.add(MessageUtil.colorize("&e클릭하여 관전"));
        } else {
            lore.add(MessageUtil.colorize("&c참가 불가"));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * 필터 아이템 생성
     */
    private ItemStack createFilterItem(ArenaType type, boolean selected) {
        Material material;
        String name;

        if (type == null) {
            material = Material. COMPASS;
            name = "&f전체";
        } else {
            material = type.getIcon();
            name = type.getFormattedName();
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        if (selected) {
            meta.setDisplayName(MessageUtil. colorize("&a▶ " + name + " &a◀"));
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.setDisplayName(MessageUtil.colorize(name));
        }

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageUtil.colorize("&7클릭하여 필터 적용"));
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * 네비게이션 아이템 생성
     */
    private ItemStack createNavigationItem(String name, Material material, int targetPage) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        meta.setDisplayName(MessageUtil.colorize("&e" + name));

        List<String> lore = new ArrayList<>();
        lore.add(MessageUtil.colorize("&7페이지 " + (targetPage + 1)));
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * 페이지 정보 아이템 생성
     */
    private ItemStack createPageInfoItem(int current, int total) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        meta.setDisplayName(MessageUtil.colorize("&e페이지 " + current + "/" + total));

        item.setItemMeta(meta);
        return item;
    }

    /**
     * 매칭 큐 버튼 생성
     */
    private ItemStack createQueueButton() {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        meta. setDisplayName(MessageUtil.colorize("&b빠른 매칭"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore. add(MessageUtil. colorize("&7자동으로 매칭을 찾습니다. "));
        lore.add(MessageUtil.colorize("&7비슷한 레이팅의 상대와 매칭됩니다."));
        lore. add("");
        lore.add(MessageUtil.colorize("&a클릭하여 매칭 시작"));
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * 닫기 버튼 생성
     */
    private ItemStack createCloseButton() {
        ItemStack item = new ItemStack(Material. BARRIER);
        ItemMeta meta = item. getItemMeta();

        if (meta == null) return item;

        meta. setDisplayName(MessageUtil.colorize("&c닫기"));

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

        if (inventory == null || !event.getInventory().equals(inventory)) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        int slot = event.getRawSlot();

        // 필터 버튼 (0-5)
        if (slot >= 0 && slot <= 5) {
            ArenaType[] types = ArenaType.values();
            if (slot == 0) {
                open(player, null, 0);
            } else if (slot - 1 < types.length) {
                open(player, types[slot - 1], 0);
            }
            return;
        }

        // 이전 페이지
        if (slot == 45 && clicked.getType() == Material.ARROW) {
            int page = currentPage.getOrDefault(player.getUniqueId(), 0);
            if (page > 0) {
                open(player, selectedType. get(player.getUniqueId()), page - 1);
            }
            return;
        }

        // 다음 페이지
        if (slot == 53 && clicked.getType() == Material.ARROW) {
            int page = currentPage. getOrDefault(player.getUniqueId(), 0);
            open(player, selectedType.get(player. getUniqueId()), page + 1);
            return;
        }

        // 매칭 큐 버튼
        if (slot == 47) {
            player.closeInventory();
            ArenaType type = selectedType.get(player.getUniqueId());
            if (type == null) {
                type = ArenaType. DUEL_1V1;
            }
            plugin.getArenaManager().queueForArena(player, type);
            return;
        }

        // 닫기 버튼
        if (slot == 51) {
            player. closeInventory();
            return;
        }

        // 아레나 클릭
        if (slot >= 10 && slot <= 43) {
            String itemName = clicked.getItemMeta() != null ? clicked. getItemMeta().getDisplayName() : "";
            String arenaName = MessageUtil.stripColor(itemName).split(" \\[")[0].trim();

            PvPArena arena = plugin.getArenaManager().getArenaByName(arenaName);
            if (arena != null) {
                player.closeInventory();

                if (arena.getStatus().canJoin() && !arena.isFull()) {
                    plugin.getArenaManager().joinArena(player, arena. getArenaId());
                } else if (arena. getStatus().canSpectate()) {
                    plugin.getArenaManager().spectateArena(player, arena.getArenaId());
                } else {
                    MessageUtil.sendMessage(player, "&c이 아레나에 참가할 수 없습니다.");
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
        currentPage.remove(player.getUniqueId());
        selectedType.remove(player.getUniqueId());
    }
}