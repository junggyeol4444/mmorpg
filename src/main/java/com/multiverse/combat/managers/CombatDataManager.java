package com.multiverse.combat. managers;

import org.bukkit.entity.Player;
import com.multiverse.combat.CombatCore;
import java.util.*;

/**
 * 전투 데이터 관리 클래스
 * 플레이어의 전투 통계를 관리합니다.
 */
public class CombatDataManager {
    
    private final CombatCore plugin;
    
    // 플레이어별 통계 데이터
    private final Map<UUID, Map<String, Object>> combatStats = new HashMap<>();
    
    // 통계 키
    private static final String TOTAL_DAMAGE_DEALT = "total_damage_dealt";
    private static final String TOTAL_DAMAGE_TAKEN = "total_damage_taken";
    private static final String TOTAL_KILLS = "total_kills";
    private static final String TOTAL_DEATHS = "total_deaths";
    private static final String MAX_COMBO = "max_combo";
    private static final String TOTAL_COMBOS = "total_combos";
    private static final String TOTAL_CRITS = "total_crits";
    private static final String CRIT_DAMAGE = "crit_damage";
    private static final String TIMES_DODGED = "times_dodged";
    
    /**
     * CombatDataManager 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public CombatDataManager(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어 통계 데이터 초기화
     * @param player 플레이어
     */
    private Map<String, Object> initializeStats(Player player) {
        UUID uuid = player.getUniqueId();
        Map<String, Object> stats = new HashMap<>();
        
        stats.put(TOTAL_DAMAGE_DEALT, 0. 0);
        stats.put(TOTAL_DAMAGE_TAKEN, 0.0);
        stats.put(TOTAL_KILLS, 0);
        stats.put(TOTAL_DEATHS, 0);
        stats. put(MAX_COMBO, 0);
        stats.put(TOTAL_COMBOS, 0);
        stats.put(TOTAL_CRITS, 0);
        stats.put(CRIT_DAMAGE, 0.0);
        stats. put(TIMES_DODGED, 0);
        
        combatStats.put(uuid, stats);
        return stats;
    }
    
    /**
     * 플레이어 통계 조회 (없으면 생성)
     * @param player 플레이어
     * @return 통계 맵
     */
    private Map<String, Object> getStats(Player player) {
        UUID uuid = player.getUniqueId();
        return combatStats.computeIfAbsent(uuid, k -> initializeStats(player));
    }
    
    /**
     * 누적 피해량 추가
     * @param player 플레이어
     * @param damage 피해량
     */
    public void addDamageDealt(Player player, double damage) {
        Map<String, Object> stats = getStats(player);
        double current = (double) stats.getOrDefault(TOTAL_DAMAGE_DEALT, 0.0);
        stats.put(TOTAL_DAMAGE_DEALT, current + damage);
    }
    
    /**
     * 누적 피해량 조회
     * @param player 플레이어
     * @return 누적 피해량
     */
    public double getTotalDamageDealt(Player player) {
        Map<String, Object> stats = getStats(player);
        return (double) stats.getOrDefault(TOTAL_DAMAGE_DEALT, 0.0);
    }
    
    /**
     * 누적 피해입은량 추가
     * @param player 플레이어
     * @param damage 피해량
     */
    public void addDamageTaken(Player player, double damage) {
        Map<String, Object> stats = getStats(player);
        double current = (double) stats.getOrDefault(TOTAL_DAMAGE_TAKEN, 0.0);
        stats.put(TOTAL_DAMAGE_TAKEN, current + damage);
    }
    
    /**
     * 누적 피해입은량 조회
     * @param player 플레이어
     * @return 누적 피해입은량
     */
    public double getTotalDamageTaken(Player player) {
        Map<String, Object> stats = getStats(player);
        return (double) stats.getOrDefault(TOTAL_DAMAGE_TAKEN, 0.0);
    }
    
    /**
     * 처치 추가
     * @param player 플레이어
     */
    public void addKill(Player player) {
        Map<String, Object> stats = getStats(player);
        int current = (int) stats.getOrDefault(TOTAL_KILLS, 0);
        stats. put(TOTAL_KILLS, current + 1);
    }
    
    /**
     * 총 처치 수 조회
     * @param player 플레이어
     * @return 처치 수
     */
    public int getTotalKills(Player player) {
        Map<String, Object> stats = getStats(player);
        return (int) stats.getOrDefault(TOTAL_KILLS, 0);
    }
    
