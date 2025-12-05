package com. multiverse.skill. data. storage;

import com.multiverse.skill. SkillCore;
import com.multiverse. skill.data.models.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io. File;
import java.util.*;

/**
 * 플레이어 데이터 로더
 */
public class PlayerDataLoader {

    private final SkillCore plugin;
    private final DataStorage storage;
    private final Map<UUID, PlayerSkillData> playerDataCache;

    public PlayerDataLoader(SkillCore plugin, DataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.playerDataCache = new HashMap<>();
    }

    /**
     * 플레이어 데이터 로드
     */
    public PlayerSkillData loadPlayerData(UUID playerUUID) {
        // 캐시 확인
        if (playerDataCache.containsKey(playerUUID)) {
            return playerDataCache.get(playerUUID);
        }

        File playerFile = getPlayerFile(playerUUID);
        PlayerSkillData skillData = new PlayerSkillData(playerUUID, "Unknown");

        if (!playerFile. exists()) {
            // 새로운 플레이어 데이터 생성
            playerDataCache.put(playerUUID, skillData);
            return skillData;
        }

        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            skillData.setPlayerName(config.getString("player-name", "Unknown"));
            skillData.setTotalSkillPoints(config.getInt("total-skill-points", 0));
            skillData.setUsedSkillPoints(config.getInt("used-skill-points", 0));
            skillData. setAvailableSkillPoints(config. getInt("available-skill-points", 0));

            // 습득한 스킬 로드
            if (config.contains("skills")) {
                for (String key : config.getConfigurationSection("skills").getKeys(false)) {
                    String path = "skills." + key;
                    LearnedSkill learned = new LearnedSkill();
                    learned.setSkillId(key);
                    learned.setLevel(config.getInt(path + ".level", 1));
                    learned.setExperience(config.getLong(path + ".experience", 0));
                    learned.setLastUsedTime(config.getLong(path + ".last-used", 0));
                    learned.setTimesUsed(config.getInt(path + ".times-used", 0));
                    learned.setTotalDamage(config.getLong(path + ".total-damage", 0));

                    skillData.addSkill(key, learned);
                }
            }

            // 스킬 트리 진행도 로드
            if (config.contains("tree-progress")) {
                for (String treeId : config.getConfigurationSection("tree-progress").getKeys(false)) {
                    int progress = config.getInt("tree-progress." + treeId, 0);
                    skillData.updateTreeProgress(treeId, progress);
                }
            }

            // 프리셋 로드
            if (config.contains("presets")) {
                for (String presetName : config.getConfigurationSection("presets").getKeys(false)) {
                    SkillPreset preset = new SkillPreset(presetName);
                    String presetPath = "presets." + presetName + ".hotbar";
                    
                    if (config.contains(presetPath)) {
                        for (String slot : config.getConfigurationSection(presetPath).getKeys(false)) {
                            int slotNum = Integer.parseInt(slot);
                            String skillId = config.getString(presetPath + "." + slot, "");
                            preset.setSkillToSlot(slotNum, skillId);
                        }
                    }
                    
                    skillData.addPreset(preset);
                }
            }

            skillData.setActivePresetIndex(config.getInt("active-preset", 0));

            playerDataCache.put(playerUUID, skillData);

        } catch (Exception e) {
            plugin.getLogger().warning("플레이어 데이터 로드 실패: " + playerUUID);
            e.printStackTrace();
        }

        return skillData;
    }

    /**
     * 플레이어 데이터 저장
     */
    public void savePlayerData(UUID playerUUID) {
        PlayerSkillData skillData = playerDataCache.get(playerUUID);
        if (skillData == null) {
            return;
        }

        File playerFile = getPlayerFile(playerUUID);
        FileConfiguration config = new YamlConfiguration();

        try {
            config.set("player-name", skillData.getPlayerName());
            config.set("total-skill-points", skillData.getTotalSkillPoints());
            config.set("used-skill-points", skillData.getUsedSkillPoints());
            config.set("available-skill-points", skillData.getAvailableSkillPoints());

            // 습득한 스킬 저장
            for (Map.Entry<String, LearnedSkill> entry : skillData. getSkills().entrySet()) {
                String skillId = entry.getKey();
                LearnedSkill skill = entry.getValue();
                String path = "skills." + skillId;

                config.set(path + ". level", skill.getLevel());
                config.set(path + ". experience", skill.getExperience());
                config.set(path + ".last-used", skill. getLastUsedTime());
                config.set(path + ".times-used", skill.getTimesUsed());
                config.set(path + ".total-damage", skill.getTotalDamage());
            }

            // 스킬 트리 진행도 저장
            for (Map.Entry<String, Integer> entry : skillData.getTreeProgress(). entrySet()) {
                config.set("tree-progress." + entry.getKey(), entry.getValue());
            }

            // 프리셋 저장
            for (int i = 0; i < skillData.getPresets().size(); i++) {
                SkillPreset preset = skillData.getPresets().get(i);
                String presetPath = "presets." + preset.getName();

                for (Map.Entry<Integer, String> slot : preset.getHotbar().entrySet()) {
                    config.set(presetPath + ". hotbar." + slot.getKey(), slot.getValue());
                }
            }

            config.set("active-preset", skillData. getActivePresetIndex());

            playerFile.getParentFile().mkdirs();
            config.save(playerFile);

        } catch (Exception e) {
            plugin. getLogger().warning("플레이어 데이터 저장 실패: " + playerUUID);
            e.printStackTrace();
        }
    }

    /**
     * 플레이어 파일 경로
     */
    private File getPlayerFile(UUID playerUUID) {
        File playersFolder = new File(plugin.getDataFolder(), "players");
        return new File(playersFolder, playerUUID + ".yml");
    }

    /**
     * 캐시에서 데이터 조회
     */
    public PlayerSkillData getFromCache(UUID playerUUID) {
        return playerDataCache.get(playerUUID);
    }

    /**
     * 캐시에서 제거
     */
    public void removeFromCache(UUID playerUUID) {
        playerDataCache. remove(playerUUID);
    }

    /**
     * 모든 캐시 저장
     */
    public void saveAllCache() {
        for (UUID playerUUID : playerDataCache. keySet()) {
            savePlayerData(playerUUID);
        }
    }
}