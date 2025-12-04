package com.multiverse.quest.managers;

import com.multiverse. quest.models.*;
import com.multiverse.quest.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit. Bukkit;
import java.util.*;

/**
 * 퀘스트 관리자
 * 퀘스트 생성, 로드, 수정, 삭제 등을 관리합니다.
 */
public class QuestManager {
    private final DataManager dataManager;
    private final Map<String, Quest> questCache;
    private final Map<String, QuestHandler> questHandlers;
    private boolean initialized;

    /**
     * 생성자
     * @param dataManager 데이터 관리자
     */
    public QuestManager(DataManager dataManager) {
        this.dataManager = dataManager;
        this.questCache = new LinkedHashMap<>();
        this.questHandlers = new HashMap<>();
        this.initialized = false;
    }

    // ============ Initialization ============

    /**
     * 퀘스트 관리자 초기화
     */
    public boolean initialize() {
        try {
            loadAllQuests();
            initialized = true;
            Bukkit.getLogger().info("퀘스트 관리자 초기화 완료");
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().warning("퀘스트 관리자 초기화 실패: " + e.getMessage());
            initialized = false;
            return false;
        }
    }

    /**
     * 초기화 여부 확인
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 종료
     */
    public boolean shutdown() {
        try {
            questCache.clear();
            questHandlers.clear();
            initialized = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Quest Loading ============

    /**
     * 모든 퀘스트 로드
     */
    public void loadAllQuests() {
        questCache.clear();
        List<Quest> quests = dataManager.getAllQuests();
        quests.forEach(quest -> questCache.put(quest.getQuestId(), quest));
        Bukkit.getLogger().info("퀘스트 " + questCache.size() + "개 로드 완료");
    }

    /**
     * 퀘스트 로드 (ID로)
     */
    public Quest loadQuest(String questId) {
        if (questId == null) return null;

        if (questCache.containsKey(questId)) {
            return questCache.get(questId);
        }

        Quest quest = dataManager.getQuest(questId);
        if (quest != null) {
            questCache.put(questId, quest);
        }
        return quest;
    }

    // ============ Quest Creation & Modification ============

    /**
     * 퀘스트 생성
     */
    public boolean createQuest(Quest quest) {
        if (quest == null || questCache.containsKey(quest. getQuestId())) {
            return false;
        }

        try {
            questCache.put(quest.getQuestId(), quest);
            dataManager.saveQuest(quest);
            Bukkit.getLogger().info("퀘스트 생성: " + quest.getQuestId());
            return true;
        } catch (Exception e) {
            Bukkit.getLogger(). warning("퀘스트 생성 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 퀘스트 수정
     */
    public boolean updateQuest(Quest quest) {
        if (quest == null || !questCache.containsKey(quest.getQuestId())) {
            return false;
        }

        try {
            questCache.put(quest.getQuestId(), quest);
            dataManager.saveQuest(quest);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 퀘스트 삭제
     */
    public boolean deleteQuest(String questId) {
        if (questId == null || !questCache.containsKey(questId)) {
            return false;
        }

        try {
            questCache.remove(questId);
            dataManager. deleteQuest(questId);
            Bukkit.getLogger(). info("퀘스트 삭제: " + questId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Quest Retrieval ============

    /**
     * 퀘스트 조회
     */
    public Quest getQuest(String questId) {
        return questCache.getOrDefault(questId, null);
    }

    /**
     * 모든 퀘스트 조회
     */
    public List<Quest> getAllQuests() {
        return new ArrayList<>(questCache.values());
    }

    /**
     * 카테고리별 퀘스트 조회
     */
    public List<Quest> getQuestsByCategory(QuestCategory category) {
        return questCache.values().stream()
            .filter(q -> q.getCategory() == category)
            .toList();
    }

    /**
     * 타입별 퀘스트 조회
     */
    public List<Quest> getQuestsByType(QuestType type) {
        return questCache.values().stream()
            .filter(q -> q.getType() == type)
            .toList();
    }

    /**
     * 활성화된 퀘스트만 조회
     */
    public List<Quest> getEnabledQuests() {
        return questCache.values().stream()
            .filter(Quest::isEnabled)
            .toList();
    }

    /**
     * 퀘스트 개수 반환
     */
    public int getQuestCount() {
        return questCache.size();
    }

    /**
     * 퀘스트 존재 여부 확인
     */
    public boolean questExists(String questId) {
        return questCache.containsKey(questId);
    }

    // ============ Quest Validation ============

    /**
     * 플레이어가 퀨스트를 수락할 수 있는지 확인
     */
    public boolean canAcceptQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        Quest quest = getQuest(questId);
        if (quest == null || !quest.isEnabled()) {
            return false;
        }

        // 플레이어 레벨 확인
        if (player. getLevel() < quest.getRequiredLevel()) {
            return false;
        }

        // 선행 퀨스트 확인
        if (quest.getRequiredQuest() != null && ! quest.getRequiredQuest().isEmpty()) {
            String requiredQuest = quest.getRequiredQuest();
            if (! dataManager.hasPlayerCompletedQuest(player.getUniqueId(), requiredQuest)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 퀨스트가 유효한지 확인
     */
    public boolean isQuestValid(String questId) {
        Quest quest = getQuest(questId);
        if (quest == null) {
            return false;
        }

        // 필수 필드 확인
        if (quest.getQuestId() == null || quest. getQuestId().isEmpty()) {
            return false;
        }
        if (quest.getName() == null || quest.getName(). isEmpty()) {
            return false;
        }
        if (quest.getObjectives() == null || quest.getObjectives().isEmpty()) {
            return false;
        }

        return true;
    }

    // ============ Quest Statistics ============

    /**
     * 퀘스트 통계 조회
     */
    public Map<String, Object> getQuestStatistics(String questId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        Quest quest = getQuest(questId);

        if (quest != null) {
            stats.put("questId", quest.getQuestId());
            stats.put("name", quest.getName());
            stats.put("type", quest.getType().name());
            stats.put("category", quest.getCategory().name());
            stats.put("enabled", quest.isEnabled());
            stats.put("objectives", quest.getObjectives(). size());
            stats.put("requiredLevel", quest.getRequiredLevel());
        }

        return stats;
    }

    /**
     * 전체 퀨스트 통계
     */
    public Map<String, Object> getGlobalStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalQuests", questCache.size());
        stats. put("enabledQuests", (int) questCache.values().stream().filter(Quest::isEnabled).count());
        stats.put("disabledQuests", (int) questCache.values().stream().filter(q -> !q.isEnabled()).count());

        Map<String, Integer> byCategoryCount = new HashMap<>();
        for (Quest quest : questCache.values()) {
            String category = quest.getCategory().name();
            byCategoryCount. put(category, byCategoryCount.getOrDefault(category, 0) + 1);
        }
        stats.put("byCategory", byCategoryCount);

        return stats;
    }

    // ============ Quest Handler Management ============

    /**
     * 퀘스트 핸들러 등록
     */
    public void registerQuestHandler(String questType, QuestHandler handler) {
        if (questType != null && handler != null) {
            questHandlers.put(questType, handler);
        }
    }

    /**
     * 퀘스트 핸들러 조회
     */
    public QuestHandler getQuestHandler(String questType) {
        return questHandlers.getOrDefault(questType, null);
    }

    /**
     * 모든 퀘스트 핸들러 조회
     */
    public Map<String, QuestHandler> getAllQuestHandlers() {
        return new HashMap<>(questHandlers);
    }

    // ============ Search & Filter ============

    /**
     * 퀘스트명으로 검색
     */
    public List<Quest> searchQuestsByName(String name) {
        if (name == null || name.isEmpty()) {
            return new ArrayList<>();
        }

        String searchTerm = name.toLowerCase();
        return questCache.values().stream()
            .filter(q -> q.getName().toLowerCase().contains(searchTerm))
            .toList();
    }

    /**
     * 설명으로 검색
     */
    public List<Quest> searchQuestsByDescription(String description) {
        if (description == null || description.isEmpty()) {
            return new ArrayList<>();
        }

        String searchTerm = description.toLowerCase();
        return questCache.values().stream()
            .filter(q -> q.getDescription().toLowerCase().contains(searchTerm))
            .toList();
    }

    /**
     * 레벨 범위 내 퀈스트 조회
     */
    public List<Quest> getQuestsByLevelRange(int minLevel, int maxLevel) {
        return questCache.values().stream()
            .filter(q -> q.getRequiredLevel() >= minLevel && q.getRequiredLevel() <= maxLevel)
            .toList();
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 퀘스트 관리자 상태 ===§r\n");
        sb.append("§7초기화: ").append(initialized ? "§a완료" : "§c미완료").append("\n");
        sb.append("§7퀈스트 수: §f").append(questCache.size()). append("\n");
        sb. append("§7활성화: §f").append(questCache.values().stream().filter(Quest::isEnabled).count()).append("\n");
        sb.append("§7비활성화: §f").append(questCache.values().stream(). filter(q -> !q.isEnabled()).count()).append("\n");
        sb.append("§7등록된 핸들러: §f").append(questHandlers.size()).append("\n");
        return sb.toString();
    }

    // ============ Bulk Operations ============

    /**
     * 모든 퀈스트 저장
     */
    public boolean saveAll() {
        try {
            for (Quest quest : questCache.values()) {
                dataManager.saveQuest(quest);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 모든 퀈스트 다시 로드
     */
    public boolean reloadAll() {
        try {
            loadAllQuests();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Getters & Setters ============

    /**
     * 데이터 관리자 반환
     */
    public DataManager getDataManager() {
        return dataManager;
    }

    /**
     * 캐시 정보 반환
     */
    public Map<String, Object> getCacheInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info. put("cachedQuests", questCache.size());
        info.put("registeredHandlers", questHandlers. size());
        return info;
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        questCache.clear();
    }
}

/**
 * 퀨스트 핸들러 인터페이스
 */
public interface QuestHandler {
    /**
     * 퀨스트 처리
     */
    boolean handle(Player player, Quest quest);

    /**
     * 퀨스트 취소
     */
    boolean cancel(Player player, Quest quest);
}