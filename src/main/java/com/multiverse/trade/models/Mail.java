package com. multiverse.trade. models;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java. util.List;
import java.util. UUID;

public class Mail {

    private UUID mailId;
    private UUID sender;
    private UUID receiver;
    
    private String subject;
    private String message;
    
    private List<ItemStack> attachments;
    private String currency;
    private double attachedMoney;
    
    private boolean isCOD;
    private double codAmount;
    
    private MailStatus status;
    private boolean isRead;
    
    private long sentTime;
    private long readTime;
    private long expiryTime;
    
    private MailType type;

    public Mail() {
        this.attachments = new ArrayList<>();
        this.currency = "default";
        this. attachedMoney = 0;
        this.isCOD = false;
        this.codAmount = 0;
        this.status = MailStatus.UNREAD;
        this.isRead = false;
        this. sentTime = System.  currentTimeMillis();
        this.readTime = 0;
        this.expiryTime = 0;
        this.type = MailType. PLAYER;
    }

    public UUID getMailId() {
        return mailId;
    }

    public void setMailId(UUID mailId) {
        this.mailId = mailId;
    }

    public UUID getSender() {
        return sender;
    }

    public void setSender(UUID sender) {
        this.sender = sender;
    }

    public UUID getReceiver() {
        return receiver;
    }

    public void setReceiver(UUID receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ItemStack> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ItemStack> attachments) {
        this.attachments = attachments;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.  currency = currency;
    }

    public double getAttachedMoney() {
        return attachedMoney;
    }

    public void setAttachedMoney(double attachedMoney) {
        this.attachedMoney = attachedMoney;
    }

    public boolean isCOD() {
        return isCOD;
    }

    public void setCOD(boolean COD) {
        isCOD = COD;
    }

    public double getCodAmount() {
        return codAmount;
    }

    public void setCodAmount(double codAmount) {
        this. codAmount = codAmount;
    }

    public MailStatus getStatus() {
        return status;
    }

    public void setStatus(MailStatus status) {
        this.status = status;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.  sentTime = sentTime;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this. readTime = readTime;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public MailType getType() {
        return type;
    }

    public void setType(MailType type) {
        this. type = type;
    }

    public void addAttachment(ItemStack item) {
        attachments.add(item. clone());
    }

    public boolean hasAttachments() {
        return ! attachments.isEmpty() || attachedMoney > 0;
    }

    public int getAttachmentCount() {
        return attachments.size();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expiryTime;
    }

    public long getTimeUntilExpiry() {
        return Math.max(0, expiryTime - System.currentTimeMillis());
    }

    public boolean isSystemMail() {
        return type == MailType.SYSTEM || type == MailType.  ADMIN;
    }

    public boolean canClaim() {
        return hasAttachments() && status != MailStatus. CLAIMED && status != MailStatus.EXPIRED;
    }

    public boolean canDelete() {
        return status == MailStatus. CLAIMED || status == MailStatus.  EXPIRED || ! hasAttachments();
    }

    public void markAsRead() {
        this.isRead = true;
        this. status = MailStatus. READ;
        this.readTime = System.currentTimeMillis();
    }

    public void markAsClaimed() {
        this.status = MailStatus.CLAIMED;
        this.attachments.clear();
        this.attachedMoney = 0;
    }

    public String getShortId() {
        return mailId.toString().substring(0, 8);
    }

    public long getAge() {
        return System.currentTimeMillis() - sentTime;
    }
}