package com.multiverse.pvp.managers;

import com. multiverse.pvp.PvPCore;
import com.multiverse.pvp.data.PvPArena;
import com.multiverse.pvp.data.PvPRanking;
import com.multiverse.pvp.enums. ArenaStatus;
import com. multiverse.pvp.enums.ArenaType;
import com.multiverse. pvp.utils. MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit.GameMode;
import org. bukkit.Location;
import org. bukkit.entity.Player;

import java.util.*;
import java.util. concurrent.ConcurrentHashMap;

public class ArenaManager {

    private final PvPCore plugin;
    private final Map<UUID, PvPArena> arenas;
    private final Map<UUID, UUID> playerArenaMap; // 플레이어 UUID -> 아레나 UUID
    private final Map<UUID, ArenaType> matchmakingQueue; // 매칭 대기열
    private final Map<UUID, Long> queueStartTime; // 대기 시작 시간
    private final Map<UUID, Location> previousLocations; // 이전 위치 저장

    // 설정값
    private boolean matchmakingEnabled;
    private int matchmakingTimeout;
    private int matchmakingRatingRange;
    private int preparationTime;

    public ArenaManager(PvPCore plugin) {
        this.plugin = plugin;
        this. arenas = new ConcurrentHashMap<>();
        this.playerArenaMap = new ConcurrentHashMap<>();
        this.matchmakingQueue = new ConcurrentHashMap<>();
        this.queueStartTime = new ConcurrentHashMap<>();
        this.previousLocations = new ConcurrentHashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        this.matchmakingEnabled = plugin.getConfig().getBoolean("arenas.matchmaking.enabled", true);
        this.matchmakingTimeout = plugin.getConfig().getInt("arenas.matchmaking.timeout", 300);
        this.matchmakingRatingRange = plugin. getConfig().getInt("arenas.matchmaking.rating-range", 200);
        this.preparationTime = plugin.getConfig().getInt("arenas. preparation-time", 10);
    }

    /**
     * 아레나 생성
     */
    public PvPArena createArena(String name, ArenaType type, Location lobby) {
        UUID arenaId = UUID. randomUUID();
        PvPArena arena = new PvPArena(arenaId, name, type);
        arena.setLobby(lobby);
        arena.setPreparationTime(preparationTime);
        
        arenas.put(arenaId, arena);
        plugin.getArenaStorage().saveArena(arena);
        
        plugin.getLogger().info("아레나 생성됨: " + name + " (" + type.getDisplayName() + ")");
        return arena;
    }

