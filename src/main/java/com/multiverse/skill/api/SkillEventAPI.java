package com.multiverse.skill. api;

import com.multiverse.skill.SkillCore;
import com.multiverse.skill.events.*;
import org.bukkit. Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * 스킬 이벤트 API
 */
public class SkillEventAPI {

    private final SkillCore plugin;

    public SkillEventAPI(SkillCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 스킬 학습 이벤트 발생
     */
    public void callSkillLearnEvent(Player player, com.multiverse.skill.data. models. Skill skill, int pointsUsed) {
        if (player == null || skill == null) {
            return;
        }

        SkillLearnEvent event = new SkillLearnEvent(player, skill, pointsUsed);
        Bukkit.getPluginManager(). callEvent(event);
    }

    /**
     * 스킬 사용 이벤트 발생
     */
    public void callSkillUseEvent(Player player, com.multiverse.skill.data.models.Skill skill, 
                                 com.multiverse.skill.data. models.LearnedSkill learned, double manaCost) {
        if (player == null || skill == null) {
            return;
        }

        SkillUseEvent event = new SkillUseEvent(player, skill, learned, manaCost);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * 스킬 레벨업 이벤트 발생
     */
    public void callSkillLevelUpEvent(Player player, com.multiverse.skill.data. models.LearnedSkill learned,
                                     int previousLevel, int newLevel, long experienceGained) {
        if (player == null || learned == null) {
            return;
        }

        SkillLevelUpEvent event = new SkillLevelUpEvent(player, learned, previousLevel, newLevel, experienceGained);
        Bukkit. getPluginManager().callEvent(event);
    }

    /**
     * 스킬 콤보 완성 이벤트 발생
     */
    public void callSkillComboCompleteEvent(Player player, com. multiverse.skill.data.models. SkillCombo combo,
                                           long completionTime) {
        if (player == null || combo == null) {
            return;
        }

        SkillComboCompleteEvent event = new SkillComboCompleteEvent(player, combo, completionTime);
        Bukkit.getPluginManager(). callEvent(event);
    }

    /**
     * 스킬 진화 이벤트 발생
     */
    public void callSkillEvolutionEvent(Player player, com. multiverse.skill.data.models. SkillEvolution evolution,
                                       String fromSkillId, String toSkillId,
                                       com.multiverse.skill.data. enums.EvolutionType evolutionType) {
        if (player == null || evolution == null) {
            return;
        }

        SkillEvolutionEvent event = new SkillEvolutionEvent(player, evolution, fromSkillId, toSkillId, evolutionType);
        Bukkit. getPluginManager().callEvent(event);
    }

    /**
     * 스킬 캐스팅 완료 이벤트 발생
     */
    public void callSkillCastCompleteEvent(Player player, com.multiverse.skill.data.models. Skill skill,
                                          com.multiverse.skill.data. models.LearnedSkill learned,
                                          long castDuration, int targetsHit, double totalDamageDealt) {
        if (player == null || skill == null) {
            return;
        }

        SkillCastCompleteEvent event = new SkillCastCompleteEvent(player, skill, learned, castDuration, targetsHit, totalDamageDealt);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * 이벤트 리스너 등록
     */
    public void registerEventListener(Object listener) {
        if (listener == null) {
            return;
        }

        Bukkit.getPluginManager(). registerEvents((org.bukkit.event. Listener) listener, plugin);
    }

    /**
     * 이벤트 리스너 등록 해제
     */
    public void unregisterEventListener(Object listener) {
        if (listener == null) {
            return;
        }

        HandlerList. unregisterAll((org.bukkit.event. Listener) listener);
    }
}