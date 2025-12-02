package com.multiverse.playerdata.commands;

import com.multiverse.playerdata.PlayerDataCore;
import com.multiverse.playerdata.managers.*;
import com.multiverse.playerdata.models.enums.StatType;
import com.multiverse.playerdata.models.enums.RaceType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.UUID;

public class AdminCommand implements CommandExecutor {

    private final PlayerDataCore plugin;
    private final RaceManager raceManager;
    private final StatsManager statsManager;
    private final EvolutionManager evolutionManager;
    private final TranscendenceManager transcendenceManager;
    private final PlayerDataManager playerDataManager;

    public AdminCommand(PlayerDataCore plugin) {
        this.plugin = plugin;
        this.raceManager = plugin.getRaceManager();
        this.statsManager = plugin.getStatsManager();
        this.evolutionManager = plugin.getEvolutionManager();
        this.transcendenceManager = plugin.getTranscendenceManager();
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("playerdata.admin")) {
            sender.sendMessage(ChatColor.RED + "이 명령어를 사용할 권한이 없습니다.");
            return true;
        }
        if (args.length < 1) {
            sendAdminHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        try {
            switch (sub) {
                case "view":
                    handleView(sender, args);
                    break;
                case "setrace":
                    handleSetRace(sender, args);
                    break;
                case "setstat":
                    handleSetStat(sender, args);
                    break;
                case "addpoints":
                    handleAddPoints(sender, args);
                    break;
                case "evolve":
                    handleEvolve(sender, args);
                    break;
                case "transcend":
                    handleTranscend(sender, args);
                    break;
                case "save":
                    handleSave(sender, args);
                    break;
                case "backup":
                    handleBackup(sender, args);
                    break;
                case "restore":
                    handleRestore(sender, args);
                    break;
                case "reload":
                    handleReload(sender);
                    break;
                case "migrate":
                    handleMigrate(sender);
                    break;
                default:
                    sendAdminHelp(sender);
            }
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "명령 실행 중 오류: " + ex.getMessage());
        }

