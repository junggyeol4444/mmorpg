package com.multiverse.pet.model.battle;

import java.util.*;

/**
 * 펫 대결 데이터 클래스
 * 펫 배틀의 진행 상태와 결과를 정의
 */
public class PetBattle {

    // 기본 정보
    private UUID battleId;
    private BattleType type;
    private BattleStatus status;

    // 참가자
    private UUID player1Id;
    private UUID player2Id;
    private UUID pet1Id;
    private UUID pet2Id;
    private String player1Name;
    private String player2Name;
    private String pet1Name;
    private String pet2Name;

    // 전투 상태
    private double pet1HP;
    private double pet1MaxHP;
    private double pet2HP;
    private double pet2MaxHP;

    // 턴 정보
    private int currentTurn;
    private int maxTurns;
    private UUID currentTurnPlayerId;
    private long turnStartTime;
    private int turnTimeLimit;          // 초

    // 전투 기록
    private List<BattleTurn> turnHistory;
    private List<String> battleLog;

    // 결과
    private UUID winnerId;
    private UUID winnerPetId;
    private BattleResult result;

    // 보상
    private int winnerExp;
    private int loserExp;
    private int ratingChange;

    // 시간
    private long startTime;
    private long endTime;

    // 관전
    private List<UUID> spectators;
    private boolean allowSpectators;

    // AI 전투 (싱글플레이)
    private boolean isAIBattle;
    private int aiDifficulty;           // 1-5

    /**
     * 기본 생성자
     */
    public PetBattle() {
        this.battleId = UUID.randomUUID();
        this.status = BattleStatus.PREPARING;
        this. type = BattleType.FRIENDLY;
        this. turnHistory = new ArrayList<>();
        this.battleLog = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.currentTurn = 0;
        this. maxTurns = 50;
        this. turnTimeLimit = 30;
        this. allowSpectators = true;
        this. isAIBattle = false;
        this.aiDifficulty = 3;
    }

    /**
     * 전체 생성자
     */
    public PetBattle(UUID player1Id, UUID player2Id, UUID pet1Id, UUID pet2Id, BattleType type) {
        this();
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.pet1Id = pet1Id;
        this. pet2Id = pet2Id;
        this.type = type;
    }

    /**
     * AI 배틀 생성자
     */
    public PetBattle(UUID playerId, UUID petId, int aiDifficulty) {
        this();
        this.player1Id = playerId;
        this.pet1Id = petId;
        this.isAIBattle = true;
        this.aiDifficulty = aiDifficulty;
        this.player2Name = "AI 트레이너";
        this.pet2Name = "AI 펫";
    }

    // ===== 전투 진행 메서드 =====

    /**
     * 전투 시작
     */
    public void start(double pet1MaxHP, double pet2MaxHP) {
        this.status = BattleStatus.ACTIVE;
        this. startTime = System.currentTimeMillis();
        this.pet1HP = pet1MaxHP;
        this. pet1MaxHP = pet1MaxHP;
        this.pet2HP = pet2MaxHP;
        this. pet2MaxHP = pet2MaxHP;
        this.currentTurn = 1;
        this. currentTurnPlayerId = player1Id; // 플레이어1 선공
        this.turnStartTime = System. currentTimeMillis();

        addLog("전투가 시작되었습니다!");
        addLog(player1Name + "의 " + pet1Name + " vs " + player2Name + "의 " + pet2Name);
    }

    /**
     * 다음 턴으로 진행
     */
    public void nextTurn() {
        currentTurn++;
        turnStartTime = System.currentTimeMillis();

        // 턴 교대
        if (currentTurnPlayerId. equals(player1Id)) {
            currentTurnPlayerId = player2Id;
        } else {
            currentTurnPlayerId = player1Id;
        }

        // 최대 턴 초과 시 무승부
        if (currentTurn > maxTurns) {
            endBattle(null, BattleResult. DRAW);
        }
    }

