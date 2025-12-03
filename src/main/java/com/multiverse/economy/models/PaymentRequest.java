package com.multiverse.economy.models;

import com.multiverse.economy.models.enums.RequestStatus;

import java.util.UUID;
import java.time.LocalDateTime;

public class PaymentRequest {
    private final String id;
    private final UUID requesterId;
    private final UUID recipientId;
    private final double amount;
    private final String currencyId;
    private final LocalDateTime requestTime;
    private RequestStatus status;

    public PaymentRequest(String id, UUID requesterId, UUID recipientId, double amount, String currencyId) {
        this.id = id;
        this.requesterId = requesterId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.currencyId = currencyId;
        this.requestTime = LocalDateTime.now();
        this.status = RequestStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public UUID getRequesterId() {
        return requesterId;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}