package com.multiverse.crafting.models;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a mass crafting batch session.
 */
public class MassCraftingSession {

    private final UUID playerId;
    private final String recipeId;
    private final int totalAmount;
    private int remaining;
    private final Instant startedAt;

    public MassCraftingSession(UUID playerId, String recipeId, int totalAmount) {
        this.playerId = playerId;
        this.recipeId = recipeId;
        this.totalAmount = totalAmount;
        this.remaining = totalAmount;
        this.startedAt = Instant.now();
    }

    public UUID getPlayerId() { return playerId; }
    public String getRecipeId() { return recipeId; }
    public int getTotalAmount() { return totalAmount; }
    public int getRemaining() { return remaining; }
    public Instant getStartedAt() { return startedAt; }

    public void consumeOne() {
        if (remaining > 0) remaining--;
    }

    public boolean isFinished() {
        return remaining <= 0;
    }
}