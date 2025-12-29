package com.multiverse.pet.model.breeding;

import com.multiverse.pet.model.Pet;
import com.multiverse.pet.model.PetRarity;

import java.util.*;

/**
 * 펫 유전 데이터 클래스
 * 교배 시 유전되는 정보와 변이를 정의
 */
public class PetGenetics {

    // 상속된 스탯
    private Map<String, Double> inheritedStats;

    // 상속된 스킬
    private List<String> inheritedSkills;

    // 변이 정보
    private boolean isMutant;
    private String mutationType;
    private double mutationBonus;

    // 유전 품질 (0.0 ~ 1.0)
    private double geneticQuality;

    // 부모 정보
    private UUID parent1Id;
    private UUID parent2Id;
    private String parent1Species;
    private String parent2Species;
    private PetRarity parent1Rarity;
    private PetRarity parent2Rarity;

    // 세대 정보
    private int generation;

    // 유전 특성
    private List<GeneticTrait> traits;

    // 잠재력
    private Map<String, Double> potentialStats;
    private double overallPotential;

    // 숨겨진 능력
    private String hiddenAbility;
    private boolean hiddenAbilityUnlocked;

    /**
     * 기본 생성자
     */
    public PetGenetics() {
        this.inheritedStats = new HashMap<>();
        this.inheritedSkills = new ArrayList<>();
        this.traits = new ArrayList<>();
        this.potentialStats = new HashMap<>();
        this.isMutant = false;
        this. mutationBonus = 0.0;
        this.geneticQuality = 0.5;
        this.generation = 1;
        this. overallPotential = 0.5;
        this.hiddenAbilityUnlocked = false;
    }

    /**
     * 부모 기반 생성자
     */
    public PetGenetics(Pet parent1, Pet parent2) {
        this();
        this.parent1Id = parent1.getPetId();
        this.parent2Id = parent2.getPetId();
        this.parent1Species = parent1.getSpeciesId();
        this.parent2Species = parent2.getSpeciesId();
        this.parent1Rarity = parent1.getRarity();
        this.parent2Rarity = parent2.getRarity();

        // 세대 계산
        int p1Gen = parent1.getGenetics() != null ? parent1.getGenetics().getGeneration() : 0;
        int p2Gen = parent2.getGenetics() != null ? parent2.getGenetics().getGeneration() : 0;
        this.generation = Math.max(p1Gen, p2Gen) + 1;

        // 스탯 상속 계산
        calculateInheritedStats(parent1, parent2);

        // 스킬 상속 계산
        calculateInheritedSkills(parent1, parent2);

        // 유전 품질 계산
        calculateGeneticQuality(parent1, parent2);

        // 잠재력 계산
        calculatePotential(parent1, parent2);
    }

    // ===== 상속 계산 메서드 =====

    /**
     * 스탯 상속 계산
     */
    private void calculateInheritedStats(Pet parent1, Pet parent2) {
        Set<String> allStats = new HashSet<>();
        allStats. addAll(parent1.getBaseStats().keySet());
        allStats. addAll(parent2.getBaseStats().keySet());

        Random random = new Random();

        for (String statName : allStats) {
            double stat1 = parent1.getBaseStats().getOrDefault(statName, 0.0);
            double stat2 = parent2.getBaseStats().getOrDefault(statName, 0.0);

            // 기본:  부모 평균
            double avgStat = (stat1 + stat2) / 2.0;

            // 변동 범위:  ±10%
            double variation = avgStat * 0.1;
            double finalStat = avgStat + (random.nextDouble() * 2 - 1) * variation;

            // 부모 중 높은 값의 10% 확률로 상속
            if (random.nextDouble() < 0.1) {
                finalStat = Math.max(stat1, stat2);
            }

            inheritedStats.put(statName, Math.max(0, finalStat));
        }
    }

