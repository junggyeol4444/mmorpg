package com. multiverse.combat.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 콤보 데이터 클래스
 * 플레이어의 콤보 정보를 저장합니다.
 */
public class ComboData {
    
    private UUID playerUUID;
    private int comboCount;
    private long lastHitTime;
    private List<String> comboSequence;
    
    // 통계
    private int maxCombo;
    private int totalCombos;
    
    /**
     * ComboData 생성자
     * @param playerUUID 플레이어 UUID
     */
    public ComboData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.comboCount = 0;
        this. lastHitTime = System.currentTimeMillis();
        this. comboSequence = new ArrayList<>();
        this.maxCombo = 0;
        this.totalCombos = 0;
    }
    
    // ===== Getter & Setter =====
    
    public UUID getPlayerUUID() { return playerUUID; }
    public void setPlayerUUID(UUID playerUUID) { this.playerUUID = playerUUID; }
    
    public int getComboCount() { return comboCount; }
    public void setComboCount(int comboCount) { this. comboCount = Math.max(0, comboCount); }
    
    public long getLastHitTime() { return lastHitTime; }
    public void setLastHitTime(long lastHitTime) { this.lastHitTime = lastHitTime; }
    
    public List<String> getComboSequence() { return comboSequence; }
    public void setComboSequence(List<String> comboSequence) { this.comboSequence = comboSequence; }
    
    public int getMaxCombo() { return maxCombo; }
    public void setMaxCombo(int maxCombo) { this.maxCombo = maxCombo; }
    
    public int getTotalCombos() { return totalCombos; }
    public void setTotalCombos(int totalCombos) { this. totalCombos = totalCombos; }
}