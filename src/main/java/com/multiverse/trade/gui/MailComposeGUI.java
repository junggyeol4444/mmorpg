package com.multiverse.trade.gui;

import com.multiverse. trade.TradeCore;
import com. multiverse.trade. utils.MessageUtil;
import com.multiverse.trade.utils. NumberUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org.bukkit. OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory. Inventory;
import org.bukkit.inventory.InventoryHolder;
import org. bukkit.inventory. ItemStack;
import org.bukkit. inventory.meta.ItemMeta;
import org.bukkit.inventory.meta. SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java. util.List;
import java.util. UUID;

public class MailComposeGUI implements InventoryHolder {

    private final TradeCore plugin;
    private final Player player;
    private final Inventory inventory;

    private UUID recipient;
    private String recipientName;
    private String subject;
    private double attachedMoney;
    private List<ItemStack> attachments;

    private static final int[] ATTACHMENT_SLOTS = {0, 1, 2, 3, 4};
    private static final int RECIPIENT_SLOT = 20;
    private static final int SUBJECT_SLOT = 22;
    private static final int MONEY_SLOT = 24;
    private static final int SEND_SLOT = 40;
    private static final int CANCEL_SLOT = 44;

    public MailComposeGUI(TradeCore plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this. attachments = new ArrayList<>();
        this.attachedMoney = 0;
        this.subject = "";
        
        String title = MessageUtil.color("&8우편 작성");
        this.inventory = Bukkit.createInventory(this, 54, title);
        
        initialize();
    }

    private void initialize() {
        for (int i = 0; i < 54; i++) {
            boolean isAttachmentSlot = false;
            for (int slot : ATTACHMENT_SLOTS) {
                if (i == slot) {
                    isAttachmentSlot = true;
                    break;
                }
            }
            
            if (! isAttachmentSlot) {
                inventory.setItem(i, createItem(Material. GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
            }
        }

        updateDisplay();
    }

    private void updateDisplay() {
        if (recipient != null) {
            ItemStack head = new ItemStack(Material. PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta. setOwningPlayer(Bukkit.getOfflinePlayer(recipient));
                meta.setDisplayName(MessageUtil.color("&e받는 사람: &f" + recipientName));
                meta.setLore(Arrays.asList(MessageUtil.color("&7클릭하여 변경")));
                head.setItemMeta(meta);
            }
            inventory. setItem(RECIPIENT_SLOT, head);
        } else {
            inventory. setItem(RECIPIENT_SLOT, createItem(Material. PLAYER_HEAD, "&e받는 사람 선택",
                Arrays.asList("&7클릭하여 선택")));
        }

        String subjectDisplay = subject. isEmpty() ? "(미설정)" : subject;
        inventory.setItem(SUBJECT_SLOT, createItem(Material. NAME_TAG, "&e제목:  &f" + subjectDisplay,
            Arrays.asList("&7클릭하여 변경")));

        inventory.setItem(MONEY_SLOT, createItem(Material. GOLD_INGOT, "&6첨부 금액: &a" + NumberUtil. format(attachedMoney),
            Arrays.asList("&7클릭하여 변경")));

        boolean canSend = recipient != null && ! subject.isEmpty();
        Material sendMaterial = canSend ? Material. EMERALD_BLOCK :  Material.REDSTONE_BLOCK;
        String sendText = canSend ? "&a우편 발송" : "&c정보를 입력하세요";
        
        List<String> sendLore = new ArrayList<>();
        if (!canSend) {
            if (recipient == null) sendLore.add("&c- 받는 사람을 선택하세요");
            if (subject.isEmpty()) sendLore.add("&c- 제목을 입력하세요");
        } else {
            sendLore.add("&7받는 사람:  &f" + recipientName);
            sendLore.add("&7제목: &f" + subject);
            if (attachedMoney > 0) {
                sendLore.add("&7첨부 금액: &a" + NumberUtil. format(attachedMoney));
            }
            int attachmentCount = 0;
            for (int slot : ATTACHMENT_SLOTS) {
                ItemStack item = inventory.getItem(slot);
                if (item != null && ! item.getType().isAir()) {
                    attachmentCount++;
                }
            }
            if (attachmentCount > 0) {
                sendLore. add("&7첨부 아이템: &f" + attachmentCount + "개");
            }
            sendLore.add("");
            sendLore.add("&e클릭하여 발송");
        }
        
        inventory. setItem(SEND_SLOT, createItem(sendMaterial, sendText, sendLore));

        inventory.setItem(CANCEL_SLOT, createItem(Material.BARRIER, "&c취소",
            Arrays.asList("&7클릭하여 취소")));
    }

    public void setRecipient(Player player) {
        player.sendMessage(MessageUtil. color("&e받는 사람 이름을 채팅창에 입력하세요.  (취소:  cancel)"));
        plugin.getGuiManager().startMailRecipientInput(player, this);
        player.closeInventory();
    }

    public void setRecipientValue(String name) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(name);
        if (target. hasPlayedBefore() || target.isOnline()) {
            this.recipient = target.getUniqueId();
            this.recipientName = target.getName();
        }
    }

    public void setSubject(Player player) {
        player.sendMessage(MessageUtil.color("&e제목을 채팅창에 입력하세요. (취소: cancel)"));
        plugin.getGuiManager().startMailSubjectInput(player, this);
        player.closeInventory();
    }

    public void setSubjectValue(String subject) {
        this.subject = subject;
    }

    public void setMoney(Player player) {
        player. sendMessage(MessageUtil.color("&e첨부할 금액을 채팅창에 입력하세요. (취소: cancel)"));
        plugin.getGuiManager().startMailMoneyInput(player, this);
        player.closeInventory();
    }

    public void setMoneyValue(double amount) {
        this.attachedMoney = amount;
    }

    public void sendMail(Player player) {
        if (recipient == null) {
            player.sendMessage(MessageUtil. color("&c받는 사람을 선택하세요. "));
            return;
        }

        if (subject.isEmpty()) {
            player. sendMessage(MessageUtil.color("&c제목을 입력하세요. "));
            return;
        }

        attachments.clear();
        for (int slot : ATTACHMENT_SLOTS) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && ! item.getType().isAir()) {
                attachments. add(item. clone());
            }
        }

