package com.multiverse.combat. models;

import java.util. UUID;

/**
 * PvP 데이터 클래스
 * 플레이어의 PvP 통계를 저장합니다.
 */
public class PvPData {
    
    private UUID playerUUID;
    private String playerName;
    
    private boolean pvpEnabled;
    
    // 전적
    private int kills;
    private int deaths;
    private double kda;
    
    // 연속 처치
    private int killStreak;
    private int maxKillStreak;
    
    // 명성
    private int fame;
    private int infamy;
    
    // 랭킹
    private int rank;
    
    /**
     * PvPData 생성자
     * @param playerUUID 플레이어 UUID
     * @param playerName 플레이어 이름
     */
    public PvPData(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this. pvpEnabled = true;
        this.kills = 0;
        this.deaths = 0;
        this.kda = 0.0;
        this. killStreak = 0;
        this.maxKillStreak = 0;
        this. fame = 0;
        this.infamy = 0;
        this.rank = 0;
    }
    
    // ===== Getter & Setter =====
    
    public UUID getPlayerUUID() { return playerUUID; }
    public void setPlayerUUID(UUID playerUUID) { this.playerUUID = playerUUID; }
    
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public boolean isPvPEnabled() { return pvpEnabled; }
    public void setPvPEnabled(boolean pvpEnabled) { this.pvpEnabled = pvpEnabled; }
    
    public int getKills() { return kills; }
    public void setKills(int kills) { 
        this.kills = Math. max(0, kills);
        calculateKDA();
    }
    
    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { 
        this. deaths = Math.max(0, deaths);
        calculateKDA();
    }
    
    public double getKDA() { return kda; }
    
    public int getKillStreak() { return killStreak; }
    public void setKillStreak(int killStreak) { this.killStreak = Math.max(0, killStreak); }
    
    public int getMaxKillStreak() { return maxKillStreak; }
    public void setMaxKillStreak(int maxKillStreak) { this.maxKillStreak = Math.max(0, maxKillStreak); }
    
    public int getFame() { return fame; }
    public void setFame(int fame) { this.fame = Math.max(0, fame); }
    
    public int getInfamy() { return infamy; }
    public void setInfamy(int infamy) { this.infamy = Math.max(0, infamy); }
    
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = Math.max(0, rank); }
    
    // ===== 추가 메서드 =====
    
    /**
     * 처치 추가
     */
    public void addKill() {
        this.kills++;
        this.killStreak++;
        if (this.killStreak > this.maxKillStreak) {
            this.maxKillStreak = this.killStreak;
        }
        calculateKDA();
    }
    
    /**
     * 사망 추가
     */
    public void addDeath() {
        this.deaths++;
        calculateKDA();
    }
    
    /**
     * 연속 처치 증가
     */
    public void incrementKillStreak() {
        this.killStreak++;
        if (this.killStreak > this.maxKillStreak) {
            this.maxKillStreak = this.killStreak;
        }
    }
    
    /**
     * 연속 처치 초기화
     */
    public void resetKillStreak() {
        this.killStreak = 0;
    }
    
    /**
     * 명성 추가
     */
    public void addFame(int amount) {
        this.fame = Math.max(0, this. fame + amount);
    }
    
    /**
     * 악명 추가
     */
    public void addInfamy(int amount) {
        this.infamy = Math.max(0, this.infamy + amount);
    }
    
    /**
     * KDA 계산
     */
    private void calculateKDA() {
        if (this.deaths == 0) {
            this. kda = this.kills;
        } else {
            this.kda = (double) this.kills / this.deaths;
        }
    }
}