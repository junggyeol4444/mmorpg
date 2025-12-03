package com.multiverse.economy.commands;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.managers.PaymentManager;
import com.multiverse.economy.utils.MessageUtil;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.models.Currency;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final EconomyCore plugin;
    private final PaymentManager paymentManager;
    private final ConfigUtil config;
    private final MessageUtil msg;

    public PayCommand(EconomyCore plugin, PaymentManager paymentManager, ConfigUtil config, MessageUtil msg) {
        this.plugin = plugin;
        this.paymentManager = paymentManager;
        this.config = config;
        this.msg = msg;
        plugin.getCommand("pay").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /pay <플레이어> <금액> [화폐]
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.prefix() + "플레이어만 사용 가능합니다.");
            return true;
        }
        Player from = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(msg.prefix() + "사용법: /pay <플레이어> <금액> [화폐]");
            return true;
        }

        Player to = Bukkit.getPlayer(args[0]);
        if (to == null || !to.isOnline() || from.equals(to)) {
            sender.sendMessage(msg.prefix() + "대상 플레이어를 찾을 수 없습니다.");
            return true;
        }

        String currencyId = args.length >= 3 ? args[2] : config.getString("vault.default-currency", "fantasy_gold");
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount < config.getDouble("payment.transfer.min-amount", 1.0) ||
                amount > config.getDouble("payment.transfer.max-amount", 1000000.0)) {
                sender.sendMessage(msg.prefix() + "송금 금액은 " +
                        config.getDouble("payment.transfer.min-amount", 1.0) + " 이상, " +
                        config.getDouble("payment.transfer.max-amount", 1000000.0) + " 이하여야 합니다.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(msg.prefix() + "금액을 정확히 입력하세요.");
            return true;
        }

        if (!paymentManager.canSendMoney(from, currencyId, amount)) {
            sender.sendMessage(msg.balanceInsufficient(currencyId, amount));
            return true;
        }

        paymentManager.sendMoney(from, to, currencyId, amount);
        sender.sendMessage(msg.transferSent(to.getName(), currencyId, amount));
        to.sendMessage(msg.transferReceived(from.getName(), currencyId, amount));
        return true;
    }
}