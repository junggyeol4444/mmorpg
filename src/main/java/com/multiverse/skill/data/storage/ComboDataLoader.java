package com.multiverse.skill.data.storage;

import com.multiverse.skill.SkillCore;
import com.multiverse. skill.data.models.*;
import org.bukkit.configuration.file. FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * 콤보 데이터 로더
 */
public class ComboDataLoader {

    private final SkillCore plugin;
    private final DataStorage storage;
    private final Map<String, SkillCombo> comboCache;

    public ComboDataLoader(SkillCore plugin, DataStorage storage) {
        this. plugin = plugin;
        this. storage = storage;
        this. comboCache = new HashMap<>();
    }

    /**
     * 모든 콤보 로드
     */
    public List<SkillCombo> loadAllCombos() {
        List<SkillCombo> combos = new ArrayList<>();
        File combosFolder = new File(plugin. getDataFolder(), "combos");

        if (!combosFolder. exists()) {
            plugin.getLogger().warning("⚠️ 콤보 폴더가 없습니다: " + combosFolder.getPath());
            return combos;
        }

        File[] comboFiles = combosFolder.listFiles((d, name) -> name.endsWith(".yml"));
        if (comboFiles == null) {
            return combos;
        }

        for (File comboFile : comboFiles) {
            try {
                SkillCombo combo = loadComboFromFile(comboFile);
                if (combo != null) {
                    combos.add(combo);
                    comboCache.put(combo.getComboId(), combo);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("콤보 로드 실패: " + comboFile.getName());
                e.printStackTrace();
            }
        }

        return combos;
    }

    /**
     * 파일에서 콤보 로드
     */
    private SkillCombo loadComboFromFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        SkillCombo combo = new SkillCombo();
        combo.setComboId(config.getString("id", file. getName().replace(".yml", "")));
        combo.setName(config.getString("name", "Unknown"));
        combo.setTimeWindow(config.getInt("time-window", 3000));
        combo.setFinisherSkillId(config.getString("finisher-skill-id", ""));

        // 스킬 시퀀스
        List<String> sequence = config.getStringList("skill-sequence");
        combo.setSkillSequence(sequence);

        // 보너스
        combo.setHasBonus(config.getBoolean("has-bonus", false));
        combo.setDamageBonus(config.getDouble("damage-bonus", 0.0));

        // 추가 효과
        if (config.contains("bonus-effects")) {
            for (String effectKey : config.getConfigurationSection("bonus-effects").getKeys(false)) {
                Object value = config.get("bonus-effects." + effectKey);
                combo.addBonusEffect(effectKey, value);
            }
        }

        return combo;
    }

    /**
     * 콤보 조회
     */
    public SkillCombo getCombo(String comboId) {
        return comboCache.getOrDefault(comboId, null);
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        comboCache.clear();
    }
}