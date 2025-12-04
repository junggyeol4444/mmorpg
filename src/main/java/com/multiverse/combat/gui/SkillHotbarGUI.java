package com.multiverse.combat. gui;

import org.bukkit. Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org. bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit. Material;
import org.bukkit. event.inventory.InventoryClickEvent;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.  models.  Skill;
import java.util.ArrayList;
import java.util.List;

/**
 * 스킬 핫바 GUI 클래스
 * 플레이어의 스킬 핫바 설정 인터페이스를 제공합니다.
 */
public class SkillHotbarGUI {
    
    private final CombatCore plugin;
    private static final int GUI_SIZE = 27;  // 3행 9열
    
    /**
     * SkillHotbarGUI 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public SkillHotbarGUI(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 스킬 핫바 GUI 열기
     * @param player 플레이어
     */
    public void openHotbar(Player player) {
        Inventory inv = Bukkit.createInventory(player, GUI_SIZE, "§6§l스킬 핫바 설정");
        
        // 현재 핫바 표시
        String[] hotbar = plugin.getSkillManager().getHotbar(player);
        
        for (int i = 0; i < 5; i++) {
            String skillId = hotbar[i];
            
            if (skillId != null) {
                Skill skill = plugin.  getSkillManager().getSkill(skillId);
                if (skill != null) {
                    ItemStack item = createHotbarSkillItem(skill, i + 1);
                    inv. setItem(i * 2, item);
                }
            } else {
                ItemStack emptySlot = new ItemStack(Material. GRAY_STAINED_GLASS_PANE);
                ItemMeta meta = emptySlot.getItemMeta();
                meta.setDisplayName("§7슬롯 " + (i + 1) + " (비어있음)");
                emptySlot.setItemMeta(meta);
                inv.setItem(i * 2, emptySlot);
            }
        }
        
        // 돌아가기 버튼
        ItemStack backButton = new ItemStack(Material.  RED_WOOL);
        ItemMeta backMeta = backButton. getItemMeta();
        backMeta.setDisplayName("§c닫기");
        backButton. setItemMeta(backMeta);
        inv.setItem(GUI_SIZE - 1, backButton);
        
        player. openInventory(inv);
    }
    
    /**
     * 핫바 스킬 아이템 생성
     * @param skill 스킬
     * @param slot 슬롯 번호
     * @return 스킬 아이템
     */
    private ItemStack createHotbarSkillItem(Skill skill, int slot) {
        ItemStack item = new ItemStack(Material.  LIME_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§e" + slot + "번 슬롯: " + skill.getName());
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§f타입: §6" + skill.getType(). getDisplayName());
        lore.add("§f비용: §6" + skill.getBaseCost() + " " + skill.getCostType().getDisplayName());
        lore.add("§f쿨다운: §6" + (skill.getBaseCooldown() / 1000. 0) + "초");
        lore.add("");
        lore.add("§7" + skill.getDescription());
        lore.add("");
        lore.add("§e[우클릭] 변경");
        
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
        if (!event.getInventory(). getTitle().equals("§6§l스킬 핫바 설정")) return;
        
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked == null || clicked.  getType() == Material.AIR) return;
        
        // 닫기 버튼
        if (clicked.getType() == Material.RED_WOOL) {
            player.closeInventory();
            return;
        }
        
        // 스킬 슬롯
        if (clicked.getType() == Material.LIME_CONCRETE || clicked.getType() == Material. GRAY_STAINED_GLASS_PANE) {
            String displayName = clicked.getItemMeta().getDisplayName();
            
            if (displayName.contains("슬롯")) {
                // 슬롯 번호 추출
                String[] parts = displayName.  split("번");
                try {
                    int slot = Integer.parseInt(parts[0]. replaceAll("[^0-9]", "")) - 1;
                    
                    // 스킬 선택 GUI 열기
                    openSkillSelector(player, slot);
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getCombatConfig(). getString("messages.prefix", "[전투] ") +
                        "§c오류가 발생했습니다.");
                }
            }
        }
    }
    
    /**
     * 스킬 선택 GUI 열기
     * @param player 플레이어
     * @param slot 슬롯 번호
     */
    private void openSkillSelector(Player player, int slot) {
        Inventory inv = Bukkit.createInventory(player, 54, "§6§l스킬 선택 (슬롯 " + (slot + 1) + ")");
        
        List<Skill> playerSkills = plugin.getSkillManager().getPlayerSkills(player);
        int slotIndex = 0;
        
        for (Skill skill : playerSkills) {
            if (slotIndex >= 45) break;  // 마지막 행은 버튼용
            
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            
            meta.setDisplayName(skill.getName());
            
            List<String> lore = new ArrayList<>();
            lore. add("§f클릭하여 선택");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
            
            inv.setItem(slotIndex, item);
            slotIndex++;
        }
        
        // 슬롯 데이터 저장 (메타데이터 또는 별도 저장소 사용)
        player.setMetadata("skill_slot_select", new org.bukkit.metadata. FixedMetadataValue(plugin, slot));
        
        player.openInventory(inv);
    }
}