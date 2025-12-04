package com.multiverse.dungeon.data.enums;

/**
 * 랜덤 던전 함정 타입 열거형
 */
public enum TrapType {

    /**
     * 스파이크 함정
     * - 플레이어가 밟으면 데미지
     */
    SPIKE("스파이크", 5. 0),

    /**
     * 독 함정
     * - 플레이어에게 독 효과 적용
     */
    POISON("독", 3.0),

    /**
     * 폭탄 함정
     * - 범위 폭발
     */
    BOMB("폭탄", 15.0),

    /**
     * 얼음 함정
     * - 플레이어 이동 속도 감소
     */
    ICE("얼음", 2.0),

    /**
     * 불 함정
     * - 플레이어에게 화상 효과
     */
    FIRE("불", 8.0),

    /**
     * 전기 함정
     * - 플레이어에게 전기 데미지
     */
    LIGHTNING("전기", 10.0),

    /**
     * 낙사 함정
     * - 플레이어를 아래로 떨어뜨림
     */
    FALL("낙사", 20.0);

    private final String displayName;
    private final double baseDamage;

    TrapType(String displayName, double baseDamage) {
        this.displayName = displayName;
        this.baseDamage = baseDamage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    /**
     * 문자열로부터 TrapType 조회
     *
     * @param name 함정 타입 이름
     * @return TrapType, 없으면 SPIKE
     */
    public static TrapType fromString(String name) {
        try {
            return TrapType. valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SPIKE;
        }
    }

    /**
     * 모든 함정 타입 반환
     *
     * @return 함정 타입 배열
     */
    public static TrapType[] getAll() {
        return values();
    }
}