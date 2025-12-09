package com.multiverse.party.managers;

import com.multiverse.party.PartyCore;
import com.multiverse.party.models.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * 파티 시스템 내 채팅, 알림, 파티 메시지, 공지 등 처리 매니저
 */
public class PartyChatManager {

    private final PartyCore plugin;

    public PartyChatManager(PartyCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 파티 전체에 일반 메시지 브로드캐스트
     */
    public void sendPartyMessage(Player sender, Party party, String message) {
        if (party == null || message == null) return;
        String displayMsg = plugin.getMessageUtil().getMessage("chat.party-format",
                "%player%", sender.getName(), "%msg%", message);
        for (UUID uuid : party.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                p.sendMessage(displayMsg);
            }
        }
    }

    /**
     * 파티원 모두에게 서버 알림 메시지 브로드캐스트
     */
    public void sendNotification(Party party, String message) {
        if (party == null || message == null) return;
        for (UUID uuid : party.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                p.sendMessage(message);
            }
        }
    }

    /**
     * 파티에 새 멤버가 참가했을 때 안내, 기존 멤버에게 알림
     */
    public void notifyMemberJoin(Party party, Player newMember) {
        sendNotification(party, plugin.getMessageUtil().getMessage("party.member-join",
                "%player%", newMember.getName()));
        newMember.sendMessage(plugin.getMessageUtil().getMessage("party.join-success",
                "%party%", party.getPartyName() != null ? party.getPartyName() : "파티"));
    }

    /**
     * 공지/중요 메시지 등 추가 기능 확장 가능
     */
    public void sendAnnouncement(Party party, String announcement) {
        sendNotification(party, plugin.getMessageUtil().getMessage("chat.announcement",
                "%msg%", announcement));
    }
}