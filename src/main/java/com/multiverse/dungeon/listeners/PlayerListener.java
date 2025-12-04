package com.multiverse.dungeon. listeners;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.data.model.DungeonInstance;
import org.bukkit.event.EventHandler;
import org.bukkit. event.EventPriority;
import org. bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * 플레이어 이벤트 리스너
 */
public class PlayerListener implements Listener {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public PlayerListener(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 플레이어 사망 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getEntity();
        var playerId = player.getUniqueId();

        // 플레이어의 현재 인스턴스 확인
        DungeonInstance instance = plugin.getInstanceManager().getPlayerInstance(player);
        if (instance == null || ! instance.isActive()) {
            return;
        }

        plugin.getLogger().info("플레이어 " + player.getName() + "이(가) 인스턴스 " 
            + instance.getInstanceId() + "에서 사망했습니다.");

        // 진행도 업데이트
        instance.getProgress().incrementDeaths();

        // 플레이어 데이터 업데이트
        var playerData = plugin. getDataManager().getPlayerData(playerId);
        if (playerData != null) {
            playerData.incrementTotalDeaths();
        }

        // 파티원에게 알림
        for (var memberId : instance.getPlayers()) {
            if (memberId. equals(playerId)) continue;
            
            var member = org.bukkit.Bukkit. getPlayer(memberId);
            if (member != null && member. isOnline()) {
                member.sendMessage("§c[경고] 파티원 " + player.getName() + "이(가) 사망했습니다!");
            }
        }

        // 모든 플레이어가 사망했을 경우 던전 실패
        int aliveCount = 0;
        for (var memberId : instance.getPlayers()) {
            var member = org. bukkit.Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline() && member.isDead() == false) {
                aliveCount++;
            }
        }

        if (aliveCount == 0) {
            plugin.getLogger().info("모든 파티원이 사망해 인스턴스 " + instance.getInstanceId() + "가 실패했습니다.");
            instance.setStatus(com.multiverse.dungeon.data.enums.InstanceStatus.FAILED);
            
            var failEvent = new com.multiverse.dungeon.events.DungeonFailEvent(instance, 
                com.multiverse.dungeon.events.DungeonFailEvent. FailReason. PARTY_WIPEOUT);
            org.bukkit.Bukkit.getPluginManager().callEvent(failEvent);
        }
    }

    /**
     * 플레이어 이동 이벤트 핸들러
     * - 던전 범위 벗어남 감지
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        var player = event.getPlayer();

        // 블록 단위 이동만 감지
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        // 플레이어의 현재 인스턴스 확인
        DungeonInstance instance = plugin.getInstanceManager().getPlayerInstance(player);
        if (instance == null || !instance.isActive()) {
            return;
        }

        // 현재 위치가 인스턴스 월드인지 확인
        var world = event.getTo().getWorld();
        if (world == null || !world.getName().equals(instance.getWorldName())) {
            // 플레이어가 던전을 빠져나감
            plugin.getLogger().info("플레이어 " + player.getName() + "이(가) 인스턴스에서 빠져나갔습니다.");
            
            instance.removePlayer(player.getUniqueId());
            
            var playerData = plugin.getDataManager(). getPlayerData(player.getUniqueId());
            if (playerData != null) {
                playerData.setCurrentInstanceId(null);
            }

            player.sendMessage("§c던전을 나갔습니다.");

            // 모든 플레이어가 나갔으면 포기 처리
            if (instance. hasNoPlayers()) {
                instance.setStatus(com.multiverse.dungeon.data.enums.InstanceStatus.ABANDONED);
                
                var failEvent = new com.multiverse.dungeon.events.DungeonFailEvent(instance, 
                    com.multiverse.dungeon.events.DungeonFailEvent. FailReason. ABANDONED);
                org.bukkit. Bukkit.getPluginManager().callEvent(failEvent);
            }
        }
    }
}