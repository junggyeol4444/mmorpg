package com.multiverse.pvp. data;

import com.multiverse.pvp.enums.PvPType;

import java.util.HashSet;
import java.util.Set;
import java.util. UUID;

public class PvPMode {

    private UUID playerId;
    private PvPType type;
    private boolean enabled;

    // 보호
    private int protectionLevel;
    private long protectionEndTime;

    // 제한
    private Set<UUID> blacklist;

    // 설정
    private boolean allowPartyPvP;
    private boolean allowGuildPvP;

    // 마지막 전투 시간 (전투 태그용)
    private long lastCombatTime;
    private UUID lastAttacker;

    public PvPMode(UUID playerId) {
        this. playerId = playerId;
        this. type = PvPType.CONSENSUAL;
        this.enabled = false;
        this. protectionLevel = 0;
        this.protectionEndTime = 0;
        this.blacklist = new HashSet<>();
        this.allowPartyPvP = false;
        this. allowGuildPvP = false;
        this.lastCombatTime = 0;
        this.lastAttacker = null;
    }

    public PvPMode(UUID playerId, PvPType type, boolean enabled) {
        this(playerId);
        this.type = type;
        this. enabled = enabled;
    }

    // ==================== Getters ====================

    public UUID getPlayerId() {
        return playerId;
    }

    public PvPType getType() {
        return type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getProtectionLevel() {
        return protectionLevel;
    }

    public long getProtectionEndTime() {
        return protectionEndTime;
    }

    public Set<UUID> getBlacklist() {
        return blacklist;
    }

    public boolean isAllowPartyPvP() {
        return allowPartyPvP;
    }

    public boolean isAllowGuildPvP() {
        return allowGuildPvP;
    }

    public long getLastCombatTime() {
        return lastCombatTime;
    }

    public UUID getLastAttacker() {
        return lastAttacker;
    }

    // ==================== Setters ====================

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public void setType(PvPType type) {
        this.type = type;
    }

    public void setEnabled(boolean enabled) {
        this. enabled = enabled;
    }

    public void setProtectionLevel(int protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    public void setProtectionEndTime(long protectionEndTime) {
        this.protectionEndTime = protectionEndTime;
    }

    public void setBlacklist(Set<UUID> blacklist) {
        this.blacklist = blacklist;
    }

    public void setAllowPartyPvP(boolean allowPartyPvP) {
        this.allowPartyPvP = allowPartyPvP;
    }

    public void setAllowGuildPvP(boolean allowGuildPvP) {
        this. allowGuildPvP = allowGuildPvP;
    }

    public void setLastCombatTime(long lastCombatTime) {
        this. lastCombatTime = lastCombatTime;
    }

    public void setLastAttacker(UUID lastAttacker) {
        this.lastAttacker = lastAttacker;
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 신규 유저 보호 상태인지 확인
     */
    public boolean isNewPlayerProtected() {
        return protectionEndTime > 0 && System.currentTimeMillis() < protectionEndTime;
    }

    /**
     * 남은 보호 시간 (초)
     */
    public long getRemainingProtectionTime() {
        if (! isNewPlayerProtected()) {
            return 0;
        }
        return (protectionEndTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 보호 종료
     */
    public void endProtection() {
        this.protectionEndTime = 0;
    }

    /**
     * 보호 설정 (초 단위)
     */
    public void setProtection(int durationSeconds) {
        this.protectionEndTime = System.currentTimeMillis() + (durationSeconds * 1000L);
    }

    /**
     * 블랙리스트에 플레이어 추가
     */
    public void addToBlacklist(UUID playerId) {
        this.blacklist.add(playerId);
    }

    /**
     * 블랙리스트에서 플레이어 제거
     */
    public void removeFromBlacklist(UUID playerId) {
        this.blacklist.remove(playerId);
    }

    /**
     * 블랙리스트에 있는지 확인
     */
    public boolean isBlacklisted(UUID playerId) {
        return this.blacklist.contains(playerId);
    }

    /**
     * 전투 중인지 확인 (마지막 전투로부터 지정된 시간 이내)
     */
    public boolean isInCombat(int combatDurationSeconds) {
        if (lastCombatTime == 0) {
            return false;
        }
        long timeSinceCombat = System.currentTimeMillis() - lastCombatTime;
        return timeSinceCombat < (combatDurationSeconds * 1000L);
    }

    /**
     * 전투 태그 업데이트
     */
    public void updateCombatTag(UUID attacker) {
        this. lastCombatTime = System.currentTimeMillis();
        this.lastAttacker = attacker;
    }

    /**
     * 전투 태그 초기화
     */
    public void clearCombatTag() {
        this. lastCombatTime = 0;
        this.lastAttacker = null;
    }

    /**
     * PvP 모드 토글
     */
    public void toggle() {
        this.enabled = !this.enabled;
    }

    /**
     * 데이터 초기화
     */
    public void reset() {
        this.type = PvPType.CONSENSUAL;
        this.enabled = false;
        this.protectionLevel = 0;
        this.protectionEndTime = 0;
        this. blacklist.clear();
        this.allowPartyPvP = false;
        this.allowGuildPvP = false;
        this. lastCombatTime = 0;
        this.lastAttacker = null;
    }

    /**
     * 복사본 생성
     */
    public PvPMode clone() {
        PvPMode clone = new PvPMode(this.playerId);
        clone.type = this.type;
        clone.enabled = this.enabled;
        clone.protectionLevel = this. protectionLevel;
        clone.protectionEndTime = this. protectionEndTime;
        clone.blacklist = new HashSet<>(this.blacklist);
        clone.allowPartyPvP = this. allowPartyPvP;
        clone.allowGuildPvP = this. allowGuildPvP;
        clone.lastCombatTime = this.lastCombatTime;
        clone.lastAttacker = this. lastAttacker;
        return clone;
    }

    @Override
    public String toString() {
        return "PvPMode{" +
                "playerId=" + playerId +
                ", type=" + type +
                ", enabled=" + enabled +
                ", protectionLevel=" + protectionLevel +
                ", protectionEndTime=" + protectionEndTime +
                ", blacklistSize=" + blacklist.size() +
                ", allowPartyPvP=" + allowPartyPvP +
                ", allowGuildPvP=" + allowGuildPvP +
                ", inCombat=" + isInCombat(15) +
                '}';
    }
}