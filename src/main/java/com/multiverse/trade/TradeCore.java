package com.multiverse.trade;

import com. multiverse.trade. api.TradeAPI;
import com.multiverse.trade.commands.*;
import com.multiverse.trade.data.*;
import com.multiverse.trade.gui. GUIManager;
import com.multiverse. trade.listeners.*;
import com.multiverse.trade.managers.*;
import com. multiverse.trade. tasks.*;
import com.multiverse.trade.utils. ConfigUtil;
import com.multiverse.trade.utils.MessageUtil;
import net.milkbowl. vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java. io.File;
import java. util.logging.Level;

public class TradeCore extends JavaPlugin {

    private static TradeCore instance;
    
    // Vault Economy
    private Economy economy;
    
    // Managers
    private TradeManager tradeManager;
    private PlayerShopManager playerShopManager;
    private AuctionManager auctionManager;
    private MarketManager marketManager;
    private MailManager mailManager;
    private PriceTracker priceTracker;
    private TransactionFeeManager transactionFeeManager;
    private TradeSecurityManager tradeSecurityManager;
    private GUIManager guiManager;
    
    // Data Managers
    private DataManager dataManager;
    private ShopDataManager shopDataManager;
    private AuctionDataManager auctionDataManager;
    private MarketDataManager marketDataManager;
    private MailDataManager mailDataManager;
    private PlayerTradeDataManager playerTradeDataManager;
    
    // Tasks
    private AutoSaveTask autoSaveTask;
    private AuctionCheckTask auctionCheckTask;
    private MarketMatchTask marketMatchTask;
    private MailExpiryTask mailExpiryTask;
    private PriceUpdateTask priceUpdateTask;

