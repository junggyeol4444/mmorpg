package com.multiverse.item.data;

public enum GemRarity {
    COMMON("&7", 1.0, "일반"),
    UNCOMMON("&a", 1.2, "언커먼"),
    RARE("&b", 1.5, "레어"),
    EPIC("&5", 2.0, "에픽"),
    LEGENDARY("&6", 2.5, "전설"),
    MYTHIC("&c", 3.0, "신화");
    
    private String color;
    private double statMultiplier;
    private String koreanName;
    
    GemRarity(String color, double statMultiplier, String koreanName) {
        this.color = color;
        this.statMultiplier = statMultiplier;
        this.koreanName = koreanName;
    }
    
    /**
     * 색상 코드 반환
     */
    public String getColor() {
        return color;
    }
    
    /**
     * 스탯 배수 반환
     */
    public double getStatMultiplier() {
        return statMultiplier;
    }
    
    /**
     * 한글 이름 반환
     */
    public String getKoreanName() {
        return koreanName;
    }
    
    /**
     * 등급 비교
     */
    public boolean isHigherThan(GemRarity other) {
        return this.statMultiplier > other.statMultiplier;
    }
    
    public boolean isLowerThan(GemRarity other) {
        return this.statMultiplier < other.statMultiplier;
    }
    
    /**
     * 등급에 따른 설명
     */
    public String getDescription() {
        switch (this) {
            case COMMON:
                return "일반적인 보석입니다.";
            case UNCOMMON:
                return "약간 특별한 보석입니다. ";
            case RARE:
                return "드문 보석입니다.";
            case EPIC:
                return "매우 희귀한 보석입니다.";
            case LEGENDARY:
                return "전설적인 보석입니다. ";
            case MYTHIC:
                return "신화 속에만 존재하는 보석입니다.";
            default:
                return "알 수 없는 보석입니다.";
        }
    }
    
    /**
     * 드롭 확률 반환 (%)
     */
    public double getDropRate() {
        switch (this) {
            case COMMON:
                return 50.0;
            case UNCOMMON:
                return 30. 0;
            case RARE:
                return 12.0;
            case EPIC:
                return 5.0;
            case LEGENDARY:
                return 2.5;
            case MYTHIC:
                return 0.5;
            default:
                return 0.0;
        }
    }
}