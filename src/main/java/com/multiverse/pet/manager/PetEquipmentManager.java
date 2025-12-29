package com.multiverse.pet.manager;

import com. multiverse.pet. PetCore;
import com. multiverse.pet. model.Pet;
import com.multiverse. pet.model.PetRarity;
import com. multiverse.pet. model.equipment.PetEquipSlot;
import com.multiverse. pet.model.equipment.PetEquipmentData;
import com.multiverse. pet.util.MessageUtil;
import org.bukkit.Material;
import org. bukkit.entity.Player;
import org.bukkit. inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org. bukkit.persistence. PersistentDataType;
import org. bukkit. NamespacedKey;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 펫 장비 매니저 클래스
 * 펫 장비 장착, 해제, 강화 관리
 */
public class PetEquipmentManager {

    private final PetCore plugin;

    // 장비 템플릿 저장소 (장비ID -> 장비 데이터)
    private final Map<String, PetEquipmentData> equipmentTemplates;

    // 세트 효과 저장소 (세트ID -> 세트 효과)
    private final Map<String, EquipmentSet> equipmentSets;

    // NBT 키
    private final NamespacedKey equipmentIdKey;
    private final NamespacedKey enhanceLevelKey;
    private final NamespacedKey soulboundKey;

    // 설정 값
    private double enhanceSuccessBaseRate;
    private double enhanceSuccessDecreasePerLevel;
    private double enhanceFailDegradeChance;
    private double enhanceCostMultiplier;
    private boolean allowEquipmentTrade;

    /**
     * 생성자
     */
    public PetEquipmentManager(PetCore plugin) {
        this. plugin = plugin;
        this.equipmentTemplates = new ConcurrentHashMap<>();
        this.equipmentSets = new ConcurrentHashMap<>();
        this.equipmentIdKey = new NamespacedKey(plugin, "pet_equipment_id");
        this.enhanceLevelKey = new NamespacedKey(plugin, "pet_equipment_enhance");
        this.soulboundKey = new NamespacedKey(plugin, "pet_equipment_soulbound");
        loadSettings();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.enhanceSuccessBaseRate = plugin. getConfigManager().getConfig()
                .getDouble("equipment.enhance-base-rate", 100.0);
        this.enhanceSuccessDecreasePerLevel = plugin.getConfigManager().getConfig()
                .getDouble("equipment.enhance-decrease-per-level", 10.0);
        this.enhanceFailDegradeChance = plugin.getConfigManager().getConfig()
                .getDouble("equipment.fail-degrade-chance", 30.0);
        this.enhanceCostMultiplier = plugin.getConfigManager().getConfig()
                .getDouble("equipment.enhance-cost-multiplier", 1.5);
        this.allowEquipmentTrade = plugin.getConfigManager().getConfig()
                .getBoolean("equipment. allow-trade", true);
    }

    // ===== 장비 템플릿 관리 =====

    /**
     * 장비 템플릿 등록
     */
    public void registerEquipment(PetEquipmentData equipment) {
        equipmentTemplates.put(equipment.getEquipmentId(), equipment);
    }

    /**
     * 장비 템플릿 가져오기
     */
    public PetEquipmentData getEquipmentTemplate(String equipmentId) {
        return equipmentTemplates.get(equipmentId);
    }

    /**
     * 슬롯별 장비 목록 가져오기
     */
    public List<PetEquipmentData> getEquipmentsBySlot(PetEquipSlot slot) {
        List<PetEquipmentData> result = new ArrayList<>();
        for (PetEquipmentData equipment : equipmentTemplates.values()) {
            if (equipment. getSlot() == slot) {
                result. add(equipment);
            }
        }
        return result;
    }

    /**
     * 희귀도별 장비 목록 가져오기
     */
    public List<PetEquipmentData> getEquipmentsByRarity(PetRarity rarity) {
        List<PetEquipmentData> result = new ArrayList<>();
        for (PetEquipmentData equipment :  equipmentTemplates. values()) {
            if (equipment.getRarity() == rarity) {
                result.add(equipment);
            }
        }
        return result;
    }

