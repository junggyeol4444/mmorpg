package com.multiverse.quest.objectives;

import com.multiverse. quest.models.*;
import org.bukkit.entity.Player;
import java.util.*;

/**
 * 목표 핸들러 인터페이스
 * 모든 목표 타입의 기본 인터페이스입니다.
 */
public interface ObjectiveHandler {
    
    // ============ Initialization ============
    
    /**
     * 목표 초기화
     */
    void initialize(QuestObjective objective);
    
    /**
     * 목표 정리
     */
    void cleanup();
    
    // ============ Progress Tracking ============
    
    /**
     * 목표 진행도 업데이트
     */
    boolean updateProgress(Player player, UUID playerUUID, int amount);
    
    /**
     * 목표 진행도 조회
     */
    int getProgress(UUID playerUUID);
    
    /**
     * 목표 완료 여부 확인
     */
    boolean isCompleted(UUID playerUUID);
    
    /**
     * 목표 진행도 초기화
     */
    void resetProgress(UUID playerUUID);
    
    // ============ Validation ============
    
    /**
     * 플레이어가 목표를 진행할 수 있는지 확인
     */
    boolean canProgress(Player player, UUID playerUUID);
    
    /**
     * 목표 데이터 유효성 확인
     */
    boolean isValid();
    
    // ============ Information ============
    
    /**
     * 목표 타입 반환
     */
    String getObjectiveType();
    
    /**
     * 목표 설명 반환
     */
    String getDescription();
    
    /**
     * 목표 진행도 문자열 반환 (예: "5/10")
     */
    String getProgressString(UUID playerUUID);
    
    /**
     * 목표 상세 정보 반환
     */
    String getDetailedInfo(UUID playerUUID);
    
    // ============ Events ============
    
    /**
     * 목표 시작 이벤트
     */
    void onStart(Player player, UUID playerUUID);
    
    /**
     * 목표 진행 이벤트
     */
    void onProgress(Player player, UUID playerUUID, int amount);
    
    /**
     * 목표 완료 이벤트
     */
    void onComplete(Player player, UUID playerUUID);
    
    /**
     * 목표 실패 이벤트
     */
    void onFail(Player player, UUID playerUUID, String reason);
    
    // ============ Data Management ============
    
    /**
     * 목표 데이터 저장
     */
    Map<String, Object> serialize();
    
    /**
     * 목표 데이터 로드
     */
    void deserialize(Map<String, Object> data);
    
    // ============ Conditions ============
    
    /**
     * 목표 진행 조건 확인
     */
    boolean checkConditions(Player player);
    
    /**
     * 특정 조건 확인
     */
    boolean checkCondition(Player player, String condition);
    
    // ============ Statistics ============
    
    /**
     * 목표 통계 반환
     */
    Map<String, Object> getStatistics();
    
    /**
     * 플레이어 목표 통계 반환
     */
    Map<String, Object> getPlayerStatistics(UUID playerUUID);
}