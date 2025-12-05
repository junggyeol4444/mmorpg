package com.multiverse. item.tasks;

import org. bukkit.scheduler.BukkitRunnable;
import org. bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse. item.ItemCore;

public class DurabilityTask extends BukkitRunnable {
    
    private ItemCore plugin;
    private int taskId;
    
    /**
     * 기본 생성자
     */
    public DurabilityTask(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 작업 실행
     */
    @Override
    public void run() {
        plugin. getLogger().info("내구도 감소 작업 실행 중...");
        
        // 모든 온라인 플레이어의 내구도 감소
        for (Player player : plugin. getServer().getOnlinePlayers()) {
            decreaseDurability(player);
        }
    }
    
    /**
     * 내구도 감소
     */
    private void decreaseDurability(Player player) {
        try {
            // 손에 들고 있는 아이템 확인
            ItemStack item = player.getInventory().getItemInMainHand();
            
            if (item == null) {
                return;
            }
            
            // 내구도 감소 로직
            // 내구도가 0이 되면 처리
            
        } catch (Exception e) {
            plugin.getLogger().warning("플레이어 " + player.getName() + " 내구도 감소 오류: " + e.getMessage());
        }
    }
    
    /**
     * 작업 시작
     */
    public void start() {
        // 5초마다 실행
        taskId = this.runTaskTimer(plugin, 0L, 100L).getTaskId();
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