package com.multiverse. skill.managers;

import com. multiverse.skill.SkillCore;
import com.multiverse.skill.data.models.LearnedSkill;
import com.multiverse.skill.data. models.PlayerSkillData;
import com.multiverse. skill.data.models.Skill;
import com.multiverse. skill.events.SkillLearnEvent;
import com.multiverse. skill.events.SkillLevelUpEvent;
import com.multiverse.skill.utils.MessageUtils;
import org.bukkit. Bukkit;
import org.bukkit.entity.Player;

import java.util. UUID;

public class SkillLearningManager {

    private final SkillCore plugin;
    private final SkillManager skillManager;

    public SkillLearningManager(SkillCore plugin, SkillManager skillManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
    }

    /**
     * 플레이어에게 스킬 포인트 추가
     */
    public void addSkillPoints(Player player, int points) {
        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        int newTotal = skillData.getTotalSkillPoints() + points;
        skillData.setTotalSkillPoints(newTotal);
        skillData.setAvailableSkillPoints(skillData.getAvailableSkillPoints() + points);
        plugin.getPlayerDataLoader().savePlayerData(player.getUniqueId());
        MessageUtils.sendMessage(player, String.format("§a스킬 포인트 §e%d§a를 획득했습니다!", points));
    }

    /**
     * 플레이어 스킬 포인트 설정
     */
    public void setSkillPoints(Player player, int points) {
        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        int difference = points - skillData.getTotalSkillPoints();
        skillData.setTotalSkillPoints(points);
        skillData.setAvailableSkillPoints(skillData.getAvailableSkillPoints() + difference);
        plugin.getPlayerDataLoader().savePlayerData(player.getUniqueId());
    }

    /**
     * 사용 가능한 포인트 조회
     */
    public int getAvailablePoints(Player player) {
        PlayerSkillData skillData = plugin. getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        return skillData.getAvailableSkillPoints();
    }

    /**
     * 필요한 포인트가 충분한지 확인
     */
    public boolean hasEnoughPoints(Player player, int required) {
        return getAvailablePoints(player) >= required;
    }

    /**
     * 플레이어가 스킬을 습득했는지 확인
     */
    public boolean hasSkill(Player player, String skillId) {
        PlayerSkillData skillData = plugin. getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        return skillData.getSkills().containsKey(skillId);
    }

