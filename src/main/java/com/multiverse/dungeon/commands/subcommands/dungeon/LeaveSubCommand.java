package com.multiverse. dungeon.commands.subcommands.dungeon;

import com. multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.utils.MessageUtils;
import org. bukkit.entity.Player;

/**
 * /dungeon leave 서브커맨드
 */
public class LeaveSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public LeaveSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player) {
        var instance = plugin.getInstanceManager(). getPlayerInstance(player);
        
        if (instance == null || !instance.isActive()) {
            MessageUtils.sendError(player, "현재 던전에 진행 중이 아닙니다.");
            return;
        }

        instance.removePlayer(player. getUniqueId());
        
        var playerData = plugin.getDataManager(). getPlayerData(player.getUniqueId());
        if (playerData != null) {
            playerData.setCurrentInstanceId(null);
        }

        MessageUtils.sendSuccess(player, "던전을 나갔습니다.");

        // 다른 파티원에게 알림
        for (var memberId : instance.getPlayers()) {
            var member = org.bukkit.Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage("§c[파티] " + player.getName() + "이(가) 던전을 나갔습니다.");
            }
        }

        // 모든 플레이어가 나갔으면 인스턴스 포기 처리
        if (instance.hasNoPlayers()) {
            instance.setStatus(com.multiverse.dungeon.data.enums.InstanceStatus. ABANDONED);
            
            var failEvent = new com.multiverse.dungeon.events.DungeonFailEvent(instance,
                com.multiverse.dungeon.events.DungeonFailEvent. FailReason.ABANDONED);
            org.bukkit. Bukkit.getPluginManager(). callEvent(failEvent);
        }
    }
}