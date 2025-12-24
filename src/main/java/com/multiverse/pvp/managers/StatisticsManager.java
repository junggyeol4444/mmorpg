package com.multiverse.pvp. managers;

import com.multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPStatistics;
import org.bukkit.Material;
import org. bukkit.entity. Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsManager {

    private final PvPCore plugin;
    private final Map<UUID, PvPStatistics> statistics;

    public StatisticsManager(PvPCore plugin) {
        this.plugin = plugin;
        this.statistics = new ConcurrentHashMap<>();
    }

    /**
     * 통계 데이터 조회
     */
    public PvPStatistics getStatistics(Player player) {
        return getStatistics(player. getUniqueId());
    }

    public PvPStatistics getStatistics(UUID playerId) {
        return statistics.computeIfAbsent(playerId, PvPStatistics::new);
    }

    /**
     * 통계 데이터 설정
     */
    public void setStatistics(UUID playerId, PvPStatistics stats) {
        statistics.put(playerId, stats);
    }

    /**
     * 킬 기록
     */
    public void recordKill(Player killer, Player victim, Material weapon) {
        PvPStatistics killerStats = getStatistics(killer);
        PvPStatistics victimStats = getStatistics(victim);

        // 킬러 통계
        killerStats. recordKill(victim. getUniqueId(), weapon);

        // 피해자 통계
        victimStats.recordDeath(killer.getUniqueId());

        // 랭킹 업데이트
        plugin.getRankingManager().recordKill(killer);
        plugin.getRankingManager().recordDeath(victim);

        // 리벤지 체크
        checkRevenge(killer, victim);

        // 칭호 체크
        plugin.getTitleManager().checkTitleUnlock(killer);
    }

    /**
     * 데스 기록 (킬러 없음)
     */
    public void recordDeath(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.recordDeath(null);

        plugin.getRankingManager().recordDeath(player);
    }

    /**
     * 어시스트 기록
     */
    public void recordAssist(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.recordAssist();

        plugin.getRankingManager().recordAssist(player);
    }

    /**
     * 승리 기록
     */
    public void recordWin(Player player, String arenaType) {
        PvPStatistics stats = getStatistics(player);
        stats.recordWin(arenaType);

        plugin.getTitleManager().checkTitleUnlock(player);
    }

    /**
     * 패배 기록
     */
    public void recordLoss(Player player, String arenaType) {
        PvPStatistics stats = getStatistics(player);
        stats.recordLoss(arenaType);
    }

    /**
     * 듀얼 승리 기록
     */
    public void recordDuelWin(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.recordDuelWin();

        plugin.getTitleManager().checkTitleUnlock(player);
    }

    /**
     * 듀얼 패배 기록
     */
    public void recordDuelLoss(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.recordDuelLoss();
    }

    /**
     * 듀얼 항복 기록
     */
    public void recordDuelSurrender(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.recordDuelSurrender();
    }

    /**
     * 데미지 기록
     */
    public void recordDamageDealt(Player player, double damage) {
        PvPStatistics stats = getStatistics(player);
        stats.recordDamageDealt(damage);
    }

    /**
     * 받은 데미지 기록
     */
    public void recordDamageReceived(Player player, double damage) {
        PvPStatistics stats = getStatistics(player);
        stats.recordDamageReceived(damage);
    }

    /**
     * 힐링 기록
     */
    public void recordHealing(Player player, double amount) {
        PvPStatistics stats = getStatistics(player);
        stats.recordHealing(amount);
    }

    /**
     * 킬 스트릭 업데이트
     */
    public void updateKillStreak(Player player, int streak) {
        PvPStatistics stats = getStatistics(player);
        stats.updateKillStreak(streak);

        plugin.getTitleManager().checkTitleUnlock(player);
    }

    /**
     * 경기 내 킬 업데이트
     */
    public void updateMatchKills(Player player, int kills) {
        PvPStatistics stats = getStatistics(player);
        stats.updateMatchKills(kills);
    }

    /**
     * 퍼스트 블러드 기록
     */
    public void recordFirstBlood(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.recordFirstBlood();
    }

    /**
     * 셧다운 기록
     */
    public void recordShutdown(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.recordShutdown();
    }

    /**
     * 리벤지 체크 및 기록
     */
    private void checkRevenge(Player killer, Player victim) {
        PvPStatistics killerStats = getStatistics(killer);
        
        // 마지막으로 나를 죽인 사람을 죽였는지 체크
        Map<UUID, Integer> deathsFrom = killerStats.getDeathsFromPlayer();
        Map<UUID, Integer> killsAgainst = killerStats.getKillsAgainstPlayer();

        int deathsFromVictim = deathsFrom.getOrDefault(victim.getUniqueId(), 0);
        int killsAgainstVictim = killsAgainst.getOrDefault(victim.getUniqueId(), 0);

        // 최근에 당한 적이 있고, 이제 막 역전했다면 리벤지
        if (deathsFromVictim > 0 && killsAgainstVictim == 1) {
            killerStats.recordRevenge();
            plugin.getRewardManager().giveFirstBloodReward(killer); // 리벤지 보너스로 사용
        }
    }

    /**
     * 더블 킬 기록
     */
    public void recordDoubleKill(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.recordDoubleKill();
    }

    /**
     * 트리플 킬 기록
     */
    public void recordTripleKill(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.recordTripleKill();
    }

    /**
     * 멀티 킬 기록
     */
    public void recordMultiKill(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.recordMultiKill();
    }

    /**
     * KDA 계산
     */
    public double calculateKDA(Player player) {
        PvPStatistics stats = getStatistics(player);
        return stats.getKda();
    }

    /**
     * 승률 계산
     */
    public double calculateWinRate(Player player) {
        PvPStatistics stats = getStatistics(player);
        return stats.getWinRate();
    }

    /**
     * 플레이 시간 업데이트
     */
    public void updatePlayTime(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.updatePlayTime();
    }

    /**
     * 통계 초기화
     */
    public void resetStatistics(Player player) {
        PvPStatistics stats = getStatistics(player);
        stats.reset();
    }

    /**
     * 시즌 통계 초기화
     */
    public void resetSeasonStatistics() {
        // 시즌 관련 통계만 초기화 (일일/주간/월간 킬)
        for (PvPStatistics stats : statistics. values()) {
            stats.getDailyKills().clear();
            stats.getWeeklyKills().clear();
            stats.getMonthlyKills().clear();
        }
    }

    /**
     * 모든 통계 초기화
     */
    public void resetAllStatistics() {
        for (PvPStatistics stats : statistics. values()) {
            stats.reset();
        }
    }

    /**
     * 특정 상대와의 전적 조회
     */
    public int[] getRecordAgainst(Player player, UUID opponentId) {
        PvPStatistics stats = getStatistics(player);
        return stats.getRecordAgainst(opponentId);
    }

    /**
     * 가장 많이 사용한 무기 조회
     */
    public Material getMostUsedWeapon(Player player) {
        PvPStatistics stats = getStatistics(player);
        return stats.getMostUsedWeapon();
    }

    /**
     * 오늘 킬 수 조회
     */
    public int getTodayKills(Player player) {
        PvPStatistics stats = getStatistics(player);
        return stats.getTodayKills();
    }

    /**
     * 이번 주 킬 수 조회
     */
    public int getThisWeekKills(Player player) {
        PvPStatistics stats = getStatistics(player);
        return stats. getThisWeekKills();
    }

    /**
     * 이번 달 킬 수 조회
     */
    public int getThisMonthKills(Player player) {
        PvPStatistics stats = getStatistics(player);
        return stats.getThisMonthKills();
    }

    /**
     * 상위 킬러 조회
     */
    public List<PvPStatistics> getTopKillers(int limit) {
        List<PvPStatistics> sorted = new ArrayList<>(statistics.values());
        sorted.sort((a, b) -> Integer.compare(b.getTotalKills(), a.getTotalKills()));
        return sorted. subList(0, Math.min(limit, sorted.size()));
    }

    /**
     * 상위 KDA 조회
     */
    public List<PvPStatistics> getTopKDA(int limit) {
        List<PvPStatistics> sorted = new ArrayList<>(statistics.values());
        sorted.sort((a, b) -> Double.compare(b.getKda(), a.getKda()));
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }

    /**
     * 상위 승률 조회
     */
    public List<PvPStatistics> getTopWinRate(int limit) {
        List<PvPStatistics> sorted = new ArrayList<>(statistics.values());
        // 최소 10게임 이상인 플레이어만
        sorted.removeIf(s -> (s.getTotalWins() + s.getTotalLosses()) < 10);
        sorted.sort((a, b) -> Double.compare(b.getWinRate(), a.getWinRate()));
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }

    /**
     * 플레이어 데이터 로드
     */
    public void loadPlayerData(UUID playerId, PvPStatistics stats) {
        statistics.put(playerId, stats);
    }

    /**
     * 플레이어 데이터 언로드
     */
    public void unloadPlayerData(UUID playerId) {
        // 통계는 유지
    }

    /**
     * 모든 통계 데이터 반환
     */
    public Map<UUID, PvPStatistics> getAllStatistics() {
        return new HashMap<>(statistics);
    }
}