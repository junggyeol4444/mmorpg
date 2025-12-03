package com.multiverse.npcai.listeners;

import com.multiverse.npcai.NPCAICore;
import com.multiverse.npcai.managers.NPCManager;
import com.multiverse.npcai.models.NPCData;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;

/**
 * Citizens NPC 생성/제거 관련 이벤트 리스너
 */
public class CitizensListener implements Listener {

    private final NPCAICore plugin;
    private final NPCManager npcManager;

    public CitizensListener(NPCAICore plugin, NPCManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onNPCSpawn(NPCSpawnEvent event) {
        NPC citizensNPC = event.getNPC();
        int npcId = citizensNPC.getId();
        NPCData npc = npcManager.getNPC(npcId);
        if (npc == null) {
            // Citizens NPC가 직접 추가된 경우 동기화
            npc = npcManager.createNPC(citizensNPC.getFullName(), npcManager.getNPCsByType(null).isEmpty() ? null : npcManager.getNPCsByType(null).get(0).getType(), citizensNPC.getEntity().getLocation());
        }
        citizensNPC.data().setPersistent("npcai-id", npcId);
    }

    @EventHandler
    public void onNPCDespawn(NPCDespawnEvent event) {
        NPC citizensNPC = event.getNPC();
        int npcId = citizensNPC.getId();
        // 필요시 despawn 시 로직 추가 가능
    }

    @EventHandler
    public void onNPCRemove(NPCRemoveEvent event) {
        NPC citizensNPC = event.getNPC();
        int npcId = citizensNPC.getId();
        npcManager.removeNPC(npcId);
    }
}