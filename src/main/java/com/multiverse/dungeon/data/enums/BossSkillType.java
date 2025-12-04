package com.multiverse.dungeon.data.enums;

/**
 * 보스 스킬 타입 열거형
 */
public enum BossSkillType {

    /**
     * 광역 데미지
     * - 일정 범위의 플레이어에게 데미지
     */
    AOE_DAMAGE("광역데미지"),

    /**
     * 소환
     * - 추가 몬스터 소환
     */
    SUMMON("소환"),

    /**
     * 자기 버프
     * - 보스에게 증강 효과
     */
    BUFF("버프"),

    /**
     * 디버프
     * - 플레이어에게 약화 효과
     */
    DEBUFF("디버프"),

    /**
     * 텔레포트
     * - 보스가 위치 변경
     */
    TELEPORT("텔레포트"),

    /**
     * 돌진
     * - 보스가 플레이어에게 돌진
     */
    CHARGE("돌진"),

    /**
     * 지면 강타
     * - 지면을 강타하여 범위 데미지
     */
    GROUND_SMASH("지면강타"),

    /**
     * 투사체
     * - 투사체 발사
     */
    PROJECTILE("투사체"),

    /**
     * 회복
     * - 보스 체력 회복
     */
    HEALING("회복");

    private final String displayName;

    BossSkillType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 문자열로부터 BossSkillType 조회
     *
     * @param name 스킬 타입 이름
     * @return BossSkillType, 없으면 AOE_DAMAGE
     */
    public static BossSkillType fromString(String name) {
        try {
            return BossSkillType.valueOf(name. toUpperCase());
        } catch (IllegalArgumentException e) {
            return AOE_DAMAGE;
        }
    }

    /**
     * 스킬이 공격 스킬인지 확인
     *
     * @return 공격 스킬이면 true
     */
    public boolean isAttackSkill() {
        return this == AOE_DAMAGE || this == CHARGE || this == GROUND_SMASH || this == PROJECTILE;
    }

    /**
     * 스킬이 지원 스킬인지 확인
     *
     * @return 지원 스킬이면 true
     */
    public boolean isSupportSkill() {
        return this == BUFF || this == HEALING;
    }
}