package com.multiverse.combat.  listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org. bukkit.event.  EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit. event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org. bukkit.event.  player.PlayerMoveEvent;
import com.multiverse.combat.CombatCore;
import com.multiverse.combat. models.enums.DamageType;
import com.multiverse.combat. events.CustomDamageEvent;

/**
 * 전투 리스너 클래스
 * 기본 전투 이벤트를 처리합니다.
 */
public class CombatListener implements Listener {
    
    private final CombatCore plugin;
    
    /**
     * CombatListener 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public CombatListener(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 엔티티 피해 이벤트 (공격 시)
     */
    @EventHandler(priority = EventPriority.  HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        
        Player attacker = (Player) event.getDamager();
        LivingEntity target = (LivingEntity) event.getEntity();
        
        // 바닐라 데미지 무시
        event.setDamage(0);
        
        // 회피 체크
        if (target instanceof Player) {
            Player targetPlayer = (Player) target;
            if (plugin.getDamageCalculator().isDodged(targetPlayer)) {
                targetPlayer.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                    plugin.getCombatConfig().getString("messages.combat.dodge", "§a&l회피!"));
                
                // 회피 통계 기록
                plugin.getCombatDataManager().addDodge(targetPlayer);
                
                return;
            }
        }
        
        // 기본 데미지 계산
        double baseDamage = 10.0;  // 임시 기본값
        
        // 크리티컬 체크
        boolean isCritical = plugin.getDamageCalculator().isCritical(attacker);
        if (isCritical) {
            baseDamage = plugin.getDamageCalculator().applyCritical(baseDamage, attacker);
            attacker.sendMessage(plugin.getCombatConfig().getString("messages. prefix", "[전투] ") +
                plugin.getCombatConfig().getString("messages.combat. critical", "§6&l크리티컬! "));
        }
        
        // 최종 데미지 계산
        double finalDamage = plugin.getDamageCalculator().getFinalDamage(attacker, target, 
                                                                         baseDamage, DamageType.PHYSICAL, true);
        
        // 커스텀 이벤트 발생
        CustomDamageEvent damageEvent = new CustomDamageEvent(attacker, target, finalDamage, DamageType.PHYSICAL);
        damageEvent.setCritical(isCritical);
        org.bukkit. Bukkit.getPluginManager().callEvent(damageEvent);
        
        if (damageEvent.isCancelled()) {
            return;
        }
        
        // 실제 데미지 적용
        target.damage(damageEvent.getDamage());
        
        // 데미지 표시
        if (plugin.getCombatConfig().getBoolean("damage-indicators. enabled", true)) {
            plugin.getLogger().info("데미지: " + String.format("%. 2f", damageEvent.getDamage()));
        }
        
        // 콤보 증가
        if (attacker instanceof Player) {
            plugin.getComboManager().addCombo(attacker, 1);
            plugin.getCombatDataManager().addDamageDealt(attacker, damageEvent.getDamage());
        }
        
        // 내구도 감소
        plugin.getDurabilityManager().damageWeapon(attacker);
        if (target instanceof Player) {
            plugin. getDurabilityManager().damageArmor((Player) target, damageEvent.getDamage());
        }
        
        // PvP 처리
        if (attacker instanceof Player && target instanceof Player) {
            Player targetPlayer = (Player) target;
            
            // PvP 활성화 확인
            if (! plugin.getPvPManager().canAttack(attacker, targetPlayer)) {
                attacker.sendMessage(plugin.getCombatConfig().getString("messages. prefix", "[전투] ") +
                    "§c이 플레이어와 전투할 수 없습니다.");
                return;
            }
        }
    }
    
    /**
     * 엔티티 피해 이벤트 (피해 입을 때)
     */
    @EventHandler(priority = EventPriority. MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event. getEntity() instanceof Player)) return;
        if (event.isCancelled()) return;
        
        Player player = (Player) event.getEntity();
        double damage = event.getFinalDamage();
        
        // 피해 통계 기록
        plugin. getCombatDataManager().addDamageTaken(player, damage);
        
        // 콤보 감소
        plugin.getComboManager().onPlayerDamaged(player);
        
        // 상태이상 업데이트
        plugin.getStatusEffectManager().updateEffects(player);
    }
    
    /**
     * 플레이어 이동 이벤트
     * 상태이상(빙결, 근원) 체크
     */
    @EventHandler(priority = EventPriority.  NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // 빙결 상태 확인
        if (plugin.getStatusEffectManager().hasEffect(player, 
            com.multiverse.combat.models. enums.StatusEffectType.FREEZE)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c빙결 상태로 움직일 수 없습니다!");
            return;
        }
        
        // 근원 상태 확인
        if (plugin.getStatusEffectManager().hasEffect(player, 
            com.multiverse.combat. models.enums.StatusEffectType.ROOT)) {
            event.setCancelled(true);
            return;
        }
        
        // 콤보 타임아웃 체크
        plugin.getComboManager().checkComboTimeout(player);
    }
    
    /**
     * 플레이어 사망 이벤트
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        Player victim = event.getEntity();
        LivingEntity killer = victim.getKiller();
        
        // 통계 기록
        plugin. getCombatDataManager().addDeath(victim);
        plugin.getComboManager().resetCombo(victim);
        plugin.getPvPManager().onPlayerDeath(victim);
        
        // 처치 처리
        if (killer instanceof Player) {
            Player killerPlayer = (Player) killer;
            
            plugin.getCombatDataManager().addKill(killerPlayer);
            plugin.getPvPManager().onPlayerKill(killerPlayer, victim);
            
            // 데이터 저장
            plugin.getSkillManager().savePlayerData(killerPlayer);
            plugin.getComboManager().savePlayerData(killerPlayer);
            plugin.getPvPManager().savePlayerData(killerPlayer);
        }
        
        // 피해자 데이터 저장
        plugin.getSkillManager().savePlayerData(victim);
        plugin.getComboManager(). savePlayerData(victim);
        plugin.getPvPManager().savePlayerData(victim);
    }
    
    /**
     * 플레이어 생명력 회복 이벤트
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerHealing(org.bukkit.event.entity.EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        
        // 회피 상태 유지
        plugin.getComboManager().onPlayerDodged(player);
    }
    
    /**
     * 엔티티 버프 이벤트
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityPotionEffectAdd(org.bukkit.event. entity.PotionSplashEvent event) {
        // 상태이상 처리는 별도의 효과 시스템에서 관리
    }
}