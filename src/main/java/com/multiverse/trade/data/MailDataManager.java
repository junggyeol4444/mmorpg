package com.multiverse.trade.data;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.models.Mail;
import com. multiverse.trade. models.MailStatus;
import com. multiverse.trade. models.MailType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit. inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java. util.*;
import java.util.logging.Level;

public class MailDataManager {

    private final TradeCore plugin;
    private final File mailFolder;

    public MailDataManager(TradeCore plugin) {
        this.plugin = plugin;
        this. mailFolder = new File(plugin.getDataFolder(), "mail");
        
        if (!mailFolder.exists()) {
            mailFolder.mkdirs();
        }
    }

    private File getPlayerFolder(UUID playerId) {
        File folder = new File(mailFolder, playerId.toString());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    private File getInboxFile(UUID playerId) {
        return new File(getPlayerFolder(playerId), "inbox.yml");
    }

    private File getSentFile(UUID playerId) {
        return new File(getPlayerFolder(playerId), "sent.yml");
    }

    public List<Mail> loadPlayerInbox(UUID playerId) {
        File inboxFile = getInboxFile(playerId);
        return loadMailsFromFile(inboxFile);
    }

    public List<Mail> loadPlayerSent(UUID playerId) {
        File sentFile = getSentFile(playerId);
        return loadMailsFromFile(sentFile);
    }

    private List<Mail> loadMailsFromFile(File file) {
        List<Mail> mails = new ArrayList<>();
        
        if (!file. exists()) {
            return mails;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<?, ? >> mailsList = config.getMapList("mails");

        for (Map<?, ? > mailMap : mailsList) {
            try {
                Mail mail = deserializeMail(mailMap);
                if (mail != null) {
                    mails.add(mail);
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "우편 로드 실패", e);
            }
        }

        return mails;
    }

    private Mail deserializeMail(Map<?, ? > map) {
        Mail mail = new Mail();
        
        mail.setMailId(UUID.fromString((String) map.get("mail-id")));
        
        String senderStr = (String) map.get("sender");
        if (senderStr != null && !senderStr. isEmpty()) {
            mail.setSender(UUID.fromString(senderStr));
        }
        
        mail.setReceiver(UUID.fromString((String) map.get("receiver")));
        mail.setSubject((String) map.get("subject"));
        mail.setMessage((String) map.getOrDefault("message", ""));
        
        List<ItemStack> attachments = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> attachmentsList = (List<Map<String, Object>>) map.get("attachments");
        if (attachmentsList != null) {
            for (Map<String, Object> itemData : attachmentsList) {
                attachments.add(ItemStack.deserialize(itemData));
            }
        }
        mail.setAttachments(attachments);
        
        mail. setCurrency((String) map.getOrDefault("currency", "default"));
        mail.setAttachedMoney(((Number) map.getOrDefault("attached-money", 0.0)).doubleValue());
        mail.setCOD((Boolean) map.getOrDefault("is-cod", false));
        mail.setCodAmount(((Number) map.getOrDefault("cod-amount", 0.0)).doubleValue());
        mail.setStatus(MailStatus. valueOf((String) map.get("status")));
        mail.setRead((Boolean) map.getOrDefault("is-read", false));
        mail.setSentTime(((Number) map.get("sent-time")).longValue());
        mail.setReadTime(((Number) map.getOrDefault("read-time", 0L)).longValue());
        mail.setExpiryTime(((Number) map.get("expiry-time")).longValue());
        mail.setType(MailType. valueOf((String) map.getOrDefault("type", "PLAYER")));

        return mail;
    }

    public void saveMail(Mail mail) {
        if (mail == null) {
            return;
        }

        saveToPlayerInbox(mail. getReceiver(), mail);
        
        if (mail. getSender() != null && mail.getType() == MailType.PLAYER) {
            saveToPlayerSent(mail. getSender(), mail);
        }
    }

    private void saveToPlayerInbox(UUID playerId, Mail mail) {
        File inboxFile = getInboxFile(playerId);
        List<Mail> mails = loadMailsFromFile(inboxFile);
        
        mails.removeIf(m -> m.getMailId().equals(mail.getMailId()));
        mails.add(mail);
        
        saveMailsToFile(inboxFile, mails);
    }

    private void saveToPlayerSent(UUID playerId, Mail mail) {
        File sentFile = getSentFile(playerId);
        List<Mail> mails = loadMailsFromFile(sentFile);
        
        mails. removeIf(m -> m.getMailId().equals(mail.getMailId()));
        mails. add(mail);
        
        saveMailsToFile(sentFile, mails);
    }

    private void saveMailsToFile(File file, List<Mail> mails) {
        List<Map<String, Object>> mailsList = new ArrayList<>();

        for (Mail mail : mails) {
            mailsList.add(serializeMail(mail));
        }

        FileConfiguration config = new YamlConfiguration();
        config.set("mails", mailsList);

        try {
            config. save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level. SEVERE, "우편 저장 실패:  " + file.getName(), e);
        }
    }

    private Map<String, Object> serializeMail(Mail mail) {
        Map<String, Object> map = new LinkedHashMap<>();
        
        map. put("mail-id", mail.getMailId().toString());
        map.put("sender", mail.getSender() != null ? mail. getSender().toString() : "");
        map.put("receiver", mail.getReceiver().toString());
        map.put("subject", mail.getSubject());
        map.put("message", mail.getMessage());
        
        List<Map<String, Object>> attachmentsList = new ArrayList<>();
        for (ItemStack item : mail.getAttachments()) {
            attachmentsList.add(item.serialize());
        }
        map. put("attachments", attachmentsList);
        
        map. put("currency", mail.getCurrency());
        map.put("attached-money", mail.getAttachedMoney());
        map.put("is-cod", mail. isCOD());
        map.put("cod-amount", mail.getCodAmount());
        map.put("status", mail. getStatus().name());
        map.put("is-read", mail. isRead());
        map.put("sent-time", mail. getSentTime());
        map.put("read-time", mail.getReadTime());
        map.put("expiry-time", mail.getExpiryTime());
        map.put("type", mail.getType().name());

        return map;
    }

    public void deleteMail(UUID mailId) {
        for (File playerFolder : mailFolder.listFiles()) {
            if (! playerFolder.isDirectory()) {
                continue;
            }
            
            File inboxFile = new File(playerFolder, "inbox.yml");
            if (inboxFile.exists()) {
                List<Mail> mails = loadMailsFromFile(inboxFile);
                if (mails.removeIf(m -> m.getMailId().equals(mailId))) {
                    saveMailsToFile(inboxFile, mails);
                }
            }
            
            File sentFile = new File(playerFolder, "sent.yml");
            if (sentFile. exists()) {
                List<Mail> mails = loadMailsFromFile(sentFile);
                if (mails.removeIf(m -> m.getMailId().equals(mailId))) {
                    saveMailsToFile(sentFile, mails);
                }
            }
        }
    }

    public void clearPlayerMails(UUID playerId) {
        File playerFolder = getPlayerFolder(playerId);
        
        File inboxFile = new File(playerFolder, "inbox.yml");
        if (inboxFile.exists()) {
            inboxFile.delete();
        }
        
        File sentFile = new File(playerFolder, "sent. yml");
        if (sentFile.exists()) {
            sentFile.delete();
        }
    }

    public void saveAll() {
        plugin.getLogger().info("우편 데이터 저장 완료");
    }
}