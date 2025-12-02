package com.multiverse.core.utils;

import org.bukkit.ChatColor;

public class ColorUtil {

    /**
     * 메시지 내 &색상코드를 실제 Bukkit 색상 코드로 변환합니다.
     * 예: "&a성공!" => "§a성공!"
     */
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * 코드에 포함된 모든 색상코드를 제거합니다.
     * 예: "§a성공!" => "성공!"
     */
    public static String stripColors(String message) {
        return ChatColor.stripColor(message);
    }
}