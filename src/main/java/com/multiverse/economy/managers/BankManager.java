package com.multiverse.economy.managers;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.models.BankAccount;
import com.multiverse.economy.models.Currency;
import com.multiverse.economy.data.DataManager;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.utils.MessageUtil;
import com.multiverse.economy.models.enums.CurrencyType;
import com.multiverse.economy.models.enums.TransactionType;
import org.bukkit.entity.Player;

import java.util.*;

public class BankManager {

    private final EconomyCore plugin;
    private final ConfigUtil config;
    private final EconomyDataManager economyDataManager;
    private final CurrencyManager currencyManager;
    private final MessageUtil msg;
    private final StatisticsManager statisticsManager;

    public BankManager(EconomyCore plugin, ConfigUtil config, EconomyDataManager economyDataManager, CurrencyManager currencyManager, MessageUtil msg, StatisticsManager statisticsManager) {
        this.plugin = plugin;
        this.config = config;
        this.economyDataManager = economyDataManager;
        this.currencyManager = currencyManager;
        this.msg = msg;
        this.statisticsManager = statisticsManager;
    }

    // 계좌 관리
    public BankAccount getAccount(Player player, String currencyId) {
        BankAccount acc = economyDataManager.getBankAccount(player, currencyId);
        if (acc == null) {
            createAccount(player, currencyId);
            acc = economyDataManager.getBankAccount(player, currencyId);
        }
        return acc;
    }

    public void createAccount(Player player, String currencyId) {
        economyDataManager.createBankAccount(player, currencyId);
    }

    // 예금
    public void deposit(Player player, String currencyId, double amount) {
        Currency currency = currencyManager.getCurrency(currencyId);
        if (currency == null) throw new IllegalArgumentException("존재하지 않는 화폐");
        BankAccount acc = getAccount(player, currencyId);

        double minDeposit = 0.0;
        double maxDeposit = config.getDouble("bank.deposit.max-amount", 10000000.0);
        if (amount <= minDeposit || amount > maxDeposit) throw new IllegalArgumentException("예금 한도 초과");

        if (!currencyManager.hasEnough(player, currencyId, amount)) throw new IllegalArgumentException("잔액 부족");
        currencyManager.removeBalance(player, currencyId, amount);

        acc.setDeposit(acc.getDeposit() + amount);
        economyDataManager.saveBankAccount(player, currencyId, acc);

        statisticsManager.recordBankDeposit(currencyId, amount);
    }

    public void withdraw(Player player, String currencyId, double amount) {
        BankAccount acc = getAccount(player, currencyId);
        if (acc.getDeposit() < amount) throw new IllegalArgumentException("예금 부족");
        acc.setDeposit(acc.getDeposit() - amount);
        economyDataManager.saveBankAccount(player, currencyId, acc);

        currencyManager.addBalance(player, currencyId, amount);

        statisticsManager.recordBankWithdraw(currencyId, amount);
    }

    public double getDepositBalance(Player player, String currencyId) {
        BankAccount acc = getAccount(player, currencyId);
        return acc.getDeposit();
    }

    // 이자
    public void calculateInterest(Player player, String currencyId) {
        BankAccount acc = getAccount(player, currencyId);
        double dailyRate = config.getDouble("bank.interest.daily-rate", 0.1) / 100.0;
        double interest = acc.getDeposit() * dailyRate;
        double minInterest = config.getDouble("bank.interest.min-amount", 1.0);

        if (interest < minInterest) interest = minInterest;

        acc.setDeposit(acc.getDeposit() + interest);
        acc.setTotalInterestEarned(acc.getTotalInterestEarned() + interest);
        acc.setLastInterestDate(System.currentTimeMillis());
        economyDataManager.saveBankAccount(player, currencyId, acc);

        statisticsManager.recordInterestPaid(currencyId, interest);
    }

