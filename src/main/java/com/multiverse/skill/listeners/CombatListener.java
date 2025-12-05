package com.multiverse.skill.listeners;

import com.multiverse.skill. SkillCore;
import com.multiverse. skill.managers.SkillManager;
import com.multiverse.skill.managers. ComboManager;
import com.multiverse.skill.data.models.*;
import com.multiverse.skill.data.enums.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org. bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit. event.entity.EntityDamageByEntityEvent;

/**
 * 전투 리스너
 */
public class CombatListener implements Listener {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final ComboManager comboManager;

    public CombatListener(SkillCore plugin, SkillManager skillManager, ComboManager comboManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.comboManager = comboManager;
    }

    /**
     * 엔티티 데미지 이벤트
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        LivingEntity damager = null;
        LivingEntity victim = event.getEntity();

        // 대미저 확인
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof LivingEntity) {
            damager = (LivingEntity) event.getDamager();
        }

        if (damager == null || !(damager instanceof Player)) {
            return;
        }

        Player attacker = (Player) damager;

        // 플레이어 스킬 데이터 조회
        PlayerSkillData skillData = skillManager.getPlayerSkillData(attacker. getUniqueId());
        if (skillData == null) {
            return;
        }

        // 콤보 진행 상태 확인
        PlayerComboState comboState = comboManager. getPlayerComboState(attacker.getUniqueId());
        if (comboState != null && comboState.isInCombo()) {
            // 콤보 시퀀스 확인
            comboManager.updateComboSequence(attacker.getUniqueId(), skillData.getActivePreset());
        }

        // 데미지 통계 기록
        recordDamageStatistics(attacker, victim, event.getDamage());

        // 반격 확인
        checkCounterAttack(victim, attacker);
    }

    /**
     * 데미지 통계 기록
     */
    private void recordDamageStatistics(Player attacker, LivingEntity victim, double damage) {
        PlayerSkillData skillData = skillManager. getPlayerSkillData(attacker.getUniqueId());
        if (skillData == null) {
            return;
        }

        // 습득한 스킬 데이터에 총 데미지 기록
        for (LearnedSkill skill : skillData.getSkills(). values()) {
            if (skill != null) {
                skill.addTotalDamage((long) damage);
            }
        }
    }

    /**
     * 반격 확인
     */
    private void checkCounterAttack(LivingEntity victim, Player attacker) {
        if (!(victim instanceof Player)) {
            return;
        }

        Player victimPlayer = (Player) victim;
        PlayerSkillData victimSkillData = skillManager. getPlayerSkillData(victimPlayer.getUniqueId());

        if (victimSkillData == null) {
            return;
        }

        // 반격 확률 계산 (예: 20%)
        double counterChance = 0.2;
        if (Math.random() < counterChance) {
            // 반격 실행
            SkillPreset victimPreset = victimSkillData.getActivePreset();
            if (victimPreset != null && ! victimPreset.getHotbar().isEmpty()) {
                // 반격 스킬 선택
                String counterSkillId = victimPreset.getHotbar().values().stream(). findFirst().orElse(null);
                if (counterSkillId != null) {
                    victimPlayer.sendMessage("§c반격 발동!");
                }
            }
        }
    }
}