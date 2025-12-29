package com.multiverse.pet.          config;

import com.multiverse.pet.          PetCore;
import org.bukkit.          configuration.file.FileConfiguration;
import org.bukkit.          configuration.file.YamlConfiguration;

import java.  io.          File;
import java. io.          IOException;
import java. io.          InputStream;
import java.io.          InputStreamReader;
import java.util.          HashMap;
import java. util.          Map;

/**
 * 설정 관리자
 * 모든 설정 파일 로드 및 관리
 */
public class ConfigManager {

    private final PetCore plugin;

    // 설정 파일들
    private FileConfiguration config;
    private FileConfiguration messagesConfig;

    // 설정 객체들
    private PetSettings petSettings;
    private LevelSettings levelSettings;
    private SkillSettings skillSettings;
    private EvolutionSettings evolutionSettings;
    private CareSettings careSettings;
    private BreedingSettings breedingSettings;
    private BattleSettings battleSettings;
    private StorageSettings storageSettings;

    // 메시지 캐시
    private final Map<String, String> messageCache;

    public ConfigManager(PetCore plugin) {
        this.plugin = plugin;
        this.messageCache = new HashMap<>();
    }

    /**
     * 모든 설정 로드
     */
    public void loadAll() {
        // 기본 설정 저장
        plugin.saveDefaultConfig();
        saveDefaultMessages();

        // 설정 로드
        plugin.reloadConfig();
        config = plugin.getConfig();

        // 메시지 로드
        loadMessages();

        // 개별 설정 객체 로드
        loadSettingsObjects();

        plugin.getLogger().info("모든 설정 파일 로드 완료");
    }

    /**
     * 기본 메시지 파일 저장
     */
    private void saveDefaultMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (! messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    /**
     * 메시지 파일 로드
     */
    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // 기본값과 병합
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream));
            messagesConfig.setDefaults(defaultMessages);
        }

        // 캐시 초기화
        messageCache.clear();
    }

    /**
     * 설정 객체들 로드
     */
    private void loadSettingsObjects() {
        petSettings = new PetSettings(config);
        levelSettings = new LevelSettings(config);
        skillSettings = new SkillSettings(config);
        evolutionSettings = new EvolutionSettings(config);
        careSettings = new CareSettings(config);
        breedingSettings = new BreedingSettings(config);
        battleSettings = new BattleSettings(config);
        storageSettings = new StorageSettings(config);
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadAll();
    }

    // ===== 메시지 =====

    /**
     * 메시지 가져오기
     */
    public String getMessage(String path) {
        // 캐시 확인
        if (messageCache.containsKey(path)) {
            return messageCache.get(path);
        }

        String message = messagesConfig.getString(path);
        if (message == null) {
            message = "§c메시지를 찾을 수 없음:  " + path;
        }

        // 색상 코드 변환
        message = message.replace("&", "§");

        // 캐시 저장
        messageCache.put(path, message);

        return message;
    }

    /**
     * 메시지 가져오기 (플레이스홀더 적용)
     */
    public String getMessage(String path, Map<String, String> placeholders) {
        String message = getMessage(path);

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message. replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return message;
    }

    /**
     * 프리픽스 가져오기
     */
    public String getPrefix() {
        return getMessage("prefix");
    }

    // ===== 기본 설정 =====

    /**
     * 디버그 모드
     */
    public boolean isDebugMode() {
        return config.getBoolean("settings.debug", false);
    }

    /**
     * 자동 저장 간격 (분)
     */
    public int getAutoSaveInterval() {
        return config.getInt("settings.auto-save-interval", 5);
    }

    /**
     * 온라인만 저장
     */
    public boolean isSaveOnlyOnline() {
        return config.getBoolean("settings.save-only-online", true);
    }

    /**
     * 자동 저장 로그
     */
    public boolean isLogAutoSaves() {
        return config.getBoolean("settings.log-auto-saves", false);
    }

    /**
     * 언어
     */
    public String getLanguage() {
        return config.getString("settings. language", "ko_KR");
    }

    // ===== 설정 객체 Getter =====

    public PetSettings getPetSettings() {
        return petSettings;
    }

    public LevelSettings getLevelSettings() {
        return levelSettings;
    }

    public SkillSettings getSkillSettings() {
        return skillSettings;
    }

    public EvolutionSettings getEvolutionSettings() {
        return evolutionSettings;
    }

    public CareSettings getCareSettings() {
        return careSettings;
    }

    public BreedingSettings getBreedingSettings() {
        return breedingSettings;
    }

    public BattleSettings getBattleSettings() {
        return battleSettings;
    }

    public StorageSettings getStorageSettings() {
        return storageSettings;
    }

    // ===== 설정 저장 =====

    /**
     * 설정 저장
     */
    public void saveConfig() {
        plugin.saveConfig();
    }

    /**
     * 메시지 파일 저장
     */
    public void saveMessages() {
        try {
            File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().warning("메시지 파일 저장 실패: " + e.getMessage());
        }
    }

    // ===== 기본 설정 파일 접근 =====

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    /**
     * 설정값 가져오기 (문자열)
     */
    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    /**
     * 설정값 가져오기 (정수)
     */
    public int getInt(String path, int defaultValue) {
        return config. getInt(path, defaultValue);
    }

    /**
     * 설정값 가져오기 (실수)
     */
    public double getDouble(String path, double defaultValue) {
        return config. getDouble(path, defaultValue);
    }

    /**
     * 설정값 가져오기 (불린)
     */
    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }
}