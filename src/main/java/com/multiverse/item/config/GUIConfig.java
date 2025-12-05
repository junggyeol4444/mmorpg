package com.multiverse.item.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io. File;

public class GUIConfig {
    
    private FileConfiguration config;
    private File configFile;
    
    /**
     * 기본 생성자
     */
    public GUIConfig(File file) {
        this.configFile = file;
        loadConfig();
    }
    
    /**
     * 설정 파일 로드
     */
    private void loadConfig() {
        if (!configFile.exists()) {
            setDefaults();
            saveConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    /**
     * 기본 설정 설정
     */
    private void setDefaults() {
        config = new YamlConfiguration();
        
        // GUI 기본 설정
        config. set("gui.enable", true);
        config.set("gui.open-animation-enabled", true);
        config.set("gui.close-animation-enabled", true);
        
        // 강화 GUI
        config.set("gui.enhance. enable", true);
        config. set("gui.enhance.show-success-rate", true);
        config.set("gui.enhance.show-cost", true);
        
        // 소켓 GUI
        config.set("gui. socket.enable", true);
        config.set("gui.socket. max-gems-display", 10);
        
        // 분해 GUI
        config.set("gui.disassemble.enable", true);
        config.set("gui.disassemble.show-rewards", true);
        
        // 식별 GUI
        config.set("gui.identify.enable", true);
        config.set("gui.identify.show-hidden-options", true);
        
        // 리롤 GUI
        config.set("gui.reroll.enable", true);
        config.set("gui.reroll.show-old-options", true);
        
        // 아이템 정보 GUI
        config.set("gui.item-info.enable", true);
        config.set("gui.item-info.show-all-stats", true);
        
        // GUI 색상
        config.set("color.title", "&6");
        config.set("color. success", "&a");
        config.set("color.error", "&c");
        config.set("color.info", "&b");
        config. set("color.warning", "&e");
    }
    
    /**
     * 설정 저장
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * GUI 활성화
     */
    public boolean isGUIEnabled() {
        return config.getBoolean("gui.enable", true);
    }
    
    /**
     * 열기 애니메이션 활성화
     */
    public boolean isOpenAnimationEnabled() {
        return config.getBoolean("gui.open-animation-enabled", true);
    }
    
    /**
     * 닫기 애니메이션 활성화
     */
    public boolean isCloseAnimationEnabled() {
        return config. getBoolean("gui.close-animation-enabled", true);
    }
    
    /**
     * 강화 GUI 활성화
     */
    public boolean isEnhanceGUIEnabled() {
        return config.getBoolean("gui. enhance.enable", true);
    }
    
    /**
     * 강화 GUI에서 성공률 표시
     */
    public boolean showEnhanceSuccessRate() {
        return config.getBoolean("gui. enhance.show-success-rate", true);
    }
    
    /**
     * 강화 GUI에서 비용 표시
     */
    public boolean showEnhanceCost() {
        return config. getBoolean("gui.enhance. show-cost", true);
    }
    
    /**
     * 소켓 GUI 활성화
     */
    public boolean isSocketGUIEnabled() {
        return config.getBoolean("gui.socket.enable", true);
    }
    
    /**
     * 소켓 GUI에서 보석 최대 표시 개수
     */
    public int getSocketGUIMaxGemsDisplay() {
        return config.getInt("gui.socket. max-gems-display", 10);
    }
    
    /**
     * 분해 GUI 활성화
     */
    public boolean isDisassembleGUIEnabled() {
        return config.getBoolean("gui.disassemble.enable", true);
    }
    
    /**
     * 분해 GUI에서 보상 표시
     */
    public boolean showDisassembleRewards() {
        return config. getBoolean("gui.disassemble.show-rewards", true);
    }
    
    /**
     * 식별 GUI 활성화
     */
    public boolean isIdentifyGUIEnabled() {
        return config.getBoolean("gui.identify.enable", true);
    }
    
    /**
     * 식별 GUI에서 숨겨진 옵션 표시
     */
    public boolean showIdentifyHiddenOptions() {
        return config.getBoolean("gui.identify.show-hidden-options", true);
    }
    
    /**
     * 리롤 GUI 활성화
     */
    public boolean isRerollGUIEnabled() {
        return config.getBoolean("gui.reroll.enable", true);
    }
    
    /**
     * 리롤 GUI에서 이전 옵션 표시
     */
    public boolean showRerollOldOptions() {
        return config.getBoolean("gui.reroll.show-old-options", true);
    }
    
    /**
     * 아이템 정보 GUI 활성화
     */
    public boolean isItemInfoGUIEnabled() {
        return config.getBoolean("gui.item-info. enable", true);
    }
    
    /**
     * 아이템 정보 GUI에서 모든 스탯 표시
     */
    public boolean showItemInfoAllStats() {
        return config.getBoolean("gui.item-info.show-all-stats", true);
    }
    
    /**
     * 타이틀 색상
     */
    public String getTitleColor() {
        return config.getString("color.title", "&6");
    }
    
    /**
     * 성공 색상
     */
    public String getSuccessColor() {
        return config.getString("color. success", "&a");
    }
    
    /**
     * 오류 색상
     */
    public String getErrorColor() {
        return config.getString("color.error", "&c");
    }
    
    /**
     * 정보 색상
     */
    public String getInfoColor() {
        return config.getString("color.info", "&b");
    }
    
    /**
     * 경고 색상
     */
    public String getWarningColor() {
        return config.getString("color.warning", "&e");
    }
    
    /**
     * 설정 다시 로드
     */
    public void reload() {
        loadConfig();
    }
}