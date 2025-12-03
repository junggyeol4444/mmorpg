package com.multiverse.economy.data.storage;

import com.multiverse.economy.models.BankAccount;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankStorage {

    // 플레이어별/화폐별 은행계좌 정보 저장소
    private final Map<UUID, Map<String, BankAccount>> accountMap = new HashMap<>();

    public BankStorage() { }

    public void saveAccount(UUID playerId, String currencyId, BankAccount account) {
        accountMap.computeIfAbsent(playerId, k -> new HashMap<>()).put(currencyId, account);
    }

    public BankAccount getAccount(UUID playerId, String currencyId) {
        Map<String, BankAccount> map = accountMap.get(playerId);
        if (map == null) return null;
        return map.get(currencyId);
    }

    public void removeAccount(UUID playerId, String currencyId) {
        Map<String, BankAccount> map = accountMap.get(playerId);
        if (map != null) {
            map.remove(currencyId);
            if (map.isEmpty()) accountMap.remove(playerId);
        }
    }

    public Map<String, BankAccount> getAccounts(UUID playerId) {
        return accountMap.getOrDefault(playerId, new HashMap<>());
    }

    public void clear() {
        accountMap.clear();
    }
}