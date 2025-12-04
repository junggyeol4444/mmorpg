package com.multiverse.dungeon.commands.subcommands.party;

import com.multiverse. dungeon.DungeonCore;
import com.multiverse.dungeon.utils.MessageUtils;
import org.bukkit.entity.Player;

/**
 * /party create 서브커맨드
 */
public class CreateSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public CreateSubCommand(DungeonCore plugin) {
        this. plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player) {
        // 이미 파티에 속해있는지 확인
        var existingParty = plugin.getPartyManager().getPlayerParty(player);
        if (existingParty != null) {
            MessageUtils.sendError(player, "이미 파티에 속해있습니다.");
            player.sendMessage("§e파티를 나가려면 /party leave를 사용하세요.");
            return;
        }

        // 파티 생성
        var party = plugin.getPartyManager().createParty(player);
        if (party == null) {
            MessageUtils.sendError(player, "파티 생성에 실패했습니다.");
            return;
        }

        MessageUtils.sendSuccess(player, "파티가 생성되었습니다!");
        MessageUtils.sendBlank(player);
        player.sendMessage("§b다른 플레이어를 초대하려면:");
        player.sendMessage("§e/party invite <플레이어명>");
        MessageUtils.sendBlank(player);
        player.sendMessage("§b파티 정보 확인:");
        player.sendMessage("§e/party info");
    }
}