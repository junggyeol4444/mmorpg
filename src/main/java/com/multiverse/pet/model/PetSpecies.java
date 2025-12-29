package com. multiverse.pet. model;

import com.multiverse.pet.model.skill.PetSkill;
import org.bukkit.entity.EntityType;

import java.util.*;

/**
 * 펫 종족 데이터 클래스
 * 펫의 종족 정보와 기본 스탯을 정의
 */
public class PetSpecies {

    // 기본 정보
    private String speciesId;
    private String name;
    private String description;
    private PetType type;
    private PetRarity baseRarity;

    // 외형
    private EntityType entityType;
    private String customModelId;
    private String iconMaterial;

    // 기본 스탯
    private Map<String, Double> baseStats;

    // 스탯 성장률 (레벨당)
    private Map<String, Double> statGrowth;

    // 기본 스킬 (레벨별 해금)
    private List<SkillUnlock> skillUnlocks;

    // 레벨 제한
    private int maxLevel;

    // 진화 정보
    private List<EvolutionInfo> evolutions;

    // 획득 정보
    private double dropChance;
    private List<String> dropSources;

    // 교배 정보
    private boolean canBreed;
    private List<String> compatibleSpecies;
    private int breedingCooldown; // 시간 단위

    // 먹이 정보
    private List<String> preferredFoods;
    private double hungerDecayRate;

    // 추가 설정
    private boolean tameable;
    private boolean tradeable;
    private boolean rideable;

    /**
     * 기본 생성자
     */
    public PetSpecies() {
        this.baseStats = new HashMap<>();
        this.statGrowth = new HashMap<>();
        this.skillUnlocks = new ArrayList<>();
        this.evolutions = new ArrayList<>();
        this.dropSources = new ArrayList<>();
        this.compatibleSpecies = new ArrayList<>();
        this.preferredFoods = new ArrayList<>();
        this.maxLevel = 50;
        this. dropChance = 1.0;
        this.canBreed = true;
        this. breedingCooldown = 24;
        this.hungerDecayRate = 1.0;
        this.tameable = true;
        this.tradeable = true;
        this. rideable = false;
    }

    /**
     * 전체 생성자
     */
    public PetSpecies(String speciesId, String name, PetType type, 
                      PetRarity baseRarity, EntityType entityType) {
        this();
        this.speciesId = speciesId;
        this.name = name;
        this.type = type;
        this.baseRarity = baseRarity;
        this.entityType = entityType;
    }

    /**
     * 특정 레벨에서의 스탯 계산
     *
     * @param statName 스탯 이름
     * @param level 레벨
     * @return 계산된 스탯 값
     */
    public double getStatAtLevel(String statName, int level) {
        double baseStat = baseStats.getOrDefault(statName, 0.0);
        double growth = statGrowth.getOrDefault(statName, 0.0);
        return baseStat + (growth * (level - 1));
    }

    /**
     * 특정 레벨에서 모든 스탯 계산
     *
     * @param level 레벨
     * @return 모든 스탯 맵
     */
    public Map<String, Double> getAllStatsAtLevel(int level) {
        Map<String, Double> stats = new HashMap<>();
        for (String statName : baseStats.keySet()) {
            stats. put(statName, getStatAtLevel(statName, level));
        }
        return stats;
    }

    /**
     * 특정 레벨에서 해금된 스킬 목록 반환
     *
     * @param level 레벨
     * @return 해금된 스킬 ID 목록
     */
    public List<String> getUnlockedSkillsAtLevel(int level) {
        List<String> unlockedSkills = new ArrayList<>();
        for (SkillUnlock unlock : skillUnlocks) {
            if (unlock.getUnlockLevel() <= level) {
                unlockedSkills.add(unlock. getSkillId());
            }
        }
        return unlockedSkills;
    }

    /**
     * 특정 레벨에서 새로 해금되는 스킬 반환
     *
     * @param level 레벨
     * @return 새로 해금되는 스킬 ID 또는 null
     */
    public String getNewSkillAtLevel(int level) {
        for (SkillUnlock unlock : skillUnlocks) {
            if (unlock. getUnlockLevel() == level) {
                return unlock.getSkillId();
            }
        }
        return null;
    }

