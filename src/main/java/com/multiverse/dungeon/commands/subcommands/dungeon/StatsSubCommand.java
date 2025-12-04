package com.multiverse.dungeon.commands.subcommands. dungeon;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.utils.MessageUtils;
import org.bukkit. entity.Player;

/**
 * /dungeon stats [dungeonId] 서브커맨드
 */
public class StatsSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public StatsSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player, String[] args) {
        var playerData = plugin.getDataManager(). getPlayerData(player.getUniqueId());
        
        if (playerData == null) {
            MessageUtils.sendError(player, "플레이어 데이터를 찾을 수 없습니다.");
            return;
        }

        MessageUtils.sendDivider(player);
        player.sendMessage("§6=== 당신의 던전 통계 ===");
        MessageUtils.sendBlank(player);

        player.sendMessage("§b총 입장: §f" + playerData.getTotalRuns());
        player.sendMessage("§b총 클리어: §f" + playerData. getTotalClears());
        player.sendMessage("§b총 사망: §f" + playerData. getTotalDeaths());
        player.sendMessage("§b던전 포인트: §f" + playerData.getDungeonPoints());
        player. sendMessage("§b레벨: §f" + playerData.getLevel());

        if (args.length >= 1) {
            String dungeonId = args[0];
            var dungeon = plugin.getDungeonManager().getDungeon(dungeonId);
            
            if (dungeon == null) {
                MessageUtils. sendWarning(player, "던전을 찾을 수 없습니다: " + dungeonId);
            } else {
                MessageUtils.sendBlank(player);
                player.sendMessage("§b=== " + dungeon.getName() + " ===");
                
                for (var difficulty : com.multiverse.dungeon.data.enums.DungeonDifficulty.values()) {
                    int clears = playerData.getClearCount(dungeonId, difficulty. name());
                    if (clears > 0) {
                        player.sendMessage("§b  [" + difficulty.getDisplayName() + "§b] 클리어: §f" + clears + "회");
                    }
                }
            }
        }

        MessageUtils.sendDivider(player);
    }
}