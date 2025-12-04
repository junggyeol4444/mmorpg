package com.multiverse.dungeon.commands.subcommands. dungeon;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.data.enums.DungeonDifficulty;
import com.multiverse.dungeon.utils.MessageUtils;
import com.multiverse.dungeon.utils.ValidationUtils;
import org.bukkit.entity.Player;

/**
 * /dungeon enter <dungeonId> [난이도] 서브커맨드
 */
public class EnterSubCommand {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public EnterSubCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 실행
     */
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            MessageUtils.sendError(player, "사용법: /dungeon enter <던전ID> [난이도]");
            return;
        }

        String dungeonId = args[0];
        var dungeon = plugin.getDungeonManager().getDungeon(dungeonId);

        if (dungeon == null) {
            MessageUtils.sendError(player, "던전을 찾을 수 없습니다: " + dungeonId);
            return;
        }

        // 파티 확인
        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            MessageUtils.sendError(player, "먼저 파티를 생성해야 합니다.");
            player.sendMessage("§e/party create");
            return;
        }

        // 파티 리더 확인
        if (! party.isLeader(player. getUniqueId())) {
            MessageUtils.sendError(player, "파티 리더만 던전에 입장할 수 있습니다.");
            return;
        }

        // 난이도 확인
        DungeonDifficulty difficulty = DungeonDifficulty. NORMAL;
        if (args.length >= 2) {
            if (! ValidationUtils.isValidDifficulty(args[1])) {
                MessageUtils.sendError(player, "알 수 없는 난이도: " + args[1]);
                player.sendMessage("§e난이도: EASY, NORMAL, HARD, EXTREME");
                return;
            }
            difficulty = DungeonDifficulty.valueOf(args[1].toUpperCase());
        }

        // 던전 설정 확인
        if (! dungeon.isFullyConfigured()) {
            MessageUtils. sendError(player, "이 던전은 아직 준비 중입니다.");
            return;
        }

        // 플레이어 레벨 확인
        var playerData = plugin.getDataManager().getPlayerData(player. getUniqueId());
        if (playerData != null && playerData.getLevel() < dungeon.getRequiredLevel()) {
            MessageUtils.sendError(player, "필요 레벨에 도달하지 못했습니다.");
            player.sendMessage("§e필요 레벨: " + dungeon.getRequiredLevel() + "Lv (현재: " + playerData.getLevel() + "Lv)");
            return;
        }

        // 입장 제한 확인
        if (playerData != null) {
            int dailyRuns = playerData.getTodaysRunCount(dungeonId);
            int dailyLimit = dungeon.getDailyLimit(). getLimit();
            
            if (dailyRuns >= dailyLimit) {
                MessageUtils.sendError(player, "일일 입장 제한에 도달했습니다.");
                player.sendMessage("§e제한: " + dailyLimit + "회 (오늘 " + dailyRuns + "회 진행)");
                return;
            }
        }

        // 인스턴스 생성
        var instance = plugin.getInstanceManager().createInstance(dungeonId, party. getPartyId(), difficulty);
        if (instance == null) {
            MessageUtils. sendError(player, "던전 인스턴스 생성 실패");
            return;
        }

        // 모든 파티원을 인스턴스에 추가
        for (var memberId : party.getMembers()) {
            plugin.getInstanceManager().addPlayerToInstance(instance. getInstanceId(), 
                org.bukkit. Bukkit.getPlayer(memberId));
        }

        MessageUtils.sendSuccess(player, "던전에 입장했습니다!");
        for (var memberId : party.getMembers()) {
            var member = org.bukkit.Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage("§a[파티] 던전에 입장했습니다!  (§b" + difficulty.getDisplayName() + "§a)");
            }
        }
    }
}