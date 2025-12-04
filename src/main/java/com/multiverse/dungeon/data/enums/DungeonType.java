package com.multiverse.dungeon. data.enums;

/**
 * 던전 타입 열거형
 */
public enum DungeonType {
    
    /**
     * 일반 던전
     * - 1~5인 파티
     * - 일반 난이도
     * - 제한 시간 30분
     * - 일일 입장 제한 3회
     */
    NORMAL("일반", "goblin_cave"),
    
    /**
     * 영웅 던전
     * - 5인 파티 필수
     * - 높은 난이도
     * - 제한 시간 60분
     * - 일일 입장 제한 1회
     * - 더 좋은 보상
     */
    HEROIC("영웅", "dragon_lair"),
    
    /**
     * 신화 던전
     * - 5인 파티 필수
     * - 최고 난이도
     * - 제한 시간 90분
     * - 주간 입장 제한 1회
     * - 최고급 보상
     */
    MYTHIC("신화", "titan_fortress"),
    
    /**
     * 레이드 던전
     * - 10~20인 파티
     * - 여러 보스
     * - 제한 시간 120분
     * - 주간 입장 제한 1회
     */
    RAID("레이드", "abyss_tower"),
    
    /**
     * 솔로 던전
     * - 1인 전용
     * - 개인 난이도 조절
     * - 무제한 입장
     * - 적은 보상
     */
    SOLO("솔로", "training_ground"),
    
    /**
     * 랜덤 던전
     * - 매번 구조 변경
     * - 랜덤 몬스터
     * - 랜덤 보상
     */
    RANDOM("랜덤", "random_maze");

    private final String displayName;
    private final String defaultDungeonId;

    DungeonType(String displayName, String defaultDungeonId) {
        this. displayName = displayName;
        this.defaultDungeonId = defaultDungeonId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultDungeonId() {
        return defaultDungeonId;
    }

    /**
     * 문자열로부터 DungeonType 조회
     *
     * @param name 던전 타입 이름
     * @return DungeonType, 없으면 null
     */
    public static DungeonType fromString(String name) {
        try {
            return DungeonType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}