package com.multiverse.party;

import com. multiverse.party. api.PartyAPI;
import com. multiverse.party. commands.CommandManager;
import com.multiverse.party.data.DataManager;
import com.multiverse.party.data.YAMLDataManager;
import com. multiverse.party. gui.GUIManager;
import com.multiverse.party.integration.IntegrationManager;
import com. multiverse.party. listeners.*;
import com.multiverse.party.managers.*;
import com.multiverse.party.skills.SkillRegistry;
import com. multiverse.party. tasks.*;
import com.multiverse.party.utils.ConfigUtil;
import com. multiverse.party. utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. plugin.java.JavaPlugin;
import org. bukkit.scheduler.BukkitTask;

import java. io.File;
import java.util.ArrayList;
import java.util.List;

public class PartyCore extends JavaPlugin {

    private static PartyCore instance;
    
    // 데이터 매니저
    private DataManager dataManager;
    
    // 핵심 매니저들
    private PartyManager partyManager;
    private PartyRoleManager partyRoleManager;
    private PartyBuffManager partyBuffManager;
    private ExpShareManager expShareManager;
    private LootManager lootManager;
    private PartyLevelManager partyLevelManager;
    private PartyChatManager partyChatManager;
    private PartyQuestManager partyQuestManager;
    private PartyFinder partyFinder;
    private PartyStatisticsManager partyStatisticsManager;
    private PartyInviteManager partyInviteManager;
    private PartySkillManager partySkillManager;
    private ContributionManager contributionManager;
    
    // 기타 매니저
    private CommandManager commandManager;
    private GUIManager guiManager;
    private IntegrationManager integrationManager;
    private SkillRegistry skillRegistry;
    
    // 태스크
    private List<BukkitTask> runningTasks;
    
    // 설정
    private ConfigUtil configUtil;
    private MessageUtil messageUtil;
    
    @Override
    public void onEnable() {
        instance = this;
        runningTasks = new ArrayList<>();
        
        getLogger().info("========================================");
        getLogger().info("PartyCore v" + getDescription().getVersion() + " 활성화 중.. .");
        getLogger().info("========================================");
        
        loadConfigurations();
        initializeUtilities();
        initializeDataManager();
        initializeManagers();
        initializeSkillRegistry();
        initializeIntegrations();
        registerCommands();
        registerListeners();
        initializeGUIManager();
        startTasks();
        
        PartyAPI.init(this);
        loadAllData();
        
        getLogger().info("========================================");
        getLogger().info("PartyCore가 성공적으로 활성화되었습니다!");
        getLogger().info("========================================");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("PartyCore 비활성화 중...");
        
        stopTasks();
        saveAllData();
        
        if (guiManager != null) {
            guiManager.closeAllGUIs();
        }
        
        cleanupManagers();
        
        getLogger().info("PartyCore가 비활성화되었습니다.");
        instance = null;
    }
    
    private void loadConfigurations() {
        saveDefaultConfig();
        saveResourceIfNotExists("messages. yml");
        saveResourceIfNotExists("buffs.yml");
        saveResourceIfNotExists("skills.yml");
        saveResourceIfNotExists("gui. yml");
        
        createDataFolders();
        
        getLogger().info("설정 파일 로드 완료");
    }
    
    private void saveResourceIfNotExists(String resourceName) {
        File file = new File(getDataFolder(), resourceName);
        if (!file.exists()) {
            saveResource(resourceName, false);
        }
    }
    
    private void createDataFolders() {
        String[] folders = {"parties", "players", "listings", "backups"};
        
        for (String folderName : folders) {
            File folder = new File(getDataFolder(), folderName);
            if (!folder.exists()) {
                folder. mkdirs();
            }
        }
    }
    
    private void initializeUtilities() {
        configUtil = new ConfigUtil(this);
        messageUtil = new MessageUtil(this);
        
        getLogger().info("유틸리티 초기화 완료");
    }
    
    private void initializeDataManager() {
        String dataType = getConfig().getString("data.type", "yaml").toLowerCase();
        
        switch (dataType) {
            case "yaml":
            default:
                dataManager = new YAMLDataManager(this);
                break;
        }
        
        dataManager.initialize();
        getLogger().info("데이터 매니저 초기화 완료 (타입: " + dataType + ")");
    }
    
    private void initializeManagers() {
        partyRoleManager = new PartyRoleManager(this);
        contributionManager = new ContributionManager(this);
        partyStatisticsManager = new PartyStatisticsManager(this);
        partyInviteManager = new PartyInviteManager(this);
        partyManager = new PartyManager(this);
        partyBuffManager = new PartyBuffManager(this);
        expShareManager = new ExpShareManager(this);
        lootManager = new LootManager(this);
        partyLevelManager = new PartyLevelManager(this);
        partyChatManager = new PartyChatManager(this);
        partyQuestManager = new PartyQuestManager(this);
        partyFinder = new PartyFinder(this);
        partySkillManager = new PartySkillManager(this);
        
        getLogger().info("매니저 초기화 완료");
    }
    
    private void initializeSkillRegistry() {
        skillRegistry = new SkillRegistry(this);
        skillRegistry.registerDefaultSkills();
        
        getLogger().info("스킬 레지스트리 초기화 완료");
    }
    
