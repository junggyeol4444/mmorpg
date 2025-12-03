package com.multiverse.economy.commands;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.managers.PaymentManager;
import com.multiverse.economy.managers.CurrencyManager;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.utils.MessageUtil;
import com.multiverse.economy.models.Currency;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CheckCommand implements CommandExecutor {

    private final EconomyCore plugin;
    private final PaymentManager paymentManager;
    private final CurrencyManager currencyManager;
    private final ConfigUtil config;
    private final MessageUtil msg;

    public CheckCommand(EconomyCore plugin, PaymentManager paymentManager, CurrencyManager currencyManager, ConfigUtil config, MessageUtil msg) {
        this.plugin = plugin;
        this.paymentManager = paymentManager;
        this.currencyManager = currencyManager;
        this.config = config;
        this.msg = msg;
        plugin.getCommand("check").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /check create <화폐> <금액>
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.prefix() + "플레이어만 사용 가능합니다.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0 || !"create".equalsIgnoreCase(args[0])) {
            sender.sendMessage(msg.prefix() + "사용법: /check create <화폐> <금액>");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(msg.prefix() + "사용법: /check create <화폐> <금액>");
            return true;
        }

        String currencyId = args[1];
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(msg.prefix() + "금액을 정확히 입력하세요.");
            return true;
        }

        Currency currency = currencyManager.getCurrency(currencyId);
        if (currency == null) {
            sender.sendMessage(msg.prefix() + "존재하지 않는 화폐입니다.");
            return true;
        }

        double minAmount = config.getDouble("payment.check.min-amount", 100.0);
        double maxAmount = config.getDouble("payment.check.max-amount", 10000000.0);
        if (amount < minAmount || amount > maxAmount) {
            sender.sendMessage(msg.prefix() + "수표 금액은 " + minAmount + " 이상, " + maxAmount + " 이하여야 합니다.");
            return true;
        }

        if (!currencyManager.hasEnough(player, currencyId, amount)) {
            sender.sendMessage(msg.balanceInsufficient(currencyId, amount));
            return true;
        }

        ItemStack check = paymentManager.createCheck(player, currencyId, amount);
        player.getInventory().addItem(check);
        sender.sendMessage(msg.checkCreated(currencyId, amount));
        return true;
    }
}