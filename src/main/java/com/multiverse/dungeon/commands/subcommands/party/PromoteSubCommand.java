package com.multiverse.dungeon.commands.subcommands.party;

import com.multiverse.dungeon.DungeonCore;
import com. multiverse.dungeon.utils. MessageUtils;
import org.bukkit.entity.Player;

/**
 * /party promote <플레이어명> 서브커맨드
 */
public class PromoteSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public PromoteSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            MessageUtils.sendError(player, "사용법: /party promote <플레이어명>");
            return;
        }

        var party = plugin.getPartyManager(). getPlayerParty(player);
        if (party == null) {
            MessageUtils.sendError(player, "파티에 속해있지 않습니다.");
            return;
        }

        if (!party.isLeader(player.getUniqueId())) {
            MessageUtils.sendError(player, "파티 리더만 리더 위임을 할 수 있습니다.");
            return;
        }

        String targetName = args[0];
        var target = org.bukkit.Bukkit. getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            MessageUtils.sendError(player, "플레이어를 찾을 수 없습니다: " + targetName);
            return;
        }

        if (! party.hasMember(target.getUniqueId())) {
            MessageUtils.sendError(player, "이 플레이어는 파티에 속해있지 않습니다.");
            return;
        }

        if (plugin.getPartyManager().promoteLeader(party.getPartyId(), target.getUniqueId())) {
            MessageUtils. sendSuccess(player, target.getName() + "을(를) 리더로 위임했습니다.");
            target.sendMessage("§a리더로 승격되었습니다!");
            
            // 다른 파티원에게 알림
            for (var memberId : party.getMembers()) {
                if (memberId. equals(player.getUniqueId()) || memberId.equals(target.getUniqueId())) continue;
                
                var member = org.bukkit.Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline()) {
                    member.sendMessage("§b[파티] " + target. getName() + "이(가) 새로운 리더가 되었습니다.");
                }
            }
        } else {
            MessageUtils.sendError(player, "리더 위임에 실패했습니다.");
        }
    }
}