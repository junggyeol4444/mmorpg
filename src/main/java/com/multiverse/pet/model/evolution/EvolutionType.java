package com.multiverse. pet.model.evolution;

/**
 * 진화 타입 열거형
 * 펫 진화의 종류를 정의
 */
public enum EvolutionType {

    /**
     * 일반 진화
     * - 레벨 조건 충족 시 진화
     * - 선형적 성장
     */
    NORMAL("일반 진화", "&a", "레벨을 달성하면 진화합니다.", 
           1. 5, true, false),

    /**
     * 분기 진화
     * - 특정 조건에 따라 다른 형태로 진화
     * - 여러 경로 중 선택
     */
    BRANCH("분기 진화", "&e", "조건에 따라 다른 형태로 진화합니다.", 
           1.5, true, true),

    /**
     * 메가 진화
     * - 일시적 강화 진화
     * - 시간 제한 있음
     */
    MEGA("메가 진화", "&5", "일시적으로 강력해집니다.", 
         2.0, false, false),

    /**
     * 융합 진화
     * - 두 펫을 합쳐서 진화
     * - 재료 펫 소멸
     */
    FUSION("융합 진화", "&6", "다른 펫과 융합하여 진화합니다.", 
           2.5, true, false),

    /**
     * 각성 진화
     * - 최종 진화 단계
     * - 매우 어려운 조건
     */
    AWAKENING("각성 진화", "&c", "궁극의 형태로 각성합니다.", 
              3.0, true, false),

    /**
     * 퇴화
     * - 이전 단계로 되돌아감
     * - 특수 상황에서만 발생
     */
    DEVOLUTION("퇴화", "&8", "이전 형태로 되돌아갑니다.", 
               0.8, true, false),

    /**
     * 변이 진화
     * - 랜덤 변이
     * - 예상치 못한 결과
     */
    MUTATION("변이 진화", "&d", "예상치 못한 형태로 변이합니다.", 
             1.8, true, false),

    /**
     * 환경 진화
     * - 특정 환경에서만 진화
     * - 바이옴/날씨 조건
     */
    ENVIRONMENTAL("환경 진화", "&2", "특정 환경에서 진화합니다.", 
                  1.6, true, false),

    /**
     * 유대 진화
     * - 주인과의 친밀도로 진화
     * - 행복도 조건
     */
    BOND("유대 진화", "&b", "주인과의 유대로 진화합니다.", 
         1.7, true, false),

    /**
     * 아이템 진화
     * - 특수 아이템으로 진화
     * - 아이템 소모
     */
    ITEM("아이템 진화", "&9", "특수 아이템으로 진화합니다.", 
         1.5, true, false);

    private final String displayName;
    private final String colorCode;
    private final String description;
    private final double statBoostMultiplier;
    private final boolean permanent;
    private final boolean hasMultiplePaths;

    /**
     * EvolutionType 생성자
     *
     * @param displayName 표시 이름
     * @param colorCode 색상 코드
     * @param description 설명
     * @param statBoostMultiplier 스탯 증가 배율
     * @param permanent 영구 진화 여부
     * @param hasMultiplePaths 다중 경로 여부
     */
    EvolutionType(String displayName, String colorCode, String description,
                  double statBoostMultiplier, boolean permanent, boolean hasMultiplePaths) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.description = description;
        this.statBoostMultiplier = statBoostMultiplier;
        this.permanent = permanent;
        this. hasMultiplePaths = hasMultiplePaths;
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
     * 스탯 증가 배율 반환
     */
    public double getStatBoostMultiplier() {
        return statBoostMultiplier;
    }

    /**
     * 영구 진화 여부 반환
     */
    public boolean isPermanent() {
        return permanent;
    }

    /**
     * 다중 경로 여부 반환
     */
    public boolean hasMultiplePaths() {
        return hasMultiplePaths;
    }

    /**
     * 일반 진화인지 확인
     */
    public boolean isNormal() {
        return this == NORMAL;
    }

    /**
     * 분기 진화인지 확인
     */
    public boolean isBranch() {
        return this == BRANCH;
    }

    /**
     * 메가 진화인지 확인
     */
    public boolean isMega() {
        return this == MEGA;
    }

    /**
     * 융합 진화인지 확인
     */
    public boolean isFusion() {
        return this == FUSION;
    }

    /**
     * 각성 진화인지 확인
     */
    public boolean isAwakening() {
        return this == AWAKENING;
    }

    /**
     * 퇴화인지 확인
     */
    public boolean isDevolution() {
        return this == DEVOLUTION;
    }

    /**
     * 변이 진화인지 확인
     */
    public boolean isMutation() {
        return this == MUTATION;
    }

    /**
     * 환경 진화인지 확인
     */
    public boolean isEnvironmental() {
        return this == ENVIRONMENTAL;
    }

    /**
     * 유대 진화인지 확인
     */
    public boolean isBond() {
        return this == BOND;
    }

    /**
     * 아이템 진화인지 확인
     */
    public boolean isItem() {
        return this == ITEM;
    }

