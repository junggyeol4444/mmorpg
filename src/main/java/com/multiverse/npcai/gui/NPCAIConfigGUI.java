package com.multiverse.npcai.gui;

import com.multiverse.npcai.models.enums.NPCType;
import com.multiverse.npcai.models.enums.BehaviorType;

/**
 * NPC AI, 타입, 행동 등을 설정하는 GUI 클래스
 */
public class NPCAIConfigGUI {
    private String npcName;
    private NPCType npcType;
    private BehaviorType behaviorType;

    public NPCAIConfigGUI(String npcName, NPCType npcType, BehaviorType behaviorType) {
        this.npcName = npcName;
        this.npcType = npcType;
        this.behaviorType = behaviorType;
    }

    public String getNpcName() {
        return npcName;
    }

    public NPCType getNpcType() {
        return npcType;
    }

    public BehaviorType getBehaviorType() {
        return behaviorType;
    }

    // NPC AI 및 속성 상태 표시 기능 (실제 구현은 게임/엔진 시스템과 연동 필요)
    public void displayConfig() {
        System.out.println("NPC: " + npcName + " | 타입: " + npcType + " | 행동 패턴: " + behaviorType);
    }
}