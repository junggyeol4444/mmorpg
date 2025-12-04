package com.multiverse.dungeon.listeners;

import com. multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.data.enums. InstanceStatus;
import com.multiverse. dungeon.data.model.DungeonInstance;
import org.bukkit.event.EventHandler;
import org.bukkit. event.EventPriority;
import org. bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 인스턴스 이벤트 리스너
 */
public class InstanceListener implements Listener {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public InstanceListener(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 플레이어 퇴장 이벤트 핸들러
     * - 던전 진행 중 플레이어가 나갈 경우 처리
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        var playerId = player.getUniqueId();

        // 플레이어의 현재 인스턴스 확인
        DungeonInstance instance = plugin.getInstanceManager().getPlayerInstance(player);
        if (instance == null || ! instance.isActive()) {
            return;
        }

        plugin.getLogger().info("플레이어 " + player.getName() + "가 인스턴스 진행 중 접속을 해제했습니다.");

        // 인스턴스에서 플레이어 제거
        instance.removePlayer(playerId);

        // 플레이어 데이터 업데이트
        var playerData = plugin.getDataManager().getPlayerData(playerId);
        if (playerData != null) {
            playerData.setCurrentInstanceId(null);
        }

        // 남은 플레이어가 없으면 인스턴스 포기 처리
        if (instance.hasNoPlayers()) {
            plugin. getLogger().info("모든 플레이어가 떠나 인스턴스 " + instance.getInstanceId() + "가 포기되었습니다.");
            instance.setStatus(InstanceStatus.ABANDONED);
            
            var failEvent = new DungeonFailEvent(instance, DungeonFailEvent.FailReason.ALL_PLAYERS_LEFT);
            org.bukkit. Bukkit.getPluginManager().callEvent(failEvent);
        }

        // 다른 파티원에게 알림
        for (var memberId : instance.getPlayers()) {
            var member = org. bukkit.Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member. sendMessage("§c플레이어 " + player.getName() + "이(가) 접속을 해제했습니다!");
            }
        }
    }
}