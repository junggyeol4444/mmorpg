package com.multiverse.dungeon.managers;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.data.enums.DungeonDifficulty;
import com. multiverse.dungeon.data. enums. InstanceStatus;
import com.multiverse. dungeon.data.model.*;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * 던전 인스턴스 관리 매니저
 */
public class InstanceManager {

    private final DungeonCore plugin;
    private final DungeonDataManager dataManager;
    private final Map<UUID, DungeonInstance> instances; // instanceId -> DungeonInstance
    private final Map<UUID, UUID> playerInstances; // playerId -> instanceId

    /**
     * 생성자
     */
    public InstanceManager(DungeonCore plugin, DungeonDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.instances = new HashMap<>();
        this. playerInstances = new HashMap<>();
        
        loadAllInstances();
    }

    /**
     * 모든 인스턴스 로드
     */
    private void loadAllInstances() {
        try {
            var loadedInstances = dataManager.loadAllInstances();
            for (var instance : loadedInstances) {
                if (instance. isActive()) {
                    instances. put(instance.getInstanceId(), instance);
                    for (var playerId : instance.getPlayers()) {
                        playerInstances.put(playerId, instance.getInstanceId());
                    }
                }
            }
            
            plugin.getLogger().info("✅ " + instances.size() + "개의 활성 인스턴스가 로드되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 인스턴스 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 새로운 인스턴스 생성
     *
     * @param dungeonId 던전 ID
     * @param partyId 파티 ID
     * @param difficulty 난이도
     * @return 생성된 인스턴스
     */
    public DungeonInstance createInstance(String dungeonId, UUID partyId, DungeonDifficulty difficulty) {
        var dungeon = plugin.getDungeonManager().getDungeon(dungeonId);
        if (dungeon == null) {
            return null;
        }

        UUID instanceId = UUID.randomUUID();
        DungeonInstance instance = new DungeonInstance(instanceId, dungeonId, partyId, 
            difficulty, dungeon. getTimeLimit());

        // 월드 이름 설정 (임시)
        instance.setWorldName(dungeonId + "_" + instanceId. toString().substring(0, 8));
        instance.setSpawnLocation(dungeon.getSpawn());
        instance.setStatus(InstanceStatus.CREATING);

        instances.put(instanceId, instance);

        // 이벤트 호출
        var party = plugin.getPartyManager().getParty(partyId);
        if (party != null) {
            var event = new com.multiverse.dungeon.events.DungeonEnterEvent(party, dungeon, difficulty);
            org.bukkit. Bukkit.getPluginManager(). callEvent(event);
        }

        plugin.getLogger().info("✅ 인스턴스 생성됨: " + instanceId + " (던전: " + dungeonId + ")");

        return instance;
    }

    /**
     * 인스턴스 ID로 인스턴스 조회
     *
     * @param instanceId 인스턴스 ID
     * @return 인스턴스 객체, 없으면 null
     */
    public DungeonInstance getInstance(UUID instanceId) {
        return instances.get(instanceId);
    }

    /**
     * 플레이어의 현재 인스턴스 조회
     *
     * @param player 플레이어
     * @return 인스턴스 객체, 없으면 null
     */
    public DungeonInstance getPlayerInstance(Player player) {
        if (player == null) {
            return null;
        }
        UUID instanceId = playerInstances.get(player.getUniqueId());
        return instanceId != null ? instances.get(instanceId) : null;
    }

    /**
     * 인스턴스에 플레이어 추가
     *
     * @param instanceId 인스턴스 ID
     * @param player 추가할 플레이어
     * @return 성공하면 true
     */
    public boolean addPlayerToInstance(UUID instanceId, Player player) {
        if (player == null) {
            return false;
        }

        DungeonInstance instance = instances.get(instanceId);
        if (instance == null || !instance.isActive()) {
            return false;
        }

        instance.addPlayer(player. getUniqueId());
        playerInstances.put(player. getUniqueId(), instanceId);

        return true;
    }

    /**
     * 인스턴스에서 플레이어 제거
     *
     * @param instanceId 인스턴스 ID
     * @param playerId 플레이어 ID
     * @return 성공하면 true
     */
    public boolean removePlayerFromInstance(UUID instanceId, UUID playerId) {
        DungeonInstance instance = instances.get(instanceId);
        if (instance == null) {
            return false;
        }

        instance.removePlayer(playerId);
        playerInstances.remove(playerId);

        return true;
    }

    /**
     * 인스턴스 완료 처리
     *
     * @param instanceId 인스턴스 ID
     */
    public void completeInstance(UUID instanceId) {
        DungeonInstance instance = instances.get(instanceId);
        if (instance == null) {
            return;
        }

        instance. setStatus(InstanceStatus. COMPLETED);
        instance.setEndTime(System.currentTimeMillis());

        long clearTime = instance.getEndTime() - instance.getStartTime();
        int score = instance.getProgress(). getScore();

        var event = new com.multiverse.dungeon.events.DungeonCompleteEvent(instance, clearTime, score);
        org. bukkit.Bukkit.getPluginManager().callEvent(event);

        plugin.getLogger().info("✅ 인스턴스 완료: " + instanceId + " (클리어 시간: " 
            + instance.getElapsedTimeFormatted() + ")");
    }

    /**
     * 인스턴스 실패 처리
     *
     * @param instanceId 인스턴스 ID
     * @param reason 실패 사유
     */
    public void failInstance(UUID instanceId, String reason) {
        DungeonInstance instance = instances.get(instanceId);
        if (instance == null) {
            return;
        }

        instance.setStatus(InstanceStatus.FAILED);
        instance.setEndTime(System.currentTimeMillis());

        var event = new com.multiverse.dungeon.events.DungeonFailEvent(instance, reason);
        org.bukkit. Bukkit.getPluginManager().callEvent(event);

        plugin.getLogger().warning("❌ 인스턴스 실패: " + instanceId + " (사유: " + reason + ")");
    }

    /**
     * 만료된 인스턴스 정리
     */
    public void cleanupExpiredInstances() {
        List<UUID> toRemove = new ArrayList<>();

        for (var entry : instances. entrySet()) {
            var instance = entry.getValue();

            // 완료 또는 실패된 인스턴스 정리
            if (instance.isFinished()) {
                if (instance.getEndTime() == 0 || 
                    (System.currentTimeMillis() - instance.getEndTime()) > 300000) { // 5분
                    toRemove.add(entry.getKey());
                }
            }

            // 플레이어가 없는 인스턴스 정리
            if (instance. hasNoPlayers() && instance.isActive()) {
                failInstance(entry.getKey(), 
                    com.multiverse.dungeon.events.DungeonFailEvent. FailReason.ABANDONED);
            }
        }

        for (var instanceId : toRemove) {
            var instance = instances.remove(instanceId);
            
            for (var playerId : instance.getPlayers()) {
                playerInstances.remove(playerId);
            }

            try {
                dataManager.deleteInstance(instanceId);
            } catch (Exception e) {
                plugin.getLogger().warning("⚠️ 인스턴스 삭제 실패: " + e.getMessage());
            }
        }

        if (! toRemove.isEmpty()) {
            plugin.getLogger().info("✅ " + toRemove.size() + "개의 인스턴스가 정리되었습니다.");
        }
    }

    /**
     * 활성 인스턴스 개수
     *
     * @return 활성 인스턴스 개수
     */
    public int getActiveInstanceCount() {
        return (int) instances.values().stream()
            .filter(DungeonInstance::isActive)
            .count();
    }

    /**
     * 모든 인스턴스 저장
     */
    public void saveAllInstances() {
        try {
            for (var instance : instances.values()) {
                dataManager.saveInstance(instance);
            }
            plugin.getLogger().info("✅ 모든 인스턴스가 저장되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 인스턴스 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}