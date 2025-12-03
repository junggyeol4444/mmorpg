package com.multiverse.death.models;

import com.multiverse.death.models.enums.TransactionType;
import java.util.Date;

/**
 * 소울 코인 거래 내역 모델
 */
public class SoulCoinTransaction {
    private Date date;
    private TransactionType type;
    private double amount;
    private String source;
    private String description;

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}