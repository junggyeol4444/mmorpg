package com.multiverse.pvp;

import com.multiverse.pvp.api.PvPAPI;
import com. multiverse.pvp.commands.PvPCommand;
import com.multiverse.pvp.commands.PvPAdminCommand;
import com.multiverse. pvp.listeners.PvPListener;
import com. multiverse.pvp.listeners.ArenaListener;
import com.multiverse. pvp.listeners. DuelListener;
import com.multiverse.pvp.listeners.ZoneListener;
import com.multiverse. pvp.managers.*;
import com.multiverse.pvp. storage.*;
import com.multiverse.pvp. tasks.*;
import org.bukkit. Bukkit;
import org.bukkit. plugin.java.JavaPlugin;

public class PvPCore extends JavaPlugin {

    private static PvPCore instance;

    // Storage
    private DataManager dataManager;
    private PlayerDataStorage playerDataStorage;
    private ArenaStorage arenaStorage;
    private ZoneStorage zoneStorage;
    private SeasonStorage seasonStorage;

    // Managers
    private PvPModeManager pvpModeManager;
    private ArenaManager arenaManager;
    private DuelManager duelManager;
    private RankingManager rankingManager;
    private KillStreakManager killStreakManager;
    private RewardManager rewardManager;
    private TitleManager titleManager;
    private ZoneManager zoneManager;
    private StatisticsManager statisticsManager;
    private LeaderboardManager leaderboardManager;
    private SeasonManager seasonManager;

    // API
    private PvPAPI pvpAPI;

    // Tasks
    private AutoSaveTask autoSaveTask;
    private ArenaTask arenaTask;
    private LeaderboardUpdateTask leaderboardUpdateTask;
    private MatchmakingTask matchmakingTask;

