package com. multiverse.pvp.managers;

import com.multiverse. pvp.PvPCore;
import com.multiverse. pvp.data. PvPRanking;
import com.multiverse.pvp.data.PvPStatistics;
import com.multiverse.pvp.enums.LeaderboardType;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java. time.format.DateTimeFormatter;
import java.util.*;
import java.util. concurrent.ConcurrentHashMap;
import java.util. stream.Collectors;

public class LeaderboardManager {

    private final PvPCore plugin;

    // 캐시된 리더보드 데이터
    private final Map<LeaderboardType, List<PvPRanking>> dailyLeaderboards;
    private final Map<LeaderboardType, List<PvPRanking>> weeklyLeaderboards;
    private List<PvPRanking> seasonLeaderboard;

    // 일일/주간 통계 (별도 추적)
    private final Map<UUID, Map<LeaderboardType, Integer>> dailyStats;
    private final Map<UUID, Map<LeaderboardType, Integer>> weeklyStats;

    private String currentDailyKey;
    private String currentWeeklyKey;

    private boolean enabled;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter WEEK_FORMATTER = DateTimeFormatter.ofPattern("yyyy-'W'ww");

    public LeaderboardManager(PvPCore plugin) {
        this.plugin = plugin;
        this.dailyLeaderboards = new ConcurrentHashMap<>();
        this.weeklyLeaderboards = new ConcurrentHashMap<>();
        this.seasonLeaderboard = new ArrayList<>();
        this.dailyStats = new ConcurrentHashMap<>();
        this.weeklyStats = new ConcurrentHashMap<>();

        this.currentDailyKey = LocalDate.now().format(DATE_FORMATTER);
        this.currentWeeklyKey = LocalDate.now().format(WEEK_FORMATTER);

        loadConfig();
    }

    private void loadConfig() {
        this.enabled = plugin. getConfig().getBoolean("leaderboard.enabled", true);
    }

    /**
     * 일일 상위 랭킹 조회
     */
    public List<PvPRanking> getDailyTop(LeaderboardType type, int limit) {
        if (!enabled) {
            return new ArrayList<>();
        }

        checkDailyReset();

        List<PvPRanking> cached = dailyLeaderboards.get(type);
        if (cached != null) {
            return cached. stream().limit(limit).collect(Collectors. toList());
        }

        return new ArrayList<>();
    }

