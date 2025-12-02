package com.multiverse.core.tasks;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.multiverse.core.manager.PortalManager;
import com.multiverse.core.models.Portal;

/**
 * 포탈 위치에 파티클 효과를 반복적으로 출력하는 작업
 */
public class PortalParticleTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final PortalManager portalManager;

    public PortalParticleTask(JavaPlugin plugin, PortalManager portalManager) {
        this.plugin = plugin;
        this.portalManager = portalManager;
    }

    @Override
    public void run() {
        for (Portal portal : portalManager.getAllPortals()) {
            Location loc = portal.getLocation();
            if (loc != null) {
                plugin.getServer().getOnlinePlayers().forEach(player ->
                    player.spawnParticle(Particle.PORTAL, loc, 30, 0.5, 1, 0.5, 0.05)
                );
            }
        }
    }
}