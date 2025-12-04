package com.multiverse.quest.data;

import com.multiverse. quest.models.*;
import com.multiverse.quest.data.storage.*;
import java.util.*;

/**
 * 데이터 관리자 (통합 인터페이스)
 * 퀘스트, 플레이어, 진행도 데이터를 통합 관리합니다.
 */
public class DataManager {
    private final YAMLDataManager yamlDataManager;
    private final Map<String, Quest> questCache;
    private final Map<String, QuestChain> chainCache;
    private final Map<UUID, Map<String, PlayerQuest>> playerQuestCache;
    private final Map<UUID, Map<String, Object>> playerDataCache;
    private boolean initialized;

    /**
     * 생성자
     * @param yamlDataManager YAML 데이터 매니저
     */
    public DataManager(YAMLDataManager yamlDataManager) {
        this. yamlDataManager = yamlDataManager;
        this.questCache = new LinkedHashMap<>();
        this.chainCache = new LinkedHashMap<>();
        this.playerQuestCache = new HashMap<>();
        this.playerDataCache = new HashMap<>();
        this. initialized = false;
    }

    // ============ Initialization ============

    /**
     * 데이터 매니저 초기화
     */
    public boolean initialize() {
        if (! yamlDataManager.connect()) {
            return false;
        }

        try {
            loadAllData();
            initialized = true;
            return true;
        } catch (Exception e) {
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
            saveAllData();
            questCache.clear();
            chainCache.clear();
            playerQuestCache.clear();
            playerDataCache.clear();
            yamlDataManager.disconnect();
            initialized = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Data Loading ============

    /**
     * 모든 데이터 로드
     */
    private void loadAllData() {
        // 퀘스트 로드
        List<Quest> quests = yamlDataManager.loadAllQuests();
        quests.forEach(quest -> questCache.put(quest.getQuestId(), quest));

        // 체인 로드
        List<QuestChain> chains = yamlDataManager.loadAllQuestChains();
        chains. forEach(chain -> chainCache.put(chain.getChainId(), chain));
    }

    /**
     * 모든 데이터 저장
     */
    private void saveAllData() {
        // 퀘스트 저장
        questCache.values().forEach(yamlDataManager::saveQuest);

        // 체인 저장
        chainCache.values(). forEach(yamlDataManager::saveQuestChain);

        // 플레이어 데이터 저장
        playerQuestCache.forEach((playerUUID, questMap) ->
            questMap.values().forEach(playerQuest ->
                yamlDataManager.savePlayerQuest(playerUUID, playerQuest)
            )
        );

        playerDataCache.forEach((playerUUID, dataMap) ->
            dataMap.forEach((key, value) ->
                yamlDataManager.savePlayerData(playerUUID, key, value)
            )
        );
    }

    // ============ Quest Management ============

    /**
     * 퀘스트 저장
     */
    public boolean saveQuest(Quest quest) {
        if (quest == null) return false;

        questCache.put(quest.getQuestId(), quest);
        return yamlDataManager.saveQuest(quest);
    }

    /**
     * 퀘스트 로드
     */
    public Quest getQuest(String questId) {
        if (questId == null) return null;

        // 캐시에서 먼저 확인
        if (questCache.containsKey(questId)) {
            return questCache.get(questId);
        }

        // 파일에서 로드
        Quest quest = yamlDataManager. loadQuest(questId);
        if (quest != null) {
            questCache.put(questId, quest);
        }
        return quest;
    }

    /**
     * 모든 퀘스트 조회
     */
    public List<Quest> getAllQuests() {
        return new ArrayList<>(questCache.values());
    }

    /**
     * 퀘스트 삭제
     */
    public boolean deleteQuest(String questId) {
        if (questId == null) return false;

        questCache.remove(questId);
        return yamlDataManager.deleteQuest(questId);
    }

    /**
     * 퀘스트 존재 여부 확인
     */
    public boolean questExists(String questId) {
        return questCache.containsKey(questId);
    }

    /**
     * 퀘스트 개수 반환
     */
    public int getQuestCount() {
        return questCache.size();
    }

    // ============ Quest Chain Management ============

    /**
     * 퀘스트 체인 저장
     */
    public boolean saveQuestChain(QuestChain chain) {
        if (chain == null) return false;

        chainCache.put(chain.getChainId(), chain);
        return yamlDataManager.saveQuestChain(chain);
    }

    /**
     * 퀘스트 체인 로드
     */
    public QuestChain getQuestChain(String chainId) {
        if (chainId == null) return null;

        // 캐시에서 먼저 확인
        if (chainCache.containsKey(chainId)) {
            return chainCache. get(chainId);
        }

        // 파일에서 로드
        QuestChain chain = yamlDataManager. loadQuestChain(chainId);
        if (chain != null) {
            chainCache.put(chainId, chain);
        }
        return chain;
    }

    /**
     * 모든 퀘스트 체인 조회
     */
    public List<QuestChain> getAllQuestChains() {
        return new ArrayList<>(chainCache. values());
    }

    /**
     * 퀘스트 체인 삭제
     */
    public boolean deleteQuestChain(String chainId) {
        if (chainId == null) return false;

        chainCache.remove(chainId);
        return yamlDataManager.deleteQuestChain(chainId);
    }

    /**
     * 퀘스트 체인 존재 여부 확인
     */
    public boolean questChainExists(String chainId) {
        return chainCache.containsKey(chainId);
    }

    /**
     * 퀘스트 체인 개수 반환
     */
    public int getQuestChainCount() {
        return chainCache.size();
    }

    // ============ Player Quest Progress ============

    /**
     * 플레이어 퀨스트 저장
     */
    public boolean savePlayerQuest(UUID playerUUID, PlayerQuest playerQuest) {
        if (playerUUID == null || playerQuest == null) return false;

        Map<String, PlayerQuest> playerQuests = playerQuestCache
            .computeIfAbsent(playerUUID, k -> new HashMap<>());
        playerQuests.put(playerQuest.getQuestId(), playerQuest);

        return yamlDataManager.savePlayerQuest(playerUUID, playerQuest);
    }

    /**
     * 플레이어 퀨스트 로드
     */
    public PlayerQuest getPlayerQuest(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null) return null;

        Map<String, PlayerQuest> playerQuests = playerQuestCache. get(playerUUID);
        if (playerQuests != null && playerQuests.containsKey(questId)) {
            return playerQuests.get(questId);
        }

        // 파일에서 로드
        PlayerQuest playerQuest = yamlDataManager.loadPlayerQuest(playerUUID, questId);
        if (playerQuest != null) {
            playerQuestCache.computeIfAbsent(playerUUID, k -> new HashMap<>())
                .put(questId, playerQuest);
        }
        return playerQuest;
    }

    /**
     * 플레이어의 모든 퀨스트 조회
     */
    public List<PlayerQuest> getPlayerAllQuests(UUID playerUUID) {
        if (playerUUID == null) return new ArrayList<>();

        Map<String, PlayerQuest> playerQuests = playerQuestCache.get(playerUUID);
        if (playerQuests != null) {
            return new ArrayList<>(playerQuests.values());
        }

        // 파일에서 로드
        List<PlayerQuest> quests = yamlDataManager.loadPlayerAllQuests(playerUUID);
        if (!quests.isEmpty()) {
            playerQuestCache.computeIfAbsent(playerUUID, k -> new HashMap<>())
                .putAll(quests. stream().collect(
                    java.util.stream.Collectors. toMap(
                        PlayerQuest::getQuestId,
                        q -> q
                    )
                ));
        }
        return quests;
    }

    /**
     * 플레이어 퀨스트 삭제
     */
    public boolean deletePlayerQuest(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null) return false;

        Map<String, PlayerQuest> playerQuests = playerQuestCache.get(playerUUID);
        if (playerQuests != null) {
            playerQuests.remove(questId);
        }

        return yamlDataManager.deletePlayerQuest(playerUUID, questId);
    }