        return true;
    }

    private void sendAdminHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "PlayerDataCore 관리자 명령어:");
        sender.sendMessage("/pdata admin view <플레이어>");
        sender.sendMessage("/pdata admin setrace <플레이어> <종족>");
        sender.sendMessage("/pdata admin setstat <플레이어> <스탯> <값>");
        sender.sendMessage("/pdata admin addpoints <플레이어> <개수>");
        sender.sendMessage("/pdata admin evolve <플레이어> <진화ID>");
        sender.sendMessage("/pdata admin transcend <플레이어> [권능]");
        sender.sendMessage("/pdata admin save [플레이어|all]");
        sender.sendMessage("/pdata admin backup <daily|weekly|monthly>");
        sender.sendMessage("/pdata admin restore <플레이어> <백업파일>");
        sender.sendMessage("/pdata admin reload");
        sender.sendMessage("/pdata admin migrate");
    }

    private void handleView(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("플레이어명을 입력하세요.");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("해당 플레이어를 찾을 수 없습니다.");
            return;
        }
        // 플레이어 데이터 출력
        sender.sendMessage(ChatColor.AQUA + "==== " + target.getName() + " 데이터 ====");
        sender.sendMessage("종족: " + raceManager.getPlayerRace(target).getName());
        sender.sendMessage("레벨: " + statsManager.getLevel(target));
        sender.sendMessage("스탯: " + statsManager.getBaseStats(target).toString());
        sender.sendMessage("초월 여부: " + (transcendenceManager.isTranscendent(target) ? "O" : "X"));
    }

    private void handleSetRace(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("사용법: /pdata admin setrace <플레이어> <종족>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        String raceId = args[2].toLowerCase();
        if (target == null) {
            sender.sendMessage("해당 플레이어를 찾을 수 없습니다.");
            return;
        }
        if (!raceManager.existsRace(raceId)) {
            sender.sendMessage("존재하지 않는 종족: " + raceId);
            return;
        }
        raceManager.setPlayerRace(target, raceManager.getRace(raceId));
        sender.sendMessage("종족 변경 완료: " + target.getName() + " → " + raceId);
    }

    private void handleSetStat(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("사용법: /pdata admin setstat <플레이어> <스탯> <값>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        String statName = args[2].toUpperCase();
        int value = Integer.parseInt(args[3]);
        if (target == null) {
            sender.sendMessage("해당 플레이어를 찾을 수 없습니다.");
            return;
        }
        StatType type;
        try {
            type = StatType.valueOf(statName);
        } catch (Exception e) {
            sender.sendMessage("유효하지 않은 스탯: " + statName);
            return;
        }
        statsManager.setBaseStat(target, type, value);
        sender.sendMessage("스탯 설정 완료: " + target.getName() + " " + type + " → " + value);
    }

    private void handleAddPoints(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("사용법: /pdata admin addpoints <플레이어> <개수>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        int points = Integer.parseInt(args[2]);
        if (target == null) {
            sender.sendMessage("해당 플레이어를 찾을 수 없습니다.");
            return;
        }
        statsManager.addAvailablePoints(target, points);
        sender.sendMessage("포인트 지급 완료: " + target.getName() + " +" + points);
    }

    private void handleEvolve(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("사용법: /pdata admin evolve <플레이어> <진화ID>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        String evolutionId = args[2];
        if (target == null) {
            sender.sendMessage("해당 플레이어를 찾을 수 없습니다.");
            return;
        }
        if (!evolutionManager.existsEvolution(evolutionId)) {
            sender.sendMessage("존재하지 않는 진화 ID: " + evolutionId);
            return;
        }
        evolutionManager.evolvePlayer(target, evolutionManager.getEvolution(evolutionId));
        sender.sendMessage("진화 완료: " + target.getName() + " → " + evolutionId);
    }

    private void handleTranscend(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("사용법: /pdata admin transcend <플레이어> [권능]");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("해당 플레이어를 찾을 수 없습니다.");
            return;
        }

        if (args.length == 2) {
            transcendenceManager.transcendPlayer(target, null);
            sender.sendMessage("초월 처리: " + target.getName());
        } else {
            try {
                transcendenceManager.transcendPlayer(target, args[2]);
                sender.sendMessage("초월 처리: " + target.getName() + " 권능: " + args[2]);
            } catch (Exception e) {
                sender.sendMessage("권능 선택 오류: " + args[2]);
            }
        }
    }

    private void handleSave(CommandSender sender, String[] args) {
        if (args.length == 1 || args[1].equalsIgnoreCase("all")) {
            playerDataManager.saveAllPlayerData();
            sender.sendMessage("모든 데이터 저장 완료.");
        } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("해당 플레이어를 찾을 수 없습니다.");
                return;
            }
            playerDataManager.savePlayerData(target);
            sender.sendMessage(target.getName() + " 데이터 저장 완료.");
        }
    }

    private void handleBackup(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("사용법: /pdata admin backup <daily|weekly|monthly>");
            return;
        }
        String type = args[1];
        switch (type) {
            case "daily":
            case "weekly":
            case "monthly":
                plugin.getBackupTask().createManualBackup(type);
                sender.sendMessage(type + " 백업 생성 완료.");
                break;
            default:
                sender.sendMessage("백업 유형이 올바르지 않습니다: " + type);
        }
    }

    private void handleRestore(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("사용법: /pdata admin restore <플레이어> <백업파일>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        String backupFile = args[2];
        if (target == null) {
            sender.sendMessage("해당 플레이어를 찾을 수 없습니다.");
            return;
        }
        playerDataManager.restorePlayerData(target, backupFile);
        sender.sendMessage("복구 완료: " + target.getName() + " " + backupFile);
    }

    private void handleReload(CommandSender sender) {
        plugin.reloadConfig();
        plugin.getConfigUtil().reload();
        // 각 매니저에 컨피그 리로드 반영
        raceManager.reloadConfig();
        statsManager.reloadConfig();
        evolutionManager.reloadConfig();
        transcendenceManager.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "플러그인 설정 리로드 완료.");
    }

    private void handleMigrate(CommandSender sender) {
        // 데이터 마이그레이션 (YAML↔MySQL)
        boolean success = playerDataManager.migrate();
        if (success) {
            sender.sendMessage("데이터 마이그레이션 성공.");
        } else {
            sender.sendMessage("데이터 마이그레이션 실패.");
        }
    }
}