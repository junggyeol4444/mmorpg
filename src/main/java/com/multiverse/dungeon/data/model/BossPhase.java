package com.multiverse.dungeon.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 보스 페이즈 데이터 클래스
 * 보스의 HP 구간별 패턴 변경 정의
 */
public class BossPhase {

    private int phaseNumber; // 페이즈 번호
    private double healthThreshold; // HP 임계값 (%)
    
    private List<String> buffs; // 페이즈 버프
    private List<BossSkill> phaseSkills; // 페이즈 스킬
    private List<String> summonMobs; // 소환 몬스터
    
    private String phaseMessage; // 페이즈 시작 메시지

    /**
     * 생성자
     */
    public BossPhase(int phaseNumber, double healthThreshold) {
        this.phaseNumber = phaseNumber;
        this.healthThreshold = healthThreshold;
        this.buffs = new ArrayList<>();
        this.phaseSkills = new ArrayList<>();
        this.summonMobs = new ArrayList<>();
        this.phaseMessage = "";
    }

    /**
     * 기본 생성자
     */
    public BossPhase() {
        this(1, 100.0);
    }

    // ===== Getters & Setters =====

    public int getPhaseNumber() {
        return phaseNumber;
    }

    public void setPhaseNumber(int phaseNumber) {
        this. phaseNumber = phaseNumber;
    }

    public double getHealthThreshold() {
        return healthThreshold;
    }

    public void setHealthThreshold(double healthThreshold) {
        this.healthThreshold = Math.max(0, Math.min(100, healthThreshold));
    }

    public List<String> getBuffs() {
        return buffs;
    }

    public void setBuffs(List<String> buffs) {
        this. buffs = buffs != null ? buffs : new ArrayList<>();
    }

    public void addBuff(String buff) {
        this.buffs.add(buff);
    }

    public List<BossSkill> getPhaseSkills() {
        return phaseSkills;
    }

    public void setPhaseSkills(List<BossSkill> phaseSkills) {
        this.phaseSkills = phaseSkills != null ? phaseSkills : new ArrayList<>();
    }

    public void addPhaseSkill(BossSkill skill) {
        this.phaseSkills.add(skill);
    }

    public List<String> getSummonMobs() {
        return summonMobs;
    }

    public void setSummonMobs(List<String> summonMobs) {
        this.summonMobs = summonMobs != null ? summonMobs : new ArrayList<>();
    }

    public void addSummonMob(String mobId) {
        this.summonMobs.add(mobId);
    }

    public String getPhaseMessage() {
        return phaseMessage;
    }

    public void setPhaseMessage(String phaseMessage) {
        this.phaseMessage = phaseMessage != null ? phaseMessage : "";
    }

    /**
     * 현재 HP에서 이 페이즈로 진입해야 하는지 확인
     *
     * @param currentHealthPercent 현재 HP (%)
     * @return 진입 가능하면 true
     */
    public boolean shouldActivate(double currentHealthPercent) {
        return currentHealthPercent <= healthThreshold;
    }

    /**
     * 페이즈에 사용 가능한 스킬 개수
     *
     * @return 스킬 개수
     */
    public int getSkillCount() {
        return phaseSkills.size();
    }

    /**
     * 소환할 몬스터가 있는지 확인
     *
     * @return 소환 몬스터가 있으면 true
     */
    public boolean hasSummonMobs() {
        return !summonMobs.isEmpty();
    }

    @Override
    public String toString() {
        return "BossPhase{" +
                "phaseNumber=" + phaseNumber +
                ", healthThreshold=" + healthThreshold +
                ", buffs=" + buffs. size() +
                ", phaseSkills=" + phaseSkills.size() +
                ", summonMobs=" + summonMobs.size() +
                ", phaseMessage='" + phaseMessage + '\'' +
                '}';
    }
}