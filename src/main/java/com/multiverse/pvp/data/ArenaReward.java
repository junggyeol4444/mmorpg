package com.multiverse.pvp. data;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java. util.HashMap;
import java. util.List;
import java.util. Map;

public class ArenaReward {

    // 승리 보상
    private long winExperience;
    private Map<String, Double> winMoney;
    private int winPvpPoints;
    private int winRating;
    private List<ItemStack> winItems;

    // 패배 보상 (위로 보상)
    private long loseExperience;
    private Map<String, Double> loseMoney;
    private int losePvpPoints;
    private int loseRating;

    // 킬 보상
    private long killExperience;
    private Map<String, Double> killMoney;
    private int killPvpPoints;

    // MVP 보상
    private long mvpExperience;
    private Map<String, Double> mvpMoney;
    private int mvpPvpPoints;
    private List<ItemStack> mvpItems;

    // 참가 보상 (기본 보상)
    private long participationExperience;
    private Map<String, Double> participationMoney;
    private int participationPvpPoints;

    // 보너스 조건
    private double winStreakBonus; // 연승 보너스 배율
    private double firstBloodBonus; // 퍼스트 블러드 보너스 배율
    private double shutdownBonus; // 셧다운 보너스 배율

    public ArenaReward() {
        // 승리 보상 초기화
        this.winExperience = 100;
        this.winMoney = new HashMap<>();
        this.winMoney.put("default", 100.0);
        this.winPvpPoints = 10;
        this. winRating = 25;
        this. winItems = new ArrayList<>();

        // 패배 보상 초기화
        this.loseExperience = 25;
        this. loseMoney = new HashMap<>();
        this.loseMoney. put("default", 25.0);
        this.losePvpPoints = 2;
        this. loseRating = -20;

        // 킬 보상 초기화
        this.killExperience = 20;
        this. killMoney = new HashMap<>();
        this.killMoney. put("default", 10.0);
        this.killPvpPoints = 1;

        // MVP 보상 초기화
        this.mvpExperience = 50;
        this. mvpMoney = new HashMap<>();
        this.mvpMoney.put("default", 50.0);
        this.mvpPvpPoints = 5;
        this.mvpItems = new ArrayList<>();

        // 참가 보상 초기화
        this.participationExperience = 10;
        this. participationMoney = new HashMap<>();
        this.participationMoney.put("default", 10.0);
        this.participationPvpPoints = 1;

        // 보너스 초기화
        this.winStreakBonus = 0.1; // 연승당 10%
        this. firstBloodBonus = 0.25; // 25% 추가
        this.shutdownBonus = 0.5; // 50% 추가
    }

    // ==================== Getters ====================

    public long getWinExperience() {
        return winExperience;
    }

    public Map<String, Double> getWinMoney() {
        return winMoney;
    }

    public int getWinPvpPoints() {
        return winPvpPoints;
    }

    public int getWinRating() {
        return winRating;
    }

    public List<ItemStack> getWinItems() {
        return winItems;
    }

    public long getLoseExperience() {
        return loseExperience;
    }

    public Map<String, Double> getLoseMoney() {
        return loseMoney;
    }

    public int getLosePvpPoints() {
        return losePvpPoints;
    }

    public int getLoseRating() {
        return loseRating;
    }

    public long getKillExperience() {
        return killExperience;
    }

    public Map<String, Double> getKillMoney() {
        return killMoney;
    }

    public int getKillPvpPoints() {
        return killPvpPoints;
    }

    public long getMvpExperience() {
        return mvpExperience;
    }

    public Map<String, Double> getMvpMoney() {
        return mvpMoney;
    }

    public int getMvpPvpPoints() {
        return mvpPvpPoints;
    }

    public List<ItemStack> getMvpItems() {
        return mvpItems;
    }

    public long getParticipationExperience() {
        return participationExperience;
    }

    public Map<String, Double> getParticipationMoney() {
        return participationMoney;
    }

    public int getParticipationPvpPoints() {
        return participationPvpPoints;
    }

    public double getWinStreakBonus() {
        return winStreakBonus;
    }

    public double getFirstBloodBonus() {
        return firstBloodBonus;
    }

    public double getShutdownBonus() {
        return shutdownBonus;
    }

    // ==================== Setters ====================

    public void setWinExperience(long winExperience) {
        this.winExperience = winExperience;
    }

    public void setWinMoney(Map<String, Double> winMoney) {
        this.winMoney = winMoney;
    }

    public void setWinPvpPoints(int winPvpPoints) {
        this.winPvpPoints = winPvpPoints;
    }

    public void setWinRating(int winRating) {
        this.winRating = winRating;
    }

    public void setWinItems(List<ItemStack> winItems) {
        this. winItems = winItems;
    }

    public void setLoseExperience(long loseExperience) {
        this.loseExperience = loseExperience;
    }

    public void setLoseMoney(Map<String, Double> loseMoney) {
        this.loseMoney = loseMoney;
    }

    public void setLosePvpPoints(int losePvpPoints) {
        this.losePvpPoints = losePvpPoints;
    }

