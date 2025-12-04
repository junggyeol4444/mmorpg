package com.multiverse.quest.data.storage;

import com.multiverse.quest.models.*;
import java.util.*;

/**
 * 퀘스트 진행도 저장소 인터페이스
 * 플레이어의 퀘스트 진행 상태를 저장하고 로드하기 위한 인터페이스입니다.
 */
public interface ProgressStorage {
    
    // ============ Player Quest Progress ============
    
    /**
     * 플레이어 퀘스트 진행도 저장
     */
    boolean savePlayerQuest(UUID playerUUID, PlayerQuest playerQuest);
    
    /**
     * 플레이어 퀘스트 진행도 로드
     */
    PlayerQuest loadPlayerQuest(UUID playerUUID, String questId);
    
    /**
     * 플레이어의 모든 퀘스트 진행도 로드
     */
    List<PlayerQuest> loadPlayerAllQuests(UUID playerUUID);
    
    /**
     * 플레이어 퀘스트 진행도 삭제
     */
    boolean deletePlayerQuest(UUID playerUUID, String questId);
    
    /**
     * 플레이어의 모든 퀨스트 진행도 삭제
     */
    boolean deletePlayerAllQuests(UUID playerUUID);
    
    // ============ Quest Progress Query ============
    
    /**
     * 플레이어가 퀨스트를 진행 중인지 확인
     */
    boolean isPlayerQuestInProgress(UUID playerUUID, String questId);
    
    /**
     * 플레이어가 퀨스트를 완료했는지 확인
     */
    boolean isPlayerQuestCompleted(UUID playerUUID, String questId);
    
    /**
     * 플레이어의 완료된 퀨스트 개수 반환
     */
    int getPlayerCompletedQuestCount(UUID playerUUID);
    
    /**
     * 플레이어의 진행 중인 퀨스트 개수 반환
     */
    int getPlayerInProgressQuestCount(UUID playerUUID);
    
    /**
     * 플레이어가 특정 상태의 퀨스트를 가지는지 확인
     */
    boolean hasQuestWithStatus(UUID playerUUID, String status);
    
    // ============ Objective Progress ============
    
    /**
     * 목표 진행도 업데이트
     */
    boolean updateObjectiveProgress(UUID playerUUID, String questId, String objectiveId, int amount);
    
    /**
     * 목표 진행도 조회
     */
    int getObjectiveProgress(UUID playerUUID, String questId, String objectiveId);
    
    /**
     * 목표 완료 여부 확인
     */
    boolean isObjectiveCompleted(UUID playerUUID, String questId, String objectiveId);
    
    // ============ Status Management ============
    
    /**
     * 퀨스트 상태 변경
     */
    boolean updateQuestStatus(UUID playerUUID, String questId, String newStatus);
    
    /**
     * 퀨스트 상태 조회
     */
    String getQuestStatus(UUID playerUUID, String questId);
    
    /**
     * 특정 상태의 퀨스트 목록 조회
     */
    List<PlayerQuest> getPlayerQuestsByStatus(UUID playerUUID, String status);
    
    // ============ Timeline ============
    
    /**
     * 퀨스트 수락 시간 기록
     */
    boolean recordQuestAcceptance(UUID playerUUID, String questId, long acceptTime);
    
    /**
     * 퀨스트 완료 시간 기록
     */
    boolean recordQuestCompletion(UUID playerUUID, String questId, long completionTime);
    
    /**
     * 퀨스트 진행 시간 조회
     */
    long getQuestElapsedTime(UUID playerUUID, String questId);
    
    /**
     * 마지막 완료 시간 조회
     */
    long getLastCompletionTime(UUID playerUUID, String questId);
    
    // ============ Statistics ============
    
    /**
     * 플레이어의 퀨스트 통계 조회
     */
    Map<String, Object> getPlayerQuestStatistics(UUID playerUUID);
    
    /**
     * 퀨스트별 완료자 수 조회
     */
    int getQuestCompletionCount(String questId);
    
    /**
     * 전체 플레이어 진행도 통계
     */
    Map<String, Object> getGlobalStatistics();
    
    // ============ Search/Filter ============
    
    /**
     * 카테고리별 진행도 조회
     */
    List<PlayerQuest> getPlayerQuestsByCategory(UUID playerUUID, String category);
    
    /**
     * 타입별 진행도 조회
     */
    List<PlayerQuest> getPlayerQuestsByType(UUID playerUUID, String type);
    
    /**
     * 날짜 범위 내의 완료된 퀨스트 조회
     */
    List<PlayerQuest> getCompletedQuestsBetween(UUID playerUUID, long startTime, long endTime);
    
    // ============ Bulk Operations ============
    
    /**
     * 모든 진행도 저장
     */
    boolean saveAll();
    
    /**
     * 모든 진행도 로드
     */
    boolean loadAll();
    
    /**
     * 만료된 진행도 정리 (시간 제한 초과)
     */
    int cleanupExpiredProgress();
    
    /**
     * 데이터 초기화
     */
    boolean reset();
    
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