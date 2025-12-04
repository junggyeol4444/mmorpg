package com.multiverse.dungeon. data.model;

import com.multiverse.dungeon.data.enums.DungeonDifficulty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 던전 클리어 기록 데이터 클래스
 */
public class DungeonRecord {

    private UUID recordId; // 기록 ID
    private String dungeonId; // 던전 ID
    private DungeonDifficulty difficulty; // 난이도
    
    private List<UUID> players; // 파티원 UUIDs
    private List<String> playerNames; // 파티원 이름들
    
    private long clearTime; // 클리어 시간 (밀리초)
    private int score; // 던전 점수
    private int deaths; // 사망 횟수
    
    private long timestamp; // 기록 시간 (밀리초)
    private String date; // 기록 날짜 (YYYY-MM-DD)

    /**
     * 생성자
     */
    public DungeonRecord(String dungeonId, DungeonDifficulty difficulty) {
        this.recordId = UUID.randomUUID();
        this.dungeonId = dungeonId;
        this.difficulty = difficulty;
        this.players = new ArrayList<>();
        this.playerNames = new ArrayList<>();
        this.clearTime = 0;
        this.score = 0;
        this.deaths = 0;
        this. timestamp = System.currentTimeMillis();
        this.date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java. util.Date());
    }

    /**
     * 기본 생성자
     */
    public DungeonRecord() {
        this("unknown", DungeonDifficulty.NORMAL);
    }

    // ===== Getters & Setters =====

    public UUID getRecordId() {
        return recordId;
    }

    public void setRecordId(UUID recordId) {
        this.recordId = recordId;
    }

    public String getDungeonId() {
        return dungeonId;
    }

    public void setDungeonId(String dungeonId) {
        this.dungeonId = dungeonId;
    }

    public DungeonDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DungeonDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void setPlayers(List<UUID> players) {
        this.players = players != null ? players : new ArrayList<>();
    }

    public void addPlayer(UUID playerId) {
        if (!this.players.contains(playerId)) {
            this.players.add(playerId);
        }
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames != null ? playerNames : new ArrayList<>();
    }

    public void addPlayerName(String name) {
        if (!this. playerNames.contains(name)) {
            this.playerNames.add(name);
        }
    }

    public long getClearTime() {
        return clearTime;
    }

    public void setClearTime(long clearTime) {
        this.clearTime = Math.max(0, clearTime);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this. deaths = Math.max(0, deaths);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 클리어 시간을 분:초 형식으로 반환
     *
     * @return 시간 문자열 (MM:SS)
     */
    public String getClearTimeFormatted() {
        long seconds = clearTime / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    /**
     * 평균 점수 계산 (파티원 수로 나눔)
     *
     * @return 평균 점수
     */
    public int getAverageScore() {
        if (players.isEmpty()) {
            return score;
        }
        return score / players.size();
    }

    /**
     * 파티원 수
     *
     * @return 파티 크기
     */
    public int getPartySize() {
        return players.size();
    }

    /**
     * 파티원이 포함되어 있는지 확인
     *
     * @param playerId 플레이어 ID
     * @return 포함되면 true
     */
    public boolean hasPlayer(UUID playerId) {
        return players.contains(playerId);
    }

    /**
     * 기록 비교 (클리어 시간 기준)
     *
     * @param other 비교할 기록
     * @return 현재 기록이 더 빠르면 음수, 느리면 양수
     */
    public int compareTo(DungeonRecord other) {
        return Long.compare(this.clearTime, other.clearTime);
    }

    @Override
    public String toString() {
        return "DungeonRecord{" +
                "recordId=" + recordId +
                ", dungeonId='" + dungeonId + '\'' +
                ", difficulty=" + difficulty +
                ", players=" + players. size() +
                ", clearTime=" + getClearTimeFormatted() +
                ", score=" + score +
                ", deaths=" + deaths +
                ", date='" + date + '\'' +
                '}';
    }
}