package com.multiverse.pvp.commands;

import com.multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPArena;
import com.multiverse.pvp.data.PvPZone;
import com.multiverse.pvp.enums.ArenaType;
import com.multiverse. pvp.enums.ZoneType;
import com.multiverse. pvp.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit. command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java. util.List;
import java.util. Map;
import java.util. UUID;

public class PvPAdminCommand {

    private final PvPCore plugin;
    
    // 지역 설정을 위한 임시 저장소 (corner1, corner2)
    private final Map<UUID, org.bukkit.Location> corner1Map = new HashMap<>();
    private final Map<UUID, org.bukkit.Location> corner2Map = new HashMap<>();

    public PvPAdminCommand(PvPCore plugin) {
        this. plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (! sender.hasPermission("pvp.admin")) {
            MessageUtil.sendMessage(sender, "&c권한이 없습니다.");
            return true;
        }

        if (args.length == 0) {
            sendAdminHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;
            case "arena": 
                handleArena(sender, args);
                break;
            case "zone": 
                handleZone(sender, args);
                break;
            case "season": 
                handleSeason(sender, args);
                break;
            case "setspawn":
                handleSetSpawn(sender, args);
                break;
            case "setlobby":
                handleSetLobby(sender, args);
                break;
            case "setcorner":
                handleSetCorner(sender, args);
                break;
            case "reset":
                handleReset(sender, args);
                break;
            case "give":
                handleGive(sender, args);
                break;
            default:
                sendAdminHelp(sender);
                break;
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        plugin.reload();
        MessageUtil.sendMessage(sender, "&aPvPCore 설정이 리로드되었습니다.");
    }

    private void handleArena(CommandSender sender, String[] args) {
        if (args. length < 2) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin arena <create|delete|list|info|setspawn|setlobby|setcorner>");
            return;
        }

        String action = args[1]. toLowerCase();

        switch (action) {
            case "create":
                handleArenaCreate(sender, args);
                break;
            case "delete":
                handleArenaDelete(sender, args);
                break;
            case "list":
                handleArenaList(sender);
                break;
            case "info": 
                handleArenaInfo(sender, args);
                break;
            case "setspawn": 
                handleArenaSetSpawn(sender, args);
                break;
            case "setlobby":
                handleArenaSetLobby(sender, args);
                break;
            case "setcorner":
                handleArenaSetCorner(sender, args);
                break;
            case "addspawn":
                handleArenaAddSpawn(sender, args);
                break;
            default:
                MessageUtil.sendMessage(sender, "&c알 수 없는 아레나 명령어입니다.");
                break;
        }
    }

    private void handleArenaCreate(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil. sendMessage(sender, "&c플레이어만 사용할 수 있습니다.");
            return;
        }

