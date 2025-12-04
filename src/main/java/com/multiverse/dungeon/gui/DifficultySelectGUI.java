package com.multiverse.dungeon.gui;

import com. multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.data.enums.DungeonDifficulty;
import org. bukkit.entity.Player;
import org.bukkit.event. inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 난이도 선택 GUI
 */
public class DifficultySelectGUI extends AbstractDungeonGUI {

    private final DungeonCore plugin;
    private final String dungeonId;

    /**
     * 생성자
     */
    public DifficultySelectGUI(Player player, DungeonCore plugin, String dungeonId) {
        super(player, "§6난이도 선택");
        this.plugin = plugin;
        this.dungeonId = dungeonId;
    }

    @Override
    public void initialize() {
        fillBorder();
        
        // 쉬움
        var easyItem = new ItemStack(org.bukkit.Material.LIME_CONCRETE);
        var easyMeta = easyItem.getItemMeta();
        if (easyMeta != null) {
            easyMeta. setDisplayName("§a쉬움");
            easyItem.setItemMeta(easyMeta);
        }
        setItem(11, easyItem);
        
        // 보통
        var normalItem = new ItemStack(org.bukkit. Material.YELLOW_CONCRETE);
        var normalMeta = normalItem.getItemMeta();
        if (normalMeta != null) {
            normalMeta.setDisplayName("§e보통");
            normalItem. setItemMeta(normalMeta);
        }
        setItem(22, normalItem);
        
        // 어려움
        var hardItem = new ItemStack(org.bukkit.Material.ORANGE_CONCRETE);
        var hardMeta = hardItem.getItemMeta();
        if (hardMeta != null) {
            hardMeta.setDisplayName("§6어려움");
            hardItem.setItemMeta(hardMeta);
        }
        setItem(33, hardItem);
        
        // 극악
        var extremeItem = new ItemStack(org.bukkit. Material.RED_CONCRETE);
        var extremeMeta = extremeItem.getItemMeta();
        if (extremeMeta != null) {
            extremeMeta.setDisplayName("§c극악");
            extremeItem.setItemMeta(extremeMeta);
        }
        setItem(44, extremeItem);
        
        addCloseButton();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event. setCancelled(true);
        
        int slot = event.getSlot();
        
        switch (slot) {
            case 11:
                enterDungeon(DungeonDifficulty. EASY);
                break;
            case 22:
                enterDungeon(DungeonDifficulty.NORMAL);
                break;
            case 33:
                enterDungeon(DungeonDifficulty. HARD);
                break;
            case 44:
                enterDungeon(DungeonDifficulty.EXTREME);
                break;
            case 49:
                close();
                break;
        }
    }

    /**
     * 던전 입장
     */
    private void enterDungeon(DungeonDifficulty difficulty) {
        if (player == null) return;
        
        var dungeonEnterCommand = new com.multiverse.dungeon.commands.subcommands.dungeon.EnterSubCommand(plugin);
        dungeonEnterCommand. execute(player, new String[]{dungeonId, difficulty.name()});
        
        close();
    }
}