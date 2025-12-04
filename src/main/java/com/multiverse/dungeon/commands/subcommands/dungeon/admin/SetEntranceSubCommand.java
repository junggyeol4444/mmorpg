package com.multiverse.dungeon.commands.subcommands.dungeon.admin;

import com. multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.utils.MessageUtils;
import com.multiverse.dungeon.utils.LocationUtils;
import com.multiverse.dungeon.constants.PermissionConstants;
import org.bukkit.entity.Player;

/**
 * /dungeon admin setentrance <id> 서브커맨드
 */
public class SetEntranceSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public SetEntranceSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player, String[] args) {
        if (!player.hasPermission(PermissionConstants.ADMIN_DUNGEON_CREATE)) {
            MessageUtils.sendError(player, "권한이 없습니다.");
            return;
        }

        if (args.length < 1) {
            MessageUtils.sendError(player, "사용법: /dungeon admin setentrance <던전ID>");
            return;
        }

        String dungeonId = args[0];
        var dungeon = plugin.getDungeonManager().getDungeon(dungeonId);

        if (dungeon == null) {
            MessageUtils.sendError(player, "던전을 찾을 수 없습니다: " + dungeonId);
            return;
        }

        // 플레이어의 현재 위치를 입장 지점으로 설정
        var location = player.getLocation();
        dungeon.setEntrance(location);

        if (plugin.getDungeonManager(). updateDungeon(dungeon)) {
            MessageUtils.sendSuccess(player, "던전 입장 지점이 설정되었습니다!");
            player.sendMessage("§b위치: §f" + LocationUtils.formatSimple(location));
        } else {
            MessageUtils.sendError(player, "입장 지점 설정에 실패했습니다.");
        }
    }
}