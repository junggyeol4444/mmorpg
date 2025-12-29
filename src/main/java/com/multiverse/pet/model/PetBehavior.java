package com.multiverse. pet.model;

/**
 * 펫 행동 열거형
 * 펫의 행동 패턴을 정의
 */
public enum PetBehavior {

    /**
     * 따라오기
     * - 주인을 따라다님
     * - 일정 거리 유지
     * - 기본 행동
     */
    FOLLOW("따라오기", "&a", "주인을 따라다닙니다.", 
           true, false, 3. 0, 15.0),

    /**
     * 대기
     * - 현재 위치에서 대기
     * - 이동하지 않음
     * - 방어만 가능
     */
    STAY("대기", "&e", "현재 위치에서 대기합니다.", 
         false, false, 0.0, 0.0),

    /**
     * 공격적
     * - 주변 적을 자동 공격
     * - 주인이 공격한 대상 우선
     * - 넓은 인식 범위
     */
    AGGRESSIVE("공격적", "&c", "주변의 적을 공격합니다.", 
               true, true, 5.0, 20.0),

    /**
     * 소극적
     * - 공격받을 때만 반격
     * - 스스로 공격하지 않음
     * - 방어 위주
     */
    PASSIVE("소극적", "&7", "공격받을 때만 반격합니다.", 
            true, false, 3.0, 10.0),

    /**
     * 수호
     * - 주인 주변을 순찰
     * - 주인에게 다가오는 적 공격
     * - 주인 보호 우선
     */
    GUARD("수호", "&9", "주인을 수호합니다.", 
          true, true, 5.0, 10.0),

    /**
     * 자유
     * - 자유롭게 돌아다님
     * - 랜덤 이동
     * - 일정 범위 내 유지
     */
    WANDER("자유", "&b", "자유롭게 돌아다닙니다.", 
           true, false, 10.0, 30.0),

    /**
     * 채집
     * - 주변 아이템 수집
     * - 채집 가능한 자원 탐색
     * - 채집형 펫 전용
     */
    GATHER("채집", "&2", "주변 아이템을 수집합니다.", 
           true, false, 8.0, 20.0),

    /**
     * 탐지
     * - 특정 대상 탐지
     * - 몬스터/보물 등 탐색
     * - 탐지 시 알림
     */
    SCOUT("탐지", "&d", "주변을 탐지합니다.", 
          true, false, 15.0, 30.0);

    private final String displayName;
    private final String colorCode;
    private final String description;
    private final boolean canMove;
    private final boolean autoAttack;
    private final double followDistance;
    private final double maxDistance;

