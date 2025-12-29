package com. multiverse.pet. model.battle;

import java.util.*;

/**
 * 대결 턴 데이터 클래스
 * 대결 중 각 턴의 행동과 결과를 기록
 */
public class BattleTurn {

    // 기본 정보
    private int turnNumber;
    private UUID actorPlayerId;
    private UUID actorPetId;
    private long timestamp;

    // 행동 정보
    private TurnAction action;
    private String skillId;
    private String skillName;

    // 타겟 정보
    private UUID targetPlayerId;
    private UUID targetPetId;

    // 결과
    private double damageDealt;
    private double healingDone;
    private boolean isCritical;
    private boolean isMiss;
    private boolean isBlocked;

    // 상태 변화
    private double actorHPBefore;
    private double actorHPAfter;
    private double targetHPBefore;
    private double targetHPAfter;

    // 버프/디버프
    private List<StatusEffect> appliedEffects;
    private List<StatusEffect> removedEffects;

    // 추가 정보
    private String description;
    private List<String> logMessages;

    /**
     * 기본 생성자
     */
    public BattleTurn() {
        this.timestamp = System.currentTimeMillis();
        this.appliedEffects = new ArrayList<>();
        this.removedEffects = new ArrayList<>();
        this.logMessages = new ArrayList<>();
        this.damageDealt = 0;
        this.healingDone = 0;
        this.isCritical = false;
        this.isMiss = false;
        this.isBlocked = false;
    }

    /**
     * 전체 생성자
     */
    public BattleTurn(int turnNumber, UUID actorPlayerId, UUID actorPetId, TurnAction action) {
        this();
        this.turnNumber = turnNumber;
        this.actorPlayerId = actorPlayerId;
        this.actorPetId = actorPetId;
        this.action = action;
    }

    // ===== 행동 처리 메서드 =====

    /**
     * 공격 행동 설정
     */
    public void setAttackAction(UUID targetPetId, double damage, boolean critical, boolean miss) {
        this.action = TurnAction.ATTACK;
        this. targetPetId = targetPetId;
        this.damageDealt = damage;
        this. isCritical = critical;
        this.isMiss = miss;

        if (miss) {
            addLog("공격이 빗나갔습니다!");
        } else if (critical) {
            addLog("치명타!  " + String.format("%.1f", damage) + " 데미지!");
        } else {
            addLog(String.format("%. 1f", damage) + " 데미지를 입혔습니다.");
        }
    }

    /**
     * 스킬 사용 행동 설정
     */
    public void setSkillAction(String skillId, String skillName, UUID targetPetId) {
        this. action = TurnAction.SKILL;
        this.skillId = skillId;
        this.skillName = skillName;
        this.targetPetId = targetPetId;
        addLog("스킬 [" + skillName + "] 사용!");
    }

    /**
     * 방어 행동 설정
     */
    public void setDefendAction(double reducedDamage) {
        this. action = TurnAction.DEFEND;
        addLog("방어 태세!  받는 데미지가 " + String.format("%.1f%%", reducedDamage) + " 감소합니다.");
    }

    /**
     * 힐 행동 설정
     */
    public void setHealAction(UUID targetPetId, double healing) {
        this.action = TurnAction.HEAL;
        this.targetPetId = targetPetId;
        this.healingDone = healing;
        addLog(String.format("%. 1f", healing) + " HP를 회복했습니다.");
    }

    /**
     * 아이템 사용 행동 설정
     */
    public void setItemAction(String itemName, String effect) {
        this.action = TurnAction.ITEM;
        addLog("아이템 [" + itemName + "] 사용!  " + effect);
    }

    /**
     * 교체 행동 설정
     */
    public void setSwitchAction(UUID newPetId, String newPetName) {
        this.action = TurnAction. SWITCH;
        addLog("펫을 [" + newPetName + "](으)로 교체했습니다.");
    }

    /**
     * 도주 행동 설정
     */
    public void setFleeAction(boolean success) {
        this.action = TurnAction.FLEE;
        if (success) {
            addLog("도주에 성공했습니다!");
        } else {
            addLog("도주에 실패했습니다!");
        }
    }

