package com.multiverse.quest.listeners;

import com.multiverse. quest.models.*;
import com.multiverse.quest.managers.QuestDataManager;
import com.multiverse.quest.objectives.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit. event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit. Bukkit;
import java.util.*;

/**
 * 퀨스트 목표 리스너
 * 플레이어의 목표 진행도를 감지하고 업데이트합니다.
 */
public class QuestObjectiveListener implements Listener {
    private final QuestDataManager questDataManager;
    private final Map<String, ObjectiveHandler> objectiveHandlers;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     */
    public QuestObjectiveListener(QuestDataManager questDataManager) {
        this.questDataManager = questDataManager;
        this.objectiveHandlers = new HashMap<>();
        registerDefaultHandlers();
    }

    // ============ Handler Registration ============

    /**
     * 기본 목표 핸들러 등록
     */
    private void registerDefaultHandlers() {
        registerObjectiveHandler("KILL_MOBS", new KillMobsHandler());
        registerObjectiveHandler("COLLECT_ITEMS", new CollectItemsHandler());
        registerObjectiveHandler("EXPLORE_REGION", new ExploreRegionHandler());
        registerObjectiveHandler("CUSTOM", new CustomObjectiveHandler());
    }

    /**
     * 목표 핸들러 등록
     */
    public void registerObjectiveHandler(String objectiveType, ObjectiveHandler handler) {
        if (objectiveType != null && handler != null) {
            objectiveHandlers.put(objectiveType, handler);
            Bukkit.getLogger().info("목표 핸들러 등록: " + objectiveType);
        }
    }

    /**
     * 목표 핸들러 조회
     */
    public ObjectiveHandler getObjectiveHandler(String objectiveType) {
        return objectiveHandlers.getOrDefault(objectiveType, null);
    }

    // ============ Mob Kill Events ============

    /**
     * 몹 처치 이벤트 감지
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity(). getKiller() == null) {
            return;
        }

        Player player = event.getEntity().getKiller();
        UUID playerUUID = player.getUniqueId();
        EntityType entityType = event.getEntityType();

        // 플레이어의 진행 중인 퀨스트 확인
        List<PlayerQuest> inProgressQuests = questDataManager. getPlayerInProgressQuests(playerUUID);

        for (PlayerQuest pq : inProgressQuests) {
            Quest quest = questDataManager.getQuest(pq.getQuestId());
            if (quest == null) {
                continue;
            }

            // 목표 중 몹 처치 목표 찾기
            for (QuestObjective objective : quest.getObjectives()) {
                if (!"KILL_MOBS".equals(objective.getObjectiveType())) {
                    continue;
                }

                // 해당 몹 타입인지 확인
                String targetMob = objective.getDescription();
                if (! targetMob.equalsIgnoreCase(entityType.name())) {
                    continue;
                }

                // 진행도 업데이트
                ObjectiveHandler handler = getObjectiveHandler("KILL_MOBS");
                if (handler instanceof KillMobsHandler) {
                    KillMobsHandler killHandler = (KillMobsHandler) handler;
                    killHandler.initialize(objective);

                    if (killHandler.updateProgress(player, playerUUID, 1)) {
                        questDataManager.notifyQuestProgress(
                            player,
                            quest. getName(),
                            killHandler.getProgress(playerUUID),
                            killHandler.getRequiredCount()
                        );

                        // 목표 완료 확인
                        if (killHandler.isCompleted(playerUUID)) {
                            questDataManager.notifyObjectiveCompleted(player, objective. getDescription());
                        }
                    }
                }
            }
        }
    }

    // ============ Item Collection Events ============

    /**
     * 아이템 수집 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPickupItem(org.bukkit.event.player. PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        ItemStack item = event.getItem(). getItemStack();

        // 플레이어의 진행 중인 퀨스트 확인
        List<PlayerQuest> inProgressQuests = questDataManager.getPlayerInProgressQuests(playerUUID);

        for (PlayerQuest pq : inProgressQuests) {
            Quest quest = questDataManager.getQuest(pq. getQuestId());
            if (quest == null) {
                continue;
            }

            // 목표 중 아이템 수집 목표 찾기
            for (QuestObjective objective : quest.getObjectives()) {
                if (!"COLLECT_ITEMS".equals(objective.getObjectiveType())) {
                    continue;
                }

                // 해당 아이템인지 확인
                String targetItem = objective.getDescription();
                if (!targetItem.equalsIgnoreCase(item.getType().name())) {
                    continue;
                }

                // 진행도 업데이트
                ObjectiveHandler handler = getObjectiveHandler("COLLECT_ITEMS");
                if (handler instanceof CollectItemsHandler) {
                    CollectItemsHandler collectHandler = (CollectItemsHandler) handler;
                    collectHandler.initialize(objective);

                    if (collectHandler.updateProgress(player, playerUUID, item. getAmount())) {
                        questDataManager.notifyQuestProgress(
                            player,
                            quest.getName(),
                            collectHandler.getProgress(playerUUID),
                            collectHandler.getRequiredCount()
                        );

                        // 아이템 소비
                        int consumed = Math.min(item.getAmount(), 
                            collectHandler.getRequiredCount() - collectHandler.getProgress(playerUUID));
                        
                        if (collectHandler.isConsumeItem()) {
                            event.getItem().remove();
                        }

                        // 목표 완료 확인
                        if (collectHandler.isCompleted(playerUUID)) {
                            questDataManager.notifyObjectiveCompleted(player, objective.getDescription());
                        }
                    }
                }
            }
        }
    }

    // ============ Region Exploration Events ============

    /**
     * 플레이어 이동 이벤트 감지 (지역 탐험)
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        // 같은 블록 내에서의 이동은 무시
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 플레이어의 진행 중인 퀨스트 확인
        List<PlayerQuest> inProgressQuests = questDataManager.getPlayerInProgressQuests(playerUUID);

        for (PlayerQuest pq : inProgressQuests) {
            Quest quest = questDataManager.getQuest(pq. getQuestId());
            if (quest == null) {
                continue;
            }

            // 목표 중 지역 탐험 목표 찾기
            for (QuestObjective objective : quest.getObjectives()) {
                if (!"EXPLORE_REGION".equals(objective.getObjectiveType())) {
                    continue;
                }

                // 진행도 업데이트
                ObjectiveHandler handler = getObjectiveHandler("EXPLORE_REGION");
                if (handler instanceof ExploreRegionHandler) {
                    ExploreRegionHandler exploreHandler = (ExploreRegionHandler) handler;
                    exploreHandler.initialize(objective);

                    if (exploreHandler.updateProgress(player, playerUUID, 1)) {
                        questDataManager.notifyQuestProgress(
                            player,
                            quest.getName(),
                            exploreHandler.getProgress(playerUUID),
                            exploreHandler.getRequiredPoints()
                        );

                        // 목표 완료 확인
                        if (exploreHandler. isCompleted(playerUUID)) {
                            questDataManager. notifyObjectiveCompleted(player, objective.getDescription());
                        }
                    }
                }
            }
        }
    }

    // ============ Block Break Events ============

    /**
     * 블록 파괴 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(org. bukkit.event.block.BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 플레이어의 진행 중인 퀨스트 확인
        List<PlayerQuest> inProgressQuests = questDataManager. getPlayerInProgressQuests(playerUUID);

        for (PlayerQuest pq : inProgressQuests) {
            Quest quest = questDataManager.getQuest(pq.getQuestId());
            if (quest == null) {
                continue;
            }

            // 커스텀 목표에서 블록 파괴 이벤트 처리
            for (QuestObjective objective : quest.getObjectives()) {
                ObjectiveHandler handler = getObjectiveHandler(objective.getObjectiveType());
                if (handler != null) {
                    handler. initialize(objective);

                    if (handler.updateProgress(player, playerUUID, 1)) {
                        questDataManager.notifyQuestProgress(
                            player,
                            quest.getName(),
                            handler.getProgress(playerUUID),
                            objective.getRequired()
                        );
                    }
                }
            }
        }
    }

    // ============ Custom Event Handler ============

    /**
     * 커스텀 목표 이벤트 처리
     */
    public void handleCustomObjective(Player player, String objectiveType, int amount) {
        UUID playerUUID = player.getUniqueId();

        // 플레이어의 진행 중인 퀨스트 확인
        List<PlayerQuest> inProgressQuests = questDataManager. getPlayerInProgressQuests(playerUUID);

        for (PlayerQuest pq : inProgressQuests) {
            Quest quest = questDataManager.getQuest(pq.getQuestId());
            if (quest == null) {
                continue;
            }

            // 목표 중 해당 타입의 커스텀 목표 찾기
            for (QuestObjective objective : quest.getObjectives()) {
                if (! objectiveType.equals(objective.getObjectiveType())) {
                    continue;
                }

                ObjectiveHandler handler = getObjectiveHandler("CUSTOM");
                if (handler instanceof CustomObjectiveHandler) {
                    CustomObjectiveHandler customHandler = (CustomObjectiveHandler) handler;
                    customHandler.initialize(objective);

                    if (customHandler.updateProgress(player, playerUUID, amount)) {
                        questDataManager.notifyQuestProgress(
                            player,
                            quest.getName(),
                            customHandler.getProgress(playerUUID),
                            customHandler.getRequiredValue()
                        );

                        if (customHandler.isCompleted(playerUUID)) {
                            questDataManager.notifyObjectiveCompleted(player, objective.getDescription());
                        }
                    }
                }
            }
        }
    }

