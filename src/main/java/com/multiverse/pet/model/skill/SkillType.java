package com.multiverse.pet.model.skill;

import com.multiverse.pet.model.PetType;

/**
 * 스킬 타입 열거형
 * 스킬의 종류와 특성을 정의
 */
public enum SkillType {

    /**
     * 공격 스킬
     * - 적에게 데미지
     * - 전투형 펫 특화
     */
    ATTACK("공격", "&c", "적에게 데미지를 입힙니다.", 
           true, false, false, 1.0),

    /**
     * 방어 스킬
     * - 방어력 증가
     * - 데미지 감소
     */
    DEFENSE("방어", "&9", "방어력을 증가시킵니다.", 
            false, true, false, 0.8),

    /**
     * 버프 스킬
     * - 아군 능력 강화
     * - 지원형 펫 특화
     */
    BUFF("버프", "&a", "아군의 능력을 강화합니다.", 
         false, true, false, 0.7),

    /**
     * 디버프 스킬
     * - 적 능력 약화
     * - 상태이상 부여
     */
    DEBUFF("디버프", "&5", "적의 능력을 약화시킵니다.", 
           true, false, false, 0.9),

    /**
     * 힐 스킬
     * - 체력 회복
     * - 지원형 펫 특화
     */
    HEAL("힐", "&d", "체력을 회복합니다.", 
         false, true, false, 0.6),

    /**
     * 채집 스킬
     * - 자원 수집 강화
     * - 채집형 펫 특화
     */
    GATHERING("채집", "&2", "자원 수집 능력을 강화합니다.", 
              false, false, true, 0.5),

    /**
     * 지원 스킬
     * - 주인 보조
     * - 다양한 효과
     */
    SUPPORT("지원", "&b", "주인을 지원합니다.", 
            false, true, false, 0.7),

    /**
     * 특수 스킬
     * - 고유 효과
     * - 레어 스킬
     */
    SPECIAL("특수", "&6", "특수한 효과를 발동합니다.", 
            true, true, true, 1.2),

    /**
     * 궁극기 스킬
     * - 강력한 효과
     * - 긴 쿨다운
     */
    ULTIMATE("궁극기", "&e", "강력한 궁극기를 사용합니다.", 
             true, true, false, 2.0),

    /**
     * 패시브 스킬
     * - 상시 적용
     * - 자동 발동
     */
    PASSIVE("패시브", "&7", "상시 적용되는 효과입니다.", 
            false, false, false, 0.5);

    private final String displayName;
    private final String colorCode;
    private final String description;
    private final boolean offensive;
    private final boolean supportive;
    private final boolean utility;
    private final double powerMultiplier;

    /**
     * SkillType 생성자
     *
     * @param displayName 표시 이름
     * @param colorCode 색상 코드
     * @param description 설명
     * @param offensive 공격적 스킬 여부
     * @param supportive 지원 스킬 여부
     * @param utility 유틸리티 스킬 여부
     * @param powerMultiplier 위력 배율
     */
    SkillType(String displayName, String colorCode, String description,
              boolean offensive, boolean supportive, boolean utility,
              double powerMultiplier) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.description = description;
        this.offensive = offensive;
        this.supportive = supportive;
        this.utility = utility;
        this.powerMultiplier = powerMultiplier;
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
     * 공격적 스킬 여부
     */
    public boolean isOffensive() {
        return offensive;
    }

    /**
     * 지원 스킬 여부
     */
    public boolean isSupportive() {
        return supportive;
    }

    /**
     * 유틸리티 스킬 여부
     */
    public boolean isUtility() {
        return utility;
    }

    /**
     * 위력 배율 반환
     */
    public double getPowerMultiplier() {
        return powerMultiplier;
    }

    /**
     * 공격 타입인지 확인
     */
    public boolean isAttack() {
        return this == ATTACK;
    }

    /**
     * 방어 타입인지 확인
     */
    public boolean isDefense() {
        return this == DEFENSE;
    }

    /**
     * 버프 타입인지 확인
     */
    public boolean isBuff() {
        return this == BUFF;
    }

    /**
     * 디버프 타입인지 확인
     */
    public boolean isDebuff() {
        return this == DEBUFF;
    }

    /**
     * 힐 타입인지 확인
     */
    public boolean isHeal() {
        return this == HEAL;
    }

    /**
     * 채집 타입인지 확인
     */
    public boolean isGathering() {
        return this == GATHERING;
    }

    /**
     * 지원 타입인지 확인
     */
    public boolean isSupport() {
        return this == SUPPORT;
    }

    /**
     * 특수 타입인지 확인
     */
    public boolean isSpecial() {
        return this == SPECIAL;
    }

    /**
     * 궁극기 타입인지 확인
     */
    public boolean isUltimate() {
        return this == ULTIMATE;
    }

    /**
     * 패시브 타입인지 확인
     */
    public boolean isPassive() {
        return this == PASSIVE;
    }

    /**
     * 전투 관련 스킬인지 확인
     */
    public boolean isCombatSkill() {
        return this == ATTACK || this == DEFENSE || 
               this == BUFF || this == DEBUFF || this == ULTIMATE;
    }

    /**
     * 비전투 스킬인지 확인
     */
    public boolean isNonCombatSkill() {
        return this == GATHERING || this == SUPPORT || this == PASSIVE;
    }

