package com.multiverse.dungeon.commands.subcommands. dungeon.admin;

import com.multiverse.dungeon.DungeonCore;
import com. multiverse.dungeon.utils. MessageUtils;
import com.multiverse.dungeon.constants. PermissionConstants;
import org. bukkit.entity.Player;

/**
 * /dungeon admin reload 서브커맨드
 * 설정 파일 재로드
 */
public class ReloadSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public ReloadSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player) {
        if (!player.hasPermission(PermissionConstants. ADMIN_DUNGEON_RELOAD)) {
            MessageUtils. sendError(player, "권한이 없습니다.");
            return;
        }

        player.sendMessage("§e⏳ 설정 파일 재로드 중...");

        long startTime = System.currentTimeMillis();

        try {
            // 설정 파일 재로드
            plugin.reloadConfig();
            
            // 던전 재로드
            plugin.getDungeonManager().reloadDungeons();
            
            // 파티 재로드
            plugin.getPartyManager().reloadParties();

            long elapsedTime = System.currentTimeMillis() - startTime;

            MessageUtils.sendSuccess(player, "설정 파일이 재로드되었습니다!");
            player.sendMessage("§b소요 시간: §f" + elapsedTime + "ms");
        } catch (Exception e) {
            MessageUtils.sendError(player, "재로드 중 오류가 발생했습니다: " + e.getMessage());
            plugin.getLogger().warning("⚠️ 재로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}