package com.multiverse.pet.data;

import com.multiverse.pet.PetCore;
import com.multiverse. pet.model.skill.PetSkill;
import com. multiverse.pet. model.skill. SkillEffect;
import com. multiverse.pet. model.skill. SkillType;
import org. bukkit.configuration. ConfigurationSection;
import org.bukkit.configuration.file. YamlConfiguration;

import java.io. File;
import java.util.*;

/**
 * 펫 스킬 로더
 * YAML 파일에서 스킬 템플릿 로드
 */
public class PetSkillLoader {

    private final PetCore plugin;
    private final File skillsFolder;

    public PetSkillLoader(PetCore plugin) {
        this.plugin = plugin;
        this.skillsFolder = new File(plugin.getDataFolder(), "pets/skills");

        if (!skillsFolder.exists()) {
            skillsFolder. mkdirs();
            saveDefaultSkillFiles();
        }
    }

    /**
     * 기본 스킬 파일 저장
     */
    private void saveDefaultSkillFiles() {
        String[] defaultFiles = {
            "combat_skills.yml",
            "gathering_skills.yml",
            "support_skills.yml",
            "special_skills.yml"
        };

        for (String fileName :  defaultFiles) {
            File file = new File(skillsFolder, fileName);
            if (!file.exists()) {
                try {
                    plugin.saveResource("pets/skills/" + fileName, false);
                } catch (Exception e) {
                    plugin.getLogger().warning("기본 스킬 파일 생성 실패:  " + fileName);
                }
            }
        }
    }

    /**
     * 모든 스킬 로드
     */
    public Map<String, PetSkill> loadAllSkills() {
        Map<String, PetSkill> skills = new HashMap<>();

        File[] files = skillsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return skills;

        for (File file : files) {
            try {
                Map<String, PetSkill> loaded = loadSkillsFromFile(file);
                skills.putAll(loaded);

                if (plugin. isDebugMode()) {
                    plugin.getLogger().info("[DEBUG] 스킬 파일 로드:  " + file.getName() + 
                            " (" + loaded.size() + "개)");
                }

            } catch (Exception e) {
                plugin.getLogger().warning("스킬 파일 로드 실패:  " + file.getName() + " - " + e.getMessage());
            }
        }

        plugin. getLogger().info("총 " + skills.size() + "개의 펫 스킬 로드됨");
        return skills;
    }

    /**
     * 파일에서 스킬 로드
     */
    private Map<String, PetSkill> loadSkillsFromFile(File file) {
        Map<String, PetSkill> skills = new HashMap<>();
        YamlConfiguration config = YamlConfiguration. loadConfiguration(file);

        for (String skillId : config. getKeys(false)) {
            ConfigurationSection section = config. getConfigurationSection(skillId);
            if (section == null) continue;

            try {
                PetSkill skill = loadSkillFromSection(skillId, section);
                skills. put(skillId, skill);
            } catch (Exception e) {
                plugin. getLogger().warning("스킬 로드 실패:  " + skillId + " - " + e.getMessage());
            }
        }

        return skills;
    }

    /**
     * 섹션에서 스킬 로드
     */
    private PetSkill loadSkillFromSection(String skillId, ConfigurationSection section) {
        PetSkill skill = new PetSkill();

        skill.setSkillId(skillId);
        skill.setName(section.getString("name", skillId));
        skill.setDescription(section.getString("description", ""));

        // 타입
        String typeStr = section.getString("type", "ATTACK");
        try {
            skill.setType(SkillType.valueOf(typeStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            skill.setType(SkillType.ATTACK);
        }

        // 패시브 여부
        skill.setPassive(section.getBoolean("passive", false));

        // 레벨
        skill.setMaxLevel(section.getInt("max-level", 5));
        skill.setCurrentLevel(1);

        // 쿨다운
        skill.setCooldown(section. getInt("cooldown", 10));

        // 마나/에너지 비용
        skill.setManaCost(section. getDouble("mana-cost", 0));
        skill.setEnergyCost(section. getDouble("energy-cost", 0));

        // 요구 레벨
        skill.setRequiredLevel(section.getInt("required-level", 1));

        // 효과
        ConfigurationSection effectsSection = section.getConfigurationSection("effects");
        if (effectsSection != null) {
            Map<String, Double> effects = new HashMap<>();
            for (String key : effectsSection. getKeys(false)) {
                effects. put(key, effectsSection.getDouble(key));
            }
            skill. setEffects(effects);
        }

        // 레벨당 효과 증가
        ConfigurationSection scalingSection = section. getConfigurationSection("scaling");
        if (scalingSection != null) {
            Map<String, Double> scaling = new HashMap<>();
            for (String key : scalingSection.getKeys(false)) {
                scaling.put(key, scalingSection. getDouble(key));
            }
            skill.setScaling(scaling);
        }

        // 강화 비용
        List<Integer> upgradeCosts = section.getIntegerList("upgrade-costs");
        if (!upgradeCosts.isEmpty()) {
            skill.setUpgradeCosts(upgradeCosts);
        }

        // 타겟 타입
        skill.setTargetType(section.getString("target-type", "ENEMY"));

        // 범위
        skill. setRange(section.getDouble("range", 5.0));
        skill.setAoeRadius(section.getDouble("aoe-radius", 0));

        // 파티클/사운드
        skill.setParticle(section.getString("particle"));
        skill.setSound(section.getString("sound"));

        // 스킬 효과 목록
        ConfigurationSection skillEffectsSection = section.getConfigurationSection("skill-effects");
        if (skillEffectsSection != null) {
            List<SkillEffect> skillEffects = new ArrayList<>();
            for (String effectId : skillEffectsSection.getKeys(false)) {
                ConfigurationSection effectSection = skillEffectsSection.getConfigurationSection(effectId);
                if (effectSection != null) {
                    SkillEffect effect = loadSkillEffect(effectId, effectSection);
                    skillEffects.add(effect);
                }
            }
            skill.setSkillEffects(skillEffects);
        }

        return skill;
    }

    /**
     * 스킬 효과 로드
     */
    private SkillEffect loadSkillEffect(String effectId, ConfigurationSection section) {
        SkillEffect effect = new SkillEffect();

        effect.setEffectId(effectId);
        effect.setType(section.getString("type", "DAMAGE"));
        effect.setValue(section.getDouble("value", 0));
        effect.setDuration(section.getInt("duration", 0));
        effect.setChance(section.getDouble("chance", 100));
        effect.setAmplifier(section. getInt("amplifier", 0));
        effect.setStackable(section.getBoolean("stackable", false));

        return effect;
    }

    /**
     * 스킬 파일 리로드
     */
    public Map<String, PetSkill> reload() {
        return loadAllSkills();
    }
}