    /**
     * PetBehavior 생성자
     *
     * @param displayName 표시 이름
     * @param colorCode 색상 코드
     * @param description 설명
     * @param canMove 이동 가능 여부
     * @param autoAttack 자동 공격 여부
     * @param followDistance 따라가기 거리
     * @param maxDistance 최대 거리
     */
    PetBehavior(String displayName, String colorCode, String description,
                boolean canMove, boolean autoAttack, 
                double followDistance, double maxDistance) {
        this. displayName = displayName;
        this. colorCode = colorCode;
        this. description = description;
        this.canMove = canMove;
        this.autoAttack = autoAttack;
        this. followDistance = followDistance;
        this. maxDistance = maxDistance;
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
     * 자동 공격 여부 반환
     */
    public boolean isAutoAttack() {
        return autoAttack;
    }

    /**
     * 따라가기 거리 반환
     */
    public double getFollowDistance() {
        return followDistance;
    }

    /**
     * 최대 거리 반환
     */
    public double getMaxDistance() {
        return maxDistance;
    }

    /**
     * 텔레포트 거리 반환 (최대 거리 초과 시 텔레포트)
     */
    public double getTeleportDistance() {
        return maxDistance + 5.0;
    }

    /**
     * 따라오기 행동인지 확인
     */
    public boolean isFollow() {
        return this == FOLLOW;
    }

    /**
     * 대기 행동인지 확인
     */
    public boolean isStay() {
        return this == STAY;
    }

    /**
     * 공격적 행동인지 확인
     */
    public boolean isAggressive() {
        return this == AGGRESSIVE;
    }

    /**
     * 소극적 행동인지 확인
     */
    public boolean isPassive() {
        return this == PASSIVE;
    }

    /**
     * 수호 행동인지 확인
     */
    public boolean isGuard() {
        return this == GUARD;
    }

    /**
     * 자유 행동인지 확인
     */
    public boolean isWander() {
        return this == WANDER;
    }

    /**
     * 채집 행동인지 확인
     */
    public boolean isGather() {
        return this == GATHER;
    }

    /**
     * 탐지 행동인지 확인
     */
    public boolean isScout() {
        return this == SCOUT;
    }

    /**
     * 전투 관련 행동인지 확인
     */
    public boolean isCombatBehavior() {
        return this == AGGRESSIVE || this == GUARD;
    }

    /**
     * 비전투 행동인지 확인
     */
    public boolean isNonCombatBehavior() {
        return this == STAY || this == PASSIVE || 
               this == WANDER || this == GATHER || this == SCOUT;
    }

    /**
     * 주인 중심 행동인지 확인
     */
    public boolean isOwnerCentric() {
        return this == FOLLOW || this == GUARD || this == PASSIVE;
    }

    /**
     * 이동 속도 배율 반환
     */
    public double getSpeedMultiplier() {
        switch (this) {
            case AGGRESSIVE:
                return 1.3;
            case GUARD:
                return 1.2;
            case SCOUT:
                return 1.4;
            case WANDER:
                return 0.8;
            case GATHER: 
                return 0.9;
            case STAY:
                return 0.0;
            default:
                return 1.0;
        }
    }

    /**
     * 인식 범위 반환
     */
    public double getDetectionRange() {
        switch (this) {
            case AGGRESSIVE:
                return 15.0;
            case GUARD: 
                return 10.0;
            case SCOUT: 
                return 25.0;
            case GATHER:
                return 12.0;
            case PASSIVE:
                return 5.0;
            default:
                return 8.0;
        }
    }

    /**
     * 공격 우선순위 반환 (높을수록 우선)
     */
    public int getAttackPriority() {
        switch (this) {
            case AGGRESSIVE:
                return 10;
            case GUARD:
                return 8;
            case FOLLOW:
                return 5;
            case PASSIVE:
                return 2;
            default:
                return 0;
        }
    }

    /**
     * 다음 행동으로 순환
     */
    public PetBehavior getNextBehavior() {
        // 기본 순환:  FOLLOW -> STAY -> AGGRESSIVE -> PASSIVE -> FOLLOW
        switch (this) {
            case FOLLOW:
                return STAY;
            case STAY: 
                return AGGRESSIVE;
            case AGGRESSIVE:
                return PASSIVE;
            case PASSIVE: 
                return FOLLOW;
            case GUARD:
                return FOLLOW;
            case WANDER: 
                return FOLLOW;
            case GATHER:
                return FOLLOW;
            case SCOUT:
                return FOLLOW;
            default:
                return FOLLOW;
        }
    }

    /**
     * 펫 타입에 적합한 행동인지 확인
     */
    public boolean isSuitableFor(PetType petType) {
        switch (petType) {
            case COMBAT:
                return this == FOLLOW || this == AGGRESSIVE || 
                       this == GUARD || this == PASSIVE || this == STAY;
            case GATHERING:
                return this == FOLLOW || this == GATHER || 
                       this == WANDER || this == STAY;
            case SUPPORT:
                return this == FOLLOW || this == GUARD || 
                       this == PASSIVE || this == STAY;
            case COMPANION:
                return true; // 모든 행동 가능
            default:
                return true;
        }
    }

    /**
     * 문자열로 PetBehavior 찾기
     *
     * @param name 이름
     * @return PetBehavior 또는 null
     */
    public static PetBehavior fromString(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String upperName = name.toUpperCase().trim();

        // 영어 이름으로 찾기
        try {
            return PetBehavior.valueOf(upperName);
        } catch (IllegalArgumentException ignored) {
        }

        // 한글 이름으로 찾기
        for (PetBehavior behavior : values()) {
            if (behavior. getDisplayName().equals(name)) {
                return behavior;
            }
        }

        return null;
    }

    /**
     * 기본 행동 반환
     */
    public static PetBehavior getDefault() {
        return FOLLOW;
    }

    /**
     * 펫 타입에 맞는 기본 행동 반환
     */
    public static PetBehavior getDefaultFor(PetType petType) {
        switch (petType) {
            case COMBAT:
                return AGGRESSIVE;
            case GATHERING:
                return GATHER;
            case SUPPORT: 
                return FOLLOW;
            case COMPANION:
                return FOLLOW;
            default:
                return FOLLOW;
        }
    }

    /**
     * 모든 행동의 표시 이름 목록 반환
     */
    public static String[] getDisplayNames() {
        PetBehavior[] behaviors = values();
        String[] names = new String[behaviors.length];
        for (int i = 0; i < behaviors. length; i++) {
            names[i] = behaviors[i].getDisplayName();
        }
        return names;
    }

    /**
     * 특정 펫 타입에 적합한 행동 목록 반환
     */
    public static PetBehavior[] getSuitableBehaviors(PetType petType) {
        return java.util.Arrays. stream(values())
                .filter(b -> b.isSuitableFor(petType))
                .toArray(PetBehavior[]:: new);
    }
}