package com.multiverse.npcai.managers;

import com.multiverse.npcai.NPCAICore;
import com.multiverse.npcai.models.NPCData;
import com.multiverse.npcai.models.enums.NPCType;
import com.multiverse.npcai.models.NPCAIBehavior;
import com.multiverse.npcai.utils.ConfigUtil;
import com.multiverse.npcai.data.NPCDataManager;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * NPC 생성/관리, Citizens 연동 및 핵심 로직
 */
public class NPCManager {

    private final NPCAICore plugin;
    private final NPCRegistry registry;
    private final NPCDataManager npcDataManager;
    private final ConfigUtil configUtil;

    public NPCManager(NPCAICore plugin, NPCRegistry registry, NPCDataManager npcDataManager, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.registry = registry;
        this.npcDataManager = npcDataManager;
        this.configUtil = configUtil;
    }

    // === NPC CRUD 및 조회 ===

    public NPCData getNPC(int npcId) {
        return npcDataManager.getNPCData(npcId);
    }

    public NPCData getNPCByCustomId(String customId) {
        return npcDataManager.getNPCDataByCustomId(customId);
    }

    public List<NPCData> getAllNPCs() {
        return npcDataManager.getAllNPCs();
    }

    public List<NPCData> getNPCsByType(NPCType type) {
        return getAllNPCs().stream().filter(npc -> npc.getType() == type).collect(Collectors.toList());
    }

    public List<NPCData> getNPCsInDimension(String dimension) {
        return getAllNPCs().stream().filter(npc -> npc.getDimension().equalsIgnoreCase(dimension)).collect(Collectors.toList());
    }

    // === NPC 생성/삭제 ===

    public NPCData createNPC(String name, NPCType type, Location loc) {
        // Citizens 엔티티 생성
        NPC npc = registry.createNPC(org.bukkit.entity.EntityType.PLAYER, name, loc);
        npc.spawn(loc);
        String customId = UUID.randomUUID().toString();
        NPCData data = new NPCData(npc.getId(), customId, name, type, "world", loc, "unknown",
            "", "", null, new ArrayList<>(), false, 1.0, true, 5, new HashMap<>());
        npcDataManager.saveNPCData(data);
        return data;
    }

    public void removeNPC(int npcId) {
        NPCData npc = getNPC(npcId);
        if (npc == null) return;
        NPC citizensNPC = registry.getById(npcId);
        if (citizensNPC != null) citizensNPC.destroy();
        npcDataManager.deleteNPCData(npcId);
    }

    // === 타입/설정 ===

    public void setNPCType(int npcId, NPCType type) {
        NPCData npc = getNPC(npcId);
        if (npc == null) return;
        npc.setType(type);
        npcDataManager.saveNPCData(npc);
    }

    public void setNPCDialogues(int npcId, List<String> dialogues) {
        NPCData npc = getNPC(npcId);
        if (npc == null) return;
        npc.setDialogues(dialogues);
        npcDataManager.saveNPCData(npc);
    }

    public void setNPCBehavior(int npcId, NPCAIBehavior behavior) {
        NPCData npc = getNPC(npcId);
        if (npc == null) return;
        npc.setBehavior(behavior);
        npcDataManager.saveNPCData(npc);
    }

    // === NPC 상호작용 ===

    public void handleInteraction(Player player, NPCData npc) {
        // 쿨다운, 상호작용 여부 체크
        if (!canInteract(player, npc)) return;
        switch (npc.getType()) {
            case MERCHANT:
                plugin.getShopManager().openShop(player, npc.getNpcId());
                break;
            case QUEST_GIVER:
                // Quest 기능은 외부 연동 예시
                // plugin.getQuestManager().openQuestGUI(player, npc.getNpcId());
                break;
            case SKILL_TRAINER:
                plugin.getSkillTrainerManager().openTrainerGUI(player, npc.getNpcId());
                break;
            case INFO_GIVER:
            case CITIZEN:
                plugin.getDialogueManager().startDialogue(player, npc);
                break;
            case BANKER:
                // plugin.getBankManager().openBankGUI(player);
                break;
            case GUARD:
                // 경비 상호작용: 경비 대화 또는 상태 표시
                plugin.getDialogueManager().startDialogue(player, npc);
                break;
        }
    }

    public boolean canInteract(Player player, NPCData npc) {
        // 쿨다운, 상호작용 가능 여부
        long last = npcDataManager.getLastInteraction(player.getUniqueId(), npc.getNpcId());
        int cooldown = npc.getInteractCooldown();
        if (System.currentTimeMillis() - last < cooldown * 1000L) return false;
        return npc.isInteractable();
    }

    public void setLastInteraction(Player player, int npcId) {
        npcDataManager.updateLastInteraction(player.getUniqueId(), npcId, System.currentTimeMillis());
    }

}