    /**
     * 아레나 삭제
     */
    public void deleteArena(UUID arenaId) {
        PvPArena arena = arenas.get(arenaId);
        if (arena == null) {
            return;
        }

        // 진행 중인 경기가 있으면 종료
        if (arena.getStatus().isInProgress()) {
            endArena(arena, null);
        }

        // 플레이어 퇴장 처리
        for (UUID playerId : new ArrayList<>(arena.getPlayers())) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                leaveArena(player);
            }
        }

        arenas.remove(arenaId);
        plugin.getArenaStorage().deleteArena(arenaId);
        
        plugin.getLogger().info("아레나 삭제됨: " + arena.getArenaName());
    }

    /**
     * 아레나 조회
     */
    public PvPArena getArena(UUID arenaId) {
        return arenas.get(arenaId);
    }

    /**
     * 이름으로 아레나 조회
     */
    public PvPArena getArenaByName(String name) {
        for (PvPArena arena : arenas.values()) {
            if (arena. getArenaName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    /**
     * 모든 아레나 조회
     */
    public List<PvPArena> getAllArenas() {
        return new ArrayList<>(arenas.values());
    }

    /**
     * 타입별 아레나 조회
     */
    public List<PvPArena> getArenasByType(ArenaType type) {
        List<PvPArena> result = new ArrayList<>();
        for (PvPArena arena :  arenas.values()) {
            if (arena.getType() == type) {
                result.add(arena);
            }
        }
        return result;
    }

    /**
     * 사용 가능한 아레나 조회
     */
    public List<PvPArena> getAvailableArenas(ArenaType type) {
        List<PvPArena> result = new ArrayList<>();
        for (PvPArena arena : arenas.values()) {
            if (arena.getType() == type && 
                arena.getStatus() == ArenaStatus. WAITING && 
                ! arena.isFull()) {
                result.add(arena);
            }
        }
        return result;
    }

    /**
     * 아레나 참가
     */
    public boolean joinArena(Player player, UUID arenaId) {
        // 이미 아레나에 있는지 확인
        if (isInArena(player)) {
            MessageUtil.sendMessage(player, "&c이미 아레나에 참가 중입니다.");
            return false;
        }

        // 듀얼 중인지 확인
        if (plugin.getDuelManager().isInDuel(player)) {
            MessageUtil.sendMessage(player, "&c듀얼 중에는 아레나에 참가할 수 없습니다.");
            return false;
        }

        PvPArena arena = arenas.get(arenaId);
        if (arena == null) {
            MessageUtil.sendMessage(player, "&c아레나를 찾을 수 없습니다.");
            return false;
        }

        // 참가 가능 상태 확인
        if (!arena.getStatus().canJoin()) {
            MessageUtil.sendMessage(player, "&c현재 참가할 수 없는 상태입니다.  (" + arena.getStatus().getDisplayName() + ")");
            return false;
        }

        // 인원 확인
        if (arena.isFull()) {
            MessageUtil.sendMessage(player, "&c아레나가 가득 찼습니다.");
            return false;
        }

        // 이전 위치 저장
        previousLocations.put(player.getUniqueId(), player.getLocation().clone());

        // 아레나에 플레이어 추가
        arena. addPlayer(player. getUniqueId());
        playerArenaMap.put(player.getUniqueId(), arenaId);

        // 로비로 텔레포트
        if (arena.getLobby() != null) {
            player.teleport(arena.getLobby());
        }

        // 매칭 대기열에서 제거
        cancelQueue(player);

        // 참가 메시지
        broadcastToArena(arena, "&a" + player. getName() + "님이 아레나에 참가했습니다.  (" + 
                arena.getPlayers().size() + "/" + arena.getMaxPlayers() + ")");

        // 팀 배정 (팀전인 경우)
        if (arena.isTeamBased()) {
            assignPlayerToTeam(arena, player);
        }

        // 시작 조건 확인
        checkStartCondition(arena);

        return true;
    }

    /**
     * 아레나 퇴장
     */
    public void leaveArena(Player player) {
        UUID arenaId = playerArenaMap.get(player. getUniqueId());
        if (arenaId == null) {
            return;
        }

        PvPArena arena = arenas.get(arenaId);
        if (arena == null) {
            playerArenaMap.remove(player.getUniqueId());
            return;
        }

        // 진행 중인 경기에서 퇴장 시 패배 처리
        if (arena. getStatus() == ArenaStatus. ACTIVE) {
            handlePlayerLeaveInProgress(arena, player);
        }

        // 아레나에서 플레이어 제거
        arena.removePlayer(player.getUniqueId());
        arena.removeSpectator(player. getUniqueId());
        playerArenaMap.remove(player.getUniqueId());

        // 이전 위치로 텔레포트
        Location previousLocation = previousLocations.remove(player. getUniqueId());
        if (previousLocation != null) {
            player. teleport(previousLocation);
        }

        // 게임모드 복구
        player.setGameMode(GameMode.SURVIVAL);

        // 퇴장 메시지
        broadcastToArena(arena, "&c" + player.getName() + "님이 아레나를 떠났습니다.");

        // 경기 종료 조건 확인
        checkEndCondition(arena);
    }

    /**
     * 진행 중 퇴장 처리
     */
    private void handlePlayerLeaveInProgress(PvPArena arena, Player player) {
        // 1대1이면 상대방 승리
        if (arena. getType() == ArenaType.DUEL_1V1) {
            List<UUID> players = arena.getPlayers();
            UUID winnerId = null;
            for (UUID id : players) {
                if (! id.equals(player.getUniqueId())) {
                    winnerId = id;
                    break;
                }
            }
            if (winnerId != null) {
                endArena(arena, winnerId);
            }
        }
        
        // 배틀로얄/FFA - 탈락 처리
        if (arena.getType() == ArenaType.BATTLE_ROYALE || arena.getType() == ArenaType. KING_OF_HILL) {
            arena.addDeath(player.getUniqueId());
        }
    }

    /**
     * 아레나 관전
     */
    public void spectateArena(Player player, UUID arenaId) {
        if (isInArena(player)) {
            MessageUtil.sendMessage(player, "&c이미 아레나에 참가 중입니다.");
            return;
        }

        PvPArena arena = arenas.get(arenaId);
        if (arena == null) {
            MessageUtil.sendMessage(player, "&c아레나를 찾을 수 없습니다.");
            return;
        }

        if (!arena.getStatus().canSpectate()) {
            MessageUtil.sendMessage(player, "&c현재 관전할 수 없는 상태입니다.");
            return;
        }

        // 이전 위치 저장
        previousLocations.put(player.getUniqueId(), player.getLocation().clone());

        // 관전자로 추가
        arena. addSpectator(player.getUniqueId());
        playerArenaMap.put(player. getUniqueId(), arenaId);

        // 관전 위치로 텔레포트
        Location spectatorSpawn = arena.getSpectatorSpawn();
        if (spectatorSpawn != null) {
            player.teleport(spectatorSpawn);
        } else if (arena.getLobby() != null) {
            player. teleport(arena. getLobby());
        }

        // 관전 모드 설정
        player. setGameMode(GameMode.SPECTATOR);

        MessageUtil.sendMessage(player, "&a" + arena.getArenaName() + " 아레나를 관전합니다.");
    }

    /**
     * 아레나 시작
     */
    public void startArena(PvPArena arena) {
        if (arena.getStatus() != ArenaStatus. WAITING) {
            return;
        }

        // 준비 단계
        arena. setStatus(ArenaStatus.PREPARING);
        broadcastToArena(arena, "&e경기가 " + arena.getPreparationTime() + "초 후에 시작됩니다!");

        // 카운트다운
        new org.bukkit.scheduler.BukkitRunnable() {
            int countdown = arena.getPreparationTime();

            @Override
            public void run() {
                if (arena.getStatus() != ArenaStatus. PREPARING) {
                    cancel();
                    return;
                }

                if (countdown <= 0) {
                    // 경기 시작
                    beginMatch(arena);
                    cancel();
                    return;
                }

                if (countdown <= 5 || countdown == 10) {
                    broadcastToArena(arena, "&e" + countdown + "초.. .");
                }

                countdown--;
            }
        }. runTaskTimer(plugin, 0L, 20L);
    }

    /**
     * 경기 시작
     */
    private void beginMatch(PvPArena arena) {
        arena.startMatch();
        
        // 플레이어 스폰
        List<UUID> players = new ArrayList<>(arena. getPlayers());
        for (int i = 0; i < players.size(); i++) {
            Player player = Bukkit.getPlayer(players.get(i));
            if (player != null) {
                Location spawn = arena.getSpawnPoint(i);
                player.teleport(spawn);
                
                // 체력/허기 풀 충전
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                player. setSaturation(20f);
            }
        }

        broadcastToArena(arena, "&c&l전투 시작!");

        // 효과음
        for (UUID playerId : players) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player. playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
            }
        }
    }

    /**
     * 아레나 종료
     */
    public void endArena(PvPArena arena, UUID winnerId) {
        if (arena.getStatus() == ArenaStatus. ENDED || arena.getStatus() == ArenaStatus. WAITING) {
            return;
        }

        arena.endMatch();

        // 승자 결정
        if (winnerId == null && arena.getType().isKillBased()) {
            winnerId = arena.getWinnerByKills();
        }

        String winningTeam = null;
        if (arena.isTeamBased()) {
            winningTeam = arena. getWinningTeam();
        }

        // 결과 발표
        if (winnerId != null) {
            Player winner = Bukkit.getPlayer(winnerId);
            String winnerName = winner != null ? winner.getName() : "알 수 없음";
            broadcastToArena(arena, "&6&l========== 경기 종료 ==========");
            broadcastToArena(arena, "&a승자: &f" + winnerName);
        } else if (winningTeam != null) {
            broadcastToArena(arena, "&6&l========== 경기 종료 ==========");
            broadcastToArena(arena, "&a승리 팀: &f" + winningTeam);
        } else {
            broadcastToArena(arena, "&6&l========== 경기 종료 ==========");
            broadcastToArena(arena, "&e무승부!");
        }

        // 보상 지급
        giveArenaRewards(arena, winnerId, winningTeam);

        // 5초 후 플레이어 퇴장
        final UUID finalWinnerId = winnerId;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // 모든 플레이어 퇴장
            for (UUID playerId : new ArrayList<>(arena. getPlayers())) {
                Player player = Bukkit. getPlayer(playerId);
                if (player != null) {
                    leaveArena(player);
                }
            }
            
            // 관전자 퇴장
            for (UUID spectatorId : new ArrayList<>(arena. getSpectators())) {
                Player spectator = Bukkit.getPlayer(spectatorId);
                if (spectator != null) {
                    leaveArena(spectator);
                }
            }

            // 아레나 초기화
            arena.reset();
        }, 100L); // 5초
    }

    /**
     * 아레나 보상 지급
     */
    private void giveArenaRewards(PvPArena arena, UUID winnerId, String winningTeam) {
        for (UUID playerId : arena. getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) continue;

            int kills = arena.getPlayerKills().getOrDefault(playerId, 0);
            boolean isWinner = playerId.equals(winnerId);
            boolean isWinningTeam = winningTeam != null && 
                    winningTeam. equals(arena.getPlayerTeam(playerId));

            if (isWinner || isWinningTeam) {
                // 승리 보상
                PvPRanking ranking = plugin.getRankingManager().getRanking(playerId);
                int winStreak = ranking != null ? ranking.getWinStreak() + 1 : 1;
                
                plugin.getRewardManager().giveWinReward(player, arena. getType());
                
                // 통계 업데이트
                plugin. getStatisticsManager().recordWin(player, arena.getType().name());
            } else {
                // 패배 보상
                plugin.getRewardManager().giveLoseReward(player, arena.getType(), kills);
                
                // 통계 업데이트
                plugin.getStatisticsManager().recordLoss(player, arena. getType().name());
            }

            // MVP 체크 (최다 킬)
            UUID mvpId = arena.getWinnerByKills();
            if (playerId.equals(mvpId) && kills > 0) {
                plugin.getRewardManager().giveMvpReward(player, arena.getType());
            }
        }
    }

    /**
     * 모든 아레나 종료
     */
    public void endAllArenas() {
        for (PvPArena arena :  arenas.values()) {
            if (arena.getStatus().isInProgress()) {
                endArena(arena, null);
            }
        }
    }

    /**
     * 매칭 대기열 등록
     */
    public void queueForArena(Player player, ArenaType type) {
        if (! matchmakingEnabled) {
            MessageUtil.sendMessage(player, "&c매칭 시스템이 비활성화되어 있습니다.");
            return;
        }

        if (isInArena(player)) {
            MessageUtil.sendMessage(player, "&c이미 아레나에 참가 중입니다.");
            return;
        }

        if (isInQueue(player)) {
            MessageUtil.sendMessage(player, "&c이미 매칭 대기 중입니다.");
            return;
        }

        matchmakingQueue.put(player.getUniqueId(), type);
        queueStartTime.put(player.getUniqueId(), System.currentTimeMillis());

        MessageUtil.sendMessage(player, "&a" + type. getDisplayName() + " 매칭 대기열에 등록되었습니다.");
    }

    /**
     * 매칭 대기열 취소
     */
    public void cancelQueue(Player player) {
        if (matchmakingQueue. remove(player.getUniqueId()) != null) {
            queueStartTime.remove(player.getUniqueId());
            MessageUtil.sendMessage(player, "&e매칭 대기가 취소되었습니다.");
        }
    }

    /**
     * 매칭 대기 중인지 확인
     */
    public boolean isInQueue(Player player) {
        return matchmakingQueue.containsKey(player.getUniqueId());
    }

    /**
     * 매칭 처리 (MatchmakingTask에서 호출)
     */
    public void processMatchmaking() {
        if (! matchmakingEnabled) {
            return;
        }

        // 타입별로 매칭 처리
        for (ArenaType type : ArenaType. values()) {
            processMatchmakingForType(type);
        }

        // 타임아웃 처리
        long currentTime = System. currentTimeMillis();
        for (Map.Entry<UUID, Long> entry : new HashMap<>(queueStartTime).entrySet()) {
            if (currentTime - entry.getValue() > matchmakingTimeout * 1000L) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    cancelQueue(player);
                    MessageUtil.sendMessage(player, "&c매칭 시간이 초과되었습니다.");
                } else {
                    matchmakingQueue. remove(entry.getKey());
                    queueStartTime.remove(entry.getKey());
                }
            }
        }
    }

    /**
     * 타입별 매칭 처리
     */
    private void processMatchmakingForType(ArenaType type) {
        List<UUID> queuedPlayers = new ArrayList<>();
        
        for (Map.Entry<UUID, ArenaType> entry :  matchmakingQueue. entrySet()) {
            if (entry.getValue() == type) {
                queuedPlayers. add(entry.getKey());
            }
        }

        if (queuedPlayers.size() < type.getMinPlayers()) {
            return;
        }

        // 레이팅 기반 매칭 (선택적)
        List<UUID> matchedPlayers = new ArrayList<>();
        
        if (type == ArenaType. DUEL_1V1 && queuedPlayers.size() >= 2) {
            // 레이팅이 비슷한 두 플레이어 매칭
            matchedPlayers = findRatingMatch(queuedPlayers, 2);
        } else if (queuedPlayers. size() >= type.getMinPlayers()) {
            // 먼저 대기한 플레이어부터 매칭
            matchedPlayers = queuedPlayers. subList(0, Math.min(queuedPlayers.size(), type.getMaxPlayers()));
        }

        if (matchedPlayers. size() >= type.getMinPlayers()) {
            // 아레나 찾기 또는 생성
            PvPArena arena = findOrCreateArena(type);
            
            if (arena != null) {
                // 매칭된 플레이어 아레나에 참가
                for (UUID playerId : matchedPlayers) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null) {
                        matchmakingQueue.remove(playerId);
                        queueStartTime.remove(playerId);
                        joinArena(player, arena. getArenaId());
                    }
                }
            }
        }
    }

    /**
     * 레이팅 기반 매칭
     */
    private List<UUID> findRatingMatch(List<UUID> players, int count) {
        if (players.size() < count) {
            return new ArrayList<>();
        }

        // 레이팅순 정렬
        players.sort((a, b) -> {
            int ratingA = plugin.getRankingManager().getRanking(a) != null ?  
                    plugin.getRankingManager().getRanking(a).getRating() : 1000;
            int ratingB = plugin. getRankingManager().getRanking(b) != null ? 
                    plugin. getRankingManager().getRanking(b).getRating() : 1000;
            return Integer.compare(ratingA, ratingB);
        });

        // 레이팅 차이가 가장 적은 플레이어 찾기
        List<UUID> bestMatch = new ArrayList<>();
        int minDiff = Integer.MAX_VALUE;

        for (int i = 0; i <= players.size() - count; i++) {
            int ratingFirst = plugin.getRankingManager().getRanking(players.get(i)) != null ? 
                    plugin. getRankingManager().getRanking(players.get(i)).getRating() : 1000;
            int ratingLast = plugin.getRankingManager().getRanking(players. get(i + count - 1)) != null ?
                    plugin.getRankingManager().getRanking(players.get(i + count - 1)).getRating() : 1000;
            
            int diff = Math.abs(ratingLast - ratingFirst);
            
            if (diff < minDiff && diff <= matchmakingRatingRange) {
                minDiff = diff;
                bestMatch = new ArrayList<>(players.subList(i, i + count));
            }
        }

        // 레이팅 범위 내에서 찾지 못하면 대기 시간이 긴 플레이어 우선
        if (bestMatch.isEmpty() && players.size() >= count) {
            // 대기 시간순 정렬
            players.sort((a, b) -> Long.compare(
                    queueStartTime. getOrDefault(a, 0L),
                    queueStartTime.getOrDefault(b, 0L)));
            bestMatch = new ArrayList<>(players.subList(0, count));
        }

        return bestMatch;
    }

    /**
     * 아레나 찾기 또는 생성
     */
    private PvPArena findOrCreateArena(ArenaType type) {
        List<PvPArena> available = getAvailableArenas(type);
        
        if (!available.isEmpty()) {
            return available.get(0);
        }

        // 사용 가능한 아레나가 없으면 대기 중인 아레나 중 하나 반환
        for (PvPArena arena : getArenasByType(type)) {
            if (arena.getStatus() == ArenaStatus.WAITING) {
                return arena;
            }
        }

        return null;
    }

    /**
     * 팀 배정
     */
    private void assignPlayerToTeam(PvPArena arena, Player player) {
        Map<String, List<UUID>> teams = arena.getTeams();
        
        // 팀이 없으면 생성
        if (teams.isEmpty()) {
            teams. put("Red", new ArrayList<>());
            teams. put("Blue", new ArrayList<>());
        }

        // 인원이 적은 팀에 배정
        String assignTeam = "Red";
        int minSize = Integer.MAX_VALUE;
        
        for (Map.Entry<String, List<UUID>> entry : teams.entrySet()) {
            if (entry.getValue().size() < minSize) {
                minSize = entry.getValue().size();
                assignTeam = entry.getKey();
            }
        }

        arena.assignToTeam(player. getUniqueId(), assignTeam);
        MessageUtil.sendMessage(player, "&e" + assignTeam + " 팀에 배정되었습니다.");
    }

    /**
     * 시작 조건 확인
     */
    private void checkStartCondition(PvPArena arena) {
        if (arena.canStart()) {
            startArena(arena);
        }
    }

    /**
     * 종료 조건 확인
     */
    private void checkEndCondition(PvPArena arena) {
        if (arena.getStatus() != ArenaStatus. ACTIVE) {
            return;
        }

        List<UUID> alivePlayers = arena. getAlivePlayers();

        // 1대1 듀얼
        if (arena.getType() == ArenaType. DUEL_1V1) {
            if (alivePlayers.size() <= 1) {
                UUID winnerId = alivePlayers.isEmpty() ? null : alivePlayers.get(0);
                endArena(arena, winnerId);
            }
        }
        
        // 배틀로얄
        else if (arena.getType() == ArenaType. BATTLE_ROYALE) {
            if (alivePlayers. size() <= 1) {
                UUID winnerId = alivePlayers.isEmpty() ? null : alivePlayers.get(0);
                endArena(arena, winnerId);
            }
        }
        
        // 팀전
        else if (arena.isTeamBased()) {
            Set<String> aliveTeams = new HashSet<>();
            for (UUID playerId : alivePlayers) {
                String team = arena.getPlayerTeam(playerId);
                if (team != null) {
                    aliveTeams.add(team);
                }
            }
            
            if (aliveTeams.size() <= 1) {
                endArena(arena, null);
            }
        }
    }

    /**
     * 아레나에 있는지 확인
     */
    public boolean isInArena(Player player) {
        return playerArenaMap.containsKey(player.getUniqueId());
    }

    /**
     * 플레이어의 아레나 조회
     */
    public PvPArena getPlayerArena(Player player) {
        UUID arenaId = playerArenaMap.get(player.getUniqueId());
        if (arenaId == null) {
            return null;
        }
        return arenas.get(arenaId);
    }

    /**
     * 아레나에 메시지 브로드캐스트
     */
    public void broadcastToArena(PvPArena arena, String message) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&cPvP&8]&r ");
        
        for (UUID playerId : arena. getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                MessageUtil.sendMessage(player, message);
            }
        }
        
        for (UUID spectatorId : arena.getSpectators()) {
            Player spectator = Bukkit.getPlayer(spectatorId);
            if (spectator != null) {
                MessageUtil.sendMessage(spectator, message);
            }
        }
    }

    /**
     * 아레나 로드
     */
    public void loadArena(PvPArena arena) {
        arenas.put(arena.getArenaId(), arena);
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadConfig();
    }
}