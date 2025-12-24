package com.multiverse. pvp.api;

import com.multiverse.pvp.PvPCore;
import com.multiverse.pvp.data.*;
import com.multiverse.pvp.enums.*;
import org.bukkit.Location;
import org. bukkit.entity. Player;
import org.bukkit.inventory.ItemStack;

import java.util. List;
import java.util. Map;
import java.util. UUID;

public class PvPAPI {

    private final PvPCore plugin;

    public PvPAPI(PvPCore plugin) {
        this. plugin = plugin;
    }

    // ==================== PvP 모드 API ====================

    /**
     * 플레이어의 PvP 모드 활성화/비활성화
     */
    public void setPvPEnabled(Player player, boolean enabled) {
        plugin. getPvPModeManager().setPvPEnabled(player, enabled);
    }

    /**
     * 플레이어의 PvP 모드 상태 확인
     */
    public boolean isPvPEnabled(Player player) {
        return plugin.getPvPModeManager().isPvPEnabled(player);
    }

    /**
     * 플레이어 간 공격 가능 여부 확인
     */
    public boolean canAttack(Player attacker, Player target) {
        return plugin.getPvPModeManager().canAttack(attacker, target);
    }

    /**
     * 공격 불가 사유 조회
     */
    public String getBlockReason(Player attacker, Player target) {
        return plugin.getPvPModeManager().getBlockReason(attacker, target);
    }

    /**
     * 플레이어 보호 설정
     */
    public void setProtection(Player player, int durationSeconds) {
        plugin.getPvPModeManager().setProtection(player, durationSeconds);
    }

    /**
     * 플레이어 보호 상태 확인
     */
    public boolean isProtected(Player player) {
        return plugin.getPvPModeManager().isProtected(player);
    }

    /**
     * PvP 모드 데이터 조회
     */
    public PvPMode getPvPMode(Player player) {
        return plugin.getPvPModeManager().getPvPMode(player);
    }

    // ==================== 아레나 API ====================

    /**
     * 아레나 생성
     */
    public void createArena(String name, ArenaType type, Location lobby) {
        plugin.getArenaManager().createArena(name, type, lobby);
    }

    /**
     * 아레나 삭제
     */
    public void deleteArena(UUID arenaId) {
        plugin.getArenaManager().deleteArena(arenaId);
    }

    /**
     * 아레나 조회
     */
    public PvPArena getArena(UUID arenaId) {
        return plugin.getArenaManager().getArena(arenaId);
    }

    /**
     * 이름으로 아레나 조회
     */
    public PvPArena getArenaByName(String name) {
        return plugin. getArenaManager().getArenaByName(name);
    }

    /**
     * 모든 아레나 조회
     */
    public List<PvPArena> getAllArenas() {
        return plugin.getArenaManager().getAllArenas();
    }

    /**
     * 타입별 아레나 조회
     */
    public List<PvPArena> getArenasByType(ArenaType type) {
        return plugin. getArenaManager().getArenasByType(type);
    }

    /**
     * 아레나 참가
     */
    public boolean joinArena(Player player, UUID arenaId) {
        return plugin. getArenaManager().joinArena(player, arenaId);
    }

    /**
     * 아레나 퇴장
     */
    public void leaveArena(Player player) {
        plugin. getArenaManager().leaveArena(player);
    }

    /**
     * 아레나 관전
     */
    public void spectateArena(Player player, UUID arenaId) {
        plugin.getArenaManager().spectateArena(player, arenaId);
    }

    /**
     * 플레이어가 아레나에 있는지 확인
     */
    public boolean isInArena(Player player) {
        return plugin.getArenaManager().isInArena(player);
    }

    /**
     * 플레이어가 참가한 아레나 조회
     */
    public PvPArena getPlayerArena(Player player) {
        return plugin.getArenaManager().getPlayerArena(player);
    }

    /**
     * 매칭 큐 등록
     */
    public void queueForArena(Player player, ArenaType type) {
        plugin.getArenaManager().queueForArena(player, type);
    }

    /**
     * 매칭 큐 취소
     */
    public void cancelQueue(Player player) {
        plugin. getArenaManager().cancelQueue(player);
    }

