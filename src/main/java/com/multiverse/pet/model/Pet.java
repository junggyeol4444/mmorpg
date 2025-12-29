package com.multiverse.pet. model;

import com.multiverse.pet.model.breeding.PetGenetics;
import com. multiverse.pet. model.equipment.PetEquipSlot;
import com.multiverse.pet.model.skill.PetSkill;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * 펫 기본 데이터 클래스
 * 펫의 모든 정보를 담고 있는 핵심 모델
 */
public class Pet {

    // 고유 식별자
    private UUID petId;
    private UUID ownerId;
    private String petName;
    private String speciesId;
    private PetType type;
    private PetRarity rarity;

    // 외형
    private EntityType entityType;
    private String customModelId;       // MythicMobs ID

    // 레벨/경험치
    private int level;
    private long experience;
    private long expToNext;

    // 스탯
    private Map<String, Double> baseStats;
    private Map<String, Double> bonusStats;
    private Map<String, Double> equipmentStats;

    // 스킬
    private List<PetSkill> skills;
    private int skillPoints;

    // 진화
    private int evolutionStage;
    private String evolutionPath;

    // 장비
    private Map<PetEquipSlot, ItemStack> equipment;

    // 상태
    private PetStatus status;
    private PetBehavior behavior;
    private double hunger;              // 0-100
    private double happiness;           // 0-100
    private double health;
    private double maxHealth;

    // 유전
    private PetGenetics genetics;

    // 시간
    private long birthTime;
    private long lastFeedTime;
    private long lastPlayTime;
    private long totalActiveTime;

    // 전투 통계
    private int killCount;
    private int deathCount;
    private int battleWins;
    private int battleLosses;

    /**
     * 기본 생성자
     */
    public Pet() {
        this. petId = UUID.randomUUID();
        this.baseStats = new HashMap<>();
        this.bonusStats = new HashMap<>();
        this.equipmentStats = new HashMap<>();
        this.skills = new ArrayList<>();
        this.equipment = new EnumMap<>(PetEquipSlot.class);
        this.status = PetStatus.STORED;
        this. behavior = PetBehavior.FOLLOW;
        this. level = 1;
        this.experience = 0;
        this.expToNext = 100;
        this. evolutionStage = 1;
        this. hunger = 100.0;
        this.happiness = 100.0;
        this.skillPoints = 0;
        this.birthTime = System.currentTimeMillis();
        this.lastFeedTime = System. currentTimeMillis();
        this.lastPlayTime = System. currentTimeMillis();
        this.totalActiveTime = 0;
        this. killCount = 0;
        this. deathCount = 0;
        this.battleWins = 0;
        this.battleLosses = 0;
    }

    /**
     * 전체 생성자
     */
    public Pet(UUID ownerId, String petName, String speciesId, PetType type, 
               PetRarity rarity, EntityType entityType) {
        this();
        this. ownerId = ownerId;
        this.petName = petName;
        this.speciesId = speciesId;
        this.type = type;
        this.rarity = rarity;
        this.entityType = entityType;
    }

    /**
     * 복제 생성자 (교배용)
     */
    public Pet(Pet parent1, Pet parent2, PetGenetics genetics) {
        this();
        this.ownerId = parent1.getOwnerId();
        this.speciesId = parent1.getSpeciesId();
        this.type = parent1.getType();
        this.entityType = parent1.getEntityType();
        this.genetics = genetics;

        // 희귀도 계산 (부모 중 높은 것 + 변이 확률)
        if (genetics.isMutant()) {
            int rarityOrdinal = Math.min(
                Math.max(parent1.getRarity().ordinal(), parent2.getRarity().ordinal()) + 1,
                PetRarity.values().length - 1
            );
            this.rarity = PetRarity. values()[rarityOrdinal];
        } else {
            this.rarity = parent1.getRarity().ordinal() >= parent2.getRarity().ordinal() 
                ? parent1.getRarity() : parent2.getRarity();
        }

        // 상속된 스탯 적용
        this. baseStats = new HashMap<>(genetics.getInheritedStats());

        // 기본 이름
        this.petName = parent1.getSpeciesId() + "_baby";
    }

