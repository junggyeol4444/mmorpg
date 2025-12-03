package com.multiverse.economy.vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import com.multiverse.economy.api.EconomyAPI;

public class VaultEconomyProvider implements Economy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "MultiverseEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return String.format("%.2f", amount);
    }

    @Override
    public String currencyNamePlural() {
        return "Multiverse Currency";
    }

    @Override
    public String currencyNameSingular() {
        return "Multiverse Currency";
    }

    @Override
    public boolean hasAccount(String playerName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        return EconomyAPI.hasAccount(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return EconomyAPI.hasAccount(player.getUniqueId());
    }

    @Override
    public double getBalance(String playerName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        return EconomyAPI.getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return EconomyAPI.getBalance(player.getUniqueId());
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        boolean success = EconomyAPI.withdraw(player.getUniqueId(), amount);
        return new EconomyResponse(amount, getBalance(player), success ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, "");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        boolean success = EconomyAPI.withdraw(player.getUniqueId(), amount);
        return new EconomyResponse(amount, getBalance(player), success ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, "");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        boolean success = EconomyAPI.deposit(player.getUniqueId(), amount);
        return new EconomyResponse(amount, getBalance(player), success ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, "");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        boolean success = EconomyAPI.deposit(player.getUniqueId(), amount);
        return new EconomyResponse(amount, getBalance(player), success ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, "");
    }

    // Bank-related methods and other Vault Economy API methods can be implemented below as needed.
}