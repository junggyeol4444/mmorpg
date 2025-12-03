package com.multiverse.economy.models;

import java.util.UUID;

public class BankAccount {
    private final UUID ownerId;
    private double balance;
    private double loanBalance;
    private double interestRate;
    private boolean active;

    public BankAccount(UUID ownerId, double interestRate) {
        this.ownerId = ownerId;
        this.balance = 0.0;
        this.loanBalance = 0.0;
        this.interestRate = interestRate;
        this.active = true;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public double getBalance() {
        return balance;
    }

    public double getLoanBalance() {
        return loanBalance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public boolean isActive() {
        return active;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setLoanBalance(double loanBalance) {
        this.loanBalance = loanBalance;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // 예시: 출금, 입금, 대출 관련 메소드
    public void deposit(double amount) {
        this.balance += amount;
    }

    public boolean withdraw(double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    public void takeLoan(double amount) {
        this.loanBalance += amount;
        this.balance += amount;
    }

    public boolean repayLoan(double amount) {
        if (this.balance >= amount && this.loanBalance >= amount) {
            this.balance -= amount;
            this.loanBalance -= amount;
            return true;
        }
        return false;
    }
}