    @Override
    public void onEnable() {
        instance = this;

        // 설정 파일 로드
        saveDefaultConfig();
        reloadConfig();

        // 의존성 체크
        if (!checkDependencies()) {
            getLogger().severe("필수 의존 플러그인이 없습니다!  플러그인을 비활성화합니다.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Storage 초기화
        initializeStorage();

        // Manager 초기화
        initializeManagers();

        // API 초기화
        pvpAPI = new PvPAPI(this);

        // 리스너 등록
        registerListeners();

        // 명령어 등록
        registerCommands();

        // Task 시작
        startTasks();

        // 데이터 로드
        loadAllData();

        getLogger().info("PvPCore v" + getDescription().getVersion() + " 활성화됨!");
    }

    @Override
    public void onDisable() {
        // Task 중지
        stopTasks();

        // 진행 중인 듀얼/아레나 종료
        if (duelManager != null) {
            duelManager.endAllDuels();
        }
        if (arenaManager != null) {
            arenaManager. endAllArenas();
        }

        // 데이터 저장
        saveAllData();

        getLogger().info("PvPCore v" + getDescription().getVersion() + " 비활성화됨!");
    }

    private boolean checkDependencies() {
        // 필수 의존 플러그인 체크
        if (Bukkit. getPluginManager().getPlugin("PlayerDataCore") == null) {
            getLogger().severe("PlayerDataCore 플러그인이 필요합니다!");
            return false;
        }
        if (Bukkit.getPluginManager().getPlugin("CombatCore") == null) {
            getLogger().severe("CombatCore 플러그인이 필요합니다!");
            return false;
        }

        // 선택 의존 플러그인 체크
        if (Bukkit.getPluginManager().getPlugin("EconomyCore") != null) {
            getLogger().info("EconomyCore 연동됨!");
        }
        if (Bukkit.getPluginManager().getPlugin("GuildCore") != null) {
            getLogger().info("GuildCore 연동됨!");
        }

        return true;
    }

    private void initializeStorage() {
        dataManager = new DataManager(this);
        playerDataStorage = new PlayerDataStorage(this);
        arenaStorage = new ArenaStorage(this);
        zoneStorage = new ZoneStorage(this);
        seasonStorage = new SeasonStorage(this);
    }

    private void initializeManagers() {
        pvpModeManager = new PvPModeManager(this);
        arenaManager = new ArenaManager(this);
        duelManager = new DuelManager(this);
        rankingManager = new RankingManager(this);
        killStreakManager = new KillStreakManager(this);
        rewardManager = new RewardManager(this);
        titleManager = new TitleManager(this);
        zoneManager = new ZoneManager(this);
        statisticsManager = new StatisticsManager(this);
        leaderboardManager = new LeaderboardManager(this);
        seasonManager = new SeasonManager(this);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PvPListener(this), this);
        Bukkit. getPluginManager().registerEvents(new ArenaListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DuelListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ZoneListener(this), this);
    }

    private void registerCommands() {
        PvPCommand pvpCommand = new PvPCommand(this);
        getCommand("pvp").setExecutor(pvpCommand);
        getCommand("pvp").setTabCompleter(pvpCommand);
    }

    private void startTasks() {
        int autoSaveInterval = getConfig().getInt("data.auto-save-interval", 300) * 20;
        autoSaveTask = new AutoSaveTask(this);
        autoSaveTask.runTaskTimer(this, autoSaveInterval, autoSaveInterval);

        arenaTask = new ArenaTask(this);
        arenaTask.runTaskTimer(this, 20L, 20L);

        int leaderboardInterval = getConfig().getInt("leaderboard.update-interval", 300) * 20;
        leaderboardUpdateTask = new LeaderboardUpdateTask(this);
        leaderboardUpdateTask. runTaskTimer(this, leaderboardInterval, leaderboardInterval);

        matchmakingTask = new MatchmakingTask(this);
        matchmakingTask.runTaskTimer(this, 40L, 40L);
    }

    private void stopTasks() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }
        if (arenaTask != null) {
            arenaTask.cancel();
        }
        if (leaderboardUpdateTask != null) {
            leaderboardUpdateTask.cancel();
        }
        if (matchmakingTask != null) {
            matchmakingTask. cancel();
        }
    }

    private void loadAllData() {
        playerDataStorage.loadAllData();
        arenaStorage.loadAllArenas();
        zoneStorage.loadAllZones();
        seasonStorage.loadCurrentSeason();
        leaderboardManager.updateLeaderboard();
    }

    public void saveAllData() {
        if (playerDataStorage != null) {
            playerDataStorage.saveAllData();
        }
        if (arenaStorage != null) {
            arenaStorage.saveAllArenas();
        }
        if (zoneStorage != null) {
            zoneStorage.saveAllZones();
        }
        if (seasonStorage != null) {
            seasonStorage.saveCurrentSeason();
        }
    }

    public void reload() {
        reloadConfig();
        loadAllData();
        getLogger().info("PvPCore 설정이 리로드되었습니다.");
    }

    // Static instance getter
    public static PvPCore getInstance() {
        return instance;
    }

    // Storage getters
    public DataManager getDataManager() {
        return dataManager;
    }

    public PlayerDataStorage getPlayerDataStorage() {
        return playerDataStorage;
    }

    public ArenaStorage getArenaStorage() {
        return arenaStorage;
    }

    public ZoneStorage getZoneStorage() {
        return zoneStorage;
    }

    public SeasonStorage getSeasonStorage() {
        return seasonStorage;
    }

    // Manager getters
    public PvPModeManager getPvPModeManager() {
        return pvpModeManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public DuelManager getDuelManager() {
        return duelManager;
    }

    public RankingManager getRankingManager() {
        return rankingManager;
    }

    public KillStreakManager getKillStreakManager() {
        return killStreakManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public TitleManager getTitleManager() {
        return titleManager;
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public SeasonManager getSeasonManager() {
        return seasonManager;
    }

    // API getter
    public PvPAPI getPvPAPI() {
        return pvpAPI;
    }

    // 외부 플러그인 연동 체크
    public boolean hasEconomyCore() {
        return Bukkit.getPluginManager().getPlugin("EconomyCore") != null;
    }

    public boolean hasGuildCore() {
        return Bukkit.getPluginManager().getPlugin("GuildCore") != null;
    }
}