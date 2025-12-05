package com.multiverse.item. data;

public enum GemType {
    // 공격 관련
    STRENGTH("힘", "공격력을 증가시킵니다.", true, false),
    DEXTERITY("민첩", "공격 속도와 치명타율을 증가시킵니다.", true, false),
    
    // 방어 관련
    VITALITY("활력", "체력을 증가시킵니다.", false, true),
    ENDURANCE("인내", "방어력을 증가시킵니다.", false, true),
    
    // 특수 보석
    INTELLIGENCE("지능", "마력을 증가시킵니다.", true, false),
    WISDOM("지혜", "마나 회복을 증가시킵니다.", false, false),
    LUCK("운", "아이템 드롭율을 증가시킵니다.", false, false),
    RESISTANCE("저항", "모든 저항을 증가시킵니다.", false, true);
    
    private String koreanName;
    private String description;
    private boolean isOffensive;
    private boolean isDefensive;
    
    GemType(String koreanName, String description, boolean isOffensive, boolean isDefensive) {
        this.koreanName = koreanName;
        this.description = description;
        this.isOffensive = isOffensive;
        this.isDefensive = isDefensive;
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
     * 공격형 보석인지 확인
     */
    public boolean isOffensive() {
        return isOffensive;
    }
    
    /**
     * 방어형 보석인지 확인
     */
    public boolean isDefensive() {
        return isDefensive;
    }
    
    /**
     * 특수 보석인지 확인
     */
    public boolean isSpecial() {
        return this == LUCK || this == WISDOM;
    }
}