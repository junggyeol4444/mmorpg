package com.multiverse.combat. effects;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org. bukkit. Bukkit;
import com.multiverse.combat.CombatCore;
import com.multiverse.combat. models.StatusEffect;
import com.multiverse.combat.models.enums.StatusEffectType;
import java.util.*;

/**
 * 상태이상 틱 처리 클래스
 * 상태이상의 시간 경과 및 효과를 처리합니다. 
 */
public class StatusEffectTicker {
    
    private final CombatCore plugin;
    
    /**
     * StatusEffectTicker 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public StatusEffectTicker(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 모든 플레이어의 상태이상 틱 처리
     */
    public void tickAllPlayers() {
        for (Player player : Bukkit. getOnlinePlayers()) {
            updatePlayerEffects(player);
        }
    }
    
    /**
     * 플레이어 상태이상 업데이트
     * @param player 플레이어
     */
    public void updatePlayerEffects(Player player) {
        List<StatusEffect> effects = plugin.getStatusEffectManager().getEffects(player);
        List<StatusEffect> toRemove = new ArrayList<>();
        
        for (StatusEffect effect : effects) {
            // 효과 틱 처리
            tickEffect(player, effect);
            
            // 지속 시간 확인
            long elapsed = System.currentTimeMillis() - effect.getStartTime();
            if (elapsed > effect.getDuration()) {
                toRemove.add(effect);
            }
        }
        
        // 만료된 효과 제거
        for (StatusEffect effect : toRemove) {
            plugin. getStatusEffectManager().removeEffect(player, effect. getType());
        }
    }
    
    /**
     * 상태이상 틱 처리
     * @param target 대상
     * @param effect 상태이상
     */
    private void tickEffect(LivingEntity target, StatusEffect effect) {
        long elapsed = System.currentTimeMillis() - effect.getStartTime();
        int tickCount = (int) (elapsed / effect.getTickInterval());
        
        // 첫 번째 틱만 처리
        if (tickCount > 0 && effect.getLastTickCount() < tickCount) {
            effect. setLastTickCount(tickCount);
            
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
        double damagePerTick = 2.0 * effect.getLevel();
        target.damage(damagePerTick);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage(plugin. getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c독 데미지: " + String.format("%.1f", damagePerTick));
        }
    }
    
    private void applyBurn(LivingEntity target, StatusEffect effect) {
        double damagePerTick = 3.0 * effect.getLevel();
        target.damage(damagePerTick);
        target.setFireTicks(100);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage(plugin.getCombatConfig().getString("messages. prefix", "[전투] ") +
                "§c화상 데미지: " + String.format("%.1f", damagePerTick));
        }
    }
    
    private void applyBleed(LivingEntity target, StatusEffect effect) {
        double damagePerTick = 2.5 * effect.getLevel();
        target.damage(damagePerTick);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage(plugin.getCombatConfig(). getString("messages.prefix", "[전투] ") +
                "§c출혈 데미지: " + String.format("%.1f", damagePerTick));
        }
    }
    
    private void applySlow(LivingEntity target, StatusEffect effect) {
        if (target instanceof Player) {
            Player player = (Player) target;
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org. bukkit.potion.PotionEffectType.SLOW,
                20,
                Math.max(0, effect.getLevel() - 1),
                false,
                false
            ));
        }
    }
    
    private void applyStun(LivingEntity target, StatusEffect effect) {
        if (target instanceof Player) {
            Player player = (Player) target;
            player.addPotionEffect(new org.bukkit.potion. PotionEffect(
                org.bukkit.potion.PotionEffectType.BLINDNESS,
                5,
                0,
                false,
                false
            ));
        }
    }
    
    private void applyFreeze(LivingEntity target, StatusEffect effect) {
        target.setVelocity(target.getVelocity().multiply(0));
        
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§b빙결 상태입니다!");
        }
    }
    
    private void applySilence(LivingEntity target, StatusEffect effect) {
        // 스킬 사용 불가 (별도 구현 필요)
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage(plugin.getCombatConfig().getString("messages. prefix", "[전투] ") +
                "§c침묵 상태로 스킬을 사용할 수 없습니다!");
        }
    }
    
    private void applyWeakness(LivingEntity target, StatusEffect effect) {
        if (target instanceof Player) {
            Player player = (Player) target;
            player.addPotionEffect(new org.bukkit. potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.WEAKNESS,
                20,
                Math.max(0, effect.getLevel() - 1),
                false,
                false
            ));
        }
    }
    
    private void applyBlind(LivingEntity target, StatusEffect effect) {
        if (target instanceof Player) {
            Player player = (Player) target;
            player.addPotionEffect(new org.bukkit. potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.BLINDNESS,
                20,
                0,
                false,
                false
            ));
        }
    }
    
    private void applyRoot(LivingEntity target, StatusEffect effect) {
        target.setVelocity(target.getVelocity().multiply(0));
        
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c근원 상태로 이동할 수 없습니다!");
        }
    }
    
    private void applyDisarm(LivingEntity target, StatusEffect effect) {
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c무장해제 상태로 공격할 수 없습니다!");
        }
    }
}