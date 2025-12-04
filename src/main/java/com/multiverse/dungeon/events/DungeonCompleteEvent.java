package com.multiverse.dungeon.events;

import com.multiverse.dungeon.data.model.DungeonInstance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 던전 완료 이벤트
 */
public class DungeonCompleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private DungeonInstance instance; // 던전 인스턴스
    private long clearTime; // 클리어 시간 (밀리초)
    private int score; // 던전 점수

    /**
     * 생성자
     */
    public DungeonCompleteEvent(DungeonInstance instance, long clearTime, int score) {
        this. instance = instance;
        this. clearTime = clearTime;
        this.score = score;
    }

    // ===== Getters & Setters =====

    public DungeonInstance getInstance() {
        return instance;
    }

    public void setInstance(DungeonInstance instance) {
        this.instance = instance;
    }

    public long getClearTime() {
        return clearTime;
    }

    public void setClearTime(long clearTime) {
        this.clearTime = clearTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * 클리어 시간을 MM:SS 형식으로 반환
     *
     * @return 시간 문자열
     */
    public String getClearTimeFormatted() {
        long seconds = clearTime / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    // ===== Handler =====

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}