package com.multiverse.dungeon.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 던전 진행도 데이터 클래스
 */
public class DungeonProgress {

    private int mobsKilled; // 처치한 몬스터 개수
    private int totalMobs; // 총 몬스터 개수
    
    private int bossesKilled; // 처치한 보스 개수
    private int totalBosses; // 총 보스 개수
    
    private List<String> completedObjectives; // 완료된 목표 목록
    
    private int score; // 던전 점수
    private int deaths; // 사망 횟수

    /**
     * 생성자
     */
    public DungeonProgress() {
        this.mobsKilled = 0;
        this.totalMobs = 0;
        this.bossesKilled = 0;
        this.totalBosses = 0;
        this.completedObjectives = new ArrayList<>();
        this.score = 0;
        this.deaths = 0;
    }

    /**
     * 모든 필드를 포함한 생성자
     */
    public DungeonProgress(int totalMobs, int totalBosses) {
        this();
        this.totalMobs = totalMobs;
        this. totalBosses = totalBosses;
    }

    // ===== Getters & Setters =====

    public int getMobsKilled() {
        return mobsKilled;
    }

    public void setMobsKilled(int mobsKilled) {
        this.mobsKilled = Math.max(0, mobsKilled);
    }

    public void incrementMobsKilled() {
        this.mobsKilled++;
        this.score += 10; // 몬스터 처치 시 점수 +10
    }

    public int getTotalMobs() {
        return totalMobs;
    }

    public void setTotalMobs(int totalMobs) {
        this.totalMobs = Math.max(0, totalMobs);
    }

    public int getBossesKilled() {
        return bossesKilled;
    }

    public void setBossesKilled(int bossesKilled) {
        this.bossesKilled = Math.max(0, bossesKilled);
    }

    public void incrementBossesKilled() {
        this.bossesKilled++;
        this. score += 500; // 보스 처치 시 점수 +500
    }

    public int getTotalBosses() {
        return totalBosses;
    }

    public void setTotalBosses(int totalBosses) {
        this.totalBosses = Math.max(0, totalBosses);
    }

    public List<String> getCompletedObjectives() {
        return completedObjectives;
    }

    public void setCompletedObjectives(List<String> completedObjectives) {
        this.completedObjectives = completedObjectives != null ? completedObjectives : new ArrayList<>();
    }

    public void addCompletedObjective(String objective) {
        if (! this.completedObjectives.contains(objective)) {
            this.completedObjectives.add(objective);
            this.score += 100; // 목표 완료 시 점수 +100
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    public void addScore(int points) {
        this.score += Math.max(0, points);
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = Math. max(0, deaths);
    }

    public void incrementDeaths() {
        this.deaths++;
        this.score = Math.max(0, score - 50); // 사망 시 점수 -50
    }

    /**
     * 진행도 (%)
     *
     * @return 진행도 (0 ~ 100)
     */
    public double getProgress() {
        int total = totalMobs + totalBosses;
        if (total == 0) {
            return 0.0;
        }
        int killed = mobsKilled + bossesKilled;
        return (killed / (double) total) * 100. 0;
    }

    /**
     * 몬스터 진행도 (%)
     *
     * @return 몬스터 처치 진행도
     */
    public double getMobProgress() {
        if (totalMobs == 0) {
            return 0.0;
        }
        return (mobsKilled / (double) totalMobs) * 100. 0;
    }

    /**
     * 보스 진행도 (%)
     *
     * @return 보스 처치 진행도
     */
    public double getBossProgress() {
        if (totalBosses == 0) {
            return 0.0;
        }
        return (bossesKilled / (double) totalBosses) * 100.0;
    }

    /**
     * 모든 몬스터를 처치했는지 확인
     *
     * @return 모두 처치했으면 true
     */
    public boolean allMobsKilled() {
        return mobsKilled >= totalMobs;
    }

    /**
     * 모든 보스를 처치했는지 확인
     *
     * @return 모두 처치했으면 true
     */
    public boolean allBossesKilled() {
        return bossesKilled >= totalBosses;
    }

    /**
     * 던전 완료 조건 충족 여부
     *
     * @return 완료 가능하면 true
     */
    public boolean isComplete() {
        return allMobsKilled() && allBossesKilled();
    }

    /**
     * 진행도 초기화
     */
    public void reset() {
        this. mobsKilled = 0;
        this.bossesKilled = 0;
        this.completedObjectives.clear();
        this.score = 0;
        this.deaths = 0;
    }

    @Override
    public String toString() {
        return "DungeonProgress{" +
                "mobsKilled=" + mobsKilled +
                ", totalMobs=" + totalMobs +
                ", bossesKilled=" + bossesKilled +
                ", totalBosses=" + totalBosses +
                ", score=" + score +
                ", deaths=" + deaths +
                ", progress=" + String.format("%.1f", getProgress()) + "%" +
                '}';
    }
}