package com. multiverse.pvp.data;

import org.bukkit.Material;

import java.time.LocalDate;
import java. time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util. UUID;

public class PvPStatistics {

    private UUID playerId;

    // 기본 통계
    private int totalKills;
    private int totalDeaths;
    private int totalAssists;
    private int totalWins;
    private int totalLosses;

    // KDA
    private double kda;

    // 무기별 킬
    private Map<Material, Integer> weaponKills;

    // 최고 기록
    private int longestKillStreak;
    private double highestDamageDealt;
    private int mostKillsInMatch;

    // 시간대별 (날짜 문자열 -> 킬 수)
    private Map<String, Integer> dailyKills;
    private Map<String, Integer> weeklyKills;
    private Map<String, Integer> monthlyKills;

    // 상대별 통계 (상대 UUID -> 킬 수)
    private Map<UUID, Integer> killsAgainstPlayer;
    private Map<UUID, Integer> deathsFromPlayer;

    // 아레나 통계
    private Map<String, Integer> arenaWins;
    private Map<String, Integer> arenaLosses;
    private Map<String, Integer> arenaKills;

    // 듀얼 통계
    private int duelWins;
    private int duelLosses;
    private int duelSurrenders;

    // 데미지 통계
    private long totalDamageDealt;
    private long totalDamageReceived;
    private long totalHealing;

    // 기타 통계
    private int firstBloods;
    private int shutdowns;
    private int revenges;
    private int doubleKills;
    private int tripleKills;
    private int multiKills;

    // 시간 통계
    private long totalPlayTime; // 밀리초
    private long lastPlayTime;

    // 날짜 포맷터
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter WEEK_FORMATTER = DateTimeFormatter.ofPattern("yyyy-'W'ww");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public PvPStatistics(UUID playerId) {
        this.playerId = playerId;

        this.totalKills = 0;
        this.totalDeaths = 0;
        this.totalAssists = 0;
        this.totalWins = 0;
        this. totalLosses = 0;
        this.kda = 0.0;

        this. weaponKills = new HashMap<>();
        this.longestKillStreak = 0;
        this.highestDamageDealt = 0;
        this.mostKillsInMatch = 0;

        this.dailyKills = new HashMap<>();
        this.weeklyKills = new HashMap<>();
        this.monthlyKills = new HashMap<>();

        this.killsAgainstPlayer = new HashMap<>();
        this.deathsFromPlayer = new HashMap<>();

        this.arenaWins = new HashMap<>();
        this.arenaLosses = new HashMap<>();
        this.arenaKills = new HashMap<>();

        this.duelWins = 0;
        this. duelLosses = 0;
        this.duelSurrenders = 0;

        this.totalDamageDealt = 0;
        this. totalDamageReceived = 0;
        this.totalHealing = 0;

        this. firstBloods = 0;
        this.shutdowns = 0;
        this.revenges = 0;
        this.doubleKills = 0;
        this. tripleKills = 0;
        this.multiKills = 0;

        this.totalPlayTime = 0;
        this.lastPlayTime = System.currentTimeMillis();
    }

    // ==================== Getters ====================

