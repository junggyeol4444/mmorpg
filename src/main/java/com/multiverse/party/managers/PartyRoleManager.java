package com. multiverse.party. managers;

import com.multiverse.party.PartyCore;
import com.multiverse.party.events.PartyLeaderChangeEvent;
import com.multiverse.party.models. Party;
import com.multiverse.party.models.enums.PartyPermission;
import com.multiverse.party.models.enums.PartyRole;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;

import java.util.*;

public class PartyRoleManager {

    private final PartyCore plugin;
    private final Map<PartyRole, Set<PartyPermission>> rolePermissions;

    public PartyRoleManager(PartyCore plugin) {
        this.plugin = plugin;
        this.rolePermissions = new EnumMap<>(PartyRole.class);
        initializePermissions();
    }

    private void initializePermissions() {
        // 리더 권한
        Set<PartyPermission> leaderPerms = EnumSet.allOf(PartyPermission.class);
        rolePermissions.put(PartyRole. LEADER, leaderPerms);

        // 부리더 권한
        Set<PartyPermission> officerPerms = EnumSet.of(
                PartyPermission.INVITE_MEMBERS,
                PartyPermission.KICK_MEMBERS,
                PartyPermission.SEND_ANNOUNCEMENT,
                PartyPermission.USE_PARTY_SKILLS,
                PartyPermission.VIEW_PARTY_INFO,
                PartyPermission.CHANGE_LOOT_SETTINGS
        );
        rolePermissions.put(PartyRole. OFFICER, officerPerms);

        // 일반 멤버 권한
        Set<PartyPermission> memberPerms = EnumSet. of(
                PartyPermission.VIEW_PARTY_INFO,
                PartyPermission.USE_PARTY_CHAT,
                PartyPermission.USE_PARTY_SKILLS
        );
        rolePermissions.put(PartyRole. MEMBER, memberPerms);
    }

    // ==================== 역할 관리 ====================
    public PartyRole getRole(Party party, UUID playerUUID) {
        if (party == null || playerUUID == null) {
            return PartyRole.MEMBER;
        }

        PartyRole role = party.getRoles().get(playerUUID);
        return role != null ? role : PartyRole. MEMBER;
    }

    public void setRole(Party party, UUID playerUUID, PartyRole role) {
        if (party == null || playerUUID == null || role == null) return;

        party. getRoles().put(playerUUID, role);
        plugin.getDataManager().saveParty(party);
    }

    public boolean isLeader(Party party, UUID playerUUID) {
        return getRole(party, playerUUID) == PartyRole. LEADER;
    }

    public boolean isOfficer(Party party, UUID playerUUID) {
        return getRole(party, playerUUID) == PartyRole. OFFICER;
    }

    public boolean isMember(Party party, UUID playerUUID) {
        return party.getMembers().contains(playerUUID);
    }

    // ==================== 권한 확인 ====================
    public boolean hasPermission(Party party, UUID playerUUID, PartyPermission permission) {
        PartyRole role = getRole(party, playerUUID);
        Set<PartyPermission> permissions = rolePermissions. get(role);
        return permissions != null && permissions.contains(permission);
    }

    public boolean canInvite(Party party, UUID playerUUID) {
        // 리더는 항상 가능
        if (isLeader(party, playerUUID)) return true;

        // 부리더는 설정에 따라
        if (isOfficer(party, playerUUID) && party.isAllowInvites()) {
            return hasPermission(party, playerUUID, PartyPermission. INVITE_MEMBERS);
        }

        return false;
    }

    public boolean canKick(Party party, UUID kickerUUID, UUID targetUUID) {
        if (kickerUUID. equals(targetUUID)) return false;

        PartyRole kickerRole = getRole(party, kickerUUID);
        PartyRole targetRole = getRole(party, targetUUID);

        // 리더는 모두 추방 가능
        if (kickerRole == PartyRole.LEADER) {
            return true;
        }

        // 부리더는 일반 멤버만 추방 가능
        if (kickerRole == PartyRole. OFFICER && targetRole == PartyRole.MEMBER) {
            return hasPermission(party, kickerUUID, PartyPermission. KICK_MEMBERS);
        }

        return false;
    }

    public boolean canPromote(Party party, UUID promoterUUID, UUID targetUUID) {
        if (promoterUUID.equals(targetUUID)) return false;

        // 리더만 승격 가능
        return isLeader(party, promoterUUID) && 
               getRole(party, targetUUID) == PartyRole.MEMBER;
    }

    public boolean canDemote(Party party, UUID demoterUUID, UUID targetUUID) {
        if (demoterUUID.equals(targetUUID)) return false;

        // 리더만 강등 가능
        return isLeader(party, demoterUUID) && 
               getRole(party, targetUUID) == PartyRole.OFFICER;
    }

