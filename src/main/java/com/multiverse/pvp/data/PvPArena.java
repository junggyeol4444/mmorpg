package com.multiverse.pvp.data;

import com. multiverse.pvp.enums.ArenaStatus;
import com. multiverse.pvp.enums.ArenaType;
import org.bukkit.Location;

import java.util.*;

public class PvPArena {

    private UUID arenaId;
    private String arenaName;
    private ArenaType type;

    // 위치
    private String worldName;
    private Location lobby;
    private List<Location> spawnPoints;
    private Location spectatorSpawn;

    // 경계
    private Location corner1;
    private Location corner2;

    // 설정
    private int minPlayers;
    private int maxPlayers;
    private int teamSize;

    // 시간
    private int matchDuration;
    private int preparationTime;

    // 보상
    private ArenaReward reward;

    // 상태
    private ArenaStatus status;
    private List<UUID> players;
    private Map<String, List<UUID>> teams;
    private Set<UUID> spectators;

    // 경기 정보
    private long matchStartTime;
    private long matchEndTime;
    private Map<UUID, Integer> playerKills;
    private Map<UUID, Integer> playerDeaths;
    private Map<String, Integer> teamScores;

    // 통계
    private int totalMatches;
    private int totalKills;

    public PvPArena(UUID arenaId, String arenaName, ArenaType type) {
        this. arenaId = arenaId;
        this.arenaName = arenaName;
        this.type = type;

        this.spawnPoints = new ArrayList<>();
        this.status = ArenaStatus. WAITING;
        this. players = new ArrayList<>();
        this.teams = new HashMap<>();
        this.spectators = new HashSet<>();

        this.playerKills = new HashMap<>();
        this.playerDeaths = new HashMap<>();
        this.teamScores = new HashMap<>();

        // 기본값 설정
        setDefaultValues();
    }

    private void setDefaultValues() {
        switch (type) {
            case DUEL_1V1:
                this.minPlayers = 2;
                this.maxPlayers = 2;
                this.teamSize = 1;
                this.matchDuration = 300; // 5분
                break;
            case TEAM_DEATHMATCH:
                this.minPlayers = 6;
                this. maxPlayers = 10;
                this.teamSize = 5;
                this. matchDuration = 600; // 10분
                break;
            case BATTLE_ROYALE: 
                this.minPlayers = 10;
                this. maxPlayers = 50;
                this. teamSize = 1;
                this. matchDuration = 900; // 15분
                break;
            case CAPTURE_POINT:
                this. minPlayers = 6;
                this. maxPlayers = 12;
                this. teamSize = 6;
                this. matchDuration = 600; // 10분
                break;
            case KING_OF_HILL:
                this. minPlayers = 4;
                this. maxPlayers = 16;
                this. teamSize = 1;
                this. matchDuration = 480; // 8분
                break;
            default:
                this.minPlayers = 2;
                this.maxPlayers = 10;
                this. teamSize = 1;
                this. matchDuration = 300;
                break;
        }
        this.preparationTime = 10;
        this.reward = new ArenaReward();
        this.totalMatches = 0;
        this. totalKills = 0;
    }

    // ==================== Getters ====================

    public UUID getArenaId() {
        return arenaId;
    }

    public String getArenaName() {
        return arenaName;
    }

    public ArenaType getType() {
        return type;
    }

    public String getWorldName() {
        return worldName;
    }

    public Location getLobby() {
        return lobby;
    }

    public List<Location> getSpawnPoints() {
        return spawnPoints;
    }

