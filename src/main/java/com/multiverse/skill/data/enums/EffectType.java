package com.multiverse.skill.data. enums;

/**
 * 효과 타입
 */
public enum EffectType {
    DAMAGE("데미지", "적에게 데미지를 줌"),
    HEAL("힐", "대상을 치유함"),
    PROJECTILE("투사체", "투사체를 발사함"),
    SUMMON("소환", "소환수를 소환함"),
    BUFF("버프", "플레이어에게 버프를 줌"),
    DEBUFF("디버프", "적에게 디버프를 줌"),
    DOT("지속 데미지", "시간에 따라 데미지를 줌"),
    STUN("스턴", "대상을 기절시킴"),
    KNOCKBACK("넉백", "대상을 밀어냄"),
    TELEPORT("텔레포트", "위치를 이동함"),
    SHIELD("보호막", "보호막을 생성함"),
    CURSE("저주", "대상에게 저주를 걸음");

    private final String displayName;
    private final String description;

    EffectType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 공격형 효과 여부
     */
    public boolean isOffensive() {
        return this == DAMAGE || this == DOT || this == DEBUFF || 
               this == STUN || this == CURSE || this == KNOCKBACK;
    }

    /**
     * 방어형 효과 여부
     */
    public boolean isDefensive() {
        return this == HEAL || this == SHIELD || this == BUFF;
    }

    /**
     * 제어형 효과 여부 (CC)
     */
    public boolean isCC() {
        return this == STUN || this == KNOCKBACK || this == CURSE;
    }

    /**
     * 지속 효과 여부
     */
    public boolean isPersistent() {
        return this == DOT || this == BUFF || this == DEBUFF || this == CURSE;
    }
}