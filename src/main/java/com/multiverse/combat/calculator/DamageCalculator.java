package com.multiverse.combat.calculator;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models.enums.DamageType;
import java.util.Random;

/**
 * 데미지 계산 클래스
 * 물리, 마법 데미지 계산 및 크리티컬, 회피를 처리합니다.
 */
public class DamageCalculator {
    
    private final CombatCore plugin;
    private final Random random = new Random();
    
    // 크리티컬 설정값
    private static final double BASE_CRIT_CHANCE = 5.0;
    private static final double BASE_CRIT_MULTIPLIER = 1.5;
    private static final double MAX_CRIT_CHANCE = 100.0;
    
    // 회피 설정값
    private static final double MAX_DODGE_CHANCE = 75.0;
    
    // 방어력 설정값
    private static final double MAX_DEFENSE_REDUCTION = 75.0;
    private static final double MAX_MAGICAL_RESISTANCE = 75.0;
    
    /**
     * DamageCalculator 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public DamageCalculator(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 물리 데미지 계산
     * 기본 데미지 + (STR * 0.5) 계산
     * @param attacker 공격자
     * @param target 대상
     * @param baseDamage 기본 데미지
     * @return 계산된 물리 데미지
     */
    public double calculatePhysicalDamage(Player attacker, LivingEntity target, double baseDamage) {
        // 공격자 강도(STR) 스탯 조회 (PlayerDataCore와 연동 필요)
        // 현재는 플레이어 레벨을 기준으로 계산
        int str = attacker.getLevel() * 5;  // 임시 계산
        double weaponDamage = baseDamage + (str * 0.5);
        
        // 버프 적용
        double buffMultiplier = 1.0;  // 버프 시스템 추후 적용
        double physicalDamage = weaponDamage * buffMultiplier;
        
        // 방어율 적용
        double defenseRate = plugin.getDefenseCalculator().getDefenseRate(target);
        double finalDamage = physicalDamage * (1.0 - defenseRate);
        
        return Math.max(finalDamage, 0.1);  // 최소 0.1 데미지
    }
    
    /**
     * 마법 데미지 계산
     * 기본 데미지 + (INT * 0.8) 계산
     * @param attacker 공격자
     * @param target 대상
     * @param baseDamage 기본 데미지
     * @return 계산된 마법 데미지
     */
    public double calculateMagicalDamage(Player attacker, LivingEntity target, double baseDamage) {
        // 공격자 지능(INT) 스탯 조회
        // 현재는 플레이어 레벨을 기준으로 계산
        int intel = attacker.getLevel() * 4;  // 임시 계산
        double skillDamage = baseDamage + (intel * 0.8);
        
        // 버프 적용
        double buffMultiplier = 1.0;  // 버프 시스템 추후 적용
        double magicalDamage = skillDamage * buffMultiplier;
        
        // 마법 저항 적용
        double magicalResistance = plugin.getDefenseCalculator().getMagicalResistanceRate(target);
        double finalDamage = magicalDamage * (1.0 - magicalResistance);
        
        return Math.max(finalDamage, 0.1);  // 최소 0. 1 데미지
    }
    
    /**
     * 크리티컬 확률 계산
     * 기본 5% + (DEX * 0.05%) + (LUK * 0.1%)
     * @param attacker 공격자
     * @return 크리티컬 확률 (0~100)
     */
    public double calculateCriticalChance(Player attacker) {
        double dex = attacker.getLevel() * 3;  // 민첩(DEX) 임시 계산
        double luk = attacker.getLevel() * 2;  // 운(LUK) 임시 계산
        
        double critChance = BASE_CRIT_CHANCE + (dex * 0.05) + (luk * 0.1);
        
        // 최대 확률 제한
        return Math.min(critChance, MAX_CRIT_CHANCE);
    }
    
    /**
     * 크리티컬 체크
     * @param attacker 공격자
     * @return 크리티컬이면 true
     */
    public boolean isCritical(Player attacker) {
        double critChance = calculateCriticalChance(attacker);
        double roll = random.nextDouble() * 100. 0;
        return roll < critChance;
    }
    
