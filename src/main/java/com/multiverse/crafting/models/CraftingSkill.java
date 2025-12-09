package com.multiverse.crafting.models;

import com.multiverse.crafting.models.enums.CraftingType;

/**
 * DTO representing a player's crafting skill state.
 */
public class CraftingSkill {

    private final CraftingType type;
    private int level;
    private long experience;

    public CraftingSkill(CraftingType type, int level, long experience) {
        this.type = type;
        this.level = level;
        this.experience = experience;
    }

    public CraftingType getType() { return type; }
    public int getLevel() { return level; }
    public long getExperience() { return experience; }

    public void setLevel(int level) { this.level = level; }
    public void setExperience(long experience) { this.experience = experience; }
}