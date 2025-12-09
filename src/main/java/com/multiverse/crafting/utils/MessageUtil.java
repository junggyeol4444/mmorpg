package com.multiverse.crafting.utils;

import com.multiverse.crafting.CraftingCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {

    private static CraftingCore plugin;

    private MessageUtil() {}

    public static void init(CraftingCore instance) {
        plugin = instance;
    }

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void send(CommandSender sender, String msg) {
        sender.sendMessage(color(msg));
    }

    public static void broadcast(String msg) {
        if (plugin != null) {
            plugin.getServer().broadcastMessage(color(msg));
        }
    }
}