package com.multiverse.economy.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import com.multiverse.economy.models.PlayerBalance;
import com.multiverse.economy.models.BankAccount;
import com.multiverse.economy.models.ExchangeRate;
import com.multiverse.economy.models.Currency;
import com.multiverse.economy.models.EconomyStatistics;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MessageUtil {

    public static ItemStack createBalanceItem(PlayerBalance balance) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Balance: " + balance.getBalance());
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Currency: " + balance.getCurrencyId(),
                ChatColor.GRAY + "Owner: " + balance.getPlayerId().toString()
        ));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createBankAccountItem(BankAccount account) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Bank Account: " + account.getAccountId());
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Owner: " + account.getOwnerId().toString(),
                ChatColor.GRAY + "Balance: " + account.getBalance()
        ));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createExchangeRateItem(ExchangeRate rate, List<Currency> currencies) {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        Currency from = currencies.stream().filter(c -> c.getId().equals(rate.getFromCurrency())).findFirst().orElse(null);
        Currency to = currencies.stream().filter(c -> c.getId().equals(rate.getToCurrency())).findFirst().orElse(null);
        String fromName = from != null ? from.getName() : rate.getFromCurrency();
        String toName = to != null ? to.getName() : rate.getToCurrency();
        meta.setDisplayName(ChatColor.BLUE + "Rate: " + fromName + " → " + toName);
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Rate: " + rate.getRate()
        ));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createStatisticsItem(EconomyStatistics statistics) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Economy Stats");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Total Balance: " + statistics.getTotalBalance(),
                ChatColor.GRAY + "Total Loans: " + statistics.getTotalLoans(),
                ChatColor.GRAY + "Total Burned: " + statistics.getTotalBurned(),
                ChatColor.GRAY + "Transactions: " + statistics.getTransactionCount(),
                ChatColor.GRAY + "Inflation Rate: " + statistics.getInflationRate()
        ));
        item.setItemMeta(meta);
        return item;
    }

    public static void notifyOverdueLoan(UUID playerId) {
        // Placeholder: Actual notification logic for overdue loans
    }
}