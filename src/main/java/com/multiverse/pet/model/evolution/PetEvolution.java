package com.multiverse.pet.model.evolution;

import com.multiverse.pet.model.Pet;
import com.multiverse.pet.model.PetRarity;

import java.util.*;

/**
 * 펫 진화 데이터 클래스
 * 펫의 진화 정보와 조건을 정의
 */
public class PetEvolution {

    // 기본 정보
    private String evolutionId;
    private String name;
    private String description;
    private EvolutionType type;

    // 진화 경로
    private String fromSpeciesId;
    private String toSpeciesId;
    private int fromStage;
    private int toStage;

    // 조건
    private int requiredLevel;
    private List<ItemRequirement> requiredItems;
    private Map<String, Double> requiredStats;
    private String requiredQuest;
    private List<String> requiredSkills;
    private int requiredKills;
    private int requiredBattleWins;
    private double requiredHappiness;

    // 시간 조건
    private String requiredTimeOfDay;   // DAY, NIGHT, ANY
    private String requiredWeather;     // CLEAR, RAIN, THUNDER, ANY
    private String requiredBiome;       // 바이옴 이름 또는 ANY

    // 진화 효과
    private Map<String, Double> statBonuses;
    private List<String> newSkills;
    private double statMultiplier;
    private PetRarity newRarity;

    // 외형 변화
    private String newCustomModelId;
    private String newEntityType;

    // 진화 비용
    private double goldCost;
    private int expCost;

    // 진화 확률 (선택적)
    private double successChance;       // 100이면 확정
    private boolean consumeItemsOnFail;

    // 분기 진화 정보
    private List<String> alternativeEvolutions;

    // 활성화 상태
    private boolean enabled;

    /**
     * 기본 생성자
     */
    public PetEvolution() {
        this.requiredItems = new ArrayList<>();
        this.requiredStats = new HashMap<>();
        this.requiredSkills = new ArrayList<>();
        this.statBonuses = new HashMap<>();
        this.newSkills = new ArrayList<>();
        this.alternativeEvolutions = new ArrayList<>();
        this.requiredLevel = 1;
        this.fromStage = 1;
        this. toStage = 2;
        this. statMultiplier = 1.0;
        this.goldCost = 0;
        this. expCost = 0;
        this.successChance = 100.0;
        this.consumeItemsOnFail = false;
        this.requiredTimeOfDay = "ANY";
        this.requiredWeather = "ANY";
        this.requiredBiome = "ANY";
        this.requiredKills = 0;
        this.requiredBattleWins = 0;
        this.requiredHappiness = 0;
        this.enabled = true;
        this.type = EvolutionType.NORMAL;
    }

    /**
     * 전체 생성자
     */
    public PetEvolution(String evolutionId, String fromSpeciesId, String toSpeciesId,
                        int fromStage, int toStage, int requiredLevel) {
        this();
        this.evolutionId = evolutionId;
        this.fromSpeciesId = fromSpeciesId;
        this. toSpeciesId = toSpeciesId;
        this.fromStage = fromStage;
        this.toStage = toStage;
        this.requiredLevel = requiredLevel;
    }

    // ===== 조건 확인 메서드 =====

