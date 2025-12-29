package com.multiverse. pet.             config;

import org.bukkit.             configuration.file.FileConfiguration;

import java.util.              List;

/**
 * 스킬 설정
 * 펫 스킬 관련 설정
 */
public class SkillSettings {

    private final FileConfiguration config;
    private final String basePath = "skill";

    // 기본 설정
    private int maxSkillLevel;
    private int maxActiveSkills;
    private int maxPassiveSkills;
    private boolean allowSkillReset;
    private double skillResetCost;

    // 스킬 슬롯
    private int baseSkillSlots;
    private int skillSlotsPerRarity;
    private int skillSlotsPerEvolution;

    // 쿨다운
    private double globalCooldownReduction;
    private boolean showCooldownMessage;
    private boolean showCooldownActionBar;

    // 스킬 포인트
    private int baseUpgradeCost;
    private double upgradeCostMultiplier;
    private boolean refundOnReset;
    private double refundPercent;

    // 자동 스킬
    private boolean autoSkillEnabled;
    private int autoSkillInterval;
    private List<String> autoSkillPriority;

    // 스킬 효과
    private boolean skillParticles;
    private boolean skillSounds;
    private double skillDamageMultiplier;
    private double skillHealMultiplier;

    public SkillSettings(FileConfiguration config) {
        this.config = config;
        load();
    }

    /**
     * 설정 로드
     */
    private void load() {
        // 기본 설정
        maxSkillLevel = config. getInt(basePath + ".max-skill-level", 10);
        maxActiveSkills = config. getInt(basePath + ".max-active-skills", 4);
        maxPassiveSkills = config.getInt(basePath + ".max-passive-skills", 4);
        allowSkillReset = config.getBoolean(basePath + ".allow-skill-reset", true);
        skillResetCost = config.getDouble(basePath + ".skill-reset-cost", 1000);

        // 스킬 슬롯
        baseSkillSlots = config.getInt(basePath + ". slots.base", 2);
        skillSlotsPerRarity = config.getInt(basePath + ". slots.per-rarity", 1);
        skillSlotsPerEvolution = config.getInt(basePath + ". slots.per-evolution", 1);

        // 쿨다운
        globalCooldownReduction = config.getDouble(basePath + ".cooldown.global-reduction", 0);
        showCooldownMessage = config. getBoolean(basePath + ".cooldown.show-message", true);
        showCooldownActionBar = config.getBoolean(basePath + ".cooldown. show-actionbar", true);

        // 스킬 포인트
        baseUpgradeCost = config.getInt(basePath + ".upgrade. base-cost", 1);
        upgradeCostMultiplier = config.getDouble(basePath + ".upgrade.cost-multiplier", 1.5);
        refundOnReset = config.getBoolean(basePath + ".upgrade.refund-on-reset", true);
        refundPercent = config.getDouble(basePath + ".upgrade.refund-percent", 80);

        // 자동 스킬
        autoSkillEnabled = config.getBoolean(basePath + ".auto-skill.enabled", false);
        autoSkillInterval = config.getInt(basePath + ".auto-skill. interval", 5);
        autoSkillPriority = config.getStringList(basePath + ".auto-skill.priority");

        // 스킬 효과
        skillParticles = config.getBoolean(basePath + ".effects.particles", true);
        skillSounds = config.getBoolean(basePath + ".effects.sounds", true);
        skillDamageMultiplier = config.getDouble(basePath + ".effects.damage-multiplier", 1.0);
        skillHealMultiplier = config.getDouble(basePath + ".effects.heal-multiplier", 1.0);
    }

    /**
     * 리로드
     */
    public void reload() {
        load();
    }

    // ===== Getter =====

    public int getMaxSkillLevel() {
        return maxSkillLevel;
    }

    public int getMaxActiveSkills() {
        return maxActiveSkills;
    }

    public int getMaxPassiveSkills() {
        return maxPassiveSkills;
    }

    public boolean isAllowSkillReset() {
        return allowSkillReset;
    }

    public double getSkillResetCost() {
        return skillResetCost;
    }

    public int getBaseSkillSlots() {
        return baseSkillSlots;
    }

    public int getSkillSlotsPerRarity() {
        return skillSlotsPerRarity;
    }

    public int getSkillSlotsPerEvolution() {
        return skillSlotsPerEvolution;
    }

    public double getGlobalCooldownReduction() {
        return globalCooldownReduction;
    }

    public boolean isShowCooldownMessage() {
        return showCooldownMessage;
    }

    public boolean isShowCooldownActionBar() {
        return showCooldownActionBar;
    }

    public int getBaseUpgradeCost() {
        return baseUpgradeCost;
    }

    public double getUpgradeCostMultiplier() {
        return upgradeCostMultiplier;
    }

    public boolean isRefundOnReset() {
        return refundOnReset;
    }

    public double getRefundPercent() {
        return refundPercent;
    }

    public boolean isAutoSkillEnabled() {
        return autoSkillEnabled;
    }

    public int getAutoSkillInterval() {
        return autoSkillInterval;
    }

    public List<String> getAutoSkillPriority() {
        return autoSkillPriority;
    }

    public boolean isSkillParticles() {
        return skillParticles;
    }

    public boolean isSkillSounds() {
        return skillSounds;
    }

    public double getSkillDamageMultiplier() {
        return skillDamageMultiplier;
    }

    public double getSkillHealMultiplier() {
        return skillHealMultiplier;
    }

    // ===== 계산 메서드 =====

    /**
     * 희귀도별 스킬 슬롯 수
     */
    public int getSkillSlotsForRarity(int rarityOrdinal) {
        return baseSkillSlots + (rarityOrdinal * skillSlotsPerRarity);
    }

    /**
     * 진화 단계별 스킬 슬롯 수
     */
    public int getSkillSlotsForEvolution(int evolutionStage) {
        return (evolutionStage - 1) * skillSlotsPerEvolution;
    }

    /**
     * 총 스킬 슬롯 수
     */
    public int getTotalSkillSlots(int rarityOrdinal, int evolutionStage) {
        return getSkillSlotsForRarity(rarityOrdinal) + getSkillSlotsForEvolution(evolutionStage);
    }

    /**
     * 스킬 강화 비용 계산
     */
    public int getUpgradeCost(int currentLevel) {
        return (int) (baseUpgradeCost * Math.pow(upgradeCostMultiplier, currentLevel - 1));
    }

    /**
     * 총 강화 비용 계산 (특정 레벨까지)
     */
    public int getTotalUpgradeCost(int fromLevel, int toLevel) {
        int total = 0;
        for (int i = fromLevel; i < toLevel; i++) {
            total += getUpgradeCost(i);
        }
        return total;
    }

    /**
     * 스킬 초기화 시 환불 포인트
     */
    public int getRefundPoints(int totalSpentPoints) {
        if (! refundOnReset) return 0;
        return (int) (totalSpentPoints * refundPercent / 100);
    }

    /**
     * 실제 쿨다운 계산
     */
    public int getActualCooldown(int baseCooldown) {
        return (int) (baseCooldown * (1 - globalCooldownReduction / 100));
    }
}