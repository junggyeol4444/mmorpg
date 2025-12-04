package com.multiverse.dungeon. commands;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.constants.MessageConstants;
import com.multiverse. dungeon.constants. PermissionConstants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 던전 메인 커맨드 핸들러
 * /dungeon [subcommand] [args]
 */
public class DungeonCommand implements CommandExecutor {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public DungeonCommand(DungeonCore plugin) {
        this. plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c이 커맨드는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subcommand = args[0]. toLowerCase();

        switch (subcommand) {
            case "list":
                handleList(player);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "enter":
                handleEnter(player, args);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "stats":
                handleStats(player, args);
                break;
            case "ranking":
                handleRanking(player, args);
                break;
            case "admin":
                handleAdmin(player, args);
                break;
            default:
                player.sendMessage("§c알 수 없는 서브커맨드: " + subcommand);
                sendHelp(player);
                break;
        }

        return true;
    }

    /**
     * 던전 목록 조회
     */
    private void handleList(Player player) {
        var dungeons = plugin.getDungeonManager().getEnabledDungeons();

        player.sendMessage("§6=== 던전 목록 ===");
        if (dungeons.isEmpty()) {
            player.sendMessage("§c사용 가능한 던전이 없습니다.");
            return;
        }

        for (var dungeon : dungeons) {
            String info = "§b" + dungeon.getName() + " §7(" + dungeon.getType(). getDisplayName() + ")";
            info += " §8[" + dungeon.getRequiredLevel() + "Lv]";
            info += " §a/dungeon enter " + dungeon.getDungeonId();
            player.sendMessage(info);
        }
    }

