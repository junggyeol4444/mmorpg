package com. multiverse.combat. data. storage;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models.ComboData;
import com.multiverse. combat.models.PvPData;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 플레이어 저장소 클래스
 * 플레이어 데이터를 YAML에서 로드/저장합니다.
 */
public class PlayerStorage {
    
    private final CombatCore plugin;
    private final File playersFolder;
    
    /**
     * PlayerStorage 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     * @param playersFolder 플레이어 폴더 경로
     */
    public PlayerStorage(CombatCore plugin, File playersFolder) {
        this.plugin = plugin;
        this.playersFolder = playersFolder;
    }
    
    /**
     * 플레이어 파일 조회
     * @param player 플레이어
     * @return 파일 객체
     */
    private File getPlayerFile(Player player) {
        return new File(playersFolder, player.getUniqueId() + ".yml");
    }
    
    /**
     * 플레이어 파일 로드
     * @param player 플레이어
     * @return 파일 설정
     */
    private FileConfiguration getPlayerConfig(Player player) {
        File playerFile = getPlayerFile(player);
        
        if (!playerFile.exists()) {
            createPlayerFile(player, playerFile);
        }
        
        return YamlConfiguration. loadConfiguration(playerFile);
    }
    
    /**
     * 기본 플레이어 파일 생성
     * @param player 플레이어
     * @param file 파일 경로
     */
    private void createPlayerFile(Player player, File file) {
        try {
            file.createNewFile();
            FileConfiguration config = new YamlConfiguration();
            
            config.set("player.uuid", player.getUniqueId().toString());
            config.set("player.name", player.getName());
            config.set("player.join-time", System.currentTimeMillis());
            
            // 기본 스킬 데이터
            config.set("skills.learned", new ArrayList<>());
            config.set("skills.cooldowns", new HashMap<>());
            config.set("skills.hotbar", new String[5]);
            
            // 기본 전투 데이터
            config. set("combat.stats. total-damage-dealt", 0. 0);
            config.set("combat.stats.total-damage-taken", 0.0);
            config.set("combat.stats.total-kills", 0);
            config. set("combat.stats.total-deaths", 0);
            config.set("combat.combo. max-combo", 0);
            config.set("combat.combo.total-combos", 0);
            config.set("combat.critical. total-crits", 0);
            config.set("combat.critical.crit-damage", 0.0);
            
            // 기본 PvP 데이터
            config.set("pvp.enabled", true);
            config.set("pvp.kills", 0);
            config. set("pvp.deaths", 0);
            config.set("pvp.kda", 0.0);
            config.set("pvp.kill-streak", 0);
            config. set("pvp.max-kill-streak", 0);
            config.set("pvp.fame", 0);
            config.set("pvp.infamy", 0);
            config.set("pvp.rank", 0);
            
            // 상태이상
            config.set("status-effects.current", new ArrayList<>());
            config.set("status-effects.immunities", new HashMap<>());
            
            // 설정
            config.set("preferences.show-damage-numbers", true);
            config.set("preferences.show-combo-counter", true);
            config.set("preferences.auto-aim", false);
            
            config.save(file);
            plugin.getLogger().fine("플레이어 파일 생성: " + player.getName());
        } catch (IOException e) {
            plugin. getLogger().warning("플레이어 파일 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 플레이어 스킬 데이터 로드
     * @param player 플레이어
     * @return 스킬 ID와 레벨의 맵
     */
    public Map<String, Integer> loadPlayerSkills(Player player) {
        FileConfiguration config = getPlayerConfig(player);
        Map<String, Integer> skills = new HashMap<>();
        
        if (config.contains("skills.learned")) {
            for (String skillId : config.getConfigurationSection("skills.learned").getKeys(false)) {
                int level = config.getInt("skills.learned." + skillId + ".level", 1);
                skills.put(skillId, level);
            }
        }
        
        return skills;
    }
    
    /**
     * 플레이어 스킬 데이터 저장
     * @param player 플레이어
     * @param skills 스킬 ID와 레벨의 맵
     * @param hotbar 핫바 스킬 배열
     */
    public void savePlayerSkills(Player player, Map<String, Integer> skills, String[] hotbar) {
        File playerFile = getPlayerFile(player);
        FileConfiguration config = getPlayerConfig(player);
        
        // 스킬 저장
        config.set("skills.learned", null);  // 기존 데이터 제거
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
            plugin.getLogger().warning("플레이어 스킬 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 플레이어 핫바 로드
     * @param player 플레이어
     * @return 핫바 스킬 ID 배열
     */
    public String[] loadPlayerHotbar(Player player) {
        FileConfiguration config = getPlayerConfig(player);
        String[] hotbar = new String[5];
        
        for (int i = 0; i < 5; i++) {
            hotbar[i] = config.getString("skills.hotbar.slot-" + (i + 1), null);
        }
        
        return hotbar;
    }
    
    /**
     * 플레이어 콤보 데이터 로드
     * @param player 플레이어
     * @return 콤보 데이터
     */
    public ComboData loadComboData(Player player) {
        FileConfiguration config = getPlayerConfig(player);
        ComboData combo = new ComboData(player.getUniqueId());
        
        combo.setMaxCombo(config. getInt("combat.combo.max-combo", 0));
        combo.setTotalCombos(config.getInt("combat. combo.total-combos", 0));
        
        return combo;
    }
    
    /**
     * 플레이어 콤보 데이터 저장
     * @param player 플레이어
     * @param combo 콤보 데이터
     */
    public void saveComboData(Player player, ComboData combo) {
        File playerFile = getPlayerFile(player);
        FileConfiguration config = getPlayerConfig(player);
        
        config.set("combat.combo.max-combo", combo.getMaxCombo());
        config. set("combat.combo.total-combos", combo.getTotalCombos());
        
        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().warning("콤보 데이터 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 플레이어 PvP 데이터 로드
     * @param player 플레이어
     * @return PvP 데이터
     */
    public PvPData loadPvPData(Player player) {
        FileConfiguration config = getPlayerConfig(player);
        PvPData pvpData = new PvPData(player.getUniqueId(), player.getName());
        
        pvpData.setPvPEnabled(config.getBoolean("pvp.enabled", true));
        pvpData.setKills(config.getInt("pvp.kills", 0));
        pvpData.setDeaths(config.getInt("pvp. deaths", 0));
        pvpData.setKillStreak(config.getInt("pvp.kill-streak", 0));
        pvpData. setMaxKillStreak(config.getInt("pvp.max-kill-streak", 0));
        pvpData.setFame(config.getInt("pvp.fame", 0));
        pvpData.setInfamy(config.getInt("pvp.infamy", 0));
        
        return pvpData;
    }
    
    /**
     * 플레이어 PvP 데이터 저장
     * @param player 플레이어
     * @param pvpData PvP 데이터
     */
    public void savePvPData(Player player, PvPData pvpData) {
        File playerFile = getPlayerFile(player);
        FileConfiguration config = getPlayerConfig(player);
        
        config.set("pvp.enabled", pvpData.isPvPEnabled());
        config.set("pvp.kills", pvpData.getKills());
        config.set("pvp.deaths", pvpData.getDeaths());
        config.set("pvp.kill-streak", pvpData.getKillStreak());
        config.set("pvp. max-kill-streak", pvpData.getMaxKillStreak());
        config.set("pvp.fame", pvpData. getFame());
        config.set("pvp.infamy", pvpData.getInfamy());
        
        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().warning("PvP 데이터 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 플레이어 전투 통계 로드
     * @param player 플레이어
     * @return 통계 맵
     */
    public Map<String, Object> loadCombatStats(Player player) {
        FileConfiguration config = getPlayerConfig(player);
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total_damage_dealt", config.getDouble("combat.stats.total-damage-dealt", 0.0));
        stats. put("total_damage_taken", config.getDouble("combat.stats.total-damage-taken", 0.0));
        stats. put("total_kills", config.getInt("combat. stats.total-kills", 0));
        stats.put("total_deaths", config.getInt("combat.stats.total-deaths", 0));
        stats.put("max_combo", config.getInt("combat. combo.max-combo", 0));
        stats.put("total_combos", config.getInt("combat.combo.total-combos", 0));
        stats.put("total_crits", config.getInt("combat.critical. total-crits", 0));
        stats.put("crit_damage", config.getDouble("combat.critical.crit-damage", 0.0));
        stats.put("times_dodged", config.getInt("combat.critical.times-dodged", 0));
        
        return stats;
    }
    
    /**
     * 플레이어 전투 통계 저장
     * @param player 플레이어
     * @param stats 통계 맵
     */
    public void saveCombatStats(Player player, Map<String, Object> stats) {
        File playerFile = getPlayerFile(player);
        FileConfiguration config = getPlayerConfig(player);
        
        config.set("combat.stats.total-damage-dealt", stats.get("total_damage_dealt"));
        config.set("combat.stats.total-damage-taken", stats.get("total_damage_taken"));
        config.set("combat.stats.total-kills", stats.get("total_kills"));
        config.set("combat.stats.total-deaths", stats.get("total_deaths"));
        config.set("combat.combo.max-combo", stats.get("max_combo"));
        config.set("combat.combo.total-combos", stats. get("total_combos"));
        config.set("combat.critical.total-crits", stats. get("total_crits"));
        config.set("combat.critical.crit-damage", stats. get("crit_damage"));
        config.set("combat.critical.times-dodged", stats. get("times_dodged"));
        
        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().warning("전투 통계 저장 실패: " + e. getMessage());
        }
    }
    
    /**
     * 플레이어 데이터 존재 확인
     * @param player 플레이어
     * @return 존재하면 true
     */
    public boolean playerDataExists(Player player) {
        return getPlayerFile(player).exists();
    }
    
    /**
     * 플레이어 데이터 삭제
     * @param player 플레이어
     */
    public void deletePlayerData(Player player) {
        File playerFile = getPlayerFile(player);
        if (playerFile. exists()) {
            playerFile. delete();
            plugin.getLogger().info("플레이어 데이터 삭제: " + player. getName());
        }
    }
}