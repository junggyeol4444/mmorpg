package com.multiverse.item.data;

public class ItemOption {
    
    private String name;
    private OptionType type;
    private OptionTrigger trigger;
    private double value;
    private boolean percentage;
    
    /**
     * 기본 생성자
     */
    public ItemOption() {
    }
    
    /**
     * 모든 파라미터가 있는 생성자
     */
    public ItemOption(String name, OptionType type, OptionTrigger trigger, double value, boolean percentage) {
        this.name = name;
        this.type = type;
        this.trigger = trigger;
        this.value = value;
        this.percentage = percentage;
    }
    
    // Getters and Setters
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
        this.type = type;
    }
    
    public OptionTrigger getTrigger() {
        return trigger;
    }
    
    public void setTrigger(OptionTrigger trigger) {
        this.trigger = trigger;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = Math.max(0, value);
    }
    
    public boolean isPercentage() {
        return percentage;
    }
    
    public void setPercentage(boolean percentage) {
        this.percentage = percentage;
    }
    
    /**
     * 옵션 값을 문자열로 포맷
     */
    public String getFormattedValue() {
        if (percentage) {
            return String.format("%.1f%%", value);
        } else {
            return String.format("%.0f", value);
        }
    }
    
    /**
     * 옵션 정보 출력
     */
    @Override
    public String toString() {
        return name + ": " + getFormattedValue() + " (" + type. name() + ")";
    }
    
    /**
     * 옵션 설명 반환
     */
    public String getDescription() {
        switch (type) {
            case DAMAGE:
                return "공격력을 " + getFormattedValue() + " 증가시킵니다.";
            case DEFENSE:
                return "방어력을 " + getFormattedValue() + " 증가시킵니다.";
            case HEALTH:
                return "최대 체력을 " + getFormattedValue() + " 증가시킵니다.";
            case CRITICAL_RATE:
                return "치명타 확률을 " + getFormattedValue() + " 증가시킵니다.";
            case CRITICAL_DAMAGE:
                return "치명타 피해를 " + getFormattedValue() + " 증가시킵니다.";
            case SPEED:
                return "이동 속도를 " + getFormattedValue() + " 증가시킵니다.";
            case LIFESTEAL:
                return "피해의 " + getFormattedValue() + "를 생명력으로 흡수합니다.";
            case RESISTANCE:
                return "모든 저항을 " + getFormattedValue() + " 증가시킵니다.";
            default:
                return "알 수 없는 옵션입니다.";
        }
    }
}