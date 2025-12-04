package com.multiverse.quest.listeners;

import com.multiverse.  quest.models.*;
import com.multiverse.quest.managers.QuestDataManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit. entity.ArmorStand;
import org. bukkit.event.EventHandler;
import org.bukkit.event.  EventPriority;
import org.bukkit.event.Listener;
import org.bukkit. event.  player.PlayerInteractAtEntityEvent;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * NPC 리스너
 * NPC와의 상호작용 및 퀨스트 대화를 처리합니다.
 */
public class NPCListener implements Listener {
    private final QuestDataManager questDataManager;
    private final Map<String, NPCQuestHandler> npcQuestHandlers;
    private final Map<UUID, String> playerCurrentNPC;
    private final Map<UUID, Long> playerLastNPCInteraction;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     */
    public NPCListener(QuestDataManager questDataManager) {
        this.questDataManager = questDataManager;
        this. npcQuestHandlers = new HashMap<>();
        this.playerCurrentNPC = new HashMap<>();
        this.playerLastNPCInteraction = new HashMap<>();
    }

    // ============ NPC Handler Management ============

    /**
     * NPC 퀨스트 핸들러 등록
     */
    public void registerNPCQuestHandler(String npcId, NPCQuestHandler handler) {
        if (npcId != null && handler != null) {
            npcQuestHandlers. put(npcId, handler);
            Bukkit.getLogger().info("NPC 퀨스트 핸들러 등록: " + npcId);
        }
    }

    /**
     * NPC 퀨스트 핸들러 조회
     */
    public NPCQuestHandler getNPCQuestHandler(String npcId) {
        return npcQuestHandlers.getOrDefault(npcId, null);
    }

    /**
     * 모든 NPC 핸들러 조회
     */
    public Map<String, NPCQuestHandler> getAllNPCHandlers() {
        return new HashMap<>(npcQuestHandlers);
    }

    // ============ NPC Interaction Events ============

    /**
     * NPC 상호작용 이벤트 감지
     */
    @EventHandler(priority = EventPriority.  NORMAL)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event. getRightClicked();
        UUID playerUUID = player.  getUniqueId();

        // 쿨타임 확인 (0.5초)
        Long lastInteraction = playerLastNPCInteraction.get(playerUUID);
        if (lastInteraction != null && (System.currentTimeMillis() - lastInteraction) < 500) {
            return;
        }

        playerLastNPCInteraction.put(playerUUID, System.currentTimeMillis());

        // NPC인지 확인 (ArmorStand 기반 NPC)
        if (!(entity instanceof ArmorStand)) {
            return;
        }

        ArmorStand npc = (ArmorStand) entity;
        String npcId = getNPCId(npc);

        if (npcId == null) {
            return;
        }

        // NPC 퀨스트 핸들러 조회
        NPCQuestHandler handler = getNPCQuestHandler(npcId);
        if (handler == null) {
            return;
        }

