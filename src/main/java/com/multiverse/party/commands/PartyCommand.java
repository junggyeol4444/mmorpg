package com.multiverse.party. commands;

import com. multiverse.party. PartyCore;
import com. multiverse.party. models.Party;
import com.multiverse. party.models.PartyBuff;
import com. multiverse.party. models.PartyInvite;
import com. multiverse.party. models.PartyStatistics;
import com.multiverse.party.models.MemberStatistics;
import com.multiverse.party.models.enums.*;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;

import java.util. List;
import java.util.UUID;

public class PartyCommand {

    private final PartyCore plugin;

    public PartyCommand(PartyCore plugin) {
        this. plugin = plugin;
    }

    // ==================== 파티 생성 ====================
    public boolean handleCreate(Player player, String[] args) {
        // 이미 파티에 있는지 확인
        if (plugin.getPartyManager().isInParty(player)) {
            player. sendMessage(plugin. getMessageUtil().getMessage("party.already-in-party"));
            return true;
        }

        // 쿨다운 확인
        if (plugin.getPartyManager().isOnCooldown(player. getUniqueId())) {
            long remaining = plugin.getPartyManager().getRemainingCooldown(player.getUniqueId());
            player.sendMessage(plugin.getMessageUtil().getMessage("party.create-cooldown",
                    "%time%", String.valueOf(remaining)));
            return true;
        }

        // 생성 비용 확인
        int cost = plugin.getConfig().getInt("party.creation. cost", 0);
        if (cost > 0) {
            if (! plugin.getIntegrationManager().isPlayerDataCoreEnabled()) {
                player. sendMessage(plugin. getMessageUtil().getMessage("general.economy-not-available"));
                return true;
            }
            
            double balance = plugin.getIntegrationManager().getPlayerDataCoreIntegration().getBalance(player);
            if (balance < cost) {
                player.sendMessage(plugin.getMessageUtil().getMessage("party.not-enough-money",
                        "%cost%", String.valueOf(cost)));
                return true;
            }
            
            plugin.getIntegrationManager().getPlayerDataCoreIntegration().withdrawBalance(player, cost);
        }

        // 파티 이름 처리
        String partyName = null;
        if (args.length > 0) {
            partyName = String.join(" ", args);
            
            // 이름 길이 제한
            if (partyName.length() > 32) {
                player.sendMessage(plugin.getMessageUtil().getMessage("party.name-too-long"));
                return true;
            }
            
            // 중복 이름 확인
            if (plugin. getPartyManager().isPartyNameTaken(partyName)) {
                player.sendMessage(plugin.getMessageUtil().getMessage("party.name-taken"));
                return true;
            }
        }

        // 파티 생성
        Party party;
        if (partyName != null) {
            party = plugin.getPartyManager().createParty(player, partyName);
        } else {
            party = plugin. getPartyManager().createParty(player);
        }

        if (party != null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.created"));
            
            // 쿨다운 설정
            int cooldown = plugin.getConfig().getInt("party.creation.cooldown", 60);
            plugin.getPartyManager().setCooldown(player. getUniqueId(), cooldown);
        } else {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.create-failed"));
        }

        return true;
    }

    // ==================== 파티 해체 ====================
    public boolean handleDisband(Player player, String[] args) {
        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 리더 확인
        if (! plugin.getPartyRoleManager().isLeader(party, player. getUniqueId())) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-leader"));
            return true;
        }

        // 파티 해체
        plugin.getPartyManager().disbandParty(party);
        player.sendMessage(plugin.getMessageUtil().getMessage("party.disbanded"));

