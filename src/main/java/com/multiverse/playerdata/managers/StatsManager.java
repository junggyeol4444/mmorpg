package com.multiverse.playerdata.managers;

import com.multiverse.playerdata.PlayerDataCore;
import com.multiverse.playerdata.models.PlayerStats;
import com.multiverse.playerdata.models.StatValue;
import com.multiverse.playerdata.models.enums.StatType;
import com.multiverse.playerdata.data.DataManager;
import com.multiverse.playerdata.utils.ConfigUtil;
import org.bukkit.entity.Player;

import java.util.*;

public class StatsManager {

    private final PlayerDataCore plugin;
    private final DataManager dataManager;
    private final ConfigUtil configUtil;

    public StatsManager(PlayerDataCore plugin, DataManager dataManager, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.configUtil = configUtil;
    }

    // ===== 스탯 조회 =====
    public int getStat(Player player, StatType type) {
        PlayerStats stats = loadStats(player);
        return stats.getTotalStat(type);
    }

    public int getBaseStat(Player player, StatType type) {
        PlayerStats stats = loadStats(player);
        return stats.getBaseStat(type);
    }

    public int getTotalStat(Player player, StatType type) {
        PlayerStats stats = loadStats(player);
        return stats.getTotalStat(type);
    }

    public double getSpecialStat(Player player, String statName) {
        PlayerStats stats = loadStats(player);
        StatValue sv = stats.getSpecialStat(statName);
        return sv != null ? sv.getCurrent() : 0.0;
    }

    public Map<StatType, Integer> getBaseStats(Player player) {
        PlayerStats stats = loadStats(player);
        return new EnumMap<>(stats.getBaseStats());
    }

    // ===== 스탯 설정 =====
    public void setBaseStat(Player player, StatType type, int value) {
        PlayerStats stats = loadStats(player);
        stats.setBaseStat(type, value);
        saveStats(player, stats);
        applyStatEffects(player);
    }

    public void addEquipBonus(Player player, StatType type, int value) {
        PlayerStats stats = loadStats(player);
        stats.addEquipBonus(type, value);
        saveStats(player, stats);
        applyStatEffects(player);
    }

    public void addBuffBonus(Player player, StatType type, int value) {
        PlayerStats stats = loadStats(player);
        stats.addBuffBonus(type, value);
        saveStats(player, stats);
        applyStatEffects(player);
    }

    public int getAvailablePoints(Player player) {
        PlayerStats stats = loadStats(player);
        return stats.getAvailablePoints();
    }

    public void addAvailablePoints(Player player, int points) {
        PlayerStats stats = loadStats(player);
        stats.addAvailablePoints(points);
        saveStats(player, stats);
    }

    public void addStatPoint(Player player, StatType type, int points) {
        PlayerStats stats = loadStats(player);
        stats.addStatPoint(type, points);
        saveStats(player, stats);
        applyStatEffects(player);
    }

    public void resetStats(Player player, boolean refund) {
        PlayerStats stats = loadStats(player);
        stats.resetStats(refund);
        saveStats(player, stats);
        applyStatEffects(player);
    }

    // ===== 스탯 효과 적용 =====
    public void applyStatEffects(Player player) {
        PlayerStats stats = loadStats(player);
        // HP/마나/회피 등 실제 상태 적용
        updateMaxHealth(player);
        updateMaxMana(player);
        // 기타 버프/효과
    }

    public void applyRaceStatBonus(Player player, Map<StatType, Integer> bonus) {
        PlayerStats stats = loadStats(player);
        stats.applyRaceBonus(bonus);
        saveStats(player, stats);
        applyStatEffects(player);
    }

    public void removeRaceStatBonus(Player player, Map<StatType, Integer> bonus) {
        PlayerStats stats = loadStats(player);
        stats.removeRaceBonus(bonus);
        saveStats(player, stats);
        applyStatEffects(player);
    }

    public void updateMaxHealth(Player player) {
        // 최대 HP를 스탯/포뮬라 기반으로 계산하여 적용
        PlayerStats stats = loadStats(player);
        int vit = stats.getTotalStat(StatType.VIT);
        int maxHealth = configUtil.getInt("formulas.health.base", 100) + (vit * configUtil.getInt("formulas.health.per-vit", 5));
        player.setMaxHealth(maxHealth);
    }

    public void updateMaxMana(Player player) {
        // 최대 마나 적용 (커스텀 플러그인/시스템 필요)
        PlayerStats stats = loadStats(player);
        int intel = stats.getTotalStat(StatType.INT);
        double maxMana = configUtil.getInt("formulas.mana.base", 100) + (intel * configUtil.getInt("formulas.mana.per-int", 2));
        // 커스텀 마나 시스템 등과 연동 필요
    }

    // ===== 레벨 =====
    public int getLevel(Player player) {
        PlayerStats stats = loadStats(player);
        return stats.getLevel();
    }

    public void setLevel(Player player, int level) {
        PlayerStats stats = loadStats(player);
        stats.setLevel(level);
        saveStats(player, stats);
    }

    public void onLevelUp(Player player, int level) {
        setLevel(player, level);
        int statPoints = configUtil.getInt("leveling.rewards.stat-points", 5);
        addAvailablePoints(player, statPoints);
        applyStatEffects(player);
        // PlayerLevelUpEvent 호출
    }

    // ===== 데이터 관리 =====
    private PlayerStats loadStats(Player player) {
        return dataManager.loadPlayerStats(player.getUniqueId());
    }

    private void saveStats(Player player, PlayerStats stats) {
        dataManager.savePlayerStats(player.getUniqueId(), stats);
    }
}