package com.multiverse.  dungeon.commands.subcommands.party;

import com.  multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.utils.MessageUtils;
import org.bukkit.entity.Player;

/**
 * /party accept <플레이어명> 서브커맨드
 */
public class AcceptSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public AcceptSubCommand(DungeonCore plugin) {
        this. plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            MessageUtils.sendError(player, "사용법: /party accept <플레이어명>");
            return;
        }

        var existingParty = plugin.getPartyManager().getPlayerParty(player);
        if (existingParty != null) {
            MessageUtils. sendError(player, "이미 파티에 속해있습니다.");
            player. sendMessage("§e파티를 나가려면: /party leave");
            return;
        }

        String inviterName = args[0];
        var inviter = org.bukkit.Bukkit.getPlayer(inviterName);

        if (inviter == null || !inviter.isOnline()) {
            MessageUtils.sendError(player, "플레이어를 찾을 수 없습니다: " + inviterName);
            return;
        }

        var inviterParty = plugin.getPartyManager().getPlayerParty(inviter);
        if (inviterParty == null) {
            MessageUtils.sendError(player, "초대한 플레이어가 파티에 속해있지 않습니다.");
            return;
        }

        // 초대 확인
        java.util.UUID partyId = inviterParty.  getPartyId();
        if (!plugin.getInviteManager().hasActiveInvite(player.getUniqueId(), partyId)) {
            MessageUtils.sendError(player, "활성 초대가 없습니다.");
            return;
        }

        // 초대 수락
        if (plugin.getInviteManager().acceptInvite(player.getUniqueId(), partyId)) {
            plugin.getPartyManager().addPlayerToParty(partyId, player);
            
            MessageUtils.sendSuccess(player, "파티에 참여했습니다!");
            MessageUtils.sendBlank(player);
            
            inviter.sendMessage("§a" + player.getName() + "이(가) 파티에 참여했습니다!");
            
            // 모든 파티원에게 알림
            for (var memberId : inviterParty.getMembers()) {
                var member = org.bukkit. Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline() && !member.getUniqueId().equals(player.getUniqueId())) {
                    member.sendMessage("§b[파티] " + player.getName() + "이(가) 참여했습니다!");
                    member.sendMessage("§8(" + inviterParty.getMemberCount() + "/" + inviterParty.getMaxMembers() + ")");
                }
            }
        } else {
            MessageUtils.sendError(player, "초대 수락에 실패했습니다.");
        }
    }
}