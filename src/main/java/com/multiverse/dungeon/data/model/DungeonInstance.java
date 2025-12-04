package com.multiverse.dungeon. data.model;

import com. multiverse.dungeon.data. enums.DungeonDifficulty;
import com.multiverse.dungeon.data.enums. InstanceStatus;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 던전 인스턴스 데이터 클래스
 * 실제 진행 중인 던전 정보
 */
public class DungeonInstance {

    private UUID instanceId; // 인스턴스 ID
    private String dungeonId; // 던전 ID
    private DungeonDifficulty difficulty; // 난이도
    
    // 파티 정보
    private UUID partyId; // 파티 ID
    private List<UUID> players; // 참여 플레이어 UUIDs
    
    // 월드 정보
    private String worldName; // 인스턴스 월드 이름
    private Location spawnLocation; // 스폰 위치
    
    // 시간 정보
    private long startTime; // 시작 시간 (밀리초)
    private long endTime; // 종료 시간 (밀리초)
    private int timeLimit; // 제한 시간 (초)
    
    // 진행도
    private DungeonProgress progress; // 진행도
    
    // 상태
    private InstanceStatus status; // 인스턴스 상태
    
    // 보상
    private boolean rewardGiven; // 보상 지급 여부

    /**
     * 생성자
     */
    public DungeonInstance(UUID instanceId, String dungeonId, UUID partyId, 
                          DungeonDifficulty difficulty, int timeLimit) {
        this. instanceId = instanceId;
        this.dungeonId = dungeonId;
        this.difficulty = difficulty;
        this.partyId = partyId;
        this.players = new ArrayList<>();
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.timeLimit = timeLimit;
        this. progress = new DungeonProgress();
        this.status = InstanceStatus.CREATING;
        this.rewardGiven = false;
    }

    /**
     * 기본 생성자
     */
    public DungeonInstance() {
        this(UUID.randomUUID(), "unknown", UUID.randomUUID(), 
             DungeonDifficulty.NORMAL, 1800);
    }

    // ===== Getters & Setters =====

    public UUID getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(UUID instanceId) {
        this.instanceId = instanceId;
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

    public UUID getPartyId() {
        return partyId;
    }

    public void setPartyId(UUID partyId) {
        this.partyId = partyId;
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

    public void removePlayer(UUID playerId) {
        this.players.remove(playerId);
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = Math.max(0, timeLimit);
    }

    public DungeonProgress getProgress() {
        return progress;
    }

    public void setProgress(DungeonProgress progress) {
        this. progress = progress;
    }

    public InstanceStatus getStatus() {
        return status;
    }

    public void setStatus(InstanceStatus status) {
        this.status = status;
    }

    public boolean isRewardGiven() {
        return rewardGiven;
    }

    public void setRewardGiven(boolean rewardGiven) {
        this.rewardGiven = rewardGiven;
    }

    // ===== 비즈니스 로직 =====

    /**
     * 인스턴스 활성 여부
     *
     * @return 활성이면 true
     */
    public boolean isActive() {
        return status. isActive();
    }

    /**
     * 인스턴스 종료 여부
     *
     * @return 종료되었으면 true
     */
    public boolean isFinished() {
        return status. isFinished();
    }

    /**
     * 경과 시간 (초)
     *
     * @return 경과 시간
     */
    public long getElapsedTime() {
        long current = endTime > 0 ? endTime : System.currentTimeMillis();
        return (current - startTime) / 1000;
    }

    /**
     * 남은 시간 (초)
     *
     * @return 남은 시간 (0 이상)
     */
    public long getRemainingTime() {
        long elapsed = getElapsedTime();
        return Math.max(0, timeLimit - elapsed);
    }

    /**
     * 시간 초과 여부
     *
     * @return 초과했으면 true
     */
    public boolean isTimeLimitExceeded() {
        return getElapsedTime() >= timeLimit;
    }

    /**
     * 시간 진행도 (%)
     *
     * @return 진행도 (0 ~ 100)
     */
    public double getTimeProgress() {
        if (timeLimit == 0) {
            return 0.0;
        }
        long elapsed = getElapsedTime();
        return Math.min(100.0, (elapsed / (double) timeLimit) * 100.0);
    }

    /**
     * 파티원이 모두 떠났는지 확인
     *
     * @return 모두 떠났으면 true
     */
    public boolean hasNoPlayers() {
        return players.isEmpty();
    }

    /**
     * 경과 시간을 MM:SS 형식으로 반환
     *
     * @return 시간 문자열
     */
    public String getElapsedTimeFormatted() {
        long elapsed = getElapsedTime();
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 남은 시간을 MM:SS 형식으로 반환
     *
     * @return 시간 문자열
     */
    public String getRemainingTimeFormatted() {
        long remaining = getRemainingTime();
        long minutes = remaining / 60;
        long seconds = remaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return "DungeonInstance{" +
                "instanceId=" + instanceId +
                ", dungeonId='" + dungeonId + '\'' +
                ", difficulty=" + difficulty +
                ", players=" + players. size() +
                ", status=" + status +
                ", elapsed=" + getElapsedTimeFormatted() +
                ", remaining=" + getRemainingTimeFormatted() +
                '}';
    }
}