        double postage = plugin.getConfig().getDouble("mail.cost. postage", 10.0);
        double totalCost = postage + attachedMoney;

        if (! plugin.getEconomy().has(player, totalCost)) {
            MessageUtil.send(player, "shop.not-enough-money");
            return;
        }

        plugin.getEconomy().withdrawPlayer(player, totalCost);

        for (int slot : ATTACHMENT_SLOTS) {
            inventory.setItem(slot, null);
        }

        plugin.getMailManager().sendMail(
            player. getUniqueId(),
            recipient,
            subject,
            "",
            attachments,
            attachedMoney,
            false,
            0
        );

        player.closeInventory();
        MessageUtil.send(player, "mail.sent");

        Player receiverPlayer = Bukkit.getPlayer(recipient);
        if (receiverPlayer != null && receiverPlayer.isOnline()) {
            MessageUtil.send(receiverPlayer, "mail.received");
        }
    }

    public void cancel(Player player) {
        returnItems(player);
        player.closeInventory();
        player.sendMessage(MessageUtil.color("&c우편 작성이 취소되었습니다."));
    }

    public void returnItems(Player player) {
        for (int slot :  ATTACHMENT_SLOTS) {
            ItemStack item = inventory. getItem(slot);
            if (item != null && !item.getType().isAir()) {
                player.getInventory().addItem(item);
                inventory.setItem(slot, null);
            }
        }
    }

    public void refresh() {
        updateDisplay();
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item. getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName(MessageUtil.color(name));
            
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore. add(MessageUtil. color(line));
            }
            meta.setLore(coloredLore);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}