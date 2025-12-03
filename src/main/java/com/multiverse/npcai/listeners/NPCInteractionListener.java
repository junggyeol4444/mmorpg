package com.multiverse.npcai.listeners;

import com.multiverse.npcai.managers.*;
import com.multiverse.npcai.models.NPCData;
import com.multiverse.npcai.utils.ConfigUtil;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * NPC 상호작용(우클릭 등) 리스너
 */
public class NPCInteractionListener implements Listener {

    private final NPCManager npcManager;
    private final ReputationManager reputationManager;
    private final DialogueManager dialogueManager;
    private final ShopManager shopManager;
    private final SkillTrainerManager skillTrainerManager;
    private final ConfigUtil config;

    public NPCInteractionListener(NPCManager npcManager, ReputationManager reputationManager,
                                 DialogueManager dialogueManager, ShopManager shopManager,
                                 SkillTrainerManager skillTrainerManager, ConfigUtil config) {
        this.npcManager = npcManager;
        this.reputationManager = reputationManager;
        this.dialogueManager = dialogueManager;
        this.shopManager = shopManager;
        this.skillTrainerManager = skillTrainerManager;
        this.config = config;
    }

    @EventHandler
    public void onPlayerInteractNPC(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() == null) return;
        int npcId = getNPCIdFromEntity(event.getRightClicked());
        if (npcId == -1) return;
        NPCData npc = npcManager.getNPC(npcId);
        if (npc == null) return;

        // 쿨다운 체크 및 기록
        if (!npcManager.canInteract(player, npc)) {
            player.sendMessage(config.getString("messages.interaction.cooldown"));
            return;
        }
        npcManager.setLastInteraction(player, npcId);

        // 상호작용 분기 처리
        npcManager.handleInteraction(player, npc);

        // 호감도 상호작용 카운트 증가
        reputationManager.addPoints(player, npcId, 1, "상호작용");
    }

    private int getNPCIdFromEntity(org.bukkit.entity.Entity entity) {
        // Citizens 연동용으로 entity에서 npcID 추출 (예시)
        if (entity.hasMetadata("NPC")) {
            return entity.getMetadata("NPC").get(0).asInt();
        }
        return -1;
    }
}