    /**
     * 일시적 진화인지 확인
     */
    public boolean isTemporary() {
        return ! permanent;
    }

    /**
     * 재료가 필요한 진화인지 확인
     */
    public boolean requiresMaterial() {
        return this == FUSION || this == ITEM;
    }

    /**
     * 다른 펫이 필요한 진화인지 확인
     */
    public boolean requiresOtherPet() {
        return this == FUSION;
    }

    /**
     * 환경 조건이 필요한 진화인지 확인
     */
    public boolean requiresEnvironment() {
        return this == ENVIRONMENTAL;
    }

    /**
     * 친밀도가 필요한 진화인지 확인
     */
    public boolean requiresBond() {
        return this == BOND;
    }

    /**
     * 진화 난이도 반환 (1-5)
     */
    public int getDifficulty() {
        switch (this) {
            case NORMAL: 
                return 1;
            case BRANCH:
                return 2;
            case ITEM:
                return 2;
            case ENVIRONMENTAL:
                return 2;
            case BOND:
                return 3;
            case MEGA:
                return 3;
            case MUTATION:
                return 3;
            case FUSION:
                return 4;
            case AWAKENING:
                return 5;
            case DEVOLUTION:
                return 1;
            default:
                return 1;
        }
    }

    /**
     * 진화에 필요한 기본 레벨 반환
     */
    public int getBaseRequiredLevel() {
        switch (this) {
            case NORMAL:
                return 20;
            case BRANCH:
                return 30;
            case MEGA:
                return 50;
            case FUSION:
                return 40;
            case AWAKENING:
                return 75;
            case DEVOLUTION:
                return 1;
            case MUTATION:
                return 25;
            case ENVIRONMENTAL:
                return 25;
            case BOND:
                return 35;
            case ITEM:
                return 15;
            default: 
                return 20;
        }
    }

    /**
     * 메가 진화 지속 시간 반환 (초)
     */
    public int getMegaDuration() {
        if (this != MEGA) return -1;
        return 300; // 5분
    }

    /**
     * 진화 성공 기본 확률 반환
     */
    public double getBaseSuccessChance() {
        switch (this) {
            case NORMAL: 
                return 100.0;
            case BRANCH: 
                return 100.0;
            case MEGA: 
                return 100.0;
            case FUSION:
                return 80.0;
            case AWAKENING: 
                return 50.0;
            case DEVOLUTION: 
                return 100.0;
            case MUTATION: 
                return 30.0;
            case ENVIRONMENTAL: 
                return 90.0;
            case BOND: 
                return 95.0;
            case ITEM: 
                return 100.0;
            default:
                return 100.0;
        }
    }

    /**
     * 진화 아이콘 Material 반환
     */
    public String getIconMaterial() {
        switch (this) {
            case NORMAL:
                return "EXPERIENCE_BOTTLE";
            case BRANCH:
                return "PRISMARINE_CRYSTALS";
            case MEGA:
                return "NETHER_STAR";
            case FUSION:
                return "BEACON";
            case AWAKENING:
                return "DRAGON_EGG";
            case DEVOLUTION: 
                return "FERMENTED_SPIDER_EYE";
            case MUTATION:
                return "CHORUS_FRUIT";
            case ENVIRONMENTAL:
                return "GRASS_BLOCK";
            case BOND: 
                return "GOLDEN_APPLE";
            case ITEM:
                return "EMERALD";
            default:
                return "PAPER";
        }
    }

    /**
     * 문자열로 EvolutionType 찾기
     *
     * @param name 이름
     * @return EvolutionType 또는 null
     */
    public static EvolutionType fromString(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String upperName = name.toUpperCase().trim();

        // 영어 이름으로 찾기
        try {
            return EvolutionType. valueOf(upperName);
        } catch (IllegalArgumentException ignored) {
        }

        // 한글 이름으로 찾기
        for (EvolutionType type :  values()) {
            if (type.getDisplayName().equals(name)) {
                return type;
            }
        }

        return null;
    }

    /**
     * 기본 진화 타입 반환
     */
    public static EvolutionType getDefault() {
        return NORMAL;
    }

    /**
     * 모든 진화 타입의 표시 이름 목록 반환
     */
    public static String[] getDisplayNames() {
        EvolutionType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types. length; i++) {
            names[i] = types[i].getDisplayName();
        }
        return names;
    }

    /**
     * 영구 진화 타입 목록 반환
     */
    public static EvolutionType[] getPermanentTypes() {
        return new EvolutionType[]{
                NORMAL, BRANCH, FUSION, AWAKENING, 
                DEVOLUTION, MUTATION, ENVIRONMENTAL, BOND, ITEM
        };
    }

    /**
     * 일시적 진화 타입 목록 반환
     */
    public static EvolutionType[] getTemporaryTypes() {
        return new EvolutionType[]{MEGA};
    }

    /**
     * 난이도별 진화 타입 목록 반환
     */
    public static EvolutionType[] getByDifficulty(int difficulty) {
        return java.util.Arrays. stream(values())
                .filter(t -> t.getDifficulty() == difficulty)
                .toArray(EvolutionType[]::new);
    }
}