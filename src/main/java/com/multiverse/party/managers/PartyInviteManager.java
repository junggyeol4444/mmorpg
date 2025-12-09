package com.multiverse.party.managers;

import com. multiverse.party. PartyCore;
import com.multiverse.party.events. PartyInviteEvent;
import com.multiverse.party.models.Party;
import com.multiverse. party.models.PartyInvite;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit. scheduler.BukkitTask;

import java. util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PartyInviteManager {

    private final PartyCore plugin;
    private final Map<UUID, List<PartyInvite>> pendingInvites;
    private final Map<UUID, BukkitTask> expireTasks;

    public PartyInviteManager(PartyCore plugin) {
        this.plugin = plugin;
        this.pendingInvites = new ConcurrentHashMap<>();
        this.expireTasks = new ConcurrentHashMap<>();
    }

    // ==================== 초대 생성 ====================
    public boolean sendInvite(Party party, Player inviter, Player target) {
        if (party == null || inviter == null || target == null) {
            return false;
        }

        // 자기 자신 초대 불가
        if (inviter.getUniqueId().equals(target.getUniqueId())) {
            inviter.sendMessage(plugin.getMessageUtil().getMessage("invite.cannot-invite-self"));
            return false;
        }

        // 대상이 이미 파티에 있는지 확인
        if (plugin.getPartyManager().isInParty(target)) {
            inviter.sendMessage(plugin.getMessageUtil().getMessage("invite. target-in-party",
                    "%player%", target.getName()));
            return false;
        }

        // 파티 인원 제한 확인
        if (party.getMembers().size() >= party.getMaxMembers()) {
            inviter.sendMessage(plugin.getMessageUtil().getMessage("party.party-full"));
            return false;
        }

        // 이미 초대했는지 확인
        if (hasInviteFromParty(target, party)) {
            inviter.sendMessage(plugin.getMessageUtil().getMessage("invite.already-invited",
                    "%player%", target.getName()));
            return false;
        }

        // 대상의 초대 거부 설정 확인
        if (plugin.getDataManager().loadPlayerData(target. getUniqueId()) != null) {
            var playerData = plugin. getDataManager().loadPlayerData(target. getUniqueId());
            if (playerData.isAutoDecline()) {
                inviter.sendMessage(plugin.getMessageUtil().getMessage("invite.target-not-accepting",
                        "%player%", target. getName()));
                return false;
            }
        }

        // 초대 만료 시간
        int expireSeconds = plugin.getConfig().getInt("invite.expire-time", 60);
        long expireTime = System.currentTimeMillis() + (expireSeconds * 1000L);

        // 초대 생성
        PartyInvite invite = new PartyInvite();
        invite.setInviteId(UUID.randomUUID());
        invite.setPartyId(party.getPartyId());
        invite.setInviterId(inviter. getUniqueId());
        invite.setTargetId(target.getUniqueId());
        invite.setCreatedTime(System. currentTimeMillis());
        invite.setExpireTime(expireTime);

        // 이벤트 발생
        PartyInviteEvent inviteEvent = new PartyInviteEvent(party, inviter, target, expireTime);
        Bukkit.getPluginManager().callEvent(inviteEvent);

        if (inviteEvent.isCancelled()) {
            if (inviteEvent.getCancelReason() != null) {
                inviter.sendMessage(inviteEvent.getCancelReason());
            }
            return false;
        }

        // 초대 저장
        pendingInvites.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>()).add(invite);

        // 만료 태스크 설정
        BukkitTask expireTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            removeInvite(invite);
            
            Player inviterPlayer = Bukkit.getPlayer(invite.getInviterId());
            if (inviterPlayer != null) {
                inviterPlayer.sendMessage(plugin.getMessageUtil().getMessage("invite.expired-inviter",
                        "%player%", target.getName()));
            }
            
            Player targetPlayer = Bukkit.getPlayer(invite.getTargetId());
            if (targetPlayer != null) {
                targetPlayer.sendMessage(plugin.getMessageUtil().getMessage("invite. expired-target",
                        "%party%", party.getPartyName() != null ? party.getPartyName() : "파티"));
            }
        }, expireSeconds * 20L);

        expireTasks.put(invite.getInviteId(), expireTask);

        // 메시지 전송
        inviter.sendMessage(plugin.getMessageUtil().getMessage("invite.sent",
                "%player%", target.getName()));

        // GUI 사용 여부에 따라 처리
        boolean useGUI = plugin.getConfig().getBoolean("invite. use-gui", true);
        if (!useGUI) {
            sendInviteMessage(target, party, inviter);
        }

        return true;
    }

    private void sendInviteMessage(Player target, Party party, Player inviter) {
        String partyName = party. getPartyName() != null ? party. getPartyName() : "파티";
        
        target.sendMessage(plugin.getMessageUtil().getMessage("invite.received",
                "%player%", inviter.getName(),
                "%party%", partyName));
        target.sendMessage(plugin.getMessageUtil().getMessage("invite.accept-hint"));
        target.sendMessage(plugin.getMessageUtil().getMessage("invite. decline-hint"));
    }

    // ==================== 초대 수락 ====================
    public boolean acceptInvite(Player player, PartyInvite invite) {
        if (invite == null) return false;

        // 만료 확인
        if (invite.isExpired()) {
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.expired"));
            removeInvite(invite);
            return false;
        }

        // 파티 확인
        Party party = plugin.getPartyManager().getParty(invite.getPartyId());
        if (party == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.party-not-found"));
            removeInvite(invite);
            return false;
        }

        // 인원 제한 확인
        if (party.getMembers().size() >= party.getMaxMembers()) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.party-full"));
            return false;
        }

        // 파티 가입
        boolean joined = plugin.getPartyManager().addMember(party, player);
        
        if (joined) {
            // 모든 초대 제거
            removeAllInvites(player);

            // 초대자에게 알림
            Player inviter = Bukkit.getPlayer(invite.getInviterId());
            if (inviter != null) {
                inviter.sendMessage(plugin.getMessageUtil().getMessage("invite.accepted-inviter",
                        "%player%", player.getName()));
            }

            // 파티원들에게 알림
            plugin.getPartyChatManager().notifyMemberJoin(party, player);
        }

        return joined;
    }

    public boolean acceptFirstInvite(Player player) {
        List<PartyInvite> invites = pendingInvites.get(player. getUniqueId());
        if (invites == null || invites.isEmpty()) {
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.no-pending"));
            return false;
        }

        // 만료되지 않은 첫 번째 초대 찾기
        PartyInvite validInvite = null;
        for (PartyInvite invite : invites) {
            if (! invite.isExpired()) {
                validInvite = invite;
                break;
            }
        }

        if (validInvite == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("invite. no-pending"));
            removeAllInvites(player);
            return false;
        }

        return acceptInvite(player, validInvite);
    }

    // ==================== 초대 거절 ====================
    public boolean declineInvite(Player player, PartyInvite invite) {
        if (invite == null) return false;

        removeInvite(invite);

        // 초대자에게 알림
        Player inviter = Bukkit.getPlayer(invite.getInviterId());
        if (inviter != null) {
            inviter.sendMessage(plugin.getMessageUtil().getMessage("invite.declined",
                    "%player%", player.getName()));
        }

        player.sendMessage(plugin.getMessageUtil().getMessage("invite.you-declined"));

        return true;
    }

    public boolean declineFirstInvite(Player player) {
        List<PartyInvite> invites = pendingInvites. get(player.getUniqueId());
        if (invites == null || invites.isEmpty()) {
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.no-pending"));
            return false;
        }

        PartyInvite invite = invites.get(0);
        return declineInvite(player, invite);
    }

    public void declineAllInvites(Player player) {
        List<PartyInvite> invites = pendingInvites.get(player.getUniqueId());
        if (invites == null || invites.isEmpty()) return;

        for (PartyInvite invite : new ArrayList<>(invites)) {
            Player inviter = Bukkit. getPlayer(invite. getInviterId());
            if (inviter != null) {
                inviter.sendMessage(plugin.getMessageUtil().getMessage("invite.declined",
                        "%player%", player. getName()));
            }
        }

        removeAllInvites(player);
        player.sendMessage(plugin.getMessageUtil().getMessage("invite. all-declined"));
    }

    // ==================== 초대 관리 ====================
    public void removeInvite(PartyInvite invite) {
        if (invite == null) return;

        List<PartyInvite> invites = pendingInvites.get(invite. getTargetId());
        if (invites != null) {
            invites.remove(invite);
            if (invites.isEmpty()) {
                pendingInvites. remove(invite.getTargetId());
            }
        }

        // 만료 태스크 취소
        BukkitTask task = expireTasks.remove(invite. getInviteId());
        if (task != null) {
            task.cancel();
        }
    }

    public void removeAllInvites(Player player) {
        List<PartyInvite> invites = pendingInvites. remove(player.getUniqueId());
        if (invites != null) {
            for (PartyInvite invite :  invites) {
                BukkitTask task = expireTasks.remove(invite. getInviteId());
                if (task != null) {
                    task. cancel();
                }
            }
        }
    }

    public void removeInvitesFromParty(UUID partyId) {
        for (List<PartyInvite> invites :  pendingInvites.values()) {
            invites.removeIf(invite -> invite.getPartyId().equals(partyId));
        }
    }

    // ==================== 조회 메서드 ====================
    public List<PartyInvite> getPendingInvites(Player player) {
        List<PartyInvite> invites = pendingInvites.get(player. getUniqueId());
        if (invites == null) return new ArrayList<>();
        
        // 만료된 초대 제거
        invites.removeIf(PartyInvite:: isExpired);
        
        return new ArrayList<>(invites);
    }

    public int getPendingInviteCount(Player player) {
        return getPendingInvites(player).size();
    }

    public boolean hasInviteFromParty(Player player, Party party) {
        List<PartyInvite> invites = pendingInvites. get(player.getUniqueId());
        if (invites == null) return false;

        for (PartyInvite invite : invites) {
            if (invite.getPartyId().equals(party.getPartyId()) && !invite.isExpired()) {
                return true;
            }
        }
        return false;
    }

    public PartyInvite getInviteByParty(Player player, Party party) {
        List<PartyInvite> invites = pendingInvites.get(player.getUniqueId());
        if (invites == null) return null;

        for (PartyInvite invite :  invites) {
            if (invite. getPartyId().equals(party.getPartyId()) && !invite.isExpired()) {
                return invite;
            }
        }
        return null;
    }

    public void cleanup() {
        // 모든 태스크 취소
        for (BukkitTask task : expireTasks.values()) {
            task.cancel();
        }
        expireTasks.clear();
        pendingInvites. clear();
    }
}