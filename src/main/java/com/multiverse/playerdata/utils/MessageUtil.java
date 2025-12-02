package com.multiverse.playerdata.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtil {

    /**
     * 플레이어에게 칼라 메세지 전송
     */
    public static void send(Player player, String message) {
        if (player != null && message != null && !message.isEmpty()) {
            player.sendMessage(color(message));
        }
    }

    /**
     * 칼라 코드 적용
     */
    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    /**
     * 서버 콘솔/전체 플레이어 브로드캐스트
     */
    public static void broadcast(String message) {
        org.bukkit.Bukkit.broadcastMessage(color(message));
    }
}