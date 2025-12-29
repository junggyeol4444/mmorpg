package com.multiverse.pet.entity;

import com.multiverse.pet.PetCore;
import com.multiverse.pet.model.Pet;
import com.multiverse.pet.model.PetBehavior;
import com.multiverse. pet.model.PetType;
import org. bukkit. Bukkit;
import org.bukkit. Location;
import org. bukkit.entity.Entity;
import org.bukkit. entity.LivingEntity;
import org.bukkit.entity.Monster;
import org. bukkit.entity. Player;
import org. bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * 펫 AI 클래스
 * 펫의 행동과 이동을 제어
 */
public class PetAI {

    private final PetCore plugin;
    private final PetEntity petEntity;

    // AI 태스크
    private BukkitTask aiTask;

    // AI 상태
    private AIState currentState;
    private PetBehavior behavior;

    // 이동 관련
    private Location moveTarget;
    private List<Location> currentPath;
    private int pathIndex;

    // 전투 관련
    private LivingEntity attackTarget;
    private long lastAttackTime;
    private int attackCooldown;

    // 설정
    private double followDistance;
    private double attackRange;
    private double aggroRange;
    private double moveSpeed;

    // 상태 플래그
    private boolean isFollowing;
    private boolean isSitting;
    private boolean isRunning;

    /**
     * AI 상태
     */
    public enum AIState {
        IDLE,           // 대기
        FOLLOWING,      // 따라가기
        MOVING,         // 이동 중
        ATTACKING,      // 공격 중
        FLEEING,        // 도주 중
        SITTING,        // 앉아있음
        GATHERING,      // 채집 중
        RETURNING       // 복귀 중
    }

    /**
     * 생성자
     */
    public PetAI(PetCore plugin, PetEntity petEntity) {
        this.plugin = plugin;
        this.petEntity = petEntity;
        this.currentState = AIState.IDLE;
        this. behavior = petEntity.getPet().getBehavior();
        this.isFollowing = true;
        this. isSitting = false;
        this. pathIndex = 0;
        
        loadSettings();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        Pet pet = petEntity. getPet();
        
        this.followDistance = plugin.getConfigManager().getPetSettings().getFollowDistance();
        this.attackRange = 2.0 + pet.getTotalStat("attack_range");
        this.aggroRange = 8.0 + pet.getTotalStat("aggro_range");
        this.moveSpeed = 0.25 + pet.getTotalStat("speed") / 100.0;
        this.attackCooldown = (int) (20 - pet.getTotalStat("attack_speed") / 10);
        this.attackCooldown = Math. max(5, attackCooldown);
    }

    /**
     * AI 시작
     */
    public void start() {
        if (aiTask != null) {
            stop();
        }

        aiTask = Bukkit.getScheduler().runTaskTimer(plugin, this:: tick, 1L, 1L);
    }

    /**
     * AI 중지
     */
    public void stop() {
        if (aiTask != null && ! aiTask.isCancelled()) {
            aiTask.cancel();
            aiTask = null;
        }
        
        currentState = AIState. IDLE;
        attackTarget = null;
        moveTarget = null;
        currentPath = null;
    }

    /**
     * AI 틱 (매 틱 호출)
     */
    private void tick() {
        if (!petEntity.isValid()) {
            return;
        }

        // 앉아있으면 아무것도 안함
        if (isSitting) {
            currentState = AIState. SITTING;
            return;
        }

        // 상태별 처리
        switch (currentState) {
            case IDLE:
                processIdle();
                break;
            case FOLLOWING:
                processFollowing();
                break;
            case MOVING: 
                processMoving();
                break;
            case ATTACKING: 
                processAttacking();
                break;
            case FLEEING:
                processFleeing();
                break;
            case GATHERING:
                processGathering();
                break;
            case RETURNING:
                processReturning();
                break;
            case SITTING: 
                // 아무것도 안함
                break;
        }

        // 펫 엔티티 틱
        petEntity.tick();
    }

    // ===== 상태별 처리 =====

    /**
     * 대기 상태 처리
     */
    private void processIdle() {
        Player owner = petEntity.getOwner();
        if (owner == null) return;

        // 공격적 행동 - 주변 적 탐색
        if (behavior.isAggressive()) {
            LivingEntity enemy = findNearestEnemy();
            if (enemy != null) {
                setAttackTarget(enemy);
                return;
            }
        }

        // 따라가기
        if (isFollowing) {
            double distance = getDistanceToOwner();
            if (distance > followDistance) {
                currentState = AIState. FOLLOWING;
            }
        }

        // 랜덤 행동 (가끔)
        if (Math.random() < 0.01) {
            performRandomAction();
        }
    }