    // ============ Utility Methods ============

    /**
     * 모든 진행 중인 목표 업데이트
     */
    public void updateAllObjectives(Player player) {
        UUID playerUUID = player.getUniqueId();
        List<PlayerQuest> inProgressQuests = questDataManager. getPlayerInProgressQuests(playerUUID);

        for (PlayerQuest pq : inProgressQuests) {
            Quest quest = questDataManager.getQuest(pq.getQuestId());
            if (quest == null) {
                continue;
            }

            for (QuestObjective objective : quest.getObjectives()) {
                ObjectiveHandler handler = getObjectiveHandler(objective.getObjectiveType());
                if (handler != null) {
                    handler.initialize(objective);
                    handler.checkConditions(player);
                }
            }
        }
    }

    /**
     * 플레이어의 목표 진행도 초기화
     */
    public void resetPlayerObjectives(UUID playerUUID) {
        List<PlayerQuest> inProgressQuests = questDataManager.getPlayerInProgressQuests(playerUUID);

        for (PlayerQuest pq : inProgressQuests) {
            Quest quest = questDataManager.getQuest(pq.getQuestId());
            if (quest == null) {
                continue;
            }

            for (QuestObjective objective : quest.getObjectives()) {
                ObjectiveHandler handler = getObjectiveHandler(objective.getObjectiveType());
                if (handler != null) {
                    handler.resetProgress(playerUUID);
                }
            }
        }
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 퀨스트 목표 리스너 상태 ===§r\n");
        sb.append("§7등록된 핸들러: §f").append(objectiveHandlers.size()).append("\n");
        
        for (String type : objectiveHandlers.keySet()) {
            sb.append("§7  - §f").append(type).append("\n");
        }

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
     * 모든 핸들러 반환
     */
    public Map<String, ObjectiveHandler> getAllHandlers() {
        return new HashMap<>(objectiveHandlers);
    }
}