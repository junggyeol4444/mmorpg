package com.multiverse.dungeon. commands.subcommands.dungeon;

import com.multiverse. dungeon.DungeonCore;
import com.multiverse. dungeon.utils.MessageUtils;
import org.bukkit.entity.Player;

/**
 * /dungeon list 서브커맨드
 */
public class ListSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public ListSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player) {
        var dungeons = plugin.getDungeonManager().getEnabledDungeons();

        MessageUtils.sendDivider(player);
        player.sendMessage("§6=== 던전 목록 ===");
        MessageUtils.sendBlank(player);

        if (dungeons.isEmpty()) {
            MessageUtils.sendError(player, "사용 가능한 던전이 없습니다.");
            MessageUtils.sendDivider(player);
            return;
        }

        for (var dungeon : dungeons) {
            String name = "§b" + dungeon.getName();
            String type = "§7(" + dungeon.getType(). getDisplayName() + ")";
            String level = "§8[" + dungeon.getRequiredLevel() + "Lv]";
            String click = "§a/dungeon enter " + dungeon.getDungeonId();
            
            player.sendMessage(name + " " + type + " " + level);
            player.sendMessage("  " + click);
            MessageUtils.sendBlank(player);
        }

        MessageUtils.sendDivider(player);
    }
}