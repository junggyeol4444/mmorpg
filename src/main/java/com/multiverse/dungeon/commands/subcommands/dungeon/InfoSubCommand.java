package com.multiverse.dungeon.commands.subcommands. dungeon;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.utils.MessageUtils;
import org.bukkit. entity.Player;

/**
 * /dungeon info <dungeonId> 서브커맨드
 */
public class InfoSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public InfoSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            MessageUtils.sendError(player, "사용법: /dungeon info <던전ID>");
            return;
        }

        String dungeonId = args[0];
        var dungeon = plugin.getDungeonManager().getDungeon(dungeonId);

        if (dungeon == null) {
            MessageUtils.sendError(player, "던전을 찾을 수 없습니다: " + dungeonId);
            return;
        }

        MessageUtils.sendDivider(player);
        player.sendMessage("§6=== " + dungeon.getName() + " ===");
        MessageUtils.sendBlank(player);

        player.sendMessage("§b타입: §f" + dungeon.getType(). getDisplayName());
        player. sendMessage("§b난이도: §f" + dungeon.getDifficulty().getDisplayName());
        player.sendMessage("§b필요 레벨: §f" + dungeon.getRequiredLevel());
        player.sendMessage("§b파티 크기: §f" + dungeon.getMinPlayers() + " ~ " + dungeon.getMaxPlayers());
        player.sendMessage("§b제한 시간: §f" + formatTime(dungeon.getTimeLimit()));
        player.sendMessage("§b보스: §f" + dungeon. getBossCount() + "마리");

        if (! dungeon.getDescription().isEmpty()) {
            MessageUtils.sendBlank(player);
            player.sendMessage("§b설명:");
            for (String line : dungeon.getDescription(). split("\\n")) {
                player.sendMessage("  §f" + line);
            }
        }

        MessageUtils.sendBlank(player);
        player.sendMessage("§a/dungeon enter " + dungeonId + " [난이도]");
        MessageUtils.sendDivider(player);
    }

    /**
     * 시간 포맷팅
     */
    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%d분 %d초", minutes, remainingSeconds);
    }
}