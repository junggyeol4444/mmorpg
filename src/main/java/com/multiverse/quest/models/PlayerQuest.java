package com.multiverse.quest.models;

import com.multiverse.quest.models. enums.QuestStatus;
import java.util.*;

/**
 * 플레이어 퀘스트 진행 데이터 모델
 * 플레이어의 개별 퀘스트 진행 상태를 관리합니다.
 */
public class PlayerQuest {
    private UUID playerUUID;                          // 플레이어 UUID
    private String questId;                           // 퀘스트 ID
    private QuestStatus status;                       // 현재 상태
    
    private Map<String, QuestObjective> objectives;   // 목표 진행도 (objectiveId -> objective)
    private int completedObjectiveCount;              // 완료된 목표 개수
    
    private long acceptedTime;                        // 수락 시간
    private long completedTime;                       // 완료 시간 (보상 수령 전)
    private long finishedTime;                        // 완전 종료 시간 (보상 수령 후)
    private long expiryTime;                          // 만료 시간 (0이면 무제한)
    
    private int completionCount;                      // 완료 횟수 (반복 퀘스트)
    private long lastCompletedTime;                   // 마지막 완료 시간
    private long nextAvailableTime;                   // 다음 가능 시간 (쿨다운)
    
    private String failReason;                        // 실패 이유
    private Map<String, Object> metadata;             // 추가 데이터

    /**
     * 기본 생성자
     */
    public PlayerQuest() {
        this.objectives = new LinkedHashMap<>();
        this.metadata = new HashMap<>();
        this. status = QuestStatus.NOT_STARTED;
        this. completionCount = 0;
        this.completedObjectiveCount = 0;
        this.expiryTime = 0;
        this.nextAvailableTime = 0;
    }

    /**
     * 전체 파라미터 생성자
     */
    public PlayerQuest(UUID playerUUID, String questId) {
        this();
        this.playerUUID = playerUUID;
        this. questId = questId;
    }

