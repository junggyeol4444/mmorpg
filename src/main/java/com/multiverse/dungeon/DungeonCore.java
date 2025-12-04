package com.multiverse.dungeon;

import com.multiverse.dungeon.api.DungeonAPI;
import com.multiverse. dungeon.commands.DungeonCommand;
import com. multiverse.dungeon.commands.PartyCommand;
import com.multiverse.dungeon.listeners.*;
import com.multiverse.dungeon.managers.*;
import com.multiverse.dungeon.tasks.*;
import com.multiverse. dungeon.utils.*;
import com.multiverse.dungeon.hooks.*;
import org.bukkit. Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * DungeonCore 플러그인 메인 클래스
 * 던전, 인스턴스, 파티, 보스, 보상 시스템 관리
 */
public class DungeonCore extends JavaPlugin {

    private static DungeonCore instance;
    
    // 매니저들
    private DungeonManager dungeonManager;
    private InstanceManager instanceManager;
    private PartyManager partyManager;
    private BossManager bossManager;
    private RewardManager rewardManager;
    private LeaderboardManager leaderboardManager;
    private DungeonDataManager dataManager;
    private ScalingManager scalingManager;
    private RandomDungeonManager randomDungeonManager;
    private InviteManager inviteManager;
    
    // 훅들
    private MythicMobsHook mythicMobsHook;
    private EconomyCoreHook economyCoreHook;
    private PlayerDataCoreHook playerDataCoreHook;
    private CombatCoreHook combatCoreHook;
    private QuestCoreHook questCoreHook;
    private MultiverseCoreHook multiverseCoreHook;
    private WorldEditHook worldEditHook;
    private WorldGuardHook worldGuardHook;

    @Override
    public void onEnable() {
        instance = this;
        
        // 설정 파일 로드
        saveDefaultConfig();
        reloadConfig();
        
        // 매니저 초기화
        initializeManagers();
        
        // 훅 초기화
        initializeHooks();
        
        // 리스너 등록
        registerListeners();
        
        // 명령어 등록
        registerCommands();
        
        // 태스크 등록
        registerTasks();
        
        // API 초기화
        DungeonAPI.init(this);
        
        getLogger().info("===================================");
        getLogger().info("✅ DungeonCore v1.0.0 활성화됨");
        getLogger().info("===================================");
    }

    @Override
    public void onDisable() {
        // 모든 활성 인스턴스 정리
        if (instanceManager != null) {
            instanceManager.cleanupExpiredInstances();
        }
        
        // 데이터 저장
        if (dataManager != null) {
            dataManager.saveAllData();
        }
        
        // 타이머 취소
        Bukkit.getScheduler().cancelTasks(this);
        
        getLogger().info("===================================");
        getLogger().info("❌ DungeonCore 비활성화됨");
        getLogger().info("===================================");
    }

    /**
     * 모든 매니저 초기화
     */
    private void initializeManagers() {
        try {
            dataManager = new DungeonDataManager(this);
            scalingManager = new ScalingManager(getConfig());
            
            dungeonManager = new DungeonManager(this, dataManager);
            instanceManager = new InstanceManager(this, dataManager);
            partyManager = new PartyManager(this, dataManager);
            bossManager = new BossManager(this, instanceManager);
            rewardManager = new RewardManager(this, dataManager);
            leaderboardManager = new LeaderboardManager(this, dataManager);
            randomDungeonManager = new RandomDungeonManager(this, dataManager);
            inviteManager = new InviteManager(this);
            
            getLogger().info("✅ 모든 매니저 초기화 완료");
        } catch (Exception e) {
            getLogger().severe("❌ 매니저 초기화 실패: " + e. getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    /**
     * 모든 플러그인 훅 초기화
     */
    private void initializeHooks() {
        try {
            if (Bukkit.getPluginManager(). getPlugin("MythicMobs") != null) {
                mythicMobsHook = new MythicMobsHook(this);
                getLogger().info("✅ MythicMobs 훅 연결됨");
            }
            
            if (Bukkit.getPluginManager().getPlugin("EconomyCore") != null) {
                economyCoreHook = new EconomyCoreHook(this);
                getLogger().info("✅ EconomyCore 훅 연결됨");
            }
            
            if (Bukkit.getPluginManager().getPlugin("PlayerDataCore") != null) {
                playerDataCoreHook = new PlayerDataCoreHook(this);
                getLogger().info("✅ PlayerDataCore 훅 연결됨");
            }
            
            if (Bukkit.getPluginManager().getPlugin("CombatCore") != null) {
                combatCoreHook = new CombatCoreHook(this);
                getLogger().info("✅ CombatCore 훅 연결됨");
            }
            
            if (Bukkit.getPluginManager().getPlugin("QuestCore") != null) {
                questCoreHook = new QuestCoreHook(this);
                getLogger().info("✅ QuestCore 훅 연결됨");
            }
            
            if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null) {
                multiverseCoreHook = new MultiverseCoreHook(this);
                getLogger().info("✅ Multiverse-Core 훅 연결됨");
            }
            
            if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
                worldEditHook = new WorldEditHook(this);
                getLogger().info("✅ WorldEdit 훅 연결됨");
            }
            
            if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
                worldGuardHook = new WorldGuardHook(this);
                getLogger().info("✅ WorldGuard 훅 연결됨");
            }
        } catch (Exception e) {
            getLogger().warning("⚠️ 일부 플러그인 훅 초기화 실패: " + e.getMessage());
        }
    }

