package com.multiverse.combat. calculator;

import org.bukkit.entity.LivingEntity;
import org.bukkit. entity.Player;
import org. bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import com.multiverse.combat.CombatCore;

/**
 * 방어력 계산 클래스
 * 방어, 마법 저항, 회피를 계산합니다.
 */
public class DefenseCalculator {
    
    private final CombatCore plugin;
    
    // 방어 설정값
    private static final double MAX_DEFENSE_REDUCTION = 75.0;
    private static final double MAX_MAGICAL_RESISTANCE = 75.0;
    
    // VIT, RES 스탯 계수
    private static final double VIT_DEFENSE_COEFFICIENT = 0.3;
    private static final double RES_RESISTANCE_COEFFICIENT = 0.5;
    
    /**
     * DefenseCalculator 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public DefenseCalculator(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 물리 방어력 계산
     * 방어구 방어력 + (VIT * 0.3)
     * @param entity 대상 엔티티
     * @return 물리 방어력
     */
    public double calculatePhysicalDefense(LivingEntity entity) {
        double armorDefense = 0.0;
        
        // 방어구 방어력 계산
        if (entity instanceof Player) {
            Player player = (Player) entity;
            ItemStack[] armorContents = player.getInventory(). getArmorContents();
            
            for (ItemStack armor : armorContents) {
                if (armor != null && armor.getType() != org.bukkit.Material.AIR) {
                    // 방어구 기본 방어력
                    armorDefense += getArmorDefenseValue(armor);
                    
                    // 인챈트 (보호, 내구성 등)
                    int protectionLevel = armor.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
                    if (protectionLevel > 0) {
                        armorDefense += protectionLevel * 0.5;
                    }
                }
            }
        }
        
        // VIT 스탯 적용
        int vit = entity instanceof Player ? ((Player) entity).getLevel() * 4 : 10;
        double vitDefense = vit * VIT_DEFENSE_COEFFICIENT;
        
        return armorDefense + vitDefense;
    }
    
    /**
     * 방어구 기본 방어력 조회
     * @param armor 방어구 아이템
     * @return 방어력 값
     */
    private double getArmorDefenseValue(ItemStack armor) {
        switch (armor.getType()) {
            // 가죽 방어구
            case LEATHER_HELMET:
                return 1.0;
            case LEATHER_CHESTPLATE:
                return 3.0;
            case LEATHER_LEGGINGS:
                return 2.0;
            case LEATHER_BOOTS:
                return 1.0;
            
            // 체인 방어구
            case CHAINMAIL_HELMET:
                return 2.0;
            case CHAINMAIL_CHESTPLATE:
                return 5.0;
            case CHAINMAIL_LEGGINGS:
                return 4.0;
            case CHAINMAIL_BOOTS:
                return 2.0;
            
            // 철 방어구
            case IRON_HELMET:
                return 2.0;
            case IRON_CHESTPLATE:
                return 6.0;
            case IRON_LEGGINGS:
                return 5.0;
            case IRON_BOOTS:
                return 2.0;
            
            // 다이아몬드 방어구
            case DIAMOND_HELMET:
                return 3.0;
            case DIAMOND_CHESTPLATE:
                return 8.0;
            case DIAMOND_LEGGINGS:
                return 6.0;
            case DIAMOND_BOOTS:
                return 3.0;
            
            // 네더라이트 방어구
            case NETHERITE_HELMET:
                return 3.0;
            case NETHERITE_CHESTPLATE:
                return 8.0;
            case NETHERITE_LEGGINGS:
                return 7.0;
            case NETHERITE_BOOTS:
                return 3.0;
            
            default:
                return 0.0;
        }
    }
    
    /**
     * 방어율 계산
     * 방어력 / (방어력 + 100), 최대 75%
     * @param entity 대상 엔티티
     * @return 방어율 (0~0.75)
     */
    public double getDefenseRate(LivingEntity entity) {
        double defense = calculatePhysicalDefense(entity);
        double defenseRate = defense / (defense + 100. 0);
        
        // 최대 방어율 제한
        return Math.min(defenseRate, MAX_DEFENSE_REDUCTION / 100.0);
    }
    
    /**
     * 마법 저항 계산
     * RES * 0.5
     * @param entity 대상 엔티티
     * @return 마법 저항값
     */
    public double calculateMagicalResistance(LivingEntity entity) {
        // RES 스탯 (저항)
        int res = entity instanceof Player ? ((Player) entity).getLevel() * 3 : 8;
        
        return res * RES_RESISTANCE_COEFFICIENT;
    }
    
    /**
     * 마법 저항율 계산
     * 마법 저항 / (마법 저항 + 100), 최대 75%
     * @param entity 대상 엔티티
     * @return 마법 저항율 (0~0. 75)
     */
    public double getMagicalResistanceRate(LivingEntity entity) {
        double resistance = calculateMagicalResistance(entity);
        double resistanceRate = resistance / (resistance + 100.0);
        
        // 최대 저항율 제한
        return Math.min(resistanceRate, MAX_MAGICAL_RESISTANCE / 100.0);
    }
    
    /**
     * 회피율 계산
     * (DEX * 0.1%), 최대 75%
     * @param player 플레이어
     * @return 회피율 (0~0.75)
     */
    public double getEvasionRate(Player player) {
        int dex = player.getLevel() * 3;  // 민첩(DEX)
        double evasionRate = (dex * 0.1) / 100.0;
        
        // 최대 회피율 제한
        return Math.min(evasionRate, MAX_DEFENSE_REDUCTION / 100.0);
    }
    
    /**
     * 총 피해 감소율 계산
     * 방어율 + 마법 저항율 + 회피율 (중복 제거)
     * @param entity 대상 엔티티
     * @return 총 감소율
     */
    public double getTotalDamageReduction(LivingEntity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            double defense = getDefenseRate(player);
            double resistance = getMagicalResistanceRate(player);
            double evasion = getEvasionRate(player);
            
            // 중복되지 않도록 조합
            double combined = defense + resistance + evasion;
            
            // 최대 90% 제한 (최소 10% 데미지는 들어옴)
            return Math.min(combined, 0.9);
        }
        
        return 0.0;
    }
    
    /**
     * 방어력 검증
     * @param defense 방어력
     * @return 유효한 방어력 (0 이상)
     */
    public double validateDefense(double defense) {
        return Math.max(defense, 0.0);
    }
    
    /**
     * 저항력 검증
     * @param resistance 저항력
     * @return 유효한 저항력 (0 이상)
     */
    public double validateResistance(double resistance) {
        return Math.max(resistance, 0. 0);
    }
    
    /**
     * 감소율 검증
     * @param rate 감소율
     * @return 유효한 감소율 (0~1)
     */
    public double validateReductionRate(double rate) {
        return Math.max(0, Math.min(rate, 1.0));
    }
}