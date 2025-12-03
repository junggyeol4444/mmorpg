package com.multiverse.death.models;

import com.multiverse.death.models.enums.DeathCause;
import com.multiverse.death.models.enums.InsuranceType;
import org.bukkit.Location;
import java.util.List;
import java.util.UUID;

/**
 * 플레이어의 사망 기록을 나타내는 데이터 모델
 */
public class DeathRecord {
    private UUID playerUUID;
    private long deathTime;
    private String dimension;
    private Location deathLocation;
    private DeathCause cause;
    private int expLost;
    private double moneyLost;
    private List<org.bukkit.inventory.ItemStack> droppedItems;
    private boolean hasInsurance;
    private InsuranceType insuranceType;

    public UUID getPlayerUUID() {
        return playerUUID;
    }
    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public long getDeathTime() {
        return deathTime;
    }
    public void setDeathTime(long deathTime) {
        this.deathTime = deathTime;
    }

    public String getDimension() {
        return dimension;
    }
    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public Location getDeathLocation() {
        return deathLocation;
    }
    public void setDeathLocation(Location deathLocation) {
        this.deathLocation = deathLocation;
    }

    public DeathCause getCause() {
        return cause;
    }
    public void setCause(DeathCause cause) {
        this.cause = cause;
    }

    public int getExpLost() {
        return expLost;
    }
    public void setExpLost(int expLost) {
        this.expLost = expLost;
    }

    public double getMoneyLost() {
        return moneyLost;
    }
    public void setMoneyLost(double moneyLost) {
        this.moneyLost = moneyLost;
    }

    public List<org.bukkit.inventory.ItemStack> getDroppedItems() {
        return droppedItems;
    }
    public void setDroppedItems(List<org.bukkit.inventory.ItemStack> droppedItems) {
        this.droppedItems = droppedItems;
    }

    public boolean isHasInsurance() {
        return hasInsurance;
    }
    public void setHasInsurance(boolean hasInsurance) {
        this.hasInsurance = hasInsurance;
    }

    public InsuranceType getInsuranceType() {
        return insuranceType;
    }
    public void setInsuranceType(InsuranceType insuranceType) {
        this.insuranceType = insuranceType;
    }
}