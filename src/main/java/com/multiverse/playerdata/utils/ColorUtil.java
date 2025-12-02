package com.multiverse.playerdata.utils;

import org.bukkit.ChatColor;

/**
 * 칼라 코드 관련 유틸리티
 */
public class ColorUtil {

    /**
     * & 색상 코드 → Bukkit 색상 변환
     */
    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * 기본 색상 적용
     */
    public static String defaultColor(String text) {
        return "§f" + colorize(text);
    }

    /**
     * 경고 메시지 색상
     */
    public static String warning(String text) {
        return "§c" + colorize(text);
    }

    /**
     * 성공 메시지 색상
     */
    public static String success(String text) {
        return "§a" + colorize(text);
    }
}