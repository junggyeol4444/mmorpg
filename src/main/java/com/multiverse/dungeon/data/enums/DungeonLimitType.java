package com.multiverse.dungeon.data.enums;

/**
 * 던전 입장 제한 타입 열거형
 */
public enum DungeonLimitType {

    /**
     * 일일 제한
     * - 하루에 최대 N회 입장 가능
     */
    DAILY("일일", 86400000), // 24시간 (밀리초)

    /**
     * 주간 제한
     * - 일주일에 최대 N회 입장 가능
     */
    WEEKLY("주간", 604800000), // 7일 (밀리초)

    /**
     * 무제한
     * - 제한 없음
     */
    UNLIMITED("무제한", 0);

    private final String displayName;
    private final long duration; // 밀리초

    DungeonLimitType(String displayName, long duration) {
        this. displayName = displayName;
        this.duration = duration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getDuration() {
        return duration;
    }

    /**
     * 제한이 있는지 확인
     *
     * @return 제한이 있으면 true
     */
    public boolean hasLimit() {
        return this != UNLIMITED;
    }

    /**
     * 문자열로부터 DungeonLimitType 조회
     *
     * @param name 제한 타입 이름
     * @return DungeonLimitType, 없으면 UNLIMITED
     */
    public static DungeonLimitType fromString(String name) {
        try {
            return DungeonLimitType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNLIMITED;
        }
    }
}