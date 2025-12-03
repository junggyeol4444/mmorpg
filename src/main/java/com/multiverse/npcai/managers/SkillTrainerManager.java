package com.multiverse.npcai.managers;

import com.multiverse.npcai.NPCAICore;
import com.multiverse.npcai.models.SkillTrainer;
import com.multiverse.npcai.models.TrainableSkill;
import com.multiverse.npcai.models.enums.TrainerType;
import com.multiverse.npcai.utils.ConfigUtil;
import com.multiverse.npcai.data.DataManager;
import com.multiverse.npcai.managers.ReputationManager;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * NPC 스킬트레이너 관리, 스킬 학습/조건/일일 제한
 */
public class SkillTrainerManager {

    private final NPCAICore plugin;
    private final DataManager dataManager;
    private final ConfigUtil config;
    private final ReputationManager reputationManager;

    public SkillTrainerManager(NPCAICore plugin, DataManager dataManager, ConfigUtil config, ReputationManager reputationManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.config = config;
        this.reputationManager = reputationManager;
    }

    // === 트레이너 관리 ===
    public SkillTrainer getTrainer(int npcId) {
        return dataManager.getTrainer(npcId);
    }

    public void createTrainer(int npcId, TrainerType type) {
        SkillTrainer trainer = new SkillTrainer(npcId, "Trainer#" + npcId, type, new ArrayList<>(),
                config.getInt("skill-trainer.learning.daily-limit", 5),
                config.getInt("skill-trainer.learning.max-concurrent", 3));
        dataManager.saveTrainer(trainer);
    }

    // === 스킬 관리 ===
    public void addSkill(int npcId, TrainableSkill skill) {
        SkillTrainer trainer = getTrainer(npcId);
        if (trainer == null) return;
        trainer.getSkills().add(skill);
        dataManager.saveTrainer(trainer);
    }

    public List<TrainableSkill> getAvailableSkills(Player player, int npcId) {
        SkillTrainer trainer = getTrainer(npcId);
        if (trainer == null) return Collections.emptyList();
        List<TrainableSkill> result = new ArrayList<>();
        for (TrainableSkill skill : trainer.getSkills()) {
            if (canLearnSkill(player, skill)) result.add(skill);
        }
        return result;
    }

    // === 스킬 학습 ===
    public boolean canLearnSkill(Player player, TrainableSkill skill) {
        // 조건: 레벨, 호감도, 선행 스킬, 직업 등
        if (skill.getRequiredLevel() > player.getLevel()) return false;
        if (skill.getRequiredReputation() > reputationManager.getPoints(player, skill.getNpcId())) return false;
        // TODO: 직업 조건 등 추가
        // 이미 학습했는지 체크
        if (getCompletedSkills(player).contains(skill.getSkillId())) return false;
        // 동시 학습 제한
        if (getLearningSkills(player).size() >= config.getInt("skill-trainer.learning.max-concurrent", 3)) return false;
        return true;
    }

    public void learnSkill(Player player, int npcId, String skillId) {
        TrainableSkill skill = getTrainer(npcId).getSkillById(skillId);
        if (skill == null) return;
        if (!canLearnSkill(player, skill)) {
            player.sendMessage(config.getString("messages.skill.requirements-not-met"));
            return;
        }
        // 비용 차감
        double cost = skill.getCost();
        if (!plugin.getEconomy().has(player, cost)) {
            player.sendMessage(config.getString("messages.shop.insufficient-money").replace("{price}", String.valueOf(cost)));
            return;
        }
        plugin.getEconomy().withdrawPlayer(player, cost);
        dataManager.startSkillLearning(player.getUniqueId(), skill, npcId, skill.getLearningTime());
        player.sendMessage(config.getString("messages.skill.learning-started")
                .replace("{skill}", skill.getSkillName())
                .replace("{time}", String.valueOf(skill.getLearningTime())));
    }

    public void completeSkillLearning(Player player, String skillId) {
        dataManager.completeSkillLearning(player.getUniqueId(), skillId);
        player.sendMessage(config.getString("messages.skill.learning-complete").replace("{skill}", skillId));
        // SkillLearnCompleteEvent
        // plugin.getServer().getPluginManager().callEvent(...);
    }

    // === 제한 체크 ===
    public boolean checkDailyLimit(Player player, int npcId) {
        int learned = getDailyLearned(player, npcId);
        int limit = config.getInt("skill-trainer.learning.daily-limit", 5);
        return learned < limit;
    }

    public int getDailyLearned(Player player, int npcId) {
        return dataManager.getDailyLearnedSkillsCount(player.getUniqueId(), npcId);
    }

    // === 현재 학습/완료 여부 유틸 ===
    public List<String> getLearningSkills(Player player) {
        return dataManager.getLearningSkills(player.getUniqueId());
    }
    public List<String> getCompletedSkills(Player player) {
        return dataManager.getCompletedSkills(player.getUniqueId());
    }

    // === Skill, TrainableSkill 생성 유틸 ===
    public TrainableSkill makeSkill(String skillId, double cost, String condition) {
        return new TrainableSkill(skillId, skillId, 1, 1, 0, condition, new ArrayList<>(), "money", cost, 120, 0);
    }
}