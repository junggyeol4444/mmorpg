package com.multiverse.party.models;

import java.util.*;
import com.multiverse.party.models.enums.BuffType;

public class PartyBuff {
    private String buffId;
    private String name;
    private BuffType type;
    private Map<String, Double> effects;  // 효과 key/value, 예: "damage_bonus" → 0.1
    private int requiredMembers;
    private int requiredPartyLevel;
    private long startTime;      // 활성화 시각 (ms)
    private int duration;        // 지속시간 (초, -1: 영구)
    private double range;        // 적용 거리 (버프)

    // Getters & Setters
    public String getBuffId() { return buffId; }
    public void setBuffId(String buffId) { this.buffId = buffId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BuffType getType() { return type; }
    public void setType(BuffType type) { this.type = type; }
    public Map<String, Double> getEffects() { return effects == null ? (effects = new HashMap<>()) : effects; }
    public void setEffects(Map<String, Double> effects) { this.effects = effects; }
    public int getRequiredMembers() { return requiredMembers; }
    public void setRequiredMembers(int requiredMembers) { this.requiredMembers = requiredMembers; }
    public int getRequiredPartyLevel() { return requiredPartyLevel; }
    public void setRequiredPartyLevel(int requiredPartyLevel) { this.requiredPartyLevel = requiredPartyLevel; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public double getRange() { return range; }
    public void setRange(double range) { this.range = range; }
}