package com.multiverse.quest.data;

import com.multiverse. quest.models.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit. Bukkit;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * YAML 기반 데이터 관리자
 * 퀘스트 데이터를 YAML 파일로 저장하고 로드합니다.
 */
public class YAMLDataManager {
    private final Path dataDirectory;
    private final Path questsDirectory;
    private final Path chainsDirectory;
    private final Path playersDirectory;
    private final Map<String, FileConfiguration> configCache;
    private boolean connected;

    /**
     * 생성자
     * @param dataDirectory 데이터 저장 디렉토리
     */
    public YAMLDataManager(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.questsDirectory = dataDirectory.resolve("quests");
        this.chainsDirectory = dataDirectory.resolve("chains");
        this.playersDirectory = dataDirectory.resolve("players");
        this.configCache = new HashMap<>();
        this.connected = false;
    }

    // ============ Connection Management ============

    /**
     * 데이터 매니저 연결
     */
    public boolean connect() {
        try {
            createDirectoriesIfNotExist();
            connected = true;
            Bukkit.getLogger().info("YAML 데이터 매니저 연결 완료");
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().warning("YAML 데이터 매니저 연결 실패: " + e.getMessage());
            connected = false;
            return false;
        }
    }

    /**
     * 데이터 매니저 연결 해제
     */
    public boolean disconnect() {
        try {
            configCache.clear();
            connected = false;
            Bukkit. getLogger().info("YAML 데이터 매니저 연결 해제");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 연결 상태 확인
     */
    public boolean isConnected() {
        return connected;
    }

    // ============ Directory Management ============

    /**
     * 필요한 디렉토리 생성
     */
    private void createDirectoriesIfNotExist() throws IOException {
        Files.createDirectories(questsDirectory);
        Files.createDirectories(chainsDirectory);
        Files.createDirectories(playersDirectory);
    }

    /**
     * 플레이어 디렉토리 경로 반환
     */
    private Path getPlayerDirectory(UUID playerUUID) {
        return playersDirectory.resolve(playerUUID.toString());
    }

    // ============ File I/O ============

    /**
     * YAML 파일 로드
     */
    public FileConfiguration loadYAML(Path filePath) {
        if (!Files.exists(filePath)) {
            return new YamlConfiguration();
        }

        try {
            return YamlConfiguration.loadConfiguration(filePath.toFile());
        } catch (Exception e) {
            Bukkit.getLogger().warning("YAML 파일 로드 실패: " + filePath + " - " + e.getMessage());
            return new YamlConfiguration();
        }
    }

    /**
     * YAML 파일 저장
     */
    public boolean saveYAML(Path filePath, FileConfiguration config) {
        try {
            Files.createDirectories(filePath. getParent());
            config.save(filePath.toFile());
            return true;
        } catch (IOException e) {
            Bukkit.getLogger(). warning("YAML 파일 저장 실패: " + filePath + " - " + e.getMessage());
            return false;
        }
    }

    // ============ Quest Data ============

    /**
     * 퀘스트 저장
     */
    public boolean saveQuest(Quest quest) {
        if (quest == null || ! isConnected()) return false;

        try {
            Path questFile = questsDirectory.resolve(quest.getQuestId() + ".yml");
            FileConfiguration config = new YamlConfiguration();

            config.set("id", quest.getQuestId());
            config.set("name", quest.getName());
            config.set("type", quest.getType().name());
            config.set("category", quest.getCategory().name());
            config.set("description", quest.getDescription());
            config.set("lore", quest.getLore());
            config.set("requiredLevel", quest.getRequiredLevel());
            config.set("requiredQuest", quest.getRequiredQuest());
            config.set("requiredReputation", quest.getRequiredReputation());
            config.set("questGiverNPC", quest.getQuestGiverNPC());
            config. set("questCompleterNPC", quest.getQuestCompleterNPC());
            config.set("timeLimit", quest.getTimeLimit());
            config.set("cooldown", quest.getCooldown());
            config.set("enabled", quest.isEnabled());

            return saveYAML(questFile, config);
        } catch (Exception e) {
            Bukkit.getLogger().warning("퀘스트 저장 실패: " + quest.getQuestId());
            return false;
        }
    }

    /**
     * 퀘스트 로드
     */
    public Quest loadQuest(String questId) {
        if (!isConnected() || questId == null) return null;

        try {
            Path questFile = questsDirectory.resolve(questId + ".yml");
            if (!Files.exists(questFile)) return null;

            FileConfiguration config = loadYAML(questFile);
            Quest quest = new Quest();
            quest.setQuestId(config.getString("id"));
            quest.setName(config.getString("name"));
            quest.setDescription(config.getString("description"));
            quest.setLore(config.getStringList("lore"));
            quest.setRequiredLevel(config.getInt("requiredLevel", 1));
            quest.setRequiredQuest(config.getString("requiredQuest"));
            quest.setRequiredReputation(config.getInt("requiredReputation", 0));
            quest.setQuestGiverNPC(config.getInt("questGiverNPC", 0));
            quest.setQuestCompleterNPC(config.getInt("questCompleterNPC", 0));
            quest.setTimeLimit(config.getInt("timeLimit", 0));
            quest.setCooldown(config.getLong("cooldown", 0));
            quest.setEnabled(config.getBoolean("enabled", true));

            return quest;
        } catch (Exception e) {
            Bukkit.getLogger(). warning("퀘스트 로드 실패: " + questId);
            return null;
        }
    }

    /**
     * 모든 퀘스트 로드
     */
    public List<Quest> loadAllQuests() {
        if (!isConnected()) return new ArrayList<>();

        List<Quest> quests = new ArrayList<>();

        try {
            if (Files.exists(questsDirectory)) {
                Files.list(questsDirectory)
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(". yml"))
                    .forEach(path -> {
                        try {
                            String questId = path.getFileName(). toString().replace(".yml", "");
                            Quest quest = loadQuest(questId);
                            if (quest != null) {
                                quests.add(quest);
                            }
                        } catch (Exception e) {
                            Bukkit.getLogger().warning("퀨스트 파일 로드 실패: " + path);
                        }
                    });
            }
        } catch (IOException e) {
            Bukkit.getLogger().warning("퀘스트 목록 로드 실패: " + e.getMessage());
        }

        return quests;
    }

    /**
     * 퀘스트 삭제
     */
    public boolean deleteQuest(String questId) {
        if (!isConnected() || questId == null) return false;

        try {
            Path questFile = questsDirectory.resolve(questId + ".yml");
            return Files.deleteIfExists(questFile);
        } catch (IOException e) {
            return false;
        }
    }

    // ============ Quest Chain Data ============

    /**
     * 퀘스트 체인 저장
     */
    public boolean saveQuestChain(QuestChain chain) {
        if (chain == null || !isConnected()) return false;

        try {
            Path chainFile = chainsDirectory.resolve(chain. getChainId() + ".yml");
            FileConfiguration config = new YamlConfiguration();

            config.set("id", chain.getChainId());
            config. set("name", chain.getName());
            config.set("description", chain.getDescription());
            config.set("questSequence", chain.getQuestSequence());
            config.set("enabled", chain.isEnabled());

            return saveYAML(chainFile, config);
        } catch (Exception e) {
            Bukkit.getLogger().warning("체인 저장 실패: " + chain.getChainId());
            return false;
        }
    }

    /**
     * 퀘스트 체인 로드
     */
    public QuestChain loadQuestChain(String chainId) {
        if (!isConnected() || chainId == null) return null;

        try {
            Path chainFile = chainsDirectory.resolve(chainId + ".yml");
            if (!Files.exists(chainFile)) return null;

            FileConfiguration config = loadYAML(chainFile);
            QuestChain chain = new QuestChain();
            chain.setChainId(config.getString("id"));
            chain.setName(config. getString("name"));
            chain. setDescription(config.getString("description"));
            chain.setEnabled(config.getBoolean("enabled", true));

            List<String> questSequence = config.getStringList("questSequence");
            questSequence.forEach(chain::addQuestToSequence);

            return chain;
        } catch (Exception e) {
            Bukkit.getLogger().warning("체인 로드 실패: " + chainId);
            return null;
        }
    }

    /**
     * 모든 퀘스트 체인 로드
     */
    public List<QuestChain> loadAllQuestChains() {
        if (!isConnected()) return new ArrayList<>();

        List<QuestChain> chains = new ArrayList<>();

        try {
            if (Files.exists(chainsDirectory)) {
                Files.list(chainsDirectory)
                    . filter(path -> Files.isRegularFile(path) && path. toString().endsWith(".yml"))
                    .forEach(path -> {
                        try {
                            String chainId = path.getFileName().toString().replace(".yml", "");
                            QuestChain chain = loadQuestChain(chainId);
                            if (chain != null) {
                                chains.add(chain);
                            }
                        } catch (Exception e) {
                            Bukkit.getLogger().warning("체인 파일 로드 실패: " + path);
                        }
                    });
            }
        } catch (IOException e) {
            Bukkit.getLogger().warning("체인 목록 로드 실패: " + e.getMessage());
        }

        return chains;
    }

    /**
     * 퀘스트 체인 삭제
     */
    public boolean deleteQuestChain(String chainId) {
        if (!isConnected() || chainId == null) return false;

        try {
            Path chainFile = chainsDirectory.resolve(chainId + ".yml");
            return Files.deleteIfExists(chainFile);
        } catch (IOException e) {
            return false;
        }
    }

    // ============ Player Data ============

    /**
     * 플레이어 데이터 저장
     */
    public boolean savePlayerData(UUID playerUUID, String key, Object value) {
        if (playerUUID == null || ! isConnected()) return false;

        try {
            Path playerDir = getPlayerDirectory(playerUUID);
            Files.createDirectories(playerDir);

            Path dataFile = playerDir.resolve("data.yml");
            FileConfiguration config = Files.exists(dataFile) ? loadYAML(dataFile) : new YamlConfiguration();

            config. set(key, value);
            return saveYAML(dataFile, config);
        } catch (Exception e) {
            Bukkit.getLogger().warning("플레이어 데이터 저장 실패: " + playerUUID);
            return false;
        }
    }

    /**
     * 플레이어 데이터 로드
     */
    public Object loadPlayerData(UUID playerUUID, String key) {
        if (playerUUID == null || !isConnected()) return null;

        try {
            Path playerDir = getPlayerDirectory(playerUUID);
            Path dataFile = playerDir.resolve("data.yml");

            if (!Files.exists(dataFile)) return null;

            FileConfiguration config = loadYAML(dataFile);
            return config.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 플레이어 퀨스트 저장
     */
    public boolean savePlayerQuest(UUID playerUUID, PlayerQuest playerQuest) {
        if (playerUUID == null || playerQuest == null || !isConnected()) return false;

        try {
            Path playerDir = getPlayerDirectory(playerUUID);
            Files.createDirectories(playerDir);

            Path questFile = playerDir.resolve(playerQuest.getQuestId() + ".yml");
            FileConfiguration config = new YamlConfiguration();

            config.set("questId", playerQuest.getQuestId());
            config.set("status", playerQuest.getStatus(). name());
            config.set("acceptedTime", playerQuest.getAcceptedTime());
            config.set("completedTime", playerQuest.getCompletedTime());
            config.set("finishedTime", playerQuest. getFinishedTime());
            config.set("expiryTime", playerQuest.getExpiryTime());
            config. set("completionCount", playerQuest.getCompletionCount());
            config.set("failReason", playerQuest.getFailReason());

            return saveYAML(questFile, config);
        } catch (Exception e) {
            Bukkit.getLogger().warning("플레이어 퀨스트 저장 실패: " + playerUUID);
            return false;
        }
    }

    /**
     * 플레이어 퀨스트 로드
     */
    public PlayerQuest loadPlayerQuest(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null || !isConnected()) return null;

        try {
            Path playerDir = getPlayerDirectory(playerUUID);
            Path questFile = playerDir.resolve(questId + ".yml");

            if (!Files.exists(questFile)) return null;

            FileConfiguration config = loadYAML(questFile);
            PlayerQuest playerQuest = new PlayerQuest(playerUUID, questId);

            playerQuest.setAcceptedTime(config.getLong("acceptedTime", 0));
            playerQuest.setCompletionCount(config.getInt("completionCount", 0));
            playerQuest.setFailReason(config.getString("failReason"));

            return playerQuest;
        } catch (Exception e) {
            Bukkit.getLogger().warning("플레이어 퀨스트 로드 실패: " + playerUUID);
            return null;
        }
    }

    /**
     * 플레이어 모든 퀨스트 로드
     */
    public List<PlayerQuest> loadPlayerAllQuests(UUID playerUUID) {
        if (playerUUID == null || !isConnected()) return new ArrayList<>();

        List<PlayerQuest> quests = new ArrayList<>();

        try {
            Path playerDir = getPlayerDirectory(playerUUID);
            if (Files.exists(playerDir)) {
                Files.list(playerDir)
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".yml") && !path.getFileName().toString().equals("data.yml"))
                    .forEach(path -> {
                        try {
                            String questId = path.getFileName(). toString().replace(".yml", "");
                            PlayerQuest pq = loadPlayerQuest(playerUUID, questId);
                            if (pq != null) {
                                quests.add(pq);
                            }
                        } catch (Exception e) {
                            Bukkit.getLogger().warning("플레이어 퀨스트 파일 로드 실패: " + path);
                        }
                    });
            }
        } catch (IOException e) {
            Bukkit.getLogger().warning("플레이어 퀨스트 목록 로드 실패: " + e.getMessage());
        }

        return quests;
    }

