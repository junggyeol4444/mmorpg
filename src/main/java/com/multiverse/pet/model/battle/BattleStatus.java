package com.multiverse.pet.model.battle;

/**
 * 대결 상태 열거형
 * 펫 대결의 진행 상태를 정의
 */
public enum BattleStatus {

    /**
     * 준비 중
     * - 대결 시작 전
     * - 참가자 대기 중
     */
    PREPARING("준비 중", "&e", "대결 준비 중입니다.", false, false),

    /**
     * 대기 중
     * - 상대 수락 대기
     * - 매칭 대기
     */
    WAITING("대기 중", "&7", "상대를 기다리고 있습니다.", false, false),

    /**
     * 카운트다운
     * - 대결 시작 카운트다운
     * - 준비 시간
     */
    COUNTDOWN("카운트다운", "&b", "대결이 곧 시작됩니다!", false, false),

    /**
     * 진행 중
     * - 대결 진행 중
     * - 턴 진행
     */
    ACTIVE("진행 중", "&a", "대결이 진행 중입니다.", true, false),

    /**
     * 일시정지
     * - 대결 일시정지
     * - 연결 끊김 등
     */
    PAUSED("일시정지", "&6", "대결이 일시정지되었습니다.", false, false),

    /**
     * 종료됨
     * - 대결 완료
     * - 결과 확정
     */
    ENDED("종료됨", "&8", "대결이 종료되었습니다.", false, true),

    /**
     * 취소됨
     * - 대결 취소
     * - 중도 포기
     */
    CANCELLED("취소됨", "&c", "대결이 취소되었습니다.", false, true),

    /**
     * 오류
     * - 시스템 오류
     * - 비정상 종료
     */
    ERROR("오류", "&4", "대결 중 오류가 발생했습니다.", false, true);

    private final String displayName;
    private final String colorCode;
    private final String description;
    private final boolean active;
    private final boolean finished;

