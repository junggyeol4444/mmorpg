package com.multiverse.pet.model. battle;

import java.util.*;

/**
 * 대결 결과 데이터 클래스
 * 펫 대결의 최종 결과와 보상을 정의
 */
public class BattleResult {

    // 결과 유형
    public enum ResultType {
        WIN("승리", "&a", true),
        LOSE("패배", "&c", false),
        DRAW("무승부", "&e", false),
        FORFEIT("기권", "&7", false),
        DISCONNECT("연결 끊김", "&8", false),
        ERROR("오류", "&4", false);

        private final String displayName;
        private final String colorCode;
        private final boolean victory;

        ResultType(String displayName, String colorCode, boolean victory) {
            this.displayName = displayName;
            this.colorCode = colorCode;
            this.victory = victory;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getColorCode() {
            return colorCode;
        }

        public String getColoredName() {
            return colorCode + displayName;
        }

        public boolean isVictory() {
            return victory;
        }
    }

    // 기본 정보
    private UUID battleId;
    private ResultType resultType;
    private long timestamp;

    // 참가자 정보
    private UUID winnerId;
    private UUID loserId;
    private UUID winnerPetId;
    private UUID loserPetId;
    private String winnerName;
    private String loserName;
    private String winnerPetName;
    private String loserPetName;

    // 전투 통계
    private int totalTurns;
    private long battleDuration;        // 밀리초
    private double winnerFinalHP;
    private double winnerMaxHP;
    private double loserFinalHP;
    private double loserMaxHP;

    // 데미지 통계
    private double winnerTotalDamage;
    private double loserTotalDamage;
    private double winnerTotalHealing;
    private double loserTotalHealing;
    private int winnerCriticalHits;
    private int loserCriticalHits;
    private int winnerMisses;
    private int loserMisses;

    // 스킬 사용 통계
    private int winnerSkillsUsed;
    private int loserSkillsUsed;
    private String winnerMostUsedSkill;
    private String loserMostUsedSkill;

    // 보상 정보
    private int winnerExpGain;
    private int loserExpGain;
    private int ratingChange;
    private double goldReward;
    private List<String> itemRewards;

    // MVP 정보
    private String mvpAction;           // 가장 결정적인 행동
    private int mvpTurn;                // MVP 행동이 일어난 턴

    // 레이팅 정보 (랭킹전)
    private int winnerOldRating;
    private int winnerNewRating;
    private int loserOldRating;
    private int loserNewRating;

    // 추가 정보
    private BattleType battleType;
    private boolean isPerfectWin;       // 무피해 승리
    private boolean isCloseMatch;       // 접전 (HP 차이 10% 이하)
    private boolean isComeback;         // 역전승

    /**
     * 기본 생성자
     */
    public BattleResult() {
        this.timestamp = System.currentTimeMillis();
        this.itemRewards = new ArrayList<>();
        this.resultType = ResultType.DRAW;
    }

    /**
     * 전체 생성자
     */
    public BattleResult(UUID battleId, ResultType resultType, UUID winnerId, UUID loserId) {
        this();
        this.battleId = battleId;
        this.resultType = resultType;
        this.winnerId = winnerId;
        this.loserId = loserId;
    }

