package com. multiverse.dungeon.data. model;

/**
 * 던전 주간 입장 제한 데이터 클래스
 */
public class WeeklyLimit {

    private int maxEntries; // 주간 최대 입장 횟수

    /**
     * 생성자
     *
     * @param maxEntries 주간 최대 입장 횟수
     */
    public WeeklyLimit(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    /**
     * 기본 생성자
     */
    public WeeklyLimit() {
        this.maxEntries = 1; // 기본값: 1회
    }

    // ===== Getters & Setters =====

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    /**
     * 입장 가능한지 확인
     *
     * @param currentEntries 현재 입장 횟수
     * @return 입장 가능하면 true
     */
    public boolean canEnter(int currentEntries) {
        if (maxEntries == -1) {
            return true; // -1은 무제한
        }
        return currentEntries < maxEntries;
    }

    /**
     * 남은 입장 횟수
     *
     * @param currentEntries 현재 입장 횟수
     * @return 남은 횟수
     */
    public int getRemainingEntries(int currentEntries) {
        if (maxEntries == -1) {
            return Integer.MAX_VALUE;
        }
        return Math.max(0, maxEntries - currentEntries);
    }

    @Override
    public String toString() {
        return "WeeklyLimit{" +
                "maxEntries=" + maxEntries +
                '}';
    }
}