    /**
     * 던전 정보 조회
     */
    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player. sendMessage("§c사용법: /dungeon info <던전ID>");
            return;
        }

        String dungeonId = args[1];
        var dungeon = plugin.getDungeonManager().getDungeon(dungeonId);

        if (dungeon == null) {
            player.sendMessage("§c던전을 찾을 수 없습니다: " + dungeonId);
            return;
        }

        player.sendMessage("§6=== " + dungeon.getName() + " ===");
        player.sendMessage("§b타입: §f" + dungeon.getType(). getDisplayName());
        player.sendMessage("§b난이도: §f" + dungeon.getDifficulty().getDisplayName());
        player.sendMessage("§b필요 레벨: §f" + dungeon.getRequiredLevel());
        player.sendMessage("§b파티 크기: §f" + dungeon. getMinPlayers() + " ~ " + dungeon.getMaxPlayers());
        player.sendMessage("§b제한 시간: §f" + dungeon.getTimeLimitFormatted());
        player.sendMessage("§b보스: §f" + dungeon.getBossCount() + "마리");
        if (! dungeon.getDescription().isEmpty()) {
            player.sendMessage("§b설명: §f" + dungeon.getDescription());
        }
    }

    /**
     * 던전 입장
     */
    private void handleEnter(Player player, String[] args) {
        if (args. length < 2) {
            player.sendMessage("§c사용법: /dungeon enter <던전ID> [난이도]");
            return;
        }

        String dungeonId = args[1];
        var dungeon = plugin.getDungeonManager().getDungeon(dungeonId);

        if (dungeon == null) {
            player.sendMessage("§c던전을 찾을 수 없습니다: " + dungeonId);
            return;
        }

        // 파티 확인
        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            player.sendMessage("§c먼저 파티를 생성해야 합니다.  (/party create)");
            return;
        }

        // 파티 리더 확인
        if (!party.isLeader(player. getUniqueId())) {
            player.sendMessage("§c파티 리더만 던전에 입장할 수 있습니다.");
            return;
        }

        // 난이도 확인
        var difficulty = com.multiverse.dungeon.data.enums.DungeonDifficulty. NORMAL;
        if (args. length >= 3) {
            try {
                difficulty = com.multiverse.dungeon.data.enums.DungeonDifficulty.valueOf(args[2]. toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("§c알 수 없는 난이도: " + args[2]);
                return;
            }
        }

        // 입장 조건 확인
        if (! dungeon.isFullyConfigured()) {
            player.sendMessage("§c이 던전은 아직 준비 중입니다.");
            return;
        }

        // 인스턴스 생성
        var instance = plugin.getInstanceManager().createInstance(dungeonId, party. getPartyId(), difficulty);
        if (instance == null) {
            player.sendMessage("§c던전 인스턴스 생성 실패");
            return;
        }

        player.sendMessage("§a던전에 입장했습니다!");
        for (var memberId : party.getMembers()) {
            var member = org.bukkit. Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage("§a[파티] 던전에 입장했습니다!  (" + difficulty. getDisplayName() + ")");
            }
        }
    }

    /**
     * 던전 퇴장
     */
    private void handleLeave(Player player) {
        var instance = plugin.getInstanceManager().getPlayerInstance(player);
        if (instance == null) {
            player.sendMessage("§c현재 던전에 진행 중이 아닙니다.");
            return;
        }

        instance. removePlayer(player. getUniqueId());
        player.sendMessage("§a던전을 나갔습니다.");

        // 다른 파티원에게 알림
        for (var memberId : instance.getPlayers()) {
            var member = org.bukkit.Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage("§c[파티] " + player.getName() + "이(가) 던전을 나갔습니다.");
            }
        }
    }

    /**
     * 플레이어 통계 조회
     */
    private void handleStats(Player player, String[] args) {
        var playerData = plugin.getDataManager().getPlayerData(player. getUniqueId());
        if (playerData == null) {
            player.sendMessage("§c플레이어 데이터를 찾을 수 없습니다.");
            return;
        }

        player.sendMessage("§6=== 당신의 던전 통계 ===");
        player.sendMessage("§b총 입장: §f" + playerData.getTotalRuns());
        player.sendMessage("§b총 클리어: §f" + playerData.getTotalClears());
        player.sendMessage("§b총 사망: §f" + playerData. getTotalDeaths());
        player.sendMessage("§b던전 포인트: §f" + playerData.getDungeonPoints());

        if (args.length >= 2) {
            String dungeonId = args[1];
            int clears = playerData.getClearCount(dungeonId, "NORMAL");
            player.sendMessage("§b[" + dungeonId + "] 클리어: §f" + clears + "회");
        }
    }

    /**
     * 리더보드 조회
     */
    private void handleRanking(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /dungeon ranking <던전ID> [난이도]");
            return;
        }

        String dungeonId = args[1];
        var difficulty = com.multiverse.dungeon.data.enums.DungeonDifficulty.NORMAL;

        if (args.length >= 3) {
            try {
                difficulty = com.multiverse. dungeon.data.enums. DungeonDifficulty. valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("§c알 수 없는 난이도: " + args[2]);
                return;
            }
        }

        var records = plugin.getLeaderboardManager().getTopRecords(dungeonId, difficulty, 10);
        
        player.sendMessage("§6=== " + dungeonId + " (" + difficulty.getDisplayName() + ") TOP 10 ===");
        
        int rank = 1;
        for (var record : records) {
            String rankString = "§b#" + rank + " ";
            rankString += "§f" + String.join(", ", record.getPlayerNames());
            rankString += " §7- " + record.getClearTimeFormatted();
            rankString += " §8(점수: " + record.getScore() + ")";
            player. sendMessage(rankString);
            rank++;
        }
    }

    /**
     * 관리자 커맨드 (미작성)
     */
    private void handleAdmin(Player player, String[] args) {
        if (! player.hasPermission(PermissionConstants.ADMIN_DUNGEON_CREATE)) {
            player.sendMessage("§c권한이 없습니다.");
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§c사용법: /dungeon admin [create|delete|enable|disable|reload]");
            return;
        }

        player.sendMessage("§e관리자 커맨드는 아직 개발 중입니다.");
    }

    /**
     * 도움말 표시
     */
    private void sendHelp(Player player) {
        player.sendMessage("§6=== 던전 커맨드 도움말 ===");
        player.sendMessage("§b/dungeon list §f- 던전 목록 조회");
        player.sendMessage("§b/dungeon info <ID> §f- 던전 정보 조회");
        player.sendMessage("§b/dungeon enter <ID> [난이도] §f- 던전 입장");
        player.sendMessage("§b/dungeon leave §f- 던전 퇴장");
        player.sendMessage("§b/dungeon stats [ID] §f- 통계 조회");
        player.sendMessage("§b/dungeon ranking <ID> [난이도] §f- 리더보드 조회");
        player.sendMessage("§b/dungeon admin §f- 관리자 커맨드");
    }

    /**
     * 모든 인스턴스 조회 (임시)
     */
    private java.util.List<com.multiverse.dungeon.data.model.DungeonInstance> getAllInstances() {
        return new java.util.ArrayList<>();
    }
}