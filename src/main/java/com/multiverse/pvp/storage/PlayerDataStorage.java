package com.multiverse.pvp.  storage;

import com.multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.*;
import com.multiverse.pvp. enums.PvPTier;
import com.multiverse.pvp.enums. PvPType;
import com. multiverse.pvp.enums.  StreakLevel;
import org.bukkit.Material;
import org. bukkit.configuration.ConfigurationSection;
import org.bukkit. configuration.file.FileConfiguration;

import java.util.*;

public class PlayerDataStorage {

    private final PvPCore plugin;
    private final DataManager dataManager;

    public PlayerDataStorage(PvPCore plugin, DataManager dataManager) {
        this. plugin = plugin;
        this.dataManager = dataManager;
    }

    /**
     * 플레이어 데이터 로드
     */
    public void loadPlayer(UUID playerId) {
        FileConfiguration config = dataManager.loadPlayerDataFile(playerId.toString());

        if (config. getKeys(false).isEmpty()) {
            // 새 플레이어
            return;
        }

        // PvP 모드 로드
        loadPvPMode(playerId, config);

        // 랭킹 로드
        loadRanking(playerId, config);

        // 통계 로드
        loadStatistics(playerId, config);

        // 킬 스트릭 로드
        loadKillStreak(playerId, config);

        // 칭호 로드
        loadTitles(playerId, config);
    }

    /**
     * 플레이어 데이터 저장
     */
    public void savePlayer(UUID playerId) {
        FileConfiguration config = dataManager. loadPlayerDataFile(playerId.toString());

        // PvP 모드 저장
        savePvPMode(playerId, config);

        // 랭킹 저장
        saveRanking(playerId, config);

        // 통계 저장
        saveStatistics(playerId, config);

        // 킬 스트릭 저장
        saveKillStreak(playerId, config);

        // 칭호 저장
        saveTitles(playerId, config);

        // 저장 시간
        config.set("last-save", System.currentTimeMillis());

        dataManager.savePlayerDataFile(playerId.toString(), config);
    }

    /**
     * 모든 플레이어 저장
     */
    public void saveAllPlayers() {
        for (UUID playerId : plugin.getPvPModeManager().getAllPvPModes().keySet()) {
            savePlayer(playerId);
        }

        plugin.getLogger().info("모든 플레이어 데이터 저장 완료");
    }

    // ==================== PvP 모드 ====================

