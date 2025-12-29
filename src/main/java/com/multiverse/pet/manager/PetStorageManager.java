package com.multiverse.pet.manager;

import com. multiverse.pet. PetCore;
import com.multiverse. pet.model.Pet;
import com.multiverse. pet.model.PetRarity;
import com.multiverse.pet.model.PetStatus;
import com.multiverse.pet.model.storage.PetFilter;
import com. multiverse.pet. model.storage.SortType;
import com. multiverse.pet. util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 펫 보관함 매니저 클래스
 * 펫 보관함 관리, 필터링, 정렬
 */
public class PetStorageManager {

    private final PetCore plugin;

    // 플레이어별 정렬 설정
    private final Map<UUID, SortType> playerSortSettings;

    // 플레이어별 필터 설정
    private final Map<UUID, PetFilter> playerFilterSettings;

    // 플레이어별 페이지
    private final Map<UUID, Integer> playerPages;

    // 설정 값
    private int baseStorageSlots;
    private int maxStorageSlots;
    private int slotsPerPage;
    private boolean allowStorageExpansion;
    private double expansionCostPerSlot;

    /**
     * 생성자
     */
    public PetStorageManager(PetCore plugin) {
        this.plugin = plugin;
        this.playerSortSettings = new HashMap<>();
        this.playerFilterSettings = new HashMap<>();
        this.playerPages = new HashMap<>();
        loadSettings();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.baseStorageSlots = plugin.getConfigManager().getStorageSettings().getBaseSlots();
        this.maxStorageSlots = plugin.getConfigManager().getStorageSettings().getMaxSlots();
        this.slotsPerPage = plugin. getConfigManager().getStorageSettings().getSlotsPerPage();
        this.allowStorageExpansion = plugin.getConfigManager().getStorageSettings().isAllowExpansion();
        this.expansionCostPerSlot = plugin.getConfigManager().getStorageSettings().getExpansionCost();
    }

    // ===== 보관함 조회 =====

    /**
     * 플레이어의 모든 펫 가져오기
     */
    public List<Pet> getAllPets(UUID playerId) {
        return plugin.getPetManager().getAllPets(playerId);
    }

    /**
     * 필터링/정렬된 펫 목록 가져오기
     */
    public List<Pet> getFilteredAndSortedPets(UUID playerId) {
        List<Pet> allPets = getAllPets(playerId);

        // 필터 적용
        PetFilter filter = playerFilterSettings.get(playerId);
        if (filter != null && !filter.isEmpty()) {
            allPets = filter.filter(allPets);
        }

        // 정렬 적용
        SortType sortType = playerSortSettings.getOrDefault(playerId, SortType. getDefault());
        if (sortType. getComparator() != null) {
            allPets = sortType.sort(allPets);
        }

        return allPets;
    }

    /**
     * 페이지별 펫 목록 가져오기
     */
    public List<Pet> getPetsForPage(UUID playerId, int page) {
        List<Pet> filteredPets = getFilteredAndSortedPets(playerId);

        int startIndex = page * slotsPerPage;
        int endIndex = Math.min(startIndex + slotsPerPage, filteredPets.size());

        if (startIndex >= filteredPets.size()) {
            return new ArrayList<>();
        }

        return filteredPets.subList(startIndex, endIndex);
    }

    /**
     * 현재 페이지의 펫 목록
     */
    public List<Pet> getCurrentPagePets(UUID playerId) {
        int page = playerPages.getOrDefault(playerId, 0);
        return getPetsForPage(playerId, page);
    }

    /**
     * 총 페이지 수
     */
    public int getTotalPages(UUID playerId) {
        List<Pet> filteredPets = getFilteredAndSortedPets(playerId);
        return (int) Math.ceil((double) filteredPets.size() / slotsPerPage);
    }

    /**
     * 현재 페이지
     */
    public int getCurrentPage(UUID playerId) {
        return playerPages.getOrDefault(playerId, 0);
    }

    /**
     * 페이지 설정
     */
    public void setPage(UUID playerId, int page) {
        int maxPage = Math.max(0, getTotalPages(playerId) - 1);
        page = Math.max(0, Math. min(page, maxPage));
        playerPages.put(playerId, page);
    }

    /**
     * 다음 페이지
     */
    public void nextPage(UUID playerId) {
        int currentPage = getCurrentPage(playerId);
        int maxPage = Math. max(0, getTotalPages(playerId) - 1);
        if (currentPage < maxPage) {
            playerPages.put(playerId, currentPage + 1);
        }
    }

