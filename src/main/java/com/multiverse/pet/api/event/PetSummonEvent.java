package com.multiverse.pet.api.event;

import com.multiverse. pet.model.Pet;
import org.bukkit.Location;
import org. bukkit.entity. Player;
import org.bukkit.event.Cancellable;
import org. bukkit.event. Event;
import org. bukkit.event. HandlerList;

/**
 * 펫 소환 이벤트
 * 펫이 소환될 때 발생
 */
public class PetSummonEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Pet pet;
    private Location summonLocation;
    private boolean cancelled;
    private String cancelReason;

    /**
     * 생성자
     */
    public PetSummonEvent(Player player, Pet pet, Location summonLocation) {
        this. player = player;
        this.pet = pet;
        this.summonLocation = summonLocation;
        this.cancelled = false;
        this.cancelReason = null;
    }

    /**
     * 플레이어 가져오기
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 펫 가져오기
     */
    public Pet getPet() {
        return pet;
    }

    /**
     * 소환 위치 가져오기
     */
    public Location getSummonLocation() {
        return summonLocation;
    }

    /**
     * 소환 위치 설정
     */
    public void setSummonLocation(Location location) {
        this.summonLocation = location;
    }

    /**
     * 취소 여부
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * 취소 설정
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * 취소 사유와 함께 취소
     */
    public void setCancelled(boolean cancelled, String reason) {
        this.cancelled = cancelled;
        this.cancelReason = reason;
    }

    /**
     * 취소 사유 가져오기
     */
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * 핸들러 리스트
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * 핸들러 리스트 (static)
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}