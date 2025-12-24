package com.multiverse.pvp.managers;

import com.multiverse.pvp.PvPCore;
import com.multiverse.pvp.data.PvPRanking;
import com.multiverse. pvp.enums.PvPTier;
import com.multiverse.pvp.utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;

import java.util.*;

public class SeasonManager {

    private final PvPCore plugin;

    private int currentSeason;
    private long seasonStartTime;
    private long seasonEndTime;
    private int seasonDurationDays;
    private double ratingResetPercentage;

    private boolean seasonActive;

    public SeasonManager(PvPCore plugin) {
        this. plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        this.seasonDurationDays = plugin.getConfig().getInt("ranking.season.duration-days", 90);
        this.ratingResetPercentage = plugin. getConfig().getDouble("ranking.season. rating-reset-percentage", 50.0);
    }

    /**
     * 시즌 데이터 로드
     */
    public void loadSeasonData(int season, long startTime, long endTime) {
        this.currentSeason = season;
        this.seasonStartTime = startTime;
        this.seasonEndTime = endTime;
        this.seasonActive = System.currentTimeMillis() < endTime;
    }

    /**
     * 현재 시즌 번호 조회
     */
    public int getCurrentSeason() {
        return currentSeason;
    }

    /**
     * 시즌 시작 시간 조회
     */
    public long getSeasonStartTime() {
        return seasonStartTime;
    }

    /**
     * 시즌 종료 시간 조회
     */
    public long getSeasonEndTime() {
        return seasonEndTime;
    }

