package com.multiverse.dungeon. listeners;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event. EventPriority;
import org. bukkit.event.Listener;

/**
 * 파티 이벤트 리스너
 */
public class PartyListener implements Listener {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public PartyListener(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 파티 생성 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPartyCreated(PartyCreatedEvent event) {
        var party = event.getParty();
        var leader = event.getLeader();

        plugin.getLogger().info("플레이어 " + leader.getName() + "가 파티를 생성했습니다.  (파티 ID: " + party.getPartyId() + ")");

        // 리더 데이터 업데이트
        var leaderData = plugin.getDataManager().getPlayerData(leader. getUniqueId());
        if (leaderData != null) {
            leaderData.setPartyId(party.getPartyId());
            leaderData.setLeader(true);
        }

        // 플레이어에게 메시지 전송
        if (leader.isOnline()) {
            leader.sendMessage("§a파티가 생성되었습니다!");
        }
    }

    /**
     * 파티 해체 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPartyDisbanded(PartyDisbandedEvent event) {
        var party = event.getParty();
        var reason = event.getReason();

        plugin.getLogger().info("파티 " + party.getPartyId() + "가 해체되었습니다. (사유: " + reason + ")");

        // 모든 파티원 데이터 업데이트
        for (var memberId : party.getMembers()) {
            var memberData = plugin.getDataManager(). getPlayerData(memberId);
            if (memberData != null) {
                memberData.setPartyId(null);
                memberData.setLeader(false);
            }

            // 온라인 플레이어에게 메시지 전송
            var member = org.bukkit. Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage("§c파티가 해체되었습니다.");
            }
        }
    }

    /**
     * 파티 초대 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPartyInvite(PartyInviteEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var party = event.getParty();
        var inviter = event.getInviter();
        var invitee = event.getInvitee();
        var expireTime = event.getExpireTime();

        plugin.getLogger(). info("플레이어 " + inviter.getName() + "가 " + invitee.getName() 
            + "을(를) 파티에 초대했습니다.");

        // 초대 저장
        plugin.getInviteManager().addInvite(invitee. getUniqueId(), party. getPartyId(), expireTime);

        // 초대받은 플레이어에게 메시지 전송
        if (invitee.isOnline()) {
            invitee.sendMessage("§e" + inviter.getName() + "이(가) 파티 초대를 보냈습니다!");
            invitee.sendMessage("§e/party accept 로 초대를 수락하세요.  (제한 시간: " + event.getRemainingTime() + "초)");
        }

        // 초대한 플레이어에게 확인 메시지
        inviter.sendMessage("§a" + invitee.getName() + "에게 초대를 보냈습니다.");
    }
}