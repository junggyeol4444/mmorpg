package com.multiverse.npcai.citizens;

/**
 * Citizens 플러그인과 연동되는 NPC Trait 클래스
 */
public class CitizensNPCTrait {
    private int npcId;
    private String npcName;

    public CitizensNPCTrait(int npcId, String npcName) {
        this.npcId = npcId;
        this.npcName = npcName;
    }

    public int getNpcId() {
        return npcId;
    }

    public String getNpcName() {
        return npcName;
    }

    // 실제 Citizens API 연동 기능
    public void performCitizenAction(String action) {
        // 더미 구현 (API 연동 필요)
        System.out.println("[CitizensNPCTrait] NPC(" + npcId + ":" + npcName + ")가 행동: " + action + " 수행");
    }
}