    /**
     * 스킬 습득 가능 여부 확인
     */
    public boolean canLearnSkill(Player player, String skillId) {
        Skill skill = skillManager.getSkill(skillId);
        if (skill == null) return false;

        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        
        // 이미 습득했는지 확인
        if (skillData.getSkills().containsKey(skillId)) {
            return false;
        }

        // 포인트 확인
        if (! hasEnoughPoints(player, 1)) {
            return false;
        }

        // 플레이어 레벨 확인
        if (player.getLevel() < skill.getRequiredLevel()) {
            return false;
        }

        // 필수 클래스 확인
        if (skill.getRequiredClass() != null && !skill.getRequiredClass().isEmpty()) {
            // 플레이어 클래스 확인 로직 (PlayerDataCore 연동)
        }

        // 선행 스킬 확인
        for (String prerequisite : skill.getPrerequisites()) {
            if (!hasSkill(player, prerequisite)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 스킬 습득
     */
    public void learnSkill(Player player, String skillId) {
        if (!canLearnSkill(player, skillId)) {
            MessageUtils. sendMessage(player, "§c이 스킬을 습득할 수 없습니다.");
            return;
        }

        Skill skill = skillManager.getSkill(skillId);
        PlayerSkillData skillData = plugin. getPlayerDataLoader().loadPlayerData(player.getUniqueId());

        // 스킬 학습 이벤트 발생
        SkillLearnEvent event = new SkillLearnEvent(player, skillId);
        Bukkit.getPluginManager(). callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        // 새로운 습득 스킬 생성
        LearnedSkill learnedSkill = new LearnedSkill();
        learnedSkill.setSkillId(skillId);
        learnedSkill.setLevel(1);
        learnedSkill.setExperience(0);
        learnedSkill.setLastUsedTime(0);
        learnedSkill.setTimesUsed(0);
        learnedSkill.setTotalDamage(0);

        // 스킬 추가
        skillData.getSkills().put(skillId, learnedSkill);

        // 포인트 차감
        skillData.setUsedSkillPoints(skillData.getUsedSkillPoints() + 1);
        skillData.setAvailableSkillPoints(skillData.getAvailableSkillPoints() - 1);

        // 트리 진행도 업데이트
        if (skill.getSkillTreeId() != null) {
            int currentProgress = skillData.getTreeProgress(). getOrDefault(skill.getSkillTreeId(), 0);
            skillData.getTreeProgress().put(skill.getSkillTreeId(), currentProgress + 1);
        }

        plugin.getPlayerDataLoader().savePlayerData(player.getUniqueId());

        MessageUtils.sendMessage(player, String.format("§a스킬 '§e%s§a'을(를) 습득했습니다!", skill.getName()));
    }

    /**
     * 스킬 업그레이드
     */
    public void upgradeSkill(Player player, String skillId) {
        if (!hasSkill(player, skillId)) {
            MessageUtils.sendMessage(player, "§c이 스킬을 습득하지 않았습니다.");
            return;
        }

        Skill skill = skillManager.getSkill(skillId);
        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        LearnedSkill learnedSkill = skillData.getSkills().get(skillId);

        // 최대 레벨 확인
        if (learnedSkill. getLevel() >= skill.getMaxLevel()) {
            MessageUtils. sendMessage(player, "§c이미 최대 레벨입니다.");
            return;
        }

        // 레벨 업
        learnedSkill.setLevel(learnedSkill. getLevel() + 1);
        learnedSkill.setExperience(0);

        // 레벨업 이벤트 발생
        SkillLevelUpEvent event = new SkillLevelUpEvent(player, skillId, learnedSkill.getLevel());
        Bukkit.getPluginManager().callEvent(event);

        plugin.getPlayerDataLoader().savePlayerData(player.getUniqueId());

        MessageUtils.sendMessage(player, String.format("§a스킬 '§e%s§a'이(가) Lv.§e%d§a(으)로 성장했습니다!", 
            skill.getName(), learnedSkill.getLevel()));
    }

    /**
     * 스킬 초기화 (개별)
     */
    public void resetSkill(Player player, String skillId, boolean refund) {
        if (!hasSkill(player, skillId)) {
            MessageUtils.sendMessage(player, "§c이 스킬을 습득하지 않았습니다.");
            return;
        }

        PlayerSkillData skillData = plugin. getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        LearnedSkill learnedSkill = skillData.getSkills().get(skillId);
        Skill skill = skillManager.getSkill(skillId);

        // 포인트 환급
        if (refund) {
            int refundAmount = (int) Math.ceil(1 * (learnedSkill.getLevel() - 1) * 0.8);
            skillData.setAvailableSkillPoints(skillData.getAvailableSkillPoints() + refundAmount);
        }

        skillData.getSkills().remove(skillId);
        plugin.getPlayerDataLoader().savePlayerData(player.getUniqueId());

        MessageUtils.sendMessage(player, String.format("§a스킬 '§e%s§a'이(가) 초기화되었습니다!", skill.getName()));
    }

    /**
     * 트리 초기화
     */
    public void resetTree(Player player, String treeId, boolean refund) {
        PlayerSkillData skillData = plugin. getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        
        int refundAmount = 0;
        List<String> skillsToRemove = new ArrayList<>();

        for (Map.Entry<String, LearnedSkill> entry : skillData. getSkills().entrySet()) {
            Skill skill = skillManager.getSkill(entry.getKey());
            if (skill != null && skill.getSkillTreeId() != null && skill.getSkillTreeId().equals(treeId)) {
                skillsToRemove.add(entry.getKey());
                if (refund) {
                    refundAmount += (int) Math.ceil(1 * (entry.getValue().getLevel() - 1) * 0.8);
                }
            }
        }

        skillsToRemove.forEach(skillId -> skillData.getSkills().remove(skillId));

        if (refund) {
            skillData.setAvailableSkillPoints(skillData.getAvailableSkillPoints() + refundAmount);
        }

        skillData.getTreeProgress().put(treeId, 0);
        plugin.getPlayerDataLoader().savePlayerData(player.getUniqueId());

        MessageUtils.sendMessage(player, "§a스킬 트리가 초기화되었습니다!");
    }

    /**
     * 모든 스킬 초기화
     */
    public void resetAllSkills(Player player, boolean refund) {
        PlayerSkillData skillData = plugin. getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        
        int refundAmount = 0;
        for (LearnedSkill skill : skillData.getSkills(). values()) {
            if (refund) {
                refundAmount += (int) Math.ceil(1 * (skill.getLevel() - 1) * 0.8);
            }
        }

        skillData.getSkills().clear();
        skillData.getTreeProgress().clear();

        if (refund) {
            skillData.setAvailableSkillPoints(skillData.getAvailableSkillPoints() + refundAmount);
        }

        plugin.getPlayerDataLoader().savePlayerData(player.getUniqueId());

        MessageUtils.sendMessage(player, "§a모든 스킬이 초기화되었습니다!");
    }

    /**
     * 스킬 경험치 추가
     */
    public void addExperience(Player player, String skillId, long amount) {
        if (!hasSkill(player, skillId)) {
            return;
        }

        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        LearnedSkill learnedSkill = skillData. getSkills().get(skillId);
        Skill skill = skillManager. getSkill(skillId);

        learnedSkill.setExperience(learnedSkill. getExperience() + amount);

        plugin.getPlayerDataLoader(). savePlayerData(player.getUniqueId());
    }

    /**
     * 스킬 통계 업데이트
     */
    public void updateSkillStats(Player player, String skillId, double damage) {
        if (!hasSkill(player, skillId)) {
            return;
        }

        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        LearnedSkill learnedSkill = skillData. getSkills().get(skillId);

        learnedSkill.setTimesUsed(learnedSkill.getTimesUsed() + 1);
        learnedSkill.setTotalDamage(learnedSkill.getTotalDamage() + (long) damage);
        learnedSkill.setLastUsedTime(System.currentTimeMillis());

        plugin.getPlayerDataLoader().savePlayerData(player.getUniqueId());
    }
}