package com.multiverse.party.managers;

import com.multiverse.party.PartyCore;
import com.multiverse.party.models.Party;
import com.multiverse.party.models.PartyListing;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 공개 파티 목록 관리, 자동 매칭, 모집 공고 생성/수정/삭제 등 처리 매니저
 */
public class PartyFinder {

    private final PartyCore plugin;
    // 공개 파티 목록 (파티ID -> 파티)
    private final Map<UUID, Party> publicParties = new ConcurrentHashMap<>();
    // 플레이어 매칭 대기열
    private final Set<UUID> matchingQueue = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public PartyFinder(PartyCore plugin) {
        this.plugin = plugin;
    }

    /** 공개 파티 등록/해제 (파티 정보 변경 시 호출) */
    public void registerParty(Party party) {
        if (party == null || party.getPartyId() == null) return;
        if (party.getPrivacy() == com.multiverse.party.models.enums.PartyPrivacy.PUBLIC)
            publicParties.put(party.getPartyId(), party);
        else
            publicParties.remove(party.getPartyId());
    }

    public void unregisterParty(Party party) {
        if (party == null) return;
        publicParties.remove(party.getPartyId());
    }

    /** 현재 공개 파티 목록 반환 */
    public List<Party> getPublicParties() {
        return new ArrayList<>(publicParties.values());
    }

    /** 매칭 대기열 처리 */
    public void addToQueue(Player player) {
        if (player == null) return;
        matchingQueue.add(player.getUniqueId());
        player.sendMessage(plugin.getMessageUtil().getMessage("finder.queue-enter"));
        tryAutoMatch(player);
    }

    public void removeFromQueue(Player player) {
        if (player == null) return;
        matchingQueue.remove(player.getUniqueId());
        player.sendMessage(plugin.getMessageUtil().getMessage("finder.queue-leave"));
    }

    public boolean isInQueue(Player player) {
        if (player == null) return false;
        return matchingQueue.contains(player.getUniqueId());
    }

    /** 자동매칭: 대기열 + 공개 파티 조합! */
    private void tryAutoMatch(Player player) {
        // 간단화 - 첫 공개 파티에 참가 (풀방X, 초대X)
        for (Party party : getPublicParties()) {
            if (party.getMembers().size() < party.getMaxMembers()) {
                boolean result = plugin.getPartyManager().addMember(party, player);
                if (result) {
                    removeFromQueue(player);
                    player.sendMessage(plugin.getMessageUtil().getMessage("finder.auto-matched",
                        "%party%", party.getPartyName() != null ? party.getPartyName() : "파티"));
                    return;
                }
            }
        }
        // 못찾으면 나중에 다시 시도
    }

    /** 모집 공고 생성/수정 (파티/설명 받아서 설정) */
    public void createOrUpdateListing(Party party, String description) {
        if (party == null || description == null) return;
        PartyListing listing = plugin.getDataManager().loadListing(party.getPartyId());
        if (listing == null) {
            listing = new PartyListing();
            listing.setPartyId(party.getPartyId());
            listing.setTitle(party.getPartyName() != null ? party.getPartyName() : "파티");
            listing.setCreatedTime(System.currentTimeMillis());
            listing.setMinLevel(1);
            listing.setMaxLevel(100);
            listing.setPurpose(com.multiverse.party.models.enums.PartyPurpose.GENERAL);
        }
        listing.setDescription(description);
        listing.setExpireTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // 24시간 고정
        plugin.getDataManager().saveListing(listing);
    }

    /** 모집 공고 삭제 */
    public void removeListing(Party party) {
        if (party == null) return;
        plugin.getDataManager().deleteListing(party.getPartyId());
    }
}