    /**
     * 스킬 상속 계산
     */
    private void calculateInheritedSkills(Pet parent1, Pet parent2) {
        Random random = new Random();

        // 각 부모의 스킬에서 랜덤 선택
        List<String> parent1Skills = new ArrayList<>();
        List<String> parent2Skills = new ArrayList<>();

        parent1.getSkills().forEach(s -> parent1Skills. add(s. getSkillId()));
        parent2.getSkills().forEach(s -> parent2Skills.add(s. getSkillId()));

        // 부모1에서 50% 확률로 각 스킬 상속
        for (String skillId : parent1Skills) {
            if (random.nextDouble() < 0.5 && ! inheritedSkills.contains(skillId)) {
                inheritedSkills.add(skillId);
            }
        }

        // 부모2에서 50% 확률로 각 스킬 상속
        for (String skillId : parent2Skills) {
            if (random.nextDouble() < 0.5 && !inheritedSkills.contains(skillId)) {
                inheritedSkills.add(skillId);
            }
        }

        // 최대 3개 스킬로 제한
        while (inheritedSkills.size() > 3) {
            inheritedSkills.remove(random. nextInt(inheritedSkills.size()));
        }
    }

    /**
     * 유전 품질 계산
     */
    private void calculateGeneticQuality(Pet parent1, Pet parent2) {
        // 부모 희귀도 기반
        double rarity1 = parent1.getRarity().ordinal() / (double) (PetRarity.values().length - 1);
        double rarity2 = parent2.getRarity().ordinal() / (double) (PetRarity.values().length - 1);

        // 부모 레벨 기반
        double level1 = parent1.getLevel() / 100.0;
        double level2 = parent2.getLevel() / 100.0;

        // 부모 행복도 기반
        double happiness1 = parent1.getHappiness() / 100.0;
        double happiness2 = parent2.getHappiness() / 100.0;

        // 종합 계산
        this.geneticQuality = (rarity1 + rarity2) * 0.3 +
                              (level1 + level2) * 0.2 +
                              (happiness1 + happiness2) * 0.2;

        // 세대 보너스 (세대가 높을수록 약간의 보너스)
        this.geneticQuality += Math.min(0.1, generation * 0.01);

        // 0~1 범위로 제한
        this. geneticQuality = Math.max(0, Math.min(1, geneticQuality));
    }

    /**
     * 잠재력 계산
     */
    private void calculatePotential(Pet parent1, Pet parent2) {
        Set<String> allStats = new HashSet<>();
        allStats. addAll(parent1.getBaseStats().keySet());
        allStats.addAll(parent2.getBaseStats().keySet());

        Random random = new Random();
        double totalPotential = 0;

        for (String statName : allStats) {
            double stat1 = parent1.getBaseStats().getOrDefault(statName, 0.0);
            double stat2 = parent2.getBaseStats().getOrDefault(statName, 0.0);

            // 잠재력 = 부모 중 높은 값 * (1.0 ~ 1.5)
            double maxStat = Math. max(stat1, stat2);
            double potential = maxStat * (1.0 + random.nextDouble() * 0.5);

            potentialStats.put(statName, potential);
            totalPotential += potential;
        }

        // 전체 잠재력 정규화
        if (! potentialStats.isEmpty()) {
            this.overallPotential = totalPotential / potentialStats.size();
        }
    }

    // ===== 변이 관련 메서드 =====

    /**
     * 변이 적용
     */
    public void applyMutation(String type, double bonus) {
        this.isMutant = true;
        this. mutationType = type;
        this.mutationBonus = bonus;

        // 변이 시 스탯 보너스 적용
        for (String statName : inheritedStats.keySet()) {
            double currentStat = inheritedStats.get(statName);
            inheritedStats.put(statName, currentStat * (1 + bonus));
        }

        // 변이 시 유전 품질 증가
        this.geneticQuality = Math.min(1.0, geneticQuality + 0.2);
    }

    /**
     * 변이 확률 계산
     *
     * @param baseMutationChance 기본 변이 확률 (%)
     * @return 최종 변이 확률 (%)
     */
    public double calculateMutationChance(double baseMutationChance) {
        // 세대가 높을수록 변이 확률 증가
        double generationBonus = generation * 0.5;

        // 유전 품질이 높을수록 변이 확률 증가
        double qualityBonus = geneticQuality * 2;

        return baseMutationChance + generationBonus + qualityBonus;
    }

    /**
     * 랜덤 변이 타입 생성
     */
    public static String generateRandomMutationType() {
        String[] mutations = {
            "강화", "속도", "체력", "공격", "방어",
            "치명", "흡혈", "재생", "은신", "분노"
        };
        return mutations[new Random().nextInt(mutations.length)];
    }

