package com.multiverse. pvp.data;

import com.multiverse.pvp.enums. StreakLevel;

import java. util.EnumMap;
import java.util.Map;
import java.util. UUID;

public class KillStreak {

    private UUID playerId;
    private int currentStreak;
    private int bestStreak;

    // 스트릭 단계
    private StreakLevel currentLevel;

    // 마지막 킬
    private long lastKillTime;
    private UUID lastVictim;

    // 통계
    private Map<StreakLevel, Integer> streakAchievements;

    // 시간 제한 (연속 킬 인정 시간, 밀리초)
    private long streakTimeLimit;

    // 세션 통계
    private int sessionKills;
    private int sessionDeaths;
    private int sessionBestStreak;

    public KillStreak(UUID playerId) {
        this. playerId = playerId;
        this. currentStreak = 0;
        this.bestStreak = 0;
        this.currentLevel = null;
        this. lastKillTime = 0;
        this.lastVictim = null;

        this.streakAchievements = new EnumMap<>(StreakLevel.class);
        for (StreakLevel level : StreakLevel.values()) {
            streakAchievements.put(level, 0);
        }

        this.streakTimeLimit = 30000; // 30초

        this.sessionKills = 0;
        this.sessionDeaths = 0;
        this. sessionBestStreak = 0;
    }

    // ==================== Getters ====================