    /**
     * 따라가기 상태 처리
     */
    private void processFollowing() {
        Player owner = petEntity.getOwner();
        if (owner == null) {
            currentState = AIState.IDLE;
            return;
        }

        double distance = getDistanceToOwner();

        // 도착했으면 대기
        if (distance <= followDistance) {
            currentState = AIState.IDLE;
            currentPath = null;
            return;
        }

        // 너무 멀면 텔레포트
        double teleportDistance = plugin.getConfigManager().getPetSettings().getTeleportDistance();
        if (distance > teleportDistance) {
            petEntity.teleportToOwner();
            currentState = AIState.IDLE;
            return;
        }

        // 경로 따라 이동
        if (currentPath == null || pathIndex >= currentPath.size()) {
            currentPath = PetPathfinder. findPath(petEntity.getEntity().getLocation(), owner.getLocation());
            pathIndex = 0;
        }

        if (currentPath != null && ! currentPath.isEmpty()) {
            moveAlongPath();
        } else {
            // 직선 이동
            moveToward(owner.getLocation());
        }

        // 공격적 행동 중 적 발견
        if (behavior. isAggressive()) {
            LivingEntity enemy = findNearestEnemy();
            if (enemy != null && getDistanceToEntity(enemy) < aggroRange) {
                setAttackTarget(enemy);
            }
        }
    }

    /**
     * 이동 상태 처리
     */
    private void processMoving() {
        if (moveTarget == null) {
            currentState = AIState.IDLE;
            return;
        }

        double distance = petEntity.getEntity().getLocation().distance(moveTarget);

        // 도착
        if (distance < 1.0) {
            currentState = AIState.IDLE;
            moveTarget = null;
            currentPath = null;
            return;
        }

        // 경로 이동
        if (currentPath == null || pathIndex >= currentPath.size()) {
            currentPath = PetPathfinder.findPath(petEntity.getEntity().getLocation(), moveTarget);
            pathIndex = 0;
        }

        if (currentPath != null && !currentPath.isEmpty()) {
            moveAlongPath();
        } else {
            moveToward(moveTarget);
        }
    }

