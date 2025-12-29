package com.multiverse.pet.model.storage;

import com.multiverse.pet.model.Pet;

import java.util. Comparator;
import java.util.List;
import java.util. ArrayList;
import java.util.Collections;

/**
 * 정렬 타입 열거형
 * 펫 보관함에서 펫을 정렬하는 방법을 정의
 */
public enum SortType {

    /**
     * 레벨순 (높은 순)
     */
    LEVEL("레벨순", "&e", "레벨이 높은 순서로 정렬합니다.",
          Comparator.comparingInt(Pet::getLevel).reversed()),

    /**
     * 레벨순 (낮은 순)
     */
    LEVEL_ASC("레벨순 (낮은순)", "&e", "레벨이 낮은 순서로 정렬합니다.",
              Comparator.comparingInt(Pet::getLevel)),

    /**
     * 이름순 (가나다순)
     */
    NAME("이름순", "&a", "이름의 가나다 순서로 정렬합니다.",
         Comparator.comparing(Pet::getPetName, Comparator.nullsLast(String:: compareToIgnoreCase))),

    /**
     * 이름순 (역순)
     */
    NAME_DESC("이름순 (역순)", "&a", "이름의 역순으로 정렬합니다.",
              Comparator.comparing(Pet::getPetName, Comparator.nullsLast(String::compareToIgnoreCase)).reversed()),

    /**
     * 희귀도순 (높은 순)
     */
    RARITY("희귀도순", "&5", "희귀도가 높은 순서로 정렬합니다.",
           Comparator.comparing(pet -> pet.getRarity() != null ? pet. getRarity().ordinal() : 0,
                               Comparator.reverseOrder())),

    /**
     * 희귀도순 (낮은 순)
     */
    RARITY_ASC("희귀도순 (낮은순)", "&5", "희귀도가 낮은 순서로 정렬합니다.",
               Comparator.comparing(pet -> pet. getRarity() != null ? pet.getRarity().ordinal() : 0)),

    /**
     * 획득순 (최근)
     */
    ACQUIRED_DATE("획득순 (최근)", "&b", "최근에 획득한 순서로 정렬합니다.",
                  Comparator.comparingLong(Pet::getBirthTime).reversed()),

    /**
     * 획득순 (오래된)
     */
    ACQUIRED_DATE_ASC("획득순 (오래된)", "&b", "오래전에 획득한 순서로 정렬합니다.",
                      Comparator.comparingLong(Pet::getBirthTime)),

    /**
     * 타입순
     */
    TYPE("타입순", "&9", "펫 타입별로 정렬합니다.",
         Comparator. comparing(pet -> pet.getType() != null ? pet. getType().ordinal() : 0)),

    /**
     * 종족순
     */
    SPECIES("종족순", "&2", "종족별로 정렬합니다.",
            Comparator.comparing(Pet::getSpeciesId, Comparator. nullsLast(String::compareToIgnoreCase))),

    /**
     * 체력순 (높은 순)
     */
    HEALTH("체력순", "&c", "현재 체력이 높은 순서로 정렬합니다.",
           Comparator.comparingDouble(Pet::getHealth).reversed()),

    /**
     * 행복도순 (높은 순)
     */
    HAPPINESS("행복도순", "&d", "행복도가 높은 순서로 정렬합니다.",
              Comparator.comparingDouble(Pet::getHappiness).reversed()),

    /**
     * 행복도순 (낮은 순)
     */
    HAPPINESS_ASC("행복도순 (낮은순)", "&d", "행복도가 낮은 순서로 정렬합니다.",
                  Comparator.comparingDouble(Pet::getHappiness)),

    /**
     * 배고픔순 (높은 순)
     */
    HUNGER("배고픔순", "&6", "배고픔이 높은 순서로 정렬합니다.",
           Comparator.comparingDouble(Pet::getHunger).reversed()),

