package com.multiverse.quest.models;

import java.util.*;

/**
 * 퀘스트 체인 데이터 모델
 * 연속된 퀘스트들을 체인 형태로 관리합니다.
 */
public class QuestChain {
    private String chainId;                         // 고유 ID
    private String name;                            // 체인 이름
    private String description;                     // 설명
    
    private List<String> questSequence;             // 순차 퀘스트 목록 (순서대로)
    private Map<String, List<String>> branches;     // 분기 (questId -> 다음 퀘스트 목록)
    
    private QuestReward chainCompletionReward;      // 체인 완료 보너스 보상
    private boolean enabled;                        // 활성화 여부
    
    private long createdTime;                       // 생성 시간
    private long lastModifiedTime;                  // 마지막 수정 시간

    /**
     * 기본 생성자
     */
    public QuestChain() {
        this.questSequence = new ArrayList<>();
        this.branches = new HashMap<>();
        this.chainCompletionReward = new QuestReward();
        this.enabled = true;
        this.createdTime = System.currentTimeMillis();
        this.lastModifiedTime = this.createdTime;
    }

    /**
     * 전체 파라미터 생성자
     */
    public QuestChain(String chainId, String name, String description) {
        this();
        this.chainId = chainId;
        this.name = name;
        this.description = description;
    }

    // ============ Getters ============

