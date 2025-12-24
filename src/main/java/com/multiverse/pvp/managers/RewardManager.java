package com.multiverse.pvp.managers;

import com. multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPReward;
import com.multiverse.pvp.data.PvPZone;
import com.multiverse.pvp.enums.ArenaType;
import com.multiverse. pvp.enums.PvPTier;
import com.multiverse.pvp.enums. StreakLevel;
import com.multiverse.pvp.utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit. inventory.ItemStack;

import java.util. HashMap;
import java. util.Map;

public class RewardManager {

    private final PvPCore plugin;

    // 기본 보상 설정
    private long killExperience;
    private double killMoney;
    private int killPvpPoints;

    private double winExperienceMultiplier;
    private double winMoneyMultiplier;
    private int winPvpPoints;

    private double loseExperienceMultiplier;
    private double loseMoneyMultiplier;
    private int losePvpPoints;

    public RewardManager(PvPCore plugin) {
        this. plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        this.killExperience = plugin.getConfig().getLong("rewards.kill.experience", 100);
        this.killMoney = plugin.getConfig().getDouble("rewards.kill.money", 50.0);
        this.killPvpPoints = plugin.getConfig().getInt("rewards. kill.pvp-points", 1);

        this.winExperienceMultiplier = plugin.getConfig().getDouble("rewards.win.experience-multiplier", 2.0);
        this.winMoneyMultiplier = plugin.getConfig().getDouble("rewards.win.money-multiplier", 2.0);
        this.winPvpPoints = plugin. getConfig().getInt("rewards.win. pvp-points", 10);

        this.loseExperienceMultiplier = plugin.getConfig().getDouble("rewards.lose.experience-multiplier", 0.5);
        this.loseMoneyMultiplier = plugin.getConfig().getDouble("rewards. lose.money-multiplier", 0.5);
        this.losePvpPoints = plugin.getConfig().getInt("rewards.lose.pvp-points", 2);
    }

    /**
     * 킬 보상 지급
     */
    public void giveKillReward(Player killer, Player victim) {
        PvPReward reward = calculateKillReward(killer, victim);

        if (reward. isEmpty()) {
            return;
        }

        // 보상 지급
        applyReward(killer, reward);

        // 메시지
        MessageUtil.sendMessage(killer, "&a킬 보상: " + formatRewardSummary(reward));
    }

    /**
     * 킬 보상 계산
     */
    public PvPReward calculateKillReward(Player killer, Player victim) {
        PvPReward reward = new PvPReward();

        // 기본 보상
        reward.setExperience(killExperience);
        reward.addDefaultMoney(killMoney);
        reward.setPvpPoints(killPvpPoints);

        // 킬 스트릭 배율
        double streakMultiplier = plugin.getKillStreakManager().getCurrentMultiplier(killer);
        if (streakMultiplier > 1.0) {
            reward.applyMultiplier(streakMultiplier);
            reward.setBonus(true);
            reward.setBonusReason("킬 스트릭 x" + String.format("%.1f", streakMultiplier));
        }

        // 지역 배율
        PvPZone zone = plugin.getZoneManager().getZone(killer.getLocation());
        if (zone != null && zone.getRewardMultiplier() > 1.0) {
            reward.applyMultiplier(zone.getRewardMultiplier());
        }

        // 티어 배율
        PvPTier killerTier = plugin.getRankingManager().getTier(killer);
        reward.applyMultiplier(killerTier.getRewardMultiplier());

        return reward;
    }

    /**
     * 승리 보상 지급
     */
    public void giveWinReward(Player winner, ArenaType arenaType) {
        PvPReward reward = calculateWinReward(winner, arenaType);

        if (reward.isEmpty()) {
            return;
        }

        applyReward(winner, reward);

        MessageUtil.sendMessage(winner, "&6승리 보상:");
        MessageUtil. sendMessage(winner, reward.getSummary());
    }

    /**
     * 승리 보상 계산
     */
    public PvPReward calculateWinReward(Player winner, ArenaType arenaType) {
        PvPReward reward = new PvPReward();

        // 기본 승리 보상
        long baseExp = (long) (killExperience * winExperienceMultiplier);
        double baseMoney = killMoney * winMoneyMultiplier;

        reward.setExperience(baseExp);
        reward.addDefaultMoney(baseMoney);
        reward.setPvpPoints(winPvpPoints);

        // 아레나 타입 배율
        if (arenaType != null) {
            reward.applyMultiplier(arenaType.getRewardMultiplier());
        }

        // 연승 보너스
        int winStreak = plugin.getRankingManager().getRanking(winner).getWinStreak();
        if (winStreak > 1) {
            double streakBonus = 1.0 + (0.1 * (winStreak - 1));
            streakBonus = Math.min(streakBonus, 2.0); // 최대 2배
            reward.applyMultiplier(streakBonus);
            reward.setBonus(true);
            reward.setBonusReason(winStreak + "연승 보너스");
        }

        // 티어 배율
        PvPTier tier = plugin.getRankingManager().getTier(winner);
        reward.applyMultiplier(tier. getRewardMultiplier());

        return reward;
    }

