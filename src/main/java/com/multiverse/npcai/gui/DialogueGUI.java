package com.multiverse.npcai.gui;

import com.multiverse.npcai.models.NPCReaction;
import com.multiverse.npcai.models.enums.ReputationLevel;
import com.multiverse.npcai.models.enums.InteractType;

import java.util.List;

/**
 * NPC와의 대화 창을 관리하는 GUI 클래스
 */
public class DialogueGUI {
    private String npcName;
    private List<NPCReaction> availableReactions;
    private ReputationLevel reputationLevel;
    private List<InteractType> availableInteractions;

    public DialogueGUI(String npcName, List<NPCReaction> availableReactions, ReputationLevel reputationLevel, List<InteractType> availableInteractions) {
        this.npcName = npcName;
        this.availableReactions = availableReactions;
        this.reputationLevel = reputationLevel;
        this.availableInteractions = availableInteractions;
    }

    public String getNpcName() {
        return npcName;
    }

    public List<NPCReaction> getAvailableReactions() {
        return availableReactions;
    }

    public ReputationLevel getReputationLevel() {
        return reputationLevel;
    }

    public List<InteractType> getAvailableInteractions() {
        return availableInteractions;
    }

    // 대화 표시 기능 (실제 구현은 게임/엔진 시스템과 연동 필요)
    public void displayDialogue() {
        System.out.println("[" + npcName + "와 대화]");
        for (NPCReaction reaction : availableReactions) {
            System.out.println("- " + reaction.getDescription());
        }
    }
}