    /**
     * 배고픔순 (낮은 순)
     */
    HUNGER_ASC("배고픔순 (낮은순)", "&6", "배고픔이 낮은 순서로 정렬합니다.",
               Comparator.comparingDouble(Pet:: getHunger)),

    /**
     * 경험치순 (높은 순)
     */
    EXPERIENCE("경험치순", "&e", "경험치가 높은 순서로 정렬합니다.",
               Comparator.comparingLong(Pet::getExperience).reversed()),

    /**
     * 전투력순 (높은 순)
     */
    POWER("전투력순", "&c", "전투력이 높은 순서로 정렬합니다.",
          Comparator.comparingDouble(pet -> pet.getTotalStat("attack") + pet.getTotalStat("defense")).reversed()),

    /**
     * 킬 카운트순
     */
    KILLS("킬 카운트순", "&4", "킬 수가 많은 순서로 정렬합니다.",
          Comparator.comparingInt(Pet::getKillCount).reversed()),

    /**
     * 배틀 승률순
     */
    WIN_RATE("승률순", "&6", "배틀 승률이 높은 순서로 정렬합니다.",
             Comparator.comparingDouble(Pet:: getWinRate).reversed()),

    /**
     * 진화 단계순
     */
    EVOLUTION("진화 단계순", "&5", "진화 단계가 높은 순서로 정렬합니다.",
              Comparator.comparingInt(Pet:: getEvolutionStage).reversed()),

    /**
     * 스킬 개수순
     */
    SKILL_COUNT("스킬 개수순", "&b", "보유 스킬이 많은 순서로 정렬합니다.",
                Comparator.comparingInt(pet -> pet.getSkills() != null ? pet. getSkills().size() : 0).reversed()),

    /**
     * 활동 시간순
     */
    ACTIVE_TIME("활동 시간순", "&7", "총 활동 시간이 긴 순서로 정렬합니다.",
                Comparator. comparingLong(Pet::getTotalActiveTime).reversed()),

    /**
     * 상태순
     */
    STATUS("상태순", "&a", "상태별로 정렬합니다.",
           Comparator.comparing(pet -> pet.getStatus() != null ? pet.getStatus().ordinal() : 0)),

    /**
     * 커스텀 (기본값)
     */
    CUSTOM("사용자 정의", "&f", "사용자가 정의한 순서로 정렬합니다.", null);

    private final String displayName;
    private final String colorCode;
    private final String description;
    private final Comparator<Pet> comparator;

    /**
     * SortType 생성자
     *
     * @param displayName 표시 이름
     * @param colorCode 색상 코드
     * @param description 설명
     * @param comparator 정렬 비교자
     */
    SortType(String displayName, String colorCode, String description, Comparator<Pet> comparator) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.description = description;
        this.comparator = comparator;
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
     * 비교자 반환
     */
    public Comparator<Pet> getComparator() {
        return comparator;
    }

    /**
     * 오름차순 정렬인지 확인
     */
    public boolean isAscending() {
        return name().endsWith("_ASC");
    }

    /**
     * 내림차순 정렬인지 확인
     */
    public boolean isDescending() {
        return ! isAscending() && this != CUSTOM;
    }

    /**
     * 역순 정렬 타입 반환
     */
    public SortType getReversed() {
        switch (this) {
            case LEVEL: 
                return LEVEL_ASC;
            case LEVEL_ASC:
                return LEVEL;
            case NAME: 
                return NAME_DESC;
            case NAME_DESC:
                return NAME;
            case RARITY: 
                return RARITY_ASC;
            case RARITY_ASC: 
                return RARITY;
            case ACQUIRED_DATE: 
                return ACQUIRED_DATE_ASC;
            case ACQUIRED_DATE_ASC:
                return ACQUIRED_DATE;
            case HAPPINESS:
                return HAPPINESS_ASC;
            case HAPPINESS_ASC: 
                return HAPPINESS;
            case HUNGER:
                return HUNGER_ASC;
            case HUNGER_ASC: 
                return HUNGER;
            default:
                return this;
        }
    }

