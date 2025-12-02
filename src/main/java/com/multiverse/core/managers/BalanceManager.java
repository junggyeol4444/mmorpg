package com.multiverse.core.managers;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.data.YAMLDataManager;
import com.multiverse.core.models.BalanceLog;
import com.multiverse.core.models.Dimension;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BalanceManager {
    private final MultiverseCore plugin;
    private final YAMLDataManager dataManager;
    private final DimensionManager dimensionManager;
    private final Map<String, Integer> balanceMap = new HashMap<>();
    private final int BALANCE_MIN = 0, BALANCE_MAX = 100;

    public BalanceManager(MultiverseCore plugin, YAMLDataManager dataManager, DimensionManager dimensionManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.dimensionManager = dimensionManager;
        loadBalances();
    }

    // ===== 균형도 조회/설정 =====
    public int getBalance(String dimensionId) {
        return balanceMap.getOrDefault(dimensionId, 50);
    }

    public void setBalance(String dimensionId, int value) {
        int old = getBalance(dimensionId);
        int newVal = Math.max(BALANCE_MIN, Math.min(BALANCE_MAX, value));
        balanceMap.put(dimensionId, newVal);

        Dimension dim = dimensionManager.getDimension(dimensionId);
        if (dim != null) {
            dim.setBalanceValue(newVal);
            dimensionManager.updateDimension(dim);
        }
        applyBalanceEffects(dimensionId);
        logBalanceChange(dimensionId, old, newVal, "setBalance");
        saveBalances();
    }

    public void adjustBalance(String dimensionId, int delta, String reason) {
        int old = getBalance(dimensionId);
        int newVal = Math.max(BALANCE_MIN, Math.min(BALANCE_MAX, old + delta));
        balanceMap.put(dimensionId, newVal);

        Dimension dim = dimensionManager.getDimension(dimensionId);
        if (dim != null) {
            dim.setBalanceValue(newVal);
            dimensionManager.updateDimension(dim);
        }
        applyBalanceEffects(dimensionId);
        logBalanceChange(dimensionId, old, newVal, reason);
        saveBalances();
    }

    // ===== 균형도 효과 적용 =====
    public void applyBalanceEffects(String dimensionId) {
        Dimension dim = dimensionManager.getDimension(dimensionId);
        if (dim == null) return;
        int bal = getBalance(dimensionId);
        // (효과 적용은 실제 서버구현에서 각종 스폰/상태에 연결 필요)
        // 예: 몬스터 스폰율, 월드 환경, 플레이어 버프 등
    }

    public double getMonsterSpawnMultiplier(String dimensionId) {
        int bal = getBalance(dimensionId);
        if (bal <= 20) return 3.0;
        if (bal <= 40) return 1.5;
        if (bal >= 81) return 1.5;
        return 1.0;
    }

    public void applyPlayerEffects(Player player, String dimensionId) {
        int bal = getBalance(dimensionId);
        // (실제 버프/디버프 적용 코드 필요)
        // 예: "침식", "활성화" 등
    }

    // ===== 자동 조정 =====
    public void autoAdjustBalance(String dimensionId) {
        int bal = getBalance(dimensionId);
        int target = plugin.getConfig().getInt("balance.auto-adjust.target-value", 50);
        int amount = plugin.getConfig().getInt("balance.auto-adjust.adjust-amount", 5);
        if (bal < target) adjustBalance(dimensionId, amount, "autoAdjust");
        if (bal > target) adjustBalance(dimensionId, -amount, "autoAdjust");
    }

    public void scheduleAutoAdjust() {
        if (!plugin.getConfig().getBoolean("balance.auto-adjust.enabled", true)) return;
        int intervalHours = plugin.getConfig().getInt("balance.auto-adjust.interval", 168);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String dimId : balanceMap.keySet()) {
                    autoAdjustBalance(dimId);
                }
            }
        }.runTaskTimerAsynchronously(plugin, intervalHours * 20 * 60 * 60, intervalHours * 20 * 60 * 60);
    }

    public void scheduleBalanceDecay() {
        if (!plugin.getConfig().getBoolean("balance.decay.enabled", true)) return;
        double decayRate = plugin.getConfig().getDouble("balance.decay.rate", -0.5);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String dimId : balanceMap.keySet()) {
                    adjustBalance(dimId, (int) decayRate, "decay");
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20 * 60 * 60, 20 * 60 * 60);
    }

    // ===== 로그 =====
    public void logBalanceChange(String dimensionId, int oldValue, int newValue, String reason) {
        BalanceLog log = new BalanceLog(dimensionId, oldValue, newValue, newValue - oldValue, reason, "SYSTEM", System.currentTimeMillis());
        dataManager.addBalanceLog(log);
    }

    public List<BalanceLog> getBalanceLogs(String dimensionId, int limit) {
        List<BalanceLog> all = dataManager.loadBalanceLogs(limit);
        List<BalanceLog> result = new ArrayList<>();
        for (BalanceLog log : all) {
            if (log.getDimension().equals(dimensionId)) {
                result.add(log);
            }
        }
        return result;
    }

    // ===== LOAD/SAVE =====
    public void loadBalances() {
        List<Dimension> dims = dimensionManager.getAllDimensions();
        balanceMap.clear();
        for (Dimension d : dims) {
            balanceMap.put(d.getId(), d.getBalanceValue());
        }
    }

    public void saveBalances() {
        List<Dimension> dims = dimensionManager.getAllDimensions();
        for (Dimension d : dims) {
            d.setBalanceValue(balanceMap.getOrDefault(d.getId(), 50));
        }
        dimensionManager.saveDimensions();
    }
}