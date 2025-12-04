package com.multiverse.dungeon.data.model;

import com.multiverse.dungeon.data.enums.TrapType;
import org.bukkit.Location;
import java.util.UUID;

/**
 * 던전 함정 데이터 클래스
 */
public class DungeonTrap {

    private UUID trapId; // 함정 ID
    private TrapType type; // 함정 타입
    private Location location; // 함정 위치
    
    private double damage; // 데미지
    private double radius; // 범위 (블록)
    private int duration; // 지속 시간 (초)
    
    private int triggerCount; // 발동 횟수
    private int maxTriggers; // 최대 발동 횟수 (-1 = 무제한)
    
    private boolean active; // 활성 상태

    /**
     * 생성자
     */
    public DungeonTrap(TrapType type, Location location) {
        this.trapId = UUID.randomUUID();
        this.type = type;
        this.location = location;
        this.damage = type.getBaseDamage();
        this. radius = 2.0;
        this. duration = 5;
        this.triggerCount = 0;
        this. maxTriggers = -1;
        this.active = true;
    }

    /**
     * 기본 생성자
     */
    public DungeonTrap() {
        this(TrapType.SPIKE, null);
    }

    // ===== Getters & Setters =====

    public UUID getTrapId() {
        return trapId;
    }

    public void setTrapId(UUID trapId) {
        this.trapId = trapId;
    }

    public TrapType getType() {
        return type;
    }

    public void setType(TrapType type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = Math.max(0, damage);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = Math.max(0, radius);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = Math.max(0, duration);
    }

    public int getTriggerCount() {
        return triggerCount;
    }

    public void setTriggerCount(int triggerCount) {
        this.triggerCount = Math.max(0, triggerCount);
    }

    public int getMaxTriggers() {
        return maxTriggers;
    }

    public void setMaxTriggers(int maxTriggers) {
        this.maxTriggers = maxTriggers;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * 함정 발동 가능 여부 확인
     *
     * @return 발동 가능하면 true
     */
    public boolean canTrigger() {
        if (! active) {
            return false;
        }

        if (maxTriggers == -1) {
            return true; // 무제한
        }

        return triggerCount < maxTriggers;
    }

    /**
     * 함정 발동
     */
    public void trigger() {
        if (canTrigger()) {
            triggerCount++;
            if (maxTriggers != -1 && triggerCount >= maxTriggers) {
                active = false;
            }
        }
    }

    /**
     * 함정이 위치에서 발동되는지 확인
     *
     * @param checkLocation 확인할 위치
     * @return 발동되면 true
     */
    public boolean isTriggeredAt(Location checkLocation) {
        if (location == null || checkLocation == null) {
            return false;
        }

        if (!location.getWorld().equals(checkLocation.getWorld())) {
            return false;
        }

        return location.distance(checkLocation) <= radius;
    }

    /**
     * 함정 초기화
     */
    public void reset() {
        this.triggerCount = 0;
        this.active = true;
    }

    @Override
    public String toString() {
        return "DungeonTrap{" +
                "trapId=" + trapId +
                ", type=" + type +
                ", damage=" + damage +
                ", radius=" + radius +
                ", triggerCount=" + triggerCount +
                ", active=" + active +
                '}';
    }
}