package com.multiverse.death.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Location;
import com.multiverse.death.DeathAndRebirthPlugin;

/**
 * 명계 지역에 파티클 효과를 주기적으로 뿌리는 태스크
 */
public class NetherRealmParticleTask extends BukkitRunnable {

    private final DeathAndRebirthPlugin plugin;

    public NetherRealmParticleTask(DeathAndRebirthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        World world = plugin.getNetherRealmWorld();
        for (Location loc : plugin.getLocationManager().getNetherRealmLocations()) {
            world.spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 15, 0.5, 1, 0.5, 0.02);
        }
    }
}