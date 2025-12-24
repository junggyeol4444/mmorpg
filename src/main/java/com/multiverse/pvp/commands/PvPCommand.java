package com.multiverse. pvp.commands;

import com.multiverse.pvp. PvPCore;
import com.multiverse.pvp. data.*;
import com.multiverse.pvp. enums.*;
import com.multiverse.pvp.gui.*;
import com.multiverse.pvp.utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit. entity.Player;

import java.util.*;

public class PvPCommand implements CommandExecutor, TabCompleter {

    private final PvPCore plugin;
    private final PvPAdminCommand adminCommand;

    public PvPCommand(PvPCore plugin) {
        this.plugin = plugin;
        this. adminCommand = new PvPAdminCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // 어드민 명령어 처리
        if (subCommand.equals("admin")) {
            if (! sender.hasPermission("pvp.admin")) {
                MessageUtil.sendMessage(sender, "&c권한이 없습니다.");
                return true;
            }
            return adminCommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }

        // 플레이어 전용 명령어
        if (!(sender instanceof Player)) {
            MessageUtil. sendMessage(sender, "&c플레이어만 사용할 수 있는 명령어입니다.");
            return true;
        }

        Player player = (Player) sender;

        switch (subCommand) {
            case "toggle":
                handleToggle(player);
                break;
            case "stats":
                handleStats(player, args);
                break;
            case "ranking":
                handleRanking(player);
                break;
            case "arena":
                handleArena(player, args);
                break;
            case "duel":
                handleDuel(player, args);
                break;
            case "surrender":
                handleSurrender(player);
                break;
            case "titles":
                handleTitles(player);
                break;
            case "queue":
                handleQueue(player, args);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "spectate":
                handleSpectate(player, args);
                break;
            case "accept":
                handleAccept(player);
                break;
            case "decline": 
                handleDecline(player);
                break;
            case "leaderboard":
            case "lb":
                handleLeaderboard(player, args);
                break;
            case "streak":
                handleStreak(player);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void handleToggle(Player player) {
        if (! player.hasPermission("pvp.player")) {
            MessageUtil.sendMessage(player, "&c권한이 없습니다.");
            return;
        }

        boolean currentState = plugin.getPvPModeManager().isPvPEnabled(player);
        plugin.getPvPModeManager().setPvPEnabled(player, !currentState);

        if (! currentState) {
            MessageUtil.sendMessage(player, plugin.getConfig().getString("messages.mode.enabled", "&aPvP 모드가 활성화되었습니다."));
        } else {
            MessageUtil. sendMessage(player, plugin.getConfig().getString("messages.mode.disabled", "&cPvP 모드가 비활성화되었습니다."));
        }
    }

    private void handleStats(Player player, String[] args) {
        Player target = player;

        if (args. length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtil.sendMessage(player, "&c플레이어를 찾을 수 없습니다:  " + args[1]);
                return;
            }
        }

        PvPStatistics stats = plugin. getStatisticsManager().getStatistics(target);
        PvPRanking ranking = plugin.getRankingManager().getRanking(target);
        KillStreak streak = plugin.getKillStreakManager().getKillStreak(target);

        if (stats == null || ranking == null) {
            MessageUtil.sendMessage(player, "&c통계 데이터를 찾을 수 없습니다.");
            return;
        }

        StatisticsGUI gui = new StatisticsGUI(plugin);
        gui.open(player, target);
    }

    private void handleRanking(Player player) {
        RankingGUI gui = new RankingGUI(plugin);
        gui.open(player);
    }

    private void handleArena(Player player, String[] args) {
        if (args.length < 2) {
            ArenaGUI gui = new ArenaGUI(plugin);
            gui.open(player);
            return;
        }

        String arenaName = args[1];
        PvPArena arena = plugin.getArenaManager().getArenaByName(arenaName);

        if (arena == null) {
            MessageUtil.sendMessage(player, "&c아레나를 찾을 수 없습니다: " + arenaName);
            return;
        }

        if (!plugin.getArenaManager().joinArena(player, arena. getArenaId())) {
            MessageUtil.sendMessage(player, "&c아레나에 참가할 수 없습니다.");
        }
    }

    private void handleDuel(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendMessage(player, "&c사용법: /pvp duel <플레이어> [베팅금액]");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            MessageUtil.sendMessage(player, "&c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        if (target.equals(player)) {
            MessageUtil.sendMessage(player, "&c자기 자신에게 듀얼을 신청할 수 없습니다.");
            return;
        }

        // 베팅 처리
        Map<String, Double> betMoney = new HashMap<>();
        if (args. length > 2) {
            try {
                double betAmount = Double.parseDouble(args[2]);
                double maxBet = plugin.getConfig().getDouble("duel.betting.max-money", 100000. 0);

                if (betAmount > maxBet) {
                    MessageUtil.sendMessage(player, "&c최대 베팅 금액은 " + maxBet + "입니다.");
                    return;
                }

                if (betAmount > 0 && plugin.getConfig().getBoolean("duel.betting.enabled", true)) {
                    betMoney.put("default", betAmount);
                }
            } catch (NumberFormatException e) {
                MessageUtil.sendMessage(player, "&c올바른 금액을 입력해주세요.");
                return;
            }
        }

        plugin.getDuelManager().sendDuelRequest(player, target, betMoney, new ArrayList<>());
    }

    private void handleSurrender(Player player) {
        if (!plugin.getDuelManager().isInDuel(player)) {
            MessageUtil.sendMessage(player, "&c현재 듀얼 중이 아닙니다.");
            return;
        }

        plugin.getDuelManager().surrender(player);
    }

    private void handleTitles(Player player) {
        TitleGUI gui = new TitleGUI(plugin);
        gui.open(player);
    }

    private void handleQueue(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendMessage(player, "&c사용법: /pvp queue <아레나타입>");
            MessageUtil.sendMessage(player, "&7타입:  DUEL_1V1, TEAM_DEATHMATCH, BATTLE_ROYALE, CAPTURE_POINT");
            return;
        }

        try {
            ArenaType type = ArenaType.valueOf(args[1]. toUpperCase());
            plugin.getArenaManager().queueForArena(player, type);
            MessageUtil.sendMessage(player, "&a" + type.getDisplayName() + " 매칭 대기열에 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            MessageUtil.sendMessage(player, "&c올바른 아레나 타입을 입력해주세요.");
        }
    }

    private void handleLeave(Player player) {
        // 아레나 퇴장
        if (plugin.getArenaManager().isInArena(player)) {
            plugin.getArenaManager().leaveArena(player);
            MessageUtil.sendMessage(player, "&a아레나에서 퇴장했습니다.");
            return;
        }

        // 매칭 큐 취소
        if (plugin.getArenaManager().isInQueue(player)) {
            plugin.getArenaManager().cancelQueue(player);
            MessageUtil.sendMessage(player, "&a매칭 대기열에서 제거되었습니다.");
            return;
        }

        MessageUtil.sendMessage(player, "&c퇴장할 아레나나 취소할 매칭이 없습니다.");
    }

    private void handleSpectate(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendMessage(player, "&c사용법: /pvp spectate <아레나이름>");
            return;
        }

        String arenaName = args[1];
        PvPArena arena = plugin.getArenaManager().getArenaByName(arenaName);

        if (arena == null) {
            MessageUtil.sendMessage(player, "&c아레나를 찾을 수 없습니다:  " + arenaName);
            return;
        }

        plugin.getArenaManager().spectateArena(player, arena. getArenaId());
        MessageUtil. sendMessage(player, "&a" + arenaName + " 아레나를 관전합니다.");
    }