    // ==================== 듀얼 API ====================

    /**
     * 듀얼 신청
     */
    public void sendDuelRequest(Player challenger, Player opponent, Map<String, Double> betMoney, List<ItemStack> betItems) {
        plugin.getDuelManager().sendDuelRequest(challenger, opponent, betMoney, betItems);
    }

    /**
     * 듀얼 수락
     */
    public void acceptDuel(Player player, UUID duelId) {
        plugin.getDuelManager().acceptDuel(player, duelId);
    }

    /**
     * 듀얼 거절
     */
    public void declineDuel(Player player, UUID duelId) {
        plugin. getDuelManager().declineDuel(player, duelId);
    }

    /**
     * 듀얼 항복
     */
    public void surrender(Player player) {
        plugin. getDuelManager().surrender(player);
    }

    /**
     * 진행 중인 듀얼 조회
     */
    public Duel getActiveDuel(Player player) {
        return plugin.getDuelManager().getActiveDuel(player);
    }

    /**
     * 듀얼 중인지 확인
     */
    public boolean isInDuel(Player player) {
        return plugin.getDuelManager().isInDuel(player);
    }

    /**
     * 대기 중인 듀얼 요청 조회
     */
    public Duel getPendingDuelRequest(Player player) {
        return plugin.getDuelManager().getPendingDuelRequest(player);
    }

    // ==================== 랭킹 API ====================

    /**
     * 플레이어 랭킹 조회
     */
    public PvPRanking getRanking(Player player) {
        return plugin.getRankingManager().getRanking(player);
    }

    /**
     * UUID로 랭킹 조회
     */
    public PvPRanking getRanking(UUID playerId) {
        return plugin.getRankingManager().getRanking(playerId);
    }

    /**
     * 상위 랭킹 조회
     */
    public List<PvPRanking> getTopRankings(int limit) {
        return plugin.getRankingManager().getTopRankings(limit);
    }

    /**
     * 플레이어 순위 조회
     */
    public int getPlayerRank(Player player) {
        return plugin.getRankingManager().getPlayerRank(player);
    }

    /**
     * 플레이어 티어 조회
     */
    public PvPTier getTier(Player player) {
        return plugin.getRankingManager().getTier(player);
    }

    /**
     * 레이팅 조회
     */
    public int getRating(Player player) {
        PvPRanking ranking = getRanking(player);
        return ranking != null ? ranking.getRating() : plugin.getConfig().getInt("ranking. elo.starting-rating", 1000);
    }

    // ==================== 킬 스트릭 API ====================

    /**
     * 현재 킬 스트릭 조회
     */
    public int getCurrentStreak(Player player) {
        return plugin. getKillStreakManager().getCurrentStreak(player);
    }

    /**
     * 최고 킬 스트릭 조회
     */
    public int getBestStreak(Player player) {
        return plugin.getKillStreakManager().getBestStreak(player);
    }

    /**
     * 킬 스트릭 데이터 조회
     */
    public KillStreak getKillStreak(Player player) {
        return plugin.getKillStreakManager().getKillStreak(player);
    }

    // ==================== 타이틀 API ====================

    /**
     * 활성 타이틀 조회
     */
    public String getActiveTitle(Player player) {
        return plugin.getTitleManager().getActiveTitle(player);
    }

    /**
     * 활성 타이틀 설정
     */
    public void setActiveTitle(Player player, String titleId) {
        plugin.getTitleManager().setActiveTitle(player, titleId);
    }

    /**
     * 해금된 타이틀 목록 조회
     */
    public List<PvPTitle> getUnlockedTitles(Player player) {
        return plugin.getTitleManager().getUnlockedTitles(player);
    }

    /**
     * 타이틀 보유 여부 확인
     */
    public boolean hasTitle(Player player, String titleId) {
        return plugin.getTitleManager().hasTitle(player, titleId);
    }

    /**
     * 타이틀 해금
     */
    public void unlockTitle(Player player, String titleId) {
        plugin.getTitleManager().unlockTitle(player, titleId);
    }

    // ==================== 지역 API ====================

