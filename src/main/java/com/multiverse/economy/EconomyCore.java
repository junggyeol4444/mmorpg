package com.multiverse.economy;

import com.multiverse.economy.commands.*;
import com.multiverse.economy.managers.*;
import com.multiverse.economy.listeners.*;
import com.multiverse.economy.data.*;
import com.multiverse.economy.vault.VaultEconomyProvider;
import com.multiverse.economy.api.EconomyAPI;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.utils.MessageUtil;
import com.multiverse.economy.tasks.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

public class EconomyCore extends JavaPlugin {

    // Managers
    private CurrencyManager currencyManager;
    private ExchangeManager exchangeManager;
    private BankManager bankManager;
    private TaxManager taxManager;
    private PaymentManager paymentManager;
    private StatisticsManager statisticsManager;
    private InflationManager inflationManager;
    private EconomyDataManager economyDataManager;

    // Data
    private DataManager dataManager;
    private ConfigUtil configUtil;
    private MessageUtil messageUtil;

    @Override
    public void onEnable() {
        // Load configs
        this.saveDefaultConfig();
        configUtil = new ConfigUtil(this);
        messageUtil = new MessageUtil(this);

        // Load DataManager
        dataManager = new YAMLDataManager(this);
        economyDataManager = new EconomyDataManager(this, dataManager, configUtil, messageUtil);

        // Load Managers
        currencyManager = new CurrencyManager(this, configUtil, economyDataManager, messageUtil);
        exchangeManager = new ExchangeManager(this, configUtil, currencyManager, economyDataManager, messageUtil, statisticsManager);
        bankManager = new BankManager(this, configUtil, economyDataManager, currencyManager, messageUtil, statisticsManager);
        taxManager = new TaxManager(this, configUtil, messageUtil, statisticsManager, inflationManager);
        paymentManager = new PaymentManager(this, configUtil, currencyManager, taxManager, statisticsManager, bankManager, messageUtil);
        statisticsManager = new StatisticsManager(this, configUtil, economyDataManager, currencyManager, messageUtil);
        inflationManager = new InflationManager(this, configUtil, statisticsManager, currencyManager, economyDataManager, messageUtil);

        // Setup API instance
        EconomyAPI.init(this);

        // Vault hook
        if (configUtil.getBoolean("vault.enabled", true)) {
            VaultEconomyProvider vaultProvider = new VaultEconomyProvider(this, currencyManager, paymentManager, configUtil);
            vaultProvider.register();
        }

        // Register Commands
        new CommandManager(this, configUtil, messageUtil)
            .register(
                new AdminCommand(this, currencyManager, exchangeManager, bankManager, taxManager, statisticsManager, inflationManager, messageUtil, configUtil),
                new MoneyCommand(this, currencyManager, statisticsManager, paymentManager, configUtil, messageUtil),
                new PayCommand(this, paymentManager, configUtil, messageUtil),
                new ExchangeCommand(this, exchangeManager, currencyManager, paymentManager, configUtil, messageUtil),
                new BankCommand(this, bankManager, currencyManager, configUtil, messageUtil),
                new CheckCommand(this, paymentManager, currencyManager, configUtil, messageUtil)
            );

        // Register Listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EconomyListener(this, economyDataManager, currencyManager, bankManager, configUtil, messageUtil), this);
        pm.registerEvents(new TransactionListener(this, taxManager, statisticsManager, configUtil, messageUtil), this);

        // Schedule Tasks
        new AutoSaveTask(this, dataManager, economyDataManager, configUtil).start();
        new InterestPaymentTask(this, bankManager, configUtil, messageUtil).start();
        new LoanCheckTask(this, bankManager, configUtil, messageUtil).start();
        new StatisticsUpdateTask(this, statisticsManager, configUtil).start();
        new InflationControlTask(this, inflationManager, configUtil, statisticsManager, messageUtil).start();

        getLogger().info("EconomyCore enabled!");
    }

    @Override
    public void onDisable() {
        // Save all player data
        economyDataManager.saveAll();
        getLogger().info("EconomyCore disabled!");
    }

    // Getters for managers
    public CurrencyManager getCurrencyManager() { return currencyManager; }
    public ExchangeManager getExchangeManager() { return exchangeManager; }
    public BankManager getBankManager() { return bankManager; }
    public TaxManager getTaxManager() { return taxManager; }
    public PaymentManager getPaymentManager() { return paymentManager; }
    public StatisticsManager getStatisticsManager() { return statisticsManager; }
    public InflationManager getInflationManager() { return inflationManager; }
    public EconomyDataManager getEconomyDataManager() { return economyDataManager; }
    public ConfigUtil getConfigUtil() { return configUtil; }
    public MessageUtil getMessageUtil() { return messageUtil; }
    public DataManager getDataManager() { return dataManager; }
}