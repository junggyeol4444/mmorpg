package com.multiverse.pvp. managers;

import com.multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPRanking;
import com.multiverse. pvp.enums.PvPTier;
import com.multiverse.pvp.utils.EloCalculator;
import com.multiverse.pvp.utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;

import java.util.*;
import java.util. concurrent.ConcurrentHashMap;
import java.util. stream.Collectors;

public class RankingManager {

    private final PvPCore plugin;
    private final Map<UUID, PvPRanking> rankings;
    private List<PvPRanking> sortedRankings;

    private int startingRating;
    private int kFactor;

    public RankingManager(PvPCore plugin) {
        this.plugin = plugin;
        this.rankings = new ConcurrentHashMap<>();
        this.sortedRankings = new ArrayList<>();
        loadConfig();
    }

    private void loadConfig() {
        this.startingRating = plugin.getConfig().getInt("ranking. elo.starting-rating", 1000);
        this.kFactor = plugin.getConfig().getInt("ranking. elo.k-factor", 32);
    }

    public PvPRanking getRanking(Player player) {
        return getRanking(player.getUniqueId());
    }

    public PvPRanking getRanking(UUID playerId) {
        return rankings.computeIfAbsent(playerId, id -> {
            PvPRanking ranking = new PvPRanking(id, startingRating);
            ranking.setCurrentSeason(plugin.getSeasonManager().getCurrentSeason());
            return ranking;
        });
    }

    public void setRanking(UUID playerId, PvPRanking ranking) {
        rankings.put(playerId, ranking);
        updateSortedRankings();
    }

    public void updateRating(Player winner, Player loser) {
        PvPRanking winnerRanking = getRanking(winner);
        PvPRanking loserRanking = getRanking(loser);

        int winnerOldRating = winnerRanking.getRating();
        int loserOldRating = loserRanking.getRating();

        // ELO 계산
        int[] ratingChanges = EloCalculator.calculateRatingChange(
                winnerOldRating, loserOldRating, kFactor);

        int winnerChange = ratingChanges[0];
        int loserChange = ratingChanges[1];

        // 레이팅 적용
        winnerRanking.recordWin(winnerChange);
        loserRanking.recordLoss(loserChange);

        // 티어 변경 체크
        checkTierChange(winner, winnerRanking, winnerOldRating);
        checkTierChange(loser, loserRanking, loserOldRating);

        // 메시지
        MessageUtil.sendMessage(winner, "&a레이팅 +" + winnerChange + " (" + winnerRanking.getRating() + ")");
        MessageUtil. sendMessage(loser, "&c레이팅 " + loserChange + " (" + loserRanking. getRating() + ")");

        // 정렬 업데이트
        updateSortedRankings();
    }

    public int calculateRatingChange(int winnerRating, int loserRating) {
        return EloCalculator. calculateRatingChange(winnerRating, loserRating, kFactor)[0];
    }

