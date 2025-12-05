package com.multiverse. skill.data.models;

import java.util.*;

public class SkillPreset {

    private String name;
    private Map<Integer, String> hotbar;
    private long createdTime;
    private long lastModifiedTime;

    public SkillPreset() {
        this. hotbar = new HashMap<>();
        this.createdTime = System. currentTimeMillis();
        this.lastModifiedTime = System.currentTimeMillis();
    }

    public SkillPreset(String name) {
        this();
        this.name = name;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, String> getHotbar() {
        return hotbar;
    }

    public void setHotbar(Map<Integer, String> hotbar) {
        this.hotbar = hotbar;
        this.lastModifiedTime = System.currentTimeMillis();
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this. createdTime = createdTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    /**
     * 핫바에 스킬 설정
     */
    public void setSkillToSlot(int slot, String skillId) {
        if (slot >= 0 && slot < 9) {
            hotbar.put(slot, skillId);
            this.lastModifiedTime = System. currentTimeMillis();
        }
    }

    /**
     * 핫바에서 스킬 조회
     */
    public String getSkillFromSlot(int slot) {
        return hotbar.getOrDefault(slot, null);
    }

    /**
     * 핫바 슬롯 제거
     */
    public void removeSkillFromSlot(int slot) {
        hotbar. remove(slot);
        this.lastModifiedTime = System.currentTimeMillis();
    }

    /**
     * 핫바 전체 초기화
     */
    public void clearHotbar() {
        hotbar.clear();
        this.lastModifiedTime = System.currentTimeMillis();
    }

    /**
     * 특정 스킬이 핫바에 있는지 확인
     */
    public boolean containsSkill(String skillId) {
        return hotbar.values().contains(skillId);
    }

    /**
     * 스킬이 설정된 슬롯 조회
     */
    public Integer getSlotForSkill(String skillId) {
        for (Map.Entry<Integer, String> entry : hotbar.entrySet()) {
            if (entry.getValue().equals(skillId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 핫바 정보 문자열
     */
    public String getHotbarString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            String skill = hotbar.getOrDefault(i, "비어있음");
            sb. append(String.format("[%d: %s] ", i + 1, skill));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "SkillPreset{" +
                "name='" + name + '\'' +
                ", skillCount=" + hotbar.size() +
                ", lastModified=" + new Date(lastModifiedTime) +
                '}';
    }
}