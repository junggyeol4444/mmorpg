package com.multiverse.party.models;

import java.util.*;

/**
 * 파티 퀘스트 데이터 구조
 */
public class PartyQuest {

    private String questId;
    private String title;
    private String description;
    private int minLevel;
    private long startedTime;
    private long completedTime;
    private Map<String, Object> rewards; // 보상(아이템 등)

    public String getQuestId() { return questId; }
    public void setQuestId(String questId) { this.questId = questId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getMinLevel() { return minLevel; }
    public void setMinLevel(int minLevel) { this.minLevel = minLevel; }
    public long getStartedTime() { return startedTime; }
    public void setStartedTime(long startedTime) { this.startedTime = startedTime; }
    public long getCompletedTime() { return completedTime; }
    public void setCompletedTime(long completedTime) { this.completedTime = completedTime; }
    public Map<String, Object> getRewards() { return rewards == null ? (rewards = new HashMap<>()) : rewards; }
    public void setRewards(Map<String, Object> rewards) { this.rewards = rewards; }
}