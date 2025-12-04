package com.multiverse. dungeon.commands.subcommands.party;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.utils.MessageUtils;
import org.bukkit.entity.Player;

/**
 * /party leave 서브커맨드
 */
public class LeaveSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public LeaveSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player) {
        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            MessageUtils. sendError(player, "파티에 속해있지 않습니다.");
            return;
        }

        // 진행 중인 던전 확인
        var instance = plugin.getInstanceManager().getPlayerInstance(player);
        if (instance != null && instance.isActive()) {
            MessageUtils.sendError(player, "진행 중인 던전에서는 파티를 나갈 수 없습니다.");
            return;
        }

        boolean isLeader = party.isLeader(player. getUniqueId());
        
        plugin.getPartyManager().removePlayerFromParty(party. getPartyId(), player. getUniqueId());
        
        MessageUtils.sendSuccess(player, "파티를 나갔습니다.");

        if (isLeader) {
            MessageUtils.sendWarning(player, "리더였으므로 새로운 리더가 지정되었습니다.");
        }

        // 다른 파티원에게 알림
        for (var memberId : party.getMembers()) {
            var member = org.bukkit. Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage("§c[파티] " + player.getName() + "이(가) 파티를 나갔습니다.");
                member.sendMessage("§8(" + party.getMemberCount() + "/" + party.getMaxMembers() + ")");
            }
        }
    }
}