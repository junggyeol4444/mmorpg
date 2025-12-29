package com.multiverse. pet.api.event;

import com. multiverse.pet. model.Pet;
import com.multiverse. pet.model. battle.BattleResult;
import com. multiverse.pet. model.battle.BattleType;
import com.  multiverse.pet.  model.battle.PetBattle;
import org.  bukkit.entity.  Player;
import org.bukkit.event.Event;
import org. bukkit.event. HandlerList;

import java.util.  UUID;

/**
 * 펫 배틀 종료 이벤트
 * 펫 배틀이 종료될 때 발생
 */
public class PetBattleEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player1;
    private final Player player2;
    private final Pet pet1;
    private final Pet pet2;
    private final PetBattle battle;
    private final BattleResult result;
    private final EndReason endReason;

    // 보상 관련
    private int winnerExp;
    private int loserExp;
    private int winnerRatingChange;
    private int loserRatingChange;
    private double winnerGold;
    private double loserGold;

    /**
     * 배틀 종료 사유
     */
    public enum EndReason {
        /** 정상 종료 (승패 결정) */
        NORMAL,
        /** 시간 초과 */
        TIMEOUT,
        /** 항복 */
        SURRENDER,
        /** 연결 끊김 */
        DISCONNECT,
        /** 무승부 */
        DRAW,
        /** 관리자 강제 종료 */
        ADMIN_FORCE,
        /** 오류 */
        ERROR
    }

    /**
     * 생성자
     */
    public PetBattleEndEvent(Player player1, Player player2, Pet pet1, Pet pet2, 
                              PetBattle battle, BattleResult result, EndReason endReason) {
        this.player1 = player1;
        this.player2 = player2;
        this. pet1 = pet1;
        this.pet2 = pet2;
        this.battle = battle;
        this. result = result;
        this.endReason = endReason;
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
     * 배틀 정보 가져오기
     */
    public PetBattle getBattle() {
        return battle;
    }

    /**
     * 결과 가져오기
     */
    public BattleResult getResult() {
        return result;
    }

    /**
     * 종료 사유 가져오기
     */
    public EndReason getEndReason() {
        return endReason;
    }

    /**
     * 승자 ID 가져오기
     */
    public UUID getWinnerId() {
        return result.getWinnerId();
    }

    /**
     * 패자 ID 가져오기
     */
    public UUID getLoserId() {
        return result.getLoserId();
    }

    /**
     * 승자 플레이어 가져오기
     */
    public Player getWinner() {
        UUID winnerId = getWinnerId();
        if (winnerId == null) return null;
        if (player1 != null && player1.getUniqueId().equals(winnerId)) return player1;
        if (player2 != null && player2.getUniqueId().equals(winnerId)) return player2;
        return null;
    }

    /**
     * 패자 플레이어 가져오기
     */
    public Player getLoser() {
        UUID loserId = getLoserId();
        if (loserId == null) return null;
        if (player1 != null && player1.getUniqueId().equals(loserId)) return player1;
        if (player2 != null && player2.getUniqueId().equals(loserId)) return player2;
        return null;
    }

    /**
     * 무승부 여부
     */
    public boolean isDraw() {
        return result.getResultType() == BattleResult.ResultType.DRAW;
    }

    /**
     * AI 배틀 여부
     */
    public boolean isAIBattle() {
        return player2 == null;
    }

    /**
     * 랭크 배틀 여부
     */
    public boolean isRankedBattle() {
        return battle.getBattleType() == BattleType. RANKED;
    }

    /**
     * 배틀 지속 시간 (밀리초)
     */
    public long getDuration() {
        return battle.getDuration();
    }

    /**
     * 총 턴 수
     */
    public int getTotalTurns() {
        return battle. getCurrentTurn();
    }

    // ===== 보상 관련 =====

    public int getWinnerExp() {
        return winnerExp;
    }

    public void setWinnerExp(int winnerExp) {
        this.  winnerExp = winnerExp;
    }

    public int getLoserExp() {
        return loserExp;
    }

    public void setLoserExp(int loserExp) {
        this. loserExp = loserExp;
    }

    public int getWinnerRatingChange() {
        return winnerRatingChange;
    }

    public void setWinnerRatingChange(int winnerRatingChange) {
        this. winnerRatingChange = winnerRatingChange;
    }

    public int getLoserRatingChange() {
        return loserRatingChange;
    }

    public void setLoserRatingChange(int loserRatingChange) {
        this.loserRatingChange = loserRatingChange;
    }

    public double getWinnerGold() {
        return winnerGold;
    }

    public void setWinnerGold(double winnerGold) {
        this.winnerGold = winnerGold;
    }

    public double getLoserGold() {
        return loserGold;
    }

    public void setLoserGold(double loserGold) {
        this.  loserGold = loserGold;
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