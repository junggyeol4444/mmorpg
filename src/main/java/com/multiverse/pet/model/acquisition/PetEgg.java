package com.multiverse.pet.model.acquisition;

import com.multiverse.pet.model.PetRarity;
import org.bukkit.Material;
import org. bukkit.inventory. ItemStack;
import org.bukkit.inventory.meta. ItemMeta;

import java.util.*;

/**
 * 펫 알 데이터 클래스
 * 펫 알의 정보와 부화 조건을 정의
 */
public class PetEgg {

    // 기본 정보
    private UUID eggId;
    private String eggType;
    private String name;
    private String description;

    // 소유 정보
    private UUID ownerId;

    // 부화 정보
    private String speciesId;
    private List<String> possibleSpecies;
    private PetRarity guaranteedRarity;
    private Map<PetRarity, Double> rarityChances;

    // 부화 시간
    private long hatchTime;             // 부화 필요 시간 (밀리초)
    private long acquiredTime;          // 획득 시간
    private long hatchStartTime;        // 부화 시작 시간 (0이면 시작 안함)
    private boolean isHatching;

    // 부화 조건
    private int requiredPlayerLevel;
    private List<String> requiredBiomes;
    private String requiredTimeOfDay;   // DAY, NIGHT, ANY
    private double requiredTemperature; // -1이면 무관

    // 외형
    private Material material;
    private int customModelData;
    private String colorCode;

    // 획득 경로
    private String source;              // 드롭, 상점, 이벤트 등

    // 거래
    private boolean tradeable;
    private double marketValue;

    // 특수 효과
    private boolean isShiny;            // 빛나는 알 (변이 확률 증가)
    private double mutationBonus;       // 추가 변이 확률
    private Map<String, Double> statBonuses; // 태어날 펫에 적용될 보너스

    /**
     * 기본 생성자
     */
    public PetEgg() {
        this.eggId = UUID.randomUUID();
        this.possibleSpecies = new ArrayList<>();
        this.rarityChances = new EnumMap<>(PetRarity.class);
        this.requiredBiomes = new ArrayList<>();
        this.statBonuses = new HashMap<>();
        this.acquiredTime = System.currentTimeMillis();
        this.hatchStartTime = 0;
        this.isHatching = false;
        this. hatchTime = 3600000; // 기본 1시간
        this.requiredPlayerLevel = 1;
        this.requiredTimeOfDay = "ANY";
        this.requiredTemperature = -1;
        this.tradeable = true;
        this.isShiny = false;
        this.mutationBonus = 0;
        this.material = Material.DRAGON_EGG;
        this.colorCode = "&f";
    }

    /**
     * 전체 생성자
     */
    public PetEgg(String eggType, String name, String speciesId) {
        this();
        this.eggType = eggType;
        this.name = name;
        this.speciesId = speciesId;
    }

    /**
     * 랜덤 종족 알 생성자
     */
    public PetEgg(String eggType, String name, List<String> possibleSpecies) {
        this();
        this.eggType = eggType;
        this.name = name;
        this.possibleSpecies = new ArrayList<>(possibleSpecies);
    }

    // ===== 부화 관련 메서드 =====

    /**
     * 부화 시작
     */
    public void startHatching() {
        if (! isHatching && hatchStartTime == 0) {
            this.hatchStartTime = System.currentTimeMillis();
            this.isHatching = true;
        }
    }

    /**
     * 부화 취소
     */
    public void cancelHatching() {
        this.hatchStartTime = 0;
        this.isHatching = false;
    }

    /**
     * 부화 완료 여부 확인
     */
    public boolean isReadyToHatch() {
        if (!isHatching || hatchStartTime == 0) {
            return false;
        }
        return System.currentTimeMillis() >= (hatchStartTime + hatchTime);
    }

    /**
     * 남은 부화 시간 (밀리초)
     */
    public long getRemainingHatchTime() {
        if (!isHatching || hatchStartTime == 0) {
            return hatchTime;
        }
        long elapsed = System.currentTimeMillis() - hatchStartTime;
        return Math.max(0, hatchTime - elapsed);
    }

