package com.multiverse. dungeon.events;

import com.multiverse.dungeon.data.enums.DungeonDifficulty;
import com.multiverse.dungeon.data.model.Dungeon;
import com.multiverse.dungeon.data.model.Party;
import org.bukkit.event.Event;
import org.bukkit. event.HandlerList;

/**
 * 던전 입장 이벤트
 */
public class DungeonEnterEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private Party party; // 파티
    private Dungeon dungeon; // 던전
    private DungeonDifficulty difficulty; // 난이도
    private boolean cancelled = false;

    /**
     * 생성자
     */
    public DungeonEnterEvent(Party party, Dungeon dungeon, DungeonDifficulty difficulty) {
        this.party = party;
        this.dungeon = dungeon;
        this.difficulty = difficulty;
    }

    // ===== Getters & Setters =====

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public DungeonDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DungeonDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    // ===== Cancellable =====

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
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