    /**
     * 패배 보상 지급
     */
    public void giveLoseReward(Player loser, ArenaType arenaType, int kills) {
        PvPReward reward = calculateLoseReward(loser, arenaType, kills);

        if (reward.isEmpty()) {
            return;
        }

        applyReward(loser, reward);

        MessageUtil.sendMessage(loser, "&e참가 보상:");
        MessageUtil. sendMessage(loser, reward.getSummary());
    }

    /**
     * 패배 보상 계산
     */
    public PvPReward calculateLoseReward(Player loser, ArenaType arenaType, int kills) {
        PvPReward reward = new PvPReward();

        // 기본 패배 보상
        long baseExp = (long) (killExperience * loseExperienceMultiplier);
        double baseMoney = killMoney * loseMoneyMultiplier;

        reward.setExperience(baseExp);
        reward.addDefaultMoney(baseMoney);
        reward.setPvpPoints(losePvpPoints);

        // 킬 보상 추가
        if (kills > 0) {
            reward.addExperience(killExperience * kills);
            reward. addDefaultMoney(killMoney * kills);
            reward.addPvpPoints(killPvpPoints * kills);
        }

        return reward;
    }

    /**
     * MVP 보상 지급
     */
    public void giveMvpReward(Player player, ArenaType arenaType) {
        PvPReward reward = new PvPReward();

        reward.setExperience((long) (killExperience * 0.5));
        reward.addDefaultMoney(killMoney * 0.5);
        reward.setPvpPoints(5);
        reward.setBonus(true);
        reward.setBonusReason("MVP 보너스");

        applyReward(player, reward);

        MessageUtil.sendMessage(player, "&d&lMVP 보상:");
        MessageUtil. sendMessage(player, reward.getSummary());
    }

    /**
     * 랭킹 보상 지급
     */
    public void giveRankingReward(Player player, int rank) {
        PvPReward reward = new PvPReward();

        // 순위별 보상
        if (rank == 1) {
            reward.setExperience(10000);
            reward.addDefaultMoney(10000.0);
            reward.setPvpPoints(1000);
        } else if (rank == 2) {
            reward.setExperience(7500);
            reward.addDefaultMoney(7500.0);
            reward. setPvpPoints(750);
        } else if (rank == 3) {
            reward.setExperience(5000);
            reward.addDefaultMoney(5000.0);
            reward.setPvpPoints(500);
        } else if (rank <= 10) {
            reward.setExperience(2500);
            reward.addDefaultMoney(2500.0);
            reward.setPvpPoints(250);
        } else if (rank <= 50) {
            reward.setExperience(1000);
            reward.addDefaultMoney(1000.0);
            reward.setPvpPoints(100);
        } else if (rank <= 100) {
            reward. setExperience(500);
            reward.addDefaultMoney(500.0);
            reward.setPvpPoints(50);
        }

        if (! reward.isEmpty()) {
            applyReward(player, reward);
            MessageUtil.sendMessage(player, "&6&l순위 보상 (Top " + rank + "):");
            MessageUtil. sendMessage(player, reward.getSummary());
        }
    }

    /**
     * 시즌 보상 지급
     */
    public void giveSeasonReward(Player player, PvPTier tier) {
        PvPReward reward = new PvPReward();

        double multiplier = tier.getSeasonRewardMultiplier();

        reward.setExperience((long) (5000 * multiplier));
        reward.addDefaultMoney(5000.0 * multiplier);
        reward.setPvpPoints((int) (500 * multiplier));

        // 티어별 특별 칭호
        String title = getSeasonTitle(tier);
        if (title != null) {
            reward.setTitle(title);
            plugin.getTitleManager().unlockTitle(player, title);
        }

        applyReward(player, reward);

        MessageUtil.sendMessage(player, "&6&l시즌 종료 보상 (" + tier.getFormattedName() + "&6&l):");
        MessageUtil.sendMessage(player, reward. getSummary());
    }

    /**
     * 티어 승급 보상 지급
     */
    public void giveTierPromotionReward(Player player, PvPTier newTier) {
        PvPReward reward = new PvPReward();

        int tierLevel = newTier. getLevel();
        reward.setExperience(1000L * tierLevel);
        reward.addDefaultMoney(1000.0 * tierLevel);
        reward.setPvpPoints(100 * tierLevel);

        applyReward(player, reward);

        MessageUtil.sendMessage(player, "&a&l티어 승급 보상!");
        MessageUtil. sendMessage(player, reward.getSummary());
    }

