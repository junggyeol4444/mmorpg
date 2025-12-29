package com.multiverse. pet.   util;

import com. multiverse.pet.    model.Pet;
import com. multiverse.pet.    model.skill.PetSkill;
import org.bukkit.entity.   LivingEntity;

import java.util.    Random;

/**
 * 데미지 계산기
 * 펫 전투 데미지 계산
 */
public class DamageCalculator {

    private static final Random random = new Random();

    // 기본 상수
    private static final double BASE_DAMAGE_VARIANCE = 0.1; // ±10% 변동
    private static final double DEFENSE_SCALING = 100.0;    // 방어력 스케일링
    private static final double MIN_DAMAGE_RATIO = 0.1;     // 최소 데미지 비율

    /**
     * 기본 공격 데미지 계산
     */
    public static double calculateBasicAttackDamage(Pet attacker, Pet defender) {
        double attack = attacker.getTotalStat("attack");
        double defense = defender.getTotalStat("defense");

        // 기본 데미지
        double baseDamage = attack;

        // 방어력 감소
        double damageReduction = defense / (defense + DEFENSE_SCALING);
        double damage = baseDamage * (1 - damageReduction);

        // 최소 데미지 보장
        damage = Math.max(damage, baseDamage * MIN_DAMAGE_RATIO);

        // 데미지 변동
        damage = applyVariance(damage);

        // 치명타 체크
        CriticalResult crit = checkCritical(attacker);
        if (crit. isCritical) {
            damage *= crit.multiplier;
        }

        return Math.max(1, damage);
    }

    /**
     * 스킬 데미지 계산
     */
    public static double calculateSkillDamage(Pet attacker, Pet defender, PetSkill skill) {
        double attack = attacker.getTotalStat("attack");
        double defense = defender.getTotalStat("defense");

        // 스킬 기본 데미지
        double skillDamage = skill.getEffectValue("damage");

        // 스킬 공격력 스케일링
        double scaling = skill.getEffectValue("attack_scaling");
        if (scaling <= 0) {
            scaling = 1.0;
        }
        skillDamage += attack * scaling;

        // 스킬 레벨 보너스
        int skillLevel = skill.getCurrentLevel();
        skillDamage *= (1 + (skillLevel - 1) * 0.1);

        // 방어력 감소
        double damageReduction = defense / (defense + DEFENSE_SCALING);

        // 스킬 관통력
        double penetration = skill.getEffectValue("penetration");
        if (penetration > 0) {
            damageReduction *= (1 - penetration / 100);
        }

        double damage = skillDamage * (1 - damageReduction);

        // 최소 데미지 보장
        damage = Math.max(damage, skillDamage * MIN_DAMAGE_RATIO);

        // 데미지 변동
        damage = applyVariance(damage);

        // 치명타 체크
        CriticalResult crit = checkCritical(attacker);
        if (crit.isCritical) {
            damage *= crit.multiplier;
        }

        return Math. max(1, damage);
    }

    /**
     * 펫 -> 엔티티 데미지 계산
     */
    public static double calculateDamageToEntity(Pet attacker, LivingEntity target) {
        double attack = attacker.getTotalStat("attack");

        // 기본 데미지
        double damage = attack;

        // 타겟 방어구 고려 (간략화)
        double armorReduction = 0;
        if (target. getEquipment() != null) {
            // 방어구에 따른 감소 (간략화된 계산)
            armorReduction = target.getEquipment().getHelmet() != null ? 0.05 : 0;
            armorReduction += target.getEquipment().getChestplate() != null ? 0.1 :  0;
            armorReduction += target.getEquipment().getLeggings() != null ? 0.08 : 0;
            armorReduction += target.getEquipment().getBoots() != null ? 0.04 : 0;
        }

        damage *= (1 - armorReduction);

        // 데미지 변동
        damage = applyVariance(damage);

        // 치명타 체크
        CriticalResult crit = checkCritical(attacker);
        if (crit. isCritical) {
            damage *= crit.multiplier;
        }

        return Math.max(1, damage);
    }

    /**
     * 엔티티 -> 펫 데미지 계산
     */
    public static double calculateDamageFromEntity(LivingEntity attacker, Pet defender) {
        // 기본 데미지 추정
        double baseDamage = estimateEntityDamage(attacker);
        double defense = defender.getTotalStat("defense");

        // 방어력 감소
        double damageReduction = defense / (defense + DEFENSE_SCALING);
        double damage = baseDamage * (1 - damageReduction);

        // 최소 데미지
        damage = Math. max(damage, baseDamage * MIN_DAMAGE_RATIO);

        // 데미지 변동
        damage = applyVariance(damage);

        return Math.max(1, damage);
    }