    /**
     * 진화 가능한 목록 반환
     *
     * @param currentStage 현재 진화 단계
     * @param level 현재 레벨
     * @return 가능한 진화 정보 목록
     */
    public List<EvolutionInfo> getAvailableEvolutions(int currentStage, int level) {
        List<EvolutionInfo> available = new ArrayList<>();
        for (EvolutionInfo evo : evolutions) {
            if (evo.getFromStage() == currentStage && evo.getRequiredLevel() <= level) {
                available.add(evo);
            }
        }
        return available;
    }

    /**
     * 특정 음식이 선호 음식인지 확인
     *
     * @param foodId 음식 ID
     * @return 선호 음식 여부
     */
    public boolean isPreferredFood(String foodId) {
        return preferredFoods.contains(foodId);
    }

    /**
     * 특정 종족과 교배 가능한지 확인
     *
     * @param otherSpeciesId 다른 종족 ID
     * @return 교배 가능 여부
     */
    public boolean canBreedWith(String otherSpeciesId) {
        if (! canBreed) return false;
        if (compatibleSpecies.isEmpty()) {
            // 호환 종족이 비어있으면 같은 종족만 가능
            return this.speciesId.equals(otherSpeciesId);
        }
        return compatibleSpecies.contains(otherSpeciesId);
    }

    /**
     * 기본 스탯 설정
     */
    public void setBaseStat(String statName, double value) {
        baseStats.put(statName, value);
    }

    /**
     * 스탯 성장률 설정
     */
    public void setStatGrowth(String statName, double value) {
        statGrowth.put(statName, value);
    }

    /**
     * 스킬 해금 추가
     */
    public void addSkillUnlock(String skillId, int unlockLevel) {
        skillUnlocks.add(new SkillUnlock(skillId, unlockLevel));
    }

    /**
     * 진화 정보 추가
     */
    public void addEvolution(EvolutionInfo evolution) {
        evolutions.add(evolution);
    }

    /**
     * 드롭 소스 추가
     */
    public void addDropSource(String source) {
        dropSources.add(source);
    }

    /**
     * 호환 종족 추가
     */
    public void addCompatibleSpecies(String speciesId) {
        compatibleSpecies.add(speciesId);
    }

    /**
     * 선호 음식 추가
     */
    public void addPreferredFood(String foodId) {
        preferredFoods.add(foodId);
    }

    // ===== Getter/Setter =====

    public String getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(String speciesId) {
        this. speciesId = speciesId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this. description = description;
    }

