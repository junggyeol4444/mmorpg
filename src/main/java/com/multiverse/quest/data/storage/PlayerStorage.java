package com.multiverse.quest.data.storage;

import java.util.*;

/**
 * 플레이어 데이터 저장소 인터페이스
 * 플레이어의 퀘스트 관련 정보를 저장하고 로드하기 위한 인터페이스입니다.
 */
public interface PlayerStorage {
    
    // ============ Player Data Management ============
    
    /**
     * 플레이어 데이터 존재 여부 확인
     */
    boolean hasPlayerData(UUID playerUUID);
    
    /**
     * 플레이어 데이터 생성
     */
    boolean createPlayerData(UUID playerUUID, String playerName);
    
    /**
     * 플레이어 데이터 로드
     */
    Map<String, Object> loadPlayerData(UUID playerUUID);
    
    /**
     * 플레이어 데이터 저장
     */
    boolean savePlayerData(UUID playerUUID, Map<String, Object> data);
    
    /**
     * 플레이어 데이터 삭제
     */
    boolean deletePlayerData(UUID playerUUID);
    
    // ============ Player Token Management ============
    
    /**
     * 플레이어 토큰 조회
     */
    int getPlayerToken(UUID playerUUID, String tokenType);
    
    /**
     * 플레이어 토큰 설정
     */
    boolean setPlayerToken(UUID playerUUID, String tokenType, int amount);
    
    /**
     * 플레이어 토큰 증가
     */
    boolean addPlayerToken(UUID playerUUID, String tokenType, int amount);
    
    /**
     * 플레이어 토큰 감소
     */
    boolean subtractPlayerToken(UUID playerUUID, String tokenType, int amount);
    
    /**
     * 모든 토큰 초기화
     */
    boolean resetAllTokens(UUID playerUUID);
    
    // ============ Player Level Management ============
    
    /**
     * 플레이어 레벨 조회
     */
    int getPlayerLevel(UUID playerUUID);
    
    /**
     * 플레이어 레벨 설정
     */
    boolean setPlayerLevel(UUID playerUUID, int level);
    
    /**
     * 플레이어 경험치 조회
     */
    int getPlayerExperience(UUID playerUUID);
    
    /**
     * 플레이어 경험치 설정
     */
    boolean setPlayerExperience(UUID playerUUID, int experience);
    
    /**
     * 플레이어 경험치 증가
     */
    boolean addPlayerExperience(UUID playerUUID, int amount);
    
    // ============ Player Quest Points ============
    
    /**
     * 플레이어 퀨스트 포인트 조회
     */
    int getPlayerQuestPoints(UUID playerUUID);
    
    /**
     * 플레이어 퀨스트 포인트 설정
     */
    boolean setPlayerQuestPoints(UUID playerUUID, int points);
    
    /**
     * 플레이어 퀨스트 포인트 증가
     */
    boolean addPlayerQuestPoints(UUID playerUUID, int amount);
    
    /**
     * 플레이어 퀨스트 포인트 감소
     */
    boolean subtractPlayerQuestPoints(UUID playerUUID, int amount);
    
    // ============ Player Reputation ============
    
    /**
     * 플레이어 호감도 조회
     */
    int getPlayerReputation(UUID playerUUID, int npcId);
    
    /**
     * 플레이어 호감도 설정
     */
    boolean setPlayerReputation(UUID playerUUID, int npcId, int reputation);
    
    /**
     * 플레이어 호감도 증가
     */
    boolean addPlayerReputation(UUID playerUUID, int npcId, int amount);
    
    /**
     * 플레이어 호감도 감소
     */
    boolean subtractPlayerReputation(UUID playerUUID, int npcId, int amount);
    
    /**
     * NPC별 모든 호감도 조회
     */
    Map<Integer, Integer> getAllPlayerReputations(UUID playerUUID);
    
    // ============ Player Titles/Achievements ============
    
    /**
     * 플레이어 칭호 추가
     */
    boolean addPlayerTitle(UUID playerUUID, String title);
    
    /**
     * 플레이어 칭호 제거
     */
    boolean removePlayerTitle(UUID playerUUID, String title);
    
    /**
     * 플레이어 칭호 보유 여부 확인
     */
    boolean hasPlayerTitle(UUID playerUUID, String title);
    
    /**
     * 플레이어 모든 칭호 조회
     */
    List<String> getPlayerAllTitles(UUID playerUUID);
    
    /**
     * 플레이어 칭호 개수 반환
     */
    int getPlayerTitleCount(UUID playerUUID);
    
    // ============ Player Settings ============
    
    /**
     * 플레이어 설정값 조회
     */
    Object getPlayerSetting(UUID playerUUID, String key);
    
    /**
     * 플레이어 설정값 저장
     */
    boolean savePlayerSetting(UUID playerUUID, String key, Object value);
    
    /**
     * 플레이어 설정값 삭제
     */
    boolean deletePlayerSetting(UUID playerUUID, String key);
    
    /**
     * 플레이어 모든 설정 조회
     */
    Map<String, Object> getPlayerSettings(UUID playerUUID);
    
    // ============ Player Statistics ============
    
    /**
     * 플레이어 통계 조회
     */
    Map<String, Object> getPlayerStatistics(UUID playerUUID);
    
    /**
     * 플레이어 총 완료 퀨스트 수 반환
     */
    int getTotalCompletedQuests(UUID playerUUID);
    
    /**
     * 플레이어 총 활동 시간 반환
     */
    long getTotalPlayTime(UUID playerUUID);
    
    /**
     * 플레이어 첫 접속 시간 반환
     */
    long getFirstJoinTime(UUID playerUUID);
    
    /**
     * 플레이어 마지막 접속 시간 반환
     */
    long getLastJoinTime(UUID playerUUID);
    
    // ============ Bulk Operations ============
    
    /**
     * 모든 플레이어 데이터 저장
     */
    boolean saveAll();
    
    /**
     * 모든 플레이어 데이터 로드
     */
    boolean loadAll();
    
    /**
     * 비활성 플레이어 데이터 정리
     */
    int cleanupInactivePlayers(long inactiveTimeMillis);
    
    /**
     * 모든 플레이어 데이터 초기화
     */
    boolean resetAll();
    
    // ============ Search/Query ============
    
    /**
     * 모든 플레이어 UUID 조회
     */
    List<UUID> getAllPlayerUUIDs();
    
    /**
     * 특정 레벨 범위의 플레이어 조회
     */
    List<UUID> getPlayersByLevelRange(int minLevel, int maxLevel);
    
    /**
     * 특정 칭호를 가진 플레이어 조회
     */
    List<UUID> getPlayersByTitle(String title);
    
    /**
     * 플레이어 이름으로 UUID 검색
     */
    UUID getPlayerUUIDByName(String playerName);
    
    /**
     * 플레이어 수 반환
     */
    int getTotalPlayerCount();
    
    // ============ Connection Management ============
    
    /**
     * 저장소 연결
     */
    boolean connect();
    
    /**
     * 저장소 연결 해제
     */
    boolean disconnect();
    
    /**
     * 연결 상태 확인
     */
    boolean isConnected();
    
    /**
     * 저장소 상태 확인
     */
    boolean isAvailable();
    
    /**
     * 저장소 상태 정보 반환
     */
    String getStatusInfo();
}