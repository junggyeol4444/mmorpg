package com. multiverse.item;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit. Bukkit;
import org.bukkit.event.Listener;
import com.multiverse.item.managers.*;
import com.multiverse.item.commands.ItemCommand;
import com. multiverse.item.listeners.*;
import com.multiverse.item.tasks.*;
import java.io.File;

public class ItemCore extends JavaPlugin implements Listener {
    
    private static ItemCore instance;
    
    // Managers
    private ConfigManager configManager;
    private DataManager dataManager;
    private ItemManager itemManager;
    private ItemGenerator itemGenerator;
    private EnhanceManager enhanceManager;
    private ItemOptionManager itemOptionManager;
    private SetManager setManager;
    private GemManager gemManager;
    private IdentifySystem identifySystem;
    private DisassembleSystem disassembleSystem;
    private TradeManager tradeManager;
    
    // Tasks
    private AutoSaveTask autoSaveTask;
    private OptionTickTask optionTickTask;
    private SetEffectTask setEffectTask;
    private DurabilityTask durabilityTask;
    private DataCleanupTask dataCleanupTask;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // 로그 출력
        getLogger().info("╔════════════════════════════════════╗");
        getLogger().info("║    ItemCore v1.0.0 활성화중...     ║");
        getLogger(). info("╚════════════════════════════════════╝");
        
        try {
            // 폴더 생성
            createFolders();
            
            // 설정 로드
            loadConfigs();
            
            // 매니저 초기화
            initializeManagers();
            
            // 리스너 등록
            registerListeners();
            
            // 커맨드 등록
            registerCommands();
            
            // 작업 시작
            startTasks();
            
            getLogger().info("✅ ItemCore가 성공적으로 활성화되었습니다!");
            
        } catch (Exception e) {
            getLogger().severe("❌ ItemCore 활성화 중 오류 발생:");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        try {
            // 모든 작업 취소
            stopTasks();
            
            // 데이터 저장
            if (dataManager != null) {
                dataManager.saveAllData();
            }
            
            getLogger().info("✅ ItemCore가 성공적으로 비활성화되었습니다!");
            
        } catch (Exception e) {
            getLogger().severe("❌ ItemCore 비활성화 중 오류 발생:");
            e.printStackTrace();
        }
    }
    
    private void createFolders() {
        // 데이터 폴더 생성
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // 설정 폴더
        File configFolder = new File(getDataFolder(), "config");
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        
        // 데이터 폴더
        File dataFolder = new File(getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        // 플레이어 데이터 폴더
        File playerFolder = new File(getDataFolder(), "players");
        if (!playerFolder.exists()) {
            playerFolder.mkdirs();
        }
        
        // 백업 폴더
        File backupFolder = new File(getDataFolder(), "backups");
        if (!backupFolder.exists()) {
            backupFolder. mkdirs();
        }
    }
    
    private void loadConfigs() {
        // 설정 파일 복사 (없으면 생성)
        if (!new File(getDataFolder(), "config. yml").exists()) {
            saveResource("config.yml", false);
        }
        
        if (!new File(getDataFolder(), "messages.yml").exists()) {
            saveResource("messages.yml", false);
        }
        
        // 메인 설정 로드
        reloadConfig();
    }
    
    private void initializeManagers() {
        // 설정 매니저
        configManager = new ConfigManager(this);
        
        // 데이터 매니저
        dataManager = new DataManager(this, configManager);
        
        // 아이템 매니저
        itemManager = new ItemManager(this, dataManager);
        
        // 아이템 생성기
        itemGenerator = new ItemGenerator(this, configManager, dataManager);
        
        // 강화 매니저
        enhanceManager = new EnhanceManager(this, configManager, dataManager);
        
        // 옵션 매니저
        itemOptionManager = new ItemOptionManager(this, configManager, dataManager);
        
        // 세트 매니저
        setManager = new SetManager(this, configManager, dataManager);
        
        // 보석 매니저
        gemManager = new GemManager(this, configManager, dataManager);
        
        // 식별 시스템
        identifySystem = new IdentifySystem(this, configManager, dataManager);
        
        // 분해 시스템
        disassembleSystem = new DisassembleSystem(this, configManager, dataManager);
        
        // 거래 매니저
        tradeManager = new TradeManager(this, configManager, dataManager);
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager(). registerEvents(new ItemEquipListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemUnequipListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemUseListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemAttackListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemKillListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemPickupListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemDropListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
    }
    
    private void registerCommands() {
        getCommand("item").setExecutor(new ItemCommand(this));
    }
    
    private void startTasks() {
        // 자동 저장 작업
        int saveInterval = configManager.getConfig().getInt("data.auto-save-interval", 300);
        autoSaveTask = new AutoSaveTask(this);
        autoSaveTask.runTaskTimerAsynchronously(this, 20L * saveInterval, 20L * saveInterval);
        
        // 옵션 틱 작업
        optionTickTask = new OptionTickTask(this);
        optionTickTask.runTaskTimer(this, 0L, 1L);
        
        // 세트 효과 작업
        setEffectTask = new SetEffectTask(this);
        setEffectTask.runTaskTimer(this, 0L, 20L);
        
        // 내구도 감소 작업
        durabilityTask = new DurabilityTask(this);
        durabilityTask.runTaskTimer(this, 0L, 20L);
        
        // 데이터 정리 작업
        dataCleanupTask = new DataCleanupTask(this);
        dataCleanupTask.runTaskTimerAsynchronously(this, 20L * 3600, 20L * 3600); // 1시간마다
    }
    
    private void stopTasks() {
        if (autoSaveTask != null) autoSaveTask.cancel();
        if (optionTickTask != null) optionTickTask.cancel();
        if (setEffectTask != null) setEffectTask.cancel();
        if (durabilityTask != null) durabilityTask.cancel();
        if (dataCleanupTask != null) dataCleanupTask.cancel();
    }
    
    // Getters
    public static ItemCore getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public ItemManager getItemManager() {
        return itemManager;
    }
    
    public ItemGenerator getItemGenerator() {
        return itemGenerator;
    }
    
    public EnhanceManager getEnhanceManager() {
        return enhanceManager;
    }
    
    public ItemOptionManager getItemOptionManager() {
        return itemOptionManager;
    }
    
    public SetManager getSetManager() {
        return setManager;
    }
    
    public GemManager getGemManager() {
        return gemManager;
    }
    
    public IdentifySystem getIdentifySystem() {
        return identifySystem;
    }
    
    public DisassembleSystem getDisassembleSystem() {
        return disassembleSystem;
    }
    
    public TradeManager getTradeManager() {
        return tradeManager;
    }
}