    /**
     * 이전 페이지
     */
    public void previousPage(UUID playerId) {
        int currentPage = getCurrentPage(playerId);
        if (currentPage > 0) {
            playerPages.put(playerId, currentPage - 1);
        }
    }

    // ===== 정렬 =====

    /**
     * 정렬 방식 설정
     */
    public void setSortType(UUID playerId, SortType sortType) {
        playerSortSettings.put(playerId, sortType);
        playerPages.put(playerId, 0); // 정렬 변경 시 첫 페이지로
    }

    /**
     * 현재 정렬 방식
     */
    public SortType getSortType(UUID playerId) {
        return playerSortSettings.getOrDefault(playerId, SortType.getDefault());
    }

    /**
     * 정렬 방식 순환
     */
    public void cycleSortType(UUID playerId) {
        SortType current = getSortType(playerId);
        SortType next = current.getNext();
        setSortType(playerId, next);
    }

    // ===== 필터 =====

    /**
     * 필터 설정
     */
    public void setFilter(UUID playerId, PetFilter filter) {
        playerFilterSettings. put(playerId, filter);
        playerPages.put(playerId, 0); // 필터 변경 시 첫 페이지로
    }

    /**
     * 현재 필터
     */
    public PetFilter getFilter(UUID playerId) {
        return playerFilterSettings.getOrDefault(playerId, new PetFilter());
    }

    /**
     * 필터 초기화
     */
    public void clearFilter(UUID playerId) {
        playerFilterSettings.remove(playerId);
        playerPages.put(playerId, 0);
    }

    /**
     * 필터 적용 여부
     */
    public boolean hasActiveFilter(UUID playerId) {
        PetFilter filter = playerFilterSettings.get(playerId);
        return filter != null && !filter.isEmpty();
    }

    // ===== 보관함 용량 =====

    /**
     * 플레이어의 보관함 용량
     */
    public int getStorageCapacity(UUID playerId) {
        int baseSlots = baseStorageSlots;

        // 확장 슬롯 추가
        int expansionSlots = plugin.getPlayerDataCoreHook().getStorageExpansion(playerId);

        return Math.min(baseSlots + expansionSlots, maxStorageSlots);
    }

    /**
     * 현재 보관 중인 펫 수
     */
    public int getStoredPetCount(UUID playerId) {
        return getAllPets(playerId).size();
    }

    /**
     * 남은 보관함 슬롯
     */
    public int getRemainingSlots(UUID playerId) {
        return getStorageCapacity(playerId) - getStoredPetCount(playerId);
    }

    /**
     * 보관함이 가득 찼는지 확인
     */
    public boolean isStorageFull(UUID playerId) {
        return getRemainingSlots(playerId) <= 0;
    }

    /**
     * 펫 보관 가능 여부
     */
    public boolean canStorePet(UUID playerId) {
        return ! isStorageFull(playerId);
    }

    // ===== 보관함 확장 =====

