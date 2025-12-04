package com.multiverse.quest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.  Cancellable;
import org.  bukkit.event.Event;
import org.bukkit. event.  HandlerList;

/**
 * 퀘스트 실패 이벤트
 * 플레이어가 퀘스트에 실패할 때 발생합니다.
 */
public class QuestFailEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Player player;
    private final String questId;
    private String failReason;              // 실패 이유
    private long failTime;                  // 실패 시간
    private boolean allowRetry;             // 재시도 허용 여부
    private boolean cancelled;

    /**
     * 생성자
     * @param player 퀘스트에 실패한 플레이어
     * @param questId 실패한 퀘스트 ID
     * @param failReason 실패 이유
     */
    public QuestFailEvent(Player player, String questId, String failReason) {
        this.player = player;
        this.questId = questId;
        this.  failReason = failReason;
        this.failTime = System.currentTimeMillis();
        this.allowRetry = true;
        this.cancelled = false;
    }

    /**
     * 퀘스트에 실패한 플레이어 반환
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 실패한 퀘스트 ID 반환
     */
    public String getQuestId() {
        return questId;
    }

    /**
     * 실패 이유 반환
     */
    public String getFailReason() {
        return failReason;
    }

    /**
     * 실패 이유 설정
     */
    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    /**
     * 실패 시간 반환
     */
    public long getFailTime() {
        return failTime;
    }

    /**
     * 실패 시간 설정
     */
    public void setFailTime(long failTime) {
        this.failTime = failTime;
    }

    /**
     * 재시도 허용 여부 반환
     */
    public boolean isAllowRetry() {
        return allowRetry;
    }

    /**
     * 재시도 허용 여부 설정
     */
    public void setAllowRetry(boolean allowRetry) {
        this.allowRetry = allowRetry;
    }

    /**
     * 실패 이유가 시간 제한인지 확인
     */
    public boolean isTimeLimit() {
        return failReason != null && failReason.contains("시간");
    }

    /**
     * 실패 이유가 목표 미달성인지 확인
     */
    public boolean isObjectiveNotCompleted() {
        return failReason != null && failReason. contains("목표");
    }

    /**
     * 실패 이유가 플레이어 포기인지 확인
     */
    public boolean isPlayerAbandoned() {
        return failReason != null && failReason.contains("포기");
    }

    /**
     * 포맷된 실패 메시지 반환
     */
    public String getFormattedFailMessage() {
        return String.format("§c퀘스트 실패: %s§r\n§7이유: §f%s",
                questId, failReason != null ? failReason : "알 수 없음");
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
        return String.format("QuestFailEvent{player=%s, quest=%s, reason=%s, allowRetry=%s}",
                player.getName(), questId, failReason, allowRetry);
    }
}