    /**
     * 모든 진화 조건 충족 여부 확인
     *
     * @param pet 진화할 펫
     * @param playerItems 플레이어 보유 아이템 (아이템ID -> 수량)
     * @param playerGold 플레이어 보유 골드
     * @param completedQuests 완료한 퀘스트 목록
     * @param currentBiome 현재 바이옴
     * @param isDay 낮인지 여부
     * @param weather 현재 날씨
     * @return 모든 조건 충족 여부
     */
    public boolean canEvolve(Pet pet, Map<String, Integer> playerItems, 
                             double playerGold, List<String> completedQuests,
                             String currentBiome, boolean isDay, String weather) {
        if (!enabled) return false;
        
        // 레벨 조건
        if (pet.getLevel() < requiredLevel) return false;
        
        // 진화 단계 조건
        if (pet.getEvolutionStage() != fromStage) return false;
        
        // 종족 조건
        if (!pet.getSpeciesId().equals(fromSpeciesId)) return false;
        
        // 스탯 조건
        if (!checkStatRequirements(pet)) return false;
        
        // 아이템 조건
        if (!checkItemRequirements(playerItems)) return false;
        
        // 골드 조건
        if (playerGold < goldCost) return false;
        
        // 퀘스트 조건
        if (requiredQuest != null && !requiredQuest.isEmpty()) {
            if (! completedQuests. contains(requiredQuest)) return false;
        }
        
        // 스킬 조건
        if (!checkSkillRequirements(pet)) return false;
        
        // 킬 카운트 조건
        if (pet.getKillCount() < requiredKills) return false;
        
        // 배틀 승리 조건
        if (pet.getBattleWins() < requiredBattleWins) return false;
        
        // 행복도 조건
        if (pet.getHappiness() < requiredHappiness) return false;
        
        // 시간 조건
        if (!checkTimeRequirement(isDay)) return false;
        
        // 날씨 조건
        if (!checkWeatherRequirement(weather)) return false;
        
        // 바이옴 조건
        if (!checkBiomeRequirement(currentBiome)) return false;
        
        return true;
    }

