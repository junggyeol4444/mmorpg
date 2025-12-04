package com.multiverse.combat. models.enums;

/**
 * 상태이상 타입 열거형
 * 플레이어에게 적용될 수 있는 상태이상을 정의합니다.
 */
public enum StatusEffectType {
    /**
     * 기절 - 이동/공격 불가
     */
    STUN("기절"),
    
    /**
     * 침묵 - 스킬 사용 불가
     */
    SILENCE("침묵"),
    
    /**
     * 둔화 - 이동 속도 감소
     */
    SLOW("둔화"),
    
    /**
     * 독 - 지속 데미지
     */
    POISON("독"),
    
    /**
     * 화상 - 지속 데미지 + 방어력 감소
     */
    BURN("화상"),
    
    /**
     * 빙결 - 이동 불가
     */
    FREEZE("빙결"),
    
    /**
     * 출혈 - 지속 데미지
     */
    BLEED("출혈"),
    
    /**
     * 약화 - 공격력 감소
     */
    WEAKNESS("약화"),
    
    /**
     * 실명 - 정확도 감소
     */
    BLIND("실명"),
    
    /**
     * 근원 - 이동 불가
     */
    ROOT("근원"),
    
    /**
     * 무장해제 - 공격 불가
     */
    DISARM("무장해제");
    
    private final String displayName;
    
    /**
     * StatusEffectType 생성자
     * @param displayName 표시할 이름
     */
    StatusEffectType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 표시 이름 반환
     * @return 표시 이름
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 문자열로부터 StatusEffectType 찾기
     * @param name 찾을 이름
     * @return 해당하는 StatusEffectType, 없으면 null
     */
    public static StatusEffectType fromString(String name) {
        if (name == null) return null;
        try {
            return StatusEffectType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}