    /**
     * 적에게 사용하는 스킬인지 확인
     */
    public boolean targetsEnemy() {
        return this == ATTACK || this == DEBUFF;
    }

    /**
     * 아군에게 사용하는 스킬인지 확인
     */
    public boolean targetsAlly() {
        return this == BUFF || this == HEAL || this == SUPPORT;
    }

    /**
     * 자신에게 사용하는 스킬인지 확인
     */
    public boolean targetsSelf() {
        return this == DEFENSE || this == PASSIVE;
    }

    /**
     * 기본 쿨다운 반환 (초)
     */
    public int getBaseCooldown() {
        switch (this) {
            case ATTACK: 
                return 5;
            case DEFENSE:
                return 15;
            case BUFF:
                return 30;
            case DEBUFF: 
                return 20;
            case HEAL:
                return 25;
            case GATHERING:
                return 60;
            case SUPPORT:
                return 20;
            case SPECIAL:
                return 45;
            case ULTIMATE:
                return 120;
            case PASSIVE: 
                return 0;
            default:
                return 10;
        }
    }

    /**
     * 기본 지속 시간 반환 (초)
     */
    public int getBaseDuration() {
        switch (this) {
            case ATTACK: 
                return 0;
            case DEFENSE:
                return 10;
            case BUFF:
                return 30;
            case DEBUFF:
                return 15;
            case HEAL:
                return 0;
            case GATHERING:
                return 60;
            case SUPPORT: 
                return 20;
            case SPECIAL:
                return 10;
            case ULTIMATE:
                return 15;
            case PASSIVE:
                return -1; // 영구
            default:
                return 0;
        }
    }

    /**
     * 펫 타입에 적합한 스킬인지 확인
     */
    public boolean isSuitableFor(PetType petType) {
        switch (petType) {
            case COMBAT:
                return this == ATTACK || this == DEFENSE || 
                       this == BUFF || this == DEBUFF || 
                       this == ULTIMATE || this == PASSIVE;
            case GATHERING:
                return this == GATHERING || this == SUPPORT || 
                       this == BUFF || this == PASSIVE;
            case SUPPORT:
                return this == HEAL || this == BUFF || 
                       this == SUPPORT || this == DEFENSE || 
                       this == PASSIVE;
            case COMPANION:
                return true; // 모든 스킬 가능
            default: 
                return true;
        }
    }

    /**
     * 펫 타입에 가장 적합한 스킬 타입 반환
     */
    public static SkillType[] getPreferredTypes(PetType petType) {
        switch (petType) {
            case COMBAT:
                return new SkillType[]{ATTACK, DEFENSE, BUFF, ULTIMATE};
            case GATHERING:
                return new SkillType[]{GATHERING, SUPPORT, BUFF};
            case SUPPORT:
                return new SkillType[]{HEAL, BUFF, SUPPORT, DEFENSE};
            case COMPANION:
                return new SkillType[]{BUFF, SUPPORT, ATTACK, HEAL};
            default: 
                return values();
        }
    }

    /**
     * 스킬 타입별 아이콘 Material 반환
     */
    public String getIconMaterial() {
        switch (this) {
            case ATTACK: 
                return "IRON_SWORD";
            case DEFENSE: 
                return "SHIELD";
            case BUFF:
                return "GLOWSTONE_DUST";
            case DEBUFF:
                return "FERMENTED_SPIDER_EYE";
            case HEAL:
                return "GOLDEN_APPLE";
            case GATHERING: 
                return "IRON_PICKAXE";
            case SUPPORT: 
                return "BEACON";
            case SPECIAL:
                return "NETHER_STAR";
            case ULTIMATE:
                return "DRAGON_BREATH";
            case PASSIVE:
                return "ENCHANTED_BOOK";
            default: 
                return "PAPER";
        }
    }

    /**
     * 문자열로 SkillType 찾기
     *
     * @param name 이름
     * @return SkillType 또는 null
     */
    public static SkillType fromString(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String upperName = name.toUpperCase().trim();

        // 영어 이름으로 찾기
        try {
            return SkillType.valueOf(upperName);
        } catch (IllegalArgumentException ignored) {
        }

        // 한글 이름으로 찾기
        for (SkillType type : values()) {
            if (type.getDisplayName().equals(name)) {
                return type;
            }
        }

        return null;
    }

    /**
     * 기본 스킬 타입 반환
     */
    public static SkillType getDefault() {
        return ATTACK;
    }

    /**
     * 모든 스킬 타입의 표시 이름 목록 반환
     */
    public static String[] getDisplayNames() {
        SkillType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getDisplayName();
        }
        return names;
    }

    /**
     * 공격적 스킬 타입 목록 반환
     */
    public static SkillType[] getOffensiveTypes() {
        return new SkillType[]{ATTACK, DEBUFF, ULTIMATE, SPECIAL};
    }

    /**
     * 지원 스킬 타입 목록 반환
     */
    public static SkillType[] getSupportiveTypes() {
        return new SkillType[]{BUFF, HEAL, SUPPORT, DEFENSE};
    }

    /**
     * 유틸리티 스킬 타입 목록 반환
     */
    public static SkillType[] getUtilityTypes() {
        return new SkillType[]{GATHERING, SUPPORT, PASSIVE, SPECIAL};
    }
}