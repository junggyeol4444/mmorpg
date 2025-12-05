package com.multiverse.item. data;

public enum EnhanceResult {
    // 성공
    SUCCESS("강화 성공", true, "&a"),
    
    // 실패
    FAIL("강화 실패", false, "&c"),
    DOWNGRADE("강화 실패 및 다운그레이드", false, "&4"),
    
    // 특수 상황
    MAX_LEVEL("최대 강화 레벨", false, "&e"),
    INSUFFICIENT_MATERIALS("재료 부족", false, "&c"),
    INSUFFICIENT_GOLD("골드 부족", false, "&c"),
    
    // 오류
    INVALID_ITEM("잘못된 아이템", false, "&c"),
    NOT_ENHANCEABLE("강화할 수 없는 아이템", false, "&c");
    
    private String message;
    private boolean isSuccess;
    private String color;
    
    EnhanceResult(String message, boolean isSuccess, String color) {
        this.message = message;
        this.isSuccess = isSuccess;
        this.color = color;
    }
    
    /**
     * 메시지 반환
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * 성공 여부 반환
     */
    public boolean isSuccess() {
        return isSuccess;
    }
    
    /**
     * 색상 코드 반환
     */
    public String getColor() {
        return color;
    }
    
    /**
     * 포맷된 메시지 반환
     */
    public String getFormattedMessage() {
        return color + message;
    }
    
    /**
     * 강화 결과에 대한 상세 설명
     */
    public String getDetailedDescription() {
        switch (this) {
            case SUCCESS:
                return "아이템의 강화에 성공했습니다! ";
            case FAIL:
                return "아이템의 강화에 실패했습니다.";
            case DOWNGRADE:
                return "아이템의 강화에 실패하여 강화 레벨이 하락했습니다.";
            case MAX_LEVEL:
                return "이미 최대 강화 레벨에 도달했습니다. ";
            case INSUFFICIENT_MATERIALS:
                return "강화에 필요한 재료가 부족합니다.";
            case INSUFFICIENT_GOLD:
                return "강화에 필요한 골드가 부족합니다.";
            case INVALID_ITEM:
                return "강화할 수 없는 아이템입니다.";
            case NOT_ENHANCEABLE:
                return "이 아이템은 강화할 수 없습니다.";
            default:
                return "알 수 없는 오류가 발생했습니다.";
        }
    }
    
    /**
     * 결과에 따른 경험치 반환
     */
    public int getExperience() {
        switch (this) {
            case SUCCESS:
                return 100;
            case FAIL:
                return 25;
            case DOWNGRADE:
                return 10;
            default:
                return 0;
        }
    }
}