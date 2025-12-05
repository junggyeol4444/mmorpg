package com.multiverse. skill.listeners;

import com. multiverse.skill.SkillCore;
import com.multiverse.skill.managers.LifeSkillManager;
import com.multiverse.skill.data.models.LifeSkill;
import com.multiverse. skill.data.enums.LifeSkillType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Material;

/**
 * 생활 스킬 리스너
 */
public class LifeSkillListener implements Listener {

    private final SkillCore plugin;
    private final LifeSkillManager lifeSkillManager;

    public LifeSkillListener(SkillCore plugin, LifeSkillManager lifeSkillManager) {
        this.plugin = plugin;
        this.lifeSkillManager = lifeSkillManager;
    }

    /**
     * 블록 파괴 이벤트 (채광, 벌목 등)
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (player == null || block == null) {
            return;
        }

        Material blockType = block.getType();

        // 채광 감지
        if (isMineralBlock(blockType)) {
            handleMining(player, blockType);
        }
        // 벌목 감지
        else if (isLogBlock(blockType)) {
            handleWoodcutting(player, blockType);
        }
        // 채초 감지
        else if (isHerbBlock(blockType)) {
            handleHerbalism(player, blockType);
        }
    }

    /**
     * 채광 처리
     */
    private void handleMining(Player player, Material blockType) {
        LifeSkill miningSkill = lifeSkillManager.getLifeSkill(player.getUniqueId(), LifeSkillType.MINING);
        
        if (miningSkill == null) {
            return;
        }

        // 광석 레벨 확인
        int requiredLevel = getOreRequiredLevel(blockType);
        if (miningSkill.getLevel() < requiredLevel) {
            player.sendMessage("§c채광 레벨이 부족합니다! (필요: Lv." + requiredLevel + ")");
            return;
        }

        // 경험치 획득
        int baseExp = LifeSkillType.MINING.getBaseExperience();
        double levelBonus = 1.0 + (miningSkill.getLevel() * LifeSkillType.MINING.getLevelBonus());
        int totalExp = (int) (baseExp * levelBonus);

        miningSkill.addExperience(totalExp);

        // 레벨업 확인
        if (miningSkill.checkLevelUp()) {
            player.sendMessage("§a채광 레벨이 " + miningSkill.getLevel() + "로 상승했습니다!");
        }

        plugin.getLogger().info(player.getName() + "이 채광 경험치 " + totalExp + "을 얻었습니다.");
    }

    /**
     * 벌목 처리
     */
    private void handleWoodcutting(Player player, Material blockType) {
        LifeSkill woodcuttingSkill = lifeSkillManager.getLifeSkill(player.getUniqueId(), LifeSkillType. WOODCUTTING);
        
        if (woodcuttingSkill == null) {
            return;
        }

        // 경험치 획득
        int baseExp = LifeSkillType.WOODCUTTING. getBaseExperience();
        double levelBonus = 1. 0 + (woodcuttingSkill.getLevel() * LifeSkillType.WOODCUTTING.getLevelBonus());
        int totalExp = (int) (baseExp * levelBonus);

        woodcuttingSkill.addExperience(totalExp);

        // 레벨업 확인
        if (woodcuttingSkill.checkLevelUp()) {
            player.sendMessage("§a벌목 레벨이 " + woodcuttingSkill.getLevel() + "로 상승했습니다!");
        }

        plugin.getLogger().info(player.getName() + "이 벌목 경험치 " + totalExp + "을 얻었습니다.");
    }

    /**
     * 채초 처리
     */
    private void handleHerbalism(Player player, Material blockType) {
        LifeSkill herbalismSkill = lifeSkillManager.getLifeSkill(player.getUniqueId(), LifeSkillType. HERBALISM);
        
        if (herbalismSkill == null) {
            return;
        }

        // 경험치 획득
        int baseExp = LifeSkillType.HERBALISM.getBaseExperience();
        double levelBonus = 1.0 + (herbalismSkill.getLevel() * LifeSkillType.HERBALISM.getLevelBonus());
        int totalExp = (int) (baseExp * levelBonus);

        herbalismSkill.addExperience(totalExp);

        // 레벨업 확인
        if (herbalismSkill.checkLevelUp()) {
            player.sendMessage("§a채초 레벨이 " + herbalismSkill. getLevel() + "로 상승했습니다!");
        }

        plugin.getLogger(). info(player.getName() + "이 채초 경험치 " + totalExp + "을 얻었습니다.");
    }

    /**
     * 광석 블록 확인
     */
    private boolean isMineralBlock(Material material) {
        return material == Material.COAL_ORE || material == Material.IRON_ORE || 
               material == Material. GOLD_ORE || material == Material.DIAMOND_ORE || 
               material == Material. EMERALD_ORE;
    }

    /**
     * 목재 블록 확인
     */
    private boolean isLogBlock(Material material) {
        return material. name().contains("LOG");
    }

    /**
     * 식물 블록 확인
     */
    private boolean isHerbBlock(Material material) {
        return material == Material.GRASS_BLOCK || material == Material. SEAGRASS || 
               material == Material. TALL_SEAGRASS;
    }

    /**
     * 광석별 필요 채광 레벨
     */
    private int getOreRequiredLevel(Material ore) {
        return switch (ore) {
            case COAL_ORE -> 1;
            case IRON_ORE -> 10;
            case GOLD_ORE -> 20;
            case DIAMOND_ORE -> 40;
            case EMERALD_ORE -> 50;
            default -> 1;
        };
    }
}