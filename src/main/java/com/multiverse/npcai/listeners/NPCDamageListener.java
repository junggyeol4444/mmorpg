package com.multiverse.npcai.listeners;

import com.multiverse.npcai.managers.*;
import com.multiverse.npcai.models.NPCData;
import com.multiverse.npcai.utils.ConfigUtil;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;

/**
 * NPC 공격/피해/보호 리스너
 */
public class NPCDamageListener implements Listener {

    private final NPCManager npcManager;
    private final ReputationManager reputationManager;
    private final AIBehaviorManager aiBehaviorManager;
    private final ConfigUtil config;

    public NPCDamageListener(NPCManager npcManager, ReputationManager reputationManager,
                            AIBehaviorManager aiBehaviorManager, ConfigUtil config) {
        this.npcManager = npcManager;
        this.reputationManager = reputationManager;
        this.aiBehaviorManager = aiBehaviorManager;
        this.config = config;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        int npcId = getNPCIdFromEntity(event.getEntity());
        if (npcId == -1) return;
        NPCData npc = npcManager.getNPC(npcId);
        if (npc == null) return;

        // 피해 처리: 예시로 호감도 급감
        reputationManager.removePoints(player, npcId, 20, "공격");

        // AI 행동 변경 예시 (경비, 도주 등)
        if (npc.getType().name().equalsIgnoreCase("GUARD")) {
            aiBehaviorManager.setGuardLocation(npcId, event.getEntity().getLocation(), 10.0);
            player.sendMessage(config.getString("messages.npc.guard-alert"));
        }
    }

    private int getNPCIdFromEntity(org.bukkit.entity.Entity entity) {
        if (entity.hasMetadata("NPC")) {
            return entity.getMetadata("NPC").get(0).asInt();
        }
        return -1;
    }
}