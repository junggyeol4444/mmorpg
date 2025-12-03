package com.multiverse.death.quest;

import com.multiverse.death.models.enums.QuestType;
import com.multiverse.death.models.RevivalQuest;
import org.bukkit.entity.Player;
import java.util.Set;
import java.util.HashSet;

/**
 * NPC와 대화하는 부활 퀘스트
 */
public class TalkToNPCsQuest {

    private Set<String> talkedNPCs = new HashSet<>();
    private RevivalQuest quest;
    private Set<String> requiredNPCs;

    public TalkToNPCsQuest(RevivalQuest quest, Set<String> requiredNPCs) {
        this.quest = quest;
        this.requiredNPCs = requiredNPCs;
    }

    public void talkToNPC(String npcId) {
        talkedNPCs.add(npcId);
        updateQuestProgress();
    }

    private void updateQuestProgress() {
        quest.setProgress(talkedNPCs.size());
        if (talkedNPCs.containsAll(requiredNPCs)) {
            quest.setCompleted(true);
        }
    }

    public RevivalQuest getQuest() {
        return quest;
    }

    public Set<String> getTalkedNPCs() {
        return talkedNPCs;
    }
}