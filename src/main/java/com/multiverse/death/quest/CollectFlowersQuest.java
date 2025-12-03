package com.multiverse.death.quest;

import com.multiverse.death.models.RevivalQuest;
import org.bukkit.Material;
import java.util.HashMap;
import java.util.Map;

/**
 * 꽃을 모으는 부활 퀘스트
 */
public class CollectFlowersQuest {

    private final RevivalQuest quest;
    private final Map<Material, Integer> requiredFlowers;
    private final Map<Material, Integer> collectedFlowers = new HashMap<>();

    public CollectFlowersQuest(RevivalQuest quest, Map<Material, Integer> requiredFlowers) {
        this.quest = quest;
        this.requiredFlowers = requiredFlowers;
    }

    public void collectFlower(Material flowerType) {
        collectedFlowers.put(flowerType, collectedFlowers.getOrDefault(flowerType, 0) + 1);
        updateQuestProgress();
    }

    private void updateQuestProgress() {
        int progress = 0;
        int goal = 0;
        boolean completed = true;

        for (Map.Entry<Material, Integer> entry : requiredFlowers.entrySet()) {
            int required = entry.getValue();
            int collected = collectedFlowers.getOrDefault(entry.getKey(), 0);
            progress += Math.min(collected, required);
            goal += required;
            if (collected < required) {
                completed = false;
            }
        }

        quest.setProgress(progress);
        quest.setGoal(goal);
        quest.setCompleted(completed);
    }

    public RevivalQuest getQuest() {
        return quest;
    }

    public Map<Material, Integer> getCollectedFlowers() {
        return collectedFlowers;
    }
}