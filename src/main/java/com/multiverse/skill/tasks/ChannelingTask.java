package com.multiverse.skill.tasks;

import com.multiverse.skill.SkillCore;
import com.multiverse.skill.managers.SkillManager;
import com.multiverse.skill.data.models.*;
import com.multiverse.skill.utils.MessageUtils;
import org. bukkit.entity.Player;
import org.bukkit.scheduler. BukkitRunnable;

/**
 * 스킬 채널링 작업 (지속 스킬)
 */
public class ChannelingTask extends BukkitRunnable {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final Player player;
    private final Skill skill;
    private final LearnedSkill learnedSkill;
    private long channelingStartTime;
    private long channelingDuration;
    private long tickInterval;
    private int tickCount;
    private int totalTicks;
    private int taskId;

    public ChannelingTask(SkillCore plugin, SkillManager skillManager,
                         Player player, Skill skill, LearnedSkill learnedSkill) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.player = player;
        this.skill = skill;
        this.learnedSkill = learnedSkill;
        this.channelingStartTime = System.currentTimeMillis();
        this.channelingDuration = (long) skill. getDuration();
        this.tickInterval = 500; // 0.5초마다 틱 발생
        this.totalTicks = (int) (channelingDuration / tickInterval);
        this.tickCount = 0;
    }

    @Override
    public void run() {
        if (player == null || player.isOffline() || player.isDead()) {
            cancelChanneling();
            cancel();
            return;
        }

        // 플레이어 움직임 감지 (채널링 중단)
        if (isPlayerMoving(player)) {
            MessageUtils.sendMessage(player, "§c채널링이 중단되었습니다!");
            cancelChanneling();
            cancel();
            return;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - channelingStartTime;

        if (elapsedTime >= channelingDuration) {
            // 채널링 완료
            completeChanneling();
            cancel();
        } else {
            // 틱 발생
            tickCount++;
            executeChannelingTick();
            displayChannelingProgress(elapsedTime);
        }
    }

    /**
     * 플레이어 움직임 감지
     */
    private boolean isPlayerMoving(Player player) {
        // 간단한 구현: 이동 속도 확인
        return player.getVelocity(). length() > 0.1;
    }

    /**
     * 채널링 틱 실행
     */
    private void executeChannelingTick() {
        if (skill == null || learnedSkill == null) {
            return;
        }

        // 틱마다 효과 실행 (예: 힐, DoT 등)
        try {
            // 틱 효과 처리
            double tickDamage = skill.getBaseDamage() / totalTicks;
            
            // 주변 엔티티에 영향
            // (구현 필요)
            
        } catch (Exception e) {
            plugin.getLogger().warning("채널링 틱 오류: " + skill.getId());
            e.printStackTrace();
        }
    }

    /**
     * 채널링 진행 상황 표시
     */
    private void displayChannelingProgress(long elapsedTime) {
        double progress = (double) elapsedTime / channelingDuration;
        int barLength = 20;
        int filledLength = (int) (barLength * progress);

        StringBuilder bar = new StringBuilder("§b");
        for (int i = 0; i < filledLength; i++) {
            bar. append("█");
        }
        bar.append("§7");
        for (int i = filledLength; i < barLength; i++) {
            bar.append("█");
        }

        long remainingTime = channelingDuration - elapsedTime;
        MessageUtils.sendActionBar(player, bar.toString() + " §f" + (remainingTime / 1000. 0));
    }

    /**
     * 채널링 완료
     */
    private void completeChanneling() {
        MessageUtils.sendMessage(player, "§a" + skill.getName() + " 채널링 완료!");

        // 쿨다운 시작
        learnedSkill.startCooldown();

        // 마나 소모
        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData != null) {
            skillData.consumeMana(skill.getManaCost());
        }
    }

    /**
     * 채널링 취소
     */
    private void cancelChanneling() {
        MessageUtils.sendMessage(player, "§c" + skill.getName() + " 채널링이 취소되었습니다!");
    }

    /**
     * 작업 ID 설정
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * 현재 틱 수 조회
     */
    public int getTickCount() {
        return tickCount;
    }

    /**
     * 총 틱 수 조회
     */
    public int getTotalTicks() {
        return totalTicks;
    }
}