    /**
     * 펫 목록 정렬
     *
     * @param pets 정렬할 펫 목록
     * @return 정렬된 펫 목록 (새 리스트)
     */
    public List<Pet> sort(List<Pet> pets) {
        if (pets == null || pets.isEmpty()) {
            return new ArrayList<>();
        }

        List<Pet> sorted = new ArrayList<>(pets);
        if (comparator != null) {
            sorted.sort(comparator);
        }
        return sorted;
    }

    /**
     * 펫 목록 정렬 (원본 수정)
     *
     * @param pets 정렬할 펫 목록
     */
    public void sortInPlace(List<Pet> pets) {
        if (pets == null || pets.isEmpty() || comparator == null) {
            return;
        }
        pets.sort(comparator);
    }

    /**
     * 다중 정렬 조건 적용
     *
     * @param pets 정렬할 펫 목록
     * @param sortTypes 정렬 조건 목록 (우선순위 순)
     * @return 정렬된 펫 목록
     */
    public static List<Pet> multiSort(List<Pet> pets, SortType... sortTypes) {
        if (pets == null || pets.isEmpty() || sortTypes == null || sortTypes.length == 0) {
            return new ArrayList<>(pets != null ? pets : Collections.emptyList());
        }

        List<Pet> sorted = new ArrayList<>(pets);
        Comparator<Pet> combined = null;

        for (SortType sortType : sortTypes) {
            if (sortType. getComparator() != null) {
                if (combined == null) {
                    combined = sortType.getComparator();
                } else {
                    combined = combined.thenComparing(sortType.getComparator());
                }
            }
        }

        if (combined != null) {
            sorted.sort(combined);
        }

        return sorted;
    }

    /**
     * 다음 정렬 타입으로 순환
     */
    public SortType getNext() {
        SortType[] mainTypes = getMainSortTypes();
        for (int i = 0; i < mainTypes.length; i++) {
            if (mainTypes[i] == this) {
                return mainTypes[(i + 1) % mainTypes.length];
            }
        }
        return LEVEL;
    }

    /**
     * 이전 정렬 타입으로 순환
     */
    public SortType getPrevious() {
        SortType[] mainTypes = getMainSortTypes();
        for (int i = 0; i < mainTypes.length; i++) {
            if (mainTypes[i] == this) {
                return mainTypes[(i - 1 + mainTypes. length) % mainTypes.length];
            }
        }
        return LEVEL;
    }

    /**
     * 레벨 관련 정렬인지 확인
     */
    public boolean isLevelSort() {
        return this == LEVEL || this == LEVEL_ASC;
    }

    /**
     * 이름 관련 정렬인지 확인
     */
    public boolean isNameSort() {
        return this == NAME || this == NAME_DESC;
    }

    /**
     * 희귀도 관련 정렬인지 확인
     */
    public boolean isRaritySort() {
        return this == RARITY || this == RARITY_ASC;
    }

    /**
     * 획득일 관련 정렬인지 확인
     */
    public boolean isDateSort() {
        return this == ACQUIRED_DATE || this == ACQUIRED_DATE_ASC;
    }

    /**
     * 상태 관련 정렬인지 확인
     */
    public boolean isStatusSort() {
        return this == HAPPINESS || this == HAPPINESS_ASC ||
               this == HUNGER || this == HUNGER_ASC ||
               this == HEALTH || this == STATUS;
    }

    /**
     * 전투 관련 정렬인지 확인
     */
    public boolean isCombatSort() {
        return this == POWER || this == KILLS || this == WIN_RATE;
    }

