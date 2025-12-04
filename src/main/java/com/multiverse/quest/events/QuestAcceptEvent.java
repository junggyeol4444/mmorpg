package com.multiverse.quest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit. event.Event;
import org. bukkit.event.HandlerList;

/**
 * 퀘스트 수락 이벤트
 * 플레이어가 퀘스트를 수락할 때 발생합니다.
 */
public class QuestAcceptEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Player player;
    private final String questId;
    private boolean cancelled;

    /**
     * 생성자
     * @param player 퀘스트를 수락하는 플레이어
     * @param questId 수락하는 퀘스트 ID
     */
    public QuestAcceptEvent(Player player, String questId) {
        this.player = player;
        this.questId = questId;
        this.cancelled = false;
    }

    /**
     * 퀘스트를 수락하는 플레이어 반환
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 수락하는 퀨스트 ID 반환
     */
    public String getQuestId() {
        return questId;
    }

    /**
     * 이벤트 취소 여부 설정
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.  cancelled = cancel;
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
        return String.format("QuestAcceptEvent{player=%s, quest=%s}",
                player. getName(), questId);
    }
}