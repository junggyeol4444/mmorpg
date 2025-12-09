package com.multiverse.trade.managers;

import com.multiverse.trade.TradeCore;
import com.multiverse. trade.events. MailSendEvent;
import com.multiverse. trade.models.Mail;
import com.multiverse.trade.models. MailStatus;
import com.multiverse.trade.models. MailType;
import com.multiverse. trade.utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit. inventory.ItemStack;

import java.util.*;
import java.util. concurrent.ConcurrentHashMap;
import java.util. stream.Collectors;

public class MailManager {

    private final TradeCore plugin;
    private final Map<UUID, List<Mail>> playerInbox = new ConcurrentHashMap<>();
    private final Map<UUID, List<Mail>> playerSent = new ConcurrentHashMap<>();
    private final Map<UUID, Mail> allMails = new ConcurrentHashMap<>();

    public MailManager(TradeCore plugin) {
        this.plugin = plugin;
    }

    public void sendMail(UUID sender, UUID receiver, String subject, String message,
                         List<ItemStack> attachments, double attachedMoney,
                         boolean isCOD, double codAmount) {
        
        UUID mailId = UUID.randomUUID();

        Mail mail = new Mail();
        mail.setMailId(mailId);
        mail.setSender(sender);
        mail.setReceiver(receiver);
        mail.setSubject(subject);
        mail.setMessage(message);
        mail.setAttachments(attachments != null ? new ArrayList<>(attachments) : new ArrayList<>());
        mail.setCurrency("default");
        mail.setAttachedMoney(attachedMoney);
        mail.setCOD(isCOD);
        mail.setCodAmount(codAmount);
        mail.setStatus(MailStatus. UNREAD);
        mail.setRead(false);
        mail.setSentTime(System.currentTimeMillis());
        mail.setReadTime(0);

        int expiryDays = plugin.getConfig().getInt("mail.expiry. days", 30);
        mail.setExpiryTime(System.currentTimeMillis() + (expiryDays * 24L * 60L * 60L * 1000L));
        mail.setType(MailType. PLAYER);

        Player senderPlayer = Bukkit.getPlayer(sender);
        MailSendEvent event = new MailSendEvent(mail, senderPlayer);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        allMails.put(mailId, mail);
        playerInbox.computeIfAbsent(receiver, k -> Collections.synchronizedList(new ArrayList<>())).add(mail);
        playerSent.computeIfAbsent(sender, k -> Collections. synchronizedList(new ArrayList<>())).add(mail);

        plugin.getMailDataManager().saveMail(mail);

        Player receiverPlayer = Bukkit.getPlayer(receiver);
        if (receiverPlayer != null && receiverPlayer.isOnline()) {
            MessageUtil.send(receiverPlayer, "mail.received");
        }
    }

    public void sendSystemMail(UUID receiver, String subject, String message) {
        sendSystemMail(receiver, subject, message, new ArrayList<>(), 0, false, 0);
    }

    public void sendSystemMail(UUID receiver, String subject, String message,
                               List<ItemStack> attachments, double attachedMoney,
                               boolean isCOD, double codAmount) {
        
        UUID mailId = UUID.randomUUID();

        Mail mail = new Mail();
        mail.setMailId(mailId);
        mail.setSender(null);
        mail.setReceiver(receiver);
        mail.setSubject(subject);
        mail.setMessage(message);
        mail.setAttachments(attachments != null ? new ArrayList<>(attachments) : new ArrayList<>());
        mail.setCurrency("default");
        mail.setAttachedMoney(attachedMoney);
        mail.setCOD(isCOD);
        mail.setCodAmount(codAmount);
        mail.setStatus(MailStatus. UNREAD);
        mail.setRead(false);
        mail.setSentTime(System.currentTimeMillis());
        mail.setReadTime(0);

        int expiryDays = plugin.getConfig().getInt("mail.expiry. days", 30);
        mail.setExpiryTime(System.currentTimeMillis() + (expiryDays * 24L * 60L * 60L * 1000L));
        mail.setType(MailType.SYSTEM);

        allMails.put(mailId, mail);
        playerInbox.computeIfAbsent(receiver, k -> Collections.synchronizedList(new ArrayList<>())).add(mail);

        plugin.getMailDataManager().saveMail(mail);

        Player receiverPlayer = Bukkit. getPlayer(receiver);
        if (receiverPlayer != null && receiverPlayer.isOnline()) {
            MessageUtil.send(receiverPlayer, "mail.received");
        }
    }

    public void sendBroadcastMail(String subject, String message, List<ItemStack> attachments) {
        for (Player player :  Bukkit.getOnlinePlayers()) {
            sendSystemMail(player. getUniqueId(), subject, message, attachments, 0, false, 0);
        }
    }

    public List<Mail> getInbox(Player player) {
        return getInboxByUUID(player.getUniqueId());
    }

