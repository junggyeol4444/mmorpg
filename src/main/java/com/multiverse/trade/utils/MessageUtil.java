package com.multiverse.trade.utils;

import com. multiverse.trade. TradeCore;
import org.bukkit.  ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit. configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit. entity.Player;

import java.io. File;
import java. io. InputStream;
import java.io.  InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.  util.Map;

public class MessageUtil {

    private static TradeCore plugin;
    private static FileConfiguration messages;
    private static final Map<String, String> messageCache = new HashMap<>();

    public static void init(TradeCore tradePlugin) {
        plugin = tradePlugin;
        loadMessages();
    }

    private static void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        
        messages = YamlConfiguration.  loadConfiguration(messagesFile);
        
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultMessages = YamlConfiguration. loadConfiguration(
                new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            messages.setDefaults(defaultMessages);
        }
        
        messageCache.clear();
    }

    public static String color(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getMessage(String key) {
        if (messageCache.containsKey(key)) {
            return messageCache. get(key);
        }
        
        String message = messages.getString(key);
        if (message == null) {
            message = "&cMissing message: " + key;
        }
        
        message = color(message);
        messageCache. put(key, message);
        
        return message;
    }

    public static String getMessage(String key, String...  replacements) {
        String message = getMessage(key);
        
        if (replacements. length % 2 != 0) {
            return message;
        }
        
        for (int i = 0; i < replacements.length; i += 2) {
            String placeholder = "%" + replacements[i] + "%";
            String value = replacements[i + 1];
            message = message.replace(placeholder, value != null ? value :   "");
        }
        
        return message;
    }

    public static void send(CommandSender sender, String key) {
        String message = getMessage(key);
        if (! message.isEmpty()) {
            sender.sendMessage(message);
        }
    }

    public static void send(CommandSender sender, String key, String... replacements) {
        String message = getMessage(key, replacements);
        if (!message.isEmpty()) {
            sender.sendMessage(message);
        }
    }

    public static void send(Player player, String key) {
        send((CommandSender) player, key);
    }

    public static void send(Player player, String key, String... replacements) {
        send((CommandSender) player, key, replacements);
    }

    public static void broadcast(String key) {
        String message = getMessage(key);
        if (!message.isEmpty()) {
            plugin.getServer().broadcastMessage(message);
        }
    }

    public static void broadcast(String key, String... replacements) {
        String message = getMessage(key, replacements);
        if (!message.isEmpty()) {
            plugin. getServer().broadcastMessage(message);
        }
    }

    public static void reload() {
        loadMessages();
    }

    public static String stripColor(String message) {
        return ChatColor.stripColor(color(message));
    }

    public static String getPrefix() {
        return getMessage("prefix");
    }

    public static String withPrefix(String message) {
        return getPrefix() + " " + message;
    }
}