package com.multiverse.trade.commands;

import com.multiverse.trade.TradeCore;
import com. multiverse.trade. managers.MailManager;
import com.multiverse.trade.models.Mail;
import com. multiverse.trade. models.MailStatus;
import com. multiverse.trade. models. MailType;
import com. multiverse.trade. utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import com.multiverse.trade.utils.TimeUtil;
import org.bukkit. Bukkit;
import org.bukkit. OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit. command.CommandSender;
import org.bukkit. command.TabCompleter;
import org.bukkit.entity.Player;
import org. bukkit.inventory.ItemStack;

import java.util.*;
import java.util. stream.Collectors;

public class MailCommand implements CommandExecutor, TabCompleter {

    private final TradeCore plugin;
    private final MailManager mailManager;
    private final Map<UUID, MailDraft> mailDrafts = new HashMap<>();

    public MailCommand(TradeCore plugin) {
        this. plugin = plugin;
        this.mailManager = plugin.getMailManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&c플레이어만 사용할 수 있습니다. "));
            return true;
        }

        Player player = (Player) sender;

        if (! plugin.getConfig().getBoolean("mail.enabled", true)) {
            MessageUtil.send(player, "general.feature-disabled");
            return true;
        }

        if (! player.hasPermission("trade.mail")) {
            MessageUtil.send(player, "general.no-permission");
            return true;
        }