    // ===== 장비 장착/해제 (위 코드에서 계속) =====

    /**
     * 아이템에서 장비 데이터 추출
     */
    private PetEquipmentData extractEquipmentData(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();

        // 장비 ID 확인
        if (! container.has(equipmentIdKey, PersistentDataType.STRING)) {
            return null;
        }

        String equipmentId = container.get(equipmentIdKey, PersistentDataType.STRING);
        PetEquipmentData template = getEquipmentTemplate(equipmentId);

        if (template == null) {
            return null;
        }

        // 템플릿 복사 후 아이템 데이터 적용
        PetEquipmentData equipmentData = copyEquipmentData(template);

        // 강화 레벨 적용
        if (container.has(enhanceLevelKey, PersistentDataType.INTEGER)) {
            int enhanceLevel = container.get(enhanceLevelKey, PersistentDataType.INTEGER);
            equipmentData.setEnhanceLevel(enhanceLevel);
        }

        // 소울바운드 적용
        if (container.has(soulboundKey, PersistentDataType.STRING)) {
            String boundPlayer = container.get(soulboundKey, PersistentDataType.STRING);
            equipmentData.setSoulbound(true);
            equipmentData.setBoundToPlayer(UUID.fromString(boundPlayer));
        }

        return equipmentData;
    }

    /**
     * 장비 데이터 복사
     */
    private PetEquipmentData copyEquipmentData(PetEquipmentData original) {
        PetEquipmentData copy = new PetEquipmentData();
        copy.setEquipmentId(original.getEquipmentId());
        copy.setName(original.getName());
        copy.setDescription(original.getDescription());
        copy.setSlot(original.getSlot());
        copy.setRarity(original. getRarity());
        copy.setMaterial(original.getMaterial());
        copy.setCustomModelData(original.getCustomModelData());
        copy.setBaseStats(new HashMap<>(original.getBaseStats()));
        copy.setBonusStats(new HashMap<>(original.getBonusStats()));
        copy.setEnhanceLevel(original.getEnhanceLevel());
        copy.setMaxEnhanceLevel(original.getMaxEnhanceLevel());
        copy.setEnhanceMultiplier(original. getEnhanceMultiplier());
        copy.setRequiredPetLevel(original.getRequiredPetLevel());
        copy.setRequiredPetTypes(new ArrayList<>(original. getRequiredPetTypes()));
        copy.setRequiredPetSpecies(new ArrayList<>(original. getRequiredPetSpecies()));
        copy.setEffects(new ArrayList<>(original.getEffects()));
        copy.setSetId(original.getSetId());
        copy.setSetPieceNumber(original.getSetPieceNumber());
        copy.setTradeable(original.isTradeable());
        copy.setSocketSlots(original.getSocketSlots());
        copy.setInsertedGems(new ArrayList<>(original. getInsertedGems()));
        return copy;
    }

    /**
     * 장비 데이터를 아이템에 저장
     */
    public ItemStack createEquipmentItem(PetEquipmentData equipmentData) {
        ItemStack item = equipmentData.toItemStack();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();

            // 장비 ID 저장
            container.set(equipmentIdKey, PersistentDataType.STRING, equipmentData.getEquipmentId());

            // 강화 레벨 저장
            container.set(enhanceLevelKey, PersistentDataType.INTEGER, equipmentData.getEnhanceLevel());

            // 소울바운드 저장
            if (equipmentData. isSoulbound() && equipmentData. getBoundToPlayer() != null) {
                container.set(soulboundKey, PersistentDataType.STRING, 
                             equipmentData.getBoundToPlayer().toString());
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    // ===== 장비 강화 =====

    /**
     * 장비 강화
     *
     * @param player 플레이어
     * @param item 강화할 아이템
     * @return 강화된 아이템 (실패 시 null 또는 하락된 아이템)
     */
    public EnhanceResult enhanceEquipment(Player player, ItemStack item) {
        PetEquipmentData equipmentData = extractEquipmentData(item);
        if (equipmentData == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("equipment.invalid-item"));
            return new EnhanceResult(false, item, "유효하지 않은 장비입니다.");
        }

        // 최대 강화 확인
        if (! equipmentData.canEnhance()) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("equipment. max-enhance"));
            return new EnhanceResult(false, item, "최대 강화 단계입니다.");
        }

