package com.multiverse.pet.     util;

import com.multiverse.pet.model.     Pet;
import com.multiverse.pet.model.     PetRarity;
import com. multiverse.pet. model.     breeding.PetGenetics;

import java.util.      ArrayList;
import java.util.     HashMap;
import java.util.     List;
import java. util.     Map;
import java.util.     Random;

/**
 * 유전 계산기
 * 교배 시 자손의 유전 특성 계산
 */
public class GeneticsCalculator {

    private static final Random random = new Random();

    // 유전 확률 상수
    private static final double STAT_INHERITANCE_WEIGHT = 0.4;  // 부모 스탯 유전 비중
    private static final double RANDOM_VARIANCE = 0.15;          // 랜덤 변동
    private static final double MUTATION_STAT_BONUS = 0.2;       // 변이 시 스탯 보너스
    private static final double HYBRID_VIGOR_BONUS = 0.05;       // 잡종 강세 보너스

    /**
     * 자손 스탯 계산
     */
    public static Map<String, Double> calculateOffspringStats(Pet parent1, Pet parent2) {
        Map<String, Double> offspringStats = new HashMap<>();

        Map<String, Double> stats1 = parent1.getBaseStats();
        Map<String, Double> stats2 = parent2.getBaseStats();

        // 모든 스탯 키 수집
        List<String> allStats = new ArrayList<>();
        allStats.addAll(stats1.keySet());
        for (String key : stats2.keySet()) {
            if (!allStats.contains(key)) {
                allStats.add(key);
            }
        }

        // 각 스탯 계산
        for (String statKey : allStats) {
            double val1 = stats1.getOrDefault(statKey, 0.0);
            double val2 = stats2.getOrDefault(statKey, 0.0);

            double inheritedStat = calculateInheritedStat(val1, val2);
            offspringStats.put(statKey, inheritedStat);
        }

        // 잡종 강세 (다른 종족일 경우)
        if (! parent1.getSpeciesId().equals(parent2.getSpeciesId())) {
            applyHybridVigor(offspringStats);
        }

        return offspringStats;
    }

    /**
     * 개별 스탯 유전 계산
     */
    private static double calculateInheritedStat(double stat1, double stat2) {
        // 기본:  부모 평균
        double average = (stat1 + stat2) / 2;

        // 우성 유전 확률 (높은 스탯 쪽으로 치우침)
        double higher = Math.max(stat1, stat2);
        double lower = Math.min(stat1, stat2);

        // 70% 확률로 평균, 30% 확률로 우성 쪽
        double baseStat;
        if (random.nextDouble() < 0.7) {
            baseStat = average;
        } else {
            baseStat = average + (higher - average) * 0.5;
        }

        // 랜덤 변동 적용
        double variance = (random.nextDouble() * 2 - 1) * RANDOM_VARIANCE;
        baseStat *= (1 + variance);

        // 최소값 보장
        return Math.max(1, baseStat);
    }

    /**
     * 잡종 강세 적용
     */
    private static void applyHybridVigor(Map<String, Double> stats) {
        for (String key : stats.keySet()) {
            stats.compute(key, (k, v) -> v * (1 + HYBRID_VIGOR_BONUS));
        }
    }

    /**
     * 자손 희귀도 결정
     */
    public static PetRarity calculateOffspringRarity(Pet parent1, Pet parent2) {
        PetRarity rarity1 = parent1.getRarity();
        PetRarity rarity2 = parent2.getRarity();

        int ordinal1 = rarity1.ordinal();
        int ordinal2 = rarity2.ordinal();

        // 기본:  부모 중 낮은 희귀도
        int baseOrdinal = Math. min(ordinal1, ordinal2);

        // 희귀도 상승 확률
        double upgradeChance = calculateRarityUpgradeChance(rarity1, rarity2);

        if (random.nextDouble() * 100 < upgradeChance) {
            baseOrdinal = Math.min(baseOrdinal + 1, PetRarity.values().length - 1);
        }

        // 희귀도 하락 확률 (매우 낮음)
        if (random.nextDouble() < 0.05 && baseOrdinal > 0) {
            baseOrdinal--;
        }

        return PetRarity. values()[baseOrdinal];
    }

    /**
     * 희귀도 상승 확률 계산
     */
    private static double calculateRarityUpgradeChance(PetRarity rarity1, PetRarity rarity2) {
        int ordinal1 = rarity1.ordinal();
        int ordinal2 = rarity2.ordinal();
        int higherOrdinal = Math.max(ordinal1, ordinal2);

        // 기본 상승 확률
        double baseChance = 10.0;

        // 두 부모가 같은 희귀도면 상승 확률 증가
        if (ordinal1 == ordinal2) {
            baseChance += 15.0;
        }

        // 높은 희귀도일수록 상승 어려움
        baseChance -= higherOrdinal * 5;

        return Math.max(1, Math.min(baseChance, 50));
    }

