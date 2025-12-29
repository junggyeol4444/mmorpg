package com.multiverse.pet.model.acquisition;

import com.multiverse.pet.model.PetRarity;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * 포획구 데이터 클래스
 * 야생 몬스터를 펫으로 포획하는 아이템 정보를 정의
 */
public class CaptureBall {

    /**
     * 포획구 등급
     */
    public enum BallGrade {
        BASIC("기본", "&f", 1.0, 10.0),
        ADVANCED("고급", "&a", 1.5, 20.0),
        SUPERIOR("상급", "&9", 2.0, 35.0),
        MASTER("마스터", "&5", 3.0, 50.0),
        ULTIMATE("궁극", "&6", 5.0, 75.0),
        LEGENDARY("전설", "&c", 10.0, 95.0);

        private final String displayName;
        private final String colorCode;
        private final double captureMultiplier;
        private final double baseSuccessRate;

        BallGrade(String displayName, String colorCode, double captureMultiplier, double baseSuccessRate) {
            this.displayName = displayName;
            this.colorCode = colorCode;
            this.captureMultiplier = captureMultiplier;
            this.baseSuccessRate = baseSuccessRate;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getColorCode() {
            return colorCode;
        }

        public String getColoredName() {
            return colorCode + displayName;
        }

        public double getCaptureMultiplier() {
            return captureMultiplier;
        }

        public double getBaseSuccessRate() {
            return baseSuccessRate;
        }
    }

    // 기본 정보
    private UUID ballId;
    private BallGrade grade;
    private String name;
    private String description;

    // 소유 정보
    private UUID ownerId;

    // 포획 대상 제한
    private List<EntityType> allowedEntityTypes;
    private List<String> allowedSpecies;
    private PetRarity maxCapturableRarity;
    private int maxCaptureLevel;

    // 포획 확률 보정
    private double captureRateBonus;
    private double healthThresholdBonus;  // 체력이 낮을수록 보너스
    private double criticalCaptureChance; // 즉시 포획 확률

    // 사용 정보
    private int usesRemaining;
    private boolean isReusable;
    private int maxUses;

    // 외형
    private Material material;
    private int customModelData;

    // 획득 경로
    private String source;
    private long acquiredTime;

    // 거래
    private boolean tradeable;
    private double marketValue;

    // 특수 효과
    private boolean preventFlee;        // 도주 방지
    private boolean guaranteeMinRarity; // 최소 희귀도 보장
    private PetRarity minGuaranteedRarity;
    private double bonusExp;            // 포획 시 추가 경험치

    // 포획된 펫 정보 (사용 후)
    private boolean containsPet;
    private UUID capturedPetId;
    private String capturedPetSpecies;
    private PetRarity capturedPetRarity;

    /**
     * 기본 생성자
     */
    public CaptureBall() {
        this.ballId = UUID.randomUUID();
        this.grade = BallGrade. BASIC;
        this.allowedEntityTypes = new ArrayList<>();
        this.allowedSpecies = new ArrayList<>();
        this.acquiredTime = System. currentTimeMillis();
        this.usesRemaining = 1;
        this.isReusable = false;
        this. maxUses = 1;
        this.captureRateBonus = 0;
        this.healthThresholdBonus = 0;
        this.criticalCaptureChance = 0;
        this.maxCaptureLevel = 100;
        this. tradeable = true;
        this. preventFlee = false;
        this.guaranteeMinRarity = false;
        this. bonusExp = 0;
        this.containsPet = false;
        this.material = Material. ENDER_PEARL;
    }

    /**
     * 등급 기반 생성자
     */
    public CaptureBall(BallGrade grade) {
        this();
        this.grade = grade;
        this.name = grade.getDisplayName() + " 포획구";
        applyGradeDefaults();
    }