    /**
     * 플레이어 모든 퀨스트 삭제
     */
    public boolean deletePlayerAllQuests(UUID playerUUID) {
        if (playerUUID == null) return false;

        playerQuestCache.remove(playerUUID);
        return yamlDataManager.deletePlayerData(playerUUID);
    }

    // ============ Player Data Management ============

    /**
     * 플레이어 데이터 저장
     */
    public boolean savePlayerData(UUID playerUUID, String key, Object value) {
        if (playerUUID == null || key == null) return false;

        Map<String, Object> playerData = playerDataCache
            . computeIfAbsent(playerUUID, k -> new HashMap<>());
        playerData.put(key, value);

        return yamlDataManager.savePlayerData(playerUUID, key, value);
    }

    /**
     * 플레이어 데이터 로드
     */
    public Object getPlayerData(UUID playerUUID, String key) {
        if (playerUUID == null || key == null) return null;

        Map<String, Object> playerData = playerDataCache.get(playerUUID);
        if (playerData != null && playerData.containsKey(key)) {
            return playerData. get(key);
        }

        // 파일에서 로드
        Object value = yamlDataManager.loadPlayerData(playerUUID, key);
        if (value != null) {
            playerDataCache.computeIfAbsent(playerUUID, k -> new HashMap<>())
                .put(key, value);
        }
        return value;
    }