    /**
     * 데미지 적용
     *
     * @param targetPetNumber 타겟 펫 번호 (1 또는 2)
     * @param damage 데미지량
     * @return 타겟이 기절했는지 여부
     */
    public boolean applyDamage(int targetPetNumber, double damage) {
        if (targetPetNumber == 1) {
            pet1HP = Math.max(0, pet1HP - damage);
            addLog(pet1Name + "이(가) " + String.format("%.1f", damage) + " 데미지를 받았습니다.  (남은 HP: " + String.format("%.1f", pet1HP) + ")");
            if (pet1HP <= 0) {
                addLog(pet1Name + "이(가) 기절했습니다!");
                return true;
            }
        } else {
            pet2HP = Math. max(0, pet2HP - damage);
            addLog(pet2Name + "이(가) " + String.format("%.1f", damage) + " 데미지를 받았습니다.  (남은 HP: " + String. format("%.1f", pet2HP) + ")");
            if (pet2HP <= 0) {
                addLog(pet2Name + "이(가) 기절했습니다!");
                return true;
            }
        }
        return false;
    }

    /**
     * 힐 적용
     *
     * @param targetPetNumber 타겟 펫 번호 (1 또는 2)
     * @param healing 힐량
     */
    public void applyHealing(int targetPetNumber, double healing) {
        if (targetPetNumber == 1) {
            double oldHP = pet1HP;
            pet1HP = Math.min(pet1MaxHP, pet1HP + healing);
            addLog(pet1Name + "이(가) " + String.format("%.1f", pet1HP - oldHP) + " HP를 회복했습니다.");
        } else {
            double oldHP = pet2HP;
            pet2HP = Math.min(pet2MaxHP, pet2HP + healing);
            addLog(pet2Name + "이(가) " + String.format("%. 1f", pet2HP - oldHP) + " HP를 회복했습니다.");
        }
    }

    /**
     * 턴 기록 추가
     */
    public void recordTurn(BattleTurn turn) {
        turn.setTurnNumber(currentTurn);
        turnHistory.add(turn);
    }

    /**
     * 로그 추가
     */
    public void addLog(String message) {
        String timestamp = String.format("[%d턴] ", currentTurn);
        battleLog.add(timestamp + message);
    }

    /**
     * 전투 종료
     */
    public void endBattle(UUID winnerId, BattleResult result) {
        this.status = BattleStatus. ENDED;
        this. endTime = System.currentTimeMillis();
        this.winnerId = winnerId;
        this.result = result;

        if (winnerId != null) {
            if (winnerId.equals(player1Id)) {
                this.winnerPetId = pet1Id;
                addLog(player1Name + "의 " + pet1Name + " 승리!");
            } else {
                this.winnerPetId = pet2Id;
                addLog(player2Name + "의 " + pet2Name + " 승리!");
            }
        } else {
            addLog("무승부!");
        }
    }

    // ===== 상태 확인 메서드 =====

    /**
     * 전투 진행 중인지 확인
     */
    public boolean isActive() {
        return status == BattleStatus.ACTIVE;
    }

    /**
     * 전투 종료되었는지 확인
     */
    public boolean isEnded() {
        return status == BattleStatus.ENDED;
    }

    /**
     * 준비 중인지 확인
     */
    public boolean isPreparing() {
        return status == BattleStatus.PREPARING;
    }

    /**
     * 턴 시간 초과 여부
     */
    public boolean isTurnTimeout() {
        long elapsed = System.currentTimeMillis() - turnStartTime;
        return elapsed > (turnTimeLimit * 1000L);
    }

    /**
     * 턴 남은 시간 (초)
     */
    public int getTurnRemainingTime() {
        long elapsed = System.currentTimeMillis() - turnStartTime;
        int remaining = turnTimeLimit - (int) (elapsed / 1000);
        return Math. max(0, remaining);
    }

    /**
     * 특정 플레이어의 턴인지 확인
     */
    public boolean isPlayerTurn(UUID playerId) {
        return playerId.equals(currentTurnPlayerId);
    }

    /**
     * 플레이어가 참가자인지 확인
     */
    public boolean isParticipant(UUID playerId) {
        return playerId.equals(player1Id) || playerId.equals(player2Id);
    }