    private void handleAccept(Player player) {
        Duel pendingDuel = plugin.getDuelManager().getPendingDuelRequest(player);

        if (pendingDuel == null) {
            MessageUtil.sendMessage(player, "&c대기 중인 듀얼 요청이 없습니다.");
            return;
        }

        plugin.getDuelManager().acceptDuel(player, pendingDuel. getDuelId());
    }

    private void handleDecline(Player player) {
        Duel pendingDuel = plugin. getDuelManager().getPendingDuelRequest(player);

        if (pendingDuel == null) {
            MessageUtil. sendMessage(player, "&c대기 중인 듀얼 요청이 없습니다.");
            return;
        }

        plugin.getDuelManager().declineDuel(player, pendingDuel.getDuelId());
    }

    private void handleLeaderboard(Player player, String[] args) {
        LeaderboardType type = LeaderboardType. RATING;
        int limit = 10;

        if (args.length > 1) {
            try {
                type = LeaderboardType. valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                MessageUtil.sendMessage(player, "&c올바른 리더보드 타입을 입력해주세요.");
                MessageUtil.sendMessage(player, "&7타입: KILLS, STREAK, WINS, RATING");
                return;
            }
        }

        List<PvPRanking> topRankings = plugin.getLeaderboardManager().getSeasonTop(limit);

        MessageUtil.sendMessage(player, "&6&l========== " + type.getDisplayName() + " 리더보드 ==========");

        int rank = 1;
        for (PvPRanking ranking : topRankings) {
            String playerName = Bukkit.getOfflinePlayer(ranking. getPlayerId()).getName();
            String tierColor = ranking.getTier().getColor();

            String value;
            switch (type) {
                case KILLS:
                    value = String. valueOf(ranking.getKills());
                    break;
                case STREAK:
                    value = String.valueOf(ranking.getWinStreak());
                    break;
                case WINS: 
                    value = String.valueOf(ranking. getWins());
                    break;
                default:
                    value = String.valueOf(ranking.getRating());
                    break;
            }

            MessageUtil.sendMessage(player, "&e" + rank + ".  " + tierColor + playerName + " &7- &f" + value);
            rank++;
        }

        MessageUtil.sendMessage(player, "&6&l==========================================");
    }

