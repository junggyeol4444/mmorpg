package com.multiverse.core.listeners;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.managers.PortalManager;
import com.multiverse.core.models.Portal;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;

public class PortalListener implements Listener {
    private final MultiverseCore plugin;
    private final PortalManager portalManager;

    public PortalListener(MultiverseCore plugin) {
        this.plugin = plugin;
        this.portalManager = plugin.getPortalManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();

        if (to == null)
            return;

        // 포탈 위치에 이동했을 때 자동 텔레포트
        Portal portal = portalManager.getPortalAtLocation(to);
        if (portal != null && portal.isActive()) {
            portalManager.usePortal(player, portal);
        }
    }
}