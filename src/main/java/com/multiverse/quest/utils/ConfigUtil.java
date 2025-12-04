package com.multiverse.quest.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io. File;
import java.util.*;

/**
 * 설정 유틸리티
 * 플러그인 설정 파일을 관리합니다.
 */
public class ConfigUtil {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    /**
     * 생성자
     * @param plugin 플러그인 인스턴스
     */
    public ConfigUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    // ============ Config Load/Save ============

    /**
     * 설정 파일 로드
     */
    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(getConfigFile());
    }

    /**
     * 설정 파일 저장
     */
    public void saveConfig() {
        try {
            config.save(getConfigFile());
        } catch (Exception e) {
            plugin.getLogger().warning("설정 파일 저장 실패: " + e.getMessage());
        }
    }

    /**
     * 기본 설정 저장
     */
    public void saveDefaultConfig() {
        plugin.saveDefaultConfig();
    }

    /**
     * 설정 다시 로드
     */
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    // ============ File Management ============

    /**
     * 설정 파일 반환
     */
    private File getConfigFile() {
        return new File(plugin.getDataFolder(), "config.yml");
    }

    /**
     * 데이터 폴더 생성
     */
    public void createDataFolder() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder(). mkdirs();
        }
    }

    // ============ String Config ============

    /**
     * 문자열 값 가져오기
     */
    public String getString(String key, String defaultValue) {
        return config.getString(key, defaultValue);
    }

    /**
     * 문자열 값 설정
     */
    public void setString(String key, String value) {
        config.set(key, value);
    }

    // ============ Integer Config ============

    /**
     * 정수 값 가져오기
     */
    public int getInt(String key, int defaultValue) {
        return config.getInt(key, defaultValue);
    }

    /**
     * 정수 값 설정
     */
    public void setInt(String key, int value) {
        config.set(key, value);
    }

    // ============ Double Config ============

    /**
     * 실수 값 가져오기
     */
    public double getDouble(String key, double defaultValue) {
        return config.getDouble(key, defaultValue);
    }

    /**
     * 실수 값 설정
     */
    public void setDouble(String key, double value) {
        config.set(key, value);
    }

    // ============ Boolean Config ============

    /**
     * 불린 값 가져오기
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return config. getBoolean(key, defaultValue);
    }

    /**
     * 불린 값 설정
     */
    public void setBoolean(String key, boolean value) {
        config. set(key, value);
    }

    // ============ List Config ============

    /**
     * 문자열 리스트 가져오기
     */
    public List<String> getStringList(String key) {
        return config.getStringList(key);
    }

    /**
     * 정수 리스트 가져오기
     */
    public List<Integer> getIntList(String key) {
        return config.getIntegerList(key);
    }

    /**
     * 리스트 설정
     */
    public void setList(String key, List<? > value) {
        config. set(key, value);
    }

    // ============ Plugin Settings ============

    /**
     * 플러그인 활성화 여부
     */
    public boolean isPluginEnabled() {
        return getBoolean("plugin.enabled", true);
    }

    /**
     * 디버그 모드
     */
    public boolean isDebugMode() {
        return getBoolean("plugin.debug", false);
    }

    /**
     * 자동 저장 활성화
     */
    public boolean isAutoSaveEnabled() {
        return getBoolean("plugin.auto-save.enabled", true);
    }

    /**
     * 자동 저장 간격 (분)
     */
    public int getAutoSaveInterval() {
        return getInt("plugin.auto-save.interval", 20);
    }

    /**
     * 일일 초기화 활성화
     */
    public boolean isDailyResetEnabled() {
        return getBoolean("plugin. daily-reset.enabled", true);
    }

    /**
     * 주간 초기화 활성화
     */
    public boolean isWeeklyResetEnabled() {
        return getBoolean("plugin.weekly-reset.enabled", true);
    }

    // ============ Quest Settings ============

    /**
     * 기본 퀨스트 제한 시간 (초)
     */
    public int getDefaultQuestTimeLimit() {
        return getInt("quest.default-time-limit", 0);
    }

    /**
     * 최대 진행 중인 퀨스트 개수
     */
    public int getMaxConcurrentQuests() {
        return getInt("quest.max-concurrent-quests", 5);
    }

    /**
     * 퀨스트 보상 배수
     */
    public double getQuestRewardMultiplier() {
        return getDouble("quest.reward-multiplier", 1.0);
    }

    /**
     * 퀨스트 경험치 배수
     */
    public double getQuestExpMultiplier() {
        return getDouble("quest.exp-multiplier", 1.0);
    }

    // ============ Database Settings ============

    /**
     * 데이터베이스 타입
     */
    public String getDatabaseType() {
        return getString("database.type", "yaml");
    }

    /**
     * 데이터베이스 호스트
     */
    public String getDatabaseHost() {
        return getString("database.host", "localhost");
    }

    /**
     * 데이터베이스 포트
     */
    public int getDatabasePort() {
        return getInt("database.port", 3306);
    }

    /**
     * 데이터베이스 이름
     */
    public String getDatabaseName() {
        return getString("database. name", "questcore");
    }

    /**
     * 데이터베이스 사용자
     */
    public String getDatabaseUser() {
        return getString("database.user", "root");
    }

    /**
     * 데이터베이스 암호
     */
    public String getDatabasePassword() {
        return getString("database.password", "");
    }

    // ============ Notification Settings ============

    /**
     * 알림 활성화
     */
    public boolean isNotificationEnabled() {
        return getBoolean("notification.enabled", true);
    }

    /**
     * 액션바 알림 활성화
     */
    public boolean isActionbarEnabled() {
        return getBoolean("notification.actionbar. enabled", true);
    }

    /**
     * 제목 알림 활성화
     */
    public boolean isTitleEnabled() {
        return getBoolean("notification.title.enabled", true);
    }

    /**
     * 채팅 알림 활성화
     */
    public boolean isChatNotificationEnabled() {
        return getBoolean("notification.chat.enabled", true);
    }

    /**
     * 사운드 알림 활성화
     */
    public boolean isSoundEnabled() {
        return getBoolean("notification.sound.enabled", true);
    }

    // ============ Status ============

    /**
     * 현재 설정 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 플러그인 설정 ===§r\n");
        sb.append("§7플러그인 활성화: §f"). append(isPluginEnabled()). append("\n");
        sb. append("§7디버그 모드: §f").append(isDebugMode()).append("\n");
        sb.append("§7자동 저장: §f").append(isAutoSaveEnabled()).append(" ("). append(getAutoSaveInterval()). append("분)\n");
        sb.append("§7일일 초기화: §f").append(isDailyResetEnabled()).append("\n");
        sb.append("§7주간 초기화: §f").append(isWeeklyResetEnabled()).append("\n");
        sb.append("§7데이터베이스: §f").append(getDatabaseType()).append("\n");

        return sb.toString();
    }

    // ============ Getters ============

    /**
     * FileConfiguration 반환
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * 플러그인 인스턴스 반환
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }
}