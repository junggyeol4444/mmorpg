package com.multiverse.economy.commands;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.managers.ExchangeManager;
import com.multiverse.economy.managers.CurrencyManager;
import com.multiverse.economy.managers.PaymentManager;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.utils.MessageUtil;
import com.multiverse.economy.models.Currency;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExchangeCommand implements CommandExecutor {

    private final EconomyCore plugin;
    private final ExchangeManager exchangeManager;
    private final CurrencyManager currencyManager;
    private final PaymentManager paymentManager;
    private final ConfigUtil config;
    private final MessageUtil msg;

    public ExchangeCommand(EconomyCore plugin, ExchangeManager exchangeManager,
                          CurrencyManager currencyManager, PaymentManager paymentManager,
                          ConfigUtil config, MessageUtil msg) {
        this.plugin = plugin;
        this.exchangeManager = exchangeManager;
        this.currencyManager = currencyManager;
        this.paymentManager = paymentManager;
        this.config = config;
        this.msg = msg;
        plugin.getCommand("exchange").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /exchange <from화폐> <to화폐> <금액>
        // /exchange rates
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.prefix() + "플레이어만 사용 가능합니다.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(msg.prefix() + "사용법: /exchange <from화폐> <to화폐> <금액> 또는 /exchange rates");
            return true;
        }

        String sub = args[0].toLowerCase();
        if ("rates".equals(sub)) {
            sender.sendMessage(msg.prefix() + "현재 환율 목록:");
            exchangeManager.getAllRates().forEach(rate -> {
                sender.sendMessage(msg.exchangeRate(rate.getFromCurrency(), rate.getToCurrency(), rate.getRate()));
            });
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(msg.prefix() + "사용법: /exchange <from화폐> <to화폐> <금액>");
            return true;
        }

        String from = args[0];
        String to = args[1];
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount <= 0) {
                sender.sendMessage(msg.prefix() + "금액은 0보다 커야 합니다.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(msg.prefix() + "금액을 정확히 입력하세요.");
            return true;
        }

        if (!currencyManager.hasEnough(player, from, amount)) {
            sender.sendMessage(msg.balanceInsufficient(from, amount));
            return true;
        }

        if (!exchangeManager.canExchange(player, from, to, amount)) {
            sender.sendMessage(msg.prefix() + "환전이 불가능합니다. (조건 및 제한)");
            return true;
        }

        double toAmount = exchangeManager.calculateExchange(from, to, amount);
        double fee = exchangeManager.calculateFee(from, to, amount);

        exchangeManager.exchange(player, from, to, amount);

        sender.sendMessage(msg.exchangeSuccess(from, amount, to, toAmount, fee));
        return true;
    }
}