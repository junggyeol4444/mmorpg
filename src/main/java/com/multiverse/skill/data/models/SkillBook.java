package com.multiverse.skill.data. models;

import com.multiverse.skill.data.enums. SkillBookType;

public class SkillBook {

    private String bookId;
    private String skillId;
    private SkillBookType type;
    private int skillLevel;
    
    // 요구사항
    private int requiredLevel;
    private String requiredClass;
    
    // 특성
    private boolean isPermanent;
    private boolean isSoulbound;
    
    // 설명
    private String description;

    public SkillBook() {
    }

    // Getters and Setters

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this. skillId = skillId;
    }

    public SkillBookType getType() {
        return type;
    }

    public void setType(SkillBookType type) {
        this. type = type;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public String getRequiredClass() {
        return requiredClass;
    }

    public void setRequiredClass(String requiredClass) {
        this.requiredClass = requiredClass;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public void setPermanent(boolean permanent) {
        isPermanent = permanent;
    }

    public boolean isSoulbound() {
        return isSoulbound;
    }

    public void setSoulbound(boolean soulbound) {
        isSoulbound = soulbound;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 스킬 북 타입별 설명
     */
    public String getTypeDescription() {
        return switch (type) {
            case LEARN -> String.format("새로운 스킬 '%s'을(를) 습득합니다.", skillId);
            case UPGRADE -> String.format("스킬 '%s'의 레벨을 1 증가시킵니다.", skillId);
            case RESET -> String.format("스킬 '%s'을(를) 초기화합니다 (포인트 환급).", skillId);
            default -> "알 수 없는 북입니다. ";
        };
    }

    /**
     * 스킬 북 정보 문자열
     */
    public String getInfoString() {
        String durability = isPermanent ? "§a영구" : "§c일회용";
        String bound = isSoulbound ? " §c[귀속]" : "";
        
        return String.format("§b%s §7| §e타입: %s | 스킬: %s | Lv.%d%s%s",
                bookId,
                type. getDisplayName(),
                skillId,
                skillLevel,
                durability,
                bound);
    }

    /**
     * 사용 가능한지 확인
     */
    public boolean isUsable() {
        return skillId != null && ! skillId.isEmpty() && type != null;
    }

    @Override
    public String toString() {
        return "SkillBook{" +
                "bookId='" + bookId + '\'' +
                ", skillId='" + skillId + '\'' +
                ", type=" + type +
                ", permanent=" + isPermanent +
                '}';
    }
}