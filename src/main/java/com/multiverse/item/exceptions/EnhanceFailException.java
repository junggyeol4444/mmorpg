package com.multiverse.item.exceptions;

/**
 * 강화 실패 예외
 */
public class EnhanceFailException extends ItemException {
    
    private int enhanceLevel;
    private double successRate;
    private boolean downgraded;
    
    /**
     * 기본 생성자
     */
    public EnhanceFailException() {
        super("강화에 실패했습니다.");
    }
    
    /**
     * 강화 레벨을 포함한 생성자
     */
    public EnhanceFailException(int enhanceLevel) {
        super("+" + enhanceLevel + " 강화에 실패했습니다.");
        this. enhanceLevel = enhanceLevel;
    }
    
    /**
     * 강화 레벨과 성공률을 포함한 생성자
     */
    public EnhanceFailException(int enhanceLevel, double successRate) {
        super("+" + enhanceLevel + " 강화에 실패했습니다. (성공률: " + String.format("%.1f%%", successRate) + ")");
        this.enhanceLevel = enhanceLevel;
        this.successRate = successRate;
    }
    
    /**
     * 다운그레이드 정보를 포함한 생성자
     */
    public EnhanceFailException(int enhanceLevel, boolean downgraded, int downgradedTo) {
        super("+" + enhanceLevel + " 강화에 실패했습니다." + 
              (downgraded ? " 아이템이 +" + downgradedTo + "로 하락했습니다." : ""));
        this.enhanceLevel = enhanceLevel;
        this. downgraded = downgraded;
    }
    
    /**
     * 강화 레벨 반환
     */
    public int getEnhanceLevel() {
        return enhanceLevel;
    }
    
    /**
     * 성공률 반환
     */
    public double getSuccessRate() {
        return successRate;
    }
    
    /**
     * 다운그레이드 여부 반환
     */
    public boolean isDowngraded() {
        return downgraded;
    }
}