    // ============ Getters ============

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getQuestId() {
        return questId;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public Map<String, QuestObjective> getObjectives() {
        return new LinkedHashMap<>(objectives);
    }

    public QuestObjective getObjective(String objectiveId) {
        return objectives.get(objectiveId);
    }

    public int getCompletedObjectiveCount() {
        return completedObjectiveCount;
    }

    public int getTotalObjectiveCount() {
        return objectives.size();
    }

    public long getAcceptedTime() {
        return acceptedTime;
    }

    public long getCompletedTime() {
        return completedTime;
    }

    public long getFinishedTime() {
        return finishedTime;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public int getCompletionCount() {
        return completionCount;
    }

    public long getLastCompletedTime() {
        return lastCompletedTime;
    }

    public long getNextAvailableTime() {
        return nextAvailableTime;
    }

    public String getFailReason() {
        return failReason;
    }

    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }

    // ============ Setters ============

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void setQuestId(String questId) {
        this.questId = questId;
    }

    public void setStatus(QuestStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = Math.max(expiryTime, 0);
    }

    public void setNextAvailableTime(long nextAvailableTime) {
        this.nextAvailableTime = Math.max(nextAvailableTime, 0);
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    // ============ Objective Management ============

    /**
     * 목표 추가
     */
    public void addObjective(QuestObjective objective) {
        if (objective != null) {
            objectives. put(objective.getObjectiveId(), objective);
        }
    }

    /**
     * 목표 업데이트 (진행도 증가)
     */
    public void updateObjective(String objectiveId, int amount) {
        QuestObjective objective = objectives.get(objectiveId);
        if (objective != null) {
            int oldProgress = objective.getCurrent();
            objective.updateProgress(amount);
            
            // 완료 상태 변경 확인
            if (! objective.isCompleted() && oldProgress < objective.getRequired() && 
                objective.getCurrent() >= objective.getRequired()) {
                objective.setCompleted(true);
                completedObjectiveCount++;
            }
        }
    }

    /**
     * 목표 진행도 직접 설정
     */
    public void setObjectiveProgress(String objectiveId, int value) {
        QuestObjective objective = objectives.get(objectiveId);
        if (objective != null) {
            objective.setProgress(value);
            
            if (objective.isCompleted()) {
                if (! wasObjectiveCompleted(objectiveId)) {
                    completedObjectiveCount++;
                }
            }
        }
    }

    /**
     * 특정 목표가 이미 완료되었는지 확인
     */
    private boolean wasObjectiveCompleted(String objectiveId) {
        QuestObjective objective = objectives.get(objectiveId);
        return objective != null && objective.isCompleted();
    }

    /**
     * 모든 목표 초기화
     */
    public void resetObjectives() {
        objectives.forEach((id, objective) -> objective.resetProgress());
        completedObjectiveCount = 0;
    }

    /**
     * 목표 개수 반환
     */
    public int getObjectiveCount() {
        return objectives.size();
    }

    // ============ Status Management ============

    /**
     * 상태 전환
     */
    public boolean transitionTo(QuestStatus nextStatus) {
        if (status. canTransitionTo(nextStatus)) {
            setStatus(nextStatus);
            
            // 상태별 시간 기록
            switch (nextStatus) {
                case COMPLETED:
                    this.completedTime = System.currentTimeMillis();
                    break;
                case FINISHED:
                    this.finishedTime = System.currentTimeMillis();
                    break;
            }
            
            return true;
        }
        return false;
    }

    /**
     * 진행 중 상태로 전환
     */
    public void startQuest() {
        if (status == QuestStatus.NOT_STARTED || status == QuestStatus.AVAILABLE) {
            transitionTo(QuestStatus.IN_PROGRESS);
            acceptedTime = System.currentTimeMillis();
        }
    }

    /**
     * 완료 상태로 전환
     */
    public void completeQuest() {
        if (status == QuestStatus.IN_PROGRESS) {
            transitionTo(QuestStatus. COMPLETED);
        }
    }

    /**
     * 완전 종료 상태로 전환 (보상 수령 완료)
     */
    public void finishQuest() {
        if (status == QuestStatus. COMPLETED) {
            transitionTo(QuestStatus. FINISHED);
            completionCount++;
            lastCompletedTime = System.currentTimeMillis();
        }
    }

    /**
     * 실패 상태로 전환
     */
    public void failQuest(String reason) {
        if (status != QuestStatus.FINISHED) {
            setFailReason(reason);
            transitionTo(QuestStatus. FAILED);
        }
    }

    /**
     * 만료 상태로 전환
     */
    public void expireQuest() {
        if (status != QuestStatus.FINISHED) {
            setFailReason("시간 제한 초과");
            transitionTo(QuestStatus.EXPIRED);
        }
    }

    // ============ Business Logic ============

    /**
     * 모든 목표가 완료되었는지 확인
     */
    public boolean allObjectivesCompleted() {
        return completedObjectiveCount == objectives.size() && objectives.size() > 0;
    }

    /**
     * 진행 진행률 반환 (0~100)
     */
    public int getProgressPercentage() {
        if (objectives.isEmpty()) return 0;
        return (completedObjectiveCount * 100) / objectives.size();
    }

    /**
     * 진행도 문자열 표현 (예: "3/5")
     */
    public String getProgressString() {
        return String.format("%d/%d", completedObjectiveCount, objectives.size());
    }

    /**
     * 진행 표시줄 생성
     */
    public String getProgressBar(int barLength) {
        int percentage = getProgressPercentage();
        int filledLength = (int) ((double) percentage / 100 * barLength);
        
        StringBuilder bar = new StringBuilder(status.getColorCode());
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        
        bar.append("§f ").append(percentage). append("%");
        return bar.toString();
    }

    /**
     * 시간 제한 남은 시간 반환 (밀리초)
     */
    public long getTimeRemaining() {
        if (expiryTime == 0) return -1; // 무제한
        
        long currentTime = System.currentTimeMillis();
        long remaining = expiryTime - currentTime;
        
        return Math.max(remaining, 0);
    }

    /**
     * 시간 제한 만료되었는지 확인
     */
    public boolean isExpired() {
        if (expiryTime == 0) return false; // 무제한
        return System.currentTimeMillis() >= expiryTime;
    }

    /**
     * 시간 제한 남은 시간을 읽기 쉬운 형식으로 반환
     */
    public String getTimeRemainingFormatted() {
        long remaining = getTimeRemaining();
        
        if (remaining < 0) {
            return "무제한";
        }
        
        long seconds = remaining / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%d일 %d시간", days, hours % 24);
        } else if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String.format("%d초", seconds);
        }
    }