    /**
     * 등급 기본값 적용
     */
    private void applyGradeDefaults() {
        switch (grade) {
            case BASIC: 
                this.maxCapturableRarity = PetRarity. UNCOMMON;
                this.material = Material.SNOWBALL;
                break;
            case ADVANCED:
                this.maxCapturableRarity = PetRarity.RARE;
                this. material = Material. ENDER_PEARL;
                this.captureRateBonus = 5.0;
                break;
            case SUPERIOR:
                this.maxCapturableRarity = PetRarity.EPIC;
                this. material = Material. ENDER_PEARL;
                this.captureRateBonus = 10.0;
                this.healthThresholdBonus = 10.0;
                break;
            case MASTER:
                this.maxCapturableRarity = PetRarity.LEGENDARY;
                this. material = Material. ENDER_EYE;
                this.captureRateBonus = 20.0;
                this.healthThresholdBonus = 20.0;
                this.criticalCaptureChance = 5.0;
                break;
            case ULTIMATE:
                this.maxCapturableRarity = PetRarity. LEGENDARY;
                this. material = Material. ENDER_EYE;
                this.captureRateBonus = 35.0;
                this.healthThresholdBonus = 30.0;
                this.criticalCaptureChance = 10.0;
                this.preventFlee = true;
                break;
            case LEGENDARY: 
                this.maxCapturableRarity = PetRarity.MYTHIC;
                this.material = Material.NETHER_STAR;
                this.captureRateBonus = 50.0;
                this. healthThresholdBonus = 50.0;
                this.criticalCaptureChance = 20.0;
                this.preventFlee = true;
                this.guaranteeMinRarity = true;
                this.minGuaranteedRarity = PetRarity.RARE;
                break;
        }
    }

    // ===== 포획 관련 메서드 =====

    /**
     * 포획 확률 계산
     *
     * @param targetRarity 대상 희귀도
     * @param targetHealthPercent 대상 체력 퍼센트 (0-100)
     * @param targetLevel 대상 레벨
     * @return 포획 확률 (0-100)
     */
    public double calculateCaptureRate(PetRarity targetRarity, double targetHealthPercent, int targetLevel) {
        // 기본 확률
        double rate = grade.getBaseSuccessRate();

        // 등급 배율 적용
        rate *= grade.getCaptureMultiplier();

        // 희귀도에 따른 감소
        int rarityPenalty = targetRarity.ordinal() * 10;
        rate -= rarityPenalty;

        // 레벨에 따른 감소
        int levelPenalty = targetLevel / 10;
        rate -= levelPenalty;

        // 포획 확률 보너스 적용
        rate += captureRateBonus;

        // 체력 보너스 적용 (체력이 낮을수록 보너스)
        if (healthThresholdBonus > 0) {
            double healthBonus = (100 - targetHealthPercent) * (healthThresholdBonus / 100);
            rate += healthBonus;
        }

        // 0-100 범위로 제한
        return Math.max(1, Math.min(99, rate));
    }

    /**
     * 포획 시도
     *
     * @param targetRarity 대상 희귀도
     * @param targetHealthPercent 대상 체력 퍼센트
     * @param targetLevel 대상 레벨
     * @return 포획 성공 여부
     */
    public CaptureResult attemptCapture(PetRarity targetRarity, double targetHealthPercent, int targetLevel) {
        // 사용 가능 여부 확인
        if (usesRemaining <= 0) {
            return new CaptureResult(false, CaptureResult. FailReason.NO_USES);
        }

        // 포획된 펫이 이미 있으면 실패
        if (containsPet) {
            return new CaptureResult(false, CaptureResult.FailReason.ALREADY_CONTAINS_PET);
        }

        // 희귀도 제한 확인
        if (maxCapturableRarity != null && targetRarity. ordinal() > maxCapturableRarity.ordinal()) {
            return new CaptureResult(false, CaptureResult.FailReason.RARITY_TOO_HIGH);
        }

        // 레벨 제한 확인
        if (targetLevel > maxCaptureLevel) {
            return new CaptureResult(false, CaptureResult.FailReason.LEVEL_TOO_HIGH);
        }

        // 사용 횟수 감소
        usesRemaining--;

        // 즉시 포획 확률 확인
        if (criticalCaptureChance > 0 && Math. random() * 100 < criticalCaptureChance) {
            return new CaptureResult(true, true); // 크리티컬 포획
        }

        // 일반 포획 확률 계산
        double captureRate = calculateCaptureRate(targetRarity, targetHealthPercent, targetLevel);
        boolean success = Math.random() * 100 < captureRate;

        return new CaptureResult(success, false);
    }

