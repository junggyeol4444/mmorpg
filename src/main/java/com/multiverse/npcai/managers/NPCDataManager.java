package com.multiverse.npcai.managers;

import com.multiverse.npcai.data.DataManager;
import com.multiverse.npcai.models.NPCData;
import org.bukkit.Location;

import java.util.*;

/**
 * NPC 데이터 CRUD, 인메모리 캐싱 및 상호작용 상태 기록
 */
public class NPCDataManager {

    private final DataManager dataManager;

    // NPCId -> NPCData 메모리 캐시
    private final Map<Integer, NPCData> npcCache = new HashMap<>();
    // 플레이어별 NPC별 마지막 상호작용(쿨다운용)
    private final Map<UUID, Map<Integer, Long>> lastInteractions = new HashMap<>();

    public NPCDataManager(Object plugin, DataManager dataManager) {
        this.dataManager = dataManager;
        loadCache();
    }

    private void loadCache() {
        List<NPCData> allNPCs = dataManager.loadAllNPCs();
        for (NPCData npc : allNPCs) {
            npcCache.put(npc.getNpcId(), npc);
        }
    }

    // === NPCData CRUD ===
    public NPCData getNPCData(int npcId) {
        return npcCache.get(npcId);
    }

    public NPCData getNPCDataByCustomId(String customId) {
        for (NPCData npc : npcCache.values()) {
            if (npc.getCustomId().equals(customId)) return npc;
        }
        return null;
    }

    public List<NPCData> getAllNPCs() {
        return new ArrayList<>(npcCache.values());
    }

    public void saveNPCData(NPCData npc) {
        npcCache.put(npc.getNpcId(), npc);
        dataManager.saveNPC(npc);
    }

    public void deleteNPCData(int npcId) {
        npcCache.remove(npcId);
        dataManager.deleteNPC(npcId);
    }

    // === 상호작용 시간 기록 ===
    public long getLastInteraction(UUID playerUUID, int npcId) {
        lastInteractions.putIfAbsent(playerUUID, new HashMap<>());
        Map<Integer, Long> npcTimes = lastInteractions.get(playerUUID);
        return npcTimes.getOrDefault(npcId, 0L);
    }

    public void updateLastInteraction(UUID playerUUID, int npcId, long time) {
        lastInteractions.putIfAbsent(playerUUID, new HashMap<>());
        Map<Integer, Long> npcTimes = lastInteractions.get(playerUUID);
        npcTimes.put(npcId, time);
    }
}