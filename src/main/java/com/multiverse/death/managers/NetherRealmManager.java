package com.multiverse.death.managers;

import com.multiverse.death.DeathAndRebirthCore;
import com.multiverse.death.data.DataManager;
import com.multiverse.death.models.enums.LocationType;
import com.multiverse.death.models.NetherRealmLocation;
import com.multiverse.death.utils.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetherRealmManager {

    private final DeathAndRebirthCore plugin;
    private final DataManager dataManager;
    private final ConfigUtil configUtil;

    public NetherRealmManager(DeathAndRebirthCore plugin, DataManager dataManager, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.configUtil = configUtil;
    }

    // 위치 관리
    public Location getLocation(LocationType type) {
        NetherRealmLocation loc = dataManager.getNetherRealmLocation(type);
        return loc != null ? loc.getLocation() : null;
    }

    public void setLocation(LocationType type, Location location) {
        dataManager.setNetherRealmLocation(type, location);
    }

    public List<NetherRealmLocation> getAllLocations() {
        return dataManager.getAllNetherRealmLocations();
    }

    // 텔레포트
    public void teleportToNetherRealm(Player player, LocationType type) {
        Location loc = getLocation(type);
        if (loc != null) player.teleport(loc);
    }

    public void teleportToSpawn(Player player) {
        Location spawn = getLocation(LocationType.SPAWN);
        if (spawn != null) player.teleport(spawn);
    }

    // PVP 방지
    public boolean isPvpDisabled(Location loc) {
        if (loc == null) return false;
        NetherRealmLocation spawn = dataManager.getNetherRealmLocation(LocationType.SPAWN);
        List<NetherRealmLocation> protectedLocs = getAllLocations();
        for (NetherRealmLocation nloc : protectedLocs) {
            if (isSameWorld(nloc.getLocation(), loc)) {
                // 원의 반경 40으로 명계 PVP 전체 금지 처리 (간단 버전)
                if (nloc.getLocation().distance(loc) < 40) {
                    return true;
                }
            }
        }
        return false;
    }

    public void applyPvpProtection(Player player) {
        // 적용 지점마다 확장 가능
    }

    private boolean isSameWorld(Location a, Location b) {
        if (a == null || b == null) return false;
        return a.getWorld().getName().equals(b.getWorld().getName());
    }
}