        if (args.length < 4) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin arena create <이름> <타입>");
            MessageUtil.sendMessage(sender, "&7타입:  DUEL_1V1, TEAM_DEATHMATCH, BATTLE_ROYALE, CAPTURE_POINT, KING_OF_HILL");
            return;
        }

        Player player = (Player) sender;
        String arenaName = args[2];

        // 중복 이름 체크
        if (plugin.getArenaManager().getArenaByName(arenaName) != null) {
            MessageUtil. sendMessage(sender, "&c이미 존재하는 아레나 이름입니다:  " + arenaName);
            return;
        }

        ArenaType type;
        try {
            type = ArenaType.valueOf(args[3]. toUpperCase());
        } catch (IllegalArgumentException e) {
            MessageUtil. sendMessage(sender, "&c올바른 아레나 타입을 입력해주세요.");
            MessageUtil.sendMessage(sender, "&7타입: DUEL_1V1, TEAM_DEATHMATCH, BATTLE_ROYALE, CAPTURE_POINT, KING_OF_HILL");
            return;
        }

        plugin.getArenaManager().createArena(arenaName, type, player. getLocation());
        MessageUtil.sendMessage(sender, "&a아레나 '" + arenaName + "' (" + type. getDisplayName() + ")이(가) 생성되었습니다.");
        MessageUtil.sendMessage(sender, "&7스폰 포인트와 경계를 설정해주세요.");
        MessageUtil.sendMessage(sender, "&7/pvp admin arena setcorner <아레나> <1|2>");
        MessageUtil.sendMessage(sender, "&7/pvp admin arena addspawn <아레나>");
    }

    private void handleArenaDelete(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin arena delete <이름>");
            return;
        }

        String arenaName = args[2];
        PvPArena arena = plugin.getArenaManager().getArenaByName(arenaName);

        if (arena == null) {
            MessageUtil.sendMessage(sender, "&c아레나를 찾을 수 없습니다:  " + arenaName);
            return;
        }

        plugin.getArenaManager().deleteArena(arena. getArenaId());
        MessageUtil.sendMessage(sender, "&a아레나 '" + arenaName + "'이(가) 삭제되었습니다.");
    }

    private void handleArenaList(CommandSender sender) {
        List<PvPArena> arenas = plugin. getArenaManager().getAllArenas();

        if (arenas.isEmpty()) {
            MessageUtil.sendMessage(sender, "&c등록된 아레나가 없습니다.");
            return;
        }

        MessageUtil. sendMessage(sender, "&6&l========== 아레나 목록 ==========");
        for (PvPArena arena : arenas) {
            String status = arena.getStatus().getDisplayName();
            String type = arena.getType().getDisplayName();
            int players = arena.getPlayers().size();
            int maxPlayers = arena. getMaxPlayers();

            MessageUtil.sendMessage(sender, "&e" + arena.getArenaName() + 
                    " &7[" + type + "] &f" + players + "/" + maxPlayers + " &7(" + status + ")");
        }
        MessageUtil.sendMessage(sender, "&6&l==================================");
    }

    private void handleArenaInfo(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin arena info <이름>");
            return;
        }

        String arenaName = args[2];
        PvPArena arena = plugin.getArenaManager().getArenaByName(arenaName);

        if (arena == null) {
            MessageUtil. sendMessage(sender, "&c아레나를 찾을 수 없습니다: " + arenaName);
            return;
        }

        MessageUtil.sendMessage(sender, "&6&l========== 아레나 정보 ==========");
        MessageUtil.sendMessage(sender, "&e이름: &f" + arena. getArenaName());
        MessageUtil. sendMessage(sender, "&eID: &f" + arena.getArenaId().toString());
        MessageUtil.sendMessage(sender, "&e타입: &f" + arena.getType().getDisplayName());
        MessageUtil. sendMessage(sender, "&e상태: &f" + arena.getStatus().getDisplayName());
        MessageUtil. sendMessage(sender, "&e월드:  &f" + arena.getWorldName());
        MessageUtil. sendMessage(sender, "&e인원: &f" + arena.getMinPlayers() + " ~ " + arena.getMaxPlayers());
        MessageUtil.sendMessage(sender, "&e팀 크기: &f" + arena.getTeamSize());
        MessageUtil.sendMessage(sender, "&e경기 시간: &f" + arena.getMatchDuration() + "초");
        MessageUtil.sendMessage(sender, "&e준비 시간: &f" + arena.getPreparationTime() + "초");
        MessageUtil.sendMessage(sender, "&e스폰 포인트: &f" + arena.getSpawnPoints().size() + "개");
        MessageUtil.sendMessage(sender, "&e현재 플레이어: &f" + arena.getPlayers().size() + "명");
        MessageUtil.sendMessage(sender, "&6&l==================================");
    }

    private void handleArenaSetSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil. sendMessage(sender, "&c플레이어만 사용할 수 있습니다.");
            return;
        }

        if (args.length < 3) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin arena setspawn <아레나>");
            return;
        }

        Player player = (Player) sender;
        String arenaName = args[2];
        PvPArena arena = plugin. getArenaManager().getArenaByName(arenaName);

        if (arena == null) {
            MessageUtil.sendMessage(sender, "&c아레나를 찾을 수 없습니다: " + arenaName);
            return;
        }

        arena.setSpectatorSpawn(player.getLocation());
        plugin.getArenaStorage().saveArena(arena);
        MessageUtil.sendMessage(sender, "&a관전자 스폰이 설정되었습니다.");
    }

    private void handleArenaSetLobby(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil. sendMessage(sender, "&c플레이어만 사용할 수 있습니다.");
            return;
        }

        if (args.length < 3) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin arena setlobby <아레나>");
            return;
        }

        Player player = (Player) sender;
        String arenaName = args[2];
        PvPArena arena = plugin.getArenaManager().getArenaByName(arenaName);

        if (arena == null) {
            MessageUtil. sendMessage(sender, "&c아레나를 찾을 수 없습니다: " + arenaName);
            return;
        }

        arena. setLobby(player.getLocation());
        plugin.getArenaStorage().saveArena(arena);
        MessageUtil.sendMessage(sender, "&a로비 위치가 설정되었습니다.");
    }

    private void handleArenaSetCorner(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil. sendMessage(sender, "&c플레이어만 사용할 수 있습니다.");
            return;
        }

        if (args.length < 4) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin arena setcorner <아레나> <1|2>");
            return;
        }

        Player player = (Player) sender;
        String arenaName = args[2];
        PvPArena arena = plugin.getArenaManager().getArenaByName(arenaName);

        if (arena == null) {
            MessageUtil.sendMessage(sender, "&c아레나를 찾을 수 없습니다:  " + arenaName);
            return;
        }

        int cornerNum;
        try {
            cornerNum = Integer.parseInt(args[3]);
            if (cornerNum != 1 && cornerNum != 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            MessageUtil. sendMessage(sender, "&c코너는 1 또는 2를 입력해주세요.");
            return;
        }

        if (cornerNum == 1) {
            arena.setCorner1(player.getLocation());
            MessageUtil.sendMessage(sender, "&a코너 1이 설정되었습니다.");
        } else {
            arena.setCorner2(player.getLocation());
            MessageUtil.sendMessage(sender, "&a코너 2가 설정되었습니다.");
        }

        plugin.getArenaStorage().saveArena(arena);
    }

    private void handleArenaAddSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "&c플레이어만 사용할 수 있습니다.");
            return;
        }

        if (args.length < 3) {
            MessageUtil. sendMessage(sender, "&c사용법: /pvp admin arena addspawn <아레나>");
            return;
        }

        Player player = (Player) sender;
        String arenaName = args[2];
        PvPArena arena = plugin.getArenaManager().getArenaByName(arenaName);

        if (arena == null) {
            MessageUtil.sendMessage(sender, "&c아레나를 찾을 수 없습니다:  " + arenaName);
            return;
        }

        arena.getSpawnPoints().add(player.getLocation());
        plugin.getArenaStorage().saveArena(arena);
        MessageUtil. sendMessage(sender, "&a스폰 포인트가 추가되었습니다.  (총 " + arena. getSpawnPoints().size() + "개)");
    }

    private void handleZone(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin zone <create|delete|list|info|setcorner>");
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "create":
                handleZoneCreate(sender, args);
                break;
            case "delete":
                handleZoneDelete(sender, args);
                break;
            case "list":
                handleZoneList(sender);
                break;
            case "info": 
                handleZoneInfo(sender, args);
                break;
            case "setcorner": 
                handleZoneSetCorner(sender, args);
                break;
            default:
                MessageUtil.sendMessage(sender, "&c알 수 없는 지역 명령어입니다.");
                break;
        }
    }

    private void handleZoneCreate(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "&c플레이어만 사용할 수 있습니다.");
            return;
        }

        if (args. length < 4) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin zone create <이름> <타입>");
            MessageUtil.sendMessage(sender, "&7타입: SAFE, COMBAT, CHAOS");
            return;
        }

        Player player = (Player) sender;
        String zoneName = args[2];

        // 중복 이름 체크
        if (plugin.getZoneManager().getZoneByName(zoneName) != null) {
            MessageUtil.sendMessage(sender, "&c이미 존재하는 지역 이름입니다: " + zoneName);
            return;
        }

        ZoneType type;
        try {
            type = ZoneType.valueOf(args[3].toUpperCase());
        } catch (IllegalArgumentException e) {
            MessageUtil.sendMessage(sender, "&c올바른 지역 타입을 입력해주세요.");
            MessageUtil.sendMessage(sender, "&7타입: SAFE, COMBAT, CHAOS");
            return;
        }

        // 코너 체크
        org.bukkit.Location c1 = corner1Map.get(player.getUniqueId());
        org.bukkit.Location c2 = corner2Map. get(player.getUniqueId());

        if (c1 == null || c2 == null) {
            MessageUtil.sendMessage(sender, "&c먼저 코너를 설정해주세요.");
            MessageUtil.sendMessage(sender, "&7/pvp admin setcorner <1|2>");
            return;
        }

        plugin.getZoneManager().createZone(zoneName, type, c1, c2);
        
        // 임시 저장소 정리
        corner1Map.remove(player.getUniqueId());
        corner2Map.remove(player.getUniqueId());

        MessageUtil.sendMessage(sender, "&a지역 '" + zoneName + "' (" + type.getDisplayName() + ")이(가) 생성되었습니다.");
    }

    private void handleZoneDelete(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin zone delete <이름>");
            return;
        }

        String zoneName = args[2];
        PvPZone zone = plugin. getZoneManager().getZoneByName(zoneName);

        if (zone == null) {
            MessageUtil.sendMessage(sender, "&c지역을 찾을 수 없습니다:  " + zoneName);
            return;
        }

        plugin.getZoneManager().deleteZone(zone.getZoneId());
        MessageUtil.sendMessage(sender, "&a지역 '" + zoneName + "'이(가) 삭제되었습니다.");
    }

    private void handleZoneList(CommandSender sender) {
        List<PvPZone> zones = plugin.getZoneManager().getAllZones();

        if (zones.isEmpty()) {
            MessageUtil.sendMessage(sender, "&c등록된 지역이 없습니다.");
            return;
        }

        MessageUtil.sendMessage(sender, "&6&l========== 지역 목록 ==========");
        for (PvPZone zone : zones) {
            String type = zone.getType().getDisplayName();
            String color = zone.getType().getColor();

            MessageUtil.sendMessage(sender, color + zone.getZoneName() + " &7[" + type + "] &f" + zone.getWorldName());
        }
        MessageUtil.sendMessage(sender, "&6&l================================");
    }

    private void handleZoneInfo(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtil. sendMessage(sender, "&c사용법: /pvp admin zone info <이름>");
            return;
        }

        String zoneName = args[2];
        PvPZone zone = plugin.getZoneManager().getZoneByName(zoneName);

        if (zone == null) {
            MessageUtil.sendMessage(sender, "&c지역을 찾을 수 없습니다: " + zoneName);
            return;
        }

        MessageUtil.sendMessage(sender, "&6&l========== 지역 정보 ==========");
        MessageUtil.sendMessage(sender, "&e이름: &f" + zone.getZoneName());
        MessageUtil.sendMessage(sender, "&eID: &f" + zone.getZoneId().toString());
        MessageUtil. sendMessage(sender, "&e타입:  &f" + zone.getType().getDisplayName());
        MessageUtil.sendMessage(sender, "&e월드: &f" + zone.getWorldName());
        MessageUtil.sendMessage(sender, "&e자동 PvP: &f" + (zone.isAutoPvP() ? "활성화" : "비활성화"));
        MessageUtil.sendMessage(sender, "&e보상 배율: &f" + zone.getRewardMultiplier() + "x");
        MessageUtil.sendMessage(sender, "&e경험치 배율: &f" + zone.getExpMultiplier() + "x");
        MessageUtil.sendMessage(sender, "&e레벨 제한: &f" + zone.getMinLevel() + " ~ " + zone. getMaxLevel());
        MessageUtil.sendMessage(sender, "&6&l================================");
    }

    private void handleZoneSetCorner(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "&c플레이어만 사용할 수 있습니다.");
            return;
        }

        if (args. length < 4) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin zone setcorner <지역> <1|2>");
            return;
        }

        Player player = (Player) sender;
        String zoneName = args[2];
        PvPZone zone = plugin.getZoneManager().getZoneByName(zoneName);

        if (zone == null) {
            MessageUtil.sendMessage(sender, "&c지역을 찾을 수 없습니다: " + zoneName);
            return;
        }

        int cornerNum;
        try {
            cornerNum = Integer.parseInt(args[3]);
            if (cornerNum != 1 && cornerNum != 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            MessageUtil. sendMessage(sender, "&c코너는 1 또는 2를 입력해주세요.");
            return;
        }

        if (cornerNum == 1) {
            zone.setCorner1(player. getLocation());
            MessageUtil.sendMessage(sender, "&a지역 코너 1이 설정되었습니다.");
        } else {
            zone.setCorner2(player.getLocation());
            MessageUtil.sendMessage(sender, "&a지역 코너 2가 설정되었습니다.");
        }

        plugin.getZoneStorage().saveZone(zone);
    }

    private void handleSeason(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin season <start|end|info>");
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "start":
                plugin.getSeasonManager().startNewSeason();
                MessageUtil.sendMessage(sender, "&a새로운 시즌이 시작되었습니다!");
                break;
            case "end": 
                plugin.getSeasonManager().endSeason();
                MessageUtil.sendMessage(sender, "&a현재 시즌이 종료되었습니다.");
                break;
            case "info": 
                int currentSeason = plugin. getSeasonManager().getCurrentSeason();
                long remainingTime = plugin. getSeasonManager().getRemainingTime();
                long days = remainingTime / 86400;
                long hours = (remainingTime % 86400) / 3600;

                MessageUtil.sendMessage(sender, "&6&l========== 시즌 정보 ==========");
                MessageUtil. sendMessage(sender, "&e현재 시즌: &f" + currentSeason);
                MessageUtil.sendMessage(sender, "&e남은 시간: &f" + days + "일 " + hours + "시간");
                MessageUtil.sendMessage(sender, "&6&l================================");
                break;
            default:
                MessageUtil.sendMessage(sender, "&c알 수 없는 시즌 명령어입니다.");
                break;
        }
    }

    private void handleSetSpawn(CommandSender sender, String[] args) {
        MessageUtil.sendMessage(sender, "&c사용법: /pvp admin arena setspawn <아레나>");
    }

    private void handleSetLobby(CommandSender sender, String[] args) {
        MessageUtil.sendMessage(sender, "&c사용법: /pvp admin arena setlobby <아레나>");
    }

    private void handleSetCorner(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "&c플레이어만 사용할 수 있습니다.");
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin setcorner <1|2>");
            return;
        }

        Player player = (Player) sender;
        int cornerNum;

        try {
            cornerNum = Integer. parseInt(args[1]);
            if (cornerNum != 1 && cornerNum != 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(sender, "&c코너는 1 또는 2를 입력해주세요.");
            return;
        }

        if (cornerNum == 1) {
            corner1Map.put(player.getUniqueId(), player.getLocation());
            MessageUtil.sendMessage(sender, "&a코너 1이 설정되었습니다.");
        } else {
            corner2Map.put(player.getUniqueId(), player.getLocation());
            MessageUtil.sendMessage(sender, "&a코너 2가 설정되었습니다.");
        }
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin reset <player|all> [플레이어]");
            return;
        }

        String resetType = args[1].toLowerCase();

        switch (resetType) {
            case "player":
                if (args.length < 3) {
                    MessageUtil.sendMessage(sender, "&c사용법:  /pvp admin reset player <플레이어>");
                    return;
                }
                String playerName = args[2];
                Player target = org.bukkit. Bukkit.getPlayer(playerName);
                if (target == null) {
                    MessageUtil.sendMessage(sender, "&c플레이어를 찾을 수 없습니다: " + playerName);
                    return;
                }
                plugin.getStatisticsManager().resetStatistics(target);
                plugin.getRankingManager().resetRanking(target);
                MessageUtil.sendMessage(sender, "&a" + playerName + "의 PvP 데이터가 초기화되었습니다.");
                break;
            case "all":
                plugin.getStatisticsManager().resetAllStatistics();
                plugin.getRankingManager().resetAllRankings();
                MessageUtil.sendMessage(sender, "&a모든 플레이어의 PvP 데이터가 초기화되었습니다.");
                break;
            default:
                MessageUtil.sendMessage(sender, "&c알 수 없는 초기화 타입입니다.");
                break;
        }
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 4) {
            MessageUtil.sendMessage(sender, "&c사용법: /pvp admin give <플레이어> <rating|points> <값>");
            return;
        }

        String playerName = args[1];
        Player target = org.bukkit.Bukkit.getPlayer(playerName);

        if (target == null) {
            MessageUtil.sendMessage(sender, "&c플레이어를 찾을 수 없습니다: " + playerName);
            return;
        }

        String giveType = args[2]. toLowerCase();
        int value;

        try {
            value = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(sender, "&c올바른 숫자를 입력해주세요.");
            return;
        }

        switch (giveType) {
            case "rating": 
                plugin.getRankingManager().addRating(target, value);
                MessageUtil.sendMessage(sender, "&a" + playerName + "에게 레이팅 " + value + "을(를) 지급했습니다.");
                break;
            case "points": 
                plugin.getRankingManager().addPvPPoints(target, value);
                MessageUtil.sendMessage(sender, "&a" + playerName + "에게 PvP 포인트 " + value + "을(를) 지급했습니다.");
                break;
            default:
                MessageUtil.sendMessage(sender, "&c알 수 없는 지급 타입입니다.  (rating, points)");
                break;
        }
    }

    private void sendAdminHelp(CommandSender sender) {
        MessageUtil.sendMessage(sender, "&6&l========== PvP 관리자 도움말 ==========");
        MessageUtil.sendMessage(sender, "&e/pvp admin reload &7- 설정 리로드");
        MessageUtil. sendMessage(sender, "");
        MessageUtil. sendMessage(sender, "&c[아레나 관리]");
        MessageUtil.sendMessage(sender, "&e/pvp admin arena create <이름> <타입> &7- 아레나 생성");
        MessageUtil.sendMessage(sender, "&e/pvp admin arena delete <이름> &7- 아레나 삭제");
        MessageUtil. sendMessage(sender, "&e/pvp admin arena list &7- 아레나 목록");
        MessageUtil.sendMessage(sender, "&e/pvp admin arena info <이름> &7- 아레나 정보");
        MessageUtil. sendMessage(sender, "&e/pvp admin arena setlobby <이름> &7- 로비 설정");
        MessageUtil.sendMessage(sender, "&e/pvp admin arena setcorner <이름> <1|2> &7- 경계 설정");
        MessageUtil. sendMessage(sender, "&e/pvp admin arena addspawn <이름> &7- 스폰 추가");
        MessageUtil. sendMessage(sender, "");
        MessageUtil.sendMessage(sender, "&c[지역 관리]");
        MessageUtil. sendMessage(sender, "&e/pvp admin setcorner <1|2> &7- 지역 코너 설정");
        MessageUtil. sendMessage(sender, "&e/pvp admin zone create <이름> <타입> &7- 지역 생성");
        MessageUtil.sendMessage(sender, "&e/pvp admin zone delete <이름> &7- 지역 삭제");
        MessageUtil. sendMessage(sender, "&e/pvp admin zone list &7- 지역 목록");
        MessageUtil.sendMessage(sender, "&e/pvp admin zone info <이름> &7- 지역 정보");
        MessageUtil. sendMessage(sender, "");
        MessageUtil.sendMessage(sender, "&c[시즌 관리]");
        MessageUtil.sendMessage(sender, "&e/pvp admin season start &7- 시즌 시작");
        MessageUtil.sendMessage(sender, "&e/pvp admin season end &7- 시즌 종료");
        MessageUtil. sendMessage(sender, "&e/pvp admin season info &7- 시즌 정보");
        MessageUtil.sendMessage(sender, "");
        MessageUtil.sendMessage(sender, "&c[기타]");
        MessageUtil.sendMessage(sender, "&e/pvp admin reset <player|all> [플레이어] &7- 데이터 초기화");
        MessageUtil.sendMessage(sender, "&e/pvp admin give <플레이어> <rating|points> <값> &7- 지급");
        MessageUtil.sendMessage(sender, "&6&l=========================================");
    }
}