    public UUID getPlayerId() {
        return playerId;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public int getTotalAssists() {
        return totalAssists;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public double getKda() {
        return kda;
    }

    public Map<Material, Integer> getWeaponKills() {
        return weaponKills;
    }

    public int getLongestKillStreak() {
        return longestKillStreak;
    }

    public double getHighestDamageDealt() {
        return highestDamageDealt;
    }

    public int getMostKillsInMatch() {
        return mostKillsInMatch;
    }

    public Map<String, Integer> getDailyKills() {
        return dailyKills;
    }

    public Map<String, Integer> getWeeklyKills() {
        return weeklyKills;
    }

    public Map<String, Integer> getMonthlyKills() {
        return monthlyKills;
    }

    public Map<UUID, Integer> getKillsAgainstPlayer() {
        return killsAgainstPlayer;
    }

    public Map<UUID, Integer> getDeathsFromPlayer() {
        return deathsFromPlayer;
    }

    public Map<String, Integer> getArenaWins() {
        return arenaWins;
    }

    public Map<String, Integer> getArenaLosses() {
        return arenaLosses;
    }

    public Map<String, Integer> getArenaKills() {
        return arenaKills;
    }

    public int getDuelWins() {
        return duelWins;
    }

    public int getDuelLosses() {
        return duelLosses;
    }

    public int getDuelSurrenders() {
        return duelSurrenders;
    }

    public long getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public long getTotalDamageReceived() {
        return totalDamageReceived;
    }

    public long getTotalHealing() {
        return totalHealing;
    }

    public int getFirstBloods() {
        return firstBloods;
    }

    public int getShutdowns() {
        return shutdowns;
    }

    public int getRevenges() {
        return revenges;
    }

    public int getDoubleKills() {
        return doubleKills;
    }

    public int getTripleKills() {
        return tripleKills;
    }

    public int getMultiKills() {
        return multiKills;
    }

    public long getTotalPlayTime() {
        return totalPlayTime;
    }

    public long getLastPlayTime() {
        return lastPlayTime;
    }

    // ==================== Setters ====================

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public void setTotalKills(int totalKills) {
        this. totalKills = totalKills;
        updateKDA();
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
        updateKDA();
    }

    public void setTotalAssists(int totalAssists) {
        this.totalAssists = totalAssists;
        updateKDA();
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public void setTotalLosses(int totalLosses) {
        this.totalLosses = totalLosses;
    }

    public void setKda(double kda) {
        this.kda = kda;
    }

    public void setWeaponKills(Map<Material, Integer> weaponKills) {
        this.weaponKills = weaponKills;
    }

    public void setLongestKillStreak(int longestKillStreak) {
        this.longestKillStreak = longestKillStreak;
    }

    public void setHighestDamageDealt(double highestDamageDealt) {
        this.highestDamageDealt = highestDamageDealt;
    }

    public void setMostKillsInMatch(int mostKillsInMatch) {
        this.mostKillsInMatch = mostKillsInMatch;
    }

    public void setDailyKills(Map<String, Integer> dailyKills) {
        this.dailyKills = dailyKills;
    }

    public void setWeeklyKills(Map<String, Integer> weeklyKills) {
        this.weeklyKills = weeklyKills;
    }

    public void setMonthlyKills(Map<String, Integer> monthlyKills) {
        this.monthlyKills = monthlyKills;
    }

    public void setKillsAgainstPlayer(Map<UUID, Integer> killsAgainstPlayer) {
        this. killsAgainstPlayer = killsAgainstPlayer;
    }

    public void setDeathsFromPlayer(Map<UUID, Integer> deathsFromPlayer) {
        this. deathsFromPlayer = deathsFromPlayer;
    }

    public void setArenaWins(Map<String, Integer> arenaWins) {
        this.arenaWins = arenaWins;
    }

    public void setArenaLosses(Map<String, Integer> arenaLosses) {
        this.arenaLosses = arenaLosses;
    }

    public void setArenaKills(Map<String, Integer> arenaKills) {
        this.arenaKills = arenaKills;
    }

    public void setDuelWins(int duelWins) {
        this.duelWins = duelWins;
    }

    public void setDuelLosses(int duelLosses) {
        this.duelLosses = duelLosses;
    }

    public void setDuelSurrenders(int duelSurrenders) {
        this.duelSurrenders = duelSurrenders;
    }

    public void setTotalDamageDealt(long totalDamageDealt) {
        this.totalDamageDealt = totalDamageDealt;
    }

    public void setTotalDamageReceived(long totalDamageReceived) {
        this. totalDamageReceived = totalDamageReceived;
    }

    public void setTotalHealing(long totalHealing) {
        this.totalHealing = totalHealing;
    }

    public void setFirstBloods(int firstBloods) {
        this.firstBloods = firstBloods;
    }

    public void setShutdowns(int shutdowns) {
        this. shutdowns = shutdowns;
    }

    public void setRevenges(int revenges) {
        this.revenges = revenges;
    }

    public void setDoubleKills(int doubleKills) {
        this. doubleKills = doubleKills;
    }

    public void setTripleKills(int tripleKills) {
        this.tripleKills = tripleKills;
    }

    public void setMultiKills(int multiKills) {
        this.multiKills = multiKills;
    }

    public void setTotalPlayTime(long totalPlayTime) {
        this.totalPlayTime = totalPlayTime;
    }

    public void setLastPlayTime(long lastPlayTime) {
        this.lastPlayTime = lastPlayTime;
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * KDA 업데이트
     */
    public void updateKDA() {
        if (totalDeaths == 0) {
            this.kda = totalKills + (totalAssists * 0.5);
        } else {
            this. kda = (totalKills + (totalAssists * 0.5)) / (double) totalDeaths;
        }
    }

    /**
     * 킬 기록
     */
    public void recordKill(UUID victimId, Material weapon) {
        this.totalKills++;
        
        // 무기별 킬
        if (weapon != null) {
            weaponKills.put(weapon, weaponKills.getOrDefault(weapon, 0) + 1);
        }
        
        // 상대별 킬
        killsAgainstPlayer.put(victimId, killsAgainstPlayer.getOrDefault(victimId, 0) + 1);
        
        // 시간대별 킬
        LocalDate today = LocalDate.now();
        String dailyKey = today. format(DATE_FORMATTER);
        String weeklyKey = today.format(WEEK_FORMATTER);
        String monthlyKey = today.format(MONTH_FORMATTER);
        
        dailyKills.put(dailyKey, dailyKills. getOrDefault(dailyKey, 0) + 1);
        weeklyKills.put(weeklyKey, weeklyKills.getOrDefault(weeklyKey, 0) + 1);
        monthlyKills. put(monthlyKey, monthlyKills.getOrDefault(monthlyKey, 0) + 1);
        
        updateKDA();
    }

    /**
     * 데스 기록
     */
    public void recordDeath(UUID killerId) {
        this.totalDeaths++;
        
        if (killerId != null) {
            deathsFromPlayer. put(killerId, deathsFromPlayer.getOrDefault(killerId, 0) + 1);
        }
        
        updateKDA();
    }

    /**
     * 어시스트 기록
     */
    public void recordAssist() {
        this. totalAssists++;
        updateKDA();
    }

    /**
     * 승리 기록
     */
    public void recordWin(String arenaType) {
        this.totalWins++;
        
        if (arenaType != null) {
            arenaWins. put(arenaType, arenaWins.getOrDefault(arenaType, 0) + 1);
        }
    }

    /**
     * 패배 기록
     */
    public void recordLoss(String arenaType) {
        this. totalLosses++;
        
        if (arenaType != null) {
            arenaLosses.put(arenaType, arenaLosses.getOrDefault(arenaType, 0) + 1);
        }
    }

    /**
     * 듀얼 승리 기록
     */
    public void recordDuelWin() {
        this. duelWins++;
        this.totalWins++;
    }

    /**
     * 듀얼 패배 기록
     */
    public void recordDuelLoss() {
        this.duelLosses++;
        this.totalLosses++;
    }

    /**
     * 듀얼 항복 기록
     */
    public void recordDuelSurrender() {
        this.duelSurrenders++;
        this. duelLosses++;
        this.totalLosses++;
    }

    /**
     * 데미지 기록
     */
    public void recordDamageDealt(double damage) {
        this.totalDamageDealt += (long) damage;
        
        if (damage > this.highestDamageDealt) {
            this.highestDamageDealt = damage;
        }
    }

    /**
     * 받은 데미지 기록
     */
    public void recordDamageReceived(double damage) {
        this.totalDamageReceived += (long) damage;
    }

    /**
     * 힐링 기록
     */
    public void recordHealing(double amount) {
        this.totalHealing += (long) amount;
    }

    /**
     * 킬 스트릭 업데이트
     */
    public void updateKillStreak(int streak) {
        if (streak > this. longestKillStreak) {
            this.longestKillStreak = streak;
        }
    }

    /**
     * 경기 내 킬 업데이트
     */
    public void updateMatchKills(int kills) {
        if (kills > this. mostKillsInMatch) {
            this.mostKillsInMatch = kills;
        }
    }

    /**
     * 퍼스트 블러드 기록
     */
    public void recordFirstBlood() {
        this. firstBloods++;
    }

    /**
     * 셧다운 기록
     */
    public void recordShutdown() {
        this.shutdowns++;
    }

    /**
     * 리벤지 기록
     */
    public void recordRevenge() {
        this.revenges++;
    }

    /**
     * 더블 킬 기록
     */
    public void recordDoubleKill() {
        this.doubleKills++;
    }

    /**
     * 트리플 킬 기록
     */
    public void recordTripleKill() {
        this.tripleKills++;
    }

    /**
     * 멀티 킬 기록
     */
    public void recordMultiKill() {
        this.multiKills++;
    }

    /**
     * 플레이 시간 업데이트
     */
    public void updatePlayTime() {
        long now = System.currentTimeMillis();
        if (lastPlayTime > 0) {
            long sessionTime = now - lastPlayTime;
            // 최대 30분까지만 세션으로 인정
            if (sessionTime < 1800000) {
                this. totalPlayTime += sessionTime;
            }
        }
        this.lastPlayTime = now;
    }

    /**
     * 승률 계산
     */
    public double getWinRate() {
        int totalGames = totalWins + totalLosses;
        if (totalGames == 0) {
            return 0. 0;
        }
        return (totalWins * 100.0) / totalGames;
    }

    /**
     * 듀얼 승률 계산
     */
    public double getDuelWinRate() {
        int totalDuels = duelWins + duelLosses;
        if (totalDuels == 0) {
            return 0.0;
        }
        return (duelWins * 100.0) / totalDuels;
    }

    /**
     * 특정 날짜 킬 수 조회
     */
    public int getKillsOnDate(LocalDate date) {
        String key = date.format(DATE_FORMATTER);
        return dailyKills.getOrDefault(key, 0);
    }

    /**
     * 오늘 킬 수 조회
     */
    public int getTodayKills() {
        return getKillsOnDate(LocalDate.now());
    }

    /**
     * 이번 주 킬 수 조회
     */
    public int getThisWeekKills() {
        String key = LocalDate. now().format(WEEK_FORMATTER);
        return weeklyKills.getOrDefault(key, 0);
    }

    /**
     * 이번 달 킬 수 조회
     */
    public int getThisMonthKills() {
        String key = LocalDate.now().format(MONTH_FORMATTER);
        return monthlyKills. getOrDefault(key, 0);
    }

    /**
     * 가장 많이 사용한 무기 조회
     */
    public Material getMostUsedWeapon() {
        Material mostUsed = null;
        int maxKills = 0;
        
        for (Map.Entry<Material, Integer> entry : weaponKills.entrySet()) {
            if (entry.getValue() > maxKills) {
                maxKills = entry.getValue();
                mostUsed = entry.getKey();
            }
        }
        
        return mostUsed;
    }

    /**
     * 특정 상대와의 전적 조회
     */
    public int[] getRecordAgainst(UUID opponentId) {
        int kills = killsAgainstPlayer.getOrDefault(opponentId, 0);
        int deaths = deathsFromPlayer.getOrDefault(opponentId, 0);
        return new int[]{kills, deaths};
    }

    /**
     * 플레이 시간 문자열 (시: 분: 초)
     */
    public String getPlayTimeString() {
        long seconds = totalPlayTime / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        return String.format("%d시간 %d분 %d초", hours, minutes, secs);
    }

    /**
     * 통계 초기화
     */
    public void reset() {
        this.totalKills = 0;
        this.totalDeaths = 0;
        this.totalAssists = 0;
        this.totalWins = 0;
        this. totalLosses = 0;
        this.kda = 0.0;
        
        this.weaponKills. clear();
        this.longestKillStreak = 0;
        this. highestDamageDealt = 0;
        this. mostKillsInMatch = 0;
        
        this.dailyKills.clear();
        this.weeklyKills.clear();
        this.monthlyKills. clear();
        
        this.killsAgainstPlayer. clear();
        this.deathsFromPlayer.clear();
        
        this. arenaWins. clear();
        this.arenaLosses.clear();
        this.arenaKills.clear();
        
        this.duelWins = 0;
        this.duelLosses = 0;
        this.duelSurrenders = 0;
        
        this.totalDamageDealt = 0;
        this. totalDamageReceived = 0;
        this.totalHealing = 0;
        
        this.firstBloods = 0;
        this.shutdowns = 0;
        this.revenges = 0;
        this. doubleKills = 0;
        this.tripleKills = 0;
        this.multiKills = 0;
        
        this.totalPlayTime = 0;
        this.lastPlayTime = System.currentTimeMillis();
    }

    /**
     * 복사본 생성
     */
    public PvPStatistics clone() {
        PvPStatistics clone = new PvPStatistics(this.playerId);
        clone.totalKills = this. totalKills;
        clone.totalDeaths = this.totalDeaths;
        clone.totalAssists = this.totalAssists;
        clone.totalWins = this.totalWins;
        clone. totalLosses = this.totalLosses;
        clone.kda = this.kda;
        clone.weaponKills = new HashMap<>(this.weaponKills);
        clone.longestKillStreak = this.longestKillStreak;
        clone.highestDamageDealt = this.highestDamageDealt;
        clone. mostKillsInMatch = this.mostKillsInMatch;
        clone. dailyKills = new HashMap<>(this.dailyKills);
        clone.weeklyKills = new HashMap<>(this.weeklyKills);
        clone.monthlyKills = new HashMap<>(this.monthlyKills);
        clone.killsAgainstPlayer = new HashMap<>(this.killsAgainstPlayer);
        clone.deathsFromPlayer = new HashMap<>(this.deathsFromPlayer);
        clone.arenaWins = new HashMap<>(this.arenaWins);
        clone.arenaLosses = new HashMap<>(this.arenaLosses);
        clone.arenaKills = new HashMap<>(this.arenaKills);
        clone.duelWins = this. duelWins;
        clone. duelLosses = this.duelLosses;
        clone.duelSurrenders = this. duelSurrenders;
        clone. totalDamageDealt = this.totalDamageDealt;
        clone. totalDamageReceived = this.totalDamageReceived;
        clone. totalHealing = this.totalHealing;
        clone.firstBloods = this.firstBloods;
        clone. shutdowns = this. shutdowns;
        clone.revenges = this.revenges;
        clone.doubleKills = this.doubleKills;
        clone.tripleKills = this.tripleKills;
        clone.multiKills = this.multiKills;
        clone.totalPlayTime = this.totalPlayTime;
        clone.lastPlayTime = this.lastPlayTime;
        return clone;
    }

    @Override
    public String toString() {
        return "PvPStatistics{" +
                "playerId=" + playerId +
                ", totalKills=" + totalKills +
                ", totalDeaths=" + totalDeaths +
                ", kda=" + String.format("%.2f", kda) +
                ", winRate=" + String.format("%.1f", getWinRate()) + "%" +
                '}';
    }
}