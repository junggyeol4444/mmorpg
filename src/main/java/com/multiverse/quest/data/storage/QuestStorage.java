package com.multiverse.quest.storage;

import com.multiverse. quest.models.*;
import java.util.*;

/**
 * 퀘스트 저장소 인터페이스
 * 퀘스트 데이터를 저장하고 로드하기 위한 기본 인터페이스입니다.
 */
public interface QuestStorage {
    
    // ============ Quest Management ============
    
    /**
     * 퀘스트 저장
     */
    boolean saveQuest(Quest quest);
    
    /**
     * 퀘스트 로드
     */
    Quest loadQuest(String questId);
    
    /**
     * 모든 퀘스트 로드
     */
    List<Quest> loadAllQuests();
    
    /**
     * 퀘스트 삭제
     */
    boolean deleteQuest(String questId);
    
    /**
     * 퀘스트 존재 여부 확인
     */
    boolean questExists(String questId);
    
    /**
     * 퀘스트 개수 반환
     */
    int getQuestCount();
    
    // ============ Quest Chain Management ============
    
    /**
     * 퀘스트 체인 저장
     */
    boolean saveQuestChain(QuestChain chain);
    
    /**
     * 퀘스트 체인 로드
     */
    QuestChain loadQuestChain(String chainId);
    
    /**
     * 모든 퀘스트 체인 로드
     */
    List<QuestChain> loadAllQuestChains();
    
    /**
     * 퀘스트 체인 삭제
     */
    boolean deleteQuestChain(String chainId);
    
    /**
     * 퀘스트 체인 존재 여부 확인
     */
    boolean questChainExists(String chainId);
    
    /**
     * 퀘스트 체인 개수 반환
     */
    int getQuestChainCount();
    
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
     * 플레이어의 모든 퀨스트 진행도 로드
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
    
    // ============ Quest Tracker ============
    
    /**
     * 퀘스트 추적기 저장
     */
    boolean saveQuestTracker(QuestTracker tracker);
    
    /**
     * 퀘스트 추적기 로드
     */
    QuestTracker loadQuestTracker(UUID playerUUID);
    
    /**
     * 퀘스트 추적기 삭제
     */
    boolean deleteQuestTracker(UUID playerUUID);
    
    // ============ Bulk Operations ============
    
    /**
     * 모든 데이터 저장
     */
    boolean saveAll();
    
    /**
     * 모든 데이터 로드
     */
    boolean loadAll();
    
    /**
     * 데이터베이스 정리 (만료된 데이터 삭제)
     */
    int cleanup();
    
    /**
     * 데이터베이스 초기화
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
    
    // ============ Search/Query ============
    
    /**
     * 카테고리별 퀨스트 검색
     */
    List<Quest> searchQuestsByCategory(String category);
    
    /**
     * 타입별 퀨스트 검색
     */
    List<Quest> searchQuestsByType(String type);
    
    /**
     * 이름으로 퀨스트 검색
     */
    List<Quest> searchQuestsByName(String name);
    
    /**
     * 플레이어의 특정 상태의 퀨스트 조회
     */
    List<PlayerQuest> getPlayerQuestsByStatus(UUID playerUUID, String status);
    
    // ============ Statistics ============
    
    /**
     * 저장소 통계 정보 반환
     */
    Map<String, Object> getStatistics();
    
    /**
     * 저장소 상태 정보 반환
     */
    String getStatusInfo();
}