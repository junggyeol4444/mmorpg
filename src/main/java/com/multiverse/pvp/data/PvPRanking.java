package com.multiverse.pvp. data;

import com.multiverse.pvp.enums. PvPTier;

import java.util. UUID;

public class PvPRanking {

    private UUID playerId;

    // 레이팅
    private int rating;
    private int peakRating;
    private PvPTier tier;

    // 전적
    private int wins;
    private int losses;
    private int draws;

    // KDA
    private int kills;
    private int deaths;
    private int assists;

    // 연속 기록
    private int winStreak;
    private int loseStreak;
    private int maxWinStreak;
    private int maxLoseStreak;

    // 시즌
    private int currentSeason;
    private int seasonWins;
    private int seasonLosses;
    private int seasonRating;

    // PvP 포인트 (상점 등에서 사용)
    private int pvpPoints;

    // 마지막 업데이트
    private long lastUpdateTime;

    public PvPRanking(UUID playerId) {
        this.playerId = playerId;
        this.rating = 1000; // 기본 레이팅
        this.peakRating = 1000;
        this.tier = PvPTier.BRONZE;

        this.wins = 0;
        this.losses = 0;
        this.draws = 0;

        this. kills = 0;
        this.deaths = 0;
        this.assists = 0;

        this.winStreak = 0;
        this.loseStreak = 0;
        this. maxWinStreak = 0;
        this.maxLoseStreak = 0;

        this.currentSeason = 1;
        this. seasonWins = 0;
        this.seasonLosses = 0;
        this.seasonRating = 1000;

        this.pvpPoints = 0;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public PvPRanking(UUID playerId, int startingRating) {
        this(playerId);
        this.rating = startingRating;
        this.peakRating = startingRating;
        this.seasonRating = startingRating;
        updateTier();
    }

    // ==================== Getters ====================

    public UUID getPlayerId() {
        return playerId;
    }

    public int getRating() {
        return rating;
    }

    public int getPeakRating() {
        return peakRating;
    }

    public PvPTier getTier() {
        return tier;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getDraws() {
        return draws;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public int getLoseStreak() {
        return loseStreak;
    }

    public int getMaxWinStreak() {
        return maxWinStreak;
    }

    public int getMaxLoseStreak() {
        return maxLoseStreak;
    }

    public int getCurrentSeason() {
        return currentSeason;
    }

    public int getSeasonWins() {
        return seasonWins;
    }

    public int getSeasonLosses() {
        return seasonLosses;
    }

    public int getSeasonRating() {
        return seasonRating;
    }

    public int getPvpPoints() {
        return pvpPoints;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    // ==================== Setters ====================

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public void setRating(int rating) {
        this.rating = Math.max(0, rating);
        if (this.rating > this.peakRating) {
            this.peakRating = this.rating;
        }
        updateTier();
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void setPeakRating(int peakRating) {
        this.peakRating = peakRating;
    }

    public void setTier(PvPTier tier) {
        this.tier = tier;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(int deaths) {
        this. deaths = deaths;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public void setWinStreak(int winStreak) {
        this. winStreak = winStreak;
        if (this.winStreak > this.maxWinStreak) {
            this.maxWinStreak = this.winStreak;
        }
    }

    public void setLoseStreak(int loseStreak) {
        this.loseStreak = loseStreak;
        if (this.loseStreak > this. maxLoseStreak) {
            this.maxLoseStreak = this.loseStreak;
        }
    }

    public void setMaxWinStreak(int maxWinStreak) {
        this.maxWinStreak = maxWinStreak;
    }

    public void setMaxLoseStreak(int maxLoseStreak) {
        this. maxLoseStreak = maxLoseStreak;
    }

    public void setCurrentSeason(int currentSeason) {
        this.currentSeason = currentSeason;
    }

    public void setSeasonWins(int seasonWins) {
        this. seasonWins = seasonWins;
    }

    public void setSeasonLosses(int seasonLosses) {
        this.seasonLosses = seasonLosses;
    }

    public void setSeasonRating(int seasonRating) {
        this.seasonRating = seasonRating;
    }

    public void setPvpPoints(int pvpPoints) {
        this.pvpPoints = pvpPoints;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 티어 업데이트
     */
    public void updateTier() {
        this.tier = PvPTier. getTier(this.rating);
    }

    /**
     * 레이팅 추가
     */
    public void addRating(int amount) {
        setRating(this.rating + amount);
    }

    /**
     * 레이팅 감소
     */
    public void removeRating(int amount) {
        setRating(this.rating - amount);
    }

    /**
     * 승리 기록
     */
    public void recordWin(int ratingChange) {
        this.wins++;
        this.seasonWins++;
        this.winStreak++;
        this.loseStreak = 0;

        if (this.winStreak > this.maxWinStreak) {
            this.maxWinStreak = this.winStreak;
        }

        addRating(ratingChange);
        this.lastUpdateTime = System. currentTimeMillis();
    }

    /**
     * 패배 기록
     */
    public void recordLoss(int ratingChange) {
        this.losses++;
        this. seasonLosses++;
        this.loseStreak++;
        this.winStreak = 0;

        if (this.loseStreak > this.maxLoseStreak) {
            this. maxLoseStreak = this.loseStreak;
        }

        removeRating(ratingChange);
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * 무승부 기록
     */
    public void recordDraw() {
        this. draws++;
        this.winStreak = 0;
        this. loseStreak = 0;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * 킬 기록
     */
    public void addKill() {
        this. kills++;
        this.lastUpdateTime = System. currentTimeMillis();
    }

    /**
     * 킬 추가 (다수)
     */
    public void addKills(int amount) {
        this.kills += amount;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * 데스 기록
     */
    public void addDeath() {
        this.deaths++;
        this.lastUpdateTime = System. currentTimeMillis();
    }

    /**
     * 어시스트 기록
     */
    public void addAssist() {
        this.assists++;
        this. lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * PvP 포인트 추가
     */
    public void addPvpPoints(int amount) {
        this.pvpPoints += amount;
    }

    /**
     * PvP 포인트 사용
     */
    public boolean usePvpPoints(int amount) {
        if (this.pvpPoints >= amount) {
            this.pvpPoints -= amount;
            return true;
        }
        return false;
    }

    /**
     * KDA 계산
     */
    public double getKDA() {
        if (deaths == 0) {
            return kills + (assists * 0.5);
        }
        return (kills + (assists * 0.5)) / (double) deaths;
    }

    /**
     * 승률 계산
     */
    public double getWinRate() {
        int totalGames = wins + losses;
        if (totalGames == 0) {
            return 0.0;
        }
        return (wins * 100.0) / totalGames;
    }

    /**
     * 시즌 승률 계산
     */
    public double getSeasonWinRate() {
        int totalGames = seasonWins + seasonLosses;
        if (totalGames == 0) {
            return 0.0;
        }
        return (seasonWins * 100.0) / totalGames;
    }

    /**
     * 총 경기 수
     */
    public int getTotalGames() {
        return wins + losses + draws;
    }

    /**
     * 시즌 총 경기 수
     */
    public int getSeasonTotalGames() {
        return seasonWins + seasonLosses;
    }

    /**
     * 시즌 레이팅 변화
     */
    public int getSeasonRatingChange() {
        return rating - seasonRating;
    }

    /**
     * 시즌 초기화
     */
    public void resetSeason(int newSeason, double resetPercentage) {
        this.currentSeason = newSeason;
        this.seasonWins = 0;
        this.seasonLosses = 0;
        
        // 레이팅 부분 초기화
        int baseRating = 1000;
        int ratingDiff = this.rating - baseRating;
        int resetAmount = (int) (ratingDiff * (1 - resetPercentage / 100.0));
        this.rating = baseRating + resetAmount;
        this.seasonRating = this.rating;
        
        updateTier();
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * 전체 초기화
     */
    public void reset() {
        this.rating = 1000;
        this.peakRating = 1000;
        this.tier = PvPTier. BRONZE;

        this.wins = 0;
        this.losses = 0;
        this.draws = 0;

        this. kills = 0;
        this.deaths = 0;
        this.assists = 0;

        this.winStreak = 0;
        this.loseStreak = 0;
        this. maxWinStreak = 0;
        this.maxLoseStreak = 0;

        this.seasonWins = 0;
        this. seasonLosses = 0;
        this.seasonRating = 1000;

        this.pvpPoints = 0;
        this. lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * 다음 티어까지 필요한 레이팅
     */
    public int getRatingToNextTier() {
        PvPTier nextTier = tier.getNextTier();
        if (nextTier == null) {
            return 0;
        }
        return nextTier.getMinRating() - rating;
    }

    /**
     * 티어 승급 가능 여부
     */
    public boolean canPromote() {
        PvPTier nextTier = tier. getNextTier();
        return nextTier != null && rating >= nextTier. getMinRating();
    }

    /**
     * 티어 강등 위험 여부
     */
    public boolean isAtRiskOfDemotion() {
        return rating <= tier.getMinRating() + 50;
    }

    /**
     * 복사본 생성
     */
    public PvPRanking clone() {
        PvPRanking clone = new PvPRanking(this.playerId);
        clone.rating = this.rating;
        clone.peakRating = this.peakRating;
        clone. tier = this.tier;
        clone. wins = this.wins;
        clone. losses = this.losses;
        clone. draws = this.draws;
        clone. kills = this.kills;
        clone. deaths = this.deaths;
        clone. assists = this.assists;
        clone. winStreak = this.winStreak;
        clone.loseStreak = this.loseStreak;
        clone. maxWinStreak = this.maxWinStreak;
        clone.maxLoseStreak = this.maxLoseStreak;
        clone.currentSeason = this.currentSeason;
        clone. seasonWins = this.seasonWins;
        clone.seasonLosses = this.seasonLosses;
        clone. seasonRating = this.seasonRating;
        clone.pvpPoints = this. pvpPoints;
        clone.lastUpdateTime = this.lastUpdateTime;
        return clone;
    }

    @Override
    public String toString() {
        return "PvPRanking{" +
                "playerId=" + playerId +
                ", rating=" + rating +
                ", tier=" + tier +
                ", wins=" + wins +
                ", losses=" + losses +
                ", kda=" + String.format("%.2f", getKDA()) +
                ", winRate=" + String. format("%.1f", getWinRate()) + "%" +
                '}';
    }
}