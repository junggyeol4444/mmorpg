package com.multiverse.skill.api;

import com.multiverse.skill. SkillCore;
import com.multiverse. skill.managers.SkillManager;
import com.multiverse. skill.data.models.*;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * 스킬 트리 API
 */
public class SkillTreeAPI {

    private final SkillCore plugin;
    private final SkillManager skillManager;

    public SkillTreeAPI(SkillCore plugin, SkillManager skillManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
    }

    /**
     * 스킬 트리 조회
     */
    public SkillTree getSkillTree(String treeId) {
        if (treeId == null) {
            return null;
        }

        return skillManager.getSkillTree(treeId);
    }

    /**
     * 모든 스킬 트리 조회
     */
    public List<SkillTree> getAllSkillTrees() {
        return skillManager.getAllSkillTrees();
    }

    /**
     * 플레이어가 스킬 트리 접근 가능한지 확인
     */
    public boolean canAccessSkillTree(Player player, String treeId) {
        if (player == null || treeId == null) {
            return false;
        }

        SkillTree tree = skillManager.getSkillTree(treeId);
        if (tree == null) {
            return false;
        }

        // 레벨 확인
        if (player.getLevel() < tree.getRequiredLevel()) {
            return false;
        }

        return true;
    }

    /**
     * 스킬 노드 조회
     */
    public SkillNode getSkillNode(String treeId, String nodeId) {
        if (treeId == null || nodeId == null) {
            return null;
        }

        SkillTree tree = skillManager. getSkillTree(treeId);
        if (tree == null) {
            return null;
        }

        return tree.getNodes().stream()
                .filter(node -> node.getId().equals(nodeId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 플레이어가 스킬 노드 습득 가능한지 확인
     */
    public boolean canLearnSkillNode(Player player, String treeId, String nodeId) {
        if (player == null || treeId == null || nodeId == null) {
            return false;
        }

        PlayerSkillData skillData = skillManager.getPlayerSkillData(player. getUniqueId());
        if (skillData == null) {
            return false;
        }

        SkillNode node = getSkillNode(treeId, nodeId);
        if (node == null) {
            return false;
        }

        // 스킬 포인트 확인
        if (skillData.getAvailableSkillPoints() < node.getRequiredPoints()) {
            return false;
        }

        // 이미 습득했는지 확인
        if (skillData.hasSkill(node.getSkillId())) {
            return false;
        }

        // 사전 요구 스킬 확인
        if (node.getPrerequisiteSkillId() != null) {
            if (!skillData.hasSkill(node.getPrerequisiteSkillId())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 플레이어 스킬 노드 습득
     */
    public boolean learnSkillNode(Player player, String treeId, String nodeId) {
        if (! canLearnSkillNode(player, treeId, nodeId)) {
            return false;
        }

        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        SkillNode node = getSkillNode(treeId, nodeId);

        if (skillData == null || node == null) {
            return false;
        }

        // 포인트 소모
        skillData.consumeSkillPoints(node.getRequiredPoints());

        // 스킬 습득
        Skill skill = skillManager.getSkill(node.getSkillId());
        if (skill != null) {
            LearnedSkill learned = new LearnedSkill(node.getSkillId(), skill. getName(), 1);
            skillData.addSkill(node.getSkillId(), learned);
        }

        return true;
    }

    /**
     * 스킬 트리 진행도 조회 (습득한 스킬 수 / 전체 스킬 수)
     */
    public double getTreeProgress(Player player, String treeId) {
        if (player == null || treeId == null) {
            return 0.0;
        }

        SkillTree tree = skillManager.getSkillTree(treeId);
        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());

        if (tree == null || skillData == null) {
            return 0.0;
        }

        int totalNodes = tree.getNodes().size();
        if (totalNodes == 0) {
            return 0.0;
        }

        int learnedCount = (int) tree.getNodes().stream()
                .filter(node -> skillData. hasSkill(node.getSkillId()))
                .count();

        return (double) learnedCount / totalNodes * 100;
    }

    /**
     * 스킬 트리 최대 포인트 조회
     */
    public int getTreeMaxPoints(String treeId) {
        if (treeId == null) {
            return 0;
        }

        SkillTree tree = skillManager. getSkillTree(treeId);
        if (tree == null) {
            return 0;
        }

        return tree. getMaxPoints();
    }

    /**
     * 스킬 트리 설명 조회
     */
    public String getTreeDescription(String treeId) {
        if (treeId == null) {
            return "";
        }

        SkillTree tree = skillManager.getSkillTree(treeId);
        if (tree == null) {
            return "";
        }

        return tree.getDescription();
    }
}