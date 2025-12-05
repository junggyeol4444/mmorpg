package com.multiverse.item.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;

public class OptionTickTask extends BukkitRunnable {
    
    private ItemCore plugin;
    private int taskId;
    
    /**
     * 기본 생성자
     */
    public OptionTickTask(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 작업 실행
     */
    @Override
    public void run() {
        plugin.getLogger().info("옵션 틱 작업 실행 중...");
        
        // 모든 온라인 플레이어의 옵션 처리
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            processPlayerOptions(player);
        }
    }
    
    /**
     * 플레이어 옵션 처리
     */
    private void processPlayerOptions(Player player) {
        try {
            // 손에 들고 있는 아이템 확인
            // 옵션 발동 확인
            // 옵션 효과 적용
            
        } catch (Exception e) {
            plugin.getLogger().warning("플레이어 " + player.getName() + " 옵션 처리 오류: " + e.getMessage());
        }
    }
    
    /**
     * 작업 시작
     */
    public void start() {
        // 매 틱마다 실행 (1 틱 = 50ms)
        taskId = this.runTaskTimer(plugin, 0L, 1L). getTaskId();
    }
    
    /**
     * 작업 중지
     */
    public void stop() {
        if (taskId >= 0) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
    }
}