    /**
     * PetBattle에서 결과 생성
     */
    public static BattleResult fromBattle(PetBattle battle) {
        BattleResult result = new BattleResult();
        result.battleId = battle.getBattleId();
        result.battleType = battle.getType();
        result.totalTurns = battle.getCurrentTurn();
        result.battleDuration = battle. getBattleDuration();

        // 승자/패자 설정
        if (battle.getWinnerId() != null) {
            if (battle.getWinnerId().equals(battle.getPlayer1Id())) {
                result.resultType = ResultType. WIN;
                result.winnerId = battle.getPlayer1Id();
                result. loserId = battle.getPlayer2Id();
                result.winnerPetId = battle.getPet1Id();
                result.loserPetId = battle.getPet2Id();
                result.winnerName = battle.getPlayer1Name();
                result.loserName = battle.getPlayer2Name();
                result.winnerPetName = battle.getPet1Name();
                result.loserPetName = battle.getPet2Name();
                result.winnerFinalHP = battle. getPet1HP();
                result. winnerMaxHP = battle.getPet1MaxHP();
                result.loserFinalHP = battle.getPet2HP();
                result.loserMaxHP = battle.getPet2MaxHP();
            } else {
                result.resultType = ResultType.WIN;
                result. winnerId = battle. getPlayer2Id();
                result.loserId = battle.getPlayer1Id();
                result.winnerPetId = battle.getPet2Id();
                result. loserPetId = battle.getPet1Id();
                result.winnerName = battle.getPlayer2Name();
                result.loserName = battle.getPlayer1Name();
                result.winnerPetName = battle. getPet2Name();
                result.loserPetName = battle.getPet1Name();
                result.winnerFinalHP = battle.getPet2HP();
                result.winnerMaxHP = battle. getPet2MaxHP();
                result. loserFinalHP = battle.getPet1HP();
                result.loserMaxHP = battle.getPet1MaxHP();
            }
        } else {
            result.resultType = ResultType.DRAW;
        }

        // 특수 승리 조건 체크
        result.checkSpecialConditions();

        // 턴 히스토리에서 통계 계산
        result. calculateStatsFromHistory(battle. getTurnHistory());

        // 보상 설정
        result. winnerExpGain = battle.getWinnerExp();
        result.loserExpGain = battle.getLoserExp();
        result.ratingChange = battle.getRatingChange();

        return result;
    }

    // ===== 통계 계산 =====

