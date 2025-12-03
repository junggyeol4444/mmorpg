package com.multiverse.economy.api;

import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyAPI {

    private static final Map<UUID, Double> balances = new ConcurrentHashMap<>();

    public static boolean hasAccount(UUID playerId) {
        return balances.containsKey(playerId);
    }

    public static double getBalance(UUID playerId) {
        return balances.getOrDefault(playerId, 0.0);
    }

    public static boolean withdraw(UUID playerId, double amount) {
        double current = getBalance(playerId);
        if (current >= amount) {
            balances.put(playerId, current - amount);
            return true;
        }
        return false;
    }

    public static boolean deposit(UUID playerId, double amount) {
        double current = getBalance(playerId);
        balances.put(playerId, current + amount);
        return true;
    }

    // Additional API methods can be implemented as needed
}