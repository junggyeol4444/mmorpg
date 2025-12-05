package com.multiverse.skill. tasks;

import com.multiverse. skill. SkillCore;
import com.multiverse. skill.data.models.ProjectileConfig;
import com.multiverse. skill.utils.LocationUtils;
import com.multiverse. skill.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit. Particle;
import org.bukkit.entity.Player;
import org.bukkit. entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * 투사체 작업
 */
public class ProjectileTask extends BukkitRunnable {

    private final SkillCore plugin;
    private final Projectile projectile;
    private final Player caster;
    private final ProjectileConfig config;
    private final Location startLocation;
    private final Vector direction;
    private long startTime;
    private int taskId;

    public ProjectileTask(SkillCore plugin, Projectile projectile, Player caster, 
                         ProjectileConfig config) {
        this.plugin = plugin;
        this.projectile = projectile;
        this.caster = caster;
        this.config = config;
        this.startLocation = projectile.getLocation(). clone();
        this.direction = projectile.getVelocity().normalize();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (projectile == null || projectile.isDead()) {
            cancel();
            return;
        }

        Location projectileLoc = projectile.getLocation();
        
        // 최대 거리 확인
        double traveledDistance = startLocation.distance(projectileLoc);
        if (traveledDistance > config.getRange()) {
            projectile.remove();
            cancel();
            return;
        }

        // 최대 시간 확인
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime > config.getMaxDuration()) {
            projectile. remove();
            cancel();
            return;
        }

        // 파티클 효과 표시
        displayProjectileTrail(projectileLoc);

        // 중력 적용
        if (config.isAffectedByGravity()) {
            applyGravity();
        }

        // 회전 효과
        if (config.isRotating()) {
            applyRotation();
        }
    }

    /**
     * 투사체 자취 표시
     */
    private void displayProjectileTrail(Location location) {
        if (config.getTrailParticle() == null) {
            return;
        }

        ParticleUtils.playParticle(location, config.getTrailParticle(), 3);
    }

    /**
     * 중력 적용
     */
    private void applyGravity() {
        if (projectile == null) {
            return;
        }

        Vector velocity = projectile. getVelocity();
        velocity.setY(velocity.getY() - 0.1); // 중력 값
        projectile.setVelocity(velocity);
    }

    /**
     * 회전 효과
     */
    private void applyRotation() {
        if (projectile == null) {
            return;
        }

        Location loc = projectile.getLocation();
        loc.setYaw(loc.getYaw() + 10);
        projectile.teleport(loc);
    }

    /**
     * 작업 ID 설정
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * 투사체 조회
     */
    public Projectile getProjectile() {
        return projectile;
    }

    /**
     * 발사자 조회
     */
    public Player getCaster() {
        return caster;
    }

    /**
     * 이동 거리 조회
     */
    public double getTraveledDistance() {
        if (projectile == null) {
            return 0.0;
        }
        return startLocation.distance(projectile.getLocation());
    }
}