    /**
     * 정렬 아이콘 반환
     */
    public String getIcon() {
        switch (this) {
            case LEVEL:
            case LEVEL_ASC:
                return "⬆";
            case NAME:
            case NAME_DESC:
                return "Ａ";
            case RARITY: 
            case RARITY_ASC: 
                return "★";
            case ACQUIRED_DATE: 
            case ACQUIRED_DATE_ASC: 
                return "📅";
            case TYPE:
                return "🏷";
            case SPECIES:
                return "🐾";
            case HEALTH:
                return "❤";
            case HAPPINESS:
            case HAPPINESS_ASC:
                return "😊";
            case HUNGER: 
            case HUNGER_ASC:
                return "🍖";
            case EXPERIENCE:
                return "✨";
            case POWER:
                return "⚔";
            case KILLS:
                return "💀";
            case WIN_RATE: 
                return "🏆";
            case EVOLUTION:
                return "🔄";
            case SKILL_COUNT: 
                return "📚";
            case ACTIVE_TIME: 
                return "⏱";
            case STATUS: 
                return "📊";
            default:
                return "📋";
        }
    }

    /**
     * 정렬 아이콘 Material 반환 (GUI용)
     */
    public String getIconMaterial() {
        switch (this) {
            case LEVEL:
            case LEVEL_ASC:
                return "EXPERIENCE_BOTTLE";
            case NAME:
            case NAME_DESC:
                return "NAME_TAG";
            case RARITY:
            case RARITY_ASC:
                return "NETHER_STAR";
            case ACQUIRED_DATE:
            case ACQUIRED_DATE_ASC:
                return "CLOCK";
            case TYPE:
                return "BOOK";
            case SPECIES:
                return "SPAWNER";
            case HEALTH:
                return "RED_DYE";
            case HAPPINESS: 
            case HAPPINESS_ASC:
                return "GOLDEN_APPLE";
            case HUNGER: 
            case HUNGER_ASC: 
                return "COOKED_BEEF";
            case EXPERIENCE:
                return "ENCHANTED_BOOK";
            case POWER:
                return "DIAMOND_SWORD";
            case KILLS:
                return "WITHER_SKELETON_SKULL";
            case WIN_RATE: 
                return "GOLDEN_HELMET";
            case EVOLUTION:
                return "END_CRYSTAL";
            case SKILL_COUNT: 
                return "ENCHANTING_TABLE";
            case ACTIVE_TIME: 
                return "COMPASS";
            case STATUS:
                return "PAINTING";
            default: 
                return "PAPER";
        }
    }

    /**
     * 문자열로 SortType 찾기
     *
     * @param name 이름
     * @return SortType 또는 null
     */
    public static SortType fromString(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String upperName = name.toUpperCase().trim().replace(" ", "_");

        // 영어 이름으로 찾기
        try {
            return SortType.valueOf(upperName);
        } catch (IllegalArgumentException ignored) {
        }

        // 한글 이름으로 찾기
        for (SortType type : values()) {
            if (type.getDisplayName().equals(name)) {
                return type;
            }
        }

        return null;
    }

    /**
     * 기본 정렬 타입 반환
     */
    public static SortType getDefault() {
        return LEVEL;
    }

    /**
     * 주요 정렬 타입 목록 반환
     */
    public static SortType[] getMainSortTypes() {
        return new SortType[]{
            LEVEL, NAME, RARITY, ACQUIRED_DATE, TYPE, POWER
        };
    }

    /**
     * 모든 정렬 타입의 표시 이름 목록 반환
     */
    public static String[] getDisplayNames() {
        SortType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getDisplayName();
        }
        return names;
    }

    /**
     * 케어 관련 정렬 타입 목록 반환
     */
    public static SortType[] getCareSortTypes() {
        return new SortType[]{
            HAPPINESS_ASC, HUNGER_ASC, HEALTH
        };
    }

    /**
     * 전투 관련 정렬 타입 목록 반환
     */
    public static SortType[] getCombatSortTypes() {
        return new SortType[]{
            POWER, LEVEL, KILLS, WIN_RATE
        };
    }

    /**
     * 수집 관련 정렬 타입 목록 반환
     */
    public static SortType[] getCollectionSortTypes() {
        return new SortType[]{
            RARITY, SPECIES, ACQUIRED_DATE, EVOLUTION
        };
    }
}