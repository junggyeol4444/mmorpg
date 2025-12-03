package com.multiverse.economy.api;

import com.multiverse.economy.models.BankAccount;

import java.util.List;
import java.util.UUID;

public class BankAPI {

    public static BankAccount getAccountById(String accountId, List<BankAccount> accounts) {
        for (BankAccount account : accounts) {
            if (account.getAccountId().equals(accountId)) {
                return account;
            }
        }
        return null;
    }

    public static BankAccount getAccountByOwner(UUID ownerId, List<BankAccount> accounts) {
        for (BankAccount account : accounts) {
            if (account.getOwnerId().equals(ownerId)) {
                return account;
            }
        }
        return null;
    }

    public static boolean deposit(BankAccount account, double amount) {
        account.deposit(amount);
        return true;
    }

    public static boolean withdraw(BankAccount account, double amount) {
        return account.withdraw(amount);
    }
}