    public PetType getType() {
        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public PetRarity getBaseRarity() {
        return baseRarity;
    }

    public void setBaseRarity(PetRarity baseRarity) {
        this.baseRarity = baseRarity;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getCustomModelId() {
        return customModelId;
    }

    public void setCustomModelId(String customModelId) {
        this. customModelId = customModelId;
    }

    public String getIconMaterial() {
        return iconMaterial;
    }

    public void setIconMaterial(String iconMaterial) {
        this. iconMaterial = iconMaterial;
    }

    public Map<String, Double> getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(Map<String, Double> baseStats) {
        this.baseStats = baseStats;
    }

    public Map<String, Double> getStatGrowth() {
        return statGrowth;
    }

    public void setStatGrowth(Map<String, Double> statGrowth) {
        this.statGrowth = statGrowth;
    }

    public List<SkillUnlock> getSkillUnlocks() {
        return skillUnlocks;
    }

    public void setSkillUnlocks(List<SkillUnlock> skillUnlocks) {
        this. skillUnlocks = skillUnlocks;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public List<EvolutionInfo> getEvolutions() {
        return evolutions;
    }

    public void setEvolutions(List<EvolutionInfo> evolutions) {
        this. evolutions = evolutions;
    }

    public double getDropChance() {
        return dropChance;
    }

    public void setDropChance(double dropChance) {
        this.dropChance = dropChance;
    }

    public List<String> getDropSources() {
        return dropSources;
    }

    public void setDropSources(List<String> dropSources) {
        this.dropSources = dropSources;
    }

    public boolean isCanBreed() {
        return canBreed;
    }

    public void setCanBreed(boolean canBreed) {
        this.canBreed = canBreed;
    }

    public List<String> getCompatibleSpecies() {
        return compatibleSpecies;
    }

    public void setCompatibleSpecies(List<String> compatibleSpecies) {
        this. compatibleSpecies = compatibleSpecies;
    }

    public int getBreedingCooldown() {
        return breedingCooldown;
    }

    public void setBreedingCooldown(int breedingCooldown) {
        this.breedingCooldown = breedingCooldown;
    }

    public List<String> getPreferredFoods() {
        return preferredFoods;
    }

    public void setPreferredFoods(List<String> preferredFoods) {
        this.preferredFoods = preferredFoods;
    }

    public double getHungerDecayRate() {
        return hungerDecayRate;
    }

    public void setHungerDecayRate(double hungerDecayRate) {
        this.hungerDecayRate = hungerDecayRate;
    }

    public boolean isTameable() {
        return tameable;
    }

    public void setTameable(boolean tameable) {
        this.tameable = tameable;
    }

    public boolean isTradeable() {
        return tradeable;
    }

    public void setTradeable(boolean tradeable) {
        this.tradeable = tradeable;
    }

    public boolean isRideable() {
        return rideable;
    }

    public void setRideable(boolean rideable) {
        this. rideable = rideable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetSpecies that = (PetSpecies) o;
        return Objects.equals(speciesId, that.speciesId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(speciesId);
    }

    @Override
    public String toString() {
        return "PetSpecies{" +
                "speciesId='" + speciesId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", baseRarity=" + baseRarity +
                '}';
    }

    // ===== 내부 클래스 =====

    /**
     * 스킬 해금 정보
     */
    public static class SkillUnlock {
        private String skillId;
        private int unlockLevel;

        public SkillUnlock() {}

        public SkillUnlock(String skillId, int unlockLevel) {
            this.skillId = skillId;
            this.unlockLevel = unlockLevel;
        }

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public int getUnlockLevel() {
            return unlockLevel;
        }

        public void setUnlockLevel(int unlockLevel) {
            this.unlockLevel = unlockLevel;
        }
    }

    /**
     * 진화 정보
     */
    public static class EvolutionInfo {
        private String evolutionId;
        private String targetSpeciesId;
        private int fromStage;
        private int toStage;
        private int requiredLevel;
        private List<String> requiredItems;
        private Map<String, Double> requiredStats;

        public EvolutionInfo() {
            this.requiredItems = new ArrayList<>();
            this.requiredStats = new HashMap<>();
        }

        public EvolutionInfo(String evolutionId, String targetSpeciesId, 
                            int fromStage, int toStage, int requiredLevel) {
            this();
            this.evolutionId = evolutionId;
            this.targetSpeciesId = targetSpeciesId;
            this. fromStage = fromStage;
            this.toStage = toStage;
            this.requiredLevel = requiredLevel;
        }

        public String getEvolutionId() {
            return evolutionId;
        }

        public void setEvolutionId(String evolutionId) {
            this.evolutionId = evolutionId;
        }

        public String getTargetSpeciesId() {
            return targetSpeciesId;
        }

        public void setTargetSpeciesId(String targetSpeciesId) {
            this.targetSpeciesId = targetSpeciesId;
        }

        public int getFromStage() {
            return fromStage;
        }

        public void setFromStage(int fromStage) {
            this.fromStage = fromStage;
        }

        public int getToStage() {
            return toStage;
        }

        public void setToStage(int toStage) {
            this.toStage = toStage;
        }

        public int getRequiredLevel() {
            return requiredLevel;
        }

        public void setRequiredLevel(int requiredLevel) {
            this.requiredLevel = requiredLevel;
        }

        public List<String> getRequiredItems() {
            return requiredItems;
        }

        public void setRequiredItems(List<String> requiredItems) {
            this. requiredItems = requiredItems;
        }

        public Map<String, Double> getRequiredStats() {
            return requiredStats;
        }

        public void setRequiredStats(Map<String, Double> requiredStats) {
            this. requiredStats = requiredStats;
        }

        public void addRequiredItem(String itemId) {
            requiredItems.add(itemId);
        }

        public void addRequiredStat(String statName, double value) {
            requiredStats.put(statName, value);
        }
    }
}