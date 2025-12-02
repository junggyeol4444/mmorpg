package com.multiverse.core.managers;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.data.YAMLDataManager;
import com.multiverse.core.models.Portal;
import com.multiverse.core.models.enums.PortalType;
import com.multiverse.core.models.Dimension;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class PortalManager {
    private final MultiverseCore plugin;
    private final YAMLDataManager dataManager;
    private final DimensionManager dimensionManager;
    private final Map<String, Portal> portalsByName = new HashMap<>();
    private final Map<Integer, Portal> portalsById = new HashMap<>();
    private int nextId = 1;

    public PortalManager(MultiverseCore plugin, YAMLDataManager dataManager, DimensionManager dimensionManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.dimensionManager = dimensionManager;
        loadPortals();
    }

    // 포탈 CRUD
    public Portal createPortal(String name, String from, String to, Location loc, PortalType type, int cost) {
        Portal portal = new Portal(
                nextId++, name, from, to, loc.getWorld().getName(), loc, type, cost, true
        );
        portalsByName.put(name, portal);
        portalsById.put(portal.getId(), portal);
        savePortals();
        return portal;
    }

    public Portal getPortal(String name) {
        return portalsByName.get(name);
    }

    public Portal getPortalById(int id) {
        return portalsById.get(id);
    }

    public List<Portal> getAllPortals() {
        return new ArrayList<>(portalsByName.values());
    }

    public void updatePortal(Portal portal) {
        portalsByName.put(portal.getName(), portal);
        portalsById.put(portal.getId(), portal);
        savePortals();
    }

    public void deletePortal(String name) {
        Portal portal = portalsByName.remove(name);
        if (portal != null) {
            portalsById.remove(portal.getId());
            savePortals();
        }
    }

    // 포탈 감지
    public Portal getPortalAtLocation(Location loc) {
        for (Portal portal : portalsByName.values()) {
            Location pLoc = portal.getLocation();
            if (pLoc.getWorld().equals(loc.getWorld()) &&
                pLoc.distance(loc) < 2.0) { // 2블럭 이내 감지
                return portal;
            }
        }
        return null;
    }

    public boolean isInPortalRegion(Player player) {
        // WorldGuard 연동 필요, 여기서는 더미
        // 실제: RegionContainer, RegionManager에서 portal_<포탈이름> 영역 체크
        return false;
    }

    // 포탈 효과
    public void spawnPortalParticles(Portal portal) {
        // 실제 파티클 효과: portal.location 기준 PORTAL 파티클 50개
        // Bukkit API: location.getWorld().spawnParticle(...)
    }

    public void playPortalSound(Location loc) {
        loc.getWorld().playSound(loc, "ENTITY_ENDERMAN_TELEPORT", 1f, 1f);
    }

    // 포탈 사용
    public void usePortal(Player player, Portal portal) {
        if (portal == null || !portal.isActive()) {
            player.sendMessage(plugin.getMessageUtil().get("portal.not-found"));
            return;
        }
        Dimension toDim = dimensionManager.getDimension(portal.getToDimension());
        if (toDim == null || !toDim.isActive()) {
            player.sendMessage(plugin.getMessageUtil().get("dimension.inactive"));
            return;
        }
        int cost = portal.getCost();
        // 실제 Vault 연동 필요 (EconomyUtil)
        // 이곳에서는 비용 차감 더미 처리
        player.teleport(toDim.getWorldName() != null
                ? org.bukkit.Bukkit.getWorld(toDim.getWorldName()).getSpawnLocation()
                : player.getWorld().getSpawnLocation());
        spawnPortalParticles(portal);
        playPortalSound(portal.getLocation());
        player.sendMessage(plugin.getMessageUtil().format("teleport.success")
                .replace("{dimension}", toDim.getName()));
    }

    // LOAD/SAVE
    private void loadPortals() {
        List<Portal> loaded = dataManager.loadPortals();
        portalsByName.clear();
        portalsById.clear();
        int maxId = nextId;
        for (Portal portal : loaded) {
            portalsByName.put(portal.getName(), portal);
            portalsById.put(portal.getId(), portal);
            if (portal.getId() >= maxId) {
                maxId = portal.getId() + 1;
            }
        }
        nextId = maxId;
    }

    private void savePortals() {
        dataManager.savePortals(getAllPortals());
    }
}