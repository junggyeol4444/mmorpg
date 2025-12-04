package com.multiverse.combat. managers;

import org.bukkit.entity.Player;
import org.bukkit. Bukkit;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models.PvPData;
import java.util.*;

/**
 * PvP 관리 클래스
 * 플레이어 간 전투, 랭킹, 명성 시스템을 관리합니다.
 */
public class PvPManager {
    
    private final CombatCore plugin;
    private final Map<UUID, PvPData> pvpDataMap = new HashMap<>();
    
    /**
     * PvPManager 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public PvPManager(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * PvP 활성화 여부 설정
     * @param player 플레이어
     * @param enabled 활성화 여부
     */
    public void setPvPEnabled(Player player, boolean enabled) {
        PvPData data = getPvPData(player);
        data.setPvPEnabled(enabled);
        
        if (enabled) {
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                plugin.getCombatConfig().getString("messages.pvp. enabled", "§aPvP가 활성화되었습니다."));
        } else {
            player.sendMessage(plugin.getCombatConfig(). getString("messages.prefix", "[전투] ") +
                plugin.getCombatConfig().getString("messages.pvp.disabled", "§cPvP가 비활성화되었습니다."));
        }
    }
    
    /**
     * PvP 활성화 여부 조회
     * @param player 플레이어
     * @return 활성화되면 true
     */
    public boolean isPvPEnabled(Player player) {
        return getPvPData(player).isPvPEnabled();
    }
    
    /**
     * 공격 가능 여부 확인
     * @param attacker 공격자
     * @param target 대상
     * @return 공격 가능하면 true
     */
    public boolean canAttack(Player attacker, Player target) {
        // PvP 활성화 확인
        if (!isPvPEnabled(attacker) || !isPvPEnabled(target)) {
            return false;
        }
        
        // 레벨 제한 확인
        if (plugin.getCombatConfig().getBoolean("pvp.level-restriction. enabled", true)) {
            int maxDifference = plugin.getCombatConfig().getInt("pvp.level-restriction.max-difference", 10);
            int levelDiff = Math.abs(attacker. getLevel() - target.getLevel());
            
            if (levelDiff > maxDifference) {
                return false;
            }
        }
        
        // 신규 유저 보호
        if (plugin.getCombatConfig().getBoolean("pvp.newbie-protection.enabled", true)) {
            int protectionLevel = plugin.getCombatConfig().getInt("pvp. newbie-protection.level-threshold", 20);
            
            if (attacker.getLevel() < protectionLevel || target.getLevel() < protectionLevel) {
                if (attacker.getLevel() < protectionLevel && target.getLevel() < protectionLevel) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * 플레이어 처치
     * @param killer 처치자
     * @param victim 피해자
     */
    public void onPlayerKill(Player killer, Player victim) {
        PvPData killerData = getPvPData(killer);
        PvPData victimData = getPvPData(victim);
        
        // 처치 기록
        killerData.addKill();
        victimData.addDeath();
        
        // 연속 처치
        killerData.incrementKillStreak();
        if (killerData.getKillStreak() > killerData.getMaxKillStreak()) {
            killerData.setMaxKillStreak(killerData.getKillStreak());
        }
        
        // 명성/악명 변경
        int famePerKill = plugin.getCombatConfig().getInt("pvp.rewards.fame-per-kill", 10);
        killerData.addFame(famePerKill);
        
        victimData.resetKillStreak();
        
        // 메시지
        String killMessage = plugin.getCombatConfig().getString("messages.pvp. kill", 
            "§a{player}을(를) 처치했습니다!  (+{fame} 명성)")
            .replace("{player}", victim.getName())
            .replace("{fame}", String.valueOf(famePerKill));
        
        killer.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") + killMessage);
        
        String deathMessage = plugin.getCombatConfig().getString("messages.pvp.death",
            "§c{player}에게 처치당했습니다.")
            .replace("{player}", killer.getName());
        
        victim.sendMessage(plugin.getCombatConfig(). getString("messages.prefix", "[전투] ") + deathMessage);
        
        // 연속 처치 알림
        checkKillStreak(killer);
    }
    
    /**
     * 플레이어 사망
     * @param player 플레이어
     */
    public void onPlayerDeath(Player player) {
        PvPData data = getPvPData(player);
        data.resetKillStreak();
        
        // 경험치 손실 (선택)
        if (plugin.getCombatConfig(). getBoolean("pvp.penalties.death-exp-loss", true)) {
            double expLoss = plugin.getCombatConfig().getDouble("pvp. penalties.death-exp-loss", 5.0);
            // 경험치 손실 구현
        }
    }
    
    /**
     * PvP 데이터 조회
     * @param player 플레이어
     * @return PvP 데이터
     */
    public PvPData getPvPData(Player player) {
        UUID uuid = player.getUniqueId();
        return pvpDataMap.computeIfAbsent(uuid, k -> new PvPData(uuid, player. getName()));
    }
    
    /**
     * 명성 추가
     * @param player 플레이어
     * @param amount 추가량
     */
    public void addFame(Player player, int amount) {
        getPvPData(player).addFame(amount);
    }
    
    /**
     * 악명 추가
     * @param player 플레이어
     * @param amount 추가량
     */
    public void addInfamy(Player player, int amount) {
        getPvPData(player).addInfamy(amount);
    }
    
    /**
     * 상위 플레이어 목록
     * @param limit 조회 수
     * @return 상위 플레이어 리스트
     */
    public List<Player> getTopPlayers(int limit) {
        return pvpDataMap.values().stream()
            .sorted((a, b) -> Integer.compare(b.getKills(), a.getKills()))
            .limit(limit)
            .map(data -> Bukkit.getPlayer(data.getPlayerUUID()))
            .filter(Objects::nonNull)
            .toList();
    }
    
    /**
     * 플레이어 랭크 조회
     * @param player 플레이어
     * @return 랭크 (1부터 시작)
     */
    public int getPlayerRank(Player player) {
        PvPData playerData = getPvPData(player);
        int rank = 1;
        
        for (PvPData data : pvpDataMap.values()) {
            if (data.getKills() > playerData.getKills()) {
                rank++;
            }
        }
        
        return rank;
    }
    
    /**
     * 연속 처치 체크 및 알림
     */
    private void checkKillStreak(Player player) {
        PvPData data = getPvPData(player);
        int streak = data.getKillStreak();
        
        String message = null;
        
        if (streak == 3) {
            message = plugin. getCombatConfig().getString("messages.kill-streak.3", "§e연속 3킬!");
        } else if (streak == 5) {
            message = plugin.getCombatConfig().getString("messages.kill-streak.5", "§6무자비!");
        } else if (streak == 10) {
            message = plugin. getCombatConfig().getString("messages.kill-streak.10", "§c학살자!");
        }
        
        if (message != null) {
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") + message);
            
            // 모든 플레이어에게 알림
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (! p.equals(player)) {
                    p.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                        "§e" + player.getName() + message);
                }
            });
        }
    }
    
    /**
     * 플레이어 데이터 저장
     * @param player 플레이어
     */
    public void savePlayerData(Player player) {
        PvPData data = getPvPData(player);
        plugin.getDataManager().savePvPData(player, data);
    }
    
    /**
     * 플레이어 데이터 로드
     * @param player 플레이어
     */
    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        PvPData data = plugin.getDataManager(). loadPvPData(player);
        pvpDataMap.put(uuid, data);
    }
}