        // 플레이어와 NPC의 상호작용 처리
        playerCurrentNPC.put(playerUUID, npcId);
        handleNPCInteraction(player, npcId, handler);
    }

    // ============ NPC Interaction Handling ============

    /**
     * NPC 상호작용 처리
     */
    private void handleNPCInteraction(Player player, String npcId, NPCQuestHandler handler) {
        UUID playerUUID = player.getUniqueId();

        // NPC 대사 표시
        String greeting = handler.getGreeting(playerUUID);
        if (greeting != null && !greeting.isEmpty()) {
            player.  sendMessage("§e" + handler.getNPCName() + ": §f" + greeting);
        }

        // 퀘스트 상태 확인
        Quest availableQuest = handler.getAvailableQuest(playerUUID);
        if (availableQuest != null) {
            // 수락 가능한 퀨스트가 있음
            player.sendMessage("§6이 NPC로부터 수락할 수 있는 퀨스트가 있습니다.");
            player.sendMessage("§7퀨스트: §f" + availableQuest. getName());
            player.sendMessage("§7/accept_quest " + availableQuest. getQuestId() + "§7를 입력하세요.");
            return;
        }

        // 진행 중인 퀨스트 확인
        Quest inProgressQuest = handler.getInProgressQuest(playerUUID);
        if (inProgressQuest != null) {
            player. sendMessage("§6진행 중인 퀨스트가 있습니다.");
            player.sendMessage("§7퀨스트: §f" + inProgressQuest. getName());
            
            // 진행도 표시
            int progress = getQuestProgress(playerUUID, inProgressQuest. getQuestId());
            player.sendMessage("§7진행도: §f" + progress + "%");
            return;
        }

        // 완료 가능한 퀨스트 확인
        Quest completeableQuest = handler.getCompleteableQuest(playerUUID);
        if (completeableQuest != null) {
            player.sendMessage("§6완료할 수 있는 퀨스트가 있습니다!");
            player.sendMessage("§7퀨스트: §f" + completeableQuest.getName());
            player.sendMessage("§7/complete_quest " + completeableQuest. getQuestId() + "§7를 입력하세요.");
            return;
        }

        // 모든 퀨스트 완료
        player.sendMessage("§7이 NPC의 모든 퀨스트를 완료했습니다!");
        String farewell = handler.getFarewell(playerUUID);
        if (farewell != null && !farewell.isEmpty()) {
            player.  sendMessage("§e" + handler.getNPCName() + ": §f" + farewell);
        }
    }

    // ============ Quest Progress Retrieval ============

    /**
     * 퀨스트 진행도 백분율 반환
     */
    private int getQuestProgress(UUID playerUUID, String questId) {
        PlayerQuest pq = questDataManager.getProgressManager().  getPlayerQuest(playerUUID, questId);
        if (pq == null) {
            return 0;
        }

        Quest quest = questDataManager.getQuest(questId);
        if (quest == null || quest.getObjectives().  isEmpty()) {
            return 0;
        }

        // 목표 진행도 평균 계산
        int totalProgress = 0;
        for (QuestObjective objective : quest.getObjectives()) {
            // 실제 구현에서는 각 목표의 진행도를 계산
            totalProgress += 20; // 기본값
        }

        return Math.min(totalProgress, 100);
    }

    // ============ NPC ID Retrieval ============

    /**
     * NPC ID 조회
     */
    private String getNPCId(ArmorStand npc) {
        // ArmorStand의 커스텀 이름에서 NPC ID 추출
        if (npc.getCustomName() == null) {
            return null;
        }

        String customName = npc.getCustomName();
        // 형식: §cNPC_<npcId>
        if (customName.contains("NPC_")) {
            return customName.replace("§c", "").replace("NPC_", "");
        }

        return null;
    }

    // ============ NPC Dialog Handling ============

    /**
     * NPC 대사 표시
     */
    public void sendNPCDialog(Player player, String npcId, String dialog) {
        NPCQuestHandler handler = getNPCQuestHandler(npcId);
        if (handler == null) {
            return;
        }

        String npcName = handler.getNPCName();
        player.sendMessage("§e" + npcName + ": §f" + dialog);
    }

    /**
     * NPC 퀨스트 정보 표시
     */
    public void showNPCQuestInfo(Player player, String npcId, String questId) {
        NPCQuestHandler handler = getNPCQuestHandler(npcId);
        if (handler == null) {
            return;
        }

        Quest quest = questDataManager.getQuest(questId);
        if (quest == null) {
            return;
        }

        player.sendMessage("§6=== 퀨스트 정보 ===§r");
        player.sendMessage("§7퀨스트명: §f" + quest.getName());
        player.sendMessage("§7설명: §f" + quest. getDescription());
        player.sendMessage("§7난이도: §f" + quest.getDifficulty());
        player. sendMessage("§7필요 레벨: §f" + quest.getRequiredLevel());

        if (quest.getRewards() != null && ! quest.getRewards().isEmpty()) {
            player.sendMessage("§7보상:");
            questDataManager.previewRewards(player, quest.getRewards());
        }
    }

    // ============ Quest Acceptance/Completion ============

    /**
     * NPC를 통한 퀨스트 수락
     */
    public boolean acceptQuestFromNPC(Player player, String npcId, String questId) {
        UUID playerUUID = player.getUniqueId();

        NPCQuestHandler handler = getNPCQuestHandler(npcId);
        if (handler == null) {
            return false;
        }

        // 퀨스트 수락 가능 여부 확인
        if (!handler.canAcceptQuest(playerUUID, questId)) {
            player.sendMessage("§c이 퀨스트를 수락할 수 없습니다.");
            return false;
        }

        // 퀨스트 수락
        if (questDataManager.acceptQuest(player, questId)) {
            sendNPCDialog(player, npcId, "좋아!  이 퀨스트를 맡아줄래? ");
            return true;
        }

        return false;
    }

    /**
     * NPC를 통한 퀨스트 완료
     */
    public boolean completeQuestFromNPC(Player player, String npcId, String questId) {
        UUID playerUUID = player.getUniqueId();

        NPCQuestHandler handler = getNPCQuestHandler(npcId);
        if (handler == null) {
            return false;
        }

        // 퀨스트 완료 가능 여부 확인
        if (!handler.canCompleteQuest(playerUUID, questId)) {
            player.sendMessage("§c이 퀨스트를 완료할 수 없습니다.");
            return false;
        }

        // 퀨스트 완료
        if (questDataManager. completeQuest(player, questId)) {
            sendNPCDialog(player, npcId, "정말 잘했어!  감사해!");
            return true;
        }

        return false;
    }

    // ============ NPC Shop/Dialog ============

    /**
     * NPC 상점 열기
     */
    public void openNPCShop(Player player, String npcId) {
        NPCQuestHandler handler = getNPCQuestHandler(npcId);
        if (handler == null) {
            return;
        }

        // 상점 인벤토리 생성 (실제 구현 필요)
        player.sendMessage("§6상점: " + handler.getNPCName());
        player.sendMessage("§7상점 기능은 준비 중입니다.");
    }

    /**
     * NPC 대화 옵션 표시
     */
    public void showNPCOptions(Player player, String npcId) {
        NPCQuestHandler handler = getNPCQuestHandler(npcId);
        if (handler == null) {
            return;
        }

        player.sendMessage("§6=== " + handler.getNPCName() + "와의 대화 ===§r");
        player.sendMessage("§7/npc_shop " + npcId + " - 상점 열기");
        player.sendMessage("§7/npc_quest " + npcId + " - 퀨스트 정보");
        player.sendMessage("§7/npc_talk " + npcId + " - 대화하기");
    }

    // ============ Statistics & Tracking ============

    /**
     * 플레이어가 현재 상호작용 중인 NPC 조회
     */
    public String getPlayerCurrentNPC(UUID playerUUID) {
        return playerCurrentNPC.getOrDefault(playerUUID, null);
    }

    /**
     * 플레이어의 NPC 상호작용 기록
     */
    public long getLastNPCInteractionTime(UUID playerUUID) {
        Long lastTime = playerLastNPCInteraction.get(playerUUID);
        return lastTime != null ? lastTime : 0;
    }

    /**
     * NPC 상호작용 통계
     */
    public Map<String, Object> getNPCStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalNPCs", npcQuestHandlers.size());
        stats. put("activeInteractions", playerCurrentNPC.size());

        Map<String, Integer> npcInteractionCount = new HashMap<>();
        for (String npcId : playerCurrentNPC.values()) {
            npcInteractionCount.put(npcId, npcInteractionCount.getOrDefault(npcId, 0) + 1);
        }
        stats.put("interactionByNPC", npcInteractionCount);

        return stats;
    }

    // ============ Cleanup ============

    /**
     * 플레이어의 NPC 상호작용 기록 제거
     */
    public void clearPlayerNPCInteraction(UUID playerUUID) {
        playerCurrentNPC.remove(playerUUID);
        playerLastNPCInteraction.remove(playerUUID);
    }

    /**
     * 모든 NPC 상호작용 기록 초기화
     */
    public void clearAllNPCInteractions() {
        playerCurrentNPC.clear();
        playerLastNPCInteraction.clear();
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== NPC 리스너 상태 ===§r\n");
        sb.append("§7등록된 NPC: §f").append(npcQuestHandlers.size()).append("\n");
        sb.append("§7활성 상호작용: §f").append(playerCurrentNPC.size()). append("\n");

        return sb.toString();
    }

    // ============ Getters ============

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }

    /**
     * 현재 NPC 상호작용 맵 반환
     */
    public Map<UUID, String> getPlayerCurrentNPCMap() {
        return new HashMap<>(playerCurrentNPC);
    }
}

/**
 * NPC 퀨스트 핸들러 인터페이스
 */
public interface NPCQuestHandler {
    /**
     * NPC 이름 반환
     */
    String getNPCName();

    /**
     * 인사말 반환
     */
    String getGreeting(UUID playerUUID);

    /**
     * 작별인사 반환
     */
    String getFarewell(UUID playerUUID);

    /**
     * 수락 가능한 퀨스트 반환
     */
    Quest getAvailableQuest(UUID playerUUID);

    /**
     * 진행 중인 퀨스트 반환
     */
    Quest getInProgressQuest(UUID playerUUID);

    /**
     * 완료 가능한 퀨스트 반환
     */
    Quest getCompleteableQuest(UUID playerUUID);

    /**
     * 퀨스트 수락 가능 여부 확인
     */
    boolean canAcceptQuest(UUID playerUUID, String questId);

    /**
     * 퀨스트 완료 가능 여부 확인
     */
    boolean canCompleteQuest(UUID playerUUID, String questId);

    /**
     * NPC 상점 상품 조회
     */
    List<String> getShopItems();
}