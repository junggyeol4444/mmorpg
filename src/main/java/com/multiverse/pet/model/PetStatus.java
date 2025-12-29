package com. multiverse.pet. model;

/**
 * 펫 상태 열거형
 * 펫의 현재 상태를 정의
 */
public enum PetStatus {

    /**
     * 활성 상태 (소환됨)
     * - 주인과 함께 이동
     * - 전투 가능
     * - 스킬 사용 가능
     */
    ACTIVE("활성", "&a", "소환되어 활동 중입니다.", true, true, false),

    /**
     * 보관 상태
     * - 펫 보관함에 저장
     * - 활동 불가
     * - 소환 대기
     */
    STORED("보관", "&7", "보관함에 저장되어 있습니다.", false, false, false),

    /**
     * 휴식 상태
     * - 체력/상태 회복 중
     * - 활동 불가
     * - 자동 회복 보너스
     */
    RESTING("휴식", "&e", "휴식을 취하고 있습니다.", false, false, true),

    /**
     * 교배 상태
     * - 교배 진행 중
     * - 다른 활동 불가
     * - 교배 완료까지 대기
     */
    BREEDING("교배", "&d", "교배 중입니다.", false, false, false),

    /**
     * 대결 상태
     * - 펫 배틀 진행 중
     * - 다른 활동 불가
     * - 대결 종료까지 대기
     */
    BATTLING("대결", "&c", "펫 대결 중입니다.", false, true, false),

    /**
     * 훈련 상태
     * - 훈련 진행 중
     * - 경험치 획득 중
     * - 다른 활동 불가
     */
    TRAINING("훈련", "&b", "훈련 중입니다.", false, false, false),

    /**
     * 탐험 상태
     * - 자동 탐험 중
     * - 아이템 수집 가능
     * - 다른 활동 불가
     */
    EXPLORING("탐험", "&2", "탐험 중입니다.", false, false, false),

    /**
     * 사망 상태
     * - 체력 0
     * - 부활 필요
     * - 모든 활동 불가
     */
    DEAD("사망", "&8", "사망 상태입니다.  부활이 필요합니다.", false, false, false),

    /**
     * 알 상태
     * - 아직 부화되지 않음
     * - 부화 대기 중
     */
    EGG("알", "&f", "부화를 기다리고 있습니다.", false, false, false);

    private final String displayName;
    private final String colorCode;
    private final String description;
    private final boolean canMove;
    private final boolean canFight;
    private final boolean isRecovering;

    /**
     * PetStatus 생성자
     *
     * @param displayName 표시 이름
     * @param colorCode 색상 코드
     * @param description 설명
     * @param canMove 이동 가능 여부
     * @param canFight 전투 가능 여부
     * @param isRecovering 회복 중 여부
     */
    PetStatus(String displayName, String colorCode, String description,
              boolean canMove, boolean canFight, boolean isRecovering) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.description = description;
        this. canMove = canMove;
        this. canFight = canFight;
        this.isRecovering = isRecovering;
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
     * 이동 가능 여부 반환
     */
    public boolean canMove() {
        return canMove;
    }

    /**
     * 전투 가능 여부 반환
     */
    public boolean canFight() {
        return canFight;
    }

    /**
     * 회복 중 여부 반환
     */
    public boolean isRecovering() {
        return isRecovering;
    }

    /**
     * 활성 상태인지 확인
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 보관 상태인지 확인
     */
    public boolean isStored() {
        return this == STORED;
    }

    /**
     * 휴식 상태인지 확인
     */
    public boolean isResting() {
        return this == RESTING;
    }

    /**
     * 교배 상태인지 확인
     */
    public boolean isBreeding() {
        return this == BREEDING;
    }

    /**
     * 대결 상태인지 확인
     */
    public boolean isBattling() {
        return this == BATTLING;
    }

    /**
     * 훈련 상태인지 확인
     */
    public boolean isTraining() {
        return this == TRAINING;
    }

    /**
     * 탐험 상태인지 확인
     */
    public boolean isExploring() {
        return this == EXPLORING;
    }

    /**
     * 사망 상태인지 확인
     */
    public boolean isDead() {
        return this == DEAD;
    }

    /**
     * 알 상태인지 확인
     */
    public boolean isEgg() {
        return this == EGG;
    }

    /**
     * 소환 가능 여부 확인
     */
    public boolean canBeSummoned() {
        return this == STORED || this == RESTING;
    }

    /**
     * 해제 가능 여부 확인
     */
    public boolean canBeUnsummoned() {
        return this == ACTIVE;
    }

    /**
     * 스킬 사용 가능 여부 확인
     */
    public boolean canUseSkill() {
        return this == ACTIVE || this == BATTLING;
    }

    /**
     * 장비 변경 가능 여부 확인
     */
    public boolean canChangeEquipment() {
        return this == STORED || this == RESTING;
    }

    /**
     * 교배 가능 여부 확인
     */
    public boolean canBreed() {
        return this == STORED;
    }

    /**
     * 먹이 급여 가능 여부 확인
     */
    public boolean canBeFed() {
        return this == ACTIVE || this == STORED || this == RESTING;
    }

    /**
     * 바쁜 상태인지 확인 (다른 활동 불가)
     */
    public boolean isBusy() {
        return this == BREEDING || this == BATTLING || 
               this == TRAINING || this == EXPLORING;
    }

    /**
     * 사용 불가 상태인지 확인
     */
    public boolean isUnavailable() {
        return this == DEAD || this == EGG;
    }

    /**
     * 경험치 획득 가능 여부 확인
     */
    public boolean canGainExp() {
        return this == ACTIVE || this == BATTLING || this == TRAINING;
    }

    /**
     * 문자열로 PetStatus 찾기
     *
     * @param name 이름
     * @return PetStatus 또는 null
     */
    public static PetStatus fromString(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String upperName = name.toUpperCase().trim();

        // 영어 이름으로 찾기
        try {
            return PetStatus.valueOf(upperName);
        } catch (IllegalArgumentException ignored) {
        }

        // 한글 이름으로 찾기
        for (PetStatus status : values()) {
            if (status. getDisplayName().equals(name)) {
                return status;
            }
        }

        return null;
    }

    /**
     * 기본 상태 반환
     */
    public static PetStatus getDefault() {
        return STORED;
    }

    /**
     * 모든 상태의 표시 이름 목록 반환
     */
    public static String[] getDisplayNames() {
        PetStatus[] statuses = values();
        String[] names = new String[statuses.length];
        for (int i = 0; i < statuses.length; i++) {
            names[i] = statuses[i]. getDisplayName();
        }
        return names;
    }
}