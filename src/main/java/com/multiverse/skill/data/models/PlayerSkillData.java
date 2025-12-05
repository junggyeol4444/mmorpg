package com.multiverse.skill.data.models;

import java.util.*;

public class PlayerSkillData {

    private UUID playerUUID;
    private String playerName;

    // 스킬 포인트
    private int totalSkillPoints;
    private int usedSkillPoints;
    private int availableSkillPoints;

    // 보유 스킬
    private Map<String, LearnedSkill> skills;

    // 스킬 트리 진행도
    private Map<String, Integer> treeProgress;

    // 스킬 프리셋
    private List<SkillPreset> presets;
    private int activePresetIndex;

    // 생활 스킬
    private Map<String, LifeSkill> lifeSkills;

    public PlayerSkillData() {
        this.skills = new HashMap<>();
        this.treeProgress = new HashMap<>();
        this.presets = new ArrayList<>();
        this. activePresetIndex = 0;
        this.lifeSkills = new HashMap<>();
        this.totalSkillPoints = 0;
        this.usedSkillPoints = 0;
        this. availableSkillPoints = 0;
    }

    public PlayerSkillData(UUID playerUUID, String playerName) {
        this();
        this.playerUUID = playerUUID;
        this. playerName = playerName;
    }

    // Getters and Setters

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this. playerName = playerName;
    }

    public int getTotalSkillPoints() {
        return totalSkillPoints;
    }

    public void setTotalSkillPoints(int totalSkillPoints) {
        this.totalSkillPoints = totalSkillPoints;
    }

    public int getUsedSkillPoints() {
        return usedSkillPoints;
    }

    public void setUsedSkillPoints(int usedSkillPoints) {
        this.usedSkillPoints = usedSkillPoints;
    }

    public int getAvailableSkillPoints() {
        return availableSkillPoints;
    }

    public void setAvailableSkillPoints(int availableSkillPoints) {
        this.availableSkillPoints = availableSkillPoints;
    }

    public Map<String, LearnedSkill> getSkills() {
        return skills;
    }

    public void setSkills(Map<String, LearnedSkill> skills) {
        this.skills = skills;
    }

    public Map<String, Integer> getTreeProgress() {
        return treeProgress;
    }

    public void setTreeProgress(Map<String, Integer> treeProgress) {
        this.treeProgress = treeProgress;
    }

    public List<SkillPreset> getPresets() {
        return presets;
    }

    public void setPresets(List<SkillPreset> presets) {
        this.presets = presets;
    }

    public int getActivePresetIndex() {
        return activePresetIndex;
    }

    public void setActivePresetIndex(int activePresetIndex) {
        this.activePresetIndex = activePresetIndex;
    }

    public Map<String, LifeSkill> getLifeSkills() {
        return lifeSkills;
    }

    public void setLifeSkills(Map<String, LifeSkill> lifeSkills) {
        this.lifeSkills = lifeSkills;
    }

    /**
     * 스킬 보유 여부 확인
     */
    public boolean hasSkill(String skillId) {
        return skills.containsKey(skillId);
    }

    /**
     * 스킬 조회
     */
    public LearnedSkill getSkill(String skillId) {
        return skills.get(skillId);
    }

    /**
     * 스킬 추가
     */
    public void addSkill(String skillId, LearnedSkill skill) {
        skills.put(skillId, skill);
    }

    /**
     * 스킬 제거
     */
    public void removeSkill(String skillId) {
        skills.remove(skillId);
    }

    /**
     * 스킬 트리 진행도 조회
     */
    public int getTreeProgress(String treeId) {
        return treeProgress.getOrDefault(treeId, 0);
    }

    /**
     * 스킬 트리 진행도 업데이트
     */
    public void updateTreeProgress(String treeId, int progress) {
        treeProgress.put(treeId, progress);
    }

    /**
     * 활성 프리셋 조회
     */
    public SkillPreset getActivePreset() {
        if (activePresetIndex < 0 || activePresetIndex >= presets.size()) {
            return null;
        }
        return presets.get(activePresetIndex);
    }

    /**
     * 프리셋 추가
     */
    public void addPreset(SkillPreset preset) {
        presets.add(preset);
    }

    /**
     * 프리셋 제거
     */
    public void removePreset(int index) {
        if (index >= 0 && index < presets.size()) {
            presets.remove(index);
        }
    }

    /**
     * 총 스킬 레벨
     */
    public int getTotalSkillLevel() {
        return skills.values().stream()
                . mapToInt(LearnedSkill::getLevel)
                . sum();
    }

    /**
     * 총 사용 횟수
     */
    public int getTotalSkillUsage() {
        return skills.values().stream()
                .mapToInt(LearnedSkill::getTimesUsed)
                .sum();
    }

    /**
     * 총 데미지
     */
    public long getTotalSkillDamage() {
        return skills.values().stream()
                .mapToLong(LearnedSkill::getTotalDamage)
                .sum();
    }

    /**
     * 플레이어 데이터 요약
     */
    public String getSummary() {
        return String.format(
                "플레이어: %s | 스킬: %d개 | 포인트: %d/%d | 레벨: %d",
                playerName,
                skills.size(),
                availableSkillPoints,
                totalSkillPoints,
                getTotalSkillLevel()
        );
    }

    @Override
    public String toString() {
        return "PlayerSkillData{" +
                "playerUUID=" + playerUUID +
                ", playerName='" + playerName + '\'' +
                ", totalSkillPoints=" + totalSkillPoints +
                ", skillCount=" + skills.size() +
                '}';
    }
}