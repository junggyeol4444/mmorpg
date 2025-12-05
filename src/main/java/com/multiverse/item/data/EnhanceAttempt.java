package com.multiverse.item.  data;

public class EnhanceAttempt {
    
    private String attemptId;
    private String itemId;
    private int beforeLevel;
    private int afterLevel;
    private boolean success;
    private double successRate;
    private long timestamp;
    private String playerId;
    private int cost;
    private String result; // SUCCESS, FAIL, DOWNGRADE
    
    /**
     * 기본 생성자
     */
    public EnhanceAttempt() {
        this.attemptId = java.util.UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 주요 파라미터가 있는 생성자
     */
    public EnhanceAttempt(String itemId, int beforeLevel, int afterLevel, boolean success, double successRate) {
        this.attemptId = java.util.UUID.randomUUID().toString();
        this.itemId = itemId;
        this.beforeLevel = beforeLevel;
        this.afterLevel = afterLevel;
        this.success = success;
        this.successRate = successRate;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getAttemptId() {
        return attemptId;
    }
    
    public void setAttemptId(String attemptId) {
        this. attemptId = attemptId;
    }
    
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public int getBeforeLevel() {
        return beforeLevel;
    }
    
    public void setBeforeLevel(int beforeLevel) {
        this.beforeLevel = beforeLevel;
    }
    
    public int getAfterLevel() {
        return afterLevel;
    }
    
    public void setAfterLevel(int afterLevel) {
        this.afterLevel = afterLevel;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public double getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(double successRate) {
        this.successRate = Math.max(0, Math.min(successRate, 100. 0));
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this. playerId = playerId;
    }
    
    public int getCost() {
        return cost;
    }
    
    public void setCost(int cost) {
        this.cost = Math.max(0, cost);
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    /**
     * 강화 레벨 변화
     */
    public int getLevelChange() {
        return afterLevel - beforeLevel;
    }
    
    /**
     * 강화 시도 결과 설명
     */
    public String getResultDescription() {
        if (success) {
            return "강화 성공 (++" + Math.max(0, getLevelChange()) + ")";
        } else if (getLevelChange() < 0) {
            return "강화 실패 및 다운그레이드 (" + getLevelChange() + ")";
        } else {
            return "강화 실패";
        }
    }
    
    /**
     * 경과 시간 반환 (초 단위)
     */
    public long getElapsedSeconds() {
        return (System.currentTimeMillis() - timestamp) / 1000;
    }
    
    /**
     * 경과 시간 반환 (분 단위)
     */
    public long getElapsedMinutes() {
        return getElapsedSeconds() / 60;
    }
    
    /**
     * 강화 시도 정보 출력
     */
    @Override
    public String toString() {
        return "EnhanceAttempt{" +
                "attemptId='" + attemptId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", beforeLevel=" + beforeLevel +
                ", afterLevel=" + afterLevel +
                ", success=" + success +
                ", successRate=" + successRate +
                ", timestamp=" + timestamp +
                '}';
    }
}