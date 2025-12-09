package com.multiverse.trade.gui;

import com.multiverse. trade.TradeCore;
import com. multiverse.trade. managers.MailManager;
import com.multiverse.trade.models.Mail;
import com. multiverse.trade. models.MailStatus;
import com. multiverse.trade. models. MailType;
import com.multiverse. trade.utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import com.multiverse.trade.utils.TimeUtil;
import org. bukkit.Bukkit;
import org.bukkit.Material;
import org. bukkit.entity. Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit. inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java. util.List;

public class MailGUI implements InventoryHolder {

    private final TradeCore plugin;
    private final Player player;
    private List<Mail> mails;
    private final Inventory inventory;
    private int currentPage;
    private final int mailsPerPage = 45;
    private boolean showingInbox = true;

    public MailGUI(TradeCore plugin, Player player, int page) {
        this.plugin = plugin;
        this. player = player;
        this.currentPage = page;
        
        String title = MessageUtil. color("&8우편함");
        this.inventory = Bukkit. createInventory(this, 54, title);
        
        refreshMails();
        initialize();
    }

    private void refreshMails() {
        MailManager mailManager = plugin.getMailManager();
        if (showingInbox) {
            mails = mailManager.getInbox(player);
        } else {
            mails = mailManager. getSentMails(player);
        }
    }

    private void initialize() {
        updateMails();
        updateNavigation();
    }

    public void updateMails() {
        for (int i = 0; i < mailsPerPage; i++) {
            inventory.setItem(i, null);
        }

        int start = (currentPage - 1) * mailsPerPage;
        int end = Math.min(start + mailsPerPage, mails.size());

        for (int i = start; i < end; i++) {
            Mail mail = mails.get(i);
            ItemStack display = createMailDisplayItem(mail);
            inventory.setItem(i - start, display);
        }
    }

    private ItemStack createMailDisplayItem(Mail mail) {
        Material material;
        if (mail.getStatus() == MailStatus.UNREAD) {
            material = Material. WRITABLE_BOOK;
        } else if (mail.getStatus() == MailStatus.CLAIMED) {
            material = Material.WRITTEN_BOOK;
        } else {
            material = Material. BOOK;
        }

        ItemStack display = new ItemStack(material);
        ItemMeta meta = display.getItemMeta();
        
        if (meta != null) {
            String statusIcon = mail.getStatus().getIcon();
            String statusColor = mail. getStatus().getColor();
            meta.setDisplayName(MessageUtil.color(statusColor + statusIcon + " " + mail.getSubject()));

            List<String> lore = new ArrayList<>();
            
            String senderName;
            if (mail.isSystemMail()) {
                senderName = "&6[시스템]";
            } else if (mail.getSender() != null) {
                senderName = Bukkit.getOfflinePlayer(mail. getSender()).getName();
            } else {
                senderName = "알 수 없음";
            }
            
            lore.add(MessageUtil.color("&7보낸 사람: &f" + senderName));
            lore.add(MessageUtil.color("&7보낸 시간: &f" + TimeUtil. formatRelative(mail.getSentTime())));
            lore.add(MessageUtil.color("&7상태: " + mail.getStatus().getColor() + mail.getStatus().getDisplayName()));
            
            if (mail. hasAttachments()) {
                lore.add("");
                lore. add(MessageUtil.color("&6----- 첨부물 -----"));
                
                if (mail.getAttachedMoney() > 0) {
                    lore.add(MessageUtil.color("&7돈:  &a" + NumberUtil. format(mail.getAttachedMoney())));
                }
                
                if (! mail.getAttachments().isEmpty()) {
                    lore.add(MessageUtil.color("&7아이템:  &f" + mail.getAttachmentCount() + "개"));
                }
                
                if (mail.isCOD()) {
                    lore.add(MessageUtil.color("&c착불:  " + NumberUtil.format(mail.getCodAmount())));
                }
            }
            
            lore.add("");
            lore. add(MessageUtil. color("&e좌클릭:  &f읽기"));
            if (mail.canClaim()) {
                lore.add(MessageUtil.color("&e쉬프트+클릭: &f수령"));
            }
            
            meta. setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }

    private void updateNavigation() {
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
        }

        int totalPages = (int) Math.ceil((double) mails.size() / mailsPerPage);
        if (totalPages == 0) totalPages = 1;

        if (currentPage > 1) {
            inventory.setItem(45, createItem(Material.ARROW, "&a이전 페이지",
                Arrays.asList("&7클릭하여 이전 페이지")));
        }

        Material inboxMaterial = showingInbox ? Material.CHEST : Material. ENDER_CHEST;
        inventory.setItem(47, createItem(inboxMaterial, "&e받은 편지함",
            Arrays. asList(
                showingInbox ? "&a✔ 선택됨" : "&7클릭하여 보기"
            )));

        Material sentMaterial = showingInbox ? Material. ENDER_CHEST :  Material.CHEST;
        inventory. setItem(49, createItem(sentMaterial, "&e보낸 편지함",
            Arrays.asList(
                showingInbox ? "&7클릭하여 보기" : "&a✔ 선택됨"
            )));

        inventory.setItem(51, createItem(Material.HOPPER, "&a모두 수령",
            Arrays. asList("&7클릭하여 모든 첨부물 수령")));

        if (currentPage < totalPages) {
            inventory. setItem(53, createItem(Material. ARROW, "&a다음 페이지",
                Arrays.asList("&7클릭하여 다음 페이지")));
        }
    }

