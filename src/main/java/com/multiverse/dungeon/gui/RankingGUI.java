package com.multiverse. dungeon.gui;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.data.enums.DungeonDifficulty;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory. InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 리더보드 GUI
 */
public class RankingGUI extends AbstractDungeonGUI {

    private final DungeonCore plugin;
    private final String dungeonId;
    private DungeonDifficulty difficulty = DungeonDifficulty. NORMAL;

    /**
     * 생성자
     */
    public RankingGUI(Player player, DungeonCore plugin, String dungeonId) {
        super(player, "§6=== 리더보드 ===");
        this. plugin = plugin;
        this. dungeonId = dungeonId;
    }

    @Override
    public void initialize() {
        fillBorder();
        
        var dungeon = plugin.getDungeonManager().getDungeon(dungeonId);
        if (dungeon == null) {
            close();
            return;
        }
        
        var records = plugin.getLeaderboardManager().getTopRecords(dungeonId, difficulty, 21);
        
        int slot = 10;
        int rank = 1;
        
        for (var record : records) {
            if (slot >= 44) break;
            
            var item = createRankingItem(rank, record);
            setItem(slot, item);
            
            if ((slot - 10 + 1) % 7 == 0) {
                slot += 3;
            } else {
                slot++;
            }
            
            rank++;
        }
        
        // 난이도 버튼
        var easyItem = new ItemStack(org.bukkit. Material.LIME_CONCRETE);
        var easyMeta = easyItem.getItemMeta();
        if (easyMeta != null) {
            easyMeta. setDisplayName("§a쉬움");
            easyItem.setItemMeta(easyMeta);
        }
        setItem(30, easyItem);
        
        var normalItem = new ItemStack(org.bukkit.Material.YELLOW_CONCRETE);
        var normalMeta = normalItem.getItemMeta();
        if (normalMeta != null) {
            normalMeta.setDisplayName("§e보통");
            normalItem.setItemMeta(normalMeta);
        }
        setItem(40, normalItem);
        
        var hardItem = new ItemStack(org.bukkit.Material. ORANGE_CONCRETE);
        var hardMeta = hardItem.getItemMeta();
        if (hardMeta != null) {
            hardMeta.setDisplayName("§6어려움");
            hardItem. setItemMeta(hardMeta);
        }
        setItem(41, hardItem);
        
        addCloseButton();
    }

    /**
     * 순위 아이템 생성
     */
    private ItemStack createRankingItem(int rank, com.multiverse.dungeon.data.model.DungeonRecord record) {
        var item = new ItemStack(org.bukkit.Material.BOOK);
        var meta = item. getItemMeta();
        
        if (meta != null) {
            String rankDisplay = "§b#" + rank + " ";
            String players = "§f" + String.join(", ", record.getPlayerNames());
            meta.setDisplayName(rankDisplay + players);
            
            java. util.List<String> lore = new java.util.ArrayList<>();
            lore.add("§b클리어 시간: §f" + record.getClearTimeFormatted());
            lore.add("§b점수: §f" + record. getScore());
            lore.add("§b난이도: §f" + record.getDifficulty());
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        int slot = event.getSlot();
        
        switch (slot) {
            case 30:
                difficulty = DungeonDifficulty.EASY;
                open();
                break;
            case 40:
                difficulty = DungeonDifficulty. NORMAL;
                open();
                break;
            case 41:
                difficulty = DungeonDifficulty.HARD;
                open();
                break;
            case 49:
                close();
                break;
        }
    }
}