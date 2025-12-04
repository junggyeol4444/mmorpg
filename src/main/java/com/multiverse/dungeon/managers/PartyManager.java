package com.multiverse.dungeon.managers;

import com.multiverse.dungeon. DungeonCore;
import com.multiverse. dungeon.data.model.DungeonDataManager;
import com.multiverse. dungeon.data.model.Party;
import com.multiverse. dungeon.events.PartyCreatedEvent;
import com.multiverse.dungeon.events.PartyDisbandedEvent;
import org.bukkit. Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * 파티 관리 매니저
 */
public class PartyManager {

    private final DungeonCore plugin;
    private final DungeonDataManager dataManager;
    private final Map<UUID, Party> parties; // partyId -> Party
    private final Map<UUID, UUID> playerParties; // playerId -> partyId

    /**
     * 생성자
     */
    public PartyManager(DungeonCore plugin, DungeonDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.parties = new HashMap<>();
        this.playerParties = new HashMap<>();
        
        loadAllParties();
    }

    /**
     * 모든 파티 로드
     */
    private void loadAllParties() {
        try {
            var loadedParties = dataManager.loadAllParties();
            for (var party : loadedParties) {
                parties.put(party.getPartyId(), party);
                for (var memberId : party.getMembers()) {
                    playerParties.put(memberId, party.getPartyId());
                }
            }
            
            plugin.getLogger().info("✅ " + parties.size() + "개의 파티가 로드되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 파티 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 새로운 파티 생성
     *
     * @param leader 파티 리더
     * @return 생성된 파티
     */
    public Party createParty(Player leader) {
        if (leader == null) {
            return null;
        }

        // 이미 파티에 속해있는지 확인
        if (playerParties.containsKey(leader.getUniqueId())) {
            return null;
        }

        // 파티 생성
        UUID partyId = UUID.randomUUID();
        Party party = new Party(partyId, leader. getUniqueId());

        parties.put(partyId, party);
        playerParties.put(leader.getUniqueId(), partyId);

        // 데이터 저장
        try {
            dataManager.saveParty(party);
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 파티 저장 실패: " + e.getMessage());
        }

        // 이벤트 호출
        Bukkit.getPluginManager(). callEvent(new PartyCreatedEvent(party, leader));

        plugin.getLogger().info("✅ 파티 생성됨: " + party.getPartyId() + " (리더: " + leader.getName() + ")");

        return party;
    }

    /**
     * 파티 ID로 파티 조회
     *
     * @param partyId 파티 ID
     * @return 파티 객체, 없으면 null
     */
    public Party getParty(UUID partyId) {
        return parties.get(partyId);
    }

    /**
     * 플레이어가 속한 파티 조회
     *
     * @param player 플레이어
     * @return 파티 객체, 없으면 null
     */
    public Party getPlayerParty(Player player) {
        if (player == null) {
            return null;
        }
        UUID partyId = playerParties.get(player.getUniqueId());
        return partyId != null ? parties. get(partyId) : null;
    }

    /**
     * 플레이어가 파티에 속해있는지 확인
     *
     * @param player 플레이어
     * @return 속해있으면 true
     */
    public boolean isInParty(Player player) {
        return player != null && playerParties.containsKey(player. getUniqueId());
    }

    /**
     * 플레이어가 파티 리더인지 확인
     *
     * @param player 플레이어
     * @return 리더이면 true
     */
    public boolean isLeader(Player player) {
        if (player == null) {
            return false;
        }

        Party party = getPlayerParty(player);
        return party != null && party.isLeader(player. getUniqueId());
    }

    /**
     * 파티에 플레이어 추가
     *
     * @param partyId 파티 ID
     * @param player 추가할 플레이어
     * @return 성공하면 true
     */
    public boolean addPlayerToParty(UUID partyId, Player player) {
        if (player == null) {
            return false;
        }

        Party party = parties.get(partyId);
        if (party == null || party.isFull()) {
            return false;
        }

        if (playerParties.containsKey(player.getUniqueId())) {
            return false; // 이미 다른 파티에 속함
        }

        party.addMember(player.getUniqueId());
        playerParties.put(player.getUniqueId(), partyId);

        try {
            dataManager. saveParty(party);
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 파티 저장 실패: " + e.getMessage());
        }

        return true;
    }

    /**
     * 파티에서 플레이어 제거
     *
     * @param partyId 파티 ID
     * @param playerId 플레이어 ID
     * @return 성공하면 true
     */
    public boolean removePlayerFromParty(UUID partyId, UUID playerId) {
        Party party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        if (! party.hasMember(playerId)) {
            return false;
        }

        party.removeMember(playerId);
        playerParties. remove(playerId);

        try {
            dataManager.saveParty(party);
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 파티 저장 실패: " + e.getMessage());
        }

        // 파티가 비었으면 해체
        if (party.getMemberCount() == 0) {
            disbandParty(partyId, PartyDisbandedEvent.DisbandReason.LAST_MEMBER_LEFT);
        }

        return true;
    }

    /**
     * 플레이어를 파티에서 추방
     *
     * @param partyId 파티 ID
     * @param playerId 플레이어 ID
     * @return 성공하면 true
     */
    public boolean kickPlayer(UUID partyId, UUID playerId) {
        Party party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        if (party.isLeader(playerId)) {
            return false; // 리더는 추방할 수 없음
        }

        return removePlayerFromParty(partyId, playerId);
    }

    /**
     * 파티 리더 위임
     *
     * @param partyId 파티 ID
     * @param newLeaderId 새 리더 ID
     * @return 성공하면 true
     */
    public boolean promoteLeader(UUID partyId, UUID newLeaderId) {
        Party party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        if (! party.promoteLeader(newLeaderId)) {
            return false;
        }

        try {
            dataManager.saveParty(party);
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 파티 저장 실패: " + e.getMessage());
        }

        return true;
    }

    /**
     * 파티 해체
     *
     * @param partyId 파티 ID
     * @param reason 해체 사유
     * @return 성공하면 true
     */
    public boolean disbandParty(UUID partyId, String reason) {
        Party party = parties.remove(partyId);
        if (party == null) {
            return false;
        }

        // 모든 파티원의 파티 참조 제거
        for (var memberId : party.getMembers()) {
            playerParties.remove(memberId);
        }

        try {
            dataManager.deleteParty(partyId);
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 파티 삭제 실패: " + e.getMessage());
        }

        // 이벤트 호출
        Player disbander = Bukkit.getPlayer(party.getLeaderId());
        Bukkit. getPluginManager().callEvent(new PartyDisbandedEvent(party, disbander, reason));

        plugin.getLogger().info("✅ 파티 해체됨: " + partyId + " (사유: " + reason + ")");

        return true;
    }

    /**
     * 파티 존재 여부
     *
     * @param partyId 파티 ID
     * @return 존재하면 true
     */
    public boolean hasParty(UUID partyId) {
        return parties.containsKey(partyId);
    }

    /**
     * 모든 파티 저장
     */
    public void saveAllParties() {
        try {
            for (var party : parties.values()) {
                dataManager.saveParty(party);
            }
            plugin.getLogger().info("✅ 모든 파티가 저장되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 파티 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 모든 파티 재로드
     */
    public void reloadParties() {
        parties.clear();
        playerParties.clear();
        loadAllParties();
    }
}