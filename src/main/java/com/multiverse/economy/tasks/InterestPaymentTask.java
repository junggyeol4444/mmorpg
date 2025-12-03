package com.multiverse.economy.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.models.BankAccount;

import java.util.List;

public class InterestPaymentTask extends BukkitRunnable {

    private final List<BankAccount> accounts;

    public InterestPaymentTask(List<BankAccount> accounts) {
        this.accounts = accounts;
    }

    @Override
    public void run() {
        double interestRate = ConfigUtil.getInterestRate();
        for (BankAccount account : accounts) {
            double interest = account.getBalance() * interestRate;
            account.deposit(interest);
        }
    }
}