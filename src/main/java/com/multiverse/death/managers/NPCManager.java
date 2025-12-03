package com.multiverse.death.managers;

import com.multiverse.death.DeathAndRebirthCore;
import com.multiverse.death.data.DataManager;
import com.multiverse.death.models.NetherRealmNPC;
import com.multiverse.death.models.enums.NPCType;
import com.multiverse.death.models.enums.LocationType;
import com.multiverse.death.utils.MessageUtil;
import com.multiverse.death.utils.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class NPCManager {

    private final DeathAndRebirthCore plugin;
    private final DataManager dataManager;
    private final NetherRealmManager netherRealmManager;
    private final MessageUtil messageUtil;
    private final ConfigUtil configUtil;

    private final Map<String, NetherRealmNPC> npcCache = new HashMap<>();

    public NPCManager(DeathAndRebirthCore plugin,
                      DataManager dataManager,
                      NetherRealmManager netherRealmManager,
                      MessageUtil messageUtil,
                      ConfigUtil configUtil) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.netherRealmManager = netherRealmManager;
        this.messageUtil = messageUtil;
        this.configUtil = configUtil;
        loadNPCs();
    }

    // NPC 관리
    public void spawnNPC(NetherRealmNPC npc) {
        // Citizens 플러그인 연동, 혹은 자체 스폰
        dataManager.spawnNPC(npc);
        npcCache.put(npc.getId(), npc);
    }

    public NetherRealmNPC createNPC(NPCType type, String name, Location location) {
        String id = UUID.randomUUID().toString();
        NetherRealmNPC npc = new NetherRealmNPC();
        npc.setId(id);
        npc.setName(name);
        npc.setType(type);
        npc.setLocation(location);
        npc.setDialogues(getDefaultDialogues(type));
        return npc;
    }

    public void removeNPC(String id) {
        dataManager.removeNPC(id);
        npcCache.remove(id);
        // Citizens 연동시 despawn 처리 필요
    }

    public NetherRealmNPC getNPC(String id) {
        if (npcCache.containsKey(id)) return npcCache.get(id);
        NetherRealmNPC npc = dataManager.getNPC(id);
        if (npc != null) npcCache.put(id, npc);
        return npc;
    }

    public List<NetherRealmNPC> getAllNPCs() {
        return dataManager.getAllNPCs();
    }

    // NPC 상호작용
    public void handleNPCClick(Player player, NetherRealmNPC npc) {
        // GUI 등 역할에 따라 실행 분기
        switch (npc.getType()) {
            case REVIVAL_GUIDE:
                player.sendMessage(messageUtil.g("npcs.revival_guide"));
                // 부활 퀘스트 시작 GUI
                break;
            case INSURANCE_VENDOR:
                player.sendMessage(messageUtil.g("npcs.insurance_vendor"));
                // 보험 GUI
                break;
            case BANK_TELLER:
                player.sendMessage(messageUtil.g("npcs.bank_teller"));
                // 환전 안내
                break;
            case SOUL_MERCHANT:
                player.sendMessage(messageUtil.g("npcs.soul_merchant"));
                // 아이템 구매 GUI
                break;
            case CHECKPOINT_GUARD:
                player.sendMessage(messageUtil.g("npcs.checkpoint_guard"));
                // 차원 이동 안내
                break;
            case SOUL_GHOST:
                String dialogue = getRandomDialogue(npc);
                player.sendMessage(dialogue);
                // 퀘스트 진행도 업데이트
                break;
        }
    }

    public void openNPCGUI(Player player, NetherRealmNPC npc) {
        // GUI 연동 (보험/소울코인 등)
    }

    public String getRandomDialogue(NetherRealmNPC npc) {
        List<String> dialogues = npc.getDialogues();
        if (dialogues == null || dialogues.isEmpty()) return "&7...";
        int idx = (int) (Math.random() * dialogues.size());
        return dialogues.get(idx);
    }

    private List<String> getDefaultDialogues(NPCType type) {
        List<String> dialogues = new ArrayList<>();
        switch (type) {
            case REVIVAL_GUIDE:
                dialogues.add("어서오세요. 부활을 원하시나요?");
                dialogues.add("퀘스트를 완료하면 무료로 부활할 수 있습니다.");
                dialogues.add("또는 소울 코인으로 즉시 부활하실 수 있습니다.");
                break;
            case INSURANCE_VENDOR:
                dialogues.add("부활 보험에 가입하시겠습니까?");
                dialogues.add("보험 가입 시 사망 패널티를 받지 않습니다.");
                break;
            case BANK_TELLER:
                dialogues.add("차원 화폐를 소울 코인으로 환전할 수 있습니다.");
                break;
            case SOUL_MERCHANT:
                dialogues.add("특수 아이템을 판매합니다.");
                break;
            case CHECKPOINT_GUARD:
                dialogues.add("차원 이동 포탈은 저기입니다.");
                break;
            case SOUL_GHOST:
                dialogues.add("...나는 누구인가...");
                dialogues.add("여기는... 어디인가...");
                dialogues.add("살아있던 시절이 그립구나...");
                break;
        }
        return dialogues;
    }

    private void loadNPCs() {
        npcCache.clear();
        List<NetherRealmNPC> npcs = dataManager.getAllNPCs();
        for (NetherRealmNPC npc : npcs) npcCache.put(npc.getId(), npc);
    }
}