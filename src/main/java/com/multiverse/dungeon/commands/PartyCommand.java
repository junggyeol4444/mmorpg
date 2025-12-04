package com.multiverse.dungeon.commands;

import com.multiverse.dungeon. DungeonCore;
import com.multiverse. dungeon.constants.PermissionConstants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 파티 메인 커맨드 핸들러
 * /party [subcommand] [args]
 */
public class PartyCommand implements CommandExecutor {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public PartyCommand(DungeonCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c이 커맨드는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args. length == 0) {
            sendHelp(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "create":
                handleCreate(player);
                break;
            case "invite":
                handleInvite(player, args);
                break;
            case "accept":
                handleAccept(player, args);
                break;
            case "reject":
            case "decline":
                handleReject(player, args);
                break;
            case "kick":
                handleKick(player, args);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "promote":
                handlePromote(player, args);
                break;
            case "disband":
                handleDisband(player);
                break;
            case "info":
                handleInfo(player);
                break;
            case "list":
            case "members":
                handleMembers(player);
                break;
            default:
                player.sendMessage("§c알 수 없는 서브커맨드: " + subcommand);
                sendHelp(player);
                break;
        }

        return true;
    }

    /**
     * 파티 생성
     */
    private void handleCreate(Player player) {
        var existingParty = plugin.getPartyManager().getPlayerParty(player);
        if (existingParty != null) {
            player.sendMessage("§c이미 파티에 속해있습니다.");
            player.sendMessage("§c파티를 나가려면 /party leave를 사용하세요.");
            return;
        }

        var party = plugin.getPartyManager().createParty(player);
        if (party == null) {
            player.sendMessage("§c파티 생성에 실패했습니다.");
            return;
        }

        player.sendMessage("§a파티가 생성되었습니다!");
        player.sendMessage("§b다른 플레이어를 초대하려면 /party invite <플레이어명>를 사용하세요.");
    }

    /**
     * 플레이어 초대
     */
    private void handleInvite(Player player, String[] args) {
        if (args. length < 2) {
            player.sendMessage("§c사용법: /party invite <플레이어명>");
            return;
        }

        var party = plugin.getPartyManager(). getPlayerParty(player);
        if (party == null) {
            player.sendMessage("§c파티에 속해있지 않습니다.  먼저 파티를 생성하세요.  (/party create)");
            return;
        }

        if (!party.isLeader(player. getUniqueId())) {
            player.sendMessage("§c파티 리더만 초대할 수 있습니다.");
            return;
        }

        if (party.isFull()) {
            player.sendMessage("§c파티가 만석입니다. (최대: " + party.getMaxMembers() + "명)");
            return;
        }

        String targetName = args[1];
        var target = org.bukkit. Bukkit.getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            player.sendMessage("§c플레이어를 찾을 수 없습니다: " + targetName);
            return;
        }

        if (party.hasMember(target. getUniqueId())) {
            player.sendMessage("§c이미 파티에 속한 플레이어입니다.");
            return;
        }

        // 초대 전송
        long expireTime = System.currentTimeMillis() + (300 * 1000); // 5분
        var inviteEvent = new com.multiverse.dungeon.events.PartyInviteEvent(
            party, player, target, expireTime
        );
        org.bukkit. Bukkit.getPluginManager(). callEvent(inviteEvent);