    public void setLoseRating(int loseRating) {
        this.loseRating = loseRating;
    }

    public void setKillExperience(long killExperience) {
        this.killExperience = killExperience;
    }

    public void setKillMoney(Map<String, Double> killMoney) {
        this.killMoney = killMoney;
    }

    public void setKillPvpPoints(int killPvpPoints) {
        this.killPvpPoints = killPvpPoints;
    }

    public void setMvpExperience(long mvpExperience) {
        this. mvpExperience = mvpExperience;
    }

    public void setMvpMoney(Map<String, Double> mvpMoney) {
        this.mvpMoney = mvpMoney;
    }

    public void setMvpPvpPoints(int mvpPvpPoints) {
        this.mvpPvpPoints = mvpPvpPoints;
    }

    public void setMvpItems(List<ItemStack> mvpItems) {
        this.mvpItems = mvpItems;
    }

    public void setParticipationExperience(long participationExperience) {
        this.participationExperience = participationExperience;
    }

    public void setParticipationMoney(Map<String, Double> participationMoney) {
        this.participationMoney = participationMoney;
    }

    public void setParticipationPvpPoints(int participationPvpPoints) {
        this.participationPvpPoints = participationPvpPoints;
    }

    public void setWinStreakBonus(double winStreakBonus) {
        this.winStreakBonus = winStreakBonus;
    }

    public void setFirstBloodBonus(double firstBloodBonus) {
        this. firstBloodBonus = firstBloodBonus;
    }

