package com.multiverse.death.managers;

import com.multiverse.death.DeathAndRebirthCore;
import com.multiverse.death.models.DeathRecord;
import com.multiverse.death.models.enums.DeathCause;
import com.multiverse.death.models.Insurance;
import com.multiverse.death.models.enums.InsuranceType;
import com.multiverse.death.data.DataManager;
import com.multiverse.death.managers.InsuranceManager;
import com.multiverse.death.managers.SoulCoinManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;

public class DeathManager {

    private final DeathAndRebirthCore plugin;
    private final DataManager dataManager;
    private final InsuranceManager insuranceManager;
    private final SoulCoinManager soulCoinManager;

    public DeathManager(DeathAndRebirthCore plugin, DataManager dataManager,
                        InsuranceManager insuranceManager, SoulCoinManager soulCoinManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.insuranceManager = insuranceManager;
        this.soulCoinManager = soulCoinManager;
    }

    // 사망 처리
    public void handlePlayerDeath(Player player, PlayerDeathEvent event) {
        DeathRecord record = new DeathRecord();
        record.setPlayerUUID(player.getUniqueId());
        record.setDeathTime(System.currentTimeMillis());
        record.setDimension(getCurrentDimension(player));
        record.setDeathLocation(player.getLocation());
        record.setCause(detectDeathCause(event, player));

        Insurance insurance = insuranceManager.getInsurance(player);
        boolean hasInsurance = insurance != null && insurance.isActive();
        record.setHasInsurance(hasInsurance);

        if (hasInsurance) {
            record.setInsuranceType(insurance.getType());
            applyInsuranceDeath(record, player, event, insurance);
        } else {
            applyDeathPenalty(player, record, event);
        }

        recordDeath(player, record);
    }

    public void applyDeathPenalty(Player player, DeathRecord record, PlayerDeathEvent event) {
        int expLost = getExpPenalty(player);
        double moneyLost = getMoneyPenalty(player);
        List<org.bukkit.inventory.ItemStack> droppedItems = getItemPenalty(player, event);

        record.setExpLost(expLost);
        record.setMoneyLost(moneyLost);
        record.setDroppedItems(droppedItems);

        if (expLost > 0) event.setDroppedExp(0); // 오브 드롭 X
        if (moneyLost > 0) soulCoinManager.removeBalance(player, moneyLost, "사망 패널티");
    }

    private void applyInsuranceDeath(DeathRecord record, Player player, PlayerDeathEvent event, Insurance insurance) {
        record.setExpLost(0);
        record.setMoneyLost(0);
        record.setDroppedItems(new ArrayList<>());
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    public void teleportToNetherRealm(Player player) {
        plugin.getNetherRealmManager().teleportToSpawn(player);
    }

    // 사망 기록
    public void recordDeath(Player player, DeathRecord record) {
        dataManager.saveDeathRecord(player, record);
    }

    public DeathRecord getLastDeath(Player player) {
        return dataManager.getLastDeathRecord(player);
    }

    public List<DeathRecord> getDeathHistory(Player player, int limit) {
        return dataManager.getDeathHistory(player, limit);
    }

    public int getDeathCount(Player player) {
        return dataManager.getDeathCount(player);
    }

    // 사망 위치 관리
    public Location getDeathLocation(Player player) {
        DeathRecord last = getLastDeath(player);
        return last != null ? last.getDeathLocation() : null;
    }

    public void clearDeathLocation(Player player) {
        dataManager.clearDeathLocation(player);
    }

    // 사망 패널티 계산
    private int getExpPenalty(Player player) {
        double percent = plugin.getConfig().getDouble("death.penalty.experience.percentage", 5.0) / 100.0;
        int totalExp = player.getTotalExperience();
        return (int) (totalExp * percent);
    }

    private double getMoneyPenalty(Player player) {
        double percent = plugin.getConfig().getDouble("death.penalty.money.percentage", 10.0) / 100.0;
        double balance = soulCoinManager.getBalance(player);
        return Math.round(balance * percent);
    }

    private List<org.bukkit.inventory.ItemStack> getItemPenalty(Player player, PlayerDeathEvent event) {
        boolean isSpirit = dataManager.isPlayerSpiritRace(player);
        if (isSpirit) return new ArrayList<>();

        int minDrops = plugin.getConfig().getInt("death.penalty.items.min-drops", 1);
        int maxDrops = plugin.getConfig().getInt("death.penalty.items.max-drops", 3);
        int dropCount = minDrops + new Random().nextInt(maxDrops - minDrops + 1);

        List<org.bukkit.inventory.ItemStack> drops = new ArrayList<>(event.getDrops());
        List<org.bukkit.inventory.ItemStack> result = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < dropCount && !drops.isEmpty(); i++) {
            org.bukkit.inventory.ItemStack item = drops.remove(rand.nextInt(drops.size()));
            result.add(item);
        }
        // remove from main drops
        event.getDrops().removeAll(result);
        return result;
    }

    // 차원 감지 (Multiverse 연동/Native world)
    public String getCurrentDimension(Player player) {
        if (plugin.isMultiverseEnabled()) {
            // MultiverseCore API 연동 부분 여기서 확장 가능
            return player.getWorld().getName();
        } else {
            return player.getWorld().getName();
        }
    }

    private DeathCause detectDeathCause(PlayerDeathEvent event, Player player) {
        // DeathCause 판별 (간략 버전, 로직 확장 가능)
        if (event.getDeathMessage() != null) {
            String msg = event.getDeathMessage().toLowerCase();
            if (msg.contains("fell")) return DeathCause.FALL;
            if (msg.contains("drowned")) return DeathCause.DROWN;
            if (msg.contains("fire") || msg.contains("burn")) return DeathCause.FIRE;
            if (msg.contains("explode")) return DeathCause.EXPLOSION;
            if (msg.contains("magic") || msg.contains("spell")) return DeathCause.MAGIC;
            if (msg.contains("slain by")) return DeathCause.PVP;
            if (msg.contains("killed by")) return DeathCause.PVE;
        }
        return DeathCause.OTHER;
    }
}