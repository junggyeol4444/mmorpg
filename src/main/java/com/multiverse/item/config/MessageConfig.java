package com. multiverse.item. config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class MessageConfig {
    
    private FileConfiguration config;
    private File configFile;
    
    /**
     * 기본 생성자
     */
    public MessageConfig(File file) {
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
        
        // 강화 메시지
        config.set("enhance.success", "&a강화에 성공했습니다!  +{0}");
        config.set("enhance.fail", "&c강화에 실패했습니다.");
        config.set("enhance. downgrade", "&e아이템이 한 단계 하락했습니다.  +{0} → +{1}");
        config.set("enhance.max-level", "&c최대 강화 레벨입니다.");
        
        // 소켓 메시지
        config.set("socket.insert-success", "&a보석을 끼웠습니다!");
        config.set("socket.insert-fail", "&c보석을 끼울 수 없습니다.");
        config.set("socket.remove-success", "&a보석을 빼냈습니다!");
        config.set("socket.color-mismatch", "&e색상이 일치하지 않습니다.  효율이 감소합니다.");
        
        // 분해 메시지
        config.set("disassemble.success", "&a아이템을 분해했습니다!");
        config.set("disassemble.fail", "&c분해할 수 없습니다.");
        config.set("disassemble.reward", "&7보상: &a{0}");
        
        // 식별 메시지
        config.set("identify.success", "&a아이템을 식별했습니다!");
        config.set("identify.fail", "&c식별할 수 없습니다.");
        config.set("identify. already-identified", "&c이미 식별된 아이템입니다.");
        
        // 리롤 메시지
        config.set("reroll.success", "&a옵션이 변경되었습니다!");
        config.set("reroll. fail", "&c리롤할 수 없습니다.");
        
        // 거래 메시지
        config.set("trade.success", "&a거래가 완료되었습니다!");
        config.set("trade.fail", "&c거래할 수 없습니다.");
        config.set("trade.soulbound", "&c이 아이템은 거래할 수 없습니다.");
        config.set("trade.insufficient-gold", "&c골드가 부족합니다.");
        
        // 일반 메시지
        config.set("general.insufficient-inventory", "&c인벤토리가 가득 찼습니다.");
        config.set("general. insufficient-level", "&c필요 레벨: {0}");
        config. set("general.insufficient-gold", "&c골드가 부족합니다.");
        config. set("general.invalid-item", "&c유효하지 않은 아이템입니다.");
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
     * 강화 성공 메시지
     */
    public String getEnhanceSuccessMessage() {
        return config.getString("enhance.success", "&a강화에 성공했습니다!");
    }
    
    /**
     * 강화 실패 메시지
     */
    public String getEnhanceFailMessage() {
        return config.getString("enhance.fail", "&c강화에 실패했습니다.");
    }
    
    /**
     * 다운그레이드 메시지
     */
    public String getDowngradeMessage() {
        return config.getString("enhance.downgrade", "&e아이템이 한 단계 하락했습니다.");
    }
    
    /**
     * 최대 강화 레벨 메시지
     */
    public String getMaxEnhanceLevelMessage() {
        return config.getString("enhance. max-level", "&c최대 강화 레벨입니다.");
    }
    
    /**
     * 보석 끼우기 성공 메시지
     */
    public String getSocketInsertSuccessMessage() {
        return config.getString("socket.insert-success", "&a보석을 끼웠습니다!");
    }
    
    /**
     * 보석 끼우기 실패 메시지
     */
    public String getSocketInsertFailMessage() {
        return config.getString("socket.insert-fail", "&c보석을 끼울 수 없습니다.");
    }
    
    /**
     * 보석 빼기 성공 메시지
     */
    public String getSocketRemoveSuccessMessage() {
        return config.getString("socket.remove-success", "&a보석을 빼냈습니다!");
    }
    
    /**
     * 색상 불일치 메시지
     */
    public String getColorMismatchMessage() {
        return config.getString("socket.color-mismatch", "&e색상이 일치하지 않습니다.");
    }
    
    /**
     * 분해 성공 메시지
     */
    public String getDisassembleSuccessMessage() {
        return config.getString("disassemble.success", "&a아이템을 분해했습니다!");
    }
    
    /**
     * 분해 실패 메시지
     */
    public String getDisassembleFailMessage() {
        return config.getString("disassemble.fail", "&c분해할 수 없습니다.");
    }
    
    /**
     * 식별 성공 메시지
     */
    public String getIdentifySuccessMessage() {
        return config.getString("identify. success", "&a아이템을 식별했습니다!");
    }
    
    /**
     * 식별 실패 메시지
     */
    public String getIdentifyFailMessage() {
        return config. getString("identify.fail", "&c식별할 수 없습니다.");
    }
    
    /**
     * 리롤 성공 메시지
     */
    public String getRerollSuccessMessage() {
        return config.getString("reroll.success", "&a옵션이 변경되었습니다!");
    }
    
    /**
     * 거래 성공 메시지
     */
    public String getTradeSuccessMessage() {
        return config.getString("trade. success", "&a거래가 완료되었습니다!");
    }
    
    /**
     * 거래 실패 메시지
     */
    public String getTradeFailMessage() {
        return config. getString("trade.fail", "&c거래할 수 없습니다.");
    }
    
    /**
     * 소울바운드 메시지
     */
    public String getSoulboundMessage() {
        return config. getString("trade.soulbound", "&c이 아이템은 거래할 수 없습니다.");
    }
    
    /**
     * 인벤토리 가득 메시지
     */
    public String getInsufficientInventoryMessage() {
        return config.getString("general.insufficient-inventory", "&c인벤토리가 가득 찼습니다.");
    }
    
    /**
     * 레벨 부족 메시지
     */
    public String getInsufficientLevelMessage() {
        return config.getString("general.insufficient-level", "&c필요 레벨: {0}");
    }
    
    /**
     * 골드 부족 메시지
     */
    public String getInsufficientGoldMessage() {
        return config.getString("general.insufficient-gold", "&c골드가 부족합니다.");
    }
    
    /**
     * 유효하지 않은 아이템 메시지
     */
    public String getInvalidItemMessage() {
        return config.getString("general.invalid-item", "&c유효하지 않은 아이템입니다.");
    }
    
    /**
     * 메시지 포맷팅
     */
    public String formatMessage(String message, String... replacements) {
        String result = message;
        for (int i = 0; i < replacements.length; i++) {
            result = result.replace("{" + i + "}", replacements[i]);
        }
        return result;
    }
    
    /**
     * 설정 다시 로드
     */
    public void reload() {
        loadConfig();
    }
}