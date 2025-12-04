package com.multiverse.combat. tasks;

import org.bukkit. Bukkit;
import org.bukkit.entity.Player;
import com.multiverse.combat.CombatCore;

/**
 * 쿨다운 작업 클래스
 * 주기적으로 플레이어의 쿨다운을 업데이트합니다. 
 */
public class CooldownTask implements Runnable {
    
    private final CombatCore plugin;
    
    /**
     * CooldownTask 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public CooldownTask(CombatCore plugin) {
        this. plugin = plugin;
    }
    
    /**
     * 작업 실행
     */
    @Override
    public void run() {
        // 쿨다운은 자동으로 추적되므로 별도 작업 불필요
        // (필요 시 플레이어에게 쿨다운 상태 표시)
        for (Player player : Bukkit. getOnlinePlayers()) {
            // 쿨다운 상태 업데이트 (선택)
        }
    }
}