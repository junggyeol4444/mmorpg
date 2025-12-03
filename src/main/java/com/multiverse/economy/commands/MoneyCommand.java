package com.multiverse.economy.commands;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.managers.CurrencyManager;
import com.multiverse.economy.managers.StatisticsManager;
import com.multiverse.economy.managers.PaymentManager;
import com.multiverse.economy.utils.MessageUtil;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.models.PlayerBalance;
import com.multiverse.economy.models.Currency;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MoneyCommand implements CommandExecutor {

    private final EconomyCore plugin;
    private final CurrencyManager currencyManager;
    private final StatisticsManager statisticsManager;
    private final PaymentManager paymentManager;
    private final ConfigUtil config;
    private final MessageUtil msg;

    public MoneyCommand(EconomyCore plugin, CurrencyManager currencyManager,
                        StatisticsManager statisticsManager,
                        PaymentManager paymentManager,
                        ConfigUtil config, MessageUtil msg) {
        this.plugin = plugin;
        this.currencyManager = currencyManager;
        this.statisticsManager = statisticsManager;
        this.paymentManager = paymentManager;
        this.config = config;
        this.msg = msg;
        plugin.getCommand("money").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /money, /bal, /balance
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.prefix() + "플레이어만 사용 가능합니다.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            // 기본 잔액 - Vault 기본 화폐
            String defaultCurrency = config.getString("vault.default-currency", "fantasy_gold");
            double amount = currencyManager.getBalance(player, defaultCurrency);
            Currency currency = currencyManager.getCurrency(defaultCurrency);
            sender.sendMessage(msg.showBalance(amount, currency));
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "all":
                sender.sendMessage(msg.prefix() + msg.get("balance.show-all"));
                for (Currency currency : currencyManager.getAllCurrencies()) {
                    double amount = currencyManager.getBalance(player, currency.getId());
                    sender.sendMessage(msg.showBalance(amount, currency));
                }
                return true;

            case "top":
                String currencyId = args.length > 1 ? args[1] : config.getString("vault.default-currency", "fantasy_gold");
                int topCount = config.getInt("statistics.ranking.top-count", 10);
                List<PlayerBalance> topBalances = statisticsManager.getTopBalances(currencyId, topCount);
                sender.sendMessage(formatRanking(topBalances, currencyManager.getCurrency(currencyId)));
                return true;

            default:
                sender.sendMessage(msg.prefix() + "알 수 없는 하위 명령어입니다.");
                return true;
        }
    }

    private String formatRanking(List<PlayerBalance> topBalances, Currency currency) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg.prefix()).append(currency.getName()).append(" 잔액 랭킹 Top ").append(topBalances.size()).append(":\n");
        int rank = 1;
        for (PlayerBalance pb : topBalances) {
            sb.append(rank++).append(". ")
                    .append(pb.getPlayerName()).append(" - ")
                    .append(currency.display(pb.getBalance())).append("\n");
        }
        return sb.toString();
    }
}