    /**
     * 크리티컬 데미지 적용
     * 기본 데미지 * 1.5 * (1 + 크리티컬 보너스)
     * @param damage 기본 데미지
     * @param attacker 공격자
     * @return 크리티컬 데미지
     */
    public double applyCritical(double damage, Player attacker) {
        double critMultiplier = BASE_CRIT_MULTIPLIER;
        
        // 크리티컬 데미지 보너스 (추후 아이템 인챈트 등에서 추가)
        double critBonus = 0.0;
        
        return damage * critMultiplier * (1.0 + critBonus);
    }
    
    /**
     * 회피 확률 계산
     * (DEX * 0.1%)로 계산, 최대 75%
     * @param player 플레이어
     * @return 회피 확률 (0~100)
     */
    public double calculateDodgeChance(Player player) {
        double dex = player.getLevel() * 3;  // 민첩(DEX) 임시 계산
        double dodgeChance = dex * 0.1;
        
        // 최대 확률 제한
        return Math.min(dodgeChance, MAX_DODGE_CHANCE);
    }
    
    /**
     * 회피 체크
     * @param player 플레이어
     * @return 회피하면 true
     */
    public boolean isDodged(Player player) {
        double dodgeChance = calculateDodgeChance(player);
        double roll = random.nextDouble() * 100.0;
        return roll < dodgeChance;
    }
    
    /**
     * 방어율 계산 (DefenseCalculator에서 호출)
     * @param entity 대상 엔티티
     * @return 방어율 (0~0.75)
     */
    public double getDefenseRate(LivingEntity entity) {
        return plugin. getDefenseCalculator().getDefenseRate(entity);
    }
    
    /**
     * 최종 데미지 계산
     * 모든 요소를 종합하여 최종 데미지 결정
     * @param attacker 공격자
     * @param target 대상
     * @param baseDamage 기본 데미지
     * @param type 데미지 타입
     * @param canCrit 크리티컬 가능 여부
     * @return 최종 데미지
     */
    public double getFinalDamage(Player attacker, LivingEntity target, 
                                 double baseDamage, DamageType type, boolean canCrit) {
        double finalDamage = 0. 0;
        boolean isCritical = false;
        
        // 데미지 타입별 계산
        switch (type) {
            case PHYSICAL:
                finalDamage = calculatePhysicalDamage(attacker, target, baseDamage);
                break;
            case MAGICAL:
                finalDamage = calculateMagicalDamage(attacker, target, baseDamage);
                break;
            case TRUE_DAMAGE:
                finalDamage = baseDamage;  // 방어 무시
                break;
            case PURE_DAMAGE:
                finalDamage = baseDamage;  // 모든 감소 무시
                break;
        }
        
        // 크리티컬 체크
        if (canCrit && isCritical(attacker)) {
            finalDamage = applyCritical(finalDamage, attacker);
            isCritical = true;
            
            // 크리티컬 통계 기록
            plugin.getCombatDataManager().addCriticalHit(attacker, finalDamage);
        }
        
        // 콤보 보너스 적용
        double comboBonus = plugin.getComboManager().getComboBonus(attacker);
        finalDamage *= comboBonus;
        
        // 속성 데미지 적용 (추후 구현)
        
        // 최소 데미지 보장
        finalDamage = Math.max(finalDamage, 0.1);
        
        return finalDamage;
    }
    
    /**
     * 크리티컬 확률 범위 확인
     * @param chance 확률
     * @return 유효한 확률 (0~100)
     */
    public double validateCritChance(double chance) {
        return Math.max(0, Math.min(chance, MAX_CRIT_CHANCE));
    }
    
    /**
     * 회피 확률 범위 확인
     * @param chance 확률
     * @return 유효한 확률 (0~75)
     */
    public double validateDodgeChance(double chance) {
        return Math. max(0, Math.min(chance, MAX_DODGE_CHANCE));
    }
    
    /**
     * 데미지 범위 확인 (음수 방지)
     * @param damage 데미지
     * @return 유효한 데미지 (최소 0.1)
     */
    public double validateDamage(double damage) {
        return Math.max(damage, 0.1);
    }
}