package com.multiverse.economy.api;

import com.multiverse.economy.models.BankAccount;

public class LoanAPI {

    public static boolean requestLoan(BankAccount account, double amount) {
        if (account.canRequestLoan(amount)) {
            account.addLoan(amount);
            return true;
        }
        return false;
    }

    public static boolean repayLoan(BankAccount account, double amount) {
        return account.repayLoan(amount);
    }

    // Additional loan-related logic can be added as needed
}