package com.multiverse.pet. model.equipment;

import com.multiverse.pet.model.PetRarity;
import com. multiverse.pet. model.PetType;

/**
 * 펫 장비 슬롯 열거형
 * 펫이 장착할 수 있는 장비 슬롯을 정의
 */
public enum PetEquipSlot {

    /**
     * 무기 슬롯
     * - 공격력 증가
     * - 스킬 강화
     */
    WEAPON("무기", "&c", 0, "IRON_SWORD",
           new String[]{"attack", "damage", "critical_chance", "critical_damage"},
           new PetType[]{PetType.COMBAT, PetType. COMPANION}),

    /**
     * 방어구 슬롯
     * - 방어력 증가
     * - HP 증가
     */
    ARMOR("방어구", "&9", 1, "IRON_CHESTPLATE",
          new String[]{"defense", "health", "damage_reduction"},
          new PetType[]{PetType.COMBAT, PetType. SUPPORT, PetType. COMPANION}),

    /**
     * 악세서리 슬롯
     * - 특수 효과
     * - 스탯 보너스
     */
    ACCESSORY("악세서리", "&d", 2, "DIAMOND",
              new String[]{"speed", "luck", "exp_bonus", "skill_cooldown"},
              new PetType[]{PetType. COMBAT, PetType. GATHERING, PetType. SUPPORT, PetType. COMPANION}),

    /**
     * 장신구 슬롯
     * - 경험치 증가
     * - 드롭률 증가
     */
    TRINKET("장신구", "&e", 3, "EMERALD",
            new String[]{"exp_multiplier", "drop_rate", "gold_bonus", "gathering_speed"},
            new PetType[]{PetType.GATHERING, PetType. COMPANION});

    private final String displayName;
    private final String colorCode;
    private final int slotIndex;
    private final String defaultIcon;
    private final String[] applicableStats;
    private final PetType[] compatibleTypes;

    /**
     * PetEquipSlot 생성자
     *
     * @param displayName 표시 이름
     * @param colorCode 색상 코드
     * @param slotIndex 슬롯 인덱스
     * @param defaultIcon 기본 아이콘 Material
     * @param applicableStats 적용 가능한 스탯 목록
     * @param compatibleTypes 호환되는 펫 타입 목록
     */
    PetEquipSlot(String displayName, String colorCode, int slotIndex,
                 String defaultIcon, String[] applicableStats, PetType[] compatibleTypes) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.slotIndex = slotIndex;
        this. defaultIcon = defaultIcon;
        this. applicableStats = applicableStats;
        this.compatibleTypes = compatibleTypes;
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
     * 슬롯 인덱스 반환
     */
    public int getSlotIndex() {
        return slotIndex;
    }

    /**
     * 기본 아이콘 Material 반환
     */
    public String getDefaultIcon() {
        return defaultIcon;
    }

    /**
     * 적용 가능한 스탯 목록 반환
     */
    public String[] getApplicableStats() {
        return applicableStats;
    }

    /**
     * 호환되는 펫 타입 목록 반환
     */
    public PetType[] getCompatibleTypes() {
        return compatibleTypes;
    }

    /**
     * 무기 슬롯인지 확인
     */
    public boolean isWeapon() {
        return this == WEAPON;
    }

    /**
     * 방어구 슬롯인지 확인
     */
    public boolean isArmor() {
        return this == ARMOR;
    }

    /**
     * 악세서리 슬롯인지 확인
     */
    public boolean isAccessory() {
        return this == ACCESSORY;
    }

    /**
     * 장신구 슬롯인지 확인
     */
    public boolean isTrinket() {
        return this == TRINKET;
    }

    /**
     * 전투 관련 슬롯인지 확인
     */
    public boolean isCombatSlot() {
        return this == WEAPON || this == ARMOR;
    }

    /**
     * 유틸리티 슬롯인지 확인
     */
    public boolean isUtilitySlot() {
        return this == ACCESSORY || this == TRINKET;
    }