    private void handleStreak(Player player) {
        KillStreak streak = plugin.getKillStreakManager().getKillStreak(player);

        if (streak == null) {
            MessageUtil.sendMessage(player, "&c킬 스트릭 데이터를 찾을 수 없습니다.");
            return;
        }

        MessageUtil.sendMessage(player, "&6&l========== 킬 스트릭 ==========");
        MessageUtil.sendMessage(player, "&e현재 스트릭: &f" + streak. getCurrentStreak());
        MessageUtil. sendMessage(player, "&e최고 스트릭: &f" + streak. getBestStreak());

        if (streak.getCurrentLevel() != null) {
            MessageUtil.sendMessage(player, "&e현재 레벨: " + streak.getCurrentLevel().getAnnouncement());
        }

        MessageUtil.sendMessage(player, "&6&l================================");
    }

    private void sendHelp(CommandSender sender) {
        MessageUtil. sendMessage(sender, "&6&l========== PvP 도움말 ==========");
        MessageUtil.sendMessage(sender, "&e/pvp toggle &7- PvP 모드 토글");
        MessageUtil. sendMessage(sender, "&e/pvp stats [플레이어] &7- 통계 확인");
        MessageUtil.sendMessage(sender, "&e/pvp ranking &7- 랭킹 확인");
        MessageUtil. sendMessage(sender, "&e/pvp arena [아레나] &7- 아레나 참가");
        MessageUtil. sendMessage(sender, "&e/pvp duel <플레이어> [베팅] &7- 듀얼 신청");
        MessageUtil.sendMessage(sender, "&e/pvp accept &7- 듀얼 수락");
        MessageUtil.sendMessage(sender, "&e/pvp decline &7- 듀얼 거절");
        MessageUtil.sendMessage(sender, "&e/pvp surrender &7- 듀얼 항복");
        MessageUtil.sendMessage(sender, "&e/pvp titles &7- 타이틀 관리");
        MessageUtil.sendMessage(sender, "&e/pvp queue <타입> &7- 매칭 대기열 등록");
        MessageUtil. sendMessage(sender, "&e/pvp leave &7- 아레나 퇴장/매칭 취소");
        MessageUtil.sendMessage(sender, "&e/pvp spectate <아레나> &7- 아레나 관전");
        MessageUtil.sendMessage(sender, "&e/pvp leaderboard [타입] &7- 리더보드 확인");
        MessageUtil.sendMessage(sender, "&e/pvp streak &7- 킬 스트릭 확인");

        if (sender. hasPermission("pvp.admin")) {
            MessageUtil. sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "&c&l[관리자 명령어]");
            MessageUtil.sendMessage(sender, "&e/pvp admin reload &7- 설정 리로드");
            MessageUtil.sendMessage(sender, "&e/pvp admin arena create <이름> <타입> &7- 아레나 생성");
            MessageUtil.sendMessage(sender, "&e/pvp admin zone create <이름> <타입> &7- 지역 생성");
            MessageUtil. sendMessage(sender, "&e/pvp admin season start &7- 시즌 시작");
        }

