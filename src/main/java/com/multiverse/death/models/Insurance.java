package com.multiverse.death.models;

import com.multiverse.death.models.enums.InsuranceType;
import java.util.UUID;

/**
 * 보험 정보를 저장하는 모델 클래스
 */
public class Insurance {
    private UUID playerUUID;
    private InsuranceType type;
    private long purchaseTime;
    private long expiryTime;
    private boolean active;

    public UUID getPlayerUUID() {
        return playerUUID;
    }
    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public InsuranceType getType() {
        return type;
    }
    public void setType(InsuranceType type) {
        this.type = type;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }
    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public long getExpiryTime() {
        return expiryTime;
    }
    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}