    // ===== 스탯 관련 메서드 =====

    /**
     * 총 스탯 계산 (기본 + 보너스 + 장비)
     */
    public double getTotalStat(String statName) {
        double base = baseStats.getOrDefault(statName, 0.0);
        double bonus = bonusStats. getOrDefault(statName, 0.0);
        double equip = equipmentStats.getOrDefault(statName, 0.0);

        // 희귀도 배율 적용
        double rarityMultiplier = rarity != null ? rarity.getStatMultiplier() : 1.0;

        return (base * rarityMultiplier) + bonus + equip;
    }

    /**
     * 기본 스탯 설정
     */
    public void setBaseStat(String statName, double value) {
        baseStats.put(statName, value);
        if (statName.equals("health")) {
            this.maxHealth = getTotalStat("health");
            if (this.health > this.maxHealth) {
                this.health = this.maxHealth;
            }
        }
    }

    /**
     * 보너스 스탯 추가
     */
    public void addBonusStat(String statName, double value) {
        bonusStats.merge(statName, value, Double::sum);
        if (statName. equals("health")) {
            this.maxHealth = getTotalStat("health");
        }
    }

    /**
     * 장비 스탯 설정
     */
    public void setEquipmentStat(String statName, double value) {
        equipmentStats.put(statName, value);
        if (statName.equals("health")) {
            this.maxHealth = getTotalStat("health");
        }
    }

    /**
     * 장비 스탯 초기화
     */
    public void clearEquipmentStats() {
        equipmentStats.clear();
        this.maxHealth = getTotalStat("health");
    }

    /**
     * 모든 스탯 맵 반환
     */
    public Map<String, Double> getAllStats() {
        Map<String, Double> allStats = new HashMap<>();
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(baseStats.keySet());
        allKeys.addAll(bonusStats.keySet());
        allKeys.addAll(equipmentStats.keySet());

        for (String key : allKeys) {
            allStats. put(key, getTotalStat(key));
        }
        return allStats;
    }

    // ===== 스킬 관련 메서드 =====

    /**
     * 스킬 추가
     */
    public void addSkill(PetSkill skill) {
        if (!hasSkill(skill. getSkillId())) {
            skills.add(skill);
        }
    }

    /**
     * 스킬 제거
     */
    public void removeSkill(String skillId) {
        skills.removeIf(skill -> skill.getSkillId().equals(skillId));
    }

    /**
     * 스킬 보유 여부
     */
    public boolean hasSkill(String skillId) {
        return skills. stream().anyMatch(skill -> skill.getSkillId().equals(skillId));
    }

    /**
     * 스킬 가져오기
     */
    public PetSkill getSkill(String skillId) {
        return skills.stream()
                .filter(skill -> skill.getSkillId().equals(skillId))
                .findFirst()
                .orElse(null);
    }

    // ===== 장비 관련 메서드 =====

    /**
     * 장비 장착
     */
    public ItemStack equipItem(PetEquipSlot slot, ItemStack item) {
        ItemStack previous = equipment.get(slot);
        equipment.put(slot, item);
        return previous;
    }

    /**
     * 장비 해제
     */
    public ItemStack unequipItem(PetEquipSlot slot) {
        return equipment.remove(slot);
    }

    /**
     * 장비 가져오기
     */
    public ItemStack getEquipment(PetEquipSlot slot) {
        return equipment. get(slot);
    }

    /**
     * 장비 슬롯 비어있는지 확인
     */
    public boolean isSlotEmpty(PetEquipSlot slot) {
        return ! equipment.containsKey(slot) || equipment.get(slot) == null;
    }

    // ===== 상태 관련 메서드 =====

    /**
     * 펫 활성화 여부
     */
    public boolean isActive() {
        return status == PetStatus.ACTIVE;
    }

    /**
     * 펫 활동 가능 여부 (배고픔/행복도 체크)
     */
    public boolean canAct() {
        return hunger >= 10. 0 && happiness >= 10.0 && health > 0;
    }

