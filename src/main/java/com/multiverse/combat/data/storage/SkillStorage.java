package com.multiverse.combat.data.  storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models.  Skill;
import com.multiverse.combat.models. SkillEffect;
import com.multiverse. combat.models.enums.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 스킬 저장소 클래스
 * 스킬 데이터를 YAML에서 로드/저장합니다. 
 */
public class SkillStorage {
    
    private final CombatCore plugin;
    private final File skillsFolder;
    
    /**
     * SkillStorage 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     * @param skillsFolder 스킬 폴더 경로
     */
    public SkillStorage(CombatCore plugin, File skillsFolder) {
        this.plugin = plugin;
        this.skillsFolder = skillsFolder;
    }
    
    /**
     * 모든 스킬 로드
     * @return 스킬 ID와 Skill 객체의 맵
     */
    public Map<String, Skill> loadAllSkills() {
        Map<String, Skill> skills = new HashMap<>();
        
        if (!skillsFolder.exists()) {
            skillsFolder.mkdirs();
        }
        
        // skills.yml 파일에서 로드
        File skillsFile = new File(skillsFolder, "skills.yml");
        if (! skillsFile.exists()) {
            createDefaultSkillsFile(skillsFile);
        }
        
        FileConfiguration config = YamlConfiguration. loadConfiguration(skillsFile);
        
        if (config. contains("skills")) {
            for (String skillId : config.getConfigurationSection("skills").getKeys(false)) {
                Skill skill = loadSkill(config, skillId);
                if (skill != null) {
                    skills.put(skillId, skill);
                }
            }
        }
        
        plugin.getLogger().info("✓ " + skills.size() + "개의 스킬이 로드되었습니다.");
        return skills;
    }
    
