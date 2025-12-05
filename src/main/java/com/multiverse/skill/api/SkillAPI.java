package com.multiverse.skill.api;

import com.multiverse.skill.SkillCore;
import com.multiverse.skill.managers.SkillManager;
import com.multiverse.skill.managers.SkillCastManager;
import com.multiverse.skill.data.models.*;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * 스킬 API
 */
public class SkillAPI {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final SkillCastManager castManager;

    public SkillAPI(SkillCore plugin, SkillManager skillManager, SkillCastManager castManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.castManager = castManager;
    }

    /**
     * 플레이어에게 스킬 습득시키기
     */
    public boolean learnSkill(Player player, String skillId) {
        if (player == null || skillId == null) {
            return false;
        }

        Skill skill = skillManager.getSkill(skillId);
        if (skill == null) {
            return false;
        }

        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData == null) {
            return false;
        }

        // 이미 습득했는지 확인
        if (skillData.hasSkill(skillId)) {
            return false;
        }

        // 스킬 습득
        LearnedSkill learned = new LearnedSkill(skillId, skill.getName(), 1);
        skillData.addSkill(skillId, learned);

        return true;
    }

    /**
     * 플레이어의 스킬 레벨 상승
     */
    public boolean levelUpSkill(Player player, String skillId) {
        if (player == null || skillId == null) {
            return false;
        }

        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData == null) {
            return false;
        }

        LearnedSkill learned = skillData.getSkill(skillId);
        if (learned == null) {
            return false;
        }

        Skill skill = skillManager.getSkill(skillId);
        if (skill == null || learned.getLevel() >= skill.getMaxLevel()) {
            return false;
        }

        learned.levelUp();
        return true;
    }

    /**
     * 플레이어의 스킬 레벨 조회
     */
    public int getSkillLevel(Player player, String skillId) {
        if (player == null || skillId == null) {
            return 0;
        }

        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData == null) {
            return 0;
        }

        LearnedSkill learned = skillData.getSkill(skillId);
        if (learned == null) {
            return 0;
        }

        return learned.getLevel();
    }

    /**
     * 플레이어가 스킬을 습득했는지 확인
     */
    public boolean hasSkill(Player player, String skillId) {
        if (player == null || skillId == null) {
            return false;
        }

        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData == null) {
            return false;
        }

        return skillData.hasSkill(skillId);
    }

    /**
     * 플레이어의 습득한 스킬 목록 조회
     */
    public List<String> getLearnedSkills(Player player) {
        if (player == null) {
            return List.of();
        }

        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData == null) {
            return List.of();
        }

        return skillData.getSkills().keySet(). stream(). toList();
    }

    /**
     * 스킬 캐스팅
     */
    public boolean castSkill(Player player, String skillId) {
        if (player == null || skillId == null) {
            return false;
        }

        Skill skill = skillManager.getSkill(skillId);
        if (skill == null) {
            return false;
        }

        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData == null) {
            return false;
        }

        LearnedSkill learned = skillData.getSkill(skillId);
        if (learned == null) {
            return false;
        }

        try {
            castManager.castSkill(player, skill, learned);
            return true;
        } catch (Exception e) {
            plugin.getLogger(). warning("스킬 캐스팅 실패: " + skillId);
            return false;
        }
    }

    /**
     * 플레이어의 총 스킬 포인트 조회
     */
    public int getTotalSkillPoints(Player player) {
        if (player == null) {
            return 0;
        }

        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData == null) {
            return 0;
        }

        return skillData.getTotalSkillPoints();
    }

    /**
     * 플레이어의 남은 스킬 포인트 조회
     */
    public int getAvailableSkillPoints(Player player) {
        if (player == null) {
            return 0;
        }

        PlayerSkillData skillData = skillManager. getPlayerSkillData(player. getUniqueId());
        if (skillData == null) {
            return 0;
        }

        return skillData.getAvailableSkillPoints();
    }

    /**
     * 스킬 정보 조회
     */
    public Skill getSkill(String skillId) {
        if (skillId == null) {
            return null;
        }

        return skillManager.getSkill(skillId);
    }

    /**
     * 모든 스킬 조회
     */
    public List<Skill> getAllSkills() {
        return skillManager.getAllSkills();
    }
}