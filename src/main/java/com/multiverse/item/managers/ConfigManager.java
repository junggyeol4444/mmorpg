package com.multiverse.item. managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.multiverse.item.ItemCore;
import java.io.File;
import java.util.*;

public class ConfigManager {
    
    private ItemCore plugin;
    private FileConfiguration config;
    private File configFile;
    
    public ConfigManager(ItemCore plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        loadConfig();
    }
    
    /**
     * 설정 파일 로드
     */
    public void loadConfig() {
        if (!configFile.exists()) {
            createDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    /**
     * 기본 설정 파일 생성
     */
    private void createDefaultConfig() {
        try {
            configFile.createNewFile();
            
            FileConfiguration defaultConfig = new YamlConfiguration();
            
            // 데이터 설정
            defaultConfig.set("data.auto-save-interval", 300); // 5분
            defaultConfig.set("data.backup-enabled", true);
            defaultConfig.set("data.backup-interval", 3600); // 1시간
            
            // 아이템 설정
            defaultConfig.set("item.max-enhance-level", 15);
            defaultConfig.set("item.max-trades", 3);
            defaultConfig.set("item.default-durability", 100);
            
            // 강화 설정
            defaultConfig.set("enhance.base-cost", 1000);
            defaultConfig.set("enhance.success-decrease", 5); // 실패 시 강화 감소 확률 5%
            defaultConfig.set("enhance.fail-penalty", 0.1); // 실패 시 스탯 페널티 10%
            
            // 식별 설정
            defaultConfig.set("identify.cost", 500);
            defaultConfig.set("identify.success-rate", 100. 0);
            
            // 리롤 설정
            defaultConfig.set("reroll.cost", 2000);
            defaultConfig.set("reroll.max-per-day", 5);
            
            // 분해 설정
            defaultConfig. set("disassemble.enabled", true);
            defaultConfig. set("disassemble.cost", 500);
            
            // 소켓 설정
            defaultConfig.set("socket.gem-cost", 1500);
            defaultConfig.set("socket.removal-cost", 500);
            
            // 거래 설정
            defaultConfig.set("trade.enabled", true);
            defaultConfig. set("trade.tax-rate", 0.05); // 5% 거래세
            defaultConfig.set("trade. timeout", 300000); // 5분
            
            // 경험치 설정
            defaultConfig.set("exp.enhance-success", 100);
            defaultConfig.set("exp.disassemble", 50);
            defaultConfig.set("exp.identify", 75);
            
            // 메시지 설정
            defaultConfig.set("messages.prefix", "&8[&bItemCore&8]");
            defaultConfig.set("messages.no-permission", "&c권한이 없습니다!");
            defaultConfig.set("messages.player-only", "&c플레이어만 사용 가능합니다!");
            
            defaultConfig.save(configFile);
            config = defaultConfig;
            
            plugin.getLogger().info("✅ 기본 설정 파일이 생성되었습니다!");
        } catch (Exception e) {
            plugin.getLogger(). severe("❌ 설정 파일 생성 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 설정 다시 로드
     */
    public void reload() {
        loadConfig();
        plugin.getLogger().info("✅ 설정이 다시 로드되었습니다!");
    }
    
    /**
     * 설정 저장
     */
    public void save() {
        try {
            config.save(configFile);
            plugin. getLogger().info("✅ 설정이 저장되었습니다!");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 설정 저장 실패: " + e. getMessage());
        }
    }
    
    // 데이터 설정
    public int getAutoSaveInterval() {
        return config.getInt("data.auto-save-interval", 300);
    }
    
    public boolean isBackupEnabled() {
        return config.getBoolean("data.backup-enabled", true);
    }
    
    public int getBackupInterval() {
        return config.getInt("data.backup-interval", 3600);
    }
    
    // 아이템 설정
    public int getMaxEnhanceLevel() {
        return config. getInt("item.max-enhance-level", 15);
    }
    
    public int getMaxTrades() {
        return config.getInt("item.max-trades", 3);
    }
    
    public int getDefaultDurability() {
        return config.getInt("item. default-durability", 100);
    }
    
    // 강화 설정
    public int getEnhanceCost() {
        return config.getInt("enhance.base-cost", 1000);
    }
    
    public int getEnhanceSuccessDecrease() {
        return config.getInt("enhance.success-decrease", 5);
    }
    
    public double getEnhanceFailPenalty() {
        return config.getDouble("enhance. fail-penalty", 0.1);
    }
    
    // 식별 설정
    public int getIdentifyCost() {
        return config.getInt("identify.cost", 500);
    }
    
    public double getIdentifySuccessRate() {
        return config.getDouble("identify.success-rate", 100.0);
    }
    
    // 리롤 설정
    public int getRerollCost() {
        return config.getInt("reroll.cost", 2000);
    }
    
    public int getRerollMaxPerDay() {
        return config.getInt("reroll.max-per-day", 5);
    }
    
    // 분해 설정
    public boolean isDisassembleEnabled() {
        return config.getBoolean("disassemble.enabled", true);
    }
    
    public int getDisassembleCost() {
        return config.getInt("disassemble.cost", 500);
    }
    
    // 소켓 설정
    public int getGemCost() {
        return config.getInt("socket.gem-cost", 1500);
    }
    
    public int getGemRemovalCost() {
        return config.getInt("socket. removal-cost", 500);
    }
    
    // 거래 설정
    public boolean isTradeEnabled() {
        return config.getBoolean("trade.enabled", true);
    }
    
    public double getTradeTaxRate() {
        return config.getDouble("trade.tax-rate", 0.05);
    }
    
    public long getTradeTimeout() {
        return config.getLong("trade.timeout", 300000);
    }
    
    // 경험치 설정
    public int getEnhanceSuccessExp() {
        return config.getInt("exp.enhance-success", 100);
    }
    
    public int getDisassembleExp() {
        return config.getInt("exp.disassemble", 50);
    }
    
    public int getIdentifyExp() {
        return config.getInt("exp.identify", 75);
    }
    
    // 메시지 설정
    public String getPrefix() {
        return config.getString("messages.prefix", "&8[&bItemCore&8]");
    }
    
    public String getNoPermissionMessage() {
        return config. getString("messages.no-permission", "&c권한이 없습니다!");
    }
    
    public String getPlayerOnlyMessage() {
        return config. getString("messages.player-only", "&c플레이어만 사용 가능합니다!");
    }
    
    // FileConfiguration 반환
    public FileConfiguration getConfig() {
        return config;
    }
}