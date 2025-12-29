package com.multiverse. pet.task;

import com.multiverse.pet.PetCore;
import com.multiverse.pet.entity.PetEntity;
import com. multiverse.pet. model.Pet;
import com.multiverse.pet.model. PetBehavior;
import org.bukkit. Bukkit;
import org.bukkit. Location;
import org. bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java. util.UUID;

/**
 * 펫 추적 태스크
 * 펫이 주인을 따라다니도록 처리
 */
public class PetFollowTask extends BukkitRunnable {

    private final PetCore plugin;

    // 설정값
    private static final double FOLLOW_DISTANCE = 3.0;       // 기본 따라가기 거리
    private static final double TELEPORT_DISTANCE = 30.0;    // 텔레포트 거리
    private static final double MAX_SPEED = 0.4;             // 최대 이동 속도
    private static final double MIN_DISTANCE = 1.5;          // 최소 거리 (너무 가까우면 멈춤)

    public PetFollowTask(PetCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player :  Bukkit.getOnlinePlayers()) {
            processPlayer(player);
        }
    }

    /**
     * 플레이어의 펫들 처리
     */
    private void processPlayer(Player player) {
        UUID playerId = player. getUniqueId();
        List<PetEntity> activePets = plugin.getPetManager().getActivePets(playerId);

        if (activePets. isEmpty()) {
            return;
        }

        Location playerLoc = player.getLocation();

        for (PetEntity petEntity : activePets) {
            processPet(player, playerLoc, petEntity);
        }
    }

    /**
     * 개별 펫 처리
     */
    private void processPet(Player player, Location playerLoc, PetEntity petEntity) {
        LivingEntity entity = petEntity.getEntity();
        if (entity == null || entity.isDead()) {
            return;
        }

        Pet pet = petEntity. getPet();

        // 앉아있거나 대기 모드면 이동 안함
        if (petEntity.isSitting() || pet.getBehavior() == PetBehavior.STAY) {
            return;
        }

        // 공격 중이면 타겟 추적
        if (petEntity.hasAttackTarget()) {
            processAttackTarget(petEntity);
            return;
        }

        Location petLoc = entity.getLocation();

        // 월드가 다르면 텔레포트
        if (!petLoc.getWorld().equals(playerLoc.getWorld())) {
            teleportToOwner(petEntity, player);
            return;
        }

        double distance = petLoc.distance(playerLoc);

        // 너무 멀면 텔레포트
        if (distance > TELEPORT_DISTANCE) {
            teleportToOwner(petEntity, player);
            return;
        }

        // 적정 거리 이내면 멈춤
        if (distance < MIN_DISTANCE) {
            stopMoving(entity);
            return;
        }

        // 따라가기 거리 이내면 천천히 이동
        if (distance < FOLLOW_DISTANCE) {
            moveTowards(entity, playerLoc, 0.2);
            return;
        }

        // 그 외에는 빠르게 따라감
        double speed = calculateSpeed(pet, distance);
        moveTowards(entity, playerLoc, speed);
    }

    /**
     * 공격 타겟 추적
     */
    private void processAttackTarget(PetEntity petEntity) {
        LivingEntity target = petEntity.getAttackTarget();
        LivingEntity entity = petEntity.getEntity();

        if (target == null || target.isDead()) {
            petEntity.clearAttackTarget();
            return;
        }

        Location entityLoc = entity. getLocation();
        Location targetLoc = target.getLocation();

        // 월드가 다르면 타겟 해제
        if (! entityLoc.getWorld().equals(targetLoc.getWorld())) {
            petEntity.clearAttackTarget();
            return;
        }

        double distance = entityLoc.distance(targetLoc);

        // 공격 범위 내면 공격
        Pet pet = petEntity. getPet();
        double attackRange = pet. getTotalStat("attack_range");
        if (attackRange <= 0) attackRange = 2.0;

        if (distance <= attackRange) {
            // 공격은 별도 처리
            petEntity.attackTarget();
        } else if (distance > 30) {
            // 너무 멀어지면 타겟 해제
            petEntity.clearAttackTarget();
        } else {
            // 타겟으로 이동
            moveTowards(entity, targetLoc, MAX_SPEED);
        }
    }

    /**
     * 목표 지점으로 이동
     */
    private void moveTowards(LivingEntity entity, Location target, double speed) {
        Location current = entity.getLocation();

        // 방향 계산
        Vector direction = target.toVector().subtract(current.toVector()).normalize();

        // 속도 적용
        Vector velocity = direction.multiply(speed);

        // Y축 속도 조정 (점프/낙하)
        if (target.getY() > current.getY() + 0.5) {
            velocity.setY(0. 4); // 점프
        } else {
            velocity.setY(entity.getVelocity().getY()); // 현재 Y속도 유지
        }

        // 이동
        entity. setVelocity(velocity);

        // 바라보는 방향 설정
        Location lookAt = current.clone();
        lookAt. setDirection(direction);
        entity.teleport(entity.getLocation().setDirection(direction));
    }

    /**
     * 이동 멈춤
     */
    private void stopMoving(LivingEntity entity) {
        Vector velocity = entity.getVelocity();
        velocity.setX(0);
        velocity.setZ(0);
        entity.setVelocity(velocity);
    }

    /**
     * 주인에게 텔레포트
     */
    private void teleportToOwner(PetEntity petEntity, Player owner) {
        Location safeLoc = findSafeLocation(owner. getLocation());
        if (safeLoc != null) {
            petEntity.getEntity().teleport(safeLoc);
            
            if (plugin.isDebugMode()) {
                plugin.getLogger().info("[DEBUG] 펫 텔레포트:  " + 
                        petEntity.getPet().getPetName() + " -> " + owner.getName());
            }
        }
    }

    /**
     * 안전한 위치 찾기
     */
    private Location findSafeLocation(Location center) {
        // 주인 뒤쪽
        Vector behind = center.getDirection().multiply(-1.5);
        Location safeLoc = center. clone().add(behind);

        // 지면 높이 조정
        safeLoc.setY(center.getWorld().getHighestBlockYAt(safeLoc) + 1);

        return safeLoc;
    }

    /**
     * 속도 계산
     */
    private double calculateSpeed(Pet pet, double distance) {
        double baseSpeed = pet.getTotalStat("speed");
        if (baseSpeed <= 0) baseSpeed = 0.3;

        // 거리에 따라 속도 조절
        double speedMultiplier = 1.0;
        if (distance > 15) {
            speedMultiplier = 1.5;
        } else if (distance > 10) {
            speedMultiplier = 1.2;
        }

        double speed = baseSpeed * speedMultiplier * 0.01;
        return Math.min(speed, MAX_SPEED);
    }

    /**
     * 태스크 시작
     */
    public void start() {
        // 4틱(0.2초)마다 실행
        this.runTaskTimer(plugin, 0L, 4L);
    }

    /**
     * 태스크 중지
     */
    public void stop() {
        try {
            this. cancel();
        } catch (IllegalStateException ignored) {
        }
    }
}