    /**
     * 턴 히스토리에서 통계 계산
     */
    private void calculateStatsFromHistory(List<BattleTurn> history) {
        Map<String, Integer> winnerSkillCount = new HashMap<>();
        Map<String, Integer> loserSkillCount = new HashMap<>();

        for (BattleTurn turn :  history) {
            boolean isWinnerTurn = turn.getActorPlayerId() != null && 
                                   turn.getActorPlayerId().equals(winnerId);

            if (isWinnerTurn) {
                winnerTotalDamage += turn. getDamageDealt();
                winnerTotalHealing += turn.getHealingDone();
                if (turn.isCritical()) winnerCriticalHits++;
                if (turn.isMiss()) winnerMisses++;
                if (turn.isSkillAction()) {
                    winnerSkillsUsed++;
                    String skillName = turn.getSkillName();
                    if (skillName != null) {
                        winnerSkillCount.merge(skillName, 1, Integer::sum);
                    }
                }
            } else {
                loserTotalDamage += turn. getDamageDealt();
                loserTotalHealing += turn.getHealingDone();
                if (turn. isCritical()) loserCriticalHits++;
                if (turn.isMiss()) loserMisses++;
                if (turn. isSkillAction()) {
                    loserSkillsUsed++;
                    String skillName = turn.getSkillName();
                    if (skillName != null) {
                        loserSkillCount.merge(skillName, 1, Integer::sum);
                    }
                }
            }
        }

        // 가장 많이 사용한 스킬
        winnerMostUsedSkill = winnerSkillCount.entrySet().stream()
                .max(Map. Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        loserMostUsedSkill = loserSkillCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * 특수 조건 체크
     */
    private void checkSpecialConditions() {
        if (resultType != ResultType.WIN) return;

        // 무피해 승리 (Perfect Win)
        isPerfectWin = winnerFinalHP >= winnerMaxHP;

        // 접전 (Close Match) - HP 차이 10% 이하
        double hpDifference = (winnerFinalHP / winnerMaxHP) - (loserFinalHP / loserMaxHP);
        isCloseMatch = Math.abs(hpDifference) <= 0.1;

        // 역전승 - 승자의 HP가 30% 이하였던 적이 있으면 역전승으로 간주
        // (실제 구현 시 턴 히스토리에서 확인 필요)
        isComeback = winnerFinalHP <= winnerMaxHP * 0.3;
    }

    // ===== 결과 분석 =====

    /**
     * 승자의 HP 퍼센트
     */
    public double getWinnerHPPercentage() {
        return winnerMaxHP > 0 ? (winnerFinalHP / winnerMaxHP) * 100 : 0;
    }

    /**
     * 패자의 HP 퍼센트
     */
    public double getLoserHPPercentage() {
        return loserMaxHP > 0 ? (loserFinalHP / loserMaxHP) * 100 :  0;
    }

    /**
     * 승자의 치명타 비율
     */
    public double getWinnerCriticalRate() {
        int totalAttacks = winnerSkillsUsed + (int) (winnerTotalDamage > 0 ? 1 : 0);
        return totalAttacks > 0 ? ((double) winnerCriticalHits / totalAttacks) * 100 : 0;
    }

    /**
     * 패자의 치명타 비율
     */
    public double getLoserCriticalRate() {
        int totalAttacks = loserSkillsUsed + (int) (loserTotalDamage > 0 ? 1 : 0);
        return totalAttacks > 0 ? ((double) loserCriticalHits / totalAttacks) * 100 : 0;
    }

    /**
     * 전투 시간 포맷팅
     */
    public String getBattleDurationFormatted() {
        long seconds = battleDuration / 1000;
        long minutes = seconds / 60;
        return String.format("%d분 %d초", minutes, seconds % 60);
    }

    /**
     * 승리 마진 계산 (HP 차이 기반)
     */
    public double getVictoryMargin() {
        return getWinnerHPPercentage() - getLoserHPPercentage();
    }

    // ===== 결과 요약 =====

    /**
     * 결과 요약 문자열
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6===== 대결 결과 =====\n");
        sb.append("§7결과:  ").append(resultType.getColoredName()).append("\n\n");

        if (resultType == ResultType. WIN) {
            sb.append("§a승자: ").append(winnerName).append("의 ").append(winnerPetName).append("\n");
            sb.append("§7  남은 HP: §a").append(String.format("%.1f%%", getWinnerHPPercentage())).append("\n");
            sb.append("§7  총 데미지: §c").append(String.format("%.1f", winnerTotalDamage)).append("\n");
            sb.append("\n");
            sb.append("§c패자: ").append(loserName).append("의 ").append(loserPetName).append("\n");
            sb.append("§7  총 데미지:  §c").append(String.format("%. 1f", loserTotalDamage)).append("\n");
        } else if (resultType == ResultType.DRAW) {
            sb. append("§e무승부로 대결이 종료되었습니다.\n");
        }

        sb.append("\n§7총 턴: §f").append(totalTurns).append("\n");
        sb.append("§7소요 시간:  §f").append(getBattleDurationFormatted()).append("\n");

        // 특수 조건 표시
        if (isPerfectWin) {
            sb.append("\n§6★ 완벽한 승리!  (무피해)\n");
        }
        if (isCloseMatch) {
            sb.append("\n§e★ 접전!\n");
        }
        if (isComeback) {
            sb.append("\n§d★ 역전승!\n");
        }

        // 보상 표시
        if (winnerExpGain > 0 || ratingChange > 0) {
            sb.append("\n§6===== 보상 =====\n");
            if (winnerExpGain > 0) {
                sb. append("§7승자 경험치: §a+").append(winnerExpGain).append("\n");
            }
            if (loserExpGain > 0) {
                sb.append("§7패자 경험치: §a+").append(loserExpGain).append("\n");
            }
            if (ratingChange > 0) {
                sb.append("§7레이팅 변동: §e±").append(ratingChange).append("\n");
            }
            if (goldReward > 0) {
                sb.append("§7골드:  §6+").append(String.format("%.0f", goldReward)).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * 간단 요약 (한 줄)
     */
    public String getShortSummary() {
        if (resultType == ResultType.WIN) {
            return winnerName + "의 " + winnerPetName + " 승리!  " +
                   "(HP " + String.format("%.1f%%", getWinnerHPPercentage()) + " 남음, " +
                   totalTurns + "턴)";
        } else if (resultType == ResultType.DRAW) {
            return "무승부 (" + totalTurns + "턴)";
        } else {
            return resultType.getDisplayName() + "으로 종료";
        }
    }

    /**
     * 통계 요약
     */
    public String getStatsSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6===== 전투 통계 =====\n\n");

        sb.append("§a").append(winnerName).append(":\n");
        sb.append("§7  총 데미지: §c").append(String.format("%.1f", winnerTotalDamage)).append("\n");
        sb.append("§7  총 회복: §d").append(String.format("%.1f", winnerTotalHealing)).append("\n");
        sb.append("§7  치명타:  §e").append(winnerCriticalHits).append("회\n");
        sb.append("§7  빗나감: §8").append(winnerMisses).append("회\n");
        sb.append("§7  스킬 사용: §b").append(winnerSkillsUsed).append("회\n");
        if (winnerMostUsedSkill != null) {
            sb.append("§7  주력 스킬:  §5").append(winnerMostUsedSkill).append("\n");
        }

        sb.append("\n§c").append(loserName).append(":\n");
        sb.append("§7  총 데미지: §c").append(String.format("%.1f", loserTotalDamage)).append("\n");
        sb.append("§7  총 회복: §d").append(String.format("%.1f", loserTotalHealing)).append("\n");
        sb.append("§7  치명타: §e").append(loserCriticalHits).append("회\n");
        sb.append("§7  빗나감: §8").append(loserMisses).append("회\n");
        sb.append("§7  스킬 사용: §b").append(loserSkillsUsed).append("회\n");
        if (loserMostUsedSkill != null) {
            sb.append("§7  주력 스킬:  §5").append(loserMostUsedSkill).append("\n");
        }

        return sb.toString();
    }

    // ===== 아이템 보상 =====

    /**
     * 아이템 보상 추가
     */
    public void addItemReward(String itemId) {
        itemRewards.add(itemId);
    }

    /**
     * 아이템 보상 목록 반환
     */
    public List<String> getItemRewards() {
        return Collections.unmodifiableList(itemRewards);
    }

    // ===== Getter/Setter =====

    public UUID getBattleId() {
        return battleId;
    }

    public void setBattleId(UUID battleId) {
        this.battleId = battleId;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this. resultType = resultType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this.winnerId = winnerId;
    }

    public UUID getLoserId() {
        return loserId;
    }

    public void setLoserId(UUID loserId) {
        this.loserId = loserId;
    }

    public UUID getWinnerPetId() {
        return winnerPetId;
    }

    public void setWinnerPetId(UUID winnerPetId) {
        this. winnerPetId = winnerPetId;
    }

    public UUID getLoserPetId() {
        return loserPetId;
    }

    public void setLoserPetId(UUID loserPetId) {
        this.loserPetId = loserPetId;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public String getLoserName() {
        return loserName;
    }

    public void setLoserName(String loserName) {
        this.loserName = loserName;
    }

    public String getWinnerPetName() {
        return winnerPetName;
    }

    public void setWinnerPetName(String winnerPetName) {
        this.winnerPetName = winnerPetName;
    }

    public String getLoserPetName() {
        return loserPetName;
    }

    public void setLoserPetName(String loserPetName) {
        this. loserPetName = loserPetName;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public void setTotalTurns(int totalTurns) {
        this.totalTurns = totalTurns;
    }

    public long getBattleDuration() {
        return battleDuration;
    }

    public void setBattleDuration(long battleDuration) {
        this.battleDuration = battleDuration;
    }

    public double getWinnerFinalHP() {
        return winnerFinalHP;
    }

    public void setWinnerFinalHP(double winnerFinalHP) {
        this.winnerFinalHP = winnerFinalHP;
    }

    public double getWinnerMaxHP() {
        return winnerMaxHP;
    }

    public void setWinnerMaxHP(double winnerMaxHP) {
        this.winnerMaxHP = winnerMaxHP;
    }

    public double getLoserFinalHP() {
        return loserFinalHP;
    }

    public void setLoserFinalHP(double loserFinalHP) {
        this.loserFinalHP = loserFinalHP;
    }

    public double getLoserMaxHP() {
        return loserMaxHP;
    }

    public void setLoserMaxHP(double loserMaxHP) {
        this.loserMaxHP = loserMaxHP;
    }

    public double getWinnerTotalDamage() {
        return winnerTotalDamage;
    }

    public void setWinnerTotalDamage(double winnerTotalDamage) {
        this.winnerTotalDamage = winnerTotalDamage;
    }

    public double getLoserTotalDamage() {
        return loserTotalDamage;
    }

    public void setLoserTotalDamage(double loserTotalDamage) {
        this.loserTotalDamage = loserTotalDamage;
    }

    public double getWinnerTotalHealing() {
        return winnerTotalHealing;
    }

    public void setWinnerTotalHealing(double winnerTotalHealing) {
        this.winnerTotalHealing = winnerTotalHealing;
    }

    public double getLoserTotalHealing() {
        return loserTotalHealing;
    }

    public void setLoserTotalHealing(double loserTotalHealing) {
        this.loserTotalHealing = loserTotalHealing;
    }

    public int getWinnerCriticalHits() {
        return winnerCriticalHits;
    }

    public void setWinnerCriticalHits(int winnerCriticalHits) {
        this.winnerCriticalHits = winnerCriticalHits;
    }

    public int getLoserCriticalHits() {
        return loserCriticalHits;
    }

    public void setLoserCriticalHits(int loserCriticalHits) {
        this. loserCriticalHits = loserCriticalHits;
    }

    public int getWinnerMisses() {
        return winnerMisses;
    }

    public void setWinnerMisses(int winnerMisses) {
        this.winnerMisses = winnerMisses;
    }

    public int getLoserMisses() {
        return loserMisses;
    }

    public void setLoserMisses(int loserMisses) {
        this. loserMisses = loserMisses;
    }

    public int getWinnerSkillsUsed() {
        return winnerSkillsUsed;
    }

    public void setWinnerSkillsUsed(int winnerSkillsUsed) {
        this.winnerSkillsUsed = winnerSkillsUsed;
    }

    public int getLoserSkillsUsed() {
        return loserSkillsUsed;
    }

    public void setLoserSkillsUsed(int loserSkillsUsed) {
        this.loserSkillsUsed = loserSkillsUsed;
    }

    public String getWinnerMostUsedSkill() {
        return winnerMostUsedSkill;
    }

    public void setWinnerMostUsedSkill(String winnerMostUsedSkill) {
        this.winnerMostUsedSkill = winnerMostUsedSkill;
    }

    public String getLoserMostUsedSkill() {
        return loserMostUsedSkill;
    }

    public void setLoserMostUsedSkill(String loserMostUsedSkill) {
        this.loserMostUsedSkill = loserMostUsedSkill;
    }

    public int getWinnerExpGain() {
        return winnerExpGain;
    }

    public void setWinnerExpGain(int winnerExpGain) {
        this. winnerExpGain = winnerExpGain;
    }

    public int getLoserExpGain() {
        return loserExpGain;
    }

    public void setLoserExpGain(int loserExpGain) {
        this.loserExpGain = loserExpGain;
    }

    public int getRatingChange() {
        return ratingChange;
    }

    public void setRatingChange(int ratingChange) {
        this. ratingChange = ratingChange;
    }

    public double getGoldReward() {
        return goldReward;
    }

    public void setGoldReward(double goldReward) {
        this. goldReward = goldReward;
    }

    public void setItemRewards(List<String> itemRewards) {
        this. itemRewards = itemRewards;
    }

    public String getMvpAction() {
        return mvpAction;
    }

    public void setMvpAction(String mvpAction) {
        this.mvpAction = mvpAction;
    }

    public int getMvpTurn() {
        return mvpTurn;
    }

    public void setMvpTurn(int mvpTurn) {
        this. mvpTurn = mvpTurn;
    }

    public int getWinnerOldRating() {
        return winnerOldRating;
    }

    public void setWinnerOldRating(int winnerOldRating) {
        this.winnerOldRating = winnerOldRating;
    }

    public int getWinnerNewRating() {
        return winnerNewRating;
    }

    public void setWinnerNewRating(int winnerNewRating) {
        this.winnerNewRating = winnerNewRating;
    }

    public int getLoserOldRating() {
        return loserOldRating;
    }

    public void setLoserOldRating(int loserOldRating) {
        this. loserOldRating = loserOldRating;
    }

    public int getLoserNewRating() {
        return loserNewRating;
    }

    public void setLoserNewRating(int loserNewRating) {
        this.loserNewRating = loserNewRating;
    }

    public BattleType getBattleType() {
        return battleType;
    }

    public void setBattleType(BattleType battleType) {
        this. battleType = battleType;
    }

    public boolean isPerfectWin() {
        return isPerfectWin;
    }

    public void setPerfectWin(boolean perfectWin) {
        isPerfectWin = perfectWin;
    }

    public boolean isCloseMatch() {
        return isCloseMatch;
    }

    public void setCloseMatch(boolean closeMatch) {
        isCloseMatch = closeMatch;
    }

    public boolean isComeback() {
        return isComeback;
    }

    public void setComeback(boolean comeback) {
        isComeback = comeback;
    }

    @Override
    public String toString() {
        return "BattleResult{" +
                "battleId=" + battleId +
                ", result=" + resultType +
                ", winner=" + winnerName +
                ", turns=" + totalTurns +
                '}';
    }

    // ===== 상수 정의 (무승부 반환용) =====

    public static final BattleResult DRAW = createDrawResult();

    private static BattleResult createDrawResult() {
        BattleResult result = new BattleResult();
        result.resultType = ResultType.DRAW;
        return result;
    }
}