package com.multiverse.combat.models;

import com.multiverse.combat.models.enums.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 스킬 효과 클래스
 * 스킬의 효과 정보를 저장합니다.
 */
public class SkillEffect {
    
    private EffectType type;
    private Map<String, Object> parameters;
    
    // 데미지
    private double baseDamage;
    private DamageType damageType;
    private double damageScaling;  // 스탯 계수
    
    // 범위
    private TargetType targetType;
    private double range;
    private double radius;  // AoE 범위
    
    // 투사체
    private boolean isProjectile;
    private double projectileSpeed;
    
    // 지속 효과
    private long duration;  // 밀리초
    private int tickInterval;  // 틱 간격 (밀리초)
    
    /**
     * 기본 생성자
     */
    public SkillEffect() {
        this.parameters = new HashMap<>();
        this. damageType = DamageType. PHYSICAL;
        this.damageScaling = 1.0;
        this.targetType = TargetType.TARGET;
        this.range = 5.0;
        this.radius = 0.0;
        this.isProjectile = false;
        this.projectileSpeed = 1.0;
        this.tickInterval = 500;
    }
    
    // ===== Getter & Setter =====
    
    public EffectType getType() { return type; }
    public void setType(EffectType type) { this.type = type; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    public double getBaseDamage() { return baseDamage; }
    public void setBaseDamage(double baseDamage) { this. baseDamage = baseDamage; }
    
    public DamageType getDamageType() { return damageType; }
    public void setDamageType(DamageType damageType) { this.damageType = damageType; }
    
    public double getDamageScaling() { return damageScaling; }
    public void setDamageScaling(double damageScaling) { this.damageScaling = damageScaling; }
    
    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this. targetType = targetType; }
    
    public double getRange() { return range; }
    public void setRange(double range) { this.range = range; }
    
    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }
    
    public boolean isProjectile() { return isProjectile; }
    public void setProjectile(boolean projectile) { isProjectile = projectile; }
    
    public double getProjectileSpeed() { return projectileSpeed; }
    public void setProjectileSpeed(double projectileSpeed) { this.projectileSpeed = projectileSpeed; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    public int getTickInterval() { return tickInterval; }
    public void setTickInterval(int tickInterval) { this. tickInterval = tickInterval; }
}