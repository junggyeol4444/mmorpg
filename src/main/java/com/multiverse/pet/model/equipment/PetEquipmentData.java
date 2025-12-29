package com.multiverse. pet.model.equipment;

import com. multiverse.pet. model.PetRarity;
import org.bukkit.Material;
import org. bukkit.inventory. ItemStack;
import org.bukkit. inventory.meta.ItemMeta;

import java.util.*;

/**
 * 펫 장비 데이터 클래스
 * 펫 장비의 스탯과 효과를 정의
 */
public class PetEquipmentData {

    // 기본 정보
    private String equipmentId;
    private String name;
    private String description;
    private PetEquipSlot slot;
    private PetRarity rarity;

    // 외형
    private Material material;
    private int customModelData;
    private List<String> lore;

    // 스탯
    private Map<String, Double> baseStats;
    private Map<String, Double> bonusStats;

    // 강화
    private int enhanceLevel;
    private int maxEnhanceLevel;
    private double enhanceMultiplier;

    // 요구 조건
    private int requiredPetLevel;
    private List<String> requiredPetTypes;
    private List<String> requiredPetSpecies;

    // 특수 효과
    private List<EquipmentEffect> effects;

    // 세트 효과
    private String setId;
    private int setPieceNumber;

    // 거래/소유
    private boolean tradeable;
    private boolean soulbound;
    private UUID boundToPlayer;

    // 내구도
    private int durability;
    private int maxDurability;
    private boolean breakable;

    // 소켓
    private int socketSlots;
    private List<String> insertedGems;

    /**
     * 기본 생성자
     */
    public PetEquipmentData() {
        this.baseStats = new HashMap<>();
        this.bonusStats = new HashMap<>();
        this.lore = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.requiredPetTypes = new ArrayList<>();
        this.requiredPetSpecies = new ArrayList<>();
        this.insertedGems = new ArrayList<>();
        this.enhanceLevel = 0;
        this. maxEnhanceLevel = 10;
        this. enhanceMultiplier = 0.1;
        this. requiredPetLevel = 1;
        this.tradeable = true;
        this.soulbound = false;
        this.breakable = false;
        this.durability = 100;
        this.maxDurability = 100;
        this. socketSlots = 0;
        this.rarity = PetRarity.COMMON;
    }

    /**
     * 전체 생성자
     */
    public PetEquipmentData(String equipmentId, String name, PetEquipSlot slot,
                            PetRarity rarity, Material material) {
        this();
        this.equipmentId = equipmentId;
        this.name = name;
        this.slot = slot;
        this.rarity = rarity;
        this.material = material;
    }

    // ===== 스탯 관련 메서드 =====

    /**
     * 총 스탯 계산 (기본 + 보너스 + 강화 + 젬)
     *
     * @param statName 스탯 이름
     * @return 총 스탯 값
     */
    public double getTotalStat(String statName) {
        double base = baseStats.getOrDefault(statName, 0.0);
        double bonus = bonusStats. getOrDefault(statName, 0.0);

        // 강화 보너스 적용
        double enhanceBonus = base * (enhanceLevel * enhanceMultiplier);

        // 희귀도 배율 적용
        double rarityMultiplier = rarity != null ? rarity. getStatMultiplier() : 1.0;

        return ((base + enhanceBonus) * rarityMultiplier) + bonus;
    }

    /**
     * 모든 스탯 반환
     */
    public Map<String, Double> getAllStats() {
        Map<String, Double> allStats = new HashMap<>();
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(baseStats.keySet());
        allKeys.addAll(bonusStats.keySet());

        for (String key : allKeys) {
            allStats.put(key, getTotalStat(key));
        }
        return allStats;
    }

    /**
     * 기본 스탯 설정
     */
    public void setBaseStat(String statName, double value) {
        baseStats.put(statName, value);
    }

    /**
     * 보너스 스탯 추가
     */
    public void addBonusStat(String statName, double value) {
        bonusStats.merge(statName, value, Double::sum);
    }

    // ===== 강화 관련 메서드 =====

    /**
     * 강화 가능 여부 확인
     */
    public boolean canEnhance() {
        return enhanceLevel < maxEnhanceLevel;
    }

    /**
     * 강화 실행
     *
     * @return 강화 성공 여부
     */
    public boolean enhance() {
        if (!canEnhance()) {
            return false;
        }
        enhanceLevel++;
        return true;
    }

