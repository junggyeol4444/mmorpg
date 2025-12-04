package com. multiverse.combat. models;

import com.multiverse.combat.models.enums.*;
import java.util.List;

/**
 * 스킬 데이터 클래스
 * 스킬의 모든 정보를 저장합니다. 
 */
public class Skill {
    
    private String skillId;
    private String name;
    private SkillType type;
    private SkillCategory category;
    
    // 설명
    private String description;
    private List<String> lore;
    
    // 레벨
    private int currentLevel;
    private int maxLevel;
    
    // 조건
    private int requiredLevel;
    private int requiredStatPoints;
    private String requiredSkill;  // 선행 스킬 ID
    
    // 비용
    private CostType costType;
    private double baseCost;  // 레벨당 비용
    
    // 쿨다운
    private long baseCooldown;  // 밀리초
    
    // 효과
    private SkillEffect skillEffect;
    
    // 캐스팅
    private long castTime;  // 밀리초
    private boolean canMove;  // 캐스팅 중 이동 가능
    
    /**
     * 기본 생성자
     */
    public Skill() {
        this.currentLevel = 1;
        this.maxLevel = 1;
        this.costType = CostType.NONE;
        this.baseCost = 0.0;
        this.baseCooldown = 0L;
        this.castTime = 0L;
        this. canMove = true;
    }
    
    // ===== Getter & Setter =====
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public SkillType getType() { return type; }
    public void setType(SkillType type) { this.type = type; }
    
    public SkillCategory getCategory() { return category; }
    public void setCategory(SkillCategory category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getLore() { return lore; }
    public void setLore(List<String> lore) { this.lore = lore; }
    
    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
    
    public int getMaxLevel() { return maxLevel; }
    public void setMaxLevel(int maxLevel) { this.maxLevel = maxLevel; }
    
    public int getRequiredLevel() { return requiredLevel; }
    public void setRequiredLevel(int requiredLevel) { this.requiredLevel = requiredLevel; }
    
    public int getRequiredStatPoints() { return requiredStatPoints; }
    public void setRequiredStatPoints(int requiredStatPoints) { this.requiredStatPoints = requiredStatPoints; }
    
    public String getRequiredSkill() { return requiredSkill; }
    public void setRequiredSkill(String requiredSkill) { this.requiredSkill = requiredSkill; }
    
    public CostType getCostType() { return costType; }
    public void setCostType(CostType costType) { this.costType = costType; }
    
    public double getBaseCost() { return baseCost; }
    public void setBaseCost(double baseCost) { this.baseCost = baseCost; }
    
    public long getBaseCooldown() { return baseCooldown; }
    public void setBaseCooldown(long baseCooldown) { this. baseCooldown = baseCooldown; }
    
    public SkillEffect getSkillEffect() { return skillEffect; }
    public void setSkillEffect(SkillEffect skillEffect) { this.skillEffect = skillEffect; }
    
    public long getCastTime() { return castTime; }
    public void setCastTime(long castTime) { this.castTime = castTime; }
    
    public boolean isCanMove() { return canMove; }
    public void setCanMove(boolean canMove) { this.canMove = canMove; }
}