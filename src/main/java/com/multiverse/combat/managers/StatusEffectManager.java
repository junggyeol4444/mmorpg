package com.multiverse.combat.managers;

import org.bukkit.entity.LivingEntity;
import org. bukkit.entity.Player;
import org.bukkit. Bukkit;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models.StatusEffect;
import com. multiverse.combat.models.enums.StatusEffectType;
import java.util.*;

/**
 * 상태이상 관리 클래스
 * 플레이어/엔티티의 상태이상 적용, 제거, 업데이트를 관리합니다.
 */
public class StatusEffectManager {
    
    private final CombatCore plugin;
    private final Map<UUID, List<StatusEffect>> entityEffects = new HashMap<>();
    private final Map<UUID, Set<StatusEffectType>> entityImmunities = new HashMap<>();
    
    /**
     * StatusEffectManager 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public StatusEffectManager(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 상태이상 적용
     * @param target 대상 엔티티
     * @param effect 적용할 상태이상
     */
    public void applyEffect(LivingEntity target, StatusEffect effect) {
        UUID targetUUID = target.getUniqueId();
        
        // 면역 확인
        if (isImmune(target, effect. getType())) {
            if (target instanceof Player) {
                Player player = (Player) target;
                player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                    "§e" + effect.getType().getDisplayName() + "§f에 면역입니다!");
            }
            return;
        }
        
        // 효과 리스트 초기화
        entityEffects.computeIfAbsent(targetUUID, k -> new ArrayList<>());
        List<StatusEffect> effects = entityEffects.get(targetUUID);
        
        // 중복 효과 처리
        StatusEffect existing = getEffect(target, effect.getType());
        if (existing != null) {
            if (effect.isStackable()) {
                // 스택 증가
                existing.addStack();
                if (existing.getCurrentStacks() > effect.getMaxStacks()) {
                    existing.setCurrentStacks(effect.getMaxStacks());
                }
            } else {
                // 기존 효과 제거 후 새 효과 적용
                effects.remove(existing);
            }
        }
        
        // 효과 추가
        effect.setStartTime(System.currentTimeMillis());
        effects.add(effect);
        
