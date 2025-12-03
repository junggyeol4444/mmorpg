package com.multiverse.npcai.listeners;

import com.multiverse.npcai.managers.AIBehaviorManager;
import com.multiverse.npcai.managers.NPCManager;
import com.multiverse.npcai.models.NPCData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;

import java.util.*;

/**
 * 플레이어 이동 감지 (NPC 배회/경비 등 트리거)
 */
public class PlayerMoveListener implements Listener {

    private final AIBehaviorManager aiBehaviorManager;
    private final NPCManager npcManager;

    public PlayerMoveListener(AIBehaviorManager aiBehaviorManager, NPCManager npcManager) {
        this.aiBehaviorManager = aiBehaviorManager;
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to == null || from == null) return;
        // 간단히 거리가 조금만 이동해도 체크
        if (to.distanceSquared(from) < 0.1) return;

        List<NPCData> npcs = npcManager.getAllNPCs();
        for (NPCData npc : npcs) {
            // NPC와의 거리 계산, 근접 시 AI 행동(배회, 추적 등) 트리거 예시
            double distance = npc.getLocation().distance(to);
            if (distance < 10.0) {
                aiBehaviorManager.updateAI(npc.getNpcId());
            }
        }
    }
}