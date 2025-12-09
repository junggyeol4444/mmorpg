package com.multiverse. trade.events;

import com.multiverse.trade.models. Mail;
import com. multiverse.trade. models. MailType;
import org. bukkit.entity.Player;
import org.bukkit. event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit. event.HandlerList;
import org. bukkit.inventory. ItemStack;

import java.util.List;
import java.util. UUID;

public class MailSendEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Mail mail;
    private final Player sender;
    private boolean cancelled;

    public MailSendEvent(Mail mail, Player sender) {
        this.mail = mail;
        this. sender = sender;
        this.cancelled = false;
    }

    public Mail getMail() {
        return mail;
    }

    public UUID getMailId() {
        return mail.getMailId();
    }

    public Player getSender() {
        return sender;
    }

    public UUID getSenderId() {
        return mail.getSender();
    }

    public UUID getReceiverId() {
        return mail.getReceiver();
    }

    public String getSubject() {
        return mail.getSubject();
    }

    public String getMessage() {
        return mail.getMessage();
    }

    public List<ItemStack> getAttachments() {
        return mail.getAttachments();
    }

    public double getAttachedMoney() {
        return mail.getAttachedMoney();
    }

    public boolean isCOD() {
        return mail.isCOD();
    }

    public double getCodAmount() {
        return mail.getCodAmount();
    }

    public MailType getMailType() {
        return mail. getType();
    }

    public boolean isSystemMail() {
        return mail.getType() == MailType.SYSTEM || mail.getType() == MailType.ADMIN;
    }

    public boolean hasAttachments() {
        return !mail.getAttachments().isEmpty() || mail.getAttachedMoney() > 0;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}