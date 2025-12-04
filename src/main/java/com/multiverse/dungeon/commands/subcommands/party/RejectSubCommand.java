package com.multiverse. dungeon.commands.subcommands.party;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.utils.MessageUtils;
import org.bukkit. entity.Player;

/**
 * /party reject <플레이어명> 서브커맨드
 */
public class RejectSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public RejectSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            MessageUtils.sendError(player, "사용법: /party reject <플레이어명>");
            return;
        }

        String inviterName = args[0];
        var inviter = org. bukkit.Bukkit.getPlayer(inviterName);

        if (inviter == null || ! inviter.isOnline()) {
            MessageUtils.sendError(player, "플레이어를 찾을 수 없습니다: " + inviterName);
            return;
        }

        var inviterParty = plugin.getPartyManager(). getPlayerParty(inviter);
        if (inviterParty == null) {
            MessageUtils.sendError(player, "초대한 플레이어가 파티에 속해있지 않습니다.");
            return;
        }

        java.util.UUID partyId = inviterParty. getPartyId();
        if (plugin.getInviteManager(). rejectInvite(player. getUniqueId(), partyId)) {
            MessageUtils. sendSuccess(player, "초대를 거절했습니다.");
            
            inviter.sendMessage("§c" + player.getName() + "이(가) 파티 초대를 거절했습니다.");
        } else {
            MessageUtils. sendError(player, "초대 거절에 실패했습니다.");
        }
    }
}