package com.multiverse.pvp.managers;

import com.multiverse.pvp.PvPCore;
import com.multiverse.pvp.data.KillStreak;
import com.multiverse.pvp.enums. StreakLevel;
import com. multiverse.pvp.utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;

import java.util. HashMap;
import java. util.Map;
import java. util.UUID;
import java.util. concurrent.ConcurrentHashMap;

public class KillStreakManager {

    private final PvPCore plugin;
    private final Map<UUID, KillStreak> killStreaks;

    private boolean enabled;
    private boolean rewardsEnabled;
    private boolean multiplierEnabled;
    private boolean announcementsEnabled;
    private boolean broadcastEnabled;

    public KillStreakManager(PvPCore plugin) {
        this.plugin = plugin;
        this.killStreaks = new ConcurrentHashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        this.enabled = plugin.getConfig().getBoolean("kill-streak.enabled", true);
        this.rewardsEnabled = plugin.getConfig().getBoolean("kill-streak.rewards. enabled", true);
        this.multiplierEnabled = plugin.getConfig().getBoolean("kill-streak. rewards.multiplier", true);
        this.announcementsEnabled = plugin.getConfig().getBoolean("kill-streak. announcements.enabled", true);
        this.broadcastEnabled = plugin.getConfig().getBoolean("kill-streak.announcements.broadcast", true);
    }

    public KillStreak getKillStreak(Player player) {
        return getKillStreak(player.getUniqueId());
    }

    public KillStreak getKillStreak(UUID playerId) {
        return killStreaks.computeIfAbsent(playerId, KillStreak::new);
    }

    public void setKillStreak(UUID playerId, KillStreak killStreak) {
        killStreaks.put(playerId, killStreak);
    }

    public void addKill(Player player) {
        addKill(player, null);
    }

    public void addKill(Player player, UUID victimId) {
        if (!enabled) {
            return;
        }

        KillStreak streak = getKillStreak(player);
        StreakLevel newLevel;

        // 아레나/듀얼에서는 시간 제한 없음
        if (plugin.getArenaManager().isInArena(player) || plugin.getDuelManager().isInDuel(player)) {
            newLevel = streak.addKillNoTimeLimit(victimId);
        } else {
            newLevel = streak.addKill(victimId);
        }

        // 새로운 스트릭 레벨 달성
        if (newLevel != null) {
            announceStreak(player, newLevel);

            if (rewardsEnabled) {
                giveStreakReward(player, newLevel);
            }
        }

        // 랭킹 업데이트
        plugin.getRankingManager().getRanking(player).setWinStreak(streak.getCurrentStreak());

        // 통계 업데이트
        plugin. getStatisticsManager().updateKillStreak(player, streak. getCurrentStreak());
    }

    public void resetStreak(Player player) {
        if (!enabled) {
            return;
        }

        KillStreak streak = getKillStreak(player);
        StreakLevel shutdownLevel = streak. resetStreak();

        // 랭킹의 연승 초기화
        plugin.getRankingManager().getRanking(player).setWinStreak(0);

        // 셧다운 레벨 반환 (차단자에게 보상 지급용)
    }

    public StreakLevel resetStreakAndGetLevel(Player player) {
        if (!enabled) {
            return null;
        }

        KillStreak streak = getKillStreak(player);
        StreakLevel shutdownLevel = streak. resetStreak();

        plugin.getRankingManager().getRanking(player).setWinStreak(0);

        return shutdownLevel;
    }

    public int getCurrentStreak(Player player) {
        return getKillStreak(player).getCurrentStreak();
    }

    public int getBestStreak(Player player) {
        return getKillStreak(player).getBestStreak();
    }

    public StreakLevel getStreakLevel(int kills) {
        return StreakLevel.getLevel(kills);
    }

    public void announceStreak(Player player, StreakLevel level) {
        if (!announcementsEnabled) {
            return;
        }

        String announcement = level.getAnnouncement();
        String playerName = player.getName();

        // 플레이어에게 메시지
        MessageUtil.sendMessage(player, announcement);

        // 효과음
        player.playSound(player.getLocation(), level.getSound(), 1f, 1f);

        // 파티클
        player.getWorld().spawnParticle(level.getParticle(), player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);

        // 브로드캐스트 (높은 스트릭만)
        if (broadcastEnabled && level.shouldBroadcast()) {
            String broadcastMessage = "&6[PvP] &f" + playerName + "님이 " + announcement + " &f달성! ";

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (! onlinePlayer.equals(player)) {
                    MessageUtil.sendMessage(onlinePlayer, broadcastMessage);
                }
            }
        }

        // 타이틀 표시
        String title = level.getAnnouncement();
        String subtitle = "&7" + level.getKillsRequired() + " 연속 킬! ";

        player.sendTitle(
                MessageUtil.colorize(title),
                MessageUtil. colorize(subtitle),
                10, 40, 10
        );
    }

    public void giveStreakReward(Player player, StreakLevel level) {
        if (!rewardsEnabled) {
            return;
        }

        // 보너스 포인트
        int bonusPoints = level. getBonusPoints();
        plugin.getRankingManager().addPvPPoints(player, bonusPoints);

        // 경험치 보너스
        long bonusExp = (long) (plugin.getConfig().getLong("rewards.kill. experience", 100) * level.getRewardMultiplier());
        // PlayerDataCore 연동하여 경험치 지급

        // 골드 보너스
        double bonusMoney = plugin.getConfig().getDouble("rewards.kill.money", 50.0) * level.getRewardMultiplier();
        // EconomyCore 연동하여 골드 지급

        MessageUtil.sendMessage(player, "&a스트릭 보상:  +" + bonusPoints + " PvP 포인트!");
    }

    public void giveShutdownReward(Player killer, Player victim) {
        if (! rewardsEnabled) {
            return;
        }

        KillStreak victimStreak = getKillStreak(victim);
        StreakLevel victimLevel = victimStreak.getCurrentLevel();

        if (victimLevel == null) {
            return;
        }

        int shutdownBonus = victimLevel.getShutdownBonus();

        if (shutdownBonus > 0) {
            plugin.getRankingManager().addPvPPoints(killer, shutdownBonus);

            MessageUtil.sendMessage(killer, "&6셧다운 보너스!  &a+" + shutdownBonus + " PvP 포인트");
            MessageUtil.sendMessage(killer, "&7" + victim.getName() + "의 " +
                    victimLevel.getDisplayName() + " 스트릭을 종료했습니다!");

            // 통계 기록
            plugin. getStatisticsManager().recordShutdown(killer);
        }
    }

    public double getCurrentMultiplier(Player player) {
        if (!multiplierEnabled) {
            return 1.0;
        }

        KillStreak streak = getKillStreak(player);
        return streak.getCurrentMultiplier();
    }

    public void resetSession(Player player) {
        KillStreak streak = getKillStreak(player);
        streak.resetSession();
    }

    public void loadPlayerData(UUID playerId, KillStreak killStreak) {
        killStreaks.put(playerId, killStreak);
    }

    public void unloadPlayerData(UUID playerId) {
        // 세션 데이터만 초기화
        KillStreak streak = killStreaks.get(playerId);
        if (streak != null) {
            streak.resetSession();
        }
    }

    public Map<UUID, KillStreak> getAllKillStreaks() {
        return new HashMap<>(killStreaks);
    }

    public void reload() {
        loadConfig();
    }
}