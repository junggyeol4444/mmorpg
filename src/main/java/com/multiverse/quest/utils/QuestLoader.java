package com.multiverse.quest.utils;

import com.multiverse. quest.models.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.util.*;

/**
 * 퀨스트 로더
 * YAML 파일에서 퀨스트 데이터를 로드합니다.
 */
public class QuestLoader {
    private final JavaPlugin plugin;
    private final File questDirectory;

    /**
     * 생성자
     * @param plugin 플러그인 인스턴스
     */
    public QuestLoader(JavaPlugin plugin) {
        this. plugin = plugin;
        this. questDirectory = new File(plugin.getDataFolder(), "quests");
        
        if (!questDirectory.exists()) {
            questDirectory.mkdirs();
        }
    }

    // ============ Quest Loading ============

    /**
     * 모든 퀨스트 로드
     */
    public List<Quest> loadAllQuests() {
        List<Quest> quests = new ArrayList<>();

        if (!questDirectory.exists()) {
            plugin.getLogger().warning("퀨스트 디렉토리가 없습니다: " + questDirectory.getPath());
            return quests;
        }

        File[] files = questDirectory.listFiles((dir, name) -> name.endsWith(".yml"));
        
        if (files == null || files.length == 0) {
            plugin.getLogger().warning("로드할 퀨스트 파일이 없습니다.");
            return quests;
        }

        for (File file : files) {
            try {
                Quest quest = loadQuest(file);
                if (quest != null) {
                    quests.add(quest);
                }
            } catch (Exception e) {
                plugin.getLogger(). warning("퀨스트 로드 실패 (" + file.getName() + "): " + e.getMessage());
            }
        }

        plugin.getLogger().info("§a✓ " + quests.size() + "개의 퀨스트를 로드했습니다.");
        return quests;
    }

    /**
     * 단일 퀨스트 로드
     */
    public Quest loadQuest(File file) throws Exception {
        YamlConfiguration config = YamlConfiguration. loadConfiguration(file);

        String questId = config.getString("id");
        if (questId == null || questId.isEmpty()) {
            throw new Exception("퀨스트 ID가 없습니다.");
        }

        String name = config.getString("name", "Unknown Quest");
        String description = config.getString("description", "");
        String type = config.getString("type", "normal");
        String difficulty = config.getString("difficulty", "normal");
        int requiredLevel = config.getInt("required-level", 1);
        int timeLimit = config.getInt("time-limit", 0);

        // 목표 로드
        List<QuestObjective> objectives = loadObjectives(config);

        // 보상 로드
        List<QuestReward> rewards = loadRewards(config);

        // 퀨스트 생성
        Quest quest = new Quest(questId, name, description, type, difficulty, requiredLevel, timeLimit);
        quest.setObjectives(objectives);
        quest.setRewards(rewards);

        return quest;
    }

    /**
     * 퀨스트 ID로 로드
     */
    public Quest loadQuestById(String questId) {
        File questFile = new File(questDirectory, questId + ".yml");
        
        if (!questFile.exists()) {
            plugin.getLogger().warning("퀨스트 파일을 찾을 수 없습니다: " + questId);
            return null;
        }

        try {
            return loadQuest(questFile);
        } catch (Exception e) {
            plugin.getLogger().warning("퀨스트 로드 실패 (" + questId + "): " + e.getMessage());
            return null;
        }
    }

    // ============ Objective Loading ============

    /**
     * 목표 로드
     */
    private List<QuestObjective> loadObjectives(YamlConfiguration config) {
        List<QuestObjective> objectives = new ArrayList<>();

        if (! config.contains("objectives")) {
            return objectives;
        }

        for (String key : config.getConfigurationSection("objectives").getKeys(false)) {
            try {
                String objectivePath = "objectives." + key;
                String objectiveId = config.getString(objectivePath + ".id", key);
                String type = config.getString(objectivePath + ".type", "custom");
                String description = config. getString(objectivePath + ".description", "");
                int targetProgress = config.getInt(objectivePath + ".target", 1);

                QuestObjective objective = new QuestObjective(objectiveId, type, description, targetProgress);
                objectives.add(objective);

            } catch (Exception e) {
                plugin.getLogger().warning("목표 로드 실패 (" + key + "): " + e.getMessage());
            }
        }

        return objectives;
    }

    // ============ Reward Loading ============

    /**
     * 보상 로드
     */
    private List<QuestReward> loadRewards(YamlConfiguration config) {
        List<QuestReward> rewards = new ArrayList<>();

        if (!config. contains("rewards")) {
            return rewards;
        }

        for (String key : config.getConfigurationSection("rewards").getKeys(false)) {
            try {
                String rewardPath = "rewards." + key;
                String type = config.getString(rewardPath + ".type", "experience");
                long amount = config.getLong(rewardPath + ".amount", 0);
                String itemName = config.getString(rewardPath + ".item-name", "");

                QuestReward reward = new QuestReward(type, amount, itemName);
                rewards.add(reward);

            } catch (Exception e) {
                plugin.getLogger().warning("보상 로드 실패 (" + key + "): " + e.getMessage());
            }
        }

        return rewards;
    }

    // ============ Directory Management ============

    /**
     * 퀨스트 디렉토리 반환
     */
    public File getQuestDirectory() {
        return questDirectory;
    }

    /**
     * 퀨스트 파일 존재 여부 확인
     */
    public boolean questExists(String questId) {
        File questFile = new File(questDirectory, questId + ".yml");
        return questFile.exists();
    }

    /**
     * 퀨스트 파일 개수 반환
     */
    public int getQuestFileCount() {
        File[] files = questDirectory.listFiles((dir, name) -> name.endsWith(". yml"));
        return files != null ? files.length : 0;
    }

    /**
     * 모든 퀨스트 파일 목록 반환
     */
    public List<String> getQuestFileList() {
        List<String> questFiles = new ArrayList<>();
        
        File[] files = questDirectory. listFiles((dir, name) -> name.endsWith(".yml"));
        
        if (files != null) {
            for (File file : files) {
                questFiles.add(file.getName(). replace(".yml", ""));
            }
        }

        return questFiles;
    }

    // ============ Quest Validation ============

    /**
     * 퀨스트 유효성 검사
     */
    public boolean isValidQuest(Quest quest) {
        if (quest == null) {
            return false;
        }

        if (quest.getQuestId() == null || quest. getQuestId().isEmpty()) {
            return false;
        }

        if (quest.getName() == null || quest.getName().isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * 퀨스트 로드 통계
     */
    public String getLoadStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 퀨스트 로더 통계 ===§r\n");
        sb.append("§7퀨스트 디렉토리: §f"). append(questDirectory.getPath()).append("\n");
        sb.append("§7로드된 파일: §f").append(getQuestFileCount()).append("개\n");

        List<String> questFiles = getQuestFileList();
        if (! questFiles.isEmpty()) {
            sb.append("§7파일 목록:\n");
            for (String questFile : questFiles) {
                sb.append("  §f- ").append(questFile).append("\n");
            }
        }

        return sb.toString();
    }

    // ============ Error Handling ============

    /**
     * 로드 오류 보고
     */
    public void reportLoadError(String questId, Exception e) {
        plugin.getLogger().severe("퀨스트 로드 실패: " + questId);
        plugin.getLogger().severe("오류: " + e.getMessage());
        e.printStackTrace();
    }

    // ============ Getters ============

    /**
     * 플러그인 인스턴스 반환
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }
}