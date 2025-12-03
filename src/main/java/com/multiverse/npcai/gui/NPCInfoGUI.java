package com.multiverse.npcai.gui;

import com.multiverse.npcai.models.enums.NPCType;
import com.multiverse.npcai.models.enums.ReputationLevel;

/**
 * NPC의 기본 정보(이름, 타입, 평판 등)를 표시하는 GUI 클래스
 */
public class NPCInfoGUI {
    private String npcName;
    private NPCType npcType;
    private ReputationLevel reputationLevel;

    public NPCInfoGUI(String npcName, NPCType npcType, ReputationLevel reputationLevel) {
        this.npcName = npcName;
        this.npcType = npcType;
        this.reputationLevel = reputationLevel;
    }

    public String getNpcName() {
        return npcName;
    }

    public NPCType getNpcType() {
        return npcType;
    }

    public ReputationLevel getReputationLevel() {
        return reputationLevel;
    }

    // NPC 정보 표시 기능 (실제 구현은 게임/엔진 시스템과 연동 필요)
    public void displayInfo() {
        System.out.println("NPC 이름: " + npcName + " | 타입: " + npcType + " | 평판: " + reputationLevel);
    }
}