    /**
     * 남은 시간 조회 (초)
     */
    public long getRemainingTime() {
        long remaining = (seasonEndTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    /**
     * 남은 일수 조회
     */
    public int getRemainingDays() {
        return (int) (getRemainingTime() / 86400);
    }

    /**
     * 시즌 활성 여부
     */
    public boolean isSeasonActive() {
        return seasonActive && System.currentTimeMillis() < seasonEndTime;
    }

    /**
     * 시즌 진행률 (0.0 ~ 1.0)
     */
    public double getSeasonProgress() {
        long totalDuration = seasonEndTime - seasonStartTime;
        long elapsed = System.currentTimeMillis() - seasonStartTime;
        return Math.min(1.0, Math.max(0.0, (double) elapsed / totalDuration));
    }

    /**
     * 새 시즌 시작
     */
    public void startNewSeason() {
        // 이전 시즌 종료 처리
        if (seasonActive) {
            endSeason();
        }

        // 새 시즌 설정
        currentSeason++;
        seasonStartTime = System.currentTimeMillis();
        seasonEndTime = seasonStartTime + (seasonDurationDays * 24L * 60L * 60L * 1000L);
        seasonActive = true;

        // 랭킹 초기화
        plugin.getRankingManager().startNewSeason();

        // 리더보드 초기화
        plugin.getLeaderboardManager().resetDailyLeaderboard();
        plugin.getLeaderboardManager().resetWeeklyLeaderboard();

        // 통계 시즌 초기화
        plugin.getStatisticsManager().resetSeasonStatistics();

        // 저장
        plugin.getSeasonStorage().saveCurrentSeason();

        // 공지
        Bukkit.broadcastMessage(MessageUtil.colorize(
                "&6&l========================================"));
        Bukkit.broadcastMessage(MessageUtil.colorize(
                "&e&l        PvP 시즌 " + currentSeason + " 시작! "));
        Bukkit.broadcastMessage(MessageUtil.colorize(
                "&a   새로운 시즌이 시작되었습니다! "));
        Bukkit.broadcastMessage(MessageUtil.colorize(
                "&7   기간: " + seasonDurationDays + "일"));
        Bukkit.broadcastMessage(MessageUtil. colorize(
                "&6&l========================================"));

        // 효과음
        for (Player player :  Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(),
                    org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }

        plugin.getLogger().info("시즌 " + currentSeason + " 시작됨");
    }

    /**
     * 시즌 종료
     */
    public void endSeason() {
        if (!seasonActive) {
            return;
        }

        seasonActive = false;

        // 시즌 보상 지급
        giveSeasonRewards();

        // 공지
        Bukkit.broadcastMessage(MessageUtil.colorize(
                "&6&l========================================"));
        Bukkit.broadcastMessage(MessageUtil.colorize(
                "&c&l        PvP 시즌 " + currentSeason + " 종료!"));
        Bukkit. broadcastMessage(MessageUtil.colorize(
                "&e   시즌 보상이 지급되었습니다!"));
        Bukkit.broadcastMessage(MessageUtil.colorize(
                "&6&l========================================"));

        // 저장
        plugin. getSeasonStorage().saveCurrentSeason();

        plugin.getLogger().info("시즌 " + currentSeason + " 종료됨");
    }

    /**
     * 시즌 보상 지급
     */
    private void giveSeasonRewards() {
        Map<UUID, PvPRanking> allRankings = plugin.getRankingManager().getAllRankings();

        // 순위별 보상
        List<PvPRanking> sortedRankings = new ArrayList<>(allRankings.values());
        sortedRankings.sort((a, b) -> Integer.compare(b. getRating(), a.getRating()));

        for (int i = 0; i < sortedRankings. size(); i++) {
            PvPRanking ranking = sortedRankings.get(i);
            Player player = Bukkit.getPlayer(ranking.getPlayerId());

            if (player != null) {
                // 순위 보상
                if (i < 100) {
                    plugin.getRewardManager().giveRankingReward(player, i + 1);
                }

                // 티어 보상
                plugin.getRewardManager().giveSeasonReward(player, ranking.getTier());

                // 시즌 칭호 해금
                unlockSeasonTitle(player, ranking, i + 1);
            } else {
                // 오프라인 플레이어는 저장해두고 다음 접속 시 지급
                savePendingReward(ranking. getPlayerId(), ranking.getTier(), i + 1);
            }
        }
    }

    /**
     * 시즌 칭호 해금
     */
    private void unlockSeasonTitle(Player player, PvPRanking ranking, int rank) {
        String titleId = null;

        // 랭크 기반 칭호
        if (rank == 1) {
            titleId = "season_" + currentSeason + "_champion";
            plugin.getTitleManager().registerTitle(
                    com.multiverse.pvp.data.PvPTitle.builder(titleId)
                            .displayName("시즌 " + currentSeason + " 챔피언")
                            .category(com.multiverse. pvp.enums.TitleCategory.SEASONAL)
                            . color("&6&l")
                            .prefix("&6&l[S" + currentSeason + " 챔피언]")
                            .rarity(com.multiverse.pvp. data.PvPTitle.TitleRarity. MYTHIC)
                            .description("시즌 " + currentSeason + " 1위 달성")
                            .build()
            );
        } else if (rank <= 3) {
            titleId = "season_" + currentSeason + "_top3";
        } else if (rank <= 10) {
            titleId = "season_" + currentSeason + "_top10";
        }

        // 티어 기반 칭호
        if (ranking.getTier().ordinal() >= PvPTier. GOLD.ordinal()) {
            String tierTitleId = "season_" + currentSeason + "_" + ranking.getTier().name().toLowerCase();
            plugin.getTitleManager().unlockTitle(player, tierTitleId);
        }

        if (titleId != null) {
            plugin.getTitleManager().unlockTitle(player, titleId);
        }
    }

    /**
     * 오프라인 플레이어 보상 저장
     */
    private void savePendingReward(UUID playerId, PvPTier tier, int rank) {
        // 파일 또는 DB에 저장하여 다음 접속 시 지급
        // 구현은 PlayerDataStorage와 연동
    }

    /**
     * 오프라인 플레이어 보상 확인 및 지급 (로그인 시 호출)
     */
    public void checkPendingRewards(Player player) {
        // 저장된 보상이 있는지 확인하고 지급
    }

    /**
     * 시즌 종료 체크 (주기적으로 호출)
     */
    public void checkSeasonEnd() {
        if (seasonActive && System.currentTimeMillis() >= seasonEndTime) {
            endSeason();
            
            // 자동으로 새 시즌 시작 (선택적)
            if (plugin.getConfig().getBoolean("ranking.season.auto-start", true)) {
                Bukkit.getScheduler().runTaskLater(plugin, this::startNewSeason, 200L); // 10초 후
            }
        }
    }

    /**
     * 시즌 정보 문자열
     */
    public String getSeasonInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("&6시즌 ").append(currentSeason);
        
        if (seasonActive) {
            sb.append(" &7(진행 중)");
            long remaining = getRemainingTime();
            int days = (int) (remaining / 86400);
            int hours = (int) ((remaining % 86400) / 3600);
            sb.append("\n&e남은 시간: &f").append(days).append("일 ").append(hours).append("시간");
        } else {
            sb.append(" &7(종료됨)");
        }
        
        return sb.toString();
    }

    /**
     * 시즌 통계
     */
    public Map<String, Object> getSeasonStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Map<UUID, PvPRanking> allRankings = plugin.getRankingManager().getAllRankings();
        
        stats.put("totalPlayers", allRankings.size());
        stats.put("averageRating", plugin.getRankingManager().getAverageRating());
        stats.put("tierDistribution", plugin.getRankingManager().getTierDistribution());
        stats.put("seasonDuration", seasonDurationDays);
        stats.put("progress", getSeasonProgress());
        
        return stats;
    }

    /**
     * 설정된 시즌 시작
     */
    public void initializeSeason(int season, long startTime, long endTime, boolean active) {
        this.currentSeason = season;
        this.seasonStartTime = startTime;
        this.seasonEndTime = endTime;
        this.seasonActive = active;
    }

    /**
     * 첫 시즌 초기화 (데이터 없을 때)
     */
    public void initializeFirstSeason() {
        this.currentSeason = 1;
        this.seasonStartTime = System.currentTimeMillis();
        this.seasonEndTime = seasonStartTime + (seasonDurationDays * 24L * 60L * 60L * 1000L);
        this.seasonActive = true;
        
        plugin.getSeasonStorage().saveCurrentSeason();
        plugin.getLogger().info("첫 번째 시즌이 초기화되었습니다.");
    }

    public void reload() {
        loadConfig();
    }
}