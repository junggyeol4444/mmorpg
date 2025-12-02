package com.multiverse.playerdata;

import com.multiverse.playerdata.api.PlayerDataAPI;
import com.multiverse.playerdata.commands.CommandManager;
import com.multiverse.playerdata.data.DataManager;
import com.multiverse.playerdata.data.YAMLDataManager;
import com.multiverse.playerdata.listeners.CombatListener;
import com.multiverse.playerdata.listeners.PlayerDataListener;
import com.multiverse.playerdata.managers.*;
import com.multiverse.playerdata.tasks.AutoSaveTask;
import com.multiverse.playerdata.tasks.BackupTask;
import com.multiverse.playerdata.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PlayerDataCore extends JavaPlugin {

    private RaceManager raceManager;
    private StatsManager statsManager;
    private EvolutionManager evolutionManager;
    private TranscendenceManager transcendenceManager;
    private PlayerDataManager playerDataManager;
    private DataManager dataManager;
    private ConfigUtil configUtil;

    private AutoSaveTask autoSaveTask;
    private BackupTask backupTask;

    private static PlayerDataCore instance;

    public static PlayerDataCore getInstance() { return instance; }

    @Override
    public void onEnable() {
        instance = this;

        // 1. 경로/폴더/설정 파일 준비
        createDefaultConfigFiles();

        // 2. 컨피그 관리 초기화
        configUtil = new ConfigUtil(this);

        // 3. 데이터 매니저(YAML/MySQL) 선택
        String dataType = configUtil.getString("data.type", "yaml");
        if (dataType.equalsIgnoreCase("mysql")) {
            // MySQLDataManager 구현 후 교체(초기버전은 YAML 고정)
            dataManager = new YAMLDataManager(this);
        } else {
            dataManager = new YAMLDataManager(this);
        }

        // 4. 핵심 매니저 초기화
        raceManager = new RaceManager(this, dataManager, configUtil);
        statsManager = new StatsManager(this, dataManager, configUtil);
        evolutionManager = new EvolutionManager(this, dataManager, configUtil);
        transcendenceManager = new TranscendenceManager(this, dataManager, configUtil);
        playerDataManager = new PlayerDataManager(this, dataManager, configUtil);

        // 5. API 초기화
        PlayerDataAPI.init(this);

        // 6. 리스너 등록
        Bukkit.getPluginManager().registerEvents(new PlayerDataListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CombatListener(this), this);
        // 추가적으로 LevelListener 등 있으면 등록

        // 7. 명령어 등록
        if(getCommand("pdata") != null) {
            getCommand("pdata").setExecutor(new CommandManager(this));
            getCommand("pdata").setTabCompleter(new CommandManager(this));
        }

        // 8. 자동 저장/백업 태스크 시작
        autoSaveTask = new AutoSaveTask(this);
        autoSaveTask.start();

        backupTask = new BackupTask(this);
        backupTask.start();

        getLogger().info("PlayerDataCore enabled!");
    }

    @Override
    public void onDisable() {
        // 모든 플레이어 데이터 저장
        try {
            playerDataManager.saveAllPlayerData();
        } catch(Exception e) {
            getLogger().warning("플레이어 데이터 저장 중 오류 발생: " + e.getMessage());
        }

        // 태스크 중지
        if(autoSaveTask != null) autoSaveTask.stop();
        if(backupTask != null) backupTask.stop();

        instance = null;

        getLogger().info("PlayerDataCore disabled!");
    }

    private void createDefaultConfigFiles() {
        saveDefaultConfig();
        makeResourceFile("races.yml");
        makeResourceFile("evolutions.yml");
        makeResourceFile("transcendent_powers.yml");
        makeResourceFile("stat_formulas.yml");
    }

    private void makeResourceFile(String name) {
        File resFile = new File(getDataFolder(), name);
        if (!resFile.exists()) {
            saveResource(name, false);
        }
    }

    // Getter for managers
    public RaceManager getRaceManager() { return raceManager; }
    public StatsManager getStatsManager() { return statsManager; }
    public EvolutionManager getEvolutionManager() { return evolutionManager; }
    public TranscendenceManager getTranscendenceManager() { return transcendenceManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public DataManager getDataManager() { return dataManager; }
    public ConfigUtil getConfigUtil() { return configUtil; }

}