    /**
     * 플레이어 퀨스트 삭제
     */
    public boolean deletePlayerQuest(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null || !isConnected()) return false;

        try {
            Path playerDir = getPlayerDirectory(playerUUID);
            Path questFile = playerDir. resolve(questId + ".yml");
            return Files.deleteIfExists(questFile);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 플레이어 데이터 삭제
     */
    public boolean deletePlayerData(UUID playerUUID) {
        if (playerUUID == null || !isConnected()) return false;

        try {
            Path playerDir = getPlayerDirectory(playerUUID);
            if (Files.exists(playerDir)) {
                Files.walk(playerDir)
                    . sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // Ignore
                        }
                    });
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ============ Utility Methods ============

    /**
     * 저장소 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== YAML 데이터 매니저 상태 ===§r\n");
        sb.append("§7상태: ").append(connected ? "§a연결됨" : "§c연결 안됨").append("\n");
        sb.append("§7디렉토리: §f").append(dataDirectory). append("\n");

        try {
            long questCount = Files.list(questsDirectory). count();
            long chainCount = Files.list(chainsDirectory).count();
            sb. append("§7퀘스트: §f").append(questCount). append("개\n");
            sb.append("§7체인: §f").append(chainCount).append("개\n");
        } catch (IOException e) {
            sb.append("§7통계 조회 실패\n");
        }

        return sb.toString();
    }
}