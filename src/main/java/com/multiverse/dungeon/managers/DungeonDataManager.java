package com.multiverse.dungeon.managers;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse.  dungeon.data.model.*;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io. File;
import java.util.*;

/**
 * 던전 데이터 저장/로드 관리
 * 파일 시스템 기반
 */
public class DungeonDataManager {

    private final DungeonCore plugin;
    private final File dataFolder;
    
    private final File dungeonsFolder;
    private final File partiesFolder;
    private final File instancesFolder;
    private final File playerDataFolder;
    private final File recordsFolder;

    /**
     * 생성자
     */
    public DungeonDataManager(DungeonCore plugin) {
        this. plugin = plugin;
        this. dataFolder = new File(plugin.getDataFolder(), "data");
        
        this.dungeonsFolder = new File(dataFolder, "dungeons");
        this.partiesFolder = new File(dataFolder, "parties");
        this.instancesFolder = new File(dataFolder, "instances");
        this.playerDataFolder = new File(dataFolder, "players");
        this.recordsFolder = new File(dataFolder, "records");
        
        // 폴더 생성
        createFolders();
    }

    /**
     * 필요한 모든 폴더 생성
     */
    private void createFolders() {
        dungeonsFolder.mkdirs();
        partiesFolder.mkdirs();
        instancesFolder. mkdirs();
        playerDataFolder.mkdirs();
        recordsFolder.  mkdirs();
    }

    // ===== 던전 관련 =====

    /**
     * 모든 던전 로드
     */
    public Map<String, Dungeon> loadAllDungeons() {
        Map<String, Dungeon> dungeons = new HashMap<>();
        
        File[] files = dungeonsFolder.listFiles();
        if (files == null) {
            return dungeons;
        }

        for (File file : files) {
            if (! file.getName().endsWith(".yml")) continue;
            
            try {
                var config = YamlConfiguration.loadConfiguration(file);
                // 여기서 YAML을 Dungeon 객체로 변환 (구현 필요)
                // 예시로 스킵
            } catch (Exception e) {
                plugin.getLogger().warning("⚠️ 던전 로드 실패: " + file.getName());
            }
        }

        return dungeons;
    }

    /**
     * 던전 저장
     */
    public void saveDungeon(Dungeon dungeon) {
        if (dungeon == null) {
            return;
        }

        try {
            File file = new File(dungeonsFolder, dungeon.getDungeonId() + ".yml");
            var config = new YamlConfiguration();
            
            // Dungeon 객체를 YAML로 변환 (구현 필요)
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 던전 저장 실패: " + dungeon.getDungeonId());
        }
    }

    /**
     * 던전 삭제
     */
    public void deleteDungeon(String dungeonId) {
        File file = new File(dungeonsFolder, dungeonId + ". yml");
        if (file. exists()) {
            file.delete();
        }
    }

    // ===== 파티 관련 =====

    /**
     * 모든 파티 로드
     */
    public List<Party> loadAllParties() {
        List<Party> parties = new ArrayList<>();
        
        File[] files = partiesFolder. listFiles();
        if (files == null) {
            return parties;
        }

        for (File file : files) {
            if (!file.getName().endsWith(".yml")) continue;
            
            try {
                var config = YamlConfiguration.loadConfiguration(file);
                // 여기서 YAML을 Party 객체로 변환 (구현 필요)
            } catch (Exception e) {
                plugin.getLogger().warning("⚠️ 파티 로드 실패: " + file.getName());
            }
        }

        return parties;
    }

    /**
     * 파티 저장
     */
    public void saveParty(Party party) {
        if (party == null) {
            return;
        }

        try {
            File file = new File(partiesFolder, party. getPartyId() + ".yml");
            var config = new YamlConfiguration();
            
            // Party 객체를 YAML로 변환 (구현 필요)
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 파티 저장 실패: " + party.getPartyId());
        }
    }

