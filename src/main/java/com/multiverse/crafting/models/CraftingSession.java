package com.multiverse.crafting.models;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents an in-progress crafting session (metadata only).
 */
public class CraftingSession {

    private final UUID playerId;
    private final String recipeId;
    private final int amount;
    private final Instant startedAt;
    private final long durationSeconds;

    public CraftingSession(UUID playerId, String recipeId, int amount, long durationSeconds) {
        this.playerId = playerId;
        this.recipeId = recipeId;
        this.amount = amount;
        this.durationSeconds = durationSeconds;
        this.startedAt = Instant.now();
    }

    public UUID getPlayerId() { return playerId; }
    public String getRecipeId() { return recipeId; }
    public int getAmount() { return amount; }
    public Instant getStartedAt() { return startedAt; }
    public long getDurationSeconds() { return durationSeconds; }
}