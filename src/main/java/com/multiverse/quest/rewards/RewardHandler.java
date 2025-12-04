package com.multiverse.quest.rewards;

import com.multiverse.  quest.models.*;
import org.bukkit.entity.Player;
import java.util.*;

/**
 * 보상 핸들러 인터페이스
 * 모든 보상 타입의 기본 인터페이스입니다.
 */
public interface RewardHandler {
    
    // ============ Reward Distribution ============
    
    /**
     * 플레이어에게 보상 지급
     */
    boolean giveReward(Player player, UUID playerUUID);
    
    /**
     * 플레이어에게 보상 지급 (커스텀 양)
     */
    boolean giveReward(Player player, UUID playerUUID, int amount);
    
    /**
     * 보상 미리보기 (실제 지급 X)
     */
    boolean previewReward(Player player, UUID playerUUID);
    
    // ============ Validation ============
    
    /**
     * 플레이어가 보상을 받을 수 있는지 확인
     */
    boolean canGiveReward(Player player, UUID playerUUID);
    
    /**
     * 보상이 유효한지 확인
     */
    boolean isValid();
    
    /**
     * 보상 데이터 유효성 확인
     */
    boolean validateRewardData();
    
    // ============ Information ============
    
    /**
     * 보상 타입 반환
     */
    String getRewardType();
    
    /**
     * 보상 설명 반환
     */
    String getDescription();
    
    /**
     * 보상 상세 정보 반환
     */
    String getDetailedInfo();
    
    /**
     * 보상 가치 반환 (게임 밸런스 계산용)
     */
    double getRewardValue();
    
    // ============ Configuration ============
    
    /**
     * 보상 설정 초기화
     */
    void initialize(QuestReward reward);
    
    /**
     * 보상 설정 정리
     */
    void cleanup();
    
    // ============ Events ============
    
    /**
     * 보상 지급 전 이벤트
     */
    void onBeforeGive(Player player, UUID playerUUID);
    
    /**
     * 보상 지급 후 이벤트
     */
    void onAfterGive(Player player, UUID playerUUID);
    
    /**
     * 보상 지급 실패 이벤트
     */
    void onGiveFailed(Player player, UUID playerUUID, String reason);
    
    // ============ Data Management ============
    
    /**
     * 보상 데이터 저장
     */
    Map<String, Object> serialize();
    
    /**
     * 보상 데이터 로드
     */
    void deserialize(Map<String, Object> data);
    
    // ============ Conditions ============
    
    /**
     * 플레이어가 보상을 받기 위한 조건 확인
     */
    boolean checkConditions(Player player);
    
    /**
     * 특정 조건 확인
     */
    boolean checkCondition(Player player, String condition);
    
    // ============ Statistics ============
    
    /**
     * 보상 통계 반환
     */
    Map<String, Object> getStatistics();
    
    /**
     * 플레이어 보상 통계 반환
     */
    Map<String, Object> getPlayerStatistics(UUID playerUUID);
    
    /**
     * 총 지급된 보상 반환
     */
    int getTotalRewardsGiven();
    
    /**
     * 보상 지급 이력 반환
     */
    List<Map<String, Object>> getRewardHistory(UUID playerUUID);
}