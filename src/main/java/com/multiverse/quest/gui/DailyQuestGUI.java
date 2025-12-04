package com.multiverse.quest.gui;

import com.multiverse.  quest.models.*;
import com.multiverse.quest.managers.QuestDataManager;
import org.bukkit.  Bukkit;
import org.bukkit.Material;
import org.  bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org. bukkit.  inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

/**
 * 일일 퀨스트 GUI
 * 플레이어가 일일 퀨스트를 조회하고 수락합니다.
 */
public class DailyQuestGUI {
    private final QuestDataManager questDataManager;
    private final Player player;
    private final UUID playerUUID;
    private Inventory inventory;
    private static final int INVENTORY_SIZE = 45;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     * @param player 플레이어
     */
    public DailyQuestGUI(QuestDataManager questDataManager, Player player) {
        this.questDataManager = questDataManager;
        this.player = player;
        this.playerUUID = player.getUniqueId();
    }

    // ============ GUI Creation ============

    /**
     * GUI 생성 및 표시
     */
    public void open() {
        inventory = Bukkit.createInventory(null, INVENTORY_SIZE, "§6【 일일 퀨스트 】");

        // 제목
        ItemStack titleItem = createTitleItem();
        inventory.setItem(4, titleItem);

        // 일일 퀨스트 표시
        displayDailyQuests();

        // 하단 정보
        inventory.setItem(36, createResetTimeItem());
        inventory.setItem(40, createStatisticsItem());

        player.openInventory(inventory);
    }

    /**
     * 일일 퀨스트 표시
     */
    private void displayDailyQuests() {
        List<Quest> dailyQuests = questDataManager.getQuestManager().getQuestsByType(QuestType.DAILY);

        if (dailyQuests. isEmpty()) {
            ItemStack noQuest = createNoQuestItem();
            inventory.setItem(13, noQuest);
            return;
        }

        int slot = 9;
        for (Quest quest : dailyQuests) {
            if (slot >= 36) break;

            boolean canAccept = questDataManager.canAcceptDailyQuest(playerUUID, quest. getQuestId());
            ItemStack questItem = createDailyQuestItem(quest, canAccept);
            inventory.setItem(slot, questItem);
            slot++;
        }
    }

    /**
     * 제목 아이템 생성
     */
    private ItemStack createTitleItem() {
        ItemStack item = new ItemStack(Material.  SUNRISE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§f일일 퀨스트");

            List<String> lore = new ArrayList<>();
            lore.add("§7매일 자정에 초기화됩니다.");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 일일 퀨스트 아이템 생성
     */
    private ItemStack createDailyQuestItem(Quest quest, boolean canAccept) {
        ItemStack item = new ItemStack(Material.  ORANGE_CONCRETE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            String displayName = canAccept ? "§f" + quest.getName() : "§7" + quest.getName() + " (완료됨)";
            meta.setDisplayName(displayName);

            List<String> lore = new ArrayList<>();
            lore.add("§7설명: §f" + quest.getDescription());
            lore.add("§7난이도: " + formatDifficulty(quest.getDifficulty()));
            lore.add("§7필요 레벨: §f" + quest.getRequiredLevel());
            lore.add(" ");

            // 보상 미리보기
            if (quest.getRewards() != null && !quest.getRewards().isEmpty()) {
                lore.add("§7보상:");
                for (QuestReward reward : quest.getRewards()) {
                    lore.add("  §f- " + formatReward(reward));
                }
            }

            lore.add(" ");

            if (canAccept) {
                lore.add("§a클릭하여 수락");
            } else {
                lore.add("§7이미 오늘 완료했습니다.");
                lore.add("§7내일 다시 시도하세요.");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 리셋 시간 아이템 생성
     */
    private ItemStack createResetTimeItem() {
        ItemStack item = new ItemStack(Material. CLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6다음 리셋 시간");

            List<String> lore = new ArrayList<>();
            long timeUntilReset = questDataManager.getDailyWeeklyManager().getTimeUntilDailyReset();
            lore.add("§7" + formatTime(timeUntilReset) + " 후");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 통계 아이템 생성
     */
    private ItemStack createStatisticsItem() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6통계");

            List<String> lore = new ArrayList<>();
            List<Quest> dailyQuests = questDataManager.getQuestManager().getQuestsByType(QuestType.DAILY);
            int totalDaily = dailyQuests.size();
            int completedToday = (int) dailyQuests.stream()
                .filter(q -> ! questDataManager.canAcceptDailyQuest(playerUUID, q.getQuestId()))
                .count();

            lore.add("§7일일 퀨스트: §f" + totalDaily);
            lore.add("§7오늘 완료: §a" + completedToday);
            lore.add("§7남은 퀨스트: §e" + (totalDaily - completedToday));

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 퀨스트 없음 아이템
     */
    private ItemStack createNoQuestItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c일일 퀨스트 없음");

            List<String> lore = new ArrayList<>();
            lore.add("§7사용 가능한 일일 퀨스트가 없습니다.");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 보상 포맷
     */
    private String formatReward(QuestReward reward) {
        return switch (reward. getType(). toLowerCase()) {
            case "experience" -> "경험치 " + reward.getAmount();
            case "money" -> "$" + reward.getAmount();
            case "item" -> reward.getItemName() + " x" + reward.getAmount();
            case "command" -> "명령어 실행";
            default -> reward.getType();
        };
    }

    /**
     * 난이도 포맷
     */
    private String formatDifficulty(String difficulty) {
        if (difficulty == null) return "§7보통";

        return switch (difficulty.toLowerCase()) {
            case "easy" -> "§a쉬움";
            case "normal" -> "§7보통";
            case "hard" -> "§c어려움";
            case "very_hard" -> "§4매우 어려움";
            default -> "§7" + difficulty;
        };
    }

    /**
     * 시간 포맷
     */
    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%d시간 %d분 %d초", hours, minutes, secs);
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