package com.multiverse.death.managers;

import com.multiverse.death.DeathAndRebirthCore;
import com.multiverse.death.data.DataManager;
import com.multiverse.death.models.Insurance;
import com.multiverse.death.models.enums.InsuranceType;
import com.multiverse.death.models.enums.TransactionType;
import com.multiverse.death.managers.SoulCoinManager;
import org.bukkit.entity.Player;

public class InsuranceManager {
    private final DeathAndRebirthCore plugin;
    private final DataManager dataManager;
    private final SoulCoinManager soulCoinManager;

    public InsuranceManager(DeathAndRebirthCore plugin, DataManager dataManager, SoulCoinManager soulCoinManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.soulCoinManager = soulCoinManager;
    }

    // 보험 관리
    public Insurance getInsurance(Player player) {
        return dataManager.getPlayerInsurance(player);
    }

    public boolean hasActiveInsurance(Player player) {
        Insurance insurance = getInsurance(player);
        return insurance != null && insurance.isActive() && !isExpired(insurance);
    }

    public boolean hasInsuranceType(Player player, InsuranceType type) {
        Insurance insurance = getInsurance(player);
        return insurance != null && insurance.getType() == type && insurance.isActive();
    }

    // 보험 구매 가능 여부
    public boolean canPurchaseInsurance(Player player, InsuranceType type) {
        if (hasActiveInsurance(player)) return false;
        double cost = getInsuranceCost(type);
        return soulCoinManager.hasEnough(player, cost);
    }

    // 보험 구매
    public void purchaseInsurance(Player player, InsuranceType type) {
        double cost = getInsuranceCost(type);
        int duration = getInsuranceDuration(type);
        if (!canPurchaseInsurance(player, type)) return;
        soulCoinManager.removeBalance(player, cost, "보험 구매");
        double burnPercent = plugin.getConfig().getDouble("insurance.burn-percentage", 20.0)/100.0;
        soulCoinManager.burnCoins(cost * burnPercent, "보험 구매 소각");

        Insurance insurance = new Insurance();
        insurance.setPlayerUUID(player.getUniqueId());
        insurance.setType(type);
        insurance.setPurchaseDate(System.currentTimeMillis());
        insurance.setExpiryDate(System.currentTimeMillis() + duration * 24L * 60L * 60L * 1000L);
        insurance.setRemainingUses(type == InsuranceType.PLATINUM ? Integer.MAX_VALUE : 999);
        insurance.setActive(true);

        dataManager.savePlayerInsurance(player, insurance);
    }

    // 관리자 지급
    public void giveInsurance(Player player, InsuranceType type) {
        Insurance insurance = new Insurance();
        int duration = getInsuranceDuration(type);
        insurance.setPlayerUUID(player.getUniqueId());
        insurance.setType(type);
        insurance.setPurchaseDate(System.currentTimeMillis());
        insurance.setExpiryDate(System.currentTimeMillis() + (duration * 24L * 60L * 60L * 1000L));
        insurance.setRemainingUses(type == InsuranceType.PLATINUM ? Integer.MAX_VALUE : 999);
        insurance.setActive(true);
        dataManager.savePlayerInsurance(player, insurance);
    }

    public int getInsuranceCost(InsuranceType type) {
        switch(type) {
            case BASIC: return plugin.getConfig().getInt("insurance.types.basic.cost", 50000);
            case PREMIUM: return plugin.getConfig().getInt("insurance.types.premium.cost", 200000);
            case PLATINUM: return plugin.getConfig().getInt("insurance.types.platinum.cost", 500000);
            default: return 0;
        }
    }

    public int getInsuranceDuration(InsuranceType type) {
        switch(type) {
            case BASIC: return plugin.getConfig().getInt("insurance.types.basic.duration-days", 7);
            case PREMIUM: return plugin.getConfig().getInt("insurance.types.premium.duration-days", 30);
            case PLATINUM: return plugin.getConfig().getInt("insurance.types.platinum.duration-days", 90);
            default: return 0;
        }
    }

    // 보험 사용
    public void useInsurance(Player player) {
        Insurance insurance = getInsurance(player);
        if (insurance == null || !insurance.isActive()) return;
        if (insurance.getType() == InsuranceType.PLATINUM) return; // 무제한
        insurance.setRemainingUses(insurance.getRemainingUses() - 1);
        if (insurance.getRemainingUses() <= 0) insurance.setActive(false);
        dataManager.savePlayerInsurance(player, insurance);
    }

    public void applyInsuranceBenefits(Player player, Insurance insurance) {
        // 보험 혜택: DeathManager에서 적용
    }

    // 보험 만료
    public boolean isExpired(Insurance insurance) {
        return insurance.getExpiryDate() <= System.currentTimeMillis();
    }

    public void checkExpiredInsurances() {
        for(Player player : dataManager.getAllOnlinePlayers()) {
            Insurance insurance = getInsurance(player);
            if (insurance != null && isExpired(insurance)) {
                insurance.setActive(false);
                dataManager.savePlayerInsurance(player, insurance);
                player.sendMessage("&c보험이 만료되었습니다.");
            } else if (insurance != null && insurance.isActive()) {
                long left = insurance.getExpiryDate() - System.currentTimeMillis();
                if (left <= plugin.getConfig().getInt("insurance.expiry.notify-before-days", 3) * 24L * 60L * 60L * 1000L) {
                    int daysLeft = (int) Math.ceil(left / 1000.0 / 60 / 60 / 24);
                    player.sendMessage("&e보험이 " + daysLeft + "일 후 만료됩니다.");
                }
            }
        }
    }
}