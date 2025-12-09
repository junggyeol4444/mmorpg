package com.multiverse.party.managers;

import com.multiverse.party.PartyCore;
import com.multiverse.party.models.Party;
import com.multiverse.party.models.MemberStatistics;
import com.multiverse.party.models.PartyStatistics;

import java.util.HashMap;
import java.util.UUID;

/**
 * 파티 전체 및 개별 멤버 통계 처리, 기록, 조회 매니저.
 * 전투력/경험치/몬스터/던전 등 모든 수치 관리.
 */
public class PartyStatisticsManager {

    private final PartyCore plugin;

    public PartyStatisticsManager(PartyCore plugin) {
        this.plugin = plugin;
    }

    public void initializeStatistics(Party party) {
        if (party == null) return;
        PartyStatistics stats = new PartyStatistics();
        stats.setMemberStats(new HashMap<>());
        party.setStatistics(stats);
        plugin.getDataManager().saveParty(party);
    }

    public void initializeMemberStats(Party party, UUID memberUUID) {
        if (party == null || memberUUID == null) return;
        PartyStatistics stats = party.getStatistics();
        if (stats == null) {
            stats = new PartyStatistics();
            party.setStatistics(stats);
        }
        stats.getMemberStats().put(memberUUID, new MemberStatistics());
        plugin.getDataManager().saveParty(party);
    }

    public void recordDamage(Party party, UUID memberUUID, double amount) {
        if (party == null || memberUUID == null || amount <= 0) return;
        PartyStatistics stats = party.getStatistics();
        if (stats == null) return;
        MemberStatistics ms = stats.getMemberStats().computeIfAbsent(memberUUID, k -> new MemberStatistics());
        ms.setDamageDealt(ms.getDamageDealt() + (long) amount);
        stats.setTotalDamage(stats.getTotalDamage() + amount);
        plugin.getDataManager().saveParty(party);
    }

    public void recordHealing(Party party, UUID memberUUID, double amount) {
        if (party == null || memberUUID == null || amount <= 0) return;
        PartyStatistics stats = party.getStatistics();
        if (stats == null) return;
        MemberStatistics ms = stats.getMemberStats().computeIfAbsent(memberUUID, k -> new MemberStatistics());
        ms.setHealingDone(ms.getHealingDone() + (long) amount);
        stats.setTotalHealing(stats.getTotalHealing() + amount);
        plugin.getDataManager().saveParty(party);
    }

    public void recordMonsterKill(Party party, UUID memberUUID) {
        if (party == null || memberUUID == null) return;
        PartyStatistics stats = party.getStatistics();
        if (stats == null) return;
        stats.setMonstersKilled(stats.getMonstersKilled() + 1);
        plugin.getDataManager().saveParty(party);
    }

    public void recordBossKill(Party party, UUID memberUUID) {
        if (party == null || memberUUID == null) return;
        PartyStatistics stats = party.getStatistics();
        if (stats == null) return;
        stats.setBossesKilled(stats.getBossesKilled() + 1);
        plugin.getDataManager().saveParty(party);
    }

    public void recordDungeonClear(Party party, String dungeonId) {
        if (party == null) return;
        PartyStatistics stats = party.getStatistics();
        if (stats == null) return;
        stats.setDungeonsCompleted(stats.getDungeonsCompleted() + 1);
        plugin.getDataManager().saveParty(party);
    }

    public void recordQuestComplete(Party party, String questId) {
        if (party == null) return;
        PartyStatistics stats = party.getStatistics();
        if (stats == null) return;
        stats.setQuestsCompleted(stats.getQuestsCompleted() + 1);
        plugin.getDataManager().saveParty(party);
    }

    public MemberStatistics getMemberStats(Party party, UUID memberUUID) {
        if (party == null || memberUUID == null) return null;
        PartyStatistics stats = party.getStatistics();
        if (stats == null) return null;
        return stats.getMemberStats().get(memberUUID);
    }

    public PartyStatistics getStatistics(Party party) {
        if (party == null) return null;
        return party.getStatistics();
    }
}