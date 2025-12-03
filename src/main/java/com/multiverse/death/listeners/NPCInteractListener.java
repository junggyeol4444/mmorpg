package com.multiverse.death.listeners;

import com.multiverse.death.managers.NPCManager;
import com.multiverse.death.models.NetherRealmNPC;
import com.multiverse.death.utils.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NPCInteractListener implements Listener {

    private final NPCManager npcManager;
    private final ConfigUtil configUtil;

    public NPCInteractListener(NPCManager npcManager, ConfigUtil configUtil) {
        this.npcManager = npcManager;
        this.configUtil = configUtil;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        // Citizens의 NPC 체크: entity.hasMetadata("NPC")
        if (!entity.hasMetadata("NPC")) return;
        String npcId = entity.getMetadata("NPC").get(0).asString();

        NetherRealmNPC npc = npcManager.getNPC(npcId);
        if (npc != null) npcManager.handleNPCClick(player, npc);
    }
}