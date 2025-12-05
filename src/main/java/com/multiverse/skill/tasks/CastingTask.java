package com.multiverse.skill.tasks;

import com.multiverse.skill. SkillCore;
import com.multiverse. skill.managers.SkillManager;
import com.multiverse.skill.managers.SkillCastManager;
import com.multiverse. skill.data.models.*;
import com.multiverse.skill.data.enums.CastStatus;
import com.multiverse.skill.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 스킬 캐스팅 작업
 */
public class CastingTask extends BukkitRunnable {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final SkillCastManager castManager;
    private final Player player;
    private final Skill skill;
    private final LearnedSkill learnedSkill;
    private final SkillCast skillCast;
    private long castStartTime;
    private long castDuration;
    private int taskId;

    public CastingTask(SkillCore plugin, SkillManager skillManager, SkillCastManager castManager,
                      Player player, Skill skill, LearnedSkill learnedSkill) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.castManager = castManager;
        this.player = player;
        this.skill = skill;
        this.learnedSkill = learnedSkill;
        this.skillCast = new SkillCast();
        this.castStartTime = System. currentTimeMillis();
        this.castDuration = (long) skill.getCastTime();
    }

    @Override
    public void run() {
        if (player == null || player.isOffline() || player.isDead()) {
            cancel();
            return;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - castStartTime;
        long remainingTime = castDuration - elapsedTime;

        // 캐스팅 진행 상황 표시
        if (remainingTime > 0) {
            displayCastingProgress(player, skill, remainingTime);
        } else {
            // 캐스팅 완료
            completeCasting();
            cancel();
        }
    }

    /**
     * 캐스팅 진행 상황 표시
     */
    private void displayCastingProgress(Player player, Skill skill, long remainingTime) {
        double progress = ((double) (castDuration - remainingTime)) / castDuration;
        int barLength = 20;
        int filledLength = (int) (barLength * progress);

        StringBuilder bar = new StringBuilder("§a");
        for (int i = 0; i < filledLength; i++) {
            bar.append("█");
        }
        bar. append("§7");
        for (int i = filledLength; i < barLength; i++) {
            bar.append("█");
        }

        MessageUtils.sendActionBar(player, bar.toString() + " §f" + (remainingTime / 1000. 0));
    }

    /**
     * 캐스팅 완료
     */
    private void completeCasting() {
        if (skill == null || learnedSkill == null) {
            return;
        }

        // 스킬 효과 실행
        try {
            castManager.executeSkillEffect(player, skill, learnedSkill);
            MessageUtils.sendMessage(player, "§a" + skill.getName() + " 시전 완료!");
        } catch (Exception e) {
            MessageUtils.sendMessage(player, "§c스킬 시전 중 오류가 발생했습니다!");
            plugin.getLogger().warning("캐스팅 오류: " + skill.getId());
            e.printStackTrace();
        }

        // 스킬 쿨다운 시작
        learnedSkill.startCooldown();

        // 마나 소모
        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData != null) {
            skillData.consumeMana(skill.getManaCost());
        }
    }

    /**
     * 캐스팅 취소
     */
    public void cancelCasting() {
        MessageUtils.sendMessage(player, "§c" + skill.getName() + " 시전이 취소되었습니다!");
        cancel();
    }

    /**
     * 작업 ID 설정
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * 작업 ID 조회
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * 캐스팅 시간 조회
     */
    public long getCastDuration() {
        return castDuration;
    }

    /**
     * 경과 시간 조회
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - castStartTime;
    }
}