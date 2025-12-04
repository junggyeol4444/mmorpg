package com.multiverse. dungeon.data.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 플레이어 던전 데이터 클래스
 */
public class PlayerDungeonData {

    private UUID playerId; // 플레이어 ID
    private String playerName; // 플레이어 이름

    // 던전 통계
    private int totalRuns; // 총 입장 횟수
    private int totalClears; // 총 클리어 횟수
    private int totalDeaths; // 총 사망 횟수

    // 던전별 클리어 기록
    private Map<String, Map<String, Integer>> clearsByDungeon; // dungeonId -> (difficulty -> count)

    // 최고 기록
    private Map<String, Long> bestTimes; // dungeonId_difficulty -> clearTime (밀리초)

    // 일일 입장 기록
    private Map<String, DailyEntry> dailyEntries; // dungeonId -> DailyEntry

    // 주간 입장 기록
    private Map<String, WeeklyEntry> weeklyEntries; // dungeonId -> WeeklyEntry

    // 던전 포인트
    private int dungeonPoints; // 총 던전 포인트

    // 현재 진행 중인 인스턴스
    private UUID currentInstanceId; // 현재 인스턴스 ID

    // 파티
    private UUID partyId; // 파티 ID
    private boolean isLeader; // 파티 리더 여부

    /**
     * 생성자
     */
    public PlayerDungeonData(UUID playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.totalRuns = 0;
        this.totalClears = 0;
        this.totalDeaths = 0;
        this.clearsByDungeon = new HashMap<>();
        this.bestTimes = new HashMap<>();
        this.dailyEntries = new HashMap<>();
        this.weeklyEntries = new HashMap<>();
        this.dungeonPoints = 0;
        this.currentInstanceId = null;
        this.partyId = null;
        this.isLeader = false;
    }

    // ===== Getters & Setters =====

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = Math.max(0, totalRuns);
    }

    public void incrementTotalRuns() {
        this.totalRuns++;
    }

    public int getTotalClears() {
        return totalClears;
    }

    public void setTotalClears(int totalClears) {
        this.totalClears = Math.max(0, totalClears);
    }

    public void incrementTotalClears() {
        this.totalClears++;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(int totalDeaths) {
        this. totalDeaths = Math.max(0, totalDeaths);
    }

    public void incrementTotalDeaths() {
        this. totalDeaths++;
    }

    public Map<String, Map<String, Integer>> getClearsByDungeon() {
        return clearsByDungeon;
    }

    public void recordClear(String dungeonId, String difficulty) {
        Map<String, Integer> dungeonClears = clearsByDungeon. computeIfAbsent(dungeonId, k -> new HashMap<>());
        int count = dungeonClears.getOrDefault(difficulty, 0);
        dungeonClears.put(difficulty, count + 1);
        incrementTotalClears();
    }

    public int getClearCount(String dungeonId, String difficulty) {
        return clearsByDungeon.getOrDefault(dungeonId, new HashMap<>()).getOrDefault(difficulty, 0);
    }

    public Map<String, Long> getBestTimes() {
        return bestTimes;
    }

    public void recordBestTime(String dungeonId, String difficulty, long clearTime) {
        String key = dungeonId + "_" + difficulty;
        Long currentBest = bestTimes.get(key);
        
        if (currentBest == null || clearTime < currentBest) {
            bestTimes.put(key, clearTime);
        }
    }

    public Long getBestTime(String dungeonId, String difficulty) {
        return bestTimes.get(dungeonId + "_" + difficulty);
    }

    public Map<String, DailyEntry> getDailyEntries() {
        return dailyEntries;
    }

    public void recordDailyEntry(String dungeonId) {
        DailyEntry entry = dailyEntries.getOrDefault(dungeonId, new DailyEntry());
        entry.increment();
        dailyEntries. put(dungeonId, entry);
    }

    public int getDailyEntryCount(String dungeonId) {
        DailyEntry entry = dailyEntries.get(dungeonId);
        if (entry != null && entry.isToday()) {
            return entry. count;
        }
        return 0;
    }

    public Map<String, WeeklyEntry> getWeeklyEntries() {
        return weeklyEntries;
    }

    public void recordWeeklyEntry(String dungeonId) {
        WeeklyEntry entry = weeklyEntries. getOrDefault(dungeonId, new WeeklyEntry());
        entry.increment();
        weeklyEntries.put(dungeonId, entry);
    }

    public int getWeeklyEntryCount(String dungeonId) {
        WeeklyEntry entry = weeklyEntries.get(dungeonId);
        if (entry != null && entry.isThisWeek()) {
            return entry.count;
        }
        return 0;
    }

    public int getDungeonPoints() {
        return dungeonPoints;
    }

    public void setDungeonPoints(int dungeonPoints) {
        this.dungeonPoints = Math.max(0, dungeonPoints);
    }

    public void addDungeonPoints(int points) {
        this.dungeonPoints += Math.max(0, points);
    }

    public UUID getCurrentInstanceId() {
        return currentInstanceId;
    }

    public void setCurrentInstanceId(UUID currentInstanceId) {
        this.currentInstanceId = currentInstanceId;
    }

    public UUID getPartyId() {
        return partyId;
    }

    public void setPartyId(UUID partyId) {
        this.partyId = partyId;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    // ===== 내부 클래스 =====

    public static class DailyEntry {
        public int count;
        public String date;

        public DailyEntry() {
            this.count = 1;
            this.date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java. util.Date());
        }

        public void increment() {
            this.count++;
        }

        public boolean isToday() {
            String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util. Date());
            return this.date.equals(today);
        }
    }

    public static class WeeklyEntry {
        public int count;
        public String week;

        public WeeklyEntry() {
            this.count = 1;
            calculateWeek();
        }

        private void calculateWeek() {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int year = cal.get(java. util.Calendar.YEAR);
            int week = cal.get(java.util.Calendar.WEEK_OF_YEAR);
            this.week = year + "-W" + String.format("%02d", week);
        }

        public void increment() {
            this.count++;
        }

        public boolean isThisWeek() {
            java.util.Calendar cal = java. util.Calendar.getInstance();
            int year = cal.get(java.util.Calendar.YEAR);
            int week = cal.get(java.util.Calendar. WEEK_OF_YEAR);
            String currentWeek = year + "-W" + String.format("%02d", week);
            return this.week.equals(currentWeek);
        }
    }

    @Override
    public String toString() {
        return "PlayerDungeonData{" +
                "playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", totalRuns=" + totalRuns +
                ", totalClears=" + totalClears +
                ", totalDeaths=" + totalDeaths +
                ", dungeonPoints=" + dungeonPoints +
                '}';
    }
}