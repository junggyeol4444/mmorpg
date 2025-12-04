package com.multiverse.dungeon. commands.subcommands.dungeon.admin;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.data.model. Dungeon;
import com.multiverse.dungeon.data.enums.DungeonType;
import com.multiverse. dungeon.data.enums. DungeonDifficulty;
import com. multiverse.dungeon.utils.MessageUtils;
import com. multiverse.dungeon.utils. ValidationUtils;
import com.multiverse.dungeon.constants. PermissionConstants;
import org.bukkit.entity.Player;

/**
 * /dungeon admin create <id> <name> <type> <difficulty> 서브커맨드
 */
public class CreateSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public CreateSubCommand(DungeonCore plugin) {
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

        if (args.length < 4) {
            MessageUtils. sendError(player, "사용법: /dungeon admin create <ID> <이름> <타입> <난이도>");
            player.sendMessage("§e타입: NORMAL, HEROIC, MYTHIC, RAID");
            player.sendMessage("§e난이도: EASY, NORMAL, HARD, EXTREME");
            return;
        }

        String dungeonId = args[0];
        String name = args[1];
        String typeStr = args[2]. toUpperCase();
        String difficultyStr = args[3]. toUpperCase();

        // 던전 ID 검증
        if (!ValidationUtils.isValidName(dungeonId)) {
            MessageUtils.sendError(player, "던전 ID는 영문자, 숫자, 밑줄만 허용됩니다.");
            return;
        }

        // 이미 존재하는지 확인
        if (plugin.getDungeonManager().hasDungeon(dungeonId)) {
            MessageUtils.sendError(player, "이미 존재하는 던전입니다: " + dungeonId);
            return;
        }

        // 타입 검증
        DungeonType type;
        try {
            type = DungeonType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            MessageUtils.sendError(player, "알 수 없는 타입: " + typeStr);
            return;
        }

        // 난이도 검증
        DungeonDifficulty difficulty;
        try {
            difficulty = DungeonDifficulty.valueOf(difficultyStr);
        } catch (IllegalArgumentException e) {
            MessageUtils.sendError(player, "알 수 없는 난이도: " + difficultyStr);
            return;
        }

        // 던전 생성
        Dungeon dungeon = new Dungeon(dungeonId, name, type, difficulty);
        dungeon.setCreatedBy(player.getUniqueId());
        dungeon.setCreatedAt(System.currentTimeMillis());

        if (plugin.getDungeonManager(). addDungeon(dungeon)) {
            MessageUtils.sendSuccess(player, "던전이 생성되었습니다!");
            player.sendMessage("§b던전 ID: §f" + dungeonId);
            player.sendMessage("§b이름: §f" + name);
            player.sendMessage("§b타입: §f" + type.getDisplayName());
            player.sendMessage("§b난이도: §f" + difficulty. getDisplayName());
            player.sendMessage("§e다음 설정을 해야 합니다:");
            player.sendMessage("§e  /dungeon admin setentrance " + dungeonId);
            player.sendMessage("§e  /dungeon admin setspawn " + dungeonId);
        } else {
            MessageUtils. sendError(player, "던전 생성에 실패했습니다.");
        }
    }
}