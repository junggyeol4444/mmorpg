package com.multiverse.quest.events;

import com.multiverse. quest.models.QuestReward;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit. event.Event;
import org.bukkit. event.HandlerList;
import java.util.*;

/**
 * 퀘스트 체인 완료 이벤트
 * 플레이어가 퀘스트 체인을 완료할 때 발생합니다. 
 */
public class QuestChainCompleteEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Player player;
    private final String chainId;
    private final List<String> completedQuests;   // 완료한 퀘스트 목록
    private final QuestReward chainReward;        // 체인 완료 보너스 보상
    private long completionTime;
    private boolean cancelled;

    /**
     * 생성자
     * @param player 체인을 완료한 플레이어
     * @param chainId 완료한 체인 ID
     * @param completedQuests 완료한 퀘스트 목록
     * @param chainReward 체인 완료 보너스 보상
     */
    public QuestChainCompleteEvent(Player player, String chainId, List<String> completedQuests, QuestReward chainReward) {
        this.player = player;
        this.chainId = chainId;
        this.completedQuests = new ArrayList<>(completedQuests);
        this.chainReward = chainReward;
        this. completionTime = System.currentTimeMillis();
        this.cancelled = false;
    }

    /**
     * 체인을 완료한 플레이어 반환
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 완료한 체인 ID 반환
     */
    public String getChainId() {
        return chainId;
    }

    /**
     * 완료한 퀘스트 목록 반환
     */
    public List<String> getCompletedQuests() {
        return new ArrayList<>(completedQuests);
    }

    /**
     * 완료한 퀘스트 개수 반환
     */
    public int getCompletedQuestCount() {
        return completedQuests.size();
    }

    /**
     * 체인 완료 보너스 보상 반환
     */
    public QuestReward getChainReward() {
        return chainReward;
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
    public int getChainRewardExperience() {
        return chainReward != null ? chainReward.getExperience() : 0;
    }

    /**
     * 보상 경험치 설정
     */
    public void setChainRewardExperience(int experience) {
        if (chainReward != null) {
            chainReward.setExperience(experience);
        }
    }

    /**
     * 보상 퀘스트 포인트 반환
     */
    public int getChainRewardQuestPoints() {
        return chainReward != null ? chainReward.getQuestPoints() : 0;
    }

    /**
     * 보상 퀘스트 포인트 설정
     */
    public void setChainRewardQuestPoints(int points) {
        if (chainReward != null) {
            chainReward.setQuestPoints(points);
        }
    }

    /**
     * 특정 퀘스트가 완료되었는지 확인
     */
    public boolean isQuestCompleted(String questId) {
        return completedQuests.contains(questId);
    }

    /**
     * 모든 퀘스트가 완료되었는지 확인
     */
    public boolean areAllQuestsCompleted() {
        return ! completedQuests.isEmpty();
    }

    /**
     * 체인 보상이 있는지 확인
     */
    public boolean hasChainReward() {
        return chainReward != null && chainReward.hasRewards();
    }

    /**
     * 포맷된 완료 메시지 반환
     */
    public String getFormattedCompletionMessage() {
        return String.format("§a퀘스트 체인 완료: %s§r\n§7완료한 퀘스트: §f%d개",
                chainId, completedQuests.size());
    }

    /**
     * 완료 요약 반환
     */
    public String getCompletionSummary() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 체인 완료 요약 ===§r\n");
        sb. append("§7체인: §f"). append(chainId).append("\n");
        sb.append("§7완료한 퀘스트: §f").append(completedQuests.size()). append("개\n");
        
        if (! completedQuests.isEmpty()) {
            sb.append("\n§e완료 목록:§r\n");
            for (int i = 0; i < completedQuests.size(); i++) {
                sb.append("§f").append(i + 1). append(".  ").append(completedQuests.get(i)).append("\n");
            }
        }
        
        if (hasChainReward()) {
            sb.append("\n§e체인 보너스 보상:§r\n");
            sb.append(chainReward.getSummary());
        }
        
        return sb.toString();
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
        return String.format("QuestChainCompleteEvent{player=%s, chain=%s, quests=%d, exp=%d}",
                player.getName(), chainId, completedQuests.size(), getChainRewardExperience());
    }
}