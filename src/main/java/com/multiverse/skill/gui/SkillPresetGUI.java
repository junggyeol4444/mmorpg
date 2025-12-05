package com.multiverse.skill.gui;

import com.multiverse.skill.SkillCore;
import com.multiverse.skill.managers.SkillManager;
import com.multiverse.skill.data.models.*;
import org.bukkit. Bukkit;
import org.bukkit.Material;
import org. bukkit.entity.Player;
import org.bukkit.inventory. Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory. meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 스킬 프리셋 GUI
 */
public class SkillPresetGUI {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final int guiSize = 27;
    private final String guiTitle = "§b스킬 프리셋";

    public SkillPresetGUI(SkillCore plugin, SkillManager skillManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
    }

    /**
     * 프리셋 GUI 열기
     */
    public void openPresetGUI(Player player) {
        if (player == null) {
            return;
        }

        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData == null) {
            player. sendMessage("§c스킬 데이터를 찾을 수 없습니다!");
            return;
        }

        Inventory gui = Bukkit.createInventory(null, guiSize, guiTitle);

        // 배경 채우기
        fillBackground(gui);

        // 프리셋 목록 표시
        displayPresets(gui, player, skillData);

        player.openInventory(gui);
    }

    /**
     * 프리셋 호트바 GUI 열기
     */
    public void openPresetHotbarGUI(Player player, SkillPreset preset) {
        if (player == null || preset == null) {
            return;
        }

        PlayerSkillData skillData = skillManager. getPlayerSkillData(player. getUniqueId());
        if (skillData == null) {
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 27, "§b" + preset.getName() + " - 호트바");

        // 배경 채우기
        fillBackground(gui);

        // 호트바 슬롯 표시
        displayHotbarSlots(gui, player, preset, skillData);

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
     * 프리셋 목록 표시
     */
    private void displayPresets(Inventory gui, Player player, PlayerSkillData skillData) {
        List<SkillPreset> presets = skillData.getPresets();
        int slotIndex = 10;

        for (int i = 0; i < presets.size(); i++) {
            if (slotIndex >= gui.getSize() - 10) {
                break;
            }

            SkillPreset preset = presets.get(i);
            ItemStack presetItem = createPresetItem(preset, i == skillData.getActivePresetIndex());
            gui. setItem(slotIndex, presetItem);
            slotIndex++;
        }
    }

    /**
     * 프리셋 아이템 생성
     */
    private ItemStack createPresetItem(SkillPreset preset, boolean isActive) {
        Material material = isActive ? Material.LIME_CONCRETE : Material.ORANGE_CONCRETE;
        ItemStack item = createItem(material, "§a" + preset.getName());
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        if (isActive) {
            lore.add("§a활성 프리셋");
        }
        lore.add("§7호트바 스킬: " + preset.getHotbar().size());
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    /**
     * 호트바 슬롯 표시
     */
    private void displayHotbarSlots(Inventory gui, Player player, SkillPreset preset, PlayerSkillData skillData) {
        Map<Integer, String> hotbar = preset.getHotbar();
        
        for (Map.Entry<Integer, String> entry : hotbar.entrySet()) {
            int slot = entry. getKey();
            String skillId = entry.getValue();
            
            if (slot < 0 || slot >= 9) {
                continue;
            }

            Skill skill = skillManager.getSkill(skillId);
            if (skill == null) {
                continue;
            }

            LearnedSkill learned = skillData.getSkill(skillId);
            ItemStack slotItem = createSlotItem(skill, learned, slot);
            gui.setItem(slot, slotItem);
        }
    }

    /**
     * 슬롯 아이템 생성
     */
    private ItemStack createSlotItem(Skill skill, LearnedSkill learned, int slot) {
        ItemStack item = createItem(Material.DIAMOND_SWORD, "§a" + skill.getName());
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§7슬롯: " + (slot + 1));
        
        if (learned != null) {
            lore.add("§7레벨: " + learned.getLevel());
        }
        
        lore.add("§7" + skill.getDescription());
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
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