    private void checkTierChange(Player player, PvPRanking ranking, int oldRating) {
        PvPTier oldTier = PvPTier. getTier(oldRating);
        PvPTier newTier = ranking.getTier();

        if (oldTier != newTier) {
            if (newTier. isHigherThan(oldTier)) {
                // 승급
                MessageUtil.sendMessage(player, plugin.getConfig().getString("messages.ranking.promoted",
                        "&a티어 승급! {tier}").replace("{tier}", newTier.getFormattedName()));

                player.playSound(player.getLocation(),
                        org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

                // 승급 보상
                plugin.getRewardManager().giveTierPromotionReward(player, newTier);
            } else {
                // 강등
                MessageUtil.sendMessage(player, plugin.getConfig().getString("messages. ranking.demoted",
                        "&c티어 강등! {tier}").replace("{tier}", newTier.getFormattedName()));

                player.playSound(player.getLocation(),
                        org.bukkit.Sound. ENTITY_VILLAGER_NO, 1f, 1f);
            }
        }
    }

    public void checkTierPromotion(Player player) {
        PvPRanking ranking = getRanking(player);
        ranking.updateTier();
    }

    public List<PvPRanking> getTopRankings(int limit) {
        if (sortedRankings. isEmpty()) {
            updateSortedRankings();
        }

        return sortedRankings.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public int getPlayerRank(Player player) {
        return getPlayerRank(player.getUniqueId());
    }

    public int getPlayerRank(UUID playerId) {
        if (sortedRankings. isEmpty()) {
            updateSortedRankings();
        }

        for (int i = 0; i < sortedRankings.size(); i++) {
            if (sortedRankings.get(i).getPlayerId().equals(playerId)) {
                return i + 1;
            }
        }

        return -1;
    }

    public PvPTier getTier(Player player) {
        PvPRanking ranking = getRanking(player);
        return ranking.getTier();
    }

    public void addRating(Player player, int amount) {
        PvPRanking ranking = getRanking(player);
        int oldRating = ranking.getRating();
        ranking.addRating(amount);

        checkTierChange(player, ranking, oldRating);
        updateSortedRankings();

        MessageUtil.sendMessage(player, "&a레이팅 +" + amount + " (" + ranking. getRating() + ")");
    }

    public void removeRating(Player player, int amount) {
        PvPRanking ranking = getRanking(player);
        int oldRating = ranking.getRating();
        ranking.removeRating(amount);

        checkTierChange(player, ranking, oldRating);
        updateSortedRankings();

        MessageUtil.sendMessage(player, "&c레이팅 -" + amount + " (" + ranking. getRating() + ")");
    }

    public void addPvPPoints(Player player, int amount) {
        PvPRanking ranking = getRanking(player);
        ranking.addPvpPoints(amount);

        MessageUtil.sendMessage(player, "&aPvP 포인트 +" + amount);
    }

    public void recordKill(Player player) {
        PvPRanking ranking = getRanking(player);
        ranking.addKill();
    }

    public void recordDeath(Player player) {
        PvPRanking ranking = getRanking(player);
        ranking.addDeath();
    }

    public void recordAssist(Player player) {
        PvPRanking ranking = getRanking(player);
        ranking.addAssist();
    }

    public void startNewSeason() {
        int newSeason = plugin. getSeasonManager().getCurrentSeason();
        double resetPercentage = plugin. getConfig().getDouble("ranking.season. rating-reset-percentage", 50.0);

        for (PvPRanking ranking : rankings.values()) {
            ranking.resetSeason(newSeason, resetPercentage);
        }

        updateSortedRankings();
        plugin.getLogger().info("시즌 " + newSeason + " 시작 - 모든 레이팅이 초기화되었습니다.");
    }

    public void resetRatings(double percentage) {
        for (PvPRanking ranking : rankings.values()) {
            int baseRating = startingRating;
            int currentRating = ranking. getRating();
            int diff = currentRating - baseRating;
            int newRating = baseRating + (int) (diff * (percentage / 100.0));
            ranking.setRating(newRating);
        }

        updateSortedRankings();
    }

    public void resetRanking(Player player) {
        PvPRanking ranking = getRanking(player);
        ranking.reset();
        updateSortedRankings();
    }

    public void resetAllRankings() {
        for (PvPRanking ranking : rankings.values()) {
            ranking.reset();
        }
        updateSortedRankings();
    }

    private void updateSortedRankings() {
        sortedRankings = new ArrayList<>(rankings.values());
        sortedRankings.sort((a, b) -> Integer.compare(b.getRating(), a.getRating()));
    }

    public List<PvPRanking> getRankingsByTier(PvPTier tier) {
        return rankings.values().stream()
                .filter(r -> r.getTier() == tier)
                .sorted((a, b) -> Integer.compare(b.getRating(), a.getRating()))
                .collect(Collectors.toList());
    }

    public Map<PvPTier, Integer> getTierDistribution() {
        Map<PvPTier, Integer> distribution = new EnumMap<>(PvPTier. class);

        for (PvPTier tier : PvPTier.values()) {
            distribution.put(tier, 0);
        }

        for (PvPRanking ranking : rankings.values()) {
            PvPTier tier = ranking.getTier();
            distribution. put(tier, distribution. get(tier) + 1);
        }

        return distribution;
    }

    public double getAverageRating() {
        if (rankings.isEmpty()) {
            return startingRating;
        }

        long totalRating = 0;
        for (PvPRanking ranking : rankings.values()) {
            totalRating += ranking.getRating();
        }

        return (double) totalRating / rankings.size();
    }

    public void loadPlayerData(UUID playerId, PvPRanking ranking) {
        rankings.put(playerId, ranking);
    }

    public void unloadPlayerData(UUID playerId) {
        // 저장 후 언로드하지 않음 (랭킹은 유지)
    }

    public Map<UUID, PvPRanking> getAllRankings() {
        return new HashMap<>(rankings);
    }

    public void reload() {
        loadConfig();
        updateSortedRankings();
    }
}