    /**
     * 스트릭 보상 지급
     */
    public void giveStreakReward(Player player, StreakLevel level) {
        PvPReward reward = new PvPReward();

        reward.setPvpPoints(level.getBonusPoints());
        reward.applyMultiplier(level.getRewardMultiplier());

        applyReward(player, reward);
    }

    /**
     * 퍼스트 블러드 보상 지급
     */
    public void giveFirstBloodReward(Player player) {
        PvPReward reward = new PvPReward();

        reward.setExperience(killExperience);
        reward.addDefaultMoney(killMoney);
        reward.setPvpPoints(5);
        reward.setBonus(true);
        reward.setBonusReason("퍼스트 블러드");

        applyReward(player, reward);

        MessageUtil.sendMessage(player, "&c&l퍼스트 블러드!  &a보너스 보상 지급!");

        // 통계 기록
        plugin. getStatisticsManager().recordFirstBlood(player);
    }

    /**
     * 보상 적용
     */
    private void applyReward(Player player, PvPReward reward) {
        // 경험치 지급 (PlayerDataCore 연동)
        if (reward.getExperience() > 0) {
            giveExperience(player, reward.getExperience());
        }

        // 골드 지급 (EconomyCore 연동)
        for (Map.Entry<String, Double> entry : reward.getMoney().entrySet()) {
            if (entry.getValue() > 0) {
                giveMoney(player, entry.getKey(), entry.getValue());
            }
        }

        // PvP 포인트 지급
        if (reward.getPvpPoints() > 0) {
            plugin.getRankingManager().addPvPPoints(player, reward.getPvpPoints());
        }

        // 아이템 지급
        for (ItemStack item : reward. getItems()) {
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
            for (ItemStack left : leftover.values()) {
                player.getWorld().dropItemNaturally(player. getLocation(), left);
            }
        }

        // 칭호 해금
        if (reward.getTitle() != null) {
            plugin.getTitleManager().unlockTitle(player, reward.getTitle());
        }
    }

    /**
     * 경험치 지급 (PlayerDataCore 연동)
     */
    private void giveExperience(Player player, long amount) {
        try {
            var playerDataPlugin = Bukkit.getPluginManager().getPlugin("PlayerDataCore");
            if (playerDataPlugin != null) {
                // PlayerDataCore API를 통해 경험치 지급
                // 실제 구현은 PlayerDataCore의 API에 따라 달라짐
            }
        } catch (Exception e) {
            plugin.getLogger().warning("PlayerDataCore 경험치 지급 오류: " + e.getMessage());
        }

        // 바닐라 경험치로 폴백
        player.giveExp((int) Math.min(amount, Integer.MAX_VALUE));
    }

    /**
     * 골드 지급 (EconomyCore 연동)
     */
    private void giveMoney(Player player, String currency, double amount) {
        if (! plugin.hasEconomyCore()) {
            return;
        }

        try {
            var economyPlugin = Bukkit. getPluginManager().getPlugin("EconomyCore");
            if (economyPlugin != null) {
                // EconomyCore API를 통해 골드 지급
                // 실제 구현은 EconomyCore의 API에 따라 달라짐
            }
        } catch (Exception e) {
            plugin. getLogger().warning("EconomyCore 골드 지급 오류: " + e.getMessage());
        }
    }

    /**
     * 시즌 칭호 반환
     */
    private String getSeasonTitle(PvPTier tier) {
        int season = plugin.getSeasonManager().getCurrentSeason();

        switch (tier) {
            case MASTER:
                return "season_" + season + "_master";
            case DIAMOND:
                return "season_" + season + "_diamond";
            case PLATINUM: 
                return "season_" + season + "_platinum";
            case GOLD: 
                return "season_" + season + "_gold";
            default:
                return null;
        }
    }

    /**
     * 보상 요약 문자열
     */
    private String formatRewardSummary(PvPReward reward) {
        StringBuilder sb = new StringBuilder();

        if (reward.getExperience() > 0) {
            sb.append("&e+").append(reward.getExperience()).append(" EXP ");
        }

        double money = reward.getDefaultMoney();
        if (money > 0) {
            sb.append("&6+").append(String.format("%.1f", money)).append(" 골드 ");
        }

        if (reward. getPvpPoints() > 0) {
            sb.append("&b+").append(reward.getPvpPoints()).append(" 포인트");
        }

        return sb.toString();
    }

    public void reload() {
        loadConfig();
    }
}