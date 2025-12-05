package com.multiverse.skill. data.enums;

/**
 * 스킬 트리 타입
 */
public enum SkillTreeType {
    WARRIOR("전사", "§c", "전투 능력에 특화된 전사 클래스"),
    MAGE("마법사", "§b", "마법 능력에 특화된 마법사 클래스"),
    ARCHER("궁수", "§e", "원거리 공격에 특화된 궁수 클래스"),
    ROGUE("도적", "§8", "민첩성과 암살에 특화된 도적 클래스"),
    PRIEST("사제", "§d", "치유와 지원 능력에 특화된 사제 클래스"),
    GATHERING("채집", "§2", "생활 스킬 - 채집 관련"),
    CRAFTING("제작", "§6", "생활 스킬 - 제작 관련"),
    COOKING("요리", "§a", "생활 스킬 - 요리 관련"),
    FISHING("낚시", "§9", "생활 스킬 - 낚시 관련"),
    ALCHEMY("연금술", "§5", "생활 스킬 - 연금술 관련"),
    SPECIAL("특수", "§f", "특수 스킬");

    private final String displayName;
    private final String colorCode;
    private final String description;

    SkillTreeType(String displayName, String colorCode, String description) {
        this. displayName = displayName;
        this.colorCode = colorCode;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 색상이 적용된 이름
     */
    public String getColoredName() {
        return colorCode + "§l" + displayName;
    }

    /**
     * 트리 타입이 생활 스킬인지 확인
     */
    public boolean isLifeSkill() {
        return this == GATHERING || this == CRAFTING || this == COOKING || 
               this == FISHING || this == ALCHEMY;
    }

    /**
     * 트리 타입이 전투 스킬인지 확인
     */
    public boolean isCombatSkill() {
        return this == WARRIOR || this == MAGE || this == ARCHER || 
               this == ROGUE || this == PRIEST;
    }
}