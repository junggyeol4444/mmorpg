package com.multiverse.economy.commands;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.managers.*;
import com.multiverse.economy.utils.MessageUtil;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.models.enums.TransactionType;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final EconomyCore plugin;
    private final CurrencyManager currencyManager;
    private final ExchangeManager exchangeManager;
    private final BankManager bankManager;
    private final TaxManager taxManager;
    private final StatisticsManager statisticsManager;
    private final InflationManager inflationManager;
    private final MessageUtil msg;
    private final ConfigUtil config;

    public AdminCommand(EconomyCore plugin,
                        CurrencyManager currencyManager,
                        ExchangeManager exchangeManager,
                        BankManager bankManager,
                        TaxManager taxManager,
                        StatisticsManager statisticsManager,
                        InflationManager inflationManager,
                        MessageUtil msg,
                        ConfigUtil config) {
        this.plugin = plugin;
        this.currencyManager = currencyManager;
        this.exchangeManager = exchangeManager;
        this.bankManager = bankManager;
        this.taxManager = taxManager;
        this.statisticsManager = statisticsManager;
        this.inflationManager = inflationManager;
        this.msg = msg;
        this.config = config;
        plugin.getCommand("economy").setExecutor(this);
        plugin.getCommand("economy").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(msg.prefix() + "관리자 명령어 도움말: /economy admin ...");
            return true;
        }

        if (!(sender.hasPermission("economy.admin"))) {
            sender.sendMessage(msg.prefix() + "이 명령어를 사용할 권한이 없습니다.");
            return true;
        }

        String sub = args[0].toLowerCase();
        if ("admin".equals(sub)) {
            return handleAdmin(sender, args);
        } else if ("reload".equals(sub)) {
            plugin.reloadConfig();
            config.reload();
            sender.sendMessage(msg.prefix() + "설정이 리로드되었습니다.");
            return true;
        } else {
            sender.sendMessage(msg.prefix() + "알 수 없는 명령어입니다.");
            return true;
        }
    }

    private boolean handleAdmin(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(msg.prefix() + "사용법: /economy admin <set|add|remove|burn|setrate|stats|inflation|autocontrol|reload>");
            return true;
        }
        String cmd = args[1].toLowerCase();

        try {
            switch (cmd) {
                case "set": // /economy admin set <플레이어> <화폐> <금액>
                    if (args.length < 5) {
                        sender.sendMessage(msg.prefix() + "사용법: /economy admin set <플레이어> <화폐> <금액>");
                        return true;
                    }
                    Player target = Bukkit.getPlayer(args[2]);
                    if (target == null) {
                        sender.sendMessage(msg.prefix() + "플레이어를 찾을 수 없습니다.");
                        return true;
                    }
                    String currencyId = args[3];
                    double amount = Double.parseDouble(args[4]);
                    currencyManager.setBalance(target, currencyId, amount);
                    sender.sendMessage(msg.adminSetBalance(target, currencyId, amount));
                    return true;

                case "add": // /economy admin add <플레이어> <화폐> <금액>
                    if (args.length < 5) {
                        sender.sendMessage(msg.prefix() + "사용법: /economy admin add <플레이어> <화폐> <금액>");
                        return true;
                    }
                    target = Bukkit.getPlayer(args[2]);
                    if (target == null) {
                        sender.sendMessage(msg.prefix() + "플레이어를 찾을 수 없습니다.");
                        return true;
                    }
                    currencyId = args[3];
                    amount = Double.parseDouble(args[4]);
                    currencyManager.addBalance(target, currencyId, amount);
                    sender.sendMessage(msg.adminAddBalance(target, currencyId, amount));
                    return true;

                case "remove": // /economy admin remove <플레이어> <화폐> <금액>
                    if (args.length < 5) {
                        sender.sendMessage(msg.prefix() + "사용법: /economy admin remove <플레이어> <화폐> <금액>");
                        return true;
                    }
                    target = Bukkit.getPlayer(args[2]);
                    if (target == null) {
                        sender.sendMessage(msg.prefix() + "플레이어를 찾을 수 없습니다.");
                        return true;
                    }
                    currencyId = args[3];
                    amount = Double.parseDouble(args[4]);
                    currencyManager.removeBalance(target, currencyId, amount);
                    sender.sendMessage(msg.adminRemoveBalance(target, currencyId, amount));
                    return true;

                case "burn": // /economy admin burn <화폐> <금액> [사유]
                    if (args.length < 4) {
                        sender.sendMessage(msg.prefix() + "사용법: /economy admin burn <화폐> <금액> [사유]");
                        return true;
                    }
                    currencyId = args[2];
                    amount = Double.parseDouble(args[3]);
                    String reason = args.length >= 5 ? args[4] : "관리자 소각";
                    inflationManager.burnCurrency(currencyId, amount, reason);
                    sender.sendMessage(msg.adminBurn(currencyId, amount));
                    return true;

                case "setrate": // /economy admin setrate <화폐1> <화폐2> <비율>
                    if (args.length < 5) {
                        sender.sendMessage(msg.prefix() + "사용법: /economy admin setrate <화폐1> <화폐2> <비율>");
                        return true;
                    }
                    String from = args[2];
                    String to = args[3];
                    double rate = Double.parseDouble(args[4]);
                    exchangeManager.setRate(from, to, rate);
                    sender.sendMessage(msg.prefix() + String.format("환율 설정: %s -> %s = %f", from, to, rate));
                    return true;

                case "stats": // /economy admin stats <화폐>
                    if (args.length < 3) {
                        sender.sendMessage(msg.prefix() + "사용법: /economy admin stats <화폐>");
                        return true;
                    }
                    currencyId = args[2];
                    sender.sendMessage(statisticsManager.getStatisticsString(currencyId));
                    return true;

                case "inflation": // /economy admin inflation <화폐>
                    if (args.length < 3) {
                        sender.sendMessage(msg.prefix() + "사용법: /economy admin inflation <화폐>");
                        return true;
                    }
                    currencyId = args[2];
                    sender.sendMessage(inflationManager.getInflationInfo(currencyId));
                    return true;

                case "autocontrol": // /economy admin autocontrol <화폐>
                    if (args.length < 3) {
                        sender.sendMessage(msg.prefix() + "사용법: /economy admin autocontrol <화폐>");
                        return true;
                    }
                    currencyId = args[2];
                    inflationManager.emergencyBurn(currencyId);
                    sender.sendMessage(msg.prefix() + currencyId + " 긴급 인플레이션 통제 실행.");
                    return true;

                case "reload":
                    plugin.reloadConfig();
                    config.reload();
                    sender.sendMessage(msg.prefix() + "설정이 리로드되었습니다.");
                    return true;

                default:
                    sender.sendMessage(msg.prefix() + "알 수 없는 관리자 명령어입니다.");
                    return true;
            }
        } catch (Exception e) {
            sender.sendMessage(msg.prefix() + "명령 처리 오류: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // 관리자 명령어 탭 완성
        if (args.length == 2) {
            return List.of("set", "add", "remove", "burn", "setrate", "stats", "inflation", "autocontrol", "reload");
        }
        // 화폐 아이디 자동 완성 등
        return null;
    }
}