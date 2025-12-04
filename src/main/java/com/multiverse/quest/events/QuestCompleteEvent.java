package com.multiverse.quest.events;

import com.multiverse. quest.models.QuestReward;
import org.bukkit.entity.Player;
import org.bukkit.event. Cancellable;
import org. bukkit.event.Event;
import org.bukkit.event. HandlerList;

/**
 * 퀘스트 완료 이벤트
 * 플레이어가 퀘스트를 완료할 때 발생합니다.
 * (보상 수령 전 단계)
 */
public class QuestCompleteEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Player player;
    private final String questId;
    private final QuestReward reward;
    private long completionTime;
    private boolean cancelled;

    /**
     * 생성자
     * @param player 퀘스트를 완료하는 플레이어
     * @param questId 완료하는 퀘스트 ID
     * @param reward 지급될 보상
     */
    public QuestCompleteEvent(Player player, String questId, QuestReward reward) {
        this.player = player;
        this.questId = questId;
        this.reward = reward;
        this.completionTime = System.currentTimeMillis();
        this.cancelled = false;
    }

    /**
     * 퀘스트를 완료하는 플레이어 반환
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 완료하는 퀘스트 ID 반환
     */
    public String getQuestId() {
        return questId;
    }

    /**
     * 지급될 보상 반환
     */
    public QuestReward getReward() {
        return reward;
    }

    /**
     * 완료 시간 반환
     */
    public long getCompletionTime() {
        return completionTime;
    }

    /**
     * 완료 시간 설정
     */
    public void setCompletionTime(long completionTime) {
        this. completionTime = completionTime;
    }

    /**
     * 보상 경험치 반환
     */
    public int getRewardExperience() {
        return reward != null ? reward.getExperience() : 0;
    }

    /**
     * 보상 경험치 설정
     */
    public void setRewardExperience(int experience) {
        if (reward != null) {
            reward.setExperience(experience);
        }
    }

    /**
     * 보상 퀘스트 포인트 반환
     */
    public int getRewardQuestPoints() {
        return reward != null ? reward.getQuestPoints() : 0;
    }

    /**
     * 보상 퀘스트 포인트 설정
     */
    public void setRewardQuestPoints(int points) {
        if (reward != null) {
            reward.setQuestPoints(points);
        }
    }

    /**
     * 보상이 있는지 확인
     */
    public boolean hasReward() {
        return reward != null && reward.hasRewards();
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
        return String.format("QuestCompleteEvent{player=%s, quest=%s, exp=%d, questPoints=%d}",
                player.getName(), questId, getRewardExperience(), getRewardQuestPoints());
    }
}