    /**
     * 엔티티 기본 데미지 추정
     */
    private static double estimateEntityDamage(LivingEntity entity) {
        switch (entity.getType()) {
            case ZOMBIE:
            case HUSK:
            case DROWNED:
                return 6.0;
            case SKELETON:
            case STRAY:
                return 5.0;
            case SPIDER:
            case CAVE_SPIDER:
                return 4.0;
            case CREEPER:
                return 15.0;
            case ENDERMAN:
                return 10.0;
            case BLAZE:
                return 8.0;
            case WITHER_SKELETON: 
                return 12.0;
            case PIGLIN_BRUTE:
                return 14.0;
            case RAVAGER:
                return 18.0;
            case WARDEN:
                return 30.0;
            case ENDER_DRAGON: 
                return 15.0;
            case WITHER: 
                return 12.0;
            default:
                return 5.0;
        }
    }

    /**
     * 치명타 체크
     */
    public static CriticalResult checkCritical(Pet attacker) {
        double critChance = attacker.getTotalStat("critical_chance");
        double critDamage = attacker.getTotalStat("critical_damage");

        if (critDamage <= 0) {
            critDamage = 150; // 기본 150%
        }

        boolean isCrit = random.nextDouble() * 100 < critChance;

        return new CriticalResult(isCrit, isCrit ? critDamage / 100 : 1.0);
    }

    /**
     * 데미지 변동 적용
     */
    private static double applyVariance(double damage) {
        double variance = (random.nextDouble() * 2 - 1) * BASE_DAMAGE_VARIANCE;
        return damage * (1 + variance);
    }

    /**
     * 회복량 계산
     */
    public static double calculateHealAmount(Pet healer, PetSkill skill) {
        double healAmount = skill.getEffectValue("healing");

        // 힐러 스탯 스케일링
        double maxHealth = healer.getMaxHealth();
        double healthScaling = skill.getEffectValue("health_scaling");
        if (healthScaling > 0) {
            healAmount += maxHealth * healthScaling / 100;
        }

        // 스킬 레벨 보너스
        int skillLevel = skill.getCurrentLevel();
        healAmount *= (1 + (skillLevel - 1) * 0.1);

        return healAmount;
    }

    /**
     * 방어 시 데미지 감소 계산
     */
    public static double calculateDefendedDamage(double incomingDamage, Pet defender) {
        // 방어 시 50% 기본 감소
        double reduction = 0.5;

        // 방어력 추가 감소
        double defense = defender.getTotalStat("defense");
        double defenseBonus = defense / (defense + DEFENSE_SCALING) * 0.3; // 최대 30% 추가

        reduction += defenseBonus;
        reduction = Math.min(reduction, 0.9); // 최대 90% 감소

        return incomingDamage * (1 - reduction);
    }

    /**
     * 타입 상성 배율
     */
    public static double getTypeMultiplier(Pet attacker, Pet defender) {
        if (attacker.getType() == null || defender.getType() == null) {
            return 1.0;
        }

        // 간단한 상성 시스템
        // COMBAT > GATHERING > SUPPORT > COMBAT
        switch (attacker.getType()) {
            case COMBAT:
                if (defender.getType() == com.multiverse.pet.model.PetType.GATHERING) {
                    return 1.2;
                }
                if (defender.getType() == com.multiverse.pet.model.PetType. SUPPORT) {
                    return 0.8;
                }
                break;
            case GATHERING:
                if (defender.getType() == com.multiverse.pet.model.PetType. SUPPORT) {
                    return 1.2;
                }
                if (defender.getType() == com.multiverse.pet.model.PetType. COMBAT) {
                    return 0.8;
                }
                break;
            case SUPPORT:
                if (defender.getType() == com.multiverse.pet.model. PetType. COMBAT) {
                    return 1.2;
                }
                if (defender.getType() == com.multiverse. pet.model.PetType.GATHERING) {
                    return 0.8;
                }
                break;
        }

        return 1.0;
    }

    /**
     * 치명타 결과 클래스
     */
    public static class CriticalResult {
        public final boolean isCritical;
        public final double multiplier;

        public CriticalResult(boolean isCritical, double multiplier) {
            this. isCritical = isCritical;
            this.multiplier = multiplier;
        }
    }

    /**
     * 데미지 결과 클래스
     */
    public static class DamageResult {
        public final double damage;
        public final boolean isCritical;
        public final double critMultiplier;
        public final double typeMultiplier;

        public DamageResult(double damage, boolean isCritical, 
                           double critMultiplier, double typeMultiplier) {
            this. damage = damage;
            this.isCritical = isCritical;
            this.critMultiplier = critMultiplier;
            this.typeMultiplier = typeMultiplier;
        }

        public String getDisplayString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%. 1f", damage));
            if (isCritical) {
                sb.append(" §6(치명타!)");
            }
            if (typeMultiplier > 1.0) {
                sb.append(" §a(효과적!)");
            } else if (typeMultiplier < 1.0) {
                sb. append(" §c(별로...)");
            }
            return sb.toString();
        }
    }
}