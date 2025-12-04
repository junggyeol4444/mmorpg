package com.multiverse.dungeon.data.enums;

/**
 * 던전 인스턴스 상태 열거형
 */
public enum InstanceStatus {

    /**
     * 생성 중
     * - 인스턴스 월드 복사 중
     * - 플레이어 진입 대기
     */
    CREATING("생성 중"),

    /**
     * 진행 중
     * - 인스턴스 활성
     * - 플레이어 던전 진행 중
     */
    ACTIVE("진행 중"),

    /**
     * 완료
     * - 모든 목표 달성
     * - 보상 지급 완료
     */
    COMPLETED("완료"),

    /**
     * 실패
     * - 파티 전멸
     * - 목표 미달성
     */
    FAILED("실패"),

    /**
     * 시간 초과
     * - 제한 시간 경과
     */
    EXPIRED("시간초과"),

    /**
     * 포기
     * - 파티원이 모두 나감
     */
    ABANDONED("포기");

    private final String displayName;

    InstanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 문자열로부터 InstanceStatus 조회
     *
     * @param name 상태 이름
     * @return InstanceStatus, 없으면 ACTIVE
     */
    public static InstanceStatus fromString(String name) {
        try {
            return InstanceStatus.valueOf(name. toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }

    /**
     * 인스턴스가 진행 중인지 확인
     *
     * @return 진행 중이면 true
     */
    public boolean isActive() {
        return this == ACTIVE || this == CREATING;
    }

    /**
     * 인스턴스가 종료되었는지 확인
     *
     * @return 종료되었으면 true
     */
    public boolean isFinished() {
        return this == COMPLETED || this == FAILED || this == EXPIRED || this == ABANDONED;
    }
}