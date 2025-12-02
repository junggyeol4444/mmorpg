package com.multiverse.core.utils;

import java.util.List;

public class QuestUtil {

    // 퀘스트 요구조건이 충족되었는지 체크
    public static boolean isQuestMet(String questRequirement, List<String> completedQuests) {
        if (questRequirement == null || questRequirement.isEmpty()) return true;
        return completedQuests != null && completedQuests.contains(questRequirement);
    }

    // 다단계 퀘스트 조건 형식 지원 (ex: "questA,questB")
    public static boolean areAllQuestsMet(String questRequirements, List<String> completedQuests) {
        if (questRequirements == null || questRequirements.isEmpty()) return true;
        if (completedQuests == null) return false;
        String[] required = questRequirements.split(",");
        for (String quest : required) {
            if (!completedQuests.contains(quest.trim())) return false;
        }
        return true;
    }

    // 퀘스트 이름 형식
    public static boolean isValidQuestName(String name) {
        return name != null && name.matches("^[a-zA-Z0-9_-]{3,32}$");
    }
}