    /**
     * HP 퍼센트 반환
     */
    public double getPet1HPPercentage() {
        return pet1MaxHP > 0 ? (pet1HP / pet1MaxHP) * 100 : 0;
    }

    public double getPet2HPPercentage() {
        return pet2MaxHP > 0 ? (pet2HP / pet2MaxHP) * 100 : 0;
    }

    // ===== 관전 관련 =====

    /**
     * 관전자 추가
     */
    public void addSpectator(UUID playerId) {
        if (allowSpectators && ! spectators.contains(playerId)) {
            spectators.add(playerId);
        }
    }

    /**
     * 관전자 제거
     */
    public void removeSpectator(UUID playerId) {
        spectators.remove(playerId);
    }

    /**
     * 관전자인지 확인
     */
    public boolean isSpectator(UUID playerId) {
        return spectators.contains(playerId);
    }

    // ===== 보상 관련 =====

    /**
     * 보상 계산
     */
    public void calculateRewards(int baseExp, int baseRating) {
        switch (type) {
            case FRIENDLY: 
                winnerExp = baseExp / 2;
                loserExp = baseExp / 4;
                ratingChange = 0;
                break;
            case RANKED:
                winnerExp = baseExp;
                loserExp = baseExp / 3;
                ratingChange = baseRating;
                break;
            case TOURNAMENT:
                winnerExp = baseExp * 2;
                loserExp = baseExp / 2;
                ratingChange = baseRating * 2;
                break;
            default:
                winnerExp = baseExp;
                loserExp = baseExp / 4;
                ratingChange = 0;
        }
    }

    // ===== 전투 시간 관련 =====

