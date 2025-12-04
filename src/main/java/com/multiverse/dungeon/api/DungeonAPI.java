package com.multiverse.dungeon. api;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.data.enums.DungeonDifficulty;
import com. multiverse.dungeon.data. model.*;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * DungeonCore 플러그인의 공개 API
 * 다른 플러그인에서 던전 시스템 제어 가능
 */
public class DungeonAPI {

    private static DungeonCore plugin;

    /**
     * API 초기화 (플러그인 시작 시에만 호출)
     */
    public static void init(DungeonCore instance) {
        plugin = instance;
    }

    // ===== 던전 관련 API =====

    /**
     * 던전 ID로 던전 정보 조회
     *
     * @param dungeonId 던전 ID
     * @return 던전 객체, 없으면 null
     */
    public static Dungeon getDungeon(String dungeonId) {
        return plugin.getDungeonManager().getDungeon(dungeonId);
    }

    /**
     * 모든 던전 조회
     *
     * @return 던전 목록
     */
    public static List<Dungeon> getAllDungeons() {
        return plugin. getDungeonManager().getAllDungeons();
    }

    /**
     * 새로운 인스턴스 생성
     *
     * @param dungeonId 던전 ID
     * @param partyId 파티 ID
     * @param difficulty 난이도
     * @return 생성된 인스턴스
     */
    public static DungeonInstance createInstance(String dungeonId, UUID partyId, DungeonDifficulty difficulty) {
        return plugin.getInstanceManager().createInstance(dungeonId, partyId, difficulty);
    }

    /**
     * 플레이어의 현재 인스턴스 조회
     *
     * @param player 플레이어
     * @return 인스턴스, 없으면 null
     */
    public static DungeonInstance getPlayerInstance(Player player) {
        return plugin.getInstanceManager().getPlayerInstance(player);
    }

    /**
     * 인스턴스 완료 처리
     *
     * @param instanceId 인스턴스 ID
     */
    public static void completeInstance(UUID instanceId) {
        plugin. getInstanceManager().completeInstance(instanceId);
    }

    /**
     * 인스턴스 실패 처리
     *
     * @param instanceId 인스턴스 ID
     * @param reason 실패 사유
     */
    public static void failInstance(UUID instanceId, String reason) {
        plugin. getInstanceManager().failInstance(instanceId, reason);
    }

    // ===== 파티 관련 API =====

    /**
     * 플레이어의 파티 조회
     *
     * @param player 플레이어
     * @return 파티 객체, 없으면 null
     */
    public static Party getPlayerParty(Player player) {
        return plugin.getPartyManager().getPlayerParty(player);
    }

    /**
     * 파티 ID로 파티 조회
     *
     * @param partyId 파티 ID
     * @return 파티 객체, 없으면 null
     */
    public static Party getParty(UUID partyId) {
        return plugin.getPartyManager().getParty(partyId);
    }

    /**
     * 새로운 파티 생성
     *
     * @param leader 파티 리더
     * @return 생성된 파티
     */
    public static Party createParty(Player leader) {
        return plugin.getPartyManager().createParty(leader);
    }

    /**
     * 플레이어가 파티에 속해 있는지 확인
     *
     * @param player 플레이어
     * @return 파티 속하면 true, 아니면 false
     */
    public static boolean isInParty(Player player) {
        return plugin.getPartyManager().isInParty(player);
    }

    /**
     * 플레이어가 파티 리더인지 확인
     *
     * @param player 플레이어
     * @return 리더이면 true, 아니면 false
     */
    public static boolean isLeader(Player player) {
        return plugin.getPartyManager().isLeader(player);
    }

    /**
     * 파티 해체
     *
     * @param partyId 파티 ID
     */
    public static void disbandParty(UUID partyId) {
        plugin.getPartyManager().disbandParty(partyId);
    }

    /**
     * 플레이어를 파티에서 추방
     *
     * @param partyId 파티 ID
     * @param playerId 플레이어 ID
     */
    public static void kickPlayer(UUID partyId, UUID playerId) {
        plugin. getPartyManager().kickPlayer(partyId, playerId);
    }

    // ===== 보상 관련 API =====

    /**
     * 플레이어에게 보상 지급
     *
     * @param player 플레이어
     * @param reward 보상
     */
    public static void giveReward(Player player, DungeonReward reward) {
        plugin.getRewardManager(). giveReward(player, reward);
    }

    /**
     * 던전 포인트 추가
     *
     * @param player 플레이어
     * @param points 포인트
     */
    public static void addDungeonPoints(Player player, int points) {
        plugin.getRewardManager().addDungeonPoints(player, points);
    }

    /**
     * 플레이어의 던전 포인트 조회
     *
     * @param player 플레이어
     * @return 던전 포인트
     */
    public static int getDungeonPoints(Player player) {
        return plugin.getRewardManager().getDungeonPoints(player);
    }

    // ===== 리더보드 관련 API =====

    /**
     * 특정 던전의 최고 기록 조회
     *
     * @param dungeonId 던전 ID
     * @param difficulty 난이도
     * @param limit 조회 개수
     * @return 기록 목록
     */
    public static List<DungeonRecord> getTopRecords(String dungeonId, DungeonDifficulty difficulty, int limit) {
        return plugin.getLeaderboardManager().getTopRecords(dungeonId, difficulty, limit);
    }

    /**
     * 플레이어의 순위 조회
     *
     * @param player 플레이어
     * @param dungeonId 던전 ID
     * @param difficulty 난이도
     * @return 순위
     */
    public static int getPlayerRank(Player player, String dungeonId, DungeonDifficulty difficulty) {
        return plugin.getLeaderboardManager().getPlayerRank(player, dungeonId, difficulty);
    }

    /**
     * 플레이어의 총 클리어 횟수
     *
     * @param player 플레이어
     * @param dungeonId 던전 ID
     * @return 클리어 횟수
     */
    public static int getTotalClears(Player player, String dungeonId) {
        return plugin.getLeaderboardManager().getTotalClears(player, dungeonId);
    }

    /**
     * 플레이어의 최고 기록 조회
     *
     * @param player 플레이어
     * @param dungeonId 던전 ID
     * @param difficulty 난이도
     * @return 기록 (밀리초)
     */
    public static long getBestTime(Player player, String dungeonId, DungeonDifficulty difficulty) {
        return plugin. getLeaderboardManager().getBestTime(player, dungeonId, difficulty);
    }

    // ===== 난이도 스케일링 API =====

    /**
     * 난이도에 따른 몬스터 체력 배율 조회
     *
     * @param difficulty 난이도
     * @param partySize 파티 크기
     * @return 체력 배율
     */
    public static double getMobHealthMultiplier(DungeonDifficulty difficulty, int partySize) {
        return plugin.getScalingManager().getMobHealthMultiplier(difficulty, partySize);
    }

    /**
     * 난이도에 따른 몬스터 데미지 배율 조회
     *
     * @param difficulty 난이도
     * @param partySize 파티 크기
     * @return 데미지 배율
     */
    public static double getMobDamageMultiplier(DungeonDifficulty difficulty, int partySize) {
        return plugin.getScalingManager().getMobDamageMultiplier(difficulty, partySize);
    }

    /**
     * 난이도에 따른 보상 배율 조회
     *
     * @param difficulty 난이도
     * @return 보상 배율
     */
    public static double getRewardMultiplier(DungeonDifficulty difficulty) {
        return plugin.getScalingManager().getRewardMultiplier(difficulty);
    }
}