    public String getChainId() {
        return chainId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getQuestSequence() {
        return new ArrayList<>(questSequence);
    }

    public Map<String, List<String>> getBranches() {
        return new HashMap<>(branches);
    }

    public List<String> getBranches(String questId) {
        List<String> branchList = branches.get(questId);
        return branchList != null ? new ArrayList<>(branchList) : new ArrayList<>();
    }

    public QuestReward getChainCompletionReward() {
        return chainCompletionReward;
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

    public int getQuestCount() {
        Set<String> allQuests = new HashSet<>(questSequence);
        branches.values().forEach(allQuests::addAll);
        return allQuests.size();
    }

    // ============ Setters ============

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public void setName(String name) {
        this.name = name;
        updateModifiedTime();
    }

    public void setDescription(String description) {
        this.description = description;
        updateModifiedTime();
    }

    public void setChainCompletionReward(QuestReward reward) {
        if (reward != null) {
            this.chainCompletionReward = reward;
            updateModifiedTime();
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateModifiedTime();
    }

    // ============ Quest Sequence Management ============

    /**
     * 순차 퀘스트 추가
     */
    public void addQuestToSequence(String questId) {
        if (questId != null && !questId.isEmpty() && !questSequence.contains(questId)) {
            questSequence.add(questId);
            updateModifiedTime();
        }
    }

    /**
     * 순차 퀘스트 추가 (위치 지정)
     */
    public void addQuestToSequence(int index, String questId) {
        if (questId != null && !questId. isEmpty() && !questSequence. contains(questId)) {
            if (index >= 0 && index <= questSequence.size()) {
                questSequence.add(index, questId);
            } else {
                questSequence.add(questId);
            }
            updateModifiedTime();
        }
    }

    /**
     * 순차 퀘스트 제거
     */
    public boolean removeQuestFromSequence(String questId) {
        boolean removed = questSequence.remove(questId);
        if (removed) {
            // 이 퀘스트를 시작점으로 하는 분기도 제거
            branches.remove(questId);
            updateModifiedTime();
        }
        return removed;
    }

    /**
     * 순차 퀘스트 순서 변경
     */
    public void reorderQuestSequence(String questId, int newIndex) {
        int currentIndex = questSequence.indexOf(questId);
        if (currentIndex >= 0 && newIndex >= 0 && newIndex < questSequence.size()) {
            questSequence.remove(currentIndex);
            questSequence. add(newIndex, questId);
            updateModifiedTime();
        }
    }

    /**
     * 다음 순차 퀘스트 가져오기
     */
    public String getNextQuestInSequence(String currentQuestId) {
        int index = questSequence.indexOf(currentQuestId);
        if (index >= 0 && index < questSequence.size() - 1) {
            return questSequence.get(index + 1);
        }
        return null;
    }

    /**
     * 이전 순차 퀘스트 가져오기
     */
    public String getPreviousQuestInSequence(String currentQuestId) {
        int index = questSequence.indexOf(currentQuestId);
        if (index > 0) {
            return questSequence.get(index - 1);
        }
        return null;
    }

    /**
     * 첫 번째 퀘스트 가져오기
     */
    public String getFirstQuest() {
        return questSequence.isEmpty() ? null : questSequence. get(0);
    }

    /**
     * 마지막 퀘스트 가져오기
     */
    public String getLastQuest() {
        return questSequence. isEmpty() ? null : questSequence.get(questSequence.size() - 1);
    }

    /**
     * 퀘스트 인덱스 가져오기
     */
    public int getQuestIndex(String questId) {
        return questSequence.indexOf(questId);
    }

    // ============ Branch Management ============

    /**
     * 분기 추가 (분기점 퀘스트 -> 다음 퀘스트들)
     */
    public void addBranch(String fromQuestId, String toQuestId) {
        if (fromQuestId != null && toQuestId != null && 
            !fromQuestId. isEmpty() && !toQuestId.isEmpty()) {
            
            List<String> branchList = branches.computeIfAbsent(fromQuestId, k -> new ArrayList<>());
            if (!branchList.contains(toQuestId)) {
                branchList.add(toQuestId);
                updateModifiedTime();
            }
        }
    }

    /**
     * 분기 제거
     */
    public boolean removeBranch(String fromQuestId, String toQuestId) {
        List<String> branchList = branches.get(fromQuestId);
        if (branchList != null) {
            boolean removed = branchList.remove(toQuestId);
            if (branchList.isEmpty()) {
                branches.remove(fromQuestId);
            }
            if (removed) {
                updateModifiedTime();
            }
            return removed;
        }
        return false;
    }

    /**
     * 분기점 여부 확인
     */
    public boolean isBranchPoint(String questId) {
        List<String> branchList = branches.get(questId);
        return branchList != null && branchList.size() > 1;
    }

    /**
     * 특정 퀘스트가 분기를 가지는지 확인
     */
    public boolean hasBranches(String questId) {
        return branches.containsKey(questId) && ! branches.get(questId).isEmpty();
    }

    /**
     * 체인에 퀘스트가 포함되어 있는지 확인
     */
    public boolean containsQuest(String questId) {
        if (questSequence.contains(questId)) {
            return true;
        }
        
        for (List<String> branchList : branches.values()) {
            if (branchList. contains(questId)) {
                return true;
            }
        }
        
        return false;
    }

    // ============ Business Logic ============

    /**
     * 체인이 유효한지 확인
     */
    public boolean isValid() {
        return chainId != null && ! chainId.isEmpty() &&
               name != null && !name.isEmpty() &&
               ! questSequence.isEmpty();
    }

    /**
     * 체인이 선형 구조인지 확인 (분기 없음)
     */
    public boolean isLinearChain() {
        return branches.isEmpty();
    }

    /**
     * 체인이 분기를 가지는지 확인
     */
    public boolean hasBranchingPath() {
        return ! branches.isEmpty();
    }

    /**
     * 체인의 깊이 반환 (최대 분기 깊이)
     */
    public int getChainDepth() {
        if (questSequence.isEmpty()) return 0;
        return calculateDepth(getFirstQuest(), new HashSet<>());
    }

    /**
     * 재귀적으로 깊이 계산
     */
    private int calculateDepth(String questId, Set<String> visited) {
        if (questId == null || visited.contains(questId)) {
            return 0;
        }
        
        visited.add(questId);
        
        List<String> nextQuests = new ArrayList<>();
        
        // 다음 순차 퀘스트
        String nextSequence = getNextQuestInSequence(questId);
        if (nextSequence != null) {
            nextQuests.add(nextSequence);
        }
        
        // 분기 퀘스트들
        List<String> branches = getBranches(questId);
        nextQuests.addAll(branches);
        
        if (nextQuests.isEmpty()) {
            return 1;
        }
        
        int maxDepth = 0;
        for (String nextQuest : nextQuests) {
            maxDepth = Math.max(maxDepth, calculateDepth(nextQuest, new HashSet<>(visited)));
        }
        
        return maxDepth + 1;
    }

    /**
     * 체인 진행률 계산
     */
    public int getProgressPercentage(int completedQuests) {
        int totalQuests = getQuestCount();
        if (totalQuests == 0) return 0;
        return (completedQuests * 100) / totalQuests;
    }

    /**
     * 체인 완료 여부 확인 (모든 경로)
     */
    public boolean isChainCompleted(Set<String> completedQuests) {
        // 모든 순차 퀘스트가 완료되었는지 확인
        for (String questId : questSequence) {
            if (!completedQuests.contains(questId)) {
                return false;
            }
        }
        
        // 분기의 경우, 분기점의 다음 퀘스트 중 하나라도 완료되면 OK
        for (Map.Entry<String, List<String>> entry : branches.entrySet()) {
            String fromQuestId = entry.getKey();
            List<String> toQuestIds = entry.getValue();
            
            if (completedQuests.contains(fromQuestId)) {
                boolean anyBranchCompleted = toQuestIds.stream()
                        .anyMatch(completedQuests::contains);
                
                if (! anyBranchCompleted) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * 체인 요약 반환
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6"). append(name).append("§r\n");
        
        if (description != null && !description.isEmpty()) {
            sb.append("§7").append(description).append("\n");
        }
        
        sb.append("§7퀘스트: §f").append(getQuestCount()).append("개\n");
        sb.append("§7깊이: §f").append(getChainDepth()).append("\n");
        
        if (hasBranchingPath()) {
            sb.append("§7구조: §f분기형\n");
        } else {
            sb.append("§7구조: §f선형\n");
        }
        
        return sb.toString();
    }

    /**
     * 상세 정보 반환
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== ").append(name).append(" ===§r\n");
        
        if (description != null && !description.isEmpty()) {
            sb. append("§7"). append(description).append("\n");
        }
        
        sb.append("\n§e=== 퀘스트 시퀀스 ===§r\n");
        for (int i = 0; i < questSequence.size(); i++) {
            String questId = questSequence.get(i);
            sb.append("§f"). append(i + 1).append(".  ").append(questId);
            
            if (hasBranches(questId)) {
                sb.append(" §6[분기점]");
                List<String> branchList = getBranches(questId);
                for (String branch : branchList) {
                    sb.append("\n  §7→ ").append(branch);
                }
            }
            
            sb.append("\n");
        }
        
        sb.append("\n§e=== 체인 완료 보상 ===§r\n");
        sb.append(chainCompletionReward. getSummary());
        
        return sb.toString();
    }

    /**
     * 복사본 생성
     */
    public QuestChain copy() {
        QuestChain copy = new QuestChain();
        copy. chainId = this.chainId;
        copy. name = this.name;
        copy.description = this.description;
        copy.questSequence = new ArrayList<>(this.questSequence);
        copy.branches = new HashMap<>();
        this.branches.forEach((key, value) -> copy.branches.put(key, new ArrayList<>(value)));
        copy.chainCompletionReward = this.chainCompletionReward. copy();
        copy.enabled = this.enabled;
        copy.createdTime = this.createdTime;
        copy.lastModifiedTime = this.lastModifiedTime;
        return copy;
    }

    /**
     * 수정 시간 업데이트
     */
    private void updateModifiedTime() {
        this.lastModifiedTime = System.currentTimeMillis();
    }

    /**
     * 문자열 표현
     */
    @Override
    public String toString() {
        return String.format("QuestChain{id=%s, name=%s, quests=%d, depth=%d}",
                chainId, name, getQuestCount(), getChainDepth());
    }

    /**
     * 비교 메서드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        QuestChain that = (QuestChain) o;
        return chainId != null && chainId.equals(that.chainId);
    }

    /**
     * 해시 코드
     */
    @Override
    public int hashCode() {
        return chainId != null ?  chainId.hashCode() : 0;
    }
}