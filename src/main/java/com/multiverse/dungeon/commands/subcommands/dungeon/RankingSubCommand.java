package com.multiverse.dungeon.commands.subcommands. dungeon;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.data.enums.DungeonDifficulty;
import com. multiverse.dungeon.utils. MessageUtils;
import com.multiverse.dungeon.utils.ValidationUtils;
import org.bukkit.entity.Player;

/**
 * /dungeon ranking <dungeonId> [난이도] 서브커맨드
 */
public class RankingSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public RankingSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player, String[] args) {
        if (args. length < 1) {
            MessageUtils.sendError(player, "사용법: /dungeon ranking <던전ID> [난이도]");
            return;
        }

        String dungeonId = args[0];
        var dungeon = plugin.getDungeonManager().getDungeon(dungeonId);

        if (dungeon == null) {
            MessageUtils.sendError(player, "던전을 찾을 수 없습니다: " + dungeonId);
            return;
        }

        DungeonDifficulty difficulty = DungeonDifficulty. NORMAL;
        if (args.length >= 2) {
            if (! ValidationUtils.isValidDifficulty(args[1])) {
                MessageUtils.sendError(player, "알 수 없는 난이도: " + args[1]);
                return;
            }
            difficulty = DungeonDifficulty.valueOf(args[1]. toUpperCase());
        }

        var records = plugin.getLeaderboardManager().getTopRecords(dungeonId, difficulty, 10);

        MessageUtils.sendDivider(player);
        player.sendMessage("§6=== " + dungeon.getName() + " (" + difficulty.getDisplayName() + "§6) TOP 10 ===");
        MessageUtils.sendBlank(player);

        if (records.isEmpty()) {
            MessageUtils.sendWarning(player, "아직 기록이 없습니다.");
            MessageUtils.sendDivider(player);
            return;
        }

        int rank = 1;
        for (var record : records) {
            String rankStr = "§b#" + rank + " ";
            String players = "§f" + String.join(", ", record.getPlayerNames());
            String time = "§7" + record.getClearTimeFormatted();
            String score = "§8(점수: " + record.getScore() + ")";
            
            player.sendMessage(rankStr + players + " " + time + " " + score);
            rank++;
        }

        MessageUtils.sendDivider(player);
    }
}