    /**
     * 강화 성공 확률 계산
     */
    public double getEnhanceSuccessChance() {
        // 기본 확률 100%에서 강화 레벨당 10% 감소
        double baseChance = 100.0 - (enhanceLevel * 10);
        return Math.max(10.0, baseChance); // 최소 10%
    }

    /**
     * 강화 비용 계산
     */
    public double getEnhanceCost() {
        double baseCost = 100.0;
        double rarityMultiplier = rarity != null ?  rarity.getStatMultiplier() : 1.0;
        double slotMultiplier = slot != null ? slot.getEnhanceCostMultiplier() : 1.0;
        return baseCost * rarityMultiplier * slotMultiplier * Math.pow(1.5, enhanceLevel);
    }

    /**
     * 강화 레벨 표시 문자열
     */
    public String getEnhanceDisplay() {
        if (enhanceLevel <= 0) return "";
        return " &e+" + enhanceLevel;
    }

    // ===== 내구도 관련 메서드 =====

    /**
     * 내구도 감소
     *
     * @param amount 감소량
     * @return 파괴 여부
     */
    public boolean reduceDurability(int amount) {
        if (! breakable) return false;
        
        durability -= amount;
        if (durability <= 0) {
            durability = 0;
            return true; // 파괴됨
        }
        return false;
    }

    /**
     * 내구도 수리
     *
     * @param amount 수리량
     */
    public void repairDurability(int amount) {
        durability = Math.min(maxDurability, durability + amount);
    }

    /**
     * 완전 수리
     */
    public void fullRepair() {
        durability = maxDurability;
    }

    /**
     * 내구도 퍼센트
     */
    public double getDurabilityPercentage() {
        return maxDurability > 0 ? ((double) durability / maxDurability) * 100 :  100;
    }

    /**
     * 내구도가 낮은지 확인 (30% 이하)
     */
    public boolean isLowDurability() {
        return getDurabilityPercentage() <= 30;
    }

    // ===== 소켓/젬 관련 메서드 =====

    /**
     * 젬 삽입 가능 여부
     */
    public boolean canInsertGem() {
        return insertedGems.size() < socketSlots;
    }

    /**
     * 젬 삽입
     *
     * @param gemId 젬 ID
     * @return 삽입 성공 여부
     */
    public boolean insertGem(String gemId) {
        if (! canInsertGem()) return false;
        insertedGems.add(gemId);
        return true;
    }

    /**
     * 젬 제거
     *
     * @param gemId 젬 ID
     * @return 제거 성공 여부
     */
    public boolean removeGem(String gemId) {
        return insertedGems. remove(gemId);
    }

    /**
     * 모든 젬 제거
     */
    public List<String> removeAllGems() {
        List<String> removed = new ArrayList<>(insertedGems);
        insertedGems.clear();
        return removed;
    }

    // ===== 효과 관련 메서드 =====

    /**
     * 효과 추가
     */
    public void addEffect(EquipmentEffect effect) {
        effects. add(effect);
    }

    /**
     * 효과 제거
     */
    public void removeEffect(String effectId) {
        effects.removeIf(e -> e.getEffectId().equals(effectId));
    }

    /**
     * 특정 효과 보유 여부
     */
    public boolean hasEffect(String effectId) {
        return effects.stream().anyMatch(e -> e.getEffectId().equals(effectId));
    }

    /**
     * 효과 가져오기
     */
    public EquipmentEffect getEffect(String effectId) {
        return effects.stream()
                .filter(e -> e.getEffectId().equals(effectId))
                .findFirst()
                .orElse(null);
    }

    // ===== 요구 조건 확인 =====

