package com.multiverse.dungeon.commands.subcommands.dungeon.admin;

import com. multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.utils.MessageUtils;
import com.multiverse.dungeon.utils.LocationUtils;
import com.multiverse. dungeon.constants.PermissionConstants;
import org.bukkit.entity.Player;

/**
 * /dungeon admin tp <id> [entrance|spawn] 서브커맨드
 */
public class TPSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public TPSubCommand(DungeonCore plugin) {
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

        if (args. length < 1) {
            MessageUtils.sendError(player, "사용법: /dungeon admin tp <던전ID> [entrance|spawn]");
            return;
        }

        String dungeonId = args[0];
        var dungeon = plugin.getDungeonManager().getDungeon(dungeonId);

        if (dungeon == null) {
            MessageUtils.sendError(player, "던전을 찾을 수 없습니다: " + dungeonId);
            return;
        }

        String type = args. length >= 2 ? args[1]. toLowerCase() : "spawn";
        org.bukkit.Location location = null;

        switch (type) {
            case "entrance":
                location = dungeon.getEntrance();
                if (location == null) {
                    MessageUtils.sendError(player, "입장 지점이 설정되지 않았습니다.");
                    return;
                }
                break;
            case "spawn":
                location = dungeon.getSpawn();
                if (location == null) {
                    MessageUtils. sendError(player, "스폰 지점이 설정되지 않았습니다.");
                    return;
                }
                break;
            default:
                MessageUtils.sendError(player, "알 수 없는 타입: " + type);
                return;
        }

        if (LocationUtils.teleport(player, location)) {
            MessageUtils.sendSuccess(player, type + " 지점으로 텔레포트했습니다!");
            player.sendMessage("§b위치: §f" + LocationUtils.formatSimple(location));
        } else {
            MessageUtils.sendError(player, "텔레포트에 실패했습니다.");
        }
    }
}