package com.multiverse.combat. api;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models. Skill;
import com.multiverse. combat.models.StatusEffect;
import com.multiverse.combat.models.enums. DamageType;
import com. multiverse.combat.models.enums.StatusEffectType;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class CombatAPI {
    
    private static CombatCore instance;
    private static final Map<UUID, Long> skillCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> playerCombos = new HashMap<>();
    
    public static void init(CombatCore plugin) {
        instance = plugin;
    }
    
    public static boolean useSkill(Player player, String skillId, LivingEntity target) {
        if (instance == null) return false;
        if (player == null || skillId == null) return false;
        
        Skill skill = instance.getSkillManager().getSkill(skillId);
        if (skill == null) {
            player.sendMessage("§c스킬을 찾을 수 없습니다.");
            return false;
        }
        
        if (!instance.getSkillManager().hasSkill(player, skillId)) {
            player.sendMessage("§c배우지 않은 스킬입니다.");
            return false;
        }
        
        UUID playerUuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long lastUseTime = skillCooldowns. getOrDefault(playerUuid, 0L);
        
        if (currentTime - lastUseTime < skill.getBaseCooldown()) {
            long remainingTime = (skill.getBaseCooldown() - (currentTime - lastUseTime)) / 1000;
            player.sendMessage("§c쿨다운 중...  (" + remainingTime + "초)");
            return false;
        }
        
        if (! instance.getSkillManager().hasEnoughCost(player, skill)) {
            player.sendMessage("§c" + skill.getCostType(). getDisplayName() + "이(가) 부족합니다.");
            return false;
        }
        
        instance.getSkillManager().consumeCost(player, skill);
        skillCooldowns.put(playerUuid, currentTime);
        
        if (skill.getSkillEffect() != null) {
            instance.getSkillManager().executeSkillEffect(player, skill, target);
        }
        
        return true;
    }
    
    public static void dealDamage(Player attacker, LivingEntity target, double damage, DamageType type) {
        if (instance == null || attacker == null || target == null) return;
        
        double finalDamage = instance.getDamageCalculator().calculateDamage(
            attacker,
            target,
            damage,
            type
        );
        
        target.damage(finalDamage);
        instance.getCombatDataManager().addDamageDealt(attacker, finalDamage);
        
        boolean isCritical = Math.random() < instance.getDamageCalculator().getCriticalChance(attacker);
        if (isCritical) {
            finalDamage *= instance.getDamageCalculator().getCriticalMultiplier(attacker);
            target.damage(finalDamage);
            instance.indicators.DamageIndicator.showDamage(target, finalDamage, true);
        } else {
            instance.indicators.DamageIndicator.showDamage(target, finalDamage, false);
        }
    }
    
    public static void applyStatusEffect(LivingEntity target, StatusEffectType type, int level, long duration) {
        if (instance == null || target == null) return;
        
        StatusEffect effect = new StatusEffect(type, level, duration);
        effect.setStartTime(System.currentTimeMillis());
        effect.setTickInterval(500);
        
        instance.getStatusEffectManager().applyEffect(target, effect);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage("§c" + type.getDisplayName() + " 상태이상에 걸렸습니다!");
        }
    }
    
    public static void removeStatusEffect(LivingEntity target, StatusEffectType type) {
        if (instance == null || target == null) return;
        
        instance. getStatusEffectManager().removeEffect(target, type);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage("§a" + type.getDisplayName() + " 상태이상이 해제되었습니다!");
        }
    }
    
    public static void addCombo(Player player, int amount) {
        if (instance == null || player == null) return;
        
        UUID playerUuid = player.getUniqueId();
        int currentCombo = playerCombos. getOrDefault(playerUuid, 0);
        int newCombo = currentCombo + amount;
        
        playerCombos.put(playerUuid, newCombo);
        instance.getComboManager().addCombo(player, amount);
        
        if (newCombo % 5 == 0) {
            player.sendMessage("§e콤보 ×" + newCombo);
        }
    }
    
    public static int getCombo(Player player) {
        if (instance == null || player == null) return 0;
        return playerCombos.getOrDefault(player.getUniqueId(), 0);
    }
    
    public static void resetCombo(Player player) {
        if (player == null) return;
        playerCombos.put(player.getUniqueId(), 0);
    }
    
    public static boolean isPvPEnabled(Player player) {
        if (instance == null || player == null) return false;
        return instance.getPvPManager().isPvPEnabled(player);
    }
    
    public static void setPvPEnabled(Player player, boolean enabled) {
        if (instance == null || player == null) return;
        instance.getPvPManager().setPvPEnabled(player, enabled);
    }
    
    public static int getPlayerKills(Player player) {
        if (instance == null || player == null) return 0;
        return instance.getCombatDataManager().getTotalKills(player);
    }
    
    public static int getPlayerDeaths(Player player) {
        if (instance == null || player == null) return 0;
        return instance.getCombatDataManager().getTotalDeaths(player);
    }
    
    public static double getKDA(Player player) {
        if (instance == null || player == null) return 0.0;
        int kills = getPlayerKills(player);
        int deaths = getPlayerDeaths(player);
        return deaths == 0 ? kills : (double) kills / deaths;
    }
    
    public static CombatCore getInstance() {
        return instance;
    }
    
    public static boolean isInitialized() {
        return instance != null;
    }
}