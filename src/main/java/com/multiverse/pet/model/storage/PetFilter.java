package com.multiverse.pet. model. storage;

import com.multiverse.pet.model.Pet;
import com.multiverse.pet.model. PetRarity;
import com.multiverse.pet.model. PetStatus;
import com.multiverse.pet.model. PetType;

import java.util.ArrayList;
import java. util.List;
import java.util. function.Predicate;

/**
 * 펫 필터 데이터 클래스
 * 펫 보관함에서 펫을 필터링하는 조건을 정의
 */
public class PetFilter {

    // 필터 조건
    private PetType type;
    private PetRarity minRarity;
    private PetRarity maxRarity;
    private PetStatus status;
    private Integer minLevel;
    private Integer maxLevel;
    private String speciesId;
    private String nameContains;
    private Boolean hasSkill;
    private String skillId;
    private Boolean canEvolve;
    private Boolean canBreed;
    private Boolean isActive;
    private Double minHappiness;
    private Double minHunger;

    // 고급 필터
    private List<String> includedSpecies;
    private List<String> excludedSpecies;
    private List<PetRarity> includedRarities;
    private List<PetType> includedTypes;

    /**
     * 기본 생성자 (모든 펫 통과)
     */
    public PetFilter() {
        this.includedSpecies = new ArrayList<>();
        this.excludedSpecies = new ArrayList<>();
        this.includedRarities = new ArrayList<>();
        this.includedTypes = new ArrayList<>();
    }

    /**
     * 빌더 패턴 시작
     */
    public static PetFilter builder() {
        return new PetFilter();
    }

    // ===== 빌더 메서드 =====

    /**
     * 타입 필터 설정
     */
    public PetFilter withType(PetType type) {
        this.type = type;
        return this;
    }

    /**
     * 최소 희귀도 설정
     */
    public PetFilter withMinRarity(PetRarity rarity) {
        this.minRarity = rarity;
        return this;
    }

    /**
     * 최대 희귀도 설정
     */
    public PetFilter withMaxRarity(PetRarity rarity) {
        this. maxRarity = rarity;
        return this;
    }

    /**
     * 희귀도 범위 설정
     */
    public PetFilter withRarityRange(PetRarity min, PetRarity max) {
        this.minRarity = min;
        this.maxRarity = max;
        return this;
    }

    /**
     * 상태 필터 설정
     */
    public PetFilter withStatus(PetStatus status) {
        this.status = status;
        return this;
    }

    /**
     * 최소 레벨 설정
     */
    public PetFilter withMinLevel(int level) {
        this.minLevel = level;
        return this;
    }

    /**
     * 최대 레벨 설정
     */
    public PetFilter withMaxLevel(int level) {
        this.maxLevel = level;
        return this;
    }

    /**
     * 레벨 범위 설정
     */
    public PetFilter withLevelRange(int min, int max) {
        this.minLevel = min;
        this.maxLevel = max;
        return this;
    }

    /**
     * 종족 ID 필터 설정
     */
    public PetFilter withSpecies(String speciesId) {
        this.speciesId = speciesId;
        return this;
    }

    /**
     * 이름 포함 필터 설정
     */
    public PetFilter withNameContains(String text) {
        this.nameContains = text;
        return this;
    }

    /**
     * 스킬 보유 필터 설정
     */
    public PetFilter withHasSkill(boolean hasSkill) {
        this.hasSkill = hasSkill;
        return this;
    }

    /**
     * 특정 스킬 보유 필터 설정
     */
    public PetFilter withSkill(String skillId) {
        this. skillId = skillId;
        this.hasSkill = true;
        return this;
    }

    /**
     * 진화 가능 필터 설정
     */
    public PetFilter withCanEvolve(boolean canEvolve) {
        this. canEvolve = canEvolve;
        return this;
    }

    /**
     * 교배 가능 필터 설정
     */
    public PetFilter withCanBreed(boolean canBreed) {
        this.canBreed = canBreed;
        return this;
    }

