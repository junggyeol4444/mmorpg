package com.multiverse.npcai.gui;

import com.multiverse.npcai.models.enums.ReputationLevel;

/**
 * NPC와 플레이어 사이의 평판(명성) 상태를 표시하는 GUI 클래스
 */
public class ReputationGUI {
    private String npcName;
    private ReputationLevel reputationLevel;

    public ReputationGUI(String npcName, ReputationLevel reputationLevel) {
        this.npcName = npcName;
        this.reputationLevel = reputationLevel;
    }

    public String getNpcName() {
        return npcName;
    }

    public ReputationLevel getReputationLevel() {
        return reputationLevel;
    }

    // 평판 상태 표시 기능 (실제 구현은 게임/엔진 시스템과 연동 필요)
    public void displayReputation() {
        System.out.println(npcName + "와(과) 플레이어의 평판: " + reputationLevel);
    }
}