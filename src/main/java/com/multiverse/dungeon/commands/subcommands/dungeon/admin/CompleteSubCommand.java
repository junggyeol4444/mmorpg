package com.multiverse.dungeon. commands.subcommands.dungeon.admin;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.utils.MessageUtils;
import com.multiverse.dungeon.constants. PermissionConstants;
import org.bukkit.entity.Player;

/**
 * /dungeon admin complete <instanceId> 서브커맨드
 * 인스턴스 강제 완료
 */
public class CompleteSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public CompleteSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player, String[] args) {
        if (! player.hasPermission(PermissionConstants.ADMIN_DUNGEON_CREATE)) {
            MessageUtils.sendError(player, "권한이 없습니다.");
            return;
        }

        if (args.length < 1) {
            MessageUtils.sendError(player, "사용법: /dungeon admin complete <인스턴스ID>");
            return;
        }

        String instanceIdStr = args[0];
        
        try {
            java.util.UUID instanceId = java.util.UUID.fromString(instanceIdStr);
            var instance = plugin.getInstanceManager().getInstance(instanceId);

            if (instance == null) {
                MessageUtils.sendError(player, "인스턴스를 찾을 수 없습니다: " + instanceIdStr);
                return;
            }

            if (! instance.isActive()) {
                MessageUtils.sendError(player, "활성 인스턴스가 아닙니다.");
                return;
            }

            // 인스턴스 완료 처리
            plugin.getInstanceManager().completeInstance(instanceId);

            MessageUtils.sendSuccess(player, "인스턴스가 강제 완료되었습니다!");
            player.sendMessage("§b인스턴스 ID: §f" + instanceId);

            // 모든 플레이어에게 알림
            for (var playerId : instance.getPlayers()) {
                var member = org.bukkit. Bukkit.getPlayer(playerId);
                if (member != null && member.isOnline()) {
                    member.sendMessage("§a[관리자] 던전이 강제 완료되었습니다.");
                }
            }
        } catch (IllegalArgumentException e) {
            MessageUtils.sendError(player, "잘못된 인스턴스 ID 형식입니다.");
        }
    }
}