    /**
     * 포획 가능 여부 확인
     */
    public boolean canCapture(EntityType entityType, String speciesId, PetRarity rarity, int level) {
        if (usesRemaining <= 0) return false;
        if (containsPet) return false;

        // 엔티티 타입 제한
        if (! allowedEntityTypes. isEmpty() && !allowedEntityTypes.contains(entityType)) {
            return false;
        }

        // 종족 제한
        if (!allowedSpecies.isEmpty() && !allowedSpecies.contains(speciesId)) {
            return false;
        }

        // 희귀도 제한
        if (maxCapturableRarity != null && rarity.ordinal() > maxCapturableRarity.ordinal()) {
            return false;
        }

        // 레벨 제한
        if (level > maxCaptureLevel) {
            return false;
        }

        return true;
    }

    /**
     * 포획 불가 이유 반환
     */
    public String getCannotCaptureReason(EntityType entityType, String speciesId, PetRarity rarity, int level) {
        if (usesRemaining <= 0) {
            return "포획구의 사용 횟수가 모두 소진되었습니다. ";
        }

        if (containsPet) {
            return "이미 펫이 포획되어 있습니다.";
        }

        if (! allowedEntityTypes.isEmpty() && !allowedEntityTypes.contains(entityType)) {
            return "이 포획구로는 해당 몬스터를 포획할 수 없습니다.";
        }

        if (!allowedSpecies.isEmpty() && !allowedSpecies.contains(speciesId)) {
            return "이 포획구로는 해당 종족을 포획할 수 없습니다. ";
        }

        if (maxCapturableRarity != null && rarity. ordinal() > maxCapturableRarity.ordinal()) {
            return "이 포획구로는 " + maxCapturableRarity.getDisplayName() + " 등급까지만 포획 가능합니다.";
        }

        if (level > maxCaptureLevel) {
            return "이 포획구로는 레벨 " + maxCaptureLevel + "까지만 포획 가능합니다.";
        }

        return null;
    }

    // ===== 포획된 펫 관련 =====

    /**
     * 펫 포획 처리
     */
    public void setCapturedPet(UUID petId, String species, PetRarity rarity) {
        this. containsPet = true;
        this.capturedPetId = petId;
        this.capturedPetSpecies = species;
        this. capturedPetRarity = rarity;
    }

    /**
     * 포획된 펫 해제
     */
    public UUID releasePet() {
        UUID petId = this. capturedPetId;
        this.containsPet = false;
        this.capturedPetId = null;
        this.capturedPetSpecies = null;
        this.capturedPetRarity = null;
        return petId;
    }

    // ===== 제한 추가 =====

    /**
     * 허용 엔티티 타입 추가
     */
    public void addAllowedEntityType(EntityType type) {
        if (!allowedEntityTypes.contains(type)) {
            allowedEntityTypes.add(type);
        }
    }

    /**
     * 허용 종족 추가
     */
    public void addAllowedSpecies(String species) {
        if (! allowedSpecies. contains(species)) {
            allowedSpecies.add(species);
        }
    }

    // ===== 아이템 생성 =====

    /**
     * ItemStack 생성
     */
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material != null ? material : Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // 이름 설정
            String displayName = grade.getColorCode() + name;
            if (containsPet) {
                displayName += " §7(포획됨)";
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

        // 등급 표시
        lore.add("§7등급:  " + grade.getColoredName().replace("&", "§"));

        if (description != null && !description.isEmpty()) {
            lore.add(("§7" + description).replace("&", "§"));
        }

        lore.add("");

        // 포획된 펫 정보
        if (containsPet) {
            lore. add("§6【 포획된 펫 】");
            lore.add("§7종족: §f" + capturedPetSpecies);
            lore. add("§7희귀도: " + capturedPetRarity.getColoredName().replace("&", "§"));
            lore. add("");
            lore.add("§e우클릭하여 펫을 꺼내세요!");
        } else {
            // 포획 정보
            lore.add("§6【 포획 정보 】");
            lore. add("§7기본 성공률: §f" + String.format("%.1f%%", grade.getBaseSuccessRate()));
            lore.add("§7포획 배율: §a" + String.format("%.1fx", grade.getCaptureMultiplier()));

            if (captureRateBonus > 0) {
                lore.add("§7추가 확률: §a+" + String.format("%.1f%%", captureRateBonus));
            }

            if (healthThresholdBonus > 0) {
                lore.add("§7체력 보너스: §a최대 +" + String.format("%.1f%%", healthThresholdBonus));
            }

            if (criticalCaptureChance > 0) {
                lore.add("§7즉시 포획:  §d" + String.format("%.1f%%", criticalCaptureChance));
            }

            lore.add("");

            // 제한 사항
            lore.add("§c【 제한 】");
            if (maxCapturableRarity != null) {
                lore.add("§7최대 희귀도: " + maxCapturableRarity.getColoredName().replace("&", "§"));
            }
            if (maxCaptureLevel < 100) {
                lore.add("§7최대 레벨:  §f" + maxCaptureLevel);
            }

            // 특수 효과
            if (preventFlee || guaranteeMinRarity) {
                lore.add("");
                lore. add("§b【 특수 효과 】");
                if (preventFlee) {
                    lore. add("§7- 도주 방지");
                }
                if (guaranteeMinRarity) {
                    lore.add("§7- 최소 " + minGuaranteedRarity.getDisplayName() + " 등급 보장");
                }
            }

            lore.add("");

            // 사용 정보
            if (isReusable) {
                lore. add("§7남은 사용:  §f" + usesRemaining + "/" + maxUses);
            } else {
                lore.add("§7사용 횟수: §f1회용");
            }

            lore.add("");
            lore.add("§e야생 몬스터에게 우클릭하여 포획하세요!");
        }

        // 거래 불가 표시
        if (! tradeable) {
            lore.add("");
            lore. add("§4거래 불가");
        }

        return lore;
    }

