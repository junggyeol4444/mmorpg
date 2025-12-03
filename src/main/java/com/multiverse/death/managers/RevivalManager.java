package com.multiverse.death.managers;

import com.multiverse.death.DeathAndRebirthCore;
import com.multiverse.death.data.DataManager;
import com.multiverse.death.managers.*;
import com.multiverse.death.models.*;
import com.multiverse.death.models.enums.*;
import com.multiverse.death.utils.ConfigUtil;
import com.multiverse.death.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RevivalManager {

    private final DeathAndRebirthCore plugin;
    private final DataManager dataManager;
    private final DeathManager deathManager;
    private final InsuranceManager insuranceManager;
    private final SoulCoinManager soulCoinManager;
    private final NetherRealmManager netherRealmManager;
    private final ConfigUtil configUtil;
    private final MessageUtil messageUtil;

    public RevivalManager(DeathAndRebirthCore plugin, DataManager dataManager, 
                         DeathManager deathManager, InsuranceManager insuranceManager, SoulCoinManager soulCoinManager, 
                         NetherRealmManager netherRealmManager, ConfigUtil configUtil, MessageUtil messageUtil) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.deathManager = deathManager;
        this.insuranceManager = insuranceManager;
        this.soulCoinManager = soulCoinManager;
        this.netherRealmManager = netherRealmManager;
        this.configUtil = configUtil;
        this.messageUtil = messageUtil;
    }

    // ---- 부활 퀘스트 ----

    public RevivalQuest createQuest(Player player) {
        RevivalQuest quest = new RevivalQuest();
        quest.setPlayerUUID(player.getUniqueId());
        quest.setType(selectQuestType());
        quest.setProgress(new HashMap<>());
        quest.setRequired(getQuestRequirements(quest.getType()));
        quest.setStartTime(System.currentTimeMillis());
        quest.setCompleted(false);

        dataManager.saveRevivalQuest(player, quest);
        return quest;
    }

    public RevivalQuest getQuest(Player player) {
        return dataManager.getRevivalQuest(player);
    }

    public boolean hasActiveQuest(Player player) {
        RevivalQuest quest = getQuest(player);
        return quest != null && !quest.isCompleted();
    }

    public void updateQuestProgress(Player player, QuestType type, String key) {
        RevivalQuest quest = getQuest(player);
        if (quest == null || quest.isCompleted()) return;
        Map<String, Integer> progress = quest.getProgress();
        progress.put(key, progress.getOrDefault(key, 0) + 1);
        dataManager.saveRevivalQuest(player, quest);
    }

    public boolean isQuestCompleted(Player player) {
        RevivalQuest quest = getQuest(player);
        if (quest == null) return false;
        for (Map.Entry<String, Integer> req : quest.getRequired().entrySet()) {
            int count = quest.getProgress().getOrDefault(req.getKey(), 0);
            if (count < req.getValue()) return false;
        }
        return true;
    }

    public void completeQuest(Player player) {
        RevivalQuest quest = getQuest(player);
        if (quest == null) return;
        quest.setCompleted(true);
        dataManager.saveRevivalQuest(player, quest);

        DeathRecord lastDeath = deathManager.getLastDeath(player);
        int expRecovery = (int)((lastDeath.getExpLost()) *
            plugin.getConfig().getDouble("revival.quest.rewards.exp-recovery", 50.0) / 100.0);
        player.giveExp(expRecovery);

        revivePlayer(player, false);
    }

    private QuestType selectQuestType() {
        String[] types = plugin.getConfig().getStringList("revival.quest.types").toArray(new String[0]);
        int idx = (int) (Math.random() * types.length);
        return QuestType.valueOf(types[idx]);
    }

    private Map<String, Integer> getQuestRequirements(QuestType type) {
        Map<String, Integer> req = new HashMap<>();
        int required = plugin.getConfig().getInt("revival.quest.requirements." + type.name(), 1);
        req.put(type.name(), required);
        return req;
    }

    // ---- 소울 코인 부활 ----

    public int getRevivalCost(Player player) {
        int base = plugin.getConfig().getInt("revival.soul-coin.base-cost", 10000);
        int perLevel = plugin.getConfig().getInt("revival.soul-coin.cost-per-level", 100);
        int max = plugin.getConfig().getInt("revival.soul-coin.max-cost", 50000);
        int level = player.getLevel();
        return Math.min(base + (level * perLevel), max);
    }

    public boolean canAffordRevival(Player player) {
        int cost = getRevivalCost(player);
        return soulCoinManager.hasEnough(player, cost);
    }

    public void reviveWithSoulCoin(Player player) {
        int cost = getRevivalCost(player);
        if (!canAffordRevival(player)) return;
        soulCoinManager.removeBalance(player, cost, "부활 비용");
        double burnPercent = plugin.getConfig().getDouble("revival.soul-coin.burn-percentage", 30.0)/100.0;
        soulCoinManager.burnCoins(cost * burnPercent, "부활 비용 소각");
        revivePlayer(player, false);
    }

    // ---- 부활 실행 ----

    public void revivePlayer(Player player) { revivePlayer(player, false); }

    public void revivePlayer(Player player, boolean forceAdmin) {
        // 부활 위치 결정
        Location revivalLoc;
        Insurance insurance = insuranceManager.getInsurance(player);
        InsuranceType insType = (insurance != null && insurance.isActive()) ? insurance.getType() : null;
        boolean reviveAtDeath = plugin.getConfig().getBoolean("revival.teleport-to-death-location", true);
        int invTime = plugin.getConfig().getInt("revival.invincibility-duration", 5);

        if (forceAdmin) {
            // 강제 부활: 사망 위치, 무적 5초
            revivalLoc = deathManager.getDeathLocation(player);
            if (revivalLoc == null) revivalLoc = player.getWorld().getSpawnLocation();
            player.teleport(revivalLoc);
            applyInvincibility(player, invTime);
        } else if (insurance != null && insurance.isActive()) {
            switch (insType) {
                case PLATINUM:
                    revivalLoc = deathManager.getDeathLocation(player);
                    player.teleport(revivalLoc);
                    applyInvincibility(player, plugin.getConfig().getInt("insurance.types.platinum.benefits.invincibility-seconds", 5));
                    break;
                case PREMIUM:
                    revivalLoc = plugin.getNetherRealmManager().getLocation(LocationType.SPAWN);
                    player.teleport(revivalLoc);
                    applyInvincibility(player, plugin.getConfig().getInt("insurance.types.premium.benefits.invincibility-seconds", 3));
                    break;
                default:
                    revivalLoc = plugin.getNetherRealmManager().getLocation(LocationType.SPAWN);
                    player.teleport(revivalLoc);
            }
        } else if (reviveAtDeath && deathManager.getDeathLocation(player) != null) {
            revivalLoc = deathManager.getDeathLocation(player);
            player.teleport(revivalLoc);
            applyInvincibility(player, invTime);
        } else {
            revivalLoc = plugin.getNetherRealmManager().getLocation(LocationType.SPAWN);
            player.teleport(revivalLoc);
        }
        // 사망 위치 클리어
        deathManager.clearDeathLocation(player);
        player.sendMessage(messageUtil.g("revival.revived"));
    }

    private void applyInvincibility(Player player, int seconds) {
        // 무적 구현 (간략: 플레이어 metadata로 상태 설정)
        player.setInvulnerable(true);
        player.sendMessage(messageUtil.g("revival.invincible", "time", seconds+""));
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.setInvulnerable(false), 20L*seconds);
    }

    public void reviveWithInsurance(Player player) {
        Insurance insurance = insuranceManager.getInsurance(player);
        if (insurance == null || !insurance.isActive()) return;
        insuranceManager.useInsurance(player);
        revivePlayer(player, false);
    }

    public void teleportToDeathLocation(Player player) {
        Location loc = deathManager.getDeathLocation(player);
        if (loc != null) player.teleport(loc);
    }
}