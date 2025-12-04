package com.multiverse.dungeon.managers;

import com.multiverse.dungeon. DungeonCore;
import java.util.*;

/**
 * 파티 초대 관리 매니저
 */
public class InviteManager {

    private final DungeonCore plugin;
    private final Map<UUID, Map<UUID, Long>> invites; // playerId -> (partyId -> expireTime)

    /**
     * 생성자
     */
    public InviteManager(DungeonCore plugin) {
        this. plugin = plugin;
        this. invites = new HashMap<>();
    }

    /**
     * 초대 추가
     *
     * @param playerId 초대받은 플레이어 ID
     * @param partyId 파티 ID
     * @param expireTime 만료 시간 (밀리초)
     */
    public void addInvite(UUID playerId, UUID partyId, long expireTime) {
        if (playerId == null || partyId == null) {
            return;
        }

        var playerInvites = invites.computeIfAbsent(playerId, k -> new HashMap<>());
        playerInvites.put(partyId, expireTime);

        plugin.getLogger().info("✅ 플레이어 " + playerId + "에게 파티 초대 추가됨");
    }

    /**
     * 플레이어의 모든 초대 조회
     *
     * @param playerId 플레이어 ID
     * @return 초대 목록 (partyId -> expireTime)
     */
    public Map<UUID, Long> getPlayerInvites(UUID playerId) {
        return invites.getOrDefault(playerId, new HashMap<>());
    }

    /**
     * 특정 초대 조회
     *
     * @param playerId 플레이어 ID
     * @param partyId 파티 ID
     * @return 만료 시간 (밀리초), 없으면 -1
     */
    public long getInvite(UUID playerId, UUID partyId) {
        var playerInvites = invites.get(playerId);
        if (playerInvites == null) {
            return -1;
        }

        return playerInvites.getOrDefault(partyId, -1L);
    }

    /**
     * 초대 수락
     *
     * @param playerId 플레이어 ID
     * @param partyId 파티 ID
     * @return 성공하면 true
     */
    public boolean acceptInvite(UUID playerId, UUID partyId) {
        if (playerId == null || partyId == null) {
            return false;
        }

        var playerInvites = invites. get(playerId);
        if (playerInvites == null || !  playerInvites.containsKey(partyId)) {
            return false;
        }

        // 초대가 만료되었는지 확인
        long expireTime = playerInvites.get(partyId);
        if (System.currentTimeMillis() >= expireTime) {
            playerInvites.remove(partyId);
            return false; // 만료됨
        }

        playerInvites.remove(partyId);
        plugin.getLogger().info("✅ 플레이어 " + playerId + "가 파티 초대를 수락했습니다");

        return true;
    }

    /**
     * 초대 거절
     *
     * @param playerId 플레이어 ID
     * @param partyId 파티 ID
     * @return 성공하면 true
     */
    public boolean rejectInvite(UUID playerId, UUID partyId) {
        if (playerId == null || partyId == null) {
            return false;
        }

        var playerInvites = invites.get(playerId);
        if (playerInvites == null || !  playerInvites.containsKey(partyId)) {
            return false;
        }

        playerInvites.remove(partyId);
        plugin.getLogger().info("✅ 플레이어 " + playerId + "가 파티 초대를 거절했습니다");

        return true;
    }

    /**
     * 모든 만료된 초대 정리
     */
    public void cleanupExpiredInvites() {
        List<UUID> playersToRemove = new ArrayList<>();

        for (var playerEntry : invites.entrySet()) {
            UUID playerId = playerEntry.getKey();
            var playerInvites = playerEntry.getValue();

            List<UUID> partiesToRemove = new ArrayList<>();

            for (var inviteEntry : playerInvites. entrySet()) {
                UUID partyId = inviteEntry. getKey();
                long expireTime = inviteEntry.getValue();

                if (System.currentTimeMillis() >= expireTime) {
                    partiesToRemove.add(partyId);
                }
            }

            for (var partyId : partiesToRemove) {
                playerInvites.remove(partyId);
            }

            if (playerInvites. isEmpty()) {
                playersToRemove.add(playerId);
            }
        }

        for (var playerId : playersToRemove) {
            invites.remove(playerId);
        }

        if (!playersToRemove.isEmpty()) {
            plugin.getLogger().info("✅ " + playersToRemove.size() + "개의 만료된 초대가 정리되었습니다");
        }
    }

    /**
     * 특정 파티의 모든 초대 취소
     *
     * @param partyId 파티 ID
     */
    public void cancelPartyInvites(UUID partyId) {
        if (partyId == null) {
            return;
        }

        for (var playerInvites : invites.values()) {
            playerInvites. remove(partyId);
        }

        plugin.getLogger().info("✅ 파티 " + partyId + "의 모든 초대가 취소되었습니다");
    }

    /**
     * 플레이어의 모든 초대 취소
     *
     * @param playerId 플레이어 ID
     */
    public void cancelPlayerInvites(UUID playerId) {
        if (playerId == null) {
            return;
        }

        invites.remove(playerId);
        plugin.getLogger().info("✅ 플레이어 " + playerId + "의 모든 초대가 취소되었습니다");
    }

    /**
     * 플레이어가 파티 초대를 받았는지 확인
     *
     * @param playerId 플레이어 ID
     * @param partyId 파티 ID
     * @return 초대를 받았으면 true
     */
    public boolean hasInvite(UUID playerId, UUID partyId) {
        var playerInvites = invites. get(playerId);
        if (playerInvites == null) {
            return false;
        }

        return playerInvites.containsKey(partyId);
    }

    /**
     * 플레이어가 활성 초대를 받았는지 확인 (만료 전)
     *
     * @param playerId 플레이어 ID
     * @param partyId 파티 ID
     * @return 활성 초대가 있으면 true
     */
    public boolean hasActiveInvite(UUID playerId, UUID partyId) {
        if (! hasInvite(playerId, partyId)) {
            return false;
        }

        long expireTime = getInvite(playerId, partyId);
        return System. currentTimeMillis() < expireTime;
    }
}