package com.multiverse.core.data.storage;

import com.multiverse.core.models.Portal;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalStorage {

    // 포탈을 ID(고유값)로 관리
    private final Map<UUID, Portal> portals = new HashMap<>();

    /**
     * 포탈 추가
     */
    public void addPortal(Portal portal) {
        if (portal != null) {
            portals.put(portal.getId(), portal);
        }
    }

    /**
     * 포탈 삭제
     */
    public void removePortal(UUID portalId) {
        portals.remove(portalId);
    }

    /**
     * 포탈 가져오기
     */
    public Portal getPortal(UUID portalId) {
        return portals.get(portalId);
    }

    /**
     * 좌표로 포탈 찾기
     */
    public Portal getPortalByLocation(Location location) {
        return portals.values().stream()
            .filter(portal -> portal.getLocation().equals(location))
            .findFirst().orElse(null);
    }

    /**
     * 전체 포탈 목록
     */
    public Map<UUID, Portal> getAllPortals() {
        return portals;
    }
}