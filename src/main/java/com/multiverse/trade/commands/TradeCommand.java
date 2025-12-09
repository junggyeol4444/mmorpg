package com. multiverse.trade. commands;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.managers.TradeManager;
import com.multiverse.trade.managers.TradeSecurityManager;
import com.multiverse.trade.models.Trade;
import com.multiverse. trade.utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. command.Command;
import org.bukkit. command.CommandExecutor;
import org. bukkit.command. CommandSender;
import org.bukkit. command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java. util.Arrays;
import java. util.List;
import java.util. UUID;
import java.util. stream.Collectors;

public class TradeCommand implements CommandExecutor, TabCompleter {

    private final TradeCore plugin;
    private final TradeManager tradeManager;
    private final TradeSecurityManager securityManager;

    public TradeCommand(TradeCore plugin) {
        this. plugin = plugin;
        this.tradeManager = plugin.getTradeManager();
        this.securityManager = plugin.getTradeSecurityManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&c플레이어만 사용할 수 있습니다."));
            return true;
        }

        Player player = (Player) sender;

        // 기능 활성화 확인
        if (! plugin.getConfig().getBoolean("direct-trade.enabled", true)) {
            MessageUtil.send(player, "general.feature-disabled");
            return true;
        }

        // 권한 확인
        if (!player.hasPermission("trade.trade")) {
            MessageUtil.send(player, "general.no-permission");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0]. toLowerCase();

        switch (subCommand) {
            case "accept":
            case "수락":
                handleAccept(player);
                break;
            case "deny":
            case "거부":
                handleDeny(player);
                break;
            case "cancel":
            case "취소": 
                handleCancel(player);
                break;
            case "admin":
                if (args.length >= 2) {
                    handleAdmin(player, args);
                } else {
                    sendAdminHelp(player);
                }
                break;
            default:
                // 플레이어 이름으로 거래 요청
                handleTradeRequest(player, args[0]);
                break;
        }

