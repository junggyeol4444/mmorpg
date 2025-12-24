package com.multiverse.guild.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationUtil {
    private LocationUtil() {}

    public static Location loc(String world, double x, double y, double z) {
        World w = Bukkit.getWorld(world);
        return new Location(w, x, y, z);
    }

    public static Location loc(String world, double x, double y, double z, float yaw, float pitch) {
        World w = Bukkit.getWorld(world);
        return new Location(w, x, y, z, yaw, pitch);
    }

    public static boolean isInside(Location loc, Location c1, Location c2) {
        if (loc == null || c1 == null || c2 == null) return false;
        if (!loc.getWorld().equals(c1.getWorld())) return false;
        double minX = Math.min(c1.getX(), c2.getX());
        double maxX = Math.max(c1.getX(), c2.getX());
        double minY = Math.min(c1.getY(), c2.getY());
        double maxY = Math.max(c1.getY(), c2.getY());
        double minZ = Math.min(c1.getZ(), c2.getZ());
        double maxZ = Math.max(c1.getZ(), c2.getZ());
        return loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
}