package com.multiverse.combat. effects;

import org.bukkit. entity.LivingEntity;
import org.bukkit.entity.Player;
import com.multiverse.combat.CombatCore;
import com.multiverse.combat. models.StatusEffect;
import com.multiverse.combat. models.enums.StatusEffectType;

/**
 * DoT(지속 데미지) 처리 클래스
 * 독, 화상, 출혈 등의 지속 데미지를 관리합니다.
 */
public class DamageOverTime {
    
    private final CombatCore plugin;
    
    /**
     * DamageOverTime 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public DamageOverTime(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * DoT 적용
     * @param target 대상
     * @param type 상태이상 타입
     * @param level 레벨
     * @param duration 지속 시간 (밀리초)
     */
    public void applyDoT(LivingEntity target, StatusEffectType type, int level, long duration) {
        StatusEffect effect = new StatusEffect(type, level, duration);
        effect.setTickInterval(500);  // 500ms마다 틱
        
        plugin.getStatusEffectManager().applyEffect(target, effect);
    }
    
    /**
     * DoT 데미지 계산
     * @param type 상태이상 타입
     * @param level 레벨
     * @return 틱당 데미지
     */
    public double calculateDotDamage(StatusEffectType type, int level) {
        switch (type) {
            case POISON:
                return 2. 0 * level;
            case BURN:
                return 3.0 * level;
            case BLEED:
                return 2.5 * level;
            default:
                return 0. 0;
        }
    }
    
    /**
     * DoT 효율 (방어력으로 감소 여부)
     * @param type 상태이상 타입
     * @return 방어력으로 감소하면 true
     */
    public boolean isDefenseReducible(StatusEffectType type) {
        switch (type) {
            case POISON:
            case BLEED:
                return true;  // 방어력으로 감소
            case BURN:
                return false;  // 방어력 무시
            default:
                return false;
        }
    }
    
    /**
     * DoT 시각 효과
     * @param target 대상
     * @param type 상태이상 타입
     */
    public void applyVisualEffect(LivingEntity target, StatusEffectType type) {
        if (target instanceof Player) {
            Player player = (Player) target;
            
            switch (type) {
                case POISON:
                    player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                        "§a독 상태입니다!  §f(지속 데미지)");
                    break;
                case BURN:
                    player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                        "§c화상 상태입니다! §f(지속 데미지)");
                    break;
                case BLEED:
                    player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                        "§c출혈 상태입니다! §f(지속 데미지)");
                    break;
            }
        }
    }
    
    /**
     * DoT 제거
     * @param target 대상
     * @param type 상태이상 타입
     */
    public void removeDoT(LivingEntity target, StatusEffectType type) {
        plugin.getStatusEffectManager().removeEffect(target, type);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§a" + type.getDisplayName() + "§a 상태가 해제되었습니다!");
        }
    }
    
    /**
     * 전체 DoT 스택 데미지 계산
     * @param target 대상
     * @return 전체 DoT 데미지
     */
    public double getTotalDotDamage(LivingEntity target) {
        double totalDamage = 0.0;
        
        for (StatusEffect effect : plugin.getStatusEffectManager().getEffects(target)) {
            if (isDotEffect(effect. getType())) {
                totalDamage += calculateDotDamage(effect.getType(), effect.getLevel());
            }
        }
        
        return totalDamage;
    }
    
    /**
     * DoT 효과인지 확인
     * @param type 상태이상 타입
     * @return DoT 효과면 true
     */
    private boolean isDotEffect(StatusEffectType type) {
        return type == StatusEffectType. POISON || 
               type == StatusEffectType.BURN || 
               type == StatusEffectType.BLEED;
    }
}