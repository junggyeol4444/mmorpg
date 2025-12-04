package com.multiverse.combat. tasks;

import org.bukkit. Bukkit;
import org.bukkit.entity.Player;
import com.multiverse.combat.CombatCore;

/**
 * 자동 저장 작업 클래스
 * 주기적으로 플레이어 데이터를 저장합니다. 
 */
public class AutoSaveTask implements Runnable {
    
    private final CombatCore plugin;
    
    /**
     * AutoSaveTask 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public AutoSaveTask(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 작업 실행
     */
    @Override
    public void run() {
        for (Player player : Bukkit. getOnlinePlayers()) {
            try {
                // 모든 데이터 저장
                plugin.getSkillManager().savePlayerData(player);
                plugin. getComboManager().savePlayerData(player);
                plugin. getPvPManager().savePlayerData(player);
                plugin.getCombatDataManager().savePlayerData(player);
            } catch (Exception e) {
                plugin.getLogger().warning("플레이어 " + player.getName() + "의 자동 저장 중 오류: " + e.getMessage());
            }
        }
        
        plugin.getLogger().info("✓ 자동 저장 완료 (" + Bukkit.getOnlinePlayers().size() + "명)");
    }
}