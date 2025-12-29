package com.multiverse. pet.api. event;

import com.multiverse.pet.model.Pet;
import com.multiverse.  pet.model.battle.BattleType;
import com.multiverse.pet.model. battle.PetBattle;
import org.bukkit.entity.Player;
import org.bukkit. event.Cancellable;
import org.bukkit.  event.Event;
import org.bukkit.event.HandlerList;

/**
 * 펫 배틀 시작 이벤트
 * 펫 배틀이 시작될 때 발생
 */
public class PetBattleStartEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player1;
    private final Player player2;
    private final Pet pet1;
    private final Pet pet2;
    private final BattleType battleType;
    private PetBattle battle;
    private boolean cancelled;
    private String cancelReason;

    /**
     * 생성자 (PvP)
     */
    public PetBattleStartEvent(Player player1, Pet pet1, Player player2, Pet pet2, BattleType battleType) {
        this.player1 = player1;
        this.player2 = player2;
        this. pet1 = pet1;
        this.pet2 = pet2;
        this.battleType = battleType;
        this.cancelled = false;
    }

    /**
     * 생성자 (AI)
     */
    public PetBattleStartEvent(Player player1, Pet pet1, BattleType battleType) {
        this. player1 = player1;
        this.player2 = null;
        this.pet1 = pet1;
        this. pet2 = null;
        this. battleType = battleType;
        this. cancelled = false;
    }

    /**
     * 플레이어 1 가져오기
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * 플레이어 2 가져오기
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * 펫 1 가져오기
     */
    public Pet getPet1() {
        return pet1;
    }

    /**
     * 펫 2 가져오기
     */
    public Pet getPet2() {
        return pet2;
    }

    /**
     * 배틀 타입 가져오기
     */
    public BattleType getBattleType() {
        return battleType;
    }

    /**
     * 배틀 정보 가져오기
     */
    public PetBattle getBattle() {
        return battle;
    }

    /**
     * 배틀 정보 설정
     */
    public void setBattle(PetBattle battle) {
        this. battle = battle;
    }

    /**
     * AI 배틀 여부
     */
    public boolean isAIBattle() {
        return player2 == null;
    }

    /**
     * PvP 배틀 여부
     */
    public boolean isPvPBattle() {
        return player2 != null;
    }

    /**
     * 랭크 배틀 여부
     */
    public boolean isRankedBattle() {
        return battleType == BattleType.RANKED;
    }

    /**
     * 친선 배틀 여부
     */
    public boolean isFriendlyBattle() {
        return battleType == BattleType. FRIENDLY;
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