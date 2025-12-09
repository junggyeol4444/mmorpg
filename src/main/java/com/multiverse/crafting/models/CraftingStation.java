package com.multiverse.crafting.models;

import com.multiverse.crafting.models.enums.CraftingStationType;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

/**
 * Represents a placed crafting station with bonuses/upgrades.
 */
public class CraftingStation {

    private final Location location;          // may be null for requiredStation template
    private final CraftingStationType type;
    private final int tier;
    private final UUID owner;                 // nullable
    private final double successRateBonus;
    private final double speedBonus;
    private final int maxMassCraft;
    private final int upgradeLevel;
    private final Map<String, Object> upgrades;

    public CraftingStation(Location location,
                           CraftingStationType type,
                           int tier,
                           UUID owner,
                           double successRateBonus,
                           double speedBonus,
                           int maxMassCraft,
                           int upgradeLevel,
                           Map<String, Object> upgrades) {
        this.location = location;
        this.type = type;
        this.tier = tier;
        this.owner = owner;
        this.successRateBonus = successRateBonus;
        this.speedBonus = speedBonus;
        this.maxMassCraft = maxMassCraft;
        this.upgradeLevel = upgradeLevel;
        this.upgrades = upgrades;
    }

    public Location getLocation() { return location; }
    public CraftingStationType getType() { return type; }
    public int getTier() { return tier; }
    public UUID getOwner() { return owner; }
    public double getSuccessRateBonus() { return successRateBonus; }
    public double getSpeedBonus() { return speedBonus; }
    public int getMaxMassCraft() { return maxMassCraft; }
    public int getUpgradeLevel() { return upgradeLevel; }
    public Map<String, Object> getUpgrades() { return upgrades; }
}