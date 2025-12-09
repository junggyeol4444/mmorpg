package com.multiverse.party. managers;

import com. multiverse.party. PartyCore;
import com. multiverse.party. events.*;
import com.multiverse.party.events.PartyMemberJoinEvent. JoinType;
import com.multiverse. party.models.Party;
import com.multiverse. party.models.PartyLevel;
import com. multiverse.party. models.PlayerPartyData;
import com. multiverse.party. models.enums.*;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PartyManager {

    private final PartyCore plugin;
    private final Map<UUID, Party> parties;
    private final Map<UUID, UUID> playerToParty;

    public PartyManager(PartyCore plugin) {
        this.plugin = plugin;
        this.parties = new ConcurrentHashMap<>();
        this.playerToParty = new ConcurrentHashMap<>();
    }

    public void initialize() {
        // 데이터 로드
        Map<UUID, Party> loadedParties = plugin.getDataManager().loadAllParties();
        parties.putAll(loadedParties);

        // 플레이어-파티 매핑 구축
        for (Party party : parties.values()) {
            for (UUID memberUUID : party. getMembers()) {
                playerToParty.put(memberUUID, party. getPartyId());
            }
        }

        plugin.getLogger().info("파티 매니저 초기화 완료 (" + parties.size() + "개 파티 로드)");
    }

    public void shutdown() {
        // 모든 파티 저장
        for (Party party : parties.values()) {
            plugin.getDataManager().saveParty(party);
        }
        
        parties.clear();
        playerToParty. clear();
        
        plugin. getLogger().info("파티 매니저 종료 완료");
    }

    // ==================== 파티 생성 ====================
    public Party createParty(Player leader) {
        return createParty(leader, null);
    }

    public Party createParty(Player leader, String partyName) {
        // 이미 파티에 있는지 확인
        if (isInParty(leader)) {
            leader. sendMessage(plugin. getMessageUtil().getMessage("party.already-in-party"));
            return null;
        }

        // 파티 이름 중복 확인
        if (partyName != null && isPartyNameTaken(partyName)) {
            leader.sendMessage(plugin.getMessageUtil().getMessage("party.name-taken"));
            return null;
        }

        // 파티 생성
        Party party = new Party();
        party.setPartyId(UUID.randomUUID());
        party.setLeaderId(leader.getUniqueId());
        party.setPartyName(partyName);
        party.setCreatedTime(System.currentTimeMillis());

        // 기본 설정
        int defaultMaxMembers = plugin.getConfig().getInt("party.size. default", 5);
        party.setMaxMembers(defaultMaxMembers);
        party.setPrivacy(PartyPrivacy.INVITE_ONLY);
        party.setAllowInvites(true);
        party.setLootDistribution(LootDistribution.FREE_FOR_ALL);
        party.setExpDistribution(ExpDistribution.EQUAL);

        // 멤버 추가
        List<UUID> members = new ArrayList<>();
        members.add(leader.getUniqueId());
        party.setMembers(members);

        // 역할 설정
        Map<UUID, PartyRole> roles = new HashMap<>();
        roles.put(leader.getUniqueId(), PartyRole.LEADER);
        party.setRoles(roles);

        // 파티 레벨 초기화
        PartyLevel partyLevel = new PartyLevel();
        partyLevel.setLevel(1);
        partyLevel.setExperience(0);
        partyLevel. setSkillPoints(0);
        partyLevel.setUsedSkillPoints(0);
        partyLevel. setLearnedSkills(new ArrayList<>());
        party.setPartyLevel(partyLevel);

        // 버프 초기화
        party.setActiveBuffs(new ArrayList<>());

        // 이벤트 발생
        PartyCreateEvent createEvent = new PartyCreateEvent(leader, party);
        Bukkit.getPluginManager().callEvent(createEvent);

        if (createEvent.isCancelled()) {
            if (createEvent.getCancelReason() != null) {
                leader.sendMessage(createEvent.getCancelReason());
            }
            return null;
        }

        // 파티 등록
        parties.put(party. getPartyId(), party);
        playerToParty.put(leader.getUniqueId(), party.getPartyId());

        // 플레이어 데이터 업데이트
        PlayerPartyData playerData = plugin.getDataManager().loadPlayerData(leader.getUniqueId());
        if (playerData == null) {
            playerData = new PlayerPartyData();
            playerData.setPlayerUUID(leader. getUniqueId());
            playerData.setPlayerName(leader.getName());
        }
        playerData.setCurrentParty(party. getPartyId());
        playerData. setPartiesCreated(playerData.getPartiesCreated() + 1);
        playerData.setTotalParties(playerData.getTotalParties() + 1);
        playerData.setLastPartyTime(System. currentTimeMillis());
        plugin.getDataManager().savePlayerData(leader.getUniqueId(), playerData);

        // 파티 저장
        plugin. getDataManager().saveParty(party);

        leader.sendMessage(plugin.getMessageUtil().getMessage("party.created",
                "%party%", partyName != null ? partyName : "파티"));

        return party;
    }

    // ==================== 파티 해체 ====================
    public boolean disbandParty(Party party, UUID disbanderId) {
        return disbandParty(party, PartyDisbandReason. LEADER_DISBAND, disbanderId);
    }

    public boolean disbandParty(Party party, PartyDisbandReason reason, UUID disbanderId) {
        if (party == null) return false;

        // 이벤트 발생
        PartyDisbandEvent disbandEvent = new PartyDisbandEvent(party, reason, disbanderId);
        Bukkit.getPluginManager().callEvent(disbandEvent);

        if (disbandEvent.isCancelled() && reason != PartyDisbandReason. ADMIN_FORCE) {
            return false;
        }

        // 멤버들에게 알림
        String disbandMessage = plugin. getMessageUtil().getMessage("party.disbanded");
        for (UUID memberUUID : party. getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null) {
                member.sendMessage(disbandMessage);
                plugin.getPartyBuffManager().removeBuffsFromPlayer(member);
            }

            // 매핑 제거
            playerToParty.remove(memberUUID);

            // 플레이어 데이터 업데이트
            PlayerPartyData playerData = plugin.getDataManager().loadPlayerData(memberUUID);
            if (playerData != null) {
                playerData.setCurrentParty(null);
                plugin.getDataManager().savePlayerData(memberUUID, playerData);
            }
        }

        // 파티 삭제
        parties.remove(party. getPartyId());
        plugin.getDataManager().deleteParty(party. getPartyId());

        // 모집 공고 삭제
        plugin.getDataManager().deleteListing(party.getPartyId());

        return true;
    }

    public void disbandPartyByAdmin(Party party) {
        disbandParty(party, PartyDisbandReason. ADMIN_FORCE, null);
    }

    // ==================== 멤버 추가 ====================
    public boolean addMember(Party party, Player player) {
        return addMember(party, player, JoinType.INVITE);
    }

    public boolean addMember(Party party, Player player, JoinType joinType) {
        if (party == null || player == null) return false;

        // 이미 파티에 있는지 확인
        if (isInParty(player)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.already-in-party"));
            return false;
        }

        // 인원 제한 확인
        if (party.getMembers().size() >= party.getMaxMembers()) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.party-full"));
            return false;
        }

        // 이벤트 발생
        PartyMemberJoinEvent joinEvent = new PartyMemberJoinEvent(party, player, joinType);
        Bukkit.getPluginManager().callEvent(joinEvent);

        if (joinEvent.isCancelled()) {
            if (joinEvent.getCancelReason() != null) {
                player.sendMessage(joinEvent.getCancelReason());
            }
            return false;
        }

        // 멤버 추가
        party.getMembers().add(player.getUniqueId());
        party.getRoles().put(player.getUniqueId(), PartyRole.MEMBER);

        // 매핑 추가
        playerToParty.put(player.getUniqueId(), party.getPartyId());

        // 파티 저장
        plugin. getDataManager().saveParty(party);

        return true;
    }

    // ==================== 멤버 제거 ====================
    public boolean removeMember(Party party, UUID playerUUID) {
        return removeMember(party, playerUUID, LeaveReason.VOLUNTARY);
    }

    public boolean removeMember(Party party, UUID playerUUID, LeaveReason reason) {
        if (party == null || playerUUID == null) return false;

        if (!party.getMembers().contains(playerUUID)) {
            return false;
        }

        // 멤버 제거
        party.getMembers().remove(playerUUID);
        party.getRoles().remove(playerUUID);

        // 매핑 제거
        playerToParty.remove(playerUUID);

        // 파티가 비었으면 해체
        if (party.getMembers().isEmpty()) {
            disbandParty(party, PartyDisbandReason.NO_MEMBERS, null);
            return true;
        }

        // 리더가 나갔으면 다음 리더 지정
        if (party.getLeaderId().equals(playerUUID)) {
            assignNewLeader(party, playerUUID);
        }

        // 파티 저장
        plugin.getDataManager().saveParty(party);

        return true;
    }

    private void assignNewLeader(Party party, UUID previousLeader) {
        UUID newLeader = null;

        // 부리더 중 선택
        for (UUID memberUUID : party. getMembers()) {
            if (party.getRoles().get(memberUUID) == PartyRole. OFFICER) {
                newLeader = memberUUID;
                break;
            }
        }

        // 부리더가 없으면 아무나
        if (newLeader == null && ! party.getMembers().isEmpty()) {
            newLeader = party. getMembers().get(0);
        }

        if (newLeader != null) {
            party.setLeaderId(newLeader);
            party.getRoles().put(newLeader, PartyRole.LEADER);

            String previousName = getOfflinePlayerName(previousLeader);
            String newName = getOfflinePlayerName(newLeader);

            // 이벤트 발생
            PartyLeaderChangeEvent event = new PartyLeaderChangeEvent(
                    party, previousLeader, previousName, newLeader, newName,
                    PartyLeaderChangeEvent. ChangeReason. LEADER_LEFT);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    // ==================== 멤버 추방 ====================
    public boolean kickMember(Party party, UUID kickerUUID, UUID targetUUID) {
        if (party == null) return false;

        // 권한 확인
        if (!plugin.getPartyRoleManager().canKick(party, kickerUUID, targetUUID)) {
            return false;
        }

        String targetName = getOfflinePlayerName(targetUUID);
        String kickerName = getOfflinePlayerName(kickerUUID);
        PartyRole targetRole = party. getRoles().get(targetUUID);

        // 이벤트 발생
        PartyKickEvent kickEvent = new PartyKickEvent(
                party, kickerUUID, kickerName, targetUUID, targetName, targetRole);
        Bukkit.getPluginManager().callEvent(kickEvent);

        if (kickEvent.isCancelled()) {
            return false;
        }

        // 멤버 제거
        return removeMember(party, targetUUID, LeaveReason.KICKED);
    }

    // ==================== 조회 메서드 ====================
    public Party getParty(UUID partyId) {
        return parties.get(partyId);
    }

    public Party getPlayerParty(Player player) {
        return getPlayerParty(player. getUniqueId());
    }

    public Party getPlayerParty(UUID playerUUID) {
        UUID partyId = playerToParty.get(playerUUID);
        if (partyId == null) return null;
        return parties.get(partyId);
    }

    public boolean isInParty(Player player) {
        return playerToParty.containsKey(player. getUniqueId());
    }

    public boolean isInParty(UUID playerUUID) {
        return playerToParty.containsKey(playerUUID);
    }

    public List<Party> getAllParties() {
        return new ArrayList<>(parties. values());
    }

    public int getPartyCount() {
        return parties.size();
    }

    public Party getPartyByName(String name) {
        if (name == null) return null;
        
        for (Party party : parties.values()) {
            if (party.getPartyName() != null && 
                party.getPartyName().equalsIgnoreCase(name)) {
                return party;
            }
        }
        return null;
    }

    public boolean isPartyNameTaken(String name) {
        return getPartyByName(name) != null;
    }

    // ==================== 유틸리티 ====================
    public String getOfflinePlayerName(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            return player.getName();
        }

        String name = plugin.getDataManager().getPlayerName(playerUUID);
        if (name != null) {
            return name;
        }

        return Bukkit.getOfflinePlayer(playerUUID).getName();
    }

    public void saveParty(Party party) {
        if (party != null) {
            plugin.getDataManager().saveParty(party);
        }
    }

    public void updatePartyCache(Party party) {
        if (party != null) {
            parties.put(party. getPartyId(), party);
        }
    }
}