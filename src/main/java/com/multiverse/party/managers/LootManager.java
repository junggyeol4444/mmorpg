package com.multiverse.party.managers;

import com.multiverse.party.PartyCore;
import com.multiverse.party.models.Party;
import com.multiverse.party.models.LootSession;
import com.multiverse.party.models.enums.LootVoteType;
import org.bukkit.entity.Player;
import java.util.*;

/**
 * 아이템 분배(투표, 라운드 로빈, 마스터룻 등) 관리 및 세션 처리 매니저 
 */
public class LootManager {

    private final PartyCore plugin;
    // 세션ID별 분배 세션 객체
    private final Map<UUID, LootSession> sessionMap = new HashMap<>();

    public LootManager(PartyCore plugin) {
        this.plugin = plugin;
    }

    public void registerSession(LootSession session) {
        if (session == null) return;
        sessionMap.put(session.getSessionId(), session);
    }

    public LootSession getSession(UUID sessionId) {
        return sessionMap.get(sessionId);
    }

    public boolean vote(UUID sessionId, UUID playerId, LootVoteType voteType) {
        LootSession session = getSession(sessionId);
        if (session == null || session.hasVoted(playerId)) return false;
        session.setVote(playerId, voteType);
        return true;
    }

    public void finishSession(UUID sessionId) {
        LootSession session = sessionMap.remove(sessionId);
        if (session == null) return;

        // 분배 결과 처리 (예시)
        UUID winner = calculateWinner(session);
        if (winner != null) {
            Player winnerPlayer = plugin.getServer().getPlayer(winner);
            if (winnerPlayer != null) {
                winnerPlayer.getInventory().addItem(session.getItem());
                winnerPlayer.sendMessage(plugin.getMessageUtil()
                    .getMessage("loot.award-winner", "%item%",
                    session.getItem().getType().name()));
            }
        }
        // 참가자 알림 등 추가 구현 가능
    }

    private UUID calculateWinner(LootSession session) {
        // 간단화: NEED > GREED > PASS 그룹에서 랜덤 추첨
        Map<UUID, LootVoteType> votes = session.getVotes();
        List<UUID> needers = new ArrayList<>();
        List<UUID> greeders = new ArrayList<>();
        for (Map.Entry<UUID, LootVoteType> e : votes.entrySet()) {
            if (e.getValue() == LootVoteType.NEED) needers.add(e.getKey());
            else if (e.getValue() == LootVoteType.GREED) greeders.add(e.getKey());
        }
        List<UUID> candidates = !needers.isEmpty() ? needers : !greeders.isEmpty() ? greeders : new ArrayList<>();
        if (candidates.isEmpty()) return null;
        Collections.shuffle(candidates);
        return candidates.get(0);
    }
}