    public void setShutdownBonus(double shutdownBonus) {
        this.shutdownBonus = shutdownBonus;
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 승리 보상 생성
     */
    public PvPReward createWinReward(int winStreak, boolean firstBlood, int kills) {
        PvPReward reward = new PvPReward();
        
        // 기본 승리 보상
        reward.setExperience(winExperience);
        for (Map.Entry<String, Double> entry : winMoney. entrySet()) {
            reward.addMoney(entry. getKey(), entry.getValue());
        }
        reward.setPvpPoints(winPvpPoints);
        reward.setRatingChange(winRating);
        reward.addItems(winItems);
        
        // 킬 보상
        long killExp = killExperience * kills;
        reward.addExperience(killExp);
        for (Map.Entry<String, Double> entry :  killMoney.entrySet()) {
            reward.addMoney(entry.getKey(), entry.getValue() * kills);
        }
        reward.addPvpPoints(killPvpPoints * kills);
        
        // 연승 보너스
        if (winStreak > 1) {
            double bonus = 1.0 + (winStreakBonus * (winStreak - 1));
            reward.applyMultiplier(bonus);
            reward.setBonus(true);
            reward.setBonusReason(winStreak + "연승 보너스");
        }
        
        // 퍼스트 블러드 보너스
        if (firstBlood) {
            reward. applyMultiplier(1.0 + firstBloodBonus);
        }
        
        return reward;
    }

    /**
     * 패배 보상 생성
     */
    public PvPReward createLoseReward(int kills) {
        PvPReward reward = new PvPReward();
        
        // 기본 패배 보상
        reward.setExperience(loseExperience);
        for (Map.Entry<String, Double> entry : loseMoney. entrySet()) {
            reward.addMoney(entry. getKey(), entry.getValue());
        }
        reward.setPvpPoints(losePvpPoints);
        reward.setRatingChange(loseRating);
        
        // 킬 보상 (패배해도 킬 보상은 지급)
        long killExp = killExperience * kills;
        reward.addExperience(killExp);
        for (Map.Entry<String, Double> entry : killMoney. entrySet()) {
            reward.addMoney(entry. getKey(), entry.getValue() * kills);
        }
        reward.addPvpPoints(killPvpPoints * kills);
        
        return reward;
    }

    /**
     * MVP 보상 생성
     */
    public PvPReward createMvpReward() {
        PvPReward reward = new PvPReward();
        
        reward.setExperience(mvpExperience);
        for (Map.Entry<String, Double> entry : mvpMoney.entrySet()) {
            reward. addMoney(entry.getKey(), entry.getValue());
        }
        reward. setPvpPoints(mvpPvpPoints);
        reward.addItems(mvpItems);
        reward.setBonus(true);
        reward.setBonusReason("MVP 보너스");
        
        return reward;
    }

    /**
     * 참가 보상 생성
     */
    public PvPReward createParticipationReward() {
        PvPReward reward = new PvPReward();
        
        reward.setExperience(participationExperience);
        for (Map. Entry<String, Double> entry : participationMoney.entrySet()) {
            reward.addMoney(entry.getKey(), entry.getValue());
        }
        reward.setPvpPoints(participationPvpPoints);
        
        return reward;
    }

    /**
     * 킬 보상 생성
     */
    public PvPReward createKillReward(int kills, boolean shutdown) {
        PvPReward reward = new PvPReward();
        
        long exp = killExperience * kills;
        reward.setExperience(exp);
        
        for (Map.Entry<String, Double> entry : killMoney. entrySet()) {
            reward.addMoney(entry. getKey(), entry.getValue() * kills);
        }
        
        reward.setPvpPoints(killPvpPoints * kills);
        
        // 셧다운 보너스
        if (shutdown) {
            reward.applyMultiplier(1.0 + shutdownBonus);
            reward.setBonus(true);
            reward.setBonusReason("셧다운 보너스");
        }
        
        return reward;
    }

    /**
     * 승리 머니 설정 (기본 화폐)
     */
    public void setWinDefaultMoney(double amount) {
        this.winMoney. put("default", amount);
    }

    /**
     * 패배 머니 설정 (기본 화폐)
     */
    public void setLoseDefaultMoney(double amount) {
        this. loseMoney.put("default", amount);
    }

    /**
     * 킬 머니 설정 (기본 화폐)
     */
    public void setKillDefaultMoney(double amount) {
        this.killMoney.put("default", amount);
    }

    /**
     * MVP 머니 설정 (기본 화폐)
     */
    public void setMvpDefaultMoney(double amount) {
        this.mvpMoney.put("default", amount);
    }

    /**
     * 참가 머니 설정 (기본 화폐)
     */
    public void setParticipationDefaultMoney(double amount) {
        this. participationMoney. put("default", amount);
    }

    /**
     * 승리 아이템 추가
     */
    public void addWinItem(ItemStack item) {
        if (item != null) {
            this. winItems.add(item. clone());
        }
    }

    /**
     * MVP 아이템 추가
     */
    public void addMvpItem(ItemStack item) {
        if (item != null) {
            this.mvpItems.add(item.clone());
        }
    }

    /**
     * 배율 적용 (아레나 타입별 보상 조정용)
     */
    public void applyMultiplier(double multiplier) {
        this.winExperience = (long) (this.winExperience * multiplier);
        this.loseExperience = (long) (this.loseExperience * multiplier);
        this.killExperience = (long) (this.killExperience * multiplier);
        this.mvpExperience = (long) (this.mvpExperience * multiplier);
        this.participationExperience = (long) (this.participationExperience * multiplier);
        
        for (String key : winMoney.keySet()) {
            winMoney.put(key, winMoney.get(key) * multiplier);
        }
        for (String key : loseMoney.keySet()) {
            loseMoney.put(key, loseMoney.get(key) * multiplier);
        }
        for (String key : killMoney.keySet()) {
            killMoney.put(key, killMoney.get(key) * multiplier);
        }
        for (String key : mvpMoney.keySet()) {
            mvpMoney. put(key, mvpMoney.get(key) * multiplier);
        }
        for (String key : participationMoney.keySet()) {
            participationMoney.put(key, participationMoney.get(key) * multiplier);
        }
        
        this.winPvpPoints = (int) (this.winPvpPoints * multiplier);
        this.losePvpPoints = (int) (this.losePvpPoints * multiplier);
        this.killPvpPoints = (int) (this.killPvpPoints * multiplier);
        this.mvpPvpPoints = (int) (this.mvpPvpPoints * multiplier);
        this.participationPvpPoints = (int) (this.participationPvpPoints * multiplier);
    }

    /**
     * 복사본 생성
     */
    public ArenaReward clone() {
        ArenaReward clone = new ArenaReward();
        clone.winExperience = this.winExperience;
        clone. winMoney = new HashMap<>(this.winMoney);
        clone.winPvpPoints = this.winPvpPoints;
        clone.winRating = this. winRating;
        clone.winItems = new ArrayList<>();
        for (ItemStack item :  this.winItems) {
            clone. winItems.add(item. clone());
        }
        
        clone.loseExperience = this.loseExperience;
        clone.loseMoney = new HashMap<>(this.loseMoney);
        clone.losePvpPoints = this.losePvpPoints;
        clone.loseRating = this. loseRating;
        
        clone.killExperience = this. killExperience;
        clone.killMoney = new HashMap<>(this.killMoney);
        clone.killPvpPoints = this. killPvpPoints;
        
        clone.mvpExperience = this.mvpExperience;
        clone.mvpMoney = new HashMap<>(this.mvpMoney);
        clone.mvpPvpPoints = this. mvpPvpPoints;
        clone.mvpItems = new ArrayList<>();
        for (ItemStack item : this. mvpItems) {
            clone.mvpItems.add(item.clone());
        }
        
        clone.participationExperience = this.participationExperience;
        clone.participationMoney = new HashMap<>(this.participationMoney);
        clone.participationPvpPoints = this. participationPvpPoints;
        
        clone.winStreakBonus = this.winStreakBonus;
        clone.firstBloodBonus = this. firstBloodBonus;
        clone. shutdownBonus = this.shutdownBonus;
        
        return clone;
    }

    @Override
    public String toString() {
        return "ArenaReward{" +
                "winExp=" + winExperience +
                ", winMoney=" + winMoney. get("default") +
                ", winPoints=" + winPvpPoints +
                ", winRating=" + winRating +
                '}';
    }
}