    @Override
    public void onEnable() {
        instance = this;
        
        // 설정 파일 저장
        saveDefaultConfig();
        createDataFolders();
        
        // Vault 연동
        if (! setupEconomy()) {
            getLogger().severe("Vault Economy를 찾을 수 없습니다!  플러그인을 비활성화합니다.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // 의존 플러그인 확인
        if (! checkDependencies()) {
            getLogger().severe("필수 의존 플러그인이 없습니다!  플러그인을 비활성화합니다.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // 유틸리티 초기화
        MessageUtil.init(this);
        ConfigUtil.init(this);
        
        // 데이터 매니저 초기화
        initDataManagers();
        
        // 매니저 초기화
        initManagers();
        
        // GUI 매니저 초기화
        guiManager = new GUIManager(this);
        
        // API 초기화
        TradeAPI. init(this);
        
        // 이벤트 리스너 등록
        registerListeners();
        
        // 명령어 등록
        registerCommands();
        
        // 태스크 시작
        startTasks();
        
        getLogger().info("TradeCore v" + getDescription().getVersion() + " 활성화됨!");
    }

    @Override
    public void onDisable() {
        // 태스크 중지
        stopTasks();
        
        // 진행 중인 모든 거래 취소
        if (tradeManager != null) {
            tradeManager.cancelAllTrades();
        }
        
        // 모든 데이터 저장
        saveAllData();
        
        getLogger().info("TradeCore 비활성화됨!");
        instance = null;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean checkDependencies() {
        String[] requiredPlugins = {"PlayerDataCore", "ItemCore", "EconomyCore"};
        for (String plugin : requiredPlugins) {
            if (getServer().getPluginManager().getPlugin(plugin) == null) {
                getLogger().severe("필수 플러그인 '" + plugin + "'이(가) 없습니다!");
                return false;
            }
        }
        return true;
    }

    private void createDataFolders() {
        String[] folders = {"shops", "auctions", "market", "mail", "players", "backups"};
        for (String folder :  folders) {
            File dir = new File(getDataFolder(), folder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    private void initDataManagers() {
        dataManager = new DataManager(this);
        shopDataManager = new ShopDataManager(this);
        auctionDataManager = new AuctionDataManager(this);
        marketDataManager = new MarketDataManager(this);
        mailDataManager = new MailDataManager(this);
        playerTradeDataManager = new PlayerTradeDataManager(this);
        
        // 데이터 로드
        shopDataManager.loadAll();
        auctionDataManager.loadAll();
        marketDataManager.loadAll();
        playerTradeDataManager. loadAll();
    }

    private void initManagers() {
        transactionFeeManager = new TransactionFeeManager(this);
        tradeSecurityManager = new TradeSecurityManager(this);
        priceTracker = new PriceTracker(this);
        tradeManager = new TradeManager(this);
        playerShopManager = new PlayerShopManager(this);
        auctionManager = new AuctionManager(this);
        marketManager = new MarketManager(this);
        mailManager = new MailManager(this);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new TradeListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
    }

    private void registerCommands() {
        getCommand("trade").setExecutor(new TradeCommand(this));
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("auction").setExecutor(new AuctionCommand(this));
        getCommand("market").setExecutor(new MarketCommand(this));
        getCommand("mail").setExecutor(new MailCommand(this));
    }

    private void startTasks() {
        // 자동 저장 태스크
        int autoSaveInterval = getConfig().getInt("data.auto-save-interval", 300) * 20;
        autoSaveTask = new AutoSaveTask(this);
        autoSaveTask.runTaskTimer(this, autoSaveInterval, autoSaveInterval);
        
        // 경매 체크 태스크 (1분마다)
        auctionCheckTask = new AuctionCheckTask(this);
        auctionCheckTask.runTaskTimer(this, 1200L, 1200L);
        
        // 거래소 매칭 태스크
        int matchInterval = getConfig().getInt("market.matching. match-interval", 60) * 20;
        marketMatchTask = new MarketMatchTask(this);
        marketMatchTask.runTaskTimer(this, matchInterval, matchInterval);
        
        // 우편 만료 체크 태스크 (1시간마다)
        mailExpiryTask = new MailExpiryTask(this);
        mailExpiryTask.runTaskTimer(this, 72000L, 72000L);
        
        // 가격 업데이트 태스크
        int priceInterval = getConfig().getInt("price-tracking.update-interval", 300) * 20;
        priceUpdateTask = new PriceUpdateTask(this);
        priceUpdateTask. runTaskTimer(this, priceInterval, priceInterval);
    }

    private void stopTasks() {
        if (autoSaveTask != null && ! autoSaveTask. isCancelled()) {
            autoSaveTask.cancel();
        }
        if (auctionCheckTask != null && ! auctionCheckTask.isCancelled()) {
            auctionCheckTask. cancel();
        }
        if (marketMatchTask != null && !marketMatchTask. isCancelled()) {
            marketMatchTask.cancel();
        }
        if (mailExpiryTask != null && ! mailExpiryTask.isCancelled()) {
            mailExpiryTask. cancel();
        }
        if (priceUpdateTask != null && !priceUpdateTask.isCancelled()) {
            priceUpdateTask. cancel();
        }
    }

    public void saveAllData() {
        try {
            if (shopDataManager != null) {
                shopDataManager.saveAll();
            }
            if (auctionDataManager != null) {
                auctionDataManager.saveAll();
            }
            if (marketDataManager != null) {
                marketDataManager.saveAll();
            }
            if (mailDataManager != null) {
                mailDataManager.saveAll();
            }
            if (playerTradeDataManager != null) {
                playerTradeDataManager.saveAll();
            }
            getLogger().info("모든 데이터가 저장되었습니다.");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "데이터 저장 중 오류 발생!", e);
        }
    }

    public void reload() {
        reloadConfig();
        MessageUtil.init(this);
        ConfigUtil.init(this);
        getLogger().info("설정이 다시 로드되었습니다.");
    }

    // Getters
    public static TradeCore getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }

    public PlayerShopManager getPlayerShopManager() {
        return playerShopManager;
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }

    public MarketManager getMarketManager() {
        return marketManager;
    }

    public MailManager getMailManager() {
        return mailManager;
    }

    public PriceTracker getPriceTracker() {
        return priceTracker;
    }

    public TransactionFeeManager getTransactionFeeManager() {
        return transactionFeeManager;
    }

    public TradeSecurityManager getTradeSecurityManager() {
        return tradeSecurityManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public ShopDataManager getShopDataManager() {
        return shopDataManager;
    }

    public AuctionDataManager getAuctionDataManager() {
        return auctionDataManager;
    }

    public MarketDataManager getMarketDataManager() {
        return marketDataManager;
    }

    public MailDataManager getMailDataManager() {
        return mailDataManager;
    }

    public PlayerTradeDataManager getPlayerTradeDataManager() {
        return playerTradeDataManager;
    }
}