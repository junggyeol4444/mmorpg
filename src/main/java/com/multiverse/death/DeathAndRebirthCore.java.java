package com.multiverse.death;

import com.multiverse.death.commands.CommandManager;
import com.multiverse.death.data.DataManager;
import com.multiverse.death.data.YAMLDataManager;
import com.multiverse.death.managers.*;
import com.multiverse.death.listeners.*;
import com.multiverse.death.tasks.*;
import com.multiverse.death.utils.ConfigUtil;
import com.multiverse.death.utils.MessageUtil;
import com.multiverse.death.api.DeathAndRebirthAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

public class DeathAndRebirthCore extends JavaPlugin {

    // -------- 매니저 인스턴스 --------
    private DataManager dataManager;
    private DeathManager deathManager;
    private RevivalManager revivalManager;
    private InsuranceManager insuranceManager;
    private SoulCoinManager soulCoinManager;
    private NetherRealmManager netherRealmManager;
    private NPCManager npcManager;
    private CommandManager commandManager;

    // 유틸/설정
    private ConfigUtil configUtil;
    private MessageUtil messageUtil;
    private Economy economy;

    // 외부 플러그인 연동
    private boolean multiverseEnabled = false;
    private boolean playerDataCoreEnabled = false;
    private boolean citizensEnabled = false;
    private boolean vaultEnabled = false;
    private boolean protocolLibEnabled = false;

    @Override
    public void onEnable() {
        // -------- 플러그인 초기화 --------
        saveDefaultConfig();
        configUtil = new ConfigUtil(this);
        messageUtil = new MessageUtil(configUtil);

        // 플러그인 의존성 체크
        multiverseEnabled = Bukkit.getPluginManager().isPluginEnabled("MultiverseCore");
        playerDataCoreEnabled = Bukkit.getPluginManager().isPluginEnabled("PlayerDataCore");
        citizensEnabled = Bukkit.getPluginManager().isPluginEnabled("Citizens");
        vaultEnabled = setupEconomy();
        protocolLibEnabled = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");

        // 파일&데이터 초기화
        dataManager = new YAMLDataManager(this);
        soulCoinManager = new SoulCoinManager(this, dataManager);
        insuranceManager = new InsuranceManager(this, dataManager, soulCoinManager);
        deathManager = new DeathManager(this, dataManager, insuranceManager, soulCoinManager);
        netherRealmManager = new NetherRealmManager(this, dataManager, configUtil);
        npcManager = new NPCManager(this, dataManager, netherRealmManager, messageUtil, configUtil);
        revivalManager = new RevivalManager(this, dataManager, deathManager, insuranceManager, soulCoinManager, netherRealmManager, configUtil, messageUtil);

        // 명령어 등록
        commandManager = new CommandManager(this, messageUtil, configUtil);

        // 리스너 등록
        registerListeners();

        // 태스크 등록
        new AutoSaveTask(this, dataManager).start();
        new InsuranceExpiryCheckTask(this, insuranceManager, messageUtil, configUtil).start();
        new NetherRealmParticleTask(this, netherRealmManager).start();

        // API 초기화
        DeathAndRebirthAPI.init(this);

        getLogger().info("DeathAndRebirthCore 플러그인 활성화 완료!");
    }

    @Override
    public void onDisable() {
        // 데이터 저장
        dataManager.saveAll();
        getLogger().info("DeathAndRebirthCore 플러그인 비활성화, 데이터 저장 완료.");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new DeathListener(
            this, deathManager, insuranceManager, netherRealmManager, messageUtil, configUtil), this);
        Bukkit.getPluginManager().registerEvents(new PvPListener(netherRealmManager, messageUtil, configUtil), this);
        Bukkit.getPluginManager().registerEvents(new NPCInteractListener(npcManager, configUtil), this);
        Bukkit.getPluginManager().registerEvents(new RevivalQuestListener(
            revivalManager, deathManager, insuranceManager, netherRealmManager, soulCoinManager, messageUtil, configUtil), this);
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault 플러그인 미설치! 경제 기능 비활성화됨.");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null || rsp.getProvider() == null) {
            getLogger().severe("Vault의 Economy 연결 실패! 경제 기능 비활성화됨.");
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    // --------------- 매니저 및 유틸 getter ---------------
    public DeathManager getDeathManager() { return deathManager; }
    public RevivalManager getRevivalManager() { return revivalManager; }
    public InsuranceManager getInsuranceManager() { return insuranceManager; }
    public SoulCoinManager getSoulCoinManager() { return soulCoinManager; }
    public NetherRealmManager getNetherRealmManager() { return netherRealmManager; }
    public NPCManager getNPCManager() { return npcManager; }
    public DataManager getDataManager() { return dataManager; }
    public CommandManager getCommandManager() { return commandManager; }

    public ConfigUtil getConfigUtil() { return configUtil; }
    public MessageUtil getMessageUtil() { return messageUtil; }
    public Economy getEconomy() { return economy; }

    // --------------- 외부 플러그인 getter ---------------
    public boolean isMultiverseEnabled() { return multiverseEnabled; }
    public boolean isPlayerDataCoreEnabled() { return playerDataCoreEnabled; }
    public boolean isVaultEnabled() { return vaultEnabled; }
    public boolean isCitizensEnabled() { return citizensEnabled; }
    public boolean isProtocolLibEnabled() { return protocolLibEnabled; }
}