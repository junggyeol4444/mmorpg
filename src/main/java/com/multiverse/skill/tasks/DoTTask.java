package com.multiverse.skill.tasks;

import com.multiverse. skill.SkillCore;
import com.multiverse.skill. utils.MessageUtils;
import org. bukkit.entity.LivingEntity;
import org. bukkit.entity.Player;
import org.bukkit.scheduler. BukkitRunnable;

/**
 * DoT (지속 데미지) 작업
 */
public class DoTTask extends BukkitRunnable {

    private final SkillCore plugin;
    private final LivingEntity target;
    private final Player caster;
    private final String effectName;
    private final double damagePerTick;
    private final long duration;
    private final long tickInterval;
    private long startTime;
    private int tickCount;
    private int taskId;

    public DoTTask(SkillCore plugin, LivingEntity target, Player caster, String effectName,
                  double damagePerTick, long duration, long tickInterval) {
        this.plugin = plugin;
        this.target = target;
        this.caster = caster;
        this.effectName = effectName;
        this.damagePerTick = damagePerTick;
        this.duration = duration;
        this.tickInterval = tickInterval;
        this.startTime = System.currentTimeMillis();
        this.tickCount = 0;
    }

    @Override
    public void run() {
        if (target == null || target.isDead()) {
            cancel();
            return;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        if (elapsedTime >= duration) {
            // DoT 종료
            completeDoT();
            cancel();
        } else {
            // 틱 데미지 적용
            tickCount++;
            applyTickDamage();
        }
    }

    /**
     * 틱 데미지 적용
     */
    private void applyTickDamage() {
        if (target == null || damagePerTick <= 0) {
            return;
        }

        // 현재 체력 감소
        double newHealth = target.getHealth() - damagePerTick;
        
        if (newHealth <= 0) {
            target.setHealth(0);
        } else {
            target.setHealth(newHealth);
        }

        // 시각 효과 (선택)
        if (caster instanceof Player) {
            caster.sendMessage("§c" + effectName + ": " + (int) damagePerTick + " 데미지");
        }
    }

    /**
     * DoT 종료
     */
    private void completeDoT() {
        if (target instanceof Player) {
            ((Player) target).sendMessage("§c" + effectName + " 효과가 해제되었습니다!");
        }

        plugin.getLogger().info("DoT 종료: " + effectName + " (" + tickCount + " 틱)");
    }

    /**
     * 작업 ID 설정
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * 대상 조회
     */
    public LivingEntity getTarget() {
        return target;
    }

    /**
     * 효과명 조회
     */
    public String getEffectName() {
        return effectName;
    }

    /**
     * 현재 틱 수 조회
     */
    public int getTickCount() {
        return tickCount;
    }

    /**
     * 경과 시간 조회
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
}