    /**
     * 파티 삭제
     */
    public void deleteParty(UUID partyId) {
        File file = new File(partiesFolder, partyId + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }

    // ===== 인스턴스 관련 =====

    /**
     * 모든 인스턴스 로드
     */
    public List<DungeonInstance> loadAllInstances() {
        List<DungeonInstance> instances = new ArrayList<>();
        
        File[] files = instancesFolder.listFiles();
        if (files == null) {
            return instances;
        }

        for (File file : files) {
            if (!file. getName().endsWith(".yml")) continue;
            
            try {
                var config = YamlConfiguration.loadConfiguration(file);
                // 여기서 YAML을 DungeonInstance 객체로 변환 (구현 필요)
            } catch (Exception e) {
                plugin.getLogger().warning("⚠️ 인스턴스 로드 실패: " + file.getName());
            }
        }

        return instances;
    }

    /**
     * 인스턴스 저장
     */
    public void saveInstance(DungeonInstance instance) {
        if (instance == null) {
            return;
        }

        try {
            File file = new File(instancesFolder, instance.getInstanceId() + ".yml");
            var config = new YamlConfiguration();
            
            // DungeonInstance 객체를 YAML로 변환 (구현 필요)
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 인스턴스 저장 실패: " + instance.getInstanceId());
        }
    }

    /**
     * 인스턴스 삭제
     */
    public void deleteInstance(UUID instanceId) {
        File file = new File(instancesFolder, instanceId + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }

    // ===== 플레이어 데이터 관련 =====

    /**
     * 모든 플레이어 데이터 로드
     */
    public List<PlayerDungeonData> loadAllPlayerData() {
        List<PlayerDungeonData> playerDataList = new ArrayList<>();
        
        File[] files = playerDataFolder.listFiles();
        if (files == null) {
            return playerDataList;
        }

        for (File file : files) {
            if (!file.getName().endsWith(".yml")) continue;
            
            try {
                var config = YamlConfiguration. loadConfiguration(file);
                // 여기서 YAML을 PlayerDungeonData 객체로 변환 (구현 필요)
            } catch (Exception e) {
                plugin.getLogger().warning("⚠️ 플레이어 데이터 로드 실패: " + file. getName());
            }
        }

        return playerDataList;
    }

    /**
     * 플레이어 데이터 조회
     */
    public PlayerDungeonData getPlayerData(UUID playerId) {
        try {
            File file = new File(playerDataFolder, playerId + ".yml");
            if (!file.exists()) {
                return new PlayerDungeonData(playerId, "Unknown");
            }
            
            var config = YamlConfiguration.loadConfiguration(file);
            // 여기서 YAML을 PlayerDungeonData 객체로 변환 (구현 필요)
            return new PlayerDungeonData(playerId, "Unknown");
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 플레이어 데이터 로드 실패: " + playerId);
            return new PlayerDungeonData(playerId, "Unknown");
        }
    }

    /**
     * 플레이어 데이터 저장
     */
    public void savePlayerData(PlayerDungeonData playerData) {
        if (playerData == null) {
            return;
        }

        try {
            File file = new File(playerDataFolder, playerData.getPlayerId() + ".yml");
            var config = new YamlConfiguration();
            
            // PlayerDungeonData 객체를 YAML로 변환 (구현 필요)
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 플레이어 데이터 저장 실패: " + playerData.getPlayerId());
        }
    }

    // ===== 기록 관련 =====

    /**
     * 모든 기록 로드
     */
    public List<DungeonRecord> loadAllRecords() {
        List<DungeonRecord> records = new ArrayList<>();
        
        File[] files = recordsFolder.listFiles();
        if (files == null) {
            return records;
        }

        for (File file : files) {
            if (!file.getName(). endsWith(".yml")) continue;
            
            try {
                var config = YamlConfiguration.loadConfiguration(file);
                // 여기서 YAML을 DungeonRecord 객체로 변환 (구현 필요)
            } catch (Exception e) {
                plugin. getLogger().warning("⚠️ 기록 로드 실패: " + file. getName());
            }
        }

        return records;
    }

    /**
     * 기록 저장
     */
    public void saveRecord(DungeonRecord record) {
        if (record == null) {
            return;
        }

        try {
            File file = new File(recordsFolder, record.getRecordId() + ".yml");
            var config = new YamlConfiguration();
            
            // DungeonRecord 객체를 YAML로 변환 (구현 필요)
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 기록 저장 실패: " + record.getRecordId());
        }
    }

    /**
     * 모든 데이터 저장
     */
    public void saveAllData() {
        plugin.getDungeonManager().saveAllDungeons();
        plugin.getPartyManager().saveAllParties();
        plugin.getInstanceManager().saveAllInstances();
        plugin.getRewardManager().saveAllDungeonPoints();
        plugin.getLeaderboardManager().saveAllRecords();
        
        plugin.getLogger().info("✅ 모든 데이터가 저장되었습니다.");
    }

    /**
     * 모든 플레이어 데이터 조회
     */
    public List<PlayerDungeonData> getAllPlayerData() {
        return loadAllPlayerData();
    }
}