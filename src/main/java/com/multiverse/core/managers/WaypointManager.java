package com.multiverse.core.managers;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.data.YAMLDataManager;
import com.multiverse.core.models.Waypoint;
import com.multiverse.core.models.Dimension;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.*;

public class WaypointManager {
    private final MultiverseCore plugin;
    private final YAMLDataManager dataManager;
    private final DimensionManager dimensionManager;
    private final Map<UUID, List<Waypoint>> playerWaypoints = new HashMap<>();
    private final int MAX_WAYPOINTS;

    public WaypointManager(MultiverseCore plugin, YAMLDataManager dataManager, DimensionManager dimensionManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.dimensionManager = dimensionManager;
        this.MAX_WAYPOINTS = plugin.getConfig().getInt("waypoints.max-per-player", 10);
        loadWaypoints();
    }

    // 웨이포인트 CRUD
    public Waypoint createWaypoint(Player player, String name) {
        UUID uuid = player.getUniqueId();
        Location loc = player.getLocation();
        Dimension dim = dimensionManager.getDimension(dimensionManager.getPlayerCurrentDimension(player));
        if (dim == null) return null;

        List<Waypoint> waypoints = playerWaypoints.computeIfAbsent(uuid, k -> new ArrayList<>());
        Waypoint wp = new Waypoint(
                waypoints.size() + 1,
                uuid,
                name,
                dim.getId(),
                loc,
                System.currentTimeMillis()
        );
        waypoints.add(wp);
        saveWaypoints();
        return wp;
    }

    public Waypoint getWaypoint(Player player, String name) {
        UUID uuid = player.getUniqueId();
        List<Waypoint> waypoints = playerWaypoints.getOrDefault(uuid, new ArrayList<>());
        for (Waypoint wp : waypoints) {
            if (wp.getName().equalsIgnoreCase(name)) return wp;
        }
        return null;
    }

    public List<Waypoint> getPlayerWaypoints(Player player) {
        return playerWaypoints.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    public boolean deleteWaypoint(Player player, String name) {
        UUID uuid = player.getUniqueId();
        List<Waypoint> waypoints = playerWaypoints.getOrDefault(uuid, new ArrayList<>());
        Iterator<Waypoint> it = waypoints.iterator();
        while (it.hasNext()) {
            Waypoint wp = it.next();
            if (wp.getName().equalsIgnoreCase(name)) {
                it.remove();
                saveWaypoints();
                return true;
            }
        }
        return false;
    }

    // 제한 확인
    public int getMaxWaypoints() {
        return MAX_WAYPOINTS;
    }

    public boolean canCreateWaypoint(Player player) {
        return getPlayerWaypoints(player).size() < MAX_WAYPOINTS;
    }

    // 텔레포트
    public void teleportToWaypoint(Player player, Waypoint waypoint) {
        if (waypoint == null) return;
        player.teleport(waypoint.getLocation());
        player.sendMessage(plugin.getMessageUtil().format("teleport.success").replace("{dimension}", waypoint.getDimensionId()));
    }

    // GUI (stub)
    public void openWaypointList(Player player) {
        // 실제 GUI 구현 필요
        player.sendMessage("§e[웨이포인트 목록] 미구현");
    }

    public void openWaypointCreate(Player player) {
        // 실제 GUI 구현 필요
        player.sendMessage("§e[웨이포인트 생성] 미구현");
    }

    // LOAD/SAVE
    public void loadWaypoints() {
        Map<UUID, List<Waypoint>> all = dataManager.loadWaypoints();
        playerWaypoints.clear();
        playerWaypoints.putAll(all);
    }

    public void saveWaypoints() {
        dataManager.saveWaypoints(playerWaypoints);
    }
}