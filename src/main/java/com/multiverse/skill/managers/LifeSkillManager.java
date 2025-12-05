package com.multiverse.skill.managers;

import com.multiverse.skill. SkillCore;
import com.multiverse. skill.data.enums.LifeSkillType;
import com.multiverse.skill.data. models.LifeSkill;
import com.multiverse.skill. data.models.PlayerSkillData;
import com.multiverse.skill.utils.MessageUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LifeSkillManager {

    private final SkillCore plugin;
    private final Map<UUID, Map<LifeSkillType, LifeSkill>> playerLifeSkills = new HashMap<>();

    public LifeSkillManager(SkillCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 플레이어의 생활 스킬 로드
     */
    public Map<LifeSkillType, LifeSkill> loadPlayerLifeSkills(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (!playerLifeSkills.containsKey(playerUUID)) {
            Map<LifeSkillType, LifeSkill> skills = new HashMap<>();
            
            for (LifeSkillType type : LifeSkillType. values()) {
                LifeSkill skill = new LifeSkill();
                skill.setType(type);
                skill.setLevel(1);
                skill.setExperience(0);
                skill. setExperienceToNext(1000);
                skills.put(type, skill);
            }

            playerLifeSkills.put(playerUUID, skills);
        }

        return playerLifeSkills.get(playerUUID);
    }

    /**
     * 생활 스킬 경험치 추가
     */
    public void addExperience(Player player, LifeSkillType type, long amount) {
        Map<LifeSkillType, LifeSkill> skills = loadPlayerLifeSkills(player);
        LifeSkill skill = skills. get(type);

        if (skill == null) {
            return;
        }

        skill.setExperience(skill.getExperience() + amount);

        // 레벨업 체크
        while (skill.getExperience() >= skill.getExperienceToNext()) {
            levelUpSkill(player, type, skill);
        }

        // 저장
        savePlayerLifeSkills(player);
    }

    /**
     * 생활 스킬 레벨업
     */
    private void levelUpSkill(Player player, LifeSkillType type, LifeSkill skill) {
        long experienceOver = skill.getExperience() - skill.getExperienceToNext();
        skill.setLevel(skill.getLevel() + 1);
        skill.setExperience(experienceOver);
        
        // 다음 레벨 필요 경험치 계산 (지수적 증가)
        long nextExp = (long) (skill.getExperienceToNext() * 1.1);
        skill.setExperienceToNext(nextExp);

        MessageUtils.sendMessage(player, String.format("§a[생활 스킬] §e%s §aLv.  %d 달성! ",
            type.getDisplayName(), skill.getLevel()));
    }

    /**
     * 플레이어 생활 스킬 저장
     */
    private void savePlayerLifeSkills(Player player) {
        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        Map<LifeSkillType, LifeSkill> playerSkills = playerLifeSkills.get(player.getUniqueId());

        if (playerSkills != null) {
            skillData.setLifeSkills(playerSkills);
            plugin.getPlayerDataLoader().savePlayerData(player.getUniqueId());
        }
    }

    /**
     * 생활 스킬 레벨 조회
     */
    public int getLevel(Player player, LifeSkillType type) {
        Map<LifeSkillType, LifeSkill> skills = loadPlayerLifeSkills(player);
        LifeSkill skill = skills.get(type);
        return skill != null ? skill.getLevel() : 1;
    }

    /**
     * 생활 스킬 경험치 조회
     */
    public long getExperience(Player player, LifeSkillType type) {
        Map<LifeSkillType, LifeSkill> skills = loadPlayerLifeSkills(player);
        LifeSkill skill = skills.get(type);
        return skill != null ? skill.getExperience() : 0;
    }

    /**
     * 보너스 값 조회
     */
    public double getBonus(Player player, LifeSkillType type, String bonusKey) {
        Map<LifeSkillType, LifeSkill> skills = loadPlayerLifeSkills(player);
        LifeSkill skill = skills.get(type);

        if (skill == null || skill.getBonuses() == null) {
            return 1.0;
        }

        Double bonus = skill.getBonuses(). get(bonusKey);
        return bonus != null ? bonus : 1.0;
    }

    /**
     * 채광 블록 파괴 이벤트 처리
     */
    public void onBlockBreak(Player player, Block block) {
        LifeSkillType skillType = null;

        switch (block.getType()) {
            case STONE, DEEPSLATE, GRANITE, DIORITE, ANDESITE, COBBLESTONE -> skillType = LifeSkillType.MINING;
            case OAK_LOG, BIRCH_LOG, SPRUCE_LOG, DARK_OAK_LOG, JUNGLE_LOG, ACACIA_LOG -> skillType = LifeSkillType.WOODCUTTING;
            case TALL_SEAGRASS, SEAGRASS -> skillType = LifeSkillType.FISHING;
            case GRASS, TALL_GRASS, DANDELION, POPPY -> skillType = LifeSkillType.HERBALISM;
        }

        if (skillType != null) {
            int experience = plugin.getConfig().getInt("life-skills.experience." + skillType.name(). toLowerCase() + "-per-block", 10);
            addExperience(player, skillType, experience);
        }
    }

    /**
     * 제작 완료 이벤트 처리
     */
    public void onCraft(Player player, ItemStack result) {
        LifeSkillType skillType = null;

        String itemName = result.getType().name();
        
        // 아이템 타입별 생활 스킬 분류
        if (itemName.contains("SWORD") || itemName.contains("PICKAXE") || itemName.contains("AXE")) {
            skillType = LifeSkillType. SMITHING;
        } else if (itemName.contains("POTION")) {
            skillType = LifeSkillType. ALCHEMY;
        } else if (itemName.contains("BREAD") || itemName.contains("CAKE")) {
            skillType = LifeSkillType. COOKING;
        }

        if (skillType != null) {
            int experience = plugin.getConfig().getInt("life-skills.experience. crafting-per-item", 50);
            addExperience(player, skillType, experience);
        }
    }

    /**
     * 낚시 완료 이벤트 처리
     */
    public void onFish(Player player, ItemStack caught) {
        int experience = plugin.getConfig().getInt("life-skills.experience. fishing-per-catch", 50);
        addExperience(player, LifeSkillType.FISHING, experience);
    }

    /**
     * 생활 스킬 모두 초기화
     */
    public void resetAllLifeSkills(Player player) {
        Map<LifeSkillType, LifeSkill> skills = loadPlayerLifeSkills(player);
        
        for (LifeSkill skill : skills.values()) {
            skill.setLevel(1);
            skill.setExperience(0);
            skill.setExperienceToNext(1000);
        }

        savePlayerLifeSkills(player);
        MessageUtils.sendMessage(player, "§a모든 생활 스킬이 초기화되었습니다!");
    }

    /**
     * 생활 스킬 정보 표시
     */
    public void showLifeSkillInfo(Player player) {
        MessageUtils.sendMessage(player, "§b=== 생활 스킬 정보 ===");
        
        Map<LifeSkillType, LifeSkill> skills = loadPlayerLifeSkills(player);
        for (Map.Entry<LifeSkillType, LifeSkill> entry : skills.entrySet()) {
            LifeSkill skill = entry. getValue();
            long expNeeded = skill.getExperienceToNext() - skill.getExperience();
            
            MessageUtils.sendMessage(player, String.format("§e%s: §bLv. %d §7(§b%d§7/§b%d§7)",
                entry.getKey().getDisplayName(),
                skill.getLevel(),
                skill.getExperience(),
                skill.getExperienceToNext()));
        }
    }

    /**
     * 플레이어 로그아웃 시 정리
     */
    public void cleanupPlayer(Player player) {
        playerLifeSkills.remove(player.getUniqueId());
    }

    /**
     * 생활 스킬 통계
     */
    public Map<String, Object> getLifeSkillStats(Player player) {
        Map<String, Object> stats = new HashMap<>();
        Map<LifeSkillType, LifeSkill> skills = loadPlayerLifeSkills(player);

        int totalLevel = 0;
        long totalExp = 0;

        for (LifeSkill skill : skills. values()) {
            totalLevel += skill.getLevel();
            totalExp += skill.getExperience();
        }

        stats. put("total_level", totalLevel);
        stats.put("total_experience", totalExp);
        stats.put("average_level", totalLevel / (double) skills.size());

        return stats;
    }
}