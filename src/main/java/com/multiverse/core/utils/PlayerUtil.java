package com.multiverse.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtil {

    public static Player getPlayerByUUID(UUID uuid) {
        if (uuid == null) return null;
        return Bukkit.getPlayer(uuid);
    }

    public static Player getPlayerByName(String name) {
        if (name == null || name.isEmpty()) return null;
        return Bukkit.getPlayerExact(name);
    }

    public static boolean isOnline(UUID uuid) {
        return getPlayerByUUID(uuid) != null;
    }

    public static boolean isOnline(String name) {
        return getPlayerByName(name) != null;
    }
}