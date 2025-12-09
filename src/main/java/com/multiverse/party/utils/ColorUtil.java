package com.multiverse.party.utils;

import org.bukkit.ChatColor;

/**
 * 색상 코드 유틸리티
 */
public class ColorUtil {

    /** 문자열 내 &색상코드 → Bukkit 색상 적용 */
    public static String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}