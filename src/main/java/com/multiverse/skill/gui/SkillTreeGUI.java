package com.multiverse. skill.gui;

import com.multiverse.skill. SkillCore;
import com.multiverse. skill.managers.SkillManager;
import com.multiverse. skill.data.models.*;
import com.multiverse.skill.data.enums.SkillType;
import org.bukkit. Bukkit;
import org.bukkit.Material;
import org.bukkit. entity.Player;
import org. bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * 스킬 트리 GUI
 */
public class SkillTreeGUI {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final int guiSize = 54;
    private final String guiTitle = "§b스킬 트리";

    public SkillTreeGUI(SkillCore plugin, SkillManager skillManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
    }

    /**
     * 스킬 트리 GUI 열기
     */
    public void openSkillTreeGUI(Player player, String treeId) {
        if (player == null || treeId == null) {
            return;
        }

        SkillTree tree = skillManager. getSkillTree(treeId);
        if (tree == null) {
            player.sendMessage("§c스킬 트리를 찾을 수 없습니다!");
            return;
        }

        Inventory gui = Bukkit.createInventory(null, guiSize, guiTitle + " - " + tree.getName());

        // 배경 채우기
        fillBackground(gui);

        // 스킬 노드 표시
        displaySkillNodes(gui, player, tree);

        // 정보 창
        addInfoPanel(gui, tree);

        player.openInventory(gui);
    }

    /**
     * GUI 배경 채우기
     */
    private void fillBackground(Inventory gui) {
        ItemStack background = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, background);
            }
        }
    }

    /**
     * 스킬 노드 표시
     */
    private void displaySkillNodes(Inventory gui, Player player, SkillTree tree) {
        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData == null) {
            return;
        }

        int slotIndex = 10;
        for (SkillNode node : tree.getNodes()) {
            if (slotIndex >= gui.getSize()) {
                break;
            }

            ItemStack nodeItem = createNodeItem(player, node, skillData);
            gui.setItem(slotIndex, nodeItem);
            slotIndex += 2;
        }
    }

    /**
     * 스킬 노드 아이템 생성
     */
    private ItemStack createNodeItem(Player player, SkillNode node, PlayerSkillData skillData) {
        Skill skill = skillManager.getSkill(node.getSkillId());
        if (skill == null) {
            return createItem(Material.BARRIER, "§c알 수 없는 스킬");
        }

        LearnedSkill learned = skillData.getSkill(node.getSkillId());
        Material material = learned != null ? Material.LIME_CONCRETE : Material.RED_CONCRETE;

        ItemStack item = createItem(material, "§a" + skill.getName());
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§7Tier: " + node.getTier());
        lore.add("§7필요 포인트: " + node.getRequiredPoints());
        
        if (learned != null) {
            lore.add("§a습득 (Lv." + learned.getLevel() + ")");
        } else {
            lore.add("§c미습득");
        }
        
        lore.add("§7" + skill.getDescription());
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    /**
     * 정보 패널 추가
     */
    private void addInfoPanel(Inventory gui, SkillTree tree) {
        ItemStack info = createItem(Material.BOOK, "§b" + tree.getName());
        ItemMeta meta = info.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§7" + tree.getDescription());
        lore.add("§7최대 포인트: " + tree.getMaxPoints());
        lore.add("§7필요 레벨: " + tree.getRequiredLevel());
        
        meta.setLore(lore);
        info.setItemMeta(meta);
        
        gui.setItem(gui.getSize() - 5, info);
    }

    /**
     * 아이템 생성 헬퍼
     */
    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}