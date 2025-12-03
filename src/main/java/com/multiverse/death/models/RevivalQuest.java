package com.multiverse.death.models;

import com.multiverse.death.models.enums.QuestType;
import java.util.UUID;

/**
 * 플레이어 부활 퀘스트 진행 정보 모델
 */
public class RevivalQuest {
    private UUID playerUUID;
    private QuestType type;
    private int progress;
    private int goal;
    private boolean completed;

    public UUID getPlayerUUID() {
        return playerUUID;
    }
    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public QuestType getType() {
        return type;
    }
    public void setType(QuestType type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getGoal() {
        return goal;
    }
    public void setGoal(int goal) {
        this.goal = goal;
    }

    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}