    /**
     * 활성 상태 필터 설정
     */
    public PetFilter withIsActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    /**
     * 최소 행복도 설정
     */
    public PetFilter withMinHappiness(double happiness) {
        this.minHappiness = happiness;
        return this;
    }

    /**
     * 최소 배고픔 설정
     */
    public PetFilter withMinHunger(double hunger) {
        this.minHunger = hunger;
        return this;
    }

    /**
     * 포함할 종족 목록 추가
     */
    public PetFilter includeSpecies(String... species) {
        for (String s : species) {
            if (! includedSpecies. contains(s)) {
                includedSpecies.add(s);
            }
        }
        return this;
    }

    /**
     * 제외할 종족 목록 추가
     */
    public PetFilter excludeSpecies(String...  species) {
        for (String s :  species) {
            if (! excludedSpecies. contains(s)) {
                excludedSpecies.add(s);
            }
        }
        return this;
    }

    /**
     * 포함할 희귀도 목록 추가
     */
    public PetFilter includeRarities(PetRarity... rarities) {
        for (PetRarity r : rarities) {
            if (!includedRarities. contains(r)) {
                includedRarities.add(r);
            }
        }
        return this;
    }

    /**
     * 포함할 타입 목록 추가
     */
    public PetFilter includeTypes(PetType... types) {
        for (PetType t : types) {
            if (!includedTypes.contains(t)) {
                includedTypes.add(t);
            }
        }
        return this;
    }

    // ===== 필터 적용 메서드 =====

    /**
     * 펫이 필터 조건에 맞는지 확인
     *
     * @param pet 확인할 펫
     * @return 조건 충족 여부
     */
    public boolean matches(Pet pet) {
        if (pet == null) return false;

        // 타입 체크
        if (type != null && pet.getType() != type) {
            return false;
        }

        // 타입 목록 체크
        if (! includedTypes.isEmpty() && !includedTypes.contains(pet.getType())) {
            return false;
        }

        // 희귀도 범위 체크
        if (minRarity != null && pet.getRarity().ordinal() < minRarity.ordinal()) {
            return false;
        }
        if (maxRarity != null && pet.getRarity().ordinal() > maxRarity.ordinal()) {
            return false;
        }

        // 희귀도 목록 체크
        if (!includedRarities.isEmpty() && !includedRarities.contains(pet.getRarity())) {
            return false;
        }

        // 상태 체크
        if (status != null && pet.getStatus() != status) {
            return false;
        }

        // 레벨 범위 체크
        if (minLevel != null && pet. getLevel() < minLevel) {
            return false;
        }
        if (maxLevel != null && pet.getLevel() > maxLevel) {
            return false;
        }

        // 종족 ID 체크
        if (speciesId != null && ! speciesId.isEmpty()) {
            if (! speciesId.equalsIgnoreCase(pet.getSpeciesId())) {
                return false;
            }
        }

        // 종족 포함 목록 체크
        if (!includedSpecies.isEmpty() && !includedSpecies.contains(pet.getSpeciesId())) {
            return false;
        }

        // 종족 제외 목록 체크
        if (!excludedSpecies.isEmpty() && excludedSpecies.contains(pet.getSpeciesId())) {
            return false;
        }

        // 이름 포함 체크
        if (nameContains != null && !nameContains.isEmpty()) {
            if (pet.getPetName() == null || 
                !pet. getPetName().toLowerCase().contains(nameContains.toLowerCase())) {
                return false;
            }
        }

        // 스킬 보유 체크
        if (hasSkill != null) {
            boolean petHasSkills = pet.getSkills() != null && !pet.getSkills().isEmpty();
            if (hasSkill != petHasSkills) {
                return false;
            }
        }

        // 특정 스킬 체크
        if (skillId != null && !skillId.isEmpty()) {
            if (!pet.hasSkill(skillId)) {
                return false;
            }
        }

        // 활성 상태 체크
        if (isActive != null) {
            if (isActive != pet.isActive()) {
                return false;
            }
        }

        // 행복도 체크
        if (minHappiness != null && pet. getHappiness() < minHappiness) {
            return false;
        }

        // 배고픔 체크
        if (minHunger != null && pet.getHunger() < minHunger) {
            return false;
        }

        // 교배 가능 체크
        if (canBreed != null && canBreed) {
            if (pet.getStatus() != PetStatus.STORED || 
                pet.getLevel() < 30 || 
                pet.getHappiness() < 80) {
                return false;
            }
        }

        return true;
    }