        return true;
    }

    private void handleTradeRequest(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);

        // 대상 플레이어 확인
        if (target == null || !target.isOnline()) {
            MessageUtil.send(player, "general.player-not-found");
            return;
        }

        // 자기 자신과 거래 불가
        if (player. getUniqueId().equals(target.getUniqueId())) {
            MessageUtil.send(player, "trade.self-trade");
            return;
        }

        // 거래 제한 확인
        if (!securityManager.canTrade(player)) {
            long remaining = securityManager. getTradeDelayRemaining(player);
            MessageUtil.send(player, "trade.cooldown", "time", String.valueOf(remaining / 1000));
            return;
        }

        // 레벨 요구사항 확인
        if (! securityManager.meetsLevelRequirement(player)) {
            int minLevel = plugin.getConfig().getInt("direct-trade.restrictions.min-level", 5);
            MessageUtil.send(player, "trade.level-required", "level", String.valueOf(minLevel));
            return;
        }

        // 블랙리스트 확인
        if (securityManager.isBlacklisted(player. getUniqueId(), target.getUniqueId())) {
            MessageUtil.send(player, "trade.blacklisted");
            return;
        }

        // 이미 거래 중인지 확인
        if (tradeManager.isTrading(player)) {
            MessageUtil.send(player, "trade.already-trading");
            return;
        }

        if (tradeManager. isTrading(target)) {
            MessageUtil.send(player, "trade.target-trading");
            return;
        }

        // 거리 확인 (50블록 이내)
        if (player.getWorld() != target.getWorld() || 
            player.getLocation().distance(target.getLocation()) > 50) {
            MessageUtil.send(player, "trade. too-far");
            return;
        }

        // 거래 요청 전송
        tradeManager.sendTradeRequest(player, target);
        MessageUtil.send(player, "trade. request-sent");
        MessageUtil.send(target, "trade.request-received", "player", player.getName());
    }

    private void handleAccept(Player player) {
        UUID pendingFrom = tradeManager.getPendingRequest(player. getUniqueId());

        if (pendingFrom == null) {
            MessageUtil.send(player, "trade. no-pending");
            return;
        }

        Player requester = Bukkit.getPlayer(pendingFrom);
        if (requester == null || !requester.isOnline()) {
            tradeManager.removePendingRequest(player.getUniqueId());
            MessageUtil.send(player, "general.player-not-found");
            return;
        }

        // 거래 수락
        Trade trade = tradeManager.acceptTradeRequest(player, pendingFrom);
        if (trade != null) {
            MessageUtil.send(player, "trade.started");
            MessageUtil. send(requester, "trade. started");
            
            // 거래 GUI 열기
            plugin.getGuiManager().openTradeGUI(player, trade);
            plugin.getGuiManager().openTradeGUI(requester, trade);
        }
    }

    private void handleDeny(Player player) {
        UUID pendingFrom = tradeManager.getPendingRequest(player. getUniqueId());

        if (pendingFrom == null) {
            MessageUtil.send(player, "trade.no-pending");
            return;
        }

        Player requester = Bukkit. getPlayer(pendingFrom);
        tradeManager.declineTradeRequest(player, pendingFrom);
        
        MessageUtil.send(player, "trade.cancelled");
        if (requester != null && requester.isOnline()) {
            MessageUtil.send(requester, "trade.cancelled");
        }
    }

    private void handleCancel(Player player) {
        Trade trade = tradeManager.getTrade(player);
        
        if (trade == null) {
            MessageUtil.send(player, "trade. no-pending");
            return;
        }

        tradeManager.cancelTrade(trade);
        
        Player other = trade.getOtherPlayer(player);
        MessageUtil.send(player, "trade.cancelled");
        if (other != null && other.isOnline()) {
            MessageUtil.send(other, "trade.cancelled");
        }
    }

    private void handleAdmin(Player player, String[] args) {
        if (! player.hasPermission("trade.admin")) {
            MessageUtil.send(player, "general.no-permission");
            return;
        }

        String adminCommand = args[1].toLowerCase();

        switch (adminCommand) {
            case "reload":
                plugin. reload();
                MessageUtil.send(player, "general.reload-success");
                break;
            case "history":
                if (args.length >= 3) {
                    String targetName = args[2];
                    Player target = Bukkit.getPlayer(targetName);
                    if (target != null) {
                        showTradeHistory(player, target);
                    } else {
                        MessageUtil.send(player, "general.player-not-found");
                    }
                } else {
                    player.sendMessage(MessageUtil.color("&c사용법: /trade admin history <플레이어>"));
                }
                break;
            case "restore":
                if (args.length >= 3) {
                    String tradeId = args[2];
                    restoreTrade(player, tradeId);
                } else {
                    player.sendMessage(MessageUtil. color("&c사용법: /trade admin restore <거래ID>"));
                }
                break;
            default:
                sendAdminHelp(player);
                break;
        }
    }

    private void showTradeHistory(Player admin, Player target) {
        List<Trade> history = securityManager.getTradeHistory(target, 7);
        
        if (history.isEmpty()) {
            admin.sendMessage(MessageUtil.color("&e" + target.getName() + "님의 최근 거래 기록이 없습니다."));
            return;
        }

        admin. sendMessage(MessageUtil.color("&6===== " + target.getName() + "님의 거래 기록 ====="));
        for (Trade trade : history) {
            String otherName = Bukkit.getOfflinePlayer(trade. getOtherPlayerUUID(target.getUniqueId())).getName();
            String status = trade.getStatus().name();
            String time = new java.text.SimpleDateFormat("MM/dd HH: mm").format(new java.util.Date(trade. getStartTime()));
            admin.sendMessage(MessageUtil.color("&7[" + time + "] &f" + otherName + " &7- " + status));
        }
    }

    private void restoreTrade(Player admin, String tradeId) {
        try {
            UUID uuid = UUID.fromString(tradeId);
            Trade trade = securityManager.getTradeById(uuid);
            
            if (trade == null) {
                admin. sendMessage(MessageUtil.color("&c해당 거래를 찾을 수 없습니다."));
                return;
            }

            // 거래 복구 로직
            admin.sendMessage(MessageUtil.color("&a거래 복구 기능은 아직 구현 중입니다. "));
        } catch (IllegalArgumentException e) {
            admin.sendMessage(MessageUtil. color("&c올바른 거래 ID가 아닙니다. "));
        }
    }

    private void sendHelp(Player player) {
        player. sendMessage(MessageUtil.color("&6===== TradeCore 도움말 ====="));
        player.sendMessage(MessageUtil.color("&e/trade <플레이어> &7- 거래 요청"));
        player.sendMessage(MessageUtil. color("&e/trade accept &7- 거래 수락"));
        player.sendMessage(MessageUtil.color("&e/trade deny &7- 거래 거부"));
        player.sendMessage(MessageUtil.color("&e/trade cancel &7- 거래 취소"));
    }

    private void sendAdminHelp(Player player) {
        player.sendMessage(MessageUtil.color("&6===== TradeCore 관리자 명령어 ====="));
        player.sendMessage(MessageUtil.color("&e/trade admin reload &7- 설정 리로드"));
        player.sendMessage(MessageUtil.color("&e/trade admin history <플레이어> &7- 거래 기록 조회"));
        player.sendMessage(MessageUtil.color("&e/trade admin restore <거래ID> &7- 거래 복구"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args. length == 1) {
            List<String> subCommands = Arrays.asList("accept", "deny", "cancel");
            if (sender. hasPermission("trade.admin")) {
                subCommands = new ArrayList<>(subCommands);
                subCommands.add("admin");
            }
            
            // 온라인 플레이어 추가
            for (Player p : Bukkit. getOnlinePlayers()) {
                if (! p.getName().equals(sender.getName())) {
                    subCommands.add(p.getName());
                }
            }
            
            String input = args[0].toLowerCase();
            completions = subCommands.stream()
                    . filter(s -> s.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            List<String> adminCommands = Arrays. asList("reload", "history", "restore");
            String input = args[1].toLowerCase();
            completions = adminCommands.stream()
                    .filter(s -> s.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("history")) {
            String input = args[2]. toLowerCase();
            completions = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(s -> s. toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }

        return completions;
    }
}