    /**
     * 쿨다운이 지났는지 확인
     */
    public boolean isCooldownExpired() {
        return System.currentTimeMillis() >= nextAvailableTime;
    }

    /**
     * 쿨다운 남은 시간 반환 (밀리초)
     */
    public long getCooldownRemaining() {
        long remaining = nextAvailableTime - System.currentTimeMillis();
        return Math.max(remaining, 0);
    }

    /**
     * 진행 시간 반환 (현재 - 수락 시간)
     */
    public long getElapsedTime() {
        if (acceptedTime == 0) return 0;
        return System.currentTimeMillis() - acceptedTime;
    }

    /**
     * 상세 정보 반환
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 퀘스트 진행 정보 ===§r\n");
        sb.append("§7퀘스트 ID: §f").append(questId).append("\n");
        sb.append("§7상태: ").append(status. getFormattedName()).append("\n");
        sb.append("§7진행도: §f").append(getProgressString()). append("\n");
        
        sb.append("\n§e=== 목표 진행도 ===§r\n");
        objectives.forEach((id, obj) -> {
            String completed = obj.isCompleted() ?  "§a✓" : "§c✗";
            sb.append(completed).append(" §f"). append(obj.getDescription())
                    .append(" (").append(obj.getProgressString()).append(")\n");
        });
        
        if (expiryTime > 0) {
            sb.append("\n§7시간 제한: §f"). append(getTimeRemainingFormatted()). append("\n");
        }
        
        if (completionCount > 0) {
            sb.append("§7완료 횟수: §f").append(completionCount).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * 복사본 생성
     */
    public PlayerQuest copy() {
        PlayerQuest copy = new PlayerQuest();
        copy.playerUUID = this.playerUUID;
        copy.questId = this.questId;
        copy.status = this.status;
        copy.objectives = new LinkedHashMap<>();
        this.objectives.forEach((id, obj) -> copy.objectives.put(id, obj.copy()));
        copy.completedObjectiveCount = this.completedObjectiveCount;
        copy. acceptedTime = this.acceptedTime;
        copy.completedTime = this.completedTime;
        copy.finishedTime = this.finishedTime;
        copy.expiryTime = this.expiryTime;
        copy.completionCount = this.completionCount;
        copy.lastCompletedTime = this.lastCompletedTime;
        copy.nextAvailableTime = this.nextAvailableTime;
        copy.failReason = this.failReason;
        copy.metadata = new HashMap<>(this.metadata);
        return copy;
    }

    /**
     * 문자열 표현
     */
    @Override
    public String toString() {
        return String.format("PlayerQuest{questId=%s, status=%s, progress=%s}",
                questId, status, getProgressString());
    }

    /**
     * 비교 메서드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        PlayerQuest that = (PlayerQuest) o;
        return playerUUID.equals(that.playerUUID) && questId.equals(that. questId);
    }

    /**
     * 해시 코드
     */
    @Override
    public int hashCode() {
        return Objects.hash(playerUUID, questId);
    }
}