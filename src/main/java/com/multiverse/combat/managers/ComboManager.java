package com.multiverse.combat. managers;

import org.bukkit.entity.Player;
import org.bukkit. Bukkit;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models.ComboData;
import java.util.*;

/**
 * 콤보 관리 클래스
 * 플레이어의 콤보 카운트, 보너스, 타임아웃을 관리합니다.
 */
public class ComboManager {
    
    private final CombatCore plugin;
    private final Map<UUID, ComboData> playerCombos = new HashMap<>();
    
    // 콤보 보너스 테이블
    private static final Map<Integer, Double> COMBO_BONUSES = new LinkedHashMap<>();
    static {
        COMBO_BONUSES.put(5, 1.1);    // +10%
        COMBO_BONUSES.put(10, 1. 2);   // +20%
        COMBO_BONUSES.put(20, 1.3);   // +30%
        COMBO_BONUSES.put(50, 1.5);   // +50%
    }
    
    // 설정값
    private static final long COMBO_TIMEOUT = 3000;  // 3초
    private static final int COMBO_REDUCTION_ON_HIT = 50;  // 50%
    
    /**
     * ComboManager 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public ComboManager(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어의 콤보 데이터 조회
     * @param player 플레이어
     * @return 콤보 데이터
     */
    public ComboData getCombo(Player player) {
        UUID uuid = player.getUniqueId();
        return playerCombos.computeIfAbsent(uuid, k -> new ComboData(uuid));
    }
    
    /**
     * 플레이어의 현재 콤보 카운트 조회
     * @param player 플레이어
     * @return 콤보 카운트
     */
    public int getComboCount(Player player) {
        return getCombo(player).getComboCount();
    }
    
    /**
     * 콤보 증가
     * @param player 플레이어
     * @param amount 증가량
     */
    public void addCombo(Player player, int amount) {
        ComboData combo = getCombo(player);
        int previousCombo = combo.getComboCount();
        int newCombo = previousCombo + amount;
        
        combo.setComboCount(newCombo);
        combo.setLastHitTime(System.currentTimeMillis());
        
        // 콤보 마일스톤 체크
        checkComboMilestone(player, previousCombo, newCombo);
    }
    
    /**
     * 콤보 초기화
     * @param player 플레이어
     */
    public void resetCombo(Player player) {
        ComboData combo = getCombo(player);
        combo. setComboCount(0);
        combo.setComboSequence(new ArrayList<>());
    }
    
    /**
     * 콤보 감소
     * @param player 플레이어
     * @param amount 감소량
     */
    public void reduceCombo(Player player, int amount) {
        ComboData combo = getCombo(player);
        int newCombo = Math.max(0, combo.getComboCount() - amount);
        combo.setComboCount(newCombo);
    }
    
    /**
     * 콤보 보너스 계산
     * @param player 플레이어
     * @return 데미지 배수 (1. 0 = 기본)
     */
    public double getComboBonus(Player player) {
        int comboCount = getComboCount(player);
        
        // 높은 콤보부터 확인
        for (Map.Entry<Integer, Double> entry : COMBO_BONUSES.entrySet()) {
            if (comboCount >= entry.getKey()) {
                return entry.getValue();
            }
        }
        
        return 1.0;  // 보너스 없음
    }
    
    /**
     * 특정 콤보 스킬 사용 가능 여부
     * @param player 플레이어
     * @param requiredCombo 필요한 콤보 수
     * @return 사용 가능하면 true
     */
    public boolean canUseComboSkill(Player player, int requiredCombo) {
        return getComboCount(player) >= requiredCombo;
    }
    
    /**
     * 콤보 타임아웃 체크
     * @param player 플레이어
     */
    public void checkComboTimeout(Player player) {
        ComboData combo = getCombo(player);
        long timeSinceLastHit = System.currentTimeMillis() - combo.getLastHitTime();
        
        if (timeSinceLastHit > COMBO_TIMEOUT && combo.getComboCount() > 0) {
            // 콤보 종료
            int finalCombo = combo.getComboCount();
            resetCombo(player);
            
            // 플레이어에게 콤보 끝남 알림
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") + 
                "§c콤보가 끊겼습니다. (총 §e" + finalCombo + "§c콤보)");
        }
    }
    
    /**
     * 플레이어 피격 시 콤보 감소
     * @param player 플레이어
     */
    public void onPlayerDamaged(Player player) {
        ComboData combo = getCombo(player);
        int reduction = (int) (combo.getComboCount() * COMBO_REDUCTION_ON_HIT / 100.0);
        reduceCombo(player, reduction);
    }
    
    /**
     * 플레이어 회피 시 콤보 유지
     * @param player 플레이어
     */
    public void onPlayerDodged(Player player) {
        ComboData combo = getCombo(player);
        combo.setLastHitTime(System.currentTimeMillis());
    }
    
    /**
     * 콤보 마일스톤 체크 및 이벤트 발생
     */
    private void checkComboMilestone(Player player, int previousCombo, int newCombo) {
        for (Integer milestone : COMBO_BONUSES.keySet()) {
            if (previousCombo < milestone && newCombo >= milestone) {
                // 마일스톤 달성
                double bonus = COMBO_BONUSES.get(milestone);
                String message = plugin.getCombatConfig().getString("messages.combo", "§e{combo} 콤보!")
                    .replace("{combo}", String.valueOf(milestone));
                
                player.sendMessage(plugin. getCombatConfig().getString("messages.prefix", "[전투] ") + message);
                
                // 파티클 효과 (선택)
                if (plugin.getCombatConfig().getBoolean("particles.enabled", true)) {
                    // 파티클 효과 재생
                }
                
                // 커스텀 이벤트 발생
                Bukkit.getPluginManager().callEvent(
                    new com.multiverse.combat.events.ComboMilestoneEvent(player, milestone, bonus)
                );
            }
        }
    }
    
    /**
     * 플레이어 데이터 저장
     * @param player 플레이어
     */
    public void savePlayerData(Player player) {
        ComboData combo = getCombo(player);
        plugin.getDataManager().saveComboData(player, combo);
    }
    
    /**
     * 플레이어 데이터 로드
     * @param player 플레이어
     */
    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        ComboData combo = plugin.getDataManager().loadComboData(player);
        playerCombos.put(uuid, combo);
    }
    
    /**
     * 모든 플레이어 콤보 타임아웃 체크
     */
    public void checkAllComboTimeouts() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            checkComboTimeout(player);
        }
    }
}