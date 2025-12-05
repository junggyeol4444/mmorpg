package com. multiverse.skill.utils;

import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * 메시지 유틸리티
 */
public class MessageUtils {

    /**
     * 플레이어에게 메시지 전송
     */
    public static void sendMessage(Player player, String message) {
        if (player == null || message == null) {
            return;
        }

        player.sendMessage(message);
    }

    /**
     * 플레이어에게 액션바 메시지 전송
     */
    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) {
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    /**
     * 플레이어에게 제목 메시지 전송
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) {
            return;
        }

        player.sendTitle(title != null ? title : "", subtitle != null ? subtitle : "", fadeIn, stay, fadeOut);
    }

    /**
     * 플레이어에게 사운드 메시지 전송
     */
    public static void sendSound(Player player, String soundName) {
        if (player == null || soundName == null) {
            return;
        }

        try {
            player.playSound(player.getLocation(), soundName, 1.0f, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 브로드캐스트 메시지
     */
    public static void broadcastMessage(String message) {
        if (message == null) {
            return;
        }

        org.bukkit. Bukkit.broadcastMessage(message);
    }

    /**
     * 색상 코드 적용
     */
    public static String colorize(String message) {
        if (message == null) {
            return "";
        }

        return message. replace("&", "§");
    }

    /**
     * 색상 코드 제거
     */
    public static String stripColor(String message) {
        if (message == null) {
            return "";
        }

        return message. replaceAll("§[0-9a-fk-or]", "");
    }

    /**
     * 진행바 생성
     */
    public static String createProgressBar(double progress, int length) {
        if (progress < 0) progress = 0;
        if (progress > 1) progress = 1;

        int filledLength = (int) (length * progress);
        StringBuilder bar = new StringBuilder("§a");

        for (int i = 0; i < filledLength; i++) {
            bar.append("█");
        }

        bar. append("§7");
        for (int i = filledLength; i < length; i++) {
            bar.append("█");
        }

        return bar.toString();
    }
}