    /**
     * 보관함 확장
     */
    public boolean expandStorage(Player player, int slots) {
        UUID playerId = player. getUniqueId();

        if (!allowStorageExpansion) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("storage.expansion-disabled"));
            return false;
        }

        int currentCapacity = getStorageCapacity(playerId);
        int newCapacity = currentCapacity + slots;

        if (newCapacity > maxStorageSlots) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("storage.max-reached")
                    .replace("{max}", String.valueOf(maxStorageSlots)));
            return false;
        }

        // 비용 계산
        double cost = slots * expansionCostPerSlot;

        if (! plugin.getPlayerDataCoreHook().hasGold(playerId, cost)) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("storage. not-enough-gold")
                    .replace("{cost}", String. format("%.0f", cost)));
            return false;
        }

        // 비용 차감
        plugin.getPlayerDataCoreHook().withdrawGold(playerId, cost);

        // 확장 적용
        plugin.getPlayerDataCoreHook().addStorageExpansion(playerId, slots);

        // 알림
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("storage.expanded")
                .replace("{slots}", String.valueOf(slots))
                .replace("{total}", String.valueOf(newCapacity)));

        return true;
    }

    /**
     * 확장 비용 계산
     */
    public double getExpansionCost(int slots) {
        return slots * expansionCostPerSlot;
    }

    /**
     * 확장 가능한 슬롯 수
     */
    public int getExpandableSlots(UUID playerId) {
        return maxStorageSlots - getStorageCapacity(playerId);
    }

    // ===== 펫 관리 =====

    /**
     * 펫 보관함에 추가
     */
    public boolean addPetToStorage(Player player, Pet pet) {
        UUID playerId = player.getUniqueId();

        if (isStorageFull(playerId)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("storage.full"));
            return false;
        }

        return plugin.getPetManager().addNewPet(playerId, pet);
    }

    /**
     * 펫 보관함에서 제거
     */
    public boolean removePetFromStorage(Player player, UUID petId) {
        UUID playerId = player.getUniqueId();
        Pet pet = plugin.getPetManager().getPetById(playerId, petId);

        if (pet == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("storage.pet-not-found"));
            return false;
        }

        // 활성 상태면 먼저 해제
        if (pet.isActive()) {
            plugin. getPetManager().unsummonPet(player, petId);
        }

        // 다른 활동 중이면 제거 불가
        if (pet.getStatus() == PetStatus. BREEDING || pet.getStatus() == PetStatus. BATTLING) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("storage.pet-busy")
                    .replace("{status}", pet.getStatus().getDisplayName()));
            return false;
        }

        return plugin.getPetManager().removePet(playerId, petId);
    }

    /**
     * 펫 해방 (영구 삭제)
     */
    public boolean releasePet(Player player, UUID petId, boolean confirm) {
        if (! confirm) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("storage.release-confirm"));
            return false;
        }

        UUID playerId = player.getUniqueId();
        Pet pet = plugin. getPetManager().getPetById(playerId, petId);

        if (pet == null) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("storage.pet-not-found"));
            return false;
        }

        String petName = pet.getPetName();

        if (removePetFromStorage(player, petId)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("storage.released")
                    .replace("{name}", petName));
            return true;
        }

        return false;
    }

    // ===== 검색 =====

    /**
     * 이름으로 펫 검색
     */
    public List<Pet> searchByName(UUID playerId, String name) {
        List<Pet> allPets = getAllPets(playerId);
        String searchName = name.toLowerCase();

        return allPets.stream()
                .filter(pet -> pet.getPetName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
    }

    /**
     * 종족으로 펫 검색
     */
    public List<Pet> searchBySpecies(UUID playerId, String speciesId) {
        List<Pet> allPets = getAllPets(playerId);

        return allPets.stream()
                .filter(pet -> pet.getSpeciesId().equalsIgnoreCase(speciesId))
                .collect(Collectors.toList());
    }

    /**
     * 희귀도로 펫 검색
     */
    public List<Pet> searchByRarity(UUID playerId, PetRarity rarity) {
        List<Pet> allPets = getAllPets(playerId);

        return allPets.stream()
                .filter(pet -> pet.getRarity() == rarity)
                .collect(Collectors.toList());
    }

    /**
     * 레벨 범위로 펫 검색
     */
    public List<Pet> searchByLevelRange(UUID playerId, int minLevel, int maxLevel) {
        List<Pet> allPets = getAllPets(playerId);

        return allPets. stream()
                .filter(pet -> pet.getLevel() >= minLevel && pet.getLevel() <= maxLevel)
                .collect(Collectors.toList());
    }

    // ===== 통계 =====

    /**
     * 보관함 통계
     */
    public StorageStats getStorageStats(UUID playerId) {
        List<Pet> allPets = getAllPets(playerId);
        StorageStats stats = new StorageStats();

        stats.totalPets = allPets.size();
        stats.capacity = getStorageCapacity(playerId);
        stats.usedSlots = stats.totalPets;
        stats.remainingSlots = stats.capacity - stats. usedSlots;

        // 희귀도별 통계
        for (Pet pet : allPets) {
            stats.rarityCount.merge(pet.getRarity(), 1, Integer::sum);
        }

        // 타입별 통계
        for (Pet pet : allPets) {
            stats.typeCount.merge(pet.getType().name(), 1, Integer::sum);
        }

        // 평균 레벨
        if (! allPets.isEmpty()) {
            int totalLevel = allPets.stream().mapToInt(Pet::getLevel).sum();
            stats.averageLevel = (double) totalLevel / allPets.size();
        }

        // 최고 레벨 펫
        stats.highestLevelPet = allPets.stream()
                .max(Comparator. comparingInt(Pet::getLevel))
                .orElse(null);

        // 가장 희귀한 펫
        stats. rarestPet = allPets.stream()
                .max(Comparator. comparingInt(pet -> pet.getRarity().ordinal()))
                .orElse(null);

        return stats;
    }

    // ===== 즐겨찾기 =====

    /**
     * 펫 즐겨찾기 토글
     */
    public boolean toggleFavorite(UUID playerId, UUID petId) {
        Pet pet = plugin.getPetManager().getPetById(playerId, petId);
        if (pet == null) return false;

        pet.setFavorite(!pet.isFavorite());
        plugin.getPetManager().savePetData(playerId, pet);

        return pet.isFavorite();
    }

    /**
     * 즐겨찾기 펫 목록
     */
    public List<Pet> getFavoritePets(UUID playerId) {
        return getAllPets(playerId).stream()
                .filter(Pet::isFavorite)
                .collect(Collectors.toList());
    }

    // ===== 빠른 선택 =====

    /**
     * 가장 강한 펫
     */
    public Pet getStrongestPet(UUID playerId) {
        return getAllPets(playerId).stream()
                .filter(pet -> pet. getStatus() == PetStatus.STORED)
                .max(Comparator. comparingDouble(pet ->
                        pet. getTotalStat("attack") + pet.getTotalStat("defense")))
                .orElse(null);
    }

    /**
     * 가장 레벨이 높은 펫
     */
    public Pet getHighestLevelPet(UUID playerId) {
        return getAllPets(playerId).stream()
                .filter(pet -> pet. getStatus() == PetStatus.STORED)
                .max(Comparator.comparingInt(Pet::getLevel))
                .orElse(null);
    }

    /**
     * 전투 준비된 펫 목록
     */
    public List<Pet> getBattleReadyPets(UUID playerId) {
        return PetFilter.combatReady().filter(getAllPets(playerId));
    }

    /**
     * 교배 가능한 펫 목록
     */
    public List<Pet> getBreedingReadyPets(UUID playerId) {
        return PetFilter.breedingReady().filter(getAllPets(playerId));
    }

    // ===== 정리 =====

    /**
     * 플레이어 데이터 정리
     */
    public void cleanupPlayerData(UUID playerId) {
        playerSortSettings.remove(playerId);
        playerFilterSettings.remove(playerId);
        playerPages.remove(playerId);
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadSettings();
    }

    // ===== Getter =====

    public int getBaseStorageSlots() {
        return baseStorageSlots;
    }

    public int getMaxStorageSlots() {
        return maxStorageSlots;
    }

    public int getSlotsPerPage() {
        return slotsPerPage;
    }

    public boolean isAllowStorageExpansion() {
        return allowStorageExpansion;
    }

    public double getExpansionCostPerSlot() {
        return expansionCostPerSlot;
    }

    // ===== 내부 클래스 =====

    /**
     * 보관함 통계
     */
    public static class StorageStats {
        public int totalPets;
        public int capacity;
        public int usedSlots;
        public int remainingSlots;
        public Map<PetRarity, Integer> rarityCount = new EnumMap<>(PetRarity.class);
        public Map<String, Integer> typeCount = new HashMap<>();
        public double averageLevel;
        public Pet highestLevelPet;
        public Pet rarestPet;

        public String getSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("§6===== 보관함 통계 =====\n");
            sb.append("§7총 펫:  §f").append(totalPets).append("/").append(capacity).append("\n");
            sb.append("§7남은 슬롯: §f").append(remainingSlots).append("\n");
            sb.append("§7평균 레벨: §f").append(String.format("%.1f", averageLevel)).append("\n");

            if (highestLevelPet != null) {
                sb.append("§7최고 레벨:  §f").append(highestLevelPet.getPetName())
                        .append(" (Lv. ").append(highestLevelPet. getLevel()).append(")\n");
            }

            if (rarestPet != null) {
                sb.append("§7가장 희귀:  ").append(rarestPet.getRarity().getColoredName())
                        .append(" §f").append(rarestPet.getPetName()).append("\n");
            }

            sb.append("\n§7희귀도별:\n");
            for (PetRarity rarity : PetRarity.values()) {
                int count = rarityCount.getOrDefault(rarity, 0);
                if (count > 0) {
                    sb.append("  ").append(rarity.getColoredName()).append("§7:  §f").append(count).append("\n");
                }
            }

            return sb. toString();
        }
    }
}