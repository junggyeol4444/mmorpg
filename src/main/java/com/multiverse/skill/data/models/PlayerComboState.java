package com.multiverse.skill.data. models;

import java.util.*;

public class PlayerComboState {

    private UUID playerUUID;
    private List<String> currentSequence;
    private long lastSkillTime;
    private String matchingComboId;

    public PlayerComboState() {
        this.currentSequence = new ArrayList<>();
        this.lastSkillTime = 0;
        this.matchingComboId = "";
    }

    public PlayerComboState(UUID playerUUID) {
        this();
        this.playerUUID = playerUUID;
    }

    // Getters and Setters

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this. playerUUID = playerUUID;
    }

    public List<String> getCurrentSequence() {
        return currentSequence;
    }

    public void setCurrentSequence(List<String> currentSequence) {
        this.currentSequence = currentSequence;
    }

    public long getLastSkillTime() {
        return lastSkillTime;
    }

    public void setLastSkillTime(long lastSkillTime) {
        this.lastSkillTime = lastSkillTime;
    }

    public String getMatchingComboId() {
        return matchingComboId;
    }

    public void setMatchingComboId(String matchingComboId) {
        this.matchingComboId = matchingComboId;
    }

    /**
     * 시퀀스에 스킬 추가
     */
    public void addSkill(String skillId) {
        currentSequence.add(skillId);
        this.lastSkillTime = System. currentTimeMillis();
    }

    /**
     * 현재 시퀀스 길이
     */
    public int getSequenceLength() {
        return currentSequence.size();
    }

    /**
     * 마지막 스킬 조회
     */
    public String getLastSkill() {
        if (currentSequence.isEmpty()) {
            return null;
        }
        return currentSequence.get(currentSequence.size() - 1);
    }

    /**
     * 시퀀스 초기화
     */
    public void resetSequence() {
        currentSequence.clear();
        lastSkillTime = 0;
        matchingComboId = "";
    }

    /**
     * 시간 경과 (밀리초)
     */
    public long getTimeSinceLastSkill() {
        if (lastSkillTime == 0) {
            return 0;
        }
        return System. currentTimeMillis() - lastSkillTime;
    }

    /**
     * 콤보 진행 중인지 확인
     */
    public boolean isComboInProgress() {
        return !currentSequence.isEmpty() && matchingComboId != null && !matchingComboId.isEmpty();
    }

    /**
     * 콤보 시퀀스 문자열
     */
    public String getSequenceString() {
        return String.join(" → ", currentSequence);
    }

    /**
     * 상태 정보 문자열
     */
    public String getStateString() {
        return String. format("Sequence: %s | Length: %d | ComboId: %s | Active: %s",
                getSequenceString(). isEmpty() ? "없음" : getSequenceString(),
                getSequenceLength(),
                matchingComboId.isEmpty() ? "없음" : matchingComboId,
                isComboInProgress() ? "진행 중" : "대기");
    }

    @Override
    public String toString() {
        return "PlayerComboState{" +
                "playerUUID=" + playerUUID +
                ", sequenceLength=" + currentSequence.size() +
                ", matchingComboId='" + matchingComboId + '\'' +
                '}';
    }
}