    /**
     * 스탯 조건 확인
     */
    private boolean checkStatRequirements(Pet pet) {
        for (Map.Entry<String, Double> entry : requiredStats.entrySet()) {
            double petStat = pet.getTotalStat(entry.getKey());
            if (petStat < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 아이템 조건 확인
     */
    private boolean checkItemRequirements(Map<String, Integer> playerItems) {
        for (ItemRequirement req : requiredItems) {
            int playerAmount = playerItems.getOrDefault(req. getItemId(), 0);
            if (playerAmount < req.getAmount()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 스킬 조건 확인
     */
    private boolean checkSkillRequirements(Pet pet) {
        for (String skillId : requiredSkills) {
            if (! pet.hasSkill(skillId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 시간 조건 확인
     */
    private boolean checkTimeRequirement(boolean isDay) {
        if ("ANY".equalsIgnoreCase(requiredTimeOfDay)) return true;
        if ("DAY".equalsIgnoreCase(requiredTimeOfDay)) return isDay;
        if ("NIGHT".equalsIgnoreCase(requiredTimeOfDay)) return !isDay;
        return true;
    }

    /**
     * 날씨 조건 확인
     */
    private boolean checkWeatherRequirement(String weather) {
        if ("ANY".equalsIgnoreCase(requiredWeather)) return true;
        return requiredWeather.equalsIgnoreCase(weather);
    }

    /**
     * 바이옴 조건 확인
     */
    private boolean checkBiomeRequirement(String currentBiome) {
        if ("ANY".equalsIgnoreCase(requiredBiome)) return true;
        return requiredBiome.equalsIgnoreCase(currentBiome);
    }

    /**
     * 충족되지 않은 조건 목록 반환
     */
    public List<String> getUnmetConditions(Pet pet, Map<String, Integer> playerItems,
                                           double playerGold, List<String> completedQuests,
                                           String currentBiome, boolean isDay, String weather) {
        List<String> unmet = new ArrayList<>();
        
        if (!enabled) {
            unmet. add("이 진화는 현재 비활성화되어 있습니다.");
            return unmet;
        }
        
        if (pet.getLevel() < requiredLevel) {
            unmet.add("레벨 " + requiredLevel + " 필요 (현재:  " + pet.getLevel() + ")");
        }
        
        if (pet.getEvolutionStage() != fromStage) {
            unmet.add("진화 단계 " + fromStage + " 필요 (현재: " + pet.getEvolutionStage() + ")");
        }
        
        if (! pet.getSpeciesId().equals(fromSpeciesId)) {
            unmet. add("종족이 맞지 않습니다.");
        }
        
        for (Map.Entry<String, Double> entry : requiredStats. entrySet()) {
            double petStat = pet.getTotalStat(entry.getKey());
            if (petStat < entry.getValue()) {
                unmet.add(entry.getKey() + " " + entry.getValue() + " 필요 (현재: " + petStat + ")");
            }
        }
        
        for (ItemRequirement req : requiredItems) {
            int playerAmount = playerItems.getOrDefault(req.getItemId(), 0);
            if (playerAmount < req.getAmount()) {
                unmet.add(req.getItemId() + " " + req.getAmount() + "개 필요 (보유: " + playerAmount + ")");
            }
        }
        
        if (playerGold < goldCost) {
            unmet.add("골드 " + goldCost + " 필요 (보유: " + playerGold + ")");
        }
        
        if (requiredQuest != null && !requiredQuest.isEmpty()) {
            if (!completedQuests.contains(requiredQuest)) {
                unmet.add("퀘스트 '" + requiredQuest + "' 완료 필요");
            }
        }
        
        for (String skillId :  requiredSkills) {
            if (! pet.hasSkill(skillId)) {
                unmet. add("스킬 '" + skillId + "' 습득 필요");
            }
        }
        
        if (pet.getKillCount() < requiredKills) {
            unmet.add("킬 카운트 " + requiredKills + " 필요 (현재: " + pet.getKillCount() + ")");
        }
        
        if (pet.getBattleWins() < requiredBattleWins) {
            unmet.add("배틀 승리 " + requiredBattleWins + " 필요 (현재: " + pet. getBattleWins() + ")");
        }
        
        if (pet. getHappiness() < requiredHappiness) {
            unmet.add("행복도 " + requiredHappiness + " 필요 (현재: " + pet. getHappiness() + ")");
        }
        
        if (!checkTimeRequirement(isDay)) {
            unmet.add("시간대:  " + requiredTimeOfDay + " 필요");
        }
        
        if (!checkWeatherRequirement(weather)) {
            unmet.add("날씨: " + requiredWeather + " 필요");
        }
        
        if (! checkBiomeRequirement(currentBiome)) {
            unmet.add("바이옴:  " + requiredBiome + " 필요");
        }
        
        return unmet;
    }

    /**
     * 진화 성공 여부 굴림
     */
    public boolean rollSuccess() {
        if (successChance >= 100.0) return true;
        return Math.random() * 100 < successChance;
    }

    // ===== 요구 아이템 관련 =====

    /**
     * 요구 아이템 추가
     */
    public void addRequiredItem(String itemId, int amount) {
        requiredItems.add(new ItemRequirement(itemId, amount));
    }

    /**
     * 요구 스탯 추가
     */
    public void addRequiredStat(String statName, double value) {
        requiredStats.put(statName, value);
    }

    /**
     * 요구 스킬 추가
     */
    public void addRequiredSkill(String skillId) {
        requiredSkills.add(skillId);
    }

    /**
     * 스탯 보너스 추가
     */
    public void addStatBonus(String statName, double value) {
        statBonuses.put(statName, value);
    }

    /**
     * 새 스킬 추가
     */
    public void addNewSkill(String skillId) {
        newSkills.add(skillId);
    }

    /**
     * 대체 진화 추가
     */
    public void addAlternativeEvolution(String evolutionId) {
        alternativeEvolutions.add(evolutionId);
    }

    // ===== Getter/Setter =====

    public String getEvolutionId() {
        return evolutionId;
    }

    public void setEvolutionId(String evolutionId) {
        this.evolutionId = evolutionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this. name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EvolutionType getType() {
        return type;
    }

    public void setType(EvolutionType type) {
        this.type = type;
    }

    public String getFromSpeciesId() {
        return fromSpeciesId;
    }

    public void setFromSpeciesId(String fromSpeciesId) {
        this.fromSpeciesId = fromSpeciesId;
    }

    public String getToSpeciesId() {
        return toSpeciesId;
    }

    public void setToSpeciesId(String toSpeciesId) {
        this. toSpeciesId = toSpeciesId;
    }

    public int getFromStage() {
        return fromStage;
    }

    public void setFromStage(int fromStage) {
        this.fromStage = fromStage;
    }

    public int getToStage() {
        return toStage;
    }

    public void setToStage(int toStage) {
        this. toStage = toStage;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this. requiredLevel = requiredLevel;
    }

    public List<ItemRequirement> getRequiredItems() {
        return requiredItems;
    }

    public void setRequiredItems(List<ItemRequirement> requiredItems) {
        this. requiredItems = requiredItems;
    }

    public Map<String, Double> getRequiredStats() {
        return requiredStats;
    }

    public void setRequiredStats(Map<String, Double> requiredStats) {
        this. requiredStats = requiredStats;
    }

    public String getRequiredQuest() {
        return requiredQuest;
    }

    public void setRequiredQuest(String requiredQuest) {
        this.requiredQuest = requiredQuest;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public int getRequiredKills() {
        return requiredKills;
    }

    public void setRequiredKills(int requiredKills) {
        this.requiredKills = requiredKills;
    }

    public int getRequiredBattleWins() {
        return requiredBattleWins;
    }

    public void setRequiredBattleWins(int requiredBattleWins) {
        this. requiredBattleWins = requiredBattleWins;
    }

    public double getRequiredHappiness() {
        return requiredHappiness;
    }

    public void setRequiredHappiness(double requiredHappiness) {
        this.requiredHappiness = requiredHappiness;
    }

    public String getRequiredTimeOfDay() {
        return requiredTimeOfDay;
    }

    public void setRequiredTimeOfDay(String requiredTimeOfDay) {
        this.requiredTimeOfDay = requiredTimeOfDay;
    }

    public String getRequiredWeather() {
        return requiredWeather;
    }

    public void setRequiredWeather(String requiredWeather) {
        this.requiredWeather = requiredWeather;
    }

    public String getRequiredBiome() {
        return requiredBiome;
    }

    public void setRequiredBiome(String requiredBiome) {
        this.requiredBiome = requiredBiome;
    }

    public Map<String, Double> getStatBonuses() {
        return statBonuses;
    }

    public void setStatBonuses(Map<String, Double> statBonuses) {
        this.statBonuses = statBonuses;
    }

    public List<String> getNewSkills() {
        return newSkills;
    }

    public void setNewSkills(List<String> newSkills) {
        this.newSkills = newSkills;
    }

    public double getStatMultiplier() {
        return statMultiplier;
    }

    public void setStatMultiplier(double statMultiplier) {
        this.statMultiplier = statMultiplier;
    }

    public PetRarity getNewRarity() {
        return newRarity;
    }

    public void setNewRarity(PetRarity newRarity) {
        this.newRarity = newRarity;
    }

    public String getNewCustomModelId() {
        return newCustomModelId;
    }

    public void setNewCustomModelId(String newCustomModelId) {
        this.newCustomModelId = newCustomModelId;
    }

    public String getNewEntityType() {
        return newEntityType;
    }

    public void setNewEntityType(String newEntityType) {
        this.newEntityType = newEntityType;
    }

    public double getGoldCost() {
        return goldCost;
    }

    public void setGoldCost(double goldCost) {
        this. goldCost = goldCost;
    }

    public int getExpCost() {
        return expCost;
    }

    public void setExpCost(int expCost) {
        this. expCost = expCost;
    }

    public double getSuccessChance() {
        return successChance;
    }

    public void setSuccessChance(double successChance) {
        this.successChance = successChance;
    }

    public boolean isConsumeItemsOnFail() {
        return consumeItemsOnFail;
    }

    public void setConsumeItemsOnFail(boolean consumeItemsOnFail) {
        this.consumeItemsOnFail = consumeItemsOnFail;
    }

    public List<String> getAlternativeEvolutions() {
        return alternativeEvolutions;
    }

    public void setAlternativeEvolutions(List<String> alternativeEvolutions) {
        this.alternativeEvolutions = alternativeEvolutions;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetEvolution that = (PetEvolution) o;
        return Objects.equals(evolutionId, that.evolutionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(evolutionId);
    }

    @Override
    public String toString() {
        return "PetEvolution{" +
                "evolutionId='" + evolutionId + '\'' +
                ", fromSpeciesId='" + fromSpeciesId + '\'' +
                ", toSpeciesId='" + toSpeciesId + '\'' +
                ", type=" + type +
                ", requiredLevel=" + requiredLevel +
                '}';
    }

    // ===== 내부 클래스 =====

    /**
     * 아이템 요구 조건
     */
    public static class ItemRequirement {
        private String itemId;
        private int amount;

        public ItemRequirement() {}

        public ItemRequirement(String itemId, int amount) {
            this.itemId = itemId;
            this.amount = amount;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}