    /**
     * BattleStatus 생성자
     *
     * @param displayName 표시 이름
     * @param colorCode 색상 코드
     * @param description 설명
     * @param active 활성 상태 여부
     * @param finished 종료 상태 여부
     */
    BattleStatus(String displayName, String colorCode, String description,
                 boolean active, boolean finished) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.description = description;
        this.active = active;
        this. finished = finished;
    }

    /**
     * 표시 이름 반환
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 색상 코드 반환
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * 색상 적용된 이름 반환
     */
    public String getColoredName() {
        return colorCode + displayName;
    }

    /**
     * 설명 반환
     */
    public String getDescription() {
        return description;
    }

    /**
     * 활성 상태 여부 반환
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 종료 상태 여부 반환
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * 준비 중인지 확인
     */
    public boolean isPreparing() {
        return this == PREPARING;
    }

    /**
     * 대기 중인지 확인
     */
    public boolean isWaiting() {
        return this == WAITING;
    }

    /**
     * 카운트다운 중인지 확인
     */
    public boolean isCountdown() {
        return this == COUNTDOWN;
    }

    /**
     * 진행 중인지 확인
     */
    public boolean isInProgress() {
        return this == ACTIVE;
    }

    /**
     * 일시정지인지 확인
     */
    public boolean isPaused() {
        return this == PAUSED;
    }

    /**
     * 종료되었는지 확인
     */
    public boolean isEnded() {
        return this == ENDED;
    }

    /**
     * 취소되었는지 확인
     */
    public boolean isCancelled() {
        return this == CANCELLED;
    }

    /**
     * 오류인지 확인
     */
    public boolean isError() {
        return this == ERROR;
    }

    /**
     * 대결 시작 전인지 확인
     */
    public boolean isBeforeStart() {
        return this == PREPARING || this == WAITING || this == COUNTDOWN;
    }

    /**
     * 대결 진행 가능한지 확인
     */
    public boolean canProgress() {
        return this == ACTIVE;
    }

    /**
     * 취소 가능한지 확인
     */
    public boolean isCancellable() {
        return this == PREPARING || this == WAITING || 
               this == COUNTDOWN || this == PAUSED;
    }

    /**
     * 재개 가능한지 확인
     */
    public boolean isResumable() {
        return this == PAUSED;
    }

    /**
     * 일시정지 가능한지 확인
     */
    public boolean isPausable() {
        return this == ACTIVE;
    }

    /**
     * 행동 가능한지 확인
     */
    public boolean canAct() {
        return this == ACTIVE;
    }

    /**
     * 관전 가능한지 확인
     */
    public boolean canSpectate() {
        return this == ACTIVE || this == COUNTDOWN;
    }

    /**
     * 결과 조회 가능한지 확인
     */
    public boolean canViewResult() {
        return this == ENDED;
    }

    /**
     * 상태 아이콘 반환
     */
    public String getIcon() {
        switch (this) {
            case PREPARING: 
                return "⚙";
            case WAITING:
                return "⏳";
            case COUNTDOWN:
                return "⏱";
            case ACTIVE:
                return "⚔";
            case PAUSED:
                return "⏸";
            case ENDED:
                return "🏁";
            case CANCELLED: 
                return "✖";
            case ERROR:
                return "⚠";
            default:
                return "? ";
        }
    }

    /**
     * 상태별 Material 반환 (GUI용)
     */
    public String getIconMaterial() {
        switch (this) {
            case PREPARING:
                return "CRAFTING_TABLE";
            case WAITING:
                return "CLOCK";
            case COUNTDOWN:
                return "REPEATER";
            case ACTIVE:
                return "DIAMOND_SWORD";
            case PAUSED:
                return "BARRIER";
            case ENDED:
                return "GOLDEN_APPLE";
            case CANCELLED:
                return "RED_STAINED_GLASS_PANE";
            case ERROR: 
                return "TNT";
            default:
                return "PAPER";
        }
    }

    /**
     * 알림 사운드 반환
     */
    public String getSound() {
        switch (this) {
            case PREPARING:
                return "BLOCK_NOTE_BLOCK_PLING";
            case WAITING:
                return "BLOCK_NOTE_BLOCK_HAT";
            case COUNTDOWN:
                return "BLOCK_NOTE_BLOCK_BASS";
            case ACTIVE:
                return "ENTITY_ENDER_DRAGON_GROWL";
            case PAUSED: 
                return "BLOCK_ANVIL_LAND";
            case ENDED:
                return "UI_TOAST_CHALLENGE_COMPLETE";
            case CANCELLED: 
                return "ENTITY_VILLAGER_NO";
            case ERROR: 
                return "ENTITY_WITHER_SPAWN";
            default: 
                return "BLOCK_NOTE_BLOCK_PLING";
        }
    }

    /**
     * 다음 상태로 전환 가능한지 확인
     *
     * @param nextStatus 다음 상태
     * @return 전환 가능 여부
     */
    public boolean canTransitionTo(BattleStatus nextStatus) {
        switch (this) {
            case PREPARING: 
                return nextStatus == WAITING || nextStatus == COUNTDOWN || 
                       nextStatus == CANCELLED;
            case WAITING:
                return nextStatus == COUNTDOWN || nextStatus == CANCELLED;
            case COUNTDOWN:
                return nextStatus == ACTIVE || nextStatus == CANCELLED;
            case ACTIVE:
                return nextStatus == PAUSED || nextStatus == ENDED || 
                       nextStatus == CANCELLED || nextStatus == ERROR;
            case PAUSED:
                return nextStatus == ACTIVE || nextStatus == CANCELLED;
            case ENDED:
            case CANCELLED: 
            case ERROR: 
                return false; // 최종 상태
            default:
                return false;
        }
    }

    /**
     * 자동 전환 대상 상태 반환
     */
    public BattleStatus getNextAutoStatus() {
        switch (this) {
            case PREPARING:
                return WAITING;
            case WAITING:
                return COUNTDOWN;
            case COUNTDOWN: 
                return ACTIVE;
            default:
                return null;
        }
    }

    /**
     * 문자열로 BattleStatus 찾기
     *
     * @param name 이름
     * @return BattleStatus 또는 null
     */
    public static BattleStatus fromString(String name) {
        if (name == null || name. isEmpty()) {
            return null;
        }

        String upperName = name.toUpperCase().trim();

        // 영어 이름으로 찾기
        try {
            return BattleStatus.valueOf(upperName);
        } catch (IllegalArgumentException ignored) {
        }

        // 한글 이름으로 찾기
        for (BattleStatus status : values()) {
            if (status. getDisplayName().equals(name)) {
                return status;
            }
        }

        return null;
    }

    /**
     * 기본 상태 반환
     */
    public static BattleStatus getDefault() {
        return PREPARING;
    }

    /**
     * 모든 상태의 표시 이름 목록 반환
     */
    public static String[] getDisplayNames() {
        BattleStatus[] statuses = values();
        String[] names = new String[statuses.length];
        for (int i = 0; i < statuses.length; i++) {
            names[i] = statuses[i]. getDisplayName();
        }
        return names;
    }

    /**
     * 활성 상태 목록 반환
     */
    public static BattleStatus[] getActiveStatuses() {
        return new BattleStatus[]{ACTIVE};
    }

    /**
     * 시작 전 상태 목록 반환
     */
    public static BattleStatus[] getPreStartStatuses() {
        return new BattleStatus[]{PREPARING, WAITING, COUNTDOWN};
    }

    /**
     * 종료 상태 목록 반환
     */
    public static BattleStatus[] getFinishedStatuses() {
        return new BattleStatus[]{ENDED, CANCELLED, ERROR};
    }
}