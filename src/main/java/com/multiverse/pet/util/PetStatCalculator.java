package com.multiverse.   pet.util;

import com.multiverse.pet.model.   Pet;
import com. multiverse.pet. model.   PetRarity;
import com. multiverse.pet. model.   PetSpecies;
import com.multiverse.pet.model.   equipment.PetEquipSlot;
import com.multiverse.pet.model.   equipment.PetEquipmentData;

import java.util.   HashMap;
import java. util.   Map;

/**
 * 펫 스탯 계산기
 * 펫의 최종 스탯 계산
 */
public class PetStatCalculator {

    // 희귀도별 스탯 배율
    private static final Map<PetRarity, Double> RARITY_MULTIPLIERS = new HashMap<>();

    static {
        RARITY_MULTIPLIERS.put(PetRarity.COMMON, 1.0);
        RARITY_MULTIPLIERS.put(PetRarity.UNCOMMON, 1.1);
        RARITY_MULTIPLIERS.put(PetRarity.RARE, 1.25);
        RARITY_MULTIPLIERS.put(PetRarity.EPIC, 1.45);
        RARITY_MULTIPLIERS.put(PetRarity.LEGENDARY, 1.7);
        RARITY_MULTIPLIERS.put(PetRarity.MYTHIC, 2.0);
    }

    /**
     * 펫의 모든 스탯 계산
     */
    public static Map<String, Double> calculateAllStats(Pet pet, PetSpecies species) {
        Map<String, Double> finalStats = new HashMap<>();

        // 1. 기본 스탯 (레벨 반영)
        Map<String, Double> baseStats = calculateBaseStats(pet, species);

        // 2. 희귀도 배율 적용
        double rarityMultiplier = getRarityMultiplier(pet.getRarity());

        for (Map.Entry<String, Double> entry : baseStats.entrySet()) {
            finalStats.put(entry.getKey(), entry.getValue() * rarityMultiplier);
        }

        // 3. 보너스 스탯 추가
        Map<String, Double> bonusStats = pet.getBonusStats();
        for (Map.Entry<String, Double> entry : bonusStats. entrySet()) {
            finalStats. merge(entry.getKey(), entry.getValue(), Double::sum);
        }

        // 4. 장비 스탯 추가
        Map<String, Double> equipmentStats = calculateEquipmentStats(pet);
        for (Map.Entry<String, Double> entry : equipmentStats.entrySet()) {
            finalStats.merge(entry.getKey(), entry.getValue(), Double::sum);
        }

        // 5. 진화 보너스
        double evolutionBonus = getEvolutionBonus(pet. getEvolutionStage());
        for (String key : finalStats.keySet()) {
            finalStats.compute(key, (k, v) -> v * evolutionBonus);
        }

        // 6. 변이 보너스
        if (pet.isMutation()) {
            double mutationBonus = 1.15;
            for (String key : finalStats. keySet()) {
                finalStats.compute(key, (k, v) -> v * mutationBonus);
            }
        }

        // 7. 컨디션 페널티/보너스
        applyConditionModifiers(pet, finalStats);

        return finalStats;
    }

    /**
     * 기본 스탯 계산 (레벨 반영)
     */
    public static Map<String, Double> calculateBaseStats(Pet pet, PetSpecies species) {
        Map<String, Double> stats = new HashMap<>();

        if (species == null) {
            return pet.getBaseStats();
        }

        Map<String, Double> baseStats = species.getBaseStats();
        Map<String, Double> statsPerLevel = species.getStatsPerLevel();
        int level = pet.getLevel();

        for (Map.Entry<String, Double> entry : baseStats.entrySet()) {
            String statName = entry. getKey();
            double baseValue = entry.getValue();
            double perLevel = statsPerLevel. getOrDefault(statName, 0.0);

            double finalValue = baseValue + (perLevel * (level - 1));
            stats.put(statName, finalValue);
        }

        return stats;
    }

    /**
     * 장비 스탯 계산
     */
    public static Map<String, Double> calculateEquipmentStats(Pet pet) {
        Map<String, Double> stats = new HashMap<>();

        Map<PetEquipSlot, PetEquipmentData> equipment = pet.getEquipment();
        if (equipment == null) {
            return stats;
        }

        for (PetEquipmentData data : equipment. values()) {
            if (data != null && data.getStatBonuses() != null) {
                for (Map.Entry<String, Double> entry : data.getStatBonuses().entrySet()) {
                    stats.merge(entry.getKey(), entry.getValue(), Double::sum);
                }
            }
        }

        return stats;
    }

    /**
     * 희귀도 배율 가져오기
     */
    public static double getRarityMultiplier(PetRarity rarity) {
        return RARITY_MULTIPLIERS.getOrDefault(rarity, 1.0);
    }

