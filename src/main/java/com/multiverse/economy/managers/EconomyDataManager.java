package com.multiverse.economy.managers;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.models.BankAccount;
import com.multiverse.economy.models.InflationControl;
import com.multiverse.economy.models.Currency;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.utils.MessageUtil;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyDataManager {

    private final EconomyCore plugin;
    private final DataManager dataManager;
    private final ConfigUtil config;
    private final MessageUtil msg;

    // In-memory data (실제는 YAMLDataManager 등에서 Load/Save)
    private final Map<UUID, Map<String, Double>> playerBalances = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, BankAccount>> bankAccounts = new ConcurrentHashMap<>();
    private final Map<String, InflationControl> inflationControls = new ConcurrentHashMap<>();

    public EconomyDataManager(EconomyCore plugin, DataManager dataManager, ConfigUtil config, MessageUtil msg) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.config = config;
        this.msg = msg;
    }

    // ----- Player Balance ------
    public double getBalance(Player player, String currencyId) {
        Map<String, Double> balances = playerBalances.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());
        return balances.getOrDefault(currencyId, getCurrencyStartingBalance(currencyId));
    }
    public void setBalance(Player player, String currencyId, double amount) {
        Map<String, Double> balances = playerBalances.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());
        balances.put(currencyId, amount);
        savePlayerData(player);
    }
    public Map<String, Double> getAllBalances(Player player) {
        return playerBalances.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());
    }

    // ----- Bank Account -----
    public BankAccount getBankAccount(Player player, String currencyId) {
        Map<String, BankAccount> accounts = bankAccounts.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        return accounts.get(currencyId);
    }
    public void createBankAccount(Player player, String currencyId) {
        Map<String, BankAccount> accounts = bankAccounts.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        if (!accounts.containsKey(currencyId)) {
            BankAccount acc = new BankAccount(player.getUniqueId(), currencyId, 0, 0, 0, 0, 0, 0, 0, false);
            accounts.put(currencyId, acc);
            saveBankAccount(player, currencyId, acc);
        }
    }
    public void saveBankAccount(Player player, String currencyId, BankAccount acc) {
        Map<String, BankAccount> accounts = bankAccounts.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        accounts.put(currencyId, acc);
        savePlayerData(player);
    }

    // ----- Inflation Control -----
    public void saveInflationControl(String currencyId, InflationControl ctrl) {
        inflationControls.put(currencyId, ctrl);
        // 실제 파일 저장/로드 필요시 구현, 여기선 임시
    }
    public InflationControl getInflationControl(String currencyId) {
        return inflationControls.get(currencyId);
    }

    // ----- 유통량 등 통계 -----
    public double getTotalCirculation(String currencyId) {
        // 모든 플레이어의 해당 화폐 총합
        return playerBalances.values().stream().mapToDouble(v -> v.getOrDefault(currencyId, 0)).sum();
    }
    public double getTotalIssued(String currencyId) {
        // TODO: 총 발행량 기록 로직 필요(관리자 지급, 환전 등)
        return getTotalCirculation(currencyId);
    }
    public double getTotalBurned(String currencyId) {
        InflationControl ctrl = getInflationControl(currencyId);
        return ctrl != null ? ctrl.getTotalBurned() : 0;
    }
    public double getAverageBalance(String currencyId) {
        int count = playerBalances.size();
        if (count == 0) return 0;
        return getTotalCirculation(currencyId) / count;
    }
    public double getTop10PercentBalance(String currencyId) {
        List<Double> balances = new ArrayList<>();
        for (Map<String, Double> map : playerBalances.values()) {
            balances.add(map.getOrDefault(currencyId, 0.0));
        }
        balances.sort(Comparator.reverseOrder());
        int num = Math.max(1, (int)(balances.size() * 0.1));
        return balances.subList(0, num).stream().mapToDouble(Double::doubleValue).sum();
    }
    public Map<UUID, Double> getPlayerBalances(String currencyId) {
        Map<UUID, Double> map = new HashMap<>();
        for (UUID uuid : playerBalances.keySet()) {
            map.put(uuid, playerBalances.get(uuid).getOrDefault(currencyId, 0.0));
        }
        return map;
    }

    public double getCurrencyStartingBalance(String currencyId) {
        return config.getDouble("currencies.starting-balances." + currencyId, 0.0);
    }

    // ----- Player Data Load/Save -----
    public void loadPlayerData(Player player) {
        // Load from file (이 예시에서는 생략)
        // 신규이면 시작금 넣어주기
        if (!playerBalances.containsKey(player.getUniqueId())) {
            Map<String, Double> initBalances = new HashMap<>();
            for (String currency : config.getStringList("currencies")) {
                initBalances.put(currency, getCurrencyStartingBalance(currency));
            }
            playerBalances.put(player.getUniqueId(), initBalances);
            savePlayerData(player);
        }
    }
    public boolean hasData(Player player) {
        return playerBalances.containsKey(player.getUniqueId());
    }
    public void savePlayerData(Player player) {
        // 실제 파일로 저장
    }
    public void saveAll() {
        // 모든 플레이어 데이터 저장
        for (UUID uuid : playerBalances.keySet()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) savePlayerData(player);
        }
    }

    // 전체 플레이어 UUID
    public Set<UUID> getAllPlayerUUIDs() {
        return playerBalances.keySet();
    }
}