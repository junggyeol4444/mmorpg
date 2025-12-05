package com. multiverse.item.data;

import java.util.*;

public class ItemTrade {
    
    private String tradeId;
    private UUID senderId;
    private UUID receiverId;
    private CustomItem item;
    private long createdTime;
    private long acceptedTime;
    private long declinedTime;
    private long cancelledTime;
    private long timeout;
    private String status; // PENDING, ACCEPTED, DECLINED, CANCELLED, EXPIRED
    private String senderName;
    private String receiverName;
    private int tradeValue; // 거래 가치 (골드)
    private double taxRate; // 거래세
    
    /**
     * 기본 생성자
     */
    public ItemTrade() {
        this.tradeId = UUID.randomUUID().toString();
        this.createdTime = System.currentTimeMillis();
        this.status = "PENDING";
        this.taxRate = 0.05; // 기본 5% 거래세
    }
    
    // Getters and Setters
    public String getTradeId() {
        return tradeId;
    }
    
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }
    
    public UUID getSenderId() {
        return senderId;
    }
    
    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }
    
    public UUID getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }
    
    public CustomItem getItem() {
        return item;
    }
    
    public void setItem(CustomItem item) {
        this.item = item;
    }
    
    public long getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
    
    public long getAcceptedTime() {
        return acceptedTime;
    }
    
    public void setAcceptedTime(long acceptedTime) {
        this.acceptedTime = acceptedTime;
    }
    
    public long getDeclinedTime() {
        return declinedTime;
    }
    
    public void setDeclinedTime(long declinedTime) {
        this.declinedTime = declinedTime;
    }
    
    public long getCancelledTime() {
        return cancelledTime;
    }
    
    public void setCancelledTime(long cancelledTime) {
        this.cancelledTime = cancelledTime;
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public void setTimeout(long timeout) {
        this. timeout = Math.max(0, timeout);
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getReceiverName() {
        return receiverName;
    }
    
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
    
    public int getTradeValue() {
        return tradeValue;
    }
    
    public void setTradeValue(int tradeValue) {
        this.tradeValue = Math.max(0, tradeValue);
    }
    
    public double getTaxRate() {
        return taxRate;
    }
    
    public void setTaxRate(double taxRate) {
        this.taxRate = Math.max(0, Math.min(taxRate, 1.0));
    }
    
    /**
     * 거래세 계산
     */
    public int calculateTax() {
        return (int) (tradeValue * taxRate);
    }
    
    /**
     * 실제 거래 금액 (세금 제외)
     */
    public int getActualTradeValue() {
        return tradeValue - calculateTax();
    }
    
    /**
     * 거래 만료 여부 확인
     */
    public boolean isExpired() {
        return (System.currentTimeMillis() - createdTime) > timeout;
    }
    
    /**
     * 거래 진행 중 여부
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    
    /**
     * 거래 완료 여부
     */
    public boolean isCompleted() {
        return "ACCEPTED".equals(status);
    }
    
    /**
     * 거래 경과 시간 (초 단위)
     */
    public long getElapsedSeconds() {
        return (System. currentTimeMillis() - createdTime) / 1000;
    }
    
    /**
     * 거래 경과 시간 (분 단위)
     */
    public long getElapsedMinutes() {
        return getElapsedSeconds() / 60;
    }
    
    /**
     * 남은 시간 (초 단위)
     */
    public long getRemainingSeconds() {
        long remaining = (timeout - (System.currentTimeMillis() - createdTime)) / 1000;
        return Math.max(0, remaining);
    }
    
    /**
     * 거래 상태 설명
     */
    public String getStatusDescription() {
        switch (status) {
            case "PENDING":
                return "대기 중... ";
            case "ACCEPTED":
                return "완료됨";
            case "DECLINED":
                return "거절됨";
            case "CANCELLED":
                return "취소됨";
            case "EXPIRED":
                return "만료됨";
            default:
                return "알 수 없음";
        }
    }
    
    /**
     * ItemTrade 정보 출력
     */
    @Override
    public String toString() {
        return "ItemTrade{" +
                "tradeId='" + tradeId + '\'' +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", item='" + (item != null ? item.getName() : "null") + '\'' +
                ", status='" + status + '\'' +
                ", tradeValue=" + tradeValue +
                '}';
    }
}