    /**
     * 변이 확률 계산
     */
    public static double calculateMutationChance(Pet parent1, Pet parent2) {
        double baseChance = 5.0; // 기본 5%

        // 부모가 다른 종족이면 변이 확률 증가
        if (!parent1.getSpeciesId().equals(parent2.getSpeciesId())) {
            baseChance += 3.0;
        }

        // 부모 중 하나가 변이면 확률 증가
        if (parent1.isMutation() || parent2.isMutation()) {
            baseChance += 5.0;
        }

        // 두 부모 모두 변이면 더 증가
        if (parent1.isMutation() && parent2.isMutation()) {
            baseChance += 10.0;
        }

        // 희귀도 높을수록 변이 확률 증가
        int avgRarity = (parent1.getRarity().ordinal() + parent2.getRarity().ordinal()) / 2;
        baseChance += avgRarity * 1.5;

        return Math.min(baseChance, 30); // 최대 30%
    }

    /**
     * 변이 적용
     */
    public static void applyMutation(Map<String, Double> stats) {
        // 모든 스탯에 보너스
        for (String key : stats.keySet()) {
            stats.compute(key, (k, v) -> v * (1 + MUTATION_STAT_BONUS));
        }

        // 랜덤 스탯 하나에 추가 보너스
        List<String> keys = new ArrayList<>(stats.keySet());
        if (! keys.isEmpty()) {
            String bonusStat = keys.get(random.nextInt(keys.size()));
            stats.compute(bonusStat, (k, v) -> v * 1.15);
        }
    }

    /**
     * 자손 종족 결정
     */
    public static String determineOffspringSpecies(Pet parent1, Pet parent2, 
                                                    List<String> possibleSpecies) {
        if (possibleSpecies == null || possibleSpecies.isEmpty()) {
            // 기본:  부모 중 하나
            return random.nextBoolean() ? parent1.getSpeciesId() : parent2.getSpeciesId();
        }

        // 부모 종족 우선
        List<String> weightedList = new ArrayList<>();

        String species1 = parent1.getSpeciesId();
        String species2 = parent2.getSpeciesId();

        // 부모 종족에 가중치 부여
        for (int i = 0; i < 3; i++) {
            if (possibleSpecies.contains(species1)) {
                weightedList.add(species1);
            }
            if (possibleSpecies.contains(species2)) {
                weightedList.add(species2);
            }
        }

        // 다른 가능한 종족 추가
        weightedList.addAll(possibleSpecies);

        return weightedList.get(random.nextInt(weightedList. size()));
    }

    /**
     * 스킬 유전 결정
     */
    public static List<String> determineInheritedSkills(Pet parent1, Pet parent2, int maxSkills) {
        List<String> inheritedSkills = new ArrayList<>();

        List<String> skills1 = new ArrayList<>();
        List<String> skills2 = new ArrayList<>();

        for (var skill : parent1.getSkills()) {
            skills1.add(skill.getSkillId());
        }
        for (var skill : parent2.getSkills()) {
            skills2.add(skill.getSkillId());
        }

        // 공통 스킬 우선 유전 (80%)
        for (String skill : skills1) {
            if (skills2.contains(skill) && inheritedSkills.size() < maxSkills) {
                if (random.nextDouble() < 0.8) {
                    inheritedSkills.add(skill);
                }
            }
        }

        // 나머지 스킬 랜덤 유전 (40%)
        List<String> remainingSkills = new ArrayList<>();
        remainingSkills.addAll(skills1);
        remainingSkills.addAll(skills2);
        remainingSkills.removeAll(inheritedSkills);

        for (String skill : remainingSkills) {
            if (inheritedSkills.size() >= maxSkills) break;
            if (random.nextDouble() < 0.4) {
                inheritedSkills.add(skill);
            }
        }

        return inheritedSkills;
    }

    /**
     * 유전 결과 생성
     */
    public static PetGenetics generateOffspringGenetics(Pet parent1, Pet parent2) {
        PetGenetics genetics = new PetGenetics();

        // 스탯 유전
        genetics. setInheritedStats(calculateOffspringStats(parent1, parent2));

        // 희귀도
        genetics.setRarity(calculateOffspringRarity(parent1, parent2));

        // 변이
        double mutationChance = calculateMutationChance(parent1, parent2);
        boolean isMutation = random.nextDouble() * 100 < mutationChance;
        genetics.setMutation(isMutation);

        if (isMutation) {
            applyMutation(genetics.getInheritedStats());
        }

        // 부모 정보 저장
        genetics. setParent1Id(parent1.getPetId());
        genetics.setParent2Id(parent2.getPetId());
        genetics.setParent1Species(parent1.getSpeciesId());
        genetics.setParent2Species(parent2.getSpeciesId());

        return genetics;
    }

    /**
     * 유전 품질 점수 계산
     */
    public static double calculateGeneticsQuality(PetGenetics genetics) {
        double quality = 0;

        // 스탯 품질
        Map<String, Double> stats = genetics.getInheritedStats();
        double totalStats = stats.values().stream().mapToDouble(Double::doubleValue).sum();
        quality += totalStats / 10;

        // 희귀도 보너스
        quality += genetics.getRarity().ordinal() * 10;

        // 변이 보너스
        if (genetics.isMutation()) {
            quality += 20;
        }

        return quality;
    }
}