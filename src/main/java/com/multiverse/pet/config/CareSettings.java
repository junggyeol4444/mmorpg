package com.multiverse.pet.               config;

import org.bukkit.               configuration.file.FileConfiguration;

import java.util.                HashMap;
import java. util.                Map;

/**
 * 케어 설정
 * 펫 케어 (배고픔, 행복도, 치료) 관련 설정
 */
public class CareSettings {

    private final FileConfiguration config;
    private final String basePath = "care";

    // 배고픔
    private double hungerDecreaseRate;
    private double activeHungerMultiplier;
    private double starvationDamage;
    private boolean starvationEnabled;

    // 행복도
    private double happinessDecreaseRate;
    private double activeHappinessMultiplier;
    private double unhappyStatPenalty;
    private boolean runawayEnabled;
    private double runawayChance;

    // 체력 재생
    private double healthRegenRate;
    private double outOfCombatRegenMultiplier;
    private double lowHealthRegenMultiplier;
    private double happinessRegenBonus;
    private int combatRegenCooldown;

    // 치료
    private double healCostPerHP;
    private double reviveCost;
    private int reviveCooldown;
    private double reviveHealthPercent;

    // 음식
    private Map<String, Double> foodHungerValues;
    private Map<String, Double> foodHealthValues;
    private Map<String, Double> foodHappinessValues;

    // 장난감
    private Map<String, Double> toyHappinessValues;
    private Map<String, Integer> toyDurabilities;

    public CareSettings(FileConfiguration config) {
        this.config = config;
        this.foodHungerValues = new HashMap<>();
        this.foodHealthValues = new HashMap<>();
        this.foodHappinessValues = new HashMap<>();
        this.toyHappinessValues = new HashMap<>();
        this.toyDurabilities = new HashMap<>();
        load();
    }

    /**
     * 설정 로드
     */
    private void load() {
        // 배고픔
        hungerDecreaseRate = config.getDouble(basePath + ". hunger. decrease-rate", 1.0);
        activeHungerMultiplier = config. getDouble(basePath + ".hunger.active-multiplier", 2.0);
        starvationDamage = config.getDouble(basePath + ".hunger.starvation-damage", 1.0);
        starvationEnabled = config.getBoolean(basePath + ". hunger.starvation-enabled", true);

        // 행복도
        happinessDecreaseRate = config. getDouble(basePath + ".happiness.decrease-rate", 0.5);
        activeHappinessMultiplier = config.getDouble(basePath + ".happiness.active-multiplier", 0.5);
        unhappyStatPenalty = config.  getDouble(basePath + ".happiness.unhappy-stat-penalty", 20.0);
        runawayEnabled = config.getBoolean(basePath + ". happiness.runaway-enabled", false);
        runawayChance = config.  getDouble(basePath + ".happiness.runaway-chance", 1.0);

        // 체력 재생
        healthRegenRate = config.getDouble(basePath + ".health-regen.rate", 1.0);
        outOfCombatRegenMultiplier = config.getDouble(basePath + ".health-regen.out-of-combat-multiplier", 2.0);
        lowHealthRegenMultiplier = config. getDouble(basePath + ".health-regen.low-health-multiplier", 1.5);
        happinessRegenBonus = config.getDouble(basePath + ". health-regen. happiness-bonus", 0.5);
        combatRegenCooldown = config.getInt(basePath + ". health-regen. combat-cooldown", 10);

        // 치료
        healCostPerHP = config.getDouble(basePath + ".healing.cost-per-hp", 2.0);
        reviveCost = config.getDouble(basePath + ".healing.revive-cost", 500.0);
        reviveCooldown = config.getInt(basePath + ". healing.revive-cooldown", 300);
        reviveHealthPercent = config.getDouble(basePath + ".healing. revive-health-percent", 50.0);

        // 음식 로드
        loadFoodValues();

        // 장난감 로드
        loadToyValues();
    }

