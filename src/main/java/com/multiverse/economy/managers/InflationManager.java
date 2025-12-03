package com.multiverse.economy.managers;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.models.InflationControl;
import com.multiverse.economy.models.BurnRecord;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.utils.MessageUtil;

import java.util.*;

public class InflationManager {

    private final EconomyCore plugin;
    private final ConfigUtil config;
    private final StatisticsManager statisticsManager;
    private final CurrencyManager currencyManager;
    private final EconomyDataManager economyDataManager;
    private final MessageUtil msg;

    // 통제 정보 메모리 캐시, 실제 데이터는 EconomyDataManager에 위임
    private final Map<String, InflationControl> controls = new HashMap<>();
    private final Map<String, List<BurnRecord>> burnHistory = new HashMap<>();

    public InflationManager(EconomyCore plugin, ConfigUtil config, StatisticsManager statisticsManager, CurrencyManager currencyManager, EconomyDataManager economyDataManager, MessageUtil msg) {
        this.plugin = plugin;
        this.config = config;
        this.statisticsManager = statisticsManager;
        this.currencyManager = currencyManager;
        this.economyDataManager = economyDataManager;
        this.msg = msg;
        loadControls();
    }

    private void loadControls() {
        // inflation_control.yml 파일에서 load
        controls.clear();
        // ...실제 파일 I/O 생략
    }

    // 인플레이션율 계산
    public double getInflationRate(String currencyId) {
        InflationControl ctrl = getControl(currencyId);
        if (ctrl.getTargetCirculation() == 0) return 0;
        double rate = ((ctrl.getCurrentCirculation() - ctrl.getTargetCirculation()) / ctrl.getTargetCirculation()) * 100.0;
        ctrl.setInflationRate(rate);
        return rate;
    }

    public boolean isOverTarget(String currencyId) {
        return getControl(currencyId).getCurrentCirculation() > getControl(currencyId).getTargetCirculation();
    }

    public double getExcessAmount(String currencyId) {
        InflationControl ctrl = getControl(currencyId);
        double excess = ctrl.getCurrentCirculation() - ctrl.getTargetCirculation();
        return excess > 0 ? excess : 0;
    }

    // 소각 실행 (사유 기록)
    public void burnCurrency(String currencyId, double amount, String reason) {
        InflationControl ctrl = getControl(currencyId);
        if (amount <= 0 || ctrl == null) return;
        ctrl.setTotalBurned(ctrl.getTotalBurned() + amount);
        ctrl.setLastBurnAmount(amount);
        ctrl.setLastBurnDate(System.currentTimeMillis());
        ctrl.setCurrentCirculation(ctrl.getCurrentCirculation() - amount);
        addBurnRecord(currencyId, amount, reason);
        economyDataManager.saveInflationControl(currencyId, ctrl);
        msg.sendAdminBurn(currencyId, amount, reason);
    }

    public void autoControl(String currencyId) {
        InflationControl ctrl = getControl(currencyId);
        double threshold = ctrl.getBurnThreshold();
        double rate = getInflationRate(currencyId);
        if (rate > threshold) {
            // burn-percentage: 초과금의 n% 소각
            double burnPercent = config.getDouble("inflation.auto-control.burn-percentage", 10.0);
            double burnAmount = getExcessAmount(currencyId) * burnPercent / 100.0;
            burnCurrency(currencyId, burnAmount, "자동 인플레이션 통제 소각");
        }
    }

    public void emergencyBurn(String currencyId) {
        InflationControl ctrl = getControl(currencyId);
        double threshold = config.getDouble("inflation.emergency.threshold", 30.0);
        double rate = getInflationRate(currencyId);
        if (rate > threshold) {
            double burnPercent = config.getDouble("inflation.emergency.burn-percentage", 20.0);
            double burnAmount = getExcessAmount(currencyId) * burnPercent / 100.0;
            burnCurrency(currencyId, burnAmount, "긴급 소각 이벤트");
        }
    }

    public double getTotalBurned(String currencyId) {
        return getControl(currencyId).getTotalBurned();
    }

    public List<BurnRecord> getBurnHistory(String currencyId, int limit) {
        List<BurnRecord> history = burnHistory.getOrDefault(currencyId, new ArrayList<>());
        return history.subList(Math.max(0, history.size() - limit), history.size());
    }

    private void addBurnRecord(String currencyId, double amount, String reason) {
        BurnRecord record = new BurnRecord(System.currentTimeMillis(), currencyId, amount, reason);
        List<BurnRecord> list = burnHistory.computeIfAbsent(currencyId, k -> new ArrayList<>());
        list.add(record);
        // 실제 파일 저장 등은 EconomyDataManager 역할
    }

    private InflationControl getControl(String currencyId) {
        return controls.getOrDefault(currencyId, new InflationControl(currencyId, config.getDouble("inflation.targets." + currencyId, 100000000.0),
                economyDataManager.getTotalCirculation(currencyId), 0, 0, 0, 0, true, config.getDouble("inflation.auto-control.burn-threshold", 20.0)));
    }

    public String getInflationInfo(String currencyId) {
        InflationControl ctrl = getControl(currencyId);
        StringBuilder sb = new StringBuilder();
        sb.append(currencyId).append(" 인플레이션 정보\n");
        sb.append("목표 유통량: ").append(ctrl.getTargetCirculation()).append("\n");
        sb.append("현재 유통량: ").append(ctrl.getCurrentCirculation()).append("\n");
        sb.append("인플레이션율: ").append(String.format("%.2f%%", getInflationRate(currencyId))).append("\n");
        sb.append("총 소각량: ").append(ctrl.getTotalBurned()).append("\n");
        sb.append("최근 소각: ").append(ctrl.getLastBurnAmount()).append(" (").append(new Date(ctrl.getLastBurnDate())).append(")\n");
        sb.append("자동 통제: ").append(ctrl.isAutoControlEnabled() ? "ON" : "OFF").append("\n");
        sb.append("임계치: ").append(ctrl.getBurnThreshold()).append("%\n");
        return sb.toString();
    }
}