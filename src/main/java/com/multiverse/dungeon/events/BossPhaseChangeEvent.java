package com.multiverse.dungeon.events;

import com.multiverse.dungeon.data.model.BossPhase;
import com. multiverse.dungeon.data. model.DungeonBoss;
import com.multiverse.dungeon.data.model. DungeonInstance;
import org.bukkit.entity.LivingEntity;
import org. bukkit.event.Event;
import org.bukkit.event. HandlerList;

/**
 * 보스 페이즈 변경 이벤트
 */
public class BossPhaseChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private DungeonInstance instance; // 던전 인스턴스
    private DungeonBoss boss; // 던전 보스
    private LivingEntity bossEntity; // 보스 엔티티
    private BossPhase previousPhase; // 이전 페이즈
    private BossPhase newPhase; // 새 페이즈
    private double healthPercent; // 현재 체력 (%)

    /**
     * 생성자
     */
    public BossPhaseChangeEvent(DungeonInstance instance, DungeonBoss boss, 
                                 LivingEntity bossEntity, BossPhase previousPhase, 
                                 BossPhase newPhase, double healthPercent) {
        this.instance = instance;
        this.boss = boss;
        this.bossEntity = bossEntity;
        this.previousPhase = previousPhase;
        this.newPhase = newPhase;
        this.healthPercent = healthPercent;
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

    public BossPhase getPreviousPhase() {
        return previousPhase;
    }

    public void setPreviousPhase(BossPhase previousPhase) {
        this.previousPhase = previousPhase;
    }

    public BossPhase getNewPhase() {
        return newPhase;
    }

    public void setNewPhase(BossPhase newPhase) {
        this.newPhase = newPhase;
    }

    public double getHealthPercent() {
        return healthPercent;
    }

    public void setHealthPercent(double healthPercent) {
        this.healthPercent = Math.max(0, Math.min(100, healthPercent));
    }

    /**
     * 이전 페이즈 번호
     *
     * @return 페이즈 번호, 없으면 0
     */
    public int getPreviousPhaseNumber() {
        return previousPhase != null ? previousPhase.getPhaseNumber() : 0;
    }

    /**
     * 새 페이즈 번호
     *
     * @return 페이즈 번호
     */
    public int getNewPhaseNumber() {
        return newPhase != null ? newPhase.getPhaseNumber() : 0;
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