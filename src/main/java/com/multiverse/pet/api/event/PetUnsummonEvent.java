package com. multiverse.pet. api.event;

import com.multiverse.pet.model. Pet;
import org.bukkit.entity.Player;
import org.bukkit. event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit. event.HandlerList;

/**
 * 펫 소환 해제 이벤트
 * 펫 소환이 해제될 때 발생
 */
public class PetUnsummonEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Pet pet;
    private final UnsummonReason reason;
    private boolean cancelled;
    private String cancelReason;

    /**
     * 소환 해제 사유
     */
    public enum UnsummonReason {
        /** 플레이어가 직접 해제 */
        PLAYER_COMMAND,
        /** 펫 사망/기절 */
        PET_FAINTED,
        /** 플레이어 로그아웃 */
        PLAYER_QUIT,
        /** 월드 변경 */
        WORLD_CHANGE,
        /** 다른 펫 소환 */
        REPLACE,
        /** 보이드 추락 */
        VOID,
        /** 플러그인 리로드 */
        PLUGIN_RELOAD,
        /** 관리자 명령 */
        ADMIN_COMMAND,
        /** 교배 시작 */
        BREEDING,
        /** 배틀 종료 */
        BATTLE_END,
        /** 기타 */
        OTHER
    }

    /**
     * 생성자
     */
    public PetUnsummonEvent(Player player, Pet pet, UnsummonReason reason) {
        this.player = player;
        this.pet = pet;
        this. reason = reason;
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
     * 소환 해제 사유 가져오기
     */
    public UnsummonReason getReason() {
        return reason;
    }

    /**
     * 취소 가능 여부
     * 일부 사유는 취소 불가
     */
    public boolean isCancellable() {
        switch (reason) {
            case PET_FAINTED:
            case PLAYER_QUIT: 
            case VOID:
            case PLUGIN_RELOAD: 
                return false;
            default:
                return true;
        }
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
        if (! isCancellable() && cancelled) {
            return; // 취소 불가능한 경우 무시
        }
        this.cancelled = cancelled;
    }

    /**
     * 취소 사유와 함께 취소
     */
    public void setCancelled(boolean cancelled, String reason) {
        setCancelled(cancelled);
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