    /**
     * 사망 추가
     * @param player 플레이어
     */
    public void addDeath(Player player) {
        Map<String, Object> stats = getStats(player);
        int current = (int) stats.getOrDefault(TOTAL_DEATHS, 0);
        stats. put(TOTAL_DEATHS, current + 1);
    }
    
    /**
     * 총 사망 수 조회
     * @param player 플레이어
     * @return 사망 수
     */
    public int getTotalDeaths(Player player) {
        Map<String, Object> stats = getStats(player);
        return (int) stats.getOrDefault(TOTAL_DEATHS, 0);
    }
    
    /**
     * 최대 콤보 설정
     * @param player 플레이어
     * @param combo 콤보 수
     */
    public void setMaxCombo(Player player, int combo) {
        Map<String, Object> stats = getStats(player);
        int current = (int) stats.getOrDefault(MAX_COMBO, 0);
        if (combo > current) {
            stats.put(MAX_COMBO, combo);
        }
    }
    
    /**
     * 최대 콤보 조회
     * @param player 플레이어
     * @return 최대 콤보
     */
    public int getMaxCombo(Player player) {
        Map<String, Object> stats = getStats(player);
        return (int) stats.getOrDefault(MAX_COMBO, 0);
    }
    
    /**
     * 총 콤보 횟수 추가
     * @param player 플레이어
     */
    public void addComboCount(Player player) {
        Map<String, Object> stats = getStats(player);
        int current = (int) stats.getOrDefault(TOTAL_COMBOS, 0);
        stats.put(TOTAL_COMBOS, current + 1);
    }
    
    /**
     * 총 콤보 횟수 조회
     * @param player 플레이어
     * @return 총 콤보 횟수
     */
    public int getTotalCombos(Player player) {
        Map<String, Object> stats = getStats(player);
        return (int) stats.getOrDefault(TOTAL_COMBOS, 0);
    }
    
    /**
     * 크리티컬 히트 추가
     * @param player 플레이어
     * @param damage 크리티컬 데미지
     */
    public void addCriticalHit(Player player, double damage) {
        Map<String, Object> stats = getStats(player);
        
        int crits = (int) stats.getOrDefault(TOTAL_CRITS, 0);
        stats.put(TOTAL_CRITS, crits + 1);
        
        double critDamage = (double) stats.getOrDefault(CRIT_DAMAGE, 0.0);
        stats.put(CRIT_DAMAGE, critDamage + damage);
    }
    
    /**
     * 총 크리티컬 히트 수 조회
     * @param player 플레이어
     * @return 크리티컬 히트 수
     */
    public int getTotalCrits(Player player) {
        Map<String, Object> stats = getStats(player);
        return (int) stats.getOrDefault(TOTAL_CRITS, 0);
    }
    
    /**
     * 총 크리티컬 데미지 조회
     * @param player 플레이어
     * @return 크리티컬 데미지
     */
    public double getTotalCritDamage(Player player) {
        Map<String, Object> stats = getStats(player);
        return (double) stats.getOrDefault(CRIT_DAMAGE, 0. 0);
    }
    
    /**
     * 회피 추가
     * @param player 플레이어
     */
    public void addDodge(Player player) {
        Map<String, Object> stats = getStats(player);
        int current = (int) stats.getOrDefault(TIMES_DODGED, 0);
        stats.put(TIMES_DODGED, current + 1);
    }
    
    /**
     * 총 회피 수 조회
     * @param player 플레이어
     * @return 회피 수
     */
    public int getTotalDodges(Player player) {
        Map<String, Object> stats = getStats(player);
        return (int) stats.getOrDefault(TIMES_DODGED, 0);
    }
    
    /**
     * 플레이어 통계 초기화
     * @param player 플레이어
     */
    public void resetStats(Player player) {
        UUID uuid = player.getUniqueId();
        combatStats.remove(uuid);
        initializeStats(player);
    }
    
    /**
     * 모든 통계 조회
     * @param player 플레이어
     * @return 통계 맵
     */
    public Map<String, Object> getAllStats(Player player) {
        return new HashMap<>(getStats(player));
    }
    
    /**
     * 플레이어 데이터 저장
     * @param player 플레이어
     */
    public void savePlayerData(Player player) {
        Map<String, Object> stats = getStats(player);
        plugin.getDataManager().saveCombatStats(player, stats);
    }
    
    /**
     * 플레이어 데이터 로드
     * @param player 플레이어
     */
    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        Map<String, Object> stats = plugin.getDataManager().loadCombatStats(player);
        combatStats.put(uuid, stats);
    }
}