    public UUID getPlayerId() {
        return playerId;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public StreakLevel getCurrentLevel() {
        return currentLevel;
    }

    public long getLastKillTime() {
        return lastKillTime;
    }

    public UUID getLastVictim() {
        return lastVictim;
    }

    public Map<StreakLevel, Integer> getStreakAchievements() {
        return streakAchievements;
    }

    public long getStreakTimeLimit() {
        return streakTimeLimit;
    }

    public int getSessionKills() {
        return sessionKills;
    }

    public int getSessionDeaths() {
        return sessionDeaths;
    }

    public int getSessionBestStreak() {
        return sessionBestStreak;
    }

    // ==================== Setters ====================

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
        updateCurrentLevel();
        if (this.currentStreak > this.bestStreak) {
            this. bestStreak = this.currentStreak;
        }
        if (this.currentStreak > this.sessionBestStreak) {
            this.sessionBestStreak = this. currentStreak;
        }
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public void setCurrentLevel(StreakLevel currentLevel) {
        this. currentLevel = currentLevel;
    }

    public void setLastKillTime(long lastKillTime) {
        this.lastKillTime = lastKillTime;
    }

    public void setLastVictim(UUID lastVictim) {
        this.lastVictim = lastVictim;
    }

    public void setStreakAchievements(Map<StreakLevel, Integer> streakAchievements) {
        this. streakAchievements = streakAchievements;
    }

    public void setStreakTimeLimit(long streakTimeLimit) {
        this.streakTimeLimit = streakTimeLimit;
    }

    public void setSessionKills(int sessionKills) {
        this. sessionKills = sessionKills;
    }

    public void setSessionDeaths(int sessionDeaths) {
        this. sessionDeaths = sessionDeaths;
    }

    public void setSessionBestStreak(int sessionBestStreak) {
        this.sessionBestStreak = sessionBestStreak;
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 현재 스트릭 레벨 업데이트
     */
    private void updateCurrentLevel() {
        this.currentLevel = StreakLevel.getLevel(this.currentStreak);
    }

    /**
     * 킬 추가
     * @return 새로운 스트릭 레벨 (레벨업 시), null (레벨업 없음)
     */
    public StreakLevel addKill(UUID victimId) {
        long currentTime = System. currentTimeMillis();

        // 시간 제한 체크 (아레나/듀얼에서는 시간 제한 무시 가능)
        if (lastKillTime > 0 && (currentTime - lastKillTime) > streakTimeLimit) {
            // 시간 초과로 스트릭 초기화
            this. currentStreak = 0;
        }

        // 킬 추가
        this.currentStreak++;
        this.sessionKills++;
        this.lastKillTime = currentTime;
        this.lastVictim = victimId;

        // 최고 기록 업데이트
        if (this.currentStreak > this.bestStreak) {
            this.bestStreak = this.currentStreak;
        }
        if (this. currentStreak > this.sessionBestStreak) {
            this.sessionBestStreak = this. currentStreak;
        }

        // 이전 레벨
        StreakLevel previousLevel = this.currentLevel;

        // 현재 레벨 업데이트
        updateCurrentLevel();

        // 레벨업 체크
        if (this.currentLevel != null && this.currentLevel != previousLevel) {
            // 업적 기록
            incrementAchievement(this.currentLevel);
            return this.currentLevel;
        }

        return null;
    }

    /**
     * 스트릭 초기화 (사망 시)
     * @return 차단된 스트릭 레벨 (차단 보상용)
     */
    public StreakLevel resetStreak() {
        StreakLevel shutdownLevel = this.currentLevel;
        
        this.currentStreak = 0;
        this.currentLevel = null;
        this.lastKillTime = 0;
        this. lastVictim = null;
        this.sessionDeaths++;

        return shutdownLevel;
    }

    /**
     * 업적 증가
     */
    public void incrementAchievement(StreakLevel level) {
        streakAchievements.put(level, streakAchievements.getOrDefault(level, 0) + 1);
    }

    /**
     * 특정 레벨 달성 횟수 조회
     */
    public int getAchievementCount(StreakLevel level) {
        return streakAchievements. getOrDefault(level, 0);
    }

    /**
     * 총 업적 달성 횟수
     */
    public int getTotalAchievements() {
        int total = 0;
        for (int count : streakAchievements.values()) {
            total += count;
        }
        return total;
    }

    /**
     * 스트릭이 활성 상태인지 확인
     */
    public boolean isStreakActive() {
        if (lastKillTime == 0) {
            return false;
        }
        return (System.currentTimeMillis() - lastKillTime) <= streakTimeLimit;
    }

    /**
     * 스트릭 남은 시간 (밀리초)
     */
    public long getStreakRemainingTime() {
        if (!isStreakActive()) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - lastKillTime;
        return Math.max(0, streakTimeLimit - elapsed);
    }

    /**
     * 다음 스트릭 레벨까지 필요한 킬 수
     */
    public int getKillsToNextLevel() {
        StreakLevel nextLevel = StreakLevel.getNextLevel(currentStreak);
        if (nextLevel == null) {
            return 0;
        }
        return nextLevel.getKillsRequired() - currentStreak;
    }

    /**
     * 현재 보상 배율
     */
    public double getCurrentMultiplier() {
        if (currentLevel == null) {
            return 1.0;
        }
        return currentLevel.getRewardMultiplier();
    }

    /**
     * 세션 KDA 계산
     */
    public double getSessionKDA() {
        if (sessionDeaths == 0) {
            return sessionKills;
        }
        return (double) sessionKills / sessionDeaths;
    }

    /**
     * 세션 초기화
     */
    public void resetSession() {
        this. sessionKills = 0;
        this.sessionDeaths = 0;
        this.sessionBestStreak = 0;
    }

    /**
     * 전체 초기화
     */
    public void reset() {
        this.currentStreak = 0;
        this.bestStreak = 0;
        this. currentLevel = null;
        this.lastKillTime = 0;
        this. lastVictim = null;
        
        for (StreakLevel level :  StreakLevel. values()) {
            streakAchievements.put(level, 0);
        }

        resetSession();
    }

    /**
     * 시간 제한 없이 킬 추가 (아레나/듀얼용)
     */
    public StreakLevel addKillNoTimeLimit(UUID victimId) {
        this.currentStreak++;
        this.sessionKills++;
        this.lastKillTime = System.currentTimeMillis();
        this.lastVictim = victimId;

        if (this.currentStreak > this.bestStreak) {
            this.bestStreak = this.currentStreak;
        }
        if (this.currentStreak > this.sessionBestStreak) {
            this.sessionBestStreak = this.currentStreak;
        }

        StreakLevel previousLevel = this.currentLevel;
        updateCurrentLevel();

        if (this.currentLevel != null && this. currentLevel != previousLevel) {
            incrementAchievement(this.currentLevel);
            return this. currentLevel;
        }

        return null;
    }

    /**
     * 스트릭 레벨 체크 (공지용)
     */
    public boolean shouldAnnounce() {
        return currentLevel != null && currentStreak == currentLevel.getKillsRequired();
    }

    /**
     * 복사본 생성
     */
    public KillStreak clone() {
        KillStreak clone = new KillStreak(this.playerId);
        clone.currentStreak = this.currentStreak;
        clone.bestStreak = this.bestStreak;
        clone.currentLevel = this.currentLevel;
        clone.lastKillTime = this. lastKillTime;
        clone.lastVictim = this.lastVictim;
        clone. streakAchievements = new EnumMap<>(this.streakAchievements);
        clone.streakTimeLimit = this.streakTimeLimit;
        clone.sessionKills = this.sessionKills;
        clone.sessionDeaths = this.sessionDeaths;
        clone. sessionBestStreak = this.sessionBestStreak;
        return clone;
    }

    @Override
    public String toString() {
        return "KillStreak{" +
                "playerId=" + playerId +
                ", currentStreak=" + currentStreak +
                ", bestStreak=" + bestStreak +
                ", currentLevel=" + (currentLevel != null ?  currentLevel.name() : "NONE") +
                ", sessionKills=" + sessionKills +
                ", sessionDeaths=" + sessionDeaths +
                '}';
    }
}