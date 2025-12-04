package com.multiverse.dungeon.events;

import com. multiverse.dungeon.data. model.DungeonBoss;
import com.multiverse.dungeon.data.model. DungeonInstance;
import org.bukkit.Location;
import org.bukkit. entity.LivingEntity;
import org. bukkit.event.Event;
import org.bukkit.event. HandlerList;

/**
 * 던전 보스 스폰 이벤트
 */
public class DungeonBossSpawnEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private DungeonInstance instance; // 던전 인스턴스
    private DungeonBoss boss; // 던전 보스
    private Location spawnLocation; // 스폰 위치
    private LivingEntity entity; // 스폰된 엔티티
    private boolean cancelled = false;

    /**
     * 생성자
     */
    public DungeonBossSpawnEvent(DungeonInstance instance, DungeonBoss boss, Location spawnLocation) {
        this.instance = instance;
        this.boss = boss;
        this.spawnLocation = spawnLocation;
        this.entity = null;
    }

    // ===== Getters & Setters =====

    public DungeonInstance getInstance() {
        return instance;
    }

    public void setInstance(DungeonInstance instance) {
        this.instance = instance;
    }

    public DungeonBoss getBoss() {
        return boss;
    }

    public void setBoss(DungeonBoss boss) {
        this.boss = boss;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this. spawnLocation = spawnLocation;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void setEntity(LivingEntity entity) {
        this. entity = entity;
    }

    // ===== Cancellable =====

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.  cancelled = cancel;
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