    /**
     * 진화 보너스 가져오기
     */
    public static double getEvolutionBonus(int evolutionStage) {
        return 1.0 + (evolutionStage - 1) * 0.1; // 단계당 10% 증가
    }

    /**
     * 컨디션 수정자 적용
     */
    private static void applyConditionModifiers(Pet pet, Map<String, Double> stats) {
        double happiness = pet.getHappiness();
        double hunger = pet.getHunger();

        // 행복도 보너스/페널티
        double happinessModifier = 1.0;
        if (happiness >= 80) {
            happinessModifier = 1.1; // 10% 보너스
        } else if (happiness < 30) {
            happinessModifier = 0.9; // 10% 페널티
        }

        // 배고픔 페널티
        double hungerModifier = 1.0;
        if (hunger < 30) {
            hungerModifier = 0.85; // 15% 페널티
        } else if (hunger <= 0) {
            hungerModifier = 0.7; // 30% 페널티
        }

        double totalModifier = happinessModifier * hungerModifier;

        for (String key : stats.keySet()) {
            stats.compute(key, (k, v) -> v * totalModifier);
        }
    }

    /**
     * 특정 스탯 계산
     */
    public static double calculateStat(Pet pet, PetSpecies species, String statName) {
        Map<String, Double> allStats = calculateAllStats(pet, species);
        return allStats.getOrDefault(statName, 0.0);
    }

    /**
     * 전투력 계산
     */
    public static double calculateCombatPower(Pet pet, PetSpecies species) {
        Map<String, Double> stats = calculateAllStats(pet, species);

        double attack = stats.getOrDefault("attack", 0.0);
        double defense = stats.getOrDefault("defense", 0.0);
        double health = stats.getOrDefault("health", 0.0);
        double speed = stats.getOrDefault("speed", 0.0);
        double critChance = stats.getOrDefault("critical_chance", 0.0);
        double critDamage = stats. getOrDefault("critical_damage", 0.0);

        // 전투력 공식
        double combatPower = 0;
        combatPower += attack * 1.5;
        combatPower += defense * 1.2;
        combatPower += health * 0.5;
        combatPower += speed * 0.8;
        combatPower += critChance * 2;
        combatPower += critDamage * 1;

        // 레벨 보너스
        combatPower += pet.getLevel() * 10;

        // 희귀도 보너스
        combatPower *= getRarityMultiplier(pet.getRarity());

        return Math.round(combatPower);
    }

    /**
     * 스탯 비교
     */
    public static Map<String, Double> compareStats(Pet pet1, Pet pet2, 
                                                    PetSpecies species1, PetSpecies species2) {
        Map<String, Double> stats1 = calculateAllStats(pet1, species1);
        Map<String, Double> stats2 = calculateAllStats(pet2, species2);
        Map<String, Double> diff = new HashMap<>();

        for (String key : stats1.keySet()) {
            double val1 = stats1.getOrDefault(key, 0.0);
            double val2 = stats2.getOrDefault(key, 0.0);
            diff.put(key, val1 - val2);
        }

        return diff;
    }

    /**
     * 레벨업 시 스탯 증가량 미리보기
     */
    public static Map<String, Double> previewLevelUpStats(Pet pet, PetSpecies species, int levels) {
        Map<String, Double> currentStats = calculateAllStats(pet, species);

        int originalLevel = pet.getLevel();
        pet.setLevel(originalLevel + levels);
        Map<String, Double> newStats = calculateAllStats(pet, species);
        pet.setLevel(originalLevel);

        Map<String, Double> diff = new HashMap<>();
        for (String key : currentStats.keySet()) {
            double current = currentStats.getOrDefault(key, 0.0);
            double newVal = newStats.getOrDefault(key, 0.0);
            diff.put(key, newVal - current);
        }

        return diff;
    }

    /**
     * 스탯 요약 문자열
     */
    public static String getStatsSummary(Pet pet, PetSpecies species) {
        Map<String, Double> stats = calculateAllStats(pet, species);

        StringBuilder sb = new StringBuilder();
        sb.append("§c⚔ 공격력: §f").append(String.format("%.1f", stats.getOrDefault("attack", 0.0))).append("\n");
        sb.append("§9🛡 방어력: §f").append(String.format("%.1f", stats.getOrDefault("defense", 0.0))).append("\n");
        sb.append("§a❤ 체력: §f").append(String.format("%.1f", stats.getOrDefault("health", 0.0))).append("\n");
        sb.append("§b💨 속도: §f").append(String.format("%.1f", stats.getOrDefault("speed", 0.0))).append("\n");
        sb.append("§e⚡ 전투력: §f").append(String.format("%.0f", calculateCombatPower(pet, species)));

        return sb. toString();
    }
}