    /**
     * 펫 목록 필터링
     *
     * @param pets 펫 목록
     * @return 필터링된 펫 목록
     */
    public List<Pet> filter(List<Pet> pets) {
        List<Pet> result = new ArrayList<>();
        if (pets == null) return result;

        for (Pet pet :  pets) {
            if (matches(pet)) {
                result.add(pet);
            }
        }
        return result;
    }

    /**
     * Predicate로 변환
     */
    public Predicate<Pet> toPredicate() {
        return this::matches;
    }

    // ===== 필터 초기화 =====

    /**
     * 모든 필터 초기화
     */
    public PetFilter clear() {
        this.type = null;
        this.minRarity = null;
        this.maxRarity = null;
        this.status = null;
        this.minLevel = null;
        this.maxLevel = null;
        this. speciesId = null;
        this. nameContains = null;
        this. hasSkill = null;
        this. skillId = null;
        this.canEvolve = null;
        this. canBreed = null;
        this. isActive = null;
        this.minHappiness = null;
        this. minHunger = null;
        this.includedSpecies. clear();
        this.excludedSpecies.clear();
        this.includedRarities.clear();
        this.includedTypes.clear();
        return this;
    }

    /**
     * 필터가 비어있는지 확인
     */
    public boolean isEmpty() {
        return type == null && minRarity == null && maxRarity == null &&
               status == null && minLevel == null && maxLevel == null &&
               speciesId == null && nameContains == null && hasSkill == null &&
               skillId == null && canEvolve == null && canBreed == null &&
               isActive == null && minHappiness == null && minHunger == null &&
               includedSpecies.isEmpty() && excludedSpecies. isEmpty() &&
               includedRarities.isEmpty() && includedTypes.isEmpty();
    }

    // ===== 사전 정의된 필터 =====

    /**
     * 전투 가능한 펫 필터
     */
    public static PetFilter combatReady() {
        return new PetFilter()
                .withStatus(PetStatus. STORED)
                .withMinHunger(20. 0)
                .withMinHappiness(30.0);
    }

    /**
     * 교배 가능한 펫 필터
     */
    public static PetFilter breedingReady() {
        return new PetFilter()
                .withStatus(PetStatus.STORED)
                .withMinLevel(30)
                .withMinHappiness(80.0);
    }

    /**
     * 고급 이상 펫 필터
     */
    public static PetFilter rareOrAbove() {
        return new PetFilter()
                .withMinRarity(PetRarity.RARE);
    }

    /**
     * 전투형 펫 필터
     */
    public static PetFilter combatType() {
        return new PetFilter()
                .withType(PetType. COMBAT);
    }

    /**
     * 채집형 펫 필터
     */
    public static PetFilter gatheringType() {
        return new PetFilter()
                .withType(PetType.GATHERING);
    }

    /**
     * 활성 펫 필터
     */
    public static PetFilter activePets() {
        return new PetFilter()
                .withIsActive(true);
    }

    /**
     * 케어 필요 펫 필터 (배고픔 또는 행복도 낮음)
     */
    public static PetFilter needsCare() {
        // 커스텀 로직이 필요한 경우
        PetFilter filter = new PetFilter();
        // 이 필터는 matches에서 추가 로직 구현 필요
        return filter;
    }

    // ===== 필터 설명 =====

