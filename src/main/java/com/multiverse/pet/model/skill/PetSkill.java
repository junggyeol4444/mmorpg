package com.multiverse.pet.model. skill;

import java.util.*;

/**
 * 펫 스킬 데이터 클래스
 * 펫이 사용할 수 있는 스킬 정보를 담고 있음
 */
public class PetSkill {

    // 기본 정보
    private String skillId;
    private String name;
    private String description;
    private SkillType type;

    // 레벨
    private int currentLevel;
    private int maxLevel;

    // 효과
    private Map<String, Double> baseEffects;
    private Map<String, Double> effectGrowth;

    // 쿨다운
    private int baseCooldown;           // 초
    private double cooldownReduction;   // 레벨당 감소량
    private long lastUsedTime;

    // 비용
    private double manaCost;
    private double healthCost;
    private double hungerCost;

    // 조건
    private int requiredPetLevel;
    private PetTypeRequirement petTypeRequirement;
    private List<String> requiredSkills;

    // 타겟
    private SkillTarget target;
    private double range;
    private int maxTargets;

    // 지속 시간
    private int duration;               // 초 (0이면 즉시 효과)
    private int tickInterval;           // 틱 간격 (지속 효과용)

    // 애니메이션/효과
    private String particleEffect;
    private String soundEffect;

    // 활성화 상태
    private boolean enabled;
    private boolean passive;            // 패시브 스킬 여부

    /**
     * 기본 생성자
     */
    public PetSkill() {
        this.baseEffects = new HashMap<>();
        this.effectGrowth = new HashMap<>();
        this.requiredSkills = new ArrayList<>();
        this.currentLevel = 1;
        this.maxLevel = 10;
        this. baseCooldown = 10;
        this. cooldownReduction = 0.5;
        this. lastUsedTime = 0;
        this.manaCost = 0;
        this.healthCost = 0;
        this. hungerCost = 0;
        this.requiredPetLevel = 1;
        this. target = SkillTarget. SELF;
        this. range = 0;
        this. maxTargets = 1;
        this.duration = 0;
        this. tickInterval = 20;
        this. enabled = true;
        this.passive = false;
    }

    /**
     * 전체 생성자
     */
    public PetSkill(String skillId, String name, SkillType type) {
        this();
        this.skillId = skillId;
        this.name = name;
        this.type = type;
    }

    /**
     * 복제 생성자 (펫에 스킬 부여 시 사용)
     */
    public PetSkill(PetSkill template) {
        this. skillId = template. skillId;
        this.name = template. name;
        this.description = template. description;
        this.type = template. type;
        this.currentLevel = 1;
        this.maxLevel = template.maxLevel;
        this.baseEffects = new HashMap<>(template. baseEffects);
        this.effectGrowth = new HashMap<>(template.effectGrowth);
        this.baseCooldown = template.baseCooldown;
        this. cooldownReduction = template.cooldownReduction;
        this.lastUsedTime = 0;
        this.manaCost = template.manaCost;
        this. healthCost = template.healthCost;
        this.hungerCost = template.hungerCost;
        this. requiredPetLevel = template.requiredPetLevel;
        this.petTypeRequirement = template. petTypeRequirement;
        this.requiredSkills = new ArrayList<>(template. requiredSkills);
        this.target = template.target;
        this.range = template.range;
        this.maxTargets = template. maxTargets;
        this.duration = template.duration;
        this.tickInterval = template.tickInterval;
        this. particleEffect = template. particleEffect;
        this.soundEffect = template.soundEffect;
        this. enabled = true;
        this. passive = template.passive;
    }

    // ===== 효과 관련 메서드 =====

    /**
     * 현재 레벨에서의 효과 값 계산
     *
     * @param effectName 효과 이름
     * @return 효과 값
     */
    public double getEffectValue(String effectName) {
        double base = baseEffects. getOrDefault(effectName, 0.0);
        double growth = effectGrowth.getOrDefault(effectName, 0.0);
        return base + (growth * (currentLevel - 1));
    }

    /**
     * 현재 레벨에서의 모든 효과 계산
     *
     * @return 효과 맵
     */
    public Map<String, Double> getAllEffects() {
        Map<String, Double> effects = new HashMap<>();
        for (String effectName :  baseEffects.keySet()) {
            effects.put(effectName, getEffectValue(effectName));
        }
        return effects;
    }

