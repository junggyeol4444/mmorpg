package com.multiverse.skill.events;

import com.multiverse.skill.data.models. Skill;
import com.multiverse.skill.data.models.LearnedSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit. event.HandlerList;

/**
 * 스킬 캐스팅 완료 이벤트
 */
public class SkillCastCompleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final Skill skill;
    private final LearnedSkill learnedSkill;
    private final long castDuration;
    private final int targetsHit;
    private double totalDamageDealt;

    public SkillCastCompleteEvent(Player player, Skill skill, LearnedSkill learnedSkill, 
                                 long castDuration, int targetsHit, double totalDamageDealt) {
        this.player = player;
        this.skill = skill;
        this.learnedSkill = learnedSkill;
        this.castDuration = castDuration;
        this.targetsHit = targetsHit;
        this.totalDamageDealt = totalDamageDealt;
    }

    /**
     * 플레이어 조회
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 캐스팅한 스킬 조회
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * 습득한 스킬 정보 조회
     */
    public LearnedSkill getLearnedSkill() {
        return learnedSkill;
    }

    /**
     * 캐스팅 지속 시간 조회 (밀리초)
     */
    public long getCastDuration() {
        return castDuration;
    }

    /**
     * 맞은 대상 수 조회
     */
    public int getTargetsHit() {
        return targetsHit;
    }

    /**
     * 총 데미지 조회
     */
    public double getTotalDamageDealt() {
        return totalDamageDealt;
    }

    /**
     * 총 데미지 설정
     */
    public void setTotalDamageDealt(double totalDamageDealt) {
        this.totalDamageDealt = totalDamageDealt;
    }

    /**
     * 평균 데미지 계산
     */
    public double getAverageDamage() {
        if (targetsHit == 0) {
            return 0.0;
        }
        return totalDamageDealt / targetsHit;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}