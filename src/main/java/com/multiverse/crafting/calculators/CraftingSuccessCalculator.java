package com.multiverse.crafting.calculators;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.managers.CraftingSkillManager;
import com.multiverse.crafting.managers.CraftingStationManager;
import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.CraftingStation;
import com.multiverse.crafting.models.enums.CraftingResult;
import com.multiverse.crafting.models.enums.CraftingType;
import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Calculates success rates and rolls crafting results.
 */
public class CraftingSuccessCalculator {

    private final CraftingCore plugin;
    private final CraftingSkillManager skillManager;
    private final CraftingStationManager stationManager;
    private final SecureRandom random = new SecureRandom();

    public CraftingSuccessCalculator(CraftingCore plugin,
                                     CraftingSkillManager skillManager,
                                     CraftingStationManager stationManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.stationManager = stationManager;
    }

    /** Main success rate calculation */
    public double calculateSuccessRate(Player player, CraftingRecipe recipe) {
        double base = recipe.getSuccessRate(); // recipe-defined
        double bonus = 0.0;
        bonus += getSkillBonus(player, recipe.getType());
        bonus += getEquipmentBonus(player);
        bonus += getBuffBonus(player);
        bonus += getTimeBonus();
        bonus += getStationBonus(player, recipe);

        double finalRate = Math.min(plugin.getConfig().getDouble("crafting.success-rate.max-rate", 95.0), base + bonus);
        return Math.max(0, finalRate);
    }

    /** Skill bonus: +1% per level (configurable) */
    public double getSkillBonus(Player player, CraftingType type) {
        double perLevel = plugin.getConfig().getDouble("skills.bonuses.success-rate-per-level", 1.0);
        int level = skillManager.getLevel(player, type);
        return perLevel * level;
    }

    /** Equipment bonus: placeholder hook — should integrate with ItemCore / custom tags. */
    public double getEquipmentBonus(Player player) {
        // TODO: integrate with ItemCore; for now 0
        return 0.0;
    }

    /** Buff bonus: placeholder hook — should integrate with SkillCore/buffs/potions. */
    public double getBuffBonus(Player player) {
        // TODO: integrate with SkillCore or potion effects; for now 0
        return 0.0;
    }

    /** Time bonus: +5% during golden time (example: 18:00~23:59) */
    public double getTimeBonus() {
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.of(18, 0);
        LocalTime end = LocalTime.of(23, 59);
        if (!now.isBefore(start) && !now.isAfter(end)) {
            return 5.0;
        }
        return 0.0;
    }

    /** Station bonus: based on placed station tier/bonus if requiredStation matches. */
    private double getStationBonus(Player player, CraftingRecipe recipe) {
        if (recipe.getRequiredStation() == null) return 0.0;
        CraftingStation station = stationManager.getNearbyOrOwnedStation(player, recipe.getRequiredStation().getType());
        if (station == null) return 0.0;
        return station.getSuccessRateBonus();
    }

    /** Roll crafting result based on final success rate; supports GREAT/CRITICAL fail chance. */
    public CraftingResult rollResult(double successRate) {
        // successRate capped externally
        double roll = random.nextDouble() * 100.0;
        double greatSuccessThreshold = Math.max(0, successRate - 20.0); // ex: last 20% window for GREAT
        if (roll <= successRate) {
            // Check great success inside top window
            if (roll >= greatSuccessThreshold) {
                return CraftingResult.GREAT_SUCCESS;
            }
            return CraftingResult.SUCCESS;
        }
        // Fail paths
        double critFailChance = 5.0; // fixed 5% crit fail among fails
        double failRoll = ThreadLocalRandom.current().nextDouble() * 100.0;
        if (failRoll <= critFailChance) {
            return CraftingResult.CRITICAL_FAIL;
        }
        return CraftingResult.FAIL;
    }
}