    /**
     * 기본 효과 설정
     */
    public void setBaseEffect(String effectName, double value) {
        baseEffects. put(effectName, value);
    }

    /**
     * 효과 성장률 설정
     */
    public void setEffectGrowth(String effectName, double value) {
        effectGrowth.put(effectName, value);
    }

    // ===== 쿨다운 관련 메서드 =====

    /**
     * 현재 레벨에서의 쿨다운 계산
     *
     * @return 쿨다운 (초)
     */
    public double getCurrentCooldown() {
        double reduction = cooldownReduction * (currentLevel - 1);
        return Math.max(1, baseCooldown - reduction);
    }

    /**
     * 쿨다운 중인지 확인
     *
     * @return 쿨다운 중 여부
     */
    public boolean isOnCooldown() {
        if (lastUsedTime == 0) return false;
        long currentTime = System. currentTimeMillis();
        long cooldownMillis = (long) (getCurrentCooldown() * 1000);
        return currentTime - lastUsedTime < cooldownMillis;
    }

    /**
     * 남은 쿨다운 시간 (초)
     *
     * @return 남은 쿨다운
     */
    public double getRemainingCooldown() {
        if (! isOnCooldown()) return 0;
        long currentTime = System. currentTimeMillis();
        long cooldownMillis = (long) (getCurrentCooldown() * 1000);
        long remaining = cooldownMillis - (currentTime - lastUsedTime);
        return remaining / 1000.0;
    }

    /**
     * 스킬 사용 (쿨다운 시작)
     */
    public void use() {
        this.lastUsedTime = System.currentTimeMillis();
    }

    /**
     * 쿨다운 리셋
     */
    public void resetCooldown() {
        this.lastUsedTime = 0;
    }

    // ===== 레벨 관련 메서드 =====

    /**
     * 스킬 레벨업
     *
     * @return 레벨업 성공 여부
     */
    public boolean levelUp() {
        if (currentLevel >= maxLevel) {
            return false;
        }
        currentLevel++;
        return true;
    }

    /**
     * 최대 레벨인지 확인
     *
     * @return 최대 레벨 여부
     */
    public boolean isMaxLevel() {
        return currentLevel >= maxLevel;
    }

    /**
     * 레벨업 가능 여부
     *
     * @return 레벨업 가능 여부
     */
    public boolean canLevelUp() {
        return currentLevel < maxLevel;
    }

    // ===== 사용 가능 여부 확인 =====

    /**
     * 스킬 사용 가능 여부 (종합)
     *
     * @param petLevel 펫 레벨
     * @param petHunger 펫 배고픔
     * @return 사용 가능 여부
     */
    public boolean canUse(int petLevel, double petHunger) {
        if (! enabled) return false;
        if (passive) return false; // 패시브는 자동 발동
        if (isOnCooldown()) return false;
        if (petLevel < requiredPetLevel) return false;
        if (petHunger < hungerCost) return false;
        return true;
    }

    /**
     * 사용 불가 이유 반환
     *
     * @param petLevel 펫 레벨
     * @param petHunger 펫 배고픔
     * @return 불가 이유 또는 null
     */
    public String getCannotUseReason(int petLevel, double petHunger) {
        if (!enabled) return "스킬이 비활성화되어 있습니다.";
        if (passive) return "패시브 스킬은 수동 사용이 불가능합니다.";
        if (isOnCooldown()) return "쿨다운 중입니다.  (" + String.format("%.1f", getRemainingCooldown()) + "초)";
        if (petLevel < requiredPetLevel) return "펫 레벨이 부족합니다.  (필요:  " + requiredPetLevel + ")";
        if (petHunger < hungerCost) return "펫의 배고픔이 부족합니다. ";
        return null;
    }

    // ===== 스킬 효과 생성 =====

    /**
     * 스킬 효과 생성
     *
     * @return SkillEffect 객체
     */
    public SkillEffect createEffect() {
        SkillEffect effect = new SkillEffect();
        effect.setSkillId(this.skillId);
        effect.setEffectType(this.type);
        effect.setValues(getAllEffects());
        effect.setDuration(this.duration);
        effect.setTickInterval(this.tickInterval);
        effect.setTarget(this.target);
        effect.setRange(this.range);
        effect.setMaxTargets(this.maxTargets);
        return effect;
    }