    public void payAllInterests() {
        for (UUID uuid : economyDataManager.getAllPlayerUUIDs()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                for (Currency currency : currencyManager.getAllCurrencies()) {
                    calculateInterest(player, currency.getId());
                }
            }
        }
    }

    // 대출
    public double getLoanLimit(Player player) {
        int level = player.getLevel();
        double base = config.getDouble("bank.loan.base-limit", 10000.0);
        double perLevel = config.getDouble("bank.loan.limit-per-level", 500.0);
        double max = config.getDouble("bank.loan.max-limit", 1000000.0);
        return Math.min(base + level * perLevel, max);
    }

    public boolean canTakeLoan(Player player, double amount) {
        return amount <= getLoanLimit(player);
    }

    public void takeLoan(Player player, String currencyId, double amount) {
        BankAccount acc = getAccount(player, currencyId);
        if (acc.isHasActiveLoan()) throw new IllegalArgumentException("이미 대출 있음");
        if (!canTakeLoan(player, amount)) throw new IllegalArgumentException("대출 한도 초과");

        double interestRate = config.getDouble("bank.loan.interest-rate", 2.0) / 100.0;
        double interest = amount * interestRate;
        long now = System.currentTimeMillis();
        int termDays = config.getInt("bank.loan.term-days", 30);

        acc.setLoanAmount(amount);
        acc.setLoanInterest(interest);
        acc.setLoanDate(now);
        acc.setLoanDueDate(now + termDays * 86400000L);
        acc.setHasActiveLoan(true);

        economyDataManager.saveBankAccount(player, currencyId, acc);

        currencyManager.addBalance(player, currencyId, amount);

        statisticsManager.recordLoanTaken(currencyId, amount);
    }

    public void repayLoan(Player player, String currencyId, double amount) {
        BankAccount acc = getAccount(player, currencyId);
        if (!acc.isHasActiveLoan()) throw new IllegalArgumentException("상환할 대출 없음");
        if (!currencyManager.hasEnough(player, currencyId, amount)) throw new IllegalArgumentException("잔액 부족");

        currencyManager.removeBalance(player, currencyId, amount);

        double remain = acc.getLoanAmount() - amount;
        acc.setLoanAmount(Math.max(0, remain));
        if (remain <= 0) acc.setHasActiveLoan(false);

        economyDataManager.saveBankAccount(player, currencyId, acc);

        statisticsManager.recordLoanRepaid(currencyId, amount);
    }

    public void checkOverdueLoans() {
        long now = System.currentTimeMillis();
        for (UUID uuid : economyDataManager.getAllPlayerUUIDs()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                for (Currency currency : currencyManager.getAllCurrencies()) {
                    BankAccount acc = getAccount(player, currency.getId());
                    if (acc.isHasActiveLoan() && acc.getLoanDueDate() < now) {
                        acc.setLoanInterest(acc.getLoanInterest() + config.getDouble("bank.loan.overdue-penalty", 5.0));
                        economyDataManager.saveBankAccount(player, currency.getId(), acc);
                        // 연체 패널티 및 알림
                        player.sendMessage(msg.bankLoanOverdue(currency.getId()));
                        statisticsManager.recordLoanOverdue(currency.getId(), acc.getLoanAmount());
                    }
                }
            }
        }
    }

    public void checkPlayerLoan(Player player) {
        long now = System.currentTimeMillis();
        for (Currency currency : currencyManager.getAllCurrencies()) {
            BankAccount acc = getAccount(player, currency.getId());
            if (acc.isHasActiveLoan() && acc.getLoanDueDate() < now) {
                acc.setLoanInterest(acc.getLoanInterest() + config.getDouble("bank.loan.overdue-penalty", 5.0));
                economyDataManager.saveBankAccount(player, currency.getId(), acc);
                player.sendMessage(msg.bankLoanOverdue(currency.getId()));
                statisticsManager.recordLoanOverdue(currency.getId(), acc.getLoanAmount());
            }
        }
    }
}