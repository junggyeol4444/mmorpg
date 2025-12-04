package com.multiverse.combat. data;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models.*;
import com.multiverse.combat.models.enums.*;
import java.io. File;
import java.io.IOException;
import java.util.*;

/**
 * YAML 기반 데이터 관리 클래스
 */
public class YAMLDataManager extends DataManager {
    
    private final CombatCore plugin;
    private final File dataFolder;
    private final File skillsFolder;
    private final File playersFolder;
    private FileConfiguration skillsConfig;
    
    /**
     * YAMLDataManager 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public YAMLDataManager(CombatCore plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        this.skillsFolder = new File(dataFolder, "skills");
        this.playersFolder = new File(dataFolder, "players");
        
        // 폴더 생성
        if (!dataFolder.exists()) dataFolder.mkdirs();
        if (!skillsFolder.exists()) skillsFolder.mkdirs();
        if (!playersFolder. exists()) playersFolder.mkdirs();
    }
    
    /**
     * 모든 데이터 로드
     */
    @Override
    public void loadAll() {
        plugin.getLogger().info("✓ YAML 데이터 로드 시작");
        loadSkillsConfig();
        plugin.getLogger().info("✓ YAML 데이터 로드 완료");
    }
    
    /**
     * 모든 데이터 저장
     */
    @Override
    public void saveAll() {
        plugin.getLogger().info("✓ YAML 데이터 저장 시작");
        // 플레이어 데이터는 개별 저장
        plugin.getLogger().info("✓ YAML 데이터 저장 완료");
    }
    
    /**
     * 스킬 설정 파일 로드
     */
    private void loadSkillsConfig() {
        File skillsFile = new File(skillsFolder, "skills.yml");
        
        if (!skillsFile.exists()) {
            createDefaultSkillsFile(skillsFile);
        }
        
        skillsConfig = YamlConfiguration. loadConfiguration(skillsFile);
    }
    