    /**
     * 단일 스킬 로드
     * @param config 파일 설정
     * @param skillId 스킬 ID
     * @return 스킬 객체
     */
    private Skill loadSkill(FileConfiguration config, String skillId) {
        String path = "skills." + skillId;
        
        try {
            Skill skill = new Skill();
            
            // 기본 정보
            skill.setSkillId(skillId);
            skill.setName(config.getString(path + ".name", skillId));
            skill.setDescription(config.getString(path + ".description", ""));
            
            // 타입과 카테고리
            String typeStr = config.getString(path + ". type", "ACTIVE");
            SkillType type = SkillType.valueOf(typeStr.toUpperCase());
            skill.setType(type);
            
            String categoryStr = config.getString(path + ".category", "COMBAT");
            SkillCategory category = SkillCategory.valueOf(categoryStr.toUpperCase());
            skill.setCategory(category);
            
            // Lore
            List<String> lore = config. getStringList(path + ".lore");
            skill.setLore(lore != null ? lore : new ArrayList<>());
            
            // 레벨
            skill.setCurrentLevel(1);
            skill.setMaxLevel(config.getInt(path + ".max-level", 1));
            
            // 필수 조건
            skill.setRequiredLevel(config.getInt(path + ".requirements.level", 1));
            skill.setRequiredStatPoints(config.getInt(path + ".requirements.stat-points", 0));
            skill.setRequiredSkill(config.getString(path + ". requirements.previous-skill", null));
            
            // 비용
            String costTypeStr = config.getString(path + ".cost.type", "NONE");
            CostType costType = CostType.valueOf(costTypeStr.toUpperCase());
            skill.setCostType(costType);
            skill.setBaseCost(config.getDouble(path + ".cost.base", 0.0));
            
            // 쿨다운
            skill.setBaseCooldown(config.getLong(path + ".cooldown", 0L));
            
            // 캐스팅
            skill.setCastTime(config.getLong(path + ".cast-time", 0L));
            skill.setCanMove(config.getBoolean(path + ".can-move", true));
            
            // 효과
            SkillEffect effect = loadSkillEffect(config, path + ".effect");
            skill.setSkillEffect(effect);
            
            plugin.getLogger().fine("스킬 로드: " + skillId);
            return skill;
            
        } catch (Exception e) {
            plugin.getLogger().warning("스킬 로드 실패 (" + skillId + "): " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 스킬 효과 로드
     * @param config 파일 설정
     * @param path 경로
     * @return 스킬 효과 객체
     */
    private SkillEffect loadSkillEffect(FileConfiguration config, String path) {
        SkillEffect effect = new SkillEffect();
        
        // 효과 타입
        String effectTypeStr = config.getString(path + ".type", "DAMAGE");
        EffectType effectType = EffectType.valueOf(effectTypeStr.toUpperCase());
        effect.setType(effectType);
        
        // 데미지
        effect.setBaseDamage(config.getDouble(path + ".base-damage", 0.0));
        
        String damageTypeStr = config.getString(path + ".damage-type", "PHYSICAL");
        DamageType damageType = DamageType. valueOf(damageTypeStr.toUpperCase());
        effect.setDamageType(damageType);
        
        effect.setDamageScaling(config.getDouble(path + ".damage-scaling", 1.0));
        
        // 대상 타입
        String targetTypeStr = config.getString(path + ".target-type", "TARGET");
        TargetType targetType = TargetType.valueOf(targetTypeStr.toUpperCase());
        effect.setTargetType(targetType);
        
        // 범위
        effect.setRange(config.getDouble(path + ".range", 5.0));
        effect.setRadius(config.getDouble(path + ".radius", 0.0));
        
        // 투사체
        effect.setProjectile(config.getBoolean(path + ".is-projectile", false));
        effect.setProjectileSpeed(config.getDouble(path + ".projectile-speed", 1.0));
        
        // 지속 효과
        effect. setDuration(config.getLong(path + ".duration", 0L));
        effect.setTickInterval(config.getInt(path + ".tick-interval", 500));
        
        // 파라미터
        Map<String, Object> parameters = new HashMap<>();
        if (config.contains(path + ". parameters")) {
            for (String key : config.getConfigurationSection(path + ".parameters").getKeys(false)) {
                parameters.put(key, config.get(path + ".parameters." + key));
            }
        }
        effect.setParameters(parameters);
        
        return effect;
    }
    
    /**
     * 스킬 저장
     * @param skillId 스킬 ID
     * @param skill 스킬 객체
     */
    public void saveSkill(String skillId, Skill skill) {
        File skillsFile = new File(skillsFolder, "skills. yml");
        FileConfiguration config = YamlConfiguration. loadConfiguration(skillsFile);
        
        String path = "skills." + skillId;
        
        config.set(path + ".skill-id", skill.getSkillId());
        config.set(path + ".name", skill.getName());
        config.set(path + ".description", skill.getDescription());
        config.set(path + ".type", skill.getType().name());
        config.set(path + ".category", skill.getCategory().name());
        config.set(path + ".lore", skill.getLore());
        
        config.set(path + ". max-level", skill.getMaxLevel());
        
        config.set(path + ". requirements.level", skill.getRequiredLevel());
        config.set(path + ".requirements.stat-points", skill.getRequiredStatPoints());
        config.set(path + ".requirements.previous-skill", skill.getRequiredSkill());
        
        config.set(path + ". cost.type", skill.getCostType().name());
        config. set(path + ".cost.base", skill.getBaseCost());
        
        config.set(path + ". cooldown", skill.getBaseCooldown());
        config.set(path + ".cast-time", skill.getCastTime());
        config.set(path + ".can-move", skill.isCanMove());
        
        // 효과 저장
        if (skill.getSkillEffect() != null) {
            SkillEffect effect = skill.getSkillEffect();
            config. set(path + ".effect.type", effect.getType().name());
            config.set(path + ".effect.base-damage", effect.getBaseDamage());
            config.set(path + ". effect.damage-type", effect. getDamageType().name());
            config.set(path + ". effect.damage-scaling", effect. getDamageScaling());
            config.set(path + ".effect.target-type", effect.getTargetType().name());
            config. set(path + ".effect.range", effect.getRange());
            config.set(path + ".effect.radius", effect.getRadius());
            config.set(path + ".effect.is-projectile", effect.isProjectile());
            config.set(path + ".effect.projectile-speed", effect.getProjectileSpeed());
            config.set(path + ".effect.duration", effect. getDuration());
            config.set(path + ".effect.tick-interval", effect.getTickInterval());
        }
        
        try {
            config.save(skillsFile);
            plugin.getLogger().info("✓ 스킬 저장: " + skillId);
        } catch (IOException e) {
            plugin.getLogger().warning("스킬 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 기본 스킬 파일 생성
     * @param file 파일 경로
     */
    private void createDefaultSkillsFile(File file) {
        try {
            file.createNewFile();
            FileConfiguration config = new YamlConfiguration();
            
            // 전사 - 강력한 베기
            config.set("skills.warrior_slash. skill-id", "warrior_slash");
            config.set("skills. warrior_slash.name", "§c강력한 베기");
            config.set("skills.warrior_slash.type", "ACTIVE");
            config.set("skills.warrior_slash.category", "COMBAT");
            config.set("skills.warrior_slash.description", "적을 강력하게 베어 큰 데미지를 입힙니다.");
            config.set("skills.warrior_slash.max-level", 10);
            config.set("skills.warrior_slash.requirements.level", 1);
            config.set("skills.warrior_slash.cost.type", "STAMINA");
            config.set("skills.warrior_slash.cost. base", 10.0);
            config.set("skills.warrior_slash.cooldown", 3000);
            config.set("skills.warrior_slash.effect.type", "DAMAGE");
            config.set("skills.warrior_slash.effect.base-damage", 50.0);
            config.set("skills.warrior_slash.effect.damage-type", "PHYSICAL");
            config. set("skills.warrior_slash. effect.target-type", "TARGET");
            config.set("skills.warrior_slash.effect.range", 5.0);
            
            // 전사 - 돌진
            config.set("skills. warrior_charge.skill-id", "warrior_charge");
            config.set("skills.warrior_charge.name", "§c돌진");
            config.set("skills.warrior_charge.type", "ACTIVE");
            config.set("skills.warrior_charge.category", "MOVEMENT");
            config.set("skills.warrior_charge.description", "전방으로 돌진합니다.");
            config.set("skills.warrior_charge.max-level", 5);
            config.set("skills.warrior_charge.requirements.level", 5);
            config.set("skills.warrior_charge.cost.type", "STAMINA");
            config.set("skills.warrior_charge.cost.base", 20.0);
            config.set("skills.warrior_charge.cooldown", 5000);
            config.set("skills.warrior_charge.effect. type", "TELEPORT");
            config.set("skills.warrior_charge.effect. target-type", "LINE");
            config.set("skills.warrior_charge.effect.range", 10.0);
            
            config.save(file);
            plugin.getLogger().info("✓ 기본 스킬 파일 생성 완료");
        } catch (IOException e) {
            plugin.getLogger().severe("기본 스킬 파일 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 스킬 존재 확인
     * @param skillId 스킬 ID
     * @return 존재하면 true
     */
    public boolean skillExists(String skillId) {
        File skillsFile = new File(skillsFolder, "skills. yml");
        if (!skillsFile.exists()) return false;
        
        FileConfiguration config = YamlConfiguration. loadConfiguration(skillsFile);
        return config.contains("skills." + skillId);
    }
    
    /**
     * 스킬 삭제
     * @param skillId 스킬 ID
     */
    public void deleteSkill(String skillId) {
        File skillsFile = new File(skillsFolder, "skills. yml");
        FileConfiguration config = YamlConfiguration. loadConfiguration(skillsFile);
        
        config.set("skills." + skillId, null);
        
        try {
            config.save(skillsFile);
            plugin.getLogger(). info("✓ 스킬 삭제: " + skillId);
        } catch (IOException e) {
            plugin. getLogger().warning("스킬 삭제 실패: " + e.getMessage());
        }
    }
}