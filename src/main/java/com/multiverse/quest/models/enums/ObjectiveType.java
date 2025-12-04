package com.multiverse.quest.models.enums;

/**
 * 퀘스트 목표 타입 열거형
 * 퀘스트 목표의 종류를 정의합니다.
 */
public enum ObjectiveType {
    KILL_MOBS("몬스터 처치", "특정 몬스터를 처치합니다", true, false),
    COLLECT_ITEMS("아이템 수집", "특정 아이템을 수집합니다", true, false),
    CRAFT_ITEMS("아이템 제작", "특정 아이템을 제작합니다", true, false),
    BREAK_BLOCKS("블럭 채집", "특정 블럭을 채집합니다", true, false),
    PLACE_BLOCKS("블럭 설치", "특정 블럭을 설치합니다", true, false),
    TALK_TO_NPC("NPC 대화", "특정 NPC와 대화합니다", false, true),
    EXPLORE_REGION("지역 탐험", "특정 지역을 방문합니다", false, false),
    ESCORT("플레이어 호위", "NPC를 목적지까지 호위합니다", false, true),
    DELIVER("아이템 배달", "NPC에게 아이템을 전달합니다", false, true),
    REACH_LEVEL("레벨 달성", "특정 레벨에 도달합니다", false, false),
    USE_SKILL("스킬 사용", "특정 스킬을 사용합니다", true, false),
    EARN_MONEY("돈 획득", "특정 금액을 획득합니다", false, false),
    TRADE("거래", "특정 횟수 거래합니다", true, false),
    FISHING("낚시", "특정 수의 물고기를 낚습니다", true, false),
    CUSTOM("커스텀", "플러그인에서 정의한 커스텀 목표", true, false);

    private final String displayName;
    private final String description;
    private final boolean countable;      // 개수 세기 가능
    private final boolean interactable;   // NPC 상호작용 필요

    /**
     * ObjectiveType 생성자
     * @param displayName 표시명
     * @param description 설명
     * @param countable 개수 세기 가능 여부
     * @param interactable NPC 상호작용 필요 여부
     */
    ObjectiveType(String displayName, String description, boolean countable, boolean interactable) {
        this.displayName = displayName;
        this.description = description;
        this.countable = countable;
        this.interactable = interactable;
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
     * 개수 세기 가능 여부 반환
     * @return 개수 세기 가능 여부
     */
    public boolean isCountable() {
        return countable;
    }

    /**
     * NPC 상호작용 필요 여부 반환
     * @return NPC 상호작용 필요 여부
     */
    public boolean isInteractable() {
        return interactable;
    }

    /**
     * 문자열로부터 ObjectiveType 찾기
     * @param name 이름
     * @return ObjectiveType (없으면 null)
     */
    public static ObjectiveType fromString(String name) {
        try {
            return ObjectiveType.valueOf(name. toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 모든 목표 타입 반환
     * @return ObjectiveType 배열
     */
    public static ObjectiveType[] getAllObjectiveTypes() {
        return ObjectiveType.values();
    }

    /**
     * 이 목표가 전투 관련인지 확인
     * @return 전투 관련 여부
     */
    public boolean isCombat() {
        return this == KILL_MOBS || this == USE_SKILL;
    }

    /**
     * 이 목표가 채집/제작 관련인지 확인
     * @return 채집/제작 관련 여부
     */
    public boolean isGatheringOrCrafting() {
        return this == COLLECT_ITEMS || this == CRAFT_ITEMS || 
               this == BREAK_BLOCKS || this == PLACE_BLOCKS || this == FISHING;
    }

    /**
     * 이 목표가 상호작용 관련인지 확인
     * @return 상호작용 관련 여부
     */
    public boolean isInteraction() {
        return this == TALK_TO_NPC || this == ESCORT || this == DELIVER;
    }

    /**
     * 이 목표가 탐험 관련인지 확인
     * @return 탐험 관련 여부
     */
    public boolean isExploration() {
        return this == EXPLORE_REGION;
    }

    /**
     * 진행도 표시 형식 반환 (예: "5/10" 또는 "완료")
     * @param current 현재 진행도
     * @param required 요구량
     * @return 포맷된 진행도 문자열
     */
    public String formatProgress(int current, int required) {
        if (! isCountable()) {
            return current >= required ? "✅ 완료" : "⏳ 진행 중";
        }
        return String.format("%d/%d", current, required);
    }

    /**
     * 진행도 백분율 계산
     * @param current 현재 진행도
     * @param required 요구량
     * @return 백분율 (0~100)
     */
    public int getProgressPercentage(int current, int required) {
        if (required == 0) return 0;
        return Math.min((int) ((double) current / required * 100), 100);
    }

    /**
     * 기본 경험치 배수 반환 (목표 타입별 난이도)
     * @return 경험치 배수 (0. 5 ~ 1.5)
     */
    public double getExperienceMultiplier() {
        switch (this) {
            case KILL_MOBS:
                return 1.2;
            case COLLECT_ITEMS:
                return 0.8;
            case CRAFT_ITEMS:
                return 0.9;
            case BREAK_BLOCKS:
                return 0. 7;
            case PLACE_BLOCKS:
                return 0.7;
            case TALK_TO_NPC:
                return 0.5;
            case EXPLORE_REGION:
                return 1.0;
            case ESCORT:
                return 1.5;
            case DELIVER:
                return 0.8;
            case REACH_LEVEL:
                return 1.0;
            case USE_SKILL:
                return 1.1;
            case EARN_MONEY:
                return 0. 6;
            case TRADE:
                return 0.7;
            case FISHING:
                return 0.8;
            case CUSTOM:
                return 1.0;
            default:
                return 1.0;
        }
    }

    /**
     * 자동 완료 가능 여부 확인 (조건 충족 시 자동 완료)
     * @return 자동 완료 가능 여부
     */
    public boolean isAutoCompletable() {
        return ! isInteractable();
    }
}