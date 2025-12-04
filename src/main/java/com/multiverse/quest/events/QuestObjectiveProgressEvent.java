package com.multiverse.quest.events;

import org.bukkit.entity.Player;
import org.bukkit.event. Cancellable;
import org. bukkit.event.Event;
import org.bukkit. event.HandlerList;

/**
 * 퀘스트 목표 진행 이벤트
 * 플레이어의 퀘스트 목표가 진행될 때 발생합니다.
 */
public class QuestObjectiveProgressEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Player player;
    private final String questId;
    private final String objectiveId;
    private int progressAmount;           // 진행량
    private int oldProgress;              // 이전 진행도
    private int newProgress;              // 새로운 진행도
    private int requiredAmount;           // 필요량
    private boolean completed;            // 목표 완료 여부
    private boolean cancelled;

    /**
     * 생성자
     * @param player 진행하는 플레이어
     * @param questId 퀘스트 ID
     * @param objectiveId 목표 ID
     * @param progressAmount 진행량
     * @param oldProgress 이전 진행도
     * @param newProgress 새로운 진행도
     * @param requiredAmount 필요량
     */
    public QuestObjectiveProgressEvent(Player player, String questId, String objectiveId,
                                       int progressAmount, int oldProgress, int newProgress, int requiredAmount) {
        this.player = player;
        this.questId = questId;
        this. objectiveId = objectiveId;
        this.progressAmount = progressAmount;
        this.oldProgress = oldProgress;
        this. newProgress = newProgress;
        this.requiredAmount = requiredAmount;
        this.completed = newProgress >= requiredAmount;
        this.cancelled = false;
    }

    /**
     * 목표를 진행하는 플레이어 반환
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 퀘스트 ID 반환
     */
    public String getQuestId() {
        return questId;
    }

    /**
     * 목표 ID 반환
     */
    public String getObjectiveId() {
        return objectiveId;
    }

    /**
     * 진행량 반환
     */
    public int getProgressAmount() {
        return progressAmount;
    }

    /**
     * 진행량 설정
     */
    public void setProgressAmount(int progressAmount) {
        this.progressAmount = progressAmount;
        this.newProgress = oldProgress + progressAmount;
        this.completed = newProgress >= requiredAmount;
    }

    /**
     * 이전 진행도 반환
     */
    public int getOldProgress() {
        return oldProgress;
    }

    /**
     * 새로운 진행도 반환
     */
    public int getNewProgress() {
        return newProgress;
    }

    /**
     * 새로운 진행도 설정
     */
    public void setNewProgress(int newProgress) {
        this.newProgress = Math.max(0, Math.min(newProgress, requiredAmount));
        this.progressAmount = this.newProgress - oldProgress;
        this.completed = this.newProgress >= requiredAmount;
    }

    /**
     * 필요량 반환
     */
    public int getRequiredAmount() {
        return requiredAmount;
    }

    /**
     * 목표가 완료되었는지 확인
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * 진행률 반환 (0~100)
     */
    public int getProgressPercentage() {
        if (requiredAmount == 0) return 0;
        return (newProgress * 100) / requiredAmount;
    }

    /**
     * 남은 진행량 반환
     */
    public int getRemainingAmount() {
        return Math.max(requiredAmount - newProgress, 0);
    }

    /**
     * 진행도 문자열 표현 (예: "5/10")
     */
    public String getProgressString() {
        return String.format("%d/%d", newProgress, requiredAmount);
    }

    /**
     * 이벤트 취소 여부 설정
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * 이벤트가 취소되었는지 확인
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * 핸들러 리스트 반환
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * 정적 핸들러 리스트 반환
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * 문자열 표현
     */
    @Override
    public String toString() {
        return String.format("QuestObjectiveProgressEvent{player=%s, quest=%s, objective=%s, progress=%s}",
                player. getName(), questId, objectiveId, getProgressString());
    }
}