    public boolean canChangeSettings(Party party, UUID playerUUID) {
        PartyRole role = getRole(party, playerUUID);
        return role == PartyRole.LEADER || role == PartyRole. OFFICER;
    }

    // ==================== 역할 변경 ====================
    public boolean promoteToOfficer(Party party, UUID targetUUID) {
        if (! party.getMembers().contains(targetUUID)) return false;
        if (getRole(party, targetUUID) != PartyRole.MEMBER) return false;

        setRole(party, targetUUID, PartyRole. OFFICER);
        
        Player target = Bukkit. getPlayer(targetUUID);
        if (target != null) {
            target. sendMessage(plugin. getMessageUtil().getMessage("party.you-were-promoted"));
        }

        return true;
    }

    public boolean demoteToMember(Party party, UUID targetUUID) {
        if (! party.getMembers().contains(targetUUID)) return false;
        if (getRole(party, targetUUID) != PartyRole.OFFICER) return false;

        setRole(party, targetUUID, PartyRole. MEMBER);
        
        Player target = Bukkit.getPlayer(targetUUID);
        if (target != null) {
            target.sendMessage(plugin.getMessageUtil().getMessage("party.you-were-demoted"));
        }

        return true;
    }

    public boolean transferLeadership(Party party, UUID newLeaderUUID) {
        if (! party.getMembers().contains(newLeaderUUID)) return false;

        UUID oldLeaderUUID = party.getLeaderId();
        String oldLeaderName = plugin.getPartyManager().getOfflinePlayerName(oldLeaderUUID);
        String newLeaderName = plugin.getPartyManager().getOfflinePlayerName(newLeaderUUID);

        // 이벤트 발생
        PartyLeaderChangeEvent event = new PartyLeaderChangeEvent(
                party, oldLeaderUUID, oldLeaderName, newLeaderUUID, newLeaderName,
                PartyLeaderChangeEvent. ChangeReason.VOLUNTARY_TRANSFER);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        // 역할 변경
        party.getRoles().put(oldLeaderUUID, PartyRole. OFFICER);
        party.getRoles().put(newLeaderUUID, PartyRole. LEADER);
        party.setLeaderId(newLeaderUUID);

        plugin.getDataManager().saveParty(party);

        // 알림
        Player oldLeader = Bukkit.getPlayer(oldLeaderUUID);
        if (oldLeader != null) {
            oldLeader.sendMessage(plugin.getMessageUtil().getMessage("party.leadership-transferred",
                    "%player%", newLeaderName));
        }

        Player newLeader = Bukkit.getPlayer(newLeaderUUID);
        if (newLeader != null) {
            newLeader.sendMessage(plugin.getMessageUtil().getMessage("party.you-are-now-leader"));
        }

        return true;
    }

    // ==================== 역할 목록 ====================
    public List<UUID> getOfficers(Party party) {
        List<UUID> officers = new ArrayList<>();
        
        for (Map.Entry<UUID, PartyRole> entry : party.getRoles().entrySet()) {
            if (entry.getValue() == PartyRole. OFFICER) {
                officers.add(entry.getKey());
            }
        }
        
        return officers;
    }

    public List<UUID> getRegularMembers(Party party) {
        List<UUID> members = new ArrayList<>();
        
        for (UUID memberUUID : party. getMembers()) {
            PartyRole role = getRole(party, memberUUID);
            if (role == PartyRole.MEMBER) {
                members.add(memberUUID);
            }
        }
        
        return members;
    }

    public int getOfficerCount(Party party) {
        return getOfficers(party).size();
    }

    public int getMaxOfficers(Party party) {
        int memberCount = party.getMembers().size();
        
        // 5명당 부리더 1명
        return Math.max(1, memberCount / 5);
    }

    public boolean canAddMoreOfficers(Party party) {
        return getOfficerCount(party) < getMaxOfficers(party);
    }

    // ==================== 역할 표시 ====================
    public String getRoleDisplayName(PartyRole role) {
        switch (role) {
            case LEADER: 
                return plugin.getMessageUtil().getMessage("role.leader", false);
            case OFFICER:
                return plugin.getMessageUtil().getMessage("role.officer", false);
            case MEMBER: 
            default:
                return plugin.getMessageUtil().getMessage("role.member", false);
        }
    }

    public String getRolePrefix(PartyRole role) {
        switch (role) {
            case LEADER: 
                return plugin. getConfig().getString("chat.role-prefix.leader", "&6[리더]");
            case OFFICER: 
                return plugin.getConfig().getString("chat.role-prefix. officer", "&e[부리더]");
            case MEMBER:
            default:
                return plugin.getConfig().getString("chat.role-prefix.member", "&7[멤버]");
        }
    }

    public String getRoleColor(PartyRole role) {
        switch (role) {
            case LEADER: 
                return "&6";
            case OFFICER:
                return "&e";
            case MEMBER: 
            default: 
                return "&7";
        }
    }
}