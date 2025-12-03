package com.multiverse.death.quest;

import com.multiverse.death.models.RevivalQuest;
import com.multiverse.death.models.enums.QuestType;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

/**
 * 플레이어의 부활 퀘스트를 관리하는 클래스
 */
public class QuestManager {
    private static final Map<UUID, RevivalQuest> questMap = new HashMap<>();

    public static RevivalQuest getQuest(Player player) {
        return questMap.get(player.getUniqueId());
    }

    public static void startQuest(Player player, QuestType type, int goal) {
        RevivalQuest quest = new RevivalQuest();
        quest.setPlayerUUID(player.getUniqueId());
        quest.setType(type);
        quest.setGoal(goal);
        quest.setProgress(0);
        quest.setCompleted(false);
        questMap.put(player.getUniqueId(), quest);
    }

    public static void updateProgress(Player player, int progress) {
        RevivalQuest quest = questMap.get(player.getUniqueId());
        if (quest != null) {
            quest.setProgress(progress);
            if (quest.getProgress() >= quest.getGoal()) {
                quest.setCompleted(true);
            }
        }
    }

    public static void completeQuest(Player player) {
        RevivalQuest quest = questMap.get(player.getUniqueId());
        if (quest != null) {
            quest.setCompleted(true);
        }
    }

    public static void removeQuest(Player player) {
        questMap.remove(player.getUniqueId());
    }
}