    public Location getSpectatorSpawn() {
        return spectatorSpawn;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public int getMatchDuration() {
        return matchDuration;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public ArenaReward getReward() {
        return reward;
    }

    public ArenaStatus getStatus() {
        return status;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public Map<String, List<UUID>> getTeams() {
        return teams;
    }

    public Set<UUID> getSpectators() {
        return spectators;
    }

    public long getMatchStartTime() {
        return matchStartTime;
    }

    public long getMatchEndTime() {
        return matchEndTime;
    }

    public Map<UUID, Integer> getPlayerKills() {
        return playerKills;
    }

    public Map<UUID, Integer> getPlayerDeaths() {
        return playerDeaths;
    }

    public Map<String, Integer> getTeamScores() {
        return teamScores;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public int getTotalKills() {
        return totalKills;
    }

    // ==================== Setters ====================

    public void setArenaId(UUID arenaId) {
        this.arenaId = arenaId;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public void setType(ArenaType type) {
        this. type = type;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void setLobby(Location lobby) {
        this. lobby = lobby;
        if (lobby != null) {
            this. worldName = lobby.getWorld().getName();
        }
    }

    public void setSpawnPoints(List<Location> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    public void setSpectatorSpawn(Location spectatorSpawn) {
        this. spectatorSpawn = spectatorSpawn;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public void setMatchDuration(int matchDuration) {
        this. matchDuration = matchDuration;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public void setReward(ArenaReward reward) {
        this.reward = reward;
    }

    public void setStatus(ArenaStatus status) {
        this. status = status;
    }

    public void setPlayers(List<UUID> players) {
        this.players = players;
    }

    public void setTeams(Map<String, List<UUID>> teams) {
        this.teams = teams;
    }

    public void setSpectators(Set<UUID> spectators) {
        this.spectators = spectators;
    }

    public void setMatchStartTime(long matchStartTime) {
        this.matchStartTime = matchStartTime;
    }

    public void setMatchEndTime(long matchEndTime) {
        this.matchEndTime = matchEndTime;
    }

    public void setPlayerKills(Map<UUID, Integer> playerKills) {
        this.playerKills = playerKills;
    }

    public void setPlayerDeaths(Map<UUID, Integer> playerDeaths) {
        this.playerDeaths = playerDeaths;
    }

    public void setTeamScores(Map<String, Integer> teamScores) {
        this.teamScores = teamScores;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public void setTotalKills(int totalKills) {
        this. totalKills = totalKills;
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 플레이어 추가
     */
    public boolean addPlayer(UUID playerId) {
        if (players.size() >= maxPlayers) {
            return false;
        }
        if (players.contains(playerId)) {
            return false;
        }
        players.add(playerId);
        playerKills.put(playerId, 0);
        playerDeaths.put(playerId, 0);
        return true;
    }

    /**
     * 플레이어 제거
     */
    public boolean removePlayer(UUID playerId) {
        playerKills.remove(playerId);
        playerDeaths.remove(playerId);
        
        // 팀에서도 제거
        for (List<UUID> teamPlayers : teams. values()) {
            teamPlayers.remove(playerId);
        }
        
        return players.remove(playerId);
    }

    /**
     * 관전자 추가
     */
    public void addSpectator(UUID playerId) {
        spectators.add(playerId);
    }

    /**
     * 관전자 제거
     */
    public void removeSpectator(UUID playerId) {
        spectators.remove(playerId);
    }

    /**
     * 플레이어가 아레나에 있는지 확인
     */
    public boolean hasPlayer(UUID playerId) {
        return players.contains(playerId);
    }

    /**
     * 관전자인지 확인
     */
    public boolean isSpectator(UUID playerId) {
        return spectators.contains(playerId);
    }

    /**
     * 아레나가 가득 찼는지 확인
     */
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    /**
     * 아레나 시작 가능 여부
     */
    public boolean canStart() {
        return players.size() >= minPlayers && status == ArenaStatus. WAITING;
    }

    /**
     * 스폰 포인트 추가
     */
    public void addSpawnPoint(Location location) {
        spawnPoints.add(location);
    }

    /**
     * 랜덤 스폰 포인트 반환
     */
    public Location getRandomSpawnPoint() {
        if (spawnPoints.isEmpty()) {
            return lobby;
        }
        Random random = new Random();
        return spawnPoints.get(random.nextInt(spawnPoints.size()));
    }

    /**
     * 인덱스로 스폰 포인트 반환
     */
    public Location getSpawnPoint(int index) {
        if (spawnPoints.isEmpty()) {
            return lobby;
        }
        return spawnPoints.get(index % spawnPoints.size());
    }

    /**
     * 플레이어 킬 추가
     */
    public void addKill(UUID playerId) {
        playerKills.put(playerId, playerKills.getOrDefault(playerId, 0) + 1);
        totalKills++;
    }

    /**
     * 플레이어 데스 추가
     */
    public void addDeath(UUID playerId) {
        playerDeaths.put(playerId, playerDeaths.getOrDefault(playerId, 0) + 1);
    }

    /**
     * 팀 점수 추가
     */
    public void addTeamScore(String teamName, int score) {
        teamScores.put(teamName, teamScores.getOrDefault(teamName, 0) + score);
    }

    /**
     * 팀에 플레이어 할당
     */
    public void assignToTeam(UUID playerId, String teamName) {
        teams.computeIfAbsent(teamName, k -> new ArrayList<>()).add(playerId);
    }

    /**
     * 플레이어의 팀 조회
     */
    public String getPlayerTeam(UUID playerId) {
        for (Map.Entry<String, List<UUID>> entry : teams.entrySet()) {
            if (entry.getValue().contains(playerId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 같은 팀인지 확인
     */
    public boolean isSameTeam(UUID player1, UUID player2) {
        String team1 = getPlayerTeam(player1);
        String team2 = getPlayerTeam(player2);
        return team1 != null && team1.equals(team2);
    }

    /**
     * 남은 경기 시간 (초)
     */
    public long getRemainingTime() {
        if (matchStartTime == 0) {
            return matchDuration;
        }
        long elapsed = (System.currentTimeMillis() - matchStartTime) / 1000;
        return Math.max(0, matchDuration - elapsed);
    }

    /**
     * 경기 시작
     */
    public void startMatch() {
        this.status = ArenaStatus.ACTIVE;
        this. matchStartTime = System.currentTimeMillis();
        this.totalMatches++;
    }

    /**
     * 경기 종료
     */
    public void endMatch() {
        this. status = ArenaStatus.ENDING;
        this. matchEndTime = System.currentTimeMillis();
    }

    /**
     * 아레나 초기화
     */
    public void reset() {
        this.status = ArenaStatus.WAITING;
        this.players.clear();
        this.teams.clear();
        this.spectators.clear();
        this.playerKills.clear();
        this.playerDeaths.clear();
        this.teamScores.clear();
        this.matchStartTime = 0;
        this. matchEndTime = 0;
    }

    /**
     * 위치가 아레나 내부인지 확인
     */
    public boolean isInArena(Location location) {
        if (corner1 == null || corner2 == null) {
            return false;
        }
        if (! location.getWorld().getName().equals(worldName)) {
            return false;
        }

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math. min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    /**
     * 승자 결정 (킬 기준)
     */
    public UUID getWinnerByKills() {
        UUID winner = null;
        int maxKills = -1;

        for (Map. Entry<UUID, Integer> entry : playerKills.entrySet()) {
            if (entry.getValue() > maxKills) {
                maxKills = entry.getValue();
                winner = entry.getKey();
            }
        }

        return winner;
    }

    /**
     * 승리 팀 결정
     */
    public String getWinningTeam() {
        String winningTeam = null;
        int maxScore = -1;

        for (Map.Entry<String, Integer> entry : teamScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                winningTeam = entry.getKey();
            }
        }

        return winningTeam;
    }

    /**
     * 생존자 목록 (배틀로얄용)
     */
    public List<UUID> getAlivePlayers() {
        return new ArrayList<>(players);
    }

    /**
     * 팀전 여부
     */
    public boolean isTeamBased() {
        return type == ArenaType. TEAM_DEATHMATCH || type == ArenaType.CAPTURE_POINT;
    }

    @Override
    public String toString() {
        return "PvPArena{" +
                "arenaId=" + arenaId +
                ", arenaName='" + arenaName + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", players=" + players. size() +
                "/" + maxPlayers +
                '}';
    }
}