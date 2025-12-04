package com.multiverse.dungeon.commands.subcommands. party;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.utils.MessageUtils;
import org.bukkit.entity.Player;

/**
 * /party disband 서브커맨드
 */
public class DisbandSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public DisbandSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player) {
        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            MessageUtils.sendError(player, "파티에 속해있지 않습니다.");
            return;
        }

        if (!party.isLeader(player.getUniqueId())) {
            MessageUtils.sendError(player, "파티 리더만 파티를 해체할 수 있습니다.");
            return;
        }

        plugin.getPartyManager().disbandParty(party.getPartyId(), 
            com.multiverse.dungeon.events.PartyDisbandedEvent.DisbandReason.LEADER_COMMAND);

        MessageUtils.sendSuccess(player, "파티를 해체했습니다.");

        // 모든 파티원에게 알림
        for (var memberId : party.getMembers()) {
            var member = org.bukkit. Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage("§c[파티] 파티가 해체되었습니다.");
            }
        }
    }
}