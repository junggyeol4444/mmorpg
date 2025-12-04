package com.multiverse.dungeon.commands.subcommands.party;

import com.multiverse. dungeon.DungeonCore;
import com.multiverse.dungeon.utils.MessageUtils;
import org.bukkit.entity. Player;

/**
 * /party invite <플레이어명> 서브커맨드
 */
public class InviteSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public InviteSubCommand(DungeonCore plugin) {
        this. plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            MessageUtils.sendError(player, "사용법: /party invite <플레이어명>");
            return;
        }

        var party = plugin.getPartyManager().  getPlayerParty(player);
        if (party == null) {
            MessageUtils.sendError(player, "파티에 속해있지 않습니다.");
            player.sendMessage("§e먼저 파티를 생성하세요: /party create");
            return;
        }

        if (!party.isLeader(player. getUniqueId())) {
            MessageUtils.sendError(player, "파티 리더만 초대할 수 있습니다.");
            return;
        }

        if (party.isFull()) {
            MessageUtils. sendError(player, "파티가 만석입니다.");
            player.sendMessage("§e최대 파티 크기: " + party.getMaxMembers() + "명");
            return;
        }

        String targetName = args[0];
        var target = org.bukkit. Bukkit.getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            MessageUtils.sendError(player, "플레이어를 찾을 수 없습니다: " + targetName);
            return;
        }

        if (target.getUniqueId().equals(player. getUniqueId())) {
            MessageUtils.sendError(player, "자신을 초대할 수 없습니다.");
            return;
        }

        var targetParty = plugin.getPartyManager().getPlayerParty(target);
        if (targetParty != null) {
            MessageUtils.sendError(player, "이미 파티에 속한 플레이어입니다.");
            return;
        }

        // 초대 전송
        long expireTime = System.currentTimeMillis() + (300 * 1000); // 5분
        plugin.getInviteManager().addInvite(target. getUniqueId(), party.getPartyId(), expireTime);

        var inviteEvent = new com.multiverse.dungeon.events.PartyInviteEvent(
            party, player, target, expireTime
        );
        org.bukkit. Bukkit.getPluginManager(). callEvent(inviteEvent);

        if (! inviteEvent.isCancelled()) {
            MessageUtils.sendSuccess(player, target. getName() + "에게 초대를 보냈습니다.");
            MessageUtils.sendBlank(player);
            
            target.sendMessage("§e" + player.getName() + "이(가) 파티 초대를 보냈습니다!");
            target.sendMessage("§e/party accept " + player.getName() + " - 초대 수락");
            target.sendMessage("§e/party reject " + player.getName() + " - 초대 거절");
            target.sendMessage("§8(제한 시간: 5분)");
        }
    }
}