    /**
     * 패스 행동 설정
     */
    public void setPassAction() {
        this. action = TurnAction.PASS;
        addLog("턴을 넘겼습니다.");
    }

    /**
     * 타임아웃 설정
     */
    public void setTimeoutAction() {
        this. action = TurnAction.TIMEOUT;
        addLog("시간 초과!  턴이 자동으로 넘어갑니다.");
    }

    // ===== 상태 효과 관련 =====

    /**
     * 적용된 효과 추가
     */
    public void addAppliedEffect(StatusEffect effect) {
        appliedEffects.add(effect);
        addLog("효과 [" + effect.getName() + "] 적용!");
    }

    /**
     * 제거된 효과 추가
     */
    public void addRemovedEffect(StatusEffect effect) {
        removedEffects.add(effect);
        addLog("효과 [" + effect.getName() + "] 종료.");
    }

    // ===== 로그 관련 =====

    /**
     * 로그 추가
     */
    public void addLog(String message) {
        logMessages. add(message);
    }

    /**
     * 전체 로그 문자열 반환
     */
    public String getFullLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("[턴 ").append(turnNumber).append("] ");
        for (String log : logMessages) {
            sb.append(log).append(" ");
        }
        return sb.toString().trim();
    }

    // ===== HP 변화 기록 =====

    /**
     * 행동자 HP 변화 기록
     */
    public void recordActorHPChange(double before, double after) {
        this.actorHPBefore = before;
        this.actorHPAfter = after;
    }

    /**
     * 타겟 HP 변화 기록
     */
    public void recordTargetHPChange(double before, double after) {
        this. targetHPBefore = before;
        this.targetHPAfter = after;
    }

    /**
     * 행동자 HP 변화량
     */
    public double getActorHPChange() {
        return actorHPAfter - actorHPBefore;
    }

    /**
     * 타겟 HP 변화량
     */
    public double getTargetHPChange() {
        return targetHPAfter - targetHPBefore;
    }

    // ===== 결과 확인 =====

    /**
     * 공격 행동인지 확인
     */
    public boolean isAttackAction() {
        return action == TurnAction.ATTACK;
    }

    /**
     * 스킬 행동인지 확인
     */
    public boolean isSkillAction() {
        return action == TurnAction.SKILL;
    }

    /**
     * 방어 행동인지 확인
     */
    public boolean isDefendAction() {
        return action == TurnAction.DEFEND;
    }

    /**
     * 힐 행동인지 확인
     */
    public boolean isHealAction() {
        return action == TurnAction.HEAL;
    }

    /**
     * 데미지를 입혔는지 확인
     */
    public boolean dealtDamage() {
        return damageDealt > 0 && ! isMiss;
    }

    /**
     * 회복을 했는지 확인
     */
    public boolean didHealing() {
        return healingDone > 0;
    }

    /**
     * 타겟이 기절했는지 확인
     */
    public boolean targetKnockedOut() {
        return targetHPAfter <= 0 && targetHPBefore > 0;
    }

    // ===== 턴 요약 =====

    /**
     * 턴 요약 문자열 반환
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("턴 ").append(turnNumber).append(": ");
        sb.append(action.getDisplayName());

        if (skillName != null && !skillName.isEmpty()) {
            sb.append(" [").append(skillName).append("]");
        }

        if (damageDealt > 0) {
            sb.append(" - 데미지:  ").append(String.format("%.1f", damageDealt));
            if (isCritical) sb.append(" (치명타)");
        }

        if (healingDone > 0) {
            sb. append(" - 회복: ").append(String.format("%.1f", healingDone));
        }

        if (isMiss) {
            sb.append(" (빗나감)");
        }

        if (isBlocked) {
            sb.append(" (막힘)");
        }

        return sb. toString();
    }

    // ===== Getter/Setter =====

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public UUID getActorPlayerId() {
        return actorPlayerId;
    }

    public void setActorPlayerId(UUID actorPlayerId) {
        this.actorPlayerId = actorPlayerId;
    }

    public UUID getActorPetId() {
        return actorPetId;
    }

    public void setActorPetId(UUID actorPetId) {
        this.actorPetId = actorPetId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public TurnAction getAction() {
        return action;
    }

    public void setAction(TurnAction action) {
        this.action = action;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this. skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public UUID getTargetPlayerId() {
        return targetPlayerId;
    }

    public void setTargetPlayerId(UUID targetPlayerId) {
        this.targetPlayerId = targetPlayerId;
    }

    public UUID getTargetPetId() {
        return targetPetId;
    }

    public void setTargetPetId(UUID targetPetId) {
        this.targetPetId = targetPetId;
    }

    public double getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(double damageDealt) {
        this.damageDealt = damageDealt;
    }

    public double getHealingDone() {
        return healingDone;
    }

    public void setHealingDone(double healingDone) {
        this.healingDone = healingDone;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public void setCritical(boolean critical) {
        isCritical = critical;
    }

    public boolean isMiss() {
        return isMiss;
    }

    public void setMiss(boolean miss) {
        isMiss = miss;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public double getActorHPBefore() {
        return actorHPBefore;
    }

    public void setActorHPBefore(double actorHPBefore) {
        this.actorHPBefore = actorHPBefore;
    }

    public double getActorHPAfter() {
        return actorHPAfter;
    }

    public void setActorHPAfter(double actorHPAfter) {
        this. actorHPAfter = actorHPAfter;
    }

    public double getTargetHPBefore() {
        return targetHPBefore;
    }

    public void setTargetHPBefore(double targetHPBefore) {
        this.targetHPBefore = targetHPBefore;
    }

    public double getTargetHPAfter() {
        return targetHPAfter;
    }

    public void setTargetHPAfter(double targetHPAfter) {
        this. targetHPAfter = targetHPAfter;
    }

    public List<StatusEffect> getAppliedEffects() {
        return appliedEffects;
    }

    public void setAppliedEffects(List<StatusEffect> appliedEffects) {
        this.appliedEffects = appliedEffects;
    }

    public List<StatusEffect> getRemovedEffects() {
        return removedEffects;
    }

    public void setRemovedEffects(List<StatusEffect> removedEffects) {
        this.removedEffects = removedEffects;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLogMessages() {
        return logMessages;
    }

    public void setLogMessages(List<String> logMessages) {
        this.logMessages = logMessages;
    }

    @Override
    public String toString() {
        return "BattleTurn{" +
                "turn=" + turnNumber +
                ", action=" + action +
                ", damage=" + damageDealt +
                ", healing=" + healingDone +
                '}';
    }

    // ===== 내부 열거형 =====

    /**
     * 턴 행동 유형
     */
    public enum TurnAction {
        ATTACK("공격", "&c"),
        SKILL("스킬", "&5"),
        DEFEND("방어", "&9"),
        HEAL("회복", "&d"),
        ITEM("아이템", "&e"),
        SWITCH("교체", "&b"),
        FLEE("도주", "&7"),
        PASS("패스", "&8"),
        TIMEOUT("시간초과", "&4");

        private final String displayName;
        private final String colorCode;

        TurnAction(String displayName, String colorCode) {
            this.displayName = displayName;
            this.colorCode = colorCode;
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
    }

    /**
     * 상태 효과
     */
    public static class StatusEffect {
        private String effectId;
        private String name;
        private String type;
        private double value;
        private int duration;
        private int remainingTurns;

        public StatusEffect() {}

        public StatusEffect(String effectId, String name, String type, double value, int duration) {
            this.effectId = effectId;
            this.name = name;
            this. type = type;
            this.value = value;
            this.duration = duration;
            this.remainingTurns = duration;
        }

        public String getEffectId() {
            return effectId;
        }

        public void setEffectId(String effectId) {
            this.effectId = effectId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this. value = value;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getRemainingTurns() {
            return remainingTurns;
        }

        public void setRemainingTurns(int remainingTurns) {
            this.remainingTurns = remainingTurns;
        }

        public void decrementTurn() {
            if (remainingTurns > 0) {
                remainingTurns--;
            }
        }

        public boolean isExpired() {
            return remainingTurns <= 0;
        }
    }
}