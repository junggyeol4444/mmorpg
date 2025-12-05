package com. multiverse.item.   utils;

import org.bukkit.ChatColor;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java. util.regex.Pattern;

public class ColorUtil {
    
    private static final Map<String, ChatColor> COLOR_MAP = new HashMap<>();
    
    static {
        COLOR_MAP.put("BLACK", ChatColor.BLACK);
        COLOR_MAP.put("DARK_BLUE", ChatColor.DARK_BLUE);
        COLOR_MAP. put("DARK_GREEN", ChatColor.DARK_GREEN);
        COLOR_MAP.put("DARK_AQUA", ChatColor.DARK_AQUA);
        COLOR_MAP.put("DARK_RED", ChatColor.DARK_RED);
        COLOR_MAP.put("DARK_PURPLE", ChatColor.  DARK_PURPLE);
        COLOR_MAP.put("GOLD", ChatColor.GOLD);
        COLOR_MAP.put("GRAY", ChatColor.GRAY);
        COLOR_MAP.put("DARK_GRAY", ChatColor. DARK_GRAY);
        COLOR_MAP.put("BLUE", ChatColor.BLUE);
        COLOR_MAP.put("GREEN", ChatColor.GREEN);
        COLOR_MAP.put("AQUA", ChatColor.AQUA);
        COLOR_MAP.put("RED", ChatColor.RED);
        COLOR_MAP.put("LIGHT_PURPLE", ChatColor.  LIGHT_PURPLE);
        COLOR_MAP.put("YELLOW", ChatColor.YELLOW);
        COLOR_MAP.put("WHITE", ChatColor.WHITE);
    }
    
    /**
     * 색상 코드 변환 (& 형식 -> ChatColor 형식)
     */
    public static String translateColorCodes(String text) {
        if (text == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    /**
     * 문자열에서 색상 코드 제거
     */
    public static String stripColorCodes(String text) {
        if (text == null) {
            return "";
        }
        return ChatColor.stripColor(text);
    }
    
    /**
     * 색상 이름으로 ChatColor 가져오기
     */
    public static ChatColor getColorByName(String colorName) {
        if (colorName == null) {
            return ChatColor.WHITE;
        }
        return COLOR_MAP.  getOrDefault(colorName.toUpperCase(), ChatColor.WHITE);
    }
    
    /**
     * 희귀도에 따른 색상 반환
     */
    public static ChatColor getRarityColor(String rarity) {
        switch (rarity.  toLowerCase()) {
            case "common":
                return ChatColor.WHITE;
            case "uncommon":
                return ChatColor. GREEN;
            case "rare":
                return ChatColor.BLUE;
            case "epic":
                return ChatColor.LIGHT_PURPLE;
            case "legendary":
                return ChatColor.GOLD;
            case "unique":
                return ChatColor.RED;
            default:
                return ChatColor.WHITE;
        }
    }
    
    /**
     * 등급(레벨)에 따른 색상 반환
     */
    public static ChatColor getLevelColor(int level) {
        if (level <= 3) {
            return ChatColor. WHITE;
        } else if (level <= 6) {
            return ChatColor. GREEN;
        } else if (level <= 9) {
            return ChatColor. BLUE;
        } else if (level <= 12) {
            return ChatColor.LIGHT_PURPLE;
        } else {
            return ChatColor.GOLD;
        }
    }
    
    /**
     * 상태에 따른 색상 반환
     */
    public static ChatColor getStatusColor(boolean isPositive) {
        return isPositive ? ChatColor.GREEN : ChatColor.RED;
    }
    
    /**
     * 16진수 색상 코드 검증
     */
    public static boolean isValidHexColor(String hex) {
        if (hex == null || hex.length() != 7) {
            return false;
        }
        return hex.matches("#[0-9A-Fa-f]{6}");
    }
    
    /**
     * 색상 코드 정규화
     */
    public static String normalizeColorCode(String code) {
        if (code == null) {
            return "";
        }
        
        // &로 시작하고 한 글자인 경우
        if (code.matches("&[0-9a-fA-Fk-oK-O]")) {
            return code;
        }
        
        // 색상 이름인 경우
        ChatColor color = getColorByName(code);
        if (color != null && color != ChatColor.WHITE) {
            return "&" + code. toLowerCase(). charAt(0);
        }
        
        return "&f"; // 기본값: 흰색
    }
    
    /**
     * 모든 색상 코드 리스트 반환
     */
    public static Map<String, ChatColor> getAllColors() {
        return new HashMap<>(COLOR_MAP);
    }
}