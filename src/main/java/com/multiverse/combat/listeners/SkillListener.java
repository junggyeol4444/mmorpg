package com.multiverse.combat.  listeners;

import org.bukkit. entity.Player;
import org. bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org. bukkit.event.Listener;
import org.bukkit. event.player.PlayerInteractEvent;
import org.bukkit. event.  block.Action;
import org.bukkit. inventory.ItemStack;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models.  Skill;
import com.multiverse.combat.events.  SkillUseEvent;

/**
 * 스킬 리스너 클래스
 * 스킬 사용 이벤트를 처리합니다.
 */
public class SkillListener implements Listener {
    
    private final CombatCore plugin;
    
    /**
     * SkillListener 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public SkillListener(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어 상호작용 이벤트 (우클릭 - 스킬 사용)
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event. getPlayer();
        
        if (event.getAction() != Action.RIGHT_CLICK_AIR && 
            event.getAction() != Action.  RIGHT_CLICK_BLOCK) {
            return;
        }
        
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // 아이템 체크
        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            return;
        }
        
        // 핫바 슬롯에서 스킬 조회
        int slot = player.getInventory().getHeldItemSlot();
        String skillId = plugin.getSkillManager().getHotbarSkill(player, slot);
        
        if (skillId == null) {
            return;
        }
        
        event.setCancelled(true);
        
        // 스킬 사용
        useSkill(player, skillId);
    }
    
    /**
     * 스킬 사용 처리
     */
    private void useSkill(Player player, String skillId) {
        Skill skill = plugin.getSkillManager().getSkill(skillId);
        
        if (skill == null) {
            player. sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c스킬을 찾을 수 없습니다.");
            return;
        }
        
        // 사용 가능 확인
        if (!  plugin.getSkillManager().canUseSkill(player, skill)) {
            return;
        }
        
        // 스킬 사용 이벤트 발생
        org.bukkit.entity.LivingEntity target = getTargetEntity(player);
        SkillUseEvent skillEvent = new SkillUseEvent(player, skill, target);
        org.bukkit. Bukkit.getPluginManager().callEvent(skillEvent);
        
        if (skillEvent.isCancelled()) {
            return;
        }
        
        // 비용 소모
        plugin.getSkillManager(). consumeCost(player, skill);
        
        // 쿨다운 설정
        plugin.getSkillManager().setCooldown(player, skill.  getSkillId(), skill.getBaseCooldown());
        
        // 스킬 효과 실행
        executeSkillEffect(player, skill, target);
        
        // 파티클 효과
        if (plugin.getCombatConfig().getBoolean("particles.skills", true)) {
            // 파티클 재생
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§a" + skill.getName() + "§a 스킬을 사용했습니다!");
        }
        
        // 내구도 감소 (스킬)
        plugin.getDurabilityManager().damageWeaponFromSkill(player);
    }
    
    /**
     * 스킬 효과 실행
     */
    private void executeSkillEffect(Player player, Skill skill, org.bukkit.entity.LivingEntity target) {
        if (skill.getSkillEffect() == null) {
            return;
        }
        
        switch (skill.getSkillEffect(). getType()) {
            case DAMAGE:
                executeDamageSkill(player, skill, target);
                break;
            case HEAL:
                executeHealSkill(player, skill);
                break;
            case BUFF:
                executeBuffSkill(player, skill);
                break;
            case DEBUFF:
                executeDebuffSkill(player, skill, target);
                break;
            case TELEPORT:
                executeTeleportSkill(player, skill);
                break;
            case SUMMON:
                executeSummonSkill(player, skill);
                break;
        }
    }
    