        if (! inviteEvent.isCancelled()) {
            player.sendMessage("§a" + target.getName() + "에게 초대를 보냈습니다.");
            target.sendMessage("§e" + player.getName() + "이(가) 파티 초대를 보냈습니다!");
            target.sendMessage("§e/party accept " + player.getName() + "로 수락하세요.  (제한 시간: " + inviteEvent.getRemainingTime() + "초)");
        }
    }

    /**
     * 초대 수락
     */
    private void handleAccept(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /party accept <플레이어명>");
            return;
        }

        var existingParty = plugin.getPartyManager().getPlayerParty(player);
        if (existingParty != null) {
            player.sendMessage("§c이미 파티에 속해있습니다.");
            return;
        }

        String inviterName = args[1];
        var inviter = org.bukkit.Bukkit.getPlayer(inviterName);

        if (inviter == null || !inviter.isOnline()) {
            player.sendMessage("§c플레이어를 찾을 수 없습니다: " + inviterName);
            return;
        }

        var inviterParty = plugin.getPartyManager().getPlayerParty(inviter);
        if (inviterParty == null) {
            player.sendMessage("§c초대한 플레이어가 파티에 속해있지 않습니다.");
            return;
        }

        // 초대 확인
        java.util.UUID partyId = inviterParty. getPartyId();
        if (! plugin.getInviteManager().hasActiveInvite(player. getUniqueId(), partyId)) {
            player.sendMessage("§c활성 초대가 없습니다.");
            return;
        }

        // 초대 수락
        if (plugin.getInviteManager().acceptInvite(player.getUniqueId(), partyId)) {
            plugin.getPartyManager().addPlayerToParty(partyId, player);
            
            player.sendMessage("§a파티에 참여했습니다!");
            inviter.sendMessage("§a" + player.getName() + "이(가) 파티에 참여했습니다!");
            
            // 모든 파티원에게 알림
            for (var memberId : inviterParty.getMembers()) {
                var member = org.bukkit.Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline() && ! member.getUniqueId().equals(player. getUniqueId())) {
                    member.sendMessage("§b[파티] " + player.getName() + "이(가) 파티에 참여했습니다!  (" + inviterParty.getMemberCount() + "/" + inviterParty.getMaxMembers() + ")");
                }
            }
        } else {
            player.sendMessage("§c초대 수락에 실패했습니다.");
        }
    }

    /**
     * 초대 거절
     */
    private void handleReject(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /party reject <플레이어명>");
            return;
        }

        String inviterName = args[1];
        var inviter = org.bukkit.Bukkit.getPlayer(inviterName);

        if (inviter == null || !inviter.isOnline()) {
            player.sendMessage("§c플레이어를 찾을 수 없습니다: " + inviterName);
            return;
        }

        var inviterParty = plugin. getPartyManager().getPlayerParty(inviter);
        if (inviterParty == null) {
            player.sendMessage("§c초대한 플레이어가 파티에 속해있지 않습니다.");
            return;
        }

        java.util.UUID partyId = inviterParty.getPartyId();
        if (plugin.getInviteManager(). rejectInvite(player. getUniqueId(), partyId)) {
            player.sendMessage("§a초대를 거절했습니다.");
            inviter.sendMessage("§c" + player.getName() + "이(가) 파티 초대를 거절했습니다.");
        } else {
            player.sendMessage("§c초대 거절에 실패했습니다.");
        }
    }

    /**
     * 플레이어 추방
     */
    private void handleKick(Player player, String[] args) {
        if (args. length < 2) {
            player.sendMessage("§c사용법: /party kick <플레이어명>");
            return;
        }

        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            player.sendMessage("§c파티에 속해있지 않습니다.");
            return;
        }

        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage("§c파티 리더만 추방할 수 있습니다.");
            return;
        }

        String targetName = args[1];
        var target = org.bukkit.Bukkit.getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            player.sendMessage("§c플레이어를 찾을 수 없습니다: " + targetName);
            return;
        }

        if (! party.hasMember(target. getUniqueId())) {
            player.sendMessage("§c이 플레이어는 파티에 속해있지 않습니다.");
            return;
        }

        plugin.getPartyManager().kickPlayer(party. getPartyId(), target. getUniqueId());
        player.sendMessage("§a" + target.getName() + "을(를) 파티에서 추방했습니다.");
        target.sendMessage("§c파티에서 추방되었습니다.");

        // 다른 파티원에게 알림
        for (var memberId : party.getMembers()) {
            if (memberId. equals(target.getUniqueId())) continue;
            
            var member = org.bukkit.Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage("§c[파티] " + target.getName() + "이(가) 파티에서 추방되었습니다.");
            }
        }
    }

    /**
     * 파티 탈퇴
     */
    private void handleLeave(Player player) {
        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            player.sendMessage("§c파티에 속해있지 않습니다.");
            return;
        }

        // 진행 중인 던전 확인
        var instance = plugin.getInstanceManager().getPlayerInstance(player);
        if (instance != null && instance.isActive()) {
            player.sendMessage("§c진행 중인 던전에서는 파티를 나갈 수 없습니다.");
            return;
        }

        boolean isLeader = party.isLeader(player.getUniqueId());
        
        plugin.getPartyManager().removePlayerFromParty(party.getPartyId(), player. getUniqueId());
        
        player.sendMessage("§a파티를 나갔습니다.");

        if (isLeader) {
            player.sendMessage("§e리더였으므로 새로운 리더가 지정되었습니다.");
        }

        // 다른 파티원에게 알림
        for (var memberId : party.getMembers()) {
            var member = org.bukkit. Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage("§c[파티] " + player.getName() + "이(가) 파티를 나갔습니다.  (" + party.getMemberCount() + "/" + party.getMaxMembers() + ")");
            }
        }
    }

    /**
     * 리더 위임
     */
    private void handlePromote(Player player, String[] args) {
        if (args. length < 2) {
            player.sendMessage("§c사용법: /party promote <플레이어명>");
            return;
        }

        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            player.sendMessage("§c파티에 속해있지 않습니다.");
            return;
        }

        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage("§c파티 리더만 리더 위임을 할 수 있습니다.");
            return;
        }

        String targetName = args[1];
        var target = org.bukkit.Bukkit.getPlayer(targetName);

        if (target == null || ! target.isOnline()) {
            player.sendMessage("§c플레이어를 찾을 수 없습니다: " + targetName);
            return;
        }

        if (!party.hasMember(target.getUniqueId())) {
            player.sendMessage("§c이 플레이어는 파티에 속해있지 않습니다.");
            return;
        }

        if (plugin.getPartyManager().promoteLeader(party.getPartyId(), target.getUniqueId())) {
            player.sendMessage("§a" + target. getName() + "을(를) 리더로 위임했습니다.");
            target. sendMessage("§a리더로 승격되었습니다!");
            
            // 다른 파티원에게 알림
            for (var memberId : party.getMembers()) {
                if (memberId.equals(player.getUniqueId()) || memberId.equals(target. getUniqueId())) continue;
                
                var member = org.bukkit.Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline()) {
                    member.sendMessage("§b[파티] " + target. getName() + "이(가) 새로운 리더가 되었습니다.");
                }
            }
        } else {
            player.sendMessage("§c리더 위임에 실패했습니다.");
        }
    }

    /**
     * 파티 해체
     */
    private void handleDisband(Player player) {
        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            player.sendMessage("§c파티에 속해있지 않습니다.");
            return;
        }

        if (!party.isLeader(player.getUniqueId())) {
            player. sendMessage("§c파티 리더만 파티를 해체할 수 있습니다.");
            return;
        }

        var memberNames = new java.util.ArrayList<String>();
        for (var memberId : party.getMembers()) {
            var member = org.bukkit. Bukkit.getPlayer(memberId);
            if (member != null) {
                memberNames.add(member.getName());
            }
        }

        plugin.getPartyManager().disbandParty(party.getPartyId(), 
            com.multiverse.dungeon.events.PartyDisbandedEvent.DisbandReason.LEADER_COMMAND);

        player.sendMessage("§a파티를 해체했습니다.");

        // 모든 파티원에게 알림
        for (var memberId : party.getMembers()) {
            var member = org. bukkit.Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member. sendMessage("§c[파티] 파티가 해체되었습니다.");
            }
        }
    }

    /**
     * 파티 정보 조회
     */
    private void handleInfo(Player player) {
        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            player. sendMessage("§c파티에 속해있지 않습니다.");
            return;
        }

        player.sendMessage("§6=== 파티 정보 ===");
        player.sendMessage("§b파티 ID: §f" + party.getPartyId());
        player.sendMessage("§b멤버: §f" + party.getMemberCount() + "/" + party.getMaxMembers());
        player.sendMessage("§b리더: §f" + org.bukkit.Bukkit. getPlayer(party.getLeaderId()) != null ? 
            org.bukkit.Bukkit.getPlayer(party.getLeaderId()). getName() : "Unknown");
        player.sendMessage("§b공개 여부: §f" + (party.isOpen() ? "공개" : "비공개"));
    }

    /**
     * 파티 멤버 목록
     */
    private void handleMembers(Player player) {
        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            player.sendMessage("§c파티에 속해있지 않습니다.");
            return;
        }

        player.sendMessage("§6=== 파티 멤버 (" + party.getMemberCount() + "/" + party.getMaxMembers() + ") ===");
        
        for (var memberId : party.getMembers()) {
            var member = org.bukkit. Bukkit.getPlayer(memberId);
            String prefix = party.isLeader(memberId) ? "§b[리더] " : "§7";
            String online = member != null && member.isOnline() ? "§a(온라인)" : "§c(오프라인)";
            String memberName = member != null ? member. getName() : "Unknown";
            
            player.sendMessage(prefix + memberName + " " + online);
        }
    }

    /**
     * 도움말 표시
     */
    private void sendHelp(Player player) {
        player.sendMessage("§6=== 파티 커맨드 도움말 ===");
        player.sendMessage("§b/party create §f- 파티 생성");
        player.sendMessage("§b/party invite <플레이어명> §f- 플레이어 초대");
        player.sendMessage("§b/party accept <플레이어명> §f- 초대 수락");
        player.sendMessage("§b/party reject <플레이어명> §f- 초대 거절");
        player.sendMessage("§b/party kick <플레이어명> §f- 플레이어 추방");
        player.sendMessage("§b/party leave §f- 파티 탈퇴");
        player.sendMessage("§b/party promote <플레이어명> §f- 리더 위임");
        player.sendMessage("§b/party disband §f- 파티 해체");
        player.sendMessage("§b/party info §f- 파티 정보");
        player.sendMessage("§b/party members §f- 멤버 목록");
    }
}