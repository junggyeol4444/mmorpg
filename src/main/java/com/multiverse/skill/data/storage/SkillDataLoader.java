package com.multiverse.skill.data.storage;

import com.multiverse.skill.SkillCore;
import com.multiverse. skill.data.models.*;
import com.multiverse.skill.data. enums.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io. File;
import java.util.*;

/**
 * 스킬 데이터 로더
 */
public class SkillDataLoader {

    private final SkillCore plugin;
    private final DataStorage storage;
    private final Map<String, Skill> skillCache;

    public SkillDataLoader(SkillCore plugin, DataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.skillCache = new HashMap<>();
    }

    /**
     * 모든 스킬 로드
     */
    public List<Skill> loadAllSkills() {
        List<Skill> skills = new ArrayList<>();
        File skillsFolder = new File(plugin.getDataFolder(), "skills");

        if (! skillsFolder.exists()) {
            plugin.getLogger().warning("⚠️ 스킬 폴더가 없습니다: " + skillsFolder.getPath());
            return skills;
        }

        // 모든 카테고리 폴더 순회
        File[] categories = skillsFolder.listFiles(File::isDirectory);
        if (categories == null) {
            return skills;
        }

        for (File category : categories) {
            // 카테고리 내 모든 YAML 파일 로드
            File[] skillFiles = category.listFiles((d, name) -> name.endsWith(".yml"));
            if (skillFiles == null) {
                continue;
            }

            for (File skillFile : skillFiles) {
                try {
                    Skill skill = loadSkillFromFile(skillFile);
                    if (skill != null) {
                        skills.add(skill);
                        skillCache.put(skill.getSkillId(), skill);
                    }
                } catch (Exception e) {
                    plugin.getLogger(). warning("스킬 로드 실패: " + skillFile.getName());
                    e.printStackTrace();
                }
            }
        }

        return skills;
    }

    /**
     * 파일에서 스킬 로드
     */
    private Skill loadSkillFromFile(File file) {
        FileConfiguration config = YamlConfiguration. loadConfiguration(file);
        
        Skill skill = new Skill();
        skill.setSkillId(config.getString("id", file.getName().replace(".yml", "")));
        skill.setName(config.getString("name", "Unknown"));
        skill.setDescription(config.getString("description", ""));
        skill.setLore(config.getStringList("lore"));
        
        // 타입
        String typeString = config.getString("type", "ACTIVE");
        try {
            skill.setType(SkillType.valueOf(typeString));
        } catch (IllegalArgumentException e) {
            skill.setType(SkillType.ACTIVE);
        }

        skill.setCategory(config.getString("category", "general"));

        // 레벨
        skill.setMaxLevel(config.getInt("max-level", 10));

        // 요구사항
        skill.setRequiredLevel(config.getInt("required-level", 1));
        skill.setRequiredClass(config.getString("required-class", ""));
        skill.setPrerequisites(config.getStringList("prerequisites"));

        // 비용
        skill.setBaseCost(config.getDouble("base-cost", 0));
        skill.setCostPerLevel(config.getDouble("cost-per-level", 0));
        skill.setCostType(config.getString("cost-type", "MANA"));

        // 쿨다운
        skill.setBaseCooldown(config.getLong("base-cooldown", 1000));
        skill.setCooldownReductionPerLevel(config.getLong("cooldown-reduction-per-level", 0));

        // 캐스팅
        skill.setCastTime(config.getLong("cast-time", 0));
        skill.setChanneling(config.getBoolean("channeling", false));
        skill.setCancelOnMove(config.getBoolean("cancel-on-move", false));
        skill.setCancelOnDamage(config.getBoolean("cancel-on-damage", false));

        // 기본값
        skill.setBaseValue(config.getDouble("base-value", 0));
        skill.setPerLevelValue(config.getDouble("per-level-value", 0));

        // 스킬 트리
        skill.setSkillTreeId(config.getString("skill-tree-id", ""));

        return skill;
    }

    /**
     * 캐시에서 스킬 조회
     */
    public Skill getSkillFromCache(String skillId) {
        return skillCache.get(skillId);
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        skillCache.clear();
    }
}