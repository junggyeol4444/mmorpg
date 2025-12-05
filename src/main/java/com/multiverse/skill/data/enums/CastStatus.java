package com. multiverse.skill.data.enums;

/**
 * 캐스팅 상태
 */
public enum CastStatus {
    CASTING("캐스팅 중", "§e", true),
    CHANNELING("채널링 중", "§b", true),
    COMPLETED("완료됨", "§a", false),
    CANCELLED("취소됨", "§c", false),
    INTERRUPTED("방해받음", "§4", false),
    FAILED("실패함", "§c", false),
    READY("준비됨", "§2", false);

    private final String displayName;
    private final String colorCode;
    private final boolean isActive;

    CastStatus(String displayName, String colorCode, boolean isActive) {
        this. displayName = displayName;
        this.colorCode = colorCode;
        this.isActive = isActive;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * 색상이 적용된 상태
     */
    public String getColoredStatus() {
        return colorCode + "§l" + displayName;
    }

    /**
     * 진행 중인 상태 여부
     */
    public boolean isProgressing() {
        return this == CASTING || this == CHANNELING;
    }

    /**
     * 완료된 상태 여부
     */
    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED || 
               this == INTERRUPTED || this == FAILED;
    }

    /**
     * 성공한 상태 여부
     */
    public boolean isSuccess() {
        return this == COMPLETED;
    }
}