    /**
     * 펫이 장착 가능한지 확인
     *
     * @param petLevel 펫 레벨
     * @param petType 펫 타입
     * @param petSpecies 펫 종족
     * @return 장착 가능 여부
     */
    public boolean canEquip(int petLevel, String petType, String petSpecies) {
        // 레벨 체크
        if (petLevel < requiredPetLevel) return false;

        // 타입 체크
        if (!requiredPetTypes. isEmpty() && !requiredPetTypes.contains(petType)) {
            return false;
        }

        // 종족 체크
        if (!requiredPetSpecies. isEmpty() && !requiredPetSpecies.contains(petSpecies)) {
            return false;
        }

        // 슬롯 호환성 체크
        if (slot != null) {
            try {
                com.multiverse.pet.model.PetType type = 
                        com.multiverse. pet.model.PetType.valueOf(petType.toUpperCase());
                if (!slot.isCompatibleWith(type)) {
                    return false;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        return true;
    }

    /**
     * 장착 불가 이유 반환
     */
    public String getCannotEquipReason(int petLevel, String petType, String petSpecies) {
        if (petLevel < requiredPetLevel) {
            return "펫 레벨 " + requiredPetLevel + " 필요 (현재:  " + petLevel + ")";
        }

        if (!requiredPetTypes. isEmpty() && !requiredPetTypes. contains(petType)) {
            return "이 장비는 " + String.join(", ", requiredPetTypes) + " 타입 펫만 장착 가능합니다.";
        }

        if (!requiredPetSpecies.isEmpty() && !requiredPetSpecies.contains(petSpecies)) {
            return "이 장비는 " + String.join(", ", requiredPetSpecies) + " 종족만 장착 가능합니다. ";
        }

        return null;
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

    // ===== 아이템 생성 =====

    /**
     * ItemStack 생성
     */
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material != null ? material : Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // 이름 설정
            String displayName = (rarity != null ?  rarity.getColorCode() : "&f") + 
                                 name + getEnhanceDisplay();
            meta.setDisplayName(displayName. replace("&", "§"));

            // Lore 생성
            List<String> itemLore = generateLore();
            meta.setLore(itemLore);

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
        List<String> itemLore = new ArrayList<>();

        // 슬롯 표시
        if (slot != null) {
            itemLore.add(slot. getLorePrefix().replace("&", "§"));
        }

        // 희귀도 표시
        if (rarity != null) {
            itemLore.add(("&7등급:  " + rarity. getColoredName()).replace("&", "§"));
        }

        itemLore.add("");

        // 스탯 표시
        if (! baseStats.isEmpty()) {
            itemLore.add("§6【 스탯 】");
            for (Map.Entry<String, Double> entry : getAllStats().entrySet()) {
                String sign = entry.getValue() >= 0 ? "+" : "";
                itemLore.add(("§7" + entry.getKey() + ": §a" + sign + 
                             String.format("%.1f", entry.getValue())).replace("&", "§"));
            }
            itemLore.add("");
        }

        // 효과 표시
        if (! effects.isEmpty()) {
            itemLore.add("§d【 효과 】");
            for (EquipmentEffect effect : effects) {
                itemLore.add(("§7- " + effect.getDescription()).replace("&", "§"));
            }
            itemLore.add("");
        }

        // 소켓 표시
        if (socketSlots > 0) {
            itemLore.add("§b【 소켓 " + insertedGems.size() + "/" + socketSlots + " 】");
            for (String gem : insertedGems) {
                itemLore.add("§7- §a" + gem);
            }
            for (int i = insertedGems.size(); i < socketSlots; i++) {
                itemLore.add("§7- §8[빈 소켓]");
            }
            itemLore.add("");
        }

        // 요구 조건 표시
        if (requiredPetLevel > 1) {
            itemLore.add(("§c요구 펫 레벨: " + requiredPetLevel).replace("&", "§"));
        }

        // 내구도 표시
        if (breakable) {
            String durabilityColor = isLowDurability() ? "§c" : "§a";
            itemLore.add("§7내구도:  " + durabilityColor + durability + "§7/" + maxDurability);
        }

        // 소울바운드 표시
        if (soulbound) {
            itemLore.add("§4§l귀속됨");
        }

        // 강화 레벨 표시
        if (enhanceLevel > 0) {
            itemLore.add(("§e강화:  +" + enhanceLevel + "/" + maxEnhanceLevel).replace("&", "§"));
        }

        // 커스텀 lore 추가
        if (!lore.isEmpty()) {
            itemLore. add("");
            for (String line : lore) {
                itemLore.add(line. replace("&", "§"));
            }
        }

        return itemLore;
    }

    // ===== Getter/Setter =====

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
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
        this. description = description;
    }

    public PetEquipSlot getSlot() {
        return slot;
    }

    public void setSlot(PetEquipSlot slot) {
        this. slot = slot;
    }

    public PetRarity getRarity() {
        return rarity;
    }

    public void setRarity(PetRarity rarity) {
        this.rarity = rarity;
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

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this. lore = lore;
    }

    public Map<String, Double> getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(Map<String, Double> baseStats) {
        this. baseStats = baseStats;
    }

    public Map<String, Double> getBonusStats() {
        return bonusStats;
    }

    public void setBonusStats(Map<String, Double> bonusStats) {
        this.bonusStats = bonusStats;
    }

    public int getEnhanceLevel() {
        return enhanceLevel;
    }

    public void setEnhanceLevel(int enhanceLevel) {
        this. enhanceLevel = enhanceLevel;
    }

    public int getMaxEnhanceLevel() {
        return maxEnhanceLevel;
    }

    public void setMaxEnhanceLevel(int maxEnhanceLevel) {
        this.maxEnhanceLevel = maxEnhanceLevel;
    }

    public double getEnhanceMultiplier() {
        return enhanceMultiplier;
    }

    public void setEnhanceMultiplier(double enhanceMultiplier) {
        this. enhanceMultiplier = enhanceMultiplier;
    }

    public int getRequiredPetLevel() {
        return requiredPetLevel;
    }

    public void setRequiredPetLevel(int requiredPetLevel) {
        this.requiredPetLevel = requiredPetLevel;
    }

    public List<String> getRequiredPetTypes() {
        return requiredPetTypes;
    }

    public void setRequiredPetTypes(List<String> requiredPetTypes) {
        this. requiredPetTypes = requiredPetTypes;
    }

    public List<String> getRequiredPetSpecies() {
        return requiredPetSpecies;
    }

    public void setRequiredPetSpecies(List<String> requiredPetSpecies) {
        this.requiredPetSpecies = requiredPetSpecies;
    }

    public List<EquipmentEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<EquipmentEffect> effects) {
        this.effects = effects;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public int getSetPieceNumber() {
        return setPieceNumber;
    }

    public void setSetPieceNumber(int setPieceNumber) {
        this.setPieceNumber = setPieceNumber;
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

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getMaxDurability() {
        return maxDurability;
    }

    public void setMaxDurability(int maxDurability) {
        this.maxDurability = maxDurability;
    }

    public boolean isBreakable() {
        return breakable;
    }

    public void setBreakable(boolean breakable) {
        this.breakable = breakable;
    }

    public int getSocketSlots() {
        return socketSlots;
    }

    public void setSocketSlots(int socketSlots) {
        this. socketSlots = socketSlots;
    }

    public List<String> getInsertedGems() {
        return insertedGems;
    }

    public void setInsertedGems(List<String> insertedGems) {
        this.insertedGems = insertedGems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetEquipmentData that = (PetEquipmentData) o;
        return Objects.equals(equipmentId, that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects. hash(equipmentId);
    }

    @Override
    public String toString() {
        return "PetEquipmentData{" +
                "equipmentId='" + equipmentId + '\'' +
                ", name='" + name + '\'' +
                ", slot=" + slot +
                ", rarity=" + rarity +
                ", enhanceLevel=" + enhanceLevel +
                '}';
    }

    // ===== 내부 클래스 =====

    /**
     * 장비 효과 데이터
     */
    public static class EquipmentEffect {
        private String effectId;
        private String name;
        private String description;
        private String effectType;
        private Map<String, Double> values;
        private double triggerChance;
        private int cooldown;

        public EquipmentEffect() {
            this.values = new HashMap<>();
            this.triggerChance = 100.0;
            this.cooldown = 0;
        }

        public EquipmentEffect(String effectId, String name, String description) {
            this();
            this.effectId = effectId;
            this.name = name;
            this.description = description;
        }

        public String getEffectId() {
            return effectId;
        }

        public void setEffectId(String effectId) {
            this.effectId = effectId;
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
            this. description = description;
        }

        public String getEffectType() {
            return effectType;
        }

        public void setEffectType(String effectType) {
            this.effectType = effectType;
        }

        public Map<String, Double> getValues() {
            return values;
        }

        public void setValues(Map<String, Double> values) {
            this.values = values;
        }

        public double getValue(String key) {
            return values. getOrDefault(key, 0.0);
        }

        public void setValue(String key, double value) {
            values.put(key, value);
        }

        public double getTriggerChance() {
            return triggerChance;
        }

        public void setTriggerChance(double triggerChance) {
            this.triggerChance = triggerChance;
        }

        public int getCooldown() {
            return cooldown;
        }

        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
        }
    }
}