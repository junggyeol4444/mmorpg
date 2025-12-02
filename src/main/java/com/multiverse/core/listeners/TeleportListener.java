package com.multiverse.core.listeners;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.managers.TeleportManager;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;

public class TeleportListener implements Listener {
    private final MultiverseCore plugin;
    private final TeleportManager teleportManager;

    public TeleportListener(MultiverseCore plugin) {
        this.plugin = plugin;
        this.teleportManager = plugin.getTeleportManager();
    }

    // 웜업 중 이동하면 텔레포트 취소
    @EventHandler
    public void onPlayerMoveDuringWarmup(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (teleportManager.isWarming(player)) {
            if (!event.getFrom().equals(event.getTo())) {
                teleportManager.cancelWarmup(player);
            }
        }
    }
}