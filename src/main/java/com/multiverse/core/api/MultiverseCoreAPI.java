package com.multiverse.core.api;

import com.multiverse.core.manager.DimensionManager;
import com.multiverse.core.manager.WaypointManager;
import com.multiverse.core.models.Waypoint;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class MultiverseCoreAPI {

    private final DimensionManager dimensionManager;
    private final WaypointManager waypointManager;

    public MultiverseCoreAPI(DimensionManager dimensionManager, WaypointManager waypointManager) {
        this.dimensionManager = dimensionManager;
        this.waypointManager = waypointManager;
    }

    /**
     * 플레이어를 지정한 디멘션으로 텔레포트합니다.
     */
    public boolean teleportToDimension(Player player, String dimensionName) {
        Location loc = dimensionManager.getSpawnLocation(dimensionName);
        if (loc != null) {
            player.teleport(loc);
            return true;
        }
        return false;
    }

    /**
     * 플레이어를 웨이포인트로 텔레포트합니다.
     */
    public boolean teleportToWaypoint(Player player, String waypointName) {
        Waypoint waypoint = waypointManager.getWaypoint(player, waypointName);
        if (waypoint != null) {
            player.teleport(waypoint.getLocation());
            return true;
        }
        return false;
    }

    /**
     * 플레이어의 현재 밸런스를 반환합니다.
     */
    public double getPlayerBalance(Player player) {
        // 예시: EconomyUtil 사용
        return com.multiverse.core.utils.EconomyUtil.getBalance(player);
    }
}