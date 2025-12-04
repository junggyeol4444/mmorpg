package com.multiverse.  dungeon.gui;

import com.multiverse.dungeon.DungeonCore;
import org.bukkit.entity.Player;
import org.bukkit.event.  inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 던전 목록 GUI
 */
public class DungeonListGUI extends AbstractDungeonGUI {

    private final DungeonCore plugin;
    private int page = 0;
    private static final int ITEMS_PER_PAGE = 21;

    /**
     * 생성자
     */
    public DungeonListGUI(Player player, DungeonCore plugin) {
        super(player, "§6=== 던전 목록 ===");
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        fillBorder();
        
        var dungeons = plugin.getDungeonManager().getEnabledDungeons();
        
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, dungeons.size());
        
        int slot = 10;
        for (int i = startIndex; i < endIndex; i++) {
            if (slot >= 44) break;
            
            var dungeon = dungeons.get(i);
            var item = createDungeonItem(dungeon, i);
            
            setItem(slot, item);
            
            if ((slot - 10 + 1) % 7 == 0) {
                slot += 3; // 다음 줄로
            } else {
                slot++;
            }
        }
        
        int maxPage = (dungeons.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        addPreviousButton(page);
        addCloseButton();
        addNextButton(page, maxPage - 1);
    }

    /**
     * 던전 아이템 생성
     */
    private ItemStack createDungeonItem(com.multiverse.dungeon.data.model.Dungeon dungeon, int index) {
        var item = new ItemStack(org.bukkit.Material.BOOK);
        var meta = item. getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName("§b" + dungeon.getName());
            
            java.util.List<String> lore = new java.util.ArrayList<>();
            lore.add("§7타입: §f" + dungeon.getType(). getDisplayName());
            lore.add("§7난이도: §f" + dungeon.getDifficulty().getDisplayName());
            lore.add("§7필요 레벨: §f" + dungeon.getRequiredLevel());
            lore.add("§7");
            lore.add("§e클릭하여 정보 조회");
            
            meta. setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        int slot = event.getSlot();
        
        if (slot == 48) {
            if (page > 0) {
                page--;
                open();
            }
        } else if (slot == 50) {
            page++;
            open();
        } else if (slot == 49) {
            close();
        }
    }
}