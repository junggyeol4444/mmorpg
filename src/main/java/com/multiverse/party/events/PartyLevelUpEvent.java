package com. multiverse.party. events;

import com.multiverse.party.models.Party;
import com.multiverse.party.models.PartyLevel;
import org.bukkit.event.Event;
import org. bukkit.event. HandlerList;

import java.util.ArrayList;
import java. util.List;
import java.util. UUID;

public class PartyLevelUpEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Party party;
    private final int oldLevel;
    private final int newLevel;
    private final long totalExp;
    private int bonusSkillPoints;
    private final List<String> unlockedFeatures;
    private final List<String> unlockedBuffs;
    private final List<String> unlockedSkills;

    public PartyLevelUpEvent(Party party, int oldLevel, int newLevel) {
        this. party = party;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.totalExp = party.getPartyLevel() != null ? party.getPartyLevel().getExperience() : 0;
        this.bonusSkillPoints = 0;
        this.unlockedFeatures = new ArrayList<>();
        this.unlockedBuffs = new ArrayList<>();
        this.unlockedSkills = new ArrayList<>();
    }

    /**
     * 파티 반환
     * @return 파티
     */
    public Party getParty() {
        return party;
    }

    /**
     * 파티 ID 반환
     * @return 파티 UUID
     */
    public UUID getPartyId() {
        return party.getPartyId();
    }

    /**
     * 파티 이름 반환
     * @return 파티 이름
     */
    public String getPartyName() {
        return party.getPartyName();
    }

    /**
     * 이전 레벨 반환
     * @return 이전 레벨
     */
    public int getOldLevel() {
        return oldLevel;
    }

    /**
     * 새 레벨 반환
     * @return 새 레벨
     */
    public int getNewLevel() {
        return newLevel;
    }

    /**
     * 레벨 증가량 반환
     * @return 증가한 레벨 수
     */
    public int getLevelGain() {
        return newLevel - oldLevel;
    }

    /**
     * 다중 레벨업인지 확인
     * @return 다중 레벨업 여부
     */
    public boolean isMultipleLevelUp() {
        return getLevelGain() > 1;
    }

    /**
     * 총 경험치 반환
     * @return 총 경험치
     */
    public long getTotalExp() {
        return totalExp;
    }

    /**
     * 파티 멤버 목록 반환
     * @return 멤버 UUID 목록
     */
    public List<UUID> getMembers() {
        return party.getMembers();
    }

    /**
     * 파티 멤버 수 반환
     * @return 멤버 수
     */
    public int getMemberCount() {
        return party.getMembers().size();
    }

    /**
     * 파티 리더 UUID 반환
     * @return 리더 UUID
     */
    public UUID getLeaderId() {
        return party.getLeaderId();
    }

    /**
     * 보너스 스킬 포인트 반환
     * @return 보너스 스킬 포인트
     */
    public int getBonusSkillPoints() {
        return bonusSkillPoints;
    }

    /**
     * 보너스 스킬 포인트 설정
     * @param bonusSkillPoints 보너스 스킬 포인트
     */
    public void setBonusSkillPoints(int bonusSkillPoints) {
        this.bonusSkillPoints = Math.max(0, bonusSkillPoints);
    }

    /**
     * 보너스 스킬 포인트 추가
     * @param points 추가할 포인트
     */
    public void addBonusSkillPoints(int points) {
        this. bonusSkillPoints += Math.max(0, points);
    }

    /**
     * 해금된 기능 목록 반환
     * @return 해금된 기능 목록
     */
    public List<String> getUnlockedFeatures() {
        return unlockedFeatures;
    }

    /**
     * 해금된 기능 추가
     * @param feature 기능 이름
     */
    public void addUnlockedFeature(String feature) {
        if (feature != null && !feature.isEmpty()) {
            unlockedFeatures.add(feature);
        }
    }

    /**
     * 해금된 버프 목록 반환
     * @return 해금된 버프 목록
     */
    public List<String> getUnlockedBuffs() {
        return unlockedBuffs;
    }

    /**
     * 해금된 버프 추가
     * @param buff 버프 이름
     */
    public void addUnlockedBuff(String buff) {
        if (buff != null && !buff.isEmpty()) {
            unlockedBuffs.add(buff);
        }
    }

    /**
     * 해금된 스킬 목록 반환
     * @return 해금된 스킬 목록
     */
    public List<String> getUnlockedSkills() {
        return unlockedSkills;
    }

    /**
     * 해금된 스킬 추가
     * @param skill 스킬 이름
     */
    public void addUnlockedSkill(String skill) {
        if (skill != null && !skill.isEmpty()) {
            unlockedSkills. add(skill);
        }
    }

    /**
     * 새로운 것이 해금되었는지 확인
     * @return 해금 여부
     */
    public boolean hasUnlocks() {
        return !unlockedFeatures.isEmpty() || !unlockedBuffs.isEmpty() || !unlockedSkills.isEmpty();
    }

    /**
     * 현재 사용 가능한 스킬 포인트 반환
     * @return 사용 가능한 스킬 포인트
     */
    public int getAvailableSkillPoints() {
        PartyLevel partyLevel = party.getPartyLevel();
        if (partyLevel == null) return 0;
        return partyLevel. getSkillPoints() - partyLevel.getUsedSkillPoints() + bonusSkillPoints;
    }

    /**
     * 특정 레벨에 도달했는지 확인
     * @param level 확인할 레벨
     * @return 도달 여부
     */
    public boolean reachedLevel(int level) {
        return oldLevel < level && newLevel >= level;
    }

    /**
     * 마일스톤 레벨인지 확인 (5, 10, 15, 20...)
     * @return 마일스톤 여부
     */
    public boolean isMilestoneLevel() {
        return newLevel % 5 == 0;
    }

    /**
     * 최대 레벨 도달 여부 확인
     * @param maxLevel 최대 레벨
     * @return 최대 레벨 도달 여부
     */
    public boolean isMaxLevel(int maxLevel) {
        return newLevel >= maxLevel;
    }

    /**
     * 레벨업 요약 정보 반환
     * @return 요약 문자열
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("파티 레벨업:  ").append(oldLevel).append(" -> ").append(newLevel);
        
        if (bonusSkillPoints > 0) {
            sb.append(" (+").append(bonusSkillPoints).append(" 스킬 포인트)");
        }
        
        if (!unlockedBuffs.isEmpty()) {
            sb.append(" [새 버프: ").append(String.join(", ", unlockedBuffs)).append("]");
        }
        
        if (!unlockedSkills.isEmpty()) {
            sb.append(" [새 스킬: ").append(String.join(", ", unlockedSkills)).append("]");
        }
        
        return sb.toString();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}