        return true;
    }

    // ==================== 플레이어 초대 ====================
    public boolean handleInvite(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().getMessage("usage.invite"));
            return true;
        }

        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 초대 권한 확인
        if (!plugin. getPartyRoleManager().hasPermission(party, player.getUniqueId(), PartyPermission.INVITE_MEMBERS)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.no-invite-permission"));
            return true;
        }

        // 파티 인원 확인
        if (party.getMembers().size() >= party.getMaxMembers()) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.party-full"));
            return true;
        }

        // 대상 플레이어 찾기
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("general.player-not-found",
                    "%player%", args[0]));
            return true;
        }

        // 자기 자신 초대 불가
        if (target. equals(player)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.cannot-invite-self"));
            return true;
        }

        // 이미 파티에 있는지 확인
        if (plugin.getPartyManager().isInParty(target)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.target-in-party",
                    "%player%", target.getName()));
            return true;
        }

        // 이미 초대를 보냈는지 확인
        if (plugin.getPartyInviteManager().hasPendingInvite(target, party)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.already-invited",
                    "%player%", target.getName()));
            return true;
        }

        // 초대 개수 제한 확인
        int maxPending = plugin.getConfig().getInt("party.invite.max-pending", 5);
        if (plugin.getPartyInviteManager().getPendingInviteCount(target) >= maxPending) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.target-too-many-invites",
                    "%player%", target.getName()));
            return true;
        }

        // 초대 보내기
        boolean success = plugin.getPartyInviteManager().sendInvite(party, player, target);
        
        if (success) {
            player. sendMessage(plugin. getMessageUtil().getMessage("invite.sent",
                    "%player%", target.getName()));
            
            // 대상에게 초대 메시지
            target.sendMessage(plugin.getMessageUtil().getMessage("invite.received",
                    "%player%", player. getName(),
                    "%party%", party.getPartyName() != null ? party.getPartyName() : player.getName() + "의 파티"));
            target.sendMessage(plugin.getMessageUtil().getMessage("invite.accept-hint"));
        } else {
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.failed"));
        }

        return true;
    }

    // ==================== 초대 수락 ====================
    public boolean handleAccept(Player player, String[] args) {
        // 이미 파티에 있는지 확인
        if (plugin.getPartyManager().isInParty(player)) {
            player. sendMessage(plugin. getMessageUtil().getMessage("party.already-in-party"));
            return true;
        }

        // 대기 중인 초대 확인
        PartyInvite invite = plugin.getPartyInviteManager().getLatestInvite(player);
        
        if (invite == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.no-pending"));
            return true;
        }

        Party party = plugin.getPartyManager().getParty(invite.getPartyId());
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.party-no-longer-exists"));
            plugin.getPartyInviteManager().removeInvite(invite);
            return true;
        }

        // 파티 인원 확인
        if (party. getMembers().size() >= party.getMaxMembers()) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.party-full"));
            plugin.getPartyInviteManager().removeInvite(invite);
            return true;
        }

        // 파티 가입
        boolean success = plugin.getPartyManager().addMember(party, player);
        
        if (success) {
            plugin.getPartyInviteManager().removeInvite(invite);
            plugin.getPartyInviteManager().removeAllInvites(player); // 다른 초대들도 제거
            
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.accepted",
                    "%party%", party.getPartyName() != null ? party.getPartyName() : "파티"));
            
            // 파티원들에게 알림
            plugin. getPartyChatManager().notifyMemberJoin(party, player);
        } else {
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.accept-failed"));
        }

        return true;
    }

    // ==================== 초대 거절 ====================
    public boolean handleDecline(Player player, String[] args) {
        PartyInvite invite = plugin.getPartyInviteManager().getLatestInvite(player);
        
        if (invite == null) {
            player. sendMessage(plugin. getMessageUtil().getMessage("invite.no-pending"));
            return true;
        }

        // 초대자에게 알림
        Player inviter = Bukkit.getPlayer(invite.getInviterId());
        if (inviter != null) {
            inviter.sendMessage(plugin.getMessageUtil().getMessage("invite. declined",
                    "%player%", player.getName()));
        }

        plugin. getPartyInviteManager().removeInvite(invite);
        player.sendMessage(plugin.getMessageUtil().getMessage("invite.you-declined"));

        return true;
    }

    // ==================== 공개 파티 가입 ====================
    public boolean handleJoin(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().getMessage("usage.join"));
            return true;
        }

        // 이미 파티에 있는지 확인
        if (plugin.getPartyManager().isInParty(player)) {
            player. sendMessage(plugin. getMessageUtil().getMessage("party.already-in-party"));
            return true;
        }

        String partyName = String.join(" ", args).replace("_", " ");
        Party party = plugin.getPartyManager().getPartyByName(partyName);

        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.party-not-found",
                    "%party%", partyName));
            return true;
        }

        // 공개 파티인지 확인
        if (party.getPrivacy() != PartyPrivacy. PUBLIC) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-public"));
            return true;
        }

        // 파티 인원 확인
        if (party. getMembers().size() >= party.getMaxMembers()) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.party-full"));
            return true;
        }

        // 파티 가입
        boolean success = plugin. getPartyManager().addMember(party, player);
        
        if (success) {
            player.sendMessage(plugin.getMessageUtil().getMessage("member.joined",
                    "%party%", party.getPartyName()));
            plugin.getPartyChatManager().notifyMemberJoin(party, player);
        } else {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.join-failed"));
        }

        return true;
    }

    // ==================== 파티 탈퇴 ====================
    public boolean handleLeave(Player player, String[] args) {
        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player. sendMessage(plugin. getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 리더가 탈퇴하는 경우
        if (plugin.getPartyRoleManager().isLeader(party, player.getUniqueId())) {
            if (party.getMembers().size() > 1) {
                // 다른 멤버에게 리더 위임
                UUID newLeader = findNewLeader(party, player.getUniqueId());
                if (newLeader != null) {
                    plugin.getPartyRoleManager().transferLeadership(party, newLeader);
                    Player newLeaderPlayer = Bukkit.getPlayer(newLeader);
                    if (newLeaderPlayer != null) {
                        plugin.getPartyChatManager().sendNotification(party,
                                plugin. getMessageUtil().getMessage("party.leader-transferred",
                                        "%player%", newLeaderPlayer. getName()));
                    }
                }
            }
        }

        // 파티원들에게 알림
        plugin. getPartyChatManager().notifyMemberLeave(party, player);
        
        // 파티 탈퇴
        plugin.getPartyManager().removeMember(party, player. getUniqueId());
        player.sendMessage(plugin. getMessageUtil().getMessage("member.left"));

        return true;
    }

    private UUID findNewLeader(Party party, UUID excludeUUID) {
        // 먼저 부리더 찾기
        for (UUID member : party.getMembers()) {
            if (! member.equals(excludeUUID) && 
                plugin.getPartyRoleManager().isOfficer(party, member)) {
                return member;
            }
        }
        
        // 부리더가 없으면 첫 번째 멤버
        for (UUID member : party.getMembers()) {
            if (! member.equals(excludeUUID)) {
                return member;
            }
        }
        
        return null;
    }

    // ==================== 멤버 추방 ====================
    public boolean handleKick(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().getMessage("usage.kick"));
            return true;
        }

        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 추방 권한 확인
        if (!plugin. getPartyRoleManager().hasPermission(party, player. getUniqueId(), PartyPermission.KICK_MEMBERS)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.no-kick-permission"));
            return true;
        }

        // 대상 찾기
        Player target = Bukkit.getPlayer(args[0]);
        UUID targetUUID;
        String targetName;
        
        if (target != null) {
            targetUUID = target.getUniqueId();
            targetName = target.getName();
        } else {
            // 오프라인 플레이어 처리
            targetUUID = plugin.getPartyManager().getOfflinePlayerUUID(args[0]);
            targetName = args[0];
            
            if (targetUUID == null) {
                player. sendMessage(plugin. getMessageUtil().getMessage("general.player-not-found",
                        "%player%", args[0]));
                return true;
            }
        }

        // 파티 멤버인지 확인
        if (!party.getMembers().contains(targetUUID)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.target-not-in-party",
                    "%player%", targetName));
            return true;
        }

        // 자기 자신 추방 불가
        if (targetUUID.equals(player.getUniqueId())) {
            player. sendMessage(plugin. getMessageUtil().getMessage("party.cannot-kick-self"));
            return true;
        }

        // 리더 추방 불가
        if (plugin.getPartyRoleManager().isLeader(party, targetUUID)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.cannot-kick-leader"));
            return true;
        }

        // 부리더가 다른 부리더를 추방할 수 없음
        if (plugin.getPartyRoleManager().isOfficer(party, player.getUniqueId()) &&
            plugin.getPartyRoleManager().isOfficer(party, targetUUID)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.cannot-kick-officer"));
            return true;
        }

        // 추방 처리
        boolean success = plugin.getPartyManager().kickMember(party, player.getUniqueId(), targetUUID);
        
        if (success) {
            player.sendMessage(plugin.getMessageUtil().getMessage("member.kicked",
                    "%player%", targetName));
            
            // 파티원들에게 알림
            plugin. getPartyChatManager().sendNotification(party,
                    plugin. getMessageUtil().getMessage("member.was-kicked",
                            "%player%", targetName,
                            "%kicker%", player.getName()));
            
            // 추방된 플레이어에게 알림
            if (target != null) {
                target.sendMessage(plugin.getMessageUtil().getMessage("member.you-were-kicked"));
            }
        } else {
            player.sendMessage(plugin.getMessageUtil().getMessage("member.kick-failed"));
        }

        return true;
    }

    // ==================== 멤버 승격 ====================
    public boolean handlePromote(Player player, String[] args) {
        if (args. length < 1) {
            player. sendMessage(plugin. getMessageUtil().getMessage("usage.promote"));
            return true;
        }

        Party party = plugin. getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 리더 권한 확인
        if (!plugin. getPartyRoleManager().isLeader(party, player.getUniqueId())) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-leader"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player. sendMessage(plugin. getMessageUtil().getMessage("general.player-not-found",
                    "%player%", args[0]));
            return true;
        }

        // 파티 멤버인지 확인
        if (!party. getMembers().contains(target.getUniqueId())) {
            player. sendMessage(plugin. getMessageUtil().getMessage("party.target-not-in-party",
                    "%player%", target. getName()));
            return true;
        }

        // 이미 부리더인지 확인
        if (plugin.getPartyRoleManager().isOfficer(party, target.getUniqueId())) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.already-officer",
                    "%player%", target. getName()));
            return true;
        }

        // 승격 처리
        boolean success = plugin.getPartyRoleManager().promoteToOfficer(party, target.getUniqueId());
        
        if (success) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.promoted",
                    "%player%", target. getName()));
            target.sendMessage(plugin.getMessageUtil().getMessage("party.you-were-promoted"));
            
            plugin.getPartyChatManager().sendNotification(party,
                    plugin.getMessageUtil().getMessage("party.member-promoted",
                            "%player%", target.getName()));
        }

        return true;
    }

    // ==================== 멤버 강등 ====================
    public boolean handleDemote(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().getMessage("usage.demote"));
            return true;
        }

        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 리더 권한 확인
        if (!plugin.getPartyRoleManager().isLeader(party, player.getUniqueId())) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-leader"));
            return true;
        }

        Player target = Bukkit. getPlayer(args[0]);
        if (target == null) {
            player. sendMessage(plugin. getMessageUtil().getMessage("general.player-not-found",
                    "%player%", args[0]));
            return true;
        }

        // 부리더인지 확인
        if (! plugin.getPartyRoleManager().isOfficer(party, target.getUniqueId())) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-officer",
                    "%player%", target.getName()));
            return true;
        }

        // 강등 처리
        boolean success = plugin. getPartyRoleManager().demoteToMember(party, target.getUniqueId());
        
        if (success) {
            player. sendMessage(plugin. getMessageUtil().getMessage("party.demoted",
                    "%player%", target. getName()));
            target.sendMessage(plugin.getMessageUtil().getMessage("party.you-were-demoted"));
        }

        return true;
    }

    // ==================== 리더 위임 ====================
    public boolean handleTransfer(Player player, String[] args) {
        if (args. length < 1) {
            player. sendMessage(plugin. getMessageUtil().getMessage("usage.transfer"));
            return true;
        }

        Party party = plugin. getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 리더 확인
        if (! plugin.getPartyRoleManager().isLeader(party, player.getUniqueId())) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-leader"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("general.player-not-found",
                    "%player%", args[0]));
            return true;
        }

        // 자기 자신 불가
        if (target.equals(player)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.cannot-transfer-self"));
            return true;
        }

        // 파티 멤버인지 확인
        if (!party.getMembers().contains(target.getUniqueId())) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.target-not-in-party",
                    "%player%", target.getName()));
            return true;
        }

        // 리더 위임
        boolean success = plugin.getPartyRoleManager().transferLeadership(party, target.getUniqueId());
        
        if (success) {
            player. sendMessage(plugin. getMessageUtil().getMessage("party.leadership-transferred",
                    "%player%", target.getName()));
            target.sendMessage(plugin.getMessageUtil().getMessage("party.you-are-now-leader"));
            
            plugin.getPartyChatManager().sendNotification(party,
                    plugin.getMessageUtil().getMessage("party.leader-changed",
                            "%player%", target. getName()));
        }

        return true;
    }

    // ==================== 파티 정보 ====================
    public boolean handleInfo(Player player, String[] args) {
        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        player.sendMessage(plugin.getMessageUtil().getMessage("info.header"));
        
        // 파티 이름
        String partyName = party.getPartyName() != null ? party. getPartyName() : "이름 없음";
        player.sendMessage(plugin.getMessageUtil().getMessage("info.name", "%name%", partyName));
        
        // 파티 ID
        player.sendMessage(plugin.getMessageUtil().getMessage("info.id", 
                "%id%", party. getPartyId().toString().substring(0, 8)));
        
        // 리더
        Player leader = Bukkit.getPlayer(party.getLeaderId());
        String leaderName = leader != null ? leader. getName() : "오프라인";
        player.sendMessage(plugin.getMessageUtil().getMessage("info.leader", "%leader%", leaderName));
        
        // 인원
        player.sendMessage(plugin.getMessageUtil().getMessage("info.members",
                "%current%", String.valueOf(party. getMembers().size()),
                "%max%", String.valueOf(party.getMaxMembers())));
        
        // 파티 레벨
        int level = plugin.getPartyLevelManager().getPartyLevel(party);
        long exp = plugin.getPartyLevelManager().getPartyExp(party);
        long expToNext = plugin.getPartyLevelManager().getExpToNextLevel(party);
        player.sendMessage(plugin.getMessageUtil().getMessage("info.level",
                "%level%", String.valueOf(level),
                "%exp%", String.valueOf(exp),
                "%next%", String.valueOf(expToNext)));
        
        // 공개 설정
        player.sendMessage(plugin.getMessageUtil().getMessage("info.privacy",
                "%privacy%", party.getPrivacy().name()));
        
        // 분배 방식
        player.sendMessage(plugin.getMessageUtil().getMessage("info.loot",
                "%loot%", party.getLootDistribution().name()));
        player.sendMessage(plugin.getMessageUtil().getMessage("info.exp",
                "%exp%", party.getExpDistribution().name()));
        
        // 활성 버프
        List<PartyBuff> buffs = plugin.getPartyBuffManager().getActiveBuffs(party);
        if (!buffs.isEmpty()) {
            StringBuilder buffList = new StringBuilder();
            for (PartyBuff buff : buffs) {
                if (buffList.length() > 0) buffList.append(", ");
                buffList. append(buff.getName());
            }
            player.sendMessage(plugin.getMessageUtil().getMessage("info.buffs",
                    "%buffs%", buffList.toString()));
        }
        
        player.sendMessage(plugin.getMessageUtil().getMessage("info.footer"));

        return true;
    }

    // ==================== 멤버 목록 ====================
    public boolean handleList(Player player, String[] args) {
        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        player.sendMessage(plugin.getMessageUtil().getMessage("list.header",
                "%count%", String.valueOf(party.getMembers().size()),
                "%max%", String.valueOf(party.getMaxMembers())));

        for (UUID memberUUID : party. getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            String memberName;
            String status;
            
            if (member != null) {
                memberName = member. getName();
                status = "&a온라인";
            } else {
                memberName = plugin.getPartyManager().getOfflinePlayerName(memberUUID);
                status = "&7오프라인";
            }
            
            PartyRole role = plugin.getPartyRoleManager().getRole(party, memberUUID);
            String roleDisplay = getRoleDisplay(role);
            
            player.sendMessage(plugin.getMessageUtil().getMessage("list.member",
                    "%role%", roleDisplay,
                    "%name%", memberName,
                    "%status%", status));
        }

        player.sendMessage(plugin.getMessageUtil().getMessage("list.footer"));

        return true;
    }

    private String getRoleDisplay(PartyRole role) {
        switch (role) {
            case LEADER: 
                return "&6[리더]";
            case OFFICER: 
                return "&e[부리더]";
            case MEMBER:
            default:
                return "&7[멤버]";
        }
    }

    // ==================== 파티 통계 ====================
    public boolean handleStats(Player player, String[] args) {
        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        PartyStatistics stats = plugin.getPartyStatisticsManager().getStatistics(party);
        
        player.sendMessage(plugin.getMessageUtil().getMessage("stats.header"));
        
        // 플레이 시간
        long playTimeSeconds = stats.getTotalPlayTime();
        String playTime = formatPlayTime(playTimeSeconds);
        player.sendMessage(plugin. getMessageUtil().getMessage("stats.play-time", "%time%", playTime));
        
        // 처치 통계
        player.sendMessage(plugin.getMessageUtil().getMessage("stats.monsters-killed",
                "%count%", String.valueOf(stats. getMonstersKilled())));
        player.sendMessage(plugin. getMessageUtil().getMessage("stats.bosses-killed",
                "%count%", String.valueOf(stats.getBossesKilled())));
        
        // 활동 통계
        player.sendMessage(plugin.getMessageUtil().getMessage("stats.dungeons-completed",
                "%count%", String.valueOf(stats.getDungeonsCompleted())));
        player.sendMessage(plugin. getMessageUtil().getMessage("stats.quests-completed",
                "%count%", String.valueOf(stats.getQuestsCompleted())));
        
        // 전투 통계
        player.sendMessage(plugin.getMessageUtil().getMessage("stats.total-damage",
                "%damage%", String.format("%.0f", stats.getTotalDamage())));
        player.sendMessage(plugin. getMessageUtil().getMessage("stats.total-healing",
                "%healing%", String.format("%.0f", stats.getTotalHealing())));
        
        // MVP
        UUID mvp = plugin.getPartyStatisticsManager().calculateMVP(party);
        if (mvp != null) {
            String mvpName = plugin.getPartyManager().getOfflinePlayerName(mvp);
            MemberStatistics mvpStats = plugin.getPartyStatisticsManager().getMemberStats(party, mvp);
            player.sendMessage(plugin.getMessageUtil().getMessage("stats.mvp",
                    "%player%", mvpName,
                    "%count%", String.valueOf(mvpStats. getMvpCount())));
        }
        
        player.sendMessage(plugin.getMessageUtil().getMessage("stats.footer"));

        return true;
    }

    private String formatPlayTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        
        if (hours > 0) {
            return hours + "시간 " + minutes + "분";
        } else {
            return minutes + "분";
        }
    }

    // ==================== 파티 설정 GUI ====================
    public boolean handleSettings(Player player, String[] args) {
        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 설정 변경 권한 확인
        if (!plugin.getPartyRoleManager().hasPermission(party, player.getUniqueId(), PartyPermission. CHANGE_SETTINGS)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.no-settings-permission"));
            return true;
        }

        plugin.getGuiManager().openPartySettingsGUI(player, party);

        return true;
    }

    // ==================== 공개 설정 변경 ====================
    public boolean handlePrivacy(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().getMessage("usage.privacy"));
            return true;
        }

        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 설정 변경 권한 확인
        if (!plugin.getPartyRoleManager().hasPermission(party, player.getUniqueId(), PartyPermission.CHANGE_SETTINGS)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.no-settings-permission"));
            return true;
        }

        PartyPrivacy privacy;
        switch (args[0]. toLowerCase()) {
            case "public": 
                privacy = PartyPrivacy.PUBLIC;
                break;
            case "invite":
                privacy = PartyPrivacy. INVITE_ONLY;
                break;
            case "private":
                privacy = PartyPrivacy. PRIVATE;
                break;
            default:
                player. sendMessage(plugin. getMessageUtil().getMessage("usage.privacy"));
                return true;
        }

        party.setPrivacy(privacy);
        player.sendMessage(plugin.getMessageUtil().getMessage("settings.privacy-changed",
                "%privacy%", privacy.name()));

        return true;
    }

    // ==================== 아이템 분배 방식 변경 ====================
    public boolean handleLoot(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().getMessage("usage.loot"));
            return true;
        }

        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 설정 변경 권한 확인
        if (! plugin.getPartyRoleManager().hasPermission(party, player.getUniqueId(), PartyPermission.CHANGE_SETTINGS)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.no-settings-permission"));
            return true;
        }

        LootDistribution distribution;
        switch (args[0].toLowerCase()) {
            case "free":
                distribution = LootDistribution. FREE_FOR_ALL;
                break;
            case "round":
                distribution = LootDistribution. ROUND_ROBIN;
                break;
            case "need": 
                distribution = LootDistribution. NEED_BEFORE_GREED;
                break;
            case "master":
                distribution = LootDistribution. MASTER_LOOT;
                break;
            default: 
                player.sendMessage(plugin.getMessageUtil().getMessage("usage.loot"));
                return true;
        }

        party.setLootDistribution(distribution);
        player.sendMessage(plugin.getMessageUtil().getMessage("settings.loot-changed",
                "%loot%", distribution. name()));
        
        plugin.getPartyChatManager().sendNotification(party,
                plugin.getMessageUtil().getMessage("settings.loot-changed-notify",
                        "%player%", player. getName(),
                        "%loot%", distribution. name()));

        return true;
    }

    // ==================== 경험치 분배 방식 변경 ====================
    public boolean handleExp(Player player, String[] args) {
        if (args. length < 1) {
            player. sendMessage(plugin. getMessageUtil().getMessage("usage.exp"));
            return true;
        }

        Party party = plugin. getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 설정 변경 권한 확인
        if (!plugin. getPartyRoleManager().hasPermission(party, player. getUniqueId(), PartyPermission.CHANGE_SETTINGS)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.no-settings-permission"));
            return true;
        }

        ExpDistribution distribution;
        switch (args[0].toLowerCase()) {
            case "equal":
                distribution = ExpDistribution. EQUAL;
                break;
            case "level":
                distribution = ExpDistribution. LEVEL_BASED;
                break;
            case "contribution":
                distribution = ExpDistribution. CONTRIBUTION;
                break;
            default:
                player.sendMessage(plugin.getMessageUtil().getMessage("usage.exp"));
                return true;
        }

        party.setExpDistribution(distribution);
        player.sendMessage(plugin. getMessageUtil().getMessage("settings.exp-changed",
                "%exp%", distribution. name()));

        return true;
    }

    // ==================== 파티 채팅 ====================
    public boolean handleChat(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().getMessage("usage.chat"));
            return true;
        }

        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        String message = String.join(" ", args);
        plugin.getPartyChatManager().sendPartyMessage(player, message);

        return true;
    }

    // ==================== 파티 공지 ====================
    public boolean handleAnnounce(Player player, String[] args) {
        if (args. length < 1) {
            player. sendMessage(plugin. getMessageUtil().getMessage("usage.announce"));
            return true;
        }

        Party party = plugin. getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 리더/부리더 확인
        if (! plugin.getPartyRoleManager().isLeader(party, player.getUniqueId()) &&
            !plugin. getPartyRoleManager().isOfficer(party, player.getUniqueId())) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.no-announce-permission"));
            return true;
        }

        String message = String.join(" ", args);
        plugin.getPartyChatManager().sendPartyAnnouncement(player, message);

        return true;
    }

    // ==================== 파티 찾기 GUI ====================
    public boolean handleFinder(Player player, String[] args) {
        plugin.getGuiManager().openPartyFinderGUI(player);
        return true;
    }

    // ==================== 파티 검색 ====================
    public boolean handleSearch(Player player, String[] args) {
        String query = args. length > 0 ? String.join(" ", args) : "";
        
        List<Party> results = plugin.getPartyFinder().searchParties(query);
        
        if (results.isEmpty()) {
            player.sendMessage(plugin.getMessageUtil().getMessage("finder.no-results"));
            return true;
        }

        player.sendMessage(plugin.getMessageUtil().getMessage("finder.results-header",
                "%count%", String.valueOf(results. size())));

        int count = 0;
        for (Party party : results) {
            if (count >= 10) break; // 최대 10개
            
            String partyName = party.getPartyName() != null ? party.getPartyName() : "이름 없음";
            int members = party.getMembers().size();
            int maxMembers = party. getMaxMembers();
            
            player.sendMessage(plugin.getMessageUtil().getMessage("finder.result-entry",
                    "%name%", partyName,
                    "%members%", String.valueOf(members),
                    "%max%", String.valueOf(maxMembers)));
            count++;
        }

        return true;
    }

    // ==================== 자동 매칭 대기열 ====================
    public boolean handleQueue(Player player, String[] args) {
        if (args. length < 1) {
            player. sendMessage(plugin. getMessageUtil().getMessage("usage.queue"));
            return true;
        }

        // 이미 파티에 있는지 확인
        if (plugin.getPartyManager().isInParty(player)) {
            player. sendMessage(plugin. getMessageUtil().getMessage("queue.already-in-party"));
            return true;
        }

        // 이미 대기열에 있는지 확인
        if (plugin.getPartyFinder().isInQueue(player)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("queue.already-in-queue"));
            return true;
        }

        String dungeonId = args[0];
        
        // 던전 존재 여부 확인
        if (plugin.getIntegrationManager().isDungeonCoreEnabled()) {
            if (! plugin.getIntegrationManager().getDungeonCoreIntegration().dungeonExists(dungeonId)) {
                player.sendMessage(plugin.getMessageUtil().getMessage("queue.dungeon-not-found",
                        "%dungeon%", dungeonId));
                return true;
            }
        }

        plugin.getPartyFinder().queueForMatching(player, dungeonId);
        player.sendMessage(plugin.getMessageUtil().getMessage("queue.joined",
                "%dungeon%", dungeonId));

        return true;
    }

    // ==================== 대기열 취소 ====================
    public boolean handleCancelQueue(Player player, String[] args) {
        if (! plugin.getPartyFinder().isInQueue(player)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("queue.not-in-queue"));
            return true;
        }

        plugin. getPartyFinder().cancelQueue(player);
        player.sendMessage(plugin. getMessageUtil().getMessage("queue.cancelled"));

        return true;
    }

    // ==================== 파티 스킬 사용 ====================
    public boolean handleSkill(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().getMessage("usage.skill"));
            return true;
        }

        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        String skillId = args[0]. toLowerCase();
        
        // 스킬 존재 확인
        if (!plugin.getSkillRegistry().skillExists(skillId)) {
            player. sendMessage(plugin. getMessageUtil().getMessage("skill.not-found",
                    "%skill%", skillId));
            return true;
        }

        // 스킬 습득 확인
        if (! plugin.getPartySkillManager().hasSkill(party, skillId)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("skill.not-learned",
                    "%skill%", skillId));
            return true;
        }

        // 스킬 사용
        boolean success = plugin.getPartySkillManager().useSkill(party, player, skillId);
        
        if (! success) {
            player.sendMessage(plugin.getMessageUtil().getMessage("skill.use-failed"));
        }

        return true;
    }

    // ==================== 파티 스킬 목록 ====================
    public boolean handleSkills(Player player, String[] args) {
        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        plugin.getGuiManager().openPartySkillGUI(player, party);

        return true;
    }

    // ==================== 파티 메뉴 GUI ====================
    public boolean handleMenu(Player player, String[] args) {
        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) {
            // 파티가 없으면 파티 생성/찾기 메뉴
            plugin.getGuiManager().openNoPartyMenuGUI(player);
        } else {
            plugin.getGuiManager().openPartyMenuGUI(player, party);
        }

        return true;
    }
}