    /**
     * 전투 지속 시간 (밀리초)
     */
    public long getBattleDuration() {
        if (endTime > 0) {
            return endTime - startTime;
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 전투 지속 시간 포맷팅
     */
    public String getBattleDurationFormatted() {
        long duration = getBattleDuration();
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        return String.format("%d분 %d초", minutes, seconds % 60);
    }

    // ===== 요약 정보 =====

    /**
     * 전투 요약
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 펫 배틀 결과 ===\n");
        sb.append("타입: ").append(type.getDisplayName()).append("\n");
        sb.append("상태: ").append(status.getDisplayName()).append("\n");
        sb.append("\n");
        sb.append(player1Name).append("의 ").append(pet1Name).append("\n");
        sb.append("  HP: ").append(String.format("%. 1f/%.1f", pet1HP, pet1MaxHP)).append("\n");
        sb.append("\n");
        sb.append(player2Name).append("의 ").append(pet2Name).append("\n");
        sb.append("  HP: ").append(String.format("%.1f/%. 1f", pet2HP, pet2MaxHP)).append("\n");
        sb.append("\n");
        sb.append("총 턴:  ").append(currentTurn).append("\n");
        sb.append("소요 시간: ").append(getBattleDurationFormatted()).append("\n");

        if (status == BattleStatus.ENDED) {
            sb.append("\n결과: ");
            if (result == BattleResult.DRAW) {
                sb.append("무승부");
            } else {
                sb.append(winnerId. equals(player1Id) ? player1Name : player2Name).append(" 승리!");
            }
        }

        return sb.toString();
    }

    /**
     * 최근 로그 반환
     */
    public List<String> getRecentLogs(int count) {
        int start = Math.max(0, battleLog.size() - count);
        return new ArrayList<>(battleLog.subList(start, battleLog.size()));
    }

    // ===== Getter/Setter =====

    public UUID getBattleId() {
        return battleId;
    }

    public void setBattleId(UUID battleId) {
        this.battleId = battleId;
    }

    public BattleType getType() {
        return type;
    }

    public void setType(BattleType type) {
        this. type = type;
    }

    public BattleStatus getStatus() {
        return status;
    }

    public void setStatus(BattleStatus status) {
        this.status = status;
    }

    public UUID getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(UUID player1Id) {
        this. player1Id = player1Id;
    }

    public UUID getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(UUID player2Id) {
        this. player2Id = player2Id;
    }

    public UUID getPet1Id() {
        return pet1Id;
    }

    public void setPet1Id(UUID pet1Id) {
        this.pet1Id = pet1Id;
    }

    public UUID getPet2Id() {
        return pet2Id;
    }

    public void setPet2Id(UUID pet2Id) {
        this.pet2Id = pet2Id;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this. player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this. player2Name = player2Name;
    }

    public String getPet1Name() {
        return pet1Name;
    }

    public void setPet1Name(String pet1Name) {
        this. pet1Name = pet1Name;
    }

    public String getPet2Name() {
        return pet2Name;
    }

    public void setPet2Name(String pet2Name) {
        this. pet2Name = pet2Name;
    }

    public double getPet1HP() {
        return pet1HP;
    }

    public void setPet1HP(double pet1HP) {
        this.pet1HP = pet1HP;
    }

    public double getPet1MaxHP() {
        return pet1MaxHP;
    }

    public void setPet1MaxHP(double pet1MaxHP) {
        this.pet1MaxHP = pet1MaxHP;
    }

    public double getPet2HP() {
        return pet2HP;
    }

    public void setPet2HP(double pet2HP) {
        this.pet2HP = pet2HP;
    }

    public double getPet2MaxHP() {
        return pet2MaxHP;
    }

    public void setPet2MaxHP(double pet2MaxHP) {
        this.pet2MaxHP = pet2MaxHP;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }

    public UUID getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public void setCurrentTurnPlayerId(UUID currentTurnPlayerId) {
        this.currentTurnPlayerId = currentTurnPlayerId;
    }

    public long getTurnStartTime() {
        return turnStartTime;
    }

    public void setTurnStartTime(long turnStartTime) {
        this. turnStartTime = turnStartTime;
    }

    public int getTurnTimeLimit() {
        return turnTimeLimit;
    }

    public void setTurnTimeLimit(int turnTimeLimit) {
        this.turnTimeLimit = turnTimeLimit;
    }

    public List<BattleTurn> getTurnHistory() {
        return turnHistory;
    }

    public void setTurnHistory(List<BattleTurn> turnHistory) {
        this.turnHistory = turnHistory;
    }

    public List<String> getBattleLog() {
        return battleLog;
    }

    public void setBattleLog(List<String> battleLog) {
        this. battleLog = battleLog;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this. winnerId = winnerId;
    }

    public UUID getWinnerPetId() {
        return winnerPetId;
    }

    public void setWinnerPetId(UUID winnerPetId) {
        this. winnerPetId = winnerPetId;
    }

    public BattleResult getResult() {
        return result;
    }

    public void setResult(BattleResult result) {
        this.result = result;
    }

    public int getWinnerExp() {
        return winnerExp;
    }

    public void setWinnerExp(int winnerExp) {
        this. winnerExp = winnerExp;
    }

    public int getLoserExp() {
        return loserExp;
    }

    public void setLoserExp(int loserExp) {
        this. loserExp = loserExp;
    }

    public int getRatingChange() {
        return ratingChange;
    }

    public void setRatingChange(int ratingChange) {
        this.ratingChange = ratingChange;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }

    public void setSpectators(List<UUID> spectators) {
        this.spectators = spectators;
    }

    public boolean isAllowSpectators() {
        return allowSpectators;
    }

    public void setAllowSpectators(boolean allowSpectators) {
        this.allowSpectators = allowSpectators;
    }

    public boolean isAIBattle() {
        return isAIBattle;
    }

    public void setAIBattle(boolean AIBattle) {
        isAIBattle = AIBattle;
    }

    public int getAiDifficulty() {
        return aiDifficulty;
    }

    public void setAiDifficulty(int aiDifficulty) {
        this.aiDifficulty = aiDifficulty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetBattle petBattle = (PetBattle) o;
        return Objects.equals(battleId, petBattle. battleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(battleId);
    }

    @Override
    public String toString() {
        return "PetBattle{" +
                "battleId=" + battleId +
                ", type=" + type +
                ", status=" + status +
                ", turn=" + currentTurn +
                '}';
    }
}