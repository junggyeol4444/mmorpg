package com.multiverse.skill.data.models;

import java.util.*;

public class SkillCombo {

    private String comboId;
    private String name;
    
    // 콤보 순서
    private List<String> skillSequence;
    private int timeWindow;
    
    // 피니셔
    private String finisherSkillId;
    
    // 보너스
    private boolean hasBonus;
    private double damageBonus;
    private Map<String, Object> bonusEffects;

    public SkillCombo() {
        this.skillSequence = new ArrayList<>();
        this.bonusEffects = new HashMap<>();
        this.hasBonus = false;
        this.damageBonus = 0.0;
    }

    // Getters and Setters

    public String getComboId() {
        return comboId;
    }

    public void setComboId(String comboId) {
        this. comboId = comboId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSkillSequence() {
        return skillSequence;
    }

    public void setSkillSequence(List<String> skillSequence) {
        this.skillSequence = skillSequence;
    }

    public int getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(int timeWindow) {
        this.timeWindow = timeWindow;
    }

    public String getFinisherSkillId() {
        return finisherSkillId;
    }

    public void setFinisherSkillId(String finisherSkillId) {
        this.finisherSkillId = finisherSkillId;
    }

    public boolean getHasBonus() {
        return hasBonus;
    }

    public void setHasBonus(boolean hasBonus) {
        this.hasBonus = hasBonus;
    }

    public double getDamageBonus() {
        return damageBonus;
    }

    public void setDamageBonus(double damageBonus) {
        this.damageBonus = damageBonus;
    }

    public Map<String, Object> getBonusEffects() {
        return bonusEffects;
    }

    public void setBonusEffects(Map<String, Object> bonusEffects) {
        this.bonusEffects = bonusEffects;
    }

    /**
     * 콤보에 스킬 추가
     */
    public void addSkill(String skillId) {
        skillSequence.add(skillId);
    }

    /**
     * 콤보 길이 (스킬 개수)
     */
    public int getLength() {
        return skillSequence.size();
    }

    /**
     * 콤보에 스킬이 포함되어 있는지 확인
     */
    public boolean containsSkill(String skillId) {
        return skillSequence. contains(skillId);
    }

    /**
     * 보너스 효과 추가
     */
    public void addBonusEffect(String effectKey, Object value) {
        bonusEffects.put(effectKey, value);
        this.hasBonus = true;
    }

    /**
     * 보너스 효과 조회
     */
    public Object getBonusEffect(String effectKey) {
        return bonusEffects.get(effectKey);
    }

    /**
     * 콤보 시퀀스 문자열
     */
    public String getSequenceString() {
        return String.join(" → ", skillSequence);
    }

    /**
     * 콤보 정보 문자열
     */
    public String getInfoString() {
        String finisher = finisherSkillId != null && ! finisherSkillId.isEmpty() ? 
            " + " + finisherSkillId : "";
        String bonus = hasBonus ? String.format(" (데미지 +%. 0f%%)", damageBonus * 100) : "";
        
        return String.format("%s: %s%s%s [%dms]",
                name,
                getSequenceString(),
                finisher,
                bonus,
                timeWindow);
    }

    @Override
    public String toString() {
        return "SkillCombo{" +
                "comboId='" + comboId + '\'' +
                ", name='" + name + '\'' +
                ", skillSequence=" + skillSequence +
                ", timeWindow=" + timeWindow +
                '}';
    }
}