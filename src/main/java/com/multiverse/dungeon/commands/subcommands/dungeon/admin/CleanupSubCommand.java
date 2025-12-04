package com.multiverse.dungeon.commands.subcommands. dungeon.admin;

import com.multiverse.dungeon.DungeonCore;
import com. multiverse.dungeon.utils. MessageUtils;
import com.multiverse.dungeon.constants. PermissionConstants;
import org. bukkit.entity.Player;

/**
 * /dungeon admin cleanup 서브커맨드
 * 만료된 인스턴스 정리
 */
public class CleanupSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public CleanupSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player) {
        if (!player.hasPermission(PermissionConstants. ADMIN_DUNGEON_CREATE)) {
            MessageUtils.sendError(player, "권한이 없습니다.");
            return;
        }

        player.sendMessage("§e⏳ 인스턴스 정리 중...");

        long startTime = System.currentTimeMillis();

        // 만료된 인스턴스 정리
        plugin.getInstanceManager().cleanupExpiredInstances();

        long elapsedTime = System.currentTimeMillis() - startTime;

        MessageUtils.sendSuccess(player, "인스턴스 정리가 완료되었습니다!");
        player.sendMessage("§b소요 시간: §f" + elapsedTime + "ms");
    }
}