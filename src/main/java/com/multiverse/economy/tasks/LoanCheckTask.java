package com.multiverse.economy.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import com.multiverse.economy.models.BankAccount;
import com.multiverse.economy.utils.MessageUtil;

import java.util.List;

public class LoanCheckTask extends BukkitRunnable {

    private final List<BankAccount> accounts;

    public LoanCheckTask(List<BankAccount> accounts) {
        this.accounts = accounts;
    }

    @Override
    public void run() {
        for (BankAccount account : accounts) {
            if (account.hasOverdueLoan()) {
                MessageUtil.notifyOverdueLoan(account.getOwnerId());
            }
        }
    }
}