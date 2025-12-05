package com.multiverse.skill.data. storage;

import com.multiverse.skill. SkillCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io. File;
import java.io.IOException;
import java.util.*;

/**
 * YAML 기반 저장소
 */
public class YamlStorage implements DataStorage {

    private final SkillCore plugin;
    private final File dataFolder;
    private final Map<String, FileConfiguration> loadedConfigs;

    public YamlStorage(SkillCore plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        this.loadedConfigs = new HashMap<>();
        
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    /**
     * YAML 파일 경로 생성
     */
    private File getFile(String key) {
        String fileName = key. replace(".", File.separator) + ".yml";
        return new File(dataFolder, fileName);
    }

    /**
     * 파일 로드
     */
    private FileConfiguration loadFile(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("파일 생성 실패: " + file.getPath());
                return new YamlConfiguration();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public <T> T load(String key, Class<T> type) {
        File file = getFile(key);
        FileConfiguration config = loadFile(file);
        
        loadedConfigs.put(key, config);

        // 직렬화된 데이터 로드
        try {
            if (config.contains("data")) {
                // 실제 구현에서는 GSON이나 다른 직렬화 라이브러리 사용
                return null; // 임시
            }
        } catch (Exception e) {
            plugin.getLogger().warning("데이터 로드 실패: " + key);
        }

        return null;
    }

    @Override
    public void save(String key, Object data) {
        File file = getFile(key);
        FileConfiguration config = new YamlConfiguration();

        try {
            // 직렬화된 데이터 저장
            if (data != null) {
                config.set("data", data);
            }

            file.getParentFile().mkdirs();
            config.save(file);
            loadedConfigs.put(key, config);

        } catch (IOException e) {
            plugin.getLogger().warning("데이터 저장 실패: " + key);
        }
    }

    @Override
    public void delete(String key) {
        File file = getFile(key);
        if (file.exists()) {
            file.delete();
            loadedConfigs.remove(key);
        }
    }

    @Override
    public boolean exists(String key) {
        File file = getFile(key);
        return file.exists();
    }

    @Override
    public <T> List<T> loadAll(String directory, Class<T> type) {
        List<T> results = new ArrayList<>();
        File dir = new File(dataFolder, directory);

        if (!dir.exists()) {
            return results;
        }

        File[] files = dir. listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) {
            return results;
        }

        for (File file : files) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            // 실제 구현에서는 데이터 변환
        }

        return results;
    }

    @Override
    public void saveAll(String directory, Map<String, Object> dataMap) {
        File dir = new File(dataFolder, directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (Map. Entry<String, Object> entry : dataMap.entrySet()) {
            String key = directory + "." + entry.getKey();
            save(key, entry.getValue());
        }
    }

    @Override
    public void reload() {
        loadedConfigs.clear();
        plugin.getLogger().info("✅ YAML 저장소가 새로고침되었습니다.");
    }

    @Override
    public void close() {
        loadedConfigs.clear();
        plugin. getLogger().info("✅ YAML 저장소가 종료되었습니다.");
    }

    @Override
    public boolean isReady() {
        return dataFolder.exists() && dataFolder.isDirectory();
    }

    /**
     * 설정 파일 조회
     */
    public FileConfiguration getConfig(String key) {
        return loadedConfigs.getOrDefault(key, new YamlConfiguration());
    }
}