    /**
     * 주간 상위 랭킹 조회
     */
    public List<PvPRanking> getWeeklyTop(LeaderboardType type, int limit) {
        if (!enabled) {
            return new ArrayList<>();
        }

        checkWeeklyReset();

        List<PvPRanking> cached = weeklyLeaderboards.get(type);
        if (cached != null) {
            return cached.stream().limit(limit).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    /**
     * 시즌 상위 랭킹 조회
     */
    public List<PvPRanking> getSeasonTop(int limit) {
        if (!enabled) {
            return new ArrayList<>();
        }

        if (seasonLeaderboard.isEmpty()) {
            updateLeaderboard();
        }

        return seasonLeaderboard. stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 플레이어 일일 순위 조회
     */
    public int getPlayerDailyRank(Player player, LeaderboardType type) {
        checkDailyReset();

        List<PvPRanking> leaderboard = dailyLeaderboards.get(type);
        if (leaderboard == null) {
            return -1;
        }

        for (int i = 0; i < leaderboard.size(); i++) {
            if (leaderboard.get(i).getPlayerId().equals(player.getUniqueId())) {
                return i + 1;
            }
        }

        return -1;
    }

    /**
     * 플레이어 주간 순위 조회
     */
    public int getPlayerWeeklyRank(Player player, LeaderboardType type) {
        checkWeeklyReset();

        List<PvPRanking> leaderboard = weeklyLeaderboards.get(type);
        if (leaderboard == null) {
            return -1;
        }

        for (int i = 0; i < leaderboard.size(); i++) {
            if (leaderboard.get(i).getPlayerId().equals(player.getUniqueId())) {
                return i + 1;
            }
        }

        return -1;
    }

    /**
     * 플레이어 시즌 순위 조회
     */
    public int getPlayerSeasonRank(Player player) {
        for (int i = 0; i < seasonLeaderboard.size(); i++) {
            if (seasonLeaderboard.get(i).getPlayerId().equals(player.getUniqueId())) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * 리더보드 업데이트
     */
    public void updateLeaderboard() {
        if (! enabled) {
            return;
        }

        // 시즌 리더보드 업데이트
        Map<UUID, PvPRanking> allRankings = plugin.getRankingManager().getAllRankings();
        seasonLeaderboard = new ArrayList<>(allRankings.values());
        seasonLeaderboard.sort((a, b) -> Integer.compare(b. getRating(), a.getRating()));

        // 일일/주간 리더보드 업데이트
        updateDailyLeaderboards();
        updateWeeklyLeaderboards();
    }

    /**
     * 일일 리더보드 업데이트
     */
    private void updateDailyLeaderboards() {
        checkDailyReset();

        for (LeaderboardType type : LeaderboardType.values()) {
            if (! type.supportsDailyLeaderboard()) {
                continue;
            }

            List<PvPRanking> leaderboard = buildLeaderboardFromDailyStats(type);
            dailyLeaderboards.put(type, leaderboard);
        }
    }

    /**
     * 주간 리더보드 업데이트
     */
    private void updateWeeklyLeaderboards() {
        checkWeeklyReset();

        for (LeaderboardType type : LeaderboardType.values()) {
            if (!type.supportsWeeklyLeaderboard()) {
                continue;
            }

            List<PvPRanking> leaderboard = buildLeaderboardFromWeeklyStats(type);
            weeklyLeaderboards.put(type, leaderboard);
        }
    }

    /**
     * 일일 통계로부터 리더보드 빌드
     */
    private List<PvPRanking> buildLeaderboardFromDailyStats(LeaderboardType type) {
        List<Map. Entry<UUID, Integer>> entries = new ArrayList<>();

        for (Map.Entry<UUID, Map<LeaderboardType, Integer>> entry : dailyStats.entrySet()) {
            Integer value = entry.getValue().get(type);
            if (value != null && value > 0) {
                entries.add(new AbstractMap.SimpleEntry<>(entry.getKey(), value));
            }
        }

        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        List<PvPRanking> result = new ArrayList<>();
        for (Map. Entry<UUID, Integer> entry : entries) {
            PvPRanking ranking = plugin.getRankingManager().getRanking(entry.getKey());
            if (ranking != null) {
                result.add(ranking);
            }
        }

        return result;
    }

    /**
     * 주간 통계로부터 리더보드 빌드
     */
    private List<PvPRanking> buildLeaderboardFromWeeklyStats(LeaderboardType type) {
        List<Map.Entry<UUID, Integer>> entries = new ArrayList<>();

        for (Map. Entry<UUID, Map<LeaderboardType, Integer>> entry :  weeklyStats.entrySet()) {
            Integer value = entry.getValue().get(type);
            if (value != null && value > 0) {
                entries. add(new AbstractMap.SimpleEntry<>(entry.getKey(), value));
            }
        }

        entries.sort((a, b) -> Integer.compare(b. getValue(), a.getValue()));

        List<PvPRanking> result = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : entries) {
            PvPRanking ranking = plugin.getRankingManager().getRanking(entry.getKey());
            if (ranking != null) {
                result.add(ranking);
            }
        }

        return result;
    }

    /**
     * 일일 통계 증가
     */
    public void incrementDailyStat(UUID playerId, LeaderboardType type, int amount) {
        checkDailyReset();

        Map<LeaderboardType, Integer> playerStats = dailyStats.computeIfAbsent(playerId, k -> new EnumMap<>(LeaderboardType.class));
        playerStats.put(type, playerStats.getOrDefault(type, 0) + amount);
    }

    /**
     * 주간 통계 증가
     */
    public void incrementWeeklyStat(UUID playerId, LeaderboardType type, int amount) {
        checkWeeklyReset();

        Map<LeaderboardType, Integer> playerStats = weeklyStats.computeIfAbsent(playerId, k -> new EnumMap<>(LeaderboardType.class));
        playerStats.put(type, playerStats.getOrDefault(type, 0) + amount);
    }

    /**
     * 킬 기록 시 호출
     */
    public void recordKill(UUID playerId) {
        incrementDailyStat(playerId, LeaderboardType.KILLS, 1);
        incrementWeeklyStat(playerId, LeaderboardType. KILLS, 1);
    }

    /**
     * 승리 기록 시 호출
     */
    public void recordWin(UUID playerId) {
        incrementDailyStat(playerId, LeaderboardType.WINS, 1);
        incrementWeeklyStat(playerId, LeaderboardType. WINS, 1);
    }

    /**
     * 스트릭 업데이트 시 호출
     */
    public void updateStreak(UUID playerId, int streak) {
        checkDailyReset();
        checkWeeklyReset();

        Map<LeaderboardType, Integer> dailyPlayerStats = dailyStats.computeIfAbsent(playerId, k -> new EnumMap<>(LeaderboardType.class));
        int currentDailyStreak = dailyPlayerStats. getOrDefault(LeaderboardType. STREAK, 0);
        if (streak > currentDailyStreak) {
            dailyPlayerStats.put(LeaderboardType. STREAK, streak);
        }

        Map<LeaderboardType, Integer> weeklyPlayerStats = weeklyStats.computeIfAbsent(playerId, k -> new EnumMap<>(LeaderboardType.class));
        int currentWeeklyStreak = weeklyPlayerStats.getOrDefault(LeaderboardType.STREAK, 0);
        if (streak > currentWeeklyStreak) {
            weeklyPlayerStats. put(LeaderboardType.STREAK, streak);
        }
    }

    /**
     * 일일 초기화 체크
     */
    private void checkDailyReset() {
        String todayKey = LocalDate.now().format(DATE_FORMATTER);
        if (! todayKey.equals(currentDailyKey)) {
            resetDailyLeaderboard();
            currentDailyKey = todayKey;
        }
    }

    /**
     * 주간 초기화 체크
     */
    private void checkWeeklyReset() {
        String thisWeekKey = LocalDate.now().format(WEEK_FORMATTER);
        if (!thisWeekKey.equals(currentWeeklyKey)) {
            resetWeeklyLeaderboard();
            currentWeeklyKey = thisWeekKey;
        }
    }

    /**
     * 일일 리더보드 초기화
     */
    public void resetDailyLeaderboard() {
        // 일일 보상 지급
        giveTopRewards(LeaderboardType.KILLS, dailyLeaderboards. get(LeaderboardType.KILLS), "일일");

        dailyStats.clear();
        dailyLeaderboards. clear();

        plugin.getLogger().info("일일 리더보드가 초기화되었습니다.");
    }

    /**
     * 주간 리더보드 초기화
     */
    public void resetWeeklyLeaderboard() {
        // 주간 보상 지급
        giveTopRewards(LeaderboardType. KILLS, weeklyLeaderboards.get(LeaderboardType. KILLS), "주간");

        weeklyStats.clear();
        weeklyLeaderboards.clear();

        plugin.getLogger().info("주간 리더보드가 초기화되었습니다.");
    }

    /**
     * 상위 플레이어에게 보상 지급
     */
    private void giveTopRewards(LeaderboardType type, List<PvPRanking> leaderboard, String period) {
        if (leaderboard == null || leaderboard.isEmpty()) {
            return;
        }

        for (int i = 0; i < Math.min(10, leaderboard. size()); i++) {
            PvPRanking ranking = leaderboard. get(i);
            org.bukkit.entity.Player player = org.bukkit. Bukkit.getPlayer(ranking.getPlayerId());

            if (player != null) {
                plugin.getRewardManager().giveRankingReward(player, i + 1);
            }
            // 오프라인 플레이어는 다음 접속 시 지급하도록 저장 (선택적)
        }
    }

    /**
     * 타입별 리더보드 데이터 조회
     */
    public List<PvPRanking> getLeaderboard(LeaderboardType type, int limit) {
        Map<UUID, PvPRanking> allRankings = plugin. getRankingManager().getAllRankings();
        List<PvPRanking> sorted = new ArrayList<>(allRankings. values());

        Comparator<PvPRanking> comparator = type.getComparator();
        sorted.sort(comparator);

        // 최소 게임 수 필터링
        int minGames = type.getMinimumGamesRequired();
        if (minGames > 0) {
            sorted.removeIf(r -> r. getTotalGames() < minGames);
        }

        return sorted.stream().limit(limit).collect(Collectors.toList());
    }

    public void reload() {
        loadConfig();
    }
}