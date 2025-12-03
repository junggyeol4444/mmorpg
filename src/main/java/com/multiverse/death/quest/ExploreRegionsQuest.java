package com.multiverse.death.quest;

import com.multiverse.death.models.RevivalQuest;
import org.bukkit.Location;
import java.util.Set;
import java.util.HashSet;

/**
 * 명계에서 특정 지역을 탐험하는 부활 퀘스트
 */
public class ExploreRegionsQuest {

    private final RevivalQuest quest;
    private final Set<Location> requiredRegions;
    private final Set<Location> visitedRegions = new HashSet<>();

    public ExploreRegionsQuest(RevivalQuest quest, Set<Location> requiredRegions) {
        this.quest = quest;
        this.requiredRegions = requiredRegions;
    }

    public void visitRegion(Location region) {
        visitedRegions.add(region);
        updateQuestProgress();
    }

    private void updateQuestProgress() {
        quest.setProgress(visitedRegions.size());
        quest.setGoal(requiredRegions.size());
        if (visitedRegions.containsAll(requiredRegions)) {
            quest.setCompleted(true);
        }
    }

    public RevivalQuest getQuest() {
        return quest;
    }

    public Set<Location> getVisitedRegions() {
        return visitedRegions;
    }
}