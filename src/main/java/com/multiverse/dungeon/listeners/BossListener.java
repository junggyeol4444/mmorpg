package com.multiverse.dungeon.listeners;

import com. multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.events.*;
import org.bukkit.event. EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit. event.Listener;

/**
 * 보스 이벤트 리스너
 */
public class BossListener implements Listener {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public BossListener(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 보스 스폰 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBossSpawn(DungeonBossSpawnEvent event) {
        if (event. isCancelled()) {
            return;
        }

        var boss = event.getBoss();
        var entity = event.getEntity();
        var instance = event.getInstance();

        plugin.getLogger().info("보스 '" + boss.getName() + "'가 인스턴스 " + instance.getInstanceId() + "에 스폰되었습니다.");

        // 보스 엔티티 설정
        if (entity != null) {
            entity.setCustomName("§c" + boss.getName());
            entity.setCustomNameVisible(true);
            entity.setHealth(boss.getScaledHealth(plugin. getScalingManager()
                .getMobHealthMultiplier(instance.getDifficulty(), instance.getPlayers().size())));
        }

        // 모든 파티원에게 알림
        for (var playerId : instance.getPlayers()) {
            var player = org. bukkit.Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendMessage("§c[보스] " + boss.getName() + "이(가) 나타났습니다!");
            }
        }
    }

    /**
     * 보스 처치 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBossDefeated(DungeonBossDefeatedEvent event) {
        var boss = event.getBoss();
        var lastDamager = event.getLastDamager();
        var instance = event.getInstance();

        plugin.getLogger().info("보스 '" + boss.getName() + "'가 처치되었습니다.");

        // 모든 파티원에게 알림
        for (var playerId : instance.getPlayers()) {
            var player = org. bukkit.Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                String message = "§a[보스] " + boss.getName() + "이(가) 처치되었습니다! ";
                if (lastDamager != null) {
                    message += " §b(최후 일격: " + lastDamager.getName() + ")";
                }
                player.sendMessage(message);
            }
        }

        // 진행도 업데이트
        instance.getProgress().incrementBossesKilled();

        // 보상 지급
        var reward = boss.getReward();
        if (reward != null && lastDamager != null) {
            var dungeonReward = new com.multiverse.dungeon.data.model.DungeonReward();
            dungeonReward.setBaseExperience(reward.getBaseExperience());
            dungeonReward.setDungeonPoints(reward.getDungeonPoints());
            
            plugin.getRewardManager().giveReward(lastDamager, dungeonReward);
        }
    }

    /**
     * 보스 페이즈 변경 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBossPhaseChange(BossPhaseChangeEvent event) {
        var boss = event.getBoss();
        var newPhase = event.getNewPhase();
        var instance = event.getInstance();

        plugin.getLogger().info("보스 '" + boss.getName() + "'이(가) 페이즈 " 
            + event.getNewPhaseNumber() + "로 진입했습니다.");

        // 페이즈 메시지 전송
        if (newPhase != null && ! newPhase.getPhaseMessage().isEmpty()) {
            for (var playerId : instance.getPlayers()) {
                var player = org. bukkit.Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage("§6" + newPhase.getPhaseMessage());
                }
            }
        }

        // 파티원에게 경고
        for (var playerId : instance.getPlayers()) {
            var player = org.bukkit.Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendMessage("§c[경고] 보스가 새로운 패턴을 사용하기 시작했습니다!");
            }
        }
    }
}