package com.multiverse.pvp.storage;

import com. multiverse.pvp.PvPCore;
import org.bukkit.configuration.file.FileConfiguration;

public class SeasonStorage {

    private final PvPCore plugin;
    private final DataManager dataManager;

    public SeasonStorage(PvPCore plugin, DataManager dataManager) {
        this. plugin = plugin;
        this.dataManager = dataManager;
    }

    /**
     * 시즌 데이터 로드
     */
    public void loadSeasonData() {
        FileConfiguration config = dataManager.getSeasonConfig();

        if (! config.contains("season")) {
            // 첫 시즌 초기화
            plugin.getSeasonManager().initializeFirstSeason();
            return;
        }

        int currentSeason = config. getInt("season. current", 1);
        long startTime = config.getLong("season.start-time", System.currentTimeMillis());
        long endTime = config.getLong("season.end-time", System. currentTimeMillis() + (90L * 24 * 60 * 60 * 1000));
        boolean active = config.getBoolean("season.active", true);

        plugin.getSeasonManager().initializeSeason(currentSeason, startTime, endTime, active);

        plugin.getLogger().info("시즌 " + currentSeason + " 로드 완료");
    }

    /**
     * 시즌 데이터 저장
     */
    public void saveCurrentSeason() {
        FileConfiguration config = dataManager.getSeasonConfig();

        config.set("season. current", plugin.getSeasonManager().getCurrentSeason());
        config.set("season.start-time", plugin.getSeasonManager().getSeasonStartTime());
        config.set("season. end-time", plugin.getSeasonManager().getSeasonEndTime());
        config.set("season. active", plugin.getSeasonManager().isSeasonActive());
        config.set("season.last-save", System.currentTimeMillis());

        dataManager.saveSeasonConfig();

        plugin.getLogger().info("시즌 데이터 저장 완료");
    }

    /**
     * 시즌 히스토리 저장
     */
    public void saveSeasonHistory(int season) {
        FileConfiguration config = dataManager.getSeasonConfig();

        String path = "history." + season + ". ";

        config. set(path + "start-time", plugin. getSeasonManager().getSeasonStartTime());
        config.set(path + "end-time", System.currentTimeMillis());

        // 상위 랭커 저장
        var topRankings = plugin.getRankingManager().getTopRankings(100);
        for (int i = 0; i < topRankings.size(); i++) {
            var ranking = topRankings.get(i);
            String rankPath = path + "top-players." + (i + 1) + ".";
            
            config.set(rankPath + "uuid", ranking.getPlayerId().toString());
            config.set(rankPath + "rating", ranking.getRating());
            config. set(rankPath + "tier", ranking.getTier().name());
            config.set(rankPath + "wins", ranking.getSeasonWins());
            config.set(rankPath + "losses", ranking.getSeasonLosses());
        }

        // 통계
        config.set(path + "stats. total-players", plugin.getRankingManager().getAllRankings().size());
        config.set(path + "stats.average-rating", plugin. getRankingManager().getAverageRating());

        var tierDist = plugin.getRankingManager().getTierDistribution();
        for (var entry : tierDist. entrySet()) {
            config.set(path + "stats.tier-distribution." + entry.getKey().name(), entry.getValue());
        }

        dataManager.saveSeasonConfig();

        plugin.getLogger().info("시즌 " + season + " 히스토리 저장 완료");
    }

    /**
     * 시즌 히스토리 로드
     */
    public SeasonHistory loadSeasonHistory(int season) {
        FileConfiguration config = dataManager.getSeasonConfig();

        String path = "history." + season + ". ";

        if (! config.contains(path + "start-time")) {
            return null;
        }

        SeasonHistory history = new SeasonHistory();
        history.season = season;
        history.startTime = config.getLong(path + "start-time");
        history.endTime = config.getLong(path + "end-time");
        history.totalPlayers = config. getInt(path + "stats.total-players");
        history.averageRating = config.getDouble(path + "stats.average-rating");

        return history;
    }

    /**
     * 시즌 히스토리 데이터 클래스
     */
    public static class SeasonHistory {
        public int season;
        public long startTime;
        public long endTime;
        public int totalPlayers;
        public double averageRating;
    }
}