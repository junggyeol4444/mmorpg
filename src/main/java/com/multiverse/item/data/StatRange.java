package com. multiverse.item.data;

public class StatRange {
    
    private String statName;
    private double minValue;
    private double maxValue;
    private boolean percentage;
    
    /**
     * 기본 생성자
     */
    public StatRange() {
    }
    
    /**
     * 모든 파라미터가 있는 생성자
     */
    public StatRange(String statName, double minValue, double maxValue, boolean percentage) {
        this.statName = statName;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this. percentage = percentage;
    }
    
    // Getters and Setters
    public String getStatName() {
        return statName;
    }
    
    public void setStatName(String statName) {
        this. statName = statName;
    }
    
    public double getMinValue() {
        return minValue;
    }
    
    public void setMinValue(double minValue) {
        this. minValue = minValue;
    }
    
    public double getMaxValue() {
        return maxValue;
    }
    
    public void setMaxValue(double maxValue) {
        this. maxValue = maxValue;
    }
    
    public boolean isPercentage() {
        return percentage;
    }
    
    public void setPercentage(boolean percentage) {
        this. percentage = percentage;
    }
    
    /**
     * 값이 범위 내인지 확인
     */
    public boolean isInRange(double value) {
        return value >= minValue && value <= maxValue;
    }
    
    /**
     * 값을 범위 내로 제약
     */
    public double clampValue(double value) {
        return Math.max(minValue, Math.min(value, maxValue));
    }
    
    /**
     * 범위 폭
     */
    public double getRangeWidth() {
        return maxValue - minValue;
    }
    
    /**
     * 범위의 중간값
     */
    public double getMiddleValue() {
        return (minValue + maxValue) / 2.0;
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
     * 범위를 문자열로 포맷
     */
    public String formatRange() {
        String minStr = formatValue(minValue);
        String maxStr = formatValue(maxValue);
        return minStr + " ~ " + maxStr;
    }
    
    /**
     * 정규화된 값 반환 (0. 0 ~ 1.0)
     */
    public double normalize(double value) {
        if (getRangeWidth() == 0) {
            return 0.0;
        }
        return (value - minValue) / getRangeWidth();
    }
    
    /**
     * 정규화된 값을 원래 범위로 변환
     */
    public double denormalize(double normalized) {
        return minValue + (normalized * getRangeWidth());
    }
    
    /**
     * StatRange 정보 출력
     */
    @Override
    public String toString() {
        return "StatRange{" +
                "statName='" + statName + '\'' +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", percentage=" + percentage +
                '}';
    }
}