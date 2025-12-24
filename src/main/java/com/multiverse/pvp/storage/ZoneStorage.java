package com.multiverse.pvp.storage;

import com.multiverse.pvp.PvPCore;
import com.multiverse.pvp.data.PvPZone;
import com.multiverse.pvp.enums. ZoneType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org. bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit. configuration.file.FileConfiguration;

import java.util. UUID;

public class ZoneStorage {

    private final PvPCore plugin;
    private final DataManager dataManager;

    public ZoneStorage(PvPCore plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    /**
     * 모든 지역 로드
     */
    public void loadAllZones() {
        FileConfiguration config = dataManager.getZonesConfig();

        ConfigurationSection zonesSection = config. getConfigurationSection("zones");
        if (zonesSection == null) {
            plugin.getLogger().info("저장된 지역이 없습니다.");
            return;
        }

        int loaded = 0;
        for (String key : zonesSection.getKeys(false)) {
            ConfigurationSection zoneSection = zonesSection. getConfigurationSection(key);
            if (zoneSection == null) continue;

            PvPZone zone = loadZone(zoneSection);
            if (zone != null) {
                plugin. getZoneManager().loadZone(zone);
                loaded++;
            }
        }

        plugin. getLogger().info("지역 " + loaded + "개 로드 완료");
    }

    /**
     * 지역 로드
     */
    private PvPZone loadZone(ConfigurationSection section) {
        try {
            UUID zoneId = UUID. fromString(section. getString("id"));
            String name = section.getString("name");
            ZoneType type = ZoneType.valueOf(section. getString("type", "SAFE"));

            PvPZone zone = new PvPZone(zoneId, name, type);

            // 기본 설정
            zone.setWorldName(section.getString("world"));
            zone.setAutoPvP(section.getBoolean("auto-pvp", type.isPvpEnabled()));
            zone.setRewardMultiplier(section. getDouble("reward-multiplier", type.getDefaultRewardMultiplier()));
            zone.setExpMultiplier(section. getDouble("exp-multiplier", type. getDefaultExpMultiplier()));
            zone.setMinLevel(section.getInt("min-level", 0));
            zone.setMaxLevel(section.getInt("max-level", Integer.MAX_VALUE));
            zone.setEnabled(section.getBoolean("enabled", true));

            // 메시지
            zone.setEnterMessage(section. getString("enter-message", type.getDefaultEnterMessage()));
            zone.setLeaveMessage(section. getString("leave-message", type.getDefaultLeaveMessage()));

            // 특수 설정
            zone. setAllowFlight(section.getBoolean("allow-flight", type.allowsFlight()));
            zone.setAllowTeleport(section. getBoolean("allow-teleport", type.allowsTeleport()));
            zone.setKeepInventory(section.getBoolean("keep-inventory", type.keepsInventory()));
            zone.setDropItems(section.getBoolean("drop-items", type.dropsItems()));
            zone.setDeathPenaltyMultiplier(section. getDouble("death-penalty-multiplier", type.getDefaultDeathPenalty()));

            // 경계
            zone.setCorner1(loadLocation(section.getConfigurationSection("corner1")));
            zone.setCorner2(loadLocation(section. getConfigurationSection("corner2")));

            return zone;

        } catch (Exception e) {
            plugin. getLogger().warning("지역 로드 실패: " + e.getMessage());
            return null;
        }
    }

    /**
     * 지역 저장
     */
    public void saveZone(PvPZone zone) {
        FileConfiguration config = dataManager.getZonesConfig();

        String path = "zones." + zone.getZoneId().toString() + ".";

        config.set(path + "id", zone.getZoneId().toString());
        config.set(path + "name", zone.getZoneName());
        config.set(path + "type", zone.getType().name());
        config.set(path + "world", zone. getWorldName());
        config.set(path + "auto-pvp", zone. isAutoPvP());
        config.set(path + "reward-multiplier", zone.getRewardMultiplier());
        config.set(path + "exp-multiplier", zone. getExpMultiplier());
        config.set(path + "min-level", zone.getMinLevel());
        config.set(path + "max-level", zone. getMaxLevel());
        config.set(path + "enabled", zone.isEnabled());

        config.set(path + "enter-message", zone. getEnterMessage());
        config.set(path + "leave-message", zone. getLeaveMessage());

        config.set(path + "allow-flight", zone. isAllowFlight());
        config.set(path + "allow-teleport", zone.isAllowTeleport());
        config.set(path + "keep-inventory", zone.isKeepInventory());
        config.set(path + "drop-items", zone.isDropItems());
        config.set(path + "death-penalty-multiplier", zone.getDeathPenaltyMultiplier());

        saveLocation(config, path + "corner1", zone.getCorner1());
        saveLocation(config, path + "corner2", zone.getCorner2());

        dataManager.saveZonesConfig();
    }

    /**
     * 지역 삭제
     */
    public void deleteZone(UUID zoneId) {
        FileConfiguration config = dataManager. getZonesConfig();
        config.set("zones." + zoneId.toString(), null);
        dataManager.saveZonesConfig();
    }

    /**
     * 모든 지역 저장
     */
    public void saveAllZones() {
        for (PvPZone zone : plugin.getZoneManager().getAllZones()) {
            saveZone(zone);
        }
        plugin.getLogger().info("모든 지역 저장 완료");
    }

    /**
     * 위치 로드
     */
    private Location loadLocation(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        String worldName = section. getString("world");
        if (worldName == null) {
            return null;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }

        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section. getDouble("z");

        return new Location(world, x, y, z);
    }

    /**
     * 위치 저장
     */
    private void saveLocation(FileConfiguration config, String path, Location location) {
        if (location == null) {
            config. set(path, null);
            return;
        }

        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ". x", location.getX());
        config.set(path + ".y", location. getY());
        config.set(path + ".z", location.getZ());
    }
}