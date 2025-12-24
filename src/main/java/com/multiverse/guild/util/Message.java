package com.multiverse.guild.util;

import com.multiverse.guild.GuildCore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class Message {
    private static String prefix = "&8[&6Guild&8]&r ";

    private Message() {}

    public static void init(GuildCore plugin) {
        prefix = plugin.getConfig().getString(ConfigKeys.PREFIX, prefix);
        // load optional messages.yml override prefix
        File f = new File(plugin.getDataFolder(), "messages.yml");
        if (f.exists()) {
            YamlConfiguration yc = YamlConfiguration.loadConfiguration(f);
            prefix = yc.getString("messages.prefix", prefix);
        }
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String prefixed(String s) {
        return color(prefix + s);
    }
}