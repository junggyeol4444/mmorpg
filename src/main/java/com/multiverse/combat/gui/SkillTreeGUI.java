package com.multiverse.combat.gui;

import org.bukkit. Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org. bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit. event.inventory.InventoryClickEvent;
import com.multiverse.combat.CombatCore;
import com.multiverse.  combat.models.  Skill;
import java.util.ArrayList;
import java.util.List;

/**
 * 스킬 트리 GUI 클래스
 * 플레이어의 스킬 학습 및 업그레이드 인터페이스를 제공합니다.
 */
public class SkillTreeGUI {
    
    private final CombatCore plugin;
    private static final int GUI_SIZE = 54;  // 6행 9열
    
    /**
     * SkillTreeGUI 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public SkillTreeGUI(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 스킬 트리 GUI 열기
     * @param player 플레이어
     */
    public void openSkillTree(Player player) {
        Inventory inv = Bukkit.createInventory(player, GUI_SIZE, "§6§l스킬 트리");
        
        List<Skill> skills = plugin. getSkillManager().getAllSkills();
        int slot = 0;
        
        for (Skill skill : skills) {
            if (slot >= GUI_SIZE) break;
            
            ItemStack item = createSkillItem(player, skill);
            inv.setItem(slot, item);
            slot++;
        }
        
        // 돌아가기 버튼
        ItemStack backButton = new ItemStack(Material.  RED_WOOL);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§c닫기");
        backButton.setItemMeta(backMeta);
        inv.setItem(GUI_SIZE - 1, backButton);
        
        player.openInventory(inv);
    }
    
    /**
     * 스킬 아이템 생성
     * @param player 플레이어
     * @param skill 스킬
     * @return 스킬 아이템
     */
    private ItemStack createSkillItem(Player player, Skill skill) {
        boolean hasSkill = plugin.getSkillManager().hasSkill(player, skill.getSkillId());
        
        ItemStack item = new ItemStack(hasSkill ? Material.ENCHANTED_BOOK : Material. BOOK);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(skill.getName());
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§f타입: §6" + skill.getType(). getDisplayName());
        lore.add("§f카테고리: §6" + skill.getCategory().getDisplayName());
        
        if (hasSkill) {
            int level = plugin.getSkillManager().getSkillLevel(player, skill.getSkillId());
            lore.add("§f레벨: §6" + level + "§f/§6" + skill.getMaxLevel());
            
            if (level < skill.getMaxLevel()) {
                lore.add("§e[클릭] 업그레이드");
            } else {
                lore.add("§c최대 레벨");
            }
        } else {
            lore.add("§f필요 레벨: §6" + skill.getRequiredLevel());
            
            if (skill.getRequiredSkill() != null && ! skill.getRequiredSkill(). isEmpty()) {
                Skill requiredSkill = plugin. getSkillManager().getSkill(skill.getRequiredSkill());
                if (requiredSkill != null) {
                    lore.add("§f선행 스킬: §6" + requiredSkill.getName());
                }
            }
            
            if (plugin.getSkillManager().canLearnSkill(player, skill)) {
                lore.add("§a[클릭] 배우기");
            } else {
                lore.add("§c조건 미충족");
            }
        }
        
        lore.add("");
        lore.add("§7" + skill.getDescription());
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * GUI 클릭 이벤트 처리
     * @param event 인벤토리 클릭 이벤트
     */
    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getInventory().  getTitle().equals("§6§l스킬 트리")) return;
        
        event.setCancelled(true);
        
        Player player = (Player) event.  getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        // 닫기 버튼
        if (clicked.getType() == Material.RED_WOOL) {
            player.closeInventory();
            return;
        }
        
        // 스킬 아이템
        if (clicked.  getType() == Material.BOOK || clicked.getType() == Material. ENCHANTED_BOOK) {
            String skillName = clicked.getItemMeta(). getDisplayName();
            
            // 스킬 ID 찾기
            for (Skill skill : plugin.getSkillManager().getAllSkills()) {
                if (skill.getName().equals(skillName)) {
                    boolean hasSkill = plugin.getSkillManager().hasSkill(player, skill.getSkillId());
                    
                    if (hasSkill) {
                        // 업그레이드
                        if (plugin.getSkillManager().upgradeSkill(player, skill. getSkillId())) {
                            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                                "§a" + skill.getName() + "§a이(가) 업그레이드되었습니다!");
                            openSkillTree(player);
                        } else {
                            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                                "§c업그레이드할 수 없습니다.");
                        }
                    } else {
                        // 배우기
                        if (plugin.getSkillManager().learnSkill(player, skill. getSkillId())) {
                            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                                "§a" + skill.getName() + "§a을(를) 배웠습니다!");
                            openSkillTree(player);
                        } else {
                            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                                "§c배울 수 없습니다.");
                        }
                    }
                    break;
                }
            }
        }
    }
}