    private void loadPvPMode(UUID playerId, FileConfiguration config) {
        if (! config.contains("pvp-mode")) {
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("pvp-mode");
        if (section == null) return;

        PvPMode mode = new PvPMode(playerId);
        mode.setType(PvPType.fromString(section.getString("type", "CONSENSUAL")));
        mode.setEnabled(section.getBoolean("enabled", false));
        mode.setProtectionLevel(section.getInt("protection-level", 0));
        mode.setProtectionEndTime(section.getLong("protection-end-time", 0));
        mode.setAllowPartyPvP(section.getBoolean("allow-party-pvp", false));
        mode.setAllowGuildPvP(section.getBoolean("allow-guild-pvp", false));

        // 블랙리스트
        List<String> blacklistStr = section.getStringList("blacklist");
        Set<UUID> blacklist = new HashSet<>();
        for (String str : blacklistStr) {
            try {
                blacklist.add(UUID. fromString(str));
            } catch (IllegalArgumentException ignored) {}
        }
        mode.setBlacklist(blacklist);

        plugin.getPvPModeManager().loadPlayerData(playerId, mode);
    }

    private void savePvPMode(UUID playerId, FileConfiguration config) {
        PvPMode mode = plugin.getPvPModeManager().getPvPMode(playerId);
        if (mode == null) return;

        String path = "pvp-mode. ";
        config.set(path + "type", mode. getType().name());
        config.set(path + "enabled", mode.isEnabled());
        config.set(path + "protection-level", mode.getProtectionLevel());
        config.set(path + "protection-end-time", mode.getProtectionEndTime());
        config.set(path + "allow-party-pvp", mode.isAllowPartyPvP());
        config.set(path + "allow-guild-pvp", mode.isAllowGuildPvP());

        List<String> blacklistStr = new ArrayList<>();
        for (UUID uuid : mode.getBlacklist()) {
            blacklistStr.add(uuid. toString());
        }
        config.set(path + "blacklist", blacklistStr);
    }

    // ==================== 랭킹 ====================

    private void loadRanking(UUID playerId, FileConfiguration config) {
        if (!config. contains("ranking")) {
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("ranking");
        if (section == null) return;

        PvPRanking ranking = new PvPRanking(playerId);
        ranking.setRating(section.getInt("rating", 1000));
        ranking.setPeakRating(section.getInt("peak-rating", 1000));
        ranking.setWins(section.getInt("wins", 0));
        ranking.setLosses(section.getInt("losses", 0));
        ranking.setDraws(section.getInt("draws", 0));
        ranking.setKills(section.getInt("kills", 0));
        ranking.setDeaths(section.getInt("deaths", 0));
        ranking.setAssists(section.getInt("assists", 0));
        ranking.setWinStreak(section.getInt("win-streak", 0));
        ranking.setLoseStreak(section.getInt("lose-streak", 0));
        ranking.setMaxWinStreak(section. getInt("max-win-streak", 0));
        ranking.setMaxLoseStreak(section. getInt("max-lose-streak", 0));
        ranking.setCurrentSeason(section.getInt("current-season", 1));
        ranking.setSeasonWins(section.getInt("season-wins", 0));
        ranking.setSeasonLosses(section.getInt("season-losses", 0));
        ranking.setSeasonRating(section.getInt("season-rating", 1000));
        ranking.setPvpPoints(section.getInt("pvp-points", 0));
        ranking.setLastUpdateTime(section.getLong("last-update", System.currentTimeMillis()));
        ranking.updateTier();

        plugin.getRankingManager().loadPlayerData(playerId, ranking);
    }

    private void saveRanking(UUID playerId, FileConfiguration config) {
        PvPRanking ranking = plugin.getRankingManager().getRanking(playerId);
        if (ranking == null) return;

        String path = "ranking.";
        config.set(path + "rating", ranking.getRating());
        config.set(path + "peak-rating", ranking.getPeakRating());
        config.set(path + "tier", ranking.getTier().name());
        config.set(path + "wins", ranking.getWins());
        config.set(path + "losses", ranking.getLosses());
        config.set(path + "draws", ranking. getDraws());
        config.set(path + "kills", ranking.getKills());
        config.set(path + "deaths", ranking.getDeaths());
        config.set(path + "assists", ranking.getAssists());
        config.set(path + "win-streak", ranking. getWinStreak());
        config.set(path + "lose-streak", ranking.getLoseStreak());
        config.set(path + "max-win-streak", ranking.getMaxWinStreak());
        config.set(path + "max-lose-streak", ranking.getMaxLoseStreak());
        config.set(path + "current-season", ranking.getCurrentSeason());
        config.set(path + "season-wins", ranking.getSeasonWins());
        config.set(path + "season-losses", ranking.getSeasonLosses());
        config.set(path + "season-rating", ranking.getSeasonRating());
        config.set(path + "pvp-points", ranking.getPvpPoints());
        config.set(path + "last-update", ranking.getLastUpdateTime());
    }

    // ==================== 통계 ====================

    private void loadStatistics(UUID playerId, FileConfiguration config) {
        if (!config.contains("statistics")) {
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("statistics");
        if (section == null) return;

        PvPStatistics stats = new PvPStatistics(playerId);
        stats.setTotalKills(section.getInt("total-kills", 0));
        stats.setTotalDeaths(section.getInt("total-deaths", 0));
        stats.setTotalAssists(section.getInt("total-assists", 0));
        stats.setTotalWins(section. getInt("total-wins", 0));
        stats.setTotalLosses(section.getInt("total-losses", 0));
        stats.setLongestKillStreak(section. getInt("longest-kill-streak", 0));
        stats.setHighestDamageDealt(section.getDouble("highest-damage-dealt", 0));
        stats.setMostKillsInMatch(section.getInt("most-kills-in-match", 0));
        stats.setDuelWins(section. getInt("duel-wins", 0));
        stats.setDuelLosses(section.getInt("duel-losses", 0));
        stats.setDuelSurrenders(section.getInt("duel-surrenders", 0));
        stats.setTotalDamageDealt(section. getLong("total-damage-dealt", 0));
        stats.setTotalDamageReceived(section.getLong("total-damage-received", 0));
        stats.setTotalHealing(section.getLong("total-healing", 0));
        stats.setFirstBloods(section.getInt("first-bloods", 0));
        stats.setShutdowns(section.getInt("shutdowns", 0));
        stats.setRevenges(section.getInt("revenges", 0));
        stats.setDoubleKills(section.getInt("double-kills", 0));
        stats.setTripleKills(section.getInt("triple-kills", 0));
        stats.setMultiKills(section.getInt("multi-kills", 0));
        stats.setTotalPlayTime(section.getLong("total-play-time", 0));
        stats.setLastPlayTime(section.getLong("last-play-time", System.currentTimeMillis()));

        // 무기별 킬
        ConfigurationSection weaponSection = section.getConfigurationSection("weapon-kills");
        if (weaponSection != null) {
            Map<Material, Integer> weaponKills = new HashMap<>();
            for (String key : weaponSection. getKeys(false)) {
                try {
                    Material material = Material.valueOf(key);
                    weaponKills.put(material, weaponSection.getInt(key));
                } catch (IllegalArgumentException ignored) {}
            }
            stats.setWeaponKills(weaponKills);
        }

        // 일일/주간/월간 킬
        loadStringIntMap(section, "daily-kills", stats.getDailyKills());
        loadStringIntMap(section, "weekly-kills", stats. getWeeklyKills());
        loadStringIntMap(section, "monthly-kills", stats.getMonthlyKills());

        // 아레나 승패
        loadStringIntMap(section, "arena-wins", stats.getArenaWins());
        loadStringIntMap(section, "arena-losses", stats.getArenaLosses());

        stats.updateKDA();
        plugin.getStatisticsManager().loadPlayerData(playerId, stats);
    }

    private void saveStatistics(UUID playerId, FileConfiguration config) {
        PvPStatistics stats = plugin.getStatisticsManager().getStatistics(playerId);
        if (stats == null) return;

        String path = "statistics.";
        config.set(path + "total-kills", stats.getTotalKills());
        config.set(path + "total-deaths", stats.getTotalDeaths());
        config.set(path + "total-assists", stats.getTotalAssists());
        config.set(path + "total-wins", stats.getTotalWins());
        config.set(path + "total-losses", stats.getTotalLosses());
        config.set(path + "longest-kill-streak", stats.getLongestKillStreak());
        config.set(path + "highest-damage-dealt", stats.getHighestDamageDealt());
        config.set(path + "most-kills-in-match", stats. getMostKillsInMatch());
        config.set(path + "duel-wins", stats.getDuelWins());
        config.set(path + "duel-losses", stats. getDuelLosses());
        config.set(path + "duel-surrenders", stats.getDuelSurrenders());
        config.set(path + "total-damage-dealt", stats.getTotalDamageDealt());
        config.set(path + "total-damage-received", stats.getTotalDamageReceived());
        config.set(path + "total-healing", stats.getTotalHealing());
        config.set(path + "first-bloods", stats. getFirstBloods());
        config.set(path + "shutdowns", stats. getShutdowns());
        config.set(path + "revenges", stats. getRevenges());
        config.set(path + "double-kills", stats. getDoubleKills());
        config.set(path + "triple-kills", stats.getTripleKills());
        config.set(path + "multi-kills", stats.getMultiKills());
        config.set(path + "total-play-time", stats. getTotalPlayTime());
        config.set(path + "last-play-time", stats.getLastPlayTime());

        // 무기별 킬
        for (Map.Entry<Material, Integer> entry : stats.getWeaponKills().entrySet()) {
            config.set(path + "weapon-kills." + entry.getKey().name(), entry.getValue());
        }

        // 일일/주간/월간 킬
        saveStringIntMap(config, path + "daily-kills", stats.getDailyKills());
        saveStringIntMap(config, path + "weekly-kills", stats.getWeeklyKills());
        saveStringIntMap(config, path + "monthly-kills", stats.getMonthlyKills());

        // 아레나 승패
        saveStringIntMap(config, path + "arena-wins", stats.getArenaWins());
        saveStringIntMap(config, path + "arena-losses", stats.getArenaLosses());
    }

    // ==================== 킬 스트릭 ====================

    private void loadKillStreak(UUID playerId, FileConfiguration config) {
        if (! config.contains("kill-streak")) {
            return;
        }

        ConfigurationSection section = config. getConfigurationSection("kill-streak");
        if (section == null) return;

        KillStreak streak = new KillStreak(playerId);
        streak.setCurrentStreak(0); // 현재 스트릭은 세션별
        streak.setBestStreak(section.getInt("best-streak", 0));

        // 스트릭 달성 횟수
        ConfigurationSection achievementsSection = section.getConfigurationSection("achievements");
        if (achievementsSection != null) {
            Map<StreakLevel, Integer> achievements = new EnumMap<>(StreakLevel.class);
            for (StreakLevel level :  StreakLevel. values()) {
                achievements.put(level, achievementsSection.getInt(level.name(), 0));
            }
            streak. setStreakAchievements(achievements);
        }

        plugin. getKillStreakManager().loadPlayerData(playerId, streak);
    }

    private void saveKillStreak(UUID playerId, FileConfiguration config) {
        KillStreak streak = plugin.getKillStreakManager().getKillStreak(playerId);
        if (streak == null) return;

        String path = "kill-streak. ";
        config. set(path + "best-streak", streak. getBestStreak());

        for (Map.Entry<StreakLevel, Integer> entry : streak.getStreakAchievements().entrySet()) {
            config.set(path + "achievements." + entry.getKey().name(), entry.getValue());
        }
    }

    // ==================== 칭호 ====================

    private void loadTitles(UUID playerId, FileConfiguration config) {
        if (!config.contains("titles")) {
            return;
        }

        ConfigurationSection section = config. getConfigurationSection("titles");
        if (section == null) return;

        Set<String> unlockedTitles = new HashSet<>(section.getStringList("unlocked"));
        String activeTitle = section.getString("active", null);

        plugin.getTitleManager().loadPlayerData(playerId, unlockedTitles, activeTitle);
    }

    private void saveTitles(UUID playerId, FileConfiguration config) {
        Set<String> unlockedTitles = plugin.getTitleManager().getUnlockedTitleIds(playerId);
        String activeTitle = plugin.getTitleManager().getActiveTitleId(playerId);

        config.set("titles. unlocked", new ArrayList<>(unlockedTitles));
        config.set("titles.active", activeTitle);
    }

    // ==================== 유틸리티 메서드 ====================

    private void loadStringIntMap(ConfigurationSection parent, String key, Map<String, Integer> target) {
        ConfigurationSection section = parent.getConfigurationSection(key);
        if (section != null) {
            for (String mapKey : section.getKeys(false)) {
                target.put(mapKey, section.getInt(mapKey));
            }
        }
    }

    private void saveStringIntMap(FileConfiguration config, String path, Map<String, Integer> source) {
        for (Map.Entry<String, Integer> entry : source.entrySet()) {
            config.set(path + "." + entry.getKey(), entry.getValue());
        }
    }
}