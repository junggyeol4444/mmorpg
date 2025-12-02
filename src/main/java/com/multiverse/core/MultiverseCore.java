package com.multiverse.core;

import com.multiverse.core.api.MultiverseCoreAPI;
import com.multiverse.core.commands.AdminCommand;
import com.multiverse.core.commands.CommandManager;
import com.multiverse.core.commands.PlayerCommand;
import com.multiverse.core.data.YAMLDataManager;
import com.multiverse.core.managers.*;
import com.multiverse.core.listeners.*;
import com.multiverse.core.utils.ConfigUtil;
import com.multiverse.core.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiverseCore extends JavaPlugin {

    // Core managers
    private DimensionManager dimensionManager;
    private BalanceManager balanceManager;
    private TeleportManager teleportManager;
    private PortalManager portalManager;
    private WaypointManager waypointManager;
    private FusionManager fusionManager;

    // Data manager
    private YAMLDataManager dataManager;

    // Command manager
    private CommandManager commandManager;

    // Utilities
    private ConfigUtil configUtil;
    private MessageUtil messageUtil;

    // Singleton instance
    private static MultiverseCore instance;

    public static MultiverseCore getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        MultiverseCoreAPI.init(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configUtil = new ConfigUtil(this);
        messageUtil = new MessageUtil(this);

        dataManager = new YAMLDataManager(this);
        dataManager.load();

        // Create managers (load from dataManager)
        dimensionManager = new DimensionManager(this, dataManager);
        balanceManager = new BalanceManager(this, dataManager, dimensionManager);
        teleportManager = new TeleportManager(this, dataManager, balanceManager, dimensionManager);
        portalManager = new PortalManager(this, dataManager, dimensionManager);
        waypointManager = new WaypointManager(this, dataManager, dimensionManager);
        fusionManager = new FusionManager(this, dataManager, dimensionManager, balanceManager, portalManager);

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PortalListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TeleportListener(this), this);

        // Command manager and registration
        commandManager = new CommandManager(this);
        commandManager.register(new AdminCommand(this));
        commandManager.register(new PlayerCommand(this));
        getCommand("mdim").setExecutor(commandManager);
        getCommand("dimension").setExecutor(commandManager);

        // Start async tasks (autosave, balance, fusion stages, etc.)
        balanceManager.scheduleAutoAdjust();
        balanceManager.scheduleBalanceDecay();
        fusionManager.scheduleFusionStages();
        dataManager.scheduleAutoSave();

        getLogger().info("[MultiverseCore] 플러그인이 정상적으로 활성화되었습니다.");
    }

    @Override
    public void onDisable() {
        dataManager.save();
        getLogger().info("[MultiverseCore] 데이터가 저장되었습니다. 플러그인 종료.");
    }

    // Manager getters
    public DimensionManager getDimensionManager() { return dimensionManager; }
    public BalanceManager getBalanceManager() { return balanceManager; }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public PortalManager getPortalManager() { return portalManager; }
    public WaypointManager getWaypointManager() { return waypointManager; }
    public FusionManager getFusionManager() { return fusionManager; }
    public YAMLDataManager getDataManager() { return dataManager; }
    public ConfigUtil getConfigUtil() { return configUtil; }
    public MessageUtil getMessageUtil() { return messageUtil; }
}