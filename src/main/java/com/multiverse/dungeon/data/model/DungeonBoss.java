package com.multiverse.dungeon.data.model;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * 던전 보스 데이터 클래스
 */
public class DungeonBoss {

    private String bossId; // 보스 ID
    private String name; // 보스 이름
    
    // MythicMobs 연동
    private String mythicMobId; // MythicMobs ID
    
    // 위치
    private Location spawnLocation; // 스폰 위치
    
    // 스탯
    private double baseHealth; // 기본 체력
    private double baseDamage; // 기본 데미지
    
    // 페이즈
    private List<BossPhase> phases; // 페이즈 목록
    
    // 스킬
    private List<BossSkill> skills; // 스킬 목록
    
    // 보상
    private BossReward reward; // 보상
    
    // 엔레이지 (시간 제한)
    private int enrageTime; // 엔레이지 시간 (초)
    private double enrageDamageMultiplier; // 엔레이지 데미지 배율

    /**
     * 생성자
     */
    public DungeonBoss(String bossId, String name, String mythicMobId) {
        this.bossId = bossId;
        this.name = name;
        this.mythicMobId = mythicMobId;
        this.baseHealth = 100.0;
        this.baseDamage = 10.0;
        this.phases = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.reward = new BossReward();
        this.enrageTime = 300; // 5분
        this.enrageDamageMultiplier = 2.0;
    }

    /**
     * 기본 생성자
     */
    public DungeonBoss() {
        this("unknown", "Unnamed Boss", "");
    }

    // ===== Getters & Setters =====

    public String getBossId() {
        return bossId;
    }

    public void setBossId(String bossId) {
        this.bossId = bossId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMythicMobId() {
        return mythicMobId;
    }

    public void setMythicMobId(String mythicMobId) {
        this.mythicMobId = mythicMobId;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this. spawnLocation = spawnLocation;
    }

    public double getBaseHealth() {
        return baseHealth;
    }

    public void setBaseHealth(double baseHealth) {
        this.baseHealth = Math.max(0.5, baseHealth);
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(double baseDamage) {
        this.baseDamage = Math.max(0.1, baseDamage);
    }

    public List<BossPhase> getPhases() {
        return phases;
    }

    public void setPhases(List<BossPhase> phases) {
        this.phases = phases != null ? phases : new ArrayList<>();
    }

    public void addPhase(BossPhase phase) {
        this.phases.add(phase);
    }

    public List<BossSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<BossSkill> skills) {
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    public void addSkill(BossSkill skill) {
        this.skills.add(skill);
    }

    public BossReward getReward() {
        return reward;
    }

    public void setReward(BossReward reward) {
        this.reward = reward != null ? reward : new BossReward();
    }

    public int getEnrageTime() {
        return enrageTime;
    }

    public void setEnrageTime(int enrageTime) {
        this. enrageTime = Math.max(60, enrageTime);
    }

    public double getEnrageDamageMultiplier() {
        return enrageDamageMultiplier;
    }

    public void setEnrageDamageMultiplier(double enrageDamageMultiplier) {
        this.enrageDamageMultiplier = Math.max(1.0, enrageDamageMultiplier);
    }

    // ===== 비즈니스 로직 =====

    /**
     * 현재 HP에 맞는 페이즈 조회
     *
     * @param currentHealthPercent 현재 HP (%)
     * @return 해당 페이즈, 없으면 null
     */
    public BossPhase getCurrentPhase(double currentHealthPercent) {
        BossPhase currentPhase = null;
        
        for (BossPhase phase : phases) {
            if (phase.shouldActivate(currentHealthPercent)) {
                if (currentPhase == null || phase.getPhaseNumber() > currentPhase.getPhaseNumber()) {
                    currentPhase = phase;
                }
            }
        }
        
        return currentPhase;
    }

    /**
     * 보스가 다음 페이즈로 진입했는지 확인
     *
     * @param previousHealthPercent 이전 HP (%)
     * @param currentHealthPercent 현재 HP (%)
     * @return 페이즈 변경되었으면 true
     */
    public boolean hasPhaseChanged(double previousHealthPercent, double currentHealthPercent) {
        BossPhase previousPhase = getCurrentPhase(previousHealthPercent);
        BossPhase currentPhase = getCurrentPhase(currentHealthPercent);
        
        if (previousPhase == null && currentPhase == null) {
            return false;
        }
        
        if (previousPhase == null || currentPhase == null) {
            return true;
        }
        
        return previousPhase.getPhaseNumber() != currentPhase.getPhaseNumber();
    }

    /**
     * 사용 가능한 스킬 목록 (조건 만족)
     *
     * @param currentHealthPercent 현재 HP (%)
     * @return 사용 가능한 스킬 목록
     */
    public List<BossSkill> getAvailableSkills(double currentHealthPercent) {
        List<BossSkill> available = new ArrayList<>();
        
        for (BossSkill skill : skills) {
            if (skill.  canUse(currentHealthPercent)) {
                available.add(skill);
            }
        }
        
        return available;
    }

    /**
     * 랜덤 스킬 선택
     *
     * @param currentHealthPercent 현재 HP (%)
     * @return 선택된 스킬, 없으면 null
     */
    public BossSkill getRandomSkill(double currentHealthPercent) {
        List<BossSkill> available = getAvailableSkills(currentHealthPercent);
        
        if (available.isEmpty()) {
            return null;
        }
        
        return available.get((int) (Math.random() * available. size()));
    }

    /**
     * 보스 체력을 스케일링된 값으로 계산
     *
     * @param healthMultiplier 체력 배율
     * @return 스케일링된 체력
     */
    public double getScaledHealth(double healthMultiplier) {
        return baseHealth * healthMultiplier;
    }

    /**
     * 보스 데미지를 스케일링된 값으로 계산
     *
     * @param damageMultiplier 데미지 배율
     * @return 스케일링된 데미지
     */
    public double getScaledDamage(double damageMultiplier) {
        return baseDamage * damageMultiplier;
    }

    /**
     * 보스가 완전히 설정되었는지 확인
     *
     * @return 완전히 설정되었으면 true
     */
    public boolean isFullyConfigured() {
        if (spawnLocation == null) {
            return false;
        }

        if (phases.isEmpty()) {
            return false;
        }

        if (skills.isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "DungeonBoss{" +
                "bossId='" + bossId + '\'' +
                ", name='" + name + '\'' +
                ", mythicMobId='" + mythicMobId + '\'' +
                ", baseHealth=" + baseHealth +
                ", baseDamage=" + baseDamage +
                ", phases=" + phases. size() +
                ", skills=" + skills.size() +
                ", enrageTime=" + enrageTime +
                '}';
    }
}