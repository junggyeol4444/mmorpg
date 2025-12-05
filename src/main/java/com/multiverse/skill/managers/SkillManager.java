package com.multiverse.skill.managers;

import com.multiverse.skill.SkillCore;
import com.multiverse. skill.data.models. Skill;
import org.bukkit. Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SkillManager {

    private final SkillCore plugin;
    private final Map<String, Skill> skills = new ConcurrentHashMap<>();

    public SkillManager(SkillCore plugin) {
        this.plugin = plugin;
        loadAllSkills();
    }

    /**
     * 모든 스킬 로드
     */
    private void loadAllSkills() {
        plugin.getSkillDataLoader().loadAllSkills(). forEach(skill -> 
            skills.put(skill. getSkillId(), skill)
        );
        plugin.getLogger().info("✅ " + skills.size() + "개의 스킬이 로드되었습니다.");
    }

    /**
     * 스킬 ID로 스킬 조회
     */
    public Skill getSkill(String skillId) {
        return skills.get(skillId);
    }

    /**
     * 모든 스킬 조회
     */
    public Collection<Skill> getAllSkills() {
        return new ArrayList<>(skills.values());
    }

    /**
     * 스킬이 존재하는지 확인
     */
    public boolean skillExists(String skillId) {
        return skills.containsKey(skillId);
    }

    /**
     * 스킬 등록 (동적 로드용)
     */
    public void registerSkill(Skill skill) {
        skills.put(skill.getSkillId(), skill);
    }

    /**
     * 스킬 제거
     */
    public void unregisterSkill(String skillId) {
        skills.remove(skillId);
    }

    /**
     * 카테고리별 스킬 조회
     */
    public List<Skill> getSkillsByCategory(String category) {
        List<Skill> result = new ArrayList<>();
        skills.values().forEach(skill -> {
            if (skill.getCategory().equalsIgnoreCase(category)) {
                result.add(skill);
            }
        });
        return result;
    }

    /**
     * 스킬 타입별 조회
     */
    public List<Skill> getSkillsByType(String type) {
        List<Skill> result = new ArrayList<>();
        skills.values(). forEach(skill -> {
            if (skill.getType().name(). equalsIgnoreCase(type)) {
                result.add(skill);
            }
        });
        return result;
    }

    /**
     * 스킬 데이터 새로고침
     */
    public void reloadSkills() {
        skills.clear();
        loadAllSkills();
        plugin.getLogger().info("✅ 스킬이 새로고침되었습니다.");
    }

    /**
     * 스킬 기본 데미지 계산
     */
    public double calculateBaseDamage(Skill skill, int level) {
        double baseDamage = skill.getBaseValue();
        double perLevelBonus = skill.getPerLevelValue();
        return baseDamage + (perLevelBonus * (level - 1));
    }

    /**
     * 스킬 쿨다운 계산
     */
    public long calculateCooldown(Skill skill, int level) {
        long baseCooldown = skill.getBaseCooldown();
        long cooldownReduction = skill.getCooldownReductionPerLevel();
        return Math.max(0, baseCooldown - (cooldownReduction * (level - 1)));
    }

    /**
     * 스킬 비용 계산
     */
    public double calculateCost(Skill skill, int level) {
        double baseCost = skill.getBaseCost();
        double costIncrease = skill.getCostPerLevel();
        return baseCost + (costIncrease * (level - 1));
    }

    /**
     * 스킬 통계 반환
     */
    public Map<String, Object> getSkillStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_skills", skills.size());
        stats.put("active_skills", skills.values().stream().filter(s -> s.getType(). name().equals("ACTIVE")).count());
        stats.put("passive_skills", skills.values().stream().filter(s -> s.getType().name().equals("PASSIVE")).count());
        return stats;
    }
}