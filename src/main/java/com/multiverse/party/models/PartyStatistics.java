package com.multiverse.party.models;

import java.util.*;

/**
 * 파티 전체 통계 (총 처치, 힐, 퀘스트, 던전 등) 및 멤버 개별 통계 맵
 */
public class PartyStatistics {
    private long totalDamage;
    private long totalHealing;
    private int monstersKilled;
    private int bossesKilled;
    private int dungeonsCompleted;
    private int questsCompleted;
    private Map<UUID, MemberStatistics> memberStats;

    public long getTotalDamage() { return totalDamage; }
    public void setTotalDamage(long totalDamage) { this.totalDamage = totalDamage; }

    public long getTotalHealing() { return totalHealing; }
    public void setTotalHealing(long totalHealing) { this.totalHealing = totalHealing; }

    public int getMonstersKilled() { return monstersKilled; }
    public void setMonstersKilled(int monstersKilled) { this.monstersKilled = monstersKilled; }

    public int getBossesKilled() { return bossesKilled; }
    public void setBossesKilled(int bossesKilled) { this.bossesKilled = bossesKilled; }

    public int getDungeonsCompleted() { return dungeonsCompleted; }
    public void setDungeonsCompleted(int dungeonsCompleted) { this.dungeonsCompleted = dungeonsCompleted; }

    public int getQuestsCompleted() { return questsCompleted; }
    public void setQuestsCompleted(int questsCompleted) { this.questsCompleted = questsCompleted; }

    public Map<UUID, MemberStatistics> getMemberStats() {
        if (memberStats == null) memberStats = new HashMap<>();
        return memberStats;
    }
    public void setMemberStats(Map<UUID, MemberStatistics> memberStats) { this.memberStats = memberStats; }
}