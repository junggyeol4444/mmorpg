package com.multiverse.npcai.managers;

import com.multiverse.npcai.NPCAICore;
import com.multiverse.npcai.models.Reputation;
import com.multiverse.npcai.models.enums.ReputationLevel;
import com.multiverse.npcai.utils.ConfigUtil;
import com.multiverse.npcai.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * NPC 호감도 관리, 변화, 효과 적용
 */
public class ReputationManager {

    private final NPCAICore plugin;
    private final DataManager dataManager;
    private final ConfigUtil config;

    public ReputationManager(NPCAICore plugin, DataManager dataManager, ConfigUtil config) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.config = config;
    }

    // === 호감도 조회 ===
    public Reputation getReputation(Player player, int npcId) {
        return dataManager.getReputation(player.getUniqueId(), npcId);
    }

    public int getPoints(Player player, int npcId) {
        Reputation rep = getReputation(player, npcId);
        return rep != null ? rep.getPoints() : config.getInt("reputation.initial-points", 0);
    }

    public ReputationLevel getLevel(Player player, int npcId) {
        return ReputationLevel.fromPoints(getPoints(player, npcId));
    }

    public List<Reputation> getAllReputations(Player player) {
        return dataManager.getAllReputations(player.getUniqueId());
    }

    // === 호감도 변경 ===
    public void setPoints(Player player, int npcId, int points) {
        Reputation rep = getReputation(player, npcId);
        int min = config.getInt("reputation.min-points", -100);
        int max = config.getInt("reputation.max-points", 100);
        int clamped = Math.max(min, Math.min(max, points));
        if (rep == null) rep = new Reputation(player.getUniqueId(), npcId, clamped, ReputationLevel.fromPoints(clamped), System.currentTimeMillis(), 0, 0, 0);
        else rep.setPoints(clamped);
        rep.setLevel(ReputationLevel.fromPoints(clamped));
        rep.setLastInteraction(System.currentTimeMillis());
        dataManager.saveReputation(rep);
        // ReputationChangeEvent 발행
        // plugin.getServer().getPluginManager().callEvent(new ReputationChangeEvent(...));
    }

    public void addPoints(Player player, int npcId, int delta, String reason) {
        Reputation rep = getReputation(player, npcId);
        int before = rep != null ? rep.getPoints() : config.getInt("reputation.initial-points", 0);
        int min = config.getInt("reputation.min-points", -100);
        int max = config.getInt("reputation.max-points", 100);
        int after = Math.max(min, Math.min(max, before + delta));
        if (rep == null) rep = new Reputation(player.getUniqueId(), npcId, after, ReputationLevel.fromPoints(after), System.currentTimeMillis(), 1, 0, 0);
        else {
            rep.setPoints(after);
            rep.setTotalInteractions(rep.getTotalInteractions() + 1);
            rep.setLastInteraction(System.currentTimeMillis());
        }
        ReputationLevel oldLevel = ReputationLevel.fromPoints(before);
        ReputationLevel newLevel = ReputationLevel.fromPoints(after);
        rep.setLevel(newLevel);
        dataManager.saveReputation(rep);
        // ReputationChangeEvent 호출
        // plugin.getServer().getPluginManager().callEvent(new ReputationChangeEvent(player, npcId, before, after, reason));

        // 메시지 처리, 레벨 변화 알림
        if (oldLevel != newLevel) {
            String up = config.getString("messages.reputation.level-up");
            String down = config.getString("messages.reputation.level-down");
            String msg = (newLevel.ordinal() > oldLevel.ordinal()) ? up : down;
            player.sendMessage(msg.replace("{npc}", String.valueOf(npcId)).replace("{level}", newLevel.name()));
        }
    }

    public void removePoints(Player player, int npcId, int delta, String reason) {
        addPoints(player, npcId, -delta, reason);
    }

    // === 효과 계산 ===
    public double getPriceMultiplier(Player player, int npcId) {
        ReputationLevel level = getLevel(player, npcId);
        String path = "reputation.effects.price-multipliers." + level.name();
        return config.getDouble(path, 1.0);
    }

    public boolean canAccessSpecialShop(Player player, int npcId) {
        return getLevel(player, npcId).ordinal() >= ReputationLevel.INTIMATE.ordinal();
    }

    public boolean canReceiveSpecialQuest(Player player, int npcId) {
        return getLevel(player, npcId).ordinal() >= ReputationLevel.FRIENDLY.ordinal();
    }

    // === 선물에 따른 호감도 값 계산 ===
    public int calcGiftReputation(ItemStack item) {
        if (item == null || item.getType().isAir()) return 0;
        // 간단 예시: 아이템 등급에 따름
        String mat = item.getType().name().toLowerCase();
        if (mat.contains("diamond") || mat.contains("netherite")) return config.getInt("reputation.changes.gift-epic", 20);
        if (mat.contains("gold")) return config.getInt("reputation.changes.gift-rare", 10);
        if (mat.contains("iron")) return config.getInt("reputation.changes.gift-common", 5);
        return config.getInt("reputation.changes.gift-common", 5);
    }

    // === 감쇠 ===
    public void decayReputation(UUID playerUUID) {
        List<Reputation> reps = dataManager.getAllReputations(playerUUID);
        int decayPerDay = config.getInt("reputation.decay.decay-per-day", -1);
        for (Reputation r : reps) {
            r.setPoints(r.getPoints() + decayPerDay);
            r.setLevel(ReputationLevel.fromPoints(r.getPoints()));
            dataManager.saveReputation(r);
        }
    }
}