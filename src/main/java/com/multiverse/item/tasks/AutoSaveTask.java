package com.multiverse.item. tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;

public class AutoSaveTask extends BukkitRunnable {
    
    private ItemCore plugin;
    private int taskId;
    private int saveInterval; // 초 단위
    
    /**
     * 기본 생성자
     */
    public AutoSaveTask(ItemCore plugin, int saveIntervalSeconds) {
        this.plugin = plugin;
        this.saveInterval = saveIntervalSeconds;
    }
    
    /**
     * 작업 실행
     */
    @Override
    public void run() {
        // 자동 저장
        plugin.getLogger().info("자동 저장 작업 실행 중...");
        
        saveAllData();
    }
    
    /**
     * 모든 데이터 저장
     */
    private void saveAllData() {
        try {
            // 플레이어 데이터 저장
            savePlayerData();
            
            // 아이템 데이터 저장
            saveItemData();
            
            // 설정 저장
            saveConfig();
            
            plugin.getLogger().info("모든 데이터가 저장되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().warning("자동 저장 오류: " + e.getMessage());
        }
    }
    
    /**
     * 플레이어 데이터 저장
     */
    private void savePlayerData() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            try {
                plugin. getDataManager().savePlayerItems(player. getUniqueId(), player. getInventory());
            } catch (Exception e) {
                plugin.getLogger().warning("플레이어 " + player.getName() + " 데이터 저장 오류: " + e.getMessage());
            }
        }
    }
    
    /**
     * 아이템 데이터 저장
     */
    private void saveItemData() {
        try {
            plugin.getItemManager().saveAllItems();
        } catch (Exception e) {
            plugin.getLogger().warning("아이템 데이터 저장 오류: " + e.getMessage());
        }
    }
    
    /**
     * 설정 저장
     */
    private void saveConfig() {
        try {
            plugin.getConfigManager().saveAll();
        } catch (Exception e) {
            plugin.getLogger().warning("설정 저장 오류: " + e. getMessage());
        }
    }
    
    /**
     * 작업 시작
     */
    public void start() {
        // 설정된 간격으로 실행 (기본 300초 = 5분)
        long tickInterval = (long) saveInterval * 20L;
        taskId = this.runTaskTimer(plugin, tickInterval, tickInterval).getTaskId();
        plugin.getLogger().info("자동 저장 작업 시작 (간격: " + saveInterval + "초)");
    }
    
    /**
     * 작업 중지
     */
    public void stop() {
        if (taskId >= 0) {
            plugin.getServer().getScheduler(). cancelTask(taskId);
            plugin.getLogger().info("자동 저장 작업 중지");
        }
    }
}