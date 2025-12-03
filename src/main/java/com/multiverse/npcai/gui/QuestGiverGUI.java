package com.multiverse.npcai.gui;

import java.util.List;

/**
 * NPC 퀘스트 제공자(Quest Giver) 인터페이스를 관리하는 GUI 클래스
 */
public class QuestGiverGUI {
    private String npcName;
    private List<String> availableQuests;

    public QuestGiverGUI(String npcName, List<String> availableQuests) {
        this.npcName = npcName;
        this.availableQuests = availableQuests;
    }

    public String getNpcName() {
        return npcName;
    }

    public List<String> getAvailableQuests() {
        return availableQuests;
    }

    // 퀘스트 목록 표시 기능 (실제 구현은 게임/엔진 시스템과 연동 필요)
    public void displayQuests() {
        System.out.println(npcName + "가 제공하는 퀘스트 목록:");
        for (String quest : availableQuests) {
            System.out.println("- " + quest);
        }
    }
}