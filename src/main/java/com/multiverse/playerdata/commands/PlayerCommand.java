package com.multiverse.playerdata.commands;

import com.multiverse.playerdata.PlayerDataCore;
import com.multiverse.playerdata.managers.*;
import com.multiverse.playerdata.models.*;
import com.multiverse.playerdata.models.enums.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.List;

public class PlayerCommand implements CommandExecutor {

    private final PlayerDataCore plugin;
    private final RaceManager raceManager;
    private final StatsManager statsManager;
    private final EvolutionManager evolutionManager;
    private final TranscendenceManager transcendenceManager;
    private final PlayerDataManager playerDataManager;

    public PlayerCommand(PlayerDataCore plugin) {
        this.plugin = plugin;
        this.raceManager = plugin.getRaceManager();
        this.statsManager = plugin.getStatsManager();
        this.evolutionManager = plugin.getEvolutionManager();
        this.transcendenceManager = plugin.getTranscendenceManager();
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "플레이어만 사용할 수 있는 명령어입니다.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "info":
                showInfo(player);
                break;
            case "stats":
                if (args.length == 1) showStats(player);
                else if ("add".equalsIgnoreCase(args[1]) && args.length == 4) addStatPoints(player, args[2], args[3]);
                else if ("reset".equalsIgnoreCase(args[1])) resetStats(player);
                else sendHelp(player);
                break;
            case "race":
                showRaceInfo(player);
                break;
            case "evolution":
                handleEvolution(player, args);
                break;
            case "transcend":
                handleTranscend(player, args);
                break;
            default:
                sendHelp(player);
        }
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "[PlayerDataCore] 사용 가능한 명령어:");
        player.sendMessage("/pdata info: 캐릭터 정보");
        player.sendMessage("/pdata stats: 스탯 정보");
        player.sendMessage("/pdata stats add <스탯> <포인트>: 스탯 포인트 배분");
        player.sendMessage("/pdata stats reset: 스탯 리셋");
        player.sendMessage("/pdata race: 내 종족 정보");
        player.sendMessage("/pdata evolution [list|info|evolve|confirm]: 진화 관련");
        player.sendMessage("/pdata transcend [choose|transfer]: 초월 관련");
    }

    private void showInfo(Player player) {
        Race race = raceManager.getPlayerRace(player);
        int level = statsManager.getLevel(player);
        int points = statsManager.getAvailablePoints(player);
        player.sendMessage(ChatColor.AQUA + "==== 내 정보 ====");
        player.sendMessage("종족: " + (race != null ? race.getName() : "미설정"));
        player.sendMessage("레벨: " + level);
        player.sendMessage("남은 스탯 포인트: " + points);
        player.sendMessage("초월 여부: " + (transcendenceManager.isTranscendent(player) ? "O" : "X"));
        showStats(player);
    }

    private void showStats(Player player) {
        StringBuilder sb = new StringBuilder(ChatColor.GREEN + "==== 스탯 ====\n");
        for (StatType type : StatType.values()) {
            sb.append(type.name()).append(": ").append(statsManager.getTotalStat(player, type)).append(" ");
        }
        player.sendMessage(sb.toString());
    }

    private void addStatPoints(Player player, String statStr, String pointStr) {
        StatType type;
        int points;
        try {
            type = StatType.valueOf(statStr.toUpperCase());
            points = Integer.parseInt(pointStr);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "유효하지 않은 인자.");
            return;
        }
        int available = statsManager.getAvailablePoints(player);
        if (available < points) {
            player.sendMessage(ChatColor.RED + "스탯 포인트가 부족합니다.");
            return;
        }
        statsManager.addStatPoint(player, type, points);
        player.sendMessage(ChatColor.GREEN + type.name() + " +" + points + " 증가!");
        statsManager.applyStatEffects(player);
    }

    private void resetStats(Player player) {
        boolean hasPermission = player.hasPermission("playerdata.stats.noreset");
        int cost = plugin.getConfigUtil().getInt("stats.reset.cost", 100000);
        if (!hasPermission) {
            // Vault 연동 필요
            boolean paid = plugin.getPlayerDataManager().chargeGold(player, cost);
            if (!paid) {
                player.sendMessage(ChatColor.RED + "골드가 부족합니다! (" + cost + " 골드 필요)");
                return;
            }
        }
        statsManager.resetStats(player, true);
        player.sendMessage(ChatColor.GREEN + "스탯이 초기화되었습니다.");
        statsManager.applyStatEffects(player);
    }

    private void showRaceInfo(Player player) {
        Race race = raceManager.getPlayerRace(player);
        if (race == null) {
            player.sendMessage(ChatColor.YELLOW + "아직 종족이 배정되지 않았습니다.");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "==== 종족 정보 ====");
        player.sendMessage("종족: " + race.getName() + " (" + race.getId() + ")");
        player.sendMessage("설명: " + race.getDescription());
        player.sendMessage("능력: " + (race.getAbilities() != null ? String.join(", ", race.getAbilities()) : "없음"));
    }

    private void handleEvolution(Player player, String[] args) {
        if (args.length == 1 || "list".equalsIgnoreCase(args[1])) {
            List<Evolution> available = evolutionManager.getAvailableEvolutions(player);
            if (available.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "가능한 진화가 없습니다.");
                return;
            }
            player.sendMessage(ChatColor.AQUA + "==== 가능한 진화 ====");
            for (Evolution evo : available) {
                player.sendMessage(evo.getId() + ": " + evo.getName() + " - " + evo.getDescription());
            }
        } else if ("info".equalsIgnoreCase(args[1]) && args.length >= 3) {
            Evolution evo = evolutionManager.getEvolution(args[2]);
            if (evo == null) {
                player.sendMessage(ChatColor.RED + "해당 진화를 찾을 수 없습니다.");
                return;
            }
            player.sendMessage(ChatColor.AQUA + "진화 정보: " + evo.getName());
            player.sendMessage("설명: " + evo.getDescription());
            player.sendMessage("조건: " + evolutionManager.getRequirementText(evo));
        } else if ("evolve".equalsIgnoreCase(args[1]) && args.length >= 3) {
            Evolution evo = evolutionManager.getEvolution(args[2]);
            if (evo == null) {
                player.sendMessage(ChatColor.RED + "해당 진화를 찾을 수 없습니다.");
                return;
            }
            if (!evolutionManager.canEvolve(player, evo)) {
                player.sendMessage(ChatColor.RED + "진화 조건을 만족하지 못합니다.");
                return;
            }
            if (plugin.getConfigUtil().getBoolean("evolution.requires-confirmation", true)) {
                playerDataManager.requestEvolutionConfirmation(player, evo);
                player.sendMessage(ChatColor.YELLOW + "진화를 확인하려면 /pdata evolution confirm " + evo.getId() + " 명령어를 사용하세요.");
            } else {
                evolutionManager.evolvePlayer(player, evo);
                player.sendMessage(ChatColor.GREEN + "진화에 성공했습니다!");
            }
        } else if ("confirm".equalsIgnoreCase(args[1]) && args.length >= 3) {
            Evolution evo = evolutionManager.getEvolution(args[2]);
            if (evo == null) {
                player.sendMessage(ChatColor.RED + "해당 진화를 찾을 수 없습니다.");
                return;
            }
            if (playerDataManager.isEvolutionConfirmationPending(player, evo)) {
                evolutionManager.evolvePlayer(player, evo);
                player.sendMessage(ChatColor.GREEN + "진화에 성공했습니다!");
                playerDataManager.clearEvolutionConfirmation(player);
            } else {
                player.sendMessage(ChatColor.RED + "진화 대기 중인 진화가 없습니다: " + evo.getId());
            }
        } else {
            sendHelp(player);
        }
    }

    private void handleTranscend(Player player, String[] args) {
        if (args.length == 1) {
            boolean can = transcendenceManager.canTranscend(player);
            if (can) {
                player.sendMessage(ChatColor.GOLD + "초월 조건을 충족했습니다! /pdata transcend choose <권능>으로 진행하세요.");
            } else {
                player.sendMessage(ChatColor.YELLOW + "초월 조건을 아직 만족하지 못했습니다.");
            }
        } else if ("choose".equalsIgnoreCase(args[1]) && args.length >= 3) {
            String powerId = args[2];
            TranscendentPower power;
            try {
                power = TranscendentPower.valueOf(powerId.toUpperCase());
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "존재하지 않는 권능: " + powerId);
                return;
            }
            if (!transcendenceManager.canTranscend(player)) {
                player.sendMessage(ChatColor.RED + "초월 조건을 만족하지 못합니다.");
                return;
            }
            transcendenceManager.transcendPlayer(player, power);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "초월에 성공했습니다! 권능: " + power.name());
        } else if ("transfer".equalsIgnoreCase(args[1]) && args.length >= 3) {
            Player target = player.getServer().getPlayer(args[2]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "전수받을 플레이어를 찾을 수 없습니다.");
                return;
            }
            if (!transcendenceManager.canTransfer(player, target)) {
                player.sendMessage(ChatColor.RED + "전수를 할 수 없습니다.");
                return;
            }
            transcendenceManager.transferKnowledge(player, target);
            player.sendMessage(ChatColor.GREEN + "초월 지식을 " + target.getName() + "에게 전수했습니다.");
        } else {
            sendHelp(player);
        }
    }
}