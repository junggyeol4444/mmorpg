package com.multiverse.skill. data.models;

import com.multiverse.skill.data.enums.CastStatus;
import org.bukkit.Location;
import org.bukkit. entity.LivingEntity;
import org. bukkit.entity.Player;

import java.util.UUID;

public class SkillCast {

    private UUID castId;
    private Player caster;
    private Skill skill;
    
    // 대상
    private LivingEntity target;
    private Location targetLocation;
    
    // 캐스팅
    private long startTime;
    private long castDuration;
    private boolean isChanneling;
    
    // 상태
    private CastStatus status;

    public SkillCast() {
        this.castId = UUID.randomUUID();
        this.status = CastStatus.CASTING;
        this.startTime = System.currentTimeMillis();
    }

    // Getters and Setters

    public UUID getCastId() {
        return castId;
    }

    public void setCastId(UUID castId) {
        this.castId = castId;
    }

    public Player getCaster() {
        return caster;
    }

    public void setCaster(Player caster) {
        this.caster = caster;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCastDuration() {
        return castDuration;
    }

    public void setCastDuration(long castDuration) {
        this.castDuration = castDuration;
    }

    public boolean isChanneling() {
        return isChanneling;
    }

    public void setChanneling(boolean channeling) {
        isChanneling = channeling;
    }

    public CastStatus getStatus() {
        return status;
    }

    public void setStatus(CastStatus status) {
        this.status = status;
    }

    /**
     * 경과 시간 조회 (밀리초)
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 캐스팅 진행률 (0. 0 ~ 1.0)
     */
    public double getProgress() {
        if (castDuration <= 0) {
            return 1.0;
        }
        double progress = (double) getElapsedTime() / castDuration;
        return Math.min(progress, 1.0);
    }

    /**
     * 캐스팅 완료 여부
     */
    public boolean isComplete() {
        return getElapsedTime() >= castDuration;
    }

    /**
     * 남은 시간 (밀리초)
     */
    public long getRemainingTime() {
        return Math.max(0, castDuration - getElapsedTime());
    }

    /**
     * 캐스트 정보 문자열
     */
    public String getInfoString() {
        String targetInfo = target != null ? 
            target.getName() : 
            (targetLocation != null ? targetLocation.toString() : "없음");
        
        return String.format("Skill: %s | Target: %s | Progress: %.1f%% | Status: %s",
                skill.getName(),
                targetInfo,
                getProgress() * 100,
                status. name());
    }

    @Override
    public String toString() {
        return "SkillCast{" +
                "castId=" + castId +
                ", skill=" + (skill != null ? skill.getSkillId() : "null") +
                ", status=" + status +
                ", elapsed=" + getElapsedTime() + "ms" +
                '}';
    }
}