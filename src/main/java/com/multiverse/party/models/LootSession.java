package com.multiverse.party.models;

import com.multiverse.party.models.enums.LootVoteType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * 아이템 분배 세션 데이터 (투표, 라운드로빈 등)
 */
public class LootSession {

    private UUID sessionId;
    private UUID partyId;
    private ItemStack item;
    private List<UUID> eligiblePlayers;
    private int duration; // 초 단위
    private Map<UUID, LootVoteType> votes;
    private boolean finished;

    public LootSession(UUID sessionId, UUID partyId, ItemStack item, List<UUID> eligiblePlayers, int duration) {
        this.sessionId = sessionId;
        this.partyId = partyId;
        this.item = item;
        this.eligiblePlayers = eligiblePlayers;
        this.duration = duration;
        this.votes = new HashMap<>();
        this.finished = false;
    }

    public UUID getSessionId() { return sessionId; }
    public UUID getPartyId() { return partyId; }
    public ItemStack getItem() { return item; }
    public List<UUID> getEligiblePlayers() { return eligiblePlayers; }
    public int getDuration() { return duration; }

    public Map<UUID, LootVoteType> getVotes() { return votes; }
    public void setVote(UUID playerId, LootVoteType type) { votes.put(playerId, type); }
    public boolean hasVoted(UUID playerId) { return votes.containsKey(playerId); }

    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }
}