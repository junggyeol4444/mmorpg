package com.multiverse.economy.commands;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.managers.BankManager;
import com.multiverse.economy.managers.CurrencyManager;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.utils.MessageUtil;
import com.multiverse.economy.models.BankAccount;
import com.multiverse.economy.models.Currency;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BankCommand implements CommandExecutor {

    private final EconomyCore plugin;
    private final BankManager bankManager;
    private final CurrencyManager currencyManager;
    private final ConfigUtil config;
    private final MessageUtil msg;

    public BankCommand(EconomyCore plugin, BankManager bankManager, CurrencyManager currencyManager, ConfigUtil config, MessageUtil msg) {
        this.plugin = plugin;
        this.bankManager = bankManager;
        this.currencyManager = currencyManager;
        this.config = config;
        this.msg = msg;
        plugin.getCommand("bank").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /bank, /bank deposit <화폐> <금액>, /bank withdraw <화폐> <금액>, /bank loan <화폐> <금액>, /bank repay <화폐> <금액>
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.prefix() + "플레이어만 사용 가능합니다.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            // 은행 GUI or 정보 출력 (간략 버전)
            sender.sendMessage(msg.prefix() + "은행 명령어 도움말: /bank deposit|withdraw|loan|repay <화폐> <금액>");
            for (Currency c : currencyManager.getAllCurrencies()) {
                BankAccount acc = bankManager.getAccount(player, c.getId());
                sender.sendMessage(msg.prefix() + c.getName() + ": 예금 " + acc.getDeposit() +
                        ", 대출 " + acc.getLoanAmount() + (acc.isHasActiveLoan() ? " (연체 체크 필요)" : ""));
            }
            return true;
        }

        String sub = args[0].toLowerCase();
        try {
            switch (sub) {
                case "deposit":
                    if (args.length < 3) {
                        sender.sendMessage(msg.prefix() + "사용법: /bank deposit <화폐> <금액>");
                        return true;
                    }
                    String currencyId = args[1];
                    double amount = Double.parseDouble(args[2]);
                    bankManager.deposit(player, currencyId, amount);
                    sender.sendMessage(msg.bankDeposit(currencyId, amount));
                    return true;
                case "withdraw":
                    if (args.length < 3) {
                        sender.sendMessage(msg.prefix() + "사용법: /bank withdraw <화폐> <금액>");
                        return true;
                    }
                    currencyId = args[1];
                    amount = Double.parseDouble(args[2]);
                    bankManager.withdraw(player, currencyId, amount);
                    sender.sendMessage(msg.bankWithdraw(currencyId, amount));
                    return true;
                case "loan":
                    if (args.length < 3) {
                        sender.sendMessage(msg.prefix() + "사용법: /bank loan <화폐> <금액>");
                        return true;
                    }
                    currencyId = args[1];
                    amount = Double.parseDouble(args[2]);
                    bankManager.takeLoan(player, currencyId, amount);
                    sender.sendMessage(msg.bankLoanTaken(currencyId, amount));
                    return true;
                case "repay":
                    if (args.length < 3) {
                        sender.sendMessage(msg.prefix() + "사용법: /bank repay <화폐> <금액>");
                        return true;
                    }
                    currencyId = args[1];
                    amount = Double.parseDouble(args[2]);
                    bankManager.repayLoan(player, currencyId, amount);
                    sender.sendMessage(msg.bankLoanRepaid(currencyId, amount));
                    return true;
                default:
                    sender.sendMessage(msg.prefix() + "알 수 없는 은행 하위 명령어입니다.");
                    return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(msg.prefix() + "금액을 정확히 입력하세요.");
            return true;
        } catch (Exception e) {
            sender.sendMessage(msg.prefix() + "은행 처리 오류: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
}