    /**
     * 위치의 PvP 지역 조회
     */
    public PvPZone getZone(Location location) {
        return plugin.getZoneManager().getZone(location);
    }

    /**
     * 특정 타입 지역 내 여부 확인
     */
    public boolean isInZone(Location location, ZoneType type) {
        return plugin. getZoneManager().isInZone(location, type);
    }

    /**
     * 안전 지역 여부 확인
     */
    public boolean isInSafeZone(Location location) {
        return plugin.getZoneManager().isInZone(location, ZoneType.SAFE);
    }

    /**
     * 전투 지역 여부 확인
     */
    public boolean isInCombatZone(Location location) {
        return plugin.getZoneManager().isInZone(location, ZoneType. COMBAT);
    }

    /**
     * 혼돈 지역 여부 확인
     */
    public boolean isInChaosZone(Location location) {
        return plugin.getZoneManager().isInZone(location, ZoneType. CHAOS);
    }

    /**
     * 지역 생성
     */
    public void createZone(String name, ZoneType type, Location corner1, Location corner2) {
        plugin. getZoneManager().createZone(name, type, corner1, corner2);
    }

    /**
     * 지역 삭제
     */
    public void deleteZone(UUID zoneId) {
        plugin.getZoneManager().deleteZone(zoneId);
    }

    // ==================== 통계 API ====================

    /**
     * 플레이어 통계 조회
     */
    public PvPStatistics getStatistics(Player player) {
        return plugin.getStatisticsManager().getStatistics(player);
    }

    /**
     * UUID로 통계 조회
     */
    public PvPStatistics getStatistics(UUID playerId) {
        return plugin.getStatisticsManager().getStatistics(playerId);
    }

    /**
     * KDA 계산
     */
    public double calculateKDA(Player player) {
        return plugin.getStatisticsManager().calculateKDA(player);
    }

    /**
     * 총 킬 수 조회
     */
    public int getTotalKills(Player player) {
        PvPStatistics stats = getStatistics(player);
        return stats != null ? stats.getTotalKills() : 0;
    }

    /**
     * 총 데스 수 조회
     */
    public int getTotalDeaths(Player player) {
        PvPStatistics stats = getStatistics(player);
        return stats != null ? stats.getTotalDeaths() : 0;
    }

    // ==================== 리더보드 API ====================

    /**
     * 일일 리더보드 조회
     */
    public List<PvPRanking> getDailyTop(LeaderboardType type, int limit) {
        return plugin.getLeaderboardManager().getDailyTop(type, limit);
    }

    /**
     * 주간 리더보드 조회
     */
    public List<PvPRanking> getWeeklyTop(LeaderboardType type, int limit) {
        return plugin. getLeaderboardManager().getWeeklyTop(type, limit);
    }

    /**
     * 시즌 리더보드 조회
     */
    public List<PvPRanking> getSeasonTop(int limit) {
        return plugin.getLeaderboardManager().getSeasonTop(limit);
    }

    // ==================== 시즌 API ====================

    /**
     * 현재 시즌 번호 조회
     */
    public int getCurrentSeason() {
        return plugin.getSeasonManager().getCurrentSeason();
    }

    /**
     * 시즌 남은 시간 조회 (초)
     */
    public long getSeasonRemainingTime() {
        return plugin.getSeasonManager().getRemainingTime();
    }

    // ==================== 보상 API ====================

    /**
     * 킬 보상 지급
     */
    public void giveKillReward(Player killer, Player victim) {
        plugin. getRewardManager().giveKillReward(killer, victim);
    }

    /**
     * 승리 보상 지급
     */
    public void giveWinReward(Player winner, ArenaType arenaType) {
        plugin.getRewardManager().giveWinReward(winner, arenaType);
    }

    // ==================== 유틸리티 ====================

    /**
     * 플러그인 인스턴스 조회
     */
    public PvPCore getPlugin() {
        return plugin;
    }

    /**
     * EconomyCore 연동 여부
     */
    public boolean hasEconomyCore() {
        return plugin.hasEconomyCore();
    }

    /**
     * GuildCore 연동 여부
     */
    public boolean hasGuildCore() {
        return plugin.hasGuildCore();
    }
}