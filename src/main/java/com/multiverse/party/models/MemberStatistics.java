package com.multiverse.party.models;

/**
 * 개별 파티 멤버의 데미지, 힐, MVP 등 통계
 */
public class MemberStatistics {
    private long damageDealt;
    private long healingDone;
    private int mvpCount;

    public long getDamageDealt() { return damageDealt; }
    public void setDamageDealt(long damageDealt) { this.damageDealt = damageDealt; }

    public long getHealingDone() { return healingDone; }
    public void setHealingDone(long healingDone) { this.healingDone = healingDone; }

    public int getMvpCount() { return mvpCount; }
    public void setMvpCount(int mvpCount) { this.mvpCount = mvpCount; }
}