    /**
     * 남은 부화 시간 포맷팅
     */
    public String getRemainingHatchTimeFormatted() {
        long remaining = getRemainingHatchTime();
        if (remaining <= 0) return "부화 준비 완료";

        long seconds = remaining / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String. format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String.format("%d초", seconds);
        }
    }

    /**
     * 부화 진행률 (0-100)
     */
    public double getHatchProgress() {
        if (!isHatching || hatchStartTime == 0) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - hatchStartTime;
        return Math. min(100, (elapsed / (double) hatchTime) * 100);
    }

    // ===== 종족/희귀도 결정 =====

    /**
     * 부화 시 종족 결정
     */
    public String determineSpecies() {
        // 고정 종족이 있으면 반환
        if (speciesId != null && ! speciesId.isEmpty()) {
            return speciesId;
        }

        // 가능한 종족 목록에서 랜덤 선택
        if (! possibleSpecies. isEmpty()) {
            Random random = new Random();
            return possibleSpecies. get(random.nextInt(possibleSpecies.size()));
        }

        return null;
    }

    /**
     * 부화 시 희귀도 결정
     */
    public PetRarity determineRarity() {
        // 보장된 희귀도가 있으면 반환
        if (guaranteedRarity != null) {
            return guaranteedRarity;
        }

        // 확률 기반 결정
        if (! rarityChances. isEmpty()) {
            double random = Math. random() * 100;
            double cumulative = 0;

            for (Map. Entry<PetRarity, Double> entry : rarityChances.entrySet()) {
                cumulative += entry.getValue();
                if (random <= cumulative) {
                    return entry.getKey();
                }
            }
        }

        // 빛나는 알은 희귀도 상승
        if (isShiny) {
            return PetRarity.getRandomByChance().getNextRarity();
        }

        return PetRarity.getRandomByChance();
    }

    /**
     * 변이 여부 결정
     */
    public boolean rollMutation(double baseMutationChance) {
        double totalChance = baseMutationChance + mutationBonus;
        if (isShiny) {
            totalChance += 10.0; // 빛나는 알 보너스
        }
        return Math.random() * 100 < totalChance;
    }

    // ===== 조건 확인 =====

    /**
     * 부화 조건 충족 여부 확인
     */
    public boolean canHatch(int playerLevel, String currentBiome, boolean isDay) {
        // 플레이어 레벨 체크
        if (playerLevel < requiredPlayerLevel) {
            return false;
        }

        // 바이옴 체크
        if (!requiredBiomes.isEmpty() && !requiredBiomes.contains(currentBiome)) {
            return false;
        }

        // 시간대 체크
        if (!"ANY".equalsIgnoreCase(requiredTimeOfDay)) {
            if ("DAY".equalsIgnoreCase(requiredTimeOfDay) && ! isDay) {
                return false;
            }
            if ("NIGHT".equalsIgnoreCase(requiredTimeOfDay) && isDay) {
                return false;
            }
        }

        return true;
    }

    /**
     * 부화 불가 이유 반환
     */
    public String getCannotHatchReason(int playerLevel, String currentBiome, boolean isDay) {
        if (playerLevel < requiredPlayerLevel) {
            return "플레이어 레벨 " + requiredPlayerLevel + " 필요 (현재:  " + playerLevel + ")";
        }

        if (!requiredBiomes. isEmpty() && !requiredBiomes. contains(currentBiome)) {
            return "특정 바이옴에서만 부화 가능:  " + String.join(", ", requiredBiomes);
        }

        if (!"ANY".equalsIgnoreCase(requiredTimeOfDay)) {
            if ("DAY".equalsIgnoreCase(requiredTimeOfDay) && !isDay) {
                return "낮에만 부화 가능합니다. ";
            }
            if ("NIGHT".equalsIgnoreCase(requiredTimeOfDay) && isDay) {
                return "밤에만 부화 가능합니다.";
            }
        }

        return null;
    }

    // ===== 희귀도 확률 설정 =====

    /**
     * 희귀도 확률 설정
     */
    public void setRarityChance(PetRarity rarity, double chance) {
        rarityChances.put(rarity, chance);
    }

    /**
     * 기본 희귀도 확률 설정
     */
    public void setDefaultRarityChances() {
        rarityChances.put(PetRarity.COMMON, 50.0);
        rarityChances. put(PetRarity.UNCOMMON, 25.0);
        rarityChances. put(PetRarity.RARE, 15.0);
        rarityChances. put(PetRarity.EPIC, 7.0);
        rarityChances. put(PetRarity.LEGENDARY, 2.5);
        rarityChances. put(PetRarity.MYTHIC, 0.5);
    }

    // ===== 스탯 보너스 =====

    /**
     * 스탯 보너스 추가
     */
    public void addStatBonus(String statName, double value) {
        statBonuses.put(statName, value);
    }

    /**
     * 스탯 보너스 가져오기
     */
    public double getStatBonus(String statName) {
        return statBonuses.getOrDefault(statName, 0.0);
    }

    // ===== 아이템 생성 =====

    /**
     * ItemStack 생성
     */
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material != null ? material : Material.DRAGON_EGG);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // 이름 설정
            String displayName = colorCode + name;
            if (isShiny) {
                displayName = "&e✨ " + displayName + " ✨";
            }
            meta.setDisplayName(displayName. replace("&", "§"));

            // Lore 생성
            List<String> lore = generateLore();
            meta.setLore(lore);

            // Custom Model Data
            if (customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Lore 생성
     */
    private List<String> generateLore() {
        List<String> lore = new ArrayList<>();

        if (description != null && !description.isEmpty()) {
            lore.add(("§7" + description).replace("&", "§"));
            lore.add("");
        }

        // 종족 정보
        if (speciesId != null && !speciesId.isEmpty()) {
            lore.add("§7종족: §f" + speciesId);
        } else if (!possibleSpecies.isEmpty()) {
            lore.add("§7가능한 종족: §f" + possibleSpecies. size() + "종");
        }

        // 보장된 희귀도
        if (guaranteedRarity != null) {
            lore.add("§7희귀도: " + guaranteedRarity.getColoredName().replace("&", "§"));
        }

        lore.add("");

        // 부화 상태
        if (isHatching) {
            lore. add("§a부화 중.. .");
            lore.add("§7진행률: §e" + String.format("%.1f%%", getHatchProgress()));
            lore.add("§7남은 시간: §f" + getRemainingHatchTimeFormatted());
        } else {
            lore.add("§7부화 시간: §f" + formatTime(hatchTime));
        }

        // 조건
        if (requiredPlayerLevel > 1) {
            lore.add("");
            lore. add("§c요구 플레이어 레벨: " + requiredPlayerLevel);
        }

        if (! requiredBiomes.isEmpty()) {
            lore.add("§c요구 바이옴: " + String.join(", ", requiredBiomes));
        }

        // 빛나는 알 표시
        if (isShiny) {
            lore. add("");
            lore. add("§e§l빛나는 알");
            lore.add("§7변이 확률이 증가합니다!");
        }

        // 거래 불가 표시
        if (! tradeable) {
            lore.add("");
            lore.add("§4거래 불가");
        }

        return lore;
    }

    /**
     * 시간 포맷팅
     */
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return hours + "시간";
        } else if (minutes > 0) {
            return minutes + "분";
        } else {
            return seconds + "초";
        }
    }

    // ===== Getter/Setter =====

    public UUID getEggId() {
        return eggId;
    }

    public void setEggId(UUID eggId) {
        this. eggId = eggId;
    }

    public String getEggType() {
        return eggType;
    }

    public void setEggType(String eggType) {
        this. eggType = eggType;
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

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(String speciesId) {
        this.speciesId = speciesId;
    }

    public List<String> getPossibleSpecies() {
        return possibleSpecies;
    }

    public void setPossibleSpecies(List<String> possibleSpecies) {
        this.possibleSpecies = possibleSpecies;
    }

    public PetRarity getGuaranteedRarity() {
        return guaranteedRarity;
    }

    public void setGuaranteedRarity(PetRarity guaranteedRarity) {
        this.guaranteedRarity = guaranteedRarity;
    }

    public Map<PetRarity, Double> getRarityChances() {
        return rarityChances;
    }

    public void setRarityChances(Map<PetRarity, Double> rarityChances) {
        this.rarityChances = rarityChances;
    }

    public long getHatchTime() {
        return hatchTime;
    }

    public void setHatchTime(long hatchTime) {
        this. hatchTime = hatchTime;
    }

    public long getAcquiredTime() {
        return acquiredTime;
    }

    public void setAcquiredTime(long acquiredTime) {
        this.acquiredTime = acquiredTime;
    }

    public long getHatchStartTime() {
        return hatchStartTime;
    }

    public void setHatchStartTime(long hatchStartTime) {
        this.hatchStartTime = hatchStartTime;
    }

    public boolean isHatching() {
        return isHatching;
    }

    public void setHatching(boolean hatching) {
        isHatching = hatching;
    }

    public int getRequiredPlayerLevel() {
        return requiredPlayerLevel;
    }

    public void setRequiredPlayerLevel(int requiredPlayerLevel) {
        this.requiredPlayerLevel = requiredPlayerLevel;
    }

    public List<String> getRequiredBiomes() {
        return requiredBiomes;
    }

    public void setRequiredBiomes(List<String> requiredBiomes) {
        this. requiredBiomes = requiredBiomes;
    }

    public String getRequiredTimeOfDay() {
        return requiredTimeOfDay;
    }

    public void setRequiredTimeOfDay(String requiredTimeOfDay) {
        this.requiredTimeOfDay = requiredTimeOfDay;
    }

    public double getRequiredTemperature() {
        return requiredTemperature;
    }

    public void setRequiredTemperature(double requiredTemperature) {
        this. requiredTemperature = requiredTemperature;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this. customModelData = customModelData;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this. source = source;
    }

    public boolean isTradeable() {
        return tradeable;
    }

    public void setTradeable(boolean tradeable) {
        this.tradeable = tradeable;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    public boolean isShiny() {
        return isShiny;
    }

    public void setShiny(boolean shiny) {
        isShiny = shiny;
    }

    public double getMutationBonus() {
        return mutationBonus;
    }

    public void setMutationBonus(double mutationBonus) {
        this.mutationBonus = mutationBonus;
    }

    public Map<String, Double> getStatBonuses() {
        return statBonuses;
    }

    public void setStatBonuses(Map<String, Double> statBonuses) {
        this.statBonuses = statBonuses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetEgg petEgg = (PetEgg) o;
        return Objects.equals(eggId, petEgg.eggId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eggId);
    }

    @Override
    public String toString() {
        return "PetEgg{" +
                "eggId=" + eggId +
                ", name='" + name + '\'' +
                ", speciesId='" + speciesId + '\'' +
                ", isHatching=" + isHatching +
                '}';
    }
}