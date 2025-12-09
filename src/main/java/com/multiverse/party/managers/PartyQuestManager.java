package com.multiverse.party.managers;

import com.multiverse.party.PartyCore;
import com.multiverse.party.models.Party;
import com.multiverse.party.models.PartyQuest;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * 파티 퀘스트 시스템 (퀘스트 생성/완료/보상 처리)
 */
public class PartyQuestManager {

    private final PartyCore plugin;

    // 모든 파티 퀘스트 데이터 (파티ID -> 퀘스트)
    private final Map<UUID, PartyQuest> activeQuests = new HashMap<>();

    public PartyQuestManager(PartyCore plugin) {
        this.plugin = plugin;
    }

    public boolean startQuest(Party party, PartyQuest quest) {
        if (party == null || quest == null) return false;
        activeQuests.put(party.getPartyId(), quest);
        party.sendMessageToAll("&e파티 퀘스트 시작: " + quest.getTitle());
        return true;
    }

    public boolean completeQuest(Party party) {
        PartyQuest quest = activeQuests.remove(party.getPartyId());
        if (quest == null) return false;
        party.sendMessageToAll("&a파티 퀘스트 완료!");
        // 보상 처리 구현 등
        return true;
    }

    public PartyQuest getActiveQuest(Party party) {
        return party == null ? null : activeQuests.get(party.getPartyId());
    }
}