    // ===== 등급별 포획구 생성 =====

    /**
     * 기본 포획구 생성
     */
    public static CaptureBall createBasic() {
        return new CaptureBall(BallGrade.BASIC);
    }

    /**
     * 고급 포획구 생성
     */
    public static CaptureBall createAdvanced() {
        return new CaptureBall(BallGrade.ADVANCED);
    }

    /**
     * 상급 포획구 생성
     */
    public static CaptureBall createSuperior() {
        return new CaptureBall(BallGrade. SUPERIOR);
    }

    /**
     * 마스터 포획구 생성
     */
    public static CaptureBall createMaster() {
        return new CaptureBall(BallGrade. MASTER);
    }

    /**
     * 궁극 포획구 생성
     */
    public static CaptureBall createUltimate() {
        return new CaptureBall(BallGrade.ULTIMATE);
    }

    /**
     * 전설 포획구 생성
     */
    public static CaptureBall createLegendary() {
        return new CaptureBall(BallGrade.LEGENDARY);
    }

    // ===== Getter/Setter =====

    public UUID getBallId() {
        return ballId;
    }

    public void setBallId(UUID ballId) {
        this.ballId = ballId;
    }

    public BallGrade getGrade() {
        return grade;
    }

    public void setGrade(BallGrade grade) {
        this.grade = grade;
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

    public List<EntityType> getAllowedEntityTypes() {
        return allowedEntityTypes;
    }

    public void setAllowedEntityTypes(List<EntityType> allowedEntityTypes) {
        this.allowedEntityTypes = allowedEntityTypes;
    }

    public List<String> getAllowedSpecies() {
        return allowedSpecies;
    }

    public void setAllowedSpecies(List<String> allowedSpecies) {
        this.allowedSpecies = allowedSpecies;
    }

    public PetRarity getMaxCapturableRarity() {
        return maxCapturableRarity;
    }

    public void setMaxCapturableRarity(PetRarity maxCapturableRarity) {
        this.maxCapturableRarity = maxCapturableRarity;
    }

    public int getMaxCaptureLevel() {
        return maxCaptureLevel;
    }

    public void setMaxCaptureLevel(int maxCaptureLevel) {
        this.maxCaptureLevel = maxCaptureLevel;
    }

    public double getCaptureRateBonus() {
        return captureRateBonus;
    }

    public void setCaptureRateBonus(double captureRateBonus) {
        this.captureRateBonus = captureRateBonus;
    }

    public double getHealthThresholdBonus() {
        return healthThresholdBonus;
    }

    public void setHealthThresholdBonus(double healthThresholdBonus) {
        this.healthThresholdBonus = healthThresholdBonus;
    }

    public double getCriticalCaptureChance() {
        return criticalCaptureChance;
    }

    public void setCriticalCaptureChance(double criticalCaptureChance) {
        this.criticalCaptureChance = criticalCaptureChance;
    }

    public int getUsesRemaining() {
        return usesRemaining;
    }

    public void setUsesRemaining(int usesRemaining) {
        this.usesRemaining = usesRemaining;
    }

    public boolean isReusable() {
        return isReusable;
    }

    public void setReusable(boolean reusable) {
        isReusable = reusable;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
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

    public boolean isPreventFlee() {
        return preventFlee;
    }

    public void setPreventFlee(boolean preventFlee) {
        this.preventFlee = preventFlee;
    }

    public boolean isGuaranteeMinRarity() {
        return guaranteeMinRarity;
    }

    public void setGuaranteeMinRarity(boolean guaranteeMinRarity) {
        this.guaranteeMinRarity = guaranteeMinRarity;
    }

    public PetRarity getMinGuaranteedRarity() {
        return minGuaranteedRarity;
    }

    public void setMinGuaranteedRarity(PetRarity minGuaranteedRarity) {
        this.minGuaranteedRarity = minGuaranteedRarity;
    }

    public double getBonusExp() {
        return bonusExp;
    }

    public void setBonusExp(double bonusExp) {
        this.bonusExp = bonusExp;
    }

    public boolean isContainsPet() {
        return containsPet;
    }

    public void setContainsPet(boolean containsPet) {
        this.containsPet = containsPet;
    }

    public UUID getCapturedPetId() {
        return capturedPetId;
    }

    public void setCapturedPetId(UUID capturedPetId) {
        this.capturedPetId = capturedPetId;
    }

    public String getCapturedPetSpecies() {
        return capturedPetSpecies;
    }

    public void setCapturedPetSpecies(String capturedPetSpecies) {
        this.capturedPetSpecies = capturedPetSpecies;
    }

    public PetRarity getCapturedPetRarity() {
        return capturedPetRarity;
    }

    public void setCapturedPetRarity(PetRarity capturedPetRarity) {
        this.capturedPetRarity = capturedPetRarity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaptureBall that = (CaptureBall) o;
        return Objects. equals(ballId, that.ballId);
    }

    @Override
    public int hashCode() {
        return Objects. hash(ballId);
    }

    @Override
    public String toString() {
        return "CaptureBall{" +
                "ballId=" + ballId +
                ", grade=" + grade +
                ", containsPet=" + containsPet +
                ", usesRemaining=" + usesRemaining +
                '}';
    }

    // ===== 내부 클래스 =====

    /**
     * 포획 결과
     */
    public static class CaptureResult {
        public enum FailReason {
            NO_USES,
            ALREADY_CONTAINS_PET,
            RARITY_TOO_HIGH,
            LEVEL_TOO_HIGH,
            ENTITY_NOT_ALLOWED,
            SPECIES_NOT_ALLOWED,
            RANDOM_FAILURE
        }

        private final boolean success;
        private final boolean critical;
        private final FailReason failReason;

        public CaptureResult(boolean success, boolean critical) {
            this. success = success;
            this.critical = critical;
            this.failReason = success ? null : FailReason. RANDOM_FAILURE;
        }

        public CaptureResult(boolean success, FailReason failReason) {
            this.success = success;
            this. critical = false;
            this.failReason = failReason;
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean isCritical() {
            return critical;
        }

        public FailReason getFailReason() {
            return failReason;
        }

        public String getMessage() {
            if (success) {
                if (critical) {
                    return "&d&l즉시 포획!  &a펫을 성공적으로 포획했습니다!";
                }
                return "&a펫을 성공적으로 포획했습니다!";
            }

            switch (failReason) {
                case NO_USES: 
                    return "&c포획구의 사용 횟수가 모두 소진되었습니다. ";
                case ALREADY_CONTAINS_PET:
                    return "&c이미 펫이 포획되어 있습니다.";
                case RARITY_TOO_HIGH: 
                    return "&c이 포획구로는 포획할 수 없는 등급입니다.";
                case LEVEL_TOO_HIGH: 
                    return "&c이 포획구로는 포획할 수 없는 레벨입니다. ";
                case ENTITY_NOT_ALLOWED:
                    return "&c이 포획구로는 해당 몬스터를 포획할 수 없습니다.";
                case SPECIES_NOT_ALLOWED: 
                    return "&c이 포획구로는 해당 종족을 포획할 수 없습니다.";
                case RANDOM_FAILURE:
                default:
                    return "&c포획에 실패했습니다. 다시 시도해보세요. ";
            }
        }
    }
}