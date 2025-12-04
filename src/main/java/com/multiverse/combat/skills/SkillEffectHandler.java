package com.multiverse.combat.skills;

import org.bukkit. entity.Player;
import org.bukkit. entity.LivingEntity;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models.Skill;
import com. multiverse.combat.models.enums.EffectType;

/**
 * 스킬 효과 처리 클래스
 * 스킬의 효과를 적용합니다.
 */
public class SkillEffectHandler {
    
    private final CombatCore plugin;
    
    /**
     * SkillEffectHandler 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public SkillEffectHandler(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 스킬 효과 처리
     * @param player 플레이어
     * @param skill 스킬
     * @param target 대상
     */
    public void handle(Player player, Skill skill, LivingEntity target) {
        if (skill.getSkillEffect() == null) {
            return;
        }
        
        EffectType type = skill.getSkillEffect(). getType();
        
        switch (type) {
            case DAMAGE:
                handleDamage(player, skill, target);
                break;
            case HEAL:
                handleHeal(player, skill);
                break;
            case BUFF:
                handleBuff(player, skill);
                break;
            case DEBUFF:
                handleDebuff(player, skill, target);
                break;
            case TELEPORT:
                handleTeleport(player, skill);
                break;
            case SUMMON:
                handleSummon(player, skill);
                break;
            case CROWD_CONTROL:
                handleCrowdControl(player, skill, target);
                break;
        }
    }
    
    /**
     * 데미지 효과 처리
     */
    private void handleDamage(Player player, Skill skill, LivingEntity target) {
        if (target == null) {
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c대상을 찾을 수 없습니다.");
            return;
        }
        
        double baseDamage = skill.getSkillEffect().getBaseDamage();
        int skillLevel = plugin.getSkillManager().getSkillLevel(player, skill.getSkillId());
        
        // 레벨 계수
        double scaledDamage = baseDamage * (1. 0 + (skillLevel - 1) * 0.1);
        
        // 최종 데미지 계산
        double finalDamage = plugin.getDamageCalculator().getFinalDamage(player, target,
            scaledDamage, skill.getSkillEffect().getDamageType(), true);
        
        // 데미지 적용
        target.damage(finalDamage);
        
        // 통계 기록
        plugin.getCombatDataManager().addDamageDealt(player, finalDamage);
        
        // 메시지
        player.sendMessage(plugin. getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a" + String.format("%.0f", finalDamage) + "§a의 데미지를 입혔습니다!");
    }
    
    /**
     * 치유 효과 처리
     */
    private void handleHeal(Player player, Skill skill) {
        double baseHealing = skill.getSkillEffect().getBaseDamage();
        int skillLevel = plugin.getSkillManager().getSkillLevel(player, skill.getSkillId());
        
        double scaledHealing = baseHealing * (1.0 + (skillLevel - 1) * 0.1);
        
        double currentHealth = player.getHealth();
        double maxHealth = player.getMaxHealth();
        double newHealth = Math.min(currentHealth + scaledHealing, maxHealth);
        
        player.setHealth(newHealth);
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a" + String.format("%.0f", scaledHealing) + "§a의 생명력을 회복했습니다!");
    }
    
    /**
     * 버프 효과 처리
     */
    private void handleBuff(Player player, Skill skill) {
        int skillLevel = plugin.getSkillManager().getSkillLevel(player, skill.getSkillId());
        
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a" + skill.getName() + "§a 버프가 적용되었습니다!  (Lv." + skillLevel + ")");
        
        // 버프 효과 추후 구현
        if (skill.getSkillEffect(). getDuration() > 0) {
            org.bukkit. Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                player.sendMessage(plugin.getCombatConfig().getString("messages. prefix", "[전투] ") +
                    "§c" + skill.getName() + "§c 버프가 해제되었습니다.");
            }, skill.getSkillEffect().getDuration() / 50);
        }
    }
    
    /**
     * 디버프 효과 처리
     */
    private void handleDebuff(Player player, Skill skill, LivingEntity target) {
        if (target == null) return;
        
        int skillLevel = plugin.getSkillManager().getSkillLevel(player, skill.getSkillId());
        
        target.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§c" + skill.getName() + "§c에 걸렸습니다!");
    }
    
    /**
     * 텔레포트 효과 처리
     */
    private void handleTeleport(Player player, Skill skill) {
        double range = skill.getSkillEffect(). getRange();
        org.bukkit.util.Vector direction = player.getLocation().getDirection();
        org.bukkit.Location newLocation = player.getLocation().add(direction. multiply(range));
        
        player.teleport(newLocation);
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a텔레포트했습니다!");
    }
    
    /**
     * 소환 효과 처리
     */
    private void handleSummon(Player player, Skill skill) {
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a소환 스킬이 준비 중입니다.");
    }
    
    /**
     * 군중 제어 효과 처리
     */
    private void handleCrowdControl(Player player, Skill skill, LivingEntity target) {
        if (target == null) return;
        
        int skillLevel = plugin. getSkillManager().getSkillLevel(player, skill.getSkillId());
        
        target.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§c군중 제어 상태에 빠졌습니다!");
    }
}