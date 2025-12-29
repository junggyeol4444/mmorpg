package com.multiverse.pet.model.breeding;

/**
 * 교배 상태 열거형
 * 펫 교배의 진행 상태를 정의
 */
public enum BreedingStatus {

    /**
     * 진행 중
     * - 교배가 진행 중
     * - 시간 경과 대기
     */
    IN_PROGRESS("진행 중", "&e", "교배가 진행 중입니다.", true, false),

    /**
     * 완료
     * - 교배 성공적으로 완료
     * - 자손 펫 생성됨
     */
    COMPLETED("완료", "&a", "교배가 완료되었습니다.", false, true),

    /**
     * 취소됨
     * - 사용자에 의해 취소
     * - 부분 환불 가능
     */
    CANCELLED("취소됨", "&c", "교배가 취소되었습니다.", false, false),

    /**
     * 실패
     * - 교배 실패
     * - 조건 미충족 또는 확률 실패
     */
    FAILED("실패", "&4", "교배에 실패했습니다.", false, false),

    /**
     * 대기 중
     * - 교배 시작 전 대기
     * - 조건 확인 중
     */
    PENDING("대기 중", "&7", "교배 대기 중입니다.", false, false),

    /**
     * 수집 대기
     * - 교배 완료 후 결과 수집 대기
     * - 자손 펫 수령 필요
     */
    AWAITING_COLLECTION("수집 대기", "&b", "자손을 수령해주세요.", false, true);

    private final String displayName;
    private final String colorCode;
    private final String description;
    private final boolean active;
    private final boolean successful;

    /**
     * BreedingStatus 생성자
     *
     * @param displayName 표시 이름
     * @param colorCode 색상 코드
     * @param description 설명
     * @param active 활성 상태 여부
     * @param successful 성공 상태 여부
     */
    BreedingStatus(String displayName, String colorCode, String description,
                   boolean active, boolean successful) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.description = description;
        this.active = active;
        this. successful = successful;
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
     * 성공 상태 여부 반환
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * 진행 중인지 확인
     */
    public boolean isInProgress() {
        return this == IN_PROGRESS;
    }

    /**
     * 완료되었는지 확인
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * 취소되었는지 확인
     */
    public boolean isCancelled() {
        return this == CANCELLED;
    }

    /**
     * 실패했는지 확인
     */
    public boolean isFailed() {
        return this == FAILED;
    }

    /**
     * 대기 중인지 확인
     */
    public boolean isPending() {
        return this == PENDING;
    }

    /**
     * 수집 대기 중인지 확인
     */
    public boolean isAwaitingCollection() {
        return this == AWAITING_COLLECTION;
    }

    /**
     * 종료된 상태인지 확인 (완료, 취소, 실패)
     */
    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED || 
               this == FAILED || this == AWAITING_COLLECTION;
    }

    /**
     * 취소 가능한 상태인지 확인
     */
    public boolean isCancellable() {
        return this == IN_PROGRESS || this == PENDING;
    }

    /**
     * 환불 가능한 상태인지 확인
     */
    public boolean isRefundable() {
        return this == CANCELLED || this == FAILED;
    }

    /**
     * 부분 환불 비율 반환
     */
    public double getRefundRate() {
        switch (this) {
            case CANCELLED: 
                return 0.5; // 50% 환불
            case FAILED: 
                return 0.25; // 25% 환불
            default:
                return 0.0;
        }
    }

    /**
     * 상태 아이콘 반환
     */
    public String getIcon() {
        switch (this) {
            case IN_PROGRESS:
                return "⏳";
            case COMPLETED: 
                return "✔";
            case CANCELLED:
                return "✖";
            case FAILED:
                return "✘";
            case PENDING:
                return "⏸";
            case AWAITING_COLLECTION:
                return "📦";
            default:
                return "? ";
        }
    }

    /**
     * 상태별 Material 반환 (GUI용)
     */
    public String getIconMaterial() {
        switch (this) {
            case IN_PROGRESS: 
                return "CLOCK";
            case COMPLETED:
                return "LIME_DYE";
            case CANCELLED:
                return "RED_DYE";
            case FAILED: 
                return "BARRIER";
            case PENDING:
                return "GRAY_DYE";
            case AWAITING_COLLECTION:
                return "CHEST";
            default: 
                return "PAPER";
        }
    }

    /**
     * 알림 메시지 반환
     */
    public String getNotificationMessage() {
        switch (this) {
            case IN_PROGRESS: 
                return "&e펫 교배가 시작되었습니다! ";
            case COMPLETED:
                return "&a펫 교배가 완료되었습니다!  새로운 펫이 태어났습니다! ";
            case CANCELLED:
                return "&c펫 교배가 취소되었습니다. ";
            case FAILED:
                return "&4펫 교배에 실패했습니다.";
            case PENDING:
                return "&7펫 교배 대기 중입니다.";
            case AWAITING_COLLECTION:
                return "&b교배 결과를 수령해주세요! ";
            default: 
                return "";
        }
    }

    /**
     * 다음 상태로 전환 가능한지 확인
     *
     * @param nextStatus 다음 상태
     * @return 전환 가능 여부
     */
    public boolean canTransitionTo(BreedingStatus nextStatus) {
        switch (this) {
            case PENDING:
                return nextStatus == IN_PROGRESS || nextStatus == CANCELLED;
            case IN_PROGRESS: 
                return nextStatus == COMPLETED || nextStatus == CANCELLED || 
                       nextStatus == FAILED;
            case COMPLETED:
                return nextStatus == AWAITING_COLLECTION;
            case AWAITING_COLLECTION: 
                return false; // 최종 상태
            case CANCELLED:
            case FAILED: 
                return false; // 최종 상태
            default: 
                return false;
        }
    }

    /**
     * 문자열로 BreedingStatus 찾기
     *
     * @param name 이름
     * @return BreedingStatus 또는 null
     */
    public static BreedingStatus fromString(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String upperName = name.toUpperCase().trim().replace(" ", "_");

        // 영어 이름으로 찾기
        try {
            return BreedingStatus. valueOf(upperName);
        } catch (IllegalArgumentException ignored) {
        }

        // 한글 이름으로 찾기
        for (BreedingStatus status : values()) {
            if (status.getDisplayName().equals(name)) {
                return status;
            }
        }

        return null;
    }

    /**
     * 기본 상태 반환
     */
    public static BreedingStatus getDefault() {
        return PENDING;
    }

    /**
     * 모든 상태의 표시 이름 목록 반환
     */
    public static String[] getDisplayNames() {
        BreedingStatus[] statuses = values();
        String[] names = new String[statuses.length];
        for (int i = 0; i < statuses.length; i++) {
            names[i] = statuses[i].getDisplayName();
        }
        return names;
    }

    /**
     * 활성 상태 목록 반환
     */
    public static BreedingStatus[] getActiveStatuses() {
        return new BreedingStatus[]{IN_PROGRESS, PENDING};
    }

    /**
     * 종료 상태 목록 반환
     */
    public static BreedingStatus[] getFinishedStatuses() {
        return new BreedingStatus[]{COMPLETED, CANCELLED, FAILED, AWAITING_COLLECTION};
    }
}