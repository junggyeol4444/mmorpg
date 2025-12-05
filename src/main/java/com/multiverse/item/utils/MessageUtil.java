package com.multiverse.item. utils;

import org.bukkit. Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.Collection;

public class MessageUtil {
    
    private static final String PREFIX = "§6[아이템 시스템]§f";
    
    /**
     * 플레이어에게 메시지 전송
     */
    public static void sendMessage(Player player, String message) {
        if (player == null) {
            return;
        }
        
        String formattedMessage = ColorUtil.translateColorCodes(message);
        player.sendMessage(formattedMessage);
    }
    
    /**
     * 플레이어에게 접두어가 포함된 메시지 전송
     */
    public static void sendPrefixMessage(Player player, String message) {
        if (player == null) {
            return;
        }
        
        String formattedMessage = PREFIX + " " + ColorUtil.translateColorCodes(message);
        player.sendMessage(formattedMessage);
    }
    
    /**
     * 모든 플레이어에게 메시지 브로드캐스트
     */
    public static void broadcast(String message) {
        String formattedMessage = ColorUtil. translateColorCodes(message);
        Bukkit.broadcastMessage(formattedMessage);
    }
    
    /**
     * 모든 플레이어에게 접두어가 포함된 메시지 브로드캐스트
     */
    public static void broadcastPrefix(String message) {
        String formattedMessage = PREFIX + " " + ColorUtil. translateColorCodes(message);
        Bukkit.broadcastMessage(formattedMessage);
    }
    
    /**
     * 특정 권한이 있는 플레이어들에게 메시지 전송
     */
    public static void sendToPermission(String permission, String message) {
        String formattedMessage = ColorUtil. translateColorCodes(message);
        for (Player player : Bukkit. getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                player. sendMessage(formattedMessage);
            }
        }
    }
    
    /**
     * 액션바 메시지 전송
     */
    public static void sendActionBar(Player player, String message) {
        if (player == null) {
            return;
        }
        
        String formattedMessage = ColorUtil.translateColorCodes(message);
        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                                    net.md_5.bungee.chat.ComponentBuilder(formattedMessage).create());
    }
    
    /**
     * 타이틀 메시지 전송
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) {
            return;
        }
        
        String formattedTitle = ColorUtil.translateColorCodes(title);
        String formattedSubtitle = ColorUtil.translateColorCodes(subtitle);
        player.sendTitle(formattedTitle, formattedSubtitle, fadeIn, stay, fadeOut);
    }
    
    /**
     * 성공 메시지 전송
     */
    public static void sendSuccess(Player player, String message) {
        if (player == null) {
            return;
        }
        
        sendPrefixMessage(player, "&a✓ " + message);
    }
    
    /**
     * 오류 메시지 전송
     */
    public static void sendError(Player player, String message) {
        if (player == null) {
            return;
        }
        
        sendPrefixMessage(player, "&c✗ " + message);
    }
    
    /**
     * 경고 메시지 전송
     */
    public static void sendWarning(Player player, String message) {
        if (player == null) {
            return;
        }
        
        sendPrefixMessage(player, "&e⚠ " + message);
    }
    
    /**
     * 정보 메시지 전송
     */
    public static void sendInfo(Player player, String message) {
        if (player == null) {
            return;
        }
        
        sendPrefixMessage(player, "&bℹ " + message);
    }
    
    /**
     * 플레이어 그룹에게 메시지 전송
     */
    public static void sendToGroup(Collection<Player> players, String message) {
        String formattedMessage = ColorUtil. translateColorCodes(message);
        for (Player player : players) {
            player.sendMessage(formattedMessage);
        }
    }
    
    /**
     * 색상 코드 제거하고 메시지 전송
     */
    public static void sendPlainMessage(Player player, String message) {
        if (player == null) {
            return;
        }
        
        String plainMessage = ColorUtil.stripColorCodes(message);
        player.sendMessage(plainMessage);
    }
    
    /**
     * 메시지 포맷팅
     */
    public static String formatMessage(String template, String... replacements) {
        String result = template;
        for (int i = 0; i < replacements.length; i++) {
            result = result.replace("{" + i + "}", replacements[i]);
        }
        return ColorUtil.translateColorCodes(result);
    }
}