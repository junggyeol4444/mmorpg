package com.multiverse.quest.models;

import com.multiverse.quest.models.enums.ObjectiveType;
import java.util.*;

/**
 * 퀘스트 목표 데이터 모델
 * 퀘스트 내의 개별 목표를 정의합니다.
 */
public class QuestObjective {
    private String objectiveId;           // 고유 ID
    private ObjectiveType type;           // 목표 타입
    private String description;           // 설명
    private String target;                // 대상 (몬스터, 아이템, NPC 등)
    private int required;                 // 요구량
    private int current;                  // 현재 진행도
    private String dimension;             // 특정 차원 (null이면 모든 차원)
    private String region;                // 특정 지역 (null이면 모든 지역)
    private int order;                    // 목표 순서 (0이면 동시 진행)
    private boolean completed;            // 완료 여부
    private long completedTime;           // 완료 시간
    private Map<String, Object> metadata; // 추가 데이터

    /**
     * 기본 생성자
     */
    public QuestObjective() {
        this.metadata = new HashMap<>();
        this.current = 0;
        this.completed = false;
        this.completedTime = -1;
        this.order = 0;
    }

    /**
     * 전체 파라미터 생성자
     */
    public QuestObjective(String objectiveId, ObjectiveType type, String description,
                         String target, int required) {
        this();
        this.objectiveId = objectiveId;
        this. type = type;
        this. description = description;
        this. target = target;
        this. required = required;
    }

    // ============ Getters ============

    public String getObjectiveId() {
        return objectiveId;
    }

    public ObjectiveType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getTarget() {
        return target;
    }

    public int getRequired() {
        return required;
    }

    public int getCurrent() {
        return current;
    }

    public String getDimension() {
        return dimension;
    }

    public String getRegion() {
        return region;
    }

    public int getOrder() {
        return order;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getCompletedTime() {
        return completedTime;
    }

    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }

    // ============ Setters ============

    public void setObjectiveId(String objectiveId) {
        this.objectiveId = objectiveId;
    }

