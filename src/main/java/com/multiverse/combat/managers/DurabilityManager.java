package com.multiverse.combat. managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.Material;
import com.multiverse.combat.CombatCore;
import java.util.*;

/**
 * 내구도 관리 클래스
 * 플레이어 무기와 방어구의 내구도를 관리합니다. 
 */
public class DurabilityManager {
    
    private final CombatCore plugin;
    private final Map<UUID, Map<String, Integer>> playerDurabilityData = new HashMap<>();
    
    // 설정값
    private static final int WEAPON_DAMAGE_ON_HIT = 1;
    private static final int ARMOR_DAMAGE_ON_HIT = 1;
    private static final double SKILL_DAMAGE_MULTIPLIER = 2.  0;
    private static final int BROKEN_EQUIPMENT_EFFECTIVENESS = 50;
    
    /**
     * DurabilityManager 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public DurabilityManager(CombatCore plugin) {
        this. plugin = plugin;
    }
    
    /**
     * 무기 내구도 감소
     * @param player 플레이어
     */
    public void damageWeapon(Player player) {
        ItemStack weapon = player.getInventory().  getItemInMainHand();
        
        if (weapon == null || weapon.getType() == Material.AIR) {
            return;
        }
        
        damageItem(weapon, WEAPON_DAMAGE_ON_HIT);
        updateDurabilityDisplay(weapon);
    }
    
    /**
     * 무기 내구도 감소 (스킬 사용)
     * @param player 플레이어
     */
    public void damageWeaponFromSkill(Player player) {
        ItemStack weapon = player.getInventory(). getItemInMainHand();
        
        if (weapon == null || weapon.getType() == Material.AIR) {
            return;
        }
        
        int damage = (int) (WEAPON_DAMAGE_ON_HIT * SKILL_DAMAGE_MULTIPLIER);
        damageItem(weapon, damage);
        updateDurabilityDisplay(weapon);
    }
    
