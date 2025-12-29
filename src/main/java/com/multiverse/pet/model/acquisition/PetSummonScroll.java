package com.multiverse.pet. model.acquisition;

import com.multiverse.pet.model. PetRarity;
import com. multiverse.pet. model.PetType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org. bukkit.inventory. meta.ItemMeta;

import java.util.*;

/**
 * 펫 소환서 데이터 클래스
 * 즉시 펫을 소환할 수 있는 소환서 정보를 정의
 */
public class PetSummonScroll {

    // 기본 정보
    private UUID scrollId;
    private String scrollType;
    private String name;
    private String description;

    // 소유 정보
    private UUID ownerId;

    // 소환 정보
    private String speciesId;
    private List<String> possibleSpecies;
    private PetRarity guaranteedRarity;
    private PetRarity minRarity;
    private PetRarity maxRarity;
    private PetType guaranteedType;

    // 펫 초기 스탯
    private int initialLevel;
    private Map<String, Double> bonusStats;
    private List<String> initialSkills;

    // 사용 조건
    private int requiredPlayerLevel;
    private int usesRemaining;
    private boolean isOneTimeUse;

    // 외형
    private Material material;
    private int customModelData;
    private String colorCode;

    // 획득 경로
    private String source;
    private long acquiredTime;
    private long expirationTime;        // 0이면 무제한

    // 거래
    private boolean tradeable;
    private boolean soulbound;
    private UUID boundToPlayer;
    private double marketValue;

    // 특수 효과
    private boolean isShiny;
    private double mutationChance;
    private String customPetName;       // 미리 정해진 이름
    private boolean nameLocked;         // 이름 변경 불가

    /**
     * 기본 생성자
     */
    public PetSummonScroll() {
        this.scrollId = UUID.randomUUID();
        this.possibleSpecies = new ArrayList<>();
        this.bonusStats = new HashMap<>();
        this.initialSkills = new ArrayList<>();
        this.acquiredTime = System.currentTimeMillis();
        this.expirationTime = 0;
        this.initialLevel = 1;
        this.requiredPlayerLevel = 1;
        this.usesRemaining = 1;
        this.isOneTimeUse = true;
        this. tradeable = true;
        this.soulbound = false;
        this.isShiny = false;
        this.mutationChance = 0;
        this.nameLocked = false;
        this.material = Material.PAPER;
        this. colorCode = "&f";
    }

    /**
     * 전체 생성자
     */
    public PetSummonScroll(String scrollType, String name, String speciesId) {
        this();
        this.scrollType = scrollType;
        this.name = name;
        this.speciesId = speciesId;
    }

    /**
     * 희귀도 보장 생성자
     */
    public PetSummonScroll(String scrollType, String name, PetRarity guaranteedRarity) {
        this();
        this.scrollType = scrollType;
        this.name = name;
        this.guaranteedRarity = guaranteedRarity;
    }

    // ===== 사용 관련 메서드 =====

    /**
     * 소환서 사용 가능 여부 확인
     */
    public boolean canUse(int playerLevel) {
        // 사용 횟수 체크
        if (usesRemaining <= 0) {
            return false;
        }

        // 만료 체크
        if (isExpired()) {
            return false;
        }

        // 플레이어 레벨 체크
        if (playerLevel < requiredPlayerLevel) {
            return false;
        }

        return true;
    }

    /**
     * 사용 불가 이유 반환
     */
    public String getCannotUseReason(int playerLevel) {
        if (usesRemaining <= 0) {
            return "소환서의 사용 횟수가 모두 소진되었습니다. ";
        }

        if (isExpired()) {
            return "소환서가 만료되었습니다.";
        }

        if (playerLevel < requiredPlayerLevel) {
            return "플레이어 레벨 " + requiredPlayerLevel + " 필요 (현재:  " + playerLevel + ")";
        }

        return null;
    }

    /**
     * 소환서 사용 (사용 횟수 감소)
     */
    public boolean use() {
        if (usesRemaining <= 0) {
            return false;
        }
        usesRemaining--;
        return true;
    }

    /**
     * 만료 여부 확인
     */
    public boolean isExpired() {
        if (expirationTime <= 0) {
            return false;
        }
        return System.currentTimeMillis() > expirationTime;
    }

    /**
     * 남은 만료 시간 (밀리초)
     */
    public long getRemainingTime() {
        if (expirationTime <= 0) {
            return -1; // 무제한
        }
        return Math.max(0, expirationTime - System.currentTimeMillis());
    }