        if (args.length == 0) {
            plugin.getGuiManager().openMailGUI(player, 1);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "send":
            case "보내기":
                handleSend(player, args);
                break;
            case "read":
            case "읽기":
                handleRead(player, args);
                break;
            case "claim":
            case "수령": 
                handleClaim(player, args);
                break;
            case "delete":
            case "삭제":
                handleDelete(player, args);
                break;
            case "inbox":
            case "받은편지함":
                int inboxPage = args.length >= 2 ? NumberUtil.parseInt(args[1], 1) : 1;
                plugin.getGuiManager().openMailGUI(player, inboxPage);
                break;
            case "sent":
            case "보낸편지함":
                int sentPage = args.length >= 2 ?  NumberUtil.parseInt(args[1], 1) : 1;
                handleSentMails(player, sentPage);
                break;
            case "compose":
            case "작성": 
                handleCompose(player, args);
                break;
            case "attach": 
            case "첨부":
                handleAttach(player, args);
                break;
            case "money":
            case "금액": 
                handleMoney(player, args);
                break;
            case "cod": 
            case "착불":
                handleCOD(player, args);
                break;
            case "confirm":
            case "확인":
                handleConfirm(player);
                break;
            case "cancel":
            case "취소":
                handleCancelDraft(player);
                break;
            case "claimall":
            case "모두수령":
                handleClaimAll(player);
                break;
            case "admin":
                if (player.hasPermission("trade.admin")) {
                    handleAdmin(player, args);
                } else {
                    MessageUtil.send(player, "general.no-permission");
                }
                break;
            default: 
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleSend(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(MessageUtil.color("&c사용법:  /mail send <플레이어> <제목>"));
            return;
        }

        String targetName = args[1];
        String subject = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            MessageUtil.send(player, "general.player-not-found");
            return;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtil.color("&c자기 자신에게는 우편을 보낼 수 없습니다."));
            return;
        }

        MailDraft draft = new MailDraft();
        draft.receiver = target. getUniqueId();
        draft.receiverName = target. getName();
        draft.subject = subject;
        draft.message = "";
        draft.attachments = new ArrayList<>();
        draft.attachedMoney = 0;
        draft.isCOD = false;
        draft.codAmount = 0;

        mailDrafts.put(player.getUniqueId(), draft);

        player.sendMessage(MessageUtil.color("&a우편 작성을 시작합니다."));
        player.sendMessage(MessageUtil. color("&7받는 사람:  &f" + target.getName()));
        player.sendMessage(MessageUtil. color("&7제목: &f" + subject));
        player.sendMessage(MessageUtil.color(""));
        player.sendMessage(MessageUtil. color("&e/mail attach &7- 아이템 첨부 (손에 든 아이템)"));
        player.sendMessage(MessageUtil.color("&e/mail money <금액> &7- 돈 첨부"));
        player.sendMessage(MessageUtil.color("&e/mail cod <금액> &7- 착불 설정"));
        player.sendMessage(MessageUtil.color("&e/mail confirm &7- 우편 발송"));
        player.sendMessage(MessageUtil.color("&e/mail cancel &7- 작성 취소"));
    }

    private void handleRead(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageUtil. color("&c사용법: /mail read <우편ID>"));
            return;
        }

        Mail mail = findMailByShortId(player, args[1]);
        if (mail == null) {
            MessageUtil.send(player, "mail.not-found");
            return;
        }

        mailManager.markAsRead(mail. getMailId());

        String senderName = mail. getType() == MailType. SYSTEM || mail.getType() == MailType. ADMIN
                ? "&6[시스템]"
                :  Bukkit.getOfflinePlayer(mail. getSender()).getName();

        player.sendMessage(MessageUtil.color("&6===== 우편 ====="));
        player.sendMessage(MessageUtil.color("&7보낸 사람:  &f" + senderName));
        player.sendMessage(MessageUtil.color("&7제목:  &f" + mail.getSubject()));
        player.sendMessage(MessageUtil.color("&7보낸 시간: &f" + TimeUtil.formatDate(mail.getSentTime())));
        player.sendMessage(MessageUtil.color(""));
        player.sendMessage(MessageUtil. color("&f" + mail.getMessage()));

        if (! mail.getAttachments().isEmpty() || mail.getAttachedMoney() > 0) {
            player.sendMessage(MessageUtil. color(""));
            player.sendMessage(MessageUtil. color("&6----- 첨부물 -----"));

            if (mail.getAttachedMoney() > 0) {
                player.sendMessage(MessageUtil.color("&7돈:  &a" + NumberUtil. format(mail.getAttachedMoney())));
            }

            for (ItemStack item : mail.getAttachments()) {
                String itemName = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                        ? item.getItemMeta().getDisplayName()
                        : item.getType().name();
                player. sendMessage(MessageUtil.color("&7아이템: &f" + itemName + " x" + item.getAmount()));
            }

            if (mail.isCOD()) {
                player.sendMessage(MessageUtil. color("&c착불 금액: " + NumberUtil.format(mail.getCodAmount())));
            }

            if (mail.getStatus() != MailStatus. CLAIMED) {
                String shortId = mail.getMailId().toString().substring(0, 8);
                player.sendMessage(MessageUtil.color("&e/mail claim " + shortId + " &7명령어로 수령하세요. "));
            }
        }

        MessageUtil.send(player, "mail.read");
    }

    private void handleClaim(Player player, String[] args) {
        if (args. length < 2) {
            player. sendMessage(MessageUtil.color("&c사용법: /mail claim <우편ID>"));
            return;
        }

        Mail mail = findMailByShortId(player, args[1]);
        if (mail == null) {
            MessageUtil.send(player, "mail.not-found");
            return;
        }

        if (mail.getStatus() == MailStatus. CLAIMED) {
            MessageUtil.send(player, "mail.already-claimed");
            return;
        }

        if (mail.getStatus() == MailStatus. EXPIRED) {
            MessageUtil.send(player, "mail.expired");
            return;
        }

        if (mail.getAttachments().isEmpty() && mail.getAttachedMoney() <= 0) {
            MessageUtil.send(player, "mail.no-attachments");
            return;
        }

        if (mail. isCOD() && mail.getCodAmount() > 0) {
            if (! plugin.getEconomy().has(player, mail.getCodAmount())) {
                MessageUtil.send(player, "mail.cod-not-enough");
                return;
            }
            plugin.getEconomy().withdrawPlayer(player, mail. getCodAmount());

            OfflinePlayer sender = Bukkit. getOfflinePlayer(mail.getSender());
            plugin.getEconomy().depositPlayer(sender, mail.getCodAmount());

            MessageUtil.send(player, "mail.cod-paid", "amount", NumberUtil.format(mail.getCodAmount()));
        }

        if (! mail.getAttachments().isEmpty()) {
            int emptySlots = 0;
            for (ItemStack item :  player.getInventory().getStorageContents()) {
                if (item == null || item.getType().isAir()) {
                    emptySlots++;
                }
            }

            if (emptySlots < mail.getAttachments().size()) {
                MessageUtil. send(player, "mail.inventory-full");
                return;
            }
        }

        mailManager.claimAttachments(player, mail.getMailId());
        MessageUtil.send(player, "mail.claimed");
    }

    private void handleDelete(Player player, String[] args) {
        if (args. length < 2) {
            player. sendMessage(MessageUtil.color("&c사용법: /mail delete <우편ID>"));
            return;
        }

        Mail mail = findMailByShortId(player, args[1]);
        if (mail == null) {
            MessageUtil.send(player, "mail.not-found");
            return;
        }

        if (mail.getStatus() != MailStatus. CLAIMED &&
            (! mail.getAttachments().isEmpty() || mail.getAttachedMoney() > 0)) {
            player.sendMessage(MessageUtil.color("&c첨부물을 먼저 수령하세요."));
            return;
        }

        mailManager.deleteMail(mail. getMailId());
        MessageUtil.send(player, "mail.deleted");
    }

    private void handleSentMails(Player player, int page) {
        List<Mail> sentMails = mailManager.getSentMails(player);

        if (sentMails.isEmpty()) {
            player. sendMessage(MessageUtil.color("&c보낸 우편이 없습니다."));
            return;
        }

        int perPage = 10;
        int totalPages = (int) Math.ceil((double) sentMails.size() / perPage);
        page = Math.max(1, Math.min(page, totalPages));

        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, sentMails.size());

        player.sendMessage(MessageUtil.color("&6===== 보낸 편지함 (" + page + "/" + totalPages + ") ====="));

        for (int i = start; i < end; i++) {
            Mail mail = sentMails.get(i);
            String shortId = mail.getMailId().toString().substring(0, 8);
            String receiverName = Bukkit.getOfflinePlayer(mail.getReceiver()).getName();
            String status = mail.getStatus().name();
            String time = TimeUtil.formatRelative(mail.getSentTime());

            player.sendMessage(MessageUtil.color("&7[" + shortId + "] &f" + mail.getSubject() + " &7→ " + receiverName + " (" + status + ") " + time));
        }
    }

    private void handleCompose(Player player, String[] args) {
        plugin.getGuiManager().openMailComposeGUI(player);
    }

    private void handleAttach(Player player, String[] args) {
        MailDraft draft = mailDrafts. get(player.getUniqueId());
        if (draft == null) {
            player.sendMessage(MessageUtil.color("&c먼저 /mail send <플레이어> <제목> 으로 우편 작성을 시작하세요."));
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType().isAir()) {
            player. sendMessage(MessageUtil.color("&c첨부할 아이템을 손에 들고 있어야 합니다. "));
            return;
        }

        int maxAttachments = plugin.getConfig().getInt("mail.limits.max-attachments", 5);
        if (draft.attachments. size() >= maxAttachments) {
            player.sendMessage(MessageUtil.color("&c최대 " + maxAttachments + "개까지 첨부할 수 있습니다."));
            return;
        }

        draft.attachments.add(itemInHand. clone());
        player.getInventory().setItemInMainHand(null);

        player.sendMessage(MessageUtil.color("&a아이템을 첨부했습니다.  (총 " + draft.attachments. size() + "개)"));
    }

    private void handleMoney(Player player, String[] args) {
        MailDraft draft = mailDrafts.get(player.getUniqueId());
        if (draft == null) {
            player.sendMessage(MessageUtil.color("&c먼저 /mail send <플레이어> <제목> 으로 우편 작성을 시작하세요."));
            return;
        }

        if (args. length < 2) {
            player. sendMessage(MessageUtil.color("&c사용법: /mail money <금액>"));
            return;
        }

        double amount = NumberUtil.parseDouble(args[1], -1);
        if (amount <= 0) {
            MessageUtil.send(player, "general.invalid-amount");
            return;
        }

        double maxMoney = plugin.getConfig().getDouble("mail.limits.max-money", 1000000.0);
        if (amount > maxMoney) {
            player. sendMessage(MessageUtil.color("&c최대 " + NumberUtil.format(maxMoney) + "까지 첨부할 수 있습니다."));
            return;
        }

        if (! plugin.getEconomy().has(player, amount)) {
            MessageUtil.send(player, "shop.not-enough-money");
            return;
        }

        draft.attachedMoney = amount;
        player.sendMessage(MessageUtil.color("&a" + NumberUtil.format(amount) + "을(를) 첨부했습니다."));
    }

    private void handleCOD(Player player, String[] args) {
        MailDraft draft = mailDrafts.get(player.getUniqueId());
        if (draft == null) {
            player.sendMessage(MessageUtil.color("&c먼저 /mail send <플레이어> <제목> 으로 우편 작성을 시작하세요. "));
            return;
        }

        if (! plugin.getConfig().getBoolean("mail.cod.enabled", true)) {
            player.sendMessage(MessageUtil.color("&c착불 기능이 비활성화되어 있습니다. "));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtil. color("&c사용법: /mail cod <금액>"));
            return;
        }

        double amount = NumberUtil.parseDouble(args[1], -1);
        if (amount <= 0) {
            MessageUtil.send(player, "general.invalid-amount");
            return;
        }

        double maxCod = plugin.getConfig().getDouble("mail.cod.max-amount", 1000000.0);
        if (amount > maxCod) {
            player. sendMessage(MessageUtil.color("&c최대 착불 금액은 " + NumberUtil.format(maxCod) + "입니다."));
            return;
        }

        if (draft.attachments.isEmpty()) {
            player. sendMessage(MessageUtil.color("&c착불은 아이템 첨부 시에만 설정할 수 있습니다. "));
            return;
        }

        draft.isCOD = true;
        draft.codAmount = amount;
        player.sendMessage(MessageUtil.color("&a착불 금액을 " + NumberUtil.format(amount) + "(으)로 설정했습니다. "));
    }

    private void handleConfirm(Player player) {
        MailDraft draft = mailDrafts. get(player.getUniqueId());
        if (draft == null) {
            player.sendMessage(MessageUtil.color("&c작성 중인 우편이 없습니다."));
            return;
        }

        double postage = plugin.getConfig().getDouble("mail.cost.postage", 10.0);
        double totalCost = postage + draft.attachedMoney;

        if (! plugin.getEconomy().has(player, totalCost)) {
            MessageUtil.send(player, "shop.not-enough-money");
            return;
        }

        plugin.getEconomy().withdrawPlayer(player, totalCost);

        if (postage > 0) {
            MessageUtil.send(player, "mail.postage-paid", "cost", NumberUtil.format(postage));
        }

        mailManager.sendMail(
            player.getUniqueId(),
            draft.receiver,
            draft.subject,
            draft. message,
            draft.attachments,
            draft.attachedMoney,
            draft.isCOD,
            draft.codAmount
        );

        mailDrafts.remove(player.getUniqueId());
        MessageUtil.send(player, "mail.sent");

        Player receiver = Bukkit. getPlayer(draft.receiver);
        if (receiver != null && receiver.isOnline()) {
            MessageUtil.send(receiver, "mail.received");
        }
    }

    private void handleCancelDraft(Player player) {
        MailDraft draft = mailDrafts.remove(player.getUniqueId());
        if (draft == null) {
            player.sendMessage(MessageUtil.color("&c작성 중인 우편이 없습니다."));
            return;
        }

        for (ItemStack item : draft. attachments) {
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
            for (ItemStack left : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), left);
            }
        }

        player.sendMessage(MessageUtil.color("&c우편 작성이 취소되었습니다.  첨부 아이템이 반환되었습니다."));
    }

    private void handleClaimAll(Player player) {
        List<Mail> inbox = mailManager.getInbox(player);
        List<Mail> claimable = inbox.stream()
                .filter(m -> m.getStatus() != MailStatus. CLAIMED && m.getStatus() != MailStatus. EXPIRED)
                .filter(m -> ! m.getAttachments().isEmpty() || m.getAttachedMoney() > 0)
                .filter(m -> ! m.isCOD())
                .collect(Collectors.toList());

        if (claimable.isEmpty()) {
            player.sendMessage(MessageUtil.color("&c수령 가능한 첨부물이 없습니다."));
            return;
        }

        int claimed = 0;
        for (Mail mail : claimable) {
            int emptySlots = 0;
            for (ItemStack item :  player.getInventory().getStorageContents()) {
                if (item == null || item.getType().isAir()) {
                    emptySlots++;
                }
            }

            if (emptySlots >= mail.getAttachments().size()) {
                mailManager.claimAttachments(player, mail.getMailId());
                claimed++;
            }
        }

        player. sendMessage(MessageUtil.color("&a" + claimed + "개의 우편에서 첨부물을 수령했습니다."));
    }

    private void handleAdmin(Player player, String[] args) {
        if (args.length < 2) {
            sendAdminHelp(player);
            return;
        }

        String adminCommand = args[1].toLowerCase();

        switch (adminCommand) {
            case "broadcast":
                if (args.length < 4) {
                    player.sendMessage(MessageUtil.color("&c사용법:  /mail admin broadcast <제목> <내용>"));
                    return;
                }
                String subject = args[2];
                String message = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                mailManager.sendBroadcastMail(subject, message, new ArrayList<>());
                player.sendMessage(MessageUtil.color("&a전체 공지 우편을 발송했습니다."));
                break;
            case "send":
                if (args.length < 5) {
                    player.sendMessage(MessageUtil.color("&c사용법: /mail admin send <플레이어> <제목> <내용>"));
                    return;
                }
                String targetName = args[2];
                String subj = args[3];
                String msg = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                if (!target.hasPlayedBefore() && !target.isOnline()) {
                    MessageUtil.send(player, "general.player-not-found");
                    return;
                }
                mailManager.sendSystemMail(target. getUniqueId(), subj, msg);
                player.sendMessage(MessageUtil. color("&a시스템 우편을 발송했습니다."));
                break;
            case "clear": 
                if (args.length < 3) {
                    player.sendMessage(MessageUtil.color("&c사용법: /mail admin clear <플레이어>"));
                    return;
                }
                String clearTarget = args[2];
                OfflinePlayer clearPlayer = Bukkit.getOfflinePlayer(clearTarget);
                mailManager.clearPlayerMails(clearPlayer. getUniqueId());
                player. sendMessage(MessageUtil.color("&a해당 플레이어의 우편함을 비웠습니다. "));
                break;
            default:
                sendAdminHelp(player);
                break;
        }
    }

    private Mail findMailByShortId(Player player, String shortId) {
        List<Mail> inbox = mailManager.getInbox(player);

        for (Mail mail : inbox) {
            if (mail. getMailId().toString().startsWith(shortId)) {
                return mail;
            }
        }

        try {
            UUID fullId = UUID.fromString(shortId);
            return mailManager.getMail(fullId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void sendHelp(Player player) {
        player. sendMessage(MessageUtil.color("&6===== 우편 도움말 ====="));
        player.sendMessage(MessageUtil. color("&e/mail &7- 우편함 열기"));
        player.sendMessage(MessageUtil.color("&e/mail send <플레이어> <제목> &7- 우편 작성 시작"));
        player.sendMessage(MessageUtil.color("&e/mail read <우편ID> &7- 우편 읽기"));
        player.sendMessage(MessageUtil.color("&e/mail claim <우편ID> &7- 첨부물 수령"));
        player.sendMessage(MessageUtil.color("&e/mail claimall &7- 모든 첨부물 수령"));
        player.sendMessage(MessageUtil.color("&e/mail delete <우편ID> &7- 우편 삭제"));
        player.sendMessage(MessageUtil.color("&e/mail sent [페이지] &7- 보낸 편지함"));
    }

    private void sendAdminHelp(Player player) {
        player.sendMessage(MessageUtil. color("&6===== 우편 관리자 명령어 ====="));
        player.sendMessage(MessageUtil.color("&e/mail admin broadcast <제목> <내용> &7- 전체 공지"));
        player.sendMessage(MessageUtil. color("&e/mail admin send <플레이어> <제목> <내용> &7- 시스템 우편"));
        player.sendMessage(MessageUtil. color("&e/mail admin clear <플레이어> &7- 우편함 비우기"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args. length == 1) {
            List<String> subCommands = Arrays.asList(
                "send", "read", "claim", "delete", "inbox", "sent", 
                "compose", "attach", "money", "cod", "confirm", "cancel", "claimall"
            );
            if (sender. hasPermission("trade.admin")) {
                subCommands = new ArrayList<>(subCommands);
                subCommands.add("admin");
            }
            String input = args[0]. toLowerCase();
            completions = subCommands.stream()
                    . filter(s -> s.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        } else if (args. length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand. equals("send")) {
                String input = args[1]. toLowerCase();
                completions = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(s -> s.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());
            } else if (subCommand.equals("admin") && sender.hasPermission("trade.admin")) {
                completions = Arrays.asList("broadcast", "send", "clear");
            }
        }

        return completions;
    }

    private static class MailDraft {
        UUID receiver;
        String receiverName;
        String subject;
        String message;
        List<ItemStack> attachments;
        double attachedMoney;
        boolean isCOD;
        double codAmount;
    }
}