    /**
     * 특정 스탯이 이 슬롯에 적용 가능한지 확인
     *
     * @param statName 스탯 이름
     * @return 적용 가능 여부
     */
    public boolean isStatApplicable(String statName) {
        if (statName == null) return false;
        for (String stat :  applicableStats) {
            if (stat.equalsIgnoreCase(statName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 특정 펫 타입과 호환되는지 확인
     *
     * @param petType 펫 타입
     * @return 호환 여부
     */
    public boolean isCompatibleWith(PetType petType) {
        if (petType == null) return false;
        for (PetType type : compatibleTypes) {
            if (type == petType) {
                return true;
            }
        }
        return false;
    }

    /**
     * 희귀도에 따른 슬롯 해금 여부 확인
     *
     * @param rarity 펫 희귀도
     * @return 해금 여부
     */
    public boolean isUnlockedForRarity(PetRarity rarity) {
        if (rarity == null) return false;
        int availableSlots = rarity.getEquipmentSlots();
        return slotIndex < availableSlots;
    }

    /**
     * 슬롯별 기본 스탯 보너스 배율 반환
     */
    public double getStatBonusMultiplier() {
        switch (this) {
            case WEAPON: 
                return 1.2;
            case ARMOR:
                return 1.0;
            case ACCESSORY:
                return 0.8;
            case TRINKET: 
                return 0.6;
            default: 
                return 1.0;
        }
    }

    /**
     * 슬롯별 강화 비용 배율 반환
     */
    public double getEnhanceCostMultiplier() {
        switch (this) {
            case WEAPON: 
                return 1.5;
            case ARMOR:
                return 1.3;
            case ACCESSORY:
                return 1.0;
            case TRINKET:
                return 0.8;
            default: 
                return 1.0;
        }
    }

    /**
     * GUI에서의 슬롯 위치 반환
     *
     * @return GUI 슬롯 번호
     */
    public int getGuiSlot() {
        switch (this) {
            case WEAPON: 
                return 10;
            case ARMOR:
                return 12;
            case ACCESSORY:
                return 14;
            case TRINKET:
                return 16;
            default:
                return 0;
        }
    }

    /**
     * 빈 슬롯 설명 반환
     */
    public String getEmptySlotDescription() {
        return "&7" + displayName + " 슬롯이 비어있습니다.\n&7장비를 장착하려면 클릭하세요.";
    }

    /**
     * 슬롯에 맞는 장비 lore 접두사 반환
     */
    public String getLorePrefix() {
        return colorCode + "【" + displayName + "】";
    }

    /**
     * 다음 슬롯 반환
     */
    public PetEquipSlot getNextSlot() {
        int nextIndex = (this. ordinal() + 1) % values().length;
        return values()[nextIndex];
    }

    /**
     * 이전 슬롯 반환
     */
    public PetEquipSlot getPreviousSlot() {
        int prevIndex = (this. ordinal() - 1 + values().length) % values().length;
        return values()[prevIndex];
    }

    /**
     * 인덱스로 슬롯 찾기
     *
     * @param index 슬롯 인덱스
     * @return PetEquipSlot 또는 null
     */
    public static PetEquipSlot fromIndex(int index) {
        for (PetEquipSlot slot : values()) {
            if (slot.getSlotIndex() == index) {
                return slot;
            }
        }
        return null;
    }

    /**
     * 문자열로 PetEquipSlot 찾기
     *
     * @param name 이름
     * @return PetEquipSlot 또는 null
     */
    public static PetEquipSlot fromString(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String upperName = name. toUpperCase().trim();

        // 영어 이름으로 찾기
        try {
            return PetEquipSlot. valueOf(upperName);
        } catch (IllegalArgumentException ignored) {
        }

        // 한글 이름으로 찾기
        for (PetEquipSlot slot :  values()) {
            if (slot.getDisplayName().equals(name)) {
                return slot;
            }
        }

        return null;
    }

    /**
     * 펫 타입에 사용 가능한 슬롯 목록 반환
     *
     * @param petType 펫 타입
     * @return 사용 가능한 슬롯 배열
     */
    public static PetEquipSlot[] getAvailableSlots(PetType petType) {
        return java.util.Arrays. stream(values())
                .filter(slot -> slot. isCompatibleWith(petType))
                .toArray(PetEquipSlot[]:: new);
    }

    /**
     * 희귀도에 따른 해금된 슬롯 목록 반환
     *
     * @param rarity 펫 희귀도
     * @return 해금된 슬롯 배열
     */
    public static PetEquipSlot[] getUnlockedSlots(PetRarity rarity) {
        return java.util.Arrays.stream(values())
                .filter(slot -> slot.isUnlockedForRarity(rarity))
                .toArray(PetEquipSlot[]::new);
    }

    /**
     * 모든 슬롯의 표시 이름 목록 반환
     */
    public static String[] getDisplayNames() {
        PetEquipSlot[] slots = values();
        String[] names = new String[slots.length];
        for (int i = 0; i < slots.length; i++) {
            names[i] = slots[i].getDisplayName();
        }
        return names;
    }

    /**
     * 슬롯 개수 반환
     */
    public static int getSlotCount() {
        return values().length;
    }
}