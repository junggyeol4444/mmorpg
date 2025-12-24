package com.multiverse.pvp.data;

import com. multiverse.pvp.enums.DuelEndReason;
import com.multiverse.pvp.enums. DuelStatus;
import org.bukkit.Location;
import org. bukkit.inventory.ItemStack;

import java.util.*;

public class Duel {

    private UUID duelId;
    private UUID challenger;
    private UUID opponent;

    // 베팅
    private Map<String, Double> betMoney;
    private List<ItemStack> challengerBetItems;
    private List<ItemStack> opponentBetItems;

    // 아레나
    private UUID arenaId;

    // 상태
    private DuelStatus status;

    // 시간
    private long requestTime;
    private long startTime;
    private long endTime;
    private int duration; // 최대 듀얼 시간 (초)

    // 결과
    private UUID winner;
    private UUID loser;
    private DuelEndReason endReason;

    // 위치 (듀얼 전 위치 저장)
    private Location challengerPreviousLocation;
    private Location opponentPreviousLocation;

    // 전투 통계
    private Map<UUID, Integer> damageDealt;
    private Map<UUID, Integer> damageReceived;
    private Map<UUID, Integer> hits;

    // 체력 기록
    private Map<UUID, Double> startHealth;

    public Duel(UUID challenger, UUID opponent) {
        this. duelId = UUID. randomUUID();
        this.challenger = challenger;
        this.opponent = opponent;

        this.betMoney = new HashMap<>();
        this.challengerBetItems = new ArrayList<>();
        this.opponentBetItems = new ArrayList<>();

        this.status = DuelStatus. REQUESTED;
        this. requestTime = System. currentTimeMillis();
        this.duration = 300; // 기본 5분

        this.damageDealt = new HashMap<>();
        this.damageReceived = new HashMap<>();
        this.hits = new HashMap<>();
        this.startHealth = new HashMap<>();

        // 초기화
        initializeStats(challenger);
        initializeStats(opponent);
    }

    private void initializeStats(UUID playerId) {
        damageDealt.put(playerId, 0);
        damageReceived.put(playerId, 0);
        hits.put(playerId, 0);
    }

    // ==================== Getters ====================

    public UUID getDuelId() {
        return duelId;
    }

    public UUID getChallenger() {
        return challenger;
    }

    public UUID getOpponent() {
        return opponent;
    }

    public Map<String, Double> getBetMoney() {
        return betMoney;
    }

    public List<ItemStack> getChallengerBetItems() {
        return challengerBetItems;
    }

    public List<ItemStack> getOpponentBetItems() {
        return opponentBetItems;
    }

    public UUID getArenaId() {
        return arenaId;
    }

