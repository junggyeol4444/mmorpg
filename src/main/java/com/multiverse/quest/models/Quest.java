package com.multiverse.quest.models;

import com.multiverse.quest.models.enums.QuestType;
import com.multiverse.quest. models.enums.QuestCategory;
import java.util.*;

/**
 * 퀘스트 데이터 모델
 * 퀘스트의 모든 정보를 정의합니다.
 */
public class Quest {
    private String questId;                 // 고유 ID
    private String name;                    // 퀘스트 이름
    private QuestType type;                 // 퀘스트 타입
    private QuestCategory category;         // 퀘스트 카테고리
    
    private String description;             // 설명
    private List<String> lore;              // 스토리 (여러 줄)
    
    private int requiredLevel;              // 요구 레벨
    private String requiredQuest;           // 선행 퀘스트 ID
    private int requiredReputation;         // 필요 호감도
    private String requiredRace;            // 필요 종족
    private String requiredDimension;       // 필요 차원
    
    private int questGiverNPC;              // 퀘스트 제공 NPC ID
    private int questCompleterNPC;          // 퀘스트 완료 NPC ID (0이면 자동 완료)
    
    private List<QuestObjective> objectives; // 목표 목록
    private QuestReward reward;              // 보상
    
    private int timeLimit;                  // 제한 시간 (초, 0이면 무제한)
    private long cooldown;                  // 쿨다운 (밀리초)
    private boolean enabled;                // 활성화 여부
    
    private long createdTime;               // 생성 시간
    private long lastModifiedTime;          // 마지막 수정 시간

    /**
     * 기본 생성자
     */
    public Quest() {
        this.lore = new ArrayList<>();
        this.objectives = new ArrayList<>();
        this.reward = new QuestReward();
        this.enabled = true;
        this.questCompleterNPC = 0;
        this.requiredLevel = 1;
        this.requiredReputation = 0;
        this.timeLimit = 0;
        this.cooldown = 0;
        this.createdTime = System.currentTimeMillis();
        this.lastModifiedTime = this.createdTime;
    }

    /**
     * 전체 파라미터 생성자
     */
    public Quest(String questId, String name, QuestType type, QuestCategory category) {
        this();
        this.questId = questId;
        this.name = name;
        this.type = type;
        this.category = category;
    }

    // ============ Getters ============

    public String getQuestId() {
        return questId;
    }

    public String getName() {
        return name;
    }

    public QuestType getType() {
        return type;
    }

    public QuestCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public String getRequiredQuest() {
        return requiredQuest;
    }

    public int getRequiredReputation() {
        return requiredReputation;
    }

    public String getRequiredRace() {
        return requiredRace;
    }

    public String getRequiredDimension() {
        return requiredDimension;
    }

    public int getQuestGiverNPC() {
        return questGiverNPC;
    }

    public int getQuestCompleterNPC() {
        return questCompleterNPC;
    }

    public List<QuestObjective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    public QuestObjective getObjective(String objectiveId) {
        return objectives. stream()
                .filter(obj -> obj.getObjectiveId().equals(objectiveId))
                .findFirst()
                .orElse(null);
    }