    // ===== Getter/Setter =====

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
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
        this.description = description;
    }

    /**
     * 현재 레벨에 맞는 설명 반환 (변수 치환)
     */
    public String getFormattedDescription() {
        String desc = description;
        if (desc == null) return "";
        
        for (Map.Entry<String, Double> entry : getAllEffects().entrySet()) {
            desc = desc.replace("{" + entry.getKey() + "}", 
                    String.format("%. 1f", entry.getValue()));
        }
        desc = desc.replace("{cooldown}", String.format("%.1f", getCurrentCooldown()));
        desc = desc.replace("{duration}", String.valueOf(duration));
        
        return desc;
    }

    public SkillType getType() {
        return type;
    }

    public void setType(SkillType type) {
        this.type = type;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = Math.min(currentLevel, maxLevel);
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Map<String, Double> getBaseEffects() {
        return baseEffects;
    }

    public void setBaseEffects(Map<String, Double> baseEffects) {
        this. baseEffects = baseEffects;
    }

    public Map<String, Double> getEffectGrowth() {
        return effectGrowth;
    }

    public void setEffectGrowth(Map<String, Double> effectGrowth) {
        this.effectGrowth = effectGrowth;
    }

    public int getBaseCooldown() {
        return baseCooldown;
    }

    public void setBaseCooldown(int baseCooldown) {
        this.baseCooldown = baseCooldown;
    }

    public double getCooldownReduction() {
        return cooldownReduction;
    }

    public void setCooldownReduction(double cooldownReduction) {
        this.cooldownReduction = cooldownReduction;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public double getManaCost() {
        return manaCost;
    }

    public void setManaCost(double manaCost) {
        this. manaCost = manaCost;
    }

    public double getHealthCost() {
        return healthCost;
    }

    public void setHealthCost(double healthCost) {
        this. healthCost = healthCost;
    }

    public double getHungerCost() {
        return hungerCost;
    }

    public void setHungerCost(double hungerCost) {
        this.hungerCost = hungerCost;
    }

    public int getRequiredPetLevel() {
        return requiredPetLevel;
    }

    public void setRequiredPetLevel(int requiredPetLevel) {
        this.requiredPetLevel = requiredPetLevel;
    }

    public PetTypeRequirement getPetTypeRequirement() {
        return petTypeRequirement;
    }

    public void setPetTypeRequirement(PetTypeRequirement petTypeRequirement) {
        this.petTypeRequirement = petTypeRequirement;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public void addRequiredSkill(String skillId) {
        requiredSkills.add(skillId);
    }

    public SkillTarget getTarget() {
        return target;
    }

    public void setTarget(SkillTarget target) {
        this.target = target;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public void setTickInterval(int tickInterval) {
        this. tickInterval = tickInterval;
    }

    public String getParticleEffect() {
        return particleEffect;
    }

    public void setParticleEffect(String particleEffect) {
        this.particleEffect = particleEffect;
    }

    public String getSoundEffect() {
        return soundEffect;
    }

    public void setSoundEffect(String soundEffect) {
        this.soundEffect = soundEffect;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetSkill petSkill = (PetSkill) o;
        return Objects.equals(skillId, petSkill. skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillId);
    }

    @Override
    public String toString() {
        return "PetSkill{" +
                "skillId='" + skillId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", level=" + currentLevel + "/" + maxLevel +
                '}';
    }

    // ===== 내부 열거형/클래스 =====

    /**
     * 스킬 타겟 유형
     */
    public enum SkillTarget {
        SELF,           // 자신
        OWNER,          // 주인
        ENEMY,          // 적
        ALLY,           // 아군
        AREA,           // 범위
        ALL             // 모두
    }

    /**
     * 펫 타입 요구 조건
     */
    public enum PetTypeRequirement {
        NONE,           // 제한 없음
        COMBAT,         // 전투형만
        GATHERING,      // 채집형만
        SUPPORT,        // 지원형만
        COMPANION       // 동반형만
    }
}