package com. multiverse.dungeon.data. model;

import com.multiverse.dungeon.data.enums.DungeonDifficulty;
import com. multiverse.dungeon.data. enums.DungeonType;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * 던전 데이터 클래스
 * 던전의 모든 정보를 포함
 */
public class Dungeon {

    private String dungeonId; // 던전 ID
    private String name; // 던전 이름
    private DungeonType type; // 던전 타입
    private DungeonDifficulty difficulty; // 기본 난이도
    
    // 설명
    private String description; // 던전 설명
    private List<String> lore; // 던전 정보 (여러 줄)
    
    // 조건
    private int requiredLevel; // 필요 레벨
    private int minPlayers; // 최소 플레이어
    private int maxPlayers; // 최대 플레이어
    private String requiredQuest; // 필요 퀘스트
    private int requiredItemLevel; // 필요 아이템 레벨
    
    // 위치
    private String dimension; // 차원 (MultiverseCore)
    private Location entrance; // 입구
    private Location spawn; // 스폰 위치
    
    // 시간 제한
    private int timeLimit; // 제한 시간 (초)
    
    // 입장 제한
    private DailyLimit dailyLimit; // 일일 제한
    private WeeklyLimit weeklyLimit; // 주간 제한
    
    // 보스
    private List<DungeonBoss> bosses; // 보스 목록
    
    // 보상
    private DungeonReward reward; // 보상
    
    // 랜덤 던전 설정
    private RandomDungeonConfig randomConfig; // 랜덤 던전 설정 (RANDOM 타입일 때만)
    
    // 활성화
    private boolean enabled; // 던전 활성화 여부

    /**
     * 생성자
     */
    public Dungeon(String dungeonId, String name, DungeonType type) {
        this.dungeonId = dungeonId;
        this.name = name;
        this.type = type;
        this.difficulty = DungeonDifficulty.NORMAL;
        this.description = "";
        this.lore = new ArrayList<>();
        this.requiredLevel = 1;
        this.minPlayers = 1;
        this. maxPlayers = 5;
        this.requiredQuest = null;
        this.requiredItemLevel = 0;
        this.dimension = "world";
        this.entrance = null;
        this.spawn = null;
        this.timeLimit = 1800; // 30분
        this. dailyLimit = new DailyLimit(3);
        this.weeklyLimit = new WeeklyLimit(1);
        this.bosses = new ArrayList<>();
        this.reward = new DungeonReward();
        this.randomConfig = null;
        this.enabled = true;
    }

    /**
     * 기본 생성자
     */
    public Dungeon() {
        this("unknown", "Unnamed Dungeon", DungeonType.NORMAL);
    }

    // ===== Getters & Setters =====

    public String getDungeonId() {
        return dungeonId;
    }

    public void setDungeonId(String dungeonId) {
        this.dungeonId = dungeonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DungeonType getType() {
        return type;
    }

    public void setType(DungeonType type) {
        this.type = type;
    }

    public DungeonDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DungeonDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this. lore = lore != null ? lore : new ArrayList<>();
    }

    public void addLore(String line) {
        this.lore.add(line);
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = Math.max(1, requiredLevel);
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = Math.max(1, minPlayers);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = Math.max(this.minPlayers, maxPlayers);
    }

    public String getRequiredQuest() {
        return requiredQuest;
    }

    public void setRequiredQuest(String requiredQuest) {
        this.requiredQuest = requiredQuest;
    }

    public int getRequiredItemLevel() {
        return requiredItemLevel;
    }

    public void setRequiredItemLevel(int requiredItemLevel) {
        this.requiredItemLevel = Math.max(0, requiredItemLevel);
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension != null ? dimension : "world";
    }

    public Location getEntrance() {
        return entrance;
    }

    public void setEntrance(Location entrance) {
        this.entrance = entrance;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = Math.max(60, timeLimit); // 최소 60초
    }