    /**
     * 현재 필터 조건 설명 반환
     */
    public String getDescription() {
        if (isEmpty()) {
            return "모든 펫";
        }

        StringBuilder sb = new StringBuilder();
        List<String> conditions = new ArrayList<>();

        if (type != null) {
            conditions.add("타입:  " + type. getDisplayName());
        }
        if (! includedTypes.isEmpty()) {
            List<String> typeNames = new ArrayList<>();
            for (PetType t : includedTypes) {
                typeNames.add(t. getDisplayName());
            }
            conditions.add("타입: " + String.join(", ", typeNames));
        }
        if (minRarity != null || maxRarity != null) {
            if (minRarity != null && maxRarity != null) {
                conditions.add("희귀도: " + minRarity.getDisplayName() + " ~ " + maxRarity.getDisplayName());
            } else if (minRarity != null) {
                conditions.add("희귀도: " + minRarity. getDisplayName() + " 이상");
            } else {
                conditions. add("희귀도: " + maxRarity.getDisplayName() + " 이하");
            }
        }
        if (status != null) {
            conditions.add("상태: " + status.getDisplayName());
        }
        if (minLevel != null || maxLevel != null) {
            if (minLevel != null && maxLevel != null) {
                conditions.add("레벨: " + minLevel + " ~ " + maxLevel);
            } else if (minLevel != null) {
                conditions.add("레벨: " + minLevel + " 이상");
            } else {
                conditions.add("레벨: " + maxLevel + " 이하");
            }
        }
        if (speciesId != null) {
            conditions.add("종족:  " + speciesId);
        }
        if (nameContains != null) {
            conditions.add("이름 포함: " + nameContains);
        }
        if (isActive != null) {
            conditions.add(isActive ? "활성 펫만" : "비활성 펫만");
        }

        for (int i = 0; i < conditions.size(); i++) {
            sb.append(conditions. get(i));
            if (i < conditions.size() - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    // ===== Getter/Setter =====

    public PetType getType() {
        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public PetRarity getMinRarity() {
        return minRarity;
    }

    public void setMinRarity(PetRarity minRarity) {
        this.minRarity = minRarity;
    }

    public PetRarity getMaxRarity() {
        return maxRarity;
    }

    public void setMaxRarity(PetRarity maxRarity) {
        this. maxRarity = maxRarity;
    }

    public PetStatus getStatus() {
        return status;
    }

    public void setStatus(PetStatus status) {
        this.status = status;
    }

    public Integer getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(Integer minLevel) {
        this.minLevel = minLevel;
    }

    public Integer getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(Integer maxLevel) {
        this. maxLevel = maxLevel;
    }

    public String getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(String speciesId) {
        this.speciesId = speciesId;
    }

    public String getNameContains() {
        return nameContains;
    }

    public void setNameContains(String nameContains) {
        this.nameContains = nameContains;
    }

    public Boolean getHasSkill() {
        return hasSkill;
    }

    public void setHasSkill(Boolean hasSkill) {
        this.hasSkill = hasSkill;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this. skillId = skillId;
    }

    public Boolean getCanEvolve() {
        return canEvolve;
    }

    public void setCanEvolve(Boolean canEvolve) {
        this.canEvolve = canEvolve;
    }

    public Boolean getCanBreed() {
        return canBreed;
    }

    public void setCanBreed(Boolean canBreed) {
        this.canBreed = canBreed;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this. isActive = isActive;
    }

    public Double getMinHappiness() {
        return minHappiness;
    }

    public void setMinHappiness(Double minHappiness) {
        this.minHappiness = minHappiness;
    }

    public Double getMinHunger() {
        return minHunger;
    }

    public void setMinHunger(Double minHunger) {
        this.minHunger = minHunger;
    }

    public List<String> getIncludedSpecies() {
        return includedSpecies;
    }

    public void setIncludedSpecies(List<String> includedSpecies) {
        this.includedSpecies = includedSpecies;
    }

    public List<String> getExcludedSpecies() {
        return excludedSpecies;
    }

    public void setExcludedSpecies(List<String> excludedSpecies) {
        this.excludedSpecies = excludedSpecies;
    }

    public List<PetRarity> getIncludedRarities() {
        return includedRarities;
    }

    public void setIncludedRarities(List<PetRarity> includedRarities) {
        this.includedRarities = includedRarities;
    }

    public List<PetType> getIncludedTypes() {
        return includedTypes;
    }

    public void setIncludedTypes(List<PetType> includedTypes) {
        this.includedTypes = includedTypes;
    }

    @Override
    public String toString() {
        return "PetFilter{" + getDescription() + "}";
    }
}