        // 강화 비용 계산
        double cost = equipmentData. getEnhanceCost() * enhanceCostMultiplier;

        // 비용 확인
        if (! plugin.getPlayerDataCoreHook().hasGold(player.getUniqueId(), cost)) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("equipment.not-enough-gold")
                    .replace("{cost}", String.format("%.0f", cost)));
            return new EnhanceResult(false, item, "골드가 부족합니다.");
        }

        // 비용 차감
        plugin.getPlayerDataCoreHook().withdrawGold(player. getUniqueId(), cost);

        // 성공 확률 계산
        double successRate = enhanceSuccessBaseRate - 
                            (equipmentData.getEnhanceLevel() * enhanceSuccessDecreasePerLevel);
        successRate = Math.max(10, successRate); // 최소 10%

        // 강화 시도
        boolean success = Math.random() * 100 < successRate;

        if (success) {
            // 강화 성공
            equipmentData.enhance();
            ItemStack newItem = createEquipmentItem(equipmentData);

            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("equipment.enhance-success")
                    . replace("{item}", equipmentData.getName())
                    .replace("{level}", String. valueOf(equipmentData. getEnhanceLevel())));

            return new EnhanceResult(true, newItem, "강화 성공!");
        } else {
            // 강화 실패
            boolean degrade = Math.random() * 100 < enhanceFailDegradeChance;

            if (degrade && equipmentData.getEnhanceLevel() > 0) {
                // 강화 단계 하락
                equipmentData.setEnhanceLevel(equipmentData.getEnhanceLevel() - 1);
                ItemStack newItem = createEquipmentItem(equipmentData);

                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("equipment.enhance-degrade")
                        .replace("{item}", equipmentData.getName())
                        . replace("{level}", String.valueOf(equipmentData.getEnhanceLevel())));

                return new EnhanceResult(false, newItem, "강화 실패!  강화 단계가 하락했습니다.");
            } else {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("equipment.enhance-fail")
                        . replace("{item}", equipmentData.getName()));

                return new EnhanceResult(false, item, "강화 실패!");
            }
        }
    }

    /**
     * 강화 성공 확률 조회
     */
    public double getEnhanceSuccessRate(ItemStack item) {
        PetEquipmentData equipmentData = extractEquipmentData(item);
        if (equipmentData == null) return 0;

        double successRate = enhanceSuccessBaseRate - 
                            (equipmentData.getEnhanceLevel() * enhanceSuccessDecreasePerLevel);
        return Math.max(10, successRate);
    }

    /**
     * 강화 비용 조회
     */
    public double getEnhanceCost(ItemStack item) {
        PetEquipmentData equipmentData = extractEquipmentData(item);
        if (equipmentData == null) return 0;

        return equipmentData.getEnhanceCost() * enhanceCostMultiplier;
    }

    // ===== 세트 효과 =====

    /**
     * 세트 효과 등록
     */
    public void registerEquipmentSet(EquipmentSet set) {
        equipmentSets.put(set.getSetId(), set);
    }

    /**
     * 세트 효과 확인 및 적용
     */
    public void checkAndApplySetBonus(Pet pet) {
        // 장착된 장비의 세트 정보 수집
        Map<String, Integer> setCounts = new HashMap<>();

        for (PetEquipSlot slot : PetEquipSlot.values()) {
            ItemStack item = pet.getEquipment(slot);
            if (item != null) {
                PetEquipmentData equipData = extractEquipmentData(item);
                if (equipData != null && equipData.getSetId() != null) {
                    setCounts.merge(equipData.getSetId(), 1, Integer::sum);
                }
            }
        }

        // 기존 세트 효과 제거
        pet.clearSetBonusStats();

        // 세트 효과 적용
        for (Map.Entry<String, Integer> entry : setCounts.entrySet()) {
            EquipmentSet set = equipmentSets.get(entry. getKey());
            if (set != null) {
                Map<String, Double> bonuses = set.getBonusesForPieceCount(entry.getValue());
                for (Map.Entry<String, Double> bonus : bonuses.entrySet()) {
                    pet.addSetBonusStat(bonus. getKey(), bonus.getValue());
                }
            }
        }
    }

    /**
     * 펫의 활성 세트 효과 정보 반환
     */
    public List<SetBonusInfo> getActiveSetBonuses(Pet pet) {
        List<SetBonusInfo> result = new ArrayList<>();
        Map<String, Integer> setCounts = new HashMap<>();

        for (PetEquipSlot slot : PetEquipSlot.values()) {
            ItemStack item = pet.getEquipment(slot);
            if (item != null) {
                PetEquipmentData equipData = extractEquipmentData(item);
                if (equipData != null && equipData.getSetId() != null) {
                    setCounts.merge(equipData.getSetId(), 1, Integer::sum);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : setCounts.entrySet()) {
            EquipmentSet set = equipmentSets.get(entry.getKey());
            if (set != null) {
                result.add(new SetBonusInfo(set, entry.getValue()));
            }
        }

        return result;
    }

    // ===== 소울바운드 =====

    /**
     * 장비 소울바운드 적용
     */
    public ItemStack applySoulbound(ItemStack item, UUID playerId) {
        PetEquipmentData equipmentData = extractEquipmentData(item);
        if (equipmentData == null) return item;

        equipmentData.bindToPlayer(playerId);
        return createEquipmentItem(equipmentData);
    }

    /**
     * 소울바운드 해제
     */
    public ItemStack removeSoulbound(ItemStack item) {
        PetEquipmentData equipmentData = extractEquipmentData(item);
        if (equipmentData == null) return item;

        equipmentData. unbind();
        equipmentData.setTradeable(allowEquipmentTrade);
        return createEquipmentItem(equipmentData);
    }

    // ===== 젬 소켓 =====

    /**
     * 젬 삽입
     */
    public boolean insertGem(Player player, ItemStack equipment, String gemId) {
        PetEquipmentData equipmentData = extractEquipmentData(equipment);
        if (equipmentData == null) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("equipment. invalid-item"));
            return false;
        }

        if (! equipmentData.canInsertGem()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("equipment.no-socket"));
            return false;
        }

        // 젬 삽입
        equipmentData.insertGem(gemId);

        // 아이템 업데이트
        ItemStack newItem = createEquipmentItem(equipmentData);
        equipment.setItemMeta(newItem.getItemMeta());

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("equipment.gem-inserted")
                .replace("{gem}", gemId));

        return true;
    }

    /**
     * 젬 제거
     */
    public boolean removeGem(Player player, ItemStack equipment, String gemId) {
        PetEquipmentData equipmentData = extractEquipmentData(equipment);
        if (equipmentData == null) {
            return false;
        }

        if (!equipmentData.removeGem(gemId)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("equipment.gem-not-found"));
            return false;
        }

        // 아이템 업데이트
        ItemStack newItem = createEquipmentItem(equipmentData);
        equipment.setItemMeta(newItem.getItemMeta());

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("equipment.gem-removed")
                .replace("{gem}", gemId));

        return true;
    }

    // ===== 장비 수리 =====

    /**
     * 장비 수리
     */
    public boolean repairEquipment(Player player, ItemStack equipment, int amount) {
        PetEquipmentData equipmentData = extractEquipmentData(equipment);
        if (equipmentData == null || ! equipmentData.isBreakable()) {
            return false;
        }

        equipmentData.repairDurability(amount);

        // 아이템 업데이트
        ItemStack newItem = createEquipmentItem(equipmentData);
        equipment.setItemMeta(newItem.getItemMeta());

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("equipment.repaired")
                .replace("{amount}", String.valueOf(amount)));

        return true;
    }

    /**
     * 완전 수리
     */
    public boolean fullRepairEquipment(Player player, ItemStack equipment, double cost) {
        if (! plugin.getPlayerDataCoreHook().hasGold(player.getUniqueId(), cost)) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("equipment. not-enough-gold"));
            return false;
        }

        PetEquipmentData equipmentData = extractEquipmentData(equipment);
        if (equipmentData == null || ! equipmentData.isBreakable()) {
            return false;
        }

        plugin.getPlayerDataCoreHook().withdrawGold(player. getUniqueId(), cost);
        equipmentData. fullRepair();

        // 아이템 업데이트
        ItemStack newItem = createEquipmentItem(equipmentData);
        equipment.setItemMeta(newItem.getItemMeta());

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("equipment.fully-repaired"));

        return true;
    }

    // ===== 스탯 재계산 =====

    /**
     * 장비 스탯 재계산
     */
    public void recalculateEquipmentStats(Pet pet) {
        // 기존 장비 스탯 초기화
        pet.clearEquipmentStats();

        // 각 슬롯의 장비 스탯 합산
        for (PetEquipSlot slot : PetEquipSlot.values()) {
            ItemStack item = pet. getEquipment(slot);
            if (item != null) {
                PetEquipmentData equipData = extractEquipmentData(item);
                if (equipData != null) {
                    Map<String, Double> stats = equipData.getAllStats();
                    for (Map.Entry<String, Double> entry : stats.entrySet()) {
                        double current = pet.getEquipmentStats().getOrDefault(entry.getKey(), 0.0);
                        pet.setEquipmentStat(entry.getKey(), current + entry.getValue());
                    }
                }
            }
        }

        // 세트 효과 적용
        checkAndApplySetBonus(pet);

        // 최대 체력 재계산
        pet.setMaxHealth(pet.getTotalStat("health"));
        if (pet.getHealth() > pet.getMaxHealth()) {
            pet.setHealth(pet.getMaxHealth());
        }
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadSettings();
    }

    // ===== Getter =====

    public Map<String, PetEquipmentData> getEquipmentTemplates() {
        return Collections.unmodifiableMap(equipmentTemplates);
    }

    public Map<String, EquipmentSet> getEquipmentSets() {
        return Collections.unmodifiableMap(equipmentSets);
    }

    // ===== 내부 클래스 =====

    /**
     * 강화 결과
     */
    public static class EnhanceResult {
        private final boolean success;
        private final ItemStack resultItem;
        private final String message;

        public EnhanceResult(boolean success, ItemStack resultItem, String message) {
            this. success = success;
            this.resultItem = resultItem;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public ItemStack getResultItem() {
            return resultItem;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 세트 효과 정보
     */
    public static class SetBonusInfo {
        private final EquipmentSet set;
        private final int pieceCount;

        public SetBonusInfo(EquipmentSet set, int pieceCount) {
            this.set = set;
            this.pieceCount = pieceCount;
        }

        public EquipmentSet getSet() {
            return set;
        }

        public int getPieceCount() {
            return pieceCount;
        }

        public int getTotalPieces() {
            return set.getTotalPieces();
        }

        public Map<String, Double> getActiveBonus() {
            return set.getBonusesForPieceCount(pieceCount);
        }
    }

    /**
     * 장비 세트
     */
    public static class EquipmentSet {
        private String setId;
        private String name;
        private String description;
        private int totalPieces;
        private Map<Integer, Map<String, Double>> setBonuses; // 피스 수 -> 보너스

        public EquipmentSet(String setId, String name, int totalPieces) {
            this. setId = setId;
            this. name = name;
            this.totalPieces = totalPieces;
            this.setBonuses = new HashMap<>();
        }

        public void addSetBonus(int pieceCount, String stat, double value) {
            setBonuses.computeIfAbsent(pieceCount, k -> new HashMap<>()).put(stat, value);
        }

        public Map<String, Double> getBonusesForPieceCount(int count) {
            Map<String, Double> result = new HashMap<>();
            for (Map.Entry<Integer, Map<String, Double>> entry : setBonuses.entrySet()) {
                if (count >= entry.getKey()) {
                    result.putAll(entry.getValue());
                }
            }
            return result;
        }

        public String getSetId() {
            return setId;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getTotalPieces() {
            return totalPieces;
        }

        public Map<Integer, Map<String, Double>> getSetBonuses() {
            return setBonuses;
        }
    }
}