    /**
     * 펫 전투 가능 여부
     */
    public boolean canFight() {
        return isActive() && canAct() && status != PetStatus. BREEDING;
    }

    /**
     * 데미지 적용
     */
    public void damage(double amount) {
        this.health = Math.max(0, this.health - amount);
    }

    /**
     * 힐 적용
     */
    public void heal(double amount) {
        this.health = Math.min(this.maxHealth, this.health + amount);
    }

    /**
     * 체력 퍼센트
     */
    public double getHealthPercentage() {
        return maxHealth > 0 ? (health / maxHealth) * 100.0 : 0;
    }

    /**
     * 배고픔 감소
     */
    public void decreaseHunger(double amount) {
        this.hunger = Math.max(0, this.hunger - amount);
    }

    /**
     * 배고픔 증가
     */
    public void increaseHunger(double amount) {
        this.hunger = Math.min(100, this.hunger + amount);
        this.lastFeedTime = System.currentTimeMillis();
    }

    /**
     * 행복도 감소
     */
    public void decreaseHappiness(double amount) {
        this.happiness = Math.max(0, this.happiness - amount);
    }

    /**
     * 행복도 증가
     */
    public void increaseHappiness(double amount) {
        this.happiness = Math.min(100, this.happiness + amount);
        this.lastPlayTime = System.currentTimeMillis();
    }

    // ===== 경험치/레벨 관련 메서드 =====

    /**
     * 경험치 추가 (레벨업 여부 반환)
     */
    public boolean addExperience(long amount) {
        this.experience += amount;
        if (this.experience >= this.expToNext) {
            return true; // 레벨업 필요
        }
        return false;
    }

    /**
     * 레벨업 처리
     */
    public void levelUp() {
        this.experience -= this.expToNext;
        this. level++;
        this. skillPoints++;
        calculateExpToNext();
    }

    /**
     * 다음 레벨 필요 경험치 계산
     */
    public void calculateExpToNext() {
        // 기본 공식: 100 * level^1.5
        this.expToNext = (long) (100 * Math.pow(this.level, 1.5));
    }

    /**
     * 경험치 퍼센트
     */
    public double getExpPercentage() {
        return expToNext > 0 ? ((double) experience / expToNext) * 100.0 : 0;
    }

    // ===== 전투 통계 메서드 =====

    /**
     * 킬 카운트 증가
     */
    public void incrementKillCount() {
        this.killCount++;
    }

    /**
     * 사망 카운트 증가
     */
    public void incrementDeathCount() {
        this. deathCount++;
    }

    /**
     * 배틀 승리
     */
    public void addBattleWin() {
        this.battleWins++;
    }

    /**
     * 배틀 패배
     */
    public void addBattleLoss() {
        this.battleLosses++;
    }

    /**
     * 승률 계산
     */
    public double getWinRate() {
        int total = battleWins + battleLosses;
        return total > 0 ? ((double) battleWins / total) * 100.0 :  0;
    }

    // ===== 시간 관련 메서드 =====

    /**
     * 활성 시간 추가
     */
    public void addActiveTime(long millis) {
        this.totalActiveTime += millis;
    }

    /**
     * 나이 계산 (밀리초)
     */
    public long getAge() {
        return System.currentTimeMillis() - birthTime;
    }

    /**
     * 나이 (일 단위)
     */
    public int getAgeDays() {
        return (int) (getAge() / (1000 * 60 * 60 * 24));
    }

    /**
     * 마지막 먹이 이후 시간 (시간 단위)
     */
    public double getHoursSinceLastFeed() {
        return (System.currentTimeMillis() - lastFeedTime) / (1000.0 * 60 * 60);
    }

    /**
     * 마지막 놀이 이후 시간 (시간 단위)
     */
    public double getHoursSinceLastPlay() {
        return (System.currentTimeMillis() - lastPlayTime) / (1000.0 * 60 * 60);
    }

    // ===== Getter/Setter =====