    public DuelStatus getStatus() {
        return status;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getDuration() {
        return duration;
    }

    public UUID getWinner() {
        return winner;
    }

    public UUID getLoser() {
        return loser;
    }

    public DuelEndReason getEndReason() {
        return endReason;
    }

    public Location getChallengerPreviousLocation() {
        return challengerPreviousLocation;
    }

    public Location getOpponentPreviousLocation() {
        return opponentPreviousLocation;
    }

    public Map<UUID, Integer> getDamageDealt() {
        return damageDealt;
    }

    public Map<UUID, Integer> getDamageReceived() {
        return damageReceived;
    }

    public Map<UUID, Integer> getHits() {
        return hits;
    }

    public Map<UUID, Double> getStartHealth() {
        return startHealth;
    }

    // ==================== Setters ====================

    public void setDuelId(UUID duelId) {
        this.duelId = duelId;
    }

    public void setChallenger(UUID challenger) {
        this.challenger = challenger;
    }

    public void setOpponent(UUID opponent) {
        this.opponent = opponent;
    }

    public void setBetMoney(Map<String, Double> betMoney) {
        this. betMoney = betMoney;
    }

    public void setChallengerBetItems(List<ItemStack> challengerBetItems) {
        this.challengerBetItems = challengerBetItems;
    }

    public void setOpponentBetItems(List<ItemStack> opponentBetItems) {
        this.opponentBetItems = opponentBetItems;
    }

    public void setArenaId(UUID arenaId) {
        this. arenaId = arenaId;
    }

    public void setStatus(DuelStatus status) {
        this.status = status;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setWinner(UUID winner) {
        this.winner = winner;
        this.loser = winner. equals(challenger) ? opponent : challenger;
    }

    public void setLoser(UUID loser) {
        this.loser = loser;
    }

    public void setEndReason(DuelEndReason endReason) {
        this.endReason = endReason;
    }

    public void setChallengerPreviousLocation(Location challengerPreviousLocation) {
        this.challengerPreviousLocation = challengerPreviousLocation;
    }

    public void setOpponentPreviousLocation(Location opponentPreviousLocation) {
        this.opponentPreviousLocation = opponentPreviousLocation;
    }

    public void setDamageDealt(Map<UUID, Integer> damageDealt) {
        this.damageDealt = damageDealt;
    }

    public void setDamageReceived(Map<UUID, Integer> damageReceived) {
        this.damageReceived = damageReceived;
    }

    public void setHits(Map<UUID, Integer> hits) {
        this.hits = hits;
    }

    public void setStartHealth(Map<UUID, Double> startHealth) {
        this. startHealth = startHealth;
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 듀얼 참가자인지 확인
     */
    public boolean isParticipant(UUID playerId) {
        return playerId.equals(challenger) || playerId.equals(opponent);
    }

    /**
     * 상대방 UUID 반환
     */
    public UUID getOpponentOf(UUID playerId) {
        if (playerId.equals(challenger)) {
            return opponent;
        } else if (playerId.equals(opponent)) {
            return challenger;
        }
        return null;
    }

    /**
     * 듀얼 수락
     */
    public void accept() {
        this. status = DuelStatus.ACCEPTED;
    }

    /**
     * 듀얼 시작
     */
    public void start() {
        this.status = DuelStatus.ACTIVE;
        this. startTime = System. currentTimeMillis();
    }

    /**
     * 듀얼 종료
     */
    public void end(UUID winnerId, DuelEndReason reason) {
        this.status = DuelStatus.ENDED;
        this. endTime = System. currentTimeMillis();
        this.winner = winnerId;
        this.loser = winnerId. equals(challenger) ? opponent : challenger;
        this.endReason = reason;
    }

    /**
     * 요청 만료 여부 확인
     */
    public boolean isRequestExpired(int expireTimeSeconds) {
        if (status != DuelStatus. REQUESTED) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - requestTime;
        return elapsed > (expireTimeSeconds * 1000L);
    }

    /**
     * 듀얼 시간 초과 여부 확인
     */
    public boolean isTimedOut() {
        if (status != DuelStatus.ACTIVE || startTime == 0) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed > (duration * 1000L);
    }

    /**
     * 남은 시간 (초)
     */
    public long getRemainingTime() {
        if (startTime == 0) {
            return duration;
        }
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        return Math.max(0, duration - elapsed);
    }

    /**
     * 경과 시간 (초)
     */
    public long getElapsedTime() {
        if (startTime == 0) {
            return 0;
        }
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    /**
     * 베팅 금액 추가
     */
    public void addBetMoney(String currency, double amount) {
        betMoney.put(currency, betMoney.getOrDefault(currency, 0.0) + amount);
    }

    /**
     * 총 베팅 금액 조회
     */
    public double getTotalBetMoney(String currency) {
        return betMoney.getOrDefault(currency, 0.0);
    }

    /**
     * 베팅이 있는지 확인
     */
    public boolean hasBetting() {
        if (! betMoney.isEmpty()) {
            for (double amount : betMoney.values()) {
                if (amount > 0) {
                    return true;
                }
            }
        }
        return ! challengerBetItems. isEmpty() || !opponentBetItems. isEmpty();
    }

    /**
     * 데미지 기록
     */
    public void recordDamage(UUID attacker, UUID victim, int damage) {
        damageDealt.put(attacker, damageDealt.getOrDefault(attacker, 0) + damage);
        damageReceived.put(victim, damageReceived.getOrDefault(victim, 0) + damage);
        hits.put(attacker, hits.getOrDefault(attacker, 0) + 1);
    }

    /**
     * 플레이어 데미지 조회
     */
    public int getDamageDealt(UUID playerId) {
        return damageDealt.getOrDefault(playerId, 0);
    }

    /**
     * 플레이어 히트 수 조회
     */
    public int getHitCount(UUID playerId) {
        return hits.getOrDefault(playerId, 0);
    }

    /**
     * 시작 체력 저장
     */
    public void saveStartHealth(UUID playerId, double health) {
        startHealth.put(playerId, health);
    }

    /**
     * 플레이어 이전 위치 반환
     */
    public Location getPreviousLocation(UUID playerId) {
        if (playerId.equals(challenger)) {
            return challengerPreviousLocation;
        } else if (playerId.equals(opponent)) {
            return opponentPreviousLocation;
        }
        return null;
    }

    /**
     * 플레이어 이전 위치 저장
     */
    public void savePreviousLocation(UUID playerId, Location location) {
        if (playerId. equals(challenger)) {
            this.challengerPreviousLocation = location;
        } else if (playerId.equals(opponent)) {
            this.opponentPreviousLocation = location;
        }
    }

    /**
     * 듀얼 진행 중인지 확인
     */
    public boolean isActive() {
        return status == DuelStatus.ACTIVE;
    }

    /**
     * 듀얼 대기 중인지 확인
     */
    public boolean isPending() {
        return status == DuelStatus.REQUESTED || status == DuelStatus.ACCEPTED;
    }

    /**
     * 듀얼 종료되었는지 확인
     */
    public boolean isEnded() {
        return status == DuelStatus.ENDED;
    }

    /**
     * 모든 베팅 아이템 반환 (승자에게)
     */
    public List<ItemStack> getAllBetItems() {
        List<ItemStack> allItems = new ArrayList<>();
        allItems. addAll(challengerBetItems);
        allItems.addAll(opponentBetItems);
        return allItems;
    }

    /**
     * 듀얼 통계 요약
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("듀얼 ID: ").append(duelId. toString().substring(0, 8)).append("\n");
        sb.append("상태: ").append(status. getDisplayName()).append("\n");
        
        if (isEnded()) {
            sb.append("종료 사유: ").append(endReason. getDisplayName()).append("\n");
            sb.append("진행 시간: ").append(getElapsedTime()).append("초\n");
        }
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duel duel = (Duel) o;
        return Objects.equals(duelId, duel. duelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duelId);
    }

    @Override
    public String toString() {
        return "Duel{" +
                "duelId=" + duelId +
                ", challenger=" + challenger +
                ", opponent=" + opponent +
                ", status=" + status +
                ", hasBetting=" + hasBetting() +
                '}';
    }
}