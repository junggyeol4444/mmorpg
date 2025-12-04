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
 * 퀨스트 저널 GUI
 * 플레이어가 수락한 퀨스트를 관리하는 인벤토리 GUI입니다.
 */
public class QuestJournalGUI {
    private final QuestDataManager questDataManager;
    private final Player player;
    private final UUID playerUUID;
    private Inventory inventory;
    private static final int INVENTORY_SIZE = 54;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     * @param player 플레이어
     */
    public QuestJournalGUI(QuestDataManager questDataManager, Player player) {
        this.questDataManager = questDataManager;
        this.player = player;
        this.playerUUID = player.getUniqueId();
    }

    // ============ GUI Creation ============

    /**
     * GUI 생성 및 표시
     */
    public void open() {
        inventory = Bukkit.createInventory(null, INVENTORY_SIZE, "§6【 퀨스트 저널 】");
        
        List<PlayerQuest> playerQuests = questDataManager.getPlayerInProgressQuests(playerUUID);
        
        int slot = 0;
        for (PlayerQuest pq : playerQuests) {
            if (slot >= INVENTORY_SIZE) break;
            
            Quest quest = questDataManager.getQuest(pq.getQuestId());
            if (quest != null) {
                ItemStack questItem = createQuestItem(quest, pq);
                inventory.setItem(slot, questItem);
                slot++;
            }
        }

        // 구분선
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, createSeparatorItem());
        }

        // 페이지 정보
        inventory.setItem(49, createInfoItem());

        player.openInventory(inventory);
    }

    /**
     * 퀨스트 아이템 생성
     */
    private ItemStack createQuestItem(Quest quest, PlayerQuest pq) {
        ItemStack item = new ItemStack(Material. BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§f" + quest.getName());

            List<String> lore = new ArrayList<>();
            lore.add("§7설명: §f" + quest.getDescription());
            lore.add("§7난이도: " + formatDifficulty(quest.getDifficulty()));
            lore.add("§7필요 레벨: §f" + quest.getRequiredLevel());
            lore.add(" ");
            
            // 목표 표시
            if (quest.getObjectives() != null && !quest.getObjectives().isEmpty()) {
                lore.add("§7목표:");
                for (QuestObjective obj : quest.getObjectives()) {
                    lore.add("  §f- " + obj.getDescription());
                }
            }
            
            lore.add(" ");
            lore.add("§e클릭하여 상세 정보 보기");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 정보 아이템 생성
     */
    private ItemStack createInfoItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6정보");
            
            List<String> lore = new ArrayList<>();
            List<PlayerQuest> inProgress = questDataManager.getPlayerInProgressQuests(playerUUID);
            List<PlayerQuest> completed = questDataManager.getPlayerCompletedQuests(playerUUID);
            
            lore.add("§7진행 중: §f" + inProgress.size());
            lore.add("§7완료: §f" + completed.size());

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 구분선 아이템 생성
     */
    private ItemStack createSeparatorItem() {
        ItemStack item = new ItemStack(Material. BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§r");
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
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }
}