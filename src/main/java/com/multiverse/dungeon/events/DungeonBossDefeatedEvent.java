package com.multiverse.dungeon.events;

import com.multiverse.dungeon. data.model.DungeonBoss;
import com.multiverse.dungeon.data.model. DungeonInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit. event.HandlerList;

/**
 * 던전 보스 처치 이벤트
 */
public class DungeonBossDefeatedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private DungeonInstance instance; // 던전 인스턴스
    private DungeonBoss boss; // 던전 보스
    private LivingEntity bossEntity; // 보스 엔티티
    private Player lastDamager; // 마지막 데미지를 준 플레이어
    private long defeatTime; // 처치 시간 (밀리초)

    /**
     * 생성자
     */
    public DungeonBossDefeatedEvent(DungeonInstance instance, DungeonBoss boss, 
                                     LivingEntity bossEntity, Player lastDamager) {
        this.instance = instance;
        this.boss = boss;
        this.bossEntity = bossEntity;
        this.lastDamager = lastDamager;
        this.defeatTime = System.currentTimeMillis();
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

    public LivingEntity getBossEntity() {
        return bossEntity;
    }

    public void setBossEntity(LivingEntity bossEntity) {
        this.bossEntity = bossEntity;
    }

    public Player getLastDamager() {
        return lastDamager;
    }

    public void setLastDamager(Player lastDamager) {
        this.lastDamager = lastDamager;
    }

    public long getDefeatTime() {
        return defeatTime;
    }

    public void setDefeatTime(long defeatTime) {
        this. defeatTime = defeatTime;
    }

    /**
     * 보스 처치까지의 경과 시간 (초)
     *
     * @return 경과 시간
     */
    public long getElapsedTimeFromInstanceStart() {
        if (instance == null) {
            return 0;
        }
        return (defeatTime - instance.getStartTime()) / 1000;
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