    public void setType(ObjectiveType type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setRequired(int required) {
        this.required = Math.max(required, 1);
    }

    public void setCurrent(int current) {
        this.current = Math.max(Math.min(current, required), 0);
    }

    public void setDimension(String dimension) {
        this. dimension = dimension;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setOrder(int order) {
        this.order = Math.max(order, 0);
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed && this.completedTime < 0) {
            this. completedTime = System.currentTimeMillis();
        }
    }

    public void setMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    // ============ Business Logic ============

    /**
     * 진행도 업데이트
     * @param amount 증가량
     */
    public void updateProgress(int amount) {
        if (amount <= 0) return;
        
        int newCurrent = current + amount;
        setCurrent(newCurrent);
        
        // 자동 완료 체크
        if (current >= required && !completed) {
            this.completed = true;
            this.completedTime = System.currentTimeMillis();
        }
    }

    /**
     * 진행도 직접 설정
     * @param value 설정할 값
     */
    public void setProgress(int value) {
        setCurrent(value);
        
        if (current >= required && !completed) {
            this.completed = true;
            this.completedTime = System.currentTimeMillis();
        }
    }

    /**
     * 진행도 초기화
     */
    public void resetProgress() {
        this.current = 0;
        this.completed = false;
        this.completedTime = -1;
    }

    /**
     * 완료 여부 확인
     * @return 완료 여부
     */
    public boolean isFinished() {
        return current >= required;
    }

    /**
     * 진행률 반환 (0~100%)
     * @return 진행률
     */
    public int getProgressPercentage() {
        if (required == 0) return 0;
        return (int) ((double) current / required * 100);
    }

    /**
     * 남은 진행량 반환
     * @return 남은 진행량
     */
    public int getRemaining() {
        return Math.max(required - current, 0);
    }

    /**
     * 진행 표시줄 생성
     * @param barLength 표시줄 길이
     * @return 진행 표시줄
     */
    public String getProgressBar(int barLength) {
        int filledLength = (int) ((double) getProgressPercentage() / 100 * barLength);
        
        StringBuilder bar = new StringBuilder("§6");
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        
        bar.append("§f ").append(getProgressPercentage()).append("%");
        return bar.toString();
    }

    /**
     * 진행도 문자열 표현
     * @return 진행도 (예: "5/10" 또는 "완료")
     */
    public String getProgressString() {
        if (type == null) return "알 수 없음";
        return type.formatProgress(current, required);
    }

    /**
     * 조건 충족 여부 확인
     * @param playerDimension 플레이어 현재 차원
     * @param playerRegion 플레이어 현재 지역
     * @return 조건 충족 여부
     */
    public boolean meetsLocationRequirements(String playerDimension, String playerRegion) {
        // 차원 조건 확인
        if (dimension != null && !dimension.equals(playerDimension)) {
            return false;
        }
        
        // 지역 조건 확인
        if (region != null && !region.equals(playerRegion)) {
            return false;
        }
        
        return true;
    }

    /**
     * 순차 진행 목표인지 확인
     * @return 순차 진행 여부
     */
    public boolean isSequential() {
        return order > 0;
    }

    /**
     * 동시 진행 목표인지 확인
     * @return 동시 진행 여부
     */
    public boolean isConcurrent() {
        return order == 0;
    }

    /**
     * 개수 세기 가능 여부
     * @return 개수 세기 가능 여부
     */
    public boolean isCountable() {
        return type != null && type.isCountable();
    }

    /**
     * 자동 완료 가능 여부
     * @return 자동 완료 가능 여부
     */
    public boolean isAutoCompletable() {
        return type != null && type.isAutoCompletable();
    }

    /**
     * 메타데이터 조회
     * @param key 키
     * @return 값 (없으면 null)
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * 메타데이터 조회 (기본값 제공)
     * @param key 키
     * @param defaultValue 기본값
     * @return 값
     */
    public Object getMetadata(String key, Object defaultValue) {
        return metadata.getOrDefault(key, defaultValue);
    }

    /**
     * 메타데이터 존재 여부
     * @param key 키
     * @return 존재 여부
     */
    public boolean hasMetadata(String key) {
        return metadata. containsKey(key);
    }

    /**
     * 복사본 생성
     * @return QuestObjective 복사본
     */
    public QuestObjective copy() {
        QuestObjective copy = new QuestObjective();
        copy.objectiveId = this.objectiveId;
        copy.type = this.type;
        copy.description = this.description;
        copy.target = this.target;
        copy.required = this.required;
        copy.current = this.current;
        copy.dimension = this.dimension;
        copy.region = this.region;
        copy.order = this.order;
        copy.completed = this.completed;
        copy.completedTime = this.completedTime;
        copy.metadata = new HashMap<>(this.metadata);
        return copy;
    }

    /**
     * 문자열 표현
     * @return 문자열
     */
    @Override
    public String toString() {
        return String.format("QuestObjective{id=%s, type=%s, progress=%d/%d, completed=%s}",
                objectiveId, type, current, required, completed);
    }

    /**
     * 상세 정보 문자열
     * @return 상세 정보
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 목표 상세 정보 ===§r\n");
        sb.append("§7ID: §f").append(objectiveId). append("\n");
        sb. append("§7설명: §f").append(description). append("\n");
        sb. append("§7타입: §f").append(type != null ? type.getDisplayName() : "알 수 없음").append("\n");
        sb.append("§7대상: §f").append(target).append("\n");
        sb.append("§7진행도: §f").append(getProgressString()).append("\n");
        
        if (dimension != null) {
            sb. append("§7차원: §f").append(dimension).append("\n");
        }
        
        if (region != null) {
            sb.append("§7지역: §f").append(region).append("\n");
        }
        
        sb.append("§7상태: ").append(isCompleted() ? "§a완료" : "§e진행 중").append("\n");
        
        return sb.toString();
    }
}