        // 알림
        if (target instanceof Player) {
            Player player = (Player) target;
            String message = plugin.getCombatConfig().getString("messages.status.applied", "§c{effect}§f 상태이상에 걸렸습니다!")
                . replace("{effect}", effect.getType().getDisplayName());
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") + message);
        }
    }
    
    /**
     * 상태이상 제거
     * @param target 대상 엔티티
     * @param effectId 제거할 상태이상 타입
     */
    public void removeEffect(LivingEntity target, StatusEffectType effectId) {
        UUID targetUUID = target.getUniqueId();
        List<StatusEffect> effects = entityEffects.getOrDefault(targetUUID, new ArrayList<>());
        
        effects.removeIf(effect -> effect.getType() == effectId);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            String message = plugin.getCombatConfig().getString("messages.status.removed", "§a{effect}§f 상태이상이 해제되었습니다!")
                .replace("{effect}", effectId.getDisplayName());
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") + message);
        }
    }
    
    /**
     * 모든 상태이상 제거
     * @param target 대상 엔티티
     */
    public void clearAllEffects(LivingEntity target) {
        UUID targetUUID = target.getUniqueId();
        entityEffects.remove(targetUUID);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§a모든 상태이상이 제거되었습니다!");
        }
    }
    
    /**
     * 대상의 모든 상태이상 조회
     * @param target 대상 엔티티
     * @return 상태이상 리스트
     */
    public List<StatusEffect> getEffects(LivingEntity target) {
        return entityEffects.getOrDefault(target.getUniqueId(), new ArrayList<>());
    }
    
    /**
     * 특정 타입의 상태이상 여부 확인
     * @param target 대상 엔티티
     * @param type 상태이상 타입
     * @return 해당 상태이상이 있으면 true
     */
    public boolean hasEffect(LivingEntity target, StatusEffectType type) {
        return getEffect(target, type) != null;
    }
    
    /**
     * 특정 타입의 상태이상 조회
     * @param target 대상 엔티티
     * @param type 상태이상 타입
     * @return 상태이상 객체, 없으면 null
     */
    public StatusEffect getEffect(LivingEntity target, StatusEffectType type) {
        List<StatusEffect> effects = getEffects(target);
        return effects.stream()
            .filter(effect -> effect.getType() == type)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 모든 상태이상 업데이트 (틱 처리)
     * @param target 대상 엔티티
     */
    public void updateEffects(LivingEntity target) {
        UUID targetUUID = target.getUniqueId();
        List<StatusEffect> effects = entityEffects.getOrDefault(targetUUID, new ArrayList<>());
        List<StatusEffect> toRemove = new ArrayList<>();
        
        for (StatusEffect effect : effects) {
            // 효과 틱 처리
            tickEffect(target, effect);
            
            // 지속 시간 체크
            long elapsed = System.currentTimeMillis() - effect.getStartTime();
            if (elapsed > effect.getDuration()) {
                toRemove.add(effect);
            }
        }
        
        // 만료된 효과 제거
        for (StatusEffect effect : toRemove) {
            effects.remove(effect);
            removeEffect(target, effect.getType());
        }
    }
    
    /**
     * 상태이상 틱 처리
     * @param target 대상 엔티티
     * @param effect 상태이상
     */
    public void tickEffect(LivingEntity target, StatusEffect effect) {
        long elapsed = System.currentTimeMillis() - effect.getStartTime();
        int tickCount = (int) (elapsed / effect.getTickInterval());
        
        // 첫 번째 틱만 처리 (중복 방지)
        if (tickCount > 0 && effect.getLastTickCount() < tickCount) {
            effect. setLastTickCount(tickCount);
            
            // 효과 타입별 처리
            switch (effect.getType()) {
                case POISON:
                    applyPoison(target, effect);
                    break;
                case BURN:
                    applyBurn(target, effect);
                    break;
                case BLEED:
                    applyBleed(target, effect);
                    break;
                case SLOW:
                    applySlow(target, effect);
                    break;
                case STUN:
                    applyStun(target, effect);
                    break;
                case FREEZE:
                    applyFreeze(target, effect);
                    break;
                case SILENCE:
                    applySilence(target, effect);
                    break;
                case WEAKNESS:
                    applyWeakness(target, effect);
                    break;
                case BLIND:
                    applyBlind(target, effect);
                    break;
                case ROOT:
                    applyRoot(target, effect);
                    break;
                case DISARM:
                    applyDisarm(target, effect);
                    break;
            }
        }
    }
    
    private void applyPoison(LivingEntity target, StatusEffect effect) {
        double damage = 2.0 * effect.getLevel();
        target.damage(damage);
    }
    
    private void applyBurn(LivingEntity target, StatusEffect effect) {
        double damage = 3.0 * effect.getLevel();
        target.damage(damage);
        target.setFireTicks(100);
    }
    
    private void applyBleed(LivingEntity target, StatusEffect effect) {
        double damage = 2.5 * effect.getLevel();
        target.damage(damage);
    }
    
    private void applySlow(LivingEntity target, StatusEffect effect) {
        if (target instanceof Player) {
            Player player = (Player) target;
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org. bukkit.potion.PotionEffectType.SLOW,
                20,
                effect.getLevel() - 1
            ));
        }
    }
    
    private void applyStun(LivingEntity target, StatusEffect effect) {
        if (target instanceof Player) {
            Player player = (Player) target;
            player.addPotionEffect(new org.bukkit. potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.BLINDNESS,
                5,
                0
            ));
        }
    }
    
    private void applyFreeze(LivingEntity target, StatusEffect effect) {
        target.setVelocity(target.getVelocity().multiply(0));
    }
    
    private void applySilence(LivingEntity target, StatusEffect effect) {
        // 스킬 사용 불가 (별도 구현 필요)
    }
    
    private void applyWeakness(LivingEntity target, StatusEffect effect) {
        if (target instanceof Player) {
            Player player = (Player) target;
            player.addPotionEffect(new org.bukkit. potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.WEAKNESS,
                20,
                effect.getLevel() - 1
            ));
        }
    }
    
    private void applyBlind(LivingEntity target, StatusEffect effect) {
        if (target instanceof Player) {
            Player player = (Player) target;
            player.addPotionEffect(new org.bukkit. potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.BLINDNESS,
                20,
                0
            ));
        }
    }
    
    private void applyRoot(LivingEntity target, StatusEffect effect) {
        target.setVelocity(target.getVelocity().multiply(0));
    }
    
    private void applyDisarm(LivingEntity target, StatusEffect effect) {
        // 무기 제거 (별도 구현 필요)
    }
    
    /**
     * 면역 추가
     * @param target 대상 엔티티
     * @param type 면역할 상태이상 타입
     * @param duration 지속 시간 (밀리초)
     */
    public void addImmunity(LivingEntity target, StatusEffectType type, long duration) {
        UUID targetUUID = target.getUniqueId();
        Set<StatusEffectType> immunities = entityImmunities.computeIfAbsent(targetUUID, k -> new HashSet<>());
        immunities.add(type);
        
        // 일정 시간 후 면역 제거
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            immunities.remove(type);
        }, duration / 50);  // 밀리초를 틱으로 변환
    }
    
    /**
     * 면역 여부 확인
     * @param target 대상 엔티티
     * @param type 상태이상 타입
     * @return 면역이면 true
     */
    public boolean isImmune(LivingEntity target, StatusEffectType type) {
        Set<StatusEffectType> immunities = entityImmunities. getOrDefault(target.getUniqueId(), new HashSet<>());
        return immunities.contains(type);
    }
}