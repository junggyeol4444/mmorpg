package com.multiverse.quest.models.enums;

/**
 * 퀘스트 알림 타입 열거형
 * 플레이어에게 표시되는 알림의 종류를 정의합니다.
 */
public enum NotificationType {
    NEW_QUEST("새 퀘스트", "수락 가능한 새 퀘스트 알림", "🆕", true, true, true, true),
    OBJECTIVE_COMPLETE("목표 완료", "퀘스트 목표 달성 알림", "✅", true, true, true, true),
    QUEST_COMPLETE("퀘스트 완료", "퀘스트 완료 가능 알림", "🎉", true, true, true, true),
    TIME_WARNING("시간 경고", "제한 시간 임박 알림", "⏰", true, true, true, false),
    QUEST_FAILED("퀘스트 실패", "퀘스트 실패 알림", "❌", true, true, true, true),
    RESET("리셋", "일일/주간 퀘스트 리셋 알림", "🔄", false, true, true, false),
    QUEST_ACCEPTED("퀘스트 수락", "퀘스트 수락 완료 알림", "📋", true, true, true, false),
    QUEST_ABANDONED("퀘스트 포기", "퀘스트 포기 알림", "🚫", false, true, true, false),
    REWARD_RECEIVED("보상 수령", "보상 수령 완료 알림", "🎁", true, true, true, true),
    CHAIN_COMPLETED("체인 완료", "퀘스트 체인 완료 알림", "⛓️", true, true, true, true);

    private final String displayName;
    private final String description;
    private final String emoji;
    private final boolean showTitle;        // 타이틀 바에 표시
    private final boolean showActionBar;    // 액션바에 표시
    private final boolean showChat;         // 채팅에 표시
    private final boolean playSound;        // 소리 재생

    /**
     * NotificationType 생성자
     * @param displayName 표시명
     * @param description 설명
     * @param emoji 이모지
     * @param showTitle 타이틀 표시 여부
     * @param showActionBar 액션바 표시 여부
     * @param showChat 채팅 표시 여부
     * @param playSound 소리 재생 여부
     */
    NotificationType(String displayName, String description, String emoji,
                     boolean showTitle, boolean showActionBar, boolean showChat, boolean playSound) {
        this.displayName = displayName;
        this.description = description;
        this.emoji = emoji;
        this.showTitle = showTitle;
        this.showActionBar = showActionBar;
        this.showChat = showChat;
        this.playSound = playSound;
    }

    /**
     * 표시명 반환
     * @return 표시명
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 설명 반환
     * @return 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * 이모지 반환
     * @return 이모지
     */
    public String getEmoji() {
        return emoji;
    }

    /**
     * 타이틀 표시 여부 반환
     * @return 타이틀 표시 여부
     */
    public boolean shouldShowTitle() {
        return showTitle;
    }

    /**
     * 액션바 표시 여부 반환
     * @return 액션바 표시 여부
     */
    public boolean shouldShowActionBar() {
        return showActionBar;
    }

    /**
     * 채팅 표시 여부 반환
     * @return 채팅 표시 여부
     */
    public boolean shouldShowChat() {
        return showChat;
    }

    /**
     * 소리 재생 여부 반환
     * @return 소리 재생 여부
     */
    public boolean shouldPlaySound() {
        return playSound;
    }

    /**
     * 포맷된 알림명 반환 (이모지 + 이름)
     * @return 포맷된 알림명
     */
    public String getFormattedName() {
        return emoji + " " + displayName;
    }

