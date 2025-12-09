package com.multiverse.party. data;

import com. multiverse.party. models.Party;
import com.multiverse. party.models.PartyListing;
import com. multiverse.party. models.PlayerPartyData;

import java.util. List;
import java.util.Map;
import java.util. UUID;

public interface DataManager {

    /**
     * 데이터 매니저 초기화
     */
    void initialize();

    /**
     * 데이터 매니저 종료
     */
    void shutdown();

    // ==================== 파티 데이터 ====================

    /**
     * 파티 저장
     */
    void saveParty(Party party);

    /**
     * 파티 로드
     */
    Party loadParty(UUID partyId);

    /**
     * 파티 삭제
     */
    void deleteParty(UUID partyId);

    /**
     * 모든 파티 저장
     */
    void saveAllParties();

    /**
     * 모든 파티 로드
     */
    Map<UUID, Party> loadAllParties();

    /**
     * 파티 존재 여부 확인
     */
    boolean partyExists(UUID partyId);

    // ==================== 플레이어 데이터 ====================

    /**
     * 플레이어 파티 데이터 저장
     */
    void savePlayerData(UUID playerUUID, PlayerPartyData data);

    /**
     * 플레이어 파티 데이터 로드
     */
    PlayerPartyData loadPlayerData(UUID playerUUID);

    /**
     * 플레이어 데이터 삭제
     */
    void deletePlayerData(UUID playerUUID);

    /**
     * 모든 플레이어 데이터 저장
     */
    void saveAllPlayerData();

    /**
     * 모든 플레이어 데이터 로드
     */
    Map<UUID, PlayerPartyData> loadAllPlayerData();

    // ==================== 파티 모집 공고 ====================

    /**
     * 모집 공고 저장
     */
    void saveListing(PartyListing listing);

    /**
     * 모집 공고 로드
     */
    PartyListing loadListing(UUID partyId);

    /**
     * 모집 공고 삭제
     */
    void deleteListing(UUID partyId);

    /**
     * 모든 모집 공고 저장
     */
    void saveAllListings();

    /**
     * 모든 모집 공고 로드
     */
    List<PartyListing> loadAllListings();

    // ==================== 유틸리티 ====================

    /**
     * 데이터 타입 반환
     */
    String getDataType();

    /**
     * 연결 상태 확인
     */
    boolean isConnected();

    /**
     * 데이터 백업
     */
    void backup(String backupName);

    /**
     * 플레이어 UUID 조회 (이름으로)
     */
    UUID getPlayerUUID(String playerName);

    /**
     * 플레이어 이름 조회 (UUID로)
     */
    String getPlayerName(UUID playerUUID);
}