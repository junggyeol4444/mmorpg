package com.multiverse.death.commands;

import com.multiverse.death.DeathAndRebirthCore;
import com.multiverse.death.managers.*;
import com.multiverse.death.models.Insurance;
import com.multiverse.death.models.RevivalQuest;
import com.multiverse.death.models.SoulCoinTransaction;
import com.multiverse.death.models.enums.*;
import com.multiverse.death.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.TabExecutor;

import java.util.List;
import java.util.ArrayList;

public class PlayerCommand implements TabExecutor {

    private final DeathAndRebirthCore plugin;
    private final DeathManager deathManager;
    private final RevivalManager revivalManager;
    private final InsuranceManager insuranceManager;
    private final SoulCoinManager soulCoinManager;
    private final MessageUtil msg;

    public PlayerCommand(DeathAndRebirthCore plugin, MessageUtil msg) {
        this.plugin = plugin;
        this.deathManager = plugin.getDeathManager();
        this.revivalManager = plugin.getRevivalManager();
        this.insuranceManager = plugin.getInsuranceManager();
        this.soulCoinManager = plugin.getSoulCoinManager();
        this.msg = msg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.p("&c플레이어만 사용 가능한 명령어입니다."));
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0 || "info".equalsIgnoreCase(args[0])) {
            showDeathInfo(player);
        } else if ("revive".equalsIgnoreCase(args[0])) {
            handleRevive(player, args);
        } else if ("history".equalsIgnoreCase(args[0])) {
            showDeathHistory(player);
        } else if ("insurance".equalsIgnoreCase(args[0])) {
            handleInsurance(player, args);
        } else if ("soulcoin".equalsIgnoreCase(args[0]) || "sc".equalsIgnoreCase(args[0])) {
            handleSoulCoin(player, args);
        } else {
            sender.sendMessage(msg.p("&c알 수 없는 명령어입니다. /death info"));
        }
        return true;
    }

    private void showDeathInfo(Player player) {
        player.sendMessage(msg.p("&e사망/부활 정보"));
        int deathCount = deathManager.getDeathCount(player);
        player.sendMessage(msg.p("&e총 사망 횟수: &6" + deathCount));
        player.sendMessage(msg.g("revival.methods"));
        player.sendMessage(msg.g("revival.method-quest"));
        player.sendMessage(msg.g("revival.method-coin", "cost", revivalManager.getRevivalCost(player) + ""));
        player.sendMessage(msg.g("revival.method-insurance"));
    }

    private void handleRevive(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(msg.p("&c사용법: /death revive <quest|coin|insurance>"));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "quest":
                if (!player.hasPermission("death.player.revive.quest")) {
                    player.sendMessage(msg.p("&c부활 퀘스트 시작 권한이 없습니다."));
                    return;
                }
                if (revivalManager.hasActiveQuest(player)) {
                    player.sendMessage(msg.p("&c이미 부활 퀘스트가 진행중입니다."));
                    return;
                }
                RevivalQuest quest = revivalManager.createQuest(player);
                player.sendMessage(msg.g("revival.quest-started"));
                player.sendMessage(msg.p("&e퀘스트 타입: " + quest.getType()));
                break;

            case "coin":
                if (!player.hasPermission("death.player.revive.coin")) {
                    player.sendMessage(msg.p("&c소울 코인 부활 권한이 없습니다."));
                    return;
                }
                int cost = revivalManager.getRevivalCost(player);
                if (!soulCoinManager.hasEnough(player, cost)) {
                    player.sendMessage(msg.g("revival.coin-insufficient", "cost", String.valueOf(cost)));
                    return;
                }
                revivalManager.reviveWithSoulCoin(player);
                player.sendMessage(msg.g("revival.coin-success"));
                break;

            case "insurance":
                if (!player.hasPermission("death.player.revive.insurance")) {
                    player.sendMessage(msg.p("&c보험 부활 권한이 없습니다."));
                    return;
                }
                if (!insuranceManager.hasActiveInsurance(player)) {
                    player.sendMessage(msg.g("revival.insurance-none"));
                    return;
                }
                revivalManager.reviveWithInsurance(player);
                player.sendMessage(msg.g("revival.insurance-used"));
                break;

            default:
                player.sendMessage(msg.p("&c올바른 부활 방식: quest, coin, insurance"));
        }
    }

    private void showDeathHistory(Player player) {
        if (!player.hasPermission("death.player.history")) {
            player.sendMessage(msg.p("&c사망 기록 확인 권한이 없습니다."));
            return;
        }
        List<com.multiverse.death.models.DeathRecord> history = deathManager.getDeathHistory(player, 10);
        player.sendMessage(msg.p("&e최근 사망 기록:"));
        if (history.isEmpty()) {
            player.sendMessage(msg.p("&7사망 기록이 없습니다."));
        } else {
            for (com.multiverse.death.models.DeathRecord record : history) {
                player.sendMessage(msg.p("&6[" +
                        record.getCause() + "] " +
                        record.getDimension() +
                        " - " + msg.formatTime(record.getDeathTime())
                ));
            }
        }
    }

    private void handleInsurance(Player player, String[] args) {
        if (args.length == 1) {
            // 보험 정보 조회
            Insurance insurance = insuranceManager.getInsurance(player);
            if (insurance == null || !insurance.isActive()) {
                player.sendMessage(msg.g("insurance.expired"));
            } else {
                long left = insurance.getExpiryDate() - System.currentTimeMillis();
                int daysLeft = (int) Math.ceil(left / 1000.0 / 60 / 60 / 24);
                player.sendMessage(msg.p("&a보험 타입: " + insurance.getType().name()));
                player.sendMessage(msg.p("&e만료까지: " + daysLeft + "일"));
            }
        } else if (args.length == 2 && "buy".equalsIgnoreCase(args[1])) {
            player.sendMessage(msg.p("&c사용법: /death insurance buy <basic|premium|platinum>"));
        } else if (args.length == 3 && "buy".equalsIgnoreCase(args[1])) {
            if (!player.hasPermission("death.player.insurance.buy")) {
                player.sendMessage(msg.p("&c보험 구매 권한이 없습니다."));
                return;
            }
            InsuranceType type;
            try {
                type = InsuranceType.valueOf(args[2].toUpperCase());
            } catch(Exception e) {
                player.sendMessage(msg.p("&c잘못된 보험 타입입니다."));
                return;
            }
            if (!insuranceManager.canPurchaseInsurance(player, type)) {
                player.sendMessage(msg.g("insurance.insufficient-money", "cost",
                        String.valueOf(insuranceManager.getInsuranceCost(type))));
                return;
            }
            insuranceManager.purchaseInsurance(player, type);
            player.sendMessage(msg.g("insurance.purchased", "type", type.name()));
        }
    }

    private void handleSoulCoin(Player player, String[] args) {
        if (!player.hasPermission("death.player.soulcoin")) {
            player.sendMessage(msg.p("&c잔액 확인 권한 없음."));
            return;
        }
        if (args.length == 1 || "balance".equalsIgnoreCase(args[1])) {
            double balance = soulCoinManager.getBalance(player);
            player.sendMessage(msg.g("soul-coin.balance", "balance", String.format("%.2f", balance)));
        } else if ("history".equalsIgnoreCase(args[1])) {
            List<SoulCoinTransaction> transactions =
                    soulCoinManager.getTransactions(player, 10);
            player.sendMessage(msg.p("&e최근 소울 코인 거래 내역:"));
            if (transactions.isEmpty()) {
                player.sendMessage(msg.p("&7거래 내역 없음."));
            } else {
                for (SoulCoinTransaction tx : transactions) {
                    player.sendMessage(msg.p("&7[" + tx.getType() + "] " +
                            String.format("%.2f", tx.getAmount()) +
                            " SC - " + tx.getReason() +
                            " [" + msg.formatTime(tx.getTimestamp()) + "]"
                    ));
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,
                                     Command cmd, String label, String[] args) {
        List<String> comp = new ArrayList<>();
        if (args.length == 1) {
            comp.add("info");
            comp.add("revive");
            comp.add("insurance");
            comp.add("history");
            comp.add("soulcoin");
            comp.add("sc");
        } else if (args.length == 2 && "revive".equalsIgnoreCase(args[0])) {
            comp.add("quest");
            comp.add("coin");
            comp.add("insurance");
        } else if (args.length == 2 && "insurance".equalsIgnoreCase(args[0])) {
            comp.add("buy");
        } else if (args.length == 3 && "insurance".equalsIgnoreCase(args[0]) && "buy".equalsIgnoreCase(args[1])) {
            comp.add("basic");
            comp.add("premium");
            comp.add("platinum");
        } else if (args.length == 2 && ("soulcoin".equalsIgnoreCase(args[0]) || "sc".equalsIgnoreCase(args[0]))) {
            comp.add("balance");
            comp.add("history");
        }
        return comp;
    }
}