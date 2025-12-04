package com.multiverse.combat.skills;

import org.bukkit. entity.Player;
import org. bukkit. Bukkit;
import com.multiverse.combat.CombatCore;
import com.multiverse.combat.models.Skill;
import java.util.*;

/**
 * 캐스팅 처리 클래스
 * 캐스팅 시간 및 채널링을 관리합니다. 
 */
public class CastingHandler {
    
    private final CombatCore plugin;
    private final Map<UUID, CastingData> castingPlayers = new HashMap<>();
    
    /**
     * CastingHandler 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public CastingHandler(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 캐스팅 시작
     * @param player 플레이어
     * @param skill 스킬
     */
    public void startCasting(Player player, Skill skill) {
        UUID uuid = player.getUniqueId();
        
        if (castingPlayers.containsKey(uuid)) {
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c이미 캐스팅 중입니다.");
            return;
        }
        
        CastingData data = new CastingData(skill.getSkillId(), skill. getCastTime());
        castingPlayers. put(uuid, data);
        
        long castTimeMs = skill.getCastTime();
        long ticks = castTimeMs / 50;
        
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a" + skill.getName() + "§a을(를) 시전 중.. .");
        
        // 캐스팅 타이머
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (castingPlayers.containsKey(uuid)) {
                CastingData castData = castingPlayers.get(uuid);
                
                if (castData.getSkillId(). equals(skill.getSkillId())) {
                    // 캐스팅 완료
                    completeCasting(player, skill);
                    castingPlayers.remove(uuid);
                } else {
                    castingPlayers.remove(uuid);
                }
            }
        }, ticks);
    }
    
    /**
     * 캐스팅 완료
     * @param player 플레이어
     * @param skill 스킬
     */
    private void completeCasting(Player player, Skill skill) {
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a" + skill.getName() + "§a 시전이 완료되었습니다!");
    }
    
    /**
     * 캐스팅 취소
     * @param player 플레이어
     */
    public void cancelCasting(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (castingPlayers.containsKey(uuid)) {
            castingPlayers.remove(uuid);
            player.sendMessage(plugin.getCombatConfig().getString("messages. prefix", "[전투] ") +
                "§c캐스팅이 취소되었습니다.");
        }
    }
    
    /**
     * 캐스팅 중인지 확인
     * @param player 플레이어
     * @return 캐스팅 중이면 true
     */
    public boolean isCasting(Player player) {
        return castingPlayers.containsKey(player.getUniqueId());
    }
    
    /**
     * 캐스팅 데이터 클래스
     */
    private static class CastingData {
        private final String skillId;
        private final long startTime;
        private final long duration;
        
        public CastingData(String skillId, long duration) {
            this.skillId = skillId;
            this.startTime = System.currentTimeMillis();
            this.duration = duration;
        }
        
        public String getSkillId() { return skillId; }
        public long getStartTime() { return startTime; }
        public long getDuration() { return duration; }
        
        public long getElapsedTime() {
            return System.currentTimeMillis() - startTime;
        }
        
        public double getProgress() {
            return (getElapsedTime() / (double) duration) * 100. 0;
        }
    }
}