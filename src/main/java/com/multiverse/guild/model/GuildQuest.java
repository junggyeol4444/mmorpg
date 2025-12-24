package com.multiverse.guild.model;

import java.util.List;
import java.util.Map;

public class GuildQuest {
    private String questId;
    private String name;
    private QuestType type;

    // 목표
    private List<QuestObjective> objectives;

    // 진행도
    private Map<String, Integer> progress;

    // 보상
    private GuildQuestReward reward;

    // 시간
    private long startTime;
    private long endTime;

    public GuildQuest(String questId, String name, QuestType type, List<QuestObjective> objectives, Map<String, Integer> progress, GuildQuestReward reward, long startTime, long endTime) {
        this.questId = questId;
        this.name = name;
        this.type = type;
        this.objectives = objectives;
        this.progress = progress;
        this.reward = reward;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getQuestId() { return questId; }
    public String getName() { return name; }
    public QuestType getType() { return type; }
    public List<QuestObjective> getObjectives() { return objectives; }
    public Map<String, Integer> getProgress() { return progress; }
    public GuildQuestReward getReward() { return reward; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
}