    /**
     * 기본 스킬 파일 생성
     */
    private void createDefaultSkillsFile(File file) {
        try {
            file.createNewFile();
            FileConfiguration config = new YamlConfiguration();
            
            // 기본 스킬 설정
            config. set("skills. warrior_slash. skill-id", "warrior_slash");
            config.set("skills.warrior_slash.name", "§c강력한 베기");
            config.set("skills.warrior_slash.type", "ACTIVE");
            config.set("skills.warrior_slash.category", "COMBAT");
            config.set("skills.warrior_slash.description", "적을 강력하게 베어 큰 데미지를 입힙니다.");
            config.set("skills.warrior_slash.max-level", 10);
            config.set("skills.warrior_slash.required-level", 1);
            config.set("skills.warrior_slash.cost. type", "STAMINA");
            config.set("skills.warrior_slash.cost. base", 10. 0);
            config.set("skills.warrior_slash.cooldown", 3000);
            config.set("skills.warrior_slash.effect.type", "DAMAGE");
            config.set("skills.warrior_slash.effect.base-damage", 50. 0);
            config.set("skills.warrior_slash.effect. damage-type", "PHYSICAL");
            
            config.save(file);
            plugin.getLogger().info("✓ 기본 스킬 파일 생성 완료");
        } catch (IOException e) {
            plugin.getLogger(). severe("스킬 파일 생성 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 모든 스킬 로드
     */
    @Override
    public Map<String, Skill> loadAllSkills() {
        Map<String, Skill> skills = new HashMap<>();
        
        if (skillsConfig == null) {
            loadSkillsConfig();
        }
        
        if (skillsConfig. contains("skills")) {
            for (String skillId : skillsConfig. getConfigurationSection("skills").getKeys(false)) {
                String path = "skills." + skillId;
                
                try {
                    Skill skill = new Skill();
                    skill.setSkillId(skillId);
                    skill.setName(skillsConfig.getString(path + ".name", skillId));
                    skill. setType(SkillType. fromString(skillsConfig.getString(path + ".type", "ACTIVE")));
                    skill.setCategory(SkillCategory.fromString(skillsConfig.getString(path + ".category", "COMBAT")));
                    skill.setDescription(skillsConfig.getString(path + ".description", ""));
                    skill.setMaxLevel(skillsConfig.getInt(path + ".max-level", 1));
                    skill.setRequiredLevel(skillsConfig.getInt(path + ".required-level", 1));
                    skill.setRequiredSkill(skillsConfig.getString(path + ".required-skill", null));
                    
                    // 비용 설정
                    String costType = skillsConfig.getString(path + ".cost.type", "NONE");
                    skill.setCostType(CostType.fromString(costType));
                    skill.setBaseCost(skillsConfig. getDouble(path + ".cost. base", 0.0));
                    
                    // 쿨다운
                    skill.setBaseCooldown(skillsConfig. getLong(path + ".cooldown", 0L));
                    
                    // 캐스팅
                    skill.setCastTime(skillsConfig.getLong(path + ".cast-time", 0L));
                    skill.setCanMove(skillsConfig.getBoolean(path + ".can-move", true));
                    
                    skills.put(skillId, skill);
                } catch (Exception e) {
                    plugin.getLogger().warning("스킬 로드 중 오류 (" + skillId + "): " + e.getMessage());
                }
            }
        }
        
        return skills;
    }
    
    /**
     * 플레이어 스킬 데이터 로드
     */
    @Override
    public Map<String, Integer> loadPlayerSkills(Player player) {
        File playerFile = getPlayerFile(player);
        Map<String, Integer> skills = new HashMap<>();
        
        if (! playerFile.exists()) {
            return skills;
        }
        
        FileConfiguration config = YamlConfiguration. loadConfiguration(playerFile);
        
        if (config.contains("skills. learned")) {
            for (String skillId : config.getConfigurationSection("skills.learned").getKeys(false)) {
                int level = config.getInt("skills.learned." + skillId + ". level", 1);
                skills.put(skillId, level);
            }
        }
        
        return skills;
    }
    
    /**
     * 플레이어 스킬 데이터 저장
     */
    @Override
    public void savePlayerSkills(Player player, Map<String, Integer> skills, String[] hotbar) {
        File playerFile = getPlayerFile(player);
        FileConfiguration config = playerFile.exists() ? 
            YamlConfiguration.loadConfiguration(playerFile) : new YamlConfiguration();
        
        // 스킬 저장
        for (Map.Entry<String, Integer> entry : skills.entrySet()) {
            config.set("skills. learned." + entry.getKey() + ".level", entry.getValue());
            config.set("skills.learned." + entry.getKey() + ". exp", 0);
        }
        
        // 핫바 저장
        for (int i = 0; i < hotbar.length; i++) {
            config.set("skills.hotbar. slot-" + (i + 1), hotbar[i]);
        }
        
        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().warning("플레이어 스킬 저장 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 플레이어 핫바 로드
     */
    @Override
    public String[] loadPlayerHotbar(Player player) {
        File playerFile = getPlayerFile(player);
        String[] hotbar = new String[5];
        
        if (!playerFile.exists()) {
            return hotbar;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        
        for (int i = 0; i < 5; i++) {
            hotbar[i] = config.getString("skills.hotbar.slot-" + (i + 1), null);
        }
        
        return hotbar;
    }
    
    /**
     * 플레이어 콤보 데이터 로드
     */
    @Override
    public ComboData loadComboData(Player player) {
        ComboData combo = new ComboData(player.getUniqueId());
        File playerFile = getPlayerFile(player);
        
        if (!playerFile.exists()) {
            return combo;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        
        combo.setComboCount(config.getInt("combat.combo. current", 0));
        combo. setMaxCombo(config.getInt("combat.combo.max-combo", 0));
        combo. setTotalCombos(config.getInt("combat.combo.total-combos", 0));
        
        return combo;
    }
    
    /**
     * 플레이어 콤보 데이터 저장
     */
    @Override
    public void saveComboData(Player player, ComboData combo) {
        File playerFile = getPlayerFile(player);
        FileConfiguration config = playerFile.exists() ? 
            YamlConfiguration.loadConfiguration(playerFile) : new YamlConfiguration();
        
        config.set("combat.combo.max-combo", combo.getMaxCombo());
        config.set("combat.combo.total-combos", combo.getTotalCombos());
        
        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().warning("콤보 데이터 저장 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 플레이어 PvP 데이터 로드
     */
    @Override
    public PvPData loadPvPData(Player player) {
        PvPData pvpData = new PvPData(player.getUniqueId(), player.getName());
        File playerFile = getPlayerFile(player);
        
        if (!playerFile.exists()) {
            return pvpData;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        
        if (config.contains("pvp")) {
            pvpData.setPvPEnabled(config.getBoolean("pvp.enabled", true));
            pvpData.setKills(config.getInt("pvp.kills", 0));
            pvpData.setDeaths(config.getInt("pvp. deaths", 0));
            pvpData.setKillStreak(config.getInt("pvp.kill-streak", 0));
            pvpData.setMaxKillStreak(config.getInt("pvp.max-kill-streak", 0));
            pvpData.setFame(config.getInt("pvp.fame", 0));
            pvpData.setInfamy(config.getInt("pvp.infamy", 0));
        }
        
        return pvpData;
    }
    
    /**
     * 플레이어 PvP 데이터 저장
     */
    @Override
    public void savePvPData(Player player, PvPData pvpData) {
        File playerFile = getPlayerFile(player);
        FileConfiguration config = playerFile.exists() ? 
            YamlConfiguration.loadConfiguration(playerFile) : new YamlConfiguration();
        
        config.set("pvp.enabled", pvpData.isPvPEnabled());
        config.set("pvp. kills", pvpData.getKills());
        config.set("pvp.deaths", pvpData.getDeaths());
        config.set("pvp.kill-streak", pvpData.getKillStreak());
        config. set("pvp.max-kill-streak", pvpData.getMaxKillStreak());
        config.set("pvp.fame", pvpData.getFame());
        config.set("pvp.infamy", pvpData. getInfamy());
        
        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().warning("PvP 데이터 저장 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 플레이어 전투 통계 로드
     */
    @Override
    public Map<String, Object> loadCombatStats(Player player) {
        Map<String, Object> stats = new HashMap<>();
        File playerFile = getPlayerFile(player);
        
        if (!playerFile.exists()) {
            initializeStats(stats);
            return stats;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        
        if (config.contains("combat.stats")) {
            stats.put("total_damage_dealt", config.getDouble("combat.stats.total-damage-dealt", 0.0));
            stats.put("total_damage_taken", config.getDouble("combat.stats.total-damage-taken", 0.0));
            stats.put("total_kills", config.getInt("combat.stats.total-kills", 0));
            stats.put("total_deaths", config.getInt("combat.stats.total-deaths", 0));
            stats. put("max_combo", config.getInt("combat. stats.max-combo", 0));
            stats.put("total_combos", config.getInt("combat.stats.total-combos", 0));
            stats.put("total_crits", config.getInt("combat.stats.total-crits", 0));
            stats.put("crit_damage", config.getDouble("combat. stats.crit-damage", 0.0));
            stats. put("times_dodged", config.getInt("combat.stats.times-dodged", 0));
        } else {
            initializeStats(stats);
        }
        
        return stats;
    }
    
    /**
     * 플레이어 전투 통계 저장
     */
    @Override
    public void saveCombatStats(Player player, Map<String, Object> stats) {
        File playerFile = getPlayerFile(player);
        FileConfiguration config = playerFile.exists() ? 
            YamlConfiguration.loadConfiguration(playerFile) : new YamlConfiguration();
        
        config.set("combat.stats. total-damage-dealt", stats. get("total_damage_dealt"));
        config.set("combat. stats.total-damage-taken", stats.get("total_damage_taken"));
        config.set("combat.stats.total-kills", stats.get("total_kills"));
        config.set("combat. stats.total-deaths", stats. get("total_deaths"));
        config.set("combat.stats. max-combo", stats.get("max_combo"));
        config. set("combat.stats.total-combos", stats.get("total_combos"));
        config. set("combat.stats.total-crits", stats.get("total_crits"));
        config. set("combat.stats.crit-damage", stats.get("crit_damage"));
        config. set("combat.stats.times-dodged", stats.get("times_dodged"));
        
        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin. getLogger().warning("전투 통계 저장 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 플레이어 데이터 삭제
     */
    @Override
    public void deletePlayerData(Player player) {
        File playerFile = getPlayerFile(player);
        if (playerFile.exists()) {
            playerFile.delete();
            plugin.getLogger().info("플레이어 데이터 삭제: " + player. getName());
        }
    }
    
    /**
     * 플레이어 데이터 존재 확인
     */
    @Override
    public boolean playerDataExists(Player player) {
        return getPlayerFile(player). exists();
    }
    
    /**
     * 데이터 백업
     */
    @Override
    public void backup() {
        File backupFolder = new File(dataFolder, "backups");
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }
        
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File timestampFolder = new File(backupFolder, timestamp);
        timestampFolder.mkdirs();
        
        // 플레이어 데이터 백업
        if (playersFolder.exists()) {
            for (File file : playersFolder.listFiles()) {
                try {
                    java.nio.file.Files.copy(file.toPath(), 
                        new File(timestampFolder, file. getName()).toPath());
                } catch (IOException e) {
                    plugin.getLogger().warning("백업 중 오류: " + e.getMessage());
                }
            }
        }
        
        plugin.getLogger().info("✓ 데이터 백업 완료: " + timestamp);
    }
    
    /**
     * 플레이어 파일 조회
     */
    private File getPlayerFile(Player player) {
        return new File(playersFolder, player.getUniqueId() + ".yml");
    }
    
    /**
     * 통계 초기화
     */
    private void initializeStats(Map<String, Object> stats) {
        stats.put("total_damage_dealt", 0. 0);
        stats.put("total_damage_taken", 0.0);
        stats.put("total_kills", 0);
        stats.put("total_deaths", 0);
        stats.put("max_combo", 0);
        stats.put("total_combos", 0);
        stats.put("total_crits", 0);
        stats.put("crit_damage", 0.0);
        stats.put("times_dodged", 0);
    }
}