package com.multiverse.combat.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.multiverse. combat.CombatCore;

/**
 * 콤보 타임아웃 작업 클래스
 * 주기적으로 콤보 타임아웃을 확인합니다.
 */
public class ComboTimeoutTask implements Runnable {
    
    private final CombatCore plugin;
    
    /**
     * ComboTimeoutTask 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public ComboTimeoutTask(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 작업 실행
     */
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getComboManager().checkComboTimeout(player);
        }
    }
}