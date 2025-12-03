package com.multiverse.economy.data.storage;

import com.multiverse.economy.models.Transaction;

import java.util.*;

public class TransactionStorage {

    // 거래 내역 저장소 (최근순)
    private final List<Transaction> transactions = new ArrayList<>();

    public TransactionStorage() { }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getRecentTransactions(int limit) {
        if (transactions.size() <= limit) return new ArrayList<>(transactions);
        return transactions.subList(transactions.size() - limit, transactions.size());
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public void clear() {
        transactions.clear();
    }
}