    private void initializeIntegrations() {
        integrationManager = new IntegrationManager(this);
        integrationManager.setupIntegrations();
        
        getLogger().info("외부 플러그인 연동 초기화 완료");
    }
    
    private void registerCommands() {
        commandManager = new CommandManager(this);
        commandManager.registerCommands();
        
        getLogger().info("명령어 등록 완료");
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PartyListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ExpShareListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CombatListener(this), this);
        Bukkit. getPluginManager().registerEvents(new LootListener(this), this);
        Bukkit.getPluginManager().registerEvents(new IntegrationListener(this), this);
        
        getLogger().info("이벤트 리스너 등록 완료");
    }
    
    private void initializeGUIManager() {
        guiManager = new GUIManager(this);
        
        getLogger().info("GUI 매니저 초기화 완료");
    }
    
    private void startTasks() {
        int autoSaveInterval = getConfig().getInt("data.auto-save-interval", 300) * 20;
        
        AutoSaveTask autoSaveTask = new AutoSaveTask(this);
        runningTasks.add(autoSaveTask. runTaskTimer(this, autoSaveInterval, autoSaveInterval));
        
        BuffUpdateTask buffUpdateTask = new BuffUpdateTask(this);
        runningTasks. add(buffUpdateTask.runTaskTimer(this, 20L, 20L));
        
        InviteExpireTask inviteExpireTask = new InviteExpireTask(this);
        runningTasks.add(inviteExpireTask.runTaskTimer(this, 20L, 20L));
        
        ListingCleanupTask listingCleanupTask = new ListingCleanupTask(this);
        runningTasks.add(listingCleanupTask.runTaskTimer(this, 1200L, 1200L));
        
        PartyBuffApplyTask buffApplyTask = new PartyBuffApplyTask(this);
        runningTasks.add(buffApplyTask.runTaskTimer(this, 100L, 100L));
        
        if (getConfig().getBoolean("party-finder.auto-matching. enabled", true)) {
            MatchmakingTask matchmakingTask = new MatchmakingTask(this);
            runningTasks.add(matchmakingTask.runTaskTimer(this, 100L, 100L));
        }
        
        StatisticsUpdateTask statisticsUpdateTask = new StatisticsUpdateTask(this);
        runningTasks.add(statisticsUpdateTask. runTaskTimer(this, 600L, 600L));
        
        getLogger().info("태스크 시작 완료");
    }
    
    private void stopTasks() {
        for (BukkitTask task :  runningTasks) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        runningTasks.clear();
        
        getLogger().info("태스크 중지 완료");
    }
    
    private void loadAllData() {
        dataManager.loadAllParties();
        dataManager.loadAllPlayerData();
        dataManager.loadAllListings();
        
        getLogger().info("모든 데이터 로드 완료");
    }
    
    private void saveAllData() {
        if (dataManager != null) {
            dataManager.saveAllParties();
            dataManager.saveAllPlayerData();
            dataManager.saveAllListings();
            
            getLogger().info("모든 데이터 저장 완료");
        }
    }
    
    private void cleanupManagers() {
        if (partyBuffManager != null) {
            partyBuffManager.removeAllBuffs();
        }
        
        if (partyInviteManager != null) {
            partyInviteManager.clearAllInvites();
        }
        
        if (lootManager != null) {
            lootManager.clearAllSessions();
        }
        
        getLogger().info("매니저 정리 완료");
    }
    
    public void reloadPlugin() {
        getLogger().info("PartyCore 리로드 중...");
        
        reloadConfig();
        configUtil.reloadAllConfigs();
        messageUtil.reloadMessages();
        
        if (skillRegistry != null) {
            skillRegistry. reloadSkills();
        }
        
        getLogger().info("PartyCore 리로드 완료");
    }
    
    public static PartyCore getInstance() {
        return instance;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public PartyManager getPartyManager() {
        return partyManager;
    }
    
    public PartyRoleManager getPartyRoleManager() {
        return partyRoleManager;
    }
    
    public PartyBuffManager getPartyBuffManager() {
        return partyBuffManager;
    }
    
    public ExpShareManager getExpShareManager() {
        return expShareManager;
    }
    
    public LootManager getLootManager() {
        return lootManager;
    }
    
    public PartyLevelManager getPartyLevelManager() {
        return partyLevelManager;
    }
    
    public PartyChatManager getPartyChatManager() {
        return partyChatManager;
    }
    
    public PartyQuestManager getPartyQuestManager() {
        return partyQuestManager;
    }
    
    public PartyFinder getPartyFinder() {
        return partyFinder;
    }
    
    public PartyStatisticsManager getPartyStatisticsManager() {
        return partyStatisticsManager;
    }
    
    public PartyInviteManager getPartyInviteManager() {
        return partyInviteManager;
    }
    
    public PartySkillManager getPartySkillManager() {
        return partySkillManager;
    }
    
    public ContributionManager getContributionManager() {
        return contributionManager;
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    public GUIManager getGuiManager() {
        return guiManager;
    }
    
    public IntegrationManager getIntegrationManager() {
        return integrationManager;
    }
    
    public SkillRegistry getSkillRegistry() {
        return skillRegistry;
    }
    
    public ConfigUtil getConfigUtil() {
        return configUtil;
    }
    
    public MessageUtil getMessageUtil() {
        return messageUtil;
    }
}