    public void handleMailClick(Player player, int slot, boolean shiftClick) {
        int index = (currentPage - 1) * mailsPerPage + slot;
        
        if (index >= mails.size()) {
            return;
        }

        Mail mail = mails.get(index);

        if (shiftClick && mail.canClaim()) {
            plugin.getMailManager().claimAttachments(player, mail. getMailId());
            refreshMails();
            updateMails();
            MessageUtil.send(player, "mail.claimed");
        } else {
            plugin.getMailManager().markAsRead(mail.getMailId());
            showMailContent(player, mail);
            refreshMails();
            updateMails();
        }
    }

    private void showMailContent(Player player, Mail mail) {
        String senderName = mail.isSystemMail() ? "&6[시스템]" : 
            (mail.getSender() != null ? Bukkit.getOfflinePlayer(mail.getSender()).getName() : "알 수 없음");
        
        player.sendMessage(MessageUtil.color("&6===== 우편 ====="));
        player.sendMessage(MessageUtil.color("&7보낸 사람: &f" + senderName));
        player.sendMessage(MessageUtil.color("&7제목: &f" + mail.getSubject()));
        player.sendMessage(MessageUtil.color(""));
        player.sendMessage(MessageUtil. color("&f" + mail.getMessage()));
        
        if (mail. hasAttachments() && mail.canClaim()) {
            player.sendMessage(MessageUtil.color(""));
            player.sendMessage(MessageUtil. color("&e쉬프트+클릭으로 첨부물을 수령하세요. "));
        }
    }

    public void showInbox() {
        showingInbox = true;
        currentPage = 1;
        refreshMails();
        updateMails();
        updateNavigation();
    }

    public void showSent() {
        showingInbox = false;
        currentPage = 1;
        refreshMails();
        updateMails();
        updateNavigation();
    }

    public void claimAll(Player player) {
        int claimed = 0;
        for (Mail mail : mails) {
            if (mail.canClaim() && ! mail.isCOD()) {
                int emptySlots = 0;
                for (ItemStack item : player.getInventory().getStorageContents()) {
                    if (item == null || item.getType().isAir()) {
                        emptySlots++;
                    }
                }
                
                if (emptySlots >= mail.getAttachmentCount()) {
                    plugin.getMailManager().claimAttachments(player, mail.getMailId());
                    claimed++;
                }
            }
        }

        refreshMails();
        updateMails();
        player.sendMessage(MessageUtil.color("&a" + claimed + "개의 우편에서 첨부물을 수령했습니다."));
    }

    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateMails();
            updateNavigation();
        }
    }

    public void nextPage() {
        int totalPages = (int) Math.ceil((double) mails.size() / mailsPerPage);
        
        if (currentPage < totalPages) {
            currentPage++;
            updateMails();
            updateNavigation();
        }
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