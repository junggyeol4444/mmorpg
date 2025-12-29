package com. multiverse.pet.                   config;

import org.bukkit.                  configuration.file.FileConfiguration;

import java.util.                   HashMap;
import java. util.                   Map;

/**
 * 보관함 설정
 * 펫 보관함 관련 설정
 */
public class StorageSettings {

    private final FileConfiguration config;
    private final String basePath = "storage";

    // 기본 용량
    private int defaultCapacity;
    private int maxCapacity;
    private int slotsPerPage;

    // 확장
    private boolean expansionEnabled;
    private int expansionSlots;
    private double expansionCost;
    private double expansionCostMultiplier;
    private int maxExpansions;

    // VIP 보너스
    private Map<String, Integer> vipBonusSlots;

    // 정렬
    private String defaultSortType;
    private boolean rememberSortType;

    // 필터
    private boolean filterEnabled;
    private boolean rememberFilter;

    // 즐겨찾기
    private boolean favoritesEnabled;
    private int maxFavorites;
    private boolean favoritesFirst;

    // 검색
    private boolean searchEnabled;
    private int minSearchLength;

    // 릴리즈
    private boolean releaseEnabled;
    private boolean releaseConfirmation;
    private double releaseExpReturn;
    private List<String> releaseBlockedRarities;

    // GUI
    private String menuTitle;
    private int menuRows;
    private boolean showPetPreview;

    public StorageSettings(FileConfiguration config) {
        this.config = config;
        this.vipBonusSlots = new HashMap<>();
        load();
    }

    /**
     * 설정 로드
     */
    private void load() {
        // 기본 용량
        defaultCapacity = config.getInt(basePath + ".default-capacity", 20);
        maxCapacity = config.getInt(basePath + ".max-capacity", 200);
        slotsPerPage = config.getInt(basePath + ".slots-per-page", 45);

        // 확장
        expansionEnabled = config. getBoolean(basePath + ".expansion. enabled", true);
        expansionSlots = config.getInt(basePath + ". expansion.slots", 5);
        expansionCost = config. getDouble(basePath + ".expansion.base-cost", 1000.0);
        expansionCostMultiplier = config. getDouble(basePath + ".expansion.cost-multiplier", 1.5);
        maxExpansions = config. getInt(basePath + ".expansion.max-expansions", 36);

        // VIP 보너스
        vipBonusSlots. clear();
        if (config.isConfigurationSection(basePath + ".vip-bonus")) {
            for (String rank : config.getConfigurationSection(basePath + ".vip-bonus").getKeys(false)) {
                vipBonusSlots.put(rank, config.getInt(basePath + ".vip-bonus." + rank, 0));
            }
        }

        // 기본 VIP 보너스
        if (vipBonusSlots.isEmpty()) {
            vipBonusSlots.put("vip", 10);
            vipBonusSlots.put("vip+", 20);
            vipBonusSlots.put("mvp", 30);
            vipBonusSlots.put("mvp+", 50);
        }

        // 정렬
        defaultSortType = config. getString(basePath + ".sort.default-type", "LEVEL");
        rememberSortType = config.getBoolean(basePath + ".sort.remember", true);

        // 필터
        filterEnabled = config.getBoolean(basePath + ". filter.enabled", true);
        rememberFilter = config.getBoolean(basePath + ".filter.remember", true);

        // 즐겨찾기
        favoritesEnabled = config.getBoolean(basePath + ".favorites.enabled", true);
        maxFavorites = config.getInt(basePath + ".favorites.max", 10);
        favoritesFirst = config.getBoolean(basePath + ". favorites.show-first", true);

        // 검색
        searchEnabled = config.getBoolean(basePath + ".search.enabled", true);
        minSearchLength = config. getInt(basePath + ".search.min-length", 2);

        // 릴리즈
        releaseEnabled = config.getBoolean(basePath + ".release.enabled", true);
        releaseConfirmation = config.getBoolean(basePath + ". release.confirmation", true);
        releaseExpReturn = config.getDouble(basePath + ".release.exp-return-percent", 50.0);
        releaseBlockedRarities = config.getStringList(basePath + ".release.blocked-rarities");

        // GUI
        menuTitle = config.getString(basePath + ". gui.title", "§6펫 보관함");
        menuRows = config.getInt(basePath + ". gui.rows", 6);
        showPetPreview = config.getBoolean(basePath + ".gui.show-preview", true);
    }

    // Java imports for List
    private java.util.List<String> releaseBlockedRarities;

    /**
     * 리로드
     */
    public void reload() {
        load();
    }

    // ===== Getter =====

    public int getDefaultCapacity() {
        return defaultCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getSlotsPerPage() {
        return slotsPerPage;
    }

    public boolean isExpansionEnabled() {
        return expansionEnabled;
    }

    public int getExpansionSlots() {
        return expansionSlots;
    }

    public double getExpansionCost() {
        return expansionCost;
    }

    public double getExpansionCostMultiplier() {
        return expansionCostMultiplier;
    }

    public int getMaxExpansions() {
        return maxExpansions;
    }

    public Map<String, Integer> getVipBonusSlots() {
        return vipBonusSlots;
    }

    public String getDefaultSortType() {
        return defaultSortType;
    }

    public boolean isRememberSortType() {
        return rememberSortType;
    }

    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    public boolean isRememberFilter() {
        return rememberFilter;
    }

    public boolean isFavoritesEnabled() {
        return favoritesEnabled;
    }

    public int getMaxFavorites() {
        return maxFavorites;
    }

    public boolean isFavoritesFirst() {
        return favoritesFirst;
    }

    public boolean isSearchEnabled() {
        return searchEnabled;
    }

    public int getMinSearchLength() {
        return minSearchLength;
    }

    public boolean isReleaseEnabled() {
        return releaseEnabled;
    }

    public boolean isReleaseConfirmation() {
        return releaseConfirmation;
    }

    public double getReleaseExpReturn() {
        return releaseExpReturn;
    }

    public java.util.List<String> getReleaseBlockedRarities() {
        return releaseBlockedRarities;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public int getMenuRows() {
        return menuRows;
    }

    public boolean isShowPetPreview() {
        return showPetPreview;
    }

    // ===== 계산 메서드 =====

    /**
     * VIP 보너스 슬롯 가져오기
     */
    public int getVipBonus(String rank) {
        return vipBonusSlots.getOrDefault(rank. toLowerCase(), 0);
    }

    /**
     * 확장 비용 계산
     */
    public double calculateExpansionCost(int currentExpansions) {
        return expansionCost * Math.pow(expansionCostMultiplier, currentExpansions);
    }

    /**
     * 총 확장 가능 슬롯
     */
    public int getMaxExpandedCapacity() {
        return defaultCapacity + (maxExpansions * expansionSlots);
    }

    /**
     * 페이지 수 계산
     */
    public int calculateTotalPages(int petCount) {
        return (int) Math.ceil((double) petCount / slotsPerPage);
    }

    /**
     * 릴리즈 차단 희귀도 확인
     */
    public boolean isReleaseBlocked(String rarity) {
        return releaseBlockedRarities. contains(rarity. toUpperCase());
    }

    /**
     * 용량 초과 확인
     */
    public boolean isCapacityExceeded(int currentPets, int capacity) {
        return currentPets >= capacity;
    }
}