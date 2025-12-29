package com. multiverse.pet. model;

/**
 * 펫 타입 열거형
 * 펫의 역할과 특성을 정의
 */
public enum PetType {

    /**
     * 전투형 펫
     * - 높은 공격력과 방어력
     * - 전투 스킬 특화
     * - 주인과 함께 전투
     */
    COMBAT("전투형", "&c", "전투에 특화된 펫입니다.", 
           1. 2, 1.1, 1.0, 0.9),

    /**
     * 채집형 펫
     * - 자원 수집 능력
     * - 채집 스킬 특화
     * - 아이템 탐지
     */
    GATHERING("채집형", "&a", "채집에 특화된 펫입니다.", 
              0.8, 0.9, 1.3, 1.0),

    /**
     * 지원형 펫
     * - 버프/힐 능력
     * - 지원 스킬 특화
     * - 주인 보조
     */
    SUPPORT("지원형", "&b", "지원에 특화된 펫입니다.", 
            0.7, 1.0, 1.0, 1.3),

    /**
     * 동반형 펫
     * - 균형 잡힌 스탯
     * - 다양한 스킬
     * - 범용적 활용
     */
    COMPANION("동반형", "&e", "균형 잡힌 만능 펫입니다.", 
              1.0, 1.0, 1.0, 1.0);

    private final String displayName;
    private final String colorCode;
    private final String description;
    private final double attackMultiplier;
    private final double defenseMultiplier;
    private final double gatheringMultiplier;
    private final double supportMultiplier;

    /**
     * PetType 생성자
     *
     * @param displayName 표시 이름
     * @param colorCode 색상 코드
     * @param description 설명
     * @param attackMultiplier 공격력 배율
     * @param defenseMultiplier 방어력 배율
     * @param gatheringMultiplier 채집력 배율
     * @param supportMultiplier 지원력 배율
     */
    PetType(String displayName, String colorCode, String description,
            double attackMultiplier, double defenseMultiplier,
            double gatheringMultiplier, double supportMultiplier) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.description = description;
        this.attackMultiplier = attackMultiplier;
        this.defenseMultiplier = defenseMultiplier;
        this.gatheringMultiplier = gatheringMultiplier;
        this.supportMultiplier = supportMultiplier;
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
     * 공격력 배율 반환
     */
    public double getAttackMultiplier() {
        return attackMultiplier;
    }

    /**
     * 방어력 배율 반환
     */
    public double getDefenseMultiplier() {
        return defenseMultiplier;
    }

    /**
     * 채집력 배율 반환
     */
    public double getGatheringMultiplier() {
        return gatheringMultiplier;
    }

    /**
     * 지원력 배율 반환
     */
    public double getSupportMultiplier() {
        return supportMultiplier;
    }

    /**
     * 스탯 이름에 해당하는 배율 반환
     *
     * @param statName 스탯 이름
     * @return 배율 값
     */
    public double getMultiplierForStat(String statName) {
        switch (statName.toLowerCase()) {
            case "attack":
            case "damage":
                return attackMultiplier;
            case "defense": 
            case "armor":
                return defenseMultiplier;
            case "gathering":
            case "luck":
                return gatheringMultiplier;
            case "support":
            case "healing":
                return supportMultiplier;
            default:
                return 1.0;
        }
    }

    /**
     * 전투형인지 확인
     */
    public boolean isCombatType() {
        return this == COMBAT;
    }

    /**
     * 채집형인지 확인
     */
    public boolean isGatheringType() {
        return this == GATHERING;
    }

    /**
     * 지원형인지 확인
     */
    public boolean isSupportType() {
        return this == SUPPORT;
    }

    /**
     * 동반형인지 확인
     */
    public boolean isCompanionType() {
        return this == COMPANION;
    }

    /**
     * 문자열로 PetType 찾기
     *
     * @param name 이름
     * @return PetType 또는 null
     */
    public static PetType fromString(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String upperName = name.toUpperCase().trim();

        // 영어 이름으로 찾기
        try {
            return PetType.valueOf(upperName);
        } catch (IllegalArgumentException ignored) {
        }

        // 한글 이름으로 찾기
        for (PetType type : values()) {
            if (type.getDisplayName().equals(name)) {
                return type;
            }
        }

        return null;
    }

    /**
     * 기본 타입 반환
     */
    public static PetType getDefault() {
        return COMPANION;
    }

    /**
     * 모든 타입의 표시 이름 목록 반환
     */
    public static String[] getDisplayNames() {
        PetType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types. length; i++) {
            names[i] = types[i].getDisplayName();
        }
        return names;
    }
}