package com.multiverse.skill.managers;

import com.multiverse.skill.SkillCore;
import com.multiverse. skill.data.enums.SkillBookType;
import com.multiverse.skill.data.models.SkillBook;
import com.multiverse.skill.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit. entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SkillBookManager {

    private final SkillCore plugin;
    private final SkillLearningManager learningManager;
    private final Map<String, SkillBook> skillBooks = new HashMap<>();

    public SkillBookManager(SkillCore plugin, SkillLearningManager learningManager) {
        this. plugin = plugin;
        this. learningManager = learningManager;
        loadSkillBooks();
    }

    /**
     * 모든 스킬 북 로드
     */
    private void loadSkillBooks() {
        plugin.getSkillBookLoader().loadAllSkillBooks(). forEach(book ->
            skillBooks.put(book.getBookId(), book)
        );
        plugin.getLogger().info("✅ " + skillBooks.size() + "개의 스킬 북이 로드되었습니다.");
    }

    /**
     * 스킬 북 생성
     */
    public ItemStack createSkillBook(SkillBook book) {
        ItemStack itemStack = new ItemStack(Material. ENCHANTED_BOOK);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(String.format("§b§l%s", book.getSkillId()));
            
            List<String> lore = new ArrayList<>();
            lore.add("§7스킬 북");
            lore.add(String.format("§e타입: §b%s", book.getType(). getDisplayName()));
            lore.add(String.format("§e스킬 레벨: §b%d", book.getSkillLevel()));
            
            if (book.getRequiredLevel() > 0) {
                lore.add(String.format("§e필요 레벨: §b%d", book.getRequiredLevel()));
            }
            
            if (book.isPermanent()) {
                lore. add("§a[영구 아이템]");
            } else {
                lore.add("§c[일회용]");
            }
            
            if (book.isSoulbound()) {
                lore.add("§c[귀속 아이템]");
            }
            
            lore.add("");
            lore.add("§7우클릭하여 사용하세요");

            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }

        itemStack.setAmount(1);
        return itemStack;
    }

    /**
     * 스킬 북 사용
     */
    public void useSkillBook(Player player, ItemStack book) {
        SkillBook skillBook = getSkillBook(book);

        if (skillBook == null) {
            MessageUtils. sendMessage(player, "§c올바른 스킬 북이 아닙니다.");
            return;
        }

        if (! canUseBook(player, skillBook)) {
            MessageUtils.sendMessage(player, "§c이 스킬 북을 사용할 수 없습니다.");
            return;
        }

        switch (skillBook.getType()) {
            case LEARN -> handleLearn(player, skillBook, book);
            case UPGRADE -> handleUpgrade(player, skillBook, book);
            case RESET -> handleReset(player, skillBook, book);
        }
    }

    /**
     * 스킬 습득 북 사용
     */
    private void handleLearn(Player player, SkillBook skillBook, ItemStack book) {
        learningManager.learnSkill(player, skillBook. getSkillId());

        if (! skillBook.isPermanent()) {
            book.setAmount(book.getAmount() - 1);
            if (book.getAmount() <= 0) {
                player.getInventory().removeItem(book);
            }
        }

        MessageUtils.sendMessage(player, String.format("§a스킬 '§e%s§a'을(를) 습득했습니다! ",
            skillBook.getSkillId()));
    }

    /**
     * 스킬 업그레이드 북 사용
     */
    private void handleUpgrade(Player player, SkillBook skillBook, ItemStack book) {
        learningManager.upgradeSkill(player, skillBook.getSkillId());

        if (!skillBook.isPermanent()) {
            book.setAmount(book.getAmount() - 1);
            if (book.getAmount() <= 0) {
                player. getInventory().removeItem(book);
            }
        }

        MessageUtils.sendMessage(player, "§a스킬이 업그레이드되었습니다!");
    }

    /**
     * 스킬 초기화 북 사용
     */
    private void handleReset(Player player, SkillBook skillBook, ItemStack book) {
        learningManager.resetSkill(player, skillBook. getSkillId(), true);

        if (!skillBook. isPermanent()) {
            book.setAmount(book.getAmount() - 1);
            if (book.getAmount() <= 0) {
                player.getInventory().removeItem(book);
            }
        }

        MessageUtils.sendMessage(player, "§a스킬이 초기화되었습니다!");
    }

    /**
     * 스킬 북 사용 가능 여부 확인
     */
    public boolean canUseBook(Player player, SkillBook book) {
        // 필요 레벨 확인
        if (player.getLevel() < book.getRequiredLevel()) {
            MessageUtils.sendMessage(player, String.format("§c필요 레벨: §e%d", book.getRequiredLevel()));
            return false;
        }

        // 필요 클래스 확인
        if (book.getRequiredClass() != null && !book.getRequiredClass().isEmpty()) {
            // PlayerDataCore와 연동하여 클래스 확인
        }

        // 타입별 확인
        switch (book.getType()) {
            case LEARN -> {
                if (learningManager.hasSkill(player, book.getSkillId())) {
                    MessageUtils.sendMessage(player, "§c이미 이 스킬을 습득했습니다.");
                    return false;
                }
            }
            case UPGRADE -> {
                if (! learningManager.hasSkill(player, book.getSkillId())) {
                    MessageUtils.sendMessage(player, "§c먼저 이 스킬을 습득해야 합니다.");
                    return false;
                }
            }
            case RESET -> {
                if (!learningManager.hasSkill(player, book.getSkillId())) {
                    MessageUtils.sendMessage(player, "§c이 스킬을 습득하지 않았습니다.");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 아이템이 스킬 북인지 확인
     */
    public boolean isSkillBook(ItemStack item) {
        if (item == null || item.getType() != Material. ENCHANTED_BOOK) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getLore() != null && meta.getLore(). contains("§7스킬 북");
    }

    /**
     * 아이템으로부터 스킬 북 정보 추출
     */
    public SkillBook getSkillBook(ItemStack item) {
        if (! isSkillBook(item)) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        String displayName = meta.getDisplayName();
        String skillId = displayName.replaceAll("§[0-9a-f]", ""). replaceAll("§l", "");

        // 스킬 아이디로 스킬 북 조회
        for (SkillBook book : skillBooks.values()) {
            if (book.getSkillId(). equalsIgnoreCase(skillId)) {
                return book;
            }
        }

        return null;
    }

    /**
     * 스킬 북 통계
     */
    public Map<String, Object> getSkillBookStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_books", skillBooks.size());
        
        long learnBooks = skillBooks.values().stream()
            .filter(b -> b.getType() == SkillBookType.LEARN)
            . count();
        stats.put("learn_books", learnBooks);

        long upgradeBooks = skillBooks.values().stream()
            . filter(b -> b.getType() == SkillBookType. UPGRADE)
            .count();
        stats.put("upgrade_books", upgradeBooks);

        return stats;
    }
}