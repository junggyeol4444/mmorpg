package com.multiverse.party.models;

import java.util.*;

public class PartySkill {

    private String skillId;
    private String name;
    private String description;
    private int requiredLevel;
    private int cost;               // 스킬 습득에 필요한 포인트
    private int cooldown;           // 쿨다운 (초)
    private List<String> effects;   // 효과 문자열 리스트
    private Map<String, Object> values; // 추가 값 (heal_amount 등)

    // Getters & Setters
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getRequiredLevel() { return requiredLevel; }
    public void setRequiredLevel(int requiredLevel) { this.requiredLevel = requiredLevel; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public int getCooldown() { return cooldown; }
    public void setCooldown(int cooldown) { this.cooldown = cooldown; }
    public List<String> getEffects() { return effects == null ? (effects = new ArrayList<>()) : effects; }
    public void setEffects(List<String> effects) { this.effects = effects; }
    public Map<String, Object> getValues() { return values == null ? (values = new HashMap<>()) : values; }
    public void setValues(Map<String, Object> values) { this.values = values; }

    // 편의 메서드
    public double getValue(String key, double def) {
        Object v = getValues().get(key);
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return Double.parseDouble(String.valueOf(v)); } catch (Exception e) { return def; }
    }
    public String getStringValue(String key) {
        Object v = getValues().get(key);
        return v != null ? v.toString() : null;
    }
}