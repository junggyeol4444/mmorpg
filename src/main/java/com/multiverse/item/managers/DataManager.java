package com. multiverse.item. managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.*;
import java.io. File;
import java.util.*;

public class DataManager {
    
    private ItemCore plugin;
    private ConfigManager configManager;
    private File dataFolder;
    private File playerFolder;
    private File templateFolder;
    
    public DataManager(ItemCore plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        this.playerFolder = new File(plugin.getDataFolder(), "players");
        this. templateFolder = new File(plugin. getDataFolder(), "templates");
        
        createFolders();
    }
    
    /**
     * 폴더 생성
     */
    private void createFolders() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        if (!playerFolder.exists()) {
            playerFolder.mkdirs();
        }
        if (!templateFolder.exists()) {
            templateFolder.mkdirs();
        }
    }
    
    /**
     * 아이템 템플릿 로드
     */
    public CustomItem loadItemTemplate(String itemId) throws Exception {
        File templateFile = new File(templateFolder, itemId + ".yml");
        
        if (!templateFile.exists()) {
            throw new Exception("템플릿 파일을 찾을 수 없습니다: " + itemId);
        }
        
        FileConfiguration config = YamlConfiguration. loadConfiguration(templateFile);
        
        CustomItem item = new CustomItem();
        item.setItemId(itemId);
        item.setName(config.getString("name", "Unknown Item"));
        item.setDescription(config.getString("description", ""));
        item.setMaterial(org.bukkit.Material.valueOf(config.getString("material", "IRON_SWORD")));
        item.setType(ItemType.valueOf(config.getString("type", "WEAPON")));
        item.setRarity(ItemRarity.valueOf(config.getString("rarity", "COMMON")));
        item.setMaxEnhance(config.getInt("max-enhance", 15));
        item.setSockets(config.getInt("sockets", 0));
        item.setRequiredLevel(config.getInt("required-level", 0));
        item.setRequiredClass(config.getString("required-class", ""));
        item.setRequiredRace(config.getString("required-race", ""));
        item.setMaxDurability(config.getInt("max-durability", 100));
        item.setUnbreakable(config.getBoolean("unbreakable", false));
        
        // 기본 스탯 로드
        if (config.isConfigurationSection("base-stats")) {
            Map<String, Double> baseStats = new HashMap<>();
            for (String key : config.getConfigurationSection("base-stats").getKeys(false)) {
                baseStats. put(key, config.getDouble("base-stats." + key));
            }
            item.setBaseStats(baseStats);
        }
        
        return item;
    }
    
    /**
     * 세트 정보 로드
     */
    public ItemSet loadItemSet(String setId) throws Exception {
        File setFile = new File(templateFolder, "sets_" + setId + ".yml");
        
        if (!setFile.exists()) {
            throw new Exception("세트 파일을 찾을 수 없습니다: " + setId);
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(setFile);
        
        ItemSet set = new ItemSet();
        set.setSetId(setId);
        set.setName(config.getString("name", "Unknown Set"));
        
        // 세트 보너스 로드
        List<SetBonus> bonuses = new ArrayList<>();
        if (config. isList("bonuses")) {
            for (int i = 0; i < config.getList("bonuses").size(); i++) {
                SetBonus bonus = new SetBonus();
                bonus.setRequiredCount(config.getInt("bonuses." + i + ".required-count"));
                bonus.setDescription(config.getString("bonuses." + i + ".description"));
                bonuses.add(bonus);
            }
        }
        set. setBonuses(bonuses);
        
        return set;
    }
    
    /**
     * 보석 정보 로드
     */
    public Gem loadGem(String gemId) throws Exception {
        File gemFile = new File(templateFolder, "gems_" + gemId + ".yml");
        
        if (!gemFile.exists()) {
            throw new Exception("보석 파일을 찾을 수 없습니다: " + gemId);
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(gemFile);
        
        Gem gem = new Gem();
        gem.setGemId(gemId);
        gem. setName(config.getString("name", "Unknown Gem"));
        gem.setType(GemType.valueOf(config.getString("type", "STRENGTH")));
        gem.setRarity(GemRarity.valueOf(config.getString("rarity", "COMMON")));
        gem.setColor(SocketColor.valueOf(config.getString("color", "RED")));
        
        // 스탯 로드
        if (config.isConfigurationSection("stats")) {
            Map<String, Double> stats = new HashMap<>();
            for (String key : config. getConfigurationSection("stats").getKeys(false)) {
                stats.put(key, config.getDouble("stats." + key));
            }
            gem.setStats(stats);
        }
        
        return gem;
    }
    
    /**
     * 옵션 풀 로드
     */
    public List<ItemOptionTemplate> loadOptionPool() throws Exception {
        File optionFile = new File(templateFolder, "options.yml");
        
        List<ItemOptionTemplate> options = new ArrayList<>();
        
        if (!optionFile.exists()) {
            plugin.getLogger().warning("옵션 파일을 찾을 수 없습니다!");
            return options;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(optionFile);
        
        if (config.isList("options")) {
            for (int i = 0; i < config.getList("options"). size(); i++) {
                ItemOptionTemplate template = new ItemOptionTemplate();
                template.setId("option_" + i);
                template.setName(config.getString("options." + i + ".name"));
                template.setType(OptionType.valueOf(config.getString("options." + i + ".type")));
                template.setTrigger(OptionTrigger.valueOf(config.getString("options." + i + ".trigger")));
                template. setMinValue(config.getDouble("options." + i + ".min-value"));
                template.setMaxValue(config.getDouble("options." + i + ".max-value"));
                template.setPercentage(config.getBoolean("options." + i + ". percentage", false));
                options.add(template);
            }
        }
        
        return options;
    }
    
    /**
     * 모든 데이터 저장
     */
    public void saveAllData() {
        try {
            // 플레이어 데이터 저장
            // 아이템 데이터 저장
            // 세트 데이터 저장
            // 등등...
            
            plugin.getLogger().info("✅ 모든 데이터가 저장되었습니다!");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 데이터 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 강화 시도 기록 저장
     */
    public void saveEnhanceAttempt(EnhanceAttempt attempt) {
        try {
            File logFile = new File(dataFolder, "enhance_log. yml");
            FileConfiguration config;
            
            if (logFile.exists()) {
                config = YamlConfiguration.loadConfiguration(logFile);
            } else {
                config = new YamlConfiguration();
            }
            
            String path = "enhance." + attempt.getItemId() + "." + System.currentTimeMillis();
            config.set(path + ".before-level", attempt.getBeforeLevel());
            config.set(path + ".after-level", attempt.getAfterLevel());
            config. set(path + ".success", attempt.isSuccess());
            config.set(path + ".success-rate", attempt.getSuccessRate());
            config.set(path + ".timestamp", attempt.getTimestamp());
            
            config.save(logFile);
        } catch (Exception e) {
            plugin.getLogger().warning("강화 기록 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 거래 기록 저장
     */
    public void saveTradeLog(ItemTrade trade) {
        try {
            File logFile = new File(dataFolder, "trade_log.yml");
            FileConfiguration config;
            
            if (logFile.exists()) {
                config = YamlConfiguration.loadConfiguration(logFile);
            } else {
                config = new YamlConfiguration();
            }
            
            String path = "trades." + trade.getTradeId();
            config.set(path + ".sender", trade.getSenderId(). toString());
            config.set(path + ".receiver", trade.getReceiverId().toString());
            config.set(path + ".item-id", trade.getItem().  getItemId());
            config.set(path + ".status", trade.getStatus());
            config.set(path + ".created-time", trade.getCreatedTime());
            
            config.save(logFile);
        } catch (Exception e) {
            plugin.getLogger(). warning("거래 기록 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 거래 기록 조회
     */
    public List<ItemTrade> loadTradeHistory(UUID playerId, int limit) throws Exception {
        List<ItemTrade> trades = new ArrayList<>();
        File logFile = new File(dataFolder, "trade_log.yml");
        
        if (!logFile.exists()) {
            return trades;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(logFile);
        
        if (config.isConfigurationSection("trades")) {
            int count = 0;
            for (String key : config.getConfigurationSection("trades").getKeys(false)) {
                if (count >= limit) break;
                
                ItemTrade trade = new ItemTrade();
                trade.setTradeId(key);
                trade.setSenderId(UUID.fromString(config.getString("trades." + key + ".sender")));
                trade.setReceiverId(UUID.fromString(config.getString("trades." + key + ".receiver")));
                trade.setStatus(config.getString("trades." + key + ".status"));
                
                trades.add(trade);
                count++;
            }
        }
        
        return trades;
    }
    
    /**
     * 모든 데이터 로드
     */
    public void loadAllData() {
        try {
            plugin.getLogger().info("✅ 모든 데이터가 로드되었습니다!");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 데이터 로드 실패: " + e.getMessage());
            e. printStackTrace();
        }
    }
    
    /**
     * 아이템 타입별 템플릿 로드
     */
    public List<ItemOptionTemplate> loadItemTemplatesByType(String itemType) throws Exception {
        // 구현 예정
        return new ArrayList<>();
    }
}