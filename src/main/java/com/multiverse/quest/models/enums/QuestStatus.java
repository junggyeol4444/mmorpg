package com.multiverse.quest.models.enums;

/**
 * 퀘스트 상태 열거형
 * 플레이어의 퀘스트 진행 상태를 정의합니다.
 */
public enum QuestStatus {
    NOT_STARTED("미시작", "아직 시작하지 않은 상태", "⚪"),
    AVAILABLE("수락 가능", "수락 가능한 상태", "🟢"),
    IN_PROGRESS("진행 중", "현재 진행 중인 상태", "🟡"),
    COMPLETED("완료", "목표 달성 후 보상 수령 전", "🔵"),
    FINISHED("완전 종료", "보상 수령 완료", "✅"),
    FAILED("실패", "목표 달성 실패", "❌"),
    EXPIRED("만료", "시간 제한 초과", "⏰");

    private final String displayName;
    private final String description;
    private final String emoji;

    /**
     * QuestStatus 생성자
     * @param displayName 표시명
     * @param description 설명
     * @param emoji 이모지
     */
    QuestStatus(String displayName, String description, String emoji) {
        this. displayName = displayName;
        this.description = description;
        this.emoji = emoji;
    }

    /**
     * 표시명 반환
     * @return 표시명
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 설명 반환
     * @return 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * 이모지 반환
     * @return 이모지
     */
    public String getEmoji() {
        return emoji;
    }

    /**
     * 포맷된 상태명 반환 (이모지 + 이름)
     * @return 포맷된 상태명
     */
    public String getFormattedName() {
        return emoji + " " + displayName;
    }

    /**
     * 문자열로부터 QuestStatus 찾기
     * @param name 이름
     * @return QuestStatus (없으면 null)
     */
    public static QuestStatus fromString(String name) {
        try {
            return QuestStatus.valueOf(name. toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 모든 상태 반환
     * @return QuestStatus 배열
     */
    public static QuestStatus[] getAllStatuses() {
        return QuestStatus. values();
    }

    /**
     * 진행 중인 상태인지 확인
     * @return 진행 중 여부
     */
    public boolean isActive() {
        return this == IN_PROGRESS;
    }

    /**
     * 완료된 상태인지 확인
     * @return 완료 여부
     */
    public boolean isCompleted() {
        return this == COMPLETED || this == FINISHED;
    }

    /**
     * 종료된 상태인지 확인 (더 이상 진행 불가)
     * @return 종료 여부
     */
    public boolean isTerminated() {
        return this == FAILED || this == EXPIRED || this == FINISHED;
    }

    /**
     * 수락 가능한 상태인지 확인
     * @return 수락 가능 여부
     */
    public boolean isAvailable() {
        return this == AVAILABLE;
    }

    /**
     * 진행 가능한 상태인지 확인 (진행 중 또는 완료됨)
     * @return 진행 가능 여부
     */
    public boolean isProgressable() {
        return this == IN_PROGRESS || this == COMPLETED;
    }

    /**
     * 보상 수령 가능 여부 확인
     * @return 보상 수령 가능 여부
     */
    public boolean canClaimReward() {
        return this == COMPLETED;
    }

    /**
     * 다시 시작 가능 여부 확인
     * @return 다시 시작 가능 여부
     */
    public boolean canRestart() {
        return this == FAILED || this == EXPIRED || this == FINISHED;
    }

    /**
     * 포기 가능 여부 확인
     * @return 포기 가능 여부
     */
    public boolean canAbandon() {
        return this == AVAILABLE || this == IN_PROGRESS;
    }

    /**
     * 상태 전환 가능 여부 확인
     * @param nextStatus 다음 상태
     * @return 전환 가능 여부
     */
    public boolean canTransitionTo(QuestStatus nextStatus) {
        switch (this) {
            case NOT_STARTED:
                return nextStatus == AVAILABLE;
            case AVAILABLE:
                return nextStatus == IN_PROGRESS || nextStatus == FAILED;
            case IN_PROGRESS:
                return nextStatus == COMPLETED || nextStatus == FAILED || nextStatus == EXPIRED;
            case COMPLETED:
                return nextStatus == FINISHED;
            case FINISHED:
                return nextStatus == IN_PROGRESS; // 반복 퀘스트
            case FAILED:
                return nextStatus == IN_PROGRESS || nextStatus == NOT_STARTED;
            case EXPIRED:
                return nextStatus == IN_PROGRESS || nextStatus == NOT_STARTED;
            default:
                return false;
        }
    }

    /**
     * UI에 표시할 색상 코드 반환 (ChatColor 호환)
     * @return 색상 코드
     */
    public String getColorCode() {
        switch (this) {
            case NOT_STARTED:
                return "§7"; // 흰색
            case AVAILABLE:
                return "§a"; // 초록색
            case IN_PROGRESS:
                return "§e"; // 노랑색
            case COMPLETED:
                return "§b"; // 하늘색
            case FINISHED:
                return "§2"; // 진초록색
            case FAILED:
                return "§c"; // 빨간색
            case EXPIRED:
                return "§8"; // 회색
            default:
                return "§f"; // 기본 흰색
        }
    }

    /**
     * 진행 가능한 상태로 변경 (수락 -> 진행 중)
     * @return 다음 상태
     */
    public QuestStatus toInProgress() {
        if (this == AVAILABLE || this == NOT_STARTED) {
            return IN_PROGRESS;
        }
        return this;
    }

    /**
     * 완료 상태로 변경 (진행 중 -> 완료)
     * @return 다음 상태
     */
    public QuestStatus toCompleted() {
        if (this == IN_PROGRESS) {
            return COMPLETED;
        }
        return this;
    }

    /**
     * 완전 종료 상태로 변경 (완료 -> 완전 종료)
     * @return 다음 상태
     */
    public QuestStatus toFinished() {
        if (this == COMPLETED) {
            return FINISHED;
        }
        return this;
    }

    /**
     * 퀘스트 일지에 표시할 문자열 반환
     * @return 표시 문자열
     */
    public String getQuestLogDisplay() {
        switch (this) {
            case NOT_STARTED:
                return "아직 시작하지 않음";
            case AVAILABLE:
                return "수락 가능";
            case IN_PROGRESS:
                return "진행 중... ";
            case COMPLETED:
                return "완료!  (보상 수령 가능)";
            case FINISHED:
                return "완료됨";
            case FAILED:
                return "실패함";
            case EXPIRED:
                return "시간 초과";
            default:
                return "알 수 없음";
        }
    }
}