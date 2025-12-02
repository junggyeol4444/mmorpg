package com.multiverse.core.managers;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.data.YAMLDataManager;
import com.multiverse.core.models.FusionStatus;
import com.multiverse.core.models.Dimension;
import com.multiverse.core.models.Portal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FusionManager {
    private final MultiverseCore plugin;
    private final YAMLDataManager dataManager;
    private final DimensionManager dimensionManager;
    private final BalanceManager balanceManager;
    private final PortalManager portalManager;

    private FusionStatus fusionStatus;

    public FusionManager(MultiverseCore plugin, YAMLDataManager dataManager,
                        DimensionManager dimensionManager,
                        BalanceManager balanceManager,
                        PortalManager portalManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.dimensionManager = dimensionManager;
        this.balanceManager = balanceManager;
        this.portalManager = portalManager;
        loadFusionStatus();
    }

    public boolean isFused() {
        return fusionStatus != null && fusionStatus.isFused();
    }

    public int getCurrentStage() {
        return fusionStatus != null ? fusionStatus.getCurrentStage() : 0;
    }

    public void setStage(int stage) {
        if (fusionStatus == null) return;
        int oldStage = fusionStatus.getCurrentStage();
        fusionStatus.setCurrentStage(stage);
        fusionStatus.setStageStartTime(System.currentTimeMillis());
        fusionStatus.setStageEndTime(null); // 단계마다 종료 시간 결정
        applyStageEffects(stage);
        dataManager.saveFusionStatus(fusionStatus);
        plugin.getServer().getPluginManager().callEvent(
                new com.multiverse.core.events.FusionStageChangeEvent(oldStage, stage));
    }

    public void startFusion() {
        if (fusionStatus == null) {
            fusionStatus = new FusionStatus(false, 1, System.currentTimeMillis(), null);
        }
        fusionStatus.setFused(true);
        fusionStatus.setCurrentStage(1);
        fusionStatus.setStageStartTime(System.currentTimeMillis());
        fusionStatus.setStageEndTime(null);
        applyStageEffects(1);
        broadcastStageMessage(1);
        dataManager.saveFusionStatus(fusionStatus);
    }

    public void stopFusion() {
        if (fusionStatus == null) return;
        fusionStatus.setFused(false);
        fusionStatus.setCurrentStage(0);
        fusionStatus.setStageStartTime(System.currentTimeMillis());
        fusionStatus.setStageEndTime(null);
        dataManager.saveFusionStatus(fusionStatus);
        broadcastStageMessage(0);
    }

    public boolean checkFusionConditions() {
        // 설정에 따라 퀘스트 등 조건 체크
        return true; // stub
    }

    // 단계 진행
    public void advanceStage() {
        if (fusionStatus == null) return;
        int stage = fusionStatus.getCurrentStage();
        if (stage < 4) {
            setStage(stage + 1);
        }
    }

    public void applyStageEffects(int stage) {
        switch (stage) {
            case 1: // 차원벽 균열
                for (Dimension dim : dimensionManager.getAllDimensions()) {
                    balanceManager.adjustBalance(dim.getId(), -20, "fusion1");
                }
                spawnCrossMonsters();
                broadcastStageMessage(1);
                break;
            case 2: // 포털 개방
                createTemporaryPortals();
                for (Dimension dim : dimensionManager.getAllDimensions()) {
                    balanceManager.adjustBalance(dim.getId(), -30, "fusion2");
                }
                broadcastStageMessage(2);
                break;
            case 3: // 지형 융합
                modifyTerrain();
                for (Dimension dim : dimensionManager.getAllDimensions()) {
                    balanceManager.adjustBalance(dim.getId(), -50, "fusion3");
                }
                broadcastStageMessage(3);
                break;
            case 4: // 심연 개방 (엔드게임)
                broadcastStageMessage(4);
                break;
        }
    }

    public long getStageRemainingTime() {
        if (fusionStatus == null || fusionStatus.getStageEndTime() == null) return -1;
        return fusionStatus.getStageEndTime() - System.currentTimeMillis();
    }

    // 융합 효과
    public void createTemporaryPortals() {
        // 모든 차원 간 포탈 자동 생성
        List<Dimension> dims = dimensionManager.getAllDimensions();
        for (Dimension from : dims) {
            for (Dimension to : dims) {
                if (!from.getId().equals(to.getId())) {
                    portalManager.createPortal("fusion_" + from.getId() + "_" + to.getId(),
                            from.getId(), to.getId(),
                            Bukkit.getWorld(from.getWorldName()).getSpawnLocation(),
                            com.multiverse.core.models.enums.PortalType.TEMPORARY,
                            0);
                }
            }
        }
    }

    public void spawnCrossMonsters() {
        // 크로스 몬스터 스폰 (stub)
    }

    public void modifyTerrain() {
        // WorldEdit 등으로 지형 변경 구현 필요 (stub)
    }

    public void broadcastStageMessage(int stage) {
        String msg = plugin.getConfig().getString("fusion.messages.stage-" + stage, "");
        Bukkit.broadcastMessage(msg);
    }

    // 스케줄링 융합 단계 진행
    public void scheduleFusionStages() {
        // 각 단계별 시간 지나면 자동 advanceStage
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fusionStatus == null || !fusionStatus.isFused()) return;
                int stage = fusionStatus.getCurrentStage();
                if (stage >= 4) return;
                int durationHr = plugin.getConfig().getInt("fusion.stages.stage-" + stage + ".duration", 24);
                long elapsed = System.currentTimeMillis() - fusionStatus.getStageStartTime();
                if (elapsed > durationHr * 60 * 60 * 1000) {
                    advanceStage();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20 * 60, 20 * 60); // 1분마다 체크
    }

    // LOAD/SAVE
    public void loadFusionStatus() {
        fusionStatus = dataManager.loadFusionStatus();
        if (fusionStatus == null)
            fusionStatus = new FusionStatus(false, 0, null, null);
    }
}