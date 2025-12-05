package com.multiverse.skill. gui;

import com.multiverse.skill.SkillCore;
import com.multiverse. skill.managers.SkillManager;
import com.multiverse. skill.data.models.*;
import org.bukkit. Bukkit;
import org.bukkit.Material;
import org.bukkit. entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory. ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util. ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 스킬 목록 GUI
 */
public class SkillListGUI {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final int guiSize = 54;
    private final String guiTitle = "§b습득한 스킬";

    public SkillListGUI(SkillCore plugin, SkillManager skillManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
    }

    /**
     * 스킬 목록 GUI 열기
     */
    public void openSkillListGUI(Player player) {
        if (player == null) {
            return;
        }

        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData == null) {
            player.sendMessage("§c스킬 데이터를 찾을 수 없습니다!");
            return;
        }

        Inventory gui = Bukkit.createInventory(null, guiSize, guiTitle);

        // 배경 채우기
        fillBackground(gui);

        // 습득한 스킬 표시
        displayLearnedSkills(gui, player, skillData);

        // 정보 패널
        addInfoPanel(gui, skillData);

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
     * 습득한 스킬 표시
     */
    private void displayLearnedSkills(Inventory gui, Player player, PlayerSkillData skillData) {
        Map<String, LearnedSkill> skills = skillData.getSkills();
        int slotIndex = 10;

        for (Map.Entry<String, LearnedSkill> entry : skills.entrySet()) {
            if (slotIndex >= gui.getSize() - 10) {
                break;
            }

            String skillId = entry.getKey();
            LearnedSkill learned = entry.getValue();
            
            Skill skill = skillManager.getSkill(skillId);
            if (skill == null) {
                continue;
            }

            ItemStack skillItem = createSkillItem(skill, learned);
            gui.setItem(slotIndex, skillItem);
            slotIndex++;
        }
    }

    /**
     * 스킬 아이템 생성
     */
    private ItemStack createSkillItem(Skill skill, LearnedSkill learned) {
        ItemStack item = createItem(Material.ENCHANTED_BOOK, "§a" + skill.getName());
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§7레벨: " + learned.getLevel() + "/" + skill.getMaxLevel());
        lore.add("§7경험치: " + learned.getExperience());
        lore.add("§7사용 횟수: " + learned.getTimesUsed());
        lore.add("§7총 데미지: " + learned.getTotalDamage());
        lore. add("§7");
        lore.add("§7" + skill.getDescription());
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    /**
     * 정보 패널 추가
     */
    private void addInfoPanel(Inventory gui, PlayerSkillData skillData) {
        ItemStack info = createItem(Material. KNOWLEDGE_BOOK, "§b스킬 정보");
        ItemMeta meta = info.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§7총 스킬 포인트: " + skillData. getTotalSkillPoints());
        lore. add("§7사용한 포인트: " + skillData. getUsedSkillPoints());
        lore. add("§7남은 포인트: " + skillData.getAvailableSkillPoints());
        lore.add("§7습득한 스킬: " + skillData.getSkills().size());
        
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
        item. setItemMeta(meta);
        return item;
    }
}