    /**
     * 공격 상태 처리
     */
    private void processAttacking() {
        if (attackTarget == null || ! attackTarget.isValid() || attackTarget.isDead()) {
            clearAttackTarget();
            currentState = AIState. IDLE;
            return;
        }

        // 주인과 너무 멀어지면 복귀
        double ownerDistance = getDistanceToOwner();
        if (ownerDistance > aggroRange * 2) {
            clearAttackTarget();
            currentState = AIState. RETURNING;
            return;
        }

        double targetDistance = getDistanceToEntity(attackTarget);

        // 공격 범위 내
        if (targetDistance <= attackRange) {
            // 공격 쿨다운 확인
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAttackTime >= attackCooldown * 50L) {
                petEntity.attack(attackTarget);
                lastAttackTime = currentTime;

                // 대상 처치
                if (attackTarget.isDead()) {
                    clearAttackTarget();
                    currentState = AIState.IDLE;
                }
            }
        } else {
            // 대상에게 접근
            moveToward(attackTarget. getLocation());
        }
    }

    /**
     * 도주 상태 처리
     */
    private void processFleeing() {
        Player owner = petEntity.getOwner();
        if (owner == null) {
            currentState = AIState. IDLE;
            return;
        }

        // 주인에게 도주
        double distance = getDistanceToOwner();
        if (distance <= followDistance) {
            currentState = AIState. IDLE;
            return;
        }

        // 빠르게 주인에게 이동
        moveToward(owner. getLocation(), moveSpeed * 1.5);
    }

    /**
     * 채집 상태 처리
     */
    private void processGathering() {
        // 채집형 펫 전용 로직
        Pet pet = petEntity. getPet();
        if (pet.getType() != PetType. GATHERING) {
            currentState = AIState. IDLE;
            return;
        }

        // TODO: 채집 로직 구현
        currentState = AIState. IDLE;
    }

    /**
     * 복귀 상태 처리
     */
    private void processReturning() {
        Player owner = petEntity.getOwner();
        if (owner == null) {
            currentState = AIState. IDLE;
            return;
        }

        double distance = getDistanceToOwner();
        if (distance <= followDistance) {
            currentState = AIState.IDLE;
            return;
        }

        moveToward(owner. getLocation(), moveSpeed * 1.2);
    }

    // ===== 이동 =====

    /**
     * 경로를 따라 이동
     */
    private void moveAlongPath() {
        if (currentPath == null || pathIndex >= currentPath.size()) {
            return;
        }

        Location target = currentPath.get(pathIndex);
        double distance = petEntity.getEntity().getLocation().distance(target);

        if (distance < 0.5) {
            pathIndex++;
            return;
        }

        moveToward(target);
    }

    /**
     * 대상 위치로 이동
     */
    private void moveToward(Location target) {
        moveToward(target, moveSpeed);
    }

    /**
     * 대상 위치로 이동 (속도 지정)
     */
    private void moveToward(Location target, double speed) {
        LivingEntity entity = petEntity.getEntity();
        if (entity == null) return;

        Location current = entity.getLocation();
        Vector direction = target.toVector().subtract(current.toVector());

        if (direction. lengthSquared() < 0.01) {
            return;
        }

        direction.normalize().multiply(speed);

        // Y 속도 조절 (점프/낙하)
        double yDiff = target.getY() - current.getY();
        if (yDiff > 0.5) {
            direction.setY(0.42); // 점프
        } else if (yDiff < -0.5) {
            direction.setY(-0.1);
        }

        entity.setVelocity(direction);

        // 바라보는 방향 설정
        Location lookAt = current.clone();
        lookAt. setDirection(direction);
        entity.teleport(entity.getLocation().setDirection(direction));
    }

    // ===== 전투 =====

    /**
     * 가장 가까운 적 찾기
     */
    private LivingEntity findNearestEnemy() {
        LivingEntity entity = petEntity.getEntity();
        if (entity == null) return null;

        Player owner = petEntity.getOwner();
        Location loc = entity.getLocation();
        LivingEntity nearest = null;
        double nearestDistance = aggroRange;

        for (Entity nearby : entity.getNearbyEntities(aggroRange, aggroRange, aggroRange)) {
            if (!(nearby instanceof LivingEntity)) continue;
            if (nearby.equals(owner)) continue;
            if (nearby. equals(entity)) continue;
            
            // 다른 펫 제외
            if (plugin.getPetManager().isPetEntity(nearby)) continue;

            LivingEntity living = (LivingEntity) nearby;

            // 적대적 몹만 공격
            if (behavior == PetBehavior.AGGRESSIVE && !(living instanceof Monster)) {
                continue;
            }

            // 방어적 - 주인을 공격하는 대상만
            if (behavior == PetBehavior.DEFENSIVE) {
                // TODO: 주인을 공격한 대상 확인
            }

            double distance = loc.distance(living.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = living;
            }
        }

        return nearest;
    }

    /**
     * 공격 대상 설정
     */
    public void setAttackTarget(LivingEntity target) {
        this.attackTarget = target;
        this. currentState = AIState. ATTACKING;
        petEntity.setAttackTarget(target);
    }

    /**
     * 공격 대상 해제
     */
    public void clearAttackTarget() {
        this.attackTarget = null;
        petEntity.clearAttackTarget();
    }

    // ===== 유틸리티 =====

    /**
     * 주인과의 거리
     */
    private double getDistanceToOwner() {
        Player owner = petEntity.getOwner();
        if (owner == null || petEntity.getEntity() == null) {
            return Double.MAX_VALUE;
        }
        return petEntity.getEntity().getLocation().distance(owner.getLocation());
    }

    /**
     * 엔티티와의 거리
     */
    private double getDistanceToEntity(Entity target) {
        if (target == null || petEntity.getEntity() == null) {
            return Double.MAX_VALUE;
        }
        return petEntity.getEntity().getLocation().distance(target.getLocation());
    }

    /**
     * 랜덤 행동 수행
     */
    private void performRandomAction() {
        // 주변 배회
        Location current = petEntity.getEntity().getLocation();
        double angle = Math.random() * 2 * Math.PI;
        double distance = 2 + Math.random() * 3;

        Location randomTarget = current.clone().add(
            Math.cos(angle) * distance,
            0,
            Math. sin(angle) * distance
        );

        // 유효한 위치인지 확인
        if (randomTarget.getBlock().isPassable()) {
            this.moveTarget = randomTarget;
            this.currentState = AIState. MOVING;
        }
    }

    // ===== Getter/Setter =====

    public AIState getCurrentState() {
        return currentState;
    }

    public void setState(AIState state) {
        this. currentState = state;
    }

    public PetBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(PetBehavior behavior) {
        this.behavior = behavior;
    }

    public void setMoveTarget(Location target) {
        this.moveTarget = target;
        this.currentPath = null;
        this.pathIndex = 0;
        this.currentState = AIState.MOVING;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        this. isFollowing = following;
        if (following && currentState == AIState.IDLE) {
            currentState = AIState. FOLLOWING;
        }
    }

    public boolean isSitting() {
        return isSitting;
    }

    public void setSitting(boolean sitting) {
        this. isSitting = sitting;
        if (sitting) {
            currentState = AIState. SITTING;
        } else {
            currentState = AIState. IDLE;
        }
    }

    public LivingEntity getAttackTarget() {
        return attackTarget;
    }
}