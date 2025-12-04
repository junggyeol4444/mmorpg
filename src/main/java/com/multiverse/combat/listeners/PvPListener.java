package com.multiverse.combat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org. bukkit.event.EventPriority;
import org. bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event. player.PlayerQuitEvent;
import org.bukkit.event. entity.PlayerDeathEvent;
import com.multiverse.combat.CombatCore;

/**
 * PvP 리스너 클래스
 * PvP 관련 이벤트를 처리합니다.
 */
public class PvPListener implements Listener {
    
    private final CombatCore plugin;
    
    /**
     * PvPListener 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public PvPListener(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어 입장 이벤트
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 플레이어 데이터 로드
        plugin.getSkillManager().loadPlayerData(player);
        plugin.getComboManager().loadPlayerData(player);
        plugin.getPvPManager().loadPlayerData(player);
        plugin.getCombatDataManager().loadPlayerData(player);
        
        // 웰컴 메시지
        String prefix = plugin.getCombatConfig(). getString("messages.prefix", "[전투] ");
        player.sendMessage(prefix + "§aCombatCore 시스템이 준비되었습니다!");
        
        // PvP 설정 알림
        if (plugin.getPvPManager().isPvPEnabled(player)) {
            player.sendMessage(prefix + plugin.getCombatConfig().getString("messages. pvp.enabled",
                "§aPvP가 활성화되어 있습니다."));
        } else {
            player.sendMessage(prefix + plugin.getCombatConfig().getString("messages.pvp.disabled",
                "§cPvP가 비활성화되어 있습니다."));
        }
    }
    
    /**
     * 플레이어 퇴장 이벤트
     */
    @EventHandler(priority = EventPriority.  NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // 플레이어 데이터 저장
        plugin.getSkillManager().savePlayerData(player);
        plugin.getComboManager().savePlayerData(player);
        plugin.getPvPManager(). savePlayerData(player);
        plugin.getCombatDataManager(). savePlayerData(player);
    }
    
    /**
     * 플레이어 사망 이벤트
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        org.bukkit.entity.LivingEntity killer = victim.getKiller();
        
        // PvP 처리
        if (killer instanceof Player) {
            Player killerPlayer = (Player) killer;
            
            // PvP 활성화 확인
            if (plugin.getPvPManager(). isPvPEnabled(killerPlayer) && 
                plugin.getPvPManager().isPvPEnabled(victim)) {
                
                // 처치 처리
                plugin.getPvPManager().onPlayerKill(killerPlayer, victim);
                
                // 경험치 드롭 감소
                event.  setDroppedExp((int) (event.getDroppedExp() * 0.5));
            }
        }
    }
    
    /**
     * 플레이어 데미지 이벤트 (PvP 관련)
     */
    @EventHandler(priority = EventPriority. HIGHEST)
    public void onEntityDamageByEntity(org.bukkit.event.entity. EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        
        Player attacker = (Player) event.getDamager();
        Player target = (Player) event.getEntity();
        
        // PvP 가능 여부 확인
        if (!  plugin.getPvPManager().canAttack(attacker, target)) {
            event.setCancelled(true);
            attacker.sendMessage(plugin.getCombatConfig().  getString("messages.prefix", "[전투] ") +
                "§c레벨 차이 또는 설정으로 인해 이 플레이어와 전투할 수 없습니다.");
            return;
        }
    }
}