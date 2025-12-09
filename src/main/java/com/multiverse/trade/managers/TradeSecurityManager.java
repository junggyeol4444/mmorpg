package com. multiverse.trade. managers;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.models.PlayerTradeData;
import com.multiverse.trade.models. Trade;
import org.bukkit.entity.Player;

import java.util.*;
import java.util. concurrent.ConcurrentHashMap;
import java.util. stream.Collectors;

public class TradeSecurityManager {

    private final TradeCore plugin;
    private final Map<UUID, Set<UUID>> blacklists = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastTradeTime = new ConcurrentHashMap<>();
    private final Map<UUID, Long> playerFirstJoin = new ConcurrentHashMap<>();
    private final List<TradeReport> reports = Collections.synchronizedList(new ArrayList<>());

    public TradeSecurityManager(TradeCore plugin) {
        this.plugin = plugin;
    }

    public boolean canTrade(Player player) {
        if (player.hasPermission("trade.admin. bypass")) {
            return true;
        }

        if (! meetsLevelRequirement(player)) {
            return false;
        }

        if (hasTradeDelay(player)) {
            return false;
        }

        if (isNewPlayerRestricted(player)) {
            return false;
        }

        return true;
    }

    public boolean meetsLevelRequirement(Player player) {
        if (player.hasPermission("trade.admin.bypass")) {
            return true;
        }

        int minLevel = plugin.getConfig().getInt("direct-trade.restrictions.min-level", 5);
        return player.getLevel() >= minLevel;
    }

    public boolean hasTradeDelay(Player player) {
        if (player.hasPermission("trade.admin. bypass")) {
            return false;
        }

        int cooldown = plugin.getConfig().getInt("direct-trade.restrictions.cooldown", 10);
        if (cooldown <= 0) {
            return false;
        }

        Long lastTrade = lastTradeTime.get(player.getUniqueId());
        if (lastTrade == null) {
            return false;
        }

        long elapsed = System.currentTimeMillis() - lastTrade;
        return elapsed < (cooldown * 1000L);
    }

    public long getTradeDelayRemaining(Player player) {
        int cooldown = plugin. getConfig().getInt("direct-trade. restrictions.cooldown", 10);
        Long lastTrade = lastTradeTime.get(player.getUniqueId());

        if (lastTrade == null) {
            return 0;
        }

        long elapsed = System.currentTimeMillis() - lastTrade;
        long remaining = (cooldown * 1000L) - elapsed;

        return Math.max(0, remaining);
    }

    public void recordTradeTime(Player player) {
        lastTradeTime.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private boolean isNewPlayerRestricted(Player player) {
        if (! plugin.getConfig().getBoolean("security.trade-delay-new-players", true)) {
            return false;
        }

        int delaySeconds = plugin.getConfig().getInt("security.trade-delay-new-players", 3600);
        if (delaySeconds <= 0) {
            return false;
        }

        long firstJoin = player.getFirstPlayed();
        long elapsed = System.currentTimeMillis() - firstJoin;

        return elapsed < (delaySeconds * 1000L);
    }

    public void addToBlacklist(UUID player, UUID target) {
        if (!plugin.getConfig().getBoolean("security. blacklist", true)) {
            return;
        }

        blacklists.computeIfAbsent(player, k -> ConcurrentHashMap. newKeySet()).add(target);

        PlayerTradeData data = plugin.getPlayerTradeDataManager().getPlayerData(player);
        if (data != null) {
            data.getBlacklist().add(target);
            plugin.getPlayerTradeDataManager().savePlayerData(data);
        }
    }

    public void removeFromBlacklist(UUID player, UUID target) {
        Set<UUID> blacklist = blacklists. get(player);
        if (blacklist != null) {
            blacklist.remove(target);
        }

        PlayerTradeData data = plugin.getPlayerTradeDataManager().getPlayerData(player);
        if (data != null) {
            data.getBlacklist().remove(target);
            plugin. getPlayerTradeDataManager().savePlayerData(data);
        }
    }

    public boolean isBlacklisted(UUID player, UUID target) {
        if (!plugin.getConfig().getBoolean("security.blacklist", true)) {
            return false;
        }

        Set<UUID> playerBlacklist = blacklists.get(player);
        Set<UUID> targetBlacklist = blacklists.get(target);

        if (playerBlacklist != null && playerBlacklist.contains(target)) {
            return true;
        }

        if (targetBlacklist != null && targetBlacklist.contains(player)) {
            return true;
        }

        return false;
    }

    public Set<UUID> getBlacklist(UUID player) {
        return blacklists.getOrDefault(player, new HashSet<>());
    }

    public void loadPlayerBlacklist(UUID playerId) {
        PlayerTradeData data = plugin.getPlayerTradeDataManager().getPlayerData(playerId);
        if (data != null && data.getBlacklist() != null) {
            blacklists.put(playerId, ConcurrentHashMap. newKeySet());
            blacklists.get(playerId).addAll(data.getBlacklist());
        }
    }

    public void reportTrade(UUID reporter, UUID reported, String reason) {
        TradeReport report = new TradeReport();
        report.reporter = reporter;
        report.reported = reported;
        report. reason = reason;
        report.timestamp = System. currentTimeMillis();
        report.resolved = false;

        reports.add(report);

        plugin.getLogger().warning("거래 신고:  " + reporter + " -> " + reported + " 사유: " + reason);
    }

    public List<TradeReport> getUnresolvedReports() {
        return reports.stream()
                .filter(r -> !r.resolved)
                .collect(Collectors.toList());
    }

    public void resolveReport(int index) {
        if (index >= 0 && index < reports.size()) {
            reports.get(index).resolved = true;
        }
    }

    public List<Trade> getTradeHistory(Player player, int days) {
        return plugin.getTradeManager().getPlayerTradeHistory(player. getUniqueId(), days);
    }

    public Trade getTradeById(UUID tradeId) {
        return plugin.getTradeManager().getTrade(tradeId);
    }

    public void logTrade(Trade trade) {
        if (! plugin.getConfig().getBoolean("security.trade-logging", true)) {
            return;
        }

        String log = String.format(
            "[거래 로그] ID: %s, 플레이어1: %s, 플레이어2: %s, 상태: %s, 시간: %d",
            trade. getTradeId(),
            trade.getPlayer1(),
            trade. getPlayer2(),
            trade.getStatus(),
            trade. getStartTime()
        );

        plugin.getLogger().info(log);
    }

    public static class TradeReport {
        public UUID reporter;
        public UUID reported;
        public String reason;
        public long timestamp;
        public boolean resolved;
    }
}