package com.multiverse.skill.managers;

import com.multiverse. skill. SkillCore;
import com.multiverse. skill.data.models.LearnedSkill;
import com. multiverse.skill.data.models.PlayerSkillData;
import com.multiverse. skill.data.models. Skill;
import com.multiverse.skill.data.models. SkillEvolution;
import com.multiverse.skill.events.SkillEvolutionEvent;
import com.multiverse. skill.utils.MessageUtils;
import org. bukkit. Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SkillEvolutionManager {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final SkillLearningManager learningManager;
    private final Map<String, SkillEvolution> evolutions = new HashMap<>();

    public SkillEvolutionManager(SkillCore plugin, SkillManager skillManager, SkillLearningManager learningManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.learningManager = learningManager;
        loadEvolutions();
    }

    /**
     * 모든 진화 데이터 로드
     */
    private void loadEvolutions() {
        plugin.getEvolutionDataLoader().loadAllEvolutions(). forEach(evolution ->
            evolutions.put(evolution.getEvolutionId(), evolution)
        );
        plugin.getLogger().info("✅ " + evolutions.size() + "개의 스킬 진화가 로드되었습니다.");
    }

    /**
     * 진화 가능 여부 확인
     */
    public boolean canEvolve(Player player, String skillId) {
        List<SkillEvolution> availableEvolutions = getAvailableEvolutions(player, skillId);
        return !availableEvolutions.isEmpty();
    }

    /**
     * 플레이어가 사용 가능한 진화 목록 조회
     */
    public List<SkillEvolution> getAvailableEvolutions(Player player, String skillId) {
        List<SkillEvolution> available = new ArrayList<>();

        for (SkillEvolution evolution : evolutions.values()) {
            if (! evolution.getFromSkillId().equals(skillId)) {
                continue;
            }

            if (checkRequirements(player, evolution)) {
                available. add(evolution);
            }
        }

        return available;
    }

    /**
     * 진화 조건 체크
     */
    public boolean checkRequirements(Player player, SkillEvolution evolution) {
        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player. getUniqueId());
        
        // 스킬 레벨 확인
        LearnedSkill learnedSkill = skillData.getSkills().get(evolution.getFromSkillId());
        if (learnedSkill == null || learnedSkill.getLevel() < evolution.getRequiredSkillLevel()) {
            return false;
        }

        // 플레이어 레벨 확인
        if (player.getLevel() < evolution.getRequiredPlayerLevel()) {
            return false;
        }

        // 사용 횟수 확인
        if (learnedSkill.getTimesUsed() < evolution.getRequiredUseCount()) {
            return false;
        }

        // 필요 아이템 확인
        if (evolution.getRequiredItems() != null && ! evolution.getRequiredItems().isEmpty()) {
            for (ItemStack requiredItem : evolution.getRequiredItems()) {
                if (! player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                    return false;
                }
            }
        }

        // 필요 퀘스트 확인 (연동 필요)
        if (evolution.getRequiredQuest() != null && !evolution.getRequiredQuest().isEmpty()) {
            // QuestCore와 연동하여 완료 여부 확인
        }

        return true;
    }

    /**
     * 스킬 진화 실행
     */
    public void evolveSkill(Player player, String evolutionId) {
        SkillEvolution evolution = evolutions.get(evolutionId);
        if (evolution == null) {
            MessageUtils.sendMessage(player, "§c진화 정보를 찾을 수 없습니다.");
            return;
        }

        if (!checkRequirements(player, evolution)) {
            MessageUtils.sendMessage(player, "§c진화 조건을 만족하지 않습니다.");
            return;
        }

        PlayerSkillData skillData = plugin. getPlayerDataLoader().loadPlayerData(player.getUniqueId());

        // 진화 이벤트 발생
        SkillEvolutionEvent event = new SkillEvolutionEvent(player, evolution. getFromSkillId(), evolution.getToSkillId());
        Bukkit.getPluginManager(). callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        // 필요 아이템 제거
        if (evolution.getRequiredItems() != null && ! evolution.getRequiredItems().isEmpty()) {
            for (ItemStack requiredItem : evolution.getRequiredItems()) {
                player. getInventory().removeItem(requiredItem);
            }
        }

        // 기존 스킬 처리
        LearnedSkill oldSkill = skillData.getSkills().get(evolution.getFromSkillId());
        int oldLevel = oldSkill != null ? oldSkill.getLevel() : 0;

        skillData.getSkills().remove(evolution.getFromSkillId());

        // 새로운 스킬 추가 또는 업그레이드
        switch (evolution.getType()) {
            case ENHANCE -> {
                // 기존 스킬 강화
                if (! skillData.getSkills().containsKey(evolution.getToSkillId())) {
                    LearnedSkill newSkill = new LearnedSkill();
                    newSkill.setSkillId(evolution.getToSkillId());
                    newSkill.setLevel(oldLevel);
                    newSkill.setExperience(oldSkill != null ? oldSkill.getExperience() : 0);
                    skillData.getSkills().put(evolution.getToSkillId(), newSkill);
                } else {
                    LearnedSkill existingSkill = skillData.getSkills().get(evolution.getToSkillId());
                    existingSkill.setLevel(existingSkill.getLevel() + oldLevel);
                }
            }
            case MUTATE -> {
                // 완전히 다른 스킬로 변경
                LearnedSkill newSkill = new LearnedSkill();
                newSkill.setSkillId(evolution.getToSkillId());
                newSkill. setLevel(1);
                newSkill.setExperience(0);
                skillData.getSkills().put(evolution.getToSkillId(), newSkill);
            }
            case MERGE -> {
                // 두 스킬 합성 (필요시 구현)
                LearnedSkill newSkill = new LearnedSkill();
                newSkill.setSkillId(evolution.getToSkillId());
                newSkill. setLevel(Math.max(oldLevel / 2, 1));
                newSkill.setExperience(0);
                skillData.getSkills().put(evolution.getToSkillId(), newSkill);
            }
        }

        plugin.getPlayerDataLoader().savePlayerData(player.getUniqueId());

        MessageUtils.sendMessage(player, String.format("§a스킬이 진화했습니다! §e%s §a→ §e%s",
            evolution.getFromSkillId(), evolution.getToSkillId()));

        // 진화 공지
        if (plugin.getConfig().getBoolean("evolution.announce", true)) {
            Bukkit.broadcastMessage(String.format("§e[진화] §b%s§e 플레이어가 스킬을 진화시켰습니다! ",
                player.getName()));
        }
    }

    /**
     * 특정 스킬의 최종 형태 조회
     */
    public String getFinalForm(String skillId) {
        for (SkillEvolution evolution : evolutions.values()) {
            if (evolution.getFromSkillId().equals(skillId)) {
                return getFinalForm(evolution.getToSkillId());
            }
        }
        return skillId;
    }

    /**
     * 진화 체인 조회
     */
    public List<String> getEvolutionChain(String skillId) {
        List<String> chain = new ArrayList<>();
        chain.add(skillId);

        String current = skillId;
        while (true) {
            String next = getNextEvolution(current);
            if (next == null || next.equals(current)) {
                break;
            }
            chain.add(next);
            current = next;
        }

        return chain;
    }

    /**
     * 다음 진화 스킬 조회
     */
    private String getNextEvolution(String skillId) {
        for (SkillEvolution evolution : evolutions.values()) {
            if (evolution.getFromSkillId(). equals(skillId)) {
                return evolution.getToSkillId();
            }
        }
        return null;
    }

    /**
     * 진화 통계
     */
    public Map<String, Object> getEvolutionStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_evolutions", evolutions.size());
        
        long enhanceCount = evolutions.values().stream()
            .filter(e -> e.getType(). name().equals("ENHANCE"))
            .count();
        stats.put("enhance_evolutions", enhanceCount);

        long mutateCount = evolutions.values().stream()
            . filter(e -> e.getType().name().equals("MUTATE"))
            .count();
        stats.put("mutate_evolutions", mutateCount);

        return stats;
    }
}