package com.multiverse.quest.gui;

import com.multiverse. quest.models.*;
import com.multiverse.quest.managers.QuestDataManager;
import org.bukkit. Bukkit;
import org.bukkit.Material;
import org.bukkit. entity.Player;
import org.bukkit.inventory.Inventory;
import org. bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

/**
 * 퀨스트 추적 GUI
 * 현재 추적 중인 퀨스트의 진행도를 표시합니다.
 */
public class QuestTrackerGUI {
    private final QuestDataManager questDataManager;
    private final Player player;
    private final UUID playerUUID;
    private Inventory inventory;
    private String trackedQuestId;
    private static final int INVENTORY_SIZE = 27;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     * @param player 플레이어
     */
    public QuestTrackerGUI(QuestDataManager questDataManager, Player player) {
        this.questDataManager = questDataManager;
        this.player = player;
        this.playerUUID = player.getUniqueId();
    }

    // ============ GUI Creation ============

    /**
     * GUI 생성 및 표시
     */
    public void open() {
        inventory = Bukkit.createInventory(null, INVENTORY_SIZE, "§6【 퀨스트 추적기 】");
        
        // 추적 중인 퀨스트 조회
        trackedQuestId = questDataManager.getTrackerManager().getTrackedQuestId(playerUUID);
        
        if (trackedQuestId == null || trackedQuestId.isEmpty()) {
            // 추적 중인 퀨스트 없음
            ItemStack noQuest = createNoQuestItem();
            inventory.setItem(13, noQuest);
        } else {
            // 추적 중인 퀨스트 정보 표시
            Quest quest = questDataManager.getQuest(trackedQuestId);
            if (quest != null) {
                displayQuestProgress(quest);
            }
        }

        player.openInventory(inventory);
    }

    /**
     * 퀨스트 진행도 표시
     */
    private void displayQuestProgress(Quest quest) {
        // 제목
        ItemStack titleItem = createTitleItem(quest);
        inventory.setItem(4, titleItem);

        // 목표들 표시
        if (quest.getObjectives() != null && !quest.getObjectives().isEmpty()) {
            int slot = 9;
            for (QuestObjective objective : quest.getObjectives()) {
                if (slot >= 18) break;
                
                ItemStack objectiveItem = createObjectiveItem(objective);
                inventory. setItem(slot, objectiveItem);
                slot++;
            }
        }

        // 하단 버튼들
        inventory.setItem(18, createUntrackButton());
        inventory.setItem(22, createCompleteButton(quest));
        inventory.setItem(26, createAbandonButton());
    }

    /**
     * 제목 아이템 생성
     */
    private ItemStack createTitleItem(Quest quest) {
        ItemStack item = new ItemStack(Material. DIAMOND);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§f" + quest.getName());

            List<String> lore = new ArrayList<>();
            lore.add("§7난이도: " + formatDifficulty(quest.getDifficulty()));
            lore.add("§7필요 레벨: §f" + quest.getRequiredLevel());

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 목표 아이템 생성
     */
    private ItemStack createObjectiveItem(QuestObjective objective) {
        ItemStack item = new ItemStack(Material. PAPER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§7목표");

            List<String> lore = new ArrayList<>();
            lore. add("§f" + objective.getDescription());
            
            if (objective.getCurrentProgress() != null) {
                lore.add("§7진행도: §f" + objective.getCurrentProgress() + 
                         "/" + objective.getTargetProgress());
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 추적 중지 버튼 생성
     */
    private ItemStack createUntrackButton() {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c추적 중지");

            List<String> lore = new ArrayList<>();
            lore. add("§7클릭하여 추적을 중지합니다.");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 완료 버튼 생성
     */
    private ItemStack createCompleteButton(Quest quest) {
        ItemStack item = new ItemStack(Material.GREEN_DYE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§a완료 확인");

            List<String> lore = new ArrayList<>();
            lore.add("§7클릭하여 퀨스트를 완료합니다.");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 포기 버튼 생성
     */
    private ItemStack createAbandonButton() {
        ItemStack item = new ItemStack(Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta. setDisplayName("§7포기");

            List<String> lore = new ArrayList<>();
            lore.add("§7클릭하여 퀨스트를 포기합니다.");

            meta. setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 추적 중인 퀨스트 없음 아이템
     */
    private ItemStack createNoQuestItem() {
        ItemStack item = new ItemStack(Material. BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c추적 중인 퀨스트 없음");

            List<String> lore = new ArrayList<>();
            lore. add("§7/quest track <ID>로 퀨스트를 추적하세요.");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 난이도 포맷
     */
    private String formatDifficulty(String difficulty) {
        if (difficulty == null) return "§7보통";

        return switch (difficulty. toLowerCase()) {
            case "easy" -> "§a쉬움";
            case "normal" -> "§7보통";
            case "hard" -> "§c어려움";
            case "very_hard" -> "§4매우 어려움";
            default -> "§7" + difficulty;
        };
    }

    // ============ Getters ============

    /**
     * 인벤토리 반환
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * 플레이어 반환
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 플레이어 UUID 반환
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * 추적 중인 퀨스트 ID 반환
     */
    public String getTrackedQuestId() {
        return trackedQuestId;
    }

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }
}