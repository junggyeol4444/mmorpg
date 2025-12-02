package com.multiverse.playerdata.data;

import com.multiverse.playerdata.models.*;
import com.multiverse.playerdata.models.enums.StatType;

import java.util.Map;
import java.util.UUID;

public interface DataManager {

    // ==== 종족 데이터 ====
    Map<String, Object> loadRaceData();

    // ==== 진화 데이터 ====
    Map<String, Object> loadEvolutionData();

    // ==== 플레이어 데이터 ====
    boolean playerDataExists(UUID uuid);

    PlayerStats loadPlayerStats(UUID uuid);

    void savePlayerStats(UUID uuid, PlayerStats stats);

    PlayerStats restorePlayerStats(UUID uuid, String backupFileName);

    // ==== 플레이어 종족 ====
    String getPlayerRaceId(UUID uuid);

    void setPlayerRaceId(UUID uuid, String raceId);

    // ==== 플레이어 진화 이력 ====
    void addPlayerEvolutionHistory(UUID uuid, String fromRace, String toRace);

    // ==== 초월 데이터 ====
    Transcendence loadPlayerTranscendence(UUID uuid);

    void savePlayerTranscendence(UUID uuid, Transcendence transcendence);

    // ==== 기타 확장 API ====
    // (예: 백업/마이그레이션/통계 등)
}