package com.multiverse.quest.managers;

import com.multiverse.quest.models.*;
import com.multiverse.quest.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit. Bukkit;
import java.util.*;

/**
 * 퀘스트 체인 관리자
 * 퀘스트 체인의 생성, 로드, 수정, 삭제 및 진행을 관리합니다. 
 */
public class QuestChainManager {
    private final DataManager dataManager;
    private final Map<String, QuestChain> chainCache;
    private final Map<UUID, Map<String, Integer>> playerChainProgress;
    private boolean initialized;

    /**
     * 생성자
     * @param dataManager 데이터 관리자
     */
    public QuestChainManager(DataManager dataManager) {
        this.dataManager = dataManager;
        this.chainCache = new LinkedHashMap<>();
        this.playerChainProgress = new HashMap<>();
        this.initialized = false;
    }

    // ============ Initialization ============

    /**
     * 체인 관리자 초기화
     */
    public boolean initialize() {
        try {
            loadAllChains();
            initialized = true;
            Bukkit.getLogger().info("퀘스트 체인 관리자 초기화 완료");
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().warning("퀘스트 체인 관리자 초기화 실패: " + e.getMessage());
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
            chainCache.clear();
            playerChainProgress.clear();
            initialized = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Chain Loading ============

    /**
     * 모든 체인 로드
     */
    public void loadAllChains() {
        chainCache.clear();
        List<QuestChain> chains = dataManager.getAllQuestChains();
        chains.forEach(chain -> chainCache.put(chain.getChainId(), chain));
        Bukkit.getLogger().info("퀘스트 체인 " + chainCache.size() + "개 로드 완료");
    }

    /**
     * 체인 로드 (ID로)
     */
    public QuestChain loadChain(String chainId) {
        if (chainId == null) {
            return null;
        }

        if (chainCache.containsKey(chainId)) {
            return chainCache.get(chainId);
        }

        QuestChain chain = dataManager.getQuestChain(chainId);
        if (chain != null) {
            chainCache. put(chainId, chain);
        }
        return chain;
    }

    // ============ Chain Creation & Modification ============

    /**
     * 체인 생성
     */
    public boolean createChain(QuestChain chain) {
        if (chain == null || chainCache.containsKey(chain. getChainId())) {
            return false;
        }

        try {
            chainCache.put(chain.getChainId(), chain);
            dataManager.saveQuestChain(chain);
            Bukkit. getLogger().info("퀘스트 체인 생성: " + chain.getChainId());
            return true;
        } catch (Exception e) {
            Bukkit.getLogger(). warning("퀘스트 체인 생성 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 체인 수정
     */
    public boolean updateChain(QuestChain chain) {
        if (chain == null || !chainCache.containsKey(chain. getChainId())) {
            return false;
        }

        try {
            chainCache. put(chain.getChainId(), chain);
            dataManager. saveQuestChain(chain);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 체인 삭제
     */
    public boolean deleteChain(String chainId) {
        if (chainId == null || !chainCache.containsKey(chainId)) {
            return false;
        }

        try {
            chainCache. remove(chainId);
            dataManager.deleteQuestChain(chainId);
            Bukkit.getLogger().info("퀘스트 체인 삭제: " + chainId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Chain Retrieval ============

    /**
     * 체인 조회
     */
    public QuestChain getChain(String chainId) {
        return chainCache.getOrDefault(chainId, null);
    }

    /**
     * 모든 체인 조회
     */
    public List<QuestChain> getAllChains() {
        return new ArrayList<>(chainCache.values());
    }

    /**
     * 활성화된 체인만 조회
     */
    public List<QuestChain> getEnabledChains() {
        return chainCache.values().stream()
            .filter(QuestChain::isEnabled)
            .toList();
    }

    /**
     * 체인 개수 반환
     */
    public int getChainCount() {
        return chainCache.size();
    }

    /**
     * 체인 존재 여부 확인
     */
    public boolean chainExists(String chainId) {
        return chainCache.containsKey(chainId);
    }

    // ============ Chain Progress Management ============

    /**
     * 플레이어가 체인을 시작할 수 있는지 확인
     */
    public boolean canStartChain(Player player, String chainId) {
        if (player == null || chainId == null) {
            return false;
        }

        QuestChain chain = getChain(chainId);
        if (chain == null || !chain.isEnabled()) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();
        Map<String, Integer> progress = playerChainProgress.get(playerUUID);

        if (progress != null && progress.containsKey(chainId)) {
            return false; // 이미 진행 중
        }

        return true;
    }

    /**
     * 플레이어 체인 시작
     */
    public boolean startChain(Player player, String chainId) {
        if (player == null || chainId == null || !canStartChain(player, chainId)) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();

        try {
            Map<String, Integer> progress = playerChainProgress
                .computeIfAbsent(playerUUID, k -> new HashMap<>());
            progress.put(chainId, 0); // 첫 번째 퀘스트부터 시작

            player.sendMessage("§a체인을 시작했습니다: " + chainId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 체인 진행도 업데이트
     */
    public boolean updateChainProgress(UUID playerUUID, String chainId) {
        if (playerUUID == null || chainId == null) {
            return false;
        }

        Map<String, Integer> progress = playerChainProgress. get(playerUUID);
        if (progress == null) {
            return false;
        }

        Integer currentProgress = progress.get(chainId);
        if (currentProgress == null) {
            return false;
        }

        progress.put(chainId, currentProgress + 1);
        return true;
    }

    /**
     * 플레이어 체인 완료
     */
    public boolean completeChain(Player player, String chainId) {
        if (player == null || chainId == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();

        try {
            Map<String, Integer> progress = playerChainProgress.get(playerUUID);
            if (progress != null) {
                progress.remove(chainId);
            }

            player.sendMessage("§a체인을 완료했습니다: " + chainId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 플레이어 체인 포기
     */
    public boolean abandonChain(Player player, String chainId) {
        if (player == null || chainId == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();

        try {
            Map<String, Integer> progress = playerChainProgress.get(playerUUID);
            if (progress != null) {
                progress.remove(chainId);
            }

            player.sendMessage("§7체인을 포기했습니다: " + chainId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Chain Progress Retrieval ============

    /**
     * 체인 진행도 조회
     */
    public int getChainProgress(UUID playerUUID, String chainId) {
        if (playerUUID == null || chainId == null) {
            return -1;
        }

        Map<String, Integer> progress = playerChainProgress.get(playerUUID);
        if (progress == null) {
            return -1;
        }

        Integer chainProgress = progress.get(chainId);
        return chainProgress != null ? chainProgress : -1;
    }

    /**
     * 다음 퀘스트 조회
     */
    public String getNextQuestInChain(String chainId, int index) {
        QuestChain chain = getChain(chainId);
        if (chain == null) {
            return null;
        }

        List<String> sequence = chain.getQuestSequence();
        if (index < 0 || index >= sequence.size()) {
            return null;
        }

        return sequence.get(index);
    }

    /**
     * 현재 퀨스트 조회
     */
    public String getCurrentQuestInChain(UUID playerUUID, String chainId) {
        int progress = getChainProgress(playerUUID, chainId);
        if (progress < 0) {
            return null;
        }

        return getNextQuestInChain(chainId, progress);
    }

    /**
     * 플레이어의 모든 진행 중인 체인 조회
     */
    public List<String> getPlayerChains(UUID playerUUID) {
        if (playerUUID == null) {
            return new ArrayList<>();
        }

        Map<String, Integer> progress = playerChainProgress.get(playerUUID);
        return progress != null ? new ArrayList<>(progress.keySet()) : new ArrayList<>();
    }

    /**
     * 플레이어가 체인을 진행 중인지 확인
     */
    public boolean isChainInProgress(UUID playerUUID, String chainId) {
        return getChainProgress(playerUUID, chainId) >= 0;
    }

    // ============ Chain Validation ============

    /**
     * 체인이 유효한지 확인
     */
    public boolean isChainValid(String chainId) {
        QuestChain chain = getChain(chainId);
        if (chain == null) {
            return false;
        }

        if (chain.getChainId() == null || chain. getChainId().isEmpty()) {
            return false;
        }

        if (chain.getQuestSequence() == null || chain.getQuestSequence().isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * 모든 퀘스트가 체인에 존재하는지 확인
     */
    public boolean validateChainQuests(String chainId) {
        QuestChain chain = getChain(chainId);
        if (chain == null) {
            return false;
        }

        // QuestManager와 연동하여 검증 (구현 필요)
        for (String questId : chain.getQuestSequence()) {
            if (questId == null || questId.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    // ============ Statistics ============

    /**
     * 체인 통계 조회
     */
    public Map<String, Object> getChainStatistics(String chainId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        QuestChain chain = getChain(chainId);

        if (chain != null) {
            stats.put("chainId", chain.getChainId());
            stats.put("name", chain.getName());
            stats.put("questCount", chain.getQuestSequence().size());
            stats. put("enabled", chain.isEnabled());
        }

        return stats;
    }

    /**
     * 플레이어 체인 통계
     */
    public Map<String, Object> getPlayerChainStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();

        List<String> chains = getPlayerChains(playerUUID);
        stats.put("playerUUID", playerUUID);
        stats.put("totalChains", chains.size());
        stats.put("chainsInProgress", chains.size());

        return stats;
    }

    /**
     * 전체 체인 통계
     */
    public Map<String, Object> getGlobalChainStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("totalChains", chainCache.size());
        stats.put("enabledChains", (int) chainCache.values().stream()
            .filter(QuestChain::isEnabled).count());
        stats.put("playersWithChains", playerChainProgress.size());

        int totalProgressingChains = 0;
        for (Map<String, Integer> progress : playerChainProgress.values()) {
            totalProgressingChains += progress. size();
        }
        stats. put("totalProgressingChains", totalProgressingChains);

        return stats;
    }

    // ============ Search & Filter ============

    /**
     * 체인명으로 검색
     */
    public List<QuestChain> searchChainsByName(String name) {
        if (name == null || name.isEmpty()) {
            return new ArrayList<>();
        }

        String searchTerm = name.toLowerCase();
        return chainCache.values().stream()
            .filter(c -> c.getName().toLowerCase().contains(searchTerm))
            . toList();
    }

    /**
     * 퀘스트를 포함한 체인 검색
     */
    public List<QuestChain> searchChainsByQuest(String questId) {
        if (questId == null || questId.isEmpty()) {
            return new ArrayList<>();
        }

        return chainCache.values().stream()
            .filter(c -> c. getQuestSequence().contains(questId))
            .toList();
    }

    // ============ Bulk Operations ============

    /**
     * 모든 체인 저장
     */
    public boolean saveAll() {
        try {
            for (QuestChain chain : chainCache. values()) {
                dataManager.saveQuestChain(chain);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 모든 체인 다시 로드
     */
    public boolean reloadAll() {
        try {
            loadAllChains();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 플레이어 체인 진행도 초기화
     */
    public boolean resetPlayerChains(UUID playerUUID) {
        try {
            playerChainProgress.remove(playerUUID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 퀨스트 체인 관리자 상태 ===§r\n");
        sb.append("§7초기화: ").append(initialized ? "§a완료" : "§c미완료").append("\n");
        sb.append("§7체인 수: §f").append(chainCache.size()). append("\n");
        sb. append("§7활성화: §f").append(chainCache.values().stream()
            .filter(QuestChain::isEnabled). count()).append("\n");
        sb.append("§7진행 중인 플레이어: §f").append(playerChainProgress.size()). append("\n");

        return sb.toString();
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
        info. put("cachedChains", chainCache.size());
        info.put("playersWithProgress", playerChainProgress.size());

        return info;
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        chainCache.clear();
        playerChainProgress.clear();
    }
}