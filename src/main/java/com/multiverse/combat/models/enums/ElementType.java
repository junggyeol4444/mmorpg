package com. multiverse.combat.models.enums;

/**
 * 속성 타입 열거형
 * 스킬과 방어구의 속성을 정의합니다.
 */
public enum ElementType {
    /**
     * 화 - 얼음에 강함, 물에 약함
     */
    FIRE("§c화", 1. 5, 0.5),
    
    /**
     * 물 - 화염에 강함, 번개에 약함
     */
    WATER("§9물", 1.5, 0.5),
    
    /**
     * 바람 - 대지에 약함
     */
    WIND("§e바람", 1.0, 1.0),
    
    /**
     * 대지 - 화염에 강함, 바람에 약함
     */
    EARTH("§6대지", 1.5, 0.5),
    
    /**
     * 번개 - 물에 강함, 대지에 약함
     */
    LIGHTNING("§b번개", 1.5, 0.5),
    
    /**
     * 얼음 - 물에 강함, 화염에 약함
     */
    ICE("§b얼음", 1.5, 0.5),
    
    /**
     * 빛 - 어둠에 강함, 언데드 추가 데미지
     */
    LIGHT("§e빛", 1.2, 1.0),
    
    /**
     * 어둠 - 빛에 약함, 생명력 흡수
     */
    DARK("§4어둠", 1.0, 1.0),
    
    /**
     * 무 - 속성 없음
     */
    NEUTRAL("무속성", 1.0, 1.0);
    
    private final String displayName;
    private final double damageMultiplier;
    private final double speedMultiplier;
    
    /**
     * ElementType 생성자
     * @param displayName 표시할 이름
     * @param damageMultiplier 데미지 배수
     * @param speedMultiplier 속도 배수
     */
    ElementType(String displayName, double damageMultiplier, double speedMultiplier) {
        this.displayName = displayName;
        this.damageMultiplier = damageMultiplier;
        this.speedMultiplier = speedMultiplier;
    }
    
    /**
     * 표시 이름 반환
     * @return 표시 이름
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 기본 데미지 배수 반환
     * @return 배수
     */
    public double getDamageMultiplier() {
        return damageMultiplier;
    }
    
    /**
     * 속도 배수 반환
     * @return 배수
     */
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
    
    /**
     * 상성 관계 계산
     * @param target 방어 속성
     * @return 상성 배수 (1.5 = 유리, 1.0 = 중립, 0.75 = 불리)
     */
    public double getAdvantage(ElementType target) {
        if (target == null || target == NEUTRAL) {
            return 1.0;
        }
        
        switch (this) {
            case FIRE:
                return target == ICE ? 1.5 : (target == WATER ? 0.75 : 1.0);
            case WATER:
                return target == FIRE ? 1.5 : (target == LIGHTNING ? 0.75 : 1.0);
            case WIND:
                return target == EARTH ? 0.75 : 1.0;
            case EARTH:
                return target == WIND ? 1.5 : (target == FIRE ? 0.75 : 1.0);
            case LIGHTNING:
                return target == WATER ? 1.5 : (target == EARTH ? 0.75 : 1.0);
            case ICE:
                return target == WATER ? 1.5 : (target == FIRE ? 0.75 : 1.0);
            case LIGHT:
                return target == DARK ? 1.5 : 1.0;
            case DARK:
                return target == LIGHT ? 0.75 : 1.0;
            case NEUTRAL:
            default:
                return 1. 0;
        }
    }
    
    /**
     * 문자열로부터 ElementType 찾기
     * @param name 찾을 이름
     * @return 해당하는 ElementType, 없으면 NEUTRAL
     */
    public static ElementType fromString(String name) {
        if (name == null) return NEUTRAL;
        try {
            return ElementType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NEUTRAL;
        }
    }
}