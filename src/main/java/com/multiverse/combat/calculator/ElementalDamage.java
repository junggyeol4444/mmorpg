package com.multiverse.combat. calculator;

import org.bukkit.entity.LivingEntity;
import com.multiverse.combat.CombatCore;
import com.multiverse.combat. models.  enums.ElementType;
import com.multiverse.combat. models.StatusEffect;
import com.multiverse.combat. models.enums.StatusEffectType;

/**
 * 속성 데미지 계산 클래스
 * 속성 상성, 속성 저항, 속성 효과를 처리합니다.
 */
public class ElementalDamage {
    
    private final CombatCore plugin;
    
    /**
     * ElementalDamage 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public ElementalDamage(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 속성 데미지 계산
     * @param baseDamage 기본 데미지
     * @param attackElement 공격 속성
     * @param defenseElement 방어 속성
     * @return 최종 속성 데미지
     */
    public double calculateElementalDamage(double baseDamage, 
                                          ElementType attackElement,
                                          ElementType defenseElement) {
        if (attackElement == null || defenseElement == null || 
            attackElement == ElementType.NEUTRAL) {
            return baseDamage;
        }
        
        // 상성 보너스/페널티
        double multiplier = getElementAdvantage(attackElement, defenseElement);
        
        return baseDamage * multiplier;
    }
    
    /**
     * 속성 상성 계산
     * @param attack 공격 속성
     * @param defense 방어 속성
     * @return 배수 (1.0 = 기본, 1.5 = 유리, 0.75 = 불리)
     */
    public double getElementAdvantage(ElementType attack, ElementType defense) {
        if (attack == null || defense == null) {
            return 1.0;
        }
        
        switch (attack) {
            case FIRE:
                // 화: 얼음에 강함, 물에 약함
                if (defense == ElementType.ICE) return 1.5;
                if (defense == ElementType.WATER) return 0.75;
                break;
                
            case WATER:
                // 물: 화염에 강함, 번개에 약함
                if (defense == ElementType.FIRE) return 1.5;
                if (defense == ElementType. LIGHTNING) return 0.75;
                break;
                
            case WIND:
                // 바람: 대지에 약함
                if (defense == ElementType. EARTH) return 0.75;
                break;
                
            case EARTH:
                // 대지: 화염에 강함, 바람에 약함
                if (defense == ElementType.WIND) return 1.5;
                if (defense == ElementType. FIRE) return 0.75;
                break;
                
            case LIGHTNING:
                // 번개: 물에 강함, 대지에 약함
                if (defense == ElementType.WATER) return 1.5;
                if (defense == ElementType. EARTH) return 0.75;
                break;
                
            case ICE:
                // 얼음: 물에 강함, 화염에 약함
                if (defense == ElementType.WATER) return 1.5;
                if (defense == ElementType. FIRE) return 0.75;
                break;
                
            case LIGHT:
                // 빛: 어둠에 강함, 언데드 추가 데미지
                if (defense == ElementType.DARK) return 1.5;
                break;
                
            case DARK:
                // 어둠: 빛에 약함
                if (defense == ElementType. LIGHT) return 0.75;
                break;
                
            case NEUTRAL:
            default:
                return 1.0;
        }
        
        return 1.0;
    }
    
    /**
     * 속성 저항 계산
     * @param entity 대상 엔티티
     * @param element 속성
     * @return 저항율 (0~1)
     */
    public double getElementalResistance(LivingEntity entity, ElementType element) {
        if (element == null || element == ElementType.NEUTRAL) {
            return 0.0;
        }
        
        // 기본 저항값 (추후 장비 인챈트에서 추가)
        double baseResistance = 0.0;
        
        // 상태이상으로 인한 저항 변화
        if (plugin.getStatusEffectManager().hasEffect(entity, StatusEffectType. BURN)) {
            if (element == ElementType.FIRE) {
                baseResistance += 0.2;  // 화상 상태: 불속성 저항 +20%
            }
        }
        
        if (plugin.getStatusEffectManager().hasEffect(entity, StatusEffectType.FREEZE)) {
            if (element == ElementType.ICE) {
                baseResistance += 0.2;  // 빙결 상태: 얼음속성 저항 +20%
            }
        }
        
        return Math.min(baseResistance, 0.75);  // 최대 75% 저항
    }
    
    /**
     * 속성 효과 적용
     * @param target 대상
     * @param element 속성
     * @param level 레벨
     */
    public void applyElementalEffect(LivingEntity target, ElementType element, int level) {
        if (element == null || element == ElementType. NEUTRAL) {
            return;
        }
        
        switch (element) {
            case FIRE:
                // 화상 효과
                StatusEffect burn = new StatusEffect(StatusEffectType. BURN, level, 5000);
                plugin.getStatusEffectManager().applyEffect(target, burn);
                break;
                
            case LIGHTNING:
                // 기절 효과
                StatusEffect stun = new StatusEffect(StatusEffectType.STUN, level, 2000);
                plugin.getStatusEffectManager().applyEffect(target, stun);
                break;
                
            case ICE:
                // 빙결 효과
                StatusEffect freeze = new StatusEffect(StatusEffectType.FREEZE, level, 4000);
                plugin.getStatusEffectManager().applyEffect(target, freeze);
                break;
                
            case WATER:
                // 둔화 효과
                StatusEffect slow = new StatusEffect(StatusEffectType.SLOW, level, 3000);
                plugin.getStatusEffectManager().applyEffect(target, slow);
                break;
                
            case DARK:
                // 독 효과
                StatusEffect poison = new StatusEffect(StatusEffectType.POISON, level, 6000);
                plugin.getStatusEffectManager().applyEffect(target, poison);
                break;
        }
    }
    
    /**
     * 속성 이름 조회
     * @param element 속성
     * @return 속성 이름
     */
    public String getElementName(ElementType element) {
        if (element == null) {
            return "무속성";
        }
        
        switch (element) {
            case FIRE:
                return "§c화";
            case WATER:
                return "§9물";
            case WIND:
                return "§e바람";
            case EARTH:
                return "§6대지";
            case LIGHTNING:
                return "§b번개";
            case ICE:
                return "§b얼음";
            case LIGHT:
                return "§e빛";
            case DARK:
                return "§4어둠";
            case NEUTRAL:
            default:
                return "무속성";
        }
    }
    
    /**
     * 속성 데미지 검증
     * @param damage 데미지
     * @return 유효한 데미지 (최소 0.1)
     */
    public double validateDamage(double damage) {
        return Math.max(damage, 0.1);
    }
}