    public UUID getPetId() {
        return petId;
    }

    public void setPetId(UUID petId) {
        this. petId = petId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this. ownerId = ownerId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(String speciesId) {
        this. speciesId = speciesId;
    }

    public PetType getType() {
        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public PetRarity getRarity() {
        return rarity;
    }

    public void setRarity(PetRarity rarity) {
        this.rarity = rarity;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public long getExpToNext() {
        return expToNext;
    }

    public void setExpToNext(long expToNext) {
        this. expToNext = expToNext;
    }

    public Map<String, Double> getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(Map<String, Double> baseStats) {
        this.baseStats = baseStats;
    }

    public Map<String, Double> getBonusStats() {
        return bonusStats;
    }

    public void setBonusStats(Map<String, Double> bonusStats) {
        this.bonusStats = bonusStats;
    }

    public Map<String, Double> getEquipmentStats() {
        return equipmentStats;
    }

    public void setEquipmentStats(Map<String, Double> equipmentStats) {
        this.equipmentStats = equipmentStats;
    }

    public List<PetSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<PetSkill> skills) {
        this.skills = skills;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this. skillPoints = skillPoints;
    }

    public void useSkillPoint() {
        if (skillPoints > 0) {
            skillPoints--;
        }
    }

    public int getEvolutionStage() {
        return evolutionStage;
    }

    public void setEvolutionStage(int evolutionStage) {
        this.evolutionStage = evolutionStage;
    }

    public String getEvolutionPath() {
        return evolutionPath;
    }

    public void setEvolutionPath(String evolutionPath) {
        this.evolutionPath = evolutionPath;
    }

    public Map<PetEquipSlot, ItemStack> getEquipmentMap() {
        return equipment;
    }

    public void setEquipment(Map<PetEquipSlot, ItemStack> equipment) {
        this. equipment = equipment;
    }

    public PetStatus getStatus() {
        return status;
    }

    public void setStatus(PetStatus status) {
        this.status = status;
    }

    public PetBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(PetBehavior behavior) {
        this.behavior = behavior;
    }

    public double getHunger() {
        return hunger;
    }

    public void setHunger(double hunger) {
        this.hunger = Math.max(0, Math.min(100, hunger));
    }

    public double getHappiness() {
        return happiness;
    }

    public void setHappiness(double happiness) {
        this. happiness = Math.max(0, Math. min(100, happiness));
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this. health = Math.max(0, Math. min(this.maxHealth, health));
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public PetGenetics getGenetics() {
        return genetics;
    }

    public void setGenetics(PetGenetics genetics) {
        this. genetics = genetics;
    }

    public long getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(long birthTime) {
        this.birthTime = birthTime;
    }

    public long getLastFeedTime() {
        return lastFeedTime;
    }

    public void setLastFeedTime(long lastFeedTime) {
        this.lastFeedTime = lastFeedTime;
    }

    public long getLastPlayTime() {
        return lastPlayTime;
    }

    public void setLastPlayTime(long lastPlayTime) {
        this.lastPlayTime = lastPlayTime;
    }

    public long getTotalActiveTime() {
        return totalActiveTime;
    }

    public void setTotalActiveTime(long totalActiveTime) {
        this.totalActiveTime = totalActiveTime;
    }

    public int getKillCount() {
        return killCount;
    }

    public void setKillCount(int killCount) {
        this.killCount = killCount;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public int getBattleWins() {
        return battleWins;
    }

    public void setBattleWins(int battleWins) {
        this.battleWins = battleWins;
    }

    public int getBattleLosses() {
        return battleLosses;
    }

    public void setBattleLosses(int battleLosses) {
        this.battleLosses = battleLosses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(petId, pet.petId);
    }

    @Override
    public int hashCode() {
        return Objects. hash(petId);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "petId=" + petId +
                ", petName='" + petName + '\'' +
                ", speciesId='" + speciesId + '\'' +
                ", level=" + level +
                ", rarity=" + rarity +
                ", status=" + status +
                '}';
    }
}