    /**
     * 모든 리스너 등록
     */
    private void registerListeners() {
        Bukkit.getPluginManager(). registerEvents(new DungeonListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PartyListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BossListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InstanceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        
        getLogger().info("✅ 모든 리스너 등록 완료");
    }

    /**
     * 모든 명령어 등록
     */
    private void registerCommands() {
        getCommand("dungeon").setExecutor(new DungeonCommand(this));
        getCommand("party").setExecutor(new PartyCommand(this));
        
        getLogger().info("✅ 모든 명령어 등록 완료");
    }

    /**
     * 모든 비동기 태스크 등록
     */
    private void registerTasks() {
        // 인스턴스 틱 태스크 (1틱마다 = 50ms)
        Bukkit. getScheduler().scheduleSyncRepeatingTask(this, 
            new InstanceTickTask(this), 1L, 1L);
        
        // 인스턴스 정리 태스크 (5초마다)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, 
            new InstanceCleanupTask(this), 100L, 100L);
        
        // 보스 AI 태스크 (2틱마다)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, 
            new BossAITask(this), 1L, 2L);
        
        // 시간 경고 태스크 (1초마다)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, 
            new TimeWarningTask(this), 20L, 20L);
        
        // 초대 만료 태스크 (5초마다)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, 
            new InviteExpiryTask(this), 100L, 100L);
        
        // 리더보드 업데이트 (6초마다 = 300틱)
        Bukkit. getScheduler().scheduleSyncRepeatingTask(this, 
            new LeaderboardUpdateTask(this), 300L, 300L);
        
        // 자동 저장 (5분마다)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, 
            new AutoSaveTask(this), 6000L, 6000L);
        
        getLogger().info("✅ 모든 비동기 태스크 등록 완료");
    }

    // ===== Getters =====

    public static DungeonCore getInstance() {
        return instance;
    }

    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    public InstanceManager getInstanceManager() {
        return instanceManager;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public BossManager getBossManager() {
        return bossManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public DungeonDataManager getDataManager() {
        return dataManager;
    }

    public ScalingManager getScalingManager() {
        return scalingManager;
    }

    public RandomDungeonManager getRandomDungeonManager() {
        return randomDungeonManager;
    }

    public InviteManager getInviteManager() {
        return inviteManager;
    }

    public MythicMobsHook getMythicMobsHook() {
        return mythicMobsHook;
    }

    public EconomyCoreHook getEconomyCoreHook() {
        return economyCoreHook;
    }

    public PlayerDataCoreHook getPlayerDataCoreHook() {
        return playerDataCoreHook;
    }

    public CombatCoreHook getCombatCoreHook() {
        return combatCoreHook;
    }

    public QuestCoreHook getQuestCoreHook() {
        return questCoreHook;
    }

    public MultiverseCoreHook getMultiverseCoreHook() {
        return multiverseCoreHook;
    }

    public WorldEditHook getWorldEditHook() {
        return worldEditHook;
    }

    public WorldGuardHook getWorldGuardHook() {
        return worldGuardHook;
    }
}