    public List<Mail> getInboxByUUID(UUID playerId) {
        List<Mail> inbox = playerInbox. get(playerId);
        if (inbox == null) {
            inbox = plugin.getMailDataManager().loadPlayerInbox(playerId);
            playerInbox.put(playerId, Collections.synchronizedList(new ArrayList<>(inbox)));
            for (Mail mail : inbox) {
                allMails.put(mail.getMailId(), mail);
            }
        }
        return inbox. stream()
                .filter(m -> m.getStatus() != MailStatus. EXPIRED)
                .sorted(Comparator. comparingLong(Mail::getSentTime).reversed())
                .collect(Collectors. toList());
    }

    public List<Mail> getSentMails(Player player) {
        List<Mail> sent = playerSent.get(player.getUniqueId());
        if (sent == null) {
            sent = plugin.getMailDataManager().loadPlayerSent(player.getUniqueId());
            playerSent.put(player.getUniqueId(), Collections.synchronizedList(new ArrayList<>(sent)));
        }
        return sent.stream()
                .sorted(Comparator. comparingLong(Mail::getSentTime).reversed())
                .collect(Collectors.toList());
    }

    public Mail getMail(UUID mailId) {
        return allMails.get(mailId);
    }

    public int getUnreadCount(Player player) {
        List<Mail> inbox = getInbox(player);
        return (int) inbox.stream()
                .filter(m -> ! m.isRead())
                .count();
    }

    public void markAsRead(UUID mailId) {
        Mail mail = allMails.get(mailId);
        if (mail != null) {
            mail. setRead(true);
            mail.setStatus(MailStatus. READ);
            mail.setReadTime(System.currentTimeMillis());
            plugin.getMailDataManager().saveMail(mail);
        }
    }

    public void claimAttachments(Player player, UUID mailId) {
        Mail mail = allMails.get(mailId);
        if (mail == null || ! mail.getReceiver().equals(player. getUniqueId())) {
            return;
        }

        if (mail.getStatus() == MailStatus.CLAIMED) {
            return;
        }

        for (ItemStack item : mail.getAttachments()) {
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
            for (ItemStack left : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), left);
            }
        }

        if (mail.getAttachedMoney() > 0) {
            plugin.getEconomy().depositPlayer(player, mail.getAttachedMoney());
        }

        mail. setStatus(MailStatus.CLAIMED);
        mail.getAttachments().clear();
        mail.setAttachedMoney(0);

        plugin.getMailDataManager().saveMail(mail);
    }

    public void deleteMail(UUID mailId) {
        Mail mail = allMails.remove(mailId);
        if (mail != null) {
            List<Mail> inbox = playerInbox.get(mail.getReceiver());
            if (inbox != null) {
                inbox.removeIf(m -> m.getMailId().equals(mailId));
            }

            if (mail.getSender() != null) {
                List<Mail> sent = playerSent.get(mail.getSender());
                if (sent != null) {
                    sent. removeIf(m -> m.getMailId().equals(mailId));
                }
            }

            plugin.getMailDataManager().deleteMail(mailId);
        }
    }

    public void checkExpiredMails() {
        long now = System.currentTimeMillis();

        for (Mail mail : allMails.values()) {
            if (mail. getExpiryTime() <= now && mail.getStatus() != MailStatus. EXPIRED) {
                if (mail.getStatus() != MailStatus. CLAIMED && 
                    (! mail.getAttachments().isEmpty() || mail.getAttachedMoney() > 0)) {
                    returnMail(mail);
                }
                mail.setStatus(MailStatus.EXPIRED);
                plugin. getMailDataManager().saveMail(mail);
            }
        }
    }

    public void returnMail(Mail mail) {
        if (mail.getSender() == null || mail.getType() != MailType. PLAYER) {
            return;
        }

        sendSystemMail(
            mail.getSender(),
            "반송된 우편",
            "'" + mail.getSubject() + "' 우편이 수령되지 않아 반송되었습니다.",
            mail.getAttachments(),
            mail.getAttachedMoney(),
            false, 0
        );
    }

    public void clearPlayerMails(UUID playerId) {
        List<Mail> inbox = playerInbox.remove(playerId);
        if (inbox != null) {
            for (Mail mail : inbox) {
                allMails.remove(mail.getMailId());
            }
        }
        plugin.getMailDataManager().clearPlayerMails(playerId);
    }

    public void loadPlayerMails(UUID playerId) {
        if (! playerInbox.containsKey(playerId)) {
            List<Mail> inbox = plugin.getMailDataManager().loadPlayerInbox(playerId);
            playerInbox.put(playerId, Collections.synchronizedList(new ArrayList<>(inbox)));
            for (Mail mail :  inbox) {
                allMails.put(mail.getMailId(), mail);
            }
        }
    }

    public void saveAll() {
        for (Mail mail : allMails.values()) {
            plugin. getMailDataManager().saveMail(mail);
        }
    }
}