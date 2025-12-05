package com.multiverse.item. tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;

public class SetEffectTask extends BukkitRunnable {
    
    private ItemCore plugin;
    private int taskId;
    
    /**
     * 기본 생성자
     */
    public SetEffectTask(ItemCore plugin) {
        this. plugin = plugin;
    }
    
    /**
     * 작업 실행
     */
    @Override
    public void run() {
        plugin.getLogger().info("세트 효과 작업 실행 중...");
        
        // 모든 온라인 플레이어의 세트 효과 적용
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            applySetEffects(player);
        }
    }
    
    /**
     * 세트 효과 적용
     */
    private void applySetEffects(Player player) {
        try {
            // 플레이어 장착 아이템 확인
            // 세트 아이템 개수 확인
            // 세트 보너스 적용
            // 파티클 효과 표시
            
        } catch (Exception e) {
            plugin. getLogger().warning("플레이어 " + player.getName() + " 세트 효과 적용 오류: " + e.getMessage());
        }
    }
    
    /**
     * 작업 시작
     */
    public void start() {
        // 2초마다 실행
        taskId = this.runTaskTimer(plugin, 0L, 40L).getTaskId();
    }
    
    /**
     * 작업 중지
     */
    public void stop() {
        if (taskId >= 0) {
            plugin. getServer().getScheduler().cancelTask(taskId);
        }
    }
}