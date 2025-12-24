package com.multiverse.pvp.enums;

public enum DuelEndReason {

    DEATH("사망", "&c", "상대를 처치하여 승리했습니다."),
    SURRENDER("항복", "&e", "상대가 항복하여 승리했습니다."),
    DISCONNECT("접속 종료", "&7", "상대가 접속을 종료하여 승리했습니다."),
    TIMEOUT("시간 초과", "&6", "시간이 초과되어 듀얼이 종료되었습니다."),
    CANCELLED("취소됨", "&8", "듀얼이 취소되었습니다."),
    ADMIN("관리자", "&4", "관리자에 의해 듀얼이 종료되었습니다."),
    LEAVE_ARENA("아레나 이탈", "&c", "상대가 아레나를 이탈하여 승리했습니다. "),
    ERROR("오류", "&4", "오류로 인해 듀얼이 종료되었습니다.");

    private final String displayName;
    private final String color;
    private final String message;

    DuelEndReason(String displayName, String color, String message) {
        this.displayName = displayName;
        this.color = color;
        this.message = message;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedName() {
        return color + displayName;
    }

    /**
     * 정상적인 종료인지 확인
     */
    public boolean isNormalEnd() {
        return this == DEATH || this == SURRENDER || this == TIMEOUT;
    }

    /**
     * 강제 종료인지 확인
     */
    public boolean isForcedEnd() {
        return this == DISCONNECT || this == CANCELLED || this == ADMIN || this == LEAVE_ARENA || this == ERROR;
    }

    /**
     * 승자가 있는 종료인지 확인
     */
    public boolean hasWinner() {
        return this == DEATH || this == SURRENDER || this == DISCONNECT || this == LEAVE_ARENA;
    }

    /**
     * 레이팅 변동이 있는 종료인지 확인
     */
    public boolean affectsRating() {
        return this == DEATH || this == SURRENDER;
    }

    /**
     * 베팅 보상이 지급되는 종료인지 확인
     */
    public boolean givesBetReward() {
        return this == DEATH || this == SURRENDER || this == DISCONNECT || this == LEAVE_ARENA;
    }

    /**
     * 베팅이 환불되는 종료인지 확인
     */
    public boolean refundsBet() {
        return this == CANCELLED || this == ADMIN || this == ERROR || this == TIMEOUT;
    }

    /**
     * 패배자에게 불이익이 있는 종료인지 확인
     */
    public boolean penalizesLoser() {
        return this == DEATH || this == SURRENDER;
    }

    /**
     * 접속 종료로 인한 패배인지 확인
     */
    public boolean isDisconnectLoss() {
        return this == DISCONNECT || this == LEAVE_ARENA;
    }

    /**
     * 승자용 메시지 반환
     */
    public String getWinnerMessage() {
        switch (this) {
            case DEATH: 
                return "&a상대를 처치하여 승리했습니다! ";
            case SURRENDER:
                return "&a상대가 항복하여 승리했습니다!";
            case DISCONNECT:
                return "&a상대가 접속을 종료하여 승리했습니다. ";
            case LEAVE_ARENA: 
                return "&a상대가 아레나를 이탈하여 승리했습니다. ";
            case TIMEOUT:
                return "&e시간 초과로 무승부입니다.";
            default:
                return "&7듀얼이 종료되었습니다.";
        }
    }

    /**
     * 패배자용 메시지 반환
     */
    public String getLoserMessage() {
        switch (this) {
            case DEATH:
                return "&c상대에게 처치당하여 패배했습니다. ";
            case SURRENDER:
                return "&c항복하여 패배했습니다.";
            case DISCONNECT:
                return "&c접속 종료로 패배 처리되었습니다. ";
            case LEAVE_ARENA: 
                return "&c아레나 이탈로 패배 처리되었습니다.";
            case TIMEOUT:
                return "&e시간 초과로 무승부입니다.";
            default:
                return "&7듀얼이 종료되었습니다.";
        }
    }

    /**
     * 문자열로부터 DuelEndReason 반환
     */
    public static DuelEndReason fromString(String str) {
        if (str == null) {
            return CANCELLED;
        }
        
        try {
            return valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (DuelEndReason reason : values()) {
                if (reason.displayName. equalsIgnoreCase(str)) {
                    return reason;
                }
            }
            return CANCELLED;
        }
    }
}