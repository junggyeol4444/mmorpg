package com.multiverse.quest.models;

import com.multiverse. quest.models.enums.TrackerPosition;
import java.util.*;

/**
 * 퀘스트 추적 데이터 모델
 * 플레이어의 퀘스트 추적 설정을 관리합니다.
 */
public class QuestTracker {
    private UUID playerUUID;                         // 플레이어 UUID
    private List<String> trackedQuests;              // 추적 중인 퀘스트 목록 (최대 5개)
    private String primaryQuest;                     // 주 퀘스트 (가장 위에 표시)
    
    private boolean showDistance;                    // 거리 표시 여부
    private boolean showObjectives;                  // 목표 표시 여부
    private boolean showPercentage;                  // 진행률 백분율 표시 여부
    private boolean showProgressBar;                 // 진행 표시줄 표시 여부
    private TrackerPosition position;                // 추적기 위치
    
    private boolean enabled;                         // 추적 기능 활성화 여부
    private long lastUpdateTime;                     // 마지막 업데이트 시간
    private Map<String, Object> metadata;            // 추가 데이터

    private static final int MAX_TRACKED_QUESTS = 5; // 최대 추적 퀘스트 개수

    /**
     * 기본 생성자
     */
    public QuestTracker() {
        this.trackedQuests = new ArrayList<>();
        this.metadata = new HashMap<>();
        this. showDistance = true;
        this. showObjectives = true;
        this.showPercentage = true;
        this.showProgressBar = true;
        this.position = TrackerPosition.  TOP_RIGHT;
        this.enabled = true;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * 전체 파라미터 생성자
     */
    public QuestTracker(UUID playerUUID) {
        this();
        this.playerUUID = playerUUID;
    }

    // ============ Getters ============

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public List<String> getTrackedQuests() {
        return new ArrayList<>(trackedQuests);
    }

    public String getPrimaryQuest() {
        return primaryQuest;
    }

    public boolean isShowDistance() {
        return showDistance;
    }

    public boolean isShowObjectives() {
        return showObjectives;
    }

    public boolean isShowPercentage() {
        return showPercentage;
    }

    public boolean isShowProgressBar() {
        return showProgressBar;
    }

    public TrackerPosition getPosition() {
        return position;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }

    public int getTrackedQuestCount() {
        return trackedQuests.size();
    }

    // ============ Setters ============

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void setPrimaryQuest(String primaryQuest) {
        this.primaryQuest = primaryQuest;
        updateLastUpdateTime();
    }

    public void setShowDistance(boolean showDistance) {
        this.showDistance = showDistance;
        updateLastUpdateTime();
    }

    public void setShowObjectives(boolean showObjectives) {
        this.showObjectives = showObjectives;
        updateLastUpdateTime();
    }

    public void setShowPercentage(boolean showPercentage) {
        this.showPercentage = showPercentage;
        updateLastUpdateTime();
    }

    public void setShowProgressBar(boolean showProgressBar) {
        this. showProgressBar = showProgressBar;
        updateLastUpdateTime();
    }

    public void setPosition(TrackerPosition position) {
        if (position != null) {
            this. position = position;
            updateLastUpdateTime();
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateLastUpdateTime();
    }

    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    // ============ Quest Tracking Management ============

    /**
     * 퀘스트 추적 시작
     */
    public boolean trackQuest(String questId) {
        if (questId == null || questId.isEmpty()) {
            return false;
        }
        
        // 이미 추적 중인지 확인
        if (trackedQuests.contains(questId)) {
            return false;
        }
        
        // 최대 개수 체크
        if (trackedQuests.size() >= MAX_TRACKED_QUESTS) {
            return false;
        }
        
        trackedQuests.add(questId);
        
        // 첫 번째 추적 퀘스트면 주 퀘스트로 설정
        if (primaryQuest == null) {
            primaryQuest = questId;
        }
        
        updateLastUpdateTime();
        return true;
    }

    /**
     * 퀘스트 추적 종료
     */
    public boolean untrackQuest(String questId) {
        boolean removed = trackedQuests.remove(questId);
        
        if (removed) {
            // 주 퀘스트가 제거되었으면 다른 퀨스트를 주 퀨스트로 설정
            if (questId.equals(primaryQuest)) {
                primaryQuest = trackedQuests.isEmpty() ? null : trackedQuests.get(0);
            }
            
            updateLastUpdateTime();
        }
        
        return removed;
    }

    /**
     * 모든 추적 해제
     */
    public void untrackAll() {
        trackedQuests.clear();
        primaryQuest = null;
        updateLastUpdateTime();
    }

    /**
     * 퀘스트 추적 여부 확인
     */
    public boolean isTracking(String questId) {
        return trackedQuests.contains(questId);
    }

    /**
     * 추적 중인 퀨스트인지 확인 (추적 기능이 활성화되어 있는지도 확인)
     */
    public boolean isActivelyTracking(String questId) {
        return enabled && trackedQuests.contains(questId);
    }

    /**
     * 추적 가능한 상태인지 확인
     */
    public boolean canTrack() {
        return enabled && trackedQuests.size() < MAX_TRACKED_QUESTS;
    }

    /**
     * 추적 개수가 가득 찼는지 확인
     */
    public boolean isTrackingFull() {
        return trackedQuests.size() >= MAX_TRACKED_QUESTS;
    }

    /**
     * 추적 개수 반환
     */
    public int getRemainingTrackSlots() {
        return MAX_TRACKED_QUESTS - trackedQuests.size();
    }

    /**
     * 주 퀘스트 설정
     */
    public void setPrimaryQuestId(String questId) {
        if (questId != null && trackedQuests.contains(questId)) {
            this.primaryQuest = questId;
            
            // 주 퀨스트를 목록의 첫 번째로 이동
            trackedQuests.remove(questId);
            trackedQuests. add(0, questId);
            
            updateLastUpdateTime();
        }
    }

    /**
     * 주 퀘스트 해제
     */
    public void clearPrimaryQuest() {
        this.primaryQuest = trackedQuests.isEmpty() ? null : trackedQuests.get(0);
        updateLastUpdateTime();
    }

    /**
     * 주 퀴스트인지 확인
     */
    public boolean isPrimaryQuest(String questId) {
        return questId != null && questId.equals(primaryQuest);
    }

    /**
     * 추적 순서 변경
     */
    public void reorderTrackedQuests(String questId, int newIndex) {
        int currentIndex = trackedQuests.indexOf(questId);
        if (currentIndex >= 0 && newIndex >= 0 && newIndex < trackedQuests.size()) {
            trackedQuests.remove(currentIndex);
            trackedQuests. add(newIndex, questId);
            updateLastUpdateTime();
        }
    }

    /**
     * 위치 순환 이동
     */
    public void cyclePosition() {
        this.position = position.getNext();
        updateLastUpdateTime();
    }

    /**
     * 위치 역순환 이동
     */
    public void cyclePreviousPosition() {
        this. position = position.getPrevious();
        updateLastUpdateTime();
    }

    // ============ Display Settings ============

    /**
     * 모든 표시 옵션 활성화
     */
    public void enableAllDisplay() {
        this.showDistance = true;
        this.showObjectives = true;
        this.showPercentage = true;
        this.showProgressBar = true;
        updateLastUpdateTime();
    }

    /**
     * 모든 표시 옵션 비활성화
     */
    public void disableAllDisplay() {
        this.showDistance = false;
        this.showObjectives = false;
        this.showPercentage = false;
        this.showProgressBar = false;
        updateLastUpdateTime();
    }

    /**
     * 최소 표시 설정
     */
    public void setMinimalDisplay() {
        this.showDistance = false;
        this. showObjectives = false;
        this.showPercentage = false;
        this.showProgressBar = true;
        updateLastUpdateTime();
    }

    /**
     * 최대 표시 설정
     */
    public void setMaximalDisplay() {
        enableAllDisplay();
    }

    /**
     * 표시 프리셋 적용
     */
    public void applyDisplayPreset(String preset) {
        switch (preset.toLowerCase()) {
            case "minimal":
                setMinimalDisplay();
                break;
            case "maximal":
                setMaximalDisplay();
                break;
            case "enabled":
                enableAllDisplay();
                break;
            case "disabled":
                disableAllDisplay();
                break;
            default:
                break;
        }
    }

    // ============ Business Logic ============

    /**
     * 추적이 활성화되어 있는지 확인
     */
    public boolean hasActiveTracking() {
        return enabled && !  trackedQuests.isEmpty();
    }

    /**
     * 추적 퀴스트 정보 요약
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 퀘스트 추적 ===§r\n");
        sb.append("§7상태: ").append(enabled ?  "§a활성화" : "§c비활성화").append("\n");
        sb.append("§7위치: §f").append(position.getDisplayName()).append("\n");
        sb.append("§7추적: §f").append(trackedQuests. size()).append("/").append(MAX_TRACKED_QUESTS).append("\n");
        
        if (!  trackedQuests.isEmpty()) {
            sb.append("\n§e=== 추적 중인 퀘스트 ===§r\n");
            for (int i = 0; i < trackedQuests.size(); i++) {
                String questId = trackedQuests. get(i);
                String marker = questId.equals(primaryQuest) ? "★" : " ";
                sb. append("§f").append(marker).append(" ").append(i + 1).append(".  ").append(questId).append("\n");
            }
        }
        
        return sb. toString();
    }

    /**
     * 상세 설정 정보
     */
    public String getSettingsInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 추적 설정 ===§r\n");
        sb. append("§7추적 활성화: ").append(enabled ?  "§a활성화" : "§c비활성화").append("\n");
        sb.append("§7위치: §f").append(position.getDisplayName()).append("\n");
        sb.append("§7거리 표시: ").append(showDistance ?  "§a표시" : "§c숨김").append("\n");
        sb.append("§7목표 표시: ").append(showObjectives ? "§a표시" : "§c숨김").append("\n");
        sb.append("§7진행률 표시: ").append(showPercentage ? "§a표시" : "§c숨김").append("\n");
        sb.append("§7진행 표시줄: ").append(showProgressBar ? "§a표시" : "§c숨김"). append("\n");
        
        return sb.toString();
    }

    /**
     * 다음 추적 퀘스트 반환
     */
    public String getNextTrackedQuest(String currentQuestId) {
        int index = trackedQuests.indexOf(currentQuestId);
        if (index >= 0 && index < trackedQuests.size() - 1) {
            return trackedQuests.get(index + 1);
        }
        return null;
    }

    /**
     * 이전 추적 퀘스트 반환
     */
    public String getPreviousTrackedQuest(String currentQuestId) {
        int index = trackedQuests.indexOf(currentQuestId);
        if (index > 0) {
            return trackedQuests.get(index - 1);
        }
        return null;
    }

    /**
     * 추적 인덱스 반환 (-1이면 추적 중이 아님)
     */
    public int getTrackedIndex(String questId) {
        return trackedQuests.indexOf(questId);
    }

    /**
     * 메타데이터 조회
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * 메타데이터 조회 (기본값)
     */
    public Object getMetadata(String key, Object defaultValue) {
        return metadata. getOrDefault(key, defaultValue);
    }

    /**
     * 복사본 생성
     */
    public QuestTracker copy() {
        QuestTracker copy = new QuestTracker();
        copy.playerUUID = this.playerUUID;
        copy.trackedQuests = new ArrayList<>(this.trackedQuests);
        copy.primaryQuest = this.primaryQuest;
        copy.showDistance = this.showDistance;
        copy.showObjectives = this.showObjectives;
        copy.showPercentage = this. showPercentage;
        copy.showProgressBar = this.showProgressBar;
        copy.position = this.position;
        copy.enabled = this.enabled;
        copy.lastUpdateTime = this.lastUpdateTime;
        copy.metadata = new HashMap<>(this.metadata);
        return copy;
    }

    /**
     * 마지막 업데이트 시간 업데이트
     */
    private void updateLastUpdateTime() {
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * 문자열 표현
     */
    @Override
    public String toString() {
        return String.format("QuestTracker{playerUUID=%s, tracked=%d, primary=%s, position=%s}",
                playerUUID, trackedQuests. size(), primaryQuest, position. name());
    }

    /**
     * 비교 메서드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        QuestTracker that = (QuestTracker) o;
        return playerUUID.equals(that.playerUUID);
    }

    /**
     * 해시 코드
     */
    @Override
    public int hashCode() {
        return playerUUID. hashCode();
    }
}