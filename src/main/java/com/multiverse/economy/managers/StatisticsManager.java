package com.multiverse.economy.managers;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.models.*;
import com.multiverse.economy.models.enums.TransactionType;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.utils.MessageUtil;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StatisticsManager {

    private final EconomyCore plugin;
    private final ConfigUtil config;
    private final EconomyDataManager economyDataManager;
    private final CurrencyManager currencyManager;
    private final MessageUtil msg;

    // 실시간 통계 데이터 (메모리, 실제는 EconomyDataManager에 저장/불러오기)
    private final Map<String, EconomyStatistics> statisticsCache = new ConcurrentHashMap<>();
    private final Map<String, List<PlayerBalance>> topBalancesCache = new ConcurrentHashMap<>();
    private final Map<String, Double> totalTaxCollected = new HashMap<>();
    private final Map<String, Double> totalTaxBurned = new HashMap<>();
    private final Map<String, Double> taxDistribution = new HashMap<>();
    private final Map<String, Double> totalExchanged = new HashMap<>();
    private final Map<String, Double> totalFeeCollected = new HashMap<>();

    public StatisticsManager(EconomyCore plugin, ConfigUtil config, EconomyDataManager economyDataManager, CurrencyManager currencyManager, MessageUtil msg) {
        this.plugin = plugin;
        this.config = config;
        this.economyDataManager = economyDataManager;
        this.currencyManager = currencyManager;
        this.msg = msg;
        loadStatistics();
    }

    private void loadStatistics() {
        // statistics.yml 등에서 불러오는 로직
        statisticsCache.clear();
        // ...실제 불러오기 생략
    }

    public void updateStatistics(String currencyId) {
        EconomyStatistics stat = getStatistics(currencyId);
        // 갱신로직 (총유통/총발행/소각/평균/상위 등)
        stat.setTotalCirculation(economyDataManager.getTotalCirculation(currencyId));
        stat.setTotalIssued(economyDataManager.getTotalIssued(currencyId));
        stat.setTotalBurned(economyDataManager.getTotalBurned(currencyId));
        stat.setAverageBalance(economyDataManager.getAverageBalance(currencyId));
        stat.setTop10PercentBalance(economyDataManager.getTop10PercentBalance(currencyId));
        // ... 기타
        statisticsCache.put(currencyId, stat);
    }

    public void recordTransaction(Transaction transaction) {
        // 거래 통계 집계
        EconomyStatistics stat = getStatistics(transaction.getCurrencyId());
        stat.setDailyTransactions(stat.getDailyTransactions() + 1);
        stat.setDailyVolume(stat.getDailyVolume() + transaction.getAmount());
        stat.setAverageTransaction((stat.getAverageTransaction() * (stat.getDailyTransactions() - 1) + transaction.getAmount()) / stat.getDailyTransactions());
        statisticsCache.put(transaction.getCurrencyId(), stat);
    }

    public void recordTax(double amount) {
        totalTaxCollected.merge("soul_coin", amount, Double::sum);
    }

    public EconomyStatistics getStatistics(String currencyId) {
        return statisticsCache.getOrDefault(currencyId, new EconomyStatistics(currencyId, System.currentTimeMillis()));
    }

    public String getStatisticsString(String currencyId) {
        EconomyStatistics stat = getStatistics(currencyId);
        StringBuilder sb = new StringBuilder();
        sb.append(currencyId).append(" 통계\n");
        sb.append("총 유통량: ").append(stat.getTotalCirculation()).append("\n");
        sb.append("총 발행량: ").append(stat.getTotalIssued()).append("\n");
        sb.append("총 소각량: ").append(stat.getTotalBurned()).append("\n");
        sb.append("평균 보유량: ").append(stat.getAverageBalance()).append("\n");
        sb.append("상위 10% 보유량: ").append(stat.getTop10PercentBalance()).append("\n");
        sb.append("일일 거래량: ").append(stat.getDailyVolume()).append("\n");
        sb.append("일일 거래 건수: ").append(stat.getDailyTransactions()).append("\n");
        sb.append("평균 거래 금액: ").append(stat.getAverageTransaction()).append("\n");
        sb.append("일일 세금 징수액: ").append(stat.getDailyTaxCollected()).append("\n");
        sb.append("일일 세금 소각액: ").append(stat.getDailyTaxBurned()).append("\n");
        sb.append("총 예금액: ").append(stat.getTotalDeposits()).append("\n");
        sb.append("총 대출액: ").append(stat.getTotalLoans()).append("\n");
        sb.append("지급된 이자: ").append(stat.getTotalInterestPaid()).append("\n");
        return sb.toString();
    }

    public EconomyStatistics getDailyStatistics(String currencyId, LocalDate date) {
        // 일별 데이터 불러오기(실제는 파일 I/O)
        return getStatistics(currencyId);
    }

    public List<EconomyStatistics> getHistoricalData(String currencyId, int days) {
        // 최근 n일 파일 I/O
        return List.of(getStatistics(currencyId));
    }

    public List<PlayerBalance> getTopBalances(String currencyId, int limit) {
        List<PlayerBalance> balances = new ArrayList<>();
        Map<UUID, Double> playerBalances = economyDataManager.getPlayerBalances(currencyId);
        for (UUID uuid : playerBalances.keySet()) {
            Player p = plugin.getServer().getPlayer(uuid);
            String name = (p != null) ? p.getName() : uuid.toString();
            balances.add(new PlayerBalance(uuid, name, playerBalances.get(uuid)));
        }
        balances.sort(Comparator.comparingDouble(PlayerBalance::getBalance).reversed());
        return balances.stream().limit(limit).collect(Collectors.toList());
    }

    public int getPlayerRank(Player player, String currencyId) {
        List<PlayerBalance> top = getTopBalances(currencyId, 1000);
        for (int i = 0; i < top.size(); i++) {
            if (top.get(i).getPlayerUUID().equals(player.getUniqueId()))
                return i + 1;
        }
        return -1;
    }

    // --- 세금 & 거래 통계
    public void recordTaxDistribution(String currencyId, double burn, double event, double publicFund, double adminFund) {
        totalTaxBurned.merge(currencyId, burn, Double::sum);
        // 각 부분은 statisticsCache에도 상세히 집계 추가
        taxDistribution.merge(currencyId, burn + event + publicFund + adminFund, Double::sum);
    }

    public double getTotalTaxCollected() {
        return totalTaxCollected.getOrDefault("soul_coin", 0.0);
    }

    public double getTotalTaxBurned() {
        return totalTaxBurned.getOrDefault("soul_coin", 0.0);
    }

    public Map<String, Double> getTaxDistribution() {
        return taxDistribution;
    }

    public void recordBankDeposit(String currencyId, double amount) {
        EconomyStatistics stat = getStatistics(currencyId);
        stat.setTotalDeposits(stat.getTotalDeposits() + amount);
    }

    public void recordBankWithdraw(String currencyId, double amount) {
        EconomyStatistics stat = getStatistics(currencyId);
        stat.setTotalDeposits(stat.getTotalDeposits() - amount);
    }

    public void recordInterestPaid(String currencyId, double amount) {
        EconomyStatistics stat = getStatistics(currencyId);
        stat.setTotalInterestPaid(stat.getTotalInterestPaid() + amount);
    }

    public void recordLoanTaken(String currencyId, double amount) {
        EconomyStatistics stat = getStatistics(currencyId);
        stat.setTotalLoans(stat.getTotalLoans() + amount);
    }

    public void recordLoanRepaid(String currencyId, double amount) {
        EconomyStatistics stat = getStatistics(currencyId);
        stat.setTotalLoans(stat.getTotalLoans() - amount);
    }

    public void recordLoanOverdue(String currencyId, double amount) {
        // 연체율 집계 등
    }

    public long getTotalExchanged(String from, String to) {
        return totalExchanged.getOrDefault(from + "_" + to, 0L).longValue();
    }

    public double getTotalFeeCollected() {
        return totalFeeCollected.getOrDefault("soul_coin", 0.0);
    }
}