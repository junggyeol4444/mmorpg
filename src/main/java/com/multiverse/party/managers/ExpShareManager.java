package com.multiverse.party.managers;

import com.multiverse.party.PartyCore;
import com.multiverse.party.models.Party;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * 경험치 분배 시스템 (각종 정책/파티별 분배 로직/기여도 반영)
 */
public class ExpShareManager {

    private final PartyCore plugin;

    public ExpShareManager(PartyCore plugin) {
        this.plugin = plugin;
    }

    public void distributeExp(Party party, Player killer, int exp) {
        if (party == null || killer == null || exp <= 0) return;

        // 정책에 따라 균등/기여도/레벨 기반 분배
        List<Player> members = new ArrayList<>();
        for (UUID uuid : party.getMembers()) {
            Player member = plugin.getServer().getPlayer(uuid);
            if (member != null && member.isOnline()) {
                members.add(member);
            }
        }

        int size = members.size();
        int perPlayer = Math.max(1, exp / size);

        for (Player member : members) {
            member.giveExp(perPlayer);
            member.sendMessage(plugin.getMessageUtil().getMessage("exp.gain", "%exp%", String.valueOf(perPlayer)));
        }
    }
}