    /**
     * 변이 타입에 따른 보너스 값 반환
     */
    public static double getMutationBonusValue(String mutationType) {
        switch (mutationType) {
            case "강화":  return 0.5;
            case "속도": return 0.3;
            case "체력": return 0.4;
            case "공격": return 0.35;
            case "방어": return 0.35;
            case "치명": return 0.25;
            case "흡혈":  return 0.2;
            case "재생": return 0.3;
            case "은신": return 0.15;
            case "분노": return 0.4;
            default:  return 0.2;
        }
    }

    // ===== 특성 관련 메서드 =====

    /**
     * 특성 추가
     */
    public void addTrait(GeneticTrait trait) {
        if (! hasTrait(trait. getTraitId())) {
            traits.add(trait);
        }
    }

    /**
     * 특성 제거
     */
    public void removeTrait(String traitId) {
        traits.removeIf(t -> t.getTraitId().equals(traitId));
    }

    /**
     * 특성 보유 여부
     */
    public boolean hasTrait(String traitId) {
        return traits.stream().anyMatch(t -> t. getTraitId().equals(traitId));
    }

    /**
     * 특성 가져오기
     */
    public GeneticTrait getTrait(String traitId) {
        return traits.stream()
                . filter(t -> t.getTraitId().equals(traitId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 랜덤 특성 생성
     */
    public void generateRandomTraits(int count) {
        Random random = new Random();
        String[] traitTypes = {"힘", "민첩", "지능", "체력", "행운"};

        for (int i = 0; i < count && traits.size() < 5; i++) {
            String type = traitTypes[random.nextInt(traitTypes.length)];
            String traitId = type + "_trait_" + random.nextInt(100);
            double value = 0.05 + random.nextDouble() * 0.15; // 5~20% 보너스

            GeneticTrait trait = new GeneticTrait(traitId, type + " 특성", value);
            trait.setDescription(type + " 관련 능력이 " + String.format("%.1f%%", value * 100) + " 증가합니다.");
            addTrait(trait);
        }
    }

    // ===== 숨겨진 능력 관련 =====

    /**
     * 숨겨진 능력 해금
     */
    public void unlockHiddenAbility() {
        this. hiddenAbilityUnlocked = true;
    }

    /**
     * 숨겨진 능력 설정
     */
    public void setHiddenAbility(String ability) {
        this.hiddenAbility = ability;
    }

    /**
     * 숨겨진 능력 해금 확률 계산
     */
    public double getHiddenAbilityUnlockChance() {
        // 기본 5% + 세대당 1% + 품질 * 10%
        return 5 + generation + (geneticQuality * 10);
    }

    // ===== 유틸리티 메서드 =====

    /**
     * 유전 정보 요약
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("세대: ").append(generation).append("\n");
        sb.append("유전 품질: ").append(String.format("%.1f%%", geneticQuality * 100)).append("\n");

        if (isMutant) {
            sb. append("변이:  ").append(mutationType).append(" (+")
              .append(String.format("%.1f%%", mutationBonus * 100)).append(")\n");
        }

        if (!inheritedStats.isEmpty()) {
            sb.append("상속 스탯:\n");
            for (Map.Entry<String, Double> entry : inheritedStats. entrySet()) {
                sb.append("  ").append(entry.getKey()).append(": ")
                  .append(String.format("%.1f", entry.getValue())).append("\n");
            }
        }

        if (!inheritedSkills.isEmpty()) {
            sb.append("상속 스킬:  ").append(String.join(", ", inheritedSkills)).append("\n");
        }

        if (! traits.isEmpty()) {
            sb.append("특성:\n");
            for (GeneticTrait trait : traits) {
                sb. append("  - ").append(trait.getName()).append("\n");
            }
        }

        if (hiddenAbility != null) {
            sb.append("숨겨진 능력: ").append(hiddenAbility);
            if (! hiddenAbilityUnlocked) {
                sb.append(" (미해금)");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * 유전 품질 등급 반환
     */
    public String getQualityGrade() {
        if (geneticQuality >= 0.9) return "S";
        if (geneticQuality >= 0.8) return "A";
        if (geneticQuality >= 0.6) return "B";
        if (geneticQuality >= 0.4) return "C";
        if (geneticQuality >= 0.2) return "D";
        return "F";
    }

    /**
     * 유전 품질 색상 반환
     */
    public String getQualityColor() {
        if (geneticQuality >= 0.9) return "&6";
        if (geneticQuality >= 0.8) return "&5";
        if (geneticQuality >= 0.6) return "&9";
        if (geneticQuality >= 0.4) return "&a";
        if (geneticQuality >= 0.2) return "&e";
        return "&7";
    }

    // ===== Getter/Setter =====

    public Map<String, Double> getInheritedStats() {
        return inheritedStats;
    }

    public void setInheritedStats(Map<String, Double> inheritedStats) {
        this.inheritedStats = inheritedStats;
    }

    public double getInheritedStat(String statName) {
        return inheritedStats.getOrDefault(statName, 0.0);
    }

    public void setInheritedStat(String statName, double value) {
        inheritedStats.put(statName, value);
    }

    public List<String> getInheritedSkills() {
        return inheritedSkills;
    }

    public void setInheritedSkills(List<String> inheritedSkills) {
        this.inheritedSkills = inheritedSkills;
    }

    public boolean isMutant() {
        return isMutant;
    }

    public void setMutant(boolean mutant) {
        isMutant = mutant;
    }

    public String getMutationType() {
        return mutationType;
    }

    public void setMutationType(String mutationType) {
        this.mutationType = mutationType;
    }

    public double getMutationBonus() {
        return mutationBonus;
    }

    public void setMutationBonus(double mutationBonus) {
        this.mutationBonus = mutationBonus;
    }

    public double getGeneticQuality() {
        return geneticQuality;
    }

    public void setGeneticQuality(double geneticQuality) {
        this.geneticQuality = geneticQuality;
    }

    public UUID getParent1Id() {
        return parent1Id;
    }

    public void setParent1Id(UUID parent1Id) {
        this.parent1Id = parent1Id;
    }

    public UUID getParent2Id() {
        return parent2Id;
    }

    public void setParent2Id(UUID parent2Id) {
        this.parent2Id = parent2Id;
    }

    public String getParent1Species() {
        return parent1Species;
    }

    public void setParent1Species(String parent1Species) {
        this.parent1Species = parent1Species;
    }

    public String getParent2Species() {
        return parent2Species;
    }

    public void setParent2Species(String parent2Species) {
        this.parent2Species = parent2Species;
    }

    public PetRarity getParent1Rarity() {
        return parent1Rarity;
    }

    public void setParent1Rarity(PetRarity parent1Rarity) {
        this.parent1Rarity = parent1Rarity;
    }

    public PetRarity getParent2Rarity() {
        return parent2Rarity;
    }

    public void setParent2Rarity(PetRarity parent2Rarity) {
        this.parent2Rarity = parent2Rarity;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this. generation = generation;
    }

    public List<GeneticTrait> getTraits() {
        return traits;
    }

    public void setTraits(List<GeneticTrait> traits) {
        this.traits = traits;
    }

    public Map<String, Double> getPotentialStats() {
        return potentialStats;
    }

    public void setPotentialStats(Map<String, Double> potentialStats) {
        this.potentialStats = potentialStats;
    }

    public double getOverallPotential() {
        return overallPotential;
    }

    public void setOverallPotential(double overallPotential) {
        this.overallPotential = overallPotential;
    }

    public String getHiddenAbility() {
        return hiddenAbility;
    }

    public boolean isHiddenAbilityUnlocked() {
        return hiddenAbilityUnlocked;
    }

    public void setHiddenAbilityUnlocked(boolean hiddenAbilityUnlocked) {
        this.hiddenAbilityUnlocked = hiddenAbilityUnlocked;
    }

    @Override
    public String toString() {
        return "PetGenetics{" +
                "generation=" + generation +
                ", quality=" + String.format("%.1f%%", geneticQuality * 100) +
                ", isMutant=" + isMutant +
                ", traits=" + traits. size() +
                '}';
    }

    // ===== 내부 클래스 =====

    /**
     * 유전 특성
     */
    public static class GeneticTrait {
        private String traitId;
        private String name;
        private String description;
        private double value;
        private boolean dominant;

        public GeneticTrait() {}

        public GeneticTrait(String traitId, String name, double value) {
            this. traitId = traitId;
            this.name = name;
            this. value = value;
            this.dominant = false;
        }

        public String getTraitId() {
            return traitId;
        }

        public void setTraitId(String traitId) {
            this.traitId = traitId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public boolean isDominant() {
            return dominant;
        }

        public void setDominant(boolean dominant) {
            this.dominant = dominant;
        }
    }
}