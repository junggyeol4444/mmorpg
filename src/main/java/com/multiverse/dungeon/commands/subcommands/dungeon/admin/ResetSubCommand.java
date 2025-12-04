package com.multiverse. dungeon.commands.subcommands.dungeon.admin;

import com.multiverse.dungeon. DungeonCore;
import com.multiverse.dungeon.utils.MessageUtils;
import com. multiverse.dungeon.constants. PermissionConstants;
import org.bukkit.entity.Player;

/**
 * /dungeon admin reset <id> 서브커맨드
 * 던전 데이터 초기화
 */
public class ResetSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public ResetSubCommand(DungeonCore plugin) {
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
            MessageUtils.sendError(player, "사용법: /dungeon admin reset <던전ID>");
            return;
        }

        String dungeonId = args[0];
        var dungeon = plugin.getDungeonManager().getDungeon(dungeonId);

        if (dungeon == null) {
            MessageUtils.sendError(player, "던전을 찾을 수 없습니다: " + dungeonId);
            return;
        }

        // 확인 메시지
        player.sendMessage("§c⚠ 경고: 이 작업은 되돌릴 수 없습니다!");
        player.sendMessage("§c확인하려면 /dungeon admin reset-confirm " + dungeonId + "을(를) 입력하세요.");
        return;
    }
}