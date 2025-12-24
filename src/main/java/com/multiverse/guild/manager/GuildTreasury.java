package com.multiverse.guild.manager;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.Guild;
import com.multiverse.guild.model.Transaction;
import com.multiverse.guild.model.TransactionType;
import com.multiverse.guild.util.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuildTreasury {

    private final GuildCore plugin;
    // In this simplified version, history kept in-memory per guild; persist via guild storage if needed.
    private final Map<UUID, List<Transaction>> historyCache = new java.util.concurrent.ConcurrentHashMap<>();

    public GuildTreasury(GuildCore plugin) {
        this.plugin = plugin;
    }

    public void deposit(Guild guild, String currency, double amount, UUID depositor) {
        guild.getTreasury().merge(currency, amount, Double::sum);
        addHistory(guild, new Transaction(TransactionType.DEPOSIT, currency, amount, depositor, System.currentTimeMillis(), "deposit"));
        plugin.getGuildStorage().save(guild);
    }

    public void withdraw(Guild guild, String currency, double amount, UUID withdrawer) {
        double bal = guild.getTreasury().getOrDefault(currency, 0.0);
        if (bal < amount) return;
        guild.getTreasury().put(currency, bal - amount);
        addHistory(guild, new Transaction(TransactionType.WITHDRAW, currency, amount, withdrawer, System.currentTimeMillis(), "withdraw"));
        plugin.getGuildStorage().save(guild);
    }

    public double getBalance(Guild guild, String currency) {
        return guild.getTreasury().getOrDefault(currency, 0.0);
    }

    public List<Transaction> getHistory(Guild guild, int days) {
        long cutoff = System.currentTimeMillis() - days * 86400000L;
        return historyCache.getOrDefault(guild.getGuildId(), List.of()).stream()
                .filter(t -> t.getTimestamp() >= cutoff)
                .toList();
    }

    public void collectTax(Guild guild, UUID player, double amount) {
        double rate = guild.getSettings().getTaxRate();
        double tax = amount * (rate / 100.0);
        guild.getTreasury().merge("fantasy_gold", tax, Double::sum);
        addHistory(guild, new Transaction(TransactionType.TAX, "fantasy_gold", tax, player, System.currentTimeMillis(), "tax"));
        plugin.getGuildStorage().save(guild);
    }

    public void paySalaries(Guild guild) {
        if (!plugin.getConfig().getBoolean("treasury.salary.enabled", true)) return;
        for (GuildRank rank : guild.getRanks().values()) {
            double salary = rank.getDailySalary();
            if (salary <= 0) continue;
            guild.getMembers().values().stream()
                    .filter(m -> rank.getRankName().equalsIgnoreCase(m.getRankName()))
                    .forEach(m -> {
                        double bal = guild.getTreasury().getOrDefault("fantasy_gold", 0.0);
                        if (bal >= salary) {
                            guild.getTreasury().put("fantasy_gold", bal - salary);
                            plugin.getEconomy().depositPlayer(plugin.getServer().getOfflinePlayer(m.getPlayerId()), salary);
                            addHistory(guild, new Transaction(TransactionType.SALARY, "fantasy_gold", salary, m.getPlayerId(), System.currentTimeMillis(), "salary"));
                        }
                    });
        }
        plugin.getGuildStorage().save(guild);
    }

    private void addHistory(Guild guild, Transaction tx) {
        historyCache.computeIfAbsent(guild.getGuildId(), k -> new ArrayList<>()).add(tx);
    }
}