    /**
     * 플레이어 데이터 모두 조회
     */
    public Map<String, Object> getPlayerAllData(UUID playerUUID) {
        if (playerUUID == null) return new HashMap<>();

        return playerDataCache.getOrDefault(playerUUID, new HashMap<>());
    }

    /**
     * 플레이어 데이터 삭제
     */
    public boolean deletePlayerData(UUID playerUUID) {
        if (playerUUID == null) return false;

        playerDataCache.remove(playerUUID);
        playerQuestCache.remove(playerUUID);
        return yamlDataManager.deletePlayerData(playerUUID);
    }

    // ============ Player Statistics ============

    /**
     * 플레이어의 완료된 퀨스트 개수
     */
    public int getPlayerCompletedQuestCount(UUID playerUUID) {
        List<PlayerQuest> quests = getPlayerAllQuests(playerUUID);
        return (int) quests.stream()
            .filter(pq -> pq.getStatus(). isCompleted())
            .count();
    }

    /**
     * 플레이어의 진행 중인 퀨스트 개수
     */
    public int getPlayerInProgressQuestCount(UUID playerUUID) {
        List<PlayerQuest> quests = getPlayerAllQuests(playerUUID);
        return (int) quests.stream()
            .filter(pq -> pq.getStatus().isActive())
            .count();
    }

    /**
     * 플레이어가 퀨스트를 완료했는지 확인
     */
    public boolean hasPlayerCompletedQuest(UUID playerUUID, String questId) {
        PlayerQuest playerQuest = getPlayerQuest(playerUUID, questId);
        return playerQuest != null && playerQuest.getStatus().isCompleted();
    }

    /**
     * 플레이어가 퀨스트를 진행 중인지 확인
     */
    public boolean hasPlayerQuestInProgress(UUID playerUUID, String questId) {
        PlayerQuest playerQuest = getPlayerQuest(playerUUID, questId);
        return playerQuest != null && playerQuest. getStatus().isActive();
    }

    // ============ Cache Management ============

    /**
     * 캐시 정보 반환
     */
    public Map<String, Object> getCacheInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("quests", questCache.size());
        info. put("chains", chainCache.size());
        info.put("players", playerQuestCache.size());
        info.put("playerData", playerDataCache.size());
        return info;
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        questCache.clear();
        chainCache.clear();
        playerQuestCache.clear();
        playerDataCache.clear();
    }

    // ============ Backup & Recovery ============

    /**
     * 모든 데이터 저장
     */
    public boolean saveAll() {
        try {
            saveAllData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 모든 데이터 리로드
     */
    public boolean reloadAll() {
        try {
            clearCache();
            loadAllData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Status ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 데이터 매니저 상태 ===§r\n");
        sb.append("§7초기화: ").append(initialized ? "§a완료" : "§c미완료").append("\n");
        sb.append("§7YAML 매니저: ").append(yamlDataManager.isConnected() ? "§a연결됨" : "§c연결 안됨").append("\n");
        sb.append("\n§e=== 캐시 정보 ===§r\n");
        getCacheInfo().forEach((key, value) ->
            sb.append("§7").append(key).append(": §f").append(value).append("\n")
        );
        return sb.toString();
    }
}