    /**
     * 데미지 스킬 실행
     */
    private void executeDamageSkill(Player player, Skill skill, org.bukkit.entity.LivingEntity target) {
        if (target == null) {
            player. sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c대상을 찾을 수 없습니다.");
            return;
        }
        
        double baseDamage = skill.getSkillEffect().getBaseDamage();
        int skillLevel = plugin.getSkillManager().  getSkillLevel(player, skill.getSkillId());
        
        // 레벨에 따른 데미지 증가
        double finalDamage = baseDamage * (1.0 + (skillLevel - 1) * 0.1);
        
        // 데미지 계산
        double calculatedDamage = plugin.getDamageCalculator().getFinalDamage(player, target,
            finalDamage, skill.getSkillEffect().getDamageType(), true);
        
        // 데미지 적용
        target.damage(calculatedDamage);
        
        // 속성 효과 적용
        if (skill.getSkillEffect(). getParameters(). containsKey("element")) {
            String elementStr = skill.getSkillEffect(). getParameters().get("element"). toString();
            com.multiverse.combat.models. enums.ElementType element = 
                com.multiverse.combat.models. enums.ElementType.valueOf(elementStr. toUpperCase());
            plugin.getElementalDamage(). applyElementalEffect(target, element, skillLevel);
        }
        
        // 콤보 증가
        plugin.getComboManager().addCombo(player, 1);
        
        // 통계 기록
        plugin.getCombatDataManager().addDamageDealt(player, calculatedDamage);
    }
    
    /**
     * 힐 스킬 실행
     */
    private void executeHealSkill(Player player, Skill skill) {
        double baseHealing = skill.getSkillEffect().getBaseDamage();
        int skillLevel = plugin.getSkillManager().getSkillLevel(player, skill.getSkillId());
        
        double finalHealing = baseHealing * (1.0 + (skillLevel - 1) * 0. 1);
        
        double currentHealth = player.getHealth();
        double maxHealth = player.getMaxHealth();
        double newHealth = Math.min(currentHealth + finalHealing, maxHealth);
        
        player.setHealth(newHealth);
        player. sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a" + String.format("%. 0f", finalHealing) + "§a의 생명력을 회복했습니다!");
    }
    
    /**
     * 버프 스킬 실행
     */
    private void executeBuffSkill(Player player, Skill skill) {
        int skillLevel = plugin.getSkillManager().getSkillLevel(player, skill.getSkillId());
        long duration = skill.getSkillEffect(). getDuration();
        
        // 버프 효과 적용
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a" + skill.getName() + "§a 버프가 적용되었습니다!");
    }
    
    /**
     * 디버프 스킬 실행
     */
    private void executeDebuffSkill(Player player, Skill skill, org.bukkit. entity.LivingEntity target) {
        if (target == null) return;
        
        int skillLevel = plugin.getSkillManager().getSkillLevel(player, skill.getSkillId());
        
        // 디버프 효과 적용
        if (skill.getSkillEffect(). getParameters().containsKey("status-effect")) {
            String effectStr = skill.getSkillEffect().getParameters(). get("status-effect").toString();
            com.multiverse. combat.  models.enums.StatusEffectType effectType = 
                com.multiverse.combat.models.enums.StatusEffectType.fromString(effectStr);
            
            if (effectType != null) {
                com.multiverse.combat.models.  StatusEffect statusEffect = 
                    new com.multiverse.combat.models.  StatusEffect(effectType, skillLevel, 
                        skill.getSkillEffect().getDuration());
                plugin.getStatusEffectManager().applyEffect(target, statusEffect);
            }
        }
    }
    
    /**
     * 텔레포트 스킬 실행
     */
    private void executeTeleportSkill(Player player, Skill skill) {
        double range = skill.getSkillEffect(). getRange();
        org.bukkit.util.Vector direction = player.getLocation().getDirection();
        org.bukkit.Location newLocation = player.getLocation().add(direction.multiply(range));
        
        player.teleport(newLocation);
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a텔레포트했습니다!");
    }
    
    /**
     * 소환 스킬 실행
     */
    private void executeSummonSkill(Player player, Skill skill) {
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a소환 스킬이 준비 중입니다.");
    }
    
    /**
     * 플레이어의 타겟 엔티티 조회
     */
    private org.bukkit.entity.LivingEntity getTargetEntity(Player player) {
        org.bukkit. entity.LivingEntity target = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (org.bukkit.entity.  Entity entity : player.getNearbyEntities(30, 30, 30)) {
            if (entity instanceof org.bukkit.entity.LivingEntity) {
                double distance = player.getLocation().distance(entity.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    target = (org.bukkit.entity.LivingEntity) entity;
                }
            }
        }
        
        return target;
    }
}