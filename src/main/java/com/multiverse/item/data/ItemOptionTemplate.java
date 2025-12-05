package com. multiverse.item.data;

public class ItemOptionTemplate {
    
    private String id;
    private String name;
    private OptionType type;
    private OptionTrigger trigger;
    private double minValue;
    private double maxValue;
    private boolean percentage;
    private String description;
    private int rarity; // 0-5 (일반~신화)
    
    /**
     * 기본 생성자
     */
    public ItemOptionTemplate() {
    }
    
    /**
     * 모든 파라미터가 있는 생성자
     */
    public ItemOptionTemplate(String id, String name, OptionType type, OptionTrigger trigger,
                             double minValue, double maxValue, boolean percentage, String description, int rarity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.trigger = trigger;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.percentage = percentage;
        this.description = description;
        this.rarity = Math.max(0, Math.min(rarity, 5));
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public OptionType getType() {
        return type;
    }
    
    public void setType(OptionType type) {
        this. type = type;
    }
    
    public OptionTrigger getTrigger() {
        return trigger;
    }
    
    public void setTrigger(OptionTrigger trigger) {
        this.trigger = trigger;
    }
    
    public double getMinValue() {
        return minValue;
    }
    
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }
    
    public double getMaxValue() {
        return maxValue;
    }
    
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
    
    public boolean isPercentage() {
        return percentage;
    }
    
    public void setPercentage(boolean percentage) {
        this.percentage = percentage;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getRarity() {
        return rarity;
    }
    
    public void setRarity(int rarity) {
        this.rarity = Math.max(0, Math.min(rarity, 5));
    }
    
    /**
     * 옵션 값 범위 검증
     */
    public boolean isValidValue(double value) {
        return value >= minValue && value <= maxValue;
    }
    
    /**
     * 값 범위 제약
     */
    public double clampValue(double value) {
        return Math.max(minValue, Math.min(value, maxValue));
    }
    
    /**
     * 값을 문자열로 포맷
     */
    public String formatValue(double value) {
        if (percentage) {
            return String.format("%.1f%%", value);
        } else {
            return String.format("%.0f", value);
        }
    }
    
    /**
     * 옵션 템플릿 설명 반환
     */
    public String getFormattedDescription() {
        String minStr = formatValue(minValue);
        String maxStr = formatValue(maxValue);
        return description + " (범위: " + minStr + " ~ " + maxStr + ")";
    }
    
    /**
     * 희귀도에 따른 색상 반환
     */
    public String getRarityColor() {
        switch (rarity) {
            case 0:
                return "&7"; // 일반
            case 1:
                return "&a"; // 언커먼
            case 2:
                return "&b"; // 레어
            case 3:
                return "&5"; // 에픽
            case 4:
                return "&6"; // 전설
            case 5:
                return "&c"; // 신화
            default:
                return "&f"; // 흰색
        }
    }
    
    /**
     * 희귀도 이름 반환
     */
    public String getRarityName() {
        switch (rarity) {
            case 0:
                return "일반";
            case 1:
                return "언커먼";
            case 2:
                return "레어";
            case 3:
                return "에픽";
            case 4:
                return "전설";
            case 5:
                return "신화";
            default:
                return "알 수 없음";
        }
    }
    
    /**
     * 옵션 템플릿 정보 출력
     */
    @Override
    public String toString() {
        return "ItemOptionTemplate{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", trigger=" + trigger +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", percentage=" + percentage +
                ", rarity=" + rarity +
                '}';
    }
}