    /**
     * 남은 만료 시간 포맷팅
     */
    public String getRemainingTimeFormatted() {
        long remaining = getRemainingTime();
        if (remaining < 0) return "무제한";
        if (remaining == 0) return "만료됨";

        long seconds = remaining / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "일 " + (hours % 24) + "시간";
        } else if (hours > 0) {
            return hours + "시간 " + (minutes % 60) + "분";
        } else if (minutes > 0) {
            return minutes + "분 " + (seconds % 60) + "초";
        } else {
            return seconds + "초";
        }
    }

    /**
     * 소진 여부 확인
     */
    public boolean isExhausted() {
        return usesRemaining <= 0;
    }

    // ===== 소환 결과 결정 =====

    /**
     * 소환될 종족 결정
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
     * 소환될 희귀도 결정
     */
    public PetRarity determineRarity() {
        // 보장된 희귀도가 있으면 반환
        if (guaranteedRarity != null) {
            return guaranteedRarity;
        }

        // 범위 내에서 랜덤
        if (minRarity != null || maxRarity != null) {
            int minOrdinal = minRarity != null ? minRarity.ordinal() : 0;
            int maxOrdinal = maxRarity != null ?  maxRarity.ordinal() : PetRarity.values().length - 1;

            Random random = new Random();
            int resultOrdinal = minOrdinal + random.nextInt(maxOrdinal - minOrdinal + 1);
            return PetRarity. values()[resultOrdinal];
        }

        // 빛나는 소환서는 희귀도 상승
        if (isShiny) {
            PetRarity base = PetRarity.getRandomByChance();
            return base.getNextRarity();
        }

        return PetRarity. getRandomByChance();
    }

    /**
     * 변이 여부 결정
     */
    public boolean rollMutation() {
        double chance = mutationChance;
        if (isShiny) {
            chance += 10.0;
        }
        return Math.random() * 100 < chance;
    }

    // ===== 소울바운드 관련 =====

    /**
     * 소울바운드 설정
     */
    public void bindToPlayer(UUID playerId) {
        this.soulbound = true;
        this.boundToPlayer = playerId;
        this.tradeable = false;
    }

    /**
     * 소울바운드 해제
     */
    public void unbind() {
        this.soulbound = false;
        this.boundToPlayer = null;
    }

    /**
     * 특정 플레이어에게 바인딩되어 있는지 확인
     */
    public boolean isBoundTo(UUID playerId) {
        return soulbound && boundToPlayer != null && boundToPlayer.equals(playerId);
    }

    // ===== 보너스 스탯/스킬 =====

    /**
     * 보너스 스탯 추가
     */
    public void addBonusStat(String statName, double value) {
        bonusStats.put(statName, value);
    }

    /**
     * 초기 스킬 추가
     */
    public void addInitialSkill(String skillId) {
        if (! initialSkills.contains(skillId)) {
            initialSkills.add(skillId);
        }
    }

    /**
     * 가능 종족 추가
     */
    public void addPossibleSpecies(String species) {
        if (!possibleSpecies.contains(species)) {
            possibleSpecies.add(species);
        }
    }

    // ===== 아이템 생성 =====

    /**
     * ItemStack 생성
     */
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material != null ? material : Material.PAPER);
        ItemMeta meta = item. getItemMeta();

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

        // 소환 정보
        lore.add("§6【 소환 정보 】");

        if (speciesId != null && !speciesId. isEmpty()) {
            lore.add("§7종족: §f" + speciesId);
        } else if (! possibleSpecies. isEmpty()) {
            lore.add("§7가능한 종족: §f" + possibleSpecies.size() + "종");
        } else {
            lore.add("§7종족: §f랜덤");
        }

        if (guaranteedRarity != null) {
            lore.add("§7희귀도: " + guaranteedRarity.getColoredName().replace("&", "§"));
        } else if (minRarity != null && maxRarity != null) {
            lore.add("§7희귀도: " + minRarity. getDisplayName() + " ~ " + maxRarity.getDisplayName());
        }

        if (guaranteedType != null) {
            lore.add("§7타입: " + guaranteedType.getColoredName().replace("&", "§"));
        }

        if (initialLevel > 1) {
            lore.add("§7초기 레벨: §e" + initialLevel);
        }

        // 보너스 스탯
        if (! bonusStats.isEmpty()) {
            lore.add("");
            lore.add("§b【 보너스 스탯 】");
            for (Map.Entry<String, Double> entry : bonusStats. entrySet()) {
                lore. add("§7" + entry.getKey() + ": §a+" + String.format("%.1f", entry.getValue()));
            }
        }

        // 초기 스킬
        if (! initialSkills.isEmpty()) {
            lore.add("");
            lore.add("§5【 초기 스킬 】");
            for (String skill : initialSkills) {
                lore.add("§7- " + skill);
            }
        }

        lore.add("");

        // 사용 정보
        if (! isOneTimeUse) {
            lore.add("§7남은 사용 횟수: §f" + usesRemaining);
        }

        // 만료 시간
        if (expirationTime > 0) {
            if (isExpired()) {
                lore.add("§c만료됨");
            } else {
                lore.add("§7남은 시간: §f" + getRemainingTimeFormatted());
            }
        }

        // 조건
        if (requiredPlayerLevel > 1) {
            lore.add("");
            lore. add("§c요구 플레이어 레벨: " + requiredPlayerLevel);
        }

        // 빛나는 소환서 표시
        if (isShiny) {
            lore. add("");
            lore.add("§e§l빛나는 소환서");
            lore.add("§7더 높은 희귀도의 펫이 소환됩니다!");
        }

        // 변이 확률 표시
        if (mutationChance > 0) {
            lore.add("§d변이 확률: +" + String.format("%.1f%%", mutationChance));
        }

        // 커스텀 이름
        if (customPetName != null && !customPetName.isEmpty()) {
            lore.add("");
            lore. add("§7펫 이름:  §f" + customPetName);
            if (nameLocked) {
                lore.add("§8(이름 변경 불가)");
            }
        }

        // 소울바운드 표시
        if (soulbound) {
            lore. add("");
            lore.add("§4§l귀속됨");
        }

        // 거래 불가 표시
        if (! tradeable && !soulbound) {
            lore.add("");
            lore.add("§4거래 불가");
        }

        lore.add("");
        lore.add("§e우클릭하여 펫을 소환하세요!");

        return lore;
    }

    // ===== 등급별 소환서 생성 =====

    /**
     * 일반 소환서 생성
     */
    public static PetSummonScroll createCommonScroll() {
        PetSummonScroll scroll = new PetSummonScroll();
        scroll.setScrollType("common");
        scroll.setName("일반 소환서");
        scroll.setDescription("일반 등급의 펫을 소환합니다.");
        scroll.setGuaranteedRarity(PetRarity. COMMON);
        scroll.setMaterial(Material.PAPER);
        scroll.setColorCode("&f");
        return scroll;
    }

    /**
     * 고급 소환서 생성
     */
    public static PetSummonScroll createUncommonScroll() {
        PetSummonScroll scroll = new PetSummonScroll();
        scroll.setScrollType("uncommon");
        scroll.setName("고급 소환서");
        scroll.setDescription("고급 등급의 펫을 소환합니다.");
        scroll.setGuaranteedRarity(PetRarity.UNCOMMON);
        scroll.setMaterial(Material. PAPER);
        scroll.setColorCode("&a");
        return scroll;
    }

    /**
     * 희귀 소환서 생성
     */
    public static PetSummonScroll createRareScroll() {
        PetSummonScroll scroll = new PetSummonScroll();
        scroll.setScrollType("rare");
        scroll.setName("희귀 소환서");
        scroll.setDescription("희귀 등급의 펫을 소환합니다.");
        scroll.setGuaranteedRarity(PetRarity. RARE);
        scroll.setMaterial(Material.PAPER);
        scroll.setColorCode("&9");
        return scroll;
    }

    /**
     * 영웅 소환서 생성
     */
    public static PetSummonScroll createEpicScroll() {
        PetSummonScroll scroll = new PetSummonScroll();
        scroll.setScrollType("epic");
        scroll.setName("영웅 소환서");
        scroll.setDescription("영웅 등급의 펫을 소환합니다.");
        scroll.setGuaranteedRarity(PetRarity.EPIC);
        scroll.setMaterial(Material.ENCHANTED_BOOK);
        scroll.setColorCode("&5");
        return scroll;
    }

    /**
     * 전설 소환서 생성
     */
    public static PetSummonScroll createLegendaryScroll() {
        PetSummonScroll scroll = new PetSummonScroll();
        scroll.setScrollType("legendary");
        scroll.setName("전설 소환서");
        scroll.setDescription("전설 등급의 펫을 소환합니다.");
        scroll.setGuaranteedRarity(PetRarity. LEGENDARY);
        scroll.setMaterial(Material.ENCHANTED_BOOK);
        scroll.setColorCode("&6");
        scroll.setShiny(true);
        return scroll;
    }

    /**
     * 신화 소환서 생성
     */
    public static PetSummonScroll createMythicScroll() {
        PetSummonScroll scroll = new PetSummonScroll();
        scroll.setScrollType("mythic");
        scroll.setName("신화 소환서");
        scroll.setDescription("신화 등급의 펫을 소환합니다.");
        scroll.setGuaranteedRarity(PetRarity.MYTHIC);
        scroll.setMaterial(Material.NETHER_STAR);
        scroll.setColorCode("&c");
        scroll.setShiny(true);
        scroll.setMutationChance(25.0);
        return scroll;
    }

    /**
     * 랜덤 소환서 생성
     */
    public static PetSummonScroll createRandomScroll() {
        PetSummonScroll scroll = new PetSummonScroll();
        scroll.setScrollType("random");
        scroll.setName("미지의 소환서");
        scroll.setDescription("어떤 펫이 소환될지 알 수 없습니다.");
        scroll.setMaterial(Material. PAPER);
        scroll.setColorCode("&d");
        return scroll;
    }

    // ===== Getter/Setter =====

    public UUID getScrollId() {
        return scrollId;
    }

    public void setScrollId(UUID scrollId) {
        this.scrollId = scrollId;
    }

    public String getScrollType() {
        return scrollType;
    }

    public void setScrollType(String scrollType) {
        this.scrollType = scrollType;
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
        this.maxRarity = maxRarity;
    }

    public PetType getGuaranteedType() {
        return guaranteedType;
    }

    public void setGuaranteedType(PetType guaranteedType) {
        this.guaranteedType = guaranteedType;
    }

    public int getInitialLevel() {
        return initialLevel;
    }

    public void setInitialLevel(int initialLevel) {
        this.initialLevel = initialLevel;
    }

    public Map<String, Double> getBonusStats() {
        return bonusStats;
    }

    public void setBonusStats(Map<String, Double> bonusStats) {
        this. bonusStats = bonusStats;
    }

    public List<String> getInitialSkills() {
        return initialSkills;
    }

    public void setInitialSkills(List<String> initialSkills) {
        this.initialSkills = initialSkills;
    }

    public int getRequiredPlayerLevel() {
        return requiredPlayerLevel;
    }

    public void setRequiredPlayerLevel(int requiredPlayerLevel) {
        this.requiredPlayerLevel = requiredPlayerLevel;
    }

    public int getUsesRemaining() {
        return usesRemaining;
    }

    public void setUsesRemaining(int usesRemaining) {
        this.usesRemaining = usesRemaining;
    }

    public boolean isOneTimeUse() {
        return isOneTimeUse;
    }

    public void setOneTimeUse(boolean oneTimeUse) {
        isOneTimeUse = oneTimeUse;
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
        this.customModelData = customModelData;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this. colorCode = colorCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this. source = source;
    }

    public long getAcquiredTime() {
        return acquiredTime;
    }

    public void setAcquiredTime(long acquiredTime) {
        this.acquiredTime = acquiredTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isTradeable() {
        return tradeable;
    }

    public void setTradeable(boolean tradeable) {
        this.tradeable = tradeable;
    }

    public boolean isSoulbound() {
        return soulbound;
    }

    public void setSoulbound(boolean soulbound) {
        this.soulbound = soulbound;
    }

    public UUID getBoundToPlayer() {
        return boundToPlayer;
    }

    public void setBoundToPlayer(UUID boundToPlayer) {
        this.boundToPlayer = boundToPlayer;
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

    public double getMutationChance() {
        return mutationChance;
    }

    public void setMutationChance(double mutationChance) {
        this. mutationChance = mutationChance;
    }

    public String getCustomPetName() {
        return customPetName;
    }

    public void setCustomPetName(String customPetName) {
        this.customPetName = customPetName;
    }

    public boolean isNameLocked() {
        return nameLocked;
    }

    public void setNameLocked(boolean nameLocked) {
        this.nameLocked = nameLocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetSummonScroll that = (PetSummonScroll) o;
        return Objects.equals(scrollId, that.scrollId);
    }

    @Override
    public int hashCode() {
        return Objects. hash(scrollId);
    }

    @Override
    public String toString() {
        return "PetSummonScroll{" +
                "scrollId=" + scrollId +
                ", name='" + name + '\'' +
                ", speciesId='" + speciesId + '\'' +
                ", guaranteedRarity=" + guaranteedRarity +
                ", usesRemaining=" + usesRemaining +
                '}';
    }
}