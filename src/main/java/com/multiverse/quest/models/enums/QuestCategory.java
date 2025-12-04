package com.multiverse.quest.models.enums;

/**
 * 퀘스트 카테고리 열거형
 * 퀘스트의 주제/분류를 정의합니다.
 */
public enum QuestCategory {
    STORY("스토리", "메인 스토리라인 관련 퀘스트", "📖"),
    COMBAT("전투", "몬스터 처치 및 전투 관련 퀘스트", "⚔️"),
    GATHERING("채집", "자원 수집 및 채집 관련 퀘스트", "⛏️"),
    CRAFTING("제작", "아이템 제작 및 조합 관련 퀘스트", "🔨"),
    EXPLORATION("탐험", "지역 탐험 및 발견 관련 퀘스트", "🗺️"),
    DELIVERY("배달", "아이템 전달 및 배송 관련 퀘스트", "📦"),
    ESCORT("호위", "NPC 호위 및 보호 관련 퀘스트", "🛡️"),
    PUZZLE("퍼즐", "미스터리 및 수수께끼 관련 퀘스트", "🧩"),
    SOCIAL("사교", "NPC 상호작용 및 사교 관련 퀘스트", "🤝");

    private final String displayName;
    private final String description;
    private final String emoji;

    /**
     * QuestCategory 생성자
     * @param displayName 표시명
     * @param description 설명
     * @param emoji 이모지
     */
    QuestCategory(String displayName, String description, String emoji) {
        this.displayName = displayName;
        this.description = description;
        this.emoji = emoji;
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
     * 포맷된 이름 반환 (이모지 + 이름)
     * @return 포맷된 이름
     */
    public String getFormattedName() {
        return emoji + " " + displayName;
    }

    /**
     * 문자열로부터 QuestCategory 찾기
     * @param name 이름
     * @return QuestCategory (없으면 null)
     */
    public static QuestCategory fromString(String name) {
        try {
            return QuestCategory.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 모든 카테고리 반환
     * @return QuestCategory 배열
     */
    public static QuestCategory[] getAllCategories() {
        return QuestCategory. values();
    }

    /**
     * 이 카테고리가 전투 관련인지 확인
     * @return 전투 관련 여부
     */
    public boolean isCombat() {
        return this == COMBAT;
    }

    /**
     * 이 카테고리가 채집/제작 관련인지 확인
     * @return 채집/제작 관련 여부
     */
    public boolean isGatheringOrCrafting() {
        return this == GATHERING || this == CRAFTING;
    }

    /**
     * 이 카테고리가 스토리 관련인지 확인
     * @return 스토리 관련 여부
     */
    public boolean isStory() {
        return this == STORY;
    }

    /**
     * 카테고리 난이도 계수 반환 (경험치/보상 계산용)
     * @return 난이도 계수 (0.8 ~ 1.3)
     */
    public double getDifficultyMultiplier() {
        switch (this) {
            case STORY:
                return 1. 0;
            case COMBAT:
                return 1.2;
            case GATHERING:
                return 0.8;
            case CRAFTING:
                return 0.9;
            case EXPLORATION:
                return 1.0;
            case DELIVERY:
                return 0.7;
            case ESCORT:
                return 1.3;
            case PUZZLE:
                return 1.1;
            case SOCIAL:
                return 0.8;
            default:
                return 1.0;
        }
    }

    /**
     * 추천 레벨 반환
     * @return 추천 레벨
     */
    public int getRecommendedLevel() {
        switch (this) {
            case STORY:
                return 1;
            case COMBAT:
                return 5;
            case GATHERING:
                return 1;
            case CRAFTING:
                return 3;
            case EXPLORATION:
                return 1;
            case DELIVERY:
                return 1;
            case ESCORT:
                return 10;
            case PUZZLE:
                return 5;
            case SOCIAL:
                return 1;
            default:
                return 1;
        }
    }
}