    /**
     * 음식 값 로드
     */
    private void loadFoodValues() {
        foodHungerValues. clear();
        foodHealthValues.clear();
        foodHappinessValues.clear();

        if (config.isConfigurationSection(basePath + ".foods")) {
            for (String food : config.getConfigurationSection(basePath + ".foods").getKeys(false)) {
                String path = basePath + ".foods." + food;
                foodHungerValues.put(food, config.getDouble(path + ".hunger", 20.0));
                foodHealthValues.put(food, config. getDouble(path + ".health", 0.0));
                foodHappinessValues.put(food, config.getDouble(path + ".happiness", 5.0));
            }
        }

        // 기본 음식 추가
        if (foodHungerValues. isEmpty()) {
            foodHungerValues. put("COOKED_BEEF", 30.0);
            foodHungerValues.put("COOKED_PORKCHOP", 25.0);
            foodHungerValues.put("COOKED_CHICKEN", 20.0);
            foodHungerValues.put("GOLDEN_APPLE", 50.0);
            foodHungerValues.put("GOLDEN_CARROT", 40.0);

            foodHealthValues. put("GOLDEN_APPLE", 20.0);
            foodHealthValues.put("GOLDEN_CARROT", 10.0);

            foodHappinessValues.put("GOLDEN_APPLE", 10.0);
            foodHappinessValues.put("CAKE", 30.0);
            foodHappinessValues.put("COOKIE", 15.0);
        }
    }

    /**
     * 장난감 값 로드
     */
    private void loadToyValues() {
        toyHappinessValues.clear();
        toyDurabilities.clear();

        if (config.isConfigurationSection(basePath + ". toys")) {
            for (String toy :  config.getConfigurationSection(basePath + ".toys").getKeys(false)) {
                String path = basePath + ".toys." + toy;
                toyHappinessValues.put(toy, config.getDouble(path + ". happiness", 10.0));
                toyDurabilities. put(toy, config.getInt(path + ". durability", 50));
            }
        }

        // 기본 장난감 추가
        if (toyHappinessValues.isEmpty()) {
            toyHappinessValues.put("STICK", 10.0);
            toyHappinessValues.put("BONE", 20.0);
            toyHappinessValues.put("FEATHER", 15.0);
            toyHappinessValues.put("STRING", 12.0);

            toyDurabilities.put("STICK", 100);
            toyDurabilities.put("BONE", 50);
            toyDurabilities.put("FEATHER", 30);
            toyDurabilities.put("STRING", 40);
        }
    }

    /**
     * 리로드
     */
    public void reload() {
        load();
    }

    // ===== Getter =====

    public double getHungerDecreaseRate() {
        return hungerDecreaseRate;
    }

    public double getActiveHungerMultiplier() {
        return activeHungerMultiplier;
    }

    public double getStarvationDamage() {
        return starvationDamage;
    }

    public boolean isStarvationEnabled() {
        return starvationEnabled;
    }

    public double getHappinessDecreaseRate() {
        return happinessDecreaseRate;
    }

    public double getActiveHappinessMultiplier() {
        return activeHappinessMultiplier;
    }

    public double getUnhappyStatPenalty() {
        return unhappyStatPenalty;
    }

    public boolean isRunawayEnabled() {
        return runawayEnabled;
    }

    public double getRunawayChance() {
        return runawayChance;
    }

    public double getHealthRegenRate() {
        return healthRegenRate;
    }

    public double getOutOfCombatRegenMultiplier() {
        return outOfCombatRegenMultiplier;
    }

    public double getLowHealthRegenMultiplier() {
        return lowHealthRegenMultiplier;
    }

    public double getHappinessRegenBonus() {
        return happinessRegenBonus;
    }

    public int getCombatRegenCooldown() {
        return combatRegenCooldown;
    }

    public double getHealCostPerHP() {
        return healCostPerHP;
    }

    public double getReviveCost() {
        return reviveCost;
    }

    public int getReviveCooldown() {
        return reviveCooldown;
    }

    public double getReviveHealthPercent() {
        return reviveHealthPercent;
    }

    // ===== 음식/장난감 =====

    public double getFoodHungerValue(String foodId) {
        return foodHungerValues.getOrDefault(foodId. toUpperCase(), 10.0);
    }

    public double getFoodHealthValue(String foodId) {
        return foodHealthValues.getOrDefault(foodId.toUpperCase(), 0.0);
    }

    public double getFoodHappinessValue(String foodId) {
        return foodHappinessValues.  getOrDefault(foodId.toUpperCase(), 5.0);
    }

    public double getToyHappinessValue(String toyId) {
        return toyHappinessValues.getOrDefault(toyId. toUpperCase(), 10.0);
    }

    public int getToyDurability(String toyId) {
        return toyDurabilities. getOrDefault(toyId.toUpperCase(), 50);
    }

    public boolean isValidFood(String foodId) {
        return foodHungerValues.  containsKey(foodId.toUpperCase());
    }

    public boolean isValidToy(String toyId) {
        return toyHappinessValues.containsKey(toyId.toUpperCase());
    }

    // ===== 계산 =====

    public double calculateHealCost(double hpToHeal) {
        return hpToHeal * healCostPerHP;
    }
}