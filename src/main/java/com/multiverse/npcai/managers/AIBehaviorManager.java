package com.multiverse.npcai.managers;

import com.multiverse.npcai.NPCAICore;
import com.multiverse.npcai.models.NPCAIBehavior;
import com.multiverse.npcai.models.enums.BehaviorType;
import com.multiverse.npcai.models.NPCData;
import com.multiverse.npcai.utils.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * NPC AI 행동 관리, 패턴/순찰/추적 등
 */
public class AIBehaviorManager {

    private final NPCAICore plugin;
    private final NPCManager npcManager;
    private final ConfigUtil config;

    /** NPCId -> Behavior */
    private final Map<Integer, NPCAIBehavior> behaviorMap = new HashMap<>();

    public AIBehaviorManager(NPCAICore plugin, NPCManager npcManager, ConfigUtil config) {
        this.plugin = plugin;
        this.npcManager = npcManager;
        this.config = config;
    }

    public void setBehavior(int npcId, NPCAIBehavior behavior) {
        behaviorMap.put(npcId, behavior);
        NPCData npc = npcManager.getNPC(npcId);
        if (npc != null) {
            npc.setBehavior(behavior);
            // DB 저장 등
        }
    }

    public NPCAIBehavior getBehavior(int npcId) {
        return behaviorMap.getOrDefault(npcId, defaultBehavior(BehaviorType.STATIC));
    }

    public NPCAIBehavior defaultBehavior(BehaviorType type) {
        NPCAIBehavior beh = new NPCAIBehavior(type, new HashMap<>());
        if (type == BehaviorType.STATIC) {
            beh.setCanMove(false);
        } else {
            beh.setCanMove(true);
        }
        return beh;
    }

    // 순찰
    public void setPatrolPoints(int npcId, List<Location> points) {
        NPCAIBehavior beh = getBehavior(npcId);
        beh.setPatrolPoints(points);
        behaviorMap.put(npcId, beh);
    }

    public void addPatrolPoint(int npcId, Location point) {
        NPCAIBehavior beh = getBehavior(npcId);
        if (beh.getPatrolPoints() == null) beh.setPatrolPoints(new ArrayList<>());
        beh.getPatrolPoints().add(point);
        behaviorMap.put(npcId, beh);
    }

    public void startPatrol(int npcId) {
        // 반복 태스크에서 순찰 실행
    }

    // 배회
    public void setWanderArea(int npcId, Location center, double radius) {
        NPCAIBehavior beh = getBehavior(npcId);
        beh.setWanderCenter(center);
        beh.setWanderRadius(radius);
        behaviorMap.put(npcId, beh);
    }

    public void startWander(int npcId) {
        // 반복 태스크에서 배회 실행
    }

    // 추적
    public void setFollowTarget(int npcId, Player target) {
        NPCAIBehavior beh = getBehavior(npcId);
        beh.setFollowTarget(target.getUniqueId());
        behaviorMap.put(npcId, beh);
    }

    public void stopFollow(int npcId) {
        NPCAIBehavior beh = getBehavior(npcId);
        beh.setFollowTarget(null);
        behaviorMap.put(npcId, beh);
    }

    // 경비
    public void setGuardLocation(int npcId, Location loc, double radius) {
        NPCAIBehavior beh = getBehavior(npcId);
        beh.setGuardLocation(loc);
        beh.setGuardRadius(radius);
        behaviorMap.put(npcId, beh);
    }

    public void startGuard(int npcId) {
        // 반복 태스크에서 경비 실행
    }

    // AI 업데이트 (반복 태스크 운용)
    public void updateAI(int npcId) {
        NPCAIBehavior beh = getBehavior(npcId);
        // 행동 패턴별 동작
        // 예: STATIC-대기, PATROL-이동, GUARD-적 탐지 등
        // (실제 구현은 반복 태스크와 PathfindingHelper 활용)
    }

}