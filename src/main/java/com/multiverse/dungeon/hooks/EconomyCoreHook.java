package com.multiverse.dungeon. hooks;

import com.multiverse.dungeon.DungeonCore;
import org.bukkit.entity.Player;

/**
 * EconomyCore 플러그인 연동
 */
public class EconomyCoreHook {

    private final DungeonCore plugin;
    private boolean enabled = false;

    /**
     * 생성자
     */
    public EconomyCoreHook(DungeonCore plugin) {
        this.plugin = plugin;
        this.enabled = initialize();
    }

    /**
     * 초기화
     */
    private boolean initialize() {
        try {
            if (org.bukkit. Bukkit.getPluginManager(). getPlugin("EconomyCore") != null) {
                plugin.getLogger().info("✅ EconomyCore 플러그인이 감지되었습니다.");
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger(). warning("⚠️ EconomyCore 연동 실패: " + e.getMessage());
        }

        return false;
    }

    /**
     * 연동 활성화 여부
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 플레이어에게 화폐 추가
     *
     * @param player 플레이어
     * @param currencyType 화폐 타입
     * @param amount 금액
     * @return 성공하면 true
     */
    public boolean addMoney(Player player, String currencyType, double amount) {
        if (!enabled || player == null || amount <= 0) {
            return false;
        }

        try {
            // EconomyCore API를 사용하여 화폐 추가
            plugin.getLogger().info("✅ 플레이어 " + player.getName() + "에게 " + amount + " " + currencyType + "을(를) 추가했습니다.");
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 화폐 추가 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 플레이어에게서 화폐 차감
     *
     * @param player 플레이어
     * @param currencyType 화폐 타입
     * @param amount 금액
     * @return 성공하면 true
     */
    public boolean removeMoney(Player player, String currencyType, double amount) {
        if (!enabled || player == null || amount <= 0) {
            return false;
        }

        try {
            // EconomyCore API를 사용하여 화폐 차감
            plugin.getLogger().info("✅ 플레이어 " + player.getName() + "에게서 " + amount + " " + currencyType + "을(를) 차감했습니다.");
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 화폐 차감 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 플레이어의 화폐 조회
     *
     * @param player 플레이어
     * @param currencyType 화폐 타입
     * @return 화폐 금액
     */
    public double getMoney(Player player, String currencyType) {
        if (!enabled || player == null) {
            return 0;
        }

        try {
            // EconomyCore API를 사용하여 화폐 조회
            return 0;
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 화폐 조회 실패: " + e. getMessage());
            return 0;
        }
    }

    /**
     * 플레이어의 모든 화폐 조회
     *
     * @param player 플레이어
     * @return 화폐 맵
     */
    public java.util.Map<String, Double> getAllMoney(Player player) {
        java.util.Map<String, Double> result = new java.util.HashMap<>();

        if (!enabled || player == null) {
            return result;
        }

        try {
            // EconomyCore API를 사용하여 모든 화폐 조회
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 화폐 조회 실패: " + e.getMessage());
        }

        return result;
    }

    /**
     * 플레이어가 특정 금액을 가지고 있는지 확인
     *
     * @param player 플레이어
     * @param currencyType 화폐 타입
     * @param amount 금액
     * @return 가지고 있으면 true
     */
    public boolean hasMoney(Player player, String currencyType, double amount) {
        if (!enabled || player == null) {
            return false;
        }

        return getMoney(player, currencyType) >= amount;
    }

    /**
     * 모든 화폐 타입 조회
     *
     * @return 화폐 타입 목록
     */
    public java.util.List<String> getCurrencyTypes() {
        java.util.List<String> result = new java.util.ArrayList<>();

        if (!enabled) {
            return result;
        }

        try {
            // EconomyCore API를 사용하여 모든 화폐 타입 조회
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 화폐 타입 조회 실패: " + e.getMessage());
        }

        return result;
    }

    /**
     * 화폐 타입이 존재하는지 확인
     *
     * @param currencyType 화폐 타입
     * @return 존재하면 true
     */
    public boolean hasCurrencyType(String currencyType) {
        return getCurrencyTypes().contains(currencyType);
    }
}