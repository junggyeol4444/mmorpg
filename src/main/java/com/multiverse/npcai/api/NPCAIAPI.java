package com.multiverse.npcai.api;

import com.multiverse.npcai.models.enums.NPCType;
import com.multiverse.npcai.models.enums.BehaviorType;
import com.multiverse.npcai.models.enums.ReputationLevel;

/**
 * 외부에서 NPC AI 시스템을 제어할 수 있는 API 클래스(예시)
 */
public class NPCAIAPI {
    public static void configureNPC(String npcName, NPCType type, BehaviorType behavior, ReputationLevel reputation) {
        // 실제 게임/플러그인 시스템 연동 필요 (샘플)
        System.out.println("[NPCAIAPI] NPC 등록: " + npcName + ", 타입: " + type + ", 행동: " + behavior + ", 평판: " + reputation);
    }

    public static void triggerNPCBehavior(String npcName, BehaviorType behavior) {
        // 행동 트리거 처리 (샘플)
        System.out.println("[NPCAIAPI] 행동 트리거: " + npcName + " -> " + behavior);
    }
}