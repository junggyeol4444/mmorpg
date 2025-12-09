package com.multiverse.crafting.managers;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.data.DataManager;
import com.multiverse.crafting.models.enums.CraftingType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Manages crafting skill levels and experience.
 */
public class CraftingSkillManager {

    private final CraftingCore plugin;
    private final CraftingDataManager dataManager;

    public CraftingSkillManager(CraftingCore plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = new CraftingDataManager(plugin, dataManager);
    }

    public int getLevel(Player player, CraftingType type) {
        return dataManager.getLevel(player.getUniqueId(), type);
    }

    public long getExperience(Player player, CraftingType type) {
        return dataManager.getExp(player.getUniqueId(), type);
    }

    public void setLevel(Player player, CraftingType type, int level) {
        dataManager.setLevel(player.getUniqueId(), type, level);
    }

    public void addExperience(Player player, CraftingType type, long amount) {
        long current = getExperience(player, type);
        long newExp = Math.max(0, current + amount);
        int level = getLevel(player, type);
        int newLevel = level;
        long required = expForLevel(level + 1);
        while (newExp >= required) {
            newExp -= required;
            newLevel++;
            required = expForLevel(newLevel + 1);
            // Fire level-up event
            Bukkit.getPluginManager().callEvent(new com.multiverse.crafting.events.CraftingSkillLevelUpEvent(
                    player, type, newLevel - 1, newLevel
            ));
        }
        dataManager.setLevel(player.getUniqueId(), type, newLevel);
        dataManager.setExp(player.getUniqueId(), type, newExp);
    }

    private long expForLevel(int level) {
        // Simple curve: base 100, grows quadratically
        long base = plugin.getConfig().getLong("skills.exp.base", 100);
        double scale = plugin.getConfig().getDouble("skills.exp.scale", 1.2);
        return (long) (base * Math.pow(level, scale));
    }
}