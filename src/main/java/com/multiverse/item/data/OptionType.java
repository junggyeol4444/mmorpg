package com.multiverse.item. data;

public enum OptionType {
    // 공격 관련
    DAMAGE("공격력", "공격 피해를 증가시킵니다.", true),
    CRITICAL_RATE("치명타확률", "치명타 발생 확률을 증가시킵니다.", true),
    CRITICAL_DAMAGE("치명타피해", "치명타로 인한 피해를 증가시킵니다.", true),
    
    // 방어 관련
    DEFENSE("방어력", "받는 피해를 감소시킵니다.", true),
    HEALTH("체력", "최대 체력을 증가시킵니다.", false),
    RESISTANCE("저항", "모든 저항을 증가시킵니다.", true),
    
    // 유틸리티
    SPEED("이동속도", "이동 속도를 증가시킵니다.", true),
    LIFESTEAL("생명력흡수", "피해의 일부를 생명력으로 흡수합니다.", true),
    EXPERIENCE("경험치획득", "획득하는 경험치를 증가시킵니다.", true),
    ITEM_DROP("아이템드롭", "아이템 드롭율을 증가시킵니다.", true);
    
    private String koreanName;
    private String description;
    private boolean canBePercentage;
    
    OptionType(String koreanName, String description, boolean canBePercentage) {
        this.koreanName = koreanName;
        this.description = description;
        this.canBePercentage = canBePercentage;
    }
    
    /**
     * 한글 이름 반환
     */
    public String getKoreanName() {
        return koreanName;
    }
    
    /**
     * 설명 반환
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 퍼센티지 가능 여부
     */
    public boolean canBePercentage() {
        return canBePercentage;
    }
    
    /**
     * 공격 관련 옵션인지 확인
     */
    public boolean isAttackOption() {
        return this == DAMAGE || this == CRITICAL_RATE || this == CRITICAL_DAMAGE;
    }
    
    /**
     * 방어 관련 옵션인지 확인
     */
    public boolean isDefenseOption() {
        return this == DEFENSE || this == HEALTH || this == RESISTANCE;
    }
    
    /**
     * 유틸리티 옵션인지 확인
     */
    public boolean isUtilityOption() {
        return this == SPEED || this == LIFESTEAL || this == EXPERIENCE || this == ITEM_DROP;
    }
}