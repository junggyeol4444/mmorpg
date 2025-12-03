package com.multiverse.death.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * 메시지 출력 유틸리티
 */
public class MessageUtil {

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GREEN + message);
    }

    public static void sendError(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

    public static void broadcast(String message) {
        org.bukkit.Bukkit.broadcastMessage(color(message));
    }
}