    /**
     * 문자열로부터 NotificationType 찾기
     * @param name 이름
     * @return NotificationType (없으면 null)
     */
    public static NotificationType fromString(String name) {
        try {
            return NotificationType. valueOf(name. toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 모든 알림 타입 반환
     * @return NotificationType 배열
     */
    public static NotificationType[] getAllNotificationTypes() {
        return NotificationType.values();
    }

    /**
     * 퀘스트 관련 알림인지 확인
     * @return 퀘스트 관련 여부
     */
    public boolean isQuestRelated() {
        return this == NEW_QUEST || this == QUEST_COMPLETE || 
               this == QUEST_FAILED || this == QUEST_ACCEPTED || 
               this == QUEST_ABANDONED || this == CHAIN_COMPLETED;
    }

    /**
     * 목표 관련 알림인지 확인
     * @return 목표 관련 여부
     */
    public boolean isObjectiveRelated() {
        return this == OBJECTIVE_COMPLETE;
    }

    /**
     * 시스템 알림인지 확인
     * @return 시스템 알림 여부
     */
    public boolean isSystemNotification() {
        return this == RESET || this == TIME_WARNING;
    }

    /**
     * 긴급 알림인지 확인 (빨간색 표시)
     * @return 긴급 알림 여부
     */
    public boolean isUrgent() {
        return this == QUEST_FAILED || this == TIME_WARNING;
    }

    /**
     * 긍정적 알림인지 확인 (초록색 표시)
     * @return 긍정적 알림 여부
     */
    public boolean isPositive() {
        return this == OBJECTIVE_COMPLETE || this == QUEST_COMPLETE || 
               this == REWARD_RECEIVED || this == CHAIN_COMPLETED;
    }

    /**
     * 중립적 알림인지 확인 (노란색/회색 표시)
     * @return 중립적 알림 여부
     */
    public boolean isNeutral() {
        return this == NEW_QUEST || this == QUEST_ACCEPTED || 
               this == QUEST_ABANDONED || this == RESET;
    }

    /**
     * UI에 표시할 색상 코드 반환 (ChatColor 호환)
     * @return 색상 코드
     */
    public String getColorCode() {
        if (isUrgent()) {
            return "§c"; // 빨간색
        } else if (isPositive()) {
            return "§a"; // 초록색
        } else if (isNeutral()) {
            return "§e"; // 노랑색
        } else {
            return "§f"; // 기본 흰색
        }
    }

    /**
     * 기본 알림 음성 타입 반환
     * @return 음성 타입 이름
     */
    public String getDefaultSoundType() {
        switch (this) {
            case NEW_QUEST:
                return "ENTITY_PLAYER_LEVELUP";
            case OBJECTIVE_COMPLETE:
                return "ENTITY_EXPERIENCE_ORB_PICKUP";
            case QUEST_COMPLETE:
                return "UI_TOAST_CHALLENGE_COMPLETE";
            case TIME_WARNING:
                return "BLOCK_NOTE_BLOCK_BELL";
            case QUEST_FAILED:
                return "ENTITY_VILLAGER_NO";
            case RESET:
                return "ENTITY_PLAYER_LEVELUP";
            case QUEST_ACCEPTED:
                return "ITEM_PICKUP";
            case QUEST_ABANDONED:
                return "BLOCK_DISPENSER_DISPENSE";
            case REWARD_RECEIVED:
                return "ENTITY_PLAYER_LEVELUP";
            case CHAIN_COMPLETED:
                return "UI_TOAST_CHALLENGE_COMPLETE";
            default:
                return "BLOCK_NOTE_BLOCK_PLING";
        }
    }

    /**
     * 기본 알림 지속 시간 반환 (틱 단위)
     * @return 지속 시간 (틱)
     */
    public int getDefaultDuration() {
        switch (this) {
            case TIME_WARNING:
                return 40; // 2초
            case OBJECTIVE_COMPLETE:
                return 60; // 3초
            case QUEST_COMPLETE:
                return 100; // 5초
            case REWARD_RECEIVED:
                return 100; // 5초
            case CHAIN_COMPLETED:
                return 120; // 6초
            default:
                return 80; // 4초
        }
    }

    /**
     * 기본 타이틀/서브타이틀 페이드 시간 반환 (틱 단위)
     * @return [fadeIn, stay, fadeOut]
     */
    public int[] getDefaultFadeTimes() {
        return new int[]{10, getDefaultDuration(), 10};
    }

    /**
     * 우선순위 반환 (높을수록 먼저 표시)
     * @return 우선순위 (1~10)
     */
    public int getPriority() {
        switch (this) {
            case QUEST_FAILED:
                return 10;
            case TIME_WARNING:
                return 9;
            case CHAIN_COMPLETED:
                return 8;
            case REWARD_RECEIVED:
                return 7;
            case QUEST_COMPLETE:
                return 6;
            case OBJECTIVE_COMPLETE:
                return 5;
            case NEW_QUEST:
                return 4;
            case QUEST_ACCEPTED:
                return 3;
            case RESET:
                return 2;
            case QUEST_ABANDONED:
                return 1;
            default:
                return 5;
        }
    }
}