package com.multiverse.quest.models.enums;

/**
 * 퀘스트 타입 열거형
 * 퀘스트의 종류를 정의합니다.
 */
public enum QuestType {
    MAIN("메인 퀘스트", "차원별 메인 스토리 및 순차 진행"),
    SUB("서브 퀘스트", "선택적 퀘스트로 자유 진행"),
    DAILY("일일 퀘스트", "매일 리셋되는 퀘스트"),
    WEEKLY("주간 퀘스트", "매주 리셋되는 퀘스트"),
    REPEATABLE("반복 퀘스트", "무제한 반복 가능 (쿨다운 적용)"),
    CHALLENGE("도전 퀘스트", "고난이도 특별 퀘스트"),
    EVENT("이벤트 퀘스트", "기간 한정 특수 퀘스트");

    private final String displayName;
    private final String description;

    /**
     * QuestType 생성자
     * @param displayName 표시명
     * @param description 설명
     */
    QuestType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
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
     * 문자열로부터 QuestType 찾기
     * @param name 이름
     * @return QuestType (없으면 null)
     */
    public static QuestType fromString(String name) {
        try {
            return QuestType.valueOf(name. toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 모든 QuestType 반환
     * @return QuestType 배열
     */
    public static QuestType[] getAllTypes() {
        return QuestType.values();
    }

    /**
     * 이 타입이 반복 가능한지 확인
     * @return 반복 가능 여부
     */
    public boolean isRepeatable() {
        return this == REPEATABLE || this == DAILY || this == WEEKLY;
    }

    /**
     * 이 타입이 시간 제한이 있는지 확인
     * @return 시간 제한 여부
     */
    public boolean hasTimeLimit() {
        return this == CHALLENGE || this == EVENT;
    }

    /**
     * 이 타입이 메인 퀘스트인지 확인
     * @return 메인 퀘스트 여부
     */
    public boolean isMainQuest() {
        return this == MAIN;
    }

    /**
     * 이 타입이 일일/주간 퀘스트인지 확인
     * @return 일일/주간 퀘스트 여부
     */
    public boolean isDailyOrWeekly() {
        return this == DAILY || this == WEEKLY;
    }
}