    /**
     * 방어구 내구도 감소
     * @param player 플레이어
     * @param damage 입을 데미지
     */
    public void damageArmor(Player player, double damage) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] armorContents = inventory.getArmorContents();
        
        // 데미지를 분산
        for (ItemStack armor : armorContents) {
            if (armor != null && armor.getType() != Material.AIR) {
                int armorDamage = (int) (ARMOR_DAMAGE_ON_HIT * (damage / 100.0));
                damageItem(armor, armorDamage);
                updateDurabilityDisplay(armor);
            }
        }
    }
    
    /**
     * 아이템 내구도 감소 (공통)
     * @param item 아이템
     * @param amount 감소량
     */
    private void damageItem(ItemStack item, int amount) {
        short durability = item.getDurability();
        short maxDurability = item.getType(). getMaxDurability();
        
        // 최대 내구도까지만 증가
        short newDurability = (short) Math.min(durability + amount, maxDurability);
        item.setDurability(newDurability);
    }
    
    /**
     * 아이템 내구도 조회
     * @param item 아이템
     * @return 현재 내구도
     */
    public int getDurability(ItemStack item) {
        if (item == null || item.getType() == Material. AIR) {
            return 0;
        }
        return item.getType().getMaxDurability() - item.getDurability();
    }
    
    /**
     * 아이템 최대 내구도 조회
     * @param item 아이템
     * @return 최대 내구도
     */
    public int getMaxDurability(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return 0;
        }
        return item.getType().getMaxDurability();
    }
    
    /**
     * 아이템 내구도 비율 조회 (0~100)
     * @param item 아이템
     * @return 내구도 비율
     */
    public double getDurabilityPercentage(ItemStack item) {
        int maxDurability = getMaxDurability(item);
        if (maxDurability == 0) {
            return 100.0;
        }
        int currentDurability = getDurability(item);
        return (currentDurability / (double) maxDurability) * 100.0;
    }
    
    /**
     * 아이템이 파괴되었는지 확인
     * @param item 아이템
     * @return 파괴되었으면 true
     */
    public boolean isBroken(ItemStack item) {
        return getDurabilityPercentage(item) <= 0;
    }
    
    /**
     * 파괴된 장비의 효율성 조회
     * @param item 아이템
     * @return 효율성 비율 (0~100)
     */
    public int getBrokenEquipmentEffectiveness(ItemStack item) {
        if (! isBroken(item)) {
            return 100;
        }
        return BROKEN_EQUIPMENT_EFFECTIVENESS;
    }
    
    /**
     * 아이템 수리
     * @param item 아이템
     * @param amount 수리량
     */
    public void repair(ItemStack item, int amount) {
        short durability = item.getDurability();
        short newDurability = (short) Math.max(durability - amount, 0);
        item.setDurability(newDurability);
        
        updateDurabilityDisplay(item);
    }
    
    /**
     * 아이템 완전 복구
     * @param item 아이템
     */
    public void fullyRepair(ItemStack item) {
        item.setDurability((short) 0);
        updateDurabilityDisplay(item);
    }
    
    /**
     * 수리 비용 계산
     * @param item 아이템
     * @return 수리 비용
     */
    public int getRepairCost(ItemStack item) {
        double durabilityPercentage = getDurabilityPercentage(item);
        double costMultiplier = plugin.getCombatConfig().getDouble("durability.repair.cost-multiplier", 1.0);
        
        // 내구도가 낮을수록 비용 증가
        double baseCost = 100 * costMultiplier;
        return (int) (baseCost * (1. 0 - (durabilityPercentage / 100.0)));
    }
    
    /**
     * 내구도 표시 업데이트 (Lore)
     * @param item 아이템
     */
    public void updateDurabilityDisplay(ItemStack item) {
        if (item == null || item.getType() == Material. AIR) {
            return;
        }
        
        if (! item.hasItemMeta()) {
            return;
        }
        
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        
        // 기존 내구도 정보 제거
        lore.removeIf(line -> line.contains("내구도") || line.contains("Durability"));
        
        // 새 내구도 정보 추가
        double durabilityPercentage = getDurabilityPercentage(item);
        String durabilityColor = durabilityPercentage > 50 ? "§a" : 
                                durabilityPercentage > 25 ? "§e" : "§c";
        
        String durabilityInfo = durabilityColor + "내구도: " + String.format("%.1f%%", durabilityPercentage);
        lore.add(durabilityInfo);
        
        // 파괴 상태 표시
        if (isBroken(item)) {
            lore.add("§c⚠ 파괴됨 - 효율 50%");
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
    
    /**
     * 모든 장비의 내구도 확인 및 업데이트
     * @param player 플레이어
     */
    public void updateAllEquipmentDurability(Player player) {
        // 메인 핸드
        ItemStack mainHand = player.getInventory(). getItemInMainHand();
        if (mainHand != null && mainHand.getType() != Material.AIR) {
            updateDurabilityDisplay(mainHand);
        }
        
        // 오프 핸드
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (offHand != null && offHand.getType() != Material.AIR) {
            updateDurabilityDisplay(offHand);
        }
        
        // 방어구
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                updateDurabilityDisplay(armor);
            }
        }
    }
    
    /**
     * 플레이어의 전체 방어구 내구도 계산
     * @param player 플레이어
     * @return 총 내구도
     */
    public int getTotalArmorDurability(Player player) {
        int totalDurability = 0;
        
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                totalDurability += getDurability(armor);
            }
        }
        
        return totalDurability;
    }
    
    /**
     * 플레이어 데이터 저장
     * @param player 플레이어
     */
    public void savePlayerData(Player player) {
        // 내구도는 ItemStack에 저장되므로 별도 저장 불필요
    }
    
    /**
     * 플레이어 데이터 로드
     * @param player 플레이어
     */
    public void loadPlayerData(Player player) {
        // 내구도는 ItemStack에서 자동 로드됨
    }
}