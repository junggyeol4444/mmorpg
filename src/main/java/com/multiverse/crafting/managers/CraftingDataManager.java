package com.multiverse.crafting.managers;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.data.DataManager;
import com.multiverse.crafting.data.storage.PlayerDataStorage;
import com.multiverse.crafting.models.enums.CraftingType;
import com.multiverse.crafting.models.enums.LearnMethod;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

/**
 * Handles player-specific data such as known recipes, skill levels/exp.
 * Persists via PlayerDataStorage (players/<UUID>.yml).
 */
public class CraftingDataManager {

    private final CraftingCore plugin;
    private final DataManager dataManager;
    private final PlayerDataStorage playerData;

    public CraftingDataManager(CraftingCore plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.playerData = dataManager.getPlayerDataStorage();
    }

    /* ---------- Lifecycle ---------- */

    public void loadOnlinePlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                dataManager.loadPlayer(p.getUniqueId());
                ensurePlayerDefaults(p.getUniqueId(), p.getName());
            } catch (Exception ex) {
                plugin.getLogger().warning("Failed to load player data for " + p.getName() + ": " + ex.getMessage());
            }
        }
    }

    public void saveOnlinePlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                dataManager.savePlayer(p.getUniqueId());
            } catch (Exception ex) {
                plugin.getLogger().warning("Failed to save player data for " + p.getName() + ": " + ex.getMessage());
            }
        }
    }

    public void savePlayer(UUID uuid) {
        try {
            dataManager.savePlayer(uuid);
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to save player data: " + ex.getMessage());
        }
    }

    public void loadPlayer(UUID uuid) {
        try {
            dataManager.loadPlayer(uuid);
            ensurePlayerDefaults(uuid, null);
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to load player data: " + ex.getMessage());
        }
    }

    /* ---------- Defaults ---------- */

    public void ensurePlayerDefaults(UUID uuid, String name) {
        YamlConfiguration cfg = playerData.getCached(uuid);
        if (cfg == null) return;
        if (name != null && !name.isBlank()) {
            cfg.set("name", name);
        }
        if (!cfg.isList("known-recipes")) {
            cfg.set("known-recipes", new ArrayList<String>());
        }
        for (CraftingType type : CraftingType.values()) {
            String base = "skills." + type.name();
            if (!cfg.isInt(base + ".level")) cfg.set(base + ".level", 1);
            if (!cfg.isLong(base + ".exp")) cfg.set(base + ".exp", 0L);
        }
        try {
            playerData.save(uuid);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save defaults for player " + uuid + ": " + e.getMessage());
        }
    }

    /* ---------- Recipe knowledge ---------- */

    public boolean knowsRecipe(UUID uuid, String recipeId) {
        YamlConfiguration cfg = playerData.getCached(uuid);
        if (cfg == null) return false;
        List<String> known = cfg.getStringList("known-recipes");
        return known.contains(recipeId);
    }

    public void learnRecipe(UUID uuid, String recipeId, LearnMethod method) {
        YamlConfiguration cfg = playerData.getCached(uuid);
        if (cfg == null) return;
        List<String> known = new ArrayList<>(cfg.getStringList("known-recipes"));
        if (!known.contains(recipeId)) {
            known.add(recipeId);
            cfg.set("known-recipes", known);
            try {
                playerData.save(uuid);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to save learned recipe " + recipeId + " for " + uuid + ": " + e.getMessage());
            }
            // Fire event
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                Bukkit.getPluginManager().callEvent(new com.multiverse.crafting.events.RecipeLearnEvent(
                        p,
                        plugin.getRecipeManager().getRecipe(recipeId),
                        method == null ? LearnMethod.ADMIN : method
                ));
            }
        }
    }

    public List<String> getKnownRecipeIds(UUID uuid) {
        YamlConfiguration cfg = playerData.getCached(uuid);
        if (cfg == null) return Collections.emptyList();
        return new ArrayList<>(cfg.getStringList("known-recipes"));
    }

    /* ---------- Skill data ---------- */

    public int getLevel(UUID uuid, CraftingType type) {
        YamlConfiguration cfg = playerData.getCached(uuid);
        if (cfg == null) return 1;
        return cfg.getInt("skills." + type.name() + ".level", 1);
    }

    public long getExp(UUID uuid, CraftingType type) {
        YamlConfiguration cfg = playerData.getCached(uuid);
        if (cfg == null) return 0;
        return cfg.getLong("skills." + type.name() + ".exp", 0);
    }

    public void setLevel(UUID uuid, CraftingType type, int level) {
        YamlConfiguration cfg = playerData.getCached(uuid);
        if (cfg == null) return;
        cfg.set("skills." + type.name() + ".level", level);
        try { playerData.save(uuid); } catch (IOException ignored) {}
    }

    public void setExp(UUID uuid, CraftingType type, long exp) {
        YamlConfiguration cfg = playerData.getCached(uuid);
        if (cfg == null) return;
        cfg.set("skills." + type.name() + ".exp", exp);
        try { playerData.save(uuid); } catch (IOException ignored) {}
    }
}