        MessageUtil.sendMessage(sender, "&6&l==================================");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args. length == 1) {
            List<String> subCommands = Arrays.asList(
                    "toggle", "stats", "ranking", "arena", "duel", "surrender",
                    "titles", "queue", "leave", "spectate", "accept", "decline",
                    "leaderboard", "streak"
            );

            if (sender. hasPermission("pvp.admin")) {
                subCommands = new ArrayList<>(subCommands);
                subCommands.add("admin");
            }

            for (String sub : subCommands) {
                if (sub.startsWith(args[0]. toLowerCase())) {
                    completions. add(sub);
                }
            }
        } else if (args. length == 2) {
            switch (args[0].toLowerCase()) {
                case "stats":
                case "duel":
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player. getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(player. getName());
                        }
                    }
                    break;
                case "arena":
                case "spectate":
                    for (PvPArena arena : plugin.getArenaManager().getAllArenas()) {
                        if (arena.getArenaName().toLowerCase().startsWith(args[1]. toLowerCase())) {
                            completions. add(arena.getArenaName());
                        }
                    }
                    break;
                case "queue":
                    for (ArenaType type : ArenaType.values()) {
                        if (type.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(type.name());
                        }
                    }
                    break;
                case "leaderboard":
                case "lb":
                    for (LeaderboardType type : LeaderboardType.values()) {
                        if (type.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(type.name());
                        }
                    }
                    break;
                case "admin":
                    if (sender.hasPermission("pvp. admin")) {
                        List<String> adminSubs = Arrays.asList("reload", "arena", "zone", "season");
                        for (String sub : adminSubs) {
                            if (sub.startsWith(args[1].toLowerCase())) {
                                completions.add(sub);
                            }
                        }
                    }
                    break;
            }
        } else if (args. length == 3 && args[0]. equalsIgnoreCase("admin")) {
            if (sender.hasPermission("pvp.admin")) {
                switch (args[1]. toLowerCase()) {
                    case "arena":
                    case "zone": 
                        if ("create".startsWith(args[2].toLowerCase())) {
                            completions.add("create");
                        }
                        if ("delete".startsWith(args[2].toLowerCase())) {
                            completions.add("delete");
                        }
                        if ("list".startsWith(args[2]. toLowerCase())) {
                            completions. add("list");
                        }
                        break;
                    case "season": 
                        if ("start".startsWith(args[2].toLowerCase())) {
                            completions.add("start");
                        }
                        if ("end".startsWith(args[2].toLowerCase())) {
                            completions.add("end");
                        }
                        break;
                }
            }
        } else if (args. length == 5 && args[0].equalsIgnoreCase("admin")) {
            if (sender.hasPermission("pvp.admin")) {
                if (args[1].equalsIgnoreCase("arena") && args[2].equalsIgnoreCase("create")) {
                    for (ArenaType type : ArenaType.values()) {
                        if (type.name().toLowerCase().startsWith(args[4].toLowerCase())) {
                            completions.add(type. name());
                        }
                    }
                } else if (args[1].equalsIgnoreCase("zone") && args[2]. equalsIgnoreCase("create")) {
                    for (ZoneType type : ZoneType.values()) {
                        if (type.name().toLowerCase().startsWith(args[4].toLowerCase())) {
                            completions.add(type.name());
                        }
                    }
                }
            }
        }

        return completions;
    }
}