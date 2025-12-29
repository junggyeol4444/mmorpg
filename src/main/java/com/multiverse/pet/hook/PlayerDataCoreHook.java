package com.multiverse.pet.hook;

import com.multiverse.pet.PetCore;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util. UUID;

/**
 * PlayerDataCore 연동 훅
 * 플레이어 경제, 레벨, 스탯 등 연동
 */
public class PlayerDataCoreHook {

    private final PetCore plugin;
    private Plugin playerDataCore;
    private boolean enabled;

    // 리플렉션용 캐시
    private Object economyManager;
    private Object playerManager;
    private Method getBalanceMethod;
    private Method withdrawMethod;
    private Method depositMethod;
    private Method getPlayerLevelMethod;
    private Method getPlayerStatMethod;

    public PlayerDataCoreHook(PetCore plugin) {
        this.plugin = plugin;
        this.enabled = false;
        initialize();
    }

    /**
     * 초기화
     */
    private void initialize() {
        playerDataCore = Bukkit.getPluginManager().getPlugin("PlayerDataCore");

        if (playerDataCore == null || !playerDataCore. isEnabled()) {
            plugin.getLogger().info("PlayerDataCore를 찾을 수 없습니다.  기본 기능으로 동작합니다.");
            return;
        }

        try {
            // API 클래스 로드
            Class<?> apiClass = Class. forName("com. multiverse.playerdata.api.PlayerDataAPI");

            // Economy Manager
            Method getEconomyManager = apiClass.getMethod("getEconomyManager");
            economyManager = getEconomyManager. invoke(null);

            if (economyManager != null) {
                Class<?> economyClass = economyManager.getClass();
                getBalanceMethod = economyClass.getMethod("getBalance", UUID.class);
                withdrawMethod = economyClass.getMethod("withdraw", UUID.class, double.class);
                depositMethod = economyClass.getMethod("deposit", UUID.class, double.class);
            }

            // Player Manager
            Method getPlayerManager = apiClass.getMethod("getPlayerManager");
            playerManager = getPlayerManager.invoke(null);

            if (playerManager != null) {
                Class<? > playerClass = playerManager.getClass();
                getPlayerLevelMethod = playerClass.getMethod("getLevel", UUID.class);
                getPlayerStatMethod = playerClass. getMethod("getStat", UUID.class, String.class);
            }

            enabled = true;
            plugin.getLogger().info("PlayerDataCore 연동 완료!");

        } catch (Exception e) {
            plugin.getLogger().warning("PlayerDataCore 연동 실패: " + e. getMessage());
            enabled = false;
        }
    }

    /**
     * 연동 활성화 여부
     */
    public boolean isEnabled() {
        return enabled;
    }

    // ===== 경제 시스템 =====

    /**
     * 잔액 조회
     */
    public double getBalance(UUID playerId) {
        if (!enabled || economyManager == null) {
            return 0;
        }

        try {
            Object result = getBalanceMethod.invoke(economyManager, playerId);
            return result != null ? (double) result : 0;
        } catch (Exception e) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().warning("[DEBUG] 잔액 조회 실패: " + e.getMessage());
            }
            return 0;
        }
    }

    /**
     * 골드 보유 확인
     */
    public boolean hasGold(UUID playerId, double amount) {
        return getBalance(playerId) >= amount;
    }

    /**
     * 골드 차감
     */
    public boolean withdrawGold(UUID playerId, double amount) {
        if (!enabled || economyManager == null) {
            return false;
        }

        if (! hasGold(playerId, amount)) {
            return false;
        }

        try {
            Object result = withdrawMethod. invoke(economyManager, playerId, amount);
            return result != null && (boolean) result;
        } catch (Exception e) {
            if (plugin. isDebugMode()) {
                plugin. getLogger().warning("[DEBUG] 골드 차감 실패: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * 골드 지급
     */
    public boolean depositGold(UUID playerId, double amount) {
        if (!enabled || economyManager == null) {
            return false;
        }

        try {
            Object result = depositMethod.invoke(economyManager, playerId, amount);
            return result != null && (boolean) result;
        } catch (Exception e) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().warning("[DEBUG] 골드 지급 실패: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * 골드 이체
     */
    public boolean transferGold(UUID fromPlayer, UUID toPlayer, double amount) {
        if (!hasGold(fromPlayer, amount)) {
            return false;
        }

        if (withdrawGold(fromPlayer, amount)) {
            if (depositGold(toPlayer, amount)) {
                return true;
            } else {
                // 롤백
                depositGold(fromPlayer, amount);
                return false;
            }
        }

        return false;
    }

    // ===== 플레이어 정보 =====

    /**
     * 플레이어 레벨 조회
     */
    public int getPlayerLevel(UUID playerId) {
        if (!enabled || playerManager == null) {
            Player player = Bukkit.getPlayer(playerId);
            return player != null ? player.getLevel() : 0;
        }

        try {
            Object result = getPlayerLevelMethod.invoke(playerManager, playerId);
            return result != null ? (int) result : 0;
        } catch (Exception e) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().warning("[DEBUG] 레벨 조회 실패: " + e.getMessage());
            }
            Player player = Bukkit.getPlayer(playerId);
            return player != null ? player.getLevel() : 0;
        }
    }

    /**
     * 플레이어 스탯 조회
     */
    public double getPlayerStat(UUID playerId, String statName) {
        if (!enabled || playerManager == null) {
            return 0;
        }

        try {
            Object result = getPlayerStatMethod.invoke(playerManager, playerId, statName);
            return result != null ?  (double) result : 0;
        } catch (Exception e) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().warning("[DEBUG] 스탯 조회 실패: " + e. getMessage());
            }
            return 0;
        }
    }

    /**
     * 플레이어 공격력
     */
    public double getPlayerAttack(UUID playerId) {
        return getPlayerStat(playerId, "attack");
    }

    /**
     * 플레이어 방어력
     */
    public double getPlayerDefense(UUID playerId) {
        return getPlayerStat(playerId, "defense");
    }

    // ===== 유틸리티 =====

    /**
     * 포맷된 잔액
     */
    public String getFormattedBalance(UUID playerId) {
        double balance = getBalance(playerId);
        return formatGold(balance);
    }

    /**
     * 골드 포맷팅
     */
    public String formatGold(double amount) {
        if (amount >= 1000000) {
            return String.format("%. 1fM", amount / 1000000);
        } else if (amount >= 1000) {
            return String.format("%. 1fK", amount / 1000);
        } else {
            return String.format("%.0f", amount);
        }
    }

    /**
     * 비용 지불 가능 확인 및 차감
     */
    public boolean tryPayment(Player player, double cost, String description) {
        UUID playerId = player. getUniqueId();

        if (!hasGold(playerId, cost)) {
            player.sendMessage(plugin.getConfigManager().getMessage("economy.not-enough-gold")
                    .replace("{cost}", formatGold(cost))
                    .replace("{balance}", getFormattedBalance(playerId)));
            return false;
        }

        if (withdrawGold(playerId, cost)) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().info("[DEBUG] " + player.getName() + " 골드 차감:  " + 
                        cost + " (" + description + ")");
            }
            return true;
        }

        return false;
    }

    /**
     * 리로드
     */
    public void reload() {
        enabled = false;
        economyManager = null;
        playerManager = null;
        initialize();
    }

    /**
     * 종료
     */
    public void shutdown() {
        enabled = false;
        economyManager = null;
        playerManager = null;
    }
}