    public DailyLimit getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(DailyLimit dailyLimit) {
        this.dailyLimit = dailyLimit != null ? dailyLimit : new DailyLimit();
    }

    public WeeklyLimit getWeeklyLimit() {
        return weeklyLimit;
    }

    public void setWeeklyLimit(WeeklyLimit weeklyLimit) {
        this.weeklyLimit = weeklyLimit != null ? weeklyLimit : new WeeklyLimit();
    }

    public List<DungeonBoss> getBosses() {
        return bosses;
    }

    public void setBosses(List<DungeonBoss> bosses) {
        this.bosses = bosses != null ? bosses : new ArrayList<>();
    }

    public void addBoss(DungeonBoss boss) {
        this. bosses.add(boss);
    }

    public DungeonReward getReward() {
        return reward;
    }

    public void setReward(DungeonReward reward) {
        this.reward = reward != null ? reward : new DungeonReward();
    }

    public RandomDungeonConfig getRandomConfig() {
        return randomConfig;
    }

    public void setRandomConfig(RandomDungeonConfig randomConfig) {
        this.randomConfig = randomConfig;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // ===== 비즈니스 로직 =====

    /**
     * 플레이어가 던전 입장 조건을 만족하는지 확인
     *
     * @param playerLevel 플레이어 레벨
     * @param playerHasQuest 퀘스트 완료 여부
     * @param playerItemLevel 플레이어 아이템 레벨
     * @return 조건 만족하면 true
     */
    public boolean canEnter(int playerLevel, boolean playerHasQuest, int playerItemLevel) {
        if (!enabled) {
            return false;
        }

        if (playerLevel < requiredLevel) {
            return false;
        }

        if (requiredQuest != null && !playerHasQuest) {
            return false;
        }

        if (playerItemLevel < requiredItemLevel) {
            return false;
        }

        return true;
    }

    /**
     * 파티 크기가 적절한지 확인
     *
     * @param partySize 파티 크기
     * @return 적절하면 true
     */
    public boolean isValidPartySize(int partySize) {
        return partySize >= minPlayers && partySize <= maxPlayers;
    }

    /**
     * 던전이 보스를 가지고 있는지 확인
     *
     * @return 보스가 있으면 true
     */
    public boolean hasBosses() {
        return ! bosses.isEmpty();
    }

    /**
     * 던전의 보스 개수
     *
     * @return 보스 개수
     */
    public int getBossCount() {
        return bosses. size();
    }

    /**
     * 시간 제한을 MM:SS 형식으로 반환
     *
     * @return 시간 문자열
     */
    public String getTimeLimitFormatted() {
        long minutes = timeLimit / 60;
        long seconds = timeLimit % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 던전 타입에 따른 권장 난이도
     *
     * @return 권장 난이도
     */
    public DungeonDifficulty getRecommendedDifficulty() {
        return switch (type) {
            case SOLO -> DungeonDifficulty.EASY;
            case NORMAL -> DungeonDifficulty.NORMAL;
            case HEROIC -> DungeonDifficulty.HARD;
            case MYTHIC, RAID -> DungeonDifficulty.EXTREME;
            case RANDOM -> DungeonDifficulty.NORMAL;
        };
    }

    /**
     * 던전이 활성화되었는지 그리고 모든 필수 데이터가 설정되었는지 확인
     *
     * @return 완전히 설정되었으면 true
     */
    public boolean isFullyConfigured() {
        if (! enabled) {
            return false;
        }

        if (entrance == null || spawn == null) {
            return false;
        }

        if (type != DungeonType.RANDOM && bosses.isEmpty()) {
            return false;
        }

        if (type == DungeonType.RANDOM && randomConfig == null) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Dungeon{" +
                "dungeonId='" + dungeonId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", difficulty=" + difficulty +
                ", minPlayers=" + minPlayers +
                ", maxPlayers=" + maxPlayers +
                ", timeLimit=" + getTimeLimitFormatted() +
                ", bosses=" + bosses. size() +
                ", enabled=" + enabled +
                '}';
    }
}