    public QuestReward getReward() {
        return reward;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public long getCooldown() {
        return cooldown;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public int getObjectiveCount() {
        return objectives.size();
    }

    // ============ Setters ============

    public void setQuestId(String questId) {
        this.questId = questId;
    }

    public void setName(String name) {
        this.name = name;
        updateModifiedTime();
    }

    public void setType(QuestType type) {
        this.type = type;
        updateModifiedTime();
    }

    public void setCategory(QuestCategory category) {
        this.category = category;
        updateModifiedTime();
    }

    public void setDescription(String description) {
        this.description = description;
        updateModifiedTime();
    }

    public void setLore(List<String> lore) {
        this.lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
        updateModifiedTime();
    }

    public void addLoreLine(String line) {
        if (line != null) {
            lore.add(line);
            updateModifiedTime();
        }
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = Math.max(requiredLevel, 1);
        updateModifiedTime();
    }

    public void setRequiredQuest(String requiredQuest) {
        this.requiredQuest = requiredQuest;
        updateModifiedTime();
    }

    public void setRequiredReputation(int requiredReputation) {
        this.requiredReputation = Math.max(requiredReputation, 0);
        updateModifiedTime();
    }

    public void setRequiredRace(String requiredRace) {
        this.requiredRace = requiredRace;
        updateModifiedTime();
    }

    public void setRequiredDimension(String requiredDimension) {
        this.requiredDimension = requiredDimension;
        updateModifiedTime();
    }

    public void setQuestGiverNPC(int questGiverNPC) {
        this.questGiverNPC = questGiverNPC;
        updateModifiedTime();
    }

    public void setQuestCompleterNPC(int questCompleterNPC) {
        this. questCompleterNPC = Math.max(questCompleterNPC, 0);
        updateModifiedTime();
    }

    public void setReward(QuestReward reward) {
        if (reward != null) {
            this.reward = reward;
            updateModifiedTime();
        }
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = Math.max(timeLimit, 0);
        updateModifiedTime();
    }

    public void setCooldown(long cooldown) {
        this. cooldown = Math.max(cooldown, 0);
        updateModifiedTime();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateModifiedTime();
    }

    // ============ Objective Management ============

    /**
     * 목표 추가
     */
    public void addObjective(QuestObjective objective) {
        if (objective != null) {
            objectives.add(objective);
            updateModifiedTime();
        }
    }

    /**
     * 목표 제거
     */
    public boolean removeObjective(String objectiveId) {
        boolean removed = objectives.removeIf(obj -> obj.getObjectiveId().equals(objectiveId));
        if (removed) {
            updateModifiedTime();
        }
        return removed;
    }

    /**
     * 목표 개수 반환
     */
    public int getObjectiveSize() {
        return objectives.size();
    }

    /**
     * 모든 목표 초기화
     */
    public void clearObjectives() {
        objectives.clear();
        updateModifiedTime();
    }

    // ============ Business Logic ============

    /**
     * 퀘스트가 유효한지 확인
     */
    public boolean isValid() {
        return questId != null && ! questId.isEmpty() &&
               name != null && !name.isEmpty() &&
               type != null &&
               !  objectives.isEmpty();
    }

    /**
     * 퀘스트가 시간 제한이 있는지 확인
     */
    public boolean hasTimeLimit() {
        return timeLimit > 0;
    }

    /**
     * 퀘스트가 쿨다운이 있는지 확인
     */
    public boolean hasCooldown() {
        return cooldown > 0;
    }

    /**
     * 퀘스트가 선행 퀘스트를 요구하는지 확인
     */
    public boolean hasRequiredQuest() {
        return requiredQuest != null && !requiredQuest.isEmpty();
    }

    /**
     * 퀘스트가 자동 완료인지 확인
     */
    public boolean isAutoComplete() {
        return questCompleterNPC == 0;
    }

    /**
     * 퀘스트가 NPC 완료 필요한지 확인
     */
    public boolean requiresNPCCompletion() {
        return questCompleterNPC > 0;
    }

    /**
     * 퀘스트가 반복 가능한지 확인
     */
    public boolean isRepeatable() {
        return type != null && type.isRepeatable();
    }

    /**
     * 퀘스트가 메인 퀘스트인지 확인
     */
    public boolean isMainQuest() {
        return type == QuestType.MAIN;
    }

    /**
     * 퀘스트 요구 사항 확인
     */
    public boolean meetsRequirements(int playerLevel, String playerRace, int playerReputation, String playerDimension) {
        // 레벨 확인
        if (playerLevel < requiredLevel) {
            return false;
        }
        
        // 종족 확인
        if (requiredRace != null && !requiredRace.equals(playerRace)) {
            return false;
        }
        
        // 호감도 확인
        if (playerReputation < requiredReputation) {
            return false;
        }
        
        // 차원 확인
        if (requiredDimension != null && ! requiredDimension.equals(playerDimension)) {
            return false;
        }
        
        return true;
    }

    /**
     * 퀘스트 요약 반환
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6"). append(name).append(" §7[").append(type.getDisplayName()).append("]§r\n");
        
        if (description != null && !description.isEmpty()) {
            sb.append("§7"). append(description).append("\n");
        }
        
        sb.append("§7레벨: §f").append(requiredLevel).append("\n");
        sb.append("§7목표: §f").append(objectives.size()).append("개\n");
        
        return sb.toString();
    }

    /**
     * 상세 정보 반환
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== ").append(name).append(" ===§r\n");
        sb. append("§7타입: §f").append(type.getDisplayName()).append(" / ").append(category.getDisplayName()).append("\n");
        
        if (description != null && ! description.isEmpty()) {
            sb.append("§7설명: §f").append(description).append("\n");
        }
        
        if (! lore.isEmpty()) {
            sb.append("§7스토리:\n");
            lore.forEach(line -> sb.append("  §f").append(line).append("\n"));
        }
        
        sb.append("\n§e=== 요구사항 ===§r\n");
        sb. append("§7레벨: §f").append(requiredLevel).append("\n");
        
        if (requiredQuest != null) {
            sb.append("§7선행 퀘스트: §f").append(requiredQuest).append("\n");
        }
        
        if (requiredReputation > 0) {
            sb.append("§7필요 호감도: §f").append(requiredReputation).append("\n");
        }
        
        sb.append("\n§e=== 목표 ("). append(objectives.size()).append("개) ===§r\n");
        for (int i = 0; i < objectives.size(); i++) {
            QuestObjective obj = objectives.get(i);
            sb.append("§f").append(i + 1).append(". ").append(obj.getDescription()).append("\n");
        }
        
        sb.append("\n§e=== 보상 ===§r\n");
        sb.append(reward. getSummary());
        
        return sb.toString();
    }

    /**
     * 복사본 생성
     */
    public Quest copy() {
        Quest copy = new Quest();
        copy.questId = this.questId;
        copy.name = this.name;
        copy.type = this.type;
        copy.category = this.category;
        copy.description = this.description;
        copy.lore = new ArrayList<>(this.lore);
        copy.requiredLevel = this.requiredLevel;
        copy.requiredQuest = this.requiredQuest;
        copy.requiredReputation = this.requiredReputation;
        copy.requiredRace = this.requiredRace;
        copy.requiredDimension = this.requiredDimension;
        copy.questGiverNPC = this.questGiverNPC;
        copy.questCompleterNPC = this.questCompleterNPC;
        copy.objectives = this.objectives. stream().map(QuestObjective::copy).toList();
        copy.reward = this.reward.copy();
        copy.timeLimit = this.timeLimit;
        copy.cooldown = this. cooldown;
        copy.enabled = this.enabled;
        copy.createdTime = this.createdTime;
        copy.lastModifiedTime = this.lastModifiedTime;
        return copy;
    }

    /**
     * 수정 시간 업데이트
     */
    private void updateModifiedTime() {
        this.lastModifiedTime = System. currentTimeMillis();
    }

    /**
     * 문자열 표현
     */
    @Override
    public String toString() {
        return String.format("Quest{id=%s, name=%s, type=%s, objectives=%d}",
                questId, name, type, objectives. size());
    }
}