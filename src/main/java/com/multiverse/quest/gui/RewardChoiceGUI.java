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
 * 보상 선택 GUI
 * 플레이어가 퀨스트 완료 시 여러 보상 중 하나를 선택합니다.
 */
public class RewardChoiceGUI {
    private final QuestDataManager questDataManager;
    private final Player player;
    private final UUID playerUUID;
    private final String questId;
    private final List<QuestReward> rewards;
    private Inventory inventory;
    private static final int INVENTORY_SIZE = 27;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     * @param player 플레이어
     * @param questId 퀨스트 ID
     * @param rewards 선택 가능한 보상 목록
     */
    public RewardChoiceGUI(QuestDataManager questDataManager, Player player, String questId, List<QuestReward> rewards) {
        this.questDataManager = questDataManager;
        this.player = player;
        this.playerUUID = player.getUniqueId();
        this.questId = questId;
        this. rewards = rewards;
    }

    // ============ GUI Creation ============

    /**
     * GUI 생성 및 표시
     */
    public void open() {
        inventory = Bukkit.createInventory(null, INVENTORY_SIZE, "§6【 보상 선택 】");

        if (rewards == null || rewards.isEmpty()) {
            ItemStack noReward = createNoRewardItem();
            inventory.setItem(13, noReward);
        } else {
            displayRewards();
        }

        player.openInventory(inventory);
    }

    /**
     * 보상 표시
     */
    private void displayRewards() {
        // 제목
        ItemStack titleItem = createTitleItem();
        inventory. setItem(4, titleItem);

        // 보상 아이템들
        int slot = 9;
        for (int i = 0; i < rewards.size(); i++) {
            if (slot >= 18) break;

            QuestReward reward = rewards.get(i);
            ItemStack rewardItem = createRewardItem(reward, i);
            inventory.setItem(slot, rewardItem);
            slot++;
        }

        // 취소 버튼
        inventory.setItem(22, createCancelButton());
    }

    /**
     * 제목 아이템 생성
     */
    private ItemStack createTitleItem() {
        ItemStack item = new ItemStack(Material. CHEST);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§f보상을 선택하세요");

            List<String> lore = new ArrayList<>();
            lore. add("§7클릭하여 선택합니다.");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 보상 아이템 생성
     */
    private ItemStack createRewardItem(QuestReward reward, int index) {
        Material material = getMaterialForRewardType(reward.getType());
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6보상 " + (index + 1));

            List<String> lore = new ArrayList<>();
            lore. add("§7타입: §f" + reward.getType());

            // 보상 타입별 설명
            switch (reward.getType(). toLowerCase()) {
                case "experience":
                    lore.add("§7경험치: §f" + reward.getAmount());
                    break;
                case "money":
                    lore.add("§7돈: §f$" + reward.getAmount());
                    break;
                case "item":
                    lore.add("§7아이템: §f" + reward.getItemName());
                    lore.add("§7개수: §f" + reward.getAmount());
                    break;
                case "command":
                    lore.add("§7명령어 실행");
                    break;
                default:
                    lore.add("§7" + reward.getType());
                    break;
            }

            lore.add(" ");
            lore.add("§e클릭하여 선택");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 취소 버튼 생성
     */
    private ItemStack createCancelButton() {
        ItemStack item = new ItemStack(Material. BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c취소");

            List<String> lore = new ArrayList<>();
            lore.add("§7클릭하여 보상 선택을 취소합니다.");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 보상 없음 아이템
     */
    private ItemStack createNoRewardItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c보상 없음");

            List<String> lore = new ArrayList<>();
            lore.add("§7선택 가능한 보상이 없습니다.");

            meta. setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 보상 타입에 따른 머티리얼 반환
     */
    private Material getMaterialForRewardType(String type) {
        return switch (type.toLowerCase()) {
            case "experience" -> Material. EXPERIENCE_BOTTLE;
            case "money" -> Material. GOLD_INGOT;
            case "item" -> Material.CHEST;
            case "command" -> Material.COMMAND_BLOCK;
            default -> Material.PAPER;
        };
    }

    // ============ Reward Selection ============

    /**
     * 보상 선택 처리
     */
    public void selectReward(int index) {
        if (index < 0 || index >= rewards.size()) {
            player.sendMessage("§c유효하지 않은 보상입니다.");
            return;
        }

        QuestReward selectedReward = rewards.get(index);
        
        // 보상 지급
        questDataManager.getRewardManager().grantReward(player, selectedReward);
        
        player.sendMessage("§a보상을 받았습니다!");
        player.closeInventory();
    }

    /**
     * 보상 선택 취소
     */
    public void cancel() {
        player.sendMessage("§7보상 선택이 취소되었습니다.");
        player.closeInventory();
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
     * 퀨스트 ID 반환
     */